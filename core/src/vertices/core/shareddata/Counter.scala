package vertices
package core.shareddata

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.shareddata.{ Counter => JavaCounter }
import java.lang.Boolean
import java.lang.Long
import monix.eval.Task

import scala.language.implicitConversions

case class Counter(val unwrap: JavaCounter) extends AnyVal {
  // Async handler method
  def get(): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.get(resultHandler)
    }.map(out => out: Long)

  // Async handler method
  def incrementAndGet(): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.incrementAndGet(resultHandler)
    }.map(out => out: Long)

  // Async handler method
  def getAndIncrement(): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.getAndIncrement(resultHandler)
    }.map(out => out: Long)

  // Async handler method
  def decrementAndGet(): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.decrementAndGet(resultHandler)
    }.map(out => out: Long)

  // Async handler method
  def addAndGet(value: Long): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.addAndGet(value, resultHandler)
    }.map(out => out: Long)

  // Async handler method
  def getAndAdd(value: Long): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.getAndAdd(value, resultHandler)
    }.map(out => out: Long)

  // Async handler method
  def compareAndSet(expected: Long, value: Long): Task[Boolean] =
    Task.handle[java.lang.Boolean] { resultHandler =>
      unwrap.compareAndSet(expected, value, resultHandler)
    }.map(out => out: Boolean)
}
object Counter {
  implicit def javaCounterToVerticesCounter(j: JavaCounter): Counter = apply(j)
  implicit def verticesCounterToJavaCounter(v: Counter): JavaCounter = v.unwrap


}
