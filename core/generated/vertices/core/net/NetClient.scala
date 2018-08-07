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

case class NetClient(val unwrap: JavaNetClient) extends AnyVal {
  // Standard method
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  // Async handler method
  def connect(port: Int, host: String): Task[NetSocket] =
    Task.handle[JavaNetSocket] { connectHandler =>
      unwrap.connect(port, host, connectHandler)
    }.map(out => NetSocket(out))

  // Async handler method
  def connect(port: Int, host: String, serverName: String): Task[NetSocket] =
    Task.handle[JavaNetSocket] { connectHandler =>
      unwrap.connect(port, host, serverName, connectHandler)
    }.map(out => NetSocket(out))

  // Async handler method
  def connect(remoteAddress: SocketAddress): Task[NetSocket] =
    Task.handle[JavaNetSocket] { connectHandler =>
      unwrap.connect(remoteAddress, connectHandler)
    }.map(out => NetSocket(out))

  // Async handler method
  def connect(remoteAddress: SocketAddress, serverName: String): Task[NetSocket] =
    Task.handle[JavaNetSocket] { connectHandler =>
      unwrap.connect(remoteAddress, serverName, connectHandler)
    }.map(out => NetSocket(out))

  // Standard method
  def close(): Unit =
    unwrap.close()
}
object NetClient {
  implicit def javaNetClientToVerticesNetClient(j: JavaNetClient): NetClient = apply(j)
  implicit def verticesNetClientToJavaNetClient(v: NetClient): JavaNetClient = v.unwrap


}
