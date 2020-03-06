package vertices.codegen

import io.vertx.codegen._
import io.vertx.codegen.`type`._
import io.vertx.codegen.annotations.VertxGen
import java.lang.{ Exception, String, System }
import java.nio.file.{ Files, Paths, Path }
import java.nio.charset.StandardCharsets
import java.util.{ ArrayList, List, HashSet, Set }
import java.util.stream.{ Collectors, Stream }
import javax.annotation.processing.{ AbstractProcessor, RoundEnvironment }
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import scala.{ Boolean, StringContext }
import scala.Predef.{ augmentString, classOf }

class CodegenProcessor extends AbstractProcessor {
  val excludedModels = {
    val excluded = new HashSet[String]()
    // excluded.add("io.vertx.core.Future")
    // excluded.add("io.vertx.core.CompositeFuture")
    // excluded.add("io.vertx.ext.bridge.BaseBridgeEvent")
    // excluded.add("io.vertx.ext.web.handler.sockjs.BridgeEvent")
    excluded
  }

  val importedTypes = new ArrayList[TypeInfo]()
  val opsClasses = new ArrayList[String]()

  def sanitisePackageName(name: String) =
    name.replaceAll("\\.type\\.", ".`type`.")

  def formatImports(importedTypes: List[TypeInfo]): String = {
    importedTypes.stream().distinct().reduce[List[String]](
      new ArrayList[String](), { (imps: List[String], next: TypeInfo) =>
        imps.add(sanitisePackageName(next.getName))
        imps
      }, { (l, r) =>
        l.addAll(r)
        l
      }).stream().map("import " + _).sorted.collect(Collectors.joining(System.lineSeparator))
  }

  override def process(annotations: Set[_ <: TypeElement], roundEnv: RoundEnvironment): Boolean = {
    val classLoader = getClass.getClassLoader

    val codegen = new CodeGen(processingEnv, roundEnv, classLoader)

    val models = codegen.getModels().filter {
      _.getValue.isInstanceOf[ClassModel]
    }.map {
      _.getValue.asInstanceOf[ClassModel]
    }

    val modelsWithHandlers = models.filter { mdl =>
      !mdl.getMethods.stream().filter { m =>
        m.getKind == MethodKind.FUTURE
      }.collect(Collectors.toList()).isEmpty
    }

    val nonExcludedModels = modelsWithHandlers.filter { m =>
      !m.isDeprecated && m.isConcrete && !excludedModels.contains(m.getFqn)
    }.collect(Collectors.toList())

    val outPath = Paths.get(processingEnv.getOptions.get("codegen.output.dir"))

    val newImports = nonExcludedModels.stream().flatMap { mdl =>
      Stream.concat(
        Stream.of(mdl.getType),
        mdl.getImportedTypes.stream()
      )
    }.distinct.collect(Collectors.toList[TypeInfo]())

    importedTypes.addAll(newImports)

    val newOpsClasses = nonExcludedModels.stream().map { mdl =>
      Codegen.generate(mdl)
    }.collect(Collectors.toList())

    opsClasses.addAll(newOpsClasses)

    if (roundEnv.processingOver()) {
      if (Files.exists(outPath) && !Files.isDirectory(outPath))
        throw new Exception(s"The output path ${outPath} is not a directory")

      val modNameSegments = processingEnv.getOptions.get("codegen.module.name").split("_")

      val modName = modNameSegments(modNameSegments.length - 1)

      val modPackage = Stream.of(modNameSegments: _*)
        .limit(modNameSegments.length - 1L)
        .collect(Collectors.joining("."))

      val modPackageStatement =
        if (modPackage.isEmpty()) "" else s"package ${modPackage}"

      val dest = Stream.of(modNameSegments: _*).reduce[Path](
        outPath.resolve("vertices"),
        (path, segment) => path.resolve(segment),
        (l, r) => l.resolve(r)
      )

      val destFile = dest.resolve(s"package.scala")

      if (!Files.exists(dest))
        Files.createDirectories(dest)

      Files.deleteIfExists(destFile)

      val template = s"""
      |package vertices
      |${modPackageStatement}
      |
      |import monix.eval.Task
      |${formatImports(importedTypes)}
      |
      |package object ${modName} {
      |${opsClasses.stream().collect(Collectors.joining(System.lineSeparator))}
      |}
      """.trim.stripMargin

      Files.write(destFile, template.getBytes(StandardCharsets.UTF_8))
    }

    true
  }

  override def getSupportedAnnotationTypes: Set[String] = {
    val set = new HashSet[String]()
    set.add(classOf[VertxGen].getName)
    set
  }

  override def getSupportedOptions: Set[String] = {
    val set = new HashSet[String]()
    set.add("codegen.module.name")
    set.add("codegen.output.dir")
    set
  }

  override def getSupportedSourceVersion: SourceVersion = SourceVersion.RELEASE_8
}
