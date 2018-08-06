package vertices
package ext.web.handler

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.{ AuthHandler => JavaAuthHandler }
import io.vertx.ext.web.handler.{ BasicAuthHandler => JavaBasicAuthHandler }
import java.lang.String
import java.lang.Void
import java.util.Set
import monix.eval.Task

import scala.language.implicitConversions

case class BasicAuthHandler(val unwrap: JavaBasicAuthHandler) extends AnyVal {
  // Standard method
  def handle(arg0: RoutingContext): Unit =
    unwrap.handle(arg0)

  // Wrapper method
  def addAuthority(authority: String): AuthHandler =
    AuthHandler(unwrap.addAuthority(authority))

  // Wrapper method
  def addAuthorities(authorities: Set[String]): AuthHandler =
    AuthHandler(unwrap.addAuthorities(authorities))

  // Async handler method
  def parseCredentials(context: RoutingContext): Task[JsonObject] =
    Task.handle[JsonObject] { handler =>
      unwrap.parseCredentials(context, handler)
    }

  // Async handler method
  def authorize(user: User): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.authorize(user, handler)
    }.map(_ => ())
}
object BasicAuthHandler {
  implicit def javaBasicAuthHandlerToVerticesBasicAuthHandler(j: JavaBasicAuthHandler): BasicAuthHandler = apply(j)
  implicit def verticesBasicAuthHandlerToJavaBasicAuthHandler(v: BasicAuthHandler): JavaBasicAuthHandler = v.unwrap

  // Wrapper method
  def create(authProvider: AuthProvider): AuthHandler =
    AuthHandler(JavaBasicAuthHandler.create(authProvider))

  // Wrapper method
  def create(authProvider: AuthProvider, realm: String): AuthHandler =
    AuthHandler(JavaBasicAuthHandler.create(authProvider, realm))
}
