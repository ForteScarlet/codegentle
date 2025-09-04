/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2014-2025 Forte Scarlet
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
package love.forte.codegentle.common.writer

import love.forte.codegentle.common.InternalCommonCodeGentleApi
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef

/**
 * A code writer.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleCodeWriterImplementation::class)
public interface CodeWriter {
    public val strategy: Strategy
    public val indentValue: String

    public val staticImports: Set<String> // String? or a MemberName?
    public val alwaysQualify: Set<String>

    public fun indent(levels: Int = 1)

    public fun unindent(levels: Int = 1)

    // comments and javadocs

    public fun emitComment(comment: CodeValue, vararg options: CodeValueEmitOption)

    public fun emitDoc(doc: CodeValue, vararg options: CodeValueEmitOption)

    public fun emit(code: CodeValue, vararg options: CodeValueEmitOption)

    public fun emit(typeName: TypeName, vararg options: TypeNameEmitOption)

    public fun emit(typeRef: TypeRef<*>, vararg options: TypeRefEmitOption)

    public fun emit(annotationRef: AnnotationRef, vararg options: AnnotationRefEmitOption)

    public fun emit(s: String)

    public fun emitNewLine(s: String? = null) {
        if (s != null) {
            emit(s + strategy.newline())
        } else {
            emit(strategy.newline())
        }
    }

    public companion object {
        public const val DEFAULT_COLUMN_LIMIT: Int = Int.MAX_VALUE
        public const val DEFAULT_INDENT: String = "    "
    }
}

public inline fun <C : CodeWriter> C.withIndent(
    levels: Int = 1,
    block: C.() -> Unit
) {
    indent(levels)
    try {
        block()
    } finally {
        unindent(levels)
    }
}

@InternalCommonCodeGentleApi
public inline fun <C : CodeWriter> C.withIndentBlock(
    levels: Int = 1,
    prefix: String = "",
    newLine: Boolean = true,
    bodyHead: String = "{",
    bodyTail: String = "}",
    block: C.() -> Unit
) {
    if (prefix.isNotEmpty()) {
        emit(prefix)
        if (!prefix.last().isWhitespace()) {
            emit(" ")
        }
    }
    emit(bodyHead)
    if (newLine) {
        emitNewLine()
    } else {
        emit(" ")
    }
    indent(levels)
    try {
        block()
    } finally {
        unindent(levels)
        emit(bodyTail)
    }
}

