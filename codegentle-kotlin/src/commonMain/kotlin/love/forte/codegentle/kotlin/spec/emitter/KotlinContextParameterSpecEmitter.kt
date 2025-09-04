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

import love.forte.codegentle.kotlin.spec.KotlinContextParameterSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinContextParameterSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinContextParameterSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Emit the name or underscore
    val paramName = name
    if (paramName != null) {
        codeWriter.emit(paramName)
    } else {
        codeWriter.emit("_")
    }

    // Emit the type
    codeWriter.emit(": ")
    codeWriter.emit(typeRef)
}
