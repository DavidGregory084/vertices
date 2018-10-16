package vertices
package core.eventbus

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.eventbus.DeliveryContext
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.eventbus.MessageProducer
import io.vertx.core.eventbus.{ EventBus => JavaEventBus }
import io.vertx.core.eventbus.{ Message => JavaMessage }
import io.vertx.core.eventbus.{ MessageConsumer => JavaMessageConsumer }
import io.vertx.core.metrics.Measured
import java.lang.Object
import java.lang.String
import monix.eval.Task

import scala.language.implicitConversions

  /**
   *  A Vert.x event-bus is a light-weight distributed messaging system which allows different parts of your application,
   *  or different applications and services to communicate with each in a loosely coupled way.
   *  <p>
   *  An event-bus supports publish-subscribe messaging, point-to-point messaging and request-response messaging.
   *  <p>
   *  Message delivery is best-effort and messages can be lost if failure of all or part of the event bus occurs.
   *  <p>
   *  Please refer to the documentation for more information on the event bus.
   * @author <a href="http://tfox.org">Tim Fox</a>
   */
case class EventBus(val unwrap: JavaEventBus) extends AnyVal {
  /**
   *  Whether the metrics are enabled for this measured object
   * @implSpec  The default implementation returns {@code false}
   * @return {@code true} if metrics are enabled
   */
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  /**
   *  Like {@link #send(String, Object)} but specifying a {@code replyHandler} that will be called if the recipient
   *  subsequently replies to the message.
   * @param address  the address to send it to
   * @param message  the message, may be {@code null}
   * @param replyHandler  reply handler will be called when any reply from the recipient is received, may be {@code null}
   * @return a reference to this, so the API can be used fluently
   */
  def send[T](address: String, message: Object): Task[Message[T]] =
    Task.handle[JavaMessage[T]] { replyHandler =>
      unwrap.send(address, message, replyHandler)
    }.map(out => Message[T](out))

  /**
   *  Like {@link #send(String, Object, DeliveryOptions)} but specifying a {@code replyHandler} that will be called if the recipient
   *  subsequently replies to the message.
   * @param address  the address to send it to
   * @param message  the message, may be {@code null}
   * @param options  delivery options
   * @param replyHandler  reply handler will be called when any reply from the recipient is received, may be {@code null}
   * @return a reference to this, so the API can be used fluently
   */
  def send[T](address: String, message: Object, options: DeliveryOptions): Task[Message[T]] =
    Task.handle[JavaMessage[T]] { replyHandler =>
      unwrap.send(address, message, options, replyHandler)
    }.map(out => Message[T](out))

  /**
   *  Publish a message.<p>
   *  The message will be delivered to all handlers registered to the address.
   * @param address  the address to publish it to
   * @param message  the message, may be {@code null}
   * @return a reference to this, so the API can be used fluently
   */
  def publish(address: String, message: Object): EventBus =
    EventBus(unwrap.publish(address, message))

  /**
   *  Like {@link #publish(String, Object)} but specifying {@code options} that can be used to configure the delivery.
   * @param address  the address to publish it to
   * @param message  the message, may be {@code null}
   * @param options  the delivery options
   * @return a reference to this, so the API can be used fluently
   */
  def publish(address: String, message: Object, options: DeliveryOptions): EventBus =
    EventBus(unwrap.publish(address, message, options))

  /**
   *  Create a message consumer against the specified address.
   *  <p>
   *  The returned consumer is not yet registered
   *  at the address, registration will be effective when {@link MessageConsumer#handler(io.vertx.core.Handler)}
   *  is called.
   * @param address  the address that it will register it at
   * @return the event bus message consumer
   */
  def consumer[T](address: String): MessageConsumer[T] =
    MessageConsumer[T](unwrap.consumer(address))

  /**
   *  Create a consumer and register it against the specified address.
   * @param address  the address that will register it at
   * @param handler  the handler that will process the received messages
   * @return the event bus message consumer
   */
  def consumer[T](address: String, handler: Handler[Message[T]]): MessageConsumer[T] =
    MessageConsumer[T](unwrap.consumer(address, handler.contramap((in: JavaMessage[T]) => Message[T](in))))

  /**
   *  Like {@link #consumer(String)} but the address won't be propagated across the cluster.
   * @param address  the address to register it at
   * @return the event bus message consumer
   */
  def localConsumer[T](address: String): MessageConsumer[T] =
    MessageConsumer[T](unwrap.localConsumer(address))

  /**
   *  Like {@link #consumer(String, Handler)} but the address won't be propagated across the cluster.
   * @param address  the address that will register it at
   * @param handler  the handler that will process the received messages
   * @return the event bus message consumer
   */
  def localConsumer[T](address: String, handler: Handler[Message[T]]): MessageConsumer[T] =
    MessageConsumer[T](unwrap.localConsumer(address, handler.contramap((in: JavaMessage[T]) => Message[T](in))))

  /**
   *  Create a message sender against the specified address.
   *  <p>
   *  The returned sender will invoke the {@link #send(String, Object)}
   *  method when the stream {@link io.vertx.core.streams.WriteStream#write(Object)} method is called with the sender
   *  address and the provided data.
   * @param address  the address to send it to
   * @return The sender
   */
  def sender[T](address: String): MessageProducer[T] =
    unwrap.sender(address)

  /**
   *  Like {@link #sender(String)} but specifying delivery options that will be used for configuring the delivery of
   *  the message.
   * @param address  the address to send it to
   * @param options  the delivery options
   * @return The sender
   */
  def sender[T](address: String, options: DeliveryOptions): MessageProducer[T] =
    unwrap.sender(address, options)

  /**
   *  Create a message publisher against the specified address.
   *  <p>
   *  The returned publisher will invoke the {@link #publish(String, Object)}
   *  method when the stream {@link io.vertx.core.streams.WriteStream#write(Object)} method is called with the publisher
   *  address and the provided data.
   * @param address The address to publish it to
   * @return The publisher
   */
  def publisher[T](address: String): MessageProducer[T] =
    unwrap.publisher(address)

  /**
   *  Like {@link #publisher(String)} but specifying delivery options that will be used for configuring the delivery of
   *  the message.
   * @param address  the address to publish it to
   * @param options  the delivery options
   * @return The publisher
   */
  def publisher[T](address: String, options: DeliveryOptions): MessageProducer[T] =
    unwrap.publisher(address, options)

  /**
   *  Unregister a message codec.
   *  <p>
   * @param name  the name of the codec
   * @return a reference to this, so the API can be used fluently
   */
  def unregisterCodec(name: String): EventBus =
    EventBus(unwrap.unregisterCodec(name))

  /**
   *  Add an interceptor that will be called whenever a message is sent from Vert.x
   * @param interceptor  the interceptor
   * @return a reference to this, so the API can be used fluently
   */
  def addOutboundInterceptor[T](interceptor: Handler[DeliveryContext[T]]): EventBus =
    EventBus(unwrap.addOutboundInterceptor(interceptor))

  /**
   *  Remove an interceptor that was added by {@link #addOutboundInterceptor(Handler)}
   * @param interceptor  the interceptor
   * @return a reference to this, so the API can be used fluently
   */
  def removeOutboundInterceptor[T](interceptor: Handler[DeliveryContext[T]]): EventBus =
    EventBus(unwrap.removeOutboundInterceptor(interceptor))

  /**
   *  Add an interceptor that will be called whenever a message is received by Vert.x
   * @param interceptor  the interceptor
   * @return a reference to this, so the API can be used fluently
   */
  def addInboundInterceptor[T](interceptor: Handler[DeliveryContext[T]]): EventBus =
    EventBus(unwrap.addInboundInterceptor(interceptor))

  /**
   *  Remove an interceptor that was added by {@link #addInboundInterceptor(Handler)}
   * @param interceptor  the interceptor
   * @return a reference to this, so the API can be used fluently
   */
  def removeInboundInterceptor[T](interceptor: Handler[DeliveryContext[T]]): EventBus =
    EventBus(unwrap.removeInboundInterceptor(interceptor))
}
object EventBus {
  implicit def javaEventBusToVerticesEventBus(j: JavaEventBus): EventBus = apply(j)
  implicit def verticesEventBusToJavaEventBus(v: EventBus): JavaEventBus = v.unwrap


}
