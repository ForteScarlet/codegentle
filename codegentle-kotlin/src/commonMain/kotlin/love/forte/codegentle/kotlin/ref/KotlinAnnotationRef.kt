package love.forte.codegentle.kotlin.ref

import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit an [AnnotationRef] to a [KotlinCodeWriter].
 */
internal fun AnnotationRef.emitTo(codeWriter: KotlinCodeWriter) {
    codeWriter.emit("@")
    codeWriter.emit(typeName)

    // Handle annotation parameters
    if (members.isNotEmpty()) {
        codeWriter.emit("(")
        var first = true
        for ((name, values) in members) {
            if (!first) {
                codeWriter.emit(", ")
            }
            first = false

            // Emit parameter name
            codeWriter.emit(name)
            codeWriter.emit(" = ")

            when (values) {
                is AnnotationRef.MemberValue.Single -> {
                    codeWriter.emit(values.codeValue)
                }
                is AnnotationRef.MemberValue.Multiple -> {
                    // Multiple values - emit as array
                    codeWriter.emit("[")
                    var firstValue = true
                    for (value in values.codeValues) {
                        if (!firstValue) {
                            codeWriter.emit(", ")
                        }
                        firstValue = false
                        codeWriter.emit(value)
                    }
                    codeWriter.emit("]")
                }
            }
        }
        codeWriter.emit(")")
    }
}
