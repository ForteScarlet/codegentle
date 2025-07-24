package love.forte.codegentle.kotlin

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilTest {

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
    fun testAppendCharacterLiteralWithoutSingleQuotes_dollarSign() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\u0024') // Dollar sign
        assertEquals("\\$", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteralWithoutSingleQuotes_formFeed() {
        val sb = StringBuilder()
        sb.appendCharacterLiteralWithoutSingleQuotes('\u000c')
        assertEquals("\\u000c", sb.toString())
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
        sb.appendCharacterLiteralWithoutSingleQuotes('\u000c')
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
        sb.appendCharacterLiteralWithoutSingleQuotes('\u0024')
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
}
