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

case class NetServer(val unwrap: JavaNetServer) extends AnyVal {
  // Standard method
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  // Standard method
  def connectStream(): ReadStream[NetSocket] =
    unwrap.connectStream().map(NetSocket.apply)

  // Wrapper method
  def connectHandler(handler: Handler[NetSocket]): NetServer =
    NetServer(unwrap.connectHandler(handler.contramap((in: JavaNetSocket) => NetSocket(in))))

  // Async handler method
  def listen(): Task[NetServer] =
    Task.handle[JavaNetServer] { listenHandler =>
      unwrap.listen(listenHandler)
    }.map(out => NetServer(out))

  // Async handler method
  def listen(port: Int, host: String): Task[NetServer] =
    Task.handle[JavaNetServer] { listenHandler =>
      unwrap.listen(port, host, listenHandler)
    }.map(out => NetServer(out))

  // Async handler method
  def listen(port: Int): Task[NetServer] =
    Task.handle[JavaNetServer] { listenHandler =>
      unwrap.listen(port, listenHandler)
    }.map(out => NetServer(out))

  // Async handler method
  def listen(localAddress: SocketAddress): Task[NetServer] =
    Task.handle[JavaNetServer] { listenHandler =>
      unwrap.listen(localAddress, listenHandler)
    }.map(out => NetServer(out))

  // Async handler method
  def close(): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.close(completionHandler)
    }.map(_ => ())

  // Standard method
  def actualPort(): Int =
    unwrap.actualPort()
}
object NetServer {
  implicit def javaNetServerToVerticesNetServer(j: JavaNetServer): NetServer = apply(j)
  implicit def verticesNetServerToJavaNetServer(v: NetServer): JavaNetServer = v.unwrap


}
