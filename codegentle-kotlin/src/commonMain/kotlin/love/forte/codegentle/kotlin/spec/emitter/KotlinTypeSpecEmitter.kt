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

import love.forte.codegentle.common.code.isNotEmpty
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.common.writer.CodeWriter
import love.forte.codegentle.common.writer.withIndentBlock
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [KotlinTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    when (this) {
        is KotlinSimpleTypeSpec -> emitTo(codeWriter)
        is KotlinValueClassTypeSpec -> emitTo(codeWriter)
        is KotlinAnnotationTypeSpec -> emitTo(codeWriter)
        is KotlinAnonymousClassTypeSpec -> emitTo(codeWriter)
        is KotlinEnumTypeSpec -> emitTo(codeWriter)
        is KotlinObjectTypeSpec -> emitTo(codeWriter)
        is KotlinTypealiasSpec -> emitTo(codeWriter)
    }
}

internal fun CodeWriter.emit(kind: KotlinTypeSpec.Kind, followSpace: Boolean = false) =
    emit(if (followSpace) "${kind.keyword} " else kind.keyword)

internal inline fun KotlinTypeSpec.emitAndWithTypeVariableRefs(codeWriter: KotlinCodeWriter, block: () -> Unit) {
    codeWriter.emitTypeVariableRefs(typeVariables)
    try {
        block()
    } finally {
        codeWriter.popTypeVariableRefs(typeVariables)
    }
}

/**
 * Emit body if [isMemberNotEmpty].
 * ```Kotlin
 *  {
 *   // members
 * }
 * ```
 */
internal inline fun KotlinTypeSpec.emitBody(
    codeWriter: KotlinCodeWriter,
    blankLineManager: BlankLineManager,
    prefix: String = " ",
    newLine: Boolean = true,
    bodyHead: String = "{",
    bodyTail: String = "}",
    beforeEmitMember: KotlinCodeWriter.() -> Unit = {}
) {
    if (isMemberNotEmpty()) {
        codeWriter.withIndentBlock(
            prefix = prefix,
            newLine = newLine,
            bodyHead = bodyHead,
            bodyTail = bodyTail,
        ) {
            beforeEmitMember()
            emitMembers(codeWriter, blankLineManager)
        }
    }
}

/**
 * Emit:
 * - properties
 * - functions
 * - subtypes
 */
internal fun KotlinTypeSpec.emitMembers(codeWriter: KotlinCodeWriter, blankLineManager: BlankLineManager) {
    if (initializerBlock.isNotEmpty()) {
        emitInitializerBlock(codeWriter, blankLineManager)
    }
    emitProperties(codeWriter, blankLineManager)
    emitFunctions(codeWriter, blankLineManager)
    emitSubtypes(codeWriter, blankLineManager)
}

internal fun KotlinTypeSpec.emitInitializerBlock(codeWriter: KotlinCodeWriter, blankLineManager: BlankLineManager) {
    if (initializerBlock.isNotEmpty()) {
        blankLineManager.withRequirement {
            codeWriter.withIndentBlock(prefix = "init") {
                emit(initializerBlock)
                emitNewLine()
            }
            codeWriter.emitNewLine()
        }
    }
}

internal fun KotlinTypeSpec.emitProperties(codeWriter: KotlinCodeWriter, blankLineManager: BlankLineManager) {
    if (properties.isNotEmpty()) {
        for (property in properties) {
            blankLineManager.withRequirement {
                property.emitTo(codeWriter)
                codeWriter.emitNewLine()
            }
        }
    }
}

internal fun KotlinTypeSpec.emitFunctions(codeWriter: KotlinCodeWriter, blankLineManager: BlankLineManager) {
    if (functions.isNotEmpty()) {
        for (function in functions) {
            blankLineManager.withRequirement {
                function.emitTo(codeWriter)
                codeWriter.emitNewLine()
            }
        }
    }
}

internal fun KotlinTypeSpec.emitSubtypes(codeWriter: KotlinCodeWriter, blankLineManager: BlankLineManager) {
    if (subtypes.isNotEmpty()) {
        for (subtype in subtypes) {
            blankLineManager.withRequirement {
                subtype.emitTo(codeWriter)
                codeWriter.emitNewLine()
            }
        }
    }
}

internal fun KotlinTypeSpec.emitSuperinterfaces(codeWriter: KotlinCodeWriter) {
    superinterfaces.forEachIndexed { index, typeName ->
        if (index > 0) codeWriter.emit(", ")
        codeWriter.emit(typeName)
    }
}
