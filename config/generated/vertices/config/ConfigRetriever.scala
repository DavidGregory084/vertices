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

  /**
   *  Defines a configuration retriever that read configuration from
   *  {@link ConfigStore}
   *  and tracks changes periodically.
   * @author <a href="http://escoffier.me">Clement Escoffier</a>
   */
case class ConfigRetriever(val unwrap: JavaConfigRetriever) extends AnyVal {
  /**
   *  Reads the configuration from the different {@link ConfigStore}
   *  and computes the final configuration.
   * @param completionHandler handler receiving the computed configuration, or a failure if the
   *                           configuration cannot be retrieved
   */
  def getConfig(): Task[JsonObject] =
    Task.handle[JsonObject] { completionHandler =>
      unwrap.getConfig(completionHandler)
    }

  /**
   *  Closes the retriever.
   */
  def close(): Unit =
    unwrap.close()

  /**
   *  Gets the last computed configuration.
   * @return the last configuration
   */
  def getCachedConfig(): JsonObject =
    unwrap.getCachedConfig()

  /**
   *  Registers a listener receiving configuration changes. This method cannot only be called if
   *  the configuration is broadcasted.
   * @param listener the listener
   */
  def listen(listener: Handler[ConfigChange]): Unit =
    unwrap.listen(listener)

  /**
   *  Registers a handler called before every scan. This method is mostly used for logging purpose.
   * @param function the function, must not be {@code null}
   * @return the current config retriever
   */
  def setBeforeScanHandler(function: Handler[Void]): ConfigRetriever =
    ConfigRetriever(unwrap.setBeforeScanHandler(function))

  /**
   *  Registers a handler that process the configuration before being injected into {@link #getConfig(Handler)} or {@link #listen(Handler)}. This allows
   *  the code to customize the configuration.
   * @param processor the processor, must not be {@code null}. The method must not return {@code null}. The returned configuration is used. If the processor
   *                   does not update the configuration, it must return the input configuration. If the processor throws an exception, the failure is passed
   *                   to the {@link #getConfig(Handler)} handler.
   * @return the current config retriever
   */
  def setConfigurationProcessor(processor: Function[JsonObject,JsonObject]): ConfigRetriever =
    ConfigRetriever(unwrap.setConfigurationProcessor(processor))

  /**
   * 
   * @return the stream of configurations. It's single stream (unicast) and that delivers the last known config
   *  and the successors periodically.
   */
  def configStream(): ReadStream[JsonObject] =
    unwrap.configStream()
}
object ConfigRetriever {
  implicit def javaConfigRetrieverToVerticesConfigRetriever(j: JavaConfigRetriever): ConfigRetriever = apply(j)
  implicit def verticesConfigRetrieverToJavaConfigRetriever(v: ConfigRetriever): JavaConfigRetriever = v.unwrap

  /**
   *  Creates an instance of the default implementation of the {@link ConfigRetriever}.
   * @param vertx   the vert.x instance
   * @param options the options, must not be {@code null}, must contain the list of configured store.
   * @return the created instance.
   */
  def create(vertx: Vertx, options: ConfigRetrieverOptions): ConfigRetriever =
    ConfigRetriever(JavaConfigRetriever.create(vertx, options))

  /**
   *  Creates an instance of the default implementation of the {@link ConfigRetriever}, using the default
   *  settings (json file, system properties and environment variables).
   * @param vertx the vert.x instance
   * @return the created instance.
   */
  def create(vertx: Vertx): ConfigRetriever =
    ConfigRetriever(JavaConfigRetriever.create(vertx))

  /**
   *  Same as {@link ConfigRetriever#getConfig(Handler)}, but returning a {@link Future} object. The result is a
   *  {@link JsonObject}.
   * @param retriever the config retrieve
   * @return the future completed when the configuration is retrieved
   */
  def getConfigAsFuture(retriever: ConfigRetriever): Future[JsonObject] =
    JavaConfigRetriever.getConfigAsFuture(retriever)
}
