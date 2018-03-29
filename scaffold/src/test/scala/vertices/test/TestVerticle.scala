package vertices
package test

import monix.eval.Task
import monix.reactive.Observable

class EchoHandler(vertx: Vertx) extends RpcHandler[Int, Int]("echo", vertx, i => Task.pure(i))

class AddOneHandler(vertx: Vertx) extends RpcHandler[Int, Int]("addOne", vertx, i => Task.pure(i + 1))

class EchoRepeatedlyHandler(vertx: Vertx) extends ServerStreamHandler[Int, Int](
  "echoRepeatedly", vertx,
  i => Observable.fromIterable(List.fill(5)(i)))

class SumHandler(vertx: Vertx) extends ClientStreamHandler[Int, Int](
  "sum", vertx,
  obs => obs.foldLeftL(0)(_ + _))

class EchoStreamHandler(vertx: Vertx) extends BidiStreamHandler[Int, Int](
  "echoStream",
  vertx,
  obs => obs)

class TestVerticle extends VertexVerticle {
  def startUp = Task.unit
  def shutDown = Task.unit
}
