package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
import love.forte.codegentle.kotlin.spec.KotlinValueClassSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType

/**
 * Extension function to emit a [KotlinValueClassSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinValueClassSpec.emitTo(codeWriter: KotlinCodeWriter) {
    require(KotlinModifier.VALUE in modifiers) {
        "Value class spec must have VALUE modifier, but $modifiers"
    }

    codeWriter.inType(this) {
        emitTo0(codeWriter)
    }
}

private fun KotlinValueClassSpec.emitTo0(codeWriter: KotlinCodeWriter) {
    val blankLineManager = BlankLineManager(codeWriter)

    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    codeWriter.emitModifiers(modifiers)

    // Emit the value class keyword
    codeWriter.emit(KotlinTypeSpec.Kind.CLASS, true)

    // Emit the name
    codeWriter.emit(name)

    // Emit type variables
    emitAndWithTypeVariableRefs(codeWriter) {
        emitInTypeVariableRefs(codeWriter, blankLineManager)
    }
}

private fun KotlinValueClassSpec.emitInTypeVariableRefs(
    codeWriter: KotlinCodeWriter,
    blankLineManager: BlankLineManager
) {
    emitPrimaryParameter(codeWriter)

    // Emit superinterfaces
    if (superinterfaces.isNotEmpty()) {
        codeWriter.emit(" : ")
        emitSuperinterfaces(codeWriter)
    }

    // Emit the body
    emitBody(codeWriter, blankLineManager)
}

private fun KotlinValueClassSpec.emitPrimaryParameter(codeWriter: KotlinCodeWriter) {
    val hasKDoc = !primaryParameter.kDoc.isEmpty()

    // Emit primary parameter
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
}
