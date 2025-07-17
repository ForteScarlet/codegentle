package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.kotlin.spec.KotlinGetterSpec
import love.forte.codegentle.kotlin.spec.KotlinSetterSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinGetterSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinGetterSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Emit KDoc if present
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers)

    codeWriter.emit("get")

    // If getter has code, emit it as a block
    if (!code.isEmpty()) {
        codeWriter.emit("() {")
        codeWriter.indent()
        codeWriter.emitNewLine()
        codeWriter.emit(code)
        codeWriter.unindent()
        codeWriter.emitNewLine()
        codeWriter.emit("}")
    } else {
        // Empty getter
        codeWriter.emit("()")
    }
}

/**
 * Extension function to emit a [KotlinSetterSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinSetterSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Emit KDoc if present
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers)

    codeWriter.emit("set")

    // If setter has code, emit it as a block with parameter
    if (!code.isEmpty()) {
        codeWriter.emit("(${parameterName}) {")
        codeWriter.indent()
        codeWriter.emitNewLine()
        codeWriter.emit(code)
        codeWriter.unindent()
        codeWriter.emitNewLine()
        codeWriter.emit("}")
    } else {
        // Empty setter
        codeWriter.emit("(${parameterName})")
    }
}
