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

/**
 *  Represents a message that is received from the event bus in a handler.
 *  <p>
 *  Messages have a {@link #body}, which can be null, and also {@link #headers}, which can be empty.
 *  <p>
 *  If the message was sent specifying a reply handler, it can be replied to using {@link #reply}.
 *  <p>
 *  If you want to notify the sender that processing failed, then {@link #fail} can be called.
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
case class Message[T](val unwrap: JavaMessage[T])  {
  /**
   *  The address the message was sent to
   */
  def address(): String =
    unwrap.address()

  /**
   *  Multi-map of message headers. Can be empty
   * @return  the headers
   */
  def headers(): MultiMap =
    unwrap.headers()

  /**
   *  The body of the message. Can be null.
   * @return  the body, or null.
   */
  def body(): T =
    unwrap.body()

  /**
   *  The reply address. Can be null.
   * @return the reply address, or null, if message was sent without a reply handler.
   */
  def replyAddress(): String =
    unwrap.replyAddress()

  /**
   *  Signals if this message represents a send or publish event.
   * @return true if this is a send.
   */
  def isSend(): Boolean =
    unwrap.isSend()

  /**
   *  The same as {@code reply(R message)} but you can specify handler for the reply - i.e.
   *  to receive the reply to the reply.
   * @param message  the message to reply with.
   * @param replyHandler  the reply handler for the reply.
   */
  def reply[R](message: Object): Task[Message[R]] =
    Task.handle[JavaMessage[R]] { replyHandler =>
      unwrap.reply(message, replyHandler)
    }.map(out => Message[R](out))

  /**
   *  The same as {@code reply(R message, DeliveryOptions)} but you can specify handler for the reply - i.e.
   *  to receive the reply to the reply.
   * @param message  the reply message
   * @param options  the delivery options
   * @param replyHandler  the reply handler for the reply.
   */
  def reply[R](message: Object, options: DeliveryOptions): Task[Message[R]] =
    Task.handle[JavaMessage[R]] { replyHandler =>
      unwrap.reply(message, options, replyHandler)
    }.map(out => Message[R](out))

  /**
   *  Signal to the sender that processing of this message failed.
   *  <p>
   *  If the message was sent specifying a result handler
   *  the handler will be called with a failure corresponding to the failure code and message specified here.
   * @param failureCode A failure code to pass back to the sender
   * @param message A message to pass back to the sender
   */
  def fail(failureCode: Int, message: String): Unit =
    unwrap.fail(failureCode, message)
}
object Message {
  implicit def javaMessageToVerticesMessage[T](j: JavaMessage[T]): Message[T] = apply(j)
  implicit def verticesMessageToJavaMessage[T](v: Message[T]): JavaMessage[T] = v.unwrap


}
