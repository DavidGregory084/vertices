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

/**
 *  An auth handler that chains to a sequence of handlers.
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
case class ChainAuthHandler(val unwrap: JavaChainAuthHandler) extends AnyVal {

  def handle(arg0: RoutingContext): Unit =
    unwrap.handle(arg0)

  /**
   *  Add a required authority for this auth handler
   * @param authority  the authority
   * @return a reference to this, so the API can be used fluently
   */
  def addAuthority(authority: String): AuthHandler =
    unwrap.addAuthority(authority)

  /**
   *  Add a set of required authorities for this auth handler
   * @param authorities  the set of authorities
   * @return a reference to this, so the API can be used fluently
   */
  def addAuthorities(authorities: Set[String]): AuthHandler =
    unwrap.addAuthorities(authorities)

  /**
   *  Parses the credentials from the request into a JsonObject. The implementation should
   *  be able to extract the required info for the auth provider in the format the provider
   *  expects.
   * @param context the routing context
   * @param handler the handler to be called once the information is available.
   */
  def parseCredentials(context: RoutingContext): Task[JsonObject] =
    Task.handle[JsonObject] { handler =>
      unwrap.parseCredentials(context, handler)
    }

  /**
   *  Authorizes the given user against all added authorities.
   * @param user a user.
   * @param handler the handler for the result.
   */
  def authorize(user: User): Task[Unit] =
    Task.handle[Void] { handler =>
      unwrap.authorize(user, handler)
    }.map(_ => ())

  /**
   *  Appends a auth provider to the chain.
   * @param authHandler auth handler
   * @return self
   */
  def append(authHandler: AuthHandler): ChainAuthHandler =
    ChainAuthHandler(unwrap.append(authHandler))

  /**
   *  Removes a provider from the chain.
   * @param authHandler provider to remove
   * @return true if provider was removed, false if non existent in the chain.
   */
  def remove(authHandler: AuthHandler): Boolean =
    unwrap.remove(authHandler)

  /**
   *  Clears the chain.
   */
  def clear(): Unit =
    unwrap.clear()
}
object ChainAuthHandler {
  implicit def javaChainAuthHandlerToVerticesChainAuthHandler(j: JavaChainAuthHandler): ChainAuthHandler = apply(j)
  implicit def verticesChainAuthHandlerToJavaChainAuthHandler(v: ChainAuthHandler): JavaChainAuthHandler = v.unwrap


  def create(): ChainAuthHandler =
    ChainAuthHandler(JavaChainAuthHandler.create())
}
