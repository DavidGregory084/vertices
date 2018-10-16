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

  /**
   *  A datagram socket can be used to send {@link DatagramPacket}'s to remote datagram servers
   *  and receive {@link DatagramPacket}s .
   *  <p>
   *  Usually you use a datagram socket to send UDP over the wire. UDP is connection-less which means you are not connected
   *  to the remote peer in a persistent way. Because of this you have to supply the address and port of the remote peer
   *  when sending data.
   *  <p>
   *  You can send data to ipv4 or ipv6 addresses, which also include multicast addresses.
   *  <p>
   *  Please consult the documentation for more information on datagram sockets.
   * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
   */
case class DatagramSocket(val unwrap: JavaDatagramSocket) extends AnyVal {
  /**
   *  Whether the metrics are enabled for this measured object
   * @implSpec  The default implementation returns {@code false}
   * @return {@code true} if metrics are enabled
   */
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  /**
   *  Write the given {@link io.vertx.core.buffer.Buffer} to the {@link io.vertx.core.net.SocketAddress}.
   *  The {@link io.vertx.core.Handler} will be notified once the write completes.
   * @param packet  the {@link io.vertx.core.buffer.Buffer} to write
   * @param port  the host port of the remote peer
   * @param host  the host address of the remote peer
   * @param handler  the {@link io.vertx.core.Handler} to notify once the write completes.
   * @return a reference to this, so the API can be used fluently
   */
  def send(packet: Buffer, port: Int, host: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.send(packet, port, host, handler)
    }.map(out => DatagramSocket(out))

  /**
   *  Returns a {@code WriteStream<Buffer>} able to send {@link Buffer} to the
   *  {@link io.vertx.core.net.SocketAddress}.
   * @param port the port of the remote peer
   * @param host the host address of the remote peer
   * @return the write stream for sending packets
   */
  def sender(port: Int, host: String): WriteStream[Buffer] =
    unwrap.sender(port, host)

  /**
   *  Write the given {@link String} to the {@link io.vertx.core.net.SocketAddress} using UTF8 encoding.
   *  The {@link Handler} will be notified once the write completes.
   * @param str   the {@link String} to write
   * @param port  the host port of the remote peer
   * @param host  the host address of the remote peer
   * @param handler  the {@link io.vertx.core.Handler} to notify once the write completes.
   * @return a reference to this, so the API can be used fluently
   */
  def send(str: String, port: Int, host: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.send(str, port, host, handler)
    }.map(out => DatagramSocket(out))

  /**
   *  Write the given {@link String} to the {@link io.vertx.core.net.SocketAddress} using the given encoding.
   *  The {@link Handler} will be notified once the write completes.
   * @param str  the {@link String} to write
   * @param enc  the charset used for encoding
   * @param port  the host port of the remote peer
   * @param host  the host address of the remote peer
   * @param handler  the {@link io.vertx.core.Handler} to notify once the write completes.
   * @return a reference to this, so the API can be used fluently
   */
  def send(str: String, enc: String, port: Int, host: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.send(str, enc, port, host, handler)
    }.map(out => DatagramSocket(out))

  /**
   *  Closes the {@link io.vertx.core.datagram.DatagramSocket} implementation asynchronous
   *  and notifies the handler once done.
   * @param handler  the handler to notify once complete
   */
  def close(): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.close(handler)
    }.map(_ => ())

  /**
   *  Return the {@link io.vertx.core.net.SocketAddress} to which
   *  this {@link io.vertx.core.datagram.DatagramSocket} is bound.
   * @return the socket address
   */
  def localAddress(): SocketAddress =
    unwrap.localAddress()

  /**
   *  Joins a multicast group and listens for packets send to it.
   *  The {@link Handler} is notified once the operation completes.
   * @param multicastAddress  the address of the multicast group to join
   * @param  handler  then handler to notify once the operation completes
   * @return a reference to this, so the API can be used fluently
   */
  def listenMulticastGroup(multicastAddress: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.listenMulticastGroup(multicastAddress, handler)
    }.map(out => DatagramSocket(out))

  /**
   *  Joins a multicast group and listens for packets send to it on the given network interface.
   *  The {@link Handler} is notified once the operation completes.
   * @param  multicastAddress  the address of the multicast group to join
   * @param  networkInterface  the network interface on which to listen for packets.
   * @param  source  the address of the source for which we will listen for multicast packets
   * @param  handler  then handler to notify once the operation completes
   * @return a reference to this, so the API can be used fluently
   */
  def listenMulticastGroup(multicastAddress: String, networkInterface: String, source: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.listenMulticastGroup(multicastAddress, networkInterface, source, handler)
    }.map(out => DatagramSocket(out))

  /**
   *  Leaves a multicast group and stops listening for packets send to it.
   *  The {@link Handler} is notified once the operation completes.
   * @param multicastAddress  the address of the multicast group to leave
   * @param handler  then handler to notify once the operation completes
   * @return a reference to this, so the API can be used fluently
   */
  def unlistenMulticastGroup(multicastAddress: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.unlistenMulticastGroup(multicastAddress, handler)
    }.map(out => DatagramSocket(out))

  /**
   *  Leaves a multicast group and stops listening for packets send to it on the given network interface.
   *  The {@link Handler} is notified once the operation completes.
   * @param  multicastAddress  the address of the multicast group to join
   * @param  networkInterface  the network interface on which to listen for packets.
   * @param  source  the address of the source for which we will listen for multicast packets
   * @param  handler the handler to notify once the operation completes
   * @return  a reference to this, so the API can be used fluently
   */
  def unlistenMulticastGroup(multicastAddress: String, networkInterface: String, source: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.unlistenMulticastGroup(multicastAddress, networkInterface, source, handler)
    }.map(out => DatagramSocket(out))

  /**
   *  Block the given address for the given multicast address and notifies the {@link Handler} once
   *  the operation completes.
   * @param multicastAddress  the address for which you want to block the source address
   * @param sourceToBlock  the source address which should be blocked. You will not receive an multicast packets
   *                        for it anymore.
   * @param handler  the handler to notify once the operation completes
   * @return  a reference to this, so the API can be used fluently
   */
  def blockMulticastGroup(multicastAddress: String, sourceToBlock: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.blockMulticastGroup(multicastAddress, sourceToBlock, handler)
    }.map(out => DatagramSocket(out))

  /**
   *  Block the given address for the given multicast address on the given network interface and notifies
   *  the {@link Handler} once the operation completes.
   * @param  multicastAddress  the address for which you want to block the source address
   * @param  networkInterface  the network interface on which the blocking should occur.
   * @param  sourceToBlock  the source address which should be blocked. You will not receive an multicast packets
   *                         for it anymore.
   * @param  handler  the handler to notify once the operation completes
   * @return  a reference to this, so the API can be used fluently
   */
  def blockMulticastGroup(multicastAddress: String, networkInterface: String, sourceToBlock: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.blockMulticastGroup(multicastAddress, networkInterface, sourceToBlock, handler)
    }.map(out => DatagramSocket(out))

  /**
   *  Start listening on the given port and host. The handler will be called when the socket is listening.
   * @param port  the port to listen on
   * @param host  the host to listen on
   * @param handler  the handler will be called when listening
   * @return  a reference to this, so the API can be used fluently
   */
  def listen(port: Int, host: String): Task[DatagramSocket] =
    Task.handle[JavaDatagramSocket] { handler =>
      unwrap.listen(port, host, handler)
    }.map(out => DatagramSocket(out))


  def pause(): DatagramSocket =
    DatagramSocket(unwrap.pause())


  def resume(): DatagramSocket =
    DatagramSocket(unwrap.resume())


  def fetch(amount: Long): DatagramSocket =
    DatagramSocket(unwrap.fetch(amount))


  def endHandler(endHandler: Handler[Void]): DatagramSocket =
    DatagramSocket(unwrap.endHandler(endHandler))


  def handler(handler: Handler[DatagramPacket]): DatagramSocket =
    DatagramSocket(unwrap.handler(handler))


  def exceptionHandler(handler: Handler[Throwable]): DatagramSocket =
    DatagramSocket(unwrap.exceptionHandler(handler))
}
object DatagramSocket {
  implicit def javaDatagramSocketToVerticesDatagramSocket(j: JavaDatagramSocket): DatagramSocket = apply(j)
  implicit def verticesDatagramSocketToJavaDatagramSocket(v: DatagramSocket): JavaDatagramSocket = v.unwrap


}
