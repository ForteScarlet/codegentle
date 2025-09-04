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
package love.forte.codegentle.common.codepoint

internal actual fun CodePoint.stringValue(): String {
    return if (code <= Char.MAX_VALUE.code) {
        Char(code).toString()
    } else {
        // For supplementary code points, we need to use surrogate pairs
        val highSurrogate = Char.MIN_HIGH_SURROGATE + ((code - 0x10000) shr 10)
        val lowSurrogate = Char.MIN_LOW_SURROGATE + (code and 0x3ff)
        return charArrayOf(highSurrogate, lowSurrogate).concatToString()
        // highSurrogate.toString() + lowSurrogate.toString()
    }
}

/**
 * Returns the code point at the specified index in the string.
 *
 * If the character at the specified index does not form a surrogate pair, this method returns
 * the code point of the single character. If a valid high-surrogate character is followed
 * by a low-surrogate character, this method calculates and returns the Unicode code point
 * represented by the surrogate pair.
 *
 * @param index The position in the string from which to retrieve the Unicode code point.
 *               The value must be within the valid range of indices for the string.
 * @return The [CodePoint] representing the Unicode code point at the specified index.
 * @throws IndexOutOfBoundsException If the index is less than 0 or greater than or equal to the length of the string.
 */
@InternalCodePointApi
public actual fun String.codePointAt(index: Int): CodePoint {
    if (index < 0 || index >= length) {
        throw IndexOutOfBoundsException("String index out of range: $index")
    }

    val char = this[index]
    if (!char.isHighSurrogate() || index + 1 >= length) {
        // If it's not a high surrogate or it's the last character,
        // return the code point of the character
        return CodePoint(char.code)
    }

    val nextChar = this[index + 1]
    if (!nextChar.isLowSurrogate()) {
        // If the next character is not a low surrogate,
        // return the code point of the current character
        return CodePoint(char.code)
    }

    // Calculate the code point from the surrogate pair
    val highSurrogate = char.code - Char.MIN_HIGH_SURROGATE.code
    val lowSurrogate = nextChar.code - Char.MIN_LOW_SURROGATE.code
    val codePoint = 0x10000 + (highSurrogate shl 10) + lowSurrogate

    return CodePoint(codePoint)
}
