## Vertices

[![Build Status](https://api.travis-ci.org/DavidGregory084/vertices.svg)](https://travis-ci.org/DavidGregory084/vertices)
[![License](https://img.shields.io/github/license/DavidGregory084/vertices.svg)](https://opensource.org/licenses/Apache-2.0)

### Overview

Vertices is a Scala library that provides extension methods for [Eclipse Vert.x](http://vertx.io/) which allow the Vert.x framework to be used with the Scala library [Monix](https://monix.io) to write asynchronous programs in a functional style.

The basic idea of this library is to provide replacements for Vert.x methods which accept callbacks. This makes it easer to use the diverse functionality provided by the Vert.x libraries while writing idiomatic Scala code.

The naming strategy follows that of [Monix](https://monix.io). The new methods which return `Task` are suffixed with the letter `L`, which indicates that underlying task is not executed right away (in other words that it is "lazy").

### Example

The Vert.x library provides a `SharedData` object in which we can store and retrieve named `AsyncMap` objects.

Using the original Vert.x APIs we would write code to access this data like so:

```scala
import io.vertx.core._
import vertices._
import vertices.core._
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.{ Await, Promise }
import scala.concurrent.duration._
```

```scala
val resultPromise = Promise[String]()
// resultPromise: scala.concurrent.Promise[String] = Future(<not completed>)

val sharedData = Vertx.vertx.sharedData
// sharedData: io.vertx.core.shareddata.SharedData = io.vertx.core.shareddata.impl.SharedDataImpl@575c23f1

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

As you can see this is a perfect demonstration of *callback hell*.

Using this library we can write the code above as follows:

```scala
val resultTask = for {
  asyncMap <- sharedData.getAsyncMapL[String, String]("example")
  _        <- asyncMap.putL("key", "value")
  value    <- asyncMap.getL("key")
} yield value
// resultTask: monix.eval.Task[String] = Task.FlatMap$598978484

Await.result(resultTask.runToFuture, 20.seconds)
// res2: String = value
```

We can also convert Vert.x `ReadStream` objects to Monix `Observable`s.

The example below uses the Vert.x Event Bus to define an event bus consumer that echoes messages back to the sender in all-caps:

```scala
import cats.syntax.apply._
// import cats.syntax.apply._

val vertx = Vertx.vertx
// vertx: io.vertx.core.Vertx = io.vertx.core.impl.VertxImpl@4e0a5606

val echoMessagesExuberantly = vertx.eventBus.
  consumer[String]("echo").
  toObservable(vertx).
  foreachL(msg => msg.reply(msg.body.toUpperCase))
// echoMessagesExuberantly: monix.eval.Task[Unit] = Task.Async$92551864

echoMessagesExuberantly.runToFuture
// res3: monix.execution.CancelableFuture[Unit] = Async(Future(<not completed>),monix.eval.internal.TaskConnection$Impl$$anon$1@3808bdfc)

val sendAMessage = vertx.eventBus.
  requestL[String]("echo", "hello").
  foreachL(msg => println(msg.body))
// sendAMessage: monix.eval.Task[Unit] = Task.Map$1903952121

val demoTask =
  sendAMessage *> vertx.closeL
// demoTask: monix.eval.Task[Unit] = Task.FlatMap$744172898

Await.result(demoTask.runToFuture, 20.seconds)
// HELLO
```

### Usage

The library is published for Scala 2.13 only.

The artifact names resemble those of the original Vert.x artifacts.

They are listed below using the categories defined in the [Vert.x Documentation](https://vertx.io/docs/).

SBT dependency coordinates:

```scala
val verticesVersion = "0.1.0"

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
def verticesVersion = T { "0.1.0" }

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

### Example

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
