/*
 * Copyright 2018 David Gregory and the Vertices project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vertices

import java.lang.reflect.Modifier

import scala.collection.JavaConverters._

import cats._, implicits._
import com.typesafe.scalalogging.LazyLogging
import io.vertx.core.AbstractVerticle
import io.vertx.core.{ Future => VertxFuture }
import monix.eval.Task
import org.reflections.Reflections

abstract class VertexVerticle extends AbstractVerticle with LazyLogging {
  lazy val scheduler = VertexScheduler(Vertx(getVertx()))

  override def start(started: VertxFuture[Void]): Unit = {
    val services = new Reflections(getClass.getPackage.getName)
      .getSubTypesOf(classOf[VertexHandler[_, _, _]])
      .asScala.toList.filterNot { clazz =>
        Modifier.isAbstract(clazz.getModifiers)
      }

    val serviceNames = services.map(_.getName)

    logger.debug(s"""Registering handlers: ${serviceNames.mkString("[", ", ", "]")}""")

    val registerServiceInstances = services.map { clazz =>
      clazz
        .getDeclaredConstructor(classOf[io.vertx.core.Vertx])
        .newInstance(vertx)
    }.parTraverse(_.start)

    val ready = startUp.flatMap(_ => registerServiceInstances)

    started
      .completeWith(ready)
      .runAsync(scheduler)
  }

  override def stop(stopped: VertxFuture[Void]): Unit = {
    stopped
      .completeWith(shutDown)
      .runAsync(scheduler)
  }

  def startUp: Task[Unit]

  def shutDown: Task[Unit]
}
