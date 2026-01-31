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
package love.forte.codegentle.kotlin.ref

import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit an [AnnotationRef] to a [KotlinCodeWriter].
 */
internal fun AnnotationRef.emitTo(codeWriter: KotlinCodeWriter) {
    codeWriter.emit("@")
    emitStatus(codeWriter)
    codeWriter.emit(typeName)

    // Handle annotation parameters
    if (members.isNotEmpty()) {
        codeWriter.emit("(")
        var first = true
        for ((name, values) in members) {
            if (!first) {
                codeWriter.emit(", ")
            }
            first = false

            // Emit parameter name
            if (name.isNotEmpty()) {
                codeWriter.emit(name)
                codeWriter.emit(" = ")
            }

            when (values) {
                is AnnotationRef.MemberValue.Single -> {
                    codeWriter.emit(values.codeValue)
                }

                is AnnotationRef.MemberValue.Multiple -> {
                    // Multiple values - emit as array
                    codeWriter.emit("[")
                    var firstValue = true
                    for (value in values.codeValues) {
                        if (!firstValue) {
                            codeWriter.emit(", ")
                        }
                        firstValue = false
                        codeWriter.emit(value)
                    }
                    codeWriter.emit("]")
                }
            }
        }
        codeWriter.emit(")")
    }
}

private fun AnnotationRef.emitStatus(codeWriter: KotlinCodeWriter) {
    val kotlinStatus = status as? KotlinAnnotationRefStatus
    val useSite = kotlinStatus?.useSite
    if (useSite != null) {
        codeWriter.emit(useSite.name.lowercase())
        codeWriter.emit(":")
    }
}
