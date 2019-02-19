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

/**
 *  The entry point into the Vert.x Core API.
 *  <p>
 *  You use an instance of this class for functionality including:
 *  <ul>
 *    <li>Creating TCP clients and servers</li>
 *    <li>Creating HTTP clients and servers</li>
 *    <li>Creating DNS clients</li>
 *    <li>Creating Datagram sockets</li>
 *    <li>Setting and cancelling periodic and one-shot timers</li>
 *    <li>Getting a reference to the event bus API</li>
 *    <li>Getting a reference to the file system API</li>
 *    <li>Getting a reference to the shared data API</li>
 *    <li>Deploying and undeploying verticles</li>
 *  </ul>
 *  <p>
 *  Most functionality in Vert.x core is fairly low level.
 *  <p>
 *  To create an instance of this class you can use the static factory methods: {@link #vertx},
 *  {@link #vertx(io.vertx.core.VertxOptions)} and {@link #clusteredVertx(io.vertx.core.VertxOptions, Handler)}.
 *  <p>
 *  Please see the user manual for more detailed usage information.
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
case class Vertx(val unwrap: JavaVertx) extends AnyVal {
  /**
   *  Whether the metrics are enabled for this measured object
   * @implSpec  The default implementation returns {@code false}
   * @return {@code true} if metrics are enabled
   */
  def isMetricsEnabled(): Boolean =
    unwrap.isMetricsEnabled()

  /**
   *  Gets the current context, or creates one if there isn't one
   * @return The current context (created if didn't exist)
   */
  def getOrCreateContext(): Context =
    Context(unwrap.getOrCreateContext())

  /**
   *  Create a TCP/SSL server using the specified options
   * @param options  the options to use
   * @return the server
   */
  def createNetServer(options: NetServerOptions): NetServer =
    NetServer(unwrap.createNetServer(options))

  /**
   *  Create a TCP/SSL server using default options
   * @return the server
   */
  def createNetServer(): NetServer =
    NetServer(unwrap.createNetServer())

  /**
   *  Create a TCP/SSL client using the specified options
   * @param options  the options to use
   * @return the client
   */
  def createNetClient(options: NetClientOptions): NetClient =
    NetClient(unwrap.createNetClient(options))

  /**
   *  Create a TCP/SSL client using default options
   * @return the client
   */
  def createNetClient(): NetClient =
    NetClient(unwrap.createNetClient())

  /**
   *  Create an HTTP/HTTPS server using the specified options
   * @param options  the options to use
   * @return the server
   */
  def createHttpServer(options: HttpServerOptions): HttpServer =
    HttpServer(unwrap.createHttpServer(options))

  /**
   *  Create an HTTP/HTTPS server using default options
   * @return the server
   */
  def createHttpServer(): HttpServer =
    HttpServer(unwrap.createHttpServer())

  /**
   *  Create a HTTP/HTTPS client using the specified options
   * @param options  the options to use
   * @return the client
   */
  def createHttpClient(options: HttpClientOptions): HttpClient =
    unwrap.createHttpClient(options)

  /**
   *  Create a HTTP/HTTPS client using default options
   * @return the client
   */
  def createHttpClient(): HttpClient =
    unwrap.createHttpClient()

  /**
   *  Create a datagram socket using the specified options
   * @param options  the options to use
   * @return the socket
   */
  def createDatagramSocket(options: DatagramSocketOptions): DatagramSocket =
    DatagramSocket(unwrap.createDatagramSocket(options))

  /**
   *  Create a datagram socket using default options
   * @return the socket
   */
  def createDatagramSocket(): DatagramSocket =
    DatagramSocket(unwrap.createDatagramSocket())

  /**
   *  Get the filesystem object. There is a single instance of FileSystem per Vertx instance.
   * @return the filesystem object
   */
  def fileSystem(): FileSystem =
    FileSystem(unwrap.fileSystem())

  /**
   *  Get the event bus object. There is a single instance of EventBus per Vertx instance.
   * @return the event bus object
   */
  def eventBus(): EventBus =
    EventBus(unwrap.eventBus())

  /**
   *  Create a DNS client to connect to a DNS server at the specified host and port, with the default query timeout (5 seconds)
   *  <p/>
   * @param port  the port
   * @param host  the host
   * @return the DNS client
   */
  def createDnsClient(port: Int, host: String): DnsClient =
    DnsClient(unwrap.createDnsClient(port, host))

  /**
   *  Create a DNS client to connect to the DNS server configured by {@link VertxOptions#getAddressResolverOptions()}
   *  <p>
   *  DNS client takes the first configured resolver address provided by {@link DnsResolverProvider#nameServerAddresses()}}
   * @return the DNS client
   */
  def createDnsClient(): DnsClient =
    DnsClient(unwrap.createDnsClient())

  /**
   *  Create a DNS client to connect to a DNS server
   * @param options the client options
   * @return the DNS client
   */
  def createDnsClient(options: DnsClientOptions): DnsClient =
    DnsClient(unwrap.createDnsClient(options))

  /**
   *  Get the shared data object. There is a single instance of SharedData per Vertx instance.
   * @return the shared data object
   */
  def sharedData(): SharedData =
    SharedData(unwrap.sharedData())

  /**
   *  Set a one-shot timer to fire after {@code delay} milliseconds, at which point {@code handler} will be called with
   *  the id of the timer.
   * @param delay  the delay in milliseconds, after which the timer will fire
   * @param handler  the handler that will be called with the timer ID when the timer fires
   * @return the unique ID of the timer
   */
  def setTimer(delay: Long, handler: Handler[Long]): Long =
    unwrap.setTimer(delay, handler.contramap((in: java.lang.Long) => in: Long))

  /**
   *  Returns a one-shot timer as a read stream. The timer will be fired after {@code delay} milliseconds after
   *  the {@link ReadStream#handler} has been called.
   * @param delay  the delay in milliseconds, after which the timer will fire
   * @return the timer stream
   */
  def timerStream(delay: Long): TimeoutStream =
    unwrap.timerStream(delay)

  /**
   *  Set a periodic timer to fire every {@code delay} milliseconds, at which point {@code handler} will be called with
   *  the id of the timer.
   * @param delay  the delay in milliseconds, after which the timer will fire
   * @param handler  the handler that will be called with the timer ID when the timer fires
   * @return the unique ID of the timer
   */
  def setPeriodic(delay: Long, handler: Handler[Long]): Long =
    unwrap.setPeriodic(delay, handler.contramap((in: java.lang.Long) => in: Long))

  /**
   *  Returns a periodic timer as a read stream. The timer will be fired every {@code delay} milliseconds after
   *  the {@link ReadStream#handler} has been called.
   * @param delay  the delay in milliseconds, after which the timer will fire
   * @return the periodic stream
   */
  def periodicStream(delay: Long): TimeoutStream =
    unwrap.periodicStream(delay)

  /**
   *  Cancels the timer with the specified {@code id}.
   * @param id  The id of the timer to cancel
   * @return true if the timer was successfully cancelled, or false if the timer does not exist.
   */
  def cancelTimer(id: Long): Boolean =
    unwrap.cancelTimer(id)

  /**
   *  Puts the handler on the event queue for the current context so it will be run asynchronously ASAP after all
   *  preceeding events have been handled.
   * @param action - a handler representing the action to execute
   */
  def runOnContext(action: Handler[Void]): Unit =
    unwrap.runOnContext(action)

  /**
   *  Like {@link #close} but the completionHandler will be called when the close is complete
   * @param completionHandler  The handler will be notified when the close is complete.
   */
  def close(): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.close(completionHandler)
    }.map(_ => ())

  /**
   *  Like {@link #deployVerticle(String)} but the completionHandler will be notified when the deployment is complete.
   *  <p>
   *  If the deployment is successful the result will contain a String representing the unique deployment ID of the
   *  deployment.
   *  <p>
   *  This deployment ID can subsequently be used to undeploy the verticle.
   * @param name  The identifier
   * @param completionHandler  a handler which will be notified when the deployment is complete
   */
  def deployVerticle(name: String): Task[String] =
    Task.handle[String] { completionHandler =>
      unwrap.deployVerticle(name, completionHandler)
    }

  /**
   *  Like {@link #deployVerticle(String, Handler)} but {@link io.vertx.core.DeploymentOptions} are provided to configure the
   *  deployment.
   * @param name  the name
   * @param options  the deployment options.
   * @param completionHandler  a handler which will be notified when the deployment is complete
   */
  def deployVerticle(name: String, options: DeploymentOptions): Task[String] =
    Task.handle[String] { completionHandler =>
      unwrap.deployVerticle(name, options, completionHandler)
    }

  /**
   *  Like {@link #undeploy(String) } but the completionHandler will be notified when the undeployment is complete.
   * @param deploymentID  the deployment ID
   * @param completionHandler  a handler which will be notified when the undeployment is complete
   */
  def undeploy(deploymentID: String): Task[Unit] =
    Task.handle[Void] { completionHandler =>
      unwrap.undeploy(deploymentID, completionHandler)
    }.map(_ => ())

  /**
   *  Return a Set of deployment IDs for the currently deployed deploymentIDs.
   * @return Set of deployment IDs
   */
  def deploymentIDs(): Set[String] =
    unwrap.deploymentIDs()

  /**
   *  Is this Vert.x instance clustered?
   * @return true if clustered
   */
  def isClustered(): Boolean =
    unwrap.isClustered()

  /**
   *  Safely execute some blocking code.
   *  <p>
   *  Executes the blocking code in the handler {@code blockingCodeHandler} using a thread from the worker pool.
   *  <p>
   *  When the code is complete the handler {@code resultHandler} will be called with the result on the original context
   *  (e.g. on the original event loop of the caller).
   *  <p>
   *  A {@code Future} instance is passed into {@code blockingCodeHandler}. When the blocking code successfully completes,
   *  the handler should call the {@link Future#complete} or {@link Future#complete(Object)} method, or the {@link Future#fail}
   *  method if it failed.
   *  <p>
   *  In the {@code blockingCodeHandler} the current context remains the original context and therefore any task
   *  scheduled in the {@code blockingCodeHandler} will be executed on the this context and not on the worker thread.
   *  <p>
   *  The blocking code should block for a reasonable amount of time (i.e no more than a few seconds). Long blocking operations
   *  or polling operations (i.e a thread that spin in a loop polling events in a blocking fashion) are precluded.
   *  <p>
   *  When the blocking operation lasts more than the 10 seconds, a message will be printed on the console by the
   *  blocked thread checker.
   *  <p>
   *  Long blocking operations should use a dedicated thread managed by the application, which can interact with
   *  verticles using the event-bus or {@link Context#runOnContext(Handler)}
   * @param blockingCodeHandler  handler representing the blocking code to run
   * @param resultHandler  handler that will be called when the blocking code is complete
   * @param ordered  if true then if executeBlocking is called several times on the same context, the executions
   *                  for that context will be executed serially, not in parallel. if false then they will be no ordering
   *                  guarantees
   * @param <T> the type of the result
   */
  def executeBlocking[T](blockingCodeHandler: Handler[Future[T]], ordered: Boolean): Task[T] =
    Task.handle[T] { resultHandler =>
      unwrap.executeBlocking(blockingCodeHandler, ordered, resultHandler)
    }

  /**
   *  Like {@link #executeBlocking(Handler, boolean, Handler)} called with ordered = true.
   */
  def executeBlocking[T](blockingCodeHandler: Handler[Future[T]]): Task[T] =
    Task.handle[T] { resultHandler =>
      unwrap.executeBlocking(blockingCodeHandler, resultHandler)
    }

  /**
   *  Like {@link #createSharedWorkerExecutor(String, int)} but with the {@link VertxOptions#setWorkerPoolSize} {@code poolSize}.
   */
  def createSharedWorkerExecutor(name: String): WorkerExecutor =
    WorkerExecutor(unwrap.createSharedWorkerExecutor(name))

  /**
   *  Like {@link #createSharedWorkerExecutor(String, int, long)} but with the {@link VertxOptions#setMaxWorkerExecuteTime} {@code maxExecuteTime}.
   */
  def createSharedWorkerExecutor(name: String, poolSize: Int): WorkerExecutor =
    WorkerExecutor(unwrap.createSharedWorkerExecutor(name, poolSize))

  /**
   *  Like {@link #createSharedWorkerExecutor(String, int, long, TimeUnit)} but with the {@link TimeUnit#NANOSECONDS ns unit}.
   */
  def createSharedWorkerExecutor(name: String, poolSize: Int, maxExecuteTime: Long): WorkerExecutor =
    WorkerExecutor(unwrap.createSharedWorkerExecutor(name, poolSize, maxExecuteTime))

  /**
   *  Create a named worker executor, the executor should be closed when it's not needed anymore to release
   *  resources.<p/>
   * 
   *  This method can be called mutiple times with the same {@code name}. Executors with the same name will share
   *  the same worker pool. The worker pool size , max execute time and unit of max execute time are set when the worker pool is created and
   *  won't change after.<p>
   * 
   *  The worker pool is released when all the {@link WorkerExecutor} sharing the same name are closed.
   * @param name the name of the worker executor
   * @param poolSize the size of the pool
   * @param maxExecuteTime the value of max worker execute time
   * @param maxExecuteTimeUnit the value of unit of max worker execute time
   * @return the named worker executor
   */
  def createSharedWorkerExecutor(name: String, poolSize: Int, maxExecuteTime: Long, maxExecuteTimeUnit: TimeUnit): WorkerExecutor =
    WorkerExecutor(unwrap.createSharedWorkerExecutor(name, poolSize, maxExecuteTime, maxExecuteTimeUnit))

  /**
   * 
   * @return whether the native transport is used
   */
  def isNativeTransportEnabled(): Boolean =
    unwrap.isNativeTransportEnabled()

  /**
   *  Set a default exception handler for {@link Context}, set on {@link Context#exceptionHandler(Handler)} at creation.
   * @param handler the exception handler
   * @return a reference to this, so the API can be used fluently
   */
  def exceptionHandler(handler: Handler[Throwable]): Vertx =
    Vertx(unwrap.exceptionHandler(handler))
}
object Vertx {
  implicit def javaVertxToVerticesVertx(j: JavaVertx): Vertx = apply(j)
  implicit def verticesVertxToJavaVertx(v: Vertx): JavaVertx = v.unwrap

  /**
   *  Creates a non clustered instance using default options.
   * @return the instance
   */
  def vertx(): Vertx =
    Vertx(JavaVertx.vertx())

  /**
   *  Creates a non clustered instance using the specified options
   * @param options  the options to use
   * @return the instance
   */
  def vertx(options: VertxOptions): Vertx =
    Vertx(JavaVertx.vertx(options))

  /**
   *  Creates a clustered instance using the specified options.
   *  <p>
   *  The instance is created asynchronously and the resultHandler is called with the result when it is ready.
   * @param options  the options to use
   * @param resultHandler  the result handler that will receive the result
   */
  def clusteredVertx(options: VertxOptions): Task[Vertx] =
    Task.handle[JavaVertx] { resultHandler =>
      JavaVertx.clusteredVertx(options, resultHandler)
    }.map(out => Vertx(out))

  /**
   *  Gets the current context
   * @return The current context or null if no current context
   */
  def currentContext(): Context =
    Context(JavaVertx.currentContext())
}
