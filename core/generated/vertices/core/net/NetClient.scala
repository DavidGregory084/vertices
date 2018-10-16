package vertices
package core.net

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.metrics.Measured
import io.vertx.core.net.SocketAddress
import io.vertx.core.net.{ NetClient => JavaNetClient }
import io.vertx.core.net.{ NetSocket => JavaNetSocket }
import java.lang.String
import monix.eval.Task

import scala.language.implicitConversions

/**
 *  A TCP client.
 *  <p>
 *  Multiple connections to different servers can be made using the same instance.
 *  <p>
 *  This client supports a configurable number of connection attempts and a configurable
 *  delay between attempts.
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
case class NetClient(val unwrap: JavaNetClient) extends AnyVal {
  /**
   *  Whether the metrics are enabled for this measured object
   * @implSpec  The default implementation returns {@code false}
   * @return {@code true} if metrics are enabled
   */
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  /**
   *  Open a connection to a server at the specific {@code port} and {@code host}.
   *  <p>
   *  {@code host} can be a valid host name or IP address. The connect is done asynchronously and on success, a
   *  {@link NetSocket} instance is supplied via the {@code connectHandler} instance
   * @param port  the port
   * @param host  the host
   * @return a reference to this, so the API can be used fluently
   */
  def connect(port: Int, host: String): Task[NetSocket] =
    Task.handle[JavaNetSocket] { connectHandler =>
      unwrap.connect(port, host, connectHandler)
    }.map(out => NetSocket(out))

  /**
   *  Open a connection to a server at the specific {@code port} and {@code host}.
   *  <p>
   *  {@code host} can be a valid host name or IP address. The connect is done asynchronously and on success, a
   *  {@link NetSocket} instance is supplied via the {@code connectHandler} instance
   * @param port the port
   * @param host the host
   * @param serverName the SNI server name
   * @return a reference to this, so the API can be used fluently
   */
  def connect(port: Int, host: String, serverName: String): Task[NetSocket] =
    Task.handle[JavaNetSocket] { connectHandler =>
      unwrap.connect(port, host, serverName, connectHandler)
    }.map(out => NetSocket(out))

  /**
   *  Open a connection to a server at the specific {@code remoteAddress}.
   *  <p>
   *  The connect is done asynchronously and on success, a {@link NetSocket} instance is supplied via the {@code connectHandler} instance
   * @param remoteAddress the remote address
   * @return a reference to this, so the API can be used fluently
   */
  def connect(remoteAddress: SocketAddress): Task[NetSocket] =
    Task.handle[JavaNetSocket] { connectHandler =>
      unwrap.connect(remoteAddress, connectHandler)
    }.map(out => NetSocket(out))

  /**
   *  Open a connection to a server at the specific {@code remoteAddress}.
   *  <p>
   *  The connect is done asynchronously and on success, a {@link NetSocket} instance is supplied via the {@code connectHandler} instance
   * @param remoteAddress the remote address
   * @param serverName the SNI server name
   * @return a reference to this, so the API can be used fluently
   */
  def connect(remoteAddress: SocketAddress, serverName: String): Task[NetSocket] =
    Task.handle[JavaNetSocket] { connectHandler =>
      unwrap.connect(remoteAddress, serverName, connectHandler)
    }.map(out => NetSocket(out))

  /**
   *  Close the client.
   *  <p>
   *  Any sockets which have not been closed manually will be closed here. The close is asynchronous and may not
   *  complete until some time after the method has returned.
   */
  def close(): Unit =
    unwrap.close()
}
object NetClient {
  implicit def javaNetClientToVerticesNetClient(j: JavaNetClient): NetClient = apply(j)
  implicit def verticesNetClientToJavaNetClient(v: NetClient): JavaNetClient = v.unwrap


}
