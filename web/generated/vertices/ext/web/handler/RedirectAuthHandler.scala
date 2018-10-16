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

/**
 *  An auth handler that's used to handle auth by redirecting user to a custom login page.
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
case class RedirectAuthHandler(val unwrap: JavaRedirectAuthHandler) extends AnyVal {

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
object RedirectAuthHandler {
  implicit def javaRedirectAuthHandlerToVerticesRedirectAuthHandler(j: JavaRedirectAuthHandler): RedirectAuthHandler = apply(j)
  implicit def verticesRedirectAuthHandlerToJavaRedirectAuthHandler(v: RedirectAuthHandler): JavaRedirectAuthHandler = v.unwrap

  /**
   *  Create a handler
   * @param authProvider  the auth service to use
   * @return the handler
   */
  def create(authProvider: AuthProvider): AuthHandler =
    JavaRedirectAuthHandler.create(authProvider)

  /**
   *  Create a handler
   * @param authProvider  the auth service to use
   * @param loginRedirectURL  the url to redirect the user to
   * @return the handler
   */
  def create(authProvider: AuthProvider, loginRedirectURL: String): AuthHandler =
    JavaRedirectAuthHandler.create(authProvider, loginRedirectURL)

  /**
   *  Create a handler
   * @param authProvider  the auth service to use
   * @param loginRedirectURL  the url to redirect the user to
   * @param returnURLParam  the name of param used to store return url information in session
   * @return the handler
   */
  def create(authProvider: AuthProvider, loginRedirectURL: String, returnURLParam: String): AuthHandler =
    JavaRedirectAuthHandler.create(authProvider, loginRedirectURL, returnURLParam)
}
