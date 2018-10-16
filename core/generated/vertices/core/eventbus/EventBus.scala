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

case class EventBus(val unwrap: JavaEventBus) extends AnyVal {
  // Standard method
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  // Async handler method
  def send[T](address: String, message: Object): Task[Message[T]] =
    Task.handle[JavaMessage[T]] { replyHandler =>
      unwrap.send(address, message, replyHandler)
    }.map(out => Message[T](out))

  // Async handler method
  def send[T](address: String, message: Object, options: DeliveryOptions): Task[Message[T]] =
    Task.handle[JavaMessage[T]] { replyHandler =>
      unwrap.send(address, message, options, replyHandler)
    }.map(out => Message[T](out))

  // Wrapper method
  def publish(address: String, message: Object): EventBus =
    EventBus(unwrap.publish(address, message))

  // Wrapper method
  def publish(address: String, message: Object, options: DeliveryOptions): EventBus =
    EventBus(unwrap.publish(address, message, options))

  // Wrapper method
  def consumer[T](address: String): MessageConsumer[T] =
    MessageConsumer[T](unwrap.consumer(address))

  // Wrapper method
  def consumer[T](address: String, handler: Handler[Message[T]]): MessageConsumer[T] =
    MessageConsumer[T](unwrap.consumer(address, handler.contramap((in: JavaMessage[T]) => Message[T](in))))

  // Wrapper method
  def localConsumer[T](address: String): MessageConsumer[T] =
    MessageConsumer[T](unwrap.localConsumer(address))

  // Wrapper method
  def localConsumer[T](address: String, handler: Handler[Message[T]]): MessageConsumer[T] =
    MessageConsumer[T](unwrap.localConsumer(address, handler.contramap((in: JavaMessage[T]) => Message[T](in))))

  // Standard method
  def sender[T](address: String): MessageProducer[T] =
    unwrap.sender(address)

  // Standard method
  def sender[T](address: String, options: DeliveryOptions): MessageProducer[T] =
    unwrap.sender(address, options)

  // Standard method
  def publisher[T](address: String): MessageProducer[T] =
    unwrap.publisher(address)

  // Standard method
  def publisher[T](address: String, options: DeliveryOptions): MessageProducer[T] =
    unwrap.publisher(address, options)

  // Wrapper method
  def unregisterCodec(name: String): EventBus =
    EventBus(unwrap.unregisterCodec(name))

  // Wrapper method
  def addOutboundInterceptor[T](interceptor: Handler[DeliveryContext[T]]): EventBus =
    EventBus(unwrap.addOutboundInterceptor(interceptor))

  // Wrapper method
  def removeOutboundInterceptor[T](interceptor: Handler[DeliveryContext[T]]): EventBus =
    EventBus(unwrap.removeOutboundInterceptor(interceptor))

  // Wrapper method
  def addInboundInterceptor[T](interceptor: Handler[DeliveryContext[T]]): EventBus =
    EventBus(unwrap.addInboundInterceptor(interceptor))

  // Wrapper method
  def removeInboundInterceptor[T](interceptor: Handler[DeliveryContext[T]]): EventBus =
    EventBus(unwrap.removeInboundInterceptor(interceptor))
}
object EventBus {
  implicit def javaEventBusToVerticesEventBus(j: JavaEventBus): EventBus = apply(j)
  implicit def verticesEventBusToJavaEventBus(v: EventBus): JavaEventBus = v.unwrap


}
