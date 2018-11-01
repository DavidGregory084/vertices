## Vertices

[![Build Status](https://api.travis-ci.org/DavidGregory084/vertices.svg)](https://travis-ci.org/DavidGregory084/vertices)
[![License](https://img.shields.io/github/license/DavidGregory084/vertices.svg)](https://opensource.org/licenses/Apache-2.0)

### Overview

Vertices is a Scala library which provides wrapper APIs for [Eclipse Vert.x](http://vertx.io/) which allow the Vert.x framework to be used in a more functional style.

### Example

```scala
import cats.implicits._
import vertices._
import vertices.core._
import monix.execution.Scheduler
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
```

```scala
val vertx = Vertx.vertx
// vertx: vertices.core.Vertx = Vertx(io.vertx.core.impl.VertxImpl@64f613da)

// Create a task which registers a message handler at the address "echo"
val echoMessagesExuberantly = vertx.eventBus.
  consumer[String]("echo").
  unwrap.
  toObservable(vertx).
  // It's very important that it replies enthusiastically
  foreachL(msg => msg.reply(msg.body.toUpperCase))
// echoMessagesExuberantly: monix.eval.Task[Unit] = Task.Async$481610448

// Kick that off in the background
echoMessagesExuberantly.runAsync
// res2: monix.execution.CancelableFuture[Unit] = Async(Future(<not completed>),monix.execution.cancelables.StackedCancelable$Impl@6d952747)

// Send a message to the handler
val sendAMessage = vertx.eventBus.
  send[String]("echo", "hello").
  foreachL(msg => println(msg.body))
// sendAMessage: monix.eval.Task[Unit] = Task.Map$1008904602

val demoTask =
  sendAMessage *> vertx.close // Tidy up after ourselves - this will unregister the handler and shut down Vert.x
// demoTask: monix.eval.Task[Unit] = Task.FlatMap$1611732847

Await.result(demoTask.runAsync, 20.seconds)
// HELLO
```

### Conduct

Contributors are expected to follow the [Typelevel Code of Conduct](http://typelevel.org/conduct.html) while participating on Github and any other venues associated with the project. 

### Acknowledgements

Thanks are due to Alexandru Nedelcu ([@alexandru](https://github.com/alexandru)) for the [Monix](https://github.com/monix/monix) library, which makes writing asynchronous code in Scala an absolute pleasure. Vertices makes extensive use of Monix in wrapping the callback-based code in Vert.x.

### License

All code in this repository is licensed under the Apache License, Version 2.0.  See [LICENSE](./LICENSE).