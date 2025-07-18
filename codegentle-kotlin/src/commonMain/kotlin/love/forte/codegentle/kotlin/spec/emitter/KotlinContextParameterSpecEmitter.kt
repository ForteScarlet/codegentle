package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.kotlin.spec.KotlinContextParameterSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinContextParameterSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinContextParameterSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Emit the name or underscore
    val paramName = name
    if (paramName != null) {
        codeWriter.emit(paramName)
    } else {
        codeWriter.emit("_")
    }

    // Emit the type
    codeWriter.emit(": ")
    codeWriter.emit(typeRef)
}
