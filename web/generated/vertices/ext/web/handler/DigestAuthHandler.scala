package vertices
package ext.web.handler

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.htdigest.HtdigestAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.AuthHandler
import io.vertx.ext.web.handler.{ DigestAuthHandler => JavaDigestAuthHandler }
import java.lang.String
import java.lang.Void
import java.util.Set
import monix.eval.Task

import scala.language.implicitConversions

case class DigestAuthHandler(val unwrap: JavaDigestAuthHandler) extends AnyVal {
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
object DigestAuthHandler {
  implicit def javaDigestAuthHandlerToVerticesDigestAuthHandler(j: JavaDigestAuthHandler): DigestAuthHandler = apply(j)
  implicit def verticesDigestAuthHandlerToJavaDigestAuthHandler(v: DigestAuthHandler): JavaDigestAuthHandler = v.unwrap

  // Wrapper method
  def create(authProvider: HtdigestAuth): DigestAuthHandler =
    DigestAuthHandler(JavaDigestAuthHandler.create(authProvider))

  // Wrapper method
  def create(authProvider: HtdigestAuth, nonceExpireTimeout: Long): DigestAuthHandler =
    DigestAuthHandler(JavaDigestAuthHandler.create(authProvider, nonceExpireTimeout))
}
