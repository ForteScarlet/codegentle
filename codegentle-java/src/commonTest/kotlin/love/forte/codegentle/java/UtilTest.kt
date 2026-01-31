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
package love.forte.codegentle.java

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilTest {

    @Test
    fun testIsJavaIdentifierStartCommon_letters() {
        assertTrue('a'.isJavaIdentifierStartCommon())
        assertTrue('Z'.isJavaIdentifierStartCommon())
        assertTrue('中'.isJavaIdentifierStartCommon()) // Chinese character
        assertTrue('α'.isJavaIdentifierStartCommon()) // Greek letter
    }

    @Test
    fun testIsJavaIdentifierStartCommon_dollarSign() {
        assertTrue('$'.isJavaIdentifierStartCommon())
    }

    @Test
    fun testIsJavaIdentifierStartCommon_underscore() {
        assertTrue('_'.isJavaIdentifierStartCommon())
    }

    @Test
    fun testIsJavaIdentifierStartCommon_digits() {
        assertFalse('0'.isJavaIdentifierStartCommon())
        assertFalse('9'.isJavaIdentifierStartCommon())
    }

    @Test
    fun testIsJavaIdentifierStartCommon_invalidCharacters() {
        assertFalse(' '.isJavaIdentifierStartCommon())
        assertFalse('-'.isJavaIdentifierStartCommon())
        assertFalse('+'.isJavaIdentifierStartCommon())
        assertFalse('('.isJavaIdentifierStartCommon())
        assertFalse(')'.isJavaIdentifierStartCommon())
        assertFalse('['.isJavaIdentifierStartCommon())
        assertFalse(']'.isJavaIdentifierStartCommon())
        assertFalse('{'.isJavaIdentifierStartCommon())
        assertFalse('}'.isJavaIdentifierStartCommon())
        assertFalse('.'.isJavaIdentifierStartCommon())
        assertFalse(','.isJavaIdentifierStartCommon())
        assertFalse(';'.isJavaIdentifierStartCommon())
        assertFalse(':'.isJavaIdentifierStartCommon())
    }

    @Test
    fun testIsJavaIdentifierPartCommon_letters() {
        assertTrue('a'.isJavaIdentifierPartCommon())
        assertTrue('Z'.isJavaIdentifierPartCommon())
        assertTrue('中'.isJavaIdentifierPartCommon()) // Chinese character
        assertTrue('α'.isJavaIdentifierPartCommon()) // Greek letter
    }

    @Test
    fun testIsJavaIdentifierPartCommon_digits() {
        assertTrue('0'.isJavaIdentifierPartCommon())
        assertTrue('9'.isJavaIdentifierPartCommon())
        assertTrue('5'.isJavaIdentifierPartCommon())
    }

    @Test
    fun testIsJavaIdentifierPartCommon_dollarSign() {
        assertTrue('$'.isJavaIdentifierPartCommon())
    }

    @Test
    fun testIsJavaIdentifierPartCommon_underscore() {
        assertTrue('_'.isJavaIdentifierPartCommon())
    }

    @Test
    fun testIsJavaIdentifierPartCommon_identifierIgnorable() {
        // Test some identifier ignorable characters
        assertTrue('\u0000'.isJavaIdentifierPartCommon()) // NULL
        assertTrue('\u0001'.isJavaIdentifierPartCommon()) // SOH
        assertTrue('\u0008'.isJavaIdentifierPartCommon()) // BS
        assertTrue('\u000E'.isJavaIdentifierPartCommon()) // SO
        assertTrue('\u001B'.isJavaIdentifierPartCommon()) // ESC
        assertTrue('\u007F'.isJavaIdentifierPartCommon()) // DEL
        assertTrue('\u009F'.isJavaIdentifierPartCommon()) // APC
    }

    @Test
    fun testIsJavaIdentifierPartCommon_invalidCharacters() {
        assertFalse(' '.isJavaIdentifierPartCommon())
        assertFalse('-'.isJavaIdentifierPartCommon())
        assertFalse('+'.isJavaIdentifierPartCommon())
        assertFalse('('.isJavaIdentifierPartCommon())
        assertFalse(')'.isJavaIdentifierPartCommon())
        assertFalse('['.isJavaIdentifierPartCommon())
        assertFalse(']'.isJavaIdentifierPartCommon())
        assertFalse('{'.isJavaIdentifierPartCommon())
        assertFalse('}'.isJavaIdentifierPartCommon())
        assertFalse('.'.isJavaIdentifierPartCommon())
        assertFalse(','.isJavaIdentifierPartCommon())
        assertFalse(';'.isJavaIdentifierPartCommon())
        assertFalse(':'.isJavaIdentifierPartCommon())
    }

    @Test
    fun testIsIdentifierIgnorable_isoControlCharacters() {
        // Test ISO control characters that are not whitespace
        assertTrue('\u0000'.isIdentifierIgnorable()) // NULL
        assertTrue('\u0001'.isIdentifierIgnorable()) // SOH
        assertTrue('\u0008'.isIdentifierIgnorable()) // BS
        assertTrue('\u000E'.isIdentifierIgnorable()) // SO
        assertTrue('\u001B'.isIdentifierIgnorable()) // ESC
        assertTrue('\u007F'.isIdentifierIgnorable()) // DEL
        assertTrue('\u009F'.isIdentifierIgnorable()) // APC
    }

    @Test
    fun testIsIdentifierIgnorable_whitespaceCharacters() {
        // Whitespace characters should not be identifier ignorable
        assertFalse(' '.isIdentifierIgnorable()) // SPACE
        assertFalse('\t'.isIdentifierIgnorable()) // TAB
        assertFalse('\n'.isIdentifierIgnorable()) // LF
        assertFalse('\r'.isIdentifierIgnorable()) // CR
        assertFalse('\u000C'.isIdentifierIgnorable()) // FF
    }

    @Test
    fun testIsIdentifierIgnorable_normalCharacters() {
        assertFalse('a'.isIdentifierIgnorable())
        assertFalse('Z'.isIdentifierIgnorable())
        assertFalse('0'.isIdentifierIgnorable())
        assertFalse('9'.isIdentifierIgnorable())
        assertFalse('$'.isIdentifierIgnorable())
        assertFalse('_'.isIdentifierIgnorable())
    }

    @Test
    fun testStringLiteralWithQuotes_null() {
        val result = null.stringLiteralWithQuotes("  ")
        assertEquals("null", result)
    }

    @Test
    fun testStringLiteralWithQuotes_emptyString() {
        val result = "".stringLiteralWithQuotes("  ")
        assertEquals("\"\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_simpleString() {
        val result = "hello".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withSingleQuote() {
        val result = "hello'world".stringLiteralWithQuotes("  ")
        assertEquals("\"hello'world\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withDoubleQuote() {
        val result = "hello\"world".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\\"world\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withBackslash() {
        val result = "hello\\world".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\\\world\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withNewline() {
        val result = "hello\nworld".stringLiteralWithQuotes("\t")
        assertEquals("\"hello\\n\"\n\t\t+ \"world\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withNewlineAndMoreContent() {
        val result = "hello\nworld\ntest".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\n\"\n    + \"world\\n\"\n    + \"test\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withTab() {
        val result = "hello\tworld".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\tworld\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withCarriageReturn() {
        val result = "hello\rworld".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\rworld\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withBackspace() {
        val result = "hello\bworld".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\bworld\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withFormFeed() {
        val result = "hello\u000cworld".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\fworld\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withISOControlCharacter() {
        val result = "hello\u0001world".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\u0001world\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_complexString() {
        val complexString = "Hello \"World\"\nThis is a test\twith\ttabs\r\nAnd \\backslashes\\"
        val result = complexString.stringLiteralWithQuotes("    ")
        val expected = "\"Hello \\\"World\\\"\\n\"\n        + \"This is a test\\twith\\ttabs\\r\\n\"\n        + \"And \\\\backslashes\\\\\""
        assertEquals(expected, result)
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_basicCharacters() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('a')
        assertEquals("a", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_backspace() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\b')
        assertEquals("\\b", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_tab() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\t')
        assertEquals("\\t", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_linefeed() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\n')
        assertEquals("\\n", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_carriageReturn() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\r')
        assertEquals("\\r", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_doubleQuote() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('"')
        assertEquals("\"", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_singleQuote() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\'')
        assertEquals("'", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_backslash() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\\')
        assertEquals("\\\\", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_formFeed() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\u000c')
        assertEquals("\\f", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_isoControlCharacter() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\u0001')
        assertEquals("\\u0001", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_unicodeCharacter() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('中')
        assertEquals("中", sb.toString())
    }

    @Test
    fun testCharacterLiteralWithoutSingleQuotes() {
        assertEquals("a", 'a'.characterLiteralWithoutSingleQuotes())
        assertEquals("Z", 'Z'.characterLiteralWithoutSingleQuotes())
        assertEquals("0", '0'.characterLiteralWithoutSingleQuotes())
        assertEquals("中", '中'.characterLiteralWithoutSingleQuotes())
    }

    @Test
    fun testExpectFunctions_isJavaIdentifierStart() {
        // Test that expect functions work (platform-specific implementations)
        assertTrue('a'.isJavaIdentifierStart())
        assertTrue('Z'.isJavaIdentifierStart())
        assertTrue('$'.isJavaIdentifierStart())
        assertTrue('_'.isJavaIdentifierStart())
        assertFalse('0'.isJavaIdentifierStart())
        assertFalse(' '.isJavaIdentifierStart())
        assertFalse('-'.isJavaIdentifierStart())
    }

    @Test
    fun testExpectFunctions_isJavaIdentifierPart() {
        // Test that expect functions work (platform-specific implementations)
        assertTrue('a'.isJavaIdentifierPart())
        assertTrue('Z'.isJavaIdentifierPart())
        assertTrue('$'.isJavaIdentifierPart())
        assertTrue('_'.isJavaIdentifierPart())
        assertTrue('0'.isJavaIdentifierPart())
        assertTrue('9'.isJavaIdentifierPart())
        assertFalse(' '.isJavaIdentifierPart())
        assertFalse('-'.isJavaIdentifierPart())
    }

    @Test
    fun testEdgeCases() {
        // Test string with only newlines
        assertEquals("\"\\n\"\n    + \"\\n\"", "\n\n".stringLiteralWithQuotes("  "))
        
        // Test string ending with newline
        assertEquals("\"test\\n\"", "test\n".stringLiteralWithQuotes("  "))
        
        // Test string starting with newline
        assertEquals("\"\\n\"\n    + \"test\"", "\ntest".stringLiteralWithQuotes("  "))
        
        // Test empty string with newline handling
        assertEquals("\"\\n\"", "\n".stringLiteralWithQuotes("  "))
    }

    @Test
    fun testJavaKeywords() {
        // Java keywords should still be valid identifier parts/starts
        assertTrue('c'.isJavaIdentifierStart()) // 'class'
        assertTrue('i'.isJavaIdentifierStart()) // 'int'
        assertTrue('p'.isJavaIdentifierStart()) // 'public'
        assertTrue('s'.isJavaIdentifierStart()) // 'static'
        assertTrue('v'.isJavaIdentifierStart()) // 'void'
    }

    @Test
    fun testUnicodeSupport() {
        // Test various Unicode characters
        assertTrue('α'.isJavaIdentifierStartCommon()) // Greek alpha
        assertTrue('β'.isJavaIdentifierPartCommon()) // Greek beta
        assertTrue('中'.isJavaIdentifierStartCommon()) // Chinese character
        assertTrue('文'.isJavaIdentifierPartCommon()) // Chinese character
        assertTrue('ñ'.isJavaIdentifierStartCommon()) // Latin letter with tilde
    }

    // Helper extension function to access private isIdentifierIgnorable
    private fun Char.isIdentifierIgnorable(): Boolean {
        return (
            isISOControl() && (
                this in '\u0000'..'\u0008' ||
                    this in '\u000E'..'\u001B' ||
                    this in '\u007F'..'\u009F'
                )
            ) || this in CharCategory.FORMAT
    }
}
