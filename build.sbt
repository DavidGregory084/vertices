lazy val core = project.in(file("core"))
  .settings(inThisBuild(Seq(
    scalaVersion := "2.12.4",

    organization := "io.github.davidgregory084",

    version := "0.1.0-SNAPSHOT",
  )))
  .settings(
    name := "vertices-core",

    scalacOptions ~= { _.filterNot(Set("-Ywarn-value-discard")) },

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
      "eu.timepit" %% "refined" % "0.8.7",
      "eu.timepit" %% "refined-cats" % "0.8.7",
      "eu.timepit" %% "refined-pureconfig" % "0.8.7",
      "com.github.pureconfig" %% "pureconfig" % "0.9.0",
      "org.reflections" % "reflections" % "0.9.11",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime,
      "org.typelevel" %% "cats-testkit" % "1.0.1" % Test,
      "eu.timepit" %% "refined-scalacheck" % "0.8.7" % Test
    )
  )
