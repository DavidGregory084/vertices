// Code generated by vertices Vert.x API generator. DO NOT EDIT.
package vertices
package eventbus

import io.vertx.core.Handler
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.Message
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.eventbus.MessageProducer
import io.vertx.core.eventbus.SendContext
import io.vertx.core.eventbus.{ EventBus => JavaEventBus }
import java.lang.Class
import java.lang.String
import java.lang.Void
import monix.eval.Task

case class EventBus(val unwrap: JavaEventBus) extends AnyVal {

  def addInterceptor(arg0: Handler[SendContext[_]]): EventBus =
    EventBus(unwrap.addInterceptor(arg0))

  def close(): Task[Unit] =
    Task
      .handle[Void] { handler =>
        unwrap.close(handler)
      }
      .map(_ => ())

  def consumer[T](arg0: String): MessageConsumer[T] =
    unwrap.consumer(arg0)

  def consumer[T](arg0: String, arg1: Handler[Message[T]]): MessageConsumer[T] =
    unwrap.consumer(arg0, arg1)

  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  def localConsumer[T](arg0: String): MessageConsumer[T] =
    unwrap.localConsumer(arg0)

  def localConsumer[T](arg0: String, arg1: Handler[Message[T]]): MessageConsumer[T] =
    unwrap.localConsumer(arg0, arg1)

  def publish(arg0: String, arg1: AnyRef): EventBus =
    EventBus(unwrap.publish(arg0, arg1))

  def publish(arg0: String, arg1: AnyRef, arg2: DeliveryOptions): EventBus =
    EventBus(unwrap.publish(arg0, arg1, arg2))

  def publisher[T](arg0: String): MessageProducer[T] =
    unwrap.publisher(arg0)

  def publisher[T](arg0: String, arg1: DeliveryOptions): MessageProducer[T] =
    unwrap.publisher(arg0, arg1)

  def registerCodec(arg0: MessageCodec[_, _]): EventBus =
    EventBus(unwrap.registerCodec(arg0))

  def registerDefaultCodec[T](arg0: Class[T], arg1: MessageCodec[T, _]): EventBus =
    EventBus(unwrap.registerDefaultCodec(arg0, arg1))

  def removeInterceptor(arg0: Handler[SendContext[_]]): EventBus =
    EventBus(unwrap.removeInterceptor(arg0))

  def send[T](arg0: String, arg1: AnyRef): Task[Message[T]] =
    Task.handle[Message[T]] { handler =>
      unwrap.send(arg0, arg1, handler)
    }

  def send[T](arg0: String, arg1: AnyRef, arg2: DeliveryOptions): Task[Message[T]] =
    Task.handle[Message[T]] { handler =>
      unwrap.send(arg0, arg1, arg2, handler)
    }

  def sender[T](arg0: String): MessageProducer[T] =
    unwrap.sender(arg0)

  def sender[T](arg0: String, arg1: DeliveryOptions): MessageProducer[T] =
    unwrap.sender(arg0, arg1)

  def start(): Task[Unit] =
    Task
      .handle[Void] { handler =>
        unwrap.start(handler)
      }
      .map(_ => ())

  def unregisterCodec(arg0: String): EventBus =
    EventBus(unwrap.unregisterCodec(arg0))

  def unregisterDefaultCodec(arg0: Class[_]): EventBus =
    EventBus(unwrap.unregisterDefaultCodec(arg0))
}

object EventBus {}
