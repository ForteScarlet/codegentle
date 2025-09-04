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
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinObjectTypeSpec
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType
import love.forte.codegentle.kotlin.writer.resolveDefaultVisibility

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
    codeWriter.emitModifiers(codeWriter.resolveDefaultVisibility(modifiers))

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
