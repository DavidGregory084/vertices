import mill._
import mill.api.IO
import mill.scalalib._
import mill.scalalib.publish._
import mill.modules.Util

import $ivy.`com.lihaoyi::mill-contrib-tut:0.6.1`
import mill.contrib.tut._

import $ivy.`io.github.davidgregory084::mill-tpolecat:0.1.2`
import io.github.davidgregory084.TpolecatModule

import ammonite.ops._

trait PublishSettingsModule extends PublishModule {
  def publishVersion = "0.1.0"

  def pomSettings = PomSettings(
    description = artifactName(),
    organization = "io.github.davidgregory084",
    url = "https://github.com/DavidGregory084/vertices",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("DavidGregory084", "vertices"),
    developers = Seq(Developer("DavidGregory084", "David Gregory", "https://github.com/DavidGregory084"))
  )
}

trait ScalaSettingsModule extends TpolecatModule {
  def scalaVersion = "2.13.1"

  def vertxVersion = T { "3.8.5" }

  object test extends Tests {
    def ivyDeps = Agg(
      ivy"org.scalatest::scalatest:3.0.8",
      ivy"org.scalacheck::scalacheck:1.14.1"
    )
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}

object codegen extends ScalaSettingsModule {
  def vertxDocgenVersion = T { "0.9.2" }
  def nettyVersion = T { "4.1.42.Final" }
  def jacksonVersion = T { "2.9.9" }
  def log4jVersion = T { "1.2.17" }
  def slf4jVersion = T { "1.7.21" }
  def log4j2Version = T { "2.8.2" }

  def scalacOptions = T { super.scalacOptions() :+ "-Yno-imports" }

  def ivyDeps = T {
    val vertxVer = vertxVersion()
    val nettyVer = nettyVersion()
    Agg(
      ivy"io.vertx:vertx-codegen:${vertxVer}",
      ivy"org.slf4j:slf4j-api:${slf4jVersion()}",
      ivy"log4j:log4j:${log4jVersion()}",
      ivy"org.apache.logging.log4j:log4j-api:${log4j2Version()}",
      ivy"org.apache.logging.log4j:log4j-core:${log4j2Version()}"
    )
  }
}

trait VertxCodegen extends PublishSettingsModule with ScalaSettingsModule {
  def vertxModule: T[String]

  def artifactName = T { vertxModule().replaceFirst("vertx", "vertices") }

  def vertxSourceDeps = T {
    val vertxMod = vertxModule()
    val vertxVer = vertxVersion()
    Agg(ivy"io.vertx:${vertxMod}:${vertxVer}")
  }

  def scalacOptions = T {
    // It's impractical to use -Xfatal-warnings on the generated code;
    // we can't control whether the Vert.x API uses deprecated types in its methods
    super.scalacOptions()
      .filterNot(Set(
        "-Wvalue-discard",
        "-Wunused:imports",
        "-Xfatal-warnings"
      ))
  }

  def vertxSourceJars = T.sources {
    Lib.resolveDependencies(
      repositories,
      Lib.depToDependencyJava(_),
      vertxSourceDeps().seq,
      sources = true
    ).map(_.filter { p =>
      p.path.last.startsWith("vertx") &&
      p.path.last.endsWith(s"-sources.jar") &&
      p.path.last.contains(vertxModule())
    }.toSeq)
  }

  def vertxSources = T.sources {
    vertxSourceJars().foreach { path =>
      IO.unpackZip(path.path)(T.ctx().dest)
    }

    rm(T.ctx().dest / 'unpacked / 'io / 'vertx / 'groovy)
    rm(T.ctx().dest / 'unpacked / 'io / 'vertx / 'reactivex)
    rm(T.ctx().dest / 'unpacked / 'io / 'vertx / 'rxjava)

    ls.rec(T.ctx().dest / 'unpacked / 'io).filter(p => p.isDir && p.last == "impl").foreach(rm)

    Seq(PathRef(T.ctx().dest / 'unpacked / 'io))
  }

  def generatedSourcesPath = T { millSourcePath / 'generated }

  def generate = T.sources {
    val javaSources = Agg.from(
      for {
        root <- vertxSources()
        if exists(root.path)
        path <- if (root.path.isDir) ls.rec(root.path) else Seq(root.path)
        if path.isFile && path.ext == "java"
      } yield path
    )

    if (generatedSourcesPath().toIO.exists)
      ls(generatedSourcesPath()).foreach(rm)
    else
      mkdir(generatedSourcesPath())

    val processorOptions = Seq(
      "-proc:only",
      "-processor", "vertices.codegen.CodegenProcessor",
      s"-Acodegen.module.name=${millModuleSegments.parts.last}",
      s"-Acodegen.output.dir=${generatedSourcesPath().toIO.toString}"
    )

    val classpath = Agg.from(codegen.runClasspath() ++ compileClasspath())

    zincWorker.worker().compileJava(
      upstreamCompileOutput(),
      javaSources,
      classpath.map(_.path),
      javacOptions() ++ processorOptions,
      T.reporter.apply(hashCode)
    )

    Seq(PathRef(generatedSourcesPath()))
  }

  override def generatedSources = T { super.generatedSources() ++ generate() }
}

// The modules are listed below in the categories described on the
// Vert.x documentation page https://vertx.io/docs/

// The naming of the modules is designed so that the generated package names
// resemble the package names used in the original code.
//
// In the code for a module `_` is replaced by `.` in the generated packages,
// so that `eventbus_bridge_tcp` code is in the `vertices.eventbus.bridge.tcp` package.

// Core ------------------------------------------------------
object core extends VertxCodegen with TutModule {
  def tutVersion = "0.6.13"
  def tutTargetDirectory = millSourcePath / up

  def vertxModule = "vertx-core"

  // If I upgrade either of these I get a BootstrapMethodError from Tut.
  // Ominous...
  def catsVersion = T { "2.0.0" }
  def monixVersion = T { "3.0.0" }

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-core:${vertxVersion()}",
    ivy"io.vertx:vertx-reactive-streams:${vertxVersion()}",
    ivy"org.typelevel::cats-core:${catsVersion()}",
    ivy"io.monix::monix-eval:${monixVersion()}",
    ivy"io.monix::monix-execution:${monixVersion()}",
    ivy"io.monix::monix-reactive:${monixVersion()}",
    ivy"com.chuusai::shapeless:2.3.3"
  )
}

// Web -------------------------------------------------------
object web extends VertxCodegen {
  def moduleDeps = Seq(core, auth)

  def vertxModule = "vertx-web"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-web:${vertxVersion()}",
  )
}

object web_client extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-web-client"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-web-client:${vertxVersion()}",
  )
}

object web_api_contract extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-web-api-contract"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-web-api-contract:${vertxVersion()}",
  )
}

// Data access ----------------------------------------------
object mongo extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-mongo-client"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-mongo-client:${vertxVersion()}",
  )
}

object redis_client extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-redis-client"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-redis-client:${vertxVersion()}",
  )
}

object cassandra extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-cassandra-client"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-cassandra-client:${vertxVersion()}",
  )
}

object sql extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-sql-common"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-sql-common:${vertxVersion()}",
  )
}

object jdbc extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-jdbc-client"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-jdbc-client:${vertxVersion()}",
  )
}

// Microservices --------------------------------------------
object servicediscovery extends VertxCodegen {
  def moduleDeps = Seq(
    core,
    web, web_client,
    jdbc, mongo, redis_client
  )

  def vertxModule = "vertx-service-discovery"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-service-discovery:${vertxVersion()}",
  )
}

object circuitbreaker extends VertxCodegen {
  def moduleDeps = Seq(core, web)

  def vertxModule = "vertx-circuit-breaker"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-circuit-breaker:${vertxVersion()}",
  )
}

object config extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-config"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-config:${vertxVersion()}",
  )
}

// MQTT ------------------------------------------------------
object mqtt extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-mqtt"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-mqtt:${vertxVersion()}",
  )
}

// Authentication and Authorisation --------------------------
object auth extends VertxCodegen {
  def moduleDeps = Seq(core, auth_oauth2)

  def vertxModule = "vertx-auth-common"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-auth-common:${vertxVersion()}",
    ivy"io.vertx:vertx-auth-htdigest:${vertxVersion()}",
    ivy"io.vertx:vertx-auth-jwt:${vertxVersion()}",
  )
}

object auth_oauth2 extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-auth-oauth2"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-auth-oauth2:${vertxVersion()}",
  )
}

object auth_mongo extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-auth-mongo"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-auth-mongo:${vertxVersion()}",
  )
}

// Messaging -------------------------------------------------
object stomp extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-stomp"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-stomp:${vertxVersion()}",
  )
}

object rabbitmq extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-rabbitmq-client"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-rabbitmq-client:${vertxVersion()}",
  )
}

object amqpbridge extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-amqp-bridge"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-amqp-bridge:${vertxVersion()}",
  )
}

// Integration -----------------------------------------------
object kafka_client extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-kafka-client"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-kafka-client:${vertxVersion()}",
  )
}

object mail extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-mail-client"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-mail-client:${vertxVersion()}",
  )
}

object consul extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-consul-client"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-consul-client:${vertxVersion()}",
  )
}

// Event Bus Bridge ------------------------------------------
object eventbus_bridge_tcp extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-tcp-eventbus-bridge"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-tcp-eventbus-bridge:${vertxVersion()}"
  )
}

// Devops ----------------------------------------------------
object healthchecks extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModule = "vertx-health-check"

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-health-check:${vertxVersion()}"
  )
}

// Everything ------------------------------------------------
object vertices extends PublishSettingsModule with ScalaSettingsModule {
  def moduleDeps = Seq(
    core,
    web,
    web_client,
    web_api_contract,
    mongo,
    redis_client,
    cassandra,
    sql,
    jdbc,
    servicediscovery,
    circuitbreaker,
    config,
    mqtt,
    auth,
    auth_oauth2,
    auth_mongo,
    stomp,
    rabbitmq,
    amqpbridge,
    kafka_client,
    mail,
    consul,
    eventbus_bridge_tcp,
    healthchecks
  )
}
