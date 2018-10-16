package vertices
package ext.auth

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.{ AuthProvider => JavaAuthProvider }
import io.vertx.ext.auth.{ User => JavaUser }
import java.lang.Boolean
import java.lang.String
import monix.eval.Task

import scala.language.implicitConversions

/**
 *  Represents an authenticates User and contains operations to authorise the user.
 *  <p>
 *  Please consult the documentation for a detailed explanation.
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
case class User(val unwrap: JavaUser) extends AnyVal {
  /**
   *  Is the user authorised to
   * @param authority  the authority - what this really means is determined by the specific implementation. It might
   *                    represent a permission to access a resource e.g. `printers:printer34` or it might represent
   *                    authority to a role in a roles based model, e.g. `role:admin`.
   * @param resultHandler  handler that will be called with an {@link io.vertx.core.AsyncResult} containing the value
   *                        `true` if the they has the authority or `false` otherwise.
   * @return the User to enable fluent use
   */
  def isAuthorized(authority: String): Task[Boolean] =
    Task.handle[java.lang.Boolean] { resultHandler =>
      unwrap.isAuthorized(authority, resultHandler)
    }.map(out => out: Boolean)

  /**
   *  The User object will cache any authorities that it knows it has to avoid hitting the
   *  underlying auth provider each time.  Use this method if you want to clear this cache.
   * @return the User to enable fluent use
   */
  def clearCache(): User =
    User(unwrap.clearCache())

  /**
   *  Get the underlying principal for the User. What this actually returns depends on the implementation.
   *  For a simple user/password based auth, it's likely to contain a JSON object with the following structure:
   *  <pre>
   *    {
   *      "username", "tim"
   *    }
   *  </pre>
   * @return JSON representation of the Principal
   */
  def principal(): JsonObject =
    unwrap.principal()

  /**
   *  Set the auth provider for the User. This is typically used to reattach a detached User with an AuthProvider, e.g.
   *  after it has been deserialized.
   * @param authProvider  the AuthProvider - this must be the same type of AuthProvider that originally created the User
   */
  def setAuthProvider(authProvider: AuthProvider): Unit =
    unwrap.setAuthProvider(authProvider)
}
object User {
  implicit def javaUserToVerticesUser(j: JavaUser): User = apply(j)
  implicit def verticesUserToJavaUser(v: User): JavaUser = v.unwrap


}
