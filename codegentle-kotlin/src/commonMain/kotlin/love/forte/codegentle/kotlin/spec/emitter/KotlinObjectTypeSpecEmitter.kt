package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinObjectTypeSpec
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
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
    codeWriter.emitModifiers(codeWriter.strategy.resolveModifiers(modifiers))

    // Emit "object" keyword for regular objects, "companion object" is handled by modifiers
    codeWriter.emit(KotlinTypeSpec.Kind.OBJECT)

    // Emit the name (companion objects can have names or be anonymous)
    if (name.isNotEmpty() && (KotlinModifier.COMPANION !in modifiers || name != KotlinObjectTypeSpec.DEFAULT_COMPANION_NAME)) {
        codeWriter.emit(" ")
        codeWriter.emit(name)
    }

    // Emit type variables
    codeWriter.emitTypeVariableRefs(typeVariables)

    // Emit superinterfaces (objects cannot have superclasses)
    if (superinterfaces.isNotEmpty()) {
        codeWriter.emit(" : ")
        emitSuperinterfaces(codeWriter)
    }

    // Emit the body
    emitBody(codeWriter, blankLineManager)

    // Pop type variables from scope
    codeWriter.popTypeVariableRefs(typeVariables)
}
