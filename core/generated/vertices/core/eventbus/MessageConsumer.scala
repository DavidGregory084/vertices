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

/**
 *  An event bus consumer object representing a stream of message to an {@link EventBus} address that can
 *  be read from.
 *  <p>
 *  The {@link EventBus#consumer(String)} or {@link EventBus#localConsumer(String)}
 *  creates a new consumer, the returned consumer is not yet registered against the event bus. Registration
 *  is effective after the {@link #handler(io.vertx.core.Handler)} method is invoked.<p>
 * 
 *  The consumer is unregistered from the event bus using the {@link #unregister()} method or by calling the
 *  {@link #handler(io.vertx.core.Handler)} with a null value..
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
case class MessageConsumer[T](val unwrap: JavaMessageConsumer[T])  {

  def exceptionHandler(handler: Handler[Throwable]): MessageConsumer[T] =
    MessageConsumer[T](unwrap.exceptionHandler(handler))


  def handler(handler: Handler[Message[T]]): MessageConsumer[T] =
    MessageConsumer[T](unwrap.handler(handler.contramap((in: JavaMessage[T]) => Message[T](in))))


  def pause(): MessageConsumer[T] =
    MessageConsumer[T](unwrap.pause())


  def resume(): MessageConsumer[T] =
    MessageConsumer[T](unwrap.resume())


  def fetch(amount: Long): MessageConsumer[T] =
    MessageConsumer[T](unwrap.fetch(amount))


  def endHandler(endHandler: Handler[Void]): MessageConsumer[T] =
    MessageConsumer[T](unwrap.endHandler(endHandler))

  /**
   * 
   * @return a read stream for the body of the message stream.
   */
  def bodyStream(): ReadStream[T] =
    unwrap.bodyStream()

  /**
   * 
   * @return true if the current consumer is registered
   */
  def isRegistered(): Boolean =
    unwrap.isRegistered()

  /**
   * 
   * @return The address the handler was registered with.
   */
  def address(): String =
    unwrap.address()

  /**
   *  Set the number of messages this registration will buffer when this stream is paused. The default
   *  value is <code>1000</code>.
   *  <p>
   *  When a new value is set, buffered messages may be discarded to reach the new value. The most recent
   *  messages will be kept.
   * @param maxBufferedMessages the maximum number of messages that can be buffered
   * @return this registration
   */
  def setMaxBufferedMessages(maxBufferedMessages: Int): MessageConsumer[T] =
    MessageConsumer[T](unwrap.setMaxBufferedMessages(maxBufferedMessages))

  /**
   * 
   * @return the maximum number of messages that can be buffered when this stream is paused
   */
  def getMaxBufferedMessages(): Int =
    unwrap.getMaxBufferedMessages()

  /**
   *  Optional method which can be called to indicate when the registration has been propagated across the cluster.
   * @param completionHandler the completion handler
   */
  def completionHandler(): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.completionHandler(completionHandler)
    }.map(_ => ())

  /**
   *  Unregisters the handler which created this registration
   * @param completionHandler the handler called when the unregister is done. For example in a cluster when all nodes of the
   *  event bus have been unregistered.
   */
  def unregister(): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.unregister(completionHandler)
    }.map(_ => ())
}
object MessageConsumer {
  implicit def javaMessageConsumerToVerticesMessageConsumer[T](j: JavaMessageConsumer[T]): MessageConsumer[T] = apply(j)
  implicit def verticesMessageConsumerToJavaMessageConsumer[T](v: MessageConsumer[T]): JavaMessageConsumer[T] = v.unwrap


}
