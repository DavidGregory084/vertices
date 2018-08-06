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
import io.vertx.core.streams.ReadStream
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import monix.eval.Task

import scala.language.implicitConversions

case class HttpServer(val unwrap: JavaHttpServer) extends AnyVal {
  // Standard method
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  // Standard method
  def requestStream(): ReadStream[HttpServerRequest] =
    unwrap.requestStream()

  // Wrapper method
  def requestHandler(handler: Handler[HttpServerRequest]): HttpServer =
    HttpServer(unwrap.requestHandler(handler))

  // Wrapper method
  def connectionHandler(handler: Handler[HttpConnection]): HttpServer =
    HttpServer(unwrap.connectionHandler(handler.contramap((in: JavaHttpConnection) => HttpConnection(in))))

  // Wrapper method
  def exceptionHandler(handler: Handler[Throwable]): HttpServer =
    HttpServer(unwrap.exceptionHandler(handler))

  // Standard method
  def websocketStream(): ReadStream[ServerWebSocket] =
    unwrap.websocketStream()

  // Wrapper method
  def websocketHandler(handler: Handler[ServerWebSocket]): HttpServer =
    HttpServer(unwrap.websocketHandler(handler))

  // Async handler method
  def listen(port: Int, host: String): Task[HttpServer] =
    Task.handle[JavaHttpServer] { listenHandler =>
      unwrap.listen(port, host, listenHandler)
    }.map(out => HttpServer(out))

  // Async handler method
  def listen(port: Int): Task[HttpServer] =
    Task.handle[JavaHttpServer] { listenHandler =>
      unwrap.listen(port, listenHandler)
    }.map(out => HttpServer(out))

  // Async handler method
  def listen(): Task[HttpServer] =
    Task.handle[JavaHttpServer] { listenHandler =>
      unwrap.listen(listenHandler)
    }.map(out => HttpServer(out))

  // Async handler method
  def close(): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.close(completionHandler)
    }.map(_ => ())

  // Standard method
  def actualPort(): Int =
    unwrap.actualPort()
}
object HttpServer {
  implicit def javaHttpServerToVerticesHttpServer(j: JavaHttpServer): HttpServer = apply(j)
  implicit def verticesHttpServerToJavaHttpServer(v: HttpServer): JavaHttpServer = v.unwrap


}
