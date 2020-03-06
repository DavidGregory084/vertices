package vertices


import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.healthchecks.HealthChecks
import io.vertx.ext.healthchecks.Status
import java.lang.String

package object healthchecks {
  implicit class VertxHealthChecksOps(val target: HealthChecks) extends AnyVal {
    /**
     *  Invokes the registered procedure with the given name and sub-procedures. It computes the overall
     *  outcome.
     * @param resultHandler the result handler, must not be {@code null}. The handler received an
     *                       {@link AsyncResult} marked as failed if the procedure with the given name cannot
     *                       be found or invoked.
     * @return the current {@link HealthChecks}
     */
    def invokeL(name: String): Task[JsonObject] =
      Task.handle[JsonObject] { resultHandler =>
        target.invoke(name, resultHandler)
      }
  }


}