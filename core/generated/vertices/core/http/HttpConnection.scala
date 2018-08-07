package vertices
package core.http

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.GoAway
import io.vertx.core.http.Http2Settings
import io.vertx.core.http.{ HttpConnection => JavaHttpConnection }
import io.vertx.core.net.SocketAddress
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import javax.net.ssl.SSLSession
import monix.eval.Task

import scala.language.implicitConversions

case class HttpConnection(val unwrap: JavaHttpConnection) extends AnyVal {
  // Standard method
  def getWindowSize(): Int =
    unwrap.getWindowSize()

  // Wrapper method
  def setWindowSize(windowSize: Int): HttpConnection =
    HttpConnection(unwrap.setWindowSize(windowSize))

  // Wrapper method
  def goAway(errorCode: Long): HttpConnection =
    HttpConnection(unwrap.goAway(errorCode))

  // Wrapper method
  def goAway(errorCode: Long, lastStreamId: Int): HttpConnection =
    HttpConnection(unwrap.goAway(errorCode, lastStreamId))

  // Wrapper method
  def goAway(errorCode: Long, lastStreamId: Int, debugData: Buffer): HttpConnection =
    HttpConnection(unwrap.goAway(errorCode, lastStreamId, debugData))

  // Wrapper method
  def goAwayHandler(handler: Handler[GoAway]): HttpConnection =
    HttpConnection(unwrap.goAwayHandler(handler))

  // Wrapper method
  def shutdownHandler(handler: Handler[Void]): HttpConnection =
    HttpConnection(unwrap.shutdownHandler(handler))

  // Wrapper method
  def shutdown(): HttpConnection =
    HttpConnection(unwrap.shutdown())

  // Wrapper method
  def shutdown(timeoutMs: Long): HttpConnection =
    HttpConnection(unwrap.shutdown(timeoutMs))

  // Wrapper method
  def closeHandler(handler: Handler[Void]): HttpConnection =
    HttpConnection(unwrap.closeHandler(handler))

  // Standard method
  def close(): Unit =
    unwrap.close()

  // Standard method
  def settings(): Http2Settings =
    unwrap.settings()

  // Async handler method
  def updateSettings(settings: Http2Settings): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.updateSettings(settings, completionHandler)
    }.map(_ => ())

  // Standard method
  def remoteSettings(): Http2Settings =
    unwrap.remoteSettings()

  // Wrapper method
  def remoteSettingsHandler(handler: Handler[Http2Settings]): HttpConnection =
    HttpConnection(unwrap.remoteSettingsHandler(handler))

  // Async handler method
  def ping(data: Buffer): Task[Buffer] =
    Task.handle[Buffer] { pongHandler =>
      unwrap.ping(data, pongHandler)
    }

  // Wrapper method
  def pingHandler(handler: Handler[Buffer]): HttpConnection =
    HttpConnection(unwrap.pingHandler(handler))

  // Wrapper method
  def exceptionHandler(handler: Handler[Throwable]): HttpConnection =
    HttpConnection(unwrap.exceptionHandler(handler))

  // Standard method
  def remoteAddress(): SocketAddress =
    unwrap.remoteAddress()

  // Standard method
  def localAddress(): SocketAddress =
    unwrap.localAddress()

  // Standard method
  def isSsl(): Boolean =
    unwrap.isSsl()

  // Standard method
  def indicatedServerName(): String =
    unwrap.indicatedServerName()
}
object HttpConnection {
  implicit def javaHttpConnectionToVerticesHttpConnection(j: JavaHttpConnection): HttpConnection = apply(j)
  implicit def verticesHttpConnectionToJavaHttpConnection(v: HttpConnection): JavaHttpConnection = v.unwrap


}
