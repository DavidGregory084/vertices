package vertices.codegen

import io.vertx.codegen._
import io.vertx.codegen.`type`._
import java.nio.file.{ Files, Path }
import java.nio.charset.StandardCharsets
import scala.collection.JavaConverters._

object Codegen {

  def generate(wrappedModels: List[String], outPath: Path, model: ClassModel) = {
    def newPackage(pkg: String) =
      pkg.replace("io.vertx", "vertices")

    def typeParameters(tparams: List[TypeParamInfo]) = {
      if (tparams.isEmpty)
        ""
      else
        tparams.map(_.getName).mkString("[", ", ", "]")
    }

    def methodParameters(kind: MethodKind, params: List[ParamInfo]) = {
      val parms = params.map { param =>
        param.getName + ": " + param.getType.getSimpleName.replace("<", "[").replace(">", "]")
      }

      if (kind == MethodKind.FUTURE)
        parms.init.mkString(", ")
      else
        parms.mkString(", ")
    }

    def returnType(kind: MethodKind, params: List[ParamInfo], ret: TypeInfo) = {
      if (kind == MethodKind.FUTURE) {
        params.last.getType
          .asInstanceOf[ParameterizedTypeInfo].getArg(0)
          .asInstanceOf[ParameterizedTypeInfo].getArg(0)
          .getSimpleName.replace("<", "[").replace(">", "]")
      } else {
        ret.getSimpleName.replace("<", "[").replace(">", "]")
      }
    }

    def instanceMethods(methods: List[MethodInfo]) = methods.map { method =>
      val params = method.getParams.asScala.toList
      val tparams = method.getTypeParams.asScala.toList
      val retType = returnType(method.getKind, params, method.getReturnType)
      val tparamString = typeParameters(tparams)
      val kind = method.getKind

      if (kind == MethodKind.FUTURE) {
        s"""
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType}
        """
      } else if (kind == MethodKind.HANDLER) {
        s"""
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType}
        """
      } else {
        s"""
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType}
        """
      }
    }.map(_.trim.stripMargin).mkString(System.lineSeparator * 2)

    def staticMethods(methods: List[MethodInfo]) = methods.map { method =>
      val params = method.getParams.asScala.toList
      val tparams = method.getTypeParams.asScala.toList
      val retType = returnType(method.getKind, params, method.getReturnType)
      val tparamString = typeParameters(tparams)
      val kind = method.getKind

      if (kind == MethodKind.FUTURE) {
        s"""
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType}
        """
      } else if (kind == MethodKind.HANDLER) {
        s"""
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType}
        """
      } else {
        s"""
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType}
        """
      }
    }.map(_.trim.stripMargin).mkString(System.lineSeparator * 2)

    if (!Files.exists(outPath))
      Files.createDirectories(outPath)

    if (!Files.isDirectory(outPath))
      throw new Exception(s"The output path ${outPath} is not a directory")

    val tp = model.getType
    val pkgNme = tp.getPackageName
    val tpNme = tp.getSimpleName()
    val tparams = model.getTypeParams.asScala.toList
    val anyVal = if (tparams.isEmpty) "extends AnyVal " else " "
    val tparamString = typeParameters(tparams)
    val insMethods = model.getInstanceMethods.asScala.toList
    val statMethods = model.getStaticMethods.asScala.toList

    val template = {
      s"""
      |package ${newPackage(pkgNme)}
      |
      |import ${pkgNme}.{ ${tpNme} => Java${tpNme} }
      |
      |case class ${tpNme}${tparamString}(val unwrap: Java${tpNme}${tparamString}) ${anyVal}{
      |${instanceMethods(insMethods)}
      |}
      |
      |object ${tpNme} {
      |${staticMethods(statMethods)}
      |}
      |
      """.trim.stripMargin
    }

    println(template)

    val dest = newPackage(pkgNme).split(".").foldLeft(outPath) {
      case (path, segment) => path.resolve(segment)
    }

    val destFile = dest.resolve(tpNme + ".scala")

    Files.write(destFile, template.getBytes(StandardCharsets.UTF_8))
  }
}
