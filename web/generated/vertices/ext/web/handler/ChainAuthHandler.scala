package vertices
package ext.web.handler

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.AuthHandler
import io.vertx.ext.web.handler.{ ChainAuthHandler => JavaChainAuthHandler }
import java.lang.String
import java.lang.Void
import java.util.Set
import monix.eval.Task

import scala.language.implicitConversions

case class ChainAuthHandler(val unwrap: JavaChainAuthHandler) extends AnyVal {
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
  def append(authHandler: AuthHandler): ChainAuthHandler =
    ChainAuthHandler(unwrap.append(authHandler))

  // Standard method
  def remove(authHandler: AuthHandler): Boolean =
    unwrap.remove(authHandler)

  // Standard method
  def clear(): Unit =
    unwrap.clear()
}
object ChainAuthHandler {
  implicit def javaChainAuthHandlerToVerticesChainAuthHandler(j: JavaChainAuthHandler): ChainAuthHandler = apply(j)
  implicit def verticesChainAuthHandlerToJavaChainAuthHandler(v: ChainAuthHandler): JavaChainAuthHandler = v.unwrap

  // Wrapper method
  def create(): ChainAuthHandler =
    ChainAuthHandler(JavaChainAuthHandler.create())
}
