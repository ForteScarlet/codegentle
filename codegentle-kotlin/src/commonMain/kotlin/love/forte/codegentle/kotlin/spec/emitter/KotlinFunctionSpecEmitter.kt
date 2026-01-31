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
import love.forte.codegentle.common.code.isNotEmpty
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.naming.KotlinClassNames
import love.forte.codegentle.kotlin.ref.kotlinOrNull
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeValueEmitOption
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.resolveDefaultVisibility

/**
 * Extension function to emit a [KotlinFunctionSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinFunctionSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Emit KDoc
    if (!kDoc.isEmpty()) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit context parameters if any
    if (contextParameters.isNotEmpty()) {
        codeWriter.emit("context(")
        contextParameters.forEachIndexed { index, param ->
            if (index > 0) codeWriter.emit(", ")
            param.emitTo(codeWriter)
        }
        codeWriter.emit(")\n")
    }

    // Emit modifiers
    codeWriter.emitModifiers(codeWriter.resolveDefaultVisibility(modifiers))

    // Emit the function keyword
    codeWriter.emit("fun ")

    // Emit type variables if any
    if (typeVariables.isNotEmpty()) {
        codeWriter.emitTypeVariableRefs(typeVariables)
        codeWriter.emit(" ")
    }

    // Emit receiver if any
    receiver?.let { recv ->
        codeWriter.emit(recv)
        codeWriter.emit(".")
    }

    // Emit the name
    codeWriter.emit(name)

    // Emit parameters
    codeWriter.emit("(")
    val mutableLine = parameters.any { it.kDoc.isNotEmpty() }
    if (mutableLine) {
        codeWriter.emitNewLine()
        codeWriter.indent()
    }

    parameters.forEachIndexed { index, param ->
        if (index > 0) {
            if (mutableLine) {
                codeWriter.emitNewLine(",")
            } else {
                codeWriter.emit(", ")
            }
        }
        param.emitTo(codeWriter)
    }

    if (mutableLine) {
        codeWriter.unindent()
        codeWriter.emitNewLine()
        codeWriter.emit(")")
    } else {
        codeWriter.emit(")")
    }

    val isReplaceReturnWithExpressionBody = !code.isEmpty()
        && code.isStartWithReturn()
        && codeWriter.strategy.replaceReturnWithExpressionBody()

    val isReturnTypeUnit = returnType.typeName == KotlinClassNames.UNIT
        && returnType.status.kotlinOrNull?.nullable != true

    // Emit the return type if it != Unit || it == `Unit?`
    if (!(isReturnTypeUnit && codeWriter.strategy.omitFunctionUnitReturnType())) {
        codeWriter.emit(": ")
        codeWriter.emit(returnType)
    }

    if (!code.isEmpty()) {
        if (isReplaceReturnWithExpressionBody && !isReturnTypeUnit) {
            val replacedCode = code.removeFirstReturn()
            codeWriter.emit(" = ")
            codeWriter.withIndent { emit(replacedCode) }
        } else {
            codeWriter.emitNewLine(" {")
            codeWriter.withIndent {
                emit(code, KotlinCodeValueEmitOption.EnsureTrailingNewline)
            }
            codeWriter.emit("}")
        }
    }

    // Pop type variables from scope
    codeWriter.popTypeVariableRefs(typeVariables)
}
