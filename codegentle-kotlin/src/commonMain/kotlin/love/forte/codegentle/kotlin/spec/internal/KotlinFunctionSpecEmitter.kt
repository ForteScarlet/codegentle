package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeSimplePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinFunctionSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinFunctionSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Emit KDoc
    if (!kDoc.isEmpty) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit context parameters if any
    if (contextParameters.isNotEmpty()) {
        codeWriter.emit("context(")
        contextParameters.forEachIndexed { index, param ->
            if (index > 0) codeWriter.emit(", ")
            param.emitTo(codeWriter)
        }
        codeWriter.emit(")\n")
    }

    // Emit modifiers
    codeWriter.emitModifiers(modifiers)

    // Emit the function keyword
    codeWriter.emit("fun ")

    // Emit type variables if any
    if (typeVariables.isNotEmpty()) {
        codeWriter.emitTypeVariableRefs(typeVariables)
        codeWriter.emit(" ")
    }

    // Emit receiver if any
    receiver?.let { recv ->
        codeWriter.emit(recv)
        codeWriter.emit(".")
    }

    // Emit the name
    codeWriter.emit(name)

    // Emit parameters
    codeWriter.emit("(")
    parameters.forEachIndexed { index, param ->
        if (index > 0) {
            codeWriter.emit(", ")
            if (!param.kDoc.isEmpty) {
                codeWriter.emitNewLine()
            }
        }
        param.emitTo(codeWriter)
    }
    codeWriter.emit(")")

    // Emit return type
    codeWriter.emit(": ")
    codeWriter.emit(returnType)

    if (!code.isEmpty) {
        val parts = code.parts
        val firstPart = parts.first()
        if (firstPart is CodeSimplePart && firstPart.value.trimStart().startsWith("return ")) {
            // { return ... } -> = ...
            val replacedCode = CodeValue(buildList {
                add(CodePart.simple(firstPart.value.replaceFirst("return ", "")))
                addAll(parts.subList(1, parts.size))
            })
            codeWriter.emit(" = ")
            codeWriter.indent()
            codeWriter.emit(replacedCode)
            codeWriter.unindent()
        } else {
            codeWriter.emit(" {\n")
            codeWriter.indent()
            codeWriter.emit(code)
            codeWriter.unindent()
            codeWriter.emit("\n}")
        }

        parts.first() as? CodeSimplePart


    }
}
