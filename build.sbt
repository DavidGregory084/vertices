import sbt.internal.inc.classpath.ClasspathUtilities
import scala.collection.JavaConverters._
import scala.xml.{ Elem, Node, NodeSeq }
import scala.xml.transform.{ RewriteRule, RuleTransformer }

lazy val generateAPI = taskKey[Seq[File]]("Generate Vert.x APIs")
lazy val baseName = settingKey[String]("Base package name for wrapped Vert.x APIs")
lazy val targetAPIs = settingKey[Seq[String]]("A list of Vert.x APIs to generate in this module")
lazy val wrappedAPIs = taskKey[Map[String, String]]("A mapping of all wrapped Vert.x APIs to their new names")

def baseName(name: String): Seq[Setting[_]] =
  Def.settings(baseName.in(Compile) := name)

def wrapperNames(classes: String*): Seq[Setting[_]] = Def.settings(
  targetAPIs := classes.map(baseName.in(Compile).value + "." + _),
  wrappedAPIs.in(Compile) := {
    wrappedAPIs.in(Compile).value ++ classes.map { clazz =>
      val pkgName = baseName.in(Compile).value
      val className = pkgName + "." + clazz
      val newName = "Java" + clazz.split("\\.").last
      className -> newName
    }.toMap
  }
)

lazy val codegenSettings = Def.settings(
  generateAPI.in(Compile) := {
    val cp = (fullClasspath.in(codegen, Runtime).value ++ dependencyClasspath.in(Compile).value).distinct
    // Generate into the main sources folder so that we can actually see the code
    val outDir = scalaSource.in(Compile).value
    // Use caching
    val pkgName = baseName.in(Compile).value
    val targets = targetAPIs.in(Compile).value.toArray
    val wrapped = wrappedAPIs.in(Compile).value.asJava
    FileFunction.cached(target.value / "codegen", FileInfo.hash, FileInfo.hash) { files =>
      val loader = ClasspathUtilities.toLoader(cp.map(_.data).map(_.getAbsoluteFile))
      val clazz = loader.loadClass("vertices.ApiGen")
      val constr = clazz.getConstructor(classOf[File], classOf[String], classOf[Array[String]], classOf[java.util.Map[_, _]])
      val instance = constr.newInstance(outDir, pkgName, targets, wrapped)
      val generate = clazz.getDeclaredMethod("generate")
      generate.invoke(instance).asInstanceOf[Array[File]].toSet
    }(sources.in(codegen, Compile).value.toSet).toSeq
  }
)

lazy val commonDependencies = Seq(
  "org.typelevel" %% "cats-core" % "1.0.1",
  "io.monix" %% "monix" % "3.0.0-M3",
  "io.vertx" % "vertx-core" % "3.5.1",
  "io.vertx" % "vertx-reactive-streams" % "3.5.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % Runtime,
  "org.typelevel" %% "cats-testkit" % "1.0.1" % Test
)

lazy val circeDependencies =
  "io.circe" %% "circe-derivation" % "0.9.0-M1" +: Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % "0.9.1")

lazy val commonSettings = Def.settings(
  inThisBuild(Seq(
    organization := "io.github.davidgregory084",
    organizationName := "David Gregory",
    startYear := Some(2018),
    licenses += ("Apache 2.0", url("https://www.apache.org/licenses/LICENSE-2.0.txt"))
  )),

  releaseCrossBuild := true,

  headerCreate.in(Compile) := {
    headerCreate.in(Compile).triggeredBy(compile.in(Compile)).value
  },

  headerLicense.in(Compile) := Some(HeaderLicense.ALv2("2018", "David Gregory and the Vertices project contributors")),

  coursierVerbosity := {
    val travisBuild = isTravisBuild.in(Global).value

    if (travisBuild)
      0
    else
      coursierVerbosity.value
  },

  scalacOptions ~= {
    _.filterNot(Set(
      "-Ywarn-value-discard", // This happens everywhere in Vert.x
      "-Ywarn-unused:imports", // Life is too short to get this working with code generation
      "-Ywarn-unused-import" // Same option as above but for 2.11
    ))
  }
)

lazy val publishSettings = Def.settings(
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle := true,
  publishArtifact.in(Test) := false,
  pomIncludeRepository := Function.const(false),
  autoAPIMappings := true,

  homepage := Some(url("https://github.com/DavidGregory084/vertices")),

  scmInfo := Some(ScmInfo(
    url("https://github.com/DavidGregory084/schemes"),
    "scm:git:git@github.com:DavidGregory084/schemes.git"
  )),

  developers := List(Developer(
    "DavidGregory084", "David Gregory",
    "davidgregory084@gmail.com",
    url("https://twitter.com/DavidGregory084")
  )),

  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },

  credentials ++= (for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq,

  pomPostProcess := { (node: Node) =>
    new RuleTransformer(new RewriteRule {
      override def transform(node: Node): NodeSeq = node match {
        case elem: Elem =>
          val isDependency = elem.label == "dependency"
          val isInTestScope = elem.child.exists(c => c.label == "scope" && c.text == "test")

          if (isDependency && isInTestScope)
            Nil
          else
            elem

        case _ =>
          node
      }
    }).transform(node).head
  },

  releaseProcess := {
    import ReleaseTransformations._

    Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion,
      ReleaseStep(
        action = state => state.copy(
          remainingCommands = Exec("sonatypeReleaseAll", None) +: state.remainingCommands
        ),
        enableCrossBuild = true
      ),
      pushChanges
    )
  }
)

lazy val noPublishSettings = Def.settings(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

// If you don't specify the root project explicitly sbt-header craps itself
// trying to figure out the license for the root project
lazy val vertices = project.in(file("."))
  .settings(commonSettings)
  .settings(noPublishSettings)
  .aggregate(core, config, scaffold, codegen)

lazy val core = vertxModule("core")
  .settings(commonSettings)
  .settings(codegenSettings)
  .settings(publishSettings)
  .settings(baseName("io.vertx.core"))
  .settings(wrapperNames(
    "Context",
    "Vertx",
    "WorkerExecutor",
    "datagram.DatagramSocket",
    "dns.DnsClient",
    "eventbus.EventBus",
    "file.AsyncFile",
    "file.FileSystem",
    "http.HttpClient",
    "http.HttpServer",
    "net.NetClient",
    "net.NetServer",
    "net.NetSocket",
    "shareddata.SharedData"
  )).settings(
    name := "vertices-core",
    libraryDependencies ++= {
      Seq("org.reflections" % "reflections" % "0.9.11") ++
        circeDependencies ++
        commonDependencies
    }
  )

lazy val config = vertxModule("config", core)
  .settings(commonSettings)
  .settings(codegenSettings)
  .settings(publishSettings)
  .settings(baseName("io.vertx.config"))
  .settings(wrapperNames("ConfigRetriever"))
  .settings(
    name := "vertices-config",
    libraryDependencies ++= commonDependencies ++ Seq(
      "io.vertx" % "vertx-config" % "3.5.1"
    )
  )

lazy val scaffold = project.in(file("scaffold"))
  .enablePlugins(TutPlugin)
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(
    name := "vertices-scaffold",
    scalacOptions.in(Tut) ~= filterConsoleScalacOptions,
    tutTargetDirectory := file("."),
    libraryDependencies ++= commonDependencies
  ).dependsOn(core)

lazy val codegen = project.in(file("codegen"))
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    name := "vertices-codegen",
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

def allAPIs(filter: ScopeFilter) = Def.taskDyn {
  val apis = wrappedAPIs.all(filter).value
  Def.task(apis)
}

// Declares a project which has access to information about the Vert.x APIs wrapped by each
// of its dependencies so that the code generator can reference the wrapped APIs
def vertxModule(name: String, projectDependencies: Project*): Project = {
  Project(name, file(name))
    .dependsOn(projectDependencies.map(_ % "compile"): _*)
    .settings(
      wrappedAPIs.in(Compile) := {
        allAPIs(ScopeFilter(
          inProjects(projectDependencies.map(p => p: ProjectReference): _*),
          inConfigurations(Compile)
        )).value.foldLeft(Map.empty[String, String])(_ ++ _)
      }
    )
}
