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
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.VISIBILITY_MODIFIERS
import love.forte.codegentle.kotlin.spec.KotlinConstructorDelegation
import love.forte.codegentle.kotlin.spec.KotlinConstructorSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinConstructorSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinConstructorSpec.emitTo(codeWriter: KotlinCodeWriter, isPrimary: Boolean = false) {
    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    val hasVisibilityModifiers = modifiers.any { it in VISIBILITY_MODIFIERS }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers, emptySet())

    // Emit constructor keyword if not primary
    if (!isPrimary || hasVisibilityModifiers) {
        codeWriter.emit("constructor")
    }

    // Emit parameters
    codeWriter.emit("(")
    parameters.forEachIndexed { index, parameter ->
        if (index > 0) codeWriter.emit(", ")
        parameter.emitTo(codeWriter)
    }
    codeWriter.emit(")")

    // Emit constructor delegation if present
    val delegation = constructorDelegation
    if (!isPrimary && delegation != null) {
        codeWriter.emit(" : ")
        when (delegation.kind) {
            KotlinConstructorDelegation.Kind.THIS -> codeWriter.emit("this")
            KotlinConstructorDelegation.Kind.SUPER -> codeWriter.emit("super")
        }
        codeWriter.emit("(")
        delegation.arguments.forEachIndexed { index, argument ->
            if (index > 0) codeWriter.emit(", ")
            codeWriter.emit(argument)
        }
        codeWriter.emit(")")
    }

    // Emit constructor body if not empty
    if (!code.isEmpty()) {
        codeWriter.emitNewLine(" {")
        codeWriter.withIndent {
            emit(code)
        }
        codeWriter.emitNewLine()
        codeWriter.emit("}")
    }
}
