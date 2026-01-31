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

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.emitLiteral
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.MemberName
import love.forte.codegentle.java.strategy.ToStringJavaWriteStrategy
import love.forte.codegentle.java.strategy.WrapperJavaWriteStrategy
import love.forte.codegentle.java.writer.writeToJavaString
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [MemberName] in Java code generation.
 *
 * @author ForteScarlet
 */
class MemberNameTest {

    @Test
    fun memberNameToString() {
        // Test a simple member name with package
        assertEquals(
            "sort",
            MemberName(packageName = "java.util", name = "sort").writeToJavaString()
        )

        // Test a member name with enclosing class
        val collectionsClass = ClassName(packageName = "java.util", simpleName = "Collections")
        assertEquals(
            "java.util.Collections.sort",
            MemberName(enclosingClassName = collectionsClass, name = "sort").writeToJavaString()
        )

        // Test with a strategy that doesn't omit java.lang package
        val toStringWithJavaLang =
            object : WrapperJavaWriteStrategy(ToStringJavaWriteStrategy) {
                override fun omitJavaLangPackage(): Boolean = false
            }

        assertEquals(
            "java.lang.System.out",
            MemberName(
                enclosingClassName = ClassName(packageName = "java.lang", simpleName = "System"),
                name = "out"
            ).writeToJavaString(toStringWithJavaLang)
        )

        // Test using MemberName in a CodeValue
        assertEquals(
            "import static java.util.Collections.sort;",
            CodeValue("import static %V;") {
                emitLiteral(MemberName(enclosingClassName = collectionsClass, name = "sort"))
            }.writeToJavaString()
        )

        // Test using MemberName for a top-level function
        assertEquals(
            "import static java.util.sort;",
            CodeValue("import static %V;") {
                emitLiteral(MemberName(packageName = "java.util", name = "sort"))
            }.writeToJavaString()
        )
    }
    
    @Test
    fun testCodeValueWithConstant() {
        // Test using MemberName for a constant in a CodeValue
        val mathClass = ClassName(packageName = "java.lang", simpleName = "Math")
        val memberName = MemberName(enclosingClassName = mathClass, name = "PI")
        
        // Create a strategy that omits java.lang package
        val omitJavaLangStrategy = object : WrapperJavaWriteStrategy(ToStringJavaWriteStrategy) {
            override fun omitJavaLangPackage(): Boolean = true
        }
        
        val codeValue = CodeValue("double pi = %V;") {
            emitLiteral(memberName)
        }
        
        val result = codeValue.writeToJavaString(omitJavaLangStrategy)
        assertEquals("double pi = java.lang.Math.PI;", result)
    }
    
    @Test
    fun testCodeValueWithEnumElement() {
        // Test using MemberName for an enum element in a CodeValue
        val threadClass = ClassName(packageName = "java.lang", simpleName = "Thread")
        val threadStateClass = threadClass.nestedClass("State")
        val memberName = MemberName(enclosingClassName = threadStateClass, name = "RUNNABLE")
        
        // Create a strategy that omits java.lang package
        val omitJavaLangStrategy = object : WrapperJavaWriteStrategy(ToStringJavaWriteStrategy) {
            override fun omitJavaLangPackage(): Boolean = true
        }
        
        val codeValue = CodeValue("Thread.State state = %V;") {
            emitLiteral(memberName)
        }
        
        val result = codeValue.writeToJavaString(omitJavaLangStrategy)
        assertEquals("Thread.State state = java.lang.Thread.State.RUNNABLE;", result)
    }
    
    @Test
    fun testCodeValueWithStaticField() {
        // Test using MemberName for a static field in a CodeValue
        val systemClass = ClassName(packageName = "java.lang", simpleName = "System")
        val memberName = MemberName(enclosingClassName = systemClass, name = "out")
        
        // Create a strategy that omits java.lang package
        val omitJavaLangStrategy = object : WrapperJavaWriteStrategy(ToStringJavaWriteStrategy) {
            override fun omitJavaLangPackage(): Boolean = true
        }
        
        val codeValue = CodeValue("PrintStream stream = %V;") {
            emitLiteral(memberName)
        }
        
        val result = codeValue.writeToJavaString(omitJavaLangStrategy)
        assertEquals("PrintStream stream = java.lang.System.out;", result)
    }
    
    @Test
    fun testCodeValueWithStaticMethod() {
        // Test using MemberName for a static method in a CodeValue
        val arraysClass = ClassName(packageName = "java.util", simpleName = "Arrays")
        val memberName = MemberName(enclosingClassName = arraysClass, name = "asList")
        
        val codeValue = CodeValue("List<String> list = %V(\"a\", \"b\", \"c\");") {
            emitLiteral(memberName)
        }
        
        val result = codeValue.writeToJavaString()
        assertEquals("List<String> list = java.util.Arrays.asList(\"a\", \"b\", \"c\");", result)
    }
}
