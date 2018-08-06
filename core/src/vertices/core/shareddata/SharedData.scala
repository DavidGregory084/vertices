package vertices
package core.shareddata

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.shareddata.LocalMap
import io.vertx.core.shareddata.Lock
import io.vertx.core.shareddata.{ AsyncMap => JavaAsyncMap }
import io.vertx.core.shareddata.{ Counter => JavaCounter }
import io.vertx.core.shareddata.{ SharedData => JavaSharedData }
import java.lang.String
import monix.eval.Task

import scala.language.implicitConversions

case class SharedData(val unwrap: JavaSharedData) extends AnyVal {
  // Async handler method
  def getClusterWideMap[K, V](name: String): Task[AsyncMap[K,V]] =
    Task.handle[JavaAsyncMap[K,V]] { resultHandler =>
      unwrap.getClusterWideMap(name, resultHandler)
    }.map(out => AsyncMap[K,V](out))

  // Async handler method
  def getAsyncMap[K, V](name: String): Task[AsyncMap[K,V]] =
    Task.handle[JavaAsyncMap[K,V]] { resultHandler =>
      unwrap.getAsyncMap(name, resultHandler)
    }.map(out => AsyncMap[K,V](out))

  // Async handler method
  def getLock(name: String): Task[Lock] =
    Task.handle[Lock] { resultHandler =>
      unwrap.getLock(name, resultHandler)
    }

  // Async handler method
  def getLockWithTimeout(name: String, timeout: Long): Task[Lock] =
    Task.handle[Lock] { resultHandler =>
      unwrap.getLockWithTimeout(name, timeout, resultHandler)
    }

  // Async handler method
  def getCounter(name: String): Task[Counter] =
    Task.handle[JavaCounter] { resultHandler =>
      unwrap.getCounter(name, resultHandler)
    }.map(out => Counter(out))

  // Standard method
  def getLocalMap[K, V](name: String): LocalMap[K,V] =
    unwrap.getLocalMap(name)
}
object SharedData {
  implicit def javaSharedDataToVerticesSharedData(j: JavaSharedData): SharedData = apply(j)
  implicit def verticesSharedDataToJavaSharedData(v: SharedData): JavaSharedData = v.unwrap


}
