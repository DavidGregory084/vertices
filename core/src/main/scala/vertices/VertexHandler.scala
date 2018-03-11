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

  def decode[A: Decoder](msg: Message[String]): Either[Error, A] =
    for {
      json <- parser.parse(msg.body)
      in <- json.as[A]
    } yield in

  def start: Task[Unit] =
    register.map { requests =>
      requests.flatMap {
        case (msg, RpcRequest(body)) =>
          // Send the body straight through to the service
          handle(Observable.pure(body)).headF.map { out =>
            // Reply to the message directly
            logger.debug(s"Sending reply: $out")
            msg.reply(out.asJson.noSpaces)
          }

        case (msg, ClientStreamRequest(body)) =>
          val streamAddress = address + "." + UUID.randomUUID()

          // Register a new handler at a random UUID
          val clientStream = vertx.eventBus
            .consumer[String](streamAddress)
            .toObservable(vertx)
            .map { msg =>
              (msg, decode[In](msg))
            }.map {
              case (msg, Left(error)) =>
                msg.reply(error.asJson.noSpaces)
                Left(error)
              case (_, Right(in)) =>
                Right(in)
            }.collect {
              case (Right(in)) => in
            }

          // Reply with the client stream address
          msg.reply(streamAddress)

          // Send the client stream to the service
          handle(Observable.pure(body) ++ clientStream)

        case (msg, ServerStreamRequest(body, streamAddress)) =>
          // Acknowledge the request
          msg.reply(().asJson.noSpaces)

          // Handle the request by streaming back to the client's handler
          handle(Observable.pure(body)).map { out =>
            vertx.eventBus.publish(streamAddress, out.asJson.noSpaces)
          }

        case (msg, BidiStreamRequest(body, outAddress)) =>
          val inAddress = address + "." + UUID.randomUUID()

          // Register a new handler at a random UUID
          val clientStream = vertx.eventBus
            .consumer[String](inAddress)
            .toObservable(vertx)
            .map { msg =>
              (msg, decode[In](msg))
            }.map {
              case (msg, Left(error)) =>
                msg.reply(error.asJson.noSpaces)
                Left(error)
              case (_, Right(in)) =>
                Right(in)
            }.collect {
              case (Right(in)) => in
            }

          // Reply with the client stream address
          msg.reply(inAddress)

          // Send the client stream to the service and stream back to the client's handler
          handle(Observable.pure(body) ++ clientStream).map { out =>
            vertx.eventBus.publish(outAddress, out.asJson.noSpaces)
          }
      }.completedL.runAsync(scheduler)
    }

  def register: Task[Observable[(Message[String], Request[In])]] =
    Task.eval {
      val messages = vertx.eventBus
        .consumer[String](address)
        .toObservable(vertx)

      logger.debug(s"Registered handler at address ${address}")

      messages.map { msg =>
        logger.debug(s"Received message: ${msg.body}")
        (msg, decode[Request[In]](msg))
      }.filter {
        case (msg, Left(error)) =>
          logger.debug(s"Error decoding message: ${error}")
          msg.reply(error.asJson.noSpaces)
          false
        case (_, Right(_)) =>
          true
      }.collect {
        case (msg, Right(in)) =>
          logger.debug(s"Decoded message: $in")
          (msg, in)
      }
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
