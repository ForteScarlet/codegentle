/*
 * Copyright (C) 2025 Forte Scarlet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.code.isNotEmpty
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.common.writer.withIndentBlock
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.VISIBILITY_MODIFIERS
import love.forte.codegentle.kotlin.spec.KotlinEnumTypeSpec
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
import love.forte.codegentle.kotlin.spec.isMemberNotEmpty
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType
import love.forte.codegentle.kotlin.writer.resolveDefaultVisibility

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
    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    codeWriter.emitModifiers(codeWriter.resolveDefaultVisibility(modifiers))

    // Emit the enum class keyword
    codeWriter.emit(KotlinTypeSpec.Kind.CLASS, true)

    // Emit the name
    codeWriter.emit(name)

    // Emit type variables
    codeWriter.emitTypeVariableRefs(typeVariables)

    // Emit primary constructor
    emitPrimaryConstructor(codeWriter)

    // Emit superinterfaces
    if (superinterfaces.isNotEmpty()) {
        codeWriter.emit(" : ")
        emitSuperinterfaces(codeWriter)
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
        emitEnumConstants(codeWriter, blankLineManager)
        // Emit secondary constructors
        emitSecondaryConstructors(codeWriter, blankLineManager)
        // Emit other type members
        emitMembers(codeWriter, blankLineManager)
    }
}

private fun KotlinEnumTypeSpec.emitEnumConstants(
    codeWriter: KotlinCodeWriter,
    blankLineManager: BlankLineManager
) {
    if (enumConstants.isNotEmpty()) {
        for ((index, entry) in enumConstants.entries.withIndex()) {
            val (constantName, anonymousClass) = entry
            // Add comma and newline for all but the last constant
            val anonymousClassKDoc = anonymousClass?.kDoc
            val anonymousClassKDocNotEmpty = anonymousClassKDoc?.isNotEmpty() == true

            if (index in 1..enumConstants.size - 1) {
                codeWriter.emit(",")
                codeWriter.emitNewLine()

                // pre item has body, or current item has kdoc, emit a new line.
                if (blankLineManager.blankLineRequired || anonymousClassKDocNotEmpty) {
                    codeWriter.emitNewLine()
                    blankLineManager.clear()
                }
            }

            // emit kdoc
            if (anonymousClassKDocNotEmpty) {
                codeWriter.emitDoc(anonymousClassKDoc)
            }

            // emit annotations
            if (anonymousClass?.annotations?.isNotEmpty() == true) {
                codeWriter.emitAnnotationRefs(anonymousClass.annotations, false)
            }

            codeWriter.emit(constantName)

            // If the enum constant has an anonymous class implementation
            anonymousClass?.emitTo(codeWriter, true)

            if (anonymousClass?.isMemberNotEmpty() == true) {
                blankLineManager.required()
            }
        }

        // Add semicolon after enum constants if there are other members
        val hasOtherMembers =
            secondaryConstructors.isNotEmpty()
                || initializerBlock.isNotEmpty()
                || properties.isNotEmpty()
                || functions.isNotEmpty()
                || subtypes.isNotEmpty()

        if (hasOtherMembers) {
            codeWriter.emit(";")
            blankLineManager.required()
        }
        codeWriter.emitNewLine()
    } else {
        codeWriter.emitNewLine(";")
    }
}

private fun KotlinEnumTypeSpec.emitPrimaryConstructor(codeWriter: KotlinCodeWriter) {
    val primary = this.primaryConstructor
    if (primary != null) {
        if (primary.modifiers.any { it in VISIBILITY_MODIFIERS }) {
            codeWriter.emit(" ")
        }
        primary.emitTo(codeWriter, true)
    }
}

private fun KotlinEnumTypeSpec.emitSecondaryConstructors(
    codeWriter: KotlinCodeWriter,
    blankLineManager: BlankLineManager
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
