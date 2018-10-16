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

  /**
   *  An auth handler that provides JWT Authentication support.
   * @author Paulo Lopes
   */
case class JWTAuthHandler(val unwrap: JavaJWTAuthHandler) extends AnyVal {

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
   *  Set the audience list
   * @param audience  the audience list
   * @return a reference to this for fluency
   */
  def setAudience(audience: List[String]): JWTAuthHandler =
    JWTAuthHandler(unwrap.setAudience(audience))

  /**
   *  Set the issuer
   * @param issuer  the issuer
   * @return a reference to this for fluency
   */
  def setIssuer(issuer: String): JWTAuthHandler =
    JWTAuthHandler(unwrap.setIssuer(issuer))

  /**
   *  Set whether expiration is ignored
   * @param ignoreExpiration  whether expiration is ignored
   * @return a reference to this for fluency
   */
  def setIgnoreExpiration(ignoreExpiration: Boolean): JWTAuthHandler =
    JWTAuthHandler(unwrap.setIgnoreExpiration(ignoreExpiration))
}
object JWTAuthHandler {
  implicit def javaJWTAuthHandlerToVerticesJWTAuthHandler(j: JavaJWTAuthHandler): JWTAuthHandler = apply(j)
  implicit def verticesJWTAuthHandlerToJavaJWTAuthHandler(v: JWTAuthHandler): JavaJWTAuthHandler = v.unwrap

  /**
   *  Create a JWT auth handler
   * @param authProvider  the auth provider to use
   * @return the auth handler
   */
  def create(authProvider: JWTAuth): JWTAuthHandler =
    JWTAuthHandler(JavaJWTAuthHandler.create(authProvider))

  /**
   *  Create a JWT auth handler
   * @param authProvider  the auth provider to use.
   * @return the auth handler
   */
  def create(authProvider: JWTAuth, skip: String): JWTAuthHandler =
    JWTAuthHandler(JavaJWTAuthHandler.create(authProvider, skip))
}
