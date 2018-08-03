// package vertices.codegen

// import javax.annotation.processing._
// import javax.lang.model.element._
// import javax.lang.model.element.Modifier._
// import javax.lang.model.element.ElementKind._
// import javax.lang.model.`type`._
// import javax.lang.model.`type`.TypeKind._
// import javax.lang.model.util.ElementKindVisitor8
// import scala.collection.JavaConverters._

// case class TypeInfo(name: Symbol, params: Vector[TypeInfo] = Vector.empty)

// case class ParamInfo(name: Symbol, typ: TypeInfo)

// sealed trait MethodInfo {
//   def name: Symbol
//   def typeParams: Vector[TypeInfo]
//   def params: Vector[ParamInfo]
//   def returnType: TypeInfo
//   def isStatic: Boolean
// }

// case class StandardMethodInfo(
//   name: Symbol,
//   typeParams: Vector[TypeInfo],
//   params: Vector[ParamInfo],
//   returnType: TypeInfo,
//   isStatic: Boolean
// ) extends MethodInfo

// case class AsyncHandlerMethodInfo(
//   name: Symbol,
//   typeParams: Vector[TypeInfo],
//   params: Vector[ParamInfo],
//   asyncHandlerType: TypeInfo,
//   returnType: TypeInfo,
//   isStatic: Boolean
// ) extends MethodInfo

// // object AsyncHandlerMethodInfo {
// //   def unapply(e: ExecutableElement): Option[(Symbol, Vector[TypeInfo], Vector[ParamInfo], TypeInfo, TypeInfo, Boolean)] =

// // }

// case class HandlerMethodInfo(
//   name: Symbol,
//   typeParams: Vector[TypeInfo],
//   params: Vector[ParamInfo],
//   handlerType: TypeInfo,
//   returnType: TypeInfo,
//   isStatic: Boolean
// ) extends MethodInfo

// case class ClassInfo(
//   name: Symbol,
//   typeParams: Vector[TypeInfo] = Vector.empty,
//   staticMethods: Vector[StandardMethodInfo] = Vector.empty,
//   staticHandlerMethods: Vector[HandlerMethodInfo]= Vector.empty,
//   staticAsyncHandlerMethods: Vector[AsyncHandlerMethodInfo] = Vector.empty,
//   instanceMethods: Vector[StandardMethodInfo] = Vector.empty,
//   instanceHandlerMethods: Vector[HandlerMethodInfo] = Vector.empty,
//   instanceAsyncHandlerMethods: Vector[AsyncHandlerMethodInfo] = Vector.empty
// )



// object Codegen {
//   def generate(processingEnv: ProcessingEnvironment, roundEnv: RoundEnvironment, element: Element): ClassInfo = {
//     val Elems = processingEnv.getElementUtils

//     def getTypeParams[A <: Parameterizable](p: A): Vector[TypeInfo] =
//       p.getTypeParameters.asScala
//         .toVector.map { tp =>
//           TypeInfo(Symbol(tp.toString), Vector.empty)
//         }

//     def isStatic(e: ExecutableElement): Boolean =
//       e.getModifiers.asScala.contains(STATIC)

//     def isAsyncHandler(e: ExecutableElement): Boolean =
//       e.getParameters.asScala.lastOption.map { param =>
//         val paramType = param.asType
//         if (paramType.getKind == DECLARED) {
//           val declType = paramType.asInstanceOf[DeclaredType]
//           val elem = declType.asElement
//           elem.toString == "io.vertx.core.Handler" &&
//             declType.getTypeArguments.get(0).getKind == DECLARED && {
//               val firstParam = declType.getTypeArguments.get(0)
//               val innerDeclType = firstParam.asInstanceOf[DeclaredType]
//               val innerElem = declType.asElement
//               println(innerElem)
//               innerElem.toString == "io.vertx.core.AsyncResult"
//             }
//         } else false
//       }.getOrElse(false)

//     def isHandler(e: ExecutableElement): Boolean =
//       !isAsyncHandler(e) && e.getParameters.asScala.lastOption.map { param =>
//         val paramType = param.asType
//         if (paramType.getKind == DECLARED) {
//           val elem = paramType.asInstanceOf[DeclaredType].asElement
//           elem.toString == "io.vertx.core.Handler"
//         } else false
//       }.getOrElse(false)

//     def go(elem: Element, classInfo: ClassInfo): ClassInfo = elem.getKind match {
//       case INTERFACE =>
//         elem.getEnclosedElements.asScala.foldLeft(classInfo) {
//           case (info, e) =>
//             go(e, info)
//         }
//       case METHOD =>
//         val exec = elem.asInstanceOf[ExecutableElement]
//         val typeParams = getTypeParams(exec)
//         val static = isStatic(exec)
//         val asyncHandler = isAsyncHandler(exec)
//         val handler = isHandler(exec)
//         println(exec.getSimpleName -> asyncHandler)
//         classInfo
//       case FIELD =>
//         classInfo
//       case ENUM =>
//         classInfo
//       case other =>
//         println(other)
//         classInfo
//     }

//     val typeElement = Elems.getTypeElement(element.toString)
//     val qualifiedName = typeElement.getQualifiedName
//     val typeParameters = getTypeParams(typeElement)

//     go(element, ClassInfo(Symbol(qualifiedName.toString), typeParameters))
//   }
// }

// // class CodegenVisitor(info: ClassInfo) extends ElementKindVisitor8[ClassInfo, Unit](info) {
// //   var classInfo: ClassInfo = DEFAULT_VALUE
// //   // override def defaultAction(element: Element, u: Unit) = classInfo
// //   override def visitExecutableAsMethod(element: ExecutableElement, u: Unit) = ???
// //   override def visitExecutableAsConstructor(element: ExecutableElement, u: Unit) = ???
// // }
