package vertices
package web.api

import monix.eval.Task
import io.swagger.v3.oas.models.OpenAPI
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.RouterFactory
import io.vertx.ext.web.api.contract.RouterFactoryOptions
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.ext.web.handler.BodyHandler
import java.lang.String
import java.util.List
import java.util.function.Function

package object contract {

  implicit class VertxOpenAPI3RouterFactoryCompanionOps(val target: OpenAPI3RouterFactory.type) {
    /**
     *  Create a new OpenAPI3RouterFactory
     * @param vertx
     * @param url location of your spec. It can be an absolute path, a local path or remote url (with HTTP protocol)
     * @param handler  When specification is loaded, this handler will be called with AsyncResult<OpenAPI3RouterFactory>
     */
    def createL(vertx: Vertx, url: String): Task[OpenAPI3RouterFactory] =
      Task.handle[OpenAPI3RouterFactory] { handler =>
        OpenAPI3RouterFactory.create(vertx, url, handler)
      }

    /**
     *  Create a new OpenAPI3RouterFactory
     * @param vertx
     * @param url location of your spec. It can be an absolute path, a local path or remote url (with HTTP protocol)
     * @param auth list of authorization values needed to access the remote url. Each item should be json representation
     *              of an {@link AuthorizationValue}
     * @param handler  When specification is loaded, this handler will be called with AsyncResult<OpenAPI3RouterFactory>
     */
    def createL(vertx: Vertx, url: String, auth: List[JsonObject]): Task[OpenAPI3RouterFactory] =
      Task.handle[OpenAPI3RouterFactory] { handler =>
        OpenAPI3RouterFactory.create(vertx, url, auth, handler)
      }
  }

}