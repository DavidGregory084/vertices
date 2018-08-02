package vertices.codegen

import javax.annotation.processing._
import javax.lang.model.element._

object Codegen {
  def generate(processingEnv: ProcessingEnvironment, roundEnv: RoundEnvironment, element: Element) = {
    println((processingEnv, roundEnv, element))
  }
}
