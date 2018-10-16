package vertices
package ext.web.common.template

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.common.template.{ TemplateEngine => JavaTemplateEngine }
import java.lang.String
import monix.eval.Task

import scala.language.implicitConversions

  /**
   *  A template template uses a specific template and the data in a routing context to render a resource into a buffer.
   *  <p>
   *  Concrete implementations exist for several well-known template engines.
   * @author <a href="http://tfox.org">Tim Fox</a>
   */
case class TemplateEngine(val unwrap: JavaTemplateEngine) extends AnyVal {
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
  def render(context: JsonObject, templateFileName: String): Task[Buffer] =
    Task.handle[Buffer] { handler =>
      unwrap.render(context, templateFileName, handler)
    }

  /**
   *  Returns true if the template template caches template files. If false, then template files are freshly loaded each
   *  time they are used.
   * @return True if template files are cached; otherwise, false.
   */
  def isCachingEnabled(): Boolean =
    unwrap.isCachingEnabled()
}
object TemplateEngine {
  implicit def javaTemplateEngineToVerticesTemplateEngine(j: JavaTemplateEngine): TemplateEngine = apply(j)
  implicit def verticesTemplateEngineToJavaTemplateEngine(v: TemplateEngine): JavaTemplateEngine = v.unwrap


}
