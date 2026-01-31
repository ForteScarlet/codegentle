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

import love.forte.codegentle.common.code.*
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class ControlFlowTests {

    @Test
    fun testBasicControlFlowWithBeginEnd() {
        // Test: if (condition) { statements }
        val codeValue = CodeValue.builder()
            .addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition)")))
            .addCode("statement1\n")
            .addCode("statement2\n")
            .addCode("%V", CodePart.endControlFlow())
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition) {
                statement1
                statement2
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testControlFlowWithBeginNextEnd() {
        // Test: if (condition) { statements } else { statements }
        val codeValue = CodeValue.builder()
            .addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition)")))
            .addCode("statement1\n")
            .addCode("%V", CodePart.nextControlFlow(CodeValue("else")))
            .addCode("statement2\n")
            .addCode("%V", CodePart.endControlFlow())
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition) {
                statement1
            } else {
                statement2
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testControlFlowWithBeginNextEndWithCodeValue() {
        // Test: if (condition) { statements } else if (condition2) { statements } else { statements }
        val codeValue = CodeValue.builder()
            .addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition1)")))
            .addCode("statement1\n")
            .addCode("%V", CodePart.nextControlFlow(CodeValue("else if (condition2)")))
            .addCode("statement2\n")
            .addCode("%V", CodePart.nextControlFlow(CodeValue("else")))
            .addCode("statement3\n")
            .addCode("%V", CodePart.endControlFlow())
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition1) {
                statement1
            } else if (condition2) {
                statement2
            } else {
                statement3
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testControlFlowBeginWithoutCodeValue() {
        // Test: { statements }
        val codeValue = CodeValue.builder()
            .addCode("%V", CodePart.beginControlFlow())
            .addCode("statement1\n")
            .addCode("statement2\n")
            .addCode("%V", CodePart.endControlFlow())
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
             {
                statement1
                statement2
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testControlFlowEndWithCodeValue() {
        // Test: do { statements } while (condition)
        val codeValue = CodeValue.builder()
            .addCode("%V", CodePart.beginControlFlow(CodeValue("do")))
            .addCode("statement1\n")
            .addCode("statement2\n")
            .addCode("%V", CodePart.endControlFlow(CodeValue("while (condition)")))
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            do {
                statement1
                statement2
            } while (condition)
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testNestedControlFlow() {
        // Test: if (condition1) { if (condition2) { statements } }
        val codeValue = CodeValue.builder()
            .addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition1)")))
            .addCode("outerStatement\n")
            .addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition2)")))
            .addCode("innerStatement\n")
            .addCode("%V", CodePart.endControlFlow())
            .addCode("%V", CodePart.endControlFlow())
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition1) {
                outerStatement
                if (condition2) {
                    innerStatement
                }
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testControlFlowComparisonWithBuilder() {
        // Test that new ControlFlow produces the same output as existing builder methods
        val builderResult = buildString {
            val writer = KotlinCodeWriter.create(this)
            val codeValue = CodeValue.builder()
                .beginControlFlow("if (condition)")
                .addCode("statement1\n")
                .addCode("statement2\n")
                .endControlFlow()
                .build()
            writer.emit(codeValue)
        }

        val controlFlowResult = buildString {
            val writer = KotlinCodeWriter.create(this)
            val codeValue = CodeValue.builder()
                .addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition)")))
                .addCode("statement1\n")
                .addCode("statement2\n")
                .addCode("%V", CodePart.endControlFlow())
                .build()
            writer.emit(codeValue)
        }

        assertEquals(builderResult, controlFlowResult)
    }

    // DSL API Tests - Testing the beginControlFlow {}, nextControlFlow {}, endControlFlow {} style methods

    @Test
    fun testDslBeginControlFlowWithString() {
        // Test DSL: beginControlFlow("if (condition)") - simple string version
        val codeValue = CodeValue.builder()
            .beginControlFlow("if (condition)")
            .addCode("statement1\n")
            .addCode("statement2\n")
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition) {
                statement1
                statement2
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testDslBeginControlFlowWithBlock() {
        // Test DSL: beginControlFlow("if (%V)") { addValue(CodePart.literal(condition)) }
        val codeValue = CodeValue.builder()
            .beginControlFlow("if (%V)") {
                addValue(CodePart.literal("someCondition"))
            }
            .addCode("statement1\n")
            .addCode("statement2\n")
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (someCondition) {
                statement1
                statement2
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testDslControlFlowWithBeginNextEnd() {
        // Test DSL: if-else pattern using DSL methods
        val codeValue = CodeValue.builder()
            .beginControlFlow("if (condition)")
            .addCode("statement1\n")
            .nextControlFlow("else")
            .addCode("statement2\n")
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition) {
                statement1
            } else {
                statement2
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testDslNextControlFlowWithBlock() {
        // Test DSL: nextControlFlow with block parameters
        val codeValue = CodeValue.builder()
            .beginControlFlow("if (condition1)")
            .addCode("statement1\n")
            .nextControlFlow("else if (%V)") {
                emitLiteral("condition2")
            }
            .addCode("statement2\n")
            .nextControlFlow("else")
            .addCode("statement3\n")
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition1) {
                statement1
            } else if (condition2) {
                statement2
            } else {
                statement3
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testDslEndControlFlowWithBlock() {
        // Test DSL: endControlFlow with block - do-while pattern
        val codeValue = CodeValue.builder()
            .beginControlFlow("do")
            .addCode("statement1\n")
            .addCode("statement2\n")
            .endControlFlow("while (%V)") {
                addValue(CodePart.literal("condition"))
            }
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            do {
                statement1
                statement2
            } while (condition)
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testDslInControlflow() {
        // Test DSL: inControlflow convenience method
        val codeValue = CodeValue.builder()
            .inControlFlow("if (condition)") {
                addCode("statement1\n")
                addCode("statement2\n")
            }
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition) {
                statement1
                statement2
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testDslInControlflowWithBeginAndEnd() {
        // Test DSL: inControlflow with separate begin and end code values
        val beginCode = CodeValue("do")
        val endCode = CodeValue("while (condition)")
        
        val codeValue = CodeValue.builder()
            .inControlFlow(beginCode, endCode) {
                addCode("statement1\n")
                addCode("statement2\n")
            }
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            do {
                statement1
                statement2
            } while (condition)
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testDslInControlflowWithBlocks() {
        // Test DSL: inControlflow with begin and end blocks
        val codeValue = CodeValue.builder()
            .inControlFlow(
                beginControlFlow = "if (%V)",
                beginBlock = { addValue(CodePart.literal("condition")) },
                endControlFlow = null,
                endBlock = {}
            ) {
                addCode("statement1\n")
                addCode("statement2\n")
            }
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition) {
                statement1
                statement2
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testDslNestedControlFlows() {
        // Test DSL: nested control flows using DSL methods
        val codeValue = CodeValue.builder()
            .beginControlFlow("if (condition1)")
            .addCode("outerStatement\n")
            .inControlFlow("if (condition2)") {
                addCode("innerStatement\n")
            }
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition1) {
                outerStatement
                if (condition2) {
                    innerStatement
                }
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testDslVsDirectCodePartComparison() {
        // Test that DSL methods produce exactly the same output as direct CodePart usage
        val dslResult = buildString {
            val writer = KotlinCodeWriter.create(this)
            val codeValue = CodeValue.builder()
                .beginControlFlow("if (%V)") {
                    addValue(CodePart.literal("condition"))
                }
                .addCode("statement1\n")
                .nextControlFlow("else if (%V)") {
                    addValue(CodePart.literal("condition2"))
                }
                .addCode("statement2\n")
                .endControlFlow()
                .build()
            writer.emit(codeValue)
        }

        val directResult = buildString {
            val writer = KotlinCodeWriter.create(this)
            val codeValue = CodeValue.builder()
                .addCode("%V", CodePart.beginControlFlow(CodeValue("if (%V)") {
                    addValue(CodePart.literal("condition"))
                }))
                .addCode("statement1\n")
                .addCode("%V", CodePart.nextControlFlow(CodeValue("else if (%V)") {
                    addValue(CodePart.literal("condition2"))
                }))
                .addCode("statement2\n")
                .addCode("%V", CodePart.endControlFlow())
                .build()
            writer.emit(codeValue)
        }

        assertEquals(dslResult, directResult)
    }

    // Tests for new DSL functions

    @Test
    fun testIfElseIfElseControlFlow() {
        // Test DSL: if-elseif-else control flow using new DSL methods
        val codeValue = CodeValue.builder()
            .ifControlFlow("x > 0")
            .addCode("println(\"positive\")\n")
            .elseIfControlFlow("x < 0")
            .addCode("println(\"negative\")\n")
            .elseControlFlow()
            .addCode("println(\"zero\")\n")
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (x > 0) {
                println("positive")
            } else if (x < 0) {
                println("negative")
            } else {
                println("zero")
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testElseIfControlFlowOverloads() {
        // Test DSL: elseIfControlFlow with different overloads
        val codeValue = CodeValue.builder()
            .ifControlFlow("x > 10")
            .addCode("println(\"large\")\n")
            .elseIfControlFlow("x == 5")
            .addCode("println(\"five\")\n")
            .elseIfControlFlow("x == %V") {
                addValue(CodePart.literal("SPECIAL"))
            }
            .addCode("println(\"special\")\n")
            .elseIfControlFlow {
                addCode("x < 0")
            }
            .addCode("println(\"negative\")\n")
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (x > 10) {
                println("large")
            } else if (x == 5) {
                println("five")
            } else if (x == SPECIAL) {
                println("special")
            } else if (x < 0) {
                println("negative")
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testTryCatchFinallyControlFlow() {
        // Test DSL: try-catch-finally control flow
        val codeValue = CodeValue.builder()
            .tryControlFlow()
            .addCode("riskyOperation()\n")
            .catchControlFlow("IOException e")
            .addCode("handleIOError()\n")
            .catchControlFlow("Exception e")
            .addCode("handleGeneralError()\n")
            .finallyControlFlow()
            .addCode("cleanup()\n")
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            try {
                riskyOperation()
            } catch (IOException e) {
                handleIOError()
            } catch (Exception e) {
                handleGeneralError()
            } finally {
                cleanup()
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testCatchControlFlowOverloads() {
        // Test DSL: catchControlFlow with different overloads
        val codeValue = CodeValue.builder()
            .tryControlFlow()
            .addCode("riskyOperation()\n")
            .catchControlFlow("IOException e")
            .addCode("handleIOError()\n")
            .catchControlFlow("%V") {
                addValue(CodePart.literal("Exception e"))
            }
            .addCode("handleGeneralError()\n")
            .catchControlFlow {
                addCode("RuntimeException e")
            }
            .addCode("handleRuntimeError()\n")
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            try {
                riskyOperation()
            } catch (IOException e) {
                handleIOError()
            } catch (Exception e) {
                handleGeneralError()
            } catch (RuntimeException e) {
                handleRuntimeError()
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testWhileControlFlow() {
        // Test DSL: while control flow
        val codeValue = CodeValue.builder()
            .whileControlFlow("i < 10")
            .addCode("processItem(i)\n")
            .addCode("i++\n")
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            while (i < 10) {
                processItem(i)
                i++
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testWhileControlFlowOverloads() {
        // Test DSL: whileControlFlow with different overloads
        val codeValue = CodeValue.builder()
            .whileControlFlow("index < 5")
            .addCode("process1()\n")
            .endControlFlow()
            .addCode("\n")
            .whileControlFlow("count < %V") {
                addValue(CodePart.literal("MAX_COUNT"))
            }
            .addCode("process2()\n")
            .endControlFlow()
            .addCode("\n")
            .whileControlFlow {
                addCode("hasNext() && isValid()")
            }
            .addCode("process3()\n")
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            while (index < 5) {
                process1()
            }
            
            while (count < MAX_COUNT) {
                process2()
            }
            
            while (hasNext() && isValid()) {
                process3()
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testDoWhileControlFlow() {
        // Test DSL: do-while control flow
        val codeValue = CodeValue.builder()
            .doControlFlow()
            .addCode("processItem()\n")
            .addCode("count++\n")
            .doWhileEndControlFlow("count < 5")
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            do {
                processItem()
                count++
            } while (count < 5)
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testDoWhileEndControlFlowOverloads() {
        // Test DSL: doWhileEndControlFlow with different overloads
        val codeValue = CodeValue.builder()
            .doControlFlow()
            .addCode("process1()\n")
            .doWhileEndControlFlow("count < 10")
            .addCode("\n")
            .doControlFlow()
            .addCode("process2()\n")
            .doWhileEndControlFlow("flag == %V") {
                addValue(CodePart.literal("true"))
            }
            .addCode("\n")
            .doControlFlow()
            .addCode("process3()\n")
            .doWhileEndControlFlow {
                addCode("isValid() && hasMore()")
            }
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            do {
                process1()
            } while (count < 10)
            
            do {
                process2()
            } while (flag == true)
            
            do {
                process3()
            } while (isValid() && hasMore())
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testComplexNestedControlFlow() {
        // Test DSL: complex nested control flow using all new DSL methods
        val codeValue = CodeValue.builder()
            .tryControlFlow()
            .whileControlFlow("i < 10")
            .ifControlFlow("i % 2 == 0")
            .addCode("processEven(i)\n")
            .elseIfControlFlow("i == 5")
            .addCode("processSpecial(i)\n")
            .elseControlFlow()
            .addCode("processOdd(i)\n")
            .endControlFlow()
            .addCode("i++\n")
            .endControlFlow()
            .catchControlFlow("Exception e")
            .addCode("handleError(e)\n")
            .finallyControlFlow()
            .addCode("cleanup()\n")
            .endControlFlow()
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            try {
                while (i < 10) {
                    if (i % 2 == 0) {
                        processEven(i)
                    } else if (i == 5) {
                        processSpecial(i)
                    } else {
                        processOdd(i)
                    }
                    i++
                }
            } catch (Exception e) {
                handleError(e)
            } finally {
                cleanup()
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }
}
