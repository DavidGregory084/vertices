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
    ReaderT[Task, Vertx, List[String]] { vertx =>
      classes.parTraverse { clazz =>
        logger.debug(s"Deploying new instance of ${clazz.getName}")
        val instance = clazz.newInstance()
        vertx.deployVerticle(instance).map { id =>
          logger.debug(s"Deployed ${clazz.getName} with ID ${id}")
          id
        }
      }
    }

  def stopVerticles(deploymentIds: List[String]) =
    ReaderT[Task, Vertx, Unit] { vertx =>
      deploymentIds.parTraverse { id =>
        logger.debug(s"Undeploying deployment ID ${id}")
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
