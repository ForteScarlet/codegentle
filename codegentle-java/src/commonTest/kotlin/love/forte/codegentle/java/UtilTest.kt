/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package love.forte.codegentle.java

import love.forte.codegentle.common.literalWithDoubleQuotes
import kotlin.test.Test
import kotlin.test.assertEquals


class UtilTest {
    @Test
    fun characterLiteral() {
        assertEquals("a", 'a'.characterLiteralWithoutSingleQuotes())
        assertEquals("b", 'b'.characterLiteralWithoutSingleQuotes())
        assertEquals("c", 'c'.characterLiteralWithoutSingleQuotes())
        assertEquals("%", '%'.characterLiteralWithoutSingleQuotes())

        // common escapes
        assertEquals("\\b", '\b'.characterLiteralWithoutSingleQuotes())
        assertEquals("\\t", '\t'.characterLiteralWithoutSingleQuotes())
        assertEquals("\\n", '\n'.characterLiteralWithoutSingleQuotes())
        assertEquals("\\f", '\u000c'.characterLiteralWithoutSingleQuotes())
        assertEquals("\\r", '\r'.characterLiteralWithoutSingleQuotes())
        assertEquals("\"", '"'.characterLiteralWithoutSingleQuotes())
        assertEquals("\\'", '\''.characterLiteralWithoutSingleQuotes())
        assertEquals("\\\\", '\\'.characterLiteralWithoutSingleQuotes())

        // octal escapes
        assertEquals("\\u0000", '\u0000'.characterLiteralWithoutSingleQuotes())
        assertEquals("\\u0007", '\u0007'.characterLiteralWithoutSingleQuotes())
        assertEquals("?", '\u003f'.characterLiteralWithoutSingleQuotes())
        assertEquals("\\u007f", '\u007f'.characterLiteralWithoutSingleQuotes())
        assertEquals("¿", '\u00bf'.characterLiteralWithoutSingleQuotes())
        assertEquals("ÿ", '\u00ff'.characterLiteralWithoutSingleQuotes())

        // unicode escapes
        assertEquals("\\u0000", '\u0000'.characterLiteralWithoutSingleQuotes())
        assertEquals("\\u0001", '\u0001'.characterLiteralWithoutSingleQuotes())
        assertEquals("\\u0002", '\u0002'.characterLiteralWithoutSingleQuotes())
        assertEquals("€", '\u20AC'.characterLiteralWithoutSingleQuotes())
        assertEquals("☃", '\u2603'.characterLiteralWithoutSingleQuotes())
        assertEquals("♠", '\u2660'.characterLiteralWithoutSingleQuotes())
        assertEquals("♣", '\u2663'.characterLiteralWithoutSingleQuotes())
        assertEquals("♥", '\u2665'.characterLiteralWithoutSingleQuotes())
        assertEquals("♦", '\u2666'.characterLiteralWithoutSingleQuotes())
        assertEquals("✵", '\u2735'.characterLiteralWithoutSingleQuotes())
        assertEquals("✺", '\u273A'.characterLiteralWithoutSingleQuotes())
        assertEquals("／", '\uFF0F'.characterLiteralWithoutSingleQuotes())
    }

    @Test
    fun stringLiteral() {
        stringLiteral("abc")
        stringLiteral("♦♥♠♣")
        stringLiteral("€\\t@\\t$", "€\t@\t$", " ")
        stringLiteral("abc();\\n\"\n  + \"def();", "abc();\ndef();", " ")
        stringLiteral("This is \\\"quoted\\\"!", "This is \"quoted\"!", " ")
        stringLiteral("e^{i\\\\pi}+1=0", "e^{i\\pi}+1=0", " ")
    }

    private fun stringLiteral(string: String) {
        stringLiteral(string, string, " ")
    }

    private fun stringLiteral(expected: String, value: String, indent: String) {
        assertEquals("\"" + expected + "\"", value.literalWithDoubleQuotes(indent))
    }
}
