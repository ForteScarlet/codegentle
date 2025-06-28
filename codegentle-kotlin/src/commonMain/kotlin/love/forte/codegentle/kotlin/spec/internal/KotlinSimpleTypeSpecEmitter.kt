package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.VISIBILITY_MODIFIERS
import love.forte.codegentle.kotlin.spec.KotlinSimpleTypeSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinSimpleTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinSimpleTypeSpec.emitTo(codeWriter: KotlinCodeWriter, implicitModifiers: Set<KotlinModifier> = emptySet()) {
    // Push this type spec onto the stack so that functions can check if they're in an interface
    codeWriter.pushType(this)
    var blockLineRequired = false

    // Emit KDoc
    if (!kDoc.isEmpty) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers, implicitModifiers)

    // Emit the type keyword based on the kind
    when (kind) {
        love.forte.codegentle.kotlin.spec.KotlinTypeSpec.Kind.CLASS -> codeWriter.emit("class ")
        love.forte.codegentle.kotlin.spec.KotlinTypeSpec.Kind.INTERFACE -> codeWriter.emit("interface ")
        love.forte.codegentle.kotlin.spec.KotlinTypeSpec.Kind.OBJECT -> codeWriter.emit("object ")
        love.forte.codegentle.kotlin.spec.KotlinTypeSpec.Kind.COMPANION_OBJECT -> codeWriter.emit("companion object ")
        love.forte.codegentle.kotlin.spec.KotlinTypeSpec.Kind.ENUM -> codeWriter.emit("enum class ")
        love.forte.codegentle.kotlin.spec.KotlinTypeSpec.Kind.ANNOTATION -> codeWriter.emit("annotation class ")
        love.forte.codegentle.kotlin.spec.KotlinTypeSpec.Kind.VALUE -> codeWriter.emit("value class ")
        else -> codeWriter.emit("class ") // Default to class
    }

    // Emit the name
    codeWriter.emit(name)

    // Emit type variables
    if (typeVariables.isNotEmpty()) {
        codeWriter.emitTypeVariableRefs(typeVariables)
    }

    // Emit primary constructor if present
    val primary = primaryConstructor
    if (primary != null) {
        if (primary.modifiers.any { it in VISIBILITY_MODIFIERS }) {
            codeWriter.emit(" ")
        }

        primary.emitTo(codeWriter, true)
    }

    // Emit superclass and superinterfaces
    val hasExtends = superclass != null
    val hasImplements = superinterfaces.isNotEmpty()

    if (hasExtends || hasImplements) {
        codeWriter.emit(" : ")

        if (hasExtends) {
            codeWriter.emit(superclass!!)
            codeWriter.emit("(")

            // Check if the primary constructor has super delegation and add arguments
            val primaryDelegation = primary?.constructorDelegation
            if (primaryDelegation != null && primaryDelegation.kind == love.forte.codegentle.kotlin.spec.ConstructorDelegation.Kind.SUPER) {
                primaryDelegation.arguments.forEachIndexed { index, argument ->
                    if (index > 0) codeWriter.emit(", ")
                    codeWriter.emit(argument)
                }
            }

            codeWriter.emit(")")

            if (hasImplements) {
                codeWriter.emit(", ")
            }
        }

        if (hasImplements) {
            superinterfaces.forEachIndexed { index, typeName ->
                if (index > 0) codeWriter.emit(", ")
                codeWriter.emit(typeName)
            }
        }
    }

    // Emit the body
    codeWriter.emit(" {\n")
    codeWriter.indent()

    // Emit initializer block
    if (!initializerBlock.isEmpty) {
        codeWriter.emit("init {")
        codeWriter.emitNewLine()
        codeWriter.withIndent {
            emit(initializerBlock)
        }
        codeWriter.emit("}")
        codeWriter.emitNewLine()
        blockLineRequired = true
    }

    // Emit secondary constructors
    if (secondaryConstructors.isNotEmpty()) {
        if (blockLineRequired) {
            codeWriter.emitNewLine()
        }

        for (constructor in secondaryConstructors) {
            constructor.emitTo(codeWriter, false)
            codeWriter.emitNewLine()
        }
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

/**
 * Extension function to emit a [KotlinSimpleTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinSimpleTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    emitTo(codeWriter, emptySet())
}
