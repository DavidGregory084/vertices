package vertices

import cats._, implicits._
import io.circe.parser
import io.circe.syntax._
import monix.eval.Task
import monix.execution.Scheduler
import org.scalatest._

class VertexVerticleSpec extends AsyncFlatSpec with Matchers with BeforeAndAfterAll {
  implicit val scheduler: Scheduler = Scheduler(executionContext)

  val vertx: Vertx = Vertx.vertx

  override def afterAll() = {
    vertx.unwrap.close()
  }

  def withVerticle(test: Task[Assertion]) = {
    val deployVerticle =
      vertx.deployVerticle("vertices.test.TestVerticle")
    val undeployVerticle =
      (id: String) => vertx.undeploy(id)

    val runTest = for {
      id <- deployVerticle
      assertion <- test
      _ <- undeployVerticle(id)
    } yield assertion

    runTest.runAsync
  }

  "VertexVerticle" should "register handlers for concrete subclasses of VertexHandler" in withVerticle {
    val request =
      RpcRequest(1).asJson.noSpaces
    val sendMessageToEcho =
      vertx.eventBus.send[String]("vertices.test.echo", request)
    val sendMessageToAddOne =
      vertx.eventBus.send[String]("vertices.test.addOne", request)

    // val sendMessageToSum =
    //   vertx.eventBus.send[String]("vertices.test.sum", ClientStreamRequest.asJson.noSpaces)
    // val sendMessageToEchoStream =
    //   vertx.eventBus.send[String]("vertices.test.echoStream", BidiStreamRequest("echoStreamReply").asJson.noSpaces)

    for {
      msg1 <- sendMessageToEcho
      assertion1 = msg1.body shouldBe "1"
      msg2 <- sendMessageToAddOne
      assertion2 = msg2.body shouldBe "2"
    } yield assertion2
  }

  it should "support server side streaming" in withVerticle {
    val request =
      ServerStreamRequest(1, "echoRepeatReply").asJson.noSpaces
    val sendMessageToEchoRepeat =
      vertx.eventBus.send[String]("vertices.test.echoRepeatedly", request)
    val registerReplyHandler =
      Task.eval { vertx.eventBus.consumer[String]("echoRepeatReply") }

    for {
      consumer <- registerReplyHandler
      // TODO: Establish a messaging protocol that indicates end of stream
      msgs <- sendMessageToEchoRepeat *> consumer.toObservable(vertx).take(5).toListL
      decoded = msgs.map(msg => parser.parse(msg.body).flatMap(_.as[Int]).right.get)
      _ = consumer.unregister()
    } yield decoded shouldBe List(1, 1, 1, 1, 1)
  }
}
