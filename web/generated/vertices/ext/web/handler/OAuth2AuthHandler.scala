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

  /**
   *  An auth handler that provides OAuth2 Authentication support. This handler is suitable for AuthCode flows.
   * @author Paulo Lopes
   */
case class OAuth2AuthHandler(val unwrap: JavaOAuth2AuthHandler) extends AnyVal {

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
   *  Extra parameters needed to be passed while requesting a token.
   * @param extraParams extra optional parameters.
   * @return self
   */
  def extraParams(extraParams: JsonObject): OAuth2AuthHandler =
    OAuth2AuthHandler(unwrap.extraParams(extraParams))

  /**
   *  add the callback handler to a given route.
   * @param route a given route e.g.: `/callback`
   * @return self
   */
  def setupCallback(route: Route): OAuth2AuthHandler =
    OAuth2AuthHandler(unwrap.setupCallback(route))
}
object OAuth2AuthHandler {
  implicit def javaOAuth2AuthHandlerToVerticesOAuth2AuthHandler(j: JavaOAuth2AuthHandler): OAuth2AuthHandler = apply(j)
  implicit def verticesOAuth2AuthHandlerToJavaOAuth2AuthHandler(v: OAuth2AuthHandler): JavaOAuth2AuthHandler = v.unwrap

  /**
   *  Create a OAuth2 auth handler with host pinning
   * @param authProvider  the auth provider to use
   * @param callbackURL the callback URL you entered in your provider admin console, usually it should be something like: `https://myserver:8888/callback`
   * @return the auth handler
   */
  def create(authProvider: OAuth2Auth, callbackURL: String): OAuth2AuthHandler =
    OAuth2AuthHandler(JavaOAuth2AuthHandler.create(authProvider, callbackURL))

  /**
   *  Create a OAuth2 auth handler without host pinning.
   *  Most providers will not look to the redirect url but always redirect to
   *  the preconfigured callback. So this factory does not provide a callback url.
   * @param authProvider  the auth provider to use
   * @return the auth handler
   */
  def create(authProvider: OAuth2Auth): OAuth2AuthHandler =
    OAuth2AuthHandler(JavaOAuth2AuthHandler.create(authProvider))
}
