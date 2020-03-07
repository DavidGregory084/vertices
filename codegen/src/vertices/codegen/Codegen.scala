package vertices.codegen

import io.vertx.codegen._
import io.vertx.codegen.doc._
import io.vertx.codegen.`type`._
import java.lang.{ String, System }
import java.util.{ HashSet, List }
import java.util.stream.{ Collectors, Stream }
import scala.{ Array, Boolean, Char, Int, Option, StringContext }
import scala.Predef.augmentString

object Codegen {
  val excludedMethods = {
    val excluded = new HashSet[String]()
    // excluded.add("addInterceptor")
    // excluded.add("removeInterceptor")
    // excluded.add("pipe")
    // excluded.add("pipeTo")
    // excluded.add("redirectHandler")
    excluded
  }

  def generate(model: ClassModel): String = {
    def sanitiseName(name: String) = name
      .replaceFirst("^type$", "`type`")
      .replaceFirst("^object$", "`object`")

    def scalaParamTypeName(typ: TypeInfo) = {
      if (typ.getKind == ClassKind.STRING)
        "String"
      else if (typ.getKind == ClassKind.BOXED_PRIMITIVE)
        typ.getName
      else {
        typ.getSimpleName.replace("<", "[").replace(">", "]")
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

    def typeParameters(tparams: List[_ <: TypeParamInfo]) = {
      if (tparams.isEmpty)
        ""
      else {
        "[" +
        tparams.stream()
          .map(_.getName)
          .collect(Collectors.joining(", ")) +
        "]"
      }

    }

    def methodParameters(kind: MethodKind, params: List[ParamInfo]) = {
      val parms = params.stream().map { param =>
        sanitiseName(param.getName) + ": " + scalaTypeName(param.getType)
      }

      if (kind == MethodKind.FUTURE)
        parms.limit(params.size - 1L).collect(Collectors.joining(", "))
      else
        parms.collect(Collectors.joining(", "))
    }

    def handlerParam(kind: MethodKind, params: List[ParamInfo], tparam: Boolean = false) = {
      if (kind != MethodKind.FUTURE)
        ""
      else {
        val handlerParm = params
          .stream()
          .reduce((_, p) => p)
          .get()
          .getType
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
      val handlerParm = params
        .stream()
        .reduce((_, p) => p)
        .get()
        .getType
        .asInstanceOf[ParameterizedTypeInfo].getArg(0)
        .asInstanceOf[ParameterizedTypeInfo].getArg(0)

      if (handlerParm.getName == "java.lang.Void")
        ".map(_ => ())"
      else if (handlerParm.getKind == ClassKind.BOXED_PRIMITIVE)
        s".map(out => out: ${scalaTypeName(handlerParm)})"
      else ""
    }

    def javaDocToScalaDoc(indent: Int)(doc: Doc): String = {
      val sep = System.lineSeparator

      val spaces = new String(new Array[Char](indent)).replace("\u0000", " ")

      spaces + "/**" + sep +
      Stream.of(doc.toString.split("\\r?\\n"): _*)
        .map(spaces  + " * " + _)
        .collect(Collectors.joining(sep)) + sep + spaces + " */"
    }

    def instanceMethods(methods: List[MethodInfo]) = methods.stream().map { method =>
      val tparams = method.getTypeParams
      val tparamString = typeParameters(tparams)
      val params = method.getParams
      val kind = method.getKind
      val handledParam = handlerParam(kind, params, tparam = true)
      val retType = returnType(kind, params, method.getReturnType)

      val scalaDoc = Option(method.getDoc)
        .map(javaDocToScalaDoc(4))
        .getOrElse("")

      val paramNames = params.stream()
        .map(p => sanitiseName(p.getName))
        .collect(Collectors.joining(", "))

      val lastParamName = params.stream()
        .reduce((_, p) => p)
        .get()
        .getName

      s"""
      |${scalaDoc}
      |    def ${sanitiseName(method.getName + "L")}${tparamString}(${methodParameters(kind, params)}): ${retType} =
      |      Task.handle[${handledParam}] { ${lastParamName} =>
      |        target.${sanitiseName(method.getName)}(${paramNames})
      |      }${taskConversions(params)}
      """.trim.stripMargin
    }.collect(Collectors.joining(System.lineSeparator + System.lineSeparator))

    def staticMethods(tpNme: String, methods: List[MethodInfo]) = methods.stream().map { method =>
      val tparams = method.getTypeParams
      val tparamString = typeParameters(tparams)
      val params = method.getParams
      val kind = method.getKind
      val handledParam = handlerParam(kind, params, tparam = true)
      val retType = returnType(kind, params, method.getReturnType)

      val scalaDoc = Option(method.getDoc)
        .map(javaDocToScalaDoc(4))
        .getOrElse("")

      val paramNames = params.stream()
        .map(p => sanitiseName(p.getName))
        .map(_.toString)
        .collect(Collectors.joining(", "))

      val lastParamName = params.stream()
        .reduce((_, p) => p)
        .get()
        .getName

      s"""
      |${scalaDoc}
      |    def ${sanitiseName(method.getName + "L")}${tparamString}(${methodParameters(kind, params)}): ${retType} =
      |      Task.handle[${handledParam}] { ${lastParamName} =>
      |        ${tpNme}.${sanitiseName(method.getName)}(${paramNames})
      |      }${taskConversions(params)}
      """.trim.stripMargin
    }.collect(Collectors.joining(System.lineSeparator + System.lineSeparator))

    val tp = model.getType

    val tpNme = tp.getSimpleName()
    val tparams = model.getTypeParams
    val anyVal = if (tparams.isEmpty) "extends AnyVal " else " "
    val tparamString = typeParameters(tparams)

    val instanceHandlers = model.getInstanceMethods.stream().filter { m =>
      !m.isDeprecated && m.getKind == MethodKind.FUTURE
    }.collect(Collectors.toList())

    val staticHandlers = model.getStaticMethods.stream().filter { m =>
      !m.isDeprecated && m.getKind == MethodKind.FUTURE
    }.collect(Collectors.toList())

    val classTemplate = Option(
      s"""
      |  implicit class Vertx${tpNme}Ops${tparamString}(val target: ${tpNme}${tparamString}) ${anyVal}{
      |${instanceMethods(instanceHandlers)}
      |  }
      |
      """.trim.stripMargin
    ).filterNot(_ => instanceHandlers.isEmpty).getOrElse("")

    val objectTemplate = Option(
      s"""
      |  object ${tpNme}Functions {
      |${staticMethods(tpNme, staticHandlers)}
      |  }
      |
      """.trim.stripMargin
    ).filterNot(_ => staticHandlers.isEmpty).getOrElse("")

    classTemplate + System.lineSeparator + objectTemplate
  }
}
