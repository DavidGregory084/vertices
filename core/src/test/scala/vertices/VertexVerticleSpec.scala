package vertices

import monix.execution.Scheduler
import org.scalatest._

class VertexVerticleSpec extends AsyncFlatSpec with Matchers {
  implicit val scheduler: Scheduler = Scheduler(executionContext)

  "VertexVerticle" should "register handlers for concrete subclasses of VertexHandler" in {
    val vertx = Vertx.vertx

    val deployVerticle =
      vertx.deployVerticle("vertices.test.TestVerticle")
    val undeployVerticle =
      (id: String) => vertx.undeploy(id)
    val sendMessageToEcho =
      vertx.eventBus.send[String]("vertices.test.echo", """{"RpcRequest":{"body":1}}""")
    val sendMessageToAddOne =
      vertx.eventBus.send[String]("vertices.test.addOne", """{"RpcRequest":{"body":1}}""")
    val closeVertx = vertx.close()

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
