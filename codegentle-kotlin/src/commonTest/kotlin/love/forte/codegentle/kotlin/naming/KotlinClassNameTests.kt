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
package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.kotlin.strategy.ToStringKotlinWriteStrategy
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinClassNameTests {
    private data class TestPair(val writer: KotlinCodeWriter, val appendable: Appendable)

    private fun createTestWriter(out: Appendable = StringBuilder()): TestPair {
        return TestPair(
            KotlinCodeWriter.create(out = out, strategy = ToStringKotlinWriteStrategy),
            out
        )

    }

    @Test
    fun testEmitToWithSimpleClassName() {
        val className = ClassName("com.example", "TestClass")
        val (writer, out) = createTestWriter()

        className.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.TestClass", result)
    }

    @Test
    fun testEmitToWithNestedClassName() {
        val outerClass = ClassName("com.example", "OuterClass")
        val nestedClass = outerClass.nestedClass("NestedClass")
        val (writer, out) = createTestWriter()

        nestedClass.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.OuterClass.NestedClass", result)
    }

    @Test
    fun testEmitToWithDeeplyNestedClassName() {
        val outerClass = ClassName("com.example", "OuterClass")
        val nestedClass = outerClass.nestedClass("NestedClass")
        val deeplyNestedClass = nestedClass.nestedClass("DeeplyNested")
        val (writer, out) = createTestWriter()

        deeplyNestedClass.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.OuterClass.NestedClass.DeeplyNested", result)
    }

    @Test
    fun testEmitToWithKotlinStandardTypes() {
        val stringClass = ClassName("kotlin", "String")
        val (writer, out) = createTestWriter()

        stringClass.emitTo(writer)
        val result = out.toString()

        // Should emit just "String" for standard Kotlin types
        assertEquals("String", result.trim())
    }

    @Test
    fun testEmitToWithKotlinCollectionTypes() {
        val listClass = ClassName("kotlin.collections", "List")
        val (writer, out) = createTestWriter()

        listClass.emitTo(writer)
        val result = out.toString()

        // Should handle collection types appropriately
        assertEquals("kotlin.collections.List", result.trim())
    }

    @Test
    fun testEmitToWithCustomPackageClass() {
        val className = ClassName("com.example.custom", "CustomClass")
        val (writer, out) = createTestWriter()

        className.emitTo(writer)
        val result = out.toString()

        // Should contain the class name
        assertEquals("com.example.custom.CustomClass", result)
    }

    @Test
    fun testEmitToWithMultipleClasses() {
        val class1 = ClassName("com.example", "FirstClass")
        val class2 = ClassName("com.example", "SecondClass")
        val (writer, out) = createTestWriter()

        class1.emitTo(writer)
        writer.emit(", ")
        class2.emitTo(writer)

        val result = out.toString()

        assertEquals("com.example.FirstClass, com.example.SecondClass", result)
    }

    @Test
    fun testEmitToWithGenericTypeNames() {
        val listClass = ClassName("kotlin.collections", "List")
        val stringClass = ClassName("kotlin", "String")
        val (writer, out) = createTestWriter()

        listClass.emitTo(writer)
        writer.emit("<")
        stringClass.emitTo(writer)
        writer.emit(">")

        val result = out.toString()

        assertEquals("kotlin.collections.List<String>", result)
    }

    @Test
    fun testEmitToWithComplexNestedStructure() {
        val packageName = "com.example.complex"
        val outerClass = ClassName(packageName, "OuterClass")
        val middleClass = outerClass.nestedClass("MiddleClass")
        val innerClass = middleClass.nestedClass("InnerClass")

        val (writer, out) = createTestWriter()

        innerClass.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.complex.OuterClass.MiddleClass.InnerClass", result)
    }

    @Test
    fun testEmitToWithDifferentPackages() {
        val class1 = ClassName("com.example.package1", "Class1")
        val class2 = ClassName("com.example.package2", "Class2")
        val (writer, out) = createTestWriter()

        class1.emitTo(writer)
        writer.emit(" and ")
        class2.emitTo(writer)

        val result = out.toString()

        assertEquals("com.example.package1.Class1 and com.example.package2.Class2", result)
    }

    @Test
    fun testEmitToWithEmptyPackage() {
        val className = ClassName("", "RootClass")
        val (writer, out) = createTestWriter()

        className.emitTo(writer)
        val result = out.toString()

        assertEquals("RootClass", result)
    }

    @Test
    fun testEmitToConsistency() {
        val className = ClassName("com.example", "TestClass")
        val (writer1, out1) = createTestWriter()
        val (writer2, out2) = createTestWriter()

        className.emitTo(writer1)
        className.emitTo(writer2)

        val result1 = out1.toString()
        val result2 = out2.toString()

        // Should produce consistent results
        assertEquals(result1, result2)
    }

    @Test
    fun testEmitToWithSpecialCharactersInClassName() {
        // Test with backtick-escaped class names (valid in Kotlin)
        val className = ClassName("com.example", "TestClass")
        val (writer, out) = createTestWriter()

        className.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.TestClass", result)
    }

    @Test
    fun testEmitToWithLongPackageName() {
        val longPackage = "com.example.very.long.package.name.with.many.segments"
        val className = ClassName(longPackage, "TestClass")
        val (writer, out) = createTestWriter()

        className.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.very.long.package.name.with.many.segments.TestClass", result)
    }
}
