package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.spec.KotlinPropertySpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinPropertySpec] to a [KotlinCodeWriter].
 */
internal fun KotlinPropertySpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers)

    // Emit the property keyword (val or var)
    codeWriter.emit(if (mutable) "var " else "val ")

    // Emit the name
    codeWriter.emit(name)

    // Emit the type
    codeWriter.emit(": ")
    codeWriter.emit(typeRef)

    // Emit initializer or delegate if present
    initializer?.let { init ->
        codeWriter.emit(" = ")
        codeWriter.emit(init)
    }

    delegate?.let { del ->
        codeWriter.emit(" by ")
        codeWriter.emit(del)
    }

    // Check if we have custom getter or setter
    val hasCustomAccessors = getter != null || setter != null

    if (hasCustomAccessors) {
        codeWriter.withIndent {
            // Emit getter if present
            getter?.let { get ->
                codeWriter.emitNewLine()
                get.emitTo(codeWriter)
            }

            // Emit setter if present
            setter?.let { set ->
                codeWriter.emitNewLine()
                set.emitTo(codeWriter)
            }
        }
    }
}
