import scala.util.{ Success, Failure }

import cats.Contravariant
import io.vertx.core.{ AsyncResult, Future => VertexFuture, Handler }
import io.vertx.core.streams.{ Pump, ReadStream, WriteStream }
import io.vertx.ext.reactivestreams.{ ReactiveReadStream, ReactiveWriteStream }
import monix.execution.Cancelable
import monix.execution.misc.NonFatal
import monix.eval.{ Callback, Task }
import monix.reactive.{ Observable, Observer }
import shapeless.=:!=
import vertices.core.Vertx

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
      val startStream = Task.eval {
        val writeStream = ReactiveWriteStream.writeStream[A](vertx.unwrap)
        Pump.pump(readStream, writeStream).start()
        writeStream
      }

      Observable
        .fromTask(startStream)
        .flatMap(Observable.fromReactivePublisher(_))
    }
  }

  implicit class VertxWriteStreamOps[A](writeStream: WriteStream[A]) {
    def toObserver: Task[Observer[A]] = Task.deferAction { implicit s =>
      Task.eval {
        val readStream = ReactiveReadStream.readStream[A]()
        Pump.pump(readStream, writeStream).start()
        Observer.fromReactiveSubscriber(readStream, Cancelable.empty)
      }
    }
  }

  implicit val verticesContravariantForHandler: Contravariant[Handler] = new Contravariant[Handler] {
    def contramap[A, B](handler: Handler[A], f: B => A): Handler[B] =
      b => handler.handle(f(b))
  }
}
