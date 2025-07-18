package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.VISIBILITY_MODIFIERS
import love.forte.codegentle.kotlin.spec.ConstructorDelegation
import love.forte.codegentle.kotlin.spec.KotlinSimpleTypeSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType

/**
 * Extension function to emit a [KotlinSimpleTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinSimpleTypeSpec.emitTo(codeWriter: KotlinCodeWriter, implicitModifiers: Set<KotlinModifier> = emptySet()) {
    codeWriter.inType(this) {
        emitTo0(codeWriter, implicitModifiers)
    }
}

private fun KotlinSimpleTypeSpec.emitTo0(codeWriter: KotlinCodeWriter, implicitModifiers: Set<KotlinModifier>) {
    val blankLineManager = BlankLineManager(codeWriter)

    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers, implicitModifiers)

    // Emit the type keyword based on the kind
    codeWriter.emit(kind.keyword)
    codeWriter.emit(" ")

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
            if (primaryDelegation != null && primaryDelegation.kind == ConstructorDelegation.Kind.SUPER) {
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
    if (!initializerBlock.isEmpty()) {
        codeWriter.emitNewLine("init {")
        codeWriter.withIndent {
            emit(initializerBlock)
        }
        codeWriter.emitNewLine("}")
        blankLineManager.required()
    }

    // Emit secondary constructors
    if (secondaryConstructors.isNotEmpty()) {
        blankLineManager.withRequirement {
            for (constructor in secondaryConstructors) {
                constructor.emitTo(codeWriter, false)
                codeWriter.emitNewLine()
            }
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

    codeWriter.unindent()
    codeWriter.emit("}")
}

/**
 * Extension function to emit a [KotlinSimpleTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinSimpleTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    emitTo(codeWriter, emptySet())
}
