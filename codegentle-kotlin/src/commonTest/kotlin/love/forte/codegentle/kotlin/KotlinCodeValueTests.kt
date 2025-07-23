package love.forte.codegentle.kotlin

import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KotlinCodeValueTests {

    @Test
    fun testEmptyCodeValue() {
        val codeValue = CodeValue()
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("", result)
    }

    @Test
    fun testSimpleStringCodeValue() {
        val codeValue = CodeValue("hello world")
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("hello world", result)
    }

    @Test
    fun testCodeValueWithLiteral() {
        val codeValue = CodeValue("value: %V") {
            addValue(CodePart.literal("test"))
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("value: test", result)
    }

    @Test
    fun testCodeValueWithString() {
        val codeValue = CodeValue("message: %V") {
            addValue(CodePart.string("hello"))
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("message: \"hello\"", result)
    }

    @Test
    fun testCodeValueWithNullString() {
        val codeValue = CodeValue("value: %V") {
            addValue(CodePart.string(null))
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("value: null", result)
    }

    @Test
    fun testCodeValueWithStringEscaping() {
        val codeValue = CodeValue("text: %V") {
            addValue(CodePart.string("hello \"world\""))
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("text: \"hello \\\"world\\\"\"", result)
    }

    @Test
    fun testCodeValueWithSpecialCharacters() {
        val codeValue = CodeValue("special: %V") {
            addValue(CodePart.string("tab\there\nline\rcarriage\\backslash"))
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals(
            """
                special: "tab\there\n"
                        + "line\rcarriage\\backslash"
                """.trimIndent(), result)
    }

    @Test
    fun testCodeValueWithTypeName() {
        val className = ClassName("com.example", "TestClass")
        val codeValue = CodeValue("type: %V") {
            addValue(CodePart.type(className))
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("type: com.example.TestClass", result)
    }

    @Test
    fun testCodeValueWithTypeRef() {
        val className = ClassName("com.example", "TestClass")
        val typeRef = className.kotlinRef()
        val codeValue = CodeValue("type: %V") {
            addValue(CodePart.type(typeRef))
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("type: com.example.TestClass", result)
    }

    @Test
    fun testCodeValueWithName() {
        val codeValue = CodeValue("name: %V") {
            addValue(CodePart.name("variableName"))
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("name: variableName", result)
    }

    @Test
    fun testCodeValueWithMultipleArguments() {
        val className = ClassName("com.example", "TestClass")
        val codeValue = CodeValue("fun %V(): %V = %V") {
            addValue(CodePart.name("testFunction"))
            addValue(CodePart.type(className))
            addValue(CodePart.string("result"))
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("fun testFunction(): com.example.TestClass = \"result\"", result)
    }

    @Test
    fun testCodeValueWithSkip() {
        val codeValue = CodeValue("before %V after") {
            addValue(CodePart.skip())
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("before %V after", result)
    }

    @Test
    fun testCodeValueBuilder() {
        val codeValue = CodeValue.builder()
            .add("hello ")
            .add("world")
            .build()
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("hello world", result)
    }

    @Test
    fun testCodeValueBuilderWithFormat() {
        val codeValue = CodeValue.builder("value: %V")
            .addValue(CodePart.literal("test"))
            .build()
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("value: test", result)
    }

    @Test
    fun testCodeValueBuilderWithIndentation() {
        val codeValue = CodeValue.builder()
            .add("line1\n")
            .indent()
            .add("indented line\n")
            .unindent()
            .add("back to normal")
            .build()
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertTrue(result.contains("line1"))
        assertTrue(result.contains("indented line"))
        assertTrue(result.contains("back to normal"))
    }

    @Test
    fun testCodeValueBuilderWithControlFlow() {
        val codeValue = CodeValue.builder()
            .beginControlFlow("if (condition)")
            .add("statement1\n")
            .add("statement2\n")
            .endControlFlow()
            .build()
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertTrue(result.contains("if (condition)"))
        assertTrue(result.contains("statement1"))
        assertTrue(result.contains("statement2"))
    }

    @Test
    fun testNestedCodeValues() {
        val innerCodeValue = CodeValue("inner content")
        val outerCodeValue = CodeValue.builder()
            .add("outer ")
            .add(innerCodeValue)
            .add(" end")
            .build()
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(outerCodeValue)
        }
        
        assertEquals("outer inner content end", result)
    }

    @Test
    fun testCodeValueWithOtherCodeValue() {
        val innerCodeValue = CodeValue("inner")
        val codeValue = CodeValue("outer %V end") {
            addValue(CodePart.otherCodeValue(innerCodeValue))
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertEquals("outer inner end", result)
    }

    @Test
    fun testComplexCodeValue() {
        val className = ClassName("com.example", "TestClass")
        val codeValue = CodeValue.builder()
            .add("fun test(): ")
            .add("%V", CodePart.type(className))
            .add(" {\n")
            .indent()
            .add("return ")
            .add("%V", CodePart.string("hello"))
            .add("\n")
            .unindent()
            .add("}")
            .build()
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }
        
        assertTrue(result.contains("fun test():"))
        assertTrue(result.contains("TestClass"))
        assertTrue(result.contains("return \"hello\""))
    }

    @Test
    fun testEnsureTrailingNewline() {
        val codeValue = CodeValue("test content")
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            codeValue.emitTo(writer, ensureTrailingNewline = true)
        }
        
        assertTrue(result.endsWith("\n"))
    }

    @Test
    fun testNoTrailingNewlineWhenNotRequested() {
        val codeValue = CodeValue("test content")
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            codeValue.emitTo(writer, ensureTrailingNewline = false)
        }
        
        assertEquals("test content", result)
    }
}
