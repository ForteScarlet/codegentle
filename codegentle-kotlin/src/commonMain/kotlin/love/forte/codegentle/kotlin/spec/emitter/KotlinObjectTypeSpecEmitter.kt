package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.spec.KotlinObjectTypeSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType

/**
 * Extension function to emit a [KotlinObjectTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinObjectTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    codeWriter.inType(this) {
        emitTo0(codeWriter)
    }
}

private fun KotlinObjectTypeSpec.emitTo0(codeWriter: KotlinCodeWriter) {
    val blankLineManager = BlankLineManager(codeWriter)

    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers (companion objects have COMPANION modifier)
    codeWriter.emitModifiers(modifiers)

    // Emit the name (companion objects can have names or be anonymous)
    if (name.isNotEmpty() && name != KotlinObjectTypeSpec.DEFAULT_COMPANION_NAME) {
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

    // Emit subtypes
    if (subtypes.isNotEmpty()) {
        blankLineManager.withRequirement {
            for (subtype in subtypes) {
                subtype.emitTo(codeWriter)
                codeWriter.emitNewLine()
            }
        }
    }

    codeWriter.unindent()
    codeWriter.emit("}")
}
