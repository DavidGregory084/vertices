/*
 * Copyright 2018 David Gregory and the Vertices project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vertices

import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.LazyLogging
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
