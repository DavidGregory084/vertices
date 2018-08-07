package vertices
package ext.web.handler

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.AuthHandler
import io.vertx.ext.web.handler.{ JWTAuthHandler => JavaJWTAuthHandler }
import java.lang.String
import java.lang.Void
import java.util.List
import java.util.Set
import monix.eval.Task

import scala.language.implicitConversions

case class JWTAuthHandler(val unwrap: JavaJWTAuthHandler) extends AnyVal {
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
  def setAudience(audience: List[String]): JWTAuthHandler =
    JWTAuthHandler(unwrap.setAudience(audience))

  // Wrapper method
  def setIssuer(issuer: String): JWTAuthHandler =
    JWTAuthHandler(unwrap.setIssuer(issuer))

  // Wrapper method
  def setIgnoreExpiration(ignoreExpiration: Boolean): JWTAuthHandler =
    JWTAuthHandler(unwrap.setIgnoreExpiration(ignoreExpiration))
}
object JWTAuthHandler {
  implicit def javaJWTAuthHandlerToVerticesJWTAuthHandler(j: JavaJWTAuthHandler): JWTAuthHandler = apply(j)
  implicit def verticesJWTAuthHandlerToJavaJWTAuthHandler(v: JWTAuthHandler): JavaJWTAuthHandler = v.unwrap

  // Wrapper method
  def create(authProvider: JWTAuth): JWTAuthHandler =
    JWTAuthHandler(JavaJWTAuthHandler.create(authProvider))

  // Wrapper method
  def create(authProvider: JWTAuth, skip: String): JWTAuthHandler =
    JWTAuthHandler(JavaJWTAuthHandler.create(authProvider, skip))
}
