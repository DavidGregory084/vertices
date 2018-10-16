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

/**
 *  Represents a server-side HTTP response.
 *  <p>
 *  An instance of this is created and associated to every instance of
 *  {@link HttpServerRequest} that.
 *  <p>
 *  It allows the developer to control the HTTP response that is sent back to the
 *  client for a particular HTTP request.
 *  <p>
 *  It contains methods that allow HTTP headers and trailers to be set, and for a body to be written out to the response.
 *  <p>
 *  It also allows files to be streamed by the kernel directly from disk to the
 *  outgoing HTTP connection, bypassing user space altogether (where supported by
 *  the underlying operating system). This is a very efficient way of
 *  serving files from the server since buffers do not have to be read one by one
 *  from the file and written to the outgoing socket.
 *  <p>
 *  It implements {@link io.vertx.core.streams.WriteStream} so it can be used with
 *  {@link io.vertx.core.streams.Pump} to pump data with flow control.
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
case class HttpServerResponse(val unwrap: JavaHttpServerResponse) extends AnyVal {
  /**
   *  This will return {@code true} if there are more bytes in the write queue than the value set using {@link
   *  #setWriteQueueMaxSize}
   * @return true if write queue is full
   */
  def writeQueueFull(): Boolean =
    unwrap.writeQueueFull()


  def exceptionHandler(handler: Handler[Throwable]): HttpServerResponse =
    HttpServerResponse(unwrap.exceptionHandler(handler))


  def write(data: Buffer): HttpServerResponse =
    HttpServerResponse(unwrap.write(data))


  def setWriteQueueMaxSize(maxSize: Int): HttpServerResponse =
    HttpServerResponse(unwrap.setWriteQueueMaxSize(maxSize))


  def drainHandler(handler: Handler[Void]): HttpServerResponse =
    HttpServerResponse(unwrap.drainHandler(handler))

  /**
   * 
   * @return the HTTP status code of the response. The default is {@code 200} representing {@code OK}.
   */
  def getStatusCode(): Int =
    unwrap.getStatusCode()

  /**
   *  Set the status code. If the status message hasn't been explicitly set, a default status message corresponding
   *  to the code will be looked-up and used.
   * @return a reference to this, so the API can be used fluently
   */
  def setStatusCode(statusCode: Int): HttpServerResponse =
    HttpServerResponse(unwrap.setStatusCode(statusCode))

  /**
   * 
   * @return the HTTP status message of the response. If this is not specified a default value will be used depending on what
   *  {@link #setStatusCode} has been set to.
   */
  def getStatusMessage(): String =
    unwrap.getStatusMessage()

  /**
   *  Set the status message
   * @return a reference to this, so the API can be used fluently
   */
  def setStatusMessage(statusMessage: String): HttpServerResponse =
    HttpServerResponse(unwrap.setStatusMessage(statusMessage))

  /**
   *  If {@code chunked} is {@code true}, this response will use HTTP chunked encoding, and each call to write to the body
   *  will correspond to a new HTTP chunk sent on the wire.
   *  <p>
   *  If chunked encoding is used the HTTP header {@code Transfer-Encoding} with a value of {@code Chunked} will be
   *  automatically inserted in the response.
   *  <p>
   *  If {@code chunked} is {@code false}, this response will not use HTTP chunked encoding, and therefore the total size
   *  of any data that is written in the respone body must be set in the {@code Content-Length} header <b>before</b> any
   *  data is written out.
   *  <p>
   *  An HTTP chunked response is typically used when you do not know the total size of the request body up front.
   * @return a reference to this, so the API can be used fluently
   */
  def setChunked(chunked: Boolean): HttpServerResponse =
    HttpServerResponse(unwrap.setChunked(chunked))

  /**
   * 
   * @return is the response chunked?
   */
  def isChunked(): Boolean =
    unwrap.isChunked()

  /**
   * 
   * @return The HTTP headers
   */
  def headers(): MultiMap =
    unwrap.headers()

  /**
   *  Put an HTTP header
   * @param name  the header name
   * @param value  the header value.
   * @return a reference to this, so the API can be used fluently
   */
  def putHeader(name: String, value: String): HttpServerResponse =
    HttpServerResponse(unwrap.putHeader(name, value))

  /**
   * 
   * @return The HTTP trailers
   */
  def trailers(): MultiMap =
    unwrap.trailers()

  /**
   *  Put an HTTP trailer
   * @param name  the trailer name
   * @param value  the trailer value
   * @return a reference to this, so the API can be used fluently
   */
  def putTrailer(name: String, value: String): HttpServerResponse =
    HttpServerResponse(unwrap.putTrailer(name, value))

  /**
   *  Set a close handler for the response, this is called when the underlying connection is closed and the response
   *  was still using the connection.
   *  <p>
   *  For HTTP/1.x it is called when the connection is closed before {@code end()} is called, therefore it is not
   *  guaranteed to be called.
   *  <p>
   *  For HTTP/2 it is called when the related stream is closed, and therefore it will be always be called.
   * @param handler  the handler
   * @return a reference to this, so the API can be used fluently
   */
  def closeHandler(handler: Handler[Void]): HttpServerResponse =
    HttpServerResponse(unwrap.closeHandler(handler))

  /**
   *  Set an end handler for the response. This will be called when the response is disposed to allow consistent cleanup
   *  of the response.
   * @param handler  the handler
   * @return a reference to this, so the API can be used fluently
   */
  def endHandler(handler: Handler[Void]): HttpServerResponse =
    HttpServerResponse(unwrap.endHandler(handler))

  /**
   *  Write a {@link String} to the response body, encoded using the encoding {@code enc}.
   * @param chunk  the string to write
   * @param enc  the encoding to use
   * @return a reference to this, so the API can be used fluently
   */
  def write(chunk: String, enc: String): HttpServerResponse =
    HttpServerResponse(unwrap.write(chunk, enc))

  /**
   *  Write a {@link String} to the response body, encoded in UTF-8.
   * @param chunk  the string to write
   * @return a reference to this, so the API can be used fluently
   */
  def write(chunk: String): HttpServerResponse =
    HttpServerResponse(unwrap.write(chunk))

  /**
   *  Used to write an interim 100 Continue response to signify that the client should send the rest of the request.
   *  Must only be used if the request contains an "Expect:100-Continue" header
   * @return a reference to this, so the API can be used fluently
   */
  def writeContinue(): HttpServerResponse =
    HttpServerResponse(unwrap.writeContinue())

  /**
   *  Same as {@link #end(Buffer)} but writes a String in UTF-8 encoding before ending the response.
   * @param chunk  the string to write before ending the response
   */
  def end(chunk: String): Unit =
    unwrap.end(chunk)

  /**
   *  Same as {@link #end(Buffer)} but writes a String with the specified encoding before ending the response.
   * @param chunk  the string to write before ending the response
   * @param enc  the encoding to use
   */
  def end(chunk: String, enc: String): Unit =
    unwrap.end(chunk, enc)

  /**
   *  Same as {@link #end()} but writes some data to the response body before ending. If the response is not chunked and
   *  no other data has been written then the @code{Content-Length} header will be automatically set.
   * @param chunk  the buffer to write before ending the response
   */
  def end(chunk: Buffer): Unit =
    unwrap.end(chunk)

  /**
   *  Ends the response. If no data has been written to the response body,
   *  the actual response won't get written until this method gets called.
   *  <p>
   *  Once the response has ended, it cannot be used any more.
   */
  def end(): Unit =
    unwrap.end()

  /**
   *  Like {@link #sendFile(String)} but providing a handler which will be notified once the file has been completely
   *  written to the wire.
   * @param filename path to the file to serve
   * @param resultHandler  handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def sendFile(filename: String): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.sendFile(filename, resultHandler)
    }.map(_ => ())

  /**
   *  Like {@link #sendFile(String, long)} but providing a handler which will be notified once the file has been completely
   *  written to the wire.
   * @param filename path to the file to serve
   * @param offset the offset to serve from
   * @param resultHandler  handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def sendFile(filename: String, offset: Long): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.sendFile(filename, offset, resultHandler)
    }.map(_ => ())

  /**
   *  Like {@link #sendFile(String, long, long)} but providing a handler which will be notified once the file has been
   *  completely written to the wire.
   * @param filename path to the file to serve
   * @param offset the offset to serve from
   * @param length the length to serve to
   * @param resultHandler  handler that will be called on completion
   * @return a reference to this, so the API can be used fluently
   */
  def sendFile(filename: String, offset: Long, length: Long): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.sendFile(filename, offset, length, resultHandler)
    }.map(_ => ())

  /**
   *  Close the underlying TCP connection corresponding to the request.
   */
  def close(): Unit =
    unwrap.close()

  /**
   * 
   * @return has the response already ended?
   */
  def ended(): Boolean =
    unwrap.ended()

  /**
   * 
   * @return has the underlying TCP connection corresponding to the request already been closed?
   */
  def closed(): Boolean =
    unwrap.closed()

  /**
   * 
   * @return have the headers for the response already been written?
   */
  def headWritten(): Boolean =
    unwrap.headWritten()

  /**
   *  Provide a handler that will be called just before the headers are written to the wire.<p>
   *  This provides a hook allowing you to add any more headers or do any more operations before this occurs.
   * @param handler  the handler
   * @return a reference to this, so the API can be used fluently
   */
  def headersEndHandler(handler: Handler[Void]): HttpServerResponse =
    HttpServerResponse(unwrap.headersEndHandler(handler))

  /**
   *  Provides a handler that will be called after the last part of the body is written to the wire.
   *  The handler is called asynchronously of when the response has been received by the client.
   *  This provides a hook allowing you to do more operations once the request has been sent over the wire
   *  such as resource cleanup.
   * @param handler  the handler
   * @return a reference to this, so the API can be used fluently
   */
  def bodyEndHandler(handler: Handler[Void]): HttpServerResponse =
    HttpServerResponse(unwrap.bodyEndHandler(handler))

  /**
   * 
   * @return the total number of bytes written for the body of the response.
   */
  def bytesWritten(): Long =
    unwrap.bytesWritten()

  /**
   * 
   * @return the id of the stream of this response, {@literal -1} for HTTP/1.x
   */
  def streamId(): Int =
    unwrap.streamId()

  /**
   *  Like {@link #push(HttpMethod, String, String, MultiMap, Handler)} with no headers.
   */
  def push(method: HttpMethod, host: String, path: String): Task[HttpServerResponse] =
    Task.handle[JavaHttpServerResponse] { handler =>
      unwrap.push(method, host, path, handler)
    }.map(out => HttpServerResponse(out))

  /**
   *  Like {@link #push(HttpMethod, String, String, MultiMap, Handler)} with the host copied from the current request.
   */
  def push(method: HttpMethod, path: String, headers: MultiMap): Task[HttpServerResponse] =
    Task.handle[JavaHttpServerResponse] { handler =>
      unwrap.push(method, path, headers, handler)
    }.map(out => HttpServerResponse(out))

  /**
   *  Like {@link #push(HttpMethod, String, String, MultiMap, Handler)} with the host copied from the current request.
   */
  def push(method: HttpMethod, path: String): Task[HttpServerResponse] =
    Task.handle[JavaHttpServerResponse] { handler =>
      unwrap.push(method, path, handler)
    }.map(out => HttpServerResponse(out))

  /**
   *  Push a response to the client.<p/>
   * 
   *  The {@code handler} will be notified with a <i>success</i> when the push can be sent and with
   *  a <i>failure</i> when the client has disabled push or reset the push before it has been sent.<p/>
   * 
   *  The {@code handler} may be queued if the client has reduced the maximum number of streams the server can push
   *  concurrently.<p/>
   * 
   *  Push can be sent only for peer initiated streams and if the response is not ended.
   * @param method the method of the promised request
   * @param host the host of the promised request
   * @param path the path of the promised request
   * @param headers the headers of the promised request
   * @param handler the handler notified when the response can be written
   * @return a reference to this, so the API can be used fluently
   */
  def push(method: HttpMethod, host: String, path: String, headers: MultiMap): Task[HttpServerResponse] =
    Task.handle[JavaHttpServerResponse] { handler =>
      unwrap.push(method, host, path, headers, handler)
    }.map(out => HttpServerResponse(out))

  /**
   *  Reset this HTTP/2 stream with the error code {@code 0}.
   */
  def reset(): Unit =
    unwrap.reset()

  /**
   *  Reset this HTTP/2 stream with the error {@code code}.
   * @param code the error code
   */
  def reset(code: Long): Unit =
    unwrap.reset(code)

  /**
   *  Write an HTTP/2 frame to the response, allowing to extend the HTTP/2 protocol.<p>
   * 
   *  The frame is sent immediatly and is not subject to flow control.
   * @param type the 8-bit frame type
   * @param flags the 8-bit frame flags
   * @param payload the frame payload
   * @return a reference to this, so the API can be used fluently
   */
  def writeCustomFrame(`type`: Int, flags: Int, payload: Buffer): HttpServerResponse =
    HttpServerResponse(unwrap.writeCustomFrame(`type`, flags, payload))

  /**
   *  Like {@link #writeCustomFrame(int, int, Buffer)} but with an {@link HttpFrame}.
   * @param frame the frame to write
   */
  def writeCustomFrame(frame: HttpFrame): HttpServerResponse =
    HttpServerResponse(unwrap.writeCustomFrame(frame))
}
object HttpServerResponse {
  implicit def javaHttpServerResponseToVerticesHttpServerResponse(j: JavaHttpServerResponse): HttpServerResponse = apply(j)
  implicit def verticesHttpServerResponseToJavaHttpServerResponse(v: HttpServerResponse): JavaHttpServerResponse = v.unwrap


}
