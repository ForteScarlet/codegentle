package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinObjectTypeSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinObjectTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinObjectTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Push this type spec onto the stack so that functions can check if they're in an object
    codeWriter.pushType(this)
    var blockLineRequired = false

    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers (companion objects have COMPANION modifier)
    val isCompanion = KotlinModifier.COMPANION in modifiers
    if (isCompanion) {
        // For companion objects, emit modifiers excluding COMPANION since we emit "companion object" explicitly
        codeWriter.emitModifiers(modifiers, setOf(KotlinModifier.COMPANION))
        codeWriter.emit("companion object")
    } else {
        // For regular objects, emit all modifiers
        codeWriter.emitModifiers(modifiers, emptySet())
        codeWriter.emit("object")
    }

    // Emit the name (companion objects can have names or be anonymous)
    if (name.isNotEmpty()) {
        codeWriter.emit(" ")
        codeWriter.emit(name)
    }

    // Emit type variables
    if (typeVariables.isNotEmpty()) {
        codeWriter.emitTypeVariableRefs(typeVariables)
    }

    // Emit superinterfaces (objects cannot have superclasses)
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
    if (!initializerBlock.isEmpty()) {
        codeWriter.emitNewLine("init {")
        codeWriter.withIndent {
            emit(initializerBlock)
        }
        codeWriter.emitNewLine("}")
        blockLineRequired = true
    }

    // Emit properties
    if (properties.isNotEmpty()) {
        if (blockLineRequired) {
            codeWriter.emitNewLine()
        }
        for (property in properties) {
            property.emitTo(codeWriter)
            codeWriter.emitNewLine()
        }
        blockLineRequired = true
    }

    // Emit functions
    if (functions.isNotEmpty()) {
        if (blockLineRequired) {
            codeWriter.emitNewLine()
        }
        for (function in functions) {
            function.emitTo(codeWriter)
            codeWriter.emitNewLine()
        }
        blockLineRequired = true
    }

    // Emit subtypes
    if (subtypes.isNotEmpty()) {
        if (blockLineRequired) {
            codeWriter.emitNewLine()
        }
        for (subtype in subtypes) {
            subtype.emitTo(codeWriter)
            codeWriter.emitNewLine()
        }
    }

    codeWriter.unindent()
    codeWriter.emit("}")

    // Pop this type spec from the stack
    codeWriter.popType()
}
