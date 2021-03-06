## Vertices

[![Build Status](https://api.travis-ci.org/DavidGregory084/vertices.svg)](https://travis-ci.org/DavidGregory084/vertices)
[![License](https://img.shields.io/github/license/DavidGregory084/vertices.svg)](https://opensource.org/licenses/Apache-2.0)
[![javadoc](https://javadoc.io/badge2/io.github.davidgregory084/vertices-core_2.13/javadoc.svg)](https://javadoc.io/doc/io.github.davidgregory084/vertices-core_2.13) 

### Overview

Vertices is a Scala library that provides extension methods for the [Eclipse Vert.x](http://vertx.io/) APIs.

The basic idea of this library is to provide replacements for Vert.x methods which accept callbacks. This makes it easier to use the diverse functionality provided by the Vert.x libraries while writing idiomatic Scala code.

The new methods make use of the [Task](https://monix.io/api/3.1/monix/eval/Task.html) type from the excellent [Monix](https://monix.io) library.

### Example

The Vert.x library provides a [SharedData](https://vertx.io/docs/apidocs/io/vertx/core/shareddata/SharedData.html) object which we can use to store and retrieve named [AsyncMap](https://vertx.io/docs/apidocs/io/vertx/core/shareddata/AsyncMap.html) objects.

Using the original Vert.x APIs we would write code to access this data like so:

```scala
import io.vertx.core._
import scala.concurrent.{ Await, Promise }
import scala.concurrent.duration._
```
```scala
val vertx = Vertx.vertx
// vertx: io.vertx.core.Vertx = io.vertx.core.impl.VertxImpl@69ffdaa8

val resultPromise = Promise[String]()
// resultPromise: scala.concurrent.Promise[String] = Future(<not completed>)

val sharedData = vertx.sharedData
// sharedData: io.vertx.core.shareddata.SharedData = io.vertx.core.shareddata.impl.SharedDataImpl@70439c3

sharedData.getAsyncMap[String, String]("example", getMapResult => {
  if (getMapResult.succeeded) {
    val asyncMap = getMapResult.result
    asyncMap.put("key", "value", putResult => {
      if (putResult.succeeded) {
        asyncMap.get("key", getResult => {
          if (getResult.succeeded) {
            resultPromise.success(getResult.result)
          } else {
            resultPromise.failure(getResult.cause)
          }
        })
      } else {
        resultPromise.failure(putResult.cause)
      }
    })
  } else {
    resultPromise.failure(getMapResult.cause)
  }
})

Await.result(resultPromise.future, 20.seconds)
// res1: String = value
```

As you can see this is a perfect demonstration of "callback hell".

Using this library we can write the code above as follows:

```scala
import monix.execution.Scheduler
import vertices._
import vertices.core._
```
```scala
implicit val scheduler: Scheduler = new VertxScheduler(vertx)
// scheduler: monix.execution.Scheduler = vertices.core.VertxScheduler@4131f6db

val resultTask = for {
  asyncMap <- sharedData.getAsyncMapL[String, String]("example")
  _        <- asyncMap.putL("key", "value")
  value    <- asyncMap.getL("key")
} yield value
// resultTask: monix.eval.Task[String] = Task.FlatMap$482031947

Await.result(resultTask.runToFuture, 20.seconds)
// res2: String = value
```

We can also convert Vert.x [ReadStream](https://vertx.io/docs/apidocs/io/vertx/core/streams/ReadStream.html) objects to Monix [Observable](https://monix.io/api/3.1/monix/reactive/Observable.html)s.

The example below uses the Vert.x Event Bus to define an event bus consumer that echoes messages back to the sender in all-caps:

```scala
import cats.syntax.apply._
// import cats.syntax.apply._

val messageStream = vertx.eventBus.consumer[String]("echo")
// messageStream: io.vertx.core.eventbus.MessageConsumer[String] = io.vertx.core.eventbus.impl.HandlerRegistration@32107bba

val echoMessagesExuberantly = for {
  messageObservable <- messageStream.toObservable(vertx)
  _                 <- messageObservable.foreachL(msg => msg.reply(msg.body.toUpperCase))
} yield ()
// echoMessagesExuberantly: monix.eval.Task[Unit] = Task.FlatMap$936177086

echoMessagesExuberantly.runToFuture
// res3: monix.execution.CancelableFuture[Unit] = Async(Future(<not completed>),monix.eval.internal.TaskConnection$Impl$$anon$1@2b009051)

val sendAMessage = vertx.eventBus.
  requestL[String]("echo", "hello").
  foreachL(msg => println(msg.body))
// sendAMessage: monix.eval.Task[Unit] = Task.Map$368375378

val demoTask =
  sendAMessage *> vertx.closeL
// demoTask: monix.eval.Task[Unit] = Task.FlatMap$543433178

Await.result(demoTask.runToFuture(Scheduler.global), 20.seconds)
// HELLO
```

### Usage

The library is published for Scala 2.12 and 2.13.

The artifact names resemble those of the original Vert.x artifacts.

They are listed below using the categories defined in the [Vert.x Documentation](https://vertx.io/docs/).

SBT dependency coordinates:

```scala
val verticesVersion = "0.1.2"

// Vert.x core
"io.github.davidgregory084" %% "vertices-core" % verticesVersion
// Vert.x web
"io.github.davidgregory084" %% "vertices-web" % verticesVersion
"io.github.davidgregory084" %% "vertices-web-client" % verticesVersion
"io.github.davidgregory084" %% "vertices-web-api-contract" % verticesVersion
// Data access
"io.github.davidgregory084" %% "vertices-mongo-client" % verticesVersion
"io.github.davidgregory084" %% "vertices-redis-client" % verticesVersion
"io.github.davidgregory084" %% "vertices-cassandra-client" % verticesVersion
"io.github.davidgregory084" %% "vertices-sql-common" % verticesVersion
"io.github.davidgregory084" %% "vertices-jdbc-client" % verticesVersion
// Microservices
"io.github.davidgregory084" %% "vertices-service-discovery" % verticesVersion
"io.github.davidgregory084" %% "vertices-circuit-breaker" % verticesVersion
"io.github.davidgregory084" %% "vertices-config" % verticesVersion
// MQTT
"io.github.davidgregory084" %% "vertices-mqtt" % verticesVersion
// Authentication and Authorisation
"io.github.davidgregory084" %% "vertices-auth-common" % verticesVersion
"io.github.davidgregory084" %% "vertices-auth-oauth2" % verticesVersion
"io.github.davidgregory084" %% "vertices-auth-mongo" % verticesVersion
// Messaging
"io.github.davidgregory084" %% "vertices-stomp" % verticesVersion
"io.github.davidgregory084" %% "vertices-rabbitmq-client" % verticesVersion
"io.github.davidgregory084" %% "vertices-amqp-bridge" % verticesVersion
// Integration
"io.github.davidgregory084" %% "vertices-kafka-client" % verticesVersion
"io.github.davidgregory084" %% "vertices-mail-client" % verticesVersion
"io.github.davidgregory084" %% "vertices-consul-client" % verticesVersion
// Event Bus Bridge
"io.github.davidgregory084" %% "vertices-tcp-eventbus-bridge" % verticesVersion
// Devops
"io.github.davidgregory084" %% "vertices-health-check" % verticesVersion
```

Mill dependency coordinates:

```scala
def verticesVersion = T { "0.1.2" }

// Vert.x core
ivy"io.github.davidgregory084::vertices-core:${verticesVersion()}"
// Vert.x web
ivy"io.github.davidgregory084::vertices-web:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-web-client:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-web-api-contract:${verticesVersion()}"
// Data access
ivy"io.github.davidgregory084::vertices-mongo-client:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-redis-client:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-cassandra-client:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-sql-common:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-jdbc-client:${verticesVersion()}"
// Microservices
ivy"io.github.davidgregory084::vertices-service-discovery:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-circuit-breaker:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-config:${verticesVersion()}"
// MQTT
ivy"io.github.davidgregory084::vertices-mqtt:${verticesVersion()}"
// Authentication and Authorisation
ivy"io.github.davidgregory084::vertices-auth-common:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-auth-oauth2:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-auth-mongo:${verticesVersion()}"
// Messaging
ivy"io.github.davidgregory084::vertices-stomp:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-rabbitmq-client:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-amqp-bridge:${verticesVersion()}"
// Integration
ivy"io.github.davidgregory084::vertices-kafka-client:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-mail-client:${verticesVersion()}"
ivy"io.github.davidgregory084::vertices-consul-client:${verticesVersion()}"
// Event Bus Bridge
ivy"io.github.davidgregory084::vertices-tcp-eventbus-bridge:${verticesVersion()}"
// Devops
ivy"io.github.davidgregory084::vertices-health-check:${verticesVersion()}"
```
### Cheat Sheet

The naming strategy for extension methods follows that of [Monix](https://monix.io): the new methods which return [Task](https://monix.io/api/3.1/monix/eval/Task.html) are suffixed with the letter `L` since the underlying task is not executed right away (in other words that it is "lazy").

```scala
// Instead of the io.vertx.core.file.AsyncFile method
def write(data: Buffer, handler: Handler[AsyncResult[Void]]): AsyncFile
// We can use this extension method from vertices.core
def writeL(data: Buffer): Task[Unit]

// Instead of the io.vertx.core.dns.DnsClient method
def resolveMX(name: String, handler: Handler[AsyncResult[List[MxRecord]]]): DnsClient
// We can use this extension method from vertices.core
def resolveMXL(name: String): Task[List[MxRecord]]
```

Since it's not possible to decorate a Java class with new static methods, replacements for static methods reside within a companion object named after the original class with `Functions` appended to the end. For example, `io.vertx.core.Vertx.clusteredVertx` has a matching `vertices.core.VertxFunctions.clusteredVertxL` function.

```scala
// Instead of the io.vertx.ext.auth.oauth2.providers.GoogleAuth static method
def create(vertx: Vertx, url: String, handler: Handler[AsyncResult[OAuth2Auth]]): Unit
// We can use this function from vertices.auth.GoogleAuthFunctions
def createL(vertx: Vertx, url: String): Task[OAuth2Auth]
```

### Import Guide

Extension methods are made available by importing from the package corresponding to each module. The package names are selected to resemble those used by the original APIs.

```scala
// Vert.x core
import vertices.core._
// Vert.x web modules
import vertices.web._
import vertices.web.client._
import vertices.web.api.contract._
// Vert.x data access
import vertices.mongo._
import vertices.redis.client._
import vertices.cassandra._
import vertices.sql._
import vertices.jdbc._
// Vert.x microservices
import vertices.servicediscovery._
import vertices.circuitbreaker._
import vertices.config._
// Vert.x MQTT
import vertices.mqtt._
// Vert.x authentication and authorisation
import vertices.auth._
import vertices.auth.oauth2._
import vertices.auth.mongo._
// Vert.x messaging
import vertices.stomp._
import vertices.rabbitmq._
import vertices.amqpbridge._
// Vert.x integration
import vertices.kafka.client._
import vertices.mail._
import vertices.consul._
// Vert.x event bus bridge
import vertices.eventbus.bridge.tcp._
// Vert.x devops
import vertices.healthchecks._
```

The root package `vertices` also provides some useful extension methods and type class instances for Vert.x types.

### FAQ

Q. Why is `<some module>` missing?

A. The stable modules that have `Handler` operations have been added. If there are new modules that you need please raise a PR.

Q. Why is `<some method>` missing from the generated code?

A. The Vert.x code generation process relies on annotations in the original Java code. Sometimes these annotations are missing for `Handler` methods that could be wrapped by *vertices*. The solution is to raise a PR against the corresponding Vert.x project to add the annotations ([see example](https://github.com/eclipse-vertx/vert.x/pull/2573)).

### Conduct

Contributors are expected to follow the [Scala Code of Conduct](https://www.scala-lang.org/conduct/) while participating on Github and any other venues associated with the project. 

### Acknowledgements

Thanks are due to Alexandru Nedelcu ([@alexandru](https://github.com/alexandru)) for the [Monix](https://github.com/monix/monix) library, which makes writing asynchronous code in Scala an absolute pleasure.

### License

All code in this repository is licensed under the Apache License, Version 2.0.  See [LICENSE](./LICENSE).
