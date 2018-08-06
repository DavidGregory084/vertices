package vertices
package core.net

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.net.SocketAddress
import io.vertx.core.net.{ NetSocket => JavaNetSocket }
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import monix.eval.Task

import scala.language.implicitConversions

case class NetSocket(val unwrap: JavaNetSocket) extends AnyVal {
  // Standard method
  def end(t: Buffer): Unit =
    unwrap.end(t)

  // Standard method
  def writeQueueFull(): Boolean =
    unwrap.writeQueueFull()

  // Wrapper method
  def exceptionHandler(handler: Handler[Throwable]): NetSocket =
    NetSocket(unwrap.exceptionHandler(handler))

  // Wrapper method
  def handler(handler: Handler[Buffer]): NetSocket =
    NetSocket(unwrap.handler(handler))

  // Wrapper method
  def pause(): NetSocket =
    NetSocket(unwrap.pause())

  // Wrapper method
  def resume(): NetSocket =
    NetSocket(unwrap.resume())

  // Wrapper method
  def endHandler(endHandler: Handler[Void]): NetSocket =
    NetSocket(unwrap.endHandler(endHandler))

  // Wrapper method
  def write(data: Buffer): NetSocket =
    NetSocket(unwrap.write(data))

  // Wrapper method
  def setWriteQueueMaxSize(maxSize: Int): NetSocket =
    NetSocket(unwrap.setWriteQueueMaxSize(maxSize))

  // Wrapper method
  def drainHandler(handler: Handler[Void]): NetSocket =
    NetSocket(unwrap.drainHandler(handler))

  // Standard method
  def writeHandlerID(): String =
    unwrap.writeHandlerID()

  // Wrapper method
  def write(str: String): NetSocket =
    NetSocket(unwrap.write(str))

  // Wrapper method
  def write(str: String, enc: String): NetSocket =
    NetSocket(unwrap.write(str, enc))

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
  def remoteAddress(): SocketAddress =
    unwrap.remoteAddress()

  // Standard method
  def localAddress(): SocketAddress =
    unwrap.localAddress()

  // Standard method
  def end(): Unit =
    unwrap.end()

  // Standard method
  def close(): Unit =
    unwrap.close()

  // Wrapper method
  def closeHandler(handler: Handler[Void]): NetSocket =
    NetSocket(unwrap.closeHandler(handler))

  // Wrapper method
  def upgradeToSsl(handler: Handler[Void]): NetSocket =
    NetSocket(unwrap.upgradeToSsl(handler))

  // Wrapper method
  def upgradeToSsl(serverName: String, handler: Handler[Void]): NetSocket =
    NetSocket(unwrap.upgradeToSsl(serverName, handler))

  // Standard method
  def isSsl(): Boolean =
    unwrap.isSsl()

  // Standard method
  def indicatedServerName(): String =
    unwrap.indicatedServerName()
}
object NetSocket {
  implicit def javaNetSocketToVerticesNetSocket(j: JavaNetSocket): NetSocket = apply(j)
  implicit def verticesNetSocketToJavaNetSocket(v: NetSocket): JavaNetSocket = v.unwrap


}
