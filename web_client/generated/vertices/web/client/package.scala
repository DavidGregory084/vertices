package vertices
package web

import monix.eval.Task
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.MultiMap
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.core.streams.ReadStream
import io.vertx.ext.web.client.HttpRequest
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.predicate.ResponsePredicate
import io.vertx.ext.web.client.predicate.ResponsePredicateResult
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.ext.web.multipart.MultipartForm
import java.lang.Boolean
import java.lang.Iterable
import java.lang.Object
import java.lang.String
import java.lang.Void
import java.util.function.Function

package object client {
  implicit class VertxHttpRequestOps[T](val target: HttpRequest[T])  {
    /**
     *  Like {@link #send(Handler)} but with an HTTP request {@code body} stream.
     * @param body the body
     */
    def sendStreamL(body: ReadStream[Buffer]): Task[HttpResponse[T]] =
      Task.handle[HttpResponse[T]] { handler =>
        target.sendStream(body, handler)
      }

    /**
     *  Like {@link #send(Handler)} but with an HTTP request {@code body} buffer.
     * @param body the body
     */
    def sendBufferL(body: Buffer): Task[HttpResponse[T]] =
      Task.handle[HttpResponse[T]] { handler =>
        target.sendBuffer(body, handler)
      }

    /**
     *  Like {@link #send(Handler)} but with an HTTP request {@code body} object encoded as json and the content type
     *  set to {@code application/json}.
     * @param body the body
     */
    def sendJsonObjectL(body: JsonObject): Task[HttpResponse[T]] =
      Task.handle[HttpResponse[T]] { handler =>
        target.sendJsonObject(body, handler)
      }

    /**
     *  Like {@link #send(Handler)} but with an HTTP request {@code body} object encoded as json and the content type
     *  set to {@code application/json}.
     * @param body the body
     */
    def sendJsonL(body: Object): Task[HttpResponse[T]] =
      Task.handle[HttpResponse[T]] { handler =>
        target.sendJson(body, handler)
      }

    /**
     *  Like {@link #send(Handler)} but with an HTTP request {@code body} multimap encoded as form and the content type
     *  set to {@code application/x-www-form-urlencoded}.
     *  <p>
     *  When the content type header is previously set to {@code multipart/form-data} it will be used instead.
     * @param body the body
     */
    def sendFormL(body: MultiMap): Task[HttpResponse[T]] =
      Task.handle[HttpResponse[T]] { handler =>
        target.sendForm(body, handler)
      }

    /**
     *  Like {@link #send(Handler)} but with an HTTP request {@code body} multimap encoded as form and the content type
     *  set to {@code multipart/form-data}. You may use this method to send attributes and upload files.
     * @param body the body
     */
    def sendMultipartFormL(body: MultipartForm): Task[HttpResponse[T]] =
      Task.handle[HttpResponse[T]] { handler =>
        target.sendMultipartForm(body, handler)
      }

    /**
     *  Send a request, the {@code handler} will receive the response as an {@link HttpResponse}.
     */
    def sendL(): Task[HttpResponse[T]] =
      Task.handle[HttpResponse[T]] { handler =>
        target.send(handler)
      }
  }


}