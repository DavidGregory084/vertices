package vertices

import better.files._
import java.lang.reflect.{ Array => _, _ }
import io.vertx.core.{ AsyncResult, Handler, Vertx }
import io.vertx.core.eventbus.EventBus
import io.vertx.core.dns.DnsClient
import io.vertx.core.file.FileSystem
import io.vertx.core.http.HttpServer
import io.vertx.core.net.NetServer
import monix.eval.Task
import org.scalafmt.Scalafmt
import org.scalafmt.config.ScalafmtConfig

object UnaryTC {
  def unapply(typ: Type): Option[(Type, Type)] = typ match {
    case parameterized: ParameterizedType if parameterized.getActualTypeArguments.length == 1 =>
      Some((parameterized.getRawType, parameterized.getActualTypeArguments.head))
    case _ => None
  }
}

class ApiGen(path: java.io.File) {
  val wrappers = Map[Class[_], String](
    classOf[Vertx] -> "JavaVertx",
    classOf[EventBus] -> "JavaEventBus",
    classOf[DnsClient] -> "JavaDnsClient",
    classOf[FileSystem] -> "JavaFileSystem",
    classOf[HttpServer] -> "JavaHttpServer",
    classOf[NetServer] -> "JavaNetServer")

  val ClassByte = classOf[Byte]
  val ClassShort = classOf[Short]
  val ClassInt = classOf[Int]
  val ClassLong = classOf[Long]
  val ClassFloat = classOf[Float]
  val ClassDouble = classOf[Double]
  val ClassBoolean = classOf[Boolean]
  val ClassChar = classOf[Char]
  val ClassBoxedByte = classOf[java.lang.Byte]
  val ClassBoxedShort = classOf[java.lang.Short]
  val ClassBoxedInt = classOf[java.lang.Integer]
  val ClassBoxedLong = classOf[java.lang.Long]
  val ClassBoxedFloat = classOf[java.lang.Float]
  val ClassBoxedDouble = classOf[java.lang.Double]
  val ClassBoxedBoolean = classOf[java.lang.Boolean]
  val ClassBoxedChar = classOf[java.lang.Character]
  val ClassObject = classOf[Object]
  val ClassVoid = Void.TYPE

  def tparams(t: Type): List[String] =
    t match {
      case t: GenericArrayType => tparams(t.getGenericComponentType)
      case t: ParameterizedType => t.getActualTypeArguments.toList.flatMap(tparams)
      case t: TypeVariable[_] => List(t.toString)
      case _ => Nil
    }

  def toScalaType(t: Type, hasActualParams: Boolean = false): String =
    t match {
      case t: GenericArrayType => s"Array[${toScalaType(t.getGenericComponentType)}]"
      case t: ParameterizedType => s"${toScalaType(t.getRawType, true)}${t.getActualTypeArguments.map(toScalaType(_)).mkString("[", ", ", "]")}"
      case t: WildcardType =>
        t.getUpperBounds.toList.filterNot(_ == classOf[Object]) match {
          case (c: Class[_]) :: Nil => s"_ <: ${c.getName}"
          case Nil => "_"
          case cs => sys.error("unhandled upper bounds: " + cs.toList)
        }
      case t: TypeVariable[_] => t.toString
      case ClassVoid => "Unit"
      case ClassByte => "Byte"
      case ClassShort => "Short"
      case ClassInt => "Int"
      case ClassLong => "Long"
      case ClassFloat => "Float"
      case ClassDouble => "Double"
      case ClassBoolean => "Boolean"
      case ClassChar => "Char"
      case ClassObject => "AnyRef"
      case ClassBoxedByte => "java.lang.Byte"
      case ClassBoxedShort => "java.lang.Short"
      case ClassBoxedInt => "java.lang.Int"
      case ClassBoxedLong => "java.lang.Long"
      case ClassBoxedFloat => "java.lang.Float"
      case ClassBoxedDouble => "java.lang.Double"
      case ClassBoxedBoolean => "java.lang.Boolean"
      case ClassBoxedChar => "java.lang.Char"
      case x: Class[_] if x.isArray => s"Array[${toScalaType(x.getComponentType)}]"
      case x: Class[_] =>
        val name = wrappers.getOrElse(x, x.getSimpleName)
        // Stupid raw types
        val tparams = x.getTypeParameters.map(_ => "_")
        if (tparams.isEmpty)
          name
        else if (hasActualParams)
          name
        else
          name + tparams.mkString("[", ", ", "]")
    }

  // This class, plus any superclasses and interfaces, "all the way up"
  def closure(c: Class[_]): List[Class[_]] =
    (c :: (Option(c.getSuperclass).toList ++ c.getInterfaces.toList).flatMap(closure)).distinct
      .filterNot(_.getName == "java.lang.AutoCloseable") // not available in jdk1.6
      .filterNot(_.getName == "java.lang.Object") // we don't want .equals, etc.

  def isAsyncHandler(param: Type): Boolean = param match {
    case UnaryTC(outer, UnaryTC(inner, _)) =>
      outer.getTypeName == "io.vertx.core.Handler" &&
        inner.getTypeName == "io.vertx.core.AsyncResult"
    case _ => false
  }

  def isNonAsyncHandler(param: Type): Boolean = param match {
    case UnaryTC(outer, UnaryTC(inner, _)) =>
      outer.getTypeName == "io.vertx.core.Handler" &&
        inner.getTypeName != "io.vertx.core.AsyncResult"
    case UnaryTC(outer, _) =>
      outer.getTypeName == "io.vertx.core.Handler"
    case _ => false
  }

  def nonHandlerParamString(method: Method): String =
    method.getGenericParameterTypes.filterNot(isAsyncHandler).map(toScalaType(_)).mkString(",")

  def shouldKeep(methods: List[Method], method: Method): Boolean = {
    val otherMethods = methods.filterNot(_ == method)
    val methodIsHandlerMethod = method.getGenericParameterTypes.exists(isAsyncHandler)
    val sameNameMethods = otherMethods.filter(_.getName == method.getName)

    val conflictingMethodExists = sameNameMethods.exists { other =>
      val otherParamTypes = nonHandlerParamString(other)
      val thisParamTypes = nonHandlerParamString(method)
      otherParamTypes == thisParamTypes
    }

    methodIsHandlerMethod || !conflictingMethodExists
  }

  def methods(c: Class[_], p: Method => Boolean): List[Method] =
    closure(c).flatMap(_.getDeclaredMethods.toList).distinct.filter(p).groupBy(_.getName).toList.flatMap {
      case (_, methods) =>
        methods.foldLeft(List.empty[Method]) {
          case (keep, method) =>
            if (!shouldKeep(methods, method))
              keep
            else
              method :: keep
        }
    }.sortBy(m => (m.getName, m.getGenericParameterTypes.map(toScalaType(_)).mkString(",")))

  def renameImport(c: Class[_]): String = {
    val sn = c.getSimpleName
    val an = wrappers.getOrElse(c, sn)
    if (sn == an) s"import ${c.getName}"
    else s"import ${c.getPackage.getName}.{ $sn => $an }"
  }

  def handlerType(method: Method): Option[Class[_]] =
    method.getParameters
      .find(param => isAsyncHandler(param.getParameterizedType))
      .map { param =>
        param.getParameterizedType match {
          case UnaryTC(_, UnaryTC(_, inner: Class[_])) =>
            inner
        }
      }

  def collectParams(typ: Type): List[Class[_]] = {
    def go(typ: Type, acc: List[Class[_]]): List[Class[_]] = {
      typ match {
        case UnaryTC(outer: Class[_], inner) =>
          go(inner, outer :: acc)
        case clazz: Class[_] =>
          clazz :: acc
        case _ =>
          acc
      }
    }
    go(typ, List.empty)
  }

  // All types referenced by all methods on A, superclasses, interfaces, etc.
  def imports[A](clazz: Class[A], except: Class[_] => Boolean): List[String] =
    (renameImport(clazz) :: methods(clazz, Function.const(true)).flatMap { m =>
      m.getReturnType :: m.getParameterTypes.toList ++ m.getGenericParameterTypes.flatMap(collectParams).filterNot(_ == classOf[AsyncResult[_]])
    }.map { t =>
      if (t.isArray) t.getComponentType else t
    }.filterNot(except).filterNot(t => t.isPrimitive || t == classOf[Object]).map { c =>
      renameImport(c)
    }).distinct.sorted

  def method(clazz: Class[_], wrappedName: String, method: Method): String = {
    val name = method.getName

    val isStatic = Modifier.isStatic(method.getModifiers)
    val isFluent = method.getReturnType == clazz

    val params = method
      .getParameters
      .filterNot(param => isAsyncHandler(param.getParameterizedType))

    val hasHandlerParameter = method.getGenericParameterTypes.exists(isAsyncHandler)

    val handlerTypeParam = method.getParameters
      .find(param => isAsyncHandler(param.getParameterizedType))
      .map { param =>
        param.getParameterizedType match {
          case UnaryTC(_, UnaryTC(_, inner)) =>
            inner
        }
      }

    val isVoidHandler = handlerTypeParam.map(_.getTypeName == "java.lang.Void").getOrElse(false)

    val returnsWrapper = wrappers.keys.toList.contains(method.getReturnType)

    val returnType =
      if (hasHandlerParameter && isVoidHandler)
        "Task[Unit]"
      else if (hasHandlerParameter && handlerTypeParam.map(_ == clazz).getOrElse(false))
        s"""Task[${clazz.getSimpleName}]"""
      else if (hasHandlerParameter)
        s"""Task[${handlerTypeParam.map(toScalaType(_)).getOrElse("")}]"""
      else if (isFluent)
        clazz.getSimpleName
      else if (returnsWrapper)
        method.getReturnType.getSimpleName
      else
        toScalaType(method.getGenericReturnType)

    val receiver = if (isStatic) wrappedName else "unwrap"

    val paramTypes = params
      .map(param => s"${param.getName}: ${toScalaType(param.getParameterizedType)}")
      .mkString(", ")

    val paramNames =
      (params.map(_.getName) ++ (if (hasHandlerParameter) Seq("handler") else Seq.empty))
        .mkString(", ")

    val typeParameterTypes =
      method.getTypeParameters.map(toScalaType(_))

    val typeParameters =
      if (typeParameterTypes.isEmpty) ""
      else typeParameterTypes.mkString("[", ", ", "]")

    val wrappedCall =
      if (hasHandlerParameter && isVoidHandler)
        s"Task.handle[Void] { handler => $receiver.$name($paramNames) }.map(_ => ())"
      else if (hasHandlerParameter && handlerTypeParam.map(_ == clazz).getOrElse(false))
        s"""Task.handle[${handlerTypeParam.map(toScalaType(_)).getOrElse("")}] { handler => $receiver.$name($paramNames) }.map(${clazz.getSimpleName}(_))"""
      else if (hasHandlerParameter)
        s"""Task.handle[${handlerTypeParam.map(toScalaType(_)).getOrElse("")}] { handler => $receiver.$name($paramNames) }"""
      else if (isFluent)
        s"${clazz.getSimpleName}($receiver.$name($paramNames))"
      else if (returnsWrapper)
        s"${method.getReturnType.getSimpleName}($receiver.$name($paramNames))"
      else
        s"$receiver.$name($paramNames)"

    s"""
    |def ${name}${typeParameters}($paramTypes): ${returnType} =
    |  $wrappedCall
    """.trim.stripMargin
  }

  def ctorMethod(clazzName: String, wrappedName: String, method: Method): String = {
    val name = method.getName

    val params = method
      .getParameters
      .filterNot(param => isAsyncHandler(param.getParameterizedType))

    val hasHandlerParameter = method.getGenericParameterTypes.exists(isAsyncHandler)

    val paramTypes = params
      .map(param => s"${param.getName}: ${toScalaType(param.getParameterizedType)}")
      .mkString(", ")

    val paramNames =
      (params.map(_.getName) ++ (if (hasHandlerParameter) Seq("handler") else Seq.empty))
        .mkString(", ")

    val returnType =
      if (hasHandlerParameter)
        s"Task[${clazzName}]"
      else
        clazzName

    val wrappedCall =
      if (hasHandlerParameter)
        s"Task.handle[${wrappedName}] { handler => $wrappedName.$name($paramNames) }.map($clazzName(_))"
      else
        s"$clazzName($wrappedName.$name($paramNames))"

    s"""
    |def $name($paramTypes): ${returnType} =
    |  $wrappedCall
    """.trim.stripMargin
  }

  def source[A](clazz: Class[A]): String = {
    val name = clazz.getSimpleName
    val oldName = toScalaType(clazz)
    val originalPkg = clazz.getPackage.getName

    def getNewPkg(orig: String) = {
      val vertxCore = "io.vertx.core"
      if (orig == vertxCore)
        ""
      else
        orig.replaceFirst(vertxCore + ".", "")
    }

    val newPkg = getNewPkg(originalPkg)

    val instanceMethods = methods(clazz, m => !Modifier.isStatic(m.getModifiers))
    val allStaticMethods = methods(clazz, m => Modifier.isStatic(m.getModifiers))
    val smartCtors = allStaticMethods.filter(m => m.getReturnType == clazz || handlerType(m).map(_ == clazz).getOrElse(false))
    val staticMethods = allStaticMethods.filterNot(smartCtors.contains)

    val newInstanceMethods = instanceMethods.map(method(clazz, oldName, _)).mkString("\n\n")
    val newSmartCtors = smartCtors.map(ctorMethod(name, oldName, _)).mkString("\n\n")
    val newStaticMethods = staticMethods.map(method(clazz, oldName, _)).mkString("\n\n")

    val allMethods = instanceMethods ++ allStaticMethods

    val hasHandlerMethods = allMethods.exists { method =>
      method.getGenericParameterTypes.exists(isAsyncHandler)
    }

    val hasNonAsyncHandlerMethods = allMethods.exists { method =>
      method.getGenericParameterTypes.exists(isNonAsyncHandler)
    }

    val noHandlersLeft = (cls: Class[_]) => cls == classOf[Handler[_]] && !hasNonAsyncHandlerMethods

    val removeWrapped = (cls: Class[_]) => wrappers.keys.toList.contains(cls) && !allMethods.exists { method =>
      method.getParameterTypes.contains(cls)
    }

    val returnedWrapperImports = allMethods
      .filter(m => wrappers.keys.toList.contains(m.getReturnType))
      .filterNot(_.getReturnType == clazz)
      .map { m =>
        val wrapperPkg = getNewPkg(m.getReturnType.getPackage.getName)
        s"import vertices.$wrapperPkg.${m.getReturnType.getSimpleName}"
      }

    val allImports = imports(clazz, cls => noHandlersLeft(cls) || removeWrapped(cls)) ++
      (if (hasHandlerMethods) Seq(renameImport(classOf[Task[_]])) else Seq.empty) ++
      returnedWrapperImports

    val sourceFile = s"""
    |package vertices
    |${if (newPkg.nonEmpty) "package " + newPkg else ""}
    |
    |${allImports.sorted.mkString("\n")}
    |
    |class $name(val unwrap: ${toScalaType(clazz)}) {
    |
    |$newInstanceMethods
    |}
    |
    |object $name {
    |  def apply(wrapped: ${toScalaType(clazz)}): ${clazz.getSimpleName} = new ${clazz.getSimpleName}(wrapped)
    |$newSmartCtors
    |
    |$newStaticMethods
    |}
    """.trim.stripMargin

    Scalafmt.format(sourceFile, ScalafmtConfig.default120).get
  }

  def generate[A](clazz: Class[A]): java.io.File = {
    println(s"Generating Vert.x API definitions for ${clazz.getName}")

    val vertxCore = "io.vertx.core"

    val packageName =
      if (clazz.getPackage.getName == vertxCore)
        ""
      else
        clazz.getPackage.getName.replaceFirst(s"$vertxCore.", "")

    val sourceFileName = clazz.getSimpleName + ".scala"

    val outDir = path.toScala

    val outPath = packageName.split("\\.").foldLeft(outDir / "vertices") {
      case (out, next) => out / next
    }

    outPath.createDirectories()

    val outFile = outPath / sourceFileName

    if (outFile.exists())
      outFile.delete()

    outFile.write(source[A](clazz))

    outFile.toJava
  }

  def generate(): Array[java.io.File] =
    wrappers.keys.toArray.map(generate(_))
}
