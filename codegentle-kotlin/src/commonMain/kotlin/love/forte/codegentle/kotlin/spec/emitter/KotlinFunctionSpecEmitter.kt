package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.code.isNotEmpty
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.naming.KotlinClassNames
import love.forte.codegentle.kotlin.ref.kotlinOrNull
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinFunctionSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinFunctionSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Emit KDoc
    if (!kDoc.isEmpty()) {
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
    codeWriter.emitModifiers(codeWriter.strategy.resolveModifiers(modifiers))

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
    val mutableLine = parameters.any { it.kDoc.isNotEmpty() }
    if (mutableLine) {
        codeWriter.emitNewLine()
        codeWriter.indent()
    }

    parameters.forEachIndexed { index, param ->
        if (index > 0) {
            if (mutableLine) {
                codeWriter.emitNewLine(",")
            } else {
                codeWriter.emit(", ")
            }
        }
        param.emitTo(codeWriter)
    }

    if (mutableLine) {
        codeWriter.unindent()
        codeWriter.emitNewLine()
        codeWriter.emit(")")
    } else {
        codeWriter.emit(")")
    }

    // Emit the return type if it != Unit || it == `Unit?`
    if (returnType.typeName != KotlinClassNames.UNIT
        || returnType.status.kotlinOrNull?.nullable != true) {
        codeWriter.emit(": ")
        codeWriter.emit(returnType)
    }

    if (!code.isEmpty()) {
        if (code.isStartWithReturn()) {
            val replacedCode = code.removeFirstReturn()
            codeWriter.emit(" = ")
            codeWriter.withIndent { emit(replacedCode) }
        } else {
            codeWriter.emitNewLine(" {")
            codeWriter.withIndent { emit(code) }
            codeWriter.emitNewLine()
            codeWriter.emit("}")
        }
    }

    // Pop type variables from scope
    codeWriter.popTypeVariableRefs(typeVariables)
}
