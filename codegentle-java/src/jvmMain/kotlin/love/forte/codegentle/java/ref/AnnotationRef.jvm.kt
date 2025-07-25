package love.forte.codegentle.java.ref

import love.forte.codegentle.common.code.CodePart.Companion.literal
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.emitLiteral
import love.forte.codegentle.common.code.emitString
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.naming.toClassName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefBuilder
import love.forte.codegentle.common.ref.addMember
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.java.characterLiteralWithoutSingleQuotes
import love.forte.codegentle.java.naming.toJavaClassName
import love.forte.codegentle.java.naming.toTypeName
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.SimpleAnnotationValueVisitor8


public fun AnnotationMirror.toAnnotationRef(): AnnotationRef {
    val element = annotationType.asElement() as TypeElement
    return element.toClassName().annotationRef {
        val visitor = AnnotationMirrorVisitor(this)
        for (executableElement in this@toAnnotationRef.elementValues.keys) {
            val name = executableElement.simpleName.toString()
            this@toAnnotationRef.elementValues[executableElement]?.accept(visitor, name)
        }
    }
}

private class AnnotationMirrorVisitor(
    val builder: AnnotationRefBuilder<*, *>,
) : SimpleAnnotationValueVisitor8<AnnotationRefBuilder<*, *>, String>() {
    override fun defaultAction(o: Any, name: String): AnnotationRefBuilder<*, *> {
        return builder.addMemberForValue(name, o)
    }

    override fun visitAnnotation(annotationMirror: AnnotationMirror, name: String): AnnotationRefBuilder<*, *> {
        return builder.addMember(name, "%V") {
            emitLiteral(annotationMirror.toAnnotationRef())
        }
    }

    override fun visitEnumConstant(c: VariableElement, name: String): AnnotationRefBuilder<*, *> {
        return builder.addMember(name, "%V.%V") {
            emitType(c.asType().toTypeName())
            emitLiteral(c.simpleName)
        }
    }
}

internal fun AnnotationRefBuilder<*, *>.addMemberForValue(memberName: String, value: Any): AnnotationRefBuilder<*, *> {
    check(SourceVersion.isName(memberName)) {
        "not a valid name: $memberName"
    }

    return when (value) {
        is Class<*> -> {
            addMember(memberName, "%V.class") {
                // TODO type(Class)
                emitType(value.toJavaClassName())
            }
        }

        is Enum<*> -> {
            addMember(memberName, "%V.%V") {
                // TODO type(Class)
                emitType(value.javaClass.toJavaClassName())
                emitLiteral(value.name)
            }
        }

        is String -> {
            addMember(memberName, "%V") {
                emitString(value)
            }
        }

        is Float -> {
            addMember(memberName, "%Vf") {
                emitLiteral(value)
            }
        }

        is Long -> {
            addMember(memberName, "%VL") {
                emitLiteral(value)
            }
        }

        is Char -> {
            addMember(memberName, "'%L'") {
                emitLiteral(value.characterLiteralWithoutSingleQuotes())
            }
        }

        else -> {
            addMember(memberName, CodeValue(literal(value)))
        }
    }
}
