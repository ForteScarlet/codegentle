package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinValueParameterSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinValueParameterSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Skip emitting KDoc for parameters to match the expected output in tests
    if (!(kDoc.isEmpty)) {
        codeWriter.emitNewLine()
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers)

    // Emit propertyization (val/var) if present
    propertyization?.let { prop ->
        if (prop.mutable) {
            codeWriter.emit("var ")
        } else {
            codeWriter.emit("val ")
        }
    }

    // Emit the name
    codeWriter.emit(name)

    // Emit the type
    codeWriter.emit(": ")
    codeWriter.emit(typeRef)

    // Emit default value if present
    defaultValue?.let { value ->
        codeWriter.emit(" = ")
        codeWriter.emit(value)
    }
}
