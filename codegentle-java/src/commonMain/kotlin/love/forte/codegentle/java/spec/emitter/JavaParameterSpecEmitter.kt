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
package love.forte.codegentle.java.spec.emitter

import love.forte.codegentle.java.spec.JavaParameterSpec
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.JavaTypeNameEmitOption.Vararg
import love.forte.codegentle.java.writer.JavaTypeRefEmitOption.TypeNameOptions

/**
 * Extension function to emit a [JavaParameterSpec] to a [JavaCodeWriter].
 */
internal fun JavaParameterSpec.emitTo(
    codeWriter: JavaCodeWriter,
    vararg: Boolean = false
) {
    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, true)
    
    // Emit modifiers
    codeWriter.emitModifiers(modifiers)

    // Emit type with vararg support
    if (vararg) {
        codeWriter.emit(type, TypeNameOptions(Vararg))
    } else {
        codeWriter.emit(type)
    }

    // Emit parameter name
    codeWriter.emit(" $name")
}

/**
 * Extension function to emit a [JavaParameterSpec] to a [JavaCodeWriter].
 */
internal fun JavaParameterSpec.emitTo(codeWriter: JavaCodeWriter) {
    emitTo(codeWriter, false)
}
