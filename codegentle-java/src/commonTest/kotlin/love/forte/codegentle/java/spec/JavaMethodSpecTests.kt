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
package love.forte.codegentle.java.spec

import love.forte.codegentle.common.naming.ArrayTypeName
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.naming.JavaPrimitiveTypeNames
import love.forte.codegentle.java.writer.writeToJavaString
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [JavaMethodSpec] functionality.
 */
class JavaMethodSpecTests {

    @Test
    fun testBasicMethod() {
        val method = JavaMethodSpec("hello") {
            addModifier(JavaModifier.PUBLIC)
            returns(JavaPrimitiveTypeNames.VOID.ref())
            addCode("System.out.println(\"Hello, World!\");")
        }

        val result = method.writeToJavaString()
        
        val expected = """
            public void hello() {
                System.out.println("Hello, World!");
            }
        """.trimIndent()
        
        assertEquals(expected, result)
    }

    @Test
    fun testMethodWithParameters() {
        val method = JavaMethodSpec("greet") {
            addModifier(JavaModifier.PUBLIC)
            returns(ClassName("java.lang", "String").ref())
            addParameter(JavaParameterSpec("name", ClassName("java.lang", "String").ref()) { })
            addParameter(JavaParameterSpec("age", JavaPrimitiveTypeNames.INT.ref()) { })
            addCode("return \"Hello, \" + name + \"! You are \" + age + \" years old.\";")
        }

        val result = method.writeToJavaString()
        
        val expected = """
            public String greet(String name, int age) {
                return "Hello, " + name + "! You are " + age + " years old.";
            }
        """.trimIndent()
        
        assertEquals(expected, result)
    }

    @Test
    fun testConstructor() {
        val constructor = JavaMethodSpec {
            addModifier(JavaModifier.PUBLIC)
            addParameter(JavaParameterSpec("name", ClassName("java.lang", "String").ref()) { })
            addCode("this.name = name;")
        }

        val result = constructor.writeToJavaString()
        
        val expected = """
            public null(String name) {
                this.name = name;
            }
        """.trimIndent()
        
        assertEquals(expected, result)
    }

    @Test
    fun testMethodWithSimpleDocumentation() {
        val method = JavaMethodSpec("calculateSum") {
            addModifier(JavaModifier.PUBLIC)
            returns(JavaPrimitiveTypeNames.INT.ref())
            addDoc("Calculates the sum of two numbers.")
            addParameter(JavaParameterSpec("a", JavaPrimitiveTypeNames.INT.ref()) { })
            addParameter(JavaParameterSpec("b", JavaPrimitiveTypeNames.INT.ref()) { })
            addCode("return a + b;")
        }

        val result = method.writeToJavaString()
        
        val expected = """
            /**
             * Calculates the sum of two numbers.
             */
            public int calculateSum(int a, int b) {
                return a + b;
            }
        """.trimIndent()
        
        assertEquals(expected, result)
    }

    @Test
    fun testMethodWithMultiLineDocumentation() {
        val method = JavaMethodSpec("processData") {
            addModifier(JavaModifier.PUBLIC)
            returns(JavaPrimitiveTypeNames.VOID.ref())
            addDoc("Processes the given data.\n\nThis method performs complex operations on the input data\nand produces the desired output.")
            addParameter(JavaParameterSpec("data", ClassName("java.lang", "String").ref()) { })
            addCode("// Process data here")
        }

        val result = method.writeToJavaString()
        
        val expected = """
            /**
             * Processes the given data.
             *
             * This method performs complex operations on the input data
             * and produces the desired output.
             */
            public void processData(String data) {
                // Process data here
            }
        """.trimIndent()
        
        assertEquals(expected, result)
    }

    @Test
    fun testMethodWithParameterDocumentation() {
        val method = JavaMethodSpec("createPerson") {
            addModifier(JavaModifier.PUBLIC)
            returns(ClassName("com.example", "Person").ref())
            addDoc("Creates a new Person instance.")
            addParameter(
                JavaParameterSpec(
                    "name",
                    ClassName("java.lang", "String").ref()
                ) { addDoc("the name of the person") })
            addParameter(JavaParameterSpec("age", JavaPrimitiveTypeNames.INT.ref()) { addDoc("the age of the person") })
            addCode("return new Person(name, age);")
        }

        val result = method.writeToJavaString()
        
        val expected = """
            /**
             * Creates a new Person instance.
             * @param name the name of the person
             * @param age the age of the person
             */
            public com.example.Person createPerson(String name, int age) {
                return new Person(name, age);
            }
        """.trimIndent()
        
        assertEquals(expected, result)
    }

    @Test
    fun testAbstractMethod() {
        val method = JavaMethodSpec("process") {
            addModifier(JavaModifier.PUBLIC)
            addModifier(JavaModifier.ABSTRACT)
            returns(JavaPrimitiveTypeNames.VOID.ref())
            addDoc("Abstract method to be implemented by subclasses.")
        }

        val result = method.writeToJavaString()
        
        val expected = """
            /**
             * Abstract method to be implemented by subclasses.
             */
            public abstract void process();
        """.trimIndent()
        
        assertEquals(expected, result)
    }

    @Test
    fun testMethodWithExceptions() {
        val method = JavaMethodSpec("readFile") {
            addModifier(JavaModifier.PUBLIC)
            returns(ClassName("java.lang", "String").ref())
            addParameter(JavaParameterSpec("filename", ClassName("java.lang", "String").ref()) { })
            addException(ClassName("java.io", "IOException").ref())
            addException(ClassName("java.io", "FileNotFoundException").ref())
            addCode("// File reading logic here\nreturn \"file content\";")
        }

        val result = method.writeToJavaString()
        
        val expected = """
            public String readFile(String filename) throws java.io.IOException, java.io.FileNotFoundException {
                // File reading logic here
                return "file content";
            }
        """.trimIndent()
        
        assertEquals(expected, result)
    }

    @Test
    fun testMainMethod() {
        val method = JavaMethodSpec("main") {
            addModifier(JavaModifier.PUBLIC)
            addModifier(JavaModifier.STATIC)
            returns(JavaPrimitiveTypeNames.VOID.ref())
            addParameter(JavaParameterSpec("args", ArrayTypeName(ClassName("java.lang", "String").ref()).ref()) { })
            addCode("System.out.println(\"Hello, World!\");")
        }

        val result = method.writeToJavaString()
        
        val expected = """
            public static void main(String[] args) {
                System.out.println("Hello, World!");
            }
        """.trimIndent()
        
        assertEquals(expected, result)
    }

    @Test
    fun testVarArgsMethod() {
        val method = JavaMethodSpec("sum") {
            addModifier(JavaModifier.PUBLIC)
            addModifier(JavaModifier.STATIC)
            returns(JavaPrimitiveTypeNames.INT.ref())
            addParameter(JavaParameterSpec("numbers", JavaPrimitiveTypeNames.INT.ref()) { })
            varargs(true)
            addCode("int sum = 0;\nfor (int num : numbers) {\n    sum += num;\n}\nreturn sum;")
        }

        val result = method.writeToJavaString()
        
        val expected = """
            public static int sum(int... numbers) {
                int sum = 0;
                for (int num : numbers) {
                    sum += num;
                }
                return sum;
            }
        """.trimIndent()
        
        assertEquals(expected, result)
    }
}
