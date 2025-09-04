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
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinValueParameterSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinValueParameterSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Skip emitting KDoc for parameters to match the expected output in tests
    if (!(kDoc.isEmpty())) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers)

    // Emit propertyization (val/var) if present
    propertyfication?.let { prop ->
        if (prop.mutable) {
            codeWriter.emit("var ")
        } else {
            codeWriter.emit("val ")
        }
    }

    // Emit the name
    codeWriter.emit(name)

    // Emit the type
    codeWriter.emit(": ")
    codeWriter.emit(typeRef)

    // Emit default value if present
    defaultValue?.let { value ->
        codeWriter.emit(" = ")
        codeWriter.emit(value)
    }
}
