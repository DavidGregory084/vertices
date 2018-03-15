package vertices
package test

import monix.eval.Task

class EchoHandler(vertx: Vertx) extends RpcHandler[Int, Int]("echo", vertx, identity)

class AddOneHandler(vertx: Vertx) extends RpcHandler[Int, Int]("addOne", vertx, _ + 1)

class TestVerticle extends VertexVerticle {
  def startUp = Task.unit
  def shutDown = Task.unit
}
