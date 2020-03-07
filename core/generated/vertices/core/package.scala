package vertices


import monix.eval.Task
import io.netty.channel.EventLoopGroup
import io.vertx.core.AsyncResult
import io.vertx.core.Closeable
import io.vertx.core.CompositeFuture
import io.vertx.core.Context
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.MultiMap
import io.vertx.core.Promise
import io.vertx.core.TimeoutStream
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.WorkerExecutor
import io.vertx.core.buffer.Buffer
import io.vertx.core.datagram.DatagramPacket
import io.vertx.core.datagram.DatagramSocket
import io.vertx.core.datagram.DatagramSocketOptions
import io.vertx.core.dns.DnsClient
import io.vertx.core.dns.DnsClientOptions
import io.vertx.core.dns.MxRecord
import io.vertx.core.dns.SrvRecord
import io.vertx.core.eventbus.DeliveryContext
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.eventbus.MessageProducer
import io.vertx.core.file.AsyncFile
import io.vertx.core.file.CopyOptions
import io.vertx.core.file.FileProps
import io.vertx.core.file.FileSystem
import io.vertx.core.file.FileSystemProps
import io.vertx.core.file.OpenOptions
import io.vertx.core.http.Cookie
import io.vertx.core.http.GoAway
import io.vertx.core.http.Http2Settings
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpClientRequest
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpConnection
import io.vertx.core.http.HttpFrame
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerFileUpload
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.http.HttpVersion
import io.vertx.core.http.RequestOptions
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.http.StreamPriority
import io.vertx.core.http.WebSocket
import io.vertx.core.http.WebSocketBase
import io.vertx.core.http.WebSocketConnectOptions
import io.vertx.core.http.WebSocketFrame
import io.vertx.core.http.WebsocketVersion
import io.vertx.core.json.JsonObject
import io.vertx.core.metrics.Measured
import io.vertx.core.net.NetClient
import io.vertx.core.net.NetClientOptions
import io.vertx.core.net.NetServer
import io.vertx.core.net.NetServerOptions
import io.vertx.core.net.NetSocket
import io.vertx.core.net.SocketAddress
import io.vertx.core.parsetools.JsonEvent
import io.vertx.core.parsetools.JsonParser
import io.vertx.core.parsetools.RecordParser
import io.vertx.core.shareddata.AsyncMap
import io.vertx.core.shareddata.Counter
import io.vertx.core.shareddata.LocalMap
import io.vertx.core.shareddata.Lock
import io.vertx.core.shareddata.SharedData
import io.vertx.core.spi.VerticleFactory
import io.vertx.core.streams.Pipe
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import java.lang.Boolean
import java.lang.CharSequence
import java.lang.Integer
import java.lang.Iterable
import java.lang.Long
import java.lang.Object
import java.lang.Short
import java.lang.String
import java.lang.Throwable
import java.lang.Void
import java.util.List
import java.util.Set
import java.util.concurrent.TimeUnit
import java.util.function.Function
import java.util.function.Supplier
import javax.net.ssl.SSLSession

package object core {
  implicit class VertxServerWebSocketOps(val target: ServerWebSocket) extends AnyVal {
    /**
     *  Same as {@link #end(T)} but with an {@code handler} called when the operation completes
     */
    def endL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(data, handler)
      }.map(_ => ())

    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[Buffer]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())

    /**
     *  Calls {@link #close(Handler)}
     */
    def endL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(handler)
      }.map(_ => ())

    /**
     *  Same as {@link #close()} but with an {@code handler} called when the operation completes
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())

    /**
     *  Same as {@link #close(short)} but with an {@code handler} called when the operation completes
     */
    def closeL(statusCode: Short): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(statusCode, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #close(short, String)} but with an {@code handler} called when the operation completes
     */
    def closeL(statusCode: Short, reason: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(statusCode, reason, handler)
      }.map(_ => ())


    def writeL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(data, handler)
      }.map(_ => ())


    def writeFrameL(frame: WebSocketFrame): Task[Unit] =
      Task.handle[Void] { handler =>
        target.writeFrame(frame, handler)
      }.map(_ => ())


    def writeFinalTextFrameL(text: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.writeFinalTextFrame(text, handler)
      }.map(_ => ())


    def writeFinalBinaryFrameL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.writeFinalBinaryFrame(data, handler)
      }.map(_ => ())


    def writeBinaryMessageL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.writeBinaryMessage(data, handler)
      }.map(_ => ())


    def writeTextMessageL(text: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.writeTextMessage(text, handler)
      }.map(_ => ())

    /**
     *  Set an asynchronous result for the handshake, upon completion of the specified {@code future}, the
     *  WebSocket will either be
     * 
     *  <ul>
     *    <li>accepted when the {@code future} succeeds with the HTTP {@literal 101} status code</li>
     *    <li>rejected when the {@code future} is succeeds with an HTTP status code different than {@literal 101}</li>
     *    <li>rejected when the {@code future} fails with the HTTP status code {@code 500}</li>
     *  </ul>
     * 
     *  The provided future might be completed by the WebSocket itself, e.g calling the {@link #close()} method
     *  will try to accept the handshake and close the WebSocket afterward. Thus it is advised to try to complete
     *  the {@code future} with {@link Promise#tryComplete} or {@link Promise#tryFail}.
     *  <p>
     *  This method should be called from the WebSocket handler to explicitly set an asynchronous handshake.
     *  <p>
     *  Calling this method will override the {@code future} completion handler.
     * @param future the future to complete with
     * @param handler the completion handler
     * @throws IllegalStateException when the WebSocket has already an asynchronous result
     */
    def setHandshakeL(future: Future[Integer]): Task[Int] =
      Task.handle[java.lang.Integer] { handler =>
        target.setHandshake(future, handler)
      }.map(out => out: Int)
  }


  implicit class VertxHttpServerOps(val target: HttpServer) extends AnyVal {
    /**
     *  Like {@link #listen(int, String)} but supplying a handler that will be called when the server is actually
     *  listening (or has failed).
     * @param port  the port to listen on
     * @param host  the host to listen on
     * @param listenHandler  the listen handler
     */
    def listenL(port: Int, host: String): Task[HttpServer] =
      Task.handle[HttpServer] { listenHandler =>
        target.listen(port, host, listenHandler)
      }

    /**
     *  Tell the server to start listening on the given address supplying
     *  a handler that will be called when the server is actually
     *  listening (or has failed).
     * @param address the address to listen on
     * @param listenHandler  the listen handler
     */
    def listenL(address: SocketAddress): Task[HttpServer] =
      Task.handle[HttpServer] { listenHandler =>
        target.listen(address, listenHandler)
      }

    /**
     *  Like {@link #listen(int)} but supplying a handler that will be called when the server is actually listening (or has failed).
     * @param port  the port to listen on
     * @param listenHandler  the listen handler
     */
    def listenL(port: Int): Task[HttpServer] =
      Task.handle[HttpServer] { listenHandler =>
        target.listen(port, listenHandler)
      }

    /**
     *  Like {@link #listen} but supplying a handler that will be called when the server is actually listening (or has failed).
     * @param listenHandler  the listen handler
     */
    def listenL(): Task[HttpServer] =
      Task.handle[HttpServer] { listenHandler =>
        target.listen(listenHandler)
      }

    /**
     *  Like {@link #close} but supplying a handler that will be called when the server is actually closed (or has failed).
     * @param completionHandler  the handler
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.close(completionHandler)
      }.map(_ => ())
  }


  implicit class VertxContextOps(val target: Context) extends AnyVal {
    /**
     *  Safely execute some blocking code.
     *  <p>
     *  Executes the blocking code in the handler {@code blockingCodeHandler} using a thread from the worker pool.
     *  <p>
     *  When the code is complete the handler {@code resultHandler} will be called with the result on the original context
     *  (e.g. on the original event loop of the caller).
     *  <p>
     *  A {@code Future} instance is passed into {@code blockingCodeHandler}. When the blocking code successfully completes,
     *  the handler should call the {@link Promise#complete} or {@link Promise#complete(Object)} method, or the {@link Promise#fail}
     *  method if it failed.
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
    def executeBlockingL[T](blockingCodeHandler: Handler[Promise[T]], ordered: Boolean): Task[T] =
      Task.handle[T] { resultHandler =>
        target.executeBlocking(blockingCodeHandler, ordered, resultHandler)
      }

    /**
     *  Invoke {@link #executeBlocking(Handler, boolean, Handler)} with order = true.
     * @param blockingCodeHandler  handler representing the blocking code to run
     * @param resultHandler  handler that will be called when the blocking code is complete
     * @param <T> the type of the result
     */
    def executeBlockingL[T](blockingCodeHandler: Handler[Promise[T]]): Task[T] =
      Task.handle[T] { resultHandler =>
        target.executeBlocking(blockingCodeHandler, resultHandler)
      }
  }


  implicit class VertxWorkerExecutorOps(val target: WorkerExecutor) extends AnyVal {
    /**
     *  Safely execute some blocking code.
     *  <p>
     *  Executes the blocking code in the handler {@code blockingCodeHandler} using a thread from the worker pool.
     *  <p>
     *  When the code is complete the handler {@code resultHandler} will be called with the result on the original context
     *  (i.e. on the original event loop of the caller).
     *  <p>
     *  A {@code Future} instance is passed into {@code blockingCodeHandler}. When the blocking code successfully completes,
     *  the handler should call the {@link Promise#complete} or {@link Promise#complete(Object)} method, or the {@link Promise#fail}
     *  method if it failed.
     *  <p>
     *  In the {@code blockingCodeHandler} the current context remains the original context and therefore any task
     *  scheduled in the {@code blockingCodeHandler} will be executed on the this context and not on the worker thread.
     * @param blockingCodeHandler  handler representing the blocking code to run
     * @param resultHandler  handler that will be called when the blocking code is complete
     * @param ordered  if true then if executeBlocking is called several times on the same context, the executions
     *                  for that context will be executed serially, not in parallel. if false then they will be no ordering
     *                  guarantees
     * @param <T> the type of the result
     */
    def executeBlockingL[T](blockingCodeHandler: Handler[Promise[T]], ordered: Boolean): Task[T] =
      Task.handle[T] { resultHandler =>
        target.executeBlocking(blockingCodeHandler, ordered, resultHandler)
      }

    /**
     *  Like {@link #executeBlocking(Handler, boolean, Handler)} called with ordered = true.
     */
    def executeBlockingL[T](blockingCodeHandler: Handler[Promise[T]]): Task[T] =
      Task.handle[T] { resultHandler =>
        target.executeBlocking(blockingCodeHandler, resultHandler)
      }
  }


  implicit class VertxEventBusOps(val target: EventBus) extends AnyVal {
    /**
     *  Sends a message and and specify a {@code replyHandler} that will be called if the recipient
     *  subsequently replies to the message.
     *  <p>
     *  The message will be delivered to at most one of the handlers registered to the address.
     * @param address  the address to send it to
     * @param message  the message body, may be {@code null}
     * @param replyHandler  reply handler will be called when any reply from the recipient is received
     * @return a reference to this, so the API can be used fluently
     */
    def requestL[T](address: String, message: Object): Task[Message[T]] =
      Task.handle[Message[T]] { replyHandler =>
        target.request(address, message, replyHandler)
      }

    /**
     *  Like {@link #request(String, Object, Handler)} but specifying {@code options} that can be used to configure the delivery.
     * @param address  the address to send it to
     * @param message  the message body, may be {@code null}
     * @param options  delivery options
     * @param replyHandler  reply handler will be called when any reply from the recipient is received
     * @return a reference to this, so the API can be used fluently
     */
    def requestL[T](address: String, message: Object, options: DeliveryOptions): Task[Message[T]] =
      Task.handle[Message[T]] { replyHandler =>
        target.request(address, message, options, replyHandler)
      }
  }


  implicit class VertxMessageProducerOps[T](val target: MessageProducer[T])  {
    /**
     *  Same as {@link #end(T)} but with an {@code handler} called when the operation completes
     */
    def endL(data: T): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(data, handler)
      }.map(_ => ())


    def writeL(data: T): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(data, handler)
      }.map(_ => ())

    /**
     *  Closes the producer, calls {@link #close(Handler)}
     */
    def endL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(handler)
      }.map(_ => ())

    /**
     *  Same as {@link #close()} but with an {@code handler} called when the operation completes
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())
  }


  implicit class VertxMessageOps[T](val target: Message[T])  {
    /**
     *  Reply to this message, specifying a {@code replyHandler} for the reply - i.e.
     *  to receive the reply to the reply.
     *  <p>
     *  If the message was sent specifying a reply handler, that handler will be
     *  called when it has received a reply. If the message wasn't sent specifying a receipt handler
     *  this method does nothing.
     * @param message  the message to reply with.
     * @param replyHandler  the reply handler for the reply.
     */
    def replyAndRequestL[R](message: Object): Task[Message[R]] =
      Task.handle[Message[R]] { replyHandler =>
        target.replyAndRequest(message, replyHandler)
      }

    /**
     *  Like {@link #replyAndRequest(Object, Handler)} but specifying {@code options} that can be used
     *  to configure the delivery.
     * @param message  the message body, may be {@code null}
     * @param options  delivery options
     * @param replyHandler  reply handler will be called when any reply from the recipient is received
     */
    def replyAndRequestL[R](message: Object, options: DeliveryOptions): Task[Message[R]] =
      Task.handle[Message[R]] { replyHandler =>
        target.replyAndRequest(message, options, replyHandler)
      }
  }


  implicit class VertxWebSocketOps(val target: WebSocket) extends AnyVal {
    /**
     *  Same as {@link #end(T)} but with an {@code handler} called when the operation completes
     */
    def endL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(data, handler)
      }.map(_ => ())

    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[Buffer]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())

    /**
     *  Calls {@link #close(Handler)}
     */
    def endL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(handler)
      }.map(_ => ())

    /**
     *  Same as {@link #close()} but with an {@code handler} called when the operation completes
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())

    /**
     *  Same as {@link #close(short)} but with an {@code handler} called when the operation completes
     */
    def closeL(statusCode: Short): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(statusCode, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #close(short, String)} but with an {@code handler} called when the operation completes
     */
    def closeL(statusCode: Short, reason: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(statusCode, reason, handler)
      }.map(_ => ())


    def writeL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(data, handler)
      }.map(_ => ())


    def writeFrameL(frame: WebSocketFrame): Task[Unit] =
      Task.handle[Void] { handler =>
        target.writeFrame(frame, handler)
      }.map(_ => ())


    def writeFinalTextFrameL(text: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.writeFinalTextFrame(text, handler)
      }.map(_ => ())


    def writeFinalBinaryFrameL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.writeFinalBinaryFrame(data, handler)
      }.map(_ => ())


    def writeBinaryMessageL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.writeBinaryMessage(data, handler)
      }.map(_ => ())


    def writeTextMessageL(text: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.writeTextMessage(text, handler)
      }.map(_ => ())
  }


  implicit class VertxFutureOps[T](val target: Future[T])  {
    /**
     *  Like {@link #onComplete(Handler)}.
     */
    def setHandlerL(): Task[T] =
      Task.handle[T] { handler =>
        target.setHandler(handler)
      }

    /**
     *  Add a handler to be notified of the result.
     *  <br/>
     * @param handler the handler that will be called with the result
     * @return a reference to this, so it can be used fluently
     */
    def onCompleteL(): Task[T] =
      Task.handle[T] { handler =>
        target.onComplete(handler)
      }
  }


  implicit class VertxDnsClientOps(val target: DnsClient) extends AnyVal {
    /**
     *  Try to lookup the A (ipv4) or AAAA (ipv6) record for the given name. The first found will be used.
     * @param name  the name to resolve
     * @param handler  the {@link io.vertx.core.Handler} to notify with the {@link io.vertx.core.AsyncResult}.
     *                  The handler will get notified with the resolved address if a record was found. If non was found it
     *                  will get notifed with {@code null}. If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently
     */
    def lookupL(name: String): Task[String] =
      Task.handle[String] { handler =>
        target.lookup(name, handler)
      }

    /**
     *  Try to lookup the A (ipv4) record for the given name. The first found will be used.
     * @param name  the name to resolve
     * @param handler  the {@link Handler} to notify with the {@link io.vertx.core.AsyncResult}.
     *                  The handler will get notified with the resolved {@link java.net.Inet4Address} if a record was found.
     *                  If non was found it will get notifed with {@code null}. If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently
     */
    def lookup4L(name: String): Task[String] =
      Task.handle[String] { handler =>
        target.lookup4(name, handler)
      }

    /**
     *  Try to lookup the AAAA (ipv6) record for the given name. The first found will be used.
     * @param name  the name to resolve
     * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
     *                  notified with the resolved {@link java.net.Inet6Address} if a record was found. If non was found
     *                  it will get notifed with {@code null}. If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently
     */
    def lookup6L(name: String): Task[String] =
      Task.handle[String] { handler =>
        target.lookup6(name, handler)
      }

    /**
     *  Try to resolve all A (ipv4) records for the given name.
     * @param name  the name to resolve
     * @param handler  the {@link io.vertx.core.Handler} to notify with the {@link io.vertx.core.AsyncResult}.
     *                  The handler will get notified with a {@link java.util.List} that contains all the resolved
     *                  {@link java.net.Inet4Address}es. If none was found an empty {@link java.util.List} will be used.
     *                  If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently
     */
    def resolveAL(name: String): Task[List[String]] =
      Task.handle[List[String]] { handler =>
        target.resolveA(name, handler)
      }

    /**
     *  Try to resolve all AAAA (ipv6) records for the given name.
     * @param name  the name to resolve
     * @param handler the {@link io.vertx.core.Handler} to notify with the {@link io.vertx.core.AsyncResult}.
     *                 The handler will get notified with a {@link java.util.List} that contains all the resolved
     *                 {@link java.net.Inet6Address}es. If none was found an empty {@link java.util.List} will be used.
     *                 If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently
     */
    def resolveAAAAL(name: String): Task[List[String]] =
      Task.handle[List[String]] { handler =>
        target.resolveAAAA(name, handler)
      }

    /**
     *  Try to resolve the CNAME record for the given name.
     * @param name  the name to resolve the CNAME for
     * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
     *                  notified with the resolved {@link String} if a record was found. If none was found it will
     *                  get notified with {@code null}. If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently.
     */
    def resolveCNAMEL(name: String): Task[List[String]] =
      Task.handle[List[String]] { handler =>
        target.resolveCNAME(name, handler)
      }

    /**
     *  Try to resolve the MX records for the given name.
     * @param name  the name for which the MX records should be resolved
     * @param handler  the {@link io.vertx.core.Handler} to notify with the {@link io.vertx.core.AsyncResult}.
     *                  The handler will get notified with a List that contains all resolved {@link MxRecord}s, sorted by
     *                  their {@link MxRecord#priority()}. If non was found it will get notified with an empty
     *                  {@link java.util.List}.  If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently.
     */
    def resolveMXL(name: String): Task[List[MxRecord]] =
      Task.handle[List[MxRecord]] { handler =>
        target.resolveMX(name, handler)
      }

    /**
     *  Try to resolve the TXT records for the given name.
     * @param name  the name for which the TXT records should be resolved
     * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
     *                  notified with a List that contains all resolved {@link String}s. If none was found it will
     *                  get notified with an empty {@link java.util.List}. If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently.
     */
    def resolveTXTL(name: String): Task[List[String]] =
      Task.handle[List[String]] { handler =>
        target.resolveTXT(name, handler)
      }

    /**
     *  Try to resolve the PTR record for the given name.
     * @param name  the name to resolve the PTR for
     * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
     *                  notified with the resolved {@link String} if a record was found. If none was found it will
     *                  get notified with {@code null}. If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently.
     */
    def resolvePTRL(name: String): Task[String] =
      Task.handle[String] { handler =>
        target.resolvePTR(name, handler)
      }

    /**
     *  Try to resolve the NS records for the given name.
     * @param name  the name for which the NS records should be resolved
     * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
     *                  notified with a List that contains all resolved {@link String}s. If none was found it will
     *                  get notified with an empty {@link java.util.List}.  If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently.
     */
    def resolveNSL(name: String): Task[List[String]] =
      Task.handle[List[String]] { handler =>
        target.resolveNS(name, handler)
      }

    /**
     *  Try to resolve the SRV records for the given name.
     * @param name  the name for which the SRV records should be resolved
     * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
     *                  notified with a List that contains all resolved {@link SrvRecord}s. If none was found it will
     *                  get notified with an empty {@link java.util.List}. If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently.
     */
    def resolveSRVL(name: String): Task[List[SrvRecord]] =
      Task.handle[List[SrvRecord]] { handler =>
        target.resolveSRV(name, handler)
      }

    /**
     *  Try to do a reverse lookup of an IP address. This is basically the same as doing trying to resolve a PTR record
     *  but allows you to just pass in the IP address and not a valid ptr query string.
     * @param ipaddress  the IP address to resolve the PTR for
     * @param handler  the {@link Handler} to notify with the {@link AsyncResult}. The handler will get
     *                  notified with the resolved {@link String} if a record was found. If none was found it will
     *                  get notified with {@code null}. If an error accours it will get failed.
     * @return a reference to this, so the API can be used fluently.
     */
    def reverseLookupL(ipaddress: String): Task[String] =
      Task.handle[String] { handler =>
        target.reverseLookup(ipaddress, handler)
      }
  }


  implicit class VertxHttpServerResponseOps(val target: HttpServerResponse) extends AnyVal {
    /**
     *  Same as {@link #end()} but with an {@code handler} called when the operation completes
     */
    def endL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(handler)
      }.map(_ => ())

    /**
     *  Same as {@link #write(Buffer)} but with an {@code handler} called when the operation completes
     */
    def writeL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(data, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #write(String, String)} but with an {@code handler} called when the operation completes
     */
    def writeL(chunk: String, enc: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(chunk, enc, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #write(String)} but with an {@code handler} called when the operation completes
     */
    def writeL(chunk: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(chunk, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #end(String)} but with an {@code handler} called when the operation completes
     */
    def endL(chunk: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(chunk, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #end(String, String)} but with an {@code handler} called when the operation completes
     */
    def endL(chunk: String, enc: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(chunk, enc, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #end(Buffer)} but with an {@code handler} called when the operation completes
     */
    def endL(chunk: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(chunk, handler)
      }.map(_ => ())

    /**
     *  Like {@link #sendFile(String)} but providing a handler which will be notified once the file has been completely
     *  written to the wire.
     * @param filename path to the file to serve
     * @param resultHandler  handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def sendFileL(filename: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.sendFile(filename, resultHandler)
      }.map(_ => ())

    /**
     *  Like {@link #sendFile(String, long)} but providing a handler which will be notified once the file has been completely
     *  written to the wire.
     * @param filename path to the file to serve
     * @param offset the offset to serve from
     * @param resultHandler  handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def sendFileL(filename: String, offset: Long): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.sendFile(filename, offset, resultHandler)
      }.map(_ => ())

    /**
     *  Like {@link #sendFile(String, long, long)} but providing a handler which will be notified once the file has been
     *  completely written to the wire.
     * @param filename path to the file to serve
     * @param offset the offset to serve from
     * @param length the length to serve to
     * @param resultHandler  handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def sendFileL(filename: String, offset: Long, length: Long): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.sendFile(filename, offset, length, resultHandler)
      }.map(_ => ())

    /**
     *  Like {@link #push(HttpMethod, String, String, MultiMap, Handler)} with no headers.
     */
    def pushL(method: HttpMethod, host: String, path: String): Task[HttpServerResponse] =
      Task.handle[HttpServerResponse] { handler =>
        target.push(method, host, path, handler)
      }

    /**
     *  Like {@link #push(HttpMethod, String, String, MultiMap, Handler)} with the host copied from the current request.
     */
    def pushL(method: HttpMethod, path: String, headers: MultiMap): Task[HttpServerResponse] =
      Task.handle[HttpServerResponse] { handler =>
        target.push(method, path, headers, handler)
      }

    /**
     *  Like {@link #push(HttpMethod, String, String, MultiMap, Handler)} with the host copied from the current request.
     */
    def pushL(method: HttpMethod, path: String): Task[HttpServerResponse] =
      Task.handle[HttpServerResponse] { handler =>
        target.push(method, path, handler)
      }

    /**
     *  Push a response to the client.<p/>
     * 
     *  The {@code handler} will be notified with a <i>success</i> when the push can be sent and with
     *  a <i>failure</i> when the client has disabled push or reset the push before it has been sent.<p/>
     * 
     *  The {@code handler} may be queued if the client has reduced the maximum number of streams the server can push
     *  concurrently.<p/>
     * 
     *  Push can be sent only for peer initiated streams and if the response is not ended.
     * @param method the method of the promised request
     * @param host the host of the promised request
     * @param path the path of the promised request
     * @param headers the headers of the promised request
     * @param handler the handler notified when the response can be written
     * @return a reference to this, so the API can be used fluently
     */
    def pushL(method: HttpMethod, host: String, path: String, headers: MultiMap): Task[HttpServerResponse] =
      Task.handle[HttpServerResponse] { handler =>
        target.push(method, host, path, headers, handler)
      }
  }


  implicit class VertxFileSystemOps(val target: FileSystem) extends AnyVal {
    /**
     *  Copy a file from the path {@code from} to path {@code to}, asynchronously.
     *  <p>
     *  The copy will fail if the destination already exists.
     * @param from  the path to copy from
     * @param to  the path to copy to
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def copyL(from: String, to: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.copy(from, to, handler)
      }.map(_ => ())

    /**
     *  Copy a file from the path {@code from} to path {@code to}, asynchronously.
     * @param from    the path to copy from
     * @param to      the path to copy to
     * @param options options describing how the file should be copied
     * @param handler the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def copyL(from: String, to: String, options: CopyOptions): Task[Unit] =
      Task.handle[Void] { handler =>
        target.copy(from, to, options, handler)
      }.map(_ => ())

    /**
     *  Copy a file from the path {@code from} to path {@code to}, asynchronously.
     *  <p>
     *  If {@code recursive} is {@code true} and {@code from} represents a directory, then the directory and its contents
     *  will be copied recursively to the destination {@code to}.
     *  <p>
     *  The copy will fail if the destination if the destination already exists.
     * @param from  the path to copy from
     * @param to  the path to copy to
     * @param recursive
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def copyRecursiveL(from: String, to: String, recursive: Boolean): Task[Unit] =
      Task.handle[Void] { handler =>
        target.copyRecursive(from, to, recursive, handler)
      }.map(_ => ())

    /**
     *  Move a file from the path {@code from} to path {@code to}, asynchronously.
     *  <p>
     *  The move will fail if the destination already exists.
     * @param from  the path to copy from
     * @param to  the path to copy to
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def moveL(from: String, to: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.move(from, to, handler)
      }.map(_ => ())

    /**
     *  Move a file from the path {@code from} to path {@code to}, asynchronously.
     * @param from    the path to copy from
     * @param to      the path to copy to
     * @param options options describing how the file should be copied
     * @param handler the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def moveL(from: String, to: String, options: CopyOptions): Task[Unit] =
      Task.handle[Void] { handler =>
        target.move(from, to, options, handler)
      }.map(_ => ())

    /**
     *  Truncate the file represented by {@code path} to length {@code len} in bytes, asynchronously.
     *  <p>
     *  The operation will fail if the file does not exist or {@code len} is less than {@code zero}.
     * @param path  the path to the file
     * @param len  the length to truncate it to
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def truncateL(path: String, len: Long): Task[Unit] =
      Task.handle[Void] { handler =>
        target.truncate(path, len, handler)
      }.map(_ => ())

    /**
     *  Change the permissions on the file represented by {@code path} to {@code perms}, asynchronously.
     *  <p>
     *  The permission String takes the form rwxr-x--- as
     *  specified <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
     * @param path  the path to the file
     * @param perms  the permissions string
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def chmodL(path: String, perms: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.chmod(path, perms, handler)
      }.map(_ => ())

    /**
     *  Change the permissions on the file represented by {@code path} to {@code perms}, asynchronously.<p>
     *  The permission String takes the form rwxr-x--- as
     *  specified in {<a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>}.
     *  <p>
     *  If the file is directory then all contents will also have their permissions changed recursively. Any directory permissions will
     *  be set to {@code dirPerms}, whilst any normal file permissions will be set to {@code perms}.
     * @param path  the path to the file
     * @param perms  the permissions string
     * @param dirPerms  the directory permissions
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def chmodRecursiveL(path: String, perms: String, dirPerms: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.chmodRecursive(path, perms, dirPerms, handler)
      }.map(_ => ())

    /**
     *  Change the ownership on the file represented by {@code path} to {@code user} and {code group}, asynchronously.
     * @param path  the path to the file
     * @param user  the user name, {@code null} will not change the user name
     * @param group  the user group, {@code null} will not change the user group name
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def chownL(path: String, user: String, group: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.chown(path, user, group, handler)
      }.map(_ => ())

    /**
     *  Obtain properties for the file represented by {@code path}, asynchronously.
     *  <p>
     *  If the file is a link, the link will be followed.
     * @param path  the path to the file
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def propsL(path: String): Task[FileProps] =
      Task.handle[FileProps] { handler =>
        target.props(path, handler)
      }

    /**
     *  Obtain properties for the link represented by {@code path}, asynchronously.
     *  <p>
     *  The link will not be followed.
     * @param path  the path to the file
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def lpropsL(path: String): Task[FileProps] =
      Task.handle[FileProps] { handler =>
        target.lprops(path, handler)
      }

    /**
     *  Create a hard link on the file system from {@code link} to {@code existing}, asynchronously.
     * @param link  the link
     * @param existing  the link destination
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def linkL(link: String, existing: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.link(link, existing, handler)
      }.map(_ => ())

    /**
     *  Create a symbolic link on the file system from {@code link} to {@code existing}, asynchronously.
     * @param link  the link
     * @param existing  the link destination
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def symlinkL(link: String, existing: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.symlink(link, existing, handler)
      }.map(_ => ())

    /**
     *  Unlinks the link on the file system represented by the path {@code link}, asynchronously.
     * @param link  the link
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def unlinkL(link: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.unlink(link, handler)
      }.map(_ => ())

    /**
     *  Returns the path representing the file that the symbolic link specified by {@code link} points to, asynchronously.
     * @param link  the link
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def readSymlinkL(link: String): Task[String] =
      Task.handle[String] { handler =>
        target.readSymlink(link, handler)
      }

    /**
     *  Deletes the file represented by the specified {@code path}, asynchronously.
     * @param path  path to the file
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def deleteL(path: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.delete(path, handler)
      }.map(_ => ())

    /**
     *  Deletes the file represented by the specified {@code path}, asynchronously.
     *  <p>
     *  If the path represents a directory and {@code recursive = true} then the directory and its contents will be
     *  deleted recursively.
     * @param path  path to the file
     * @param recursive  delete recursively?
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def deleteRecursiveL(path: String, recursive: Boolean): Task[Unit] =
      Task.handle[Void] { handler =>
        target.deleteRecursive(path, recursive, handler)
      }.map(_ => ())

    /**
     *  Create the directory represented by {@code path}, asynchronously.
     *  <p>
     *  The operation will fail if the directory already exists.
     * @param path  path to the file
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def mkdirL(path: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.mkdir(path, handler)
      }.map(_ => ())

    /**
     *  Create the directory represented by {@code path}, asynchronously.
     *  <p>
     *  The new directory will be created with permissions as specified by {@code perms}.
     *  <p>
     *  The permission String takes the form rwxr-x--- as specified
     *  in <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
     *  <p>
     *  The operation will fail if the directory already exists.
     * @param path  path to the file
     * @param perms  the permissions string
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def mkdirL(path: String, perms: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.mkdir(path, perms, handler)
      }.map(_ => ())

    /**
     *  Create the directory represented by {@code path} and any non existent parents, asynchronously.
     *  <p>
     *  The operation will fail if the {@code path} already exists but is not a directory.
     * @param path  path to the file
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def mkdirsL(path: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.mkdirs(path, handler)
      }.map(_ => ())

    /**
     *  Create the directory represented by {@code path} and any non existent parents, asynchronously.
     *  <p>
     *  The new directory will be created with permissions as specified by {@code perms}.
     *  <p>
     *  The permission String takes the form rwxr-x--- as specified
     *  in <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
     *  <p>
     *  The operation will fail if the {@code path} already exists but is not a directory.
     * @param path  path to the file
     * @param perms  the permissions string
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def mkdirsL(path: String, perms: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.mkdirs(path, perms, handler)
      }.map(_ => ())

    /**
     *  Read the contents of the directory specified by {@code path}, asynchronously.
     *  <p>
     *  The result is an array of String representing the paths of the files inside the directory.
     * @param path  path to the file
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def readDirL(path: String): Task[List[String]] =
      Task.handle[List[String]] { handler =>
        target.readDir(path, handler)
      }

    /**
     *  Read the contents of the directory specified by {@code path}, asynchronously.
     *  <p>
     *  The parameter {@code filter} is a regular expression. If {@code filter} is specified then only the paths that
     *  match  @{filter}will be returned.
     *  <p>
     *  The result is an array of String representing the paths of the files inside the directory.
     * @param path  path to the directory
     * @param filter  the filter expression
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def readDirL(path: String, filter: String): Task[List[String]] =
      Task.handle[List[String]] { handler =>
        target.readDir(path, filter, handler)
      }

    /**
     *  Reads the entire file as represented by the path {@code path} as a {@link Buffer}, asynchronously.
     *  <p>
     *  Do not use this method to read very large files or you risk running out of available RAM.
     * @param path  path to the file
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def readFileL(path: String): Task[Buffer] =
      Task.handle[Buffer] { handler =>
        target.readFile(path, handler)
      }

    /**
     *  Creates the file, and writes the specified {@code Buffer data} to the file represented by the path {@code path},
     *  asynchronously.
     * @param path  path to the file
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def writeFileL(path: String, data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.writeFile(path, data, handler)
      }.map(_ => ())

    /**
     *  Open the file represented by {@code path}, asynchronously.
     *  <p>
     *  The file is opened for both reading and writing. If the file does not already exist it will be created.
     * @param path  path to the file
     * @param options options describing how the file should be opened
     * @return a reference to this, so the API can be used fluently
     */
    def openL(path: String, options: OpenOptions): Task[AsyncFile] =
      Task.handle[AsyncFile] { handler =>
        target.open(path, options, handler)
      }

    /**
     *  Creates an empty file with the specified {@code path}, asynchronously.
     * @param path  path to the file
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def createFileL(path: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.createFile(path, handler)
      }.map(_ => ())

    /**
     *  Creates an empty file with the specified {@code path} and permissions {@code perms}, asynchronously.
     * @param path  path to the file
     * @param perms  the permissions string
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def createFileL(path: String, perms: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.createFile(path, perms, handler)
      }.map(_ => ())

    /**
     *  Determines whether the file as specified by the path {@code path} exists, asynchronously.
     * @param path  path to the file
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def existsL(path: String): Task[Boolean] =
      Task.handle[java.lang.Boolean] { handler =>
        target.exists(path, handler)
      }.map(out => out: Boolean)

    /**
     *  Returns properties of the file-system being used by the specified {@code path}, asynchronously.
     * @param path  path to anywhere on the filesystem
     * @param handler  the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def fsPropsL(path: String): Task[FileSystemProps] =
      Task.handle[FileSystemProps] { handler =>
        target.fsProps(path, handler)
      }

    /**
     *  Creates a new directory in the default temporary-file directory, using the given
     *  prefix to generate its name, asynchronously.
     * 
     *  <p>
     *  As with the {@code File.createTempFile} methods, this method is only
     *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
     *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
     *  </p>
     * @param prefix  the prefix string to be used in generating the directory's name;
     *                 may be {@code null}
     * @param handler the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def createTempDirectoryL(prefix: String): Task[String] =
      Task.handle[String] { handler =>
        target.createTempDirectory(prefix, handler)
      }

    /**
     *  Creates a new directory in the default temporary-file directory, using the given
     *  prefix to generate its name, asynchronously.
     *  <p>
     *  The new directory will be created with permissions as specified by {@code perms}.
     *  </p>
     *  The permission String takes the form rwxr-x--- as specified
     *  in <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
     * 
     *  <p>
     *  As with the {@code File.createTempFile} methods, this method is only
     *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
     *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
     *  </p>
     * @param prefix  the prefix string to be used in generating the directory's name;
     *                 may be {@code null}
     * @param perms   the permissions string
     * @param handler the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def createTempDirectoryL(prefix: String, perms: String): Task[String] =
      Task.handle[String] { handler =>
        target.createTempDirectory(prefix, perms, handler)
      }

    /**
     *  Creates a new directory in the directory provided by the path {@code path}, using the given
     *  prefix to generate its name, asynchronously.
     *  <p>
     *  The new directory will be created with permissions as specified by {@code perms}.
     *  </p>
     *  The permission String takes the form rwxr-x--- as specified
     *  in <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
     * 
     *  <p>
     *  As with the {@code File.createTempFile} methods, this method is only
     *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
     *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
     *  </p>
     * @param dir     the path to directory in which to create the directory
     * @param prefix  the prefix string to be used in generating the directory's name;
     *                 may be {@code null}
     * @param perms   the permissions string
     * @param handler the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def createTempDirectoryL(dir: String, prefix: String, perms: String): Task[String] =
      Task.handle[String] { handler =>
        target.createTempDirectory(dir, prefix, perms, handler)
      }

    /**
     *  Creates a new file in the default temporary-file directory, using the given
     *  prefix and suffix to generate its name, asynchronously.
     * 
     *  <p>
     *  As with the {@code File.createTempFile} methods, this method is only
     *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
     *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
     *  </p>
     * @param prefix  the prefix string to be used in generating the directory's name;
     *                 may be {@code null}
     * @param suffix  the suffix string to be used in generating the file's name;
     *                 may be {@code null}, in which case "{@code .tmp}" is used
     * @param handler the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def createTempFileL(prefix: String, suffix: String): Task[String] =
      Task.handle[String] { handler =>
        target.createTempFile(prefix, suffix, handler)
      }

    /**
     *  Creates a new file in the directory provided by the path {@code dir}, using the given
     *  prefix and suffix to generate its name, asynchronously.
     * 
     *  <p>
     *  As with the {@code File.createTempFile} methods, this method is only
     *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
     *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
     *  </p>
     * @param prefix  the prefix string to be used in generating the directory's name;
     *                 may be {@code null}
     * @param suffix  the suffix string to be used in generating the file's name;
     *                 may be {@code null}, in which case "{@code .tmp}" is used
     * @param handler the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def createTempFileL(prefix: String, suffix: String, perms: String): Task[String] =
      Task.handle[String] { handler =>
        target.createTempFile(prefix, suffix, perms, handler)
      }

    /**
     *  Creates a new file in the directory provided by the path {@code dir}, using the given
     *  prefix and suffix to generate its name, asynchronously.
     *  <p>
     *  The new directory will be created with permissions as specified by {@code perms}.
     *  </p>
     *  The permission String takes the form rwxr-x--- as specified
     *  in <a href="http://download.oracle.com/javase/7/docs/api/java/nio/file/attribute/PosixFilePermissions.html">here</a>.
     * 
     *  <p>
     *  As with the {@code File.createTempFile} methods, this method is only
     *  part of a temporary-file facility.A {@link Runtime#addShutdownHook shutdown-hook},
     *  or the {@link java.io.File#deleteOnExit} mechanism may be used to delete the directory automatically.
     *  </p>
     * @param dir     the path to directory in which to create the directory
     * @param prefix  the prefix string to be used in generating the directory's name;
     *                 may be {@code null}
     * @param suffix  the suffix string to be used in generating the file's name;
     *                 may be {@code null}, in which case "{@code .tmp}" is used
     * @param perms   the permissions string
     * @param handler the handler that will be called on completion
     * @return a reference to this, so the API can be used fluently
     */
    def createTempFileL(dir: String, prefix: String, suffix: String, perms: String): Task[String] =
      Task.handle[String] { handler =>
        target.createTempFile(dir, prefix, suffix, perms, handler)
      }
  }


  implicit class VertxMessageConsumerOps[T](val target: MessageConsumer[T])  {
    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[Message[T]]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())

    /**
     *  Optional method which can be called to indicate when the registration has been propagated across the cluster.
     * @param completionHandler the completion handler
     */
    def completionHandlerL(): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.completionHandler(completionHandler)
      }.map(_ => ())

    /**
     *  Unregisters the handler which created this registration
     * @param completionHandler the handler called when the unregister is done. For example in a cluster when all nodes of the
     *  event bus have been unregistered.
     */
    def unregisterL(): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.unregister(completionHandler)
      }.map(_ => ())
  }


  implicit class VertxHttpClientOps(val target: HttpClient) extends AnyVal {
    /**
     *  Connect a WebSocket to the specified port, host and relative request URI
     * @param port  the port
     * @param host  the host
     * @param requestURI  the relative URI
     * @param handler  handler that will be called with the WebSocket when connected
     */
    def webSocketL(port: Int, host: String, requestURI: String): Task[WebSocket] =
      Task.handle[WebSocket] { handler =>
        target.webSocket(port, host, requestURI, handler)
      }

    /**
     *  Connect a WebSocket to the host and relative request URI and default port
     * @param host  the host
     * @param requestURI  the relative URI
     * @param handler  handler that will be called with the WebSocket when connected
     */
    def webSocketL(host: String, requestURI: String): Task[WebSocket] =
      Task.handle[WebSocket] { handler =>
        target.webSocket(host, requestURI, handler)
      }

    /**
     *  Connect a WebSocket at the relative request URI using the default host and port
     * @param requestURI  the relative URI
     * @param handler  handler that will be called with the WebSocket when connected
     */
    def webSocketL(requestURI: String): Task[WebSocket] =
      Task.handle[WebSocket] { handler =>
        target.webSocket(requestURI, handler)
      }

    /**
     *  Connect a WebSocket with the specified options.
     * @param options  the request options
     */
    def webSocketL(options: WebSocketConnectOptions): Task[WebSocket] =
      Task.handle[WebSocket] { handler =>
        target.webSocket(options, handler)
      }

    /**
     *  Connect a WebSocket with the specified absolute url, with the specified headers, using
     *  the specified version of WebSockets, and the specified WebSocket sub protocols.
     * @param url            the absolute url
     * @param headers        the headers
     * @param version        the WebSocket version
     * @param subProtocols   the subprotocols to use
     * @param handler handler that will be called if WebSocket connection fails
     */
    def webSocketAbsL(url: String, headers: MultiMap, version: WebsocketVersion, subProtocols: List[String]): Task[WebSocket] =
      Task.handle[WebSocket] { handler =>
        target.webSocketAbs(url, headers, version, subProtocols, handler)
      }
  }


  implicit class VertxNetSocketOps(val target: NetSocket) extends AnyVal {
    /**
     *  Same as {@link #end(T)} but with an {@code handler} called when the operation completes
     */
    def endL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(data, handler)
      }.map(_ => ())

    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[Buffer]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #write(String)} but with an {@code handler} called when the operation completes
     */
    def writeL(str: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(str, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #write(String, String)} but with an {@code handler} called when the operation completes
     */
    def writeL(str: String, enc: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(str, enc, handler)
      }.map(_ => ())

    /**
     *  Like {@link #write(Object)} but with an {@code handler} called when the message has been written
     *  or failed to be written.
     */
    def writeL(message: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(message, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #sendFile(String)} but also takes a handler that will be called when the send has completed or
     *  a failure has occurred
     * @param filename  file name of the file to send
     * @param resultHandler  handler
     * @return a reference to this, so the API can be used fluently
     */
    def sendFileL(filename: String): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.sendFile(filename, resultHandler)
      }.map(_ => ())

    /**
     *  Same as {@link #sendFile(String, long)} but also takes a handler that will be called when the send has completed or
     *  a failure has occurred
     * @param filename  file name of the file to send
     * @param offset offset
     * @param resultHandler  handler
     * @return a reference to this, so the API can be used fluently
     */
    def sendFileL(filename: String, offset: Long): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.sendFile(filename, offset, resultHandler)
      }.map(_ => ())

    /**
     *  Same as {@link #sendFile(String, long, long)} but also takes a handler that will be called when the send has completed or
     *  a failure has occurred
     * @param filename  file name of the file to send
     * @param offset offset
     * @param length length
     * @param resultHandler  handler
     * @return a reference to this, so the API can be used fluently
     */
    def sendFileL(filename: String, offset: Long, length: Long): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.sendFile(filename, offset, length, resultHandler)
      }.map(_ => ())

    /**
     *  Calls {@link #end(Handler)}
     */
    def endL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(handler)
      }.map(_ => ())

    /**
     *  Close the NetSocket and notify the {@code handler} when the operation completes.
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())
  }


  implicit class VertxAsyncFileOps(val target: AsyncFile) extends AnyVal {
    /**
     *  Same as {@link #end(T)} but with an {@code handler} called when the operation completes
     */
    def endL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(data, handler)
      }.map(_ => ())

    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[Buffer]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #write(Buffer)} but with an {@code handler} called when the operation completes
     */
    def writeL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(data, handler)
      }.map(_ => ())

    /**
     *  Close the file, see {@link #close(Handler)}.
     */
    def endL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(handler)
      }.map(_ => ())

    /**
     *  Close the file. The actual close happens asynchronously.
     *  The handler will be called when the close is complete, or an error occurs.
     * @param handler  the handler
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())

    /**
     *  Write a {@link io.vertx.core.buffer.Buffer} to the file at position {@code position} in the file, asynchronously.
     *  <p>
     *  If {@code position} lies outside of the current size
     *  of the file, the file will be enlarged to encompass it.
     *  <p>
     *  When multiple writes are invoked on the same file
     *  there are no guarantees as to order in which those writes actually occur
     *  <p>
     *  The handler will be called when the write is complete, or if an error occurs.
     * @param buffer  the buffer to write
     * @param position  the position in the file to write it at
     * @param handler  the handler to call when the write is complete
     * @return a reference to this, so the API can be used fluently
     */
    def writeL(buffer: Buffer, position: Long): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(buffer, position, handler)
      }.map(_ => ())

    /**
     *  Reads {@code length} bytes of data from the file at position {@code position} in the file, asynchronously.
     *  <p>
     *  The read data will be written into the specified {@code Buffer buffer} at position {@code offset}.
     *  <p>
     *  If data is read past the end of the file then zero bytes will be read.<p>
     *  When multiple reads are invoked on the same file there are no guarantees as to order in which those reads actually occur.
     *  <p>
     *  The handler will be called when the close is complete, or if an error occurs.
     * @param buffer  the buffer to read into
     * @param offset  the offset into the buffer where the data will be read
     * @param position  the position in the file where to start reading
     * @param length  the number of bytes to read
     * @param handler  the handler to call when the write is complete
     * @return a reference to this, so the API can be used fluently
     */
    def readL(buffer: Buffer, offset: Int, position: Long, length: Int): Task[Buffer] =
      Task.handle[Buffer] { handler =>
        target.read(buffer, offset, position, length, handler)
      }

    /**
     *  Same as {@link #flush} but the handler will be called when the flush is complete or if an error occurs
     */
    def flushL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.flush(handler)
      }.map(_ => ())
  }


  implicit class VertxHttpServerRequestOps(val target: HttpServerRequest) extends AnyVal {
    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[Buffer]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())
  }


  implicit class VertxDatagramSocketOps(val target: DatagramSocket) extends AnyVal {
    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[DatagramPacket]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())

    /**
     *  Write the given {@link io.vertx.core.buffer.Buffer} to the {@link io.vertx.core.net.SocketAddress}.
     *  The {@link io.vertx.core.Handler} will be notified once the write completes.
     * @param packet  the {@link io.vertx.core.buffer.Buffer} to write
     * @param port  the host port of the remote peer
     * @param host  the host address of the remote peer
     * @param handler  the {@link io.vertx.core.Handler} to notify once the write completes.
     * @return a reference to this, so the API can be used fluently
     */
    def sendL(packet: Buffer, port: Int, host: String): Task[DatagramSocket] =
      Task.handle[DatagramSocket] { handler =>
        target.send(packet, port, host, handler)
      }

    /**
     *  Write the given {@link String} to the {@link io.vertx.core.net.SocketAddress} using UTF8 encoding.
     *  The {@link Handler} will be notified once the write completes.
     * @param str   the {@link String} to write
     * @param port  the host port of the remote peer
     * @param host  the host address of the remote peer
     * @param handler  the {@link io.vertx.core.Handler} to notify once the write completes.
     * @return a reference to this, so the API can be used fluently
     */
    def sendL(str: String, port: Int, host: String): Task[DatagramSocket] =
      Task.handle[DatagramSocket] { handler =>
        target.send(str, port, host, handler)
      }

    /**
     *  Write the given {@link String} to the {@link io.vertx.core.net.SocketAddress} using the given encoding.
     *  The {@link Handler} will be notified once the write completes.
     * @param str  the {@link String} to write
     * @param enc  the charset used for encoding
     * @param port  the host port of the remote peer
     * @param host  the host address of the remote peer
     * @param handler  the {@link io.vertx.core.Handler} to notify once the write completes.
     * @return a reference to this, so the API can be used fluently
     */
    def sendL(str: String, enc: String, port: Int, host: String): Task[DatagramSocket] =
      Task.handle[DatagramSocket] { handler =>
        target.send(str, enc, port, host, handler)
      }

    /**
     *  Closes the {@link io.vertx.core.datagram.DatagramSocket} implementation asynchronous
     *  and notifies the handler once done.
     * @param handler  the handler to notify once complete
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.close(handler)
      }.map(_ => ())

    /**
     *  Joins a multicast group and listens for packets send to it.
     *  The {@link Handler} is notified once the operation completes.
     * @param multicastAddress  the address of the multicast group to join
     * @param  handler  then handler to notify once the operation completes
     * @return a reference to this, so the API can be used fluently
     */
    def listenMulticastGroupL(multicastAddress: String): Task[DatagramSocket] =
      Task.handle[DatagramSocket] { handler =>
        target.listenMulticastGroup(multicastAddress, handler)
      }

    /**
     *  Joins a multicast group and listens for packets send to it on the given network interface.
     *  The {@link Handler} is notified once the operation completes.
     * @param  multicastAddress  the address of the multicast group to join
     * @param  networkInterface  the network interface on which to listen for packets.
     * @param  source  the address of the source for which we will listen for multicast packets
     * @param  handler  then handler to notify once the operation completes
     * @return a reference to this, so the API can be used fluently
     */
    def listenMulticastGroupL(multicastAddress: String, networkInterface: String, source: String): Task[DatagramSocket] =
      Task.handle[DatagramSocket] { handler =>
        target.listenMulticastGroup(multicastAddress, networkInterface, source, handler)
      }

    /**
     *  Leaves a multicast group and stops listening for packets send to it.
     *  The {@link Handler} is notified once the operation completes.
     * @param multicastAddress  the address of the multicast group to leave
     * @param handler  then handler to notify once the operation completes
     * @return a reference to this, so the API can be used fluently
     */
    def unlistenMulticastGroupL(multicastAddress: String): Task[DatagramSocket] =
      Task.handle[DatagramSocket] { handler =>
        target.unlistenMulticastGroup(multicastAddress, handler)
      }

    /**
     *  Leaves a multicast group and stops listening for packets send to it on the given network interface.
     *  The {@link Handler} is notified once the operation completes.
     * @param  multicastAddress  the address of the multicast group to join
     * @param  networkInterface  the network interface on which to listen for packets.
     * @param  source  the address of the source for which we will listen for multicast packets
     * @param  handler the handler to notify once the operation completes
     * @return  a reference to this, so the API can be used fluently
     */
    def unlistenMulticastGroupL(multicastAddress: String, networkInterface: String, source: String): Task[DatagramSocket] =
      Task.handle[DatagramSocket] { handler =>
        target.unlistenMulticastGroup(multicastAddress, networkInterface, source, handler)
      }

    /**
     *  Block the given address for the given multicast address and notifies the {@link Handler} once
     *  the operation completes.
     * @param multicastAddress  the address for which you want to block the source address
     * @param sourceToBlock  the source address which should be blocked. You will not receive an multicast packets
     *                        for it anymore.
     * @param handler  the handler to notify once the operation completes
     * @return  a reference to this, so the API can be used fluently
     */
    def blockMulticastGroupL(multicastAddress: String, sourceToBlock: String): Task[DatagramSocket] =
      Task.handle[DatagramSocket] { handler =>
        target.blockMulticastGroup(multicastAddress, sourceToBlock, handler)
      }

    /**
     *  Block the given address for the given multicast address on the given network interface and notifies
     *  the {@link Handler} once the operation completes.
     * @param  multicastAddress  the address for which you want to block the source address
     * @param  networkInterface  the network interface on which the blocking should occur.
     * @param  sourceToBlock  the source address which should be blocked. You will not receive an multicast packets
     *                         for it anymore.
     * @param  handler  the handler to notify once the operation completes
     * @return  a reference to this, so the API can be used fluently
     */
    def blockMulticastGroupL(multicastAddress: String, networkInterface: String, sourceToBlock: String): Task[DatagramSocket] =
      Task.handle[DatagramSocket] { handler =>
        target.blockMulticastGroup(multicastAddress, networkInterface, sourceToBlock, handler)
      }

    /**
     *  Start listening on the given port and host. The handler will be called when the socket is listening.
     * @param port  the port to listen on
     * @param host  the host to listen on
     * @param handler  the handler will be called when listening
     * @return  a reference to this, so the API can be used fluently
     */
    def listenL(port: Int, host: String): Task[DatagramSocket] =
      Task.handle[DatagramSocket] { handler =>
        target.listen(port, host, handler)
      }
  }


  implicit class VertxJsonParserOps(val target: JsonParser) extends AnyVal {
    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[JsonEvent]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())
  }


  implicit class VertxSharedDataOps(val target: SharedData) extends AnyVal {
    /**
     *  Get the cluster wide map with the specified name. The map is accessible to all nodes in the cluster and data
     *  put into the map from any node is visible to to any other node.
     * @param name  the name of the map
     * @param resultHandler  the map will be returned asynchronously in this handler
     * @throws IllegalStateException if the parent {@link io.vertx.core.Vertx} instance is not clustered
     */
    def getClusterWideMapL[K, V](name: String): Task[AsyncMap[K,V]] =
      Task.handle[AsyncMap[K,V]] { resultHandler =>
        target.getClusterWideMap(name, resultHandler)
      }

    /**
     *  Get the {@link AsyncMap} with the specified name. When clustered, the map is accessible to all nodes in the cluster
     *  and data put into the map from any node is visible to to any other node.
     *  <p>
     *    <strong>WARNING</strong>: In clustered mode, asynchronous shared maps rely on distributed data structures provided by the cluster manager.
     *    Beware that the latency relative to asynchronous shared maps operations can be much higher in clustered than in local mode.
     *  </p>
     * @param name the name of the map
     * @param resultHandler the map will be returned asynchronously in this handler
     */
    def getAsyncMapL[K, V](name: String): Task[AsyncMap[K,V]] =
      Task.handle[AsyncMap[K,V]] { resultHandler =>
        target.getAsyncMap(name, resultHandler)
      }

    /**
     *  Get the {@link AsyncMap} with the specified name.
     *  <p>
     *  When clustered, the map is <b>NOT</b> accessible to all nodes in the cluster.
     *  Only the instance which created the map can put and retrieve data from this map.
     * @param name the name of the map
     * @param resultHandler the map will be returned asynchronously in this handler
     */
    def getLocalAsyncMapL[K, V](name: String): Task[AsyncMap[K,V]] =
      Task.handle[AsyncMap[K,V]] { resultHandler =>
        target.getLocalAsyncMap(name, resultHandler)
      }

    /**
     *  Get an asynchronous lock with the specified name. The lock will be passed to the handler when it is available.
     *  <p>
     *    In general lock acquision is unordered, so that sequential attempts to acquire a lock,
     *    even from a single thread, can happen in non-sequential order.
     *  </p>
     * @param name  the name of the lock
     * @param resultHandler  the handler
     */
    def getLockL(name: String): Task[Lock] =
      Task.handle[Lock] { resultHandler =>
        target.getLock(name, resultHandler)
      }

    /**
     *  Like {@link #getLock(String, Handler)} but specifying a timeout. If the lock is not obtained within the timeout
     *  a failure will be sent to the handler.
     *  <p>
     *    In general lock acquision is unordered, so that sequential attempts to acquire a lock,
     *    even from a single thread, can happen in non-sequential order.
     *  </p>
     * @param name  the name of the lock
     * @param timeout  the timeout in ms
     * @param resultHandler  the handler
     */
    def getLockWithTimeoutL(name: String, timeout: Long): Task[Lock] =
      Task.handle[Lock] { resultHandler =>
        target.getLockWithTimeout(name, timeout, resultHandler)
      }

    /**
     *  Get an asynchronous local lock with the specified name. The lock will be passed to the handler when it is available.
     *  <p>
     *    In general lock acquision is unordered, so that sequential attempts to acquire a lock,
     *    even from a single thread, can happen in non-sequential order.
     *  </p>
     * @param name  the name of the lock
     * @param resultHandler  the handler
     */
    def getLocalLockL(name: String): Task[Lock] =
      Task.handle[Lock] { resultHandler =>
        target.getLocalLock(name, resultHandler)
      }

    /**
     *  Like {@link #getLocalLock(String, Handler)} but specifying a timeout. If the lock is not obtained within the timeout
     *  a failure will be sent to the handler.
     *  <p>
     *    In general lock acquision is unordered, so that sequential attempts to acquire a lock,
     *    even from a single thread, can happen in non-sequential order.
     *  </p>
     * @param name  the name of the lock
     * @param timeout  the timeout in ms
     * @param resultHandler  the handler
     */
    def getLocalLockWithTimeoutL(name: String, timeout: Long): Task[Lock] =
      Task.handle[Lock] { resultHandler =>
        target.getLocalLockWithTimeout(name, timeout, resultHandler)
      }

    /**
     *  Get an asynchronous counter. The counter will be passed to the handler.
     * @param name  the name of the counter.
     * @param resultHandler  the handler
     */
    def getCounterL(name: String): Task[Counter] =
      Task.handle[Counter] { resultHandler =>
        target.getCounter(name, resultHandler)
      }

    /**
     *  Get an asynchronous local counter. The counter will be passed to the handler.
     * @param name  the name of the counter.
     * @param resultHandler  the handler
     */
    def getLocalCounterL(name: String): Task[Counter] =
      Task.handle[Counter] { resultHandler =>
        target.getLocalCounter(name, resultHandler)
      }
  }


  implicit class VertxTimeoutStreamOps(val target: TimeoutStream) extends AnyVal {
    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[Long]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())
  }


  implicit class VertxCompositeFutureOps(val target: CompositeFuture) extends AnyVal {

    def setHandlerL(): Task[CompositeFuture] =
      Task.handle[CompositeFuture] { handler =>
        target.setHandler(handler)
      }


    def onCompleteL(): Task[CompositeFuture] =
      Task.handle[CompositeFuture] { handler =>
        target.onComplete(handler)
      }
  }


  implicit class VertxHttpClientRequestOps(val target: HttpClientRequest) extends AnyVal {
    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[HttpClientResponse]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #write(Buffer)} but with an {@code handler} called when the operation completes
     */
    def writeL(data: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(data, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #write(String)} but with an {@code handler} called when the operation completes
     */
    def writeL(chunk: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(chunk, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #write(String,String)} but with an {@code handler} called when the operation completes
     */
    def writeL(chunk: String, enc: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.write(chunk, enc, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #end(String)} but with an {@code handler} called when the operation completes
     */
    def endL(chunk: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(chunk, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #end(String,String)} but with an {@code handler} called when the operation completes
     */
    def endL(chunk: String, enc: String): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(chunk, enc, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #end(String)} but with an {@code handler} called when the operation completes
     */
    def endL(chunk: Buffer): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(chunk, handler)
      }.map(_ => ())

    /**
     *  Same as {@link #end()} but with an {@code handler} called when the operation completes
     */
    def endL(): Task[Unit] =
      Task.handle[Void] { handler =>
        target.end(handler)
      }.map(_ => ())
  }


  implicit class VertxHttpClientResponseOps(val target: HttpClientResponse) extends AnyVal {
    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[Buffer]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())
  }


  implicit class VertxAsyncMapOps[K, V](val target: AsyncMap[K, V])  {
    /**
     *  Get a value from the map, asynchronously.
     * @param k  the key
     * @param resultHandler - this will be called some time later with the async result.
     */
    def getL(k: K): Task[V] =
      Task.handle[V] { resultHandler =>
        target.get(k, resultHandler)
      }

    /**
     *  Put a value in the map, asynchronously.
     * @param k  the key
     * @param v  the value
     * @param completionHandler - this will be called some time later to signify the value has been put
     */
    def putL(k: K, v: V): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.put(k, v, completionHandler)
      }.map(_ => ())

    /**
     *  Like {@link #put} but specifying a time to live for the entry. Entry will expire and get evicted after the
     *  ttl.
     * @param k  the key
     * @param v  the value
     * @param ttl  The time to live (in ms) for the entry
     * @param completionHandler  the handler
     */
    def putL(k: K, v: V, ttl: Long): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.put(k, v, ttl, completionHandler)
      }.map(_ => ())

    /**
     *  Put the entry only if there is no entry with the key already present. If key already present then the existing
     *  value will be returned to the handler, otherwise null.
     * @param k  the key
     * @param v  the value
     * @param completionHandler  the handler
     */
    def putIfAbsentL(k: K, v: V): Task[V] =
      Task.handle[V] { completionHandler =>
        target.putIfAbsent(k, v, completionHandler)
      }

    /**
     *  Link {@link #putIfAbsent} but specifying a time to live for the entry. Entry will expire and get evicted
     *  after the ttl.
     * @param k  the key
     * @param v  the value
     * @param ttl  The time to live (in ms) for the entry
     * @param completionHandler  the handler
     */
    def putIfAbsentL(k: K, v: V, ttl: Long): Task[V] =
      Task.handle[V] { completionHandler =>
        target.putIfAbsent(k, v, ttl, completionHandler)
      }

    /**
     *  Remove a value from the map, asynchronously.
     * @param k  the key
     * @param resultHandler - this will be called some time later to signify the value has been removed
     */
    def removeL(k: K): Task[V] =
      Task.handle[V] { resultHandler =>
        target.remove(k, resultHandler)
      }

    /**
     *  Remove a value from the map, only if entry already exists with same value.
     * @param k  the key
     * @param v  the value
     * @param resultHandler - this will be called some time later to signify the value has been removed
     */
    def removeIfPresentL(k: K, v: V): Task[Boolean] =
      Task.handle[java.lang.Boolean] { resultHandler =>
        target.removeIfPresent(k, v, resultHandler)
      }.map(out => out: Boolean)

    /**
     *  Replace the entry only if it is currently mapped to some value
     * @param k  the key
     * @param v  the new value
     * @param resultHandler  the result handler will be passed the previous value
     */
    def replaceL(k: K, v: V): Task[V] =
      Task.handle[V] { resultHandler =>
        target.replace(k, v, resultHandler)
      }

    /**
     *  Replace the entry only if it is currently mapped to a specific value
     * @param k  the key
     * @param oldValue  the existing value
     * @param newValue  the new value
     * @param resultHandler the result handler
     */
    def replaceIfPresentL(k: K, oldValue: V, newValue: V): Task[Boolean] =
      Task.handle[java.lang.Boolean] { resultHandler =>
        target.replaceIfPresent(k, oldValue, newValue, resultHandler)
      }.map(out => out: Boolean)

    /**
     *  Clear all entries in the map
     * @param resultHandler  called on completion
     */
    def clearL(): Task[Unit] =
      Task.handle[Void] { resultHandler =>
        target.clear(resultHandler)
      }.map(_ => ())

    /**
     *  Provide the number of entries in the map
     * @param resultHandler  handler which will receive the number of entries
     */
    def sizeL(): Task[Int] =
      Task.handle[java.lang.Integer] { resultHandler =>
        target.size(resultHandler)
      }.map(out => out: Int)
  }


  implicit class VertxPipeOps[T](val target: Pipe[T])  {
    /**
     *  Start to pipe the elements to the destination {@code WriteStream}.
     *  <p>
     *  When the operation fails with a write error, the source stream is resumed.
     * @param dst the destination write stream
     * @param completionHandler the handler called when the pipe operation completes
     */
    def toL(dst: WriteStream[T]): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.to(dst, completionHandler)
      }.map(_ => ())
  }


  implicit class VertxRecordParserOps(val target: RecordParser) extends AnyVal {
    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[Buffer]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())
  }


  implicit class VertxCounterOps(val target: Counter) extends AnyVal {
    /**
     *  Get the current value of the counter
     * @param resultHandler handler which will be passed the value
     */
    def getL(): Task[Long] =
      Task.handle[java.lang.Long] { resultHandler =>
        target.get(resultHandler)
      }.map(out => out: Long)

    /**
     *  Increment the counter atomically and return the new count
     * @param resultHandler handler which will be passed the value
     */
    def incrementAndGetL(): Task[Long] =
      Task.handle[java.lang.Long] { resultHandler =>
        target.incrementAndGet(resultHandler)
      }.map(out => out: Long)

    /**
     *  Increment the counter atomically and return the value before the increment.
     * @param resultHandler handler which will be passed the value
     */
    def getAndIncrementL(): Task[Long] =
      Task.handle[java.lang.Long] { resultHandler =>
        target.getAndIncrement(resultHandler)
      }.map(out => out: Long)

    /**
     *  Decrement the counter atomically and return the new count
     * @param resultHandler handler which will be passed the value
     */
    def decrementAndGetL(): Task[Long] =
      Task.handle[java.lang.Long] { resultHandler =>
        target.decrementAndGet(resultHandler)
      }.map(out => out: Long)

    /**
     *  Add the value to the counter atomically and return the new count
     * @param value  the value to add
     * @param resultHandler handler which will be passed the value
     */
    def addAndGetL(value: Long): Task[Long] =
      Task.handle[java.lang.Long] { resultHandler =>
        target.addAndGet(value, resultHandler)
      }.map(out => out: Long)

    /**
     *  Add the value to the counter atomically and return the value before the add
     * @param value  the value to add
     * @param resultHandler handler which will be passed the value
     */
    def getAndAddL(value: Long): Task[Long] =
      Task.handle[java.lang.Long] { resultHandler =>
        target.getAndAdd(value, resultHandler)
      }.map(out => out: Long)

    /**
     *  Set the counter to the specified value only if the current value is the expectec value. This happens
     *  atomically.
     * @param expected  the expected value
     * @param value  the new value
     * @param resultHandler  the handler will be passed true on success
     */
    def compareAndSetL(expected: Long, value: Long): Task[Boolean] =
      Task.handle[java.lang.Boolean] { resultHandler =>
        target.compareAndSet(expected, value, resultHandler)
      }.map(out => out: Boolean)
  }


  implicit class VertxHttpConnectionOps(val target: HttpConnection) extends AnyVal {
    /**
     *  Send to the remote endpoint an update of this endpoint settings
     *  <p/>
     *  The {@code completionHandler} will be notified when the remote endpoint has acknowledged the settings.
     *  <p/>
     *  This is not implemented for HTTP/1.x.
     * @param settings the new settings
     * @param completionHandler the handler notified when the settings have been acknowledged by the remote endpoint
     * @return a reference to this, so the API can be used fluently
     */
    def updateSettingsL(settings: Http2Settings): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.updateSettings(settings, completionHandler)
      }.map(_ => ())

    /**
     *  Send a {@literal PING} frame to the remote endpoint.
     *  <p/>
     *  This is not implemented for HTTP/1.x.
     * @param data the 8 bytes data of the frame
     * @param pongHandler an async result handler notified with pong reply or the failure
     * @return a reference to this, so the API can be used fluently
     */
    def pingL(data: Buffer): Task[Buffer] =
      Task.handle[Buffer] { pongHandler =>
        target.ping(data, pongHandler)
      }
  }


  implicit class VertxNetClientOps(val target: NetClient) extends AnyVal {
    /**
     *  Open a connection to a server at the specific {@code port} and {@code host}.
     *  <p>
     *  {@code host} can be a valid host name or IP address. The connect is done asynchronously and on success, a
     *  {@link NetSocket} instance is supplied via the {@code connectHandler} instance
     * @param port  the port
     * @param host  the host
     * @return a reference to this, so the API can be used fluently
     */
    def connectL(port: Int, host: String): Task[NetSocket] =
      Task.handle[NetSocket] { connectHandler =>
        target.connect(port, host, connectHandler)
      }

    /**
     *  Open a connection to a server at the specific {@code port} and {@code host}.
     *  <p>
     *  {@code host} can be a valid host name or IP address. The connect is done asynchronously and on success, a
     *  {@link NetSocket} instance is supplied via the {@code connectHandler} instance
     * @param port the port
     * @param host the host
     * @param serverName the SNI server name
     * @return a reference to this, so the API can be used fluently
     */
    def connectL(port: Int, host: String, serverName: String): Task[NetSocket] =
      Task.handle[NetSocket] { connectHandler =>
        target.connect(port, host, serverName, connectHandler)
      }

    /**
     *  Open a connection to a server at the specific {@code remoteAddress}.
     *  <p>
     *  The connect is done asynchronously and on success, a {@link NetSocket} instance is supplied via the {@code connectHandler} instance
     * @param remoteAddress the remote address
     * @return a reference to this, so the API can be used fluently
     */
    def connectL(remoteAddress: SocketAddress): Task[NetSocket] =
      Task.handle[NetSocket] { connectHandler =>
        target.connect(remoteAddress, connectHandler)
      }

    /**
     *  Open a connection to a server at the specific {@code remoteAddress}.
     *  <p>
     *  The connect is done asynchronously and on success, a {@link NetSocket} instance is supplied via the {@code connectHandler} instance
     * @param remoteAddress the remote address
     * @param serverName the SNI server name
     * @return a reference to this, so the API can be used fluently
     */
    def connectL(remoteAddress: SocketAddress, serverName: String): Task[NetSocket] =
      Task.handle[NetSocket] { connectHandler =>
        target.connect(remoteAddress, serverName, connectHandler)
      }
  }


  implicit class VertxHttpServerFileUploadOps(val target: HttpServerFileUpload) extends AnyVal {
    /**
     *  Pipe this {@code ReadStream} to the {@code WriteStream}.
     *  <p>
     *  Elements emitted by this stream will be written to the write stream until this stream ends or fails.
     *  <p>
     *  Once this stream has ended or failed, the write stream will be ended and the {@code handler} will be
     *  called with the result.
     * @param dst the destination write stream
     */
    def pipeToL(dst: WriteStream[Buffer]): Task[Unit] =
      Task.handle[Void] { handler =>
        target.pipeTo(dst, handler)
      }.map(_ => ())
  }


  implicit class VertxVertxOps(val target: Vertx) extends AnyVal {
    /**
     *  Like {@link #close} but the completionHandler will be called when the close is complete
     * @param completionHandler  The handler will be notified when the close is complete.
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.close(completionHandler)
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
    def deployVerticleL(name: String): Task[String] =
      Task.handle[String] { completionHandler =>
        target.deployVerticle(name, completionHandler)
      }

    /**
     *  Like {@link #deployVerticle(String, Handler)} but {@link io.vertx.core.DeploymentOptions} are provided to configure the
     *  deployment.
     * @param name  the name
     * @param options  the deployment options.
     * @param completionHandler  a handler which will be notified when the deployment is complete
     */
    def deployVerticleL(name: String, options: DeploymentOptions): Task[String] =
      Task.handle[String] { completionHandler =>
        target.deployVerticle(name, options, completionHandler)
      }

    /**
     *  Like {@link #undeploy(String) } but the completionHandler will be notified when the undeployment is complete.
     * @param deploymentID  the deployment ID
     * @param completionHandler  a handler which will be notified when the undeployment is complete
     */
    def undeployL(deploymentID: String): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.undeploy(deploymentID, completionHandler)
      }.map(_ => ())

    /**
     *  Safely execute some blocking code.
     *  <p>
     *  Executes the blocking code in the handler {@code blockingCodeHandler} using a thread from the worker pool.
     *  <p>
     *  When the code is complete the handler {@code resultHandler} will be called with the result on the original context
     *  (e.g. on the original event loop of the caller).
     *  <p>
     *  A {@code Future} instance is passed into {@code blockingCodeHandler}. When the blocking code successfully completes,
     *  the handler should call the {@link Promise#complete} or {@link Promise#complete(Object)} method, or the {@link Promise#fail}
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
    def executeBlockingL[T](blockingCodeHandler: Handler[Promise[T]], ordered: Boolean): Task[T] =
      Task.handle[T] { resultHandler =>
        target.executeBlocking(blockingCodeHandler, ordered, resultHandler)
      }

    /**
     *  Like {@link #executeBlocking(Handler, boolean, Handler)} called with ordered = true.
     */
    def executeBlockingL[T](blockingCodeHandler: Handler[Promise[T]]): Task[T] =
      Task.handle[T] { resultHandler =>
        target.executeBlocking(blockingCodeHandler, resultHandler)
      }
  }

  object VertxFunctions {
    /**
     *  Creates a clustered instance using the specified options.
     *  <p>
     *  The instance is created asynchronously and the resultHandler is called with the result when it is ready.
     * @param options  the options to use
     * @param resultHandler  the result handler that will receive the result
     */
    def clusteredVertxL(options: VertxOptions): Task[Vertx] =
      Task.handle[Vertx] { resultHandler =>
        Vertx.clusteredVertx(options, resultHandler)
      }
  }

  implicit class VertxNetServerOps(val target: NetServer) extends AnyVal {
    /**
     *  Like {@link #listen} but providing a handler that will be notified when the server is listening, or fails.
     * @param listenHandler  handler that will be notified when listening or failed
     * @return a reference to this, so the API can be used fluently
     */
    def listenL(): Task[NetServer] =
      Task.handle[NetServer] { listenHandler =>
        target.listen(listenHandler)
      }

    /**
     *  Like {@link #listen(int, String)} but providing a handler that will be notified when the server is listening, or fails.
     * @param port  the port to listen on
     * @param host  the host to listen on
     * @param listenHandler handler that will be notified when listening or failed
     * @return a reference to this, so the API can be used fluently
     */
    def listenL(port: Int, host: String): Task[NetServer] =
      Task.handle[NetServer] { listenHandler =>
        target.listen(port, host, listenHandler)
      }

    /**
     *  Like {@link #listen(int)} but providing a handler that will be notified when the server is listening, or fails.
     * @param port  the port to listen on
     * @param listenHandler handler that will be notified when listening or failed
     * @return a reference to this, so the API can be used fluently
     */
    def listenL(port: Int): Task[NetServer] =
      Task.handle[NetServer] { listenHandler =>
        target.listen(port, listenHandler)
      }

    /**
     *  Like {@link #listen(SocketAddress)} but providing a handler that will be notified when the server is listening, or fails.
     * @param localAddress the local address to listen on
     * @param listenHandler handler that will be notified when listening or failed
     * @return a reference to this, so the API can be used fluently
     */
    def listenL(localAddress: SocketAddress): Task[NetServer] =
      Task.handle[NetServer] { listenHandler =>
        target.listen(localAddress, listenHandler)
      }

    /**
     *  Like {@link #close} but supplying a handler that will be notified when close is complete.
     * @param completionHandler  the handler
     */
    def closeL(): Task[Unit] =
      Task.handle[Void] { completionHandler =>
        target.close(completionHandler)
      }.map(_ => ())
  }


}