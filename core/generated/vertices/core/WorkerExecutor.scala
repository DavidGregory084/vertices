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

  /**
   *  An executor for executing blocking code in Vert.x .<p>
   * 
   *  It provides the same <code>executeBlocking</code> operation than {@link io.vertx.core.Context} and
   *  {@link io.vertx.core.Vertx} but on a separate worker pool.<p>
   * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
   */
case class WorkerExecutor(val unwrap: JavaWorkerExecutor) extends AnyVal {
  /**
   *  Whether the metrics are enabled for this measured object
   * @implSpec  The default implementation returns {@code false}
   * @return {@code true} if metrics are enabled
   */
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  /**
   *  Safely execute some blocking code.
   *  <p>
   *  Executes the blocking code in the handler {@code blockingCodeHandler} using a thread from the worker pool.
   *  <p>
   *  When the code is complete the handler {@code resultHandler} will be called with the result on the original context
   *  (i.e. on the original event loop of the caller).
   *  <p>
   *  A {@code Future} instance is passed into {@code blockingCodeHandler}. When the blocking code successfully completes,
   *  the handler should call the {@link Future#complete} or {@link Future#complete(Object)} method, or the {@link Future#fail}
   *  method if it failed.
   *  <p>
   *  In the {@code blockingCodeHandler} the current context remains the original context and therefore any task
   *  scheduled in the {@code blockingCodeHandler} will be executed on the this context and not on the worker thread.
   * @param blockingCodeHandler  handler representing the blocking code to run
   * @param resultHandler  handler that will be called when the blocking code is complete
   * @param ordered  if true then if executeBlocking is called several times on the same context, the executions
   *                  for that context will be executed serially, not in parallel. if false then they will be no ordering
   *                  guarantees
   * @param <T> the type of the result
   */
  def executeBlocking[T](blockingCodeHandler: Handler[Future[T]], ordered: Boolean): Task[T] =
    Task.handle[T] { resultHandler =>
      unwrap.executeBlocking(blockingCodeHandler, ordered, resultHandler)
    }

  /**
   *  Like {@link #executeBlocking(Handler, boolean, Handler)} called with ordered = true.
   */
  def executeBlocking[T](blockingCodeHandler: Handler[Future[T]]): Task[T] =
    Task.handle[T] { resultHandler =>
      unwrap.executeBlocking(blockingCodeHandler, resultHandler)
    }

  /**
   *  Close the executor.
   */
  def close(): Unit =
    unwrap.close()
}
object WorkerExecutor {
  implicit def javaWorkerExecutorToVerticesWorkerExecutor(j: JavaWorkerExecutor): WorkerExecutor = apply(j)
  implicit def verticesWorkerExecutorToJavaWorkerExecutor(v: WorkerExecutor): JavaWorkerExecutor = v.unwrap


}
