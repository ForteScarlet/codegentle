/*
 * Copyright (C) 2014-2024 Square, Inc.
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
package love.forte.codegentle.kotlin

import love.forte.codegentle.common.appendCharacterLiteral

internal fun String?.stringLiteralWithQuotes(indent: String, ignoreStringInterpolation: Boolean = true): String {
    if (this == null) return "null"
    val result = StringBuilder(this.length + 32)
    result.append('"')
    for ((i, c) in this.withIndex()) {
        when (c) {
            '"' -> result.append("\\\"")
            '\\' -> result.append("\\\\")
            // '\n' -> result.append("\\n\"\n$indent$indent+ \"")
            '\n' -> {
                if (i != this.lastIndex) {
                    result.append("\\n\"\n").append(indent).append(indent).append("+ \"")
                } else {
                    result.append("\\n")
                }
            }

            '\r' -> result.append("\\r")
            '\t' -> result.append("\\t")
            else -> result.appendCharacterLiteralWithoutSingleQuotes(c, ignoreStringInterpolation)
        }
    }
    result.append('"')
    return result.toString()
}

internal fun Appendable.appendCharacterLiteralWithoutSingleQuotes(c: Char, ignoreStringInterpolation: Boolean) {
    if (!appendCharacterLiteral(c)) {
        when (c) {
            '$' if ignoreStringInterpolation -> append("\\$")
            '\u000c' -> append("\\u000c") // \f \u000c: form feed (FF)
            else -> append(c)
        }
    }
}
