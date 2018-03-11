package vertices

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import io.circe.{ CursorOp, Decoder, Encoder, Error, Json, ParsingFailure, DecodingFailure }
import io.circe.parser
import io.circe.syntax._
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message

sealed abstract class VertexHandler[In: Decoder, Out: Encoder](name: String, vertx: Vertx) extends LazyLogging {
  implicit val scheduler: Scheduler = VertexScheduler(vertx)

  implicit val errorEncoder: Encoder[Error] = Encoder.instance {
    case ParsingFailure(msg, _) =>
      Json.obj(
        "message" -> msg.asJson)
    case DecodingFailure(msg, ops) =>
      Json.obj(
        "message" -> msg.asJson,
        "path" -> CursorOp.opsToPath(ops).asJson)
  }

  def address: String = s"${getClass.getPackage.getName}.${name}"

  def handle: Observable[In] => Observable[Out]

  def decode[A: Decoder](msg: Message[String]): (Message[String], Either[Error, A]) = {
    val decoded = for {
      json <- parser.parse(msg.body)
      in <- json.as[A]
    } yield in

    (msg, decoded)
  }

  def handleDecodingFailure(decoded: (Message[String], Either[Error, Request[In]])): Observable[(Message[String], Request[In])] =
    decoded match {
      case (msg, Left(error)) =>
        logger.debug(s"Error decoding message: ${error}")
        msg.reply(error)
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
          msg.reply(error.asJson.noSpaces)
          Observable.empty
        case (_, Right(in)) =>
          Observable.pure(in)
      }

    (clientStreamAddress, clientStream)
  }

  def handleRpcRequest(msg: Message[String], body: In): Task[Unit] = {
    // Send the body straight through to the service
    handle(Observable.pure(body)).headL.foreachL { out =>
      // Reply to the message directly
      logger.debug(s"Sending reply: $out")
      msg.reply(out.asJson.noSpaces)
    }
  }

  def handleClientStreamRequest(msg: Message[String], body: In): Task[Unit] = {
    // Register a new handler at a random UUID
    val (clientStreamAddress, clientStream) = registerClientStreamHandler()

    // Reply with the client stream address
    msg.reply(clientStreamAddress)

    // Send the client stream to the service
    handle(Observable.pure(body) ++ clientStream)
      .completedL
  }

  def handleServerStreamRequest(msg: Message[String], body: In, serverStreamAddress: String): Task[Unit] = {
    // Acknowledge the request
    msg.reply(().asJson.noSpaces)

    // Handle the request by streaming back to the client's handler
    handle(Observable.pure(body)).foreachL { out =>
      vertx.eventBus.publish(serverStreamAddress, out.asJson.noSpaces)
    }
  }

  def handleBidiStreamRequest(msg: Message[String], body: In, serverStreamAddress: String): Task[Unit] = {
    // Register a new handler at a random UUID
    val (clientStreamAddress, clientStream) = registerClientStreamHandler()

    // Reply with the client stream address
    msg.reply(clientStreamAddress)

    // Send the client stream to the service and stream back to the client's handler
    handle(Observable.pure(body) ++ clientStream).foreachL { out =>
      vertx.eventBus.publish(serverStreamAddress, out.asJson.noSpaces)
    }
  }

  def start: Task[Unit] =
    register.foreachL { messages =>
      messages
        .doOnNext(msg =>
          logger.debug(s"Received message: ${msg.body}"))
        .map(decode[Request[In]])
        .flatMap(handleDecodingFailure)
        .mapTask {
          case (msg, RpcRequest(body)) =>
            handleRpcRequest(msg, body)
          case (msg, ClientStreamRequest(body)) =>
            handleClientStreamRequest(msg, body)
          case (msg, ServerStreamRequest(body, serverStreamAddress)) =>
            handleServerStreamRequest(msg, body, serverStreamAddress)
          case (msg, BidiStreamRequest(body, serverStreamAddress)) =>
            handleBidiStreamRequest(msg, body, serverStreamAddress)
        }.completedL.runAsync(scheduler)
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
  handler: In => Out) extends VertexHandler[In, Out](name, vertx) {
  def handle = { messages =>
    val reply = messages.firstL.map(handler)
    Observable.fromTask(reply)
  }
}

abstract class ServerStreamHandler[In: Decoder, Out: Encoder](
  name: String,
  vertx: Vertx,
  handler: In => Observable[Out]) extends VertexHandler[In, Out](name, vertx) {
  def handle = { messages =>
    val reply = messages.firstL.map(handler)
    Observable.fromTask(reply).flatten
  }
}

abstract class ClientStreamHandler[In: Decoder, Out: Encoder](
  name: String,
  vertx: Vertx,
  handler: Observable[In] => Out) extends VertexHandler[In, Out](name, vertx) {
  def handle = { messages =>
    val reply = Task.eval(handler(messages))
    Observable.fromTask(reply)
  }
}

abstract class BidiStreamHandler[In: Decoder, Out: Encoder](
  name: String,
  vertx: Vertx,
  handler: Observable[In] => Observable[Out]) extends VertexHandler[In, Out](name, vertx) {
  def handle = handler
}
