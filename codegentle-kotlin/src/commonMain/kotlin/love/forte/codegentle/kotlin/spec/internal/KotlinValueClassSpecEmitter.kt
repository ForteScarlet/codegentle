package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierSet
import love.forte.codegentle.kotlin.spec.KotlinValueClassSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

private val DEFAULT_IMPLICIT = KotlinModifierSet.of(KotlinModifier.VALUE)

/**
 * Extension function to emit a [KotlinValueClassSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinValueClassSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Push this type spec onto the stack so that functions can check if they're in a value class
    codeWriter.pushType(this)
    var blockLineRequired = false

    // Emit KDoc
    if (!kDoc.isEmpty) {
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
    if (typeVariables.isNotEmpty()) {
        codeWriter.emitTypeVariableRefs(typeVariables)
    }

    val hasKDoc = !primaryParameter.kDoc.isEmpty

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
    codeWriter.emit(" {\n")
    codeWriter.indent()

    // Emit initializer block
    if (!initializerBlock.isEmpty) {
        codeWriter.emitNewLine("init {")
        codeWriter.withIndent {
            emit(initializerBlock)
        }
        codeWriter.emitNewLine()
        codeWriter.emitNewLine("}")
        blockLineRequired = true
    }

    // Emit properties
    if (properties.isNotEmpty()) {
        for (property in properties) {
            if (blockLineRequired) {
                codeWriter.emitNewLine()
            }
            property.emitTo(codeWriter)
        }
        blockLineRequired = true
    }

    // Emit functions
    if (functions.isNotEmpty()) {
        for (function in functions) {
            if (blockLineRequired) {
                codeWriter.emitNewLine()
            }
            function.emitTo(codeWriter)
        }
        codeWriter.emitNewLine()
    }

    codeWriter.unindent()
    codeWriter.emit("}")

    // Pop this type spec from the stack
    codeWriter.popType()
}
