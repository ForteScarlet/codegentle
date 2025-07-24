package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.VISIBILITY_MODIFIERS
import love.forte.codegentle.kotlin.spec.ConstructorDelegation
import love.forte.codegentle.kotlin.spec.KotlinConstructorSpec
import love.forte.codegentle.kotlin.spec.KotlinSimpleTypeSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType

/**
 * Extension function to emit a [KotlinSimpleTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinSimpleTypeSpec.emitTo(
    codeWriter: KotlinCodeWriter,
    implicitModifiers: Set<KotlinModifier> = emptySet()
) {
    codeWriter.inType(this) {
        emitTo0(codeWriter, implicitModifiers)
    }
}

/**
 * Extension function to emit a [KotlinSimpleTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinSimpleTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    emitTo(codeWriter, emptySet())
}

private fun KotlinSimpleTypeSpec.emitTo0(codeWriter: KotlinCodeWriter, implicitModifiers: Set<KotlinModifier>) {
    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers, implicitModifiers)

    // Emit the type keyword based on the kind
    codeWriter.emit(kind, true)

    // Emit the name
    codeWriter.emit(name)

    // Emit type variables
    emitAndWithTypeVariableRefs(codeWriter) {
        emitInTypeVariableRefs(codeWriter)
    }
}

private fun KotlinSimpleTypeSpec.emitInTypeVariableRefs(codeWriter: KotlinCodeWriter) {
    val blankLineManager = BlankLineManager(codeWriter)

    // Emit primary constructor, superclass and superinterfaces
    emitSupers(codeWriter)

    // Emit the body
    emitBody(
        codeWriter,
        blankLineManager,
        beforeEmitMember = {
            emitSecondaryConstructors(blankLineManager, codeWriter)
        }
    )
}

private fun KotlinSimpleTypeSpec.emitSupers(codeWriter: KotlinCodeWriter) {
    val primary = this.primaryConstructor
    emitPrimaryParameter(codeWriter, primary)

    val superclass = superclass
    val hasExtends = superclass != null
    val hasImplements = superinterfaces.isNotEmpty()

    if (hasExtends || hasImplements) {
        codeWriter.emit(" : ")

        if (hasExtends) {
            codeWriter.emit(superclass)
            codeWriter.emit("(")

            // Check if the primary constructor has super delegation and add arguments
            val primaryDelegation = primary?.constructorDelegation
            if (primaryDelegation != null) {
                require(primaryDelegation.kind == ConstructorDelegation.Kind.SUPER) {
                    "Primary constructor delegation must be a super call, but was $primaryDelegation"
                }

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

        emitSuperinterfaces(codeWriter)
    }
}

private fun emitPrimaryParameter(
    codeWriter: KotlinCodeWriter,
    primaryConstructor: KotlinConstructorSpec?
) {
    if (primaryConstructor != null) {
        if (primaryConstructor.modifiers.any { it in VISIBILITY_MODIFIERS }) {
            codeWriter.emit(" ")
        }

        primaryConstructor.emitTo(codeWriter, true)
    }
}

private fun KotlinSimpleTypeSpec.emitSecondaryConstructors(
    blankLineManager: BlankLineManager,
    codeWriter: KotlinCodeWriter
) {
    if (secondaryConstructors.isNotEmpty()) {
        for (constructor in secondaryConstructors) {
            blankLineManager.withRequirement {
                constructor.emitTo(codeWriter, false)
                codeWriter.emitNewLine()
            }
        }
    }
}
