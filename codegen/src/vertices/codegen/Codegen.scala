package vertices.codegen

import io.vertx.codegen._
import io.vertx.codegen.`type`._
import java.nio.file.{ Files, Path }
import java.nio.charset.StandardCharsets
import scala.collection.JavaConverters._

object Codegen {
  val excludedMethods = Set(
    "addInterceptor",
    "removeInterceptor"
  )

  def generate(wrappedModels: List[String], outPath: Path, model: ClassModel) = {
    def newPackage(pkg: String) =
      pkg.replace("io.vertx", "vertices")

    def sanitiseName(name: String) =
      name.replace("type", "`type`")

    def scalaParamTypeName(typ: TypeInfo) = {
      if (typ.getKind == ClassKind.STRING)
        "String"
      else if (typ.getKind == ClassKind.BOXED_PRIMITIVE)
        typ.getName
      else {
        val typeName = typ.getSimpleName.replace("<", "[").replace(">", "]")
        val raw = if (typ.isInstanceOf[ParameterizedTypeInfo]) typ.getRaw else typ
        if (wrappedModels.contains(raw.getName))
          "Java" + typeName
        else
          typeName
      }
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

    def taskConversions(params: List[ParamInfo]) = {
      val handlerParm = params.last.getType
        .asInstanceOf[ParameterizedTypeInfo].getArg(0)
        .asInstanceOf[ParameterizedTypeInfo].getArg(0)

      val rawHandlerParm =
        if (handlerParm.isInstanceOf[ParameterizedTypeInfo])
          handlerParm.getRaw
        else
          handlerParm

      if (handlerParm.getName == "java.lang.Void")
        ".map(_ => ())"
      else if (handlerParm.getKind == ClassKind.BOXED_PRIMITIVE)
        s".map(out => out: ${scalaTypeName(handlerParm)})"
      else if (wrappedModels.contains(rawHandlerParm.getName))
        s".map(out => ${scalaTypeName(handlerParm)}(out))"
      else ""
    }

    def miscConversions(retType: TypeInfo) = {
      val rawType = if (retType.isInstanceOf[ParameterizedTypeInfo]) retType.getRaw else retType

      if (rawType.getName == ClassModel.VERTX_READ_STREAM) {

        val typeArg = retType.asInstanceOf[ParameterizedTypeInfo].getArg(0)
        val rawTypeArg = if (typeArg.isInstanceOf[ParameterizedTypeInfo]) typeArg.getRaw else typeArg

        if (wrappedModels.contains(rawTypeArg.getName))
          s".map(${typeArg.getSimpleName}.apply)"
        else
          ""

      } else {
        ""
      }
    }

    def paramConversions(p: ParamInfo) = {
      val paramType = p.getType

      val rawParamType =
        if (paramType.isInstanceOf[ParameterizedTypeInfo])
          paramType.getRaw
        else
          paramType

      def handlerConversions(argName: String, t: TypeInfo) = {
        val raw =
          if (t.isInstanceOf[ParameterizedTypeInfo])
            t.getRaw
          else
            t

        if (wrappedModels.contains(raw.getName))
          s"${scalaTypeName(t)}(${argName})"
        else if (t.getKind == ClassKind.BOXED_PRIMITIVE)
          s"${argName}: ${scalaTypeName(t)}"
        else
          argName
      }

      if (paramType.getKind == ClassKind.HANDLER) {
        val typeArg = paramType.asInstanceOf[ParameterizedTypeInfo].getArg(0)
        if (typeArg.getKind != ClassKind.ASYNC_RESULT) {
          val argName = "in"
          val conversions = handlerConversions(argName, typeArg)
          if (conversions == argName)
            sanitiseName(p.getName)
          else
            s"""${sanitiseName(p.getName)}.contramap((${argName}: ${scalaParamTypeName(typeArg)}) => ${conversions})"""
        }
      } else {
        sanitiseName(p.getName)
      }
    }

    def instanceMethods(methods: List[MethodInfo]) = methods.map { method =>
      val tparams = method.getTypeParams.asScala.toList
      val tparamString = typeParameters(tparams)
      val params = method.getParams.asScala.toList
      val kind = method.getKind
      val handledParam = handlerParam(kind, params, tparam = true)
      val retType = returnType(kind, params, method.getReturnType)

      val rawReturnType =
        if (method.getReturnType.isInstanceOf[ParameterizedTypeInfo])
          method.getReturnType.getRaw
        else
          method.getReturnType

      if (kind == MethodKind.FUTURE) {
        val paramNames = params.map(p => sanitiseName(p.getName)).mkString(", ")
        s"""
        |  // Async handler method
        |  def ${sanitiseName(method.getName)}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    Task.handle[${handledParam}] { ${params.last.getName} =>
        |      unwrap.${sanitiseName(method.getName)}(${paramNames})
        |    }${taskConversions(params)}
        """
      } else if (wrappedModels.contains(rawReturnType.getName)) {
        val paramNames = params.map(paramConversions).mkString(", ")
        s"""
        |  // Wrapper method
        |  def ${sanitiseName(method.getName)}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    ${scalaTypeName(method.getReturnType)}(unwrap.${sanitiseName(method.getName)}(${paramNames}))${miscConversions(method.getReturnType)}
        """
      } else {
        val paramNames = params.map(paramConversions).mkString(", ")
        s"""
        |  // Standard method
        |  def ${sanitiseName(method.getName)}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    unwrap.${sanitiseName(method.getName)}(${paramNames})${miscConversions(method.getReturnType)}
        """
      }
    }.map(_.trim.stripMargin).mkString(System.lineSeparator * 2)

    def staticMethods(tpNme: String, methods: List[MethodInfo]) = methods.map { method =>
      val tparams = method.getTypeParams.asScala.toList
      val tparamString = typeParameters(tparams)
      val params = method.getParams.asScala.toList
      val kind = method.getKind
      val handledParam = handlerParam(kind, params, tparam = true)
      val retType = returnType(kind, params, method.getReturnType)

      val rawReturnType =
        if (method.getReturnType.isInstanceOf[ParameterizedTypeInfo])
          method.getReturnType.getRaw
        else
          method.getReturnType

      if (kind == MethodKind.FUTURE) {
        val paramNames = params.map(p => sanitiseName(p.getName)).mkString(", ")
        s"""
        |  // Async handler method
        |  def ${sanitiseName(method.getName)}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    Task.handle[${handledParam}] { ${params.last.getName} =>
        |      Java${tpNme}.${sanitiseName(method.getName)}(${paramNames})
        |    }${taskConversions(params)}
        """
      } else if (wrappedModels.contains(rawReturnType.getName)) {
        val paramNames = params.map(paramConversions).mkString(", ")
        s"""
        |  // Wrapper method
        |  def ${sanitiseName(method.getName)}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    ${method.getReturnType.getSimpleName}(Java${tpNme}.${sanitiseName(method.getName)}(${paramNames}))
        """
      } else {
        val paramNames = params.map(paramConversions).mkString(", ")
        s"""
        |  // Standard method
        |  def ${sanitiseName(method.getName)}${tparamString}(${methodParameters(kind, params)}): ${retType} =
        |    Java${tpNme}.${sanitiseName(method.getName)}(${paramNames})
        """
      }
    }.map(_.trim.stripMargin).mkString(System.lineSeparator * 2)

    def imports(importedTypes: List[TypeInfo]) = {
      importedTypes.foldLeft(Vector.empty[String]) {
        case (imps, next) =>
          val nm = next.getSimpleName
          val pkg = Helper.getPackageName(next.getName)
          val modelNm = model.getType.getSimpleName()
          val modelPkg = model.getType.getPackageName
          if (wrappedModels.contains(next.getName)) {
            val wrappedType = pkg + s".{ ${nm} => Java${nm} }"
            val newType = newPackage(pkg) + "." + next.getSimpleName
            if (nm != modelNm && pkg != modelPkg)
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

    val deduplicatedInsMethods = insMethods.filterNot { m =>
      excludedMethods.contains(m.getName)
    }.filter { m =>
      if (m.getKind == MethodKind.FUTURE)
        true
      else !insMethods.exists { om =>
        m.getName == om.getName &&
          om.getKind == MethodKind.FUTURE && {
          val thisParams =
            m.getParams.asScala.toList
          val otherParams =
            om.getParams.asScala.toList.init

          thisParams.map(_.getType) == otherParams.map(_.getType)
        }
      }
    }

    val statMethods = model.getStaticMethods.asScala.toList

    val deduplicatedStatMethods = statMethods.filterNot { m =>
      excludedMethods.contains(m.getName)
    }.filter { m =>
      if (m.getKind == MethodKind.FUTURE)
        true
      else !statMethods.exists { om =>
        m.getName == om.getName && {
          val thisParams =
            m.getParams.asScala.toList
          val otherParams =
            if (om.getKind == MethodKind.FUTURE)
              om.getParams.asScala.toList.init
            else
              om.getParams.asScala.toList

          thisParams.map(_.getType) == otherParams.map(_.getType)
        }
      }
    }

    val implicits = {
      val tparams = typeParameters(model.getTypeParams.asScala.toList)
      val modelNm = model.getType.getSimpleName().capitalize
      s"""
      |  implicit def java${modelNm}ToVertices${modelNm}${tparams}(j: Java${modelNm}${tparams}): ${modelNm}${tparams} = apply(j)
      |  implicit def vertices${modelNm}ToJava${modelNm}${tparams}(v: ${modelNm}${tparams}): Java${modelNm}${tparams} = v.unwrap
      |""".trim.stripMargin
    }

    val template = {
      val classTemplate =
        s"""
        |package vertices
        |package ${newPackage(pkgNme).replace("vertices.", "")}
        |
        |import cats.implicits._
        |${imports(importedTps)}
        |import monix.eval.Task
        |
        |import scala.language.implicitConversions
        |
        |case class ${tpNme}${tparamString}(val unwrap: Java${tpNme}${tparamString}) ${anyVal}{
        |${instanceMethods(deduplicatedInsMethods)}
        |}
        |
        """.trim.stripMargin

      val objectTemplate =
        s"""
        |object ${tpNme} {
        |${implicits}
        |${staticMethods(tpNme, statMethods)}
        |}
        |
        """.trim.stripMargin

      classTemplate + objectTemplate
    }

    val dest = newPackage(pkgNme).split(".").foldLeft(outPath) {
      case (path, segment) => path.resolve(segment)
    }

    val destFile = dest.resolve(tpNme + ".scala")

    Files.write(destFile, template.getBytes(StandardCharsets.UTF_8))
  }
}
