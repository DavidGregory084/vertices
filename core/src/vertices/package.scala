import scala.util.{ Success, Failure }

import cats.{ Contravariant, Functor }
import io.vertx.core.{ AsyncResult, Future => VertxFuture, Handler }
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

  implicit class VertxVoidFutureOps[A <: Void](future: VertxFuture[A]) {
    def completeWith[B](task: Task[B]): Task[Unit] = {
      task.materialize.map {
        case Success(_) => future.complete()
        case Failure(error) => future.fail(error)
      }
    }
  }

  implicit class VertxFutureOps[A](future: VertxFuture[A])(implicit neq: A =:!= Void) {
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
    def contramap[A, B](handler: Handler[A])(f: B => A): Handler[B] =
      b => handler.handle(f(b))
  }

  implicit val verticesContravariantForWriteStream: Contravariant[WriteStream] = new Contravariant[WriteStream] {
    def contramap[A, B](writeStream: WriteStream[A])(f: B => A): WriteStream[B] = new WriteStream[B] {
      def drainHandler(end: Handler[Void]): WriteStream[B] = {
        writeStream.drainHandler(end)
        this
      }
      def end(): Unit =
        writeStream.end()
      def exceptionHandler(exc: Handler[Throwable]): WriteStream[B] = {
        writeStream.exceptionHandler(exc)
        this
      }
      def setWriteQueueMaxSize(size: Int): WriteStream[B] = {
        writeStream.setWriteQueueMaxSize(size)
        this
      }
      def write(b: B): WriteStream[B] = {
        writeStream.write(f(b))
        this
      }
      def writeQueueFull(): Boolean =
        writeStream.writeQueueFull()
    }
  }

  implicit val verticesFunctorForReadStream: Functor[ReadStream] = new Functor[ReadStream] {
    def map[A, B](readStream: ReadStream[A])(f: A => B): ReadStream[B] = new ReadStream[B] {
      def endHandler(end: Handler[java.lang.Void]) = {
        readStream.endHandler(end)
        this
      }
      def exceptionHandler(exc: Handler[Throwable]) = {
        readStream.exceptionHandler(exc)
        this
      }
      def handler(b: Handler[B]) = {
        readStream.handler(verticesContravariantForHandler.contramap(b)(f))
        this
      }
      def pause() = {
        readStream.pause()
        this
      }
      def resume() = {
        readStream.resume()
        this
      }
      def fetch(amount: Long) = {
        readStream.fetch(amount)
        this
      }
    }
  }
}
