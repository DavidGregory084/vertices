package vertices
package core

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.metrics.Measured
import io.vertx.core.{ WorkerExecutor => JavaWorkerExecutor }
import monix.eval.Task

import scala.language.implicitConversions

case class WorkerExecutor(val unwrap: JavaWorkerExecutor) extends AnyVal {
  // Standard method
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

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
  def close(): Unit =
    unwrap.close()
}
object WorkerExecutor {
  implicit def javaWorkerExecutorToVerticesWorkerExecutor(j: JavaWorkerExecutor): WorkerExecutor = apply(j)
  implicit def verticesWorkerExecutorToJavaWorkerExecutor(v: WorkerExecutor): JavaWorkerExecutor = v.unwrap


}
