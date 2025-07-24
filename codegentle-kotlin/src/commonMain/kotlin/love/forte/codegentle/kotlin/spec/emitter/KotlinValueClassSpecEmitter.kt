package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierSet
import love.forte.codegentle.kotlin.spec.KotlinValueClassSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType

private val DEFAULT_IMPLICIT = KotlinModifierSet.of(KotlinModifier.VALUE)

/**
 * Extension function to emit a [KotlinValueClassSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinValueClassSpec.emitTo(codeWriter: KotlinCodeWriter) {
    codeWriter.inType(this) {
        emitTo0(codeWriter)
    }
}

private fun KotlinValueClassSpec.emitTo0(codeWriter: KotlinCodeWriter) {
    val blankLineManager = BlankLineManager(codeWriter)

    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers (exclude VALUE modifier since we emit "value class" explicitly)
    codeWriter.emitModifiers(modifiers, DEFAULT_IMPLICIT)

    // Emit the value class keyword
    codeWriter.emit("value class ")

    // Emit the name
    codeWriter.emit(name)

    // Emit type variables
    codeWriter.emitTypeVariableRefs(typeVariables)

    val hasKDoc = !primaryParameter.kDoc.isEmpty()

    // Emit primary parameter (value classes always have exactly one primary parameter)
    codeWriter.emit("(")
    if (hasKDoc) {
        codeWriter.withIndent {
            codeWriter.emitNewLine()
            primaryParameter.emitTo(codeWriter)
        }
        codeWriter.emitNewLine()
    } else {
        primaryParameter.emitTo(codeWriter)
    }
    codeWriter.emit(")")

    // Emit superinterfaces
    if (superinterfaces.isNotEmpty()) {
        codeWriter.emit(" : ")
        superinterfaces.forEachIndexed { index, typeName ->
            if (index > 0) codeWriter.emit(", ")
            codeWriter.emit(typeName)
        }
    }

    // Emit the body
    codeWriter.emitNewLine(" {")
    codeWriter.indent()

    // Emit initializer block
    if (!initializerBlock.isEmpty()) {
        codeWriter.emitNewLine("init {")
        codeWriter.withIndent {
            emit(initializerBlock)
        }
        codeWriter.emitNewLine()
        codeWriter.emit("}")
        blankLineManager.required()
    }

    // Emit properties
    if (properties.isNotEmpty()) {
        blankLineManager.withRequirement {
            for (property in properties) {
                property.emitTo(codeWriter)
                codeWriter.emitNewLine()
            }
        }
    }

    // Emit functions
    if (functions.isNotEmpty()) {
        blankLineManager.withRequirement {
            for (function in functions) {
                function.emitTo(codeWriter)
                codeWriter.emitNewLine()
            }
        }
    }

    // Pop type variables from scope
    codeWriter.popTypeVariableRefs(typeVariables)

    codeWriter.unindent()
    codeWriter.emit("}")
}
