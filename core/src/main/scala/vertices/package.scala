import scala.util.{ Success, Failure }

import io.vertx.core.{ AsyncResult, Future => VertexFuture, Handler, Vertx }
import io.vertx.core.streams.{ Pump, ReadStream }
import io.vertx.ext.reactivestreams.ReactiveWriteStream
import monix.execution.Cancelable
import monix.execution.misc.NonFatal
import monix.eval.{ Callback, Task }
import monix.reactive.Observable
import shapeless.=:!=

package object vertices {
  implicit class MonixTaskCompanionVertxOps(task: Task.type) {
    def handle[A](f: Handler[AsyncResult[A]] => Unit): Task[A] = {
      def handler(cb: Callback[A]): Handler[AsyncResult[A]] = { result =>
        if (result.succeeded)
          cb.onSuccess(result.result)
        else
          cb.onError(result.cause)
      }

      def runnable(cb: Callback[A]): Runnable =
        () => try f(handler(cb)) catch {
          case NonFatal(e) => cb.onError(e)
        }

      Task.create { (s, cb) =>
        val scb = Callback.safe(cb)(s)
        s.execute(runnable(scb))
        Cancelable.empty
      }
    }
  }

  implicit class VertxVoidFutureOps[A <: Void](future: VertexFuture[A]) {
    def completeWith[B](task: Task[B]): Task[Unit] = {
      task.materialize.map {
        case Success(_) => future.complete()
        case Failure(error) => future.fail(error)
      }
    }
  }

  implicit class VertxFutureOps[A](future: VertexFuture[A])(implicit neq: A =:!= Void) {
    def completeWith(task: Task[A]): Task[Unit] = {
      task.materialize.map {
        case Success(result) => future.complete(result)
        case Failure(error) => future.fail(error)
      }
    }
  }

  implicit class VertxReadStreamOps[A](readStream: ReadStream[A]) {
    def toObservable(vertx: Vertx): Observable[A] = {
      val writeStream = ReactiveWriteStream.writeStream[A](vertx)
      Pump.pump(readStream, writeStream).start()
      readStream.endHandler(_ => writeStream.end())
      Observable.fromReactivePublisher(writeStream)
    }
  }
}
