package vertices

import cats._, implicits._
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import monix.eval.Task
import monix.execution.Scheduler
import org.scalatest._

class VertexVerticleSpec extends AsyncFlatSpec with Matchers {
  implicit val scheduler: Scheduler = Scheduler(executionContext)

  "VertexVerticle" should "register handlers for concrete subclasses of VertexHandler" in {
    val vertx = Vertx.vertx

    val deployVerticle =
      Task.handle[String] { vertx.deployVerticle("vertices.test.TestVerticle", _) }
    val undeployVerticle =
      (id: String) => Task.handle[Void] { vertx.undeploy(id, _) }.void
    val sendMessageToEcho =
      Task.handle[Message[String]] { vertx.eventBus.send("vertices.test.echo", """{ "RpcRequest": { "body": 1 } }""", _) }
    val sendMessageToAddOne =
      Task.handle[Message[String]] { vertx.eventBus.send("vertices.test.addOne", """{ "RpcRequest": { "body": 1 } }""", _) }
    val closeVertx =
      Task.eval { vertx.close() }

    val runTest = for {
      id <- deployVerticle
      msg1 <- sendMessageToEcho
      assertion1 = msg1.body shouldBe "1"
      msg2 <- sendMessageToAddOne
      assertion2 = msg2.body shouldBe "2"
      _ <- undeployVerticle(id)
    } yield assertion2

    runTest
      .doOnFinish(_ => closeVertx)
      .runAsync
  }
}
