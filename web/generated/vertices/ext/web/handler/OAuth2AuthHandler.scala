package vertices
package ext.web.handler

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.AuthHandler
import io.vertx.ext.web.handler.{ OAuth2AuthHandler => JavaOAuth2AuthHandler }
import java.lang.String
import java.lang.Void
import java.util.Set
import monix.eval.Task

import scala.language.implicitConversions

case class OAuth2AuthHandler(val unwrap: JavaOAuth2AuthHandler) extends AnyVal {
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

  // Wrapper method
  def extraParams(extraParams: JsonObject): OAuth2AuthHandler =
    OAuth2AuthHandler(unwrap.extraParams(extraParams))

  // Wrapper method
  def setupCallback(route: Route): OAuth2AuthHandler =
    OAuth2AuthHandler(unwrap.setupCallback(route))
}
object OAuth2AuthHandler {
  implicit def javaOAuth2AuthHandlerToVerticesOAuth2AuthHandler(j: JavaOAuth2AuthHandler): OAuth2AuthHandler = apply(j)
  implicit def verticesOAuth2AuthHandlerToJavaOAuth2AuthHandler(v: OAuth2AuthHandler): JavaOAuth2AuthHandler = v.unwrap

  // Wrapper method
  def create(authProvider: OAuth2Auth, callbackURL: String): OAuth2AuthHandler =
    OAuth2AuthHandler(JavaOAuth2AuthHandler.create(authProvider, callbackURL))

  // Wrapper method
  def create(authProvider: OAuth2Auth): OAuth2AuthHandler =
    OAuth2AuthHandler(JavaOAuth2AuthHandler.create(authProvider))
}
