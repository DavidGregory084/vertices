package vertices


import monix.eval.Task
import io.vertx.config.ConfigChange
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.streams.ReadStream
import java.lang.Void
import java.util.function.Function

package object config {
  implicit class VertxConfigRetrieverOps(val target: ConfigRetriever) extends AnyVal {
    /**
     *  Reads the configuration from the different {@link ConfigStore}
     *  and computes the final configuration.
     * @param completionHandler handler receiving the computed configuration, or a failure if the
     *                           configuration cannot be retrieved
     */
    def getConfigL(): Task[JsonObject] =
      Task.handle[JsonObject] { completionHandler =>
        target.getConfig(completionHandler)
      }
  }


}