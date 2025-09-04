package love.forte.codegentle.test.apt.proc

import love.forte.codegentle.common.code.emitLiteral
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.common.naming.toClassName
import love.forte.codegentle.common.ref.annotationRefs
import love.forte.codegentle.java.JavaFile
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.addModifier
import love.forte.codegentle.java.naming.toTypeName
import love.forte.codegentle.java.naming.toTypeVariableName
import love.forte.codegentle.java.ref.addGenerated
import love.forte.codegentle.java.ref.javaRef
import love.forte.codegentle.java.spec.*
import love.forte.codegentle.java.writeTo
import java.time.Instant
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.SourceVersion
import javax.lang.model.element.*

/**
 *
 * @author ForteScarlet
 */
@SupportedAnnotationTypes("love.forte.codegentle.test.apt.proc.annotation.IncludeToFactory")
class TestProcessor : AbstractProcessor() {
    private val elements = mutableSetOf<Element>()
    private val factoryMethods = linkedSetOf<JavaMethodSpec>()

    override fun getSupportedSourceVersion(): SourceVersion =
        SourceVersion.latestSupported()

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        if (roundEnv.errorRaised()) {
            return false
        }

        if (roundEnv.processingOver()) {
            // TODO over了
            doFinal(roundEnv)
            return false
        }

        // process
        val elements = roundEnv.getElementsAnnotatedWith(annotations.first())

        processElements(elements)

        return true
    }

    private fun doFinal(roundEnv: RoundEnvironment) {
        val factoryType = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "Factories") {
            addModifier(Modifier.FINAL)
            annotationRefs {
                addGenerated(
                    values = arrayOf("love.forte.codegentle.test.apt.proc.TestProcessor"),
                    date = Instant.now().toString(),
                    comments = "A test generator."
                )
            }
            addConstructor {
                addModifier(Modifier.PRIVATE)
            }

            addMethods(factoryMethods)
        }

        val file = JavaFile("love.forte.codegentle.factories".parseToPackageName(), factoryType)

        file.writeTo(
            filer = processingEnv.filer,
            originatingElements = elements
        )
    }

    private fun processElements(elements: Set<Element>) {
        val skipModifiers = setOf(
            Modifier.PRIVATE,
            Modifier.PROTECTED,
            Modifier.ABSTRACT,
        )

        val requires = setOf(Modifier.STATIC, Modifier.PUBLIC)

        val methods = elements.filter { it.kind == ElementKind.METHOD }
            .map { it as ExecutableElement }

        methods.forEach { method ->
            // 公开、静态
            val modifiers = method.modifiers
            if (!modifiers.containsAll(requires)) {
                return@forEach
            }

            this.elements.add(method)

            val enclosingElement = method.enclosingElement as TypeElement
            val enclosingClassName = enclosingElement.toClassName()

            val newMethod =
                JavaMethodSpec(enclosingElement.simpleName.toString() + "_" + method.simpleName.toString()) {
                    addModifiers(JavaModifier.STATIC, JavaModifier.PUBLIC)
                    addExceptions(method.thrownTypes.map { it.toTypeName().javaRef() })
                    addTypeVariables(method.typeParameters.map { it.toTypeVariableName().javaRef() })
                    addParameters(method.javaParameterSpecs)
                    returns(method.returnType.toTypeName().javaRef())
                    val names = method.parameters.map { it.simpleName.toString() }

                    // Call this method
                    addStatement("return %V.%V(${names.joinToString(", ")})") {
                        emitType(enclosingClassName)
                        emitLiteral(method.simpleName.toString())
                    }
                }

            factoryMethods.add(newMethod)
        }

        // classes.forEach {
        //     messager.printMessage(NOTE, "\troundEnv.element: $it")
        //     it.kind
        //     it.accept(object : AbstractElementVisitor8<Unit, Unit>() {
        //         override fun visitPackage(e: PackageElement, p: Unit?) {
        //             processingEnv.messager.printMessage(
        //                 NOTE,
        //                 "visitPackage: $e, ${e.kind}, ${e.qualifiedName}"
        //             )
        //
        //         }
        //
        //         override fun visitType(e: TypeElement, p: Unit?) {
        //             processingEnv.messager.printMessage(
        //                 NOTE,
        //                 "visitType: $e, ${e.kind}, ${e.qualifiedName}"
        //             )
        //         }
        //
        //         override fun visitVariable(e: VariableElement, p: Unit?) {
        //             processingEnv.messager.printMessage(
        //                 NOTE,
        //                 "visitVariable: $e, ${e.kind}, ${e.simpleName}"
        //             )
        //         }
        //
        //         override fun visitExecutable(e: ExecutableElement, p: Unit?) {
        //             processingEnv.messager.printMessage(
        //                 NOTE,
        //                 "visitExecutable: $e, ${e.kind}, ${e.simpleName}"
        //             )
        //         }
        //
        //         override fun visitTypeParameter(e: TypeParameterElement, p: Unit?) {
        //             processingEnv.messager.printMessage(
        //                 NOTE,
        //                 "visitTypeParameter: $e, ${e.kind}, ${e.simpleName}"
        //             )
        //         }
        //     }, Unit)
        //
        // }
    }
}
