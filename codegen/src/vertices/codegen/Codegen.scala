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

    def sanitiseName(name: String) = name.replace("type", "`type`")

    def scalaParamTypeName(typ: TypeInfo) = {
      if (typ.getName == "byte" || typ.getName == "java.lang.Byte")
        "Byte"
      else if (typ.getName == "short" || typ.getName == "java.lang.Short")
        "Short"
      else if (typ.getName == "int" || typ.getName == "java.lang.Integer")
        "Int"
      else if (typ.getName == "long" || typ.getName == "java.lang.Long")
        "Long"
      else if (typ.getName == "float" || typ.getName == "java.lang.Float")
        "Float"
      else if (typ.getName == "double" || typ.getName == "java.lang.Double")
        "Double"
      else if (typ.getName == "boolean" || typ.getName == "java.lang.Boolean")
        "Boolean"
      else if (typ.getName == "char" || typ.getName == "java.lang.Character")
        "Char"
      else if (typ.getName == "void" || typ.getName == "java.lang.Void")
        "java.lang.Void"
      else if (typ.getKind == ClassKind.STRING)
        "String"
      else
        typ.getSimpleName
          .replace("<", "[")
          .replace(">", "]")
    }

    def scalaTypeName(typ: TypeInfo) = {
      if (typ.getName == "byte" || typ.getName == "java.lang.Byte")
        "Byte"
      else if (typ.getName == "short" || typ.getName == "java.lang.Short")
        "Short"
      else if (typ.getName == "int" || typ.getName == "java.lang.Integer")
        "Int"
      else if (typ.getName == "long" || typ.getName == "java.lang.Long")
        "Long"
      else if (typ.getName == "float" || typ.getName == "java.lang.Float")
        "Float"
      else if (typ.getName == "double" || typ.getName == "java.lang.Double")
        "Double"
      else if (typ.getName == "boolean" || typ.getName == "java.lang.Boolean")
        "Boolean"
      else if (typ.getName == "char" || typ.getName == "java.lang.Character")
        "Char"
      else if (typ.getName == "void" || typ.getName == "java.lang.Void")
        "Unit"
      else if (typ.getKind == ClassKind.STRING)
        "String"
      else
        typ.getSimpleName
          .replace("<", "[")
          .replace(">", "]")
    }

    def typeParameters(tparams: List[TypeParamInfo]) = {
      if (tparams.isEmpty)
        ""
      else
        tparams.map(_.getName).mkString("[", ", ", "]")
    }

    def methodParameters(kind: MethodKind, params: List[ParamInfo]) = {
      val parms = params.map { param =>
        sanitiseName(param.getName) + ": " + scalaTypeName(param.getType)
      }

      if (kind == MethodKind.FUTURE)
        parms.init.mkString(", ")
      else
        parms.mkString(", ")
    }

    def handlerParam(kind: MethodKind, params: List[ParamInfo], tparam: Boolean = false) = {
      if (kind != MethodKind.FUTURE)
        ""
      else {
        val handlerParm = params.last.getType
          .asInstanceOf[ParameterizedTypeInfo].getArg(0)
          .asInstanceOf[ParameterizedTypeInfo].getArg(0)
        if (tparam)
          scalaParamTypeName(handlerParm)
        else
          scalaTypeName(handlerParm)
      }
    }

    def returnType(kind: MethodKind, params: List[ParamInfo], ret: TypeInfo) = {
      if (kind == MethodKind.FUTURE) {
        s"Task[${handlerParam(kind, params)}]"
      } else {
        scalaTypeName(ret)
      }
    }

    def instanceMethods(methods: List[MethodInfo]) = methods.map { method =>
      val tparams = method.getTypeParams.asScala.toList
      val tparamString = typeParameters(tparams)
      val params = method.getParams.asScala.toList
      val paramNames = params.map(n => sanitiseName(n.getName)).mkString(", ")
      val kind = method.getKind
      val handledParam = handlerParam(kind, params, tparam = true)
      val retType = returnType(kind, params, method.getReturnType)

      if (kind == MethodKind.FUTURE) {
        s"""
        |  // Async handler method
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    Task.handle[${handledParam}] { ${params.last.getName} =>
        |      unwrap.${method.getName}(${paramNames})
        |    }
        """
      } else if (method.isFluent) {
        s"""
        |  // Fluent method
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    ${model.getType.getSimpleName()}(unwrap.${method.getName}(${paramNames}))
        """
      } else {
        s"""
        |  // Standard method
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    unwrap.${method.getName}(${paramNames})
        """
      }
    }.map(_.trim.stripMargin).mkString(System.lineSeparator * 2)

    def isFactory(tp: TypeInfo, ret: TypeInfo) = tp.getRaw == ret.getRaw

    def staticMethods(tpNme: String, methods: List[MethodInfo]) = methods.map { method =>
      val tparams = method.getTypeParams.asScala.toList
      val tparamString = typeParameters(tparams)
      val params = method.getParams.asScala.toList
      val paramNames = params.map(n => sanitiseName(n.getName)).mkString(", ")
      val kind = method.getKind
      val handledParam = handlerParam(kind, params, tparam = true)
      val retType = returnType(kind, params, method.getReturnType)

      if (kind == MethodKind.FUTURE) {
        s"""
        |  // Async handler method
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    Task.handle[${handledParam}] { ${params.last.getName} =>
        |      Java${tpNme}.${method.getName}(${paramNames})
        |    }
        """
      } else if (isFactory(model.getType, method.getReturnType)) {
        s"""
        |  // Factory method
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    ${tpNme}(Java${tpNme}.${method.getName}(${paramNames}))
        """
      } else {
        s"""
        |  // Standard method
        |  def ${method.getName}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    Java${tpNme}.${method.getName}(${paramNames})
        """
      }
    }.map(_.trim.stripMargin).mkString(System.lineSeparator * 2)

    def imports(importedTypes: List[TypeInfo]) = {
      importedTypes.foldLeft(Vector.empty[String]) {
        case (imps, next) =>
          val nm = next.getSimpleName
          val pkg = next.getName.replace(nm, "")
          if (wrappedModels.contains(next.getName)) {
            val wrappedType = pkg + s"{ ${nm} => Java${nm} }"
            val newType = newPackage(pkg) + next.getSimpleName
            if (nm != model.getType.getSimpleName() && !pkg.startsWith(model.getType.getPackageName))
              imps :+ wrappedType :+ newType
            else
              imps :+ wrappedType
          } else {
            imps :+ next.getName
          }
      }.map("import " + _).sorted.mkString(System.lineSeparator)
    }

    if (!Files.exists(outPath))
      Files.createDirectories(outPath)

    if (!Files.isDirectory(outPath))
      throw new Exception(s"The output path ${outPath} is not a directory")

    val tp = model.getType
    val importedTps = (tp :: model.getImportedTypes.asScala.toList).distinct
    val pkgNme = tp.getPackageName
    val tpNme = tp.getSimpleName()
    val tparams = model.getTypeParams.asScala.toList
    val anyVal = if (tparams.isEmpty) "extends AnyVal " else " "
    val tparamString = typeParameters(tparams)
    val insMethods = model.getInstanceMethods.asScala.toList
    val statMethods = model.getStaticMethods.asScala.toList

    val template = {
      val classTemplate =
        s"""
        |package vertices
        |package ${newPackage(pkgNme).replace("vertices.", "")}
        |
        |${imports(importedTps)}
        |import monix.eval.Task
        |
        |case class ${tpNme}${tparamString}(val unwrap: Java${tpNme}${tparamString}) ${anyVal}{
        |${instanceMethods(insMethods)}
        |}
        |
        """.trim.stripMargin

      val objectTemplate =
        s"""
        |object ${tpNme} {
        |${staticMethods(tpNme, statMethods)}
        |}
        |
        """.trim.stripMargin

      if (statMethods.nonEmpty)
        classTemplate + objectTemplate
      else
        classTemplate
    }

    println(template)

    val dest = newPackage(pkgNme).split(".").foldLeft(outPath) {
      case (path, segment) => path.resolve(segment)
    }

    val destFile = dest.resolve(tpNme + ".scala")

    Files.write(destFile, template.getBytes(StandardCharsets.UTF_8))
  }
}
