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
package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeValue
import kotlin.test.*

/**
 * Comprehensive tests for utility functions in Utils.kt
 */
class UtilsTests {

    // Tests for isStartWithReturn() function

    @Test
    fun testIsStartWithReturnEmptyCodeValue() {
        val emptyCodeValue = CodeValue()
        assertFalse(emptyCodeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnNonSimplePart() {
        // Create a CodeValue with a non-CodeSimplePart as first part
        val codeValue = CodeValue(CodePart.literal("return something"))
        assertFalse(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnTrue() {
        val codeValue = CodeValue(CodePart.simple("return value"))
        assertTrue(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnTrueWithWhitespace() {
        val codeValue = CodeValue(CodePart.simple("   return value"))
        assertTrue(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnTrueWithTabs() {
        val codeValue = CodeValue(CodePart.simple("\t\treturn value"))
        assertTrue(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnTrueWithMixedWhitespace() {
        val codeValue = CodeValue(CodePart.simple(" \t return value"))
        assertTrue(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnFalseNoSpace() {
        val codeValue = CodeValue(CodePart.simple("returnvalue"))
        assertFalse(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnFalseReturnInMiddle() {
        val codeValue = CodeValue(CodePart.simple("val x = return value"))
        assertFalse(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnFalseNotStartingWithReturn() {
        val codeValue = CodeValue(CodePart.simple("val x = 42"))
        assertFalse(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnFalseEmptyString() {
        val codeValue = CodeValue(CodePart.simple(""))
        assertFalse(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnFalseOnlyWhitespace() {
        val codeValue = CodeValue(CodePart.simple("   "))
        assertFalse(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnTrueOnlyReturn() {
        val codeValue = CodeValue(CodePart.simple("return "))
        assertTrue(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnMultipleParts() {
        val codeValue = CodeValue(listOf(
            CodePart.simple("return value"),
            CodePart.simple(" + 1")
        ))
        assertTrue(codeValue.isStartWithReturn())
    }

    @Test
    fun testIsStartWithReturnMultiplePartsFirstNotReturn() {
        val codeValue = CodeValue(listOf(
            CodePart.simple("val x = "),
            CodePart.simple("return value")
        ))
        assertFalse(codeValue.isStartWithReturn())
    }

    // Tests for removeFirstReturn() function

    @Test
    fun testRemoveFirstReturnBasic() {
        val original = CodeValue(CodePart.simple("return value"))
        val result = original.removeFirstReturn()
        
        assertEquals(1, result.parts.size)
        assertEquals("value", (result.parts.first() as love.forte.codegentle.common.code.CodeSimplePart).value)
    }

    @Test
    fun testRemoveFirstReturnNoReturn() {
        val original = CodeValue(CodePart.simple("val x = 42"))
        val result = original.removeFirstReturn()
        
        // Should return the same instance when no "return " is found
        assertSame(original, result)
    }

    @Test
    fun testRemoveFirstReturnEmptyAfterRemoval() {
        val original = CodeValue(CodePart.simple("return "))
        val result = original.removeFirstReturn()
        
        assertEquals(1, result.parts.size)
        assertEquals("", (result.parts.first() as love.forte.codegentle.common.code.CodeSimplePart).value)
    }

    @Test
    fun testRemoveFirstReturnWithWhitespace() {
        val original = CodeValue(CodePart.simple("   return value"))
        val result = original.removeFirstReturn()
        
        assertEquals(1, result.parts.size)
        assertEquals("   value", (result.parts.first() as love.forte.codegentle.common.code.CodeSimplePart).value)
    }

    @Test
    fun testRemoveFirstReturnMultipleParts() {
        val original = CodeValue(listOf(
            CodePart.simple("return value"),
            CodePart.simple(" + 1"),
            CodePart.literal(42)
        ))
        val result = original.removeFirstReturn()
        
        assertEquals(3, result.parts.size)
        assertEquals("value", (result.parts[0] as love.forte.codegentle.common.code.CodeSimplePart).value)
        assertEquals(" + 1", (result.parts[1] as love.forte.codegentle.common.code.CodeSimplePart).value)
        assertEquals(42, (result.parts[2] as love.forte.codegentle.common.code.CodeArgumentPart.Literal).value)
    }

    @Test
    fun testRemoveFirstReturnMultiplePartsNoReturn() {
        val original = CodeValue(listOf(
            CodePart.simple("val x = "),
            CodePart.simple("42")
        ))
        val result = original.removeFirstReturn()
        
        // Should return the same instance when no "return " is found
        assertSame(original, result)
    }

    @Test
    fun testRemoveFirstReturnOnlyFirstOccurrence() {
        val original = CodeValue(CodePart.simple("return return value"))
        val result = original.removeFirstReturn()
        
        assertEquals(1, result.parts.size)
        assertEquals("return value", (result.parts.first() as love.forte.codegentle.common.code.CodeSimplePart).value)
    }

    @Test
    fun testRemoveFirstReturnReturnInMiddle() {
        val original = CodeValue(CodePart.simple("val x = return value"))
        val result = original.removeFirstReturn()
        
        assertEquals(1, result.parts.size)
        assertEquals("val x = value", (result.parts.first() as love.forte.codegentle.common.code.CodeSimplePart).value)
    }

    @Test
    fun testRemoveFirstReturnComplexExpression() {
        val original = CodeValue(CodePart.simple("return if (condition) value1 else value2"))
        val result = original.removeFirstReturn()
        
        assertEquals(1, result.parts.size)
        assertEquals("if (condition) value1 else value2", (result.parts.first() as love.forte.codegentle.common.code.CodeSimplePart).value)
    }

    @Test
    fun testRemoveFirstReturnWithNewlines() {
        val original = CodeValue(CodePart.simple("return\n    value"))
        val result = original.removeFirstReturn()
        
        // Should return the same instance because there's no space after "return"
        assertSame(original, result)
    }

    // Edge case tests

    @Test
    fun testRemoveFirstReturnRequiresCodeSimplePart() {
        // This test verifies that the function requires the first part to be CodeSimplePart
        // We can't easily test the require() failure without catching exceptions,
        // but we can test that it works correctly with CodeSimplePart
        val original = CodeValue(listOf(
            CodePart.simple("return value"),
            CodePart.literal("test")
        ))
        val result = original.removeFirstReturn()
        
        assertEquals(2, result.parts.size)
        assertEquals("value", (result.parts[0] as love.forte.codegentle.common.code.CodeSimplePart).value)
    }

    @Test
    fun testBothFunctionsWorkTogether() {
        val original = CodeValue(CodePart.simple("return someValue"))
        
        // First check if it starts with return
        assertTrue(original.isStartWithReturn())
        
        // Then remove the return
        val result = original.removeFirstReturn()
        assertEquals("someValue", (result.parts.first() as love.forte.codegentle.common.code.CodeSimplePart).value)
        
        // The result should not start with return anymore
        assertFalse(result.isStartWithReturn())
    }

    @Test
    fun testBothFunctionsWithNonReturnCode() {
        val original = CodeValue(CodePart.simple("val x = 42"))
        
        // Should not start with return
        assertFalse(original.isStartWithReturn())
        
        // removeFirstReturn should return the same instance
        val result = original.removeFirstReturn()
        assertSame(original, result)
    }

    @Test
    fun testIsStartWithReturnAfterRemoval() {
        val original = CodeValue(CodePart.simple("return return value"))
        
        assertTrue(original.isStartWithReturn())
        
        val result = original.removeFirstReturn()
        // After removing first "return ", it should still start with "return "
        assertTrue(result.isStartWithReturn())
        
        val result2 = result.removeFirstReturn()
        // After removing second "return ", it should not start with "return "
        assertFalse(result2.isStartWithReturn())
    }
}
