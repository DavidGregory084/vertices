package vertices
package ext.web.sstore

import cats.implicits._
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Session
import io.vertx.ext.web.sstore.{ SessionStore => JavaSessionStore }
import java.lang.Integer
import java.lang.String
import java.lang.Void
import monix.eval.Task

import scala.language.implicitConversions

/**
 *  A session store is used to store sessions for an Vert.x-Web web app
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
case class SessionStore(val unwrap: JavaSessionStore) extends AnyVal {
  /**
   *  Initialize this store.
   * @param vertx  the vertx instance
   * @param options  optional Json with extra configuration options
   * @return  self
   */
  def init(vertx: Vertx, options: JsonObject): SessionStore =
    SessionStore(unwrap.init(vertx, options))

  /**
   *  The retry timeout value in milli seconds used by the session handler when it retrieves a value from the store.<p/>
   * 
   *  A non positive value means there is no retry at all.
   * @return the timeout value, in ms
   */
  def retryTimeout(): Long =
    unwrap.retryTimeout()

  /**
   *  Create a new session using the default min length.
   * @param timeout - the session timeout, in ms
   * @return the session
   */
  def createSession(timeout: Long): Session =
    unwrap.createSession(timeout)

  /**
   *  Create a new session.
   * @param timeout - the session timeout, in ms
   * @param length - the required length for the session id
   * @return the session
   */
  def createSession(timeout: Long, length: Int): Session =
    unwrap.createSession(timeout, length)

  /**
   *  Get the session with the specified ID.
   * @param cookieValue  the unique ID of the session
   * @param resultHandler  will be called with a result holding the session, or a failure
   */
  def get(cookieValue: String): Task[Session] =
    Task.handle[Session] { resultHandler =>
      unwrap.get(cookieValue, resultHandler)
    }

  /**
   *  Delete the session with the specified ID.
   * @param id  the session id
   * @param resultHandler  will be called with a success or a failure
   */
  def delete(id: String): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.delete(id, resultHandler)
    }.map(_ => ())

  /**
   *  Add a session with the specified ID.
   * @param session  the session
   * @param resultHandler  will be called with a success or a failure
   */
  def put(session: Session): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.put(session, resultHandler)
    }.map(_ => ())

  /**
   *  Remove all sessions from the store.
   * @param resultHandler  will be called with a success or a failure
   */
  def clear(): Task[Unit] =
    Task.handle[Void] { resultHandler =>
      unwrap.clear(resultHandler)
    }.map(_ => ())

  /**
   *  Get the number of sessions in the store.
   *  <p>
   *  Beware of the result which is just an estimate, in particular with distributed session stores.
   * @param resultHandler  will be called with the number, or a failure
   */
  def size(): Task[Int] =
    Task.handle[java.lang.Integer] { resultHandler =>
      unwrap.size(resultHandler)
    }.map(out => out: Int)

  /**
   *  Close the store
   */
  def close(): Unit =
    unwrap.close()
}
object SessionStore {
  implicit def javaSessionStoreToVerticesSessionStore(j: JavaSessionStore): SessionStore = apply(j)
  implicit def verticesSessionStoreToJavaSessionStore(v: SessionStore): JavaSessionStore = v.unwrap

  /**
   *  Create a Session store given a backend and configuration JSON.
   * @param vertx vertx instance
   * @return the store or runtime exception
   */
  def create(vertx: Vertx): SessionStore =
    SessionStore(JavaSessionStore.create(vertx))

  /**
   *  Create a Session store given a backend and configuration JSON.
   * @param vertx vertx instance
   * @param options extra options for initialization
   * @return the store or runtime exception
   */
  def create(vertx: Vertx, options: JsonObject): SessionStore =
    SessionStore(JavaSessionStore.create(vertx, options))
}
