package love.forte.codegentle.java.spec.internal

import love.forte.codegentle.common.code.CodePart.Companion.literal
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.code.emitZeroWidthSpace
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.naming.JavaPrimitiveTypeNames
import love.forte.codegentle.java.ref.javaRef
import love.forte.codegentle.java.spec.JavaMethodSpec
import love.forte.codegentle.java.spec.JavaParameterSpec
import love.forte.codegentle.java.writer.JavaCodeValueEmitOption
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emit
import love.forte.codegentle.java.writer.emitToString


internal class JavaMethodSpecImpl(
    override val name: String,
    override val javadoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<JavaModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val returnType: TypeRef<*>?,
    override val parameters: List<JavaParameterSpec>,
    override val isVarargs: Boolean,
    override val exceptions: List<TypeRef<*>>,
    override val code: CodeValue,
    override val defaultValue: CodeValue,
) : JavaMethodSpec {
    override fun emit(codeWriter: JavaCodeWriter, name: String?, implicitModifiers: Set<JavaModifier>) {
        codeWriter.emit(javadocWithParameters())
        // javadocWithParameters().emit(codeWriter)
        codeWriter.emitAnnotationRefs(annotations, false)
        codeWriter.emitModifiers(modifiers, implicitModifiers)

        if (typeVariables.isNotEmpty()) {
            codeWriter.emitTypeVariableRefs(typeVariables)
            codeWriter.emit(" ")
        }

        if (isConstructor) {
            codeWriter.emit("$name(%V") {
                emitZeroWidthSpace()
            }
        } else {
            codeWriter.emit("%V ${this.name}(%V") {
                emitType(returnType ?: JavaPrimitiveTypeNames.VOID.javaRef())
                emitZeroWidthSpace()
            }
        }

        var firstParameter = true
        val i = parameters.iterator()
        while (i.hasNext()) {
            val parameter = i.next()
            if (!firstParameter) {
                codeWriter.emit(",")
                codeWriter.emitWrappingSpace()
            }

            parameter.emit(codeWriter, !i.hasNext() && isVarargs)
            firstParameter = false
        }

        codeWriter.emit(")")

        if (!defaultValue.isEmpty()) {
            codeWriter.emit(" default ")
            codeWriter.emit(defaultValue)
            // defaultValue.emit(codeWriter)
        }

        if (exceptions.isNotEmpty()) {
            codeWriter.emitWrappingSpace()
            codeWriter.emit("throws")
            var firstException = true
            for (exception in exceptions) {
                if (!firstException) codeWriter.emit(",")
                codeWriter.emitWrappingSpace()
                codeWriter.emit("%V") {
                    emitType(exception)
                }

                firstException = false
            }
        }

        when {
            hasModifier(JavaModifier.ABSTRACT) -> {
                codeWriter.emitNewLine(";")
            }

            hasModifier(JavaModifier.NATIVE) -> {
                // Code is allowed to support stuff like GWT JSNI.
                codeWriter.emit(code)
                // code.emit(codeWriter)
                codeWriter.emitNewLine(";")
            }

            else -> {
                codeWriter.emitNewLine(" {")

                codeWriter.indent()
                codeWriter.emit(code, JavaCodeValueEmitOption.EnsureTrailingNewline)
                // code.emitTo(codeWriter, true)
                codeWriter.unindent()

                codeWriter.emitNewLine("}")
            }
        }
        codeWriter.popTypeVariableRefs(typeVariables)
    }

    private fun javadocWithParameters(): CodeValue {
        if (parameters.isEmpty()) return javadoc

        val builder = CodeValue.builder().add(javadoc)
        var emitTagNewline = true
        for (parameter in parameters) {
            val parameterDoc = parameter.javadoc
            if (parameterDoc.isEmpty()) {
                continue
            }

            // Emit a new line before @param section only if the method javadoc is present.
            if (emitTagNewline && !javadoc.isEmpty()) builder.add("\n")
            emitTagNewline = false
            builder.add("@param ${parameter.name} %V") {
                literal(parameterDoc)
            }
        }

        return builder.build()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JavaMethodSpecImpl) return false

        if (isVarargs != other.isVarargs) return false
        if (name != other.name) return false
        if (javadoc != other.javadoc) return false
        if (annotations != other.annotations) return false
        if (modifiers != other.modifiers) return false
        if (typeVariables != other.typeVariables) return false
        if (returnType != other.returnType) return false
        if (parameters != other.parameters) return false
        if (exceptions != other.exceptions) return false
        if (code != other.code) return false
        if (defaultValue != other.defaultValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isVarargs.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + javadoc.hashCode()
        result = 31 * result + annotations.hashCode()
        result = 31 * result + modifiers.hashCode()
        result = 31 * result + typeVariables.hashCode()
        result = 31 * result + (returnType?.hashCode() ?: 0)
        result = 31 * result + parameters.hashCode()
        result = 31 * result + exceptions.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + defaultValue.hashCode()
        return result
    }

    override fun toString(): String {
        return emitToString()
    }

}
