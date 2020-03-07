package vertices
package core

import io.vertx.core.Vertx
import java.util.concurrent.TimeUnit
import monix.execution.{ Cancelable, ExecutionModel }
import monix.execution.schedulers.ReferenceScheduler

class VertxScheduler(vertx: Vertx) extends ReferenceScheduler {
  val executionModel = ExecutionModel.Default

  def execute(command: Runnable): Unit =
    vertx.runOnContext(_ => command.run())

  def reportFailure(t: Throwable): Unit = {
    val handler = vertx.exceptionHandler
    if (handler != null) handler.handle(t)
  }

  def scheduleOnce(initialDelay: Long, unit: TimeUnit, r: Runnable): Cancelable = {
    val delayMs = unit.convert(initialDelay, TimeUnit.MILLISECONDS)
    val timerId = vertx.setTimer(delayMs, _ => execute(r))
    Cancelable(() => vertx.cancelTimer(timerId))
  }
}
