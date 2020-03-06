package vertices
package auth

import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.mongo.HashAlgorithm
import io.vertx.ext.auth.mongo.HashStrategy
import io.vertx.ext.auth.mongo.MongoAuth
import io.vertx.ext.mongo.MongoClient
import java.lang.String
import java.util.List

package object mongo {
  implicit class VertxMongoAuthOps(val target: MongoAuth) extends AnyVal {
    /**
     *  Insert a new user into mongo in the convenient way
     * @param username
     *           the username to be set
     * @param password
     *           the passsword in clear text, will be adapted following the definitions of the defined {@link HashStrategy}
     * @param roles
     *           a list of roles to be set
     * @param permissions
     *           a list of permissions to be set
     * @param resultHandler
     *           the ResultHandler will be provided with the id of the generated record
     */
    def insertUserL(username: String, password: String, roles: List[String], permissions: List[String]): Task[String] =
      Task.handle[String] { resultHandler =>
        target.insertUser(username, password, roles, permissions, resultHandler)
      }
  }


}