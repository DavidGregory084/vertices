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

case class User(val unwrap: JavaUser) extends AnyVal {
  // Async handler method
  def isAuthorized(authority: String): Task[Boolean] =
    Task.handle[java.lang.Boolean] { resultHandler =>
      unwrap.isAuthorized(authority, resultHandler)
    }.map(out => out: Boolean)

  // Wrapper method
  def clearCache(): User =
    User(unwrap.clearCache())

  // Standard method
  def principal(): JsonObject =
    unwrap.principal()

  // Standard method
  def setAuthProvider(authProvider: AuthProvider): Unit =
    unwrap.setAuthProvider(authProvider)
}
object User {
  implicit def javaUserToVerticesUser(j: JavaUser): User = apply(j)
  implicit def verticesUserToJavaUser(v: User): JavaUser = v.unwrap


}
