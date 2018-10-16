package vertices
package core.datagram

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.datagram.DatagramPacket
import io.vertx.core.datagram.{ DatagramSocket => JavaDatagramSocket }
import io.vertx.core.metrics.Measured
import io.vertx.core.net.SocketAddress
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import monix.eval.Task

import scala.language.implicitConversions

case class DatagramSocket(val unwrap: JavaDatagramSocket) extends AnyVal {
  // Standard method
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  // Async handler method
  def send(packet: Buffer, port: Int, host: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.send(packet, port, host, handler)
    }.map(out => DatagramSocket(out))

  // Standard method
  def sender(port: Int, host: String): WriteStream[Buffer] =
    unwrap.sender(port, host)

  // Async handler method
  def send(str: String, port: Int, host: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.send(str, port, host, handler)
    }.map(out => DatagramSocket(out))

  // Async handler method
  def send(str: String, enc: String, port: Int, host: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.send(str, enc, port, host, handler)
    }.map(out => DatagramSocket(out))

  // Async handler method
  def close(): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.close(handler)
    }.map(_ => ())

  // Standard method
  def localAddress(): SocketAddress =
    unwrap.localAddress()

  // Async handler method
  def listenMulticastGroup(multicastAddress: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.listenMulticastGroup(multicastAddress, handler)
    }.map(out => DatagramSocket(out))

  // Async handler method
  def listenMulticastGroup(multicastAddress: String, networkInterface: String, source: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.listenMulticastGroup(multicastAddress, networkInterface, source, handler)
    }.map(out => DatagramSocket(out))

  // Async handler method
  def unlistenMulticastGroup(multicastAddress: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.unlistenMulticastGroup(multicastAddress, handler)
    }.map(out => DatagramSocket(out))

  // Async handler method
  def unlistenMulticastGroup(multicastAddress: String, networkInterface: String, source: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.unlistenMulticastGroup(multicastAddress, networkInterface, source, handler)
    }.map(out => DatagramSocket(out))

  // Async handler method
  def blockMulticastGroup(multicastAddress: String, sourceToBlock: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.blockMulticastGroup(multicastAddress, sourceToBlock, handler)
    }.map(out => DatagramSocket(out))

  // Async handler method
  def blockMulticastGroup(multicastAddress: String, networkInterface: String, sourceToBlock: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.blockMulticastGroup(multicastAddress, networkInterface, sourceToBlock, handler)
    }.map(out => DatagramSocket(out))

  // Async handler method
  def listen(port: Int, host: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.listen(port, host, handler)
    }.map(out => DatagramSocket(out))

  // Wrapper method
  def pause(): DatagramSocket =
    DatagramSocket(unwrap.pause())

  // Wrapper method
  def resume(): DatagramSocket =
    DatagramSocket(unwrap.resume())

  // Wrapper method
  def fetch(amount: Long): DatagramSocket =
    DatagramSocket(unwrap.fetch(amount))

  // Wrapper method
  def endHandler(endHandler: Handler[Void]): DatagramSocket =
    DatagramSocket(unwrap.endHandler(endHandler))

  // Wrapper method
  def handler(handler: Handler[DatagramPacket]): DatagramSocket =
    DatagramSocket(unwrap.handler(handler))

  // Wrapper method
  def exceptionHandler(handler: Handler[Throwable]): DatagramSocket =
    DatagramSocket(unwrap.exceptionHandler(handler))
}
object DatagramSocket {
  implicit def javaDatagramSocketToVerticesDatagramSocket(j: JavaDatagramSocket): DatagramSocket = apply(j)
  implicit def verticesDatagramSocketToJavaDatagramSocket(v: DatagramSocket): JavaDatagramSocket = v.unwrap


}
