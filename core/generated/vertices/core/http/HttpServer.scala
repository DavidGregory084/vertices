package vertices
package core.http

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.{ HttpConnection => JavaHttpConnection }
import io.vertx.core.http.{ HttpServer => JavaHttpServer }
import io.vertx.core.metrics.Measured
import io.vertx.core.net.SocketAddress
import io.vertx.core.streams.ReadStream
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import monix.eval.Task

import scala.language.implicitConversions

/**
 *  An HTTP and WebSockets server.
 *  <p>
 *  You receive HTTP requests by providing a {@link #requestHandler}. As requests arrive on the server the handler
 *  will be called with the requests.
 *  <p>
 *  You receive WebSockets by providing a {@link #websocketHandler}. As WebSocket connections arrive on the server, the
 *  WebSocket is passed to the handler.
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
case class HttpServer(val unwrap: JavaHttpServer) extends AnyVal {
  /**
   *  Whether the metrics are enabled for this measured object
   * @implSpec  The default implementation returns {@code false}
   * @return {@code true} if metrics are enabled
   */
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  /**
   *  Return the request stream for the server. As HTTP requests are received by the server,
   *  instances of {@link HttpServerRequest} will be created and passed to the stream {@link io.vertx.core.streams.ReadStream#handler(io.vertx.core.Handler)}.
   * @return the request stream
   */
  def requestStream(): ReadStream[HttpServerRequest] =
    unwrap.requestStream()

  /**
   *  Set the request handler for the server to {@code requestHandler}. As HTTP requests are received by the server,
   *  instances of {@link HttpServerRequest} will be created and passed to this handler.
   * @return a reference to this, so the API can be used fluently
   */
  def requestHandler(handler: Handler[HttpServerRequest]): HttpServer =
    HttpServer(unwrap.requestHandler(handler))

  /**
   *  Set a connection handler for the server.
   * @return a reference to this, so the API can be used fluently
   */
  def connectionHandler(handler: Handler[HttpConnection]): HttpServer =
    HttpServer(unwrap.connectionHandler(handler.contramap((in: JavaHttpConnection) => HttpConnection(in))))

  /**
   *  Set an exception handler called for socket errors happening before the HTTP connection
   *  is established, e.g during the TLS handshake.
   * @param handler the handler to set
   * @return a reference to this, so the API can be used fluently
   */
  def exceptionHandler(handler: Handler[Throwable]): HttpServer =
    HttpServer(unwrap.exceptionHandler(handler))

  /**
   *  Return the websocket stream for the server. If a websocket connect handshake is successful a
   *  new {@link ServerWebSocket} instance will be created and passed to the stream {@link io.vertx.core.streams.ReadStream#handler(io.vertx.core.Handler)}.
   * @return the websocket stream
   */
  def websocketStream(): ReadStream[ServerWebSocket] =
    unwrap.websocketStream()

  /**
   *  Set the websocket handler for the server to {@code wsHandler}. If a websocket connect handshake is successful a
   *  new {@link ServerWebSocket} instance will be created and passed to the handler.
   * @return a reference to this, so the API can be used fluently
   */
  def websocketHandler(handler: Handler[ServerWebSocket]): HttpServer =
    HttpServer(unwrap.websocketHandler(handler))

  /**
   *  Like {@link #listen(int, String)} but supplying a handler that will be called when the server is actually
   *  listening (or has failed).
   * @param port  the port to listen on
   * @param host  the host to listen on
   * @param listenHandler  the listen handler
   */
  def listen(port: Int, host: String): Task[HttpServer] =
    Task.handle[JavaHttpServer] { listenHandler =>
      unwrap.listen(port, host, listenHandler)
    }.map(out => HttpServer(out))

  /**
   *  Tell the server to start listening on the given address supplying
   *  a handler that will be called when the server is actually
   *  listening (or has failed).
   * @param address the address to listen on
   * @param listenHandler  the listen handler
   */
  def listen(address: SocketAddress): Task[HttpServer] =
    Task.handle[JavaHttpServer] { listenHandler =>
      unwrap.listen(address, listenHandler)
    }.map(out => HttpServer(out))

  /**
   *  Like {@link #listen(int)} but supplying a handler that will be called when the server is actually listening (or has failed).
   * @param port  the port to listen on
   * @param listenHandler  the listen handler
   */
  def listen(port: Int): Task[HttpServer] =
    Task.handle[JavaHttpServer] { listenHandler =>
      unwrap.listen(port, listenHandler)
    }.map(out => HttpServer(out))

  /**
   *  Like {@link #listen} but supplying a handler that will be called when the server is actually listening (or has failed).
   * @param listenHandler  the listen handler
   */
  def listen(): Task[HttpServer] =
    Task.handle[JavaHttpServer] { listenHandler =>
      unwrap.listen(listenHandler)
    }.map(out => HttpServer(out))

  /**
   *  Like {@link #close} but supplying a handler that will be called when the server is actually closed (or has failed).
   * @param completionHandler  the handler
   */
  def close(): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.close(completionHandler)
    }.map(_ => ())

  /**
   *  The actual port the server is listening on. This is useful if you bound the server specifying 0 as port number
   *  signifying an ephemeral port
   * @return the actual port the server is listening on.
   */
  def actualPort(): Int =
    unwrap.actualPort()
}
object HttpServer {
  implicit def javaHttpServerToVerticesHttpServer(j: JavaHttpServer): HttpServer = apply(j)
  implicit def verticesHttpServerToJavaHttpServer(v: HttpServer): JavaHttpServer = v.unwrap


}
