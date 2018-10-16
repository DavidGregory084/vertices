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

  /**
   *  Represents an HTTP connection.
   *  <p/>
   *  HTTP/1.x connection provides an limited implementation, the following methods are implemented:
   *  <ul>
   *    <li>{@link #close}</li>
   *    <li>{@link #closeHandler}</li>
   *    <li>{@link #exceptionHandler}</li>
   *  </ul>
   * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
   */
case class HttpConnection(val unwrap: JavaHttpConnection) extends AnyVal {
  /**
   * 
   * @return the current connection window size or {@code -1} for HTTP/1.x
   */
  def getWindowSize(): Int =
    unwrap.getWindowSize()

  /**
   *  Update the current connection wide window size to a new size.
   *  <p/>
   *  Increasing this value, gives better performance when several data streams are multiplexed
   *  <p/>
   *  This is not implemented for HTTP/1.x.
   * @param windowSize the new window size
   * @return a reference to this, so the API can be used fluently
   */
  def setWindowSize(windowSize: Int): HttpConnection =
    HttpConnection(unwrap.setWindowSize(windowSize))

  /**
   *  Like {@link #goAway(long, int)} with a last stream id {@code -1} which means to disallow any new stream creation.
   */
  def goAway(errorCode: Long): HttpConnection =
    HttpConnection(unwrap.goAway(errorCode))

  /**
   *  Like {@link #goAway(long, int, Buffer)} with no buffer.
   */
  def goAway(errorCode: Long, lastStreamId: Int): HttpConnection =
    HttpConnection(unwrap.goAway(errorCode, lastStreamId))

  /**
   *  Send a go away frame to the remote endpoint of the connection.
   *  <p/>
   *  <ul>
   *    <li>a {@literal GOAWAY} frame is sent to the to the remote endpoint with the {@code errorCode} and {@code debugData}</li>
   *    <li>any stream created after the stream identified by {@code lastStreamId} will be closed</li>
   *    <li>for an {@literal errorCode} is different than {@code 0} when all the remaining streams are closed this connection will be closed automatically</li>
   *  </ul>
   *  <p/>
   *  This is not implemented for HTTP/1.x.
   * @param errorCode the {@literal GOAWAY} error code
   * @param lastStreamId the last stream id
   * @param debugData additional debug data sent to the remote endpoint
   * @return a reference to this, so the API can be used fluently
   */
  def goAway(errorCode: Long, lastStreamId: Int, debugData: Buffer): HttpConnection =
    HttpConnection(unwrap.goAway(errorCode, lastStreamId, debugData))

  /**
   *  Set an handler called when a {@literal GOAWAY} frame is received.
   *  <p/>
   *  This is not implemented for HTTP/1.x.
   * @param handler the handler
   * @return a reference to this, so the API can be used fluently
   */
  def goAwayHandler(handler: Handler[GoAway]): HttpConnection =
    HttpConnection(unwrap.goAwayHandler(handler))

  /**
   *  Set an handler called when a {@literal GOAWAY} frame has been sent or received and all connections are closed.
   *  <p/>
   *  This is not implemented for HTTP/1.x.
   * @param handler the handler
   * @return a reference to this, so the API can be used fluently
   */
  def shutdownHandler(handler: Handler[Void]): HttpConnection =
    HttpConnection(unwrap.shutdownHandler(handler))

  /**
   *  Initiate a connection shutdown, a go away frame is sent and the connection is closed when all current active streams
   *  are closed or after a time out of 30 seconds.
   *  <p/>
   *  This is not implemented for HTTP/1.x.
   * @return a reference to this, so the API can be used fluently
   */
  def shutdown(): HttpConnection =
    HttpConnection(unwrap.shutdown())

  /**
   *  Initiate a connection shutdown, a go away frame is sent and the connection is closed when all current streams
   *  will be closed or the {@code timeout} is fired.
   *  <p/>
   *  This is not implemented for HTTP/1.x.
   * @param timeoutMs the timeout in milliseconds
   * @return a reference to this, so the API can be used fluently
   */
  def shutdown(timeoutMs: Long): HttpConnection =
    HttpConnection(unwrap.shutdown(timeoutMs))

  /**
   *  Set a close handler. The handler will get notified when the connection is closed.
   * @param handler the handler to be notified
   * @return a reference to this, so the API can be used fluently
   */
  def closeHandler(handler: Handler[Void]): HttpConnection =
    HttpConnection(unwrap.closeHandler(handler))

  /**
   *  Close the connection and all the currently active streams.
   *  <p/>
   *  An HTTP/2 connection will send a {@literal GOAWAY} frame before.
   */
  def close(): Unit =
    unwrap.close()

  /**
   * 
   * @return the latest server settings acknowledged by the remote endpoint - this is not implemented for HTTP/1.x
   */
  def settings(): Http2Settings =
    unwrap.settings()

  /**
   *  Send to the remote endpoint an update of this endpoint settings
   *  <p/>
   *  The {@code completionHandler} will be notified when the remote endpoint has acknowledged the settings.
   *  <p/>
   *  This is not implemented for HTTP/1.x.
   * @param settings the new settings
   * @param completionHandler the handler notified when the settings have been acknowledged by the remote endpoint
   * @return a reference to this, so the API can be used fluently
   */
  def updateSettings(settings: Http2Settings): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.updateSettings(settings, completionHandler)
    }.map(_ => ())

  /**
   * 
   * @return the current remote endpoint settings for this connection - this is not implemented for HTTP/1.x
   */
  def remoteSettings(): Http2Settings =
    unwrap.remoteSettings()

  /**
   *  Set an handler that is called when remote endpoint {@link Http2Settings} are updated.
   *  <p/>
   *  This is not implemented for HTTP/1.x.
   * @param handler the handler for remote endpoint settings
   * @return a reference to this, so the API can be used fluently
   */
  def remoteSettingsHandler(handler: Handler[Http2Settings]): HttpConnection =
    HttpConnection(unwrap.remoteSettingsHandler(handler))

  /**
   *  Send a {@literal PING} frame to the remote endpoint.
   *  <p/>
   *  This is not implemented for HTTP/1.x.
   * @param data the 8 bytes data of the frame
   * @param pongHandler an async result handler notified with pong reply or the failure
   * @return a reference to this, so the API can be used fluently
   */
  def ping(data: Buffer): Task[Buffer] =
    Task.handle[Buffer] { pongHandler =>
      unwrap.ping(data, pongHandler)
    }

  /**
   *  Set an handler notified when a {@literal PING} frame is received from the remote endpoint.
   *  <p/>
   *  This is not implemented for HTTP/1.x.
   * @param handler the handler to be called when a {@literal PING} is received
   * @return a reference to this, so the API can be used fluently
   */
  def pingHandler(handler: Handler[Buffer]): HttpConnection =
    HttpConnection(unwrap.pingHandler(handler))

  /**
   *  Set an handler called when a connection error happens
   * @param handler the handler
   * @return a reference to this, so the API can be used fluently
   */
  def exceptionHandler(handler: Handler[Throwable]): HttpConnection =
    HttpConnection(unwrap.exceptionHandler(handler))

  /**
   * 
   * @return the remote address for this connection
   */
  def remoteAddress(): SocketAddress =
    unwrap.remoteAddress()

  /**
   * 
   * @return the remote address for this connection
   */
  def localAddress(): SocketAddress =
    unwrap.localAddress()

  /**
   * 
   * @return true if this {@link io.vertx.core.http.HttpConnection} is encrypted via SSL/TLS.
   */
  def isSsl(): Boolean =
    unwrap.isSsl()

  /**
   *  Returns the SNI server name presented during the SSL handshake by the client.
   * @return the indicated server name
   */
  def indicatedServerName(): String =
    unwrap.indicatedServerName()
}
object HttpConnection {
  implicit def javaHttpConnectionToVerticesHttpConnection(j: JavaHttpConnection): HttpConnection = apply(j)
  implicit def verticesHttpConnectionToJavaHttpConnection(v: HttpConnection): JavaHttpConnection = v.unwrap


}
