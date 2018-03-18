import sbt.internal.inc.classpath.ClasspathUtilities

lazy val generateAPI = taskKey[Seq[File]]("Generate Vert.x APIs")

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
      "org.reflections" % "reflections" % "0.9.11",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime,
      "org.typelevel" %% "cats-testkit" % "1.0.1" % Test,
    ),

    generateAPI.in(Compile) := {
      val cp = fullClasspath.in(codegen, Runtime).value
      // Generate into the main sources folder so that we can actually see the code
      val outDir = scalaSource.in(Compile).value
      // Use caching
      FileFunction.cached(target.value / "codegen", FileInfo.hash, FileInfo.hash) { files =>
        val loader = ClasspathUtilities.toLoader(cp.map(_.data).map(_.getAbsoluteFile))
        val clazz = loader.loadClass("vertices.ApiGen")
        val instance = clazz.getConstructor(classOf[File]).newInstance(outDir)
        val generate = clazz.getDeclaredMethod("generate")
        generate.invoke(instance).asInstanceOf[Array[File]].toSet
      }(sources.in(codegen, Compile).value.toSet).toSeq
    },

    sourceGenerators.in(Compile) += generateAPI.in(Compile).taskValue,

    // Otherwise SBT adds the generated sources to the classpath twice
    managedSources.in(Compile) := Nil
  )

lazy val codegen = project.in(file("codegen"))
  .settings(
    name := "vertices-codegen",

    scalacOptions ~= { _.filterNot(Set("-Ywarn-value-discard")) },

    libraryDependencies ++= Seq(
      "com.geirsson" %% "scalafmt-core" % "1.4.0",
      "com.google.guava" % "guava" % "24.1-jre",
      "com.github.pathikrit" %% "better-files" % "3.4.0",
      "io.monix" %% "monix" % "3.0.0-M3",
      "io.vertx" % "vertx-core" % "3.5.1",
      "io.vertx" % "vertx-reactive-streams" % "3.5.1",
      "org.typelevel" %% "cats-testkit" % "1.0.1" % Test,
    )
  )
