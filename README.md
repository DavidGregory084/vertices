## Vertices

[![Build Status](https://api.travis-ci.org/DavidGregory084/vertices.svg)](https://travis-ci.org/DavidGregory084/vertices)
[![Coverage Status](http://codecov.io/github/DavidGregory084/vertices/coverage.svg?branch=master)](http://codecov.io/github/DavidGregory084/vertices?branch=master)
[![License](https://img.shields.io/github/license/DavidGregory084/vertices.svg)](https://opensource.org/licenses/Apache-2.0)

### Overview

Vertices is a Scala library which provides wrapper APIs for [Eclipse Vert.x](http://vertx.io/) which allow the Vert.x framework to be used in a more functional style.

### Example

```scala
import vertices._
import monix.execution.Scheduler
import scala.concurrent.Await
import scala.concurrent.duration._

val vertx = Vertx.vertx

implicit val scheduler = VertexScheduler(vertx)
```

```scala
// Create a task which registers a message handler at the address "echo"
val echoMessagesExuberantly = vertx.eventBus.consumer[String]("echo").
  toObservable(vertx).
  // It's very important that it replies enthusiastically
  foreachL(msg => msg.reply(msg.body.toUpperCase))
// echoMessagesExuberantly: monix.eval.Task[Unit] = Task.Async$1147919227

// Kick that off in the background
echoMessagesExuberantly.runAsync(scheduler)
// res4: monix.execution.CancelableFuture[Unit] = Async(Future(<not completed>),monix.execution.cancelables.StackedCancelable$Impl@5fe34078)

// Send a message to the handler
val sendAMessage = vertx.eventBus.
  send[String]("echo", "hello").
  foreachL(msg => println(msg.body))
// sendAMessage: monix.eval.Task[Unit] = Task.Map$907630332

Await.result(sendAMessage.runAsync(scheduler), 20.seconds)
// HELLO

// Tidy up after ourselves - this will unregister the handler and shut down Vert.x
vertx.close.runAsync(Scheduler.global)
// res8: monix.execution.CancelableFuture[Unit] = Async(Future(<not completed>),monix.execution.cancelables.StackedCancelable$Impl@435d6a1a)
```

### Conduct

Contributors are expected to follow the [Typelevel Code of Conduct](http://typelevel.org/conduct.html) while participating on Github and any other venues associated with the project. 

### Acknowledgements

Thanks are due to Alexandru Nedelcu ([@alexandru](https://github.com/alexandru)) for the [Monix](https://github.com/monix/monix) library, which makes writing asynchronous code in Scala an absolute pleasure. Vertices makes extensive use of Monix in wrapping the callback-based code in Vert.x.

### License

All code in this repository is licensed under the Apache License, Version 2.0.  See [LICENSE](./LICENSE).
