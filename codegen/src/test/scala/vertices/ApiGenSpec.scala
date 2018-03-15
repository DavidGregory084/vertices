package vertices

import org.scalatest._

import io.vertx.core.{ Handler, Vertx }

class ApiGenSpec extends FlatSpec with Matchers {
  val apiGen = new ApiGen(new java.io.File("."))

  "ApiGen" should "generate Scala wrappers for Java API definitions" in {
    apiGen.source(classOf[Vertx]) shouldBe s"""
      |package vertices
      |package core
      |
      |import io.netty.channel.EventLoopGroup
      |import io.vertx.core.Context
      |import io.vertx.core.DeploymentOptions
      |import io.vertx.core.Handler
      |import io.vertx.core.TimeoutStream
      |import io.vertx.core.Verticle
      |import io.vertx.core.VertxOptions
      |import io.vertx.core.WorkerExecutor
      |import io.vertx.core.datagram.DatagramSocket
      |import io.vertx.core.datagram.DatagramSocketOptions
      |import io.vertx.core.dns.DnsClient
      |import io.vertx.core.dns.DnsClientOptions
      |import io.vertx.core.eventbus.EventBus
      |import io.vertx.core.file.FileSystem
      |import io.vertx.core.http.HttpClient
      |import io.vertx.core.http.HttpClientOptions
      |import io.vertx.core.http.HttpServer
      |import io.vertx.core.http.HttpServerOptions
      |import io.vertx.core.net.NetClient
      |import io.vertx.core.net.NetClientOptions
      |import io.vertx.core.net.NetServer
      |import io.vertx.core.net.NetServerOptions
      |import io.vertx.core.shareddata.SharedData
      |import io.vertx.core.spi.VerticleFactory
      |import io.vertx.core.streams.ReadStream
      |import io.vertx.core.{Vertx => JavaVertx}
      |import java.lang.Class
      |import java.lang.String
      |import java.util.Set
      |import java.util.function.Supplier
      |import monix.eval.Task
      |
      |class Vertx(wrapped: JavaVertx) {
      |  def cancelTimer(arg0: Long): Boolean =
      |    wrapped.cancelTimer(arg0)
      |
      |  def close(): monix.eval.Task[Unit] =
      |    Task
      |      .handle[Void] { handler =>
      |        wrapped.close(handler)
      |      }
      |      .map(_ => ())
      |
      |  def createDatagramSocket(): ReadStream[DatagramPacket] =
      |    wrapped.createDatagramSocket()
      |
      |  def createDatagramSocket(arg0: DatagramSocketOptions): ReadStream[DatagramPacket] =
      |    wrapped.createDatagramSocket(arg0)
      |
      |  def createDnsClient(): DnsClient =
      |    wrapped.createDnsClient()
      |
      |  def createDnsClient(arg0: DnsClientOptions): DnsClient =
      |    wrapped.createDnsClient(arg0)
      |
      |  def createDnsClient(arg0: Int, arg1: String): DnsClient =
      |    wrapped.createDnsClient(arg0, arg1)
      |
      |  def createHttpClient(): HttpClient =
      |    wrapped.createHttpClient()
      |
      |  def createHttpClient(arg0: HttpClientOptions): HttpClient =
      |    wrapped.createHttpClient(arg0)
      |
      |  def createHttpServer(): HttpServer =
      |    wrapped.createHttpServer()
      |
      |  def createHttpServer(arg0: HttpServerOptions): HttpServer =
      |    wrapped.createHttpServer(arg0)
      |
      |  def createNetClient(): NetClient =
      |    wrapped.createNetClient()
      |
      |  def createNetClient(arg0: NetClientOptions): NetClient =
      |    wrapped.createNetClient(arg0)
      |
      |  def createNetServer(): NetServer =
      |    wrapped.createNetServer()
      |
      |  def createNetServer(arg0: NetServerOptions): NetServer =
      |    wrapped.createNetServer(arg0)
      |
      |  def createSharedWorkerExecutor(arg0: String): WorkerExecutor =
      |    wrapped.createSharedWorkerExecutor(arg0)
      |
      |  def createSharedWorkerExecutor(arg0: String, arg1: Int): WorkerExecutor =
      |    wrapped.createSharedWorkerExecutor(arg0, arg1)
      |
      |  def createSharedWorkerExecutor(arg0: String, arg1: Int, arg2: Long): WorkerExecutor =
      |    wrapped.createSharedWorkerExecutor(arg0, arg1, arg2)
      |
      |  def deployVerticle(arg0: Class[_ <: io.vertx.core.Verticle], arg1: DeploymentOptions): monix.eval.Task[String] =
      |    Task.handle[String] { handler =>
      |      wrapped.deployVerticle(arg0, arg1, handler)
      |    }
      |
      |  def deployVerticle(arg0: String, arg1: DeploymentOptions): monix.eval.Task[String] =
      |    Task.handle[String] { handler =>
      |      wrapped.deployVerticle(arg0, arg1, handler)
      |    }
      |
      |  def deployVerticle(arg0: String): monix.eval.Task[String] =
      |    Task.handle[String] { handler =>
      |      wrapped.deployVerticle(arg0, handler)
      |    }
      |
      |  def deployVerticle(arg0: Supplier[Verticle], arg1: DeploymentOptions): monix.eval.Task[String] =
      |    Task.handle[String] { handler =>
      |      wrapped.deployVerticle(arg0, arg1, handler)
      |    }
      |
      |  def deployVerticle(arg0: Verticle, arg1: DeploymentOptions): monix.eval.Task[String] =
      |    Task.handle[String] { handler =>
      |      wrapped.deployVerticle(arg0, arg1, handler)
      |    }
      |
      |  def deployVerticle(arg0: Verticle): monix.eval.Task[String] =
      |    Task.handle[String] { handler =>
      |      wrapped.deployVerticle(arg0, handler)
      |    }
      |
      |  def deploymentIDs(): Set[String] =
      |    wrapped.deploymentIDs()
      |
      |  def eventBus(): EventBus =
      |    wrapped.eventBus()
      |
      |  def exceptionHandler(): Handler[Throwable] =
      |    wrapped.exceptionHandler()
      |
      |  def exceptionHandler(arg0: Handler[Throwable]): JavaVertx =
      |    wrapped.exceptionHandler(arg0)
      |
      |  def executeBlocking[T](arg0: Handler[Future[T]], arg1: Boolean): monix.eval.Task[T] =
      |    Task.handle[T] { handler =>
      |      wrapped.executeBlocking(arg0, arg1, handler)
      |    }
      |
      |  def executeBlocking[T](arg0: Handler[Future[T]]): monix.eval.Task[T] =
      |    Task.handle[T] { handler =>
      |      wrapped.executeBlocking(arg0, handler)
      |    }
      |
      |  def fileSystem(): FileSystem =
      |    wrapped.fileSystem()
      |
      |  def getOrCreateContext(): Context =
      |    wrapped.getOrCreateContext()
      |
      |  def isClustered(): Boolean =
      |    wrapped.isClustered()
      |
      |  def isMetricsEnabled(): Boolean =
      |    wrapped.isMetricsEnabled()
      |
      |  def isNativeTransportEnabled(): Boolean =
      |    wrapped.isNativeTransportEnabled()
      |
      |  def nettyEventLoopGroup(): EventLoopGroup =
      |    wrapped.nettyEventLoopGroup()
      |
      |  def periodicStream(arg0: Long): ReadStream[Long] =
      |    wrapped.periodicStream(arg0)
      |
      |  def registerVerticleFactory(arg0: VerticleFactory): Unit =
      |    wrapped.registerVerticleFactory(arg0)
      |
      |  def runOnContext(arg0: Handler[Void]): Unit =
      |    wrapped.runOnContext(arg0)
      |
      |  def setPeriodic(arg0: Long, arg1: Handler[Long]): Long =
      |    wrapped.setPeriodic(arg0, arg1)
      |
      |  def setTimer(arg0: Long, arg1: Handler[Long]): Long =
      |    wrapped.setTimer(arg0, arg1)
      |
      |  def sharedData(): SharedData =
      |    wrapped.sharedData()
      |
      |  def timerStream(arg0: Long): ReadStream[Long] =
      |    wrapped.timerStream(arg0)
      |
      |  def undeploy(arg0: String): monix.eval.Task[Unit] =
      |    Task
      |      .handle[Void] { handler =>
      |        wrapped.undeploy(arg0, handler)
      |      }
      |      .map(_ => ())
      |
      |  def unregisterVerticleFactory(arg0: VerticleFactory): Unit =
      |    wrapped.unregisterVerticleFactory(arg0)
      |
      |  def verticleFactories(): Set[VerticleFactory] =
      |    wrapped.verticleFactories()
      |}
      |
      |object Vertx {
      |  def clusteredVertx(arg0: VertxOptions): monix.eval.Task[Vertx] =
      |    Task
      |      .handle[JavaVertx] { handler =>
      |        JavaVertx.clusteredVertx(arg0, handler)
      |      }
      |      .map(new Vertx(_))
      |
      |  def vertx(): Vertx =
      |    new Vertx(JavaVertx.vertx())
      |
      |  def vertx(arg0: VertxOptions): Vertx =
      |    new Vertx(JavaVertx.vertx(arg0))
      |
      |  def currentContext(): Context =
      |    JavaVertx.currentContext()
      |}
      |
      """.trim.stripMargin
  }

  it should "generate wrapper methods for simple Java API definitions" in {
    val cancelTimerMethod = classOf[Vertx].getDeclaredMethod("cancelTimer", classOf[Long])

    apiGen.method(classOf[Vertx], apiGen.toScalaType(classOf[Vertx]), cancelTimerMethod) shouldBe
      """
      |def cancelTimer(arg0: Long): Boolean =
      |  wrapped.cancelTimer(arg0)
      """.trim.stripMargin
  }

  it should "generate Task wrapper methods for Java API definitions with async handlers" in {
    val deployVerticleMethod = classOf[Vertx].getDeclaredMethod("deployVerticle", classOf[String], classOf[Handler[_]])

    apiGen.method(classOf[Vertx], apiGen.toScalaType(classOf[Vertx]), deployVerticleMethod) shouldBe
      """
      |def deployVerticle(arg0: String): monix.eval.Task[String] =
      |  Task.handle[String] { handler => wrapped.deployVerticle(arg0, handler) }
      """.trim.stripMargin
  }
}
