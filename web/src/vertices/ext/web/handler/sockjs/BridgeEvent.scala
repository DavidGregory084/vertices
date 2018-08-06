package vertices
package ext.web.handler.sockjs

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.bridge.BaseBridgeEvent
import io.vertx.ext.bridge.BridgeEventType
import io.vertx.ext.web.handler.sockjs.SockJSSocket
import io.vertx.ext.web.handler.sockjs.{ BridgeEvent => JavaBridgeEvent }
import java.lang.Boolean
import java.lang.Throwable
import java.util.function.Function
import monix.eval.Task

import scala.language.implicitConversions

case class BridgeEvent(val unwrap: JavaBridgeEvent) extends AnyVal {
  // Async handler method
  def setHandler(): Task[Boolean] =
    Task.handle[java.lang.Boolean] { arg0 =>
      unwrap.setHandler(arg0)
    }.map(out => out: Boolean)

  // Standard method
  def complete(arg0: Boolean): Unit =
    unwrap.complete(arg0)

  // Standard method
  def tryComplete(arg0: Boolean): Boolean =
    unwrap.tryComplete(arg0)

  // Standard method
  def result(): Boolean =
    unwrap.result()

  // Standard method
  def compose[U](handler: Handler[Boolean], next: Future[U]): Future[U] =
    unwrap.compose(handler.contramap((in: java.lang.Boolean) => in: Boolean), next)

  // Standard method
  def compose[U](mapper: Function[Boolean,Future[U]]): Future[U] =
    unwrap.compose(mapper)

  // Standard method
  def map[U](mapper: Function[Boolean,U]): Future[U] =
    unwrap.map(mapper)

  // Standard method
  def completer(): Handler[AsyncResult[Boolean]] =
    unwrap.completer()

  // Standard method
  def recover(mapper: Function[Throwable,Future[Boolean]]): Future[Boolean] =
    unwrap.recover(mapper)

  // Standard method
  def otherwise(mapper: Function[Throwable,Boolean]): Future[Boolean] =
    unwrap.otherwise(mapper)

  // Standard method
  def otherwise(value: Boolean): Future[Boolean] =
    unwrap.otherwise(value)

  // Standard method
  def otherwiseEmpty(): Future[Boolean] =
    unwrap.otherwiseEmpty()

  // Wrapper method
  def setRawMessage(message: JsonObject): BridgeEvent =
    BridgeEvent(unwrap.setRawMessage(message))

  // Standard method
  def socket(): SockJSSocket =
    unwrap.socket()
}
object BridgeEvent {
  implicit def javaBridgeEventToVerticesBridgeEvent(j: JavaBridgeEvent): BridgeEvent = apply(j)
  implicit def verticesBridgeEventToJavaBridgeEvent(v: BridgeEvent): JavaBridgeEvent = v.unwrap


}
