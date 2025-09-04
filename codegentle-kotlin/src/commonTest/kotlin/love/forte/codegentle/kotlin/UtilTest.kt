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
package love.forte.codegentle.kotlin

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilTest {
    
    private val ignoreStringInterpolation = true

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
        val result = "hello\nworld".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\n\"\n    + \"world\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withCarriageReturn() {
        val result = "hello\rworld".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\rworld\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withTab() {
        val result = "hello\tworld".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\tworld\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withMultipleNewlines() {
        val result = "line1\nline2\nline3".stringLiteralWithQuotes("  ")
        assertEquals("\"line1\\n\"\n    + \"line2\\n\"\n    + \"line3\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withDollarSign() {
        // Use character code to avoid template issues
        val dollarString = "hello" + '\u0024' + "world"
        val result = dollarString.stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\\$world\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withFormFeed() {
        val result = "hello\u000cworld".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\u000cworld\"", result)
    }

    @Test
    fun testStringLiteralWithQuotes_withISOControlCharacter() {
        val result = "hello\u0001world".stringLiteralWithQuotes("  ")
        assertEquals("\"hello\\u0001world\"", result)
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_basicCharacters() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('a', ignoreStringInterpolation)
        assertEquals("a", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_backspace() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\b', ignoreStringInterpolation)
        assertEquals("\\b", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_tab() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\t', ignoreStringInterpolation)
        assertEquals("\\t", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_linefeed() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\n', ignoreStringInterpolation)
        assertEquals("\\n", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_carriageReturn() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\r', ignoreStringInterpolation)
        assertEquals("\\r", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_doubleQuote() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('"', ignoreStringInterpolation)
        assertEquals("\"", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_singleQuote() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\'', ignoreStringInterpolation)
        assertEquals("'", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_backslash() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\\', ignoreStringInterpolation)
        assertEquals("\\\\", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_dollarSign() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\u0024', ignoreStringInterpolation) // Dollar sign
        assertEquals("\\$", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_formFeed() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\u000c', ignoreStringInterpolation)
        assertEquals("\\u000c", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_isoControlCharacter() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\u0001', ignoreStringInterpolation)
        assertEquals("\\u0001", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_unicodeCharacter() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('中', ignoreStringInterpolation)
        assertEquals("中", sb.toString())
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
    fun testKotlinStringTemplateEscaping() {
        // Test various dollar sign scenarios using character codes
        val dollarChar = '\u0024'
        assertEquals("\"\\$\"", dollarChar.toString().stringLiteralWithQuotes("  "))
        
        val varString = dollarChar.toString() + "variable"
        assertEquals("\"\\\$variable\"", varString.stringLiteralWithQuotes("  "))
        
        val exprString = dollarChar.toString() + "{expression}"
        assertEquals("\"\\\${expression}\"", exprString.stringLiteralWithQuotes("  "))
        
        val textString = "text" + dollarChar.toString() + "more"
        assertEquals("\"text\\\$more\"", textString.stringLiteralWithQuotes("  "))
    }

    @Test
    fun testUnicodeSupport() {
        // Test various Unicode characters
        val result = "Hello 世界 α β γ".stringLiteralWithQuotes("  ")
        assertEquals("\"Hello 世界 α β γ\"", result)
    }

    @Test
    fun testFormFeedHandling() {
        // Test that form feed is handled differently in Kotlin (as \u000c instead of \f)
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\u000c', ignoreStringInterpolation)
        assertEquals("\\u000c", sb.toString())
        
        // Compare with Java which would use \f
        val result = "test\u000cmore".stringLiteralWithQuotes("  ")
        assertEquals("\"test\\u000cmore\"", result)
    }

    @Test
    fun testIndentationHandling() {
        // Test different indentation levels
        val testString = "line1\nline2"
        
        val result2Spaces = testString.stringLiteralWithQuotes("  ")
        assertEquals("\"line1\\n\"\n    + \"line2\"", result2Spaces)
        
        val result4Spaces = testString.stringLiteralWithQuotes("    ")
        assertEquals("\"line1\\n\"\n        + \"line2\"", result4Spaces)
        
        val resultTab = testString.stringLiteralWithQuotes("\t")
        assertEquals("\"line1\\n\"\n\t\t+ \"line2\"", resultTab)
    }

    @Test
    fun testEmptyLinesHandling() {
        // Test handling of empty lines in multiline strings
        val testString = "line1\n\nline3"
        val result = testString.stringLiteralWithQuotes("  ")
        assertEquals("\"line1\\n\"\n    + \"\\n\"\n    + \"line3\"", result)
    }

    @Test
    fun testStringWithOnlySpecialCharacters() {
        // Test strings containing only special characters
        val dollarChar = '\u0024'
        val dollarString = "" + dollarChar + dollarChar + dollarChar
        assertEquals("\"\\$\\$\\$\"", dollarString.stringLiteralWithQuotes("  "))
        
        assertEquals("\"\\\"\\\"\\\"\"", "\"\"\"".stringLiteralWithQuotes("  "))
        assertEquals("\"\\\\\\\\\\\\\"", "\\\\\\".stringLiteralWithQuotes("  "))
    }

    @Test
    fun testKotlinSpecificDifferences() {
        // Test differences between Kotlin and Java string literal handling
        
        // Dollar sign escaping (Kotlin-specific)
        val dollarChar = '\u0024'
        val dollarTest = "Price: " + dollarChar + "50"
        assertEquals("\"Price: \\\$50\"", dollarTest.stringLiteralWithQuotes("  "))
        
        // Form feed handling (different from Java's \f)
        val formFeedTest = "before\u000cafter"
        assertEquals("\"before\\u000cafter\"", formFeedTest.stringLiteralWithQuotes("  "))
    }

    @Test
    fun testDollarSignEscaping() {
        // Test specific dollar sign escaping scenarios
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\u0024', ignoreStringInterpolation)
        assertEquals("\\$", sb.toString())
        
        // Test in string context
        val dollarString = '\u0024'.toString()
        assertEquals("\"\\$\"", dollarString.stringLiteralWithQuotes("  "))
        
        // Test multiple dollars
        val multipleDollars = "" + '\u0024' + '\u0024' + '\u0024'
        assertEquals("\"\\$\\$\\$\"", multipleDollars.stringLiteralWithQuotes("  "))
    }

    @Test
    fun testComplexString() {
        // Test a complex string with multiple escape sequences
        val dollarChar = '\u0024'
        val complexString = "Hello \"World\"\nThis has\ttabs\r\nAnd \\backslashes\\ and " + dollarChar + "variables"
        val result = complexString.stringLiteralWithQuotes("    ")
        val expected = "\"Hello \\\"World\\\"\\n\"\n        + \"This has\\ttabs\\r\\n\"\n        + \"And \\\\backslashes\\\\ and \\\$variables\""
        assertEquals(expected, result)
    }

    @Test
    fun testAllEscapeSequences() {
        // Test all escape sequences that Kotlin Util handles
        val dollarChar = '\u0024'
        val testString = "\b\t\n\r\"'\\" + dollarChar + "\u000c\u0001"
        val result = testString.stringLiteralWithQuotes("  ")
        assertEquals("\"\\b\\t\\n\"\n    + \"\\r\\\"'\\\\\\$\\u000c\\u0001\"", result)
    }

    @Test
    fun testHandleSpecialCharacter_whenTrue_shouldEscapeDollarSign() {
        // Test handleSpecialCharacter = true (default behavior)
        val dollarString = "Hello \$world"
        val result = dollarString.stringLiteralWithQuotes("  ", ignoreStringInterpolation = true)
        assertEquals("\"Hello \\\$world\"", result)
        
        // Test with appendCharacterLiteralWithoutSingleQuotes
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\u0024', ignoreStringInterpolation = true)
        assertEquals("\\$", sb.toString())
    }

    @Test
    fun testHandleSpecialCharacter_whenFalse_shouldNotEscapeDollarSign() {
        // Test handleSpecialCharacter = false (no escaping)
        val dollarString = "Hello \$world"
        val result = dollarString.stringLiteralWithQuotes("  ", ignoreStringInterpolation = false)
        assertEquals("\"Hello \$world\"", result)
        
        // Test with appendCharacterLiteralWithoutSingleQuotes
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\u0024', ignoreStringInterpolation = false)
        assertEquals("$", sb.toString())
    }

    @Test
    fun testHandleSpecialCharacter_multipleScenarios() {
        val testString = "\$var and \${expr} and \$123"
        
        // With escaping
        val escaped = testString.stringLiteralWithQuotes("  ", ignoreStringInterpolation = true)
        assertEquals("\"\\\$var and \\\${expr} and \\\$123\"", escaped)
        
        // Without escaping
        val unescaped = testString.stringLiteralWithQuotes("  ", ignoreStringInterpolation = false)
        assertEquals("\"\$var and \${expr} and \$123\"", unescaped)
    }

    @Test
    fun testHandleSpecialCharacter_edgeCases() {
        // Empty string
        assertEquals("\"\"", "".stringLiteralWithQuotes("  ", ignoreStringInterpolation = true))
        assertEquals("\"\"", "".stringLiteralWithQuotes("  ", ignoreStringInterpolation = false))
        
        // Only dollar signs
        val onlyDollars = "\$\$\$"
        assertEquals("\"\\\$\\\$\\\$\"", onlyDollars.stringLiteralWithQuotes("  ", ignoreStringInterpolation = true))
        assertEquals("\"\$\$\$\"", onlyDollars.stringLiteralWithQuotes("  ", ignoreStringInterpolation = false))
        
        // Dollar sign with other special characters
        val mixed = "Hello \$world\nNew line\tTab"
        val escapedMixed = mixed.stringLiteralWithQuotes("  ", ignoreStringInterpolation = true)
        assertEquals("\"Hello \\\$world\\n\"\n    + \"New line\\tTab\"", escapedMixed)
        
        val unescapedMixed = mixed.stringLiteralWithQuotes("  ", ignoreStringInterpolation = false)
        assertEquals("\"Hello \$world\\n\"\n    + \"New line\\tTab\"", unescapedMixed)
    }

    @Test
    fun testCodePartStringFactory_withHandleSpecialCharacter() {
        // Test CodePart.string() factory methods
        val testString = "Price: \$50"
        
        // Default behavior (should escape)
        val defaultPart = love.forte.codegentle.common.code.CodePart.string(testString)
        assertEquals(true, (defaultPart as love.forte.codegentle.common.code.CodeArgumentPart.Str).handleSpecialCharacter)
        assertEquals(testString, defaultPart.value)
        
        // Explicit true
        val explicitTruePart = love.forte.codegentle.common.code.CodePart.string(testString, true)
        assertEquals(true, (explicitTruePart as love.forte.codegentle.common.code.CodeArgumentPart.Str).handleSpecialCharacter)
        assertEquals(testString, explicitTruePart.value)
        
        // Explicit false
        val explicitFalsePart = love.forte.codegentle.common.code.CodePart.string(testString, false)
        assertEquals(false, (explicitFalsePart as love.forte.codegentle.common.code.CodeArgumentPart.Str).handleSpecialCharacter)
        assertEquals(testString, explicitFalsePart.value)
    }

    @Test
    fun testCodePartStringFactory_nullValues() {
        // Test with null values
        val nullDefault = love.forte.codegentle.common.code.CodePart.string(null)
        assertEquals(true, (nullDefault as love.forte.codegentle.common.code.CodeArgumentPart.Str).handleSpecialCharacter)
        assertEquals(null, nullDefault.value)
        
        val nullFalse = love.forte.codegentle.common.code.CodePart.string(null, false)
        assertEquals(false, (nullFalse as love.forte.codegentle.common.code.CodeArgumentPart.Str).handleSpecialCharacter)
        assertEquals(null, nullFalse.value)
    }
}
