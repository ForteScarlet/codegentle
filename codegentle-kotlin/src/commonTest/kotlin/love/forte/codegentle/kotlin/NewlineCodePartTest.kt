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
package love.forte.codegentle.kotlin

import love.forte.codegentle.common.code.CodePart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Test for the Newline CodeArgumentPart implementation.
 */
class NewlineCodePartTest {

    @Test
    fun testNewlineCodePartCreation() {
        // Test that the newline factory function creates the correct type
        val newlinePart = CodePart.newline()
        assertIs<love.forte.codegentle.common.code.CodeArgumentPart.Newline>(newlinePart)
    }

    @Test
    fun testNewlineCodePartEquality() {
        // Test that multiple newline instances are equal (data object)
        val newline1 = CodePart.newline()
        val newline2 = CodePart.newline()
        assertEquals(newline1, newline2)
        assertEquals(newline1.hashCode(), newline2.hashCode())
    }
}
