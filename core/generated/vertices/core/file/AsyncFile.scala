package vertices
package core.file

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.file.{ AsyncFile => JavaAsyncFile }
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import java.lang.Throwable
import java.lang.Void
import monix.eval.Task

import scala.language.implicitConversions

  /**
   *  Represents a file on the file-system which can be read from, or written to asynchronously.
   *  <p>
   *  This class also implements {@link io.vertx.core.streams.ReadStream} and
   *  {@link io.vertx.core.streams.WriteStream}. This allows the data to be pumped to and from
   *  other streams, e.g. an {@link io.vertx.core.http.HttpClientRequest} instance,
   *  using the {@link io.vertx.core.streams.Pump} class
   * @author <a href="http://tfox.org">Tim Fox</a>
   */
case class AsyncFile(val unwrap: JavaAsyncFile) extends AnyVal {
  /**
   *  Same as {@link #end()} but writes some data to the stream before ending.
   */
  def end(t: Buffer): Unit =
    unwrap.end(t)

  /**
   *  This will return {@code true} if there are more bytes in the write queue than the value set using {@link
   *  #setWriteQueueMaxSize}
   * @return true if write queue is full
   */
  def writeQueueFull(): Boolean =
    unwrap.writeQueueFull()


  def handler(handler: Handler[Buffer]): AsyncFile =
    AsyncFile(unwrap.handler(handler))


  def pause(): AsyncFile =
    AsyncFile(unwrap.pause())


  def resume(): AsyncFile =
    AsyncFile(unwrap.resume())


  def endHandler(endHandler: Handler[Void]): AsyncFile =
    AsyncFile(unwrap.endHandler(endHandler))


  def write(data: Buffer): AsyncFile =
    AsyncFile(unwrap.write(data))


  def setWriteQueueMaxSize(maxSize: Int): AsyncFile =
    AsyncFile(unwrap.setWriteQueueMaxSize(maxSize))


  def drainHandler(handler: Handler[Void]): AsyncFile =
    AsyncFile(unwrap.drainHandler(handler))


  def exceptionHandler(handler: Handler[Throwable]): AsyncFile =
    AsyncFile(unwrap.exceptionHandler(handler))


  def fetch(amount: Long): AsyncFile =
    AsyncFile(unwrap.fetch(amount))

  /**
   *  Close the file, see {@link #close()}.
   */
  def end(): Unit =
    unwrap.end()

  /**
   *  Close the file. The actual close happens asynchronously.
   *  The handler will be called when the close is complete, or an error occurs.
   * @param handler  the handler
   */
  def close(): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.close(handler)
    }.map(_ => ())

  /**
   *  Write a {@link io.vertx.core.buffer.Buffer} to the file at position {@code position} in the file, asynchronously.
   *  <p>
   *  If {@code position} lies outside of the current size
   *  of the file, the file will be enlarged to encompass it.
   *  <p>
   *  When multiple writes are invoked on the same file
   *  there are no guarantees as to order in which those writes actually occur
   *  <p>
   *  The handler will be called when the write is complete, or if an error occurs.
   * @param buffer  the buffer to write
   * @param position  the position in the file to write it at
   * @param handler  the handler to call when the write is complete
   * @return a reference to this, so the API can be used fluently
   */
  def write(buffer: Buffer, position: Long): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.write(buffer, position, handler)
    }.map(_ => ())

  /**
   *  Reads {@code length} bytes of data from the file at position {@code position} in the file, asynchronously.
   *  <p>
   *  The read data will be written into the specified {@code Buffer buffer} at position {@code offset}.
   *  <p>
   *  If data is read past the end of the file then zero bytes will be read.<p>
   *  When multiple reads are invoked on the same file there are no guarantees as to order in which those reads actually occur.
   *  <p>
   *  The handler will be called when the close is complete, or if an error occurs.
   * @param buffer  the buffer to read into
   * @param offset  the offset into the buffer where the data will be read
   * @param position  the position in the file where to start reading
   * @param length  the number of bytes to read
   * @param handler  the handler to call when the write is complete
   * @return a reference to this, so the API can be used fluently
   */
  def read(buffer: Buffer, offset: Int, position: Long, length: Int): Task[Buffer] =
    Task.handle[Buffer] { handler =>
      unwrap.read(buffer, offset, position, length, handler)
    }

  /**
   *  Same as {@link #flush} but the handler will be called when the flush is complete or if an error occurs
   */
  def flush(): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.flush(handler)
    }.map(_ => ())

  /**
   *  Sets the position from which data will be read from when using the file as a {@link io.vertx.core.streams.ReadStream}.
   * @param readPos  the position in the file
   * @return a reference to this, so the API can be used fluently
   */
  def setReadPos(readPos: Long): AsyncFile =
    AsyncFile(unwrap.setReadPos(readPos))

  /**
   *  Sets the position from which data will be written when using the file as a {@link io.vertx.core.streams.WriteStream}.
   * @param writePos  the position in the file
   * @return a reference to this, so the API can be used fluently
   */
  def setWritePos(writePos: Long): AsyncFile =
    AsyncFile(unwrap.setWritePos(writePos))

  /**
   *  Sets the buffer size that will be used to read the data from the file. Changing this value will impact how much
   *  the data will be read at a time from the file system.
   * @param readBufferSize the buffer size
   * @return a reference to this, so the API can be used fluently
   */
  def setReadBufferSize(readBufferSize: Int): AsyncFile =
    AsyncFile(unwrap.setReadBufferSize(readBufferSize))
}
object AsyncFile {
  implicit def javaAsyncFileToVerticesAsyncFile(j: JavaAsyncFile): AsyncFile = apply(j)
  implicit def verticesAsyncFileToJavaAsyncFile(v: AsyncFile): JavaAsyncFile = v.unwrap


}
