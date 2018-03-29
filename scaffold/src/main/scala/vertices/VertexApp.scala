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
import cats.data.ReaderT
import com.typesafe.scalalogging.LazyLogging
import monix.eval.{ Task, TaskApp }
import org.reflections.Reflections
import scala.io.StdIn

object VertexApp extends TaskApp with LazyLogging {
  def startVerticles(classes: List[Class[_ <: VertexVerticle]]) =
    ReaderT[Task, Vertx, List[(Class[_ <: VertexVerticle], String)]] { vertx =>
      classes.parTraverse { clazz =>
        logger.debug(s"Deploying new instance of ${clazz.getName}")
        val instance = clazz.newInstance()
        vertx.deployVerticle(instance).map { id =>
          logger.debug(s"Deployed ${clazz.getName} with ID ${id}")
          (clazz, id)
        }
      }
    }

  def stopVerticles(deploymentIds: List[(Class[_ <: VertexVerticle], String)]) =
    ReaderT[Task, Vertx, Unit] { vertx =>
      deploymentIds.parTraverse {
        case (clazz, id) =>
          logger.debug(s"Undeploying ${clazz.getName} with deployment ID ${id}")
          vertx.undeploy(id)
      }.void
    }

  def awaitShutdown: ReaderT[Task, Vertx, Unit] =
    ReaderT.liftF[Task, Vertx, Unit] {
      Task.eval(StdIn.readLine("Press enter to shut down"))
    }

  override def run(args: Array[String]): Task[Unit] = {
    val vertx = Vertx.vertx

    val verticles = new Reflections(args(0))
      .getSubTypesOf(classOf[VertexVerticle])
      .asScala.toList.filterNot { clazz =>
        Modifier.isAbstract(clazz.getModifiers)
      }

    val verticleNames = verticles.map(_.getName)

    logger.debug(s"""Deploying verticles: ${verticleNames.mkString("[", ", ", "]")}""")

    val runApp = for {
      ids <- startVerticles(verticles)
      _ <- awaitShutdown
      _ <- stopVerticles(ids)
    } yield ()

    runApp
      .run(vertx)
      .doOnFinish(_ => vertx.close())
  }
}
