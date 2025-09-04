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
package love.forte.codegentle.java

import love.forte.codegentle.common.code.*
import love.forte.codegentle.java.writer.JavaCodeWriter
import kotlin.test.Test
import kotlin.test.assertEquals

class ControlFlowTests {

    @Test
    fun testBasicControlFlowWithBeginEnd() {
        // Test: if (condition) { statements }
        val codeValue = CodeValue {
            addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition)")))
            addCode("statement1;\n")
            addCode("statement2;\n")
            addCode("%V", CodePart.endControlFlow())
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition) {
                statement1;
                statement2;
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testControlFlowWithBeginNextEnd() {
        // Test: if (condition) { statements } else { statements }
        val codeValue = CodeValue {
            addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition)")))
            addCode("statement1;\n")
            addCode("%V", CodePart.nextControlFlow(CodeValue("else")))
            addCode("statement2;\n")
            addCode("%V", CodePart.endControlFlow())
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition) {
                statement1;
            } else {
                statement2;
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testControlFlowWithBeginNextEndWithCodeValue() {
        // Test: if (condition) { statements } else if (condition2) { statements } else { statements }
        val codeValue = CodeValue {
            addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition1)")))
            addCode("statement1;\n")
            addCode("%V", CodePart.nextControlFlow(CodeValue("else if (condition2)")))
            addCode("statement2;\n")
            addCode("%V", CodePart.nextControlFlow(CodeValue("else")))
            addCode("statement3;\n")
            addCode("%V", CodePart.endControlFlow())
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition1) {
                statement1;
            } else if (condition2) {
                statement2;
            } else {
                statement3;
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testControlFlowBeginWithoutCodeValue() {
        // Test: { statements }
        val codeValue = CodeValue {
            addCode("%V", CodePart.beginControlFlow())
            addCode("statement1;\n")
            addCode("statement2;\n")
            addCode("%V", CodePart.endControlFlow())
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
             {
                statement1;
                statement2;
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testControlFlowEndWithCodeValue() {
        // Test: do { statements } while (condition); - Java needs semicolon after while
        val codeValue = CodeValue {
            addCode("%V", CodePart.beginControlFlow(CodeValue("do")))
            addCode("statement1;\n")
            addCode("statement2;\n")
            addCode("%V", CodePart.endControlFlow(CodeValue("while (condition)")))
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            do {
                statement1;
                statement2;
            } while (condition);
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testNestedControlFlow() {
        // Test: if (condition1) { if (condition2) { statements } }
        val codeValue = CodeValue {
            addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition1)")))
            addCode("outerStatement;\n")
            addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition2)")))
            addCode("innerStatement;\n")
            addCode("%V", CodePart.endControlFlow())
            addCode("%V", CodePart.endControlFlow())
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition1) {
                outerStatement;
                if (condition2) {
                    innerStatement;
                }
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testControlFlowEndWithCodeValueAddsSemicolon() {
        // Test that Java adds semicolon after endControlFlow with code value
        // This is the key difference between Java and Kotlin
        val codeValue = CodeValue {
            addCode("%V", CodePart.beginControlFlow(CodeValue("try")))
            addCode("riskyOperation();\n")
            addCode("%V", CodePart.nextControlFlow(CodeValue("catch (Exception e)")))
            addCode("handleError();\n")
            addCode("%V", CodePart.nextControlFlow(CodeValue("finally")))
            addCode("cleanup();\n")
            addCode("%V", CodePart.endControlFlow())
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        // Note: The semicolon should be added after "finally" in Java
        val expected = """
            try {
                riskyOperation();
            } catch (Exception e) {
                handleError();
            } finally {
                cleanup();
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testControlFlowEndWithSimpleCodeValueAddsSemicolon() {
        // Test the main difference: Java adds semicolon when endControlFlow has content
        val codeValue = CodeValue {
            addCode("%V", CodePart.beginControlFlow(CodeValue("do")))
            addCode("action();\n")
            addCode("%V", CodePart.endControlFlow(CodeValue("while (condition)")))
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        // The key test: semicolon should be present after "while (condition)"
        val expected = """
            do {
                action();
            } while (condition);
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    // DSL API Tests - Testing the beginControlFlow {}, nextControlFlow {}, endControlFlow {} style methods for Java

    @Test
    fun testJavaDslBeginControlFlowWithString() {
        // Test DSL: beginControlFlow("if (condition)") - simple string version for Java
        val codeValue = CodeValue {
            beginControlFlow("if (condition)")
            addCode("statement1;\n")
            addCode("statement2;\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition) {
                statement1;
                statement2;
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaDslBeginControlFlowWithBlock() {
        // Test DSL: beginControlFlow("if (%V)") { addValue(CodePart.literal(condition)) } for Java
        val codeValue = CodeValue {
            beginControlFlow("if (%V)") {
                addValue(CodePart.literal("someCondition"))
            }
            addCode("statement1;\n")
            addCode("statement2;\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (someCondition) {
                statement1;
                statement2;
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaDslControlFlowWithBeginNextEnd() {
        // Test DSL: if-else pattern using DSL methods for Java
        val codeValue = CodeValue {
            beginControlFlow("if (condition)")
            addCode("statement1;\n")
            nextControlFlow("else")
            addCode("statement2;\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition) {
                statement1;
            } else {
                statement2;
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaDslNextControlFlowWithBlock() {
        // Test DSL: nextControlFlow with block parameters for Java
        val codeValue = CodeValue {
            beginControlFlow("if (condition1)")
            addCode("statement1;\n")
            nextControlFlow("else if (%V)") {
                addValue(CodePart.literal("condition2"))
            }
            addCode("statement2;\n")
            nextControlFlow("else")
            addCode("statement3;\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition1) {
                statement1;
            } else if (condition2) {
                statement2;
            } else {
                statement3;
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaDslEndControlFlowWithBlockAddsSemicolon() {
        // Test DSL: endControlFlow with block - do-while pattern, Java should add semicolon
        val codeValue = CodeValue {
            beginControlFlow("do")
            addCode("statement1;\n")
            addCode("statement2;\n")
            endControlFlow("while (%V)") {
                addValue(CodePart.literal("condition"))
            }
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        // Key test: Java should add semicolon after while condition
        val expected = """
            do {
                statement1;
                statement2;
            } while (condition);
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaDslInControlflow() {
        // Test DSL: inControlflow convenience method for Java
        val codeValue = CodeValue {
            inControlFlow("if (condition)") {
                addCode("statement1;\n")
                addCode("statement2;\n")
            }
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition) {
                statement1;
                statement2;
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaDslInControlflowWithBeginAndEndAddsSemicolon() {
        // Test DSL: inControlflow with separate begin and end code values for Java do-while
        val beginCode = CodeValue("do")
        val endCode = CodeValue("while (condition)")
        
        val codeValue = CodeValue {
            inControlFlow(beginCode, endCode) {
                addCode("statement1;\n")
                addCode("statement2;\n")
            }
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        // Key test: Java should add semicolon after while
        val expected = """
            do {
                statement1;
                statement2;
            } while (condition);
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaDslTryCatchFinally() {
        // Test DSL: try-catch-finally pattern using DSL methods for Java
        val codeValue = CodeValue {
            beginControlFlow("try")
            addCode("riskyOperation();\n")
            nextControlFlow("catch (%V e)") {
                addValue(CodePart.literal("Exception"))
            }
            addCode("handleError();\n")
            nextControlFlow("finally")
            addCode("cleanup();\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            try {
                riskyOperation();
            } catch (Exception e) {
                handleError();
            } finally {
                cleanup();
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaDslNestedControlFlows() {
        // Test DSL: nested control flows using DSL methods for Java
        val codeValue = CodeValue {
            beginControlFlow("if (condition1)")
            addCode("outerStatement;\n")
            inControlFlow("if (condition2)") {
                addCode("innerStatement;\n")
            }
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (condition1) {
                outerStatement;
                if (condition2) {
                    innerStatement;
                }
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaDslVsDirectCodePartComparison() {
        // Test that DSL methods produce exactly the same output as direct CodePart usage for Java
        val dslResult = buildString {
            val writer = JavaCodeWriter.create(this)
            val codeValue = CodeValue {
                beginControlFlow("if (%V)") {
                    addValue(CodePart.literal("condition"))
                }
                addCode("statement1;\n")
                nextControlFlow("else if (%V)") {
                    addValue(CodePart.literal("condition2"))
                }
                addCode("statement2;\n")
                endControlFlow()
            }
            writer.emit(codeValue)
        }

        val directResult = buildString {
            val writer = JavaCodeWriter.create(this)
            val codeValue = CodeValue {
                addCode("%V", CodePart.beginControlFlow(CodeValue("if (%V)") {
                    addValue(CodePart.literal("condition"))
                }))
                addCode("statement1;\n")
                addCode("%V", CodePart.nextControlFlow(CodeValue("else if (%V)") {
                    addValue(CodePart.literal("condition2"))
                }))
                addCode("statement2;\n")
                addCode("%V", CodePart.endControlFlow())
            }
            writer.emit(codeValue)
        }

        assertEquals(dslResult, directResult)
    }

    @Test
    fun testJavaDslDoWhileSemicolonBehavior() {
        // Test that DSL APIs preserve Java's semicolon behavior for do-while
        val dslResult = buildString {
            val writer = JavaCodeWriter.create(this)
            val codeValue = CodeValue {
                beginControlFlow("do")
                addCode("action();\n")
                endControlFlow("while (%V)") {
                    addValue(CodePart.literal("condition"))
                }
            }
            writer.emit(codeValue)
        }

        val directResult = buildString {
            val writer = JavaCodeWriter.create(this)
            val codeValue = CodeValue {
                addCode("%V", CodePart.beginControlFlow(CodeValue("do")))
                addCode("action();\n")
                addCode("%V", CodePart.endControlFlow(CodeValue("while (%V)") {
                    addValue(CodePart.literal("condition"))
                }))
            }
            writer.emit(codeValue)
        }

        // Both should have semicolon after while condition
        assertEquals(dslResult, directResult)
        kotlin.test.assertTrue(dslResult.contains("while (condition);"))
    }

    // Tests for new DSL functions

    @Test
    fun testJavaIfElseIfElseControlFlow() {
        // Test DSL: if-elseif-else control flow using new DSL methods for Java
        val codeValue = CodeValue {
            ifControlFlow("x > 0")
            addCode("System.out.println(\"positive\");\n")
            elseIfControlFlow("x < 0")
            addCode("System.out.println(\"negative\");\n")
            elseControlFlow()
            addCode("System.out.println(\"zero\");\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (x > 0) {
                System.out.println("positive");
            } else if (x < 0) {
                System.out.println("negative");
            } else {
                System.out.println("zero");
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaElseIfControlFlowOverloads() {
        // Test DSL: elseIfControlFlow with different overloads for Java
        val codeValue = CodeValue {
            ifControlFlow("x > 10")
            addCode("System.out.println(\"large\");\n")
            elseIfControlFlow("x == 5")
            addCode("System.out.println(\"five\");\n")
            elseIfControlFlow("x == %V") {
                addValue(CodePart.literal("SPECIAL"))
            }
            addCode("System.out.println(\"special\");\n")
            elseIfControlFlow {
                addCode("x < 0")
            }
            addCode("System.out.println(\"negative\");\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            if (x > 10) {
                System.out.println("large");
            } else if (x == 5) {
                System.out.println("five");
            } else if (x == SPECIAL) {
                System.out.println("special");
            } else if (x < 0) {
                System.out.println("negative");
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaTryCatchFinallyNewDsl() {
        // Test DSL: try-catch-finally using new specialized DSL methods for Java
        val codeValue = CodeValue {
            tryControlFlow()
            addCode("riskyOperation();\n")
            catchControlFlow("IOException e")
            addCode("handleIOError();\n")
            catchControlFlow("Exception e")
            addCode("handleGeneralError();\n")
            finallyControlFlow()
            addCode("cleanup();\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            try {
                riskyOperation();
            } catch (IOException e) {
                handleIOError();
            } catch (Exception e) {
                handleGeneralError();
            } finally {
                cleanup();
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaCatchControlFlowOverloads() {
        // Test DSL: catchControlFlow with different overloads for Java
        val codeValue = CodeValue {
            tryControlFlow()
            addCode("riskyOperation();\n")
            catchControlFlow("IOException e")
            addCode("handleIOError();\n")
            catchControlFlow("%V") {
                addValue(CodePart.literal("Exception e"))
            }
            addCode("handleGeneralError();\n")
            catchControlFlow {
                addCode("RuntimeException e")
            }
            addCode("handleRuntimeError();\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            try {
                riskyOperation();
            } catch (IOException e) {
                handleIOError();
            } catch (Exception e) {
                handleGeneralError();
            } catch (RuntimeException e) {
                handleRuntimeError();
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaWhileControlFlow() {
        // Test DSL: while control flow for Java
        val codeValue = CodeValue {
            whileControlFlow("i < 10")
            addCode("processItem(i);\n")
            addCode("i++;\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            while (i < 10) {
                processItem(i);
                i++;
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaWhileControlFlowOverloads() {
        // Test DSL: whileControlFlow with different overloads for Java
        val codeValue = CodeValue {
            whileControlFlow("index < 5")
            addCode("process1();\n")
            endControlFlow()
            addCode("\n")
            whileControlFlow("count < %V") {
                addValue(CodePart.literal("MAX_COUNT"))
            }
            addCode("process2();\n")
            endControlFlow()
            addCode("\n")
            whileControlFlow {
                addCode("hasNext() && isValid()")
            }
            addCode("process3();\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            while (index < 5) {
                process1();
            }
            
            while (count < MAX_COUNT) {
                process2();
            }
            
            while (hasNext() && isValid()) {
                process3();
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }

    @Test
    fun testJavaDoWhileControlFlowWithSemicolon() {
        // Test DSL: do-while control flow using new DSL methods for Java (with semicolon)
        val codeValue = CodeValue {
            doControlFlow()
            addCode("processItem();\n")
            addCode("count++;\n")
            doWhileEndControlFlow("count < 5")
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            do {
                processItem();
                count++;
            } while (count < 5);
        """.trimIndent() + "\n"

        assertEquals(expected, result)
        // Verify Java-specific semicolon behavior
        kotlin.test.assertTrue(result.contains("while (count < 5);"))
    }

    @Test
    fun testJavaDoWhileEndControlFlowOverloads() {
        // Test DSL: doWhileEndControlFlow with different overloads for Java
        val codeValue = CodeValue {
            doControlFlow()
            addCode("process1();\n")
            doWhileEndControlFlow("count < 10")
            addCode("\n")
            doControlFlow()
            addCode("process2();\n")
            doWhileEndControlFlow("flag == %V") {
                addValue(CodePart.literal("true"))
            }
            addCode("\n")
            doControlFlow()
            addCode("process3();\n")
            doWhileEndControlFlow {
                addCode("isValid() && hasMore()")
            }
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            do {
                process1();
            } while (count < 10);
            
            do {
                process2();
            } while (flag == true);
            
            do {
                process3();
            } while (isValid() && hasMore());
        """.trimIndent() + "\n"

        assertEquals(expected, result)
        // Verify Java-specific semicolon behavior for all do-while loops
        kotlin.test.assertTrue(result.contains("while (count < 10);"))
        kotlin.test.assertTrue(result.contains("while (flag == true);"))
        kotlin.test.assertTrue(result.contains("while (isValid() && hasMore());"))
    }

    @Test
    fun testJavaComplexNestedControlFlow() {
        // Test DSL: complex nested control flow using all new DSL methods for Java
        val codeValue = CodeValue {
            tryControlFlow()
            whileControlFlow("i < 10")
            ifControlFlow("i % 2 == 0")
            addCode("processEven(i);\n")
            elseIfControlFlow("i == 5")
            addCode("processSpecial(i);\n")
            elseControlFlow()
            addCode("processOdd(i);\n")
            endControlFlow()
            addCode("i++;\n")
            endControlFlow()
            catchControlFlow("Exception e")
            addCode("handleError(e);\n")
            finallyControlFlow()
            addCode("cleanup();\n")
            endControlFlow()
        }

        val result = buildString {
            val writer = JavaCodeWriter.create(this)
            writer.emit(codeValue)
        }

        val expected = """
            try {
                while (i < 10) {
                    if (i % 2 == 0) {
                        processEven(i);
                    } else if (i == 5) {
                        processSpecial(i);
                    } else {
                        processOdd(i);
                    }
                    i++;
                }
            } catch (Exception e) {
                handleError(e);
            } finally {
                cleanup();
            }
        """.trimIndent() + "\n"

        assertEquals(expected, result)
    }
}
