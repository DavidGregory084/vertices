package vertices

import java.lang.reflect.Modifier

import scala.collection.JavaConverters._

import cats._, implicits._
import cats.data.ReaderT
import com.typesafe.scalalogging.LazyLogging
import io.vertx.core.Vertx
import monix.eval.{ Task, TaskApp }
import org.reflections.Reflections

object VertexApp extends TaskApp with LazyLogging {
  def startVerticles(classes: List[Class[_ <: VertexVerticle]]) =
    ReaderT[Task, Vertx, List[String]] { vertx =>
      classes.parTraverse { clazz =>
        logger.debug(s"Deploying new instance of ${clazz.getName}")
        val instance = clazz.newInstance()
        Task.handle[String] {
          vertx.deployVerticle(instance, _)
        }.map { id =>
          logger.debug(s"Deployed ${clazz.getName} with ID ${id}")
          id
        }
      }
    }

  def stopVerticles(deploymentIds: List[String]) =
    ReaderT[Task, Vertx, Unit] { vertx =>
      deploymentIds.parTraverse { id =>
        logger.debug(s"Undeploying deployment ID ${id}")
        Task.handle[Void] { vertx.undeploy(id, _) }
      }.void
    }

  def closeVertx(vertx: Vertx) =
    Task.handle[Void] { vertx.close(_) }.void

  override def run(args: Array[String]): Task[Unit] = {
    val vertx = Vertx.vertx

    val verticles = new Reflections(args(0))
      .getSubTypesOf(classOf[VertexVerticle])
      .asScala.toList.filterNot { clazz =>
        Modifier.isAbstract(clazz.getModifiers)
      }

    val verticleNames = verticles.map(_.getName)

    logger.debug(s"""Deploying verticles: ${verticleNames.mkString("[", ", ", "]")}""")

    startVerticles(verticles)
      .flatMap(stopVerticles)
      .run(vertx)
      .doOnFinish(_ => closeVertx(vertx))
  }
}
