package vertices.codegen

import io.vertx.codegen.CodeGen
import io.vertx.codegen.ClassModel
import io.vertx.codegen.annotations.VertxGen
import javax.annotation.processing.{ AbstractProcessor, RoundEnvironment }
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import scala.compat.java8.StreamConverters._


class CodegenProcessor extends AbstractProcessor {

  override def process(annotations: java.util.Set[_ <: TypeElement], roundEnv: RoundEnvironment): Boolean = {
    val models = new CodeGen(processingEnv, roundEnv, getClass().getClassLoader()).getModels().toScala[List].collect {
      case entry if entry.getValue.isInstanceOf[ClassModel] => entry.getKey -> entry.getValue
    }.toMap

    println(models)

    return true
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
