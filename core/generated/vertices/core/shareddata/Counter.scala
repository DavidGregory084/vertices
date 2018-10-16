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

  /**
   *  An asynchronous counter that can be used to across the cluster to maintain a consistent count.
   *  <p>
   * @author <a href="http://tfox.org">Tim Fox</a>
   */
case class Counter(val unwrap: JavaCounter) extends AnyVal {
  /**
   *  Get the current value of the counter
   * @param resultHandler handler which will be passed the value
   */
  def get(): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.get(resultHandler)
    }.map(out => out: Long)

  /**
   *  Increment the counter atomically and return the new count
   * @param resultHandler handler which will be passed the value
   */
  def incrementAndGet(): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.incrementAndGet(resultHandler)
    }.map(out => out: Long)

  /**
   *  Increment the counter atomically and return the value before the increment.
   * @param resultHandler handler which will be passed the value
   */
  def getAndIncrement(): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.getAndIncrement(resultHandler)
    }.map(out => out: Long)

  /**
   *  Decrement the counter atomically and return the new count
   * @param resultHandler handler which will be passed the value
   */
  def decrementAndGet(): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.decrementAndGet(resultHandler)
    }.map(out => out: Long)

  /**
   *  Add the value to the counter atomically and return the new count
   * @param value  the value to add
   * @param resultHandler handler which will be passed the value
   */
  def addAndGet(value: Long): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.addAndGet(value, resultHandler)
    }.map(out => out: Long)

  /**
   *  Add the value to the counter atomically and return the value before the add
   * @param value  the value to add
   * @param resultHandler handler which will be passed the value
   */
  def getAndAdd(value: Long): Task[Long] =
    Task.handle[java.lang.Long] { resultHandler =>
      unwrap.getAndAdd(value, resultHandler)
    }.map(out => out: Long)

  /**
   *  Set the counter to the specified value only if the current value is the expectec value. This happens
   *  atomically.
   * @param expected  the expected value
   * @param value  the new value
   * @param resultHandler  the handler will be passed true on success
   */
  def compareAndSet(expected: Long, value: Long): Task[Boolean] =
    Task.handle[java.lang.Boolean] { resultHandler =>
      unwrap.compareAndSet(expected, value, resultHandler)
    }.map(out => out: Boolean)
}
object Counter {
  implicit def javaCounterToVerticesCounter(j: JavaCounter): Counter = apply(j)
  implicit def verticesCounterToJavaCounter(v: Counter): JavaCounter = v.unwrap


}
