package vertices
package config

import cats.implicits._
import io.vertx.config.ConfigChange
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.{ ConfigRetriever => JavaConfigRetriever }
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.streams.ReadStream
import java.lang.Void
import java.util.function.Function
import monix.eval.Task

import scala.language.implicitConversions

case class ConfigRetriever(val unwrap: JavaConfigRetriever) extends AnyVal {
  // Async handler method
  def getConfig(): Task[JsonObject] =
    Task.handle[JsonObject] { completionHandler =>
      unwrap.getConfig(completionHandler)
    }

  // Standard method
  def close(): Unit =
    unwrap.close()

  // Standard method
  def getCachedConfig(): JsonObject =
    unwrap.getCachedConfig()

  // Standard method
  def listen(listener: Handler[ConfigChange]): Unit =
    unwrap.listen(listener)

  // Wrapper method
  def setBeforeScanHandler(function: Handler[Void]): ConfigRetriever =
    ConfigRetriever(unwrap.setBeforeScanHandler(function))

  // Wrapper method
  def setConfigurationProcessor(processor: Function[JsonObject,JsonObject]): ConfigRetriever =
    ConfigRetriever(unwrap.setConfigurationProcessor(processor))

  // Standard method
  def configStream(): ReadStream[JsonObject] =
    unwrap.configStream()
}
object ConfigRetriever {
  implicit def javaConfigRetrieverToVerticesConfigRetriever(j: JavaConfigRetriever): ConfigRetriever = apply(j)
  implicit def verticesConfigRetrieverToJavaConfigRetriever(v: ConfigRetriever): JavaConfigRetriever = v.unwrap

  // Wrapper method
  def create(vertx: Vertx, options: ConfigRetrieverOptions): ConfigRetriever =
    ConfigRetriever(JavaConfigRetriever.create(vertx, options))

  // Wrapper method
  def create(vertx: Vertx): ConfigRetriever =
    ConfigRetriever(JavaConfigRetriever.create(vertx))

  // Standard method
  def getConfigAsFuture(retriever: ConfigRetriever): Future[JsonObject] =
    JavaConfigRetriever.getConfigAsFuture(retriever)
}
