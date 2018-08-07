package vertices
package core.http

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.MultiMap
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpFrame
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.{ HttpServerResponse => JavaHttpServerResponse }
import io.vertx.core.streams.WriteStream
import java.lang.CharSequence
import java.lang.Iterable
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import monix.eval.Task

import scala.language.implicitConversions

case class HttpServerResponse(val unwrap: JavaHttpServerResponse) extends AnyVal {
  // Standard method
  def writeQueueFull(): Boolean =
    unwrap.writeQueueFull()

  // Wrapper method
  def exceptionHandler(handler: Handler[Throwable]): HttpServerResponse =
    HttpServerResponse(unwrap.exceptionHandler(handler))

  // Wrapper method
  def write(data: Buffer): HttpServerResponse =
    HttpServerResponse(unwrap.write(data))

  // Wrapper method
  def setWriteQueueMaxSize(maxSize: Int): HttpServerResponse =
    HttpServerResponse(unwrap.setWriteQueueMaxSize(maxSize))

  // Wrapper method
  def drainHandler(handler: Handler[Void]): HttpServerResponse =
    HttpServerResponse(unwrap.drainHandler(handler))

  // Standard method
  def getStatusCode(): Int =
    unwrap.getStatusCode()

  // Wrapper method
  def setStatusCode(statusCode: Int): HttpServerResponse =
    HttpServerResponse(unwrap.setStatusCode(statusCode))

  // Standard method
  def getStatusMessage(): String =
    unwrap.getStatusMessage()

  // Wrapper method
  def setStatusMessage(statusMessage: String): HttpServerResponse =
    HttpServerResponse(unwrap.setStatusMessage(statusMessage))

  // Wrapper method
  def setChunked(chunked: Boolean): HttpServerResponse =
    HttpServerResponse(unwrap.setChunked(chunked))

  // Standard method
  def isChunked(): Boolean =
    unwrap.isChunked()

  // Standard method
  def headers(): MultiMap =
    unwrap.headers()

  // Wrapper method
  def putHeader(name: String, value: String): HttpServerResponse =
    HttpServerResponse(unwrap.putHeader(name, value))

  // Standard method
  def trailers(): MultiMap =
    unwrap.trailers()

  // Wrapper method
  def putTrailer(name: String, value: String): HttpServerResponse =
    HttpServerResponse(unwrap.putTrailer(name, value))

  // Wrapper method
  def closeHandler(handler: Handler[Void]): HttpServerResponse =
    HttpServerResponse(unwrap.closeHandler(handler))

  // Wrapper method
  def endHandler(handler: Handler[Void]): HttpServerResponse =
    HttpServerResponse(unwrap.endHandler(handler))

  // Wrapper method
  def write(chunk: String, enc: String): HttpServerResponse =
    HttpServerResponse(unwrap.write(chunk, enc))

  // Wrapper method
  def write(chunk: String): HttpServerResponse =
    HttpServerResponse(unwrap.write(chunk))

  // Wrapper method
  def writeContinue(): HttpServerResponse =
    HttpServerResponse(unwrap.writeContinue())

  // Standard method
  def end(chunk: String): Unit =
    unwrap.end(chunk)

  // Standard method
  def end(chunk: String, enc: String): Unit =
    unwrap.end(chunk, enc)

  // Standard method
  def end(chunk: Buffer): Unit =
    unwrap.end(chunk)

  // Standard method
  def end(): Unit =
    unwrap.end()

  // Async handler method
  def sendFile(filename: String): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.sendFile(filename, resultHandler)
    }.map(_ => ())

  // Async handler method
  def sendFile(filename: String, offset: Long): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.sendFile(filename, offset, resultHandler)
    }.map(_ => ())

  // Async handler method
  def sendFile(filename: String, offset: Long, length: Long): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.sendFile(filename, offset, length, resultHandler)
    }.map(_ => ())

  // Standard method
  def close(): Unit =
    unwrap.close()

  // Standard method
  def ended(): Boolean =
    unwrap.ended()

  // Standard method
  def closed(): Boolean =
    unwrap.closed()

  // Standard method
  def headWritten(): Boolean =
    unwrap.headWritten()

  // Wrapper method
  def headersEndHandler(handler: Handler[Void]): HttpServerResponse =
    HttpServerResponse(unwrap.headersEndHandler(handler))

  // Wrapper method
  def bodyEndHandler(handler: Handler[Void]): HttpServerResponse =
    HttpServerResponse(unwrap.bodyEndHandler(handler))

  // Standard method
  def bytesWritten(): Long =
    unwrap.bytesWritten()

  // Standard method
  def streamId(): Int =
    unwrap.streamId()

  // Async handler method
  def push(method: HttpMethod, host: String, path: String): Task[HttpServerResponse] =
    Task.handle[JavaHttpServerResponse] { handler =>
      unwrap.push(method, host, path, handler)
    }.map(out => HttpServerResponse(out))

  // Async handler method
  def push(method: HttpMethod, path: String, headers: MultiMap): Task[HttpServerResponse] =
    Task.handle[JavaHttpServerResponse] { handler =>
      unwrap.push(method, path, headers, handler)
    }.map(out => HttpServerResponse(out))

  // Async handler method
  def push(method: HttpMethod, path: String): Task[HttpServerResponse] =
    Task.handle[JavaHttpServerResponse] { handler =>
      unwrap.push(method, path, handler)
    }.map(out => HttpServerResponse(out))

  // Async handler method
  def push(method: HttpMethod, host: String, path: String, headers: MultiMap): Task[HttpServerResponse] =
    Task.handle[JavaHttpServerResponse] { handler =>
      unwrap.push(method, host, path, headers, handler)
    }.map(out => HttpServerResponse(out))

  // Standard method
  def reset(): Unit =
    unwrap.reset()

  // Standard method
  def reset(code: Long): Unit =
    unwrap.reset(code)

  // Wrapper method
  def writeCustomFrame(`type`: Int, flags: Int, payload: Buffer): HttpServerResponse =
    HttpServerResponse(unwrap.writeCustomFrame(`type`, flags, payload))

  // Wrapper method
  def writeCustomFrame(frame: HttpFrame): HttpServerResponse =
    HttpServerResponse(unwrap.writeCustomFrame(frame))
}
object HttpServerResponse {
  implicit def javaHttpServerResponseToVerticesHttpServerResponse(j: JavaHttpServerResponse): HttpServerResponse = apply(j)
  implicit def verticesHttpServerResponseToJavaHttpServerResponse(v: HttpServerResponse): JavaHttpServerResponse = v.unwrap


}
