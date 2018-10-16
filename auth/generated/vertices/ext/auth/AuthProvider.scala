package vertices
package ext.auth

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.{ AuthProvider => JavaAuthProvider }
import io.vertx.ext.auth.{ User => JavaUser }
import monix.eval.Task

import scala.language.implicitConversions

/**
 * 
 *  User-facing interface for authenticating users.
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
case class AuthProvider(val unwrap: JavaAuthProvider) extends AnyVal {
  /**
   *  Authenticate a user.
   *  <p>
   *  The first argument is a JSON object containing information for authenticating the user. What this actually contains
   *  depends on the specific implementation. In the case of a simple username/password based
   *  authentication it is likely to contain a JSON object with the following structure:
   *  <pre>
   *    {
   *      "username": "tim",
   *      "password": "mypassword"
   *    }
   *  </pre>
   *  For other types of authentication it contain different information - for example a JWT token or OAuth bearer token.
   *  <p>
   *  If the user is successfully authenticated a {@link User} object is passed to the handler in an {@link io.vertx.core.AsyncResult}.
   *  The user object can then be used for authorisation.
   * @param authInfo  The auth information
   * @param resultHandler  The result handler
   */
  def authenticate(authInfo: JsonObject): Task[User] =
    Task.handle[JavaUser] { resultHandler =>
      unwrap.authenticate(authInfo, resultHandler)
    }.map(out => User(out))
}
object AuthProvider {
  implicit def javaAuthProviderToVerticesAuthProvider(j: JavaAuthProvider): AuthProvider = apply(j)
  implicit def verticesAuthProviderToJavaAuthProvider(v: AuthProvider): JavaAuthProvider = v.unwrap


}
