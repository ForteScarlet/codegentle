package love.forte.codegentle.common

import kotlin.test.*

@OptIn(InternalCommonCodeGentleApi::class)
class UtilTest {

    @Test
    fun testAppendCharacterLiteral_backspace() {
        val sb = StringBuilder()
        val result = sb.appendCharacterLiteral('\b')
        assertTrue(result)
        assertEquals("\\b", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteral_tab() {
        val sb = StringBuilder()
        val result = sb.appendCharacterLiteral('\t')
        assertTrue(result)
        assertEquals("\\t", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteral_linefeed() {
        val sb = StringBuilder()
        val result = sb.appendCharacterLiteral('\n')
        assertTrue(result)
        assertEquals("\\n", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteral_carriageReturn() {
        val sb = StringBuilder()
        val result = sb.appendCharacterLiteral('\r')
        assertTrue(result)
        assertEquals("\\r", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteral_doubleQuote() {
        val sb = StringBuilder()
        val result = sb.appendCharacterLiteral('"')
        assertTrue(result)
        assertEquals("\"", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteral_singleQuote() {
        val sb = StringBuilder()
        val result = sb.appendCharacterLiteral('\'')
        assertFalse(result)
        assertEquals("", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteral_backslash() {
        val sb = StringBuilder()
        val result = sb.appendCharacterLiteral('\\')
        assertTrue(result)
        assertEquals("\\\\", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteral_isoControlCharacter() {
        val sb = StringBuilder()
        val result = sb.appendCharacterLiteral('\u0001')
        assertTrue(result)
        assertEquals("\\u0001", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteral_normalCharacter() {
        val sb = StringBuilder()
        val result = sb.appendCharacterLiteral('a')
        assertFalse(result)
        assertEquals("", sb.toString())
    }

    @Test
    fun testAppendCharacterLiteral_unicodeCharacter() {
        val sb = StringBuilder()
        val result = sb.appendCharacterLiteral('中')
        assertFalse(result)
        assertEquals("", sb.toString())
    }

    @Test
    fun testComputeValue_newKey() {
        val map = mutableMapOf<String, Int>()
        val result = map.computeValue("key") { k, v ->
            assertEquals("key", k)
            assertNull(v)
            42
        }
        assertEquals(42, result)
        assertEquals(42, map["key"])
    }

    @Test
    fun testComputeValue_existingKey() {
        val map = mutableMapOf("key" to 10)
        val result = map.computeValue("key") { k, v ->
            assertEquals("key", k)
            assertEquals(10, v)
            v!! + 5
        }
        assertEquals(15, result)
        assertEquals(15, map["key"])
    }

    @Test
    fun testComputeValue_removeKey() {
        val map = mutableMapOf("key" to 10)
        val result = map.computeValue("key") { k, v ->
            assertEquals("key", k)
            assertEquals(10, v)
            null
        }
        assertNull(result)
        assertFalse(map.containsKey("key"))
    }

    @Test
    fun testComputeValue_nullForNonExistentKey() {
        val map = mutableMapOf<String, Int>()
        val result = map.computeValue("key") { k, v ->
            assertEquals("key", k)
            assertNull(v)
            null
        }
        assertNull(result)
        assertFalse(map.containsKey("key"))
    }

    @Test
    fun testComputeValueIfAbsent_newKey() {
        val map = mutableMapOf<String, Int>()
        val result = map.computeValueIfAbsent("key") { k ->
            assertEquals("key", k)
            42
        }
        assertEquals(42, result)
        assertEquals(42, map["key"])
    }

    @Test
    fun testComputeValueIfAbsent_existingKey() {
        val map = mutableMapOf("key" to 10)
        val result = map.computeValueIfAbsent("key") { k ->
            // This should not be called
            throw AssertionError("Function should not be called for existing key")
        }
        assertEquals(10, result)
        assertEquals(10, map["key"])
    }

    @Test
    fun testComputeValueIfAbsent_nullValue() {
        val map = mutableMapOf<String, Int?>()
        map["key"] = null
        val result = map.computeValueIfAbsent("key") { k ->
            assertEquals("key", k)
            42
        }
        assertEquals(42, result)
        assertEquals(42, map["key"])
    }

    @Test
    fun testFormatIsoControlCode() {
        // Test various ISO control codes
        val testCases = mapOf(
            '\u0000' to "\\u0000",
            '\u0001' to "\\u0001",
            '\u0008' to "\\b",
            '\u000E' to "\\u000e",
            '\u001B' to "\\u001b",
            '\u007F' to "\\u007f",
            '\u009F' to "\\u009f"
        )

        testCases.forEach { (char, expected) ->
            val sb = StringBuilder()
            val result = sb.appendCharacterLiteral(char)
            assertTrue(result, "Should handle ISO control character: $char")
            assertEquals(expected, sb.toString(), "Wrong format for character: $char")
        }
    }
}
