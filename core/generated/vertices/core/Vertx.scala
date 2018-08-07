package vertices
package core

import cats.implicits._
import io.netty.channel.EventLoopGroup
import io.vertx.core.AsyncResult
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.TimeoutStream
import io.vertx.core.Verticle
import io.vertx.core.VertxOptions
import io.vertx.core.datagram.DatagramSocketOptions
import io.vertx.core.datagram.{ DatagramSocket => JavaDatagramSocket }
import io.vertx.core.dns.DnsClientOptions
import io.vertx.core.dns.{ DnsClient => JavaDnsClient }
import io.vertx.core.eventbus.{ EventBus => JavaEventBus }
import io.vertx.core.file.{ FileSystem => JavaFileSystem }
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.{ HttpServer => JavaHttpServer }
import io.vertx.core.metrics.Measured
import io.vertx.core.net.NetClientOptions
import io.vertx.core.net.NetServerOptions
import io.vertx.core.net.{ NetClient => JavaNetClient }
import io.vertx.core.net.{ NetServer => JavaNetServer }
import io.vertx.core.shareddata.{ SharedData => JavaSharedData }
import io.vertx.core.spi.VerticleFactory
import io.vertx.core.{ Context => JavaContext }
import io.vertx.core.{ Vertx => JavaVertx }
import io.vertx.core.{ WorkerExecutor => JavaWorkerExecutor }
import java.lang.Long
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import java.util.Set
import java.util.concurrent.TimeUnit
import java.util.function.Supplier
import vertices.core.datagram.DatagramSocket
import vertices.core.dns.DnsClient
import vertices.core.eventbus.EventBus
import vertices.core.file.FileSystem
import vertices.core.http.HttpServer
import vertices.core.net.NetClient
import vertices.core.net.NetServer
import vertices.core.shareddata.SharedData
import monix.eval.Task

import scala.language.implicitConversions

case class Vertx(val unwrap: JavaVertx) extends AnyVal {
  // Standard method
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  // Wrapper method
  def getOrCreateContext(): Context =
    Context(unwrap.getOrCreateContext())

  // Wrapper method
  def createNetServer(options: NetServerOptions): NetServer =
    NetServer(unwrap.createNetServer(options))

  // Wrapper method
  def createNetServer(): NetServer =
    NetServer(unwrap.createNetServer())

  // Wrapper method
  def createNetClient(options: NetClientOptions): NetClient =
    NetClient(unwrap.createNetClient(options))

  // Wrapper method
  def createNetClient(): NetClient =
    NetClient(unwrap.createNetClient())

  // Wrapper method
  def createHttpServer(options: HttpServerOptions): HttpServer =
    HttpServer(unwrap.createHttpServer(options))

  // Wrapper method
  def createHttpServer(): HttpServer =
    HttpServer(unwrap.createHttpServer())

  // Standard method
  def createHttpClient(options: HttpClientOptions): HttpClient =
    unwrap.createHttpClient(options)

  // Standard method
  def createHttpClient(): HttpClient =
    unwrap.createHttpClient()

  // Wrapper method
  def createDatagramSocket(options: DatagramSocketOptions): DatagramSocket =
    DatagramSocket(unwrap.createDatagramSocket(options))

  // Wrapper method
  def createDatagramSocket(): DatagramSocket =
    DatagramSocket(unwrap.createDatagramSocket())

  // Wrapper method
  def fileSystem(): FileSystem =
    FileSystem(unwrap.fileSystem())

  // Wrapper method
  def eventBus(): EventBus =
    EventBus(unwrap.eventBus())

  // Wrapper method
  def createDnsClient(port: Int, host: String): DnsClient =
    DnsClient(unwrap.createDnsClient(port, host))

  // Wrapper method
  def createDnsClient(): DnsClient =
    DnsClient(unwrap.createDnsClient())

  // Wrapper method
  def createDnsClient(options: DnsClientOptions): DnsClient =
    DnsClient(unwrap.createDnsClient(options))

  // Wrapper method
  def sharedData(): SharedData =
    SharedData(unwrap.sharedData())

  // Standard method
  def setTimer(delay: Long, handler: Handler[Long]): Long =
    unwrap.setTimer(delay, handler.contramap((in: java.lang.Long) => in: Long))

  // Standard method
  def timerStream(delay: Long): TimeoutStream =
    unwrap.timerStream(delay)

  // Standard method
  def setPeriodic(delay: Long, handler: Handler[Long]): Long =
    unwrap.setPeriodic(delay, handler.contramap((in: java.lang.Long) => in: Long))

  // Standard method
  def periodicStream(delay: Long): TimeoutStream =
    unwrap.periodicStream(delay)

  // Standard method
  def cancelTimer(id: Long): Boolean =
    unwrap.cancelTimer(id)

  // Standard method
  def runOnContext(action: Handler[Void]): Unit =
    unwrap.runOnContext(action)

  // Async handler method
  def close(): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.close(completionHandler)
    }.map(_ => ())

  // Async handler method
  def deployVerticle(name: String): Task[String] =
    Task.handle[String] { completionHandler =>
      unwrap.deployVerticle(name, completionHandler)
    }

  // Async handler method
  def deployVerticle(name: String, options: DeploymentOptions): Task[String] =
    Task.handle[String] { completionHandler =>
      unwrap.deployVerticle(name, options, completionHandler)
    }

  // Async handler method
  def undeploy(deploymentID: String): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.undeploy(deploymentID, completionHandler)
    }.map(_ => ())

  // Standard method
  def deploymentIDs(): Set[String] =
    unwrap.deploymentIDs()

  // Standard method
  def isClustered(): Boolean =
    unwrap.isClustered()

  // Async handler method
  def executeBlocking[T](blockingCodeHandler: Handler[Future[T]], ordered: Boolean): Task[T] =
    Task.handle[T] { resultHandler =>
      unwrap.executeBlocking(blockingCodeHandler, ordered, resultHandler)
    }

  // Async handler method
  def executeBlocking[T](blockingCodeHandler: Handler[Future[T]]): Task[T] =
    Task.handle[T] { resultHandler =>
      unwrap.executeBlocking(blockingCodeHandler, resultHandler)
    }

  // Wrapper method
  def createSharedWorkerExecutor(name: String): WorkerExecutor =
    WorkerExecutor(unwrap.createSharedWorkerExecutor(name))

  // Wrapper method
  def createSharedWorkerExecutor(name: String, poolSize: Int): WorkerExecutor =
    WorkerExecutor(unwrap.createSharedWorkerExecutor(name, poolSize))

  // Wrapper method
  def createSharedWorkerExecutor(name: String, poolSize: Int, maxExecuteTime: Long): WorkerExecutor =
    WorkerExecutor(unwrap.createSharedWorkerExecutor(name, poolSize, maxExecuteTime))

  // Wrapper method
  def createSharedWorkerExecutor(name: String, poolSize: Int, maxExecuteTime: Long, maxExecuteTimeUnit: TimeUnit): WorkerExecutor =
    WorkerExecutor(unwrap.createSharedWorkerExecutor(name, poolSize, maxExecuteTime, maxExecuteTimeUnit))

  // Standard method
  def isNativeTransportEnabled(): Boolean =
    unwrap.isNativeTransportEnabled()

  // Wrapper method
  def exceptionHandler(handler: Handler[Throwable]): Vertx =
    Vertx(unwrap.exceptionHandler(handler))
}
object Vertx {
  implicit def javaVertxToVerticesVertx(j: JavaVertx): Vertx = apply(j)
  implicit def verticesVertxToJavaVertx(v: Vertx): JavaVertx = v.unwrap

  // Wrapper method
  def vertx(): Vertx =
    Vertx(JavaVertx.vertx())

  // Wrapper method
  def vertx(options: VertxOptions): Vertx =
    Vertx(JavaVertx.vertx(options))

  // Async handler method
  def clusteredVertx(options: VertxOptions): Task[Vertx] =
    Task.handle[JavaVertx] { resultHandler =>
      JavaVertx.clusteredVertx(options, resultHandler)
    }.map(out => Vertx(out))

  // Wrapper method
  def currentContext(): Context =
    Context(JavaVertx.currentContext())
}
