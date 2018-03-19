package vertices
package net

import io.vertx.core.Handler
import io.vertx.core.net.NetSocket
import io.vertx.core.net.SocketAddress
import io.vertx.core.net.{ NetServer => JavaNetServer }
import io.vertx.core.streams.ReadStream
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import monix.eval.Task

/**
 * Code generated by vertices Vert.x API generator. DO NOT EDIT.
 */
case class NetServer(val unwrap: JavaNetServer) extends AnyVal {

  def actualPort(): Int =
    unwrap.actualPort()

  def close(): Task[Unit] =
    Task
      .handle[Void] { handler =>
        unwrap.close(handler)
      }
      .map(_ => ())

  def connectHandler(): Handler[NetSocket] =
    unwrap.connectHandler()

  def connectHandler(arg0: Handler[NetSocket]): NetServer =
    NetServer(unwrap.connectHandler(arg0))

  def connectStream(): ReadStream[NetSocket] =
    unwrap.connectStream()

  def exceptionHandler(arg0: Handler[Throwable]): NetServer =
    NetServer(unwrap.exceptionHandler(arg0))

  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  def listen(): Task[NetServer] =
    Task
      .handle[JavaNetServer] { handler =>
        unwrap.listen(handler)
      }
      .map(NetServer(_))

  def listen(arg0: Int): Task[NetServer] =
    Task
      .handle[JavaNetServer] { handler =>
        unwrap.listen(arg0, handler)
      }
      .map(NetServer(_))

  def listen(arg0: Int, arg1: String): Task[NetServer] =
    Task
      .handle[JavaNetServer] { handler =>
        unwrap.listen(arg0, arg1, handler)
      }
      .map(NetServer(_))

  def listen(arg0: SocketAddress): Task[NetServer] =
    Task
      .handle[JavaNetServer] { handler =>
        unwrap.listen(arg0, handler)
      }
      .map(NetServer(_))
}

object NetServer {}