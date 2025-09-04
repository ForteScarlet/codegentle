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
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinTypealiasSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.resolveDefaultVisibility

/**
 * Extension function to emit a [KotlinTypealiasSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinTypealiasSpec.emitTo(
    codeWriter: KotlinCodeWriter,
    implicitModifiers: Set<KotlinModifier> = emptySet()
) {
    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(codeWriter.resolveDefaultVisibility(modifiers), implicitModifiers)

    // Emit the typealias keyword
    codeWriter.emit("typealias ")

    // Emit the name
    codeWriter.emit(name)

    // Emit type variables if any
    if (typeVariables.isNotEmpty()) {
        codeWriter.emit("<")
        typeVariables.forEachIndexed { index, typeVariable ->
            if (index > 0) codeWriter.emit(", ")
            codeWriter.emit(typeVariable)
        }
        codeWriter.emit(">")
    }

    // Emit the assignment
    codeWriter.emit(" = ")

    // Emit the target type
    codeWriter.emit(type)
}

/**
 * Extension function to emit a [KotlinTypealiasSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinTypealiasSpec.emitTo(codeWriter: KotlinCodeWriter) {
    emitTo(codeWriter, emptySet())
}
