package vertices

import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.LazyLogging
import io.vertx.core.Vertx
import monix.execution.{ Cancelable, ExecutionModel }
import monix.execution.schedulers.ReferenceScheduler

case class VertexScheduler(vertx: Vertx) extends ReferenceScheduler with LazyLogging {
  val executionModel = ExecutionModel.Default

  def execute(r: Runnable) =
    vertx.runOnContext(_ => r.run())

  def reportFailure(t: Throwable) =
    logger.error("Uncaught exception in Vertex scheduler", t)

  def scheduleOnce(initialDelay: Long, unit: TimeUnit, r: Runnable) = {
    val millis = unit.convert(initialDelay, TimeUnit.MILLISECONDS)
    val timerId = vertx.setTimer(millis, _ => execute(r))
    Cancelable(() => vertx.cancelTimer(timerId))
  }
}
