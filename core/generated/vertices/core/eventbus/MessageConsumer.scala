package vertices
package core.eventbus

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.eventbus.{ Message => JavaMessage }
import io.vertx.core.eventbus.{ MessageConsumer => JavaMessageConsumer }
import io.vertx.core.streams.ReadStream
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import monix.eval.Task

import scala.language.implicitConversions

case class MessageConsumer[T](val unwrap: JavaMessageConsumer[T])  {
  // Wrapper method
  def exceptionHandler(handler: Handler[Throwable]): MessageConsumer[T] =
    MessageConsumer[T](unwrap.exceptionHandler(handler))

  // Wrapper method
  def handler(handler: Handler[Message[T]]): MessageConsumer[T] =
    MessageConsumer[T](unwrap.handler(handler.contramap((in: JavaMessage[T]) => Message[T](in))))

  // Wrapper method
  def pause(): MessageConsumer[T] =
    MessageConsumer[T](unwrap.pause())

  // Wrapper method
  def resume(): MessageConsumer[T] =
    MessageConsumer[T](unwrap.resume())

  // Wrapper method
  def endHandler(endHandler: Handler[Void]): MessageConsumer[T] =
    MessageConsumer[T](unwrap.endHandler(endHandler))

  // Standard method
  def bodyStream(): ReadStream[T] =
    unwrap.bodyStream()

  // Standard method
  def isRegistered(): Boolean =
    unwrap.isRegistered()

  // Standard method
  def address(): String =
    unwrap.address()

  // Wrapper method
  def setMaxBufferedMessages(maxBufferedMessages: Int): MessageConsumer[T] =
    MessageConsumer[T](unwrap.setMaxBufferedMessages(maxBufferedMessages))

  // Standard method
  def getMaxBufferedMessages(): Int =
    unwrap.getMaxBufferedMessages()

  // Async handler method
  def completionHandler(): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.completionHandler(completionHandler)
    }.map(_ => ())

  // Async handler method
  def unregister(): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.unregister(completionHandler)
    }.map(_ => ())
}
object MessageConsumer {
  implicit def javaMessageConsumerToVerticesMessageConsumer[T](j: JavaMessageConsumer[T]): MessageConsumer[T] = apply(j)
  implicit def verticesMessageConsumerToJavaMessageConsumer[T](v: MessageConsumer[T]): JavaMessageConsumer[T] = v.unwrap


}
