package vertices
package ext.web.sstore

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.ext.web.Session
import io.vertx.ext.web.sstore.{ SessionStore => JavaSessionStore }
import java.lang.Integer
import java.lang.String
import java.lang.Void
import monix.eval.Task

import scala.language.implicitConversions

case class SessionStore(val unwrap: JavaSessionStore) extends AnyVal {
  // Standard method
  def retryTimeout(): Long =
    unwrap.retryTimeout()

  // Standard method
  def createSession(timeout: Long): Session =
    unwrap.createSession(timeout)

  // Standard method
  def createSession(timeout: Long, length: Int): Session =
    unwrap.createSession(timeout, length)

  // Async handler method
  def get(id: String): Task[Session] =
    Task.handle[Session] { resultHandler =>
      unwrap.get(id, resultHandler)
    }

  // Async handler method
  def delete(id: String): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.delete(id, resultHandler)
    }.map(_ => ())

  // Async handler method
  def put(session: Session): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.put(session, resultHandler)
    }.map(_ => ())

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

  // Standard method
  def close(): Unit =
    unwrap.close()
}
object SessionStore {
  implicit def javaSessionStoreToVerticesSessionStore(j: JavaSessionStore): SessionStore = apply(j)
  implicit def verticesSessionStoreToJavaSessionStore(v: SessionStore): JavaSessionStore = v.unwrap


}