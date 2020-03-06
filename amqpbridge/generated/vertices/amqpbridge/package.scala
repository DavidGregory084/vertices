package vertices


import monix.eval.Task
import io.vertx.amqpbridge.AmqpBridge
import io.vertx.amqpbridge.AmqpBridgeOptions
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.eventbus.MessageProducer
import java.lang.String
import java.lang.Void

package object amqpbridge {
  implicit class VertxAmqpBridgeOps(val target: AmqpBridge) extends AnyVal {
    /**
     *  Starts the bridge, establishing the underlying connection.
     * @param hostname
     *           the host name to connect to
     * @param port
     *           the port to connect to
     * @param username
     *           the username
     * @param password
     *           the password
     * @param resultHandler
     *           the result handler
     */
    def startL(hostname: String, port: Int, username: String, password: String): Task[AmqpBridge] =
      Task.handle[AmqpBridge] { resultHandler =>
        target.start(hostname, port, username, password, resultHandler)
      }

    /**
     *  Starts the bridge, establishing the underlying connection.
     * @param hostname
     *           the host name to connect to
     * @param port
     *           the port to connect to
     * @param resultHandler
     *           the result handler
     */
    def startL(hostname: String, port: Int): Task[AmqpBridge] =
      Task.handle[AmqpBridge] { resultHandler =>
        target.start(hostname, port, resultHandler)
      }

    /**
     *  Shuts the bridge down, closing the underlying connection.
     * @param resultHandler
     *           the result handler
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.close(resultHandler)
      }.map(_ => ())
  }


}