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

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.emitLiteral
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.MemberName
import love.forte.codegentle.kotlin.strategy.ToStringKotlinWriteStrategy
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [MemberName] in Kotlin code generation.
 *
 * @author ForteScarlet
 */
class KotlinMemberNameTests {
    private data class TestPair(val writer: KotlinCodeWriter, val appendable: Appendable)

    private fun createTestWriter(out: Appendable = StringBuilder()): TestPair {
        return TestPair(
            KotlinCodeWriter.create(out = out, strategy = ToStringKotlinWriteStrategy),
            out
        )
    }

    @Test
    fun testEmitToWithSimpleMemberName() {
        val memberName = MemberName(packageName = "com.example", name = "testFunction")
        val (writer, out) = createTestWriter()

        memberName.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.testFunction", result)
    }

    @Test
    fun testEmitToWithEnclosingClassName() {
        val className = ClassName("com.example", "TestClass")
        val memberName = MemberName(enclosingClassName = className, name = "testMethod")
        val (writer, out) = createTestWriter()

        memberName.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.TestClass.testMethod", result)
    }

    @Test
    fun testEmitToWithNestedEnclosingClassName() {
        val outerClass = ClassName("com.example", "OuterClass")
        val nestedClass = outerClass.nestedClass("NestedClass")
        val memberName = MemberName(enclosingClassName = nestedClass, name = "testMethod")
        val (writer, out) = createTestWriter()

        memberName.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.OuterClass.NestedClass.testMethod", result)
    }

    @Test
    fun testEmitToWithKotlinStandardTypes() {
        val stringClass = ClassName("kotlin", "String")
        val memberName = MemberName(enclosingClassName = stringClass, name = "length")
        val (writer, out) = createTestWriter()

        memberName.emitTo(writer)
        val result = out.toString()

        // Should emit just "String.length" for standard Kotlin types
        assertEquals("String.length", result.trim())
    }

    @Test
    fun testEmitToWithKotlinCollectionTypes() {
        val listClass = ClassName("kotlin.collections", "List")
        val memberName = MemberName(enclosingClassName = listClass, name = "size")
        val (writer, out) = createTestWriter()

        memberName.emitTo(writer)
        val result = out.toString()

        // Should handle collection types appropriately
        assertEquals("kotlin.collections.List.size", result.trim())
    }

    @Test
    fun testEmitToWithCustomPackageMember() {
        val memberName = MemberName(packageName = "com.example.custom", name = "customFunction")
        val (writer, out) = createTestWriter()

        memberName.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.custom.customFunction", result)
    }

    @Test
    fun testEmitToWithMultipleMembers() {
        val member1 = MemberName(packageName = "com.example", name = "firstFunction")
        val member2 = MemberName(packageName = "com.example", name = "secondFunction")
        val (writer, out) = createTestWriter()

        member1.emitTo(writer)
        writer.emit(", ")
        member2.emitTo(writer)

        val result = out.toString()

        assertEquals("com.example.firstFunction, com.example.secondFunction", result)
    }

    @Test
    fun testEmitToWithDifferentPackages() {
        val member1 = MemberName(packageName = "com.example.package1", name = "function1")
        val member2 = MemberName(packageName = "com.example.package2", name = "function2")
        val (writer, out) = createTestWriter()

        member1.emitTo(writer)
        writer.emit(" and ")
        member2.emitTo(writer)

        val result = out.toString()

        assertEquals("com.example.package1.function1 and com.example.package2.function2", result)
    }

    @Test
    fun testEmitToWithEmptyPackage() {
        val memberName = MemberName(packageName = "", name = "rootFunction")
        val (writer, out) = createTestWriter()

        memberName.emitTo(writer)
        val result = out.toString()

        assertEquals(".rootFunction", result)
    }

    @Test
    fun testEmitToConsistency() {
        val memberName = MemberName(packageName = "com.example", name = "testFunction")
        val (writer1, out1) = createTestWriter()
        val (writer2, out2) = createTestWriter()

        memberName.emitTo(writer1)
        memberName.emitTo(writer2)

        val result1 = out1.toString()
        val result2 = out2.toString()

        // Should produce consistent results
        assertEquals(result1, result2)
    }

    @Test
    fun testEmitToWithSpecialCharactersInMemberName() {
        // Test with backtick-escaped member names (valid in Kotlin)
        val memberName = MemberName(packageName = "com.example", name = "test-function")
        val (writer, out) = createTestWriter()

        memberName.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.test-function", result)
    }

    @Test
    fun testEmitToWithLongPackageName() {
        val longPackage = "com.example.very.long.package.name.with.many.segments"
        val memberName = MemberName(packageName = longPackage, name = "testFunction")
        val (writer, out) = createTestWriter()

        memberName.emitTo(writer)
        val result = out.toString()

        assertEquals("com.example.very.long.package.name.with.many.segments.testFunction", result)
    }

    @Test
    fun testEmitToWithExtensionFunction() {
        val stringClass = ClassName("kotlin", "String")
        val memberName = MemberName(packageName = "kotlin.text", name = "isNotEmpty")
        val (writer, out) = createTestWriter()

        memberName.emitTo(writer)
        val result = out.toString()

        assertEquals("kotlin.text.isNotEmpty", result)
    }
    
    @Test
    fun testCodeValueWithTopLevelFunction() {
        // Test using MemberName for a top-level function in a CodeValue
        val memberName = MemberName(packageName = "kotlin.io", name = "println")
        
        val codeValue = CodeValue("import %V") {
            emitLiteral(memberName)
        }
        
        val result = codeValue.writeToKotlinString()
        assertEquals("import kotlin.io.println", result)
    }
    
    @Test
    fun testCodeValueWithExtensionFunction() {
        // Test using MemberName for an extension function in a CodeValue
        val memberName = MemberName(packageName = "kotlin.text", name = "isNotEmpty")
        
        val codeValue = CodeValue("import %V") {
            emitLiteral(memberName)
        }
        
        val result = codeValue.writeToKotlinString()
        assertEquals("import kotlin.text.isNotEmpty", result)
    }
    
    @Test
    fun testCodeValueWithConstant() {
        // Test using MemberName for a constant in a CodeValue
        val mathClass = ClassName("kotlin.math", "Math")
        val memberName = MemberName(enclosingClassName = mathClass, name = "PI")
        
        val codeValue = CodeValue("val pi = %V") {
            emitLiteral(memberName)
        }
        
        val result = codeValue.writeToKotlinString()
        assertEquals("val pi = kotlin.math.Math.PI", result)
    }
    
    @Test
    fun testCodeValueWithEnumElement() {
        // Test using MemberName for an enum element in a CodeValue
        val threadStateClass = ClassName("kotlin.concurrent", "ThreadState")
        val memberName = MemberName(enclosingClassName = threadStateClass, name = "RUNNING")
        
        val codeValue = CodeValue("val state = %V") {
            emitLiteral(memberName)
        }
        
        val result = codeValue.writeToKotlinString()
        assertEquals("val state = kotlin.concurrent.ThreadState.RUNNING", result)
    }
    
    @Test
    fun testCodeValueWithCompanionObjectFunction() {
        // Test using MemberName for a companion object function in a CodeValue
        val companionClass = ClassName("com.example", "MyClass")
        val memberName = MemberName(enclosingClassName = companionClass, name = "create")
        
        val codeValue = CodeValue("val instance = %V()") {
            emitLiteral(memberName)
        }
        
        val result = codeValue.writeToKotlinString()
        assertEquals("val instance = com.example.MyClass.create()", result)
    }
}
