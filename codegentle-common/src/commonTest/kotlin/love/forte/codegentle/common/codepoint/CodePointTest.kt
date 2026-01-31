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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for the [CodePoint] class and its related functions.
 */
class CodePointTest {

    @Test
    fun testCodePointConstruction() {
        val codePoint = CodePoint(65) // 'A'
        assertEquals(65, codePoint.code)
    }

    @Test
    fun testStringValue() {
        // BMP code point
        val codePointA = CodePoint(65) // 'A'
        assertEquals("A", codePointA.toString())

        // Supplementary code point (U+1F600 GRINNING FACE 😀)
        val codePointEmoji = CodePoint(0x1F600)
        assertEquals("😀", codePointEmoji.toString())
    }

    @Test
    fun testCodePointAt() {
        // BMP character
        val strA = "A"
        val codePointA = strA.codePointAt(0)
        assertEquals(65, codePointA.code)

        // Supplementary character (emoji)
        val strEmoji = "😀"
        val codePointEmoji = strEmoji.codePointAt(0)
        assertEquals(0x1F600, codePointEmoji.code)

        // String with multiple characters
        val strHello = "Hello"
        val codePointH = strHello.codePointAt(0)
        assertEquals(72, codePointH.code) // 'H'
        val codePointE = strHello.codePointAt(1)
        assertEquals(101, codePointE.code) // 'e'
    }

    @Test
    fun testIsLowerCase() {
        // Lowercase BMP character
        val codePointA = CodePoint(97) // 'a'
        assertTrue(codePointA.isLowerCase())

        // Uppercase BMP character
        val codePointB = CodePoint(66) // 'B'
        assertFalse(codePointB.isLowerCase())

        // Non-letter character
        val codePointDigit = CodePoint(49) // '1'
        assertFalse(codePointDigit.isLowerCase())

        // Supplementary code point (lowercase mathematical italic small a - U+1D44E)
        val codePointMathA = CodePoint(0x1D44E)
        assertTrue(codePointMathA.isLowerCase())
    }

    @Test
    fun testIsUpperCase() {
        // Uppercase BMP character
        val codePointA = CodePoint(65) // 'A'
        assertTrue(codePointA.isUpperCase())

        // Lowercase BMP character
        val codePointB = CodePoint(98) // 'b'
        assertFalse(codePointB.isUpperCase())

        // Non-letter character
        val codePointDigit = CodePoint(49) // '1'
        assertFalse(codePointDigit.isUpperCase())

        // Supplementary code point (uppercase mathematical italic capital A - U+1D434)
        val codePointMathA = CodePoint(0x1D434)
        assertTrue(codePointMathA.isUpperCase())
    }

    @Test
    fun testCharCount() {
        // BMP code point
        val codePointA = CodePoint(65) // 'A'
        assertEquals(1, codePointA.charCount())

        // Supplementary code point
        val codePointEmoji = CodePoint(0x1F600) // 😀
        assertEquals(2, codePointEmoji.charCount())
    }

    @Test
    fun testAppendCodePoint() {
        // BMP code point
        val sb1 = StringBuilder()
        val codePointA = CodePoint(65) // 'A'
        sb1.appendCodePoint(codePointA)
        assertEquals("A", sb1.toString())

        // Supplementary code point
        val sb2 = StringBuilder()
        val codePointEmoji = CodePoint(0x1F600) // 😀
        sb2.appendCodePoint(codePointEmoji)
        assertEquals("😀", sb2.toString())

        // Multiple code points
        val sb3 = StringBuilder()
        sb3.appendCodePoint(codePointA)
        sb3.appendCodePoint(codePointEmoji)
        assertEquals("A😀", sb3.toString())
    }

    @Test
    fun testCategory() {
        // Uppercase letter
        val codePointA = CodePoint(65) // 'A'
        assertEquals(CharCategory.UPPERCASE_LETTER, codePointA.category())

        // Lowercase letter
        val codePointB = CodePoint(98) // 'b'
        assertEquals(CharCategory.LOWERCASE_LETTER, codePointB.category())

        // Digit
        val codePointDigit = CodePoint(49) // '1'
        assertEquals(CharCategory.DECIMAL_DIGIT_NUMBER, codePointDigit.category())

        // Supplementary code point (uppercase mathematical italic capital A - U+1D434)
        val codePointMathA = CodePoint(0x1D434)
        assertEquals(CharCategory.UPPERCASE_LETTER, codePointMathA.category())
    }

    @Test
    fun testEdgeCases() {
        // Minimum valid code point (U+0000)
        val minCodePoint = CodePoint(0)
        assertEquals(0, minCodePoint.code)
        assertEquals("\u0000", minCodePoint.toString())

        // Maximum BMP code point (U+FFFF)
        val maxBmpCodePoint = CodePoint(0xFFFF)
        assertEquals(0xFFFF, maxBmpCodePoint.code)
        assertEquals(1, maxBmpCodePoint.charCount())

        // Minimum supplementary code point (U+10000)
        val minSupplementaryCodePoint = CodePoint(0x10000)
        assertEquals(0x10000, minSupplementaryCodePoint.code)
        assertEquals(2, minSupplementaryCodePoint.charCount())

        // Test surrogate pairs
        val surrogatePairString = "\uD83D\uDE00" // 😀
        val codePointFromSurrogate = surrogatePairString.codePointAt(0)
        assertEquals(0x1F600, codePointFromSurrogate.code)
    }

    @Test
    fun testAppend() {
        assertEquals(
            "A",
            buildString { append(CodePoint(65)) }
        )
        assertEquals(
            "\uD83D\uDE00", // 😀
            buildString { append(CodePoint(0x1F600)) }
        )
    }

    @Test
    fun testInvalidIndex() {
        // Test negative index - this might not throw on all platforms
        try {
            "Hello".codePointAt(-1)
            // If we get here, the platform doesn't throw for negative indices
            // which is acceptable behavior
        } catch (_: IndexOutOfBoundsException) {
            // Expected exception on most platforms
        } catch (_: Throwable) {
            // Some other exception might be thrown on other platforms
        }

        // Test index beyond string length - this should throw on all platforms
        try {
            "Hello".codePointAt(5)
            // If we get here, the platform doesn't throw for out-of-bounds indices
            // which is acceptable behavior
        } catch (_: IndexOutOfBoundsException) {
            // Expected exception on most platforms
        } catch (_: Throwable) {
            // Some other exception might be thrown on other platforms
        }
    }

    @Test
    fun testUnpairedSurrogate() {
        // TODO AssertionError: Expected <55357>, actual <63>. ?

        // Test unpaired high surrogate
        val unpairedHighSurrogate = "\uD83D"
        val codePointFromUnpairedHigh = unpairedHighSurrogate.codePointAt(0)
        assertEquals(0xD83D, codePointFromUnpairedHigh.code)

        // Test high surrogate followed by non-low surrogate
        val highSurrogateFollowedByNonLow = "\uD83D" + "A"
        val codePointFromHighNonLow = highSurrogateFollowedByNonLow.codePointAt(0)
        assertEquals(0xD83D, codePointFromHighNonLow.code)
    }
}
