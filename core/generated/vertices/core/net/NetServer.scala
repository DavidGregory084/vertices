package vertices
package core.net

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.metrics.Measured
import io.vertx.core.net.SocketAddress
import io.vertx.core.net.{ NetServer => JavaNetServer }
import io.vertx.core.net.{ NetSocket => JavaNetSocket }
import io.vertx.core.streams.ReadStream
import java.lang.String
import java.lang.Void
import monix.eval.Task

import scala.language.implicitConversions

/**
 *  Represents a TCP server
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
case class NetServer(val unwrap: JavaNetServer) extends AnyVal {
  /**
   *  Whether the metrics are enabled for this measured object
   * @implSpec  The default implementation returns {@code false}
   * @return {@code true} if metrics are enabled
   */
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  /**
   *  Return the connect stream for this server. The server can only have at most one handler at any one time.
   *  As the server accepts TCP or SSL connections it creates an instance of {@link NetSocket} and passes it to the
   *  connect stream {@link ReadStream#handler(io.vertx.core.Handler)}.
   * @return the connect stream
   */
  def connectStream(): ReadStream[NetSocket] =
    unwrap.connectStream().map(NetSocket.apply)

  /**
   *  Supply a connect handler for this server. The server can only have at most one connect handler at any one time.
   *  As the server accepts TCP or SSL connections it creates an instance of {@link NetSocket} and passes it to the
   *  connect handler.
   * @return a reference to this, so the API can be used fluently
   */
  def connectHandler(handler: Handler[NetSocket]): NetServer =
    NetServer(unwrap.connectHandler(handler.contramap((in: JavaNetSocket) => NetSocket(in))))

  /**
   *  Like {@link #listen} but providing a handler that will be notified when the server is listening, or fails.
   * @param listenHandler  handler that will be notified when listening or failed
   * @return a reference to this, so the API can be used fluently
   */
  def listen(): Task[NetServer] =
    Task.handle[JavaNetServer] { listenHandler =>
      unwrap.listen(listenHandler)
    }.map(out => NetServer(out))

  /**
   *  Like {@link #listen(int, String)} but providing a handler that will be notified when the server is listening, or fails.
   * @param port  the port to listen on
   * @param host  the host to listen on
   * @param listenHandler handler that will be notified when listening or failed
   * @return a reference to this, so the API can be used fluently
   */
  def listen(port: Int, host: String): Task[NetServer] =
    Task.handle[JavaNetServer] { listenHandler =>
      unwrap.listen(port, host, listenHandler)
    }.map(out => NetServer(out))

  /**
   *  Like {@link #listen(int)} but providing a handler that will be notified when the server is listening, or fails.
   * @param port  the port to listen on
   * @param listenHandler handler that will be notified when listening or failed
   * @return a reference to this, so the API can be used fluently
   */
  def listen(port: Int): Task[NetServer] =
    Task.handle[JavaNetServer] { listenHandler =>
      unwrap.listen(port, listenHandler)
    }.map(out => NetServer(out))

  /**
   *  Like {@link #listen(SocketAddress)} but providing a handler that will be notified when the server is listening, or fails.
   * @param localAddress the local address to listen on
   * @param listenHandler handler that will be notified when listening or failed
   * @return a reference to this, so the API can be used fluently
   */
  def listen(localAddress: SocketAddress): Task[NetServer] =
    Task.handle[JavaNetServer] { listenHandler =>
      unwrap.listen(localAddress, listenHandler)
    }.map(out => NetServer(out))

  /**
   *  Like {@link #close} but supplying a handler that will be notified when close is complete.
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
object NetServer {
  implicit def javaNetServerToVerticesNetServer(j: JavaNetServer): NetServer = apply(j)
  implicit def verticesNetServerToJavaNetServer(v: NetServer): JavaNetServer = v.unwrap


}
