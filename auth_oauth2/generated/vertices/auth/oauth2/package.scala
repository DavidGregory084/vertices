package vertices
package auth

import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.oauth2.AccessToken
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.ext.auth.oauth2.OAuth2ClientOptions
import io.vertx.ext.auth.oauth2.OAuth2FlowType
import io.vertx.ext.auth.oauth2.OAuth2RBAC
import io.vertx.ext.auth.oauth2.providers.AzureADAuth
import io.vertx.ext.auth.oauth2.providers.GoogleAuth
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth
import io.vertx.ext.auth.oauth2.providers.OpenIDConnectAuth
import io.vertx.ext.auth.oauth2.providers.SalesforceAuth
import java.lang.Boolean
import java.lang.String
import java.lang.Void

package object oauth2 {

  object AzureADAuthFunctions {
    /**
     *  Create a OAuth2Auth provider for OpenID Connect Discovery. The discovery will use the default site in the
     *  configuration options and attempt to load the well known descriptor. If a site is provided (for example when
     *  running on a custom instance) that site will be used to do the lookup.
     *  <p>
     *  If the discovered config includes a json web key url, it will be also fetched and the JWKs will be loaded
     *  into the OAuth provider so tokens can be decoded.
     * @param vertx   the vertx instance
     * @param config  the initial config
     * @param handler the instantiated Oauth2 provider instance handler
     */
    def discoverL(vertx: Vertx, config: OAuth2ClientOptions): Task[OAuth2Auth] =
      Task.handle[OAuth2Auth] { handler =>
        AzureADAuth.discover(vertx, config, handler)
      }
  }


  object KeycloakAuthFunctions {
    /**
     *  Create a OAuth2Auth provider for OpenID Connect Discovery. The discovery will use the default site in the
     *  configuration options and attempt to load the well known descriptor. If a site is provided (for example when
     *  running on a custom instance) that site will be used to do the lookup.
     *  <p>
     *  If the discovered config includes a json web key url, it will be also fetched and the JWKs will be loaded
     *  into the OAuth provider so tokens can be decoded.
     * @param vertx   the vertx instance
     * @param config  the initial config
     * @param handler the instantiated Oauth2 provider instance handler
     */
    def discoverL(vertx: Vertx, config: OAuth2ClientOptions): Task[OAuth2Auth] =
      Task.handle[OAuth2Auth] { handler =>
        KeycloakAuth.discover(vertx, config, handler)
      }
  }


  object OpenIDConnectAuthFunctions {
    /**
     *  Create a OAuth2Auth provider for OpenID Connect Discovery. The discovery will use the given site in the
     *  configuration options and attempt to load the well known descriptor.
     * 
     *  If the discovered config includes a json web key url, it will be also fetched and the JWKs will be loaded
     *  into the OAuth provider so tokens can be decoded.
     * @param vertx the vertx instance
     * @param config the initial config, it should contain a site url
     * @param handler the instantiated Oauth2 provider instance handler
     */
    def discoverL(vertx: Vertx, config: OAuth2ClientOptions): Task[OAuth2Auth] =
      Task.handle[OAuth2Auth] { handler =>
        OpenIDConnectAuth.discover(vertx, config, handler)
      }
  }

  implicit class VertxOAuth2RBACOps(val target: OAuth2RBAC) extends AnyVal {
    /**
     *  This method should verify if the user has the given authority and return either a boolean value or an error.
     * 
     *  Note that false and errors are not the same. A user might not have a given authority but that doesn't mean that
     *  there was an error during the call.
     * @param user the given user to assert on
     * @param authority the authority to lookup
     * @param handler the result handler.
     */
    def isAuthorizedL(user: AccessToken, authority: String): Task[Boolean] =
      Task.handle[java.lang.Boolean] { handler =>
        target.isAuthorized(user, authority, handler)
      }.map(out => out: Boolean)
  }



  object GoogleAuthFunctions {
    /**
     *  Create a OAuth2Auth provider for OpenID Connect Discovery. The discovery will use the default site in the
     *  configuration options and attempt to load the well known descriptor. If a site is provided (for example when
     *  running on a custom instance) that site will be used to do the lookup.
     *  <p>
     *  If the discovered config includes a json web key url, it will be also fetched and the JWKs will be loaded
     *  into the OAuth provider so tokens can be decoded.
     * @param vertx   the vertx instance
     * @param config  the initial config
     * @param handler the instantiated Oauth2 provider instance handler
     */
    def discoverL(vertx: Vertx, config: OAuth2ClientOptions): Task[OAuth2Auth] =
      Task.handle[OAuth2Auth] { handler =>
        GoogleAuth.discover(vertx, config, handler)
      }
  }




  object SalesforceAuthFunctions {
    /**
     *  Create a OAuth2Auth provider for OpenID Connect Discovery. The discovery will use the default site in the
     *  configuration options and attempt to load the well known descriptor. If a site is provided (for example when
     *  running on a custom instance) that site will be used to do the lookup.
     *  <p>
     *  If the discovered config includes a json web key url, it will be also fetched and the JWKs will be loaded
     *  into the OAuth provider so tokens can be decoded.
     * @param vertx   the vertx instance
     * @param config  the initial config
     * @param handler the instantiated Oauth2 provider instance handler
     */
    def discoverL(vertx: Vertx, config: OAuth2ClientOptions): Task[OAuth2Auth] =
      Task.handle[OAuth2Auth] { handler =>
        SalesforceAuth.discover(vertx, config, handler)
      }
  }

}