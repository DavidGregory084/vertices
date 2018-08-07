package vertices
package ext.web.handler

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.AuthHandler
import io.vertx.ext.web.handler.{ RedirectAuthHandler => JavaRedirectAuthHandler }
import java.lang.String
import java.lang.Void
import java.util.Set
import monix.eval.Task

import scala.language.implicitConversions

case class RedirectAuthHandler(val unwrap: JavaRedirectAuthHandler) extends AnyVal {
  // Standard method
  def handle(arg0: RoutingContext): Unit =
    unwrap.handle(arg0)

  // Standard method
  def addAuthority(authority: String): AuthHandler =
    unwrap.addAuthority(authority)

  // Standard method
  def addAuthorities(authorities: Set[String]): AuthHandler =
    unwrap.addAuthorities(authorities)

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
object RedirectAuthHandler {
  implicit def javaRedirectAuthHandlerToVerticesRedirectAuthHandler(j: JavaRedirectAuthHandler): RedirectAuthHandler = apply(j)
  implicit def verticesRedirectAuthHandlerToJavaRedirectAuthHandler(v: RedirectAuthHandler): JavaRedirectAuthHandler = v.unwrap

  // Standard method
  def create(authProvider: AuthProvider): AuthHandler =
    JavaRedirectAuthHandler.create(authProvider)

  // Standard method
  def create(authProvider: AuthProvider, loginRedirectURL: String): AuthHandler =
    JavaRedirectAuthHandler.create(authProvider, loginRedirectURL)

  // Standard method
  def create(authProvider: AuthProvider, loginRedirectURL: String, returnURLParam: String): AuthHandler =
    JavaRedirectAuthHandler.create(authProvider, loginRedirectURL, returnURLParam)
}