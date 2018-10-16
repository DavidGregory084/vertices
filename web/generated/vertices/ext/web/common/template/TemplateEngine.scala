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

case class TemplateEngine(val unwrap: JavaTemplateEngine) extends AnyVal {
  // Async handler method
  def render(context: JsonObject, templateFileName: String): Task[Buffer] =
    Task.handle[Buffer] { handler =>
      unwrap.render(context, templateFileName, handler)
    }

  // Standard method
  def isCachingEnabled(): Boolean =
    unwrap.isCachingEnabled()
}
object TemplateEngine {
  implicit def javaTemplateEngineToVerticesTemplateEngine(j: JavaTemplateEngine): TemplateEngine = apply(j)
  implicit def verticesTemplateEngineToJavaTemplateEngine(v: TemplateEngine): JavaTemplateEngine = v.unwrap


}
