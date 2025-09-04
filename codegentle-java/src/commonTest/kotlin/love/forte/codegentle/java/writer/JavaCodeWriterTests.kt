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
package love.forte.codegentle.java.writer

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.emitString
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.naming.*
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.strategy.JavaWriteStrategy
import love.forte.codegentle.java.strategy.ToStringJavaWriteStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for JavaCodeWriter functionality.
 */
class JavaCodeWriterTests {

    @Test
    fun testEmitString() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        writer.emit("Hello, World!")

        assertEquals("Hello, World!", out.toString())
    }

    @Test
    fun testEmitWithIndentation() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        writer.emitNewLine("public class Test {")
        writer.indent(1)
        writer.emitAndIndent("void method() {")
        writer.emitNewLine()
        writer.indent(1)
        writer.emitAndIndent("System.out.println(\"Hello\");")
        writer.emitNewLine()
        writer.unindent(1)
        writer.emitAndIndent("}")
        writer.emitNewLine()
        writer.unindent(1)
        writer.emitAndIndent("}")
        writer.emitNewLine()

        val expected = """
            |public class Test {
            |    void method() {
            |        System.out.println("Hello");
            |    }
            |}
            |
        """.trimMargin()

        assertEquals(expected, out.toString())
    }

    @Test
    fun testEmitCodeValue() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val codeValue = CodeValue("%V.out.println(%V)") {
            emitType(ClassName("java.lang", "System"))
            emitString("Hello, World!")
        }

        writer.emit(codeValue)

        assertEquals("System.out.println(\"Hello, World!\")", out.toString())
    }

    @Test
    fun testEmitTypeName() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val className = ClassName("java.util", "List")

        writer.emit(className)

        assertEquals("java.util.List", out.toString())
    }

    @Test
    fun testEmitAnnotationRef() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val annotationRef = ClassName("java.lang", "Override").annotationRef()

        writer.emit(annotationRef)

        assertEquals("@Override", out.toString())
    }

    @Test
    fun testEmitAnnotationRefWithJavaLang() {
        val out = StringBuilder()

        val writer = JavaCodeWriter.create(out, toStringStrategyWithJavaLang)
        val annotationRef = ClassName("java.lang", "Override").annotationRef()

        writer.emit(annotationRef)

        assertEquals("@java.lang.Override", out.toString())
    }

    @Test
    fun testEmitTypeRef() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val typeRef = ClassName("java.lang", "String").ref()

        writer.emit(typeRef)

        assertEquals("String", out.toString())
    }

    private val toStringStrategyWithJavaLang: JavaWriteStrategy
        get() {
            val strategy = object : JavaWriteStrategy by ToStringJavaWriteStrategy {
                override fun omitJavaLangPackage(): Boolean = false
            }
            return strategy
        }

    @Test
    fun testEmitTypeRefWithJavaLang() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, toStringStrategyWithJavaLang)
        val typeRef = ClassName("java.lang", "String").ref()

        writer.emit(typeRef)

        assertEquals("java.lang.String", out.toString())
    }

    @Test
    fun testEmitModifiers() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val modifiers = setOf(JavaModifier.PUBLIC, JavaModifier.STATIC, JavaModifier.FINAL)

        writer.emitModifiers(modifiers)

        assertEquals("public static final ", out.toString())
    }

    @Test
    fun testEmitComment() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val comment = CodeValue("This is a comment")

        writer.emitComment(comment)

        val result = out.toString()
        assertTrue(result.contains("// This is a comment"))
    }

    @Test
    fun testEmitDoc() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val doc = CodeValue("This is a javadoc comment")

        writer.emitDoc(doc)

        val result = out.toString()
        assertTrue(result.contains("/**"))
        assertTrue(result.contains(" * This is a javadoc comment"))
        assertTrue(result.contains(" */"))
    }

    @Test
    fun testEmitWithPackage() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, toStringStrategyWithJavaLang)
        val packageName = "com.example".parseToPackageName()

        writer.pushPackage(packageName)
        writer.emit(ClassName("java.lang", "String"))
        writer.popPackage()

        assertEquals("java.lang.String", out.toString())
    }

    @Test
    fun testEmitWithDiffPackage() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val packageName = "love.forte".parseToPackageName()

        writer.pushPackage(packageName)
        writer.emit(ClassName("love.forte", "Example"))
        writer.popPackage()

        assertEquals("Example", out.toString())
    }

    @Test
    fun testEmitWithSamePackage() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val packageName = "love.example".parseToPackageName()

        writer.pushPackage(packageName)
        writer.emit(ClassName("love.forte", "Example"))
        writer.popPackage()

        assertEquals("love.forte.Example", out.toString())
    }

    @Test
    fun testEmitLiteral() {
        val out1 = StringBuilder()
        val writer = JavaCodeWriter.create(out1, ToStringJavaWriteStrategy)

        writer.emitLiteral(42)
        assertEquals("42", out1.toString())

        val out2 = StringBuilder()
        val writer2 = JavaCodeWriter.create(out2, ToStringJavaWriteStrategy)
        writer2.emitLiteral("Hello")
        assertEquals("Hello", out2.toString())

        val out3 = StringBuilder()
        val writer3 = JavaCodeWriter.create(out3, ToStringJavaWriteStrategy)
        writer3.emitLiteral(true)
        assertEquals("true", out3.toString())
    }

    // ParameterizedTypeName tests
    @Test
    fun testEmitParameterizedTypeName() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val listClass = ClassName("java.util", "List")
        val stringClass = ClassName("java.lang", "String")
        val parameterizedType = ParameterizedTypeName(listClass, stringClass.ref())

        writer.emit(parameterizedType)

        assertEquals("java.util.List<String>", out.toString())
    }

    @Test
    fun testEmitParameterizedTypeNameMultipleArguments() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val mapClass = ClassName("java.util", "Map")
        val stringClass = ClassName("java.lang", "String")
        val integerClass = ClassName("java.lang", "Integer")
        val parameterizedType = ParameterizedTypeName(
            mapClass,
            stringClass.ref(),
            integerClass.ref()
        )

        writer.emit(parameterizedType)

        assertEquals("java.util.Map<String, Integer>", out.toString())
    }

    @Test
    fun testEmitNestedParameterizedTypeName() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val outerClass = ClassName("com.example", "Outer")
        val stringClass = ClassName("java.lang", "String")
        val outerParameterized = ParameterizedTypeName(outerClass, stringClass.ref())
        val integerClass = ClassName("java.lang", "Integer")
        val nestedParameterized = outerParameterized.nestedClass("Inner", integerClass.ref())

        writer.emit(nestedParameterized)

        assertEquals("com.example.Outer<String>.Inner<Integer>", out.toString())
    }

    @Test
    fun testEmitParameterizedTypeNameWithWildcard() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val listClass = ClassName("java.util", "List")
        val wildcardType = WildcardTypeName()
        val parameterizedType = ParameterizedTypeName(listClass, wildcardType.ref())

        writer.emit(parameterizedType)

        assertEquals("java.util.List<?>", out.toString())
    }

    // WildcardTypeName tests
    @Test
    fun testEmitEmptyWildcardTypeName() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val wildcardType = EmptyWildcardTypeName

        writer.emit(wildcardType)

        assertEquals("?", out.toString())
    }

    @Test
    fun testEmitWildcardTypeNameFunction() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val wildcardType = WildcardTypeName()

        writer.emit(wildcardType)

        assertEquals("?", out.toString())
    }

    @Test
    fun testEmitLowerWildcardTypeName() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val stringClass = ClassName("java.lang", "String")
        val wildcardType = LowerWildcardTypeName(stringClass.ref())

        writer.emit(wildcardType)

        assertEquals("? extends String", out.toString())
    }

    @Test
    fun testEmitLowerWildcardTypeNameMultipleBounds() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val stringClass = ClassName("java.lang", "String")
        val serializableClass = ClassName("java.io", "Serializable")
        val wildcardType = LowerWildcardTypeName(listOf(stringClass.ref(), serializableClass.ref()))

        writer.emit(wildcardType)

        assertEquals("? extends String & java.io.Serializable", out.toString())
    }

    @Test
    fun testEmitUpperWildcardTypeName() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val stringClass = ClassName("java.lang", "String")
        val wildcardType = UpperWildcardTypeName(stringClass.ref())

        writer.emit(wildcardType)

        assertEquals("? super String", out.toString())
    }

    @Test
    fun testEmitUpperWildcardTypeNameMultipleBounds() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val stringClass = ClassName("java.lang", "String")
        val objectClass = ClassName("java.lang", "Object")
        val wildcardType = UpperWildcardTypeName(listOf(stringClass.ref(), objectClass.ref()))

        writer.emit(wildcardType)

        assertEquals("? super String & Object", out.toString())
    }

    @Test
    fun testEmitComplexParameterizedTypeWithWildcards() {
        val out = StringBuilder()
        val writer = JavaCodeWriter.create(out, ToStringJavaWriteStrategy)
        val mapClass = ClassName("java.util", "Map")
        val stringClass = ClassName("java.lang", "String")
        val listClass = ClassName("java.util", "List")
        
        // Map<String, List<? extends Number>>
        val numberClass = ClassName("java.lang", "Number")
        val extendsNumber = LowerWildcardTypeName(numberClass.ref())
        val listOfExtendsNumber = ParameterizedTypeName(listClass, extendsNumber.ref())
        val mapType = ParameterizedTypeName(
            mapClass, 
            stringClass.ref(),
            listOfExtendsNumber.ref()
        )

        writer.emit(mapType)

        assertEquals("java.util.Map<String, java.util.List<? extends Number>>", out.toString())
    }
}
