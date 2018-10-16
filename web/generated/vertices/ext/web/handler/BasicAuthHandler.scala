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
import io.vertx.ext.web.handler.{ BasicAuthHandler => JavaBasicAuthHandler }
import java.lang.String
import java.lang.Void
import java.util.Set
import monix.eval.Task

import scala.language.implicitConversions

  /**
   *  An auth handler that provides HTTP Basic Authentication support.
   * @author <a href="http://tfox.org">Tim Fox</a>
   */
case class BasicAuthHandler(val unwrap: JavaBasicAuthHandler) extends AnyVal {

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
}
object BasicAuthHandler {
  implicit def javaBasicAuthHandlerToVerticesBasicAuthHandler(j: JavaBasicAuthHandler): BasicAuthHandler = apply(j)
  implicit def verticesBasicAuthHandlerToJavaBasicAuthHandler(v: BasicAuthHandler): JavaBasicAuthHandler = v.unwrap

  /**
   *  Create a basic auth handler
   * @param authProvider  the auth provider to use
   * @return the auth handler
   */
  def create(authProvider: AuthProvider): AuthHandler =
    JavaBasicAuthHandler.create(authProvider)

  /**
   *  Create a basic auth handler, specifying realm
   * @param authProvider  the auth service to use
   * @param realm  the realm to use
   * @return the auth handler
   */
  def create(authProvider: AuthProvider, realm: String): AuthHandler =
    JavaBasicAuthHandler.create(authProvider, realm)
}
