package vertices


import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.net.NetClient
import io.vertx.core.net.NetServer
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User
import io.vertx.ext.stomp.Acknowledgement
import io.vertx.ext.stomp.BridgeOptions
import io.vertx.ext.stomp.Destination
import io.vertx.ext.stomp.DestinationFactory
import io.vertx.ext.stomp.Frame
import io.vertx.ext.stomp.ServerFrame
import io.vertx.ext.stomp.StompClient
import io.vertx.ext.stomp.StompClientConnection
import io.vertx.ext.stomp.StompClientOptions
import io.vertx.ext.stomp.StompServer
import io.vertx.ext.stomp.StompServerConnection
import io.vertx.ext.stomp.StompServerHandler
import io.vertx.ext.stomp.StompServerOptions
import java.lang.Boolean
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import java.util.List

package object stomp {
  implicit class VertxStompClientOps(val target: StompClient) extends AnyVal {
    /**
     *  Connects to the server.
     * @param port          the server port
     * @param host          the server host
     * @param resultHandler handler called with the connection result
     * @return the current {@link StompClient}
     */
    def connectL(port: Int, host: String): Task[StompClientConnection] =
      Task.handle[StompClientConnection] { resultHandler =>
        target.connect(port, host, resultHandler)
      }

    /**
     *  Connects to the server.
     * @param net           the NET client to use
     * @param resultHandler handler called with the connection result
     * @return the current {@link StompClient}
     */
    def connectL(net: NetClient): Task[StompClientConnection] =
      Task.handle[StompClientConnection] { resultHandler =>
        target.connect(net, resultHandler)
      }

    /**
     *  Connects to the server.
     * @param port          the server port
     * @param host          the server host
     * @param net           the NET client to use
     * @param resultHandler handler called with the connection result
     * @return the current {@link StompClient}
     */
    def connectL(port: Int, host: String, net: NetClient): Task[StompClientConnection] =
      Task.handle[StompClientConnection] { resultHandler =>
        target.connect(port, host, net, resultHandler)
      }

    /**
     *  Connects to the server using the host and port configured in the client's options.
     * @param resultHandler handler called with the connection result. A failure will be sent to the handler if a TCP
     *                       level issue happen before the `CONNECTED` frame is received. Afterwards, the
     *                       {@link #exceptionHandler(Handler)} is called.
     * @return the current {@link StompClient}
     */
    def connectL(): Task[StompClientConnection] =
      Task.handle[StompClientConnection] { resultHandler =>
        target.connect(resultHandler)
      }
  }


  implicit class VertxStompServerHandlerOps(val target: StompServerHandler) extends AnyVal {
    /**
     *  Called when the client connects to a server requiring authentication. It invokes the {@link AuthProvider} configured
     *  using {@link #authProvider(AuthProvider)}.
     * @param connection server connection that contains session ID
     * @param login      the login
     * @param passcode   the password
     * @param handler    handler receiving the authentication result
     * @return the current {@link StompServerHandler}
     */
    def onAuthenticationRequestL(connection: StompServerConnection, login: String, passcode: String): Task[Boolean] =
      Task.handle[java.lang.Boolean] { handler =>
        target.onAuthenticationRequest(connection, login, passcode, handler)
      }.map(out => out: Boolean)
  }


  implicit class VertxStompServerOps(val target: StompServer) extends AnyVal {
    /**
     *  Connects the STOMP server default port (61613) and network interface ({@code 0.0.0.0}). Once the socket
     *  it bounds calls the given handler with the result. The result may be a failure if the socket is already used.
     * @param handler the handler to call with the result
     * @return the current {@link StompServer}
     */
    def listenL(): Task[StompServer] =
      Task.handle[StompServer] { handler =>
        target.listen(handler)
      }

    /**
     *  Connects the STOMP server to the given port. This method use the default host ({@code 0.0.0.0}). Once the socket
     *  it bounds calls the given handler with the result. The result may be a failure if the socket is already used.
     * @param port    the port
     * @param handler the handler to call with the result
     * @return the current {@link StompServer}
     */
    def listenL(port: Int): Task[StompServer] =
      Task.handle[StompServer] { handler =>
        target.listen(port, handler)
      }

    /**
     *  Connects the STOMP server to the given port / interface. Once the socket it bounds calls the given handler with
     *  the result. The result may be a failure if the socket is already used.
     * @param port    the port
     * @param host    the host / interface
     * @param handler the handler to call with the result
     * @return the current {@link StompServer}
     */
    def listenL(port: Int, host: String): Task[StompServer] =
      Task.handle[StompServer] { handler =>
        target.listen(port, host, handler)
      }

    /**
     *  Closes the server.
     * @param completionHandler handler called once the server has been stopped
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.close(completionHandler)
      }.map(_ => ())
  }


}