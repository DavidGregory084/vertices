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

case class AsyncFile(val unwrap: JavaAsyncFile) extends AnyVal {
  // Standard method
  def end(t: Buffer): Unit =
    unwrap.end(t)

  // Standard method
  def writeQueueFull(): Boolean =
    unwrap.writeQueueFull()

  // Wrapper method
  def handler(handler: Handler[Buffer]): AsyncFile =
    AsyncFile(unwrap.handler(handler))

  // Wrapper method
  def pause(): AsyncFile =
    AsyncFile(unwrap.pause())

  // Wrapper method
  def resume(): AsyncFile =
    AsyncFile(unwrap.resume())

  // Wrapper method
  def endHandler(endHandler: Handler[Void]): AsyncFile =
    AsyncFile(unwrap.endHandler(endHandler))

  // Wrapper method
  def write(data: Buffer): AsyncFile =
    AsyncFile(unwrap.write(data))

  // Wrapper method
  def setWriteQueueMaxSize(maxSize: Int): AsyncFile =
    AsyncFile(unwrap.setWriteQueueMaxSize(maxSize))

  // Wrapper method
  def drainHandler(handler: Handler[Void]): AsyncFile =
    AsyncFile(unwrap.drainHandler(handler))

  // Wrapper method
  def exceptionHandler(handler: Handler[Throwable]): AsyncFile =
    AsyncFile(unwrap.exceptionHandler(handler))

  // Standard method
  def end(): Unit =
    unwrap.end()

  // Async handler method
  def close(): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.close(handler)
    }.map(_ => ())

  // Async handler method
  def write(buffer: Buffer, position: Long): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.write(buffer, position, handler)
    }.map(_ => ())

  // Async handler method
  def read(buffer: Buffer, offset: Int, position: Long, length: Int): Task[Buffer] =
    Task.handle[Buffer] { handler =>
      unwrap.read(buffer, offset, position, length, handler)
    }

  // Async handler method
  def flush(): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.flush(handler)
    }.map(_ => ())

  // Wrapper method
  def setReadPos(readPos: Long): AsyncFile =
    AsyncFile(unwrap.setReadPos(readPos))

  // Wrapper method
  def setWritePos(writePos: Long): AsyncFile =
    AsyncFile(unwrap.setWritePos(writePos))

  // Wrapper method
  def setReadBufferSize(readBufferSize: Int): AsyncFile =
    AsyncFile(unwrap.setReadBufferSize(readBufferSize))
}
object AsyncFile {
  implicit def javaAsyncFileToVerticesAsyncFile(j: JavaAsyncFile): AsyncFile = apply(j)
  implicit def verticesAsyncFileToJavaAsyncFile(v: AsyncFile): JavaAsyncFile = v.unwrap


}
