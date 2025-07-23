package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinAnnotationTypeSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType

/**
 * Extension function to emit a [KotlinAnnotationTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinAnnotationTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    require(KotlinModifier.ANNOTATION in modifiers) {
        "Annotation type spec must contains ANNOTATION modifier, but $modifiers."
    }

    codeWriter.inType(this) {
        emitTo0(codeWriter)
    }
}

private fun KotlinAnnotationTypeSpec.emitTo0(codeWriter: KotlinCodeWriter) {
    val blankLineManager = BlankLineManager(codeWriter)

    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers)

    // Emit the annotation class keyword
    codeWriter.emit("class ")

    // Emit the name
    codeWriter.emit(name)

    // Emit type variables
    codeWriter.emitTypeVariableRefs(typeVariables)

    // Emit the body
    // Emit properties (annotation classes can only have properties, no functions or subtypes)
    if (properties.isNotEmpty()) {
        codeWriter.emitNewLine("(")
        codeWriter.indent()

        blankLineManager.withRequirement {
            var first = true
            for (property in properties) {
                if (!first) {
                    codeWriter.emit(",")
                    codeWriter.emitNewLine()
                }
                property.emitTo(codeWriter)
                first = false
            }
            codeWriter.emitNewLine()
        }

        codeWriter.unindent()
        codeWriter.emit(")")
    }

    // Pop type variables from scope
    codeWriter.popTypeVariableRefs(typeVariables)
}
