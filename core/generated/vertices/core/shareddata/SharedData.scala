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

  /**
   *  Shared data allows you to share data safely between different parts of your application in a safe way.
   *  <p>
   *  Shared data provides:
   *  <ul>
   *    <li>synchronous shared maps (local)</li>
   *    <li>asynchronous maps (local or cluster-wide)</li>
   *    <li>asynchronous locks (local or cluster-wide)</li>
   *    <li>asynchronous counters (local or cluster-wide)</li>
   *  </ul>
   *  <p>
   *  <p>
   *    <strong>WARNING</strong>: In clustered mode, asynchronous maps/locks/counters rely on distributed data structures provided by the cluster manager.
   *    Beware that the latency relative to asynchronous maps/locks/counters operations can be much higher in clustered than in local mode.
   *  </p>
   *  Please see the documentation for more information.
   * @author <a href="http://tfox.org">Tim Fox</a>
   */
case class SharedData(val unwrap: JavaSharedData) extends AnyVal {
  /**
   *  Get the cluster wide map with the specified name. The map is accessible to all nodes in the cluster and data
   *  put into the map from any node is visible to to any other node.
   * @param name  the name of the map
   * @param resultHandler  the map will be returned asynchronously in this handler
   * @throws IllegalStateException if the parent {@link io.vertx.core.Vertx} instance is not clustered
   */
  def getClusterWideMap[K, V](name: String): Task[AsyncMap[K,V]] =
    Task.handle[JavaAsyncMap[K,V]] { resultHandler =>
      unwrap.getClusterWideMap(name, resultHandler)
    }.map(out => AsyncMap[K,V](out))

  /**
   *  Get the {@link AsyncMap} with the specified name. When clustered, the map is accessible to all nodes in the cluster
   *  and data put into the map from any node is visible to to any other node.
   *  <p>
   *    <strong>WARNING</strong>: In clustered mode, asynchronous shared maps rely on distributed data structures provided by the cluster manager.
   *    Beware that the latency relative to asynchronous shared maps operations can be much higher in clustered than in local mode.
   *  </p>
   * @param name the name of the map
   * @param resultHandler the map will be returned asynchronously in this handler
   */
  def getAsyncMap[K, V](name: String): Task[AsyncMap[K,V]] =
    Task.handle[JavaAsyncMap[K,V]] { resultHandler =>
      unwrap.getAsyncMap(name, resultHandler)
    }.map(out => AsyncMap[K,V](out))

  /**
   *  Get an asynchronous lock with the specified name. The lock will be passed to the handler when it is available.
   *  <p>
   *    In general lock acquision is unordered, so that sequential attempts to acquire a lock,
   *    even from a single thread, can happen in non-sequential order.
   *  </p>
   * @param name  the name of the lock
   * @param resultHandler  the handler
   */
  def getLock(name: String): Task[Lock] =
    Task.handle[Lock] { resultHandler =>
      unwrap.getLock(name, resultHandler)
    }

  /**
   *  Like {@link #getLock(String, Handler)} but specifying a timeout. If the lock is not obtained within the timeout
   *  a failure will be sent to the handler.
   *  <p>
   *    In general lock acquision is unordered, so that sequential attempts to acquire a lock,
   *    even from a single thread, can happen in non-sequential order.
   *  </p>
   * @param name  the name of the lock
   * @param timeout  the timeout in ms
   * @param resultHandler  the handler
   */
  def getLockWithTimeout(name: String, timeout: Long): Task[Lock] =
    Task.handle[Lock] { resultHandler =>
      unwrap.getLockWithTimeout(name, timeout, resultHandler)
    }

  /**
   *  Get an asynchronous counter. The counter will be passed to the handler.
   * @param name  the name of the counter.
   * @param resultHandler  the handler
   */
  def getCounter(name: String): Task[Counter] =
    Task.handle[JavaCounter] { resultHandler =>
      unwrap.getCounter(name, resultHandler)
    }.map(out => Counter(out))

  /**
   *  Return a {@code LocalMap} with the specific {@code name}.
   * @param name  the name of the map
   * @return the msp
   */
  def getLocalMap[K, V](name: String): LocalMap[K,V] =
    unwrap.getLocalMap(name)
}
object SharedData {
  implicit def javaSharedDataToVerticesSharedData(j: JavaSharedData): SharedData = apply(j)
  implicit def verticesSharedDataToJavaSharedData(v: SharedData): JavaSharedData = v.unwrap


}
