package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinEnumTypeSpec
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
    val blankLineManager = BlankLineManager(codeWriter)

    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers excluding ENUM since it's part of "enum class" keyword
    codeWriter.emitModifiers(modifiers, setOf(KotlinModifier.ENUM))

    // Emit the enum class keyword
    codeWriter.emit("enum class ")

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
    codeWriter.emit(" {\n")
    codeWriter.indent()

    // Emit enum constants
    if (enumConstants.isNotEmpty()) {
        val constantEntries = enumConstants.entries.toList()
        constantEntries.forEachIndexed { index, (constantName, anonymousClass) ->
            if (blankLineManager.blankLineRequired) {
                codeWriter.emitNewLine()
            }

            codeWriter.emit(constantName)

            // If the enum constant has an anonymous class implementation
            if (anonymousClass != null) {
                codeWriter.emit("(")
                // Emit super constructor arguments if any
                if (anonymousClass.constructorDelegation.arguments.isNotEmpty()) {
                    anonymousClass.constructorDelegation.arguments.forEachIndexed { argIndex, argument ->
                        if (argIndex > 0) codeWriter.emit(", ")
                        codeWriter.emit(argument)
                    }
                }
                codeWriter.emit(")")

                // Emit the anonymous class body
                codeWriter.emit(" {")
                codeWriter.indent()

                val anonymousBlankLineManager = BlankLineManager(codeWriter)

                // Emit initializer block
                if (!anonymousClass.initializerBlock.isEmpty()) {
                    codeWriter.emitNewLine("init {")
                    codeWriter.withIndent {
                        emit(anonymousClass.initializerBlock)
                    }
                    codeWriter.emitNewLine("}")
                    anonymousBlankLineManager.required()
                }

                // Emit properties
                if (anonymousClass.properties.isNotEmpty()) {
                    anonymousBlankLineManager.withRequirement {
                        for (property in anonymousClass.properties) {
                            property.emitTo(codeWriter)
                            codeWriter.emitNewLine()
                        }
                    }
                }

                // Emit functions
                if (anonymousClass.functions.isNotEmpty()) {
                    anonymousBlankLineManager.withRequirement {
                        for (function in anonymousClass.functions) {
                            function.emitTo(codeWriter)
                            codeWriter.emitNewLine()
                        }
                    }
                }

                codeWriter.unindent()
                codeWriter.emit("}")
            }

            // Add comma and newline for all but the last constant
            if (index < constantEntries.size - 1) {
                codeWriter.emit(",")
            }
            codeWriter.emitNewLine()
            blankLineManager.required()
        }

        // Add semicolon after enum constants if there are other members
        val hasOtherMembers =
            !initializerBlock.isEmpty() || properties.isNotEmpty() || functions.isNotEmpty() || subtypes.isNotEmpty()
        if (hasOtherMembers) {
            codeWriter.emit(";")
            codeWriter.emitNewLine()
        }
    }

    // Emit initializer block
    if (!initializerBlock.isEmpty()) {
        blankLineManager.withRequirement {
            codeWriter.emitNewLine("init {")
            codeWriter.withIndent {
                emit(initializerBlock)
            }
            codeWriter.emitNewLine("}")
        }
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

    // Pop type variables from scope
    codeWriter.popTypeVariableRefs(typeVariables)

    codeWriter.unindent()
    codeWriter.emit("}")
}
