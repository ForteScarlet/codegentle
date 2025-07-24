package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.code.isNotEmpty
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.common.writer.withIndentBlock
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinEnumTypeSpec
import love.forte.codegentle.kotlin.spec.isMemberNotEmpty
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType

/**
 * Extension function to emit a [KotlinEnumTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinEnumTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    require(KotlinModifier.ENUM in modifiers) {
        "Enum type spec must have ENUM modifier, but $modifiers"
    }

    codeWriter.inType(this) {
        emitTo0(codeWriter)
    }
}

private fun KotlinEnumTypeSpec.emitTo0(codeWriter: KotlinCodeWriter) {
    require(KotlinModifier.ENUM in modifiers) {
        "KotlinEnumTypeSpec must have ENUM modifier, but $modifiers"
    }
    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers excluding ENUM since it's part of "enum class" keyword
    codeWriter.emitModifiers(modifiers)

    // Emit the enum class keyword
    codeWriter.emit("class ")

    // Emit the name
    codeWriter.emit(name)

    // Emit type variables
    codeWriter.emitTypeVariableRefs(typeVariables)

    // Emit superinterfaces
    if (superinterfaces.isNotEmpty()) {
        codeWriter.emit(" : ")
        superinterfaces.forEachIndexed { index, typeName ->
            if (index > 0) codeWriter.emit(", ")
            codeWriter.emit(typeName)
        }
    }

    // Emit the body
    if (isMemberNotEmpty()) {
        emitEnumBody(codeWriter)
    }

    codeWriter.popTypeVariableRefs(typeVariables)
}

private fun KotlinEnumTypeSpec.emitEnumBody(codeWriter: KotlinCodeWriter) {
    val blankLineManager = BlankLineManager(codeWriter)

    codeWriter.withIndentBlock(prefix = " ") {
        // Emit enum constants
        if (enumConstants.isNotEmpty()) {
            for ((index, entry) in enumConstants.entries.withIndex()) {
                val (constantName, anonymousClass) = entry
                // Add comma and newline for all but the last constant
                if (index in 1..enumConstants.size - 1) {
                    codeWriter.emit(",")
                    codeWriter.emitNewLine()
                }

                // pre item has body, or current item has kdoc, emit a new line.
                if (blankLineManager.blankLineRequired || anonymousClass?.kDoc?.isNotEmpty() == true) {
                    codeWriter.emitNewLine()
                    blankLineManager.clear()
                }

                // TODO emit kdoc
                // TODO emit annotations

                codeWriter.emit(constantName)

                // If the enum constant has an anonymous class implementation
                anonymousClass?.emitTo(codeWriter, true)

                if (anonymousClass?.isMemberNotEmpty() == true) {
                    blankLineManager.required()
                }
            }

            // Add semicolon after enum constants if there are other members
            val hasOtherMembers =
                initializerBlock.isNotEmpty() || properties.isNotEmpty() || functions.isNotEmpty() || subtypes.isNotEmpty()

            if (hasOtherMembers) {
                codeWriter.emit(";")
                blankLineManager.required()
            }
            codeWriter.emitNewLine()
        } else {
            codeWriter.emitNewLine(";")
        }

        // Emit initializer block
        if (!initializerBlock.isEmpty()) {
            blankLineManager.withRequirement {
                codeWriter.withIndentBlock(prefix = "init") {
                    emit(initializerBlock)
                    codeWriter.emitNewLine()
                }
            }
            codeWriter.emitNewLine()
        }

        // Emit properties
        if (properties.isNotEmpty()) {
            for (property in properties) {
                blankLineManager.withRequirement {
                    property.emitTo(codeWriter)
                    codeWriter.emitNewLine()
                }
            }
        }

        // Emit functions
        if (functions.isNotEmpty()) {
            for (function in functions) {
                blankLineManager.withRequirement {
                    function.emitTo(codeWriter)
                    codeWriter.emitNewLine()
                }
            }
        }

        // Emit subtypes
        if (subtypes.isNotEmpty()) {
            for (subtype in subtypes) {
                blankLineManager.withRequirement {
                    subtype.emitTo(codeWriter)
                    codeWriter.emitNewLine()
                }
            }
        }
    }
}
