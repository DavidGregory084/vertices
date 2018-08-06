package vertices
package core.eventbus

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.MultiMap
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.{ Message => JavaMessage }
import java.lang.Object
import java.lang.String
import monix.eval.Task

import scala.language.implicitConversions

case class Message[T](val unwrap: JavaMessage[T])  {
  // Standard method
  def address(): String =
    unwrap.address()

  // Standard method
  def headers(): MultiMap =
    unwrap.headers()

  // Standard method
  def body(): T =
    unwrap.body()

  // Standard method
  def replyAddress(): String =
    unwrap.replyAddress()

  // Standard method
  def isSend(): Boolean =
    unwrap.isSend()

  // Async handler method
  def reply[R](message: Object): Task[Message[R]] =
    Task.handle[JavaMessage[R]] { replyHandler =>
      unwrap.reply(message, replyHandler)
    }.map(out => Message[R](out))

  // Async handler method
  def reply[R](message: Object, options: DeliveryOptions): Task[Message[R]] =
    Task.handle[JavaMessage[R]] { replyHandler =>
      unwrap.reply(message, options, replyHandler)
    }.map(out => Message[R](out))

  // Standard method
  def fail(failureCode: Int, message: String): Unit =
    unwrap.fail(failureCode, message)
}
object Message {
  implicit def javaMessageToVerticesMessage[T](j: JavaMessage[T]): Message[T] = apply(j)
  implicit def verticesMessageToJavaMessage[T](v: Message[T]): JavaMessage[T] = v.unwrap


}
