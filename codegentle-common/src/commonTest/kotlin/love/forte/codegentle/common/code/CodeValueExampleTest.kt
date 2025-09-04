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
package love.forte.codegentle.common.code

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Example test for [CodeValue] to demonstrate testing in the project.
 *
 * @author Junie
 */
class CodeValueExampleTest {

    @Test
    fun testEmptyCodeValue() {
        val emptyValue = CodeValue()
        assertTrue(emptyValue.isEmpty(), "Empty CodeValue should be empty")
        assertEquals(0, emptyValue.parts.size, "Empty CodeValue should have no parts")
    }

    @Test
    fun testSimpleCodeValue() {
        val simpleValue = CodeValue("val x = 10")
        assertTrue(simpleValue.isNotEmpty(), "Simple CodeValue should not be empty")
        assertEquals(1, simpleValue.parts.size, "Simple CodeValue should have one part")
        
        val part = simpleValue.parts.first()
        assertTrue(part is CodeSimplePart, "Part should be a CodeSimplePart")
        assertEquals("val x = 10", part.value)
    }

    @Test
    fun testCodeValueCombination() {
        val value1 = CodeValue("val x = 10")
        val value2 = CodeValue("val y = 20")
        val combined = value1 + value2
        
        assertEquals(2, combined.parts.size, "Combined CodeValue should have two parts")
        assertTrue(combined.parts[0] is CodeSimplePart, "First part should be a CodeSimplePart")
        assertTrue(combined.parts[1] is CodeSimplePart, "Second part should be a CodeSimplePart")
        assertEquals("val x = 10", (combined.parts[0] as CodeSimplePart).value)
        assertEquals("val y = 20", (combined.parts[1] as CodeSimplePart).value)
    }
}
