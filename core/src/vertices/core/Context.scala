package vertices
package core

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.core.{ Context => JavaContext }
import io.vertx.core.{ Vertx => JavaVertx }
import java.lang.Object
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import java.util.List
import monix.eval.Task

import scala.language.implicitConversions

case class Context(val unwrap: JavaContext) extends AnyVal {
  // Standard method
  def runOnContext(action: Handler[Void]): Unit =
    unwrap.runOnContext(action)

  // Async handler method
  def executeBlocking[T](blockingCodeHandler: Handler[Future[T]], ordered: Boolean): Task[T] =
    Task.handle[T] { resultHandler =>
      unwrap.executeBlocking(blockingCodeHandler, ordered, resultHandler)
    }

  // Async handler method
  def executeBlocking[T](blockingCodeHandler: Handler[Future[T]]): Task[T] =
    Task.handle[T] { resultHandler =>
      unwrap.executeBlocking(blockingCodeHandler, resultHandler)
    }

  // Standard method
  def deploymentID(): String =
    unwrap.deploymentID()

  // Standard method
  def config(): JsonObject =
    unwrap.config()

  // Standard method
  def processArgs(): List[String] =
    unwrap.processArgs()

  // Standard method
  def isEventLoopContext(): Boolean =
    unwrap.isEventLoopContext()

  // Standard method
  def isWorkerContext(): Boolean =
    unwrap.isWorkerContext()

  // Standard method
  def isMultiThreadedWorkerContext(): Boolean =
    unwrap.isMultiThreadedWorkerContext()

  // Standard method
  def get[T](key: String): T =
    unwrap.get(key)

  // Standard method
  def put(key: String, value: Object): Unit =
    unwrap.put(key, value)

  // Standard method
  def remove(key: String): Boolean =
    unwrap.remove(key)

  // Wrapper method
  def owner(): Vertx =
    Vertx(unwrap.owner())

  // Standard method
  def getInstanceCount(): Int =
    unwrap.getInstanceCount()

  // Wrapper method
  def exceptionHandler(handler: Handler[Throwable]): Context =
    Context(unwrap.exceptionHandler(handler))
}
object Context {
  implicit def javaContextToVerticesContext(j: JavaContext): Context = apply(j)
  implicit def verticesContextToJavaContext(v: Context): JavaContext = v.unwrap

  // Standard method
  def isOnWorkerThread(): Boolean =
    JavaContext.isOnWorkerThread()

  // Standard method
  def isOnEventLoopThread(): Boolean =
    JavaContext.isOnEventLoopThread()

  // Standard method
  def isOnVertxThread(): Boolean =
    JavaContext.isOnVertxThread()
}
