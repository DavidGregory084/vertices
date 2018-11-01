import mill._, scalalib._, contrib.tut._, modules.Util

import ammonite.ops._
import coursier.maven.MavenRepository

trait ScalaSettingsModule extends ScalaModule {
  override def repositories = {
    super.repositories :+ MavenRepository("https://oss.sonatype.org/content/repositories/snapshots")
  }

  def scalaVersion = "2.12.4"

  def scalacOptions = Seq(
    // Common options
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:implicitConversions",     // Allow definition of implicit functions called views
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
    "-Xfuture",                          // Turn on future language features.
    "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ywarn-dead-code",                  // Warn when dead code is identified.
    "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
    "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen",              // Warn when numerics are widened.
    // "-Ywarn-value-discard",              // Warn when non-Unit expression results are unused.
    // ^ too common in Java code
    // scalaVersion >= 2.12
    "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
    // "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
    // "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
    // "-Ywarn-unused:locals",              // Warn if a local definition is unused.
    // "-Ywarn-unused:params",              // Warn if a value parameter is unused.
    // "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
    // "-Ywarn-unused:privates",            // Warn if a private member is unused.
    "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
    // scalaVersion >= 2.11
    "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
    "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
    "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
    "-Xlint:option-implicit",            // Option.apply used implicit view.
    "-Xlint:package-object-classes",     // Class or object defined in package object.
    "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match",              // Pattern match may not be typesafe.
    "-Ywarn-infer-any",                   // Warn when a type argument is inferred to be `Any`.
    // Partial unification
    "-Ypartial-unification"
  )

  def vertxVersion = T { "3.6.0-SNAPSHOT" }

  object test extends Tests {
    def ivyDeps = Agg(ivy"org.scalatest::scalatest:3.0.5", ivy"org.scalacheck::scalacheck:1.14.0")
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}

object codegen extends ScalaSettingsModule {
  def vertxDocgenVersion = T { "0.9.2" }
  def nettyVersion = T { "4.1.19.Final" }
  def jacksonVersion = T { "2.9.5" }
  def log4jVersion = T { "1.2.17" }
  def slf4jVersion = T { "1.7.21" }
  def log4j2Version = T { "2.8.2" }

  def ivyDeps = T {
    val vertxVer = vertxVersion()
    val nettyVer = nettyVersion()
    Agg(
      ivy"org.scala-lang.modules::scala-java8-compat:0.9.0",
      ivy"io.vertx:vertx-codegen:${vertxVersion()}",
      ivy"io.vertx:vertx-docgen:${vertxDocgenVersion()}",
      ivy"io.netty:netty-common:${nettyVer}",
      ivy"io.netty:netty-buffer:${nettyVer}",
      ivy"io.netty:netty-transport:${nettyVer}",
      ivy"io.netty:netty-handler:${nettyVer}",
      ivy"io.netty:netty-handler-proxy:${nettyVer}",
      ivy"io.netty:netty-codec-http:${nettyVer}",
      ivy"io.netty:netty-codec-http2:${nettyVer}",
      ivy"io.netty:netty-resolver:${nettyVer}",
      ivy"io.netty:netty-resolver-dns:${nettyVer}",
      ivy"io.netty:netty-transport-native-epoll:${nettyVer}",
      ivy"io.netty:netty-transport-native-kqueue:${nettyVer}",
      ivy"org.slf4j:slf4j-api:${slf4jVersion()}",
      ivy"log4j:log4j:${log4jVersion()}",
      ivy"org.apache.logging.log4j:log4j-api:${log4j2Version()}",
      ivy"org.apache.logging.log4j:log4j-core:${log4j2Version()}"
    )
  }
}

trait VertxCodegen extends ScalaSettingsModule {
  def vertxModules: T[Agg[String]]

  def vertxSourceDeps = T {
    val vertxVer = vertxVersion()
    vertxModules().map { id =>
      ivy"io.vertx:${id}:${vertxVer}"
    }
  }

  def vertxSourceJars = T {
    Lib.resolveDependencies(
      repositories,
      Lib.depToDependencyJava(_),
      vertxSourceDeps().seq,
      sources = true
    ).map(_.filter { p =>
      p.path.last.startsWith("vertx") &&
      p.path.last.endsWith(s"-sources.jar") &&
      vertxModules().exists(p.path.last.contains)
    }.map(_.path))
  }

  def vertxSources = T.sources {
    vertxSourceJars().foreach { path =>
      Util.unpackZip(path)(T.ctx().dest)
    }

    rm(T.ctx().dest / 'unpacked / 'io / 'vertx / 'groovy)
    rm(T.ctx().dest / 'unpacked / 'io / 'vertx / 'reactivex)
    rm(T.ctx().dest / 'unpacked / 'io / 'vertx / 'rxjava)

    ls.rec(T.ctx().dest / 'unpacked / 'io).filter(p => p.isDir && p.name == "impl").foreach(rm)

    Seq(PathRef(T.ctx().dest / 'unpacked / 'io))
  }

  def generatedSourcesPath = T { millSourcePath / 'generated }

  def generate = T.sources {
    val javaSources = Agg.from(
      for {
        root <- vertxSources()
        if exists(root.path)
        path <- (if (root.path.isDir) ls.rec(root.path) else Seq(root.path))
        if path.isFile && path.ext == "java"
      } yield path
    )

    if (generatedSourcesPath().toIO.exists)
      ls(generatedSourcesPath()).foreach(rm)
    else
      mkdir(generatedSourcesPath())

    val processorOptions = Seq(
      "-processor", "vertices.codegen.CodegenProcessor",
      s"-Acodegen.output.dir=${generatedSourcesPath().toIO.toString}"
    )

    zincWorker.worker().compileJava(
      upstreamCompileOutput(),
      javaSources,
      codegen.runClasspath().map(_.path) ++ compileClasspath().map(_.path),
      javacOptions() ++ processorOptions
    )

    Seq(PathRef(generatedSourcesPath()))
  }

  override def generatedSources = T { super.generatedSources() ++ generate() }
}

object core extends VertxCodegen with TutModule {
  def tutVersion = "0.6.9"
  def tutTargetDirectory = millSourcePath / up

  def vertxModules = Agg("vertx-core")

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-core:${vertxVersion()}",
    ivy"io.vertx:vertx-reactive-streams:${vertxVersion()}",
    ivy"io.monix::monix:3.0.0-RC1",
    ivy"com.chuusai::shapeless:2.3.3"
  )
}

object config extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModules = Agg("vertx-config")

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-config:${vertxVersion()}",
  )
}

object auth extends VertxCodegen {
  def moduleDeps = Seq(core)

  def vertxModules = Agg("vertx-auth-common")

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-auth-common:${vertxVersion()}",
    ivy"io.vertx:vertx-auth-htdigest:${vertxVersion()}",
    ivy"io.vertx:vertx-auth-jwt:${vertxVersion()}",
    ivy"io.vertx:vertx-auth-oauth2:${vertxVersion()}"
  )
}

object web extends VertxCodegen {
  def moduleDeps = Seq(core, auth)

  def vertxModules = Agg("vertx-web")

  def ivyDeps = Agg(
    ivy"io.vertx:vertx-core:${vertxVersion()}",
    ivy"io.vertx:vertx-web:${vertxVersion()}",
    ivy"io.vertx:vertx-web-common:${vertxVersion()}",
    ivy"io.vertx:vertx-bridge-common:${vertxVersion()}"
  )
}
