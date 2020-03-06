package vertices
package eventbus.bridge

import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.net.NetServerOptions
import io.vertx.ext.bridge.BridgeOptions
import io.vertx.ext.eventbus.bridge.tcp.BridgeEvent
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge
import java.lang.String
import java.lang.Void

package object tcp {
  implicit class VertxTcpEventBusBridgeOps(val target: TcpEventBusBridge) extends AnyVal {
    /**
     *  Listen on default port 7000 with a handler to report the state of the socket listen operation.
     * @param handler the result handler
     * @return self
     */
    def listenL(): Task[TcpEventBusBridge] =
      Task.handle[TcpEventBusBridge] { handler =>
        target.listen(handler)
      }

    /**
     *  Listen on specific port and bind to specific address
     * @param port tcp port
     * @param address tcp address to the bind
     * @param handler the result handler
     * @return self
     */
    def listenL(port: Int, address: String): Task[TcpEventBusBridge] =
      Task.handle[TcpEventBusBridge] { handler =>
        target.listen(port, address, handler)
      }

    /**
     *  Listen on specific port
     * @param port tcp port
     * @param handler the result handler
     * @return self
     */
    def listenL(port: Int): Task[TcpEventBusBridge] =
      Task.handle[TcpEventBusBridge] { handler =>
        target.listen(port, handler)
      }

    /**
     *  Close the current socket.
     * @param handler the result handler
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())
  }


}