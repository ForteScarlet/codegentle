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
        // Add other type spec implementations as they are created
        is KotlinAnnotationTypeSpec -> TODO()
        is KotlinAnonymousClassTypeSpec -> TODO()
        is KotlinEnumTypeSpec -> TODO()
        is KotlinObjectTypeSpec -> TODO()
    }
}
