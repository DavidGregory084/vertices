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

case class AuthProvider(val unwrap: JavaAuthProvider) extends AnyVal {
  // Async handler method
  def authenticate(authInfo: JsonObject): Task[User] =
    Task.handle[JavaUser] { resultHandler =>
      unwrap.authenticate(authInfo, resultHandler)
    }.map(out => User(out))
}
object AuthProvider {
  implicit def javaAuthProviderToVerticesAuthProvider(j: JavaAuthProvider): AuthProvider = apply(j)
  implicit def verticesAuthProviderToJavaAuthProvider(v: AuthProvider): JavaAuthProvider = v.unwrap


}
