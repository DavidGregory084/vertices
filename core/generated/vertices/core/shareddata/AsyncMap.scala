package vertices
package core.shareddata

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.shareddata.{ AsyncMap => JavaAsyncMap }
import java.lang.Boolean
import java.lang.Integer
import java.lang.Void
import monix.eval.Task

import scala.language.implicitConversions

case class AsyncMap[K, V](val unwrap: JavaAsyncMap[K, V])  {
  // Async handler method
  def get(k: K): Task[V] =
    Task.handle[V] { resultHandler =>
      unwrap.get(k, resultHandler)
    }

  // Async handler method
  def put(k: K, v: V): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.put(k, v, completionHandler)
    }.map(_ => ())

  // Async handler method
  def put(k: K, v: V, ttl: Long): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.put(k, v, ttl, completionHandler)
    }.map(_ => ())

  // Async handler method
  def putIfAbsent(k: K, v: V): Task[V] =
    Task.handle[V] { completionHandler =>
      unwrap.putIfAbsent(k, v, completionHandler)
    }

  // Async handler method
  def putIfAbsent(k: K, v: V, ttl: Long): Task[V] =
    Task.handle[V] { completionHandler =>
      unwrap.putIfAbsent(k, v, ttl, completionHandler)
    }

  // Async handler method
  def remove(k: K): Task[V] =
    Task.handle[V] { resultHandler =>
      unwrap.remove(k, resultHandler)
    }

  // Async handler method
  def removeIfPresent(k: K, v: V): Task[Boolean] =
    Task.handle[java.lang.Boolean] { resultHandler =>
      unwrap.removeIfPresent(k, v, resultHandler)
    }.map(out => out: Boolean)

  // Async handler method
  def replace(k: K, v: V): Task[V] =
    Task.handle[V] { resultHandler =>
      unwrap.replace(k, v, resultHandler)
    }

  // Async handler method
  def replaceIfPresent(k: K, oldValue: V, newValue: V): Task[Boolean] =
    Task.handle[java.lang.Boolean] { resultHandler =>
      unwrap.replaceIfPresent(k, oldValue, newValue, resultHandler)
    }.map(out => out: Boolean)

  // Async handler method
  def clear(): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.clear(resultHandler)
    }.map(_ => ())

  // Async handler method
  def size(): Task[Int] =
    Task.handle[java.lang.Integer] { resultHandler =>
      unwrap.size(resultHandler)
    }.map(out => out: Int)
}
object AsyncMap {
  implicit def javaAsyncMapToVerticesAsyncMap[K, V](j: JavaAsyncMap[K, V]): AsyncMap[K, V] = apply(j)
  implicit def verticesAsyncMapToJavaAsyncMap[K, V](v: AsyncMap[K, V]): JavaAsyncMap[K, V] = v.unwrap


}
