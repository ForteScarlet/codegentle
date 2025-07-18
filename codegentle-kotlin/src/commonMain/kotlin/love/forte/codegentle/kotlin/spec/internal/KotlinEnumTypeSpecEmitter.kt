package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierSet
import love.forte.codegentle.kotlin.spec.KotlinEnumTypeSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

private val DEFAULT_IMPLICIT = KotlinModifierSet.of(KotlinModifier.ENUM)

/**
 * Extension function to emit a [KotlinEnumTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinEnumTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Push this type spec onto the stack so that functions can check if they're in an enum class
    codeWriter.pushType(this)
    var blockLineRequired = false

    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers (exclude ENUM modifier since we emit "enum class" explicitly)
    codeWriter.emitModifiers(modifiers, DEFAULT_IMPLICIT)

    // Emit the enum class keyword
    codeWriter.emit("enum class ")

    // Emit the name
    codeWriter.emit(name)

    // Emit type variables
    if (typeVariables.isNotEmpty()) {
        codeWriter.emitTypeVariableRefs(typeVariables)
    }

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
            if (blockLineRequired) {
                codeWriter.emitNewLine()
            }
            
            codeWriter.emit(constantName)
            
            // If the enum constant has an anonymous class implementation
            if (anonymousClass != null) {
                codeWriter.emit("(")
                // Emit super constructor arguments if any
                if (anonymousClass.superConstructorArguments.isNotEmpty()) {
                    anonymousClass.superConstructorArguments.forEachIndexed { argIndex, argument ->
                        if (argIndex > 0) codeWriter.emit(", ")
                        codeWriter.emit(argument)
                    }
                }
                codeWriter.emit(")")
                
                // Emit the anonymous class body
                codeWriter.emit(" {")
                codeWriter.indent()
                
                var anonymousBlockLineRequired = false
                
                // Emit initializer block
                if (!anonymousClass.initializerBlock.isEmpty()) {
                    codeWriter.emitNewLine("init {")
                    codeWriter.withIndent {
                        emit(anonymousClass.initializerBlock)
                    }
                    codeWriter.emitNewLine("}")
                    anonymousBlockLineRequired = true
                }
                
                // Emit properties
                if (anonymousClass.properties.isNotEmpty()) {
                    if (anonymousBlockLineRequired) {
                        codeWriter.emitNewLine()
                    }
                    for (property in anonymousClass.properties) {
                        property.emitTo(codeWriter)
                        codeWriter.emitNewLine()
                    }
                    anonymousBlockLineRequired = true
                }
                
                // Emit functions
                if (anonymousClass.functions.isNotEmpty()) {
                    if (anonymousBlockLineRequired) {
                        codeWriter.emitNewLine()
                    }
                    for (function in anonymousClass.functions) {
                        function.emitTo(codeWriter)
                        codeWriter.emitNewLine()
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
            blockLineRequired = true
        }
        
        // Add semicolon after enum constants if there are other members
        val hasOtherMembers = !initializerBlock.isEmpty() || properties.isNotEmpty() || functions.isNotEmpty() || subtypes.isNotEmpty()
        if (hasOtherMembers) {
            codeWriter.emit(";")
            codeWriter.emitNewLine()
        }
    }

    // Emit initializer block
    if (!initializerBlock.isEmpty()) {
        if (blockLineRequired) {
            codeWriter.emitNewLine()
        }
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
