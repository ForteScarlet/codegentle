package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierSet
import love.forte.codegentle.kotlin.spec.KotlinAnnotationTypeSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType

private val DEFAULT_IMPLICIT = KotlinModifierSet.of(KotlinModifier.ANNOTATION)

/**
 * Extension function to emit a [KotlinAnnotationTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinAnnotationTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    require(modifiers.contains(KotlinModifier.ANNOTATION)) {
        "Annotation type spec must contains ANNOTATION modifier."
    }

    codeWriter.inType(this) {
        emitTo0(codeWriter)
    }
}

private fun KotlinAnnotationTypeSpec.emitTo0(codeWriter: KotlinCodeWriter) {
    var blockLineRequired = false

    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers (modifiers must contains ANNOTATIONS)
    codeWriter.emitModifiers(modifiers)

    // Emit the annotation class keyword
    codeWriter.emit("class ")

    // Emit the name
    codeWriter.emit(name)

    // Emit type variables
    if (typeVariables.isNotEmpty()) {
        codeWriter.emitTypeVariableRefs(typeVariables)
    }

    // Emit the body
    codeWriter.emit(" {\n")
    codeWriter.indent()

    // Emit properties (annotation classes can only have properties, no functions or subtypes)
    if (properties.isNotEmpty()) {
        for (property in properties) {
            if (blockLineRequired) {
                codeWriter.emitNewLine()
            }
            property.emitTo(codeWriter)
            codeWriter.emitNewLine()
            blockLineRequired = true
        }
    }

    codeWriter.unindent()
    codeWriter.emit("}")
}
