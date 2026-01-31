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
package love.forte.codegentle.common.codepoint

@Suppress("UNUSED_PARAMETER")
internal expect fun jsCodePointAt(str: String, index: Int): Int

@Suppress("UNUSED_PARAMETER")
internal fun jsCodePointAtInternal(str: String, index: Int): Int {
    // Handle potential issues with unpaired surrogates by implementing explicit logic
    // similar to the native implementation
    if (index < 0 || index >= str.length) {
        throw IndexOutOfBoundsException("String index out of range: $index")
    }

    val char = str[index]
    if (!char.isHighSurrogate() || index + 1 >= str.length) {
        // If it's not a high surrogate or it's the last character,
        // return the code point of the character
        return char.code
    }

    val nextChar = str[index + 1]
    if (!nextChar.isLowSurrogate()) {
        // If the next character is not a low surrogate,
        // return the code point of the current character (unpaired surrogate)
        return char.code
    }

    // Calculate the code point from the surrogate pair
    val highSurrogate = char.code - Char.MIN_HIGH_SURROGATE.code
    val lowSurrogate = nextChar.code - Char.MIN_LOW_SURROGATE.code
    val codePoint = 0x10000 + (highSurrogate shl 10) + lowSurrogate

    return codePoint
}

@Suppress("UNUSED_PARAMETER")
internal expect fun jsFromCodePoint(code: Int): String

@InternalCodePointApi
public actual fun String.codePointAt(index: Int): CodePoint {
    val code = jsCodePointAt(this, index)
    return CodePoint(code)
}

internal actual fun CodePoint.stringValue(): String =
    jsFromCodePoint(code)
