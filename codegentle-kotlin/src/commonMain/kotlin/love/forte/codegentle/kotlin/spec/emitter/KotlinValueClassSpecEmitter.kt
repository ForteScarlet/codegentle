/*
 * Copyright (C) 2025-2026 Forte Scarlet
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
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
import love.forte.codegentle.kotlin.spec.KotlinValueClassTypeSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType
import love.forte.codegentle.kotlin.writer.resolveDefaultVisibility

/**
 * Extension function to emit a [KotlinValueClassTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinValueClassTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    require(KotlinModifier.VALUE in modifiers) {
        "Value class spec must have VALUE modifier, but $modifiers"
    }

    codeWriter.inType(this) {
        emitTo0(codeWriter)
    }
}

private fun KotlinValueClassTypeSpec.emitTo0(codeWriter: KotlinCodeWriter) {
    val blankLineManager = BlankLineManager(codeWriter)

    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    codeWriter.emitModifiers(codeWriter.resolveDefaultVisibility(modifiers))

    // Emit the value class keyword
    codeWriter.emit(KotlinTypeSpec.Kind.CLASS, true)

    // Emit the name
    codeWriter.emit(name)

    // Emit type variables
    emitAndWithTypeVariableRefs(codeWriter) {
        emitInTypeVariableRefs(codeWriter, blankLineManager)
    }
}

private fun KotlinValueClassTypeSpec.emitInTypeVariableRefs(
    codeWriter: KotlinCodeWriter,
    blankLineManager: BlankLineManager
) {
    emitPrimaryConstructor(codeWriter)

    // Emit superinterfaces
    if (superinterfaces.isNotEmpty()) {
        codeWriter.emit(" : ")
        emitSuperinterfaces(codeWriter)
    }

    // Emit the body
    emitBody(
        codeWriter,
        blankLineManager,
        beforeEmitMember = {
            emitSecondaryConstructors(blankLineManager, codeWriter)
        }
    )
}

private fun KotlinValueClassTypeSpec.emitSecondaryConstructors(
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

private fun KotlinValueClassTypeSpec.emitPrimaryConstructor(codeWriter: KotlinCodeWriter) {
    // Value class primary constructor should have exactly one parameter
    val parameter = primaryConstructor.parameters.first()
    val hasKDoc = !parameter.kDoc.isEmpty()

    // Emit primary constructor parameter
    codeWriter.emit("(")
    if (hasKDoc) {
        codeWriter.withIndent {
            codeWriter.emitNewLine()
            parameter.emitTo(codeWriter)
        }
        codeWriter.emitNewLine()
    } else {
        parameter.emitTo(codeWriter)
    }
    codeWriter.emit(")")
}
