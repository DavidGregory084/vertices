package vertices

import org.scalatest._

import io.vertx.core.{ Handler, Vertx }

class ApiGenSpec extends FlatSpec with Matchers {
  val apiGen = new ApiGen(new java.io.File("."))

  "ApiGen" should "generate Scala wrappers for Java API definitions" in {
    apiGen.source(classOf[Vertx]) shouldBe s"""
      |package vertices
      |
      |import io.netty.channel.EventLoopGroup
      |import io.vertx.core.DeploymentOptions
      |import io.vertx.core.Future
      |import io.vertx.core.Handler
      |import io.vertx.core.TimeoutStream
      |import io.vertx.core.Verticle
      |import io.vertx.core.VertxOptions
      |import io.vertx.core.datagram.DatagramSocketOptions
      |import io.vertx.core.dns.DnsClientOptions
      |import io.vertx.core.http.HttpClientOptions
      |import io.vertx.core.http.HttpServerOptions
      |import io.vertx.core.net.NetClientOptions
      |import io.vertx.core.net.NetServerOptions
      |import io.vertx.core.spi.VerticleFactory
      |import io.vertx.core.{Vertx => JavaVertx}
      |import java.lang.Class
      |import java.lang.Long
      |import java.lang.String
      |import java.lang.Throwable
      |import java.lang.Void
      |import java.util.Set
      |import java.util.function.Supplier
      |import monix.eval.Task
      |import vertices.datagram.DatagramSocket
      |import vertices.dns.DnsClient
      |import vertices.eventbus.EventBus
      |import vertices.file.FileSystem
      |import vertices.http.HttpClient
      |import vertices.http.HttpServer
      |import vertices.net.NetClient
      |import vertices.net.NetServer
      |import vertices.shareddata.SharedData
      |
      |case class Vertx(val unwrap: JavaVertx) extends AnyVal {
      |
      |  def cancelTimer(arg0: Long): Boolean =
      |    unwrap.cancelTimer(arg0)
      |
      |  def close(): Task[Unit] =
      |    Task
      |      .handle[Void] { handler =>
      |        unwrap.close(handler)
      |      }
      |      .map(_ => ())
      |
      |  def createDatagramSocket(): DatagramSocket =
      |    DatagramSocket(unwrap.createDatagramSocket())
      |
      |  def createDatagramSocket(arg0: DatagramSocketOptions): DatagramSocket =
      |    DatagramSocket(unwrap.createDatagramSocket(arg0))
      |
      |  def createDnsClient(): DnsClient =
      |    DnsClient(unwrap.createDnsClient())
      |
      |  def createDnsClient(arg0: DnsClientOptions): DnsClient =
      |    DnsClient(unwrap.createDnsClient(arg0))
      |
      |  def createDnsClient(arg0: Int, arg1: String): DnsClient =
      |    DnsClient(unwrap.createDnsClient(arg0, arg1))
      |
      |  def createHttpClient(): HttpClient =
      |    HttpClient(unwrap.createHttpClient())
      |
      |  def createHttpClient(arg0: HttpClientOptions): HttpClient =
      |    HttpClient(unwrap.createHttpClient(arg0))
      |
      |  def createHttpServer(): HttpServer =
      |    HttpServer(unwrap.createHttpServer())
      |
      |  def createHttpServer(arg0: HttpServerOptions): HttpServer =
      |    HttpServer(unwrap.createHttpServer(arg0))
      |
      |  def createNetClient(): NetClient =
      |    NetClient(unwrap.createNetClient())
      |
      |  def createNetClient(arg0: NetClientOptions): NetClient =
      |    NetClient(unwrap.createNetClient(arg0))
      |
      |  def createNetServer(): NetServer =
      |    NetServer(unwrap.createNetServer())
      |
      |  def createNetServer(arg0: NetServerOptions): NetServer =
      |    NetServer(unwrap.createNetServer(arg0))
      |
      |  def createSharedWorkerExecutor(arg0: String): WorkerExecutor =
      |    WorkerExecutor(unwrap.createSharedWorkerExecutor(arg0))
      |
      |  def createSharedWorkerExecutor(arg0: String, arg1: Int): WorkerExecutor =
      |    WorkerExecutor(unwrap.createSharedWorkerExecutor(arg0, arg1))
      |
      |  def createSharedWorkerExecutor(arg0: String, arg1: Int, arg2: Long): WorkerExecutor =
      |    WorkerExecutor(unwrap.createSharedWorkerExecutor(arg0, arg1, arg2))
      |
      |  def deployVerticle(arg0: Class[_ <: io.vertx.core.Verticle], arg1: DeploymentOptions): Task[String] =
      |    Task.handle[String] { handler =>
      |      unwrap.deployVerticle(arg0, arg1, handler)
      |    }
      |
      |  def deployVerticle(arg0: String): Task[String] =
      |    Task.handle[String] { handler =>
      |      unwrap.deployVerticle(arg0, handler)
      |    }
      |
      |  def deployVerticle(arg0: String, arg1: DeploymentOptions): Task[String] =
      |    Task.handle[String] { handler =>
      |      unwrap.deployVerticle(arg0, arg1, handler)
      |    }
      |
      |  def deployVerticle(arg0: Supplier[Verticle], arg1: DeploymentOptions): Task[String] =
      |    Task.handle[String] { handler =>
      |      unwrap.deployVerticle(arg0, arg1, handler)
      |    }
      |
      |  def deployVerticle(arg0: Verticle): Task[String] =
      |    Task.handle[String] { handler =>
      |      unwrap.deployVerticle(arg0, handler)
      |    }
      |
      |  def deployVerticle(arg0: Verticle, arg1: DeploymentOptions): Task[String] =
      |    Task.handle[String] { handler =>
      |      unwrap.deployVerticle(arg0, arg1, handler)
      |    }
      |
      |  def deploymentIDs(): Set[String] =
      |    unwrap.deploymentIDs()
      |
      |  def eventBus(): EventBus =
      |    EventBus(unwrap.eventBus())
      |
      |  def exceptionHandler(): Handler[Throwable] =
      |    unwrap.exceptionHandler()
      |
      |  def exceptionHandler(arg0: Handler[Throwable]): Vertx =
      |    Vertx(unwrap.exceptionHandler(arg0))
      |
      |  def executeBlocking[T](arg0: Handler[Future[T]]): Task[T] =
      |    Task.handle[T] { handler =>
      |      unwrap.executeBlocking(arg0, handler)
      |    }
      |
      |  def executeBlocking[T](arg0: Handler[Future[T]], arg1: Boolean): Task[T] =
      |    Task.handle[T] { handler =>
      |      unwrap.executeBlocking(arg0, arg1, handler)
      |    }
      |
      |  def fileSystem(): FileSystem =
      |    FileSystem(unwrap.fileSystem())
      |
      |  def getOrCreateContext(): Context =
      |    Context(unwrap.getOrCreateContext())
      |
      |  def isClustered(): Boolean =
      |    unwrap.isClustered()
      |
      |  def isMetricsEnabled(): Boolean =
      |    unwrap.isMetricsEnabled()
      |
      |  def isNativeTransportEnabled(): Boolean =
      |    unwrap.isNativeTransportEnabled()
      |
      |  def nettyEventLoopGroup(): EventLoopGroup =
      |    unwrap.nettyEventLoopGroup()
      |
      |  def periodicStream(arg0: Long): TimeoutStream =
      |    unwrap.periodicStream(arg0)
      |
      |  def registerVerticleFactory(arg0: VerticleFactory): Unit =
      |    unwrap.registerVerticleFactory(arg0)
      |
      |  def runOnContext(arg0: Handler[Void]): Unit =
      |    unwrap.runOnContext(arg0)
      |
      |  def setPeriodic(arg0: Long, arg1: Handler[java.lang.Long]): Long =
      |    unwrap.setPeriodic(arg0, arg1)
      |
      |  def setTimer(arg0: Long, arg1: Handler[java.lang.Long]): Long =
      |    unwrap.setTimer(arg0, arg1)
      |
      |  def sharedData(): SharedData =
      |    SharedData(unwrap.sharedData())
      |
      |  def timerStream(arg0: Long): TimeoutStream =
      |    unwrap.timerStream(arg0)
      |
      |  def undeploy(arg0: String): Task[Unit] =
      |    Task
      |      .handle[Void] { handler =>
      |        unwrap.undeploy(arg0, handler)
      |      }
      |      .map(_ => ())
      |
      |  def unregisterVerticleFactory(arg0: VerticleFactory): Unit =
      |    unwrap.unregisterVerticleFactory(arg0)
      |
      |  def verticleFactories(): Set[VerticleFactory] =
      |    unwrap.verticleFactories()
      |}
      |
      |object Vertx {
      |
      |  def clusteredVertx(arg0: VertxOptions): Task[Vertx] =
      |    Task
      |      .handle[JavaVertx] { handler =>
      |        JavaVertx.clusteredVertx(arg0, handler)
      |      }
      |      .map(Vertx(_))
      |
      |  def vertx(): Vertx =
      |    Vertx(JavaVertx.vertx())
      |
      |  def vertx(arg0: VertxOptions): Vertx =
      |    Vertx(JavaVertx.vertx(arg0))
      |
      |  def currentContext(): Context =
      |    Context(JavaVertx.currentContext())
      |}
      |
      """.trim.stripMargin
  }

  it should "generate wrapper methods for simple Java API definitions" in {
    val cancelTimerMethod = classOf[Vertx].getDeclaredMethod("cancelTimer", classOf[Long])

    apiGen.method(classOf[Vertx], apiGen.toScalaType(classOf[Vertx]), cancelTimerMethod) shouldBe
      """
      |def cancelTimer(arg0: Long): Boolean =
      |  unwrap.cancelTimer(arg0)
      """.trim.stripMargin
  }

  it should "generate Task wrapper methods for Java API definitions with async handlers" in {
    val deployVerticleMethod = classOf[Vertx].getDeclaredMethod("deployVerticle", classOf[String], classOf[Handler[_]])

    apiGen.method(classOf[Vertx], apiGen.toScalaType(classOf[Vertx]), deployVerticleMethod) shouldBe
      """
      |def deployVerticle(arg0: String): Task[String] =
      |  Task.handle[String] { handler => unwrap.deployVerticle(arg0, handler) }
      """.trim.stripMargin
  }
}
