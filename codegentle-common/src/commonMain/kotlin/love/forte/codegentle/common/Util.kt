/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2014-2026 Forte Scarlet
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
package love.forte.codegentle.common

import love.forte.codegentle.common.codepoint.CodePoint
import love.forte.codegentle.common.codepoint.codePoint

@InternalCommonCodeGentleApi
public expect fun <K, V> MutableMap<K, V>.computeValue(key: K, f: (K, V?) -> V?): V?

@InternalCommonCodeGentleApi
public expect fun <K, V> MutableMap<K, V>.computeValueIfAbsent(key: K, f: (K) -> V): V

/**
 * If appended, return `true`.
 */
@InternalCommonCodeGentleApi
public inline fun Appendable.appendCharacterLiteral(c: Char, onElse: (Char) -> Boolean = { false }): Boolean {
    // see https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6
    when (c) {
        '\b' -> append("\\b")  // \u0008: backspace (BS)
        '\t' -> append("\\t")  // \u0009: horizontal tab (HT)
        '\n' -> append("\\n")  // \u000a: linefeed (LF)
        '\r' -> append("\\r")  // \u000d: carriage return (CR)
        '\"' -> append("\"")   // \u0022: double quote (")
        '\\' -> append("\\\\") // \u005c: backslash (\)
        else -> {
            if (onElse(c)) {
                return true
            }

            if (c.isISOControl()) {
                append(c.codePoint().formatIsoControlCode())
            } else {
                return false
            }
        }
    }

    return true
}

@PublishedApi
internal fun CodePoint.formatIsoControlCode(): String =
    "\\u${code.toHexStr().padStart(4, '0')}"

private fun Int.toHexStr(): String =
    toUInt().toString(16)
