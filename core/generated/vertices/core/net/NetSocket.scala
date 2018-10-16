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
import javax.net.ssl.SSLSession
import monix.eval.Task

import scala.language.implicitConversions

/**
 *  Represents a socket-like interface to a TCP connection on either the
 *  client or the server side.
 *  <p>
 *  Instances of this class are created on the client side by an {@link NetClient}
 *  when a connection to a server is made, or on the server side by a {@link NetServer}
 *  when a server accepts a connection.
 *  <p>
 *  It implements both {@link ReadStream} and {@link WriteStream} so it can be used with
 *  {@link io.vertx.core.streams.Pump} to pump data with flow control.
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
case class NetSocket(val unwrap: JavaNetSocket) extends AnyVal {
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


  def exceptionHandler(handler: Handler[Throwable]): NetSocket =
    NetSocket(unwrap.exceptionHandler(handler))


  def handler(handler: Handler[Buffer]): NetSocket =
    NetSocket(unwrap.handler(handler))


  def pause(): NetSocket =
    NetSocket(unwrap.pause())


  def resume(): NetSocket =
    NetSocket(unwrap.resume())


  def fetch(amount: Long): NetSocket =
    NetSocket(unwrap.fetch(amount))


  def endHandler(endHandler: Handler[Void]): NetSocket =
    NetSocket(unwrap.endHandler(endHandler))


  def write(data: Buffer): NetSocket =
    NetSocket(unwrap.write(data))


  def setWriteQueueMaxSize(maxSize: Int): NetSocket =
    NetSocket(unwrap.setWriteQueueMaxSize(maxSize))


  def drainHandler(handler: Handler[Void]): NetSocket =
    NetSocket(unwrap.drainHandler(handler))

  /**
   *  When a {@code NetSocket} is created it automatically registers an event handler with the event bus, the ID of that
   *  handler is given by {@code writeHandlerID}.
   *  <p>
   *  Given this ID, a different event loop can send a buffer to that event handler using the event bus and
   *  that buffer will be received by this instance in its own event loop and written to the underlying connection. This
   *  allows you to write data to other connections which are owned by different event loops.
   * @return the write handler ID
   */
  def writeHandlerID(): String =
    unwrap.writeHandlerID()

  /**
   *  Write a {@link String} to the connection, encoded in UTF-8.
   * @param str  the string to write
   * @return a reference to this, so the API can be used fluently
   */
  def write(str: String): NetSocket =
    NetSocket(unwrap.write(str))

  /**
   *  Write a {@link String} to the connection, encoded using the encoding {@code enc}.
   * @param str  the string to write
   * @param enc  the encoding to use
   * @return a reference to this, so the API can be used fluently
   */
  def write(str: String, enc: String): NetSocket =
    NetSocket(unwrap.write(str, enc))

  /**
   *  Same as {@link #sendFile(String)} but also takes a handler that will be called when the send has completed or
   *  a failure has occurred
   * @param filename  file name of the file to send
   * @param resultHandler  handler
   * @return a reference to this, so the API can be used fluently
   */
  def sendFile(filename: String): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.sendFile(filename, resultHandler)
    }.map(_ => ())

  /**
   *  Same as {@link #sendFile(String, long)} but also takes a handler that will be called when the send has completed or
   *  a failure has occurred
   * @param filename  file name of the file to send
   * @param offset offset
   * @param resultHandler  handler
   * @return a reference to this, so the API can be used fluently
   */
  def sendFile(filename: String, offset: Long): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.sendFile(filename, offset, resultHandler)
    }.map(_ => ())

  /**
   *  Same as {@link #sendFile(String, long, long)} but also takes a handler that will be called when the send has completed or
   *  a failure has occurred
   * @param filename  file name of the file to send
   * @param offset offset
   * @param length length
   * @param resultHandler  handler
   * @return a reference to this, so the API can be used fluently
   */
  def sendFile(filename: String, offset: Long, length: Long): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.sendFile(filename, offset, length, resultHandler)
    }.map(_ => ())

  /**
   * 
   * @return the remote address for this socket
   */
  def remoteAddress(): SocketAddress =
    unwrap.remoteAddress()

  /**
   * 
   * @return the local address for this socket
   */
  def localAddress(): SocketAddress =
    unwrap.localAddress()

  /**
   *  Calls {@link #close()}
   */
  def end(): Unit =
    unwrap.end()

  /**
   *  Close the NetSocket
   */
  def close(): Unit =
    unwrap.close()

  /**
   *  Set a handler that will be called when the NetSocket is closed
   * @param handler  the handler
   * @return a reference to this, so the API can be used fluently
   */
  def closeHandler(handler: Handler[Void]): NetSocket =
    NetSocket(unwrap.closeHandler(handler))

  /**
   *  Upgrade channel to use SSL/TLS. Be aware that for this to work SSL must be configured.
   * @param handler  the handler will be notified when it's upgraded
   * @return a reference to this, so the API can be used fluently
   */
  def upgradeToSsl(handler: Handler[Void]): NetSocket =
    NetSocket(unwrap.upgradeToSsl(handler))

  /**
   *  Upgrade channel to use SSL/TLS. Be aware that for this to work SSL must be configured.
   * @param serverName the server name
   * @param handler  the handler will be notified when it's upgraded
   * @return a reference to this, so the API can be used fluently
   */
  def upgradeToSsl(serverName: String, handler: Handler[Void]): NetSocket =
    NetSocket(unwrap.upgradeToSsl(serverName, handler))

  /**
   * 
   * @return true if this {@link io.vertx.core.net.NetSocket} is encrypted via SSL/TLS.
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
object NetSocket {
  implicit def javaNetSocketToVerticesNetSocket(j: JavaNetSocket): NetSocket = apply(j)
  implicit def verticesNetSocketToJavaNetSocket(v: NetSocket): JavaNetSocket = v.unwrap


}
