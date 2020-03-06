## Vertices

[![Build Status](https://api.travis-ci.org/DavidGregory084/vertices.svg)](https://travis-ci.org/DavidGregory084/vertices)
[![License](https://img.shields.io/github/license/DavidGregory084/vertices.svg)](https://opensource.org/licenses/Apache-2.0)

### Overview

Vertices is a Scala library that provides extension methods for [Eclipse Vert.x](http://vertx.io/) which allow the Vert.x framework to be used with the Scala library [Monix](https://monix.io) to write asynchronous programs in a functional style.

### Example

```tut:silent
import cats.implicits._
import io.vertx.core._
import vertices._
import vertices.core._
import monix.execution.Scheduler
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
```

```tut:book
val vertx = Vertx.vertx

val echoMessagesExuberantly = vertx.eventBus.
  consumer[String]("echo").
  toObservable(vertx).
  foreachL(msg => msg.reply(msg.body.toUpperCase))
  
echoMessagesExuberantly.runToFuture

val sendAMessage = vertx.eventBus.
  requestL[String]("echo", "hello").
  foreachL(msg => println(msg.body))

val demoTask =
  sendAMessage *> vertx.closeL
  
Await.result(demoTask.runToFuture, 20.seconds)
```

### Conduct

Contributors are expected to follow the [Scala Code of Conduct](https://www.scala-lang.org/conduct/) while participating on Github and any other venues associated with the project. 

### Acknowledgements

Thanks are due to Alexandru Nedelcu ([@alexandru](https://github.com/alexandru)) for the [Monix](https://github.com/monix/monix) library, which makes writing asynchronous code in Scala an absolute pleasure.

### License

All code in this repository is licensed under the Apache License, Version 2.0.  See [LICENSE](./LICENSE).
