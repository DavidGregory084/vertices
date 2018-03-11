package vertices

import java.lang.reflect.Modifier

import scala.collection.JavaConverters._

import cats._, implicits._
import com.typesafe.scalalogging.LazyLogging
import io.vertx.core.{ AbstractVerticle, Vertx }
import io.vertx.core.{ Future => VertxFuture }
import monix.eval.Task
import org.reflections.Reflections

abstract class VertexVerticle extends AbstractVerticle with LazyLogging {
  lazy val scheduler = VertexScheduler(getVertx())

  override def start(started: VertxFuture[Void]): Unit = {
    val services = new Reflections(getClass.getPackage.getName)
      .getSubTypesOf(classOf[VertexHandler[_, _]])
      .asScala.toList.filterNot { clazz =>
        Modifier.isAbstract(clazz.getModifiers)
      }

    val serviceNames = services.map(_.getName)

    logger.debug(s"""Registering handlers: ${serviceNames.mkString("[", ", ", "]")}""")

    val registerServiceInstances = services.map { clazz =>
      clazz
        .getDeclaredConstructor(classOf[Vertx])
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
