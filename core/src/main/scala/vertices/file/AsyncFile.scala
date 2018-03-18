// Code generated by vertices Vert.x API generator. DO NOT EDIT.
package vertices
package file

import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.file.{ AsyncFile => JavaAsyncFile }
import java.lang.Throwable
import java.lang.Void
import monix.eval.Task

case class AsyncFile(val unwrap: JavaAsyncFile) extends AnyVal {

  def close(): Task[Unit] =
    Task
      .handle[Void] { handler =>
        unwrap.close(handler)
      }
      .map(_ => ())

  def drainHandler(arg0: Handler[Void]): AsyncFile =
    AsyncFile(unwrap.drainHandler(arg0))

  def end(arg0: Buffer): Unit =
    unwrap.end(arg0)

  def endHandler(arg0: Handler[Void]): AsyncFile =
    AsyncFile(unwrap.endHandler(arg0))

  def exceptionHandler(arg0: Handler[Throwable]): AsyncFile =
    AsyncFile(unwrap.exceptionHandler(arg0))

  def flush(): Task[Unit] =
    Task
      .handle[Void] { handler =>
        unwrap.flush(handler)
      }
      .map(_ => ())

  def handler(arg0: Handler[Buffer]): AsyncFile =
    AsyncFile(unwrap.handler(arg0))

  def pause(): AsyncFile =
    AsyncFile(unwrap.pause())

  def read(arg0: Buffer, arg1: Int, arg2: Long, arg3: Int): Task[Buffer] =
    Task.handle[Buffer] { handler =>
      unwrap.read(arg0, arg1, arg2, arg3, handler)
    }

  def resume(): AsyncFile =
    AsyncFile(unwrap.resume())

  def setReadBufferSize(arg0: Int): AsyncFile =
    AsyncFile(unwrap.setReadBufferSize(arg0))

  def setReadPos(arg0: Long): AsyncFile =
    AsyncFile(unwrap.setReadPos(arg0))

  def setWritePos(arg0: Long): AsyncFile =
    AsyncFile(unwrap.setWritePos(arg0))

  def setWriteQueueMaxSize(arg0: Int): AsyncFile =
    AsyncFile(unwrap.setWriteQueueMaxSize(arg0))

  def write(arg0: Buffer): AsyncFile =
    AsyncFile(unwrap.write(arg0))

  def write(arg0: Buffer, arg1: Long): Task[Unit] =
    Task
      .handle[Void] { handler =>
        unwrap.write(arg0, arg1, handler)
      }
      .map(_ => ())

  def writeQueueFull(): Boolean =
    unwrap.writeQueueFull()
}

object AsyncFile {}
