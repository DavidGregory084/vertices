package vertices


import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.MultiMap
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.core.net.SocketAddress
import io.vertx.core.streams.Pipe
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import io.vertx.ext.auth.AuthProvider
import io.vertx.ext.auth.User
import io.vertx.ext.auth.htdigest.HtdigestAuth
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.oauth2.OAuth2Auth
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.Session
import io.vertx.ext.web.common.template.TemplateEngine
import io.vertx.ext.web.handler.AuthHandler
import io.vertx.ext.web.handler.BasicAuthHandler
import io.vertx.ext.web.handler.ChainAuthHandler
import io.vertx.ext.web.handler.DigestAuthHandler
import io.vertx.ext.web.handler.JWTAuthHandler
import io.vertx.ext.web.handler.OAuth2AuthHandler
import io.vertx.ext.web.handler.RedirectAuthHandler
import io.vertx.ext.web.handler.sockjs.SockJSSocket
import io.vertx.ext.web.sstore.SessionStore
import java.lang.Integer
import java.lang.Object
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import java.util.List
import java.util.Map
import java.util.Set

package object web {
  implicit class VertxJWTAuthHandlerOps(val target: JWTAuthHandler) extends AnyVal {
    /**
     *  Parses the credentials from the request into a JsonObject. The implementation should
     *  be able to extract the required info for the auth provider in the format the provider
     *  expects.
     * @param context the routing context
     * @param handler the handler to be called once the information is available.
     */
    def parseCredentialsL(context: RoutingContext): Task[JsonObject] =
      Task.handle[JsonObject] { handler =>
        target.parseCredentials(context, handler)
      }

    /**
     *  Authorizes the given user against all added authorities.
     * @param user a user.
     * @param handler the handler for the result.
     */
    def authorizeL(user: User): Task[Unit] =
      Task.handle[Void] { handler =>
        target.authorize(user, handler)
      }.map(_ => ())
  }


  implicit class VertxTemplateEngineOps(val target: TemplateEngine) extends AnyVal {
    /**
     *  Render the template. Template engines that support partials/fragments should extract the template base path from
     *  the template filename up to the last file separator.
     * 
     *  Some engines support localization, for these engines, there is a predefined key "lang" to specify the language to
     *  be used in the localization, the format should follow the standard locale formats e.g.: "en-gb", "pt-br", "en".
     * @param context  the routing context
     * @param templateFileName  the template file name to use
     * @param handler  the handler that will be called with a result containing the buffer or a failure.
     */
    def renderL(context: JsonObject, templateFileName: String): Task[Buffer] =
      Task.handle[Buffer] { handler =>
        target.render(context, templateFileName, handler)
      }
  }


  implicit class VertxDigestAuthHandlerOps(val target: DigestAuthHandler) extends AnyVal {
    /**
     *  Parses the credentials from the request into a JsonObject. The implementation should
     *  be able to extract the required info for the auth provider in the format the provider
     *  expects.
     * @param context the routing context
     * @param handler the handler to be called once the information is available.
     */
    def parseCredentialsL(context: RoutingContext): Task[JsonObject] =
      Task.handle[JsonObject] { handler =>
        target.parseCredentials(context, handler)
      }

    /**
     *  Authorizes the given user against all added authorities.
     * @param user a user.
     * @param handler the handler for the result.
     */
    def authorizeL(user: User): Task[Unit] =
      Task.handle[Void] { handler =>
        target.authorize(user, handler)
      }.map(_ => ())
  }


  implicit class VertxSockJSSocketOps(val target: SockJSSocket) extends AnyVal {

    def endL(): Task[Unit] =
      Task.handle[Void] { arg0 =>
        target.end(arg0)
      }.map(_ => ())


    def endL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(data, handler)
      }.map(_ => ())


    def pipeToL(dst: WriteStream[Buffer]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())


    def writeL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(data, handler)
      }.map(_ => ())
  }


  implicit class VertxChainAuthHandlerOps(val target: ChainAuthHandler) extends AnyVal {
    /**
     *  Parses the credentials from the request into a JsonObject. The implementation should
     *  be able to extract the required info for the auth provider in the format the provider
     *  expects.
     * @param context the routing context
     * @param handler the handler to be called once the information is available.
     */
    def parseCredentialsL(context: RoutingContext): Task[JsonObject] =
      Task.handle[JsonObject] { handler =>
        target.parseCredentials(context, handler)
      }

    /**
     *  Authorizes the given user against all added authorities.
     * @param user a user.
     * @param handler the handler for the result.
     */
    def authorizeL(user: User): Task[Unit] =
      Task.handle[Void] { handler =>
        target.authorize(user, handler)
      }.map(_ => ())
  }


  implicit class VertxOAuth2AuthHandlerOps(val target: OAuth2AuthHandler) extends AnyVal {
    /**
     *  Parses the credentials from the request into a JsonObject. The implementation should
     *  be able to extract the required info for the auth provider in the format the provider
     *  expects.
     * @param context the routing context
     * @param handler the handler to be called once the information is available.
     */
    def parseCredentialsL(context: RoutingContext): Task[JsonObject] =
      Task.handle[JsonObject] { handler =>
        target.parseCredentials(context, handler)
      }

    /**
     *  Authorizes the given user against all added authorities.
     * @param user a user.
     * @param handler the handler for the result.
     */
    def authorizeL(user: User): Task[Unit] =
      Task.handle[Void] { handler =>
        target.authorize(user, handler)
      }.map(_ => ())
  }


  implicit class VertxBasicAuthHandlerOps(val target: BasicAuthHandler) extends AnyVal {
    /**
     *  Parses the credentials from the request into a JsonObject. The implementation should
     *  be able to extract the required info for the auth provider in the format the provider
     *  expects.
     * @param context the routing context
     * @param handler the handler to be called once the information is available.
     */
    def parseCredentialsL(context: RoutingContext): Task[JsonObject] =
      Task.handle[JsonObject] { handler =>
        target.parseCredentials(context, handler)
      }

    /**
     *  Authorizes the given user against all added authorities.
     * @param user a user.
     * @param handler the handler for the result.
     */
    def authorizeL(user: User): Task[Unit] =
      Task.handle[Void] { handler =>
        target.authorize(user, handler)
      }.map(_ => ())
  }


  implicit class VertxSessionStoreOps(val target: SessionStore) extends AnyVal {
    /**
     *  Get the session with the specified ID.
     * @param cookieValue  the unique ID of the session
     * @param resultHandler  will be called with a result holding the session, or a failure
     */
    def getL(cookieValue: String): Task[Session] =
      Task.handle[Session] { resultHandler =>
        target.get(cookieValue, resultHandler)
      }

    /**
     *  Delete the session with the specified ID.
     * @param id  the session id
     * @param resultHandler  will be called with a success or a failure
     */
    def deleteL(id: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.delete(id, resultHandler)
      }.map(_ => ())

    /**
     *  Add a session with the specified ID.
     * @param session  the session
     * @param resultHandler  will be called with a success or a failure
     */
    def putL(session: Session): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.put(session, resultHandler)
      }.map(_ => ())

    /**
     *  Remove all sessions from the store.
     * @param resultHandler  will be called with a success or a failure
     */
    def clearL(): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.clear(resultHandler)
      }.map(_ => ())

    /**
     *  Get the number of sessions in the store.
     *  <p>
     *  Beware of the result which is just an estimate, in particular with distributed session stores.
     * @param resultHandler  will be called with the number, or a failure
     */
    def sizeL(): Task[Int] =
      Task.handle[java.lang.Integer] { resultHandler =>
        target.size(resultHandler)
      }.map(out => out: Int)
  }


  implicit class VertxRedirectAuthHandlerOps(val target: RedirectAuthHandler) extends AnyVal {
    /**
     *  Parses the credentials from the request into a JsonObject. The implementation should
     *  be able to extract the required info for the auth provider in the format the provider
     *  expects.
     * @param context the routing context
     * @param handler the handler to be called once the information is available.
     */
    def parseCredentialsL(context: RoutingContext): Task[JsonObject] =
      Task.handle[JsonObject] { handler =>
        target.parseCredentials(context, handler)
      }

    /**
     *  Authorizes the given user against all added authorities.
     * @param user a user.
     * @param handler the handler for the result.
     */
    def authorizeL(user: User): Task[Unit] =
      Task.handle[Void] { handler =>
        target.authorize(user, handler)
      }.map(_ => ())
  }


}