import sbt.internal.inc.classpath.ClasspathUtilities

lazy val CompileOnly = config("compileonly")
lazy val generate = taskKey[Unit]("Generate Vert.x API definitions")

lazy val core = project.in(file("core"))
  .settings(inThisBuild(Seq(
    scalaVersion := "2.12.4",

    organization := "io.github.davidgregory084",

    version := "0.1.0-SNAPSHOT",
  )))
  .settings(
    name := "vertices-core",

    scalacOptions ~= { _.filterNot(Set("-Ywarn-value-discard")) },

    connectInput.in(run) := true,

    assemblyMergeStrategy.in(assembly) := {
      case PathList(ps @ _*) if ps.last == "io.netty.versions.properties" => MergeStrategy.first
      case otherwise => assemblyMergeStrategy.in(assembly).value(otherwise)
    },

    libraryDependencies ++= "io.circe" %% "circe-derivation" % "0.9.0-M1" +: Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % "0.9.1"),

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.0.1",
      "io.monix" %% "monix" % "3.0.0-M3",
      "io.vertx" % "vertx-core" % "3.5.1",
      "io.vertx" % "vertx-reactive-streams" % "3.5.1",
      // "eu.timepit" %% "refined" % "0.8.7",
      // "eu.timepit" %% "refined-cats" % "0.8.7",
      // "eu.timepit" %% "refined-pureconfig" % "0.8.7",
      // "com.github.pureconfig" %% "pureconfig" % "0.9.0",
      "org.reflections" % "reflections" % "0.9.11",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime,
      "org.typelevel" %% "cats-testkit" % "1.0.1" % Test,
      // "eu.timepit" %% "refined-scalacheck" % "0.8.7" % Test
    ),

    ivyConfigurations += CompileOnly.hide,

    sourceGenerators.in(Compile) += Def.task {
      val cp = fullClasspath.in(codegen, Runtime).value
      val outDir = sourceManaged.in(Compile).value
      FileFunction.cached(target.value / "codegen") { files =>
        val loader = ClasspathUtilities.toLoader(cp.map(_.data).map(_.getAbsoluteFile))
        val clazz = loader.loadClass("vertices.ApiGen")
        val instance = clazz.getConstructor(classOf[File]).newInstance(outDir)
        val generate = clazz.getDeclaredMethod("generate")
        generate.invoke(instance).asInstanceOf[Array[File]].toSet
      }(sources.in(codegen, Compile).value.toSet).toSeq
    }.taskValue
  ).dependsOn(codegen % CompileOnly)

lazy val codegen = project.in(file("codegen"))
  .configs(CompileOnly)
  .settings(
    name := "vertices-codegen",

    scalacOptions ~= { _.filterNot(Set("-Ywarn-value-discard")) },

    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "1.0.1",
      "com.geirsson" %% "scalafmt-core" % "1.4.0",
      "com.google.guava" % "guava" % "24.1-jre",
      "com.github.pathikrit" %% "better-files" % "3.4.0",
      "io.monix" %% "monix" % "3.0.0-M3",
      "io.vertx" % "vertx-core" % "3.5.1",
      "io.vertx" % "vertx-reactive-streams" % "3.5.1",
      "org.reflections" % "reflections" % "0.9.11",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime,
      "org.typelevel" %% "cats-testkit" % "1.0.1" % Test,
      "eu.timepit" %% "refined-scalacheck" % "0.8.7" % Test
    )
  )
