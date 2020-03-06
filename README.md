## Vertices

[![Build Status](https://api.travis-ci.org/DavidGregory084/vertices.svg)](https://travis-ci.org/DavidGregory084/vertices)
[![License](https://img.shields.io/github/license/DavidGregory084/vertices.svg)](https://opensource.org/licenses/Apache-2.0)

### Overview

Vertices is a Scala library that provides extension methods for [Eclipse Vert.x](http://vertx.io/) which allow the Vert.x framework to be used with the Scala library [Monix](https://monix.io) to write asynchronous programs in a functional style.

### Example

```scala
import cats.implicits._
import io.vertx.core._
import vertices._
import vertices.core._
import monix.execution.Scheduler
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
```

```scala
val vertx = Vertx.vertx
// vertx: io.vertx.core.Vertx = io.vertx.core.impl.VertxImpl@21f421b8

val echoMessagesExuberantly = vertx.eventBus.
  consumer[String]("echo").
  toObservable(vertx).
  foreachL(msg => msg.reply(msg.body.toUpperCase))
// echoMessagesExuberantly: monix.eval.Task[Unit] = Task.Async$517432234

echoMessagesExuberantly.runToFuture
// res0: monix.execution.CancelableFuture[Unit] = Async(Future(<not completed>),monix.eval.internal.TaskConnection$Impl$$anon$1@199549a5)

val sendAMessage = vertx.eventBus.
  requestL[String]("echo", "hello").
  foreachL(msg => println(msg.body))
// sendAMessage: monix.eval.Task[Unit] = Task.Map$635208007

val demoTask =
  sendAMessage *> vertx.closeL
// demoTask: monix.eval.Task[Unit] = Task.FlatMap$164063646

Await.result(demoTask.runToFuture, 20.seconds)
// HELLO
```

### Conduct

Contributors are expected to follow the [Typelevel Code of Conduct](http://typelevel.org/conduct.html) while participating on Github and any other venues associated with the project. 

### Acknowledgements

Thanks are due to Alexandru Nedelcu ([@alexandru](https://github.com/alexandru)) for the [Monix](https://github.com/monix/monix) library, which makes writing asynchronous code in Scala an absolute pleasure. Vertices makes extensive use of Monix in wrapping the callback-based code in Vert.x.

### License

All code in this repository is licensed under the Apache License, Version 2.0.  See [LICENSE](./LICENSE).
