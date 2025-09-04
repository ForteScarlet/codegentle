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
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.spec.KotlinPropertySpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.resolveDefaultVisibility

/**
 * Extension function to emit a [KotlinPropertySpec] to a [KotlinCodeWriter].
 */
internal fun KotlinPropertySpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(codeWriter.resolveDefaultVisibility(modifiers))

    // Emit the property keyword (val or var)
    codeWriter.emit(if (mutable) "var " else "val ")

    // Emit the name
    codeWriter.emit(name)

    // Emit the type
    codeWriter.emit(": ")
    codeWriter.emit(typeRef)

    // Emit initializer or delegate if present
    initializer?.let { init ->
        codeWriter.emit(" = ")
        codeWriter.emit(init)
    }

    delegate?.let { del ->
        codeWriter.emit(" by ")
        codeWriter.emit(del)
    }

    // Check if we have custom getter or setter
    val hasCustomAccessors = getter != null || setter != null

    if (hasCustomAccessors) {
        codeWriter.withIndent {
            // Emit getter if present
            getter?.let { get ->
                codeWriter.emitNewLine()
                get.emitTo(codeWriter)
            }

            // Emit setter if present
            setter?.let { set ->
                codeWriter.emitNewLine()
                set.emitTo(codeWriter)
            }
        }
    }
}
