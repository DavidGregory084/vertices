package vertices.codegen

import io.vertx.codegen.CodeGen
import io.vertx.codegen.ClassModel
import io.vertx.codegen.MethodKind
import io.vertx.codegen.annotations.VertxGen
import java.nio.file.Paths
import javax.annotation.processing.{ AbstractProcessor, RoundEnvironment }
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import scala.collection.JavaConverters._
import scala.compat.java8.StreamConverters._


class CodegenProcessor extends AbstractProcessor {

  override def process(annotations: java.util.Set[_ <: TypeElement], roundEnv: RoundEnvironment): Boolean = {
    val classLoader = getClass.getClassLoader

    val codegen = new CodeGen(processingEnv, roundEnv, classLoader)

    val models = codegen.getModels().toScala[List].collect {
      case entry if entry.getValue.isInstanceOf[ClassModel] =>
        entry.getKey -> entry.getValue.asInstanceOf[ClassModel]
    }.toMap

    val modelsWithHandlers = models.collect {
      case (k, v) if v.getMethods.asScala.exists { m =>
        m.getKind == MethodKind.FUTURE || m.getKind == MethodKind.HANDLER
      } => v
    }.toList

    val outPath = Paths.get(processingEnv.getOptions.get("codegen.output.dir"))

    modelsWithHandlers.foreach { mdl =>
      Codegen.generate(modelsWithHandlers.map(_.getFqn), outPath, mdl)
    }

    true
  }

  override def getSupportedAnnotationTypes: java.util.Set[String] = {
    val set = new java.util.HashSet[String]()
    set.add(classOf[VertxGen].getName)
    set
  }

  override def getSupportedOptions: java.util.Set[String] = {
    val set = new java.util.HashSet[String]()
    set.add("codegen.output.dir")
    set
  }

  override def getSupportedSourceVersion: SourceVersion = SourceVersion.RELEASE_8
}
