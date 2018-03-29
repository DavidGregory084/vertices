/*
 * Copyright 2018 David Gregory and the Vertices project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vertices

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import io.circe.{ CursorOp, Decoder, Encoder, Error, Json, ParsingFailure, DecodingFailure }
import io.circe.parser
import io.circe.syntax._
import io.vertx.core.eventbus.Message

object VertexHandler {
  val errorEncoder: Encoder[Throwable] = Encoder.instance {
    case ParsingFailure(msg, _) =>
      Json.obj(
        "message" -> msg.asJson)
    case DecodingFailure(msg, ops) =>
      Json.obj(
        "message" -> msg.asJson,
        "path" -> CursorOp.opsToPath(ops).asJson)
  }
}

sealed abstract class VertexHandler[In: Decoder, Out: Encoder, ReqType <: Request[In]: Decoder](name: String, vertx: Vertx) extends LazyLogging {

  implicit val scheduler: Scheduler = VertexScheduler(vertx)

  def address: String = s"${getClass.getPackage.getName}.${name}"

  def handleRequest: (Message[String], ReqType) => Task[Unit]

  def handle: Observable[In] => Observable[Out]

  def decode[A: Decoder](msg: Message[String]): (Message[String], Either[Error, A]) = {
    val decoded = for {
      json <- parser.parse(msg.body)
      in <- json.as[A]
    } yield in

    (msg, decoded)
  }

  def handleDecodingFailure(decoded: (Message[String], Either[Error, ReqType])): Observable[(Message[String], ReqType)] =
    decoded match {
      case (msg, Left(error)) =>
        logger.debug(s"Error decoding message: ${error}")
        msg.reply(error.asJson(VertexHandler.errorEncoder.contramap(t => t: Throwable)).noSpaces)
        Observable.empty
      case (msg, Right(in)) =>
        logger.debug(s"Decoded message: $in")
        Observable.pure((msg, in))
    }

  def registerClientStreamHandler(): (String, Observable[In]) = {
    val clientStreamAddress = address + "." + UUID.randomUUID()

    val clientStream = vertx.eventBus
      .consumer[String](clientStreamAddress)
      .toObservable(vertx)
      .map(decode[In]).flatMap {
        case (msg, Left(error)) =>
          msg.reply(error.asJson(VertexHandler.errorEncoder.contramap(t => t: Throwable)).noSpaces)
          Observable.empty
        case (_, Right(in)) =>
          Observable.pure(in)
      }

    (clientStreamAddress, clientStream)
  }

  def start: Task[Unit] =
    register.foreachL { messages =>
      messages
        .doOnNext(msg => logger.debug(s"Received message: ${msg.body}"))
        .map(decode[ReqType])
        .flatMap(handleDecodingFailure)
        .mapTask(handleRequest.tupled)
        .completedL
        .runAsync(scheduler)
    }

  def register: Task[Observable[Message[String]]] = Task.eval {
    val handler = vertx.eventBus
      .consumer[String](address)
      .toObservable(vertx)

    logger.debug(s"Registered handler at address ${address}")

    handler
  }
}

abstract class RpcHandler[In: Decoder, Out: Encoder](
  name: String,
  vertx: Vertx,
  handler: In => Task[Out]) extends VertexHandler[In, Out, RpcRequest[In]](name, vertx) {

  def handleRequest = {
    case (msg, RpcRequest(body)) =>
      handleRpcRequest(msg, body)
  }

  def handleRpcRequest(msg: Message[String], body: In): Task[Unit] = {
    // Send the body straight through to the service
    handle(Observable.pure(body)).headL.foreachL { out =>
      // Reply to the message directly
      logger.debug(s"Sending reply: $out")
      msg.reply(out.asJson.noSpaces)
    }
  }

  def handle = { messages =>
    val reply = messages.firstL.flatMap(handler)
    Observable.fromTask(reply)
  }
}

abstract class ServerStreamHandler[In: Decoder, Out: Encoder](
  name: String,
  vertx: Vertx,
  handler: In => Observable[Out]) extends VertexHandler[In, Out, ServerStreamRequest[In]](name, vertx) {

  def handleRequest = {
    case (msg, ServerStreamRequest(body, streamAddress)) =>
      handleServerStreamRequest(msg, body, streamAddress)
  }

  def handleServerStreamRequest(msg: Message[String], body: In, serverStreamAddress: String): Task[Unit] = {
    // Acknowledge the request
    msg.reply(().asJson.noSpaces)

    // Handle the request by streaming back to the client's handler
    handle(Observable.pure(body)).zipWithIndex.foreachL {
      case (out, idx) =>
        logger.debug(s"Sending stream segment $idx: $out")
        vertx.eventBus.publish(serverStreamAddress, out.asJson.noSpaces)
    }
  }

  def handle = { messages =>
    val reply = messages.firstL.map(handler)
    Observable.fromTask(reply).flatten
  }
}

abstract class ClientStreamHandler[In: Decoder, Out: Encoder](
  name: String,
  vertx: Vertx,
  handler: Observable[In] => Task[Out]) extends VertexHandler[In, Out, ClientStreamRequest.type](name, vertx) {

  def handleRequest = {
    case (msg, ClientStreamRequest) =>
      handleClientStreamRequest(msg)
  }

  def handleClientStreamRequest(msg: Message[String]): Task[Unit] = {
    // Register a new handler at a random UUID
    val (clientStreamAddress, clientStream) = registerClientStreamHandler()

    // Reply with the client stream address
    msg.reply(clientStreamAddress)

    // Send the client stream to the service
    handle(clientStream).completedL
  }

  def handle = { messages =>
    Observable.fromTask(handler(messages))
  }
}

abstract class BidiStreamHandler[In: Decoder, Out: Encoder](
  name: String,
  vertx: Vertx,
  handler: Observable[In] => Observable[Out]) extends VertexHandler[In, Out, BidiStreamRequest](name, vertx) {

  def handleRequest = {
    case (msg, BidiStreamRequest(streamAddress)) =>
      handleBidiStreamRequest(msg, streamAddress)
  }

  def handleBidiStreamRequest(msg: Message[String], serverStreamAddress: String): Task[Unit] = {
    // Register a new handler at a random UUID
    val (clientStreamAddress, clientStream) = registerClientStreamHandler()

    // Reply with the client stream address
    msg.reply(clientStreamAddress)

    // Send the client stream to the service and stream back to the client's handler
    handle(clientStream).foreachL { out =>
      vertx.eventBus.publish(serverStreamAddress, out.asJson.noSpaces)
    }
  }

  def handle = handler
}
