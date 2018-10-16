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

  /**
   *  An asynchronous map.
   *  <p>
   *  {@link AsyncMap} does <em>not</em> allow {@code null} to be used as a key or value.
   * @implSpec Implementations of the interface must handle {@link io.vertx.core.shareddata.impl.ClusterSerializable}
   *  implementing objects.
   * @author <a href="http://tfox.org">Tim Fox</a>
   */
case class AsyncMap[K, V](val unwrap: JavaAsyncMap[K, V])  {
  /**
   *  Get a value from the map, asynchronously.
   * @param k  the key
   * @param resultHandler - this will be called some time later with the async result.
   */
  def get(k: K): Task[V] =
    Task.handle[V] { resultHandler =>
      unwrap.get(k, resultHandler)
    }

  /**
   *  Put a value in the map, asynchronously.
   * @param k  the key
   * @param v  the value
   * @param completionHandler - this will be called some time later to signify the value has been put
   */
  def put(k: K, v: V): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.put(k, v, completionHandler)
    }.map(_ => ())

  /**
   *  Like {@link #put} but specifying a time to live for the entry. Entry will expire and get evicted after the
   *  ttl.
   * @param k  the key
   * @param v  the value
   * @param ttl  The time to live (in ms) for the entry
   * @param completionHandler  the handler
   */
  def put(k: K, v: V, ttl: Long): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.put(k, v, ttl, completionHandler)
    }.map(_ => ())

  /**
   *  Put the entry only if there is no entry with the key already present. If key already present then the existing
   *  value will be returned to the handler, otherwise null.
   * @param k  the key
   * @param v  the value
   * @param completionHandler  the handler
   */
  def putIfAbsent(k: K, v: V): Task[V] =
    Task.handle[V] { completionHandler =>
      unwrap.putIfAbsent(k, v, completionHandler)
    }

  /**
   *  Link {@link #putIfAbsent} but specifying a time to live for the entry. Entry will expire and get evicted
   *  after the ttl.
   * @param k  the key
   * @param v  the value
   * @param ttl  The time to live (in ms) for the entry
   * @param completionHandler  the handler
   */
  def putIfAbsent(k: K, v: V, ttl: Long): Task[V] =
    Task.handle[V] { completionHandler =>
      unwrap.putIfAbsent(k, v, ttl, completionHandler)
    }

  /**
   *  Remove a value from the map, asynchronously.
   * @param k  the key
   * @param resultHandler - this will be called some time later to signify the value has been removed
   */
  def remove(k: K): Task[V] =
    Task.handle[V] { resultHandler =>
      unwrap.remove(k, resultHandler)
    }

  /**
   *  Remove a value from the map, only if entry already exists with same value.
   * @param k  the key
   * @param v  the value
   * @param resultHandler - this will be called some time later to signify the value has been removed
   */
  def removeIfPresent(k: K, v: V): Task[Boolean] =
    Task.handle[java.lang.Boolean] { resultHandler =>
      unwrap.removeIfPresent(k, v, resultHandler)
    }.map(out => out: Boolean)

  /**
   *  Replace the entry only if it is currently mapped to some value
   * @param k  the key
   * @param v  the new value
   * @param resultHandler  the result handler will be passed the previous value
   */
  def replace(k: K, v: V): Task[V] =
    Task.handle[V] { resultHandler =>
      unwrap.replace(k, v, resultHandler)
    }

  /**
   *  Replace the entry only if it is currently mapped to a specific value
   * @param k  the key
   * @param oldValue  the existing value
   * @param newValue  the new value
   * @param resultHandler the result handler
   */
  def replaceIfPresent(k: K, oldValue: V, newValue: V): Task[Boolean] =
    Task.handle[java.lang.Boolean] { resultHandler =>
      unwrap.replaceIfPresent(k, oldValue, newValue, resultHandler)
    }.map(out => out: Boolean)

  /**
   *  Clear all entries in the map
   * @param resultHandler  called on completion
   */
  def clear(): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.clear(resultHandler)
    }.map(_ => ())

  /**
   *  Provide the number of entries in the map
   * @param resultHandler  handler which will receive the number of entries
   */
  def size(): Task[Int] =
    Task.handle[java.lang.Integer] { resultHandler =>
      unwrap.size(resultHandler)
    }.map(out => out: Int)
}
object AsyncMap {
  implicit def javaAsyncMapToVerticesAsyncMap[K, V](j: JavaAsyncMap[K, V]): AsyncMap[K, V] = apply(j)
  implicit def verticesAsyncMapToJavaAsyncMap[K, V](v: AsyncMap[K, V]): JavaAsyncMap[K, V] = v.unwrap


}
