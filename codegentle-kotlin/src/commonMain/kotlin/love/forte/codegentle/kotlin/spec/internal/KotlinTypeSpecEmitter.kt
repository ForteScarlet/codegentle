package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    when (this) {
        is KotlinSimpleTypeSpec -> emitTo(codeWriter)
        is KotlinValueClassSpec -> emitTo(codeWriter)
        is KotlinAnnotationTypeSpec -> emitTo(codeWriter)
        is KotlinAnonymousClassTypeSpec -> emitTo(codeWriter)
        is KotlinEnumTypeSpec -> emitTo(codeWriter)
        is KotlinObjectTypeSpec -> emitTo(codeWriter)
    }
}
