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
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.spec.KotlinGetterSpec
import love.forte.codegentle.kotlin.spec.KotlinSetterSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinGetterSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinGetterSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Emit KDoc if present
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers)

    codeWriter.emit("get")

    // If getter has code, emit it as a block
    if (code.isNotEmpty()) {
        if (code.isStartWithReturn() && codeWriter.strategy.replaceReturnWithExpressionBody()) {
            val replacedCode = code.removeFirstReturn()
            codeWriter.emit("() = ")
            codeWriter.withIndent {
                codeWriter.emit(replacedCode)
            }
        } else {
            codeWriter.emit("() {")
            codeWriter.withIndent {
                codeWriter.emitNewLine()
                codeWriter.emit(code)
            }
            codeWriter.emitNewLine()
            codeWriter.emit("}")
        }
    }
}

/**
 * Extension function to emit a [KotlinSetterSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinSetterSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Emit KDoc if present
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit modifiers
    codeWriter.emitModifiers(modifiers)

    codeWriter.emit("set")

    // If setter has code, emit it as a block with parameter
    if (code.isNotEmpty()) {
        codeWriter.emit("(${parameterName}) {")
        codeWriter.indent()
        codeWriter.emitNewLine()
        codeWriter.emit(code)
        codeWriter.unindent()
        codeWriter.emitNewLine()
        codeWriter.emit("}")
    }
}
