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

import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.java.naming.JavaClassNames
import love.forte.codegentle.java.naming.JavaPrimitiveTypeNames
import love.forte.codegentle.java.spec.JavaFieldSpec
import love.forte.codegentle.java.spec.JavaMethodSpec
import love.forte.codegentle.java.spec.JavaParameterSpec
import love.forte.codegentle.java.spec.JavaRecordTypeSpec
import love.forte.codegentle.java.strategy.ToStringJavaWriteStrategy
import love.forte.codegentle.java.writer.writeToJavaString
import kotlin.test.Test
import kotlin.test.assertEquals

class JavaRecordInitializerBlockTest {

    @Test
    fun testRecordWithInitializerBlock() {
        val record = JavaRecordTypeSpec("Person") {
            addModifiers(JavaModifier.PUBLIC)

            // Add record components
            addMainConstructorParameter(
                JavaParameterSpec("name", JavaClassNames.STRING.ref())
            )
            addMainConstructorParameter(
                JavaParameterSpec("age", JavaPrimitiveTypeNames.INT.ref())
            )

            // Add a field that will be initialized in the initializer block
            addField(
                JavaFieldSpec(JavaClassNames.STRING.ref(), "displayName") {
                    addModifiers(JavaModifier.PRIVATE)
                    addModifiers(JavaModifier.STATIC)
                }
            )

            // Add initializer block
            addInitializerBlock(
                "System.out.println(\"Initializing Person: \" + name);\n" +
                    "this.displayName = name.toUpperCase();"
            )

            // Add a method that uses the initialized field
            addMethod(
                JavaMethodSpec("getDisplayName") {
                    addModifiers(JavaModifier.PUBLIC)
                    returns(JavaClassNames.STRING.ref())
                    addStatement("return displayName")
                }
            )
        }

        val file = JavaFile("com.example".parseToPackageName(), record)
        val generatedCode = file.writeToJavaString(ToStringJavaWriteStrategy)

        val expectedCode = """
            |package com.example;
            |
            |public record Person(String name, int age) {
            |    private static String displayName;
            |
            |    {
            |        System.out.println("Initializing Person: " + name);
            |        this.displayName = name.toUpperCase();
            |    }
            |
            |    public String getDisplayName() {
            |        return displayName;
            |    }
            |}
        """.trimMargin()

        assertEquals(expectedCode, generatedCode)
    }

    @Test
    fun testRecordWithMultipleInitializerBlocks() {
        val record = JavaRecordTypeSpec("Product") {
            addModifiers(JavaModifier.PUBLIC)

            // Add record components
            addMainConstructorParameter(
                JavaParameterSpec("name", JavaClassNames.STRING.ref()) { }
            )
            addMainConstructorParameter(
                JavaParameterSpec("price", JavaPrimitiveTypeNames.DOUBLE.ref()) { }
            )

            // Add fields
            addField(
                JavaFieldSpec(JavaPrimitiveTypeNames.BOOLEAN.ref(), "isValid") {
                    addModifiers(JavaModifier.PRIVATE)
                }
            )

            // Add first initializer block
            addInitializerBlock(
                "System.out.println(\"First initializer block\");\n" +
                    "this.isValid = price > 0;\n"
            )

            // Add second initializer block
            addInitializerBlock(
                "System.out.println(\"Second initializer block\");\n" +
                    "if (!isValid) throw new IllegalArgumentException(\"Invalid price\");"
            )
        }

        val file = JavaFile("com.example".parseToPackageName(), record)
        val generatedCode = buildString { file.writeTo(this, ToStringJavaWriteStrategy) }

        val expectedCode = """
            |package com.example;
            |
            |public record Product(String name, double price) {
            |    private boolean isValid;
            |
            |    {
            |        System.out.println("First initializer block");
            |        this.isValid = price > 0;
            |        System.out.println("Second initializer block");
            |        if (!isValid) throw new IllegalArgumentException("Invalid price");
            |    }
            |}
        """.trimMargin()

        assertEquals(expectedCode, generatedCode)
    }

    @Test
    fun testRecordWithoutInitializerBlock() {
        val record = JavaRecordTypeSpec("SimpleRecord") {
            addModifiers(JavaModifier.PUBLIC)

            // Add record components
            addMainConstructorParameter(
                JavaParameterSpec("value", JavaClassNames.STRING.ref()) { }
            )

            // Add a method without initializer block
            addMethod(
                JavaMethodSpec("getValue") {
                    addModifiers(JavaModifier.PUBLIC)
                    returns(JavaClassNames.STRING.ref())
                    addStatement("return value")
                }
            )
        }

        val file = JavaFile("com.example".parseToPackageName(), record)
        val generatedCode = buildString { file.writeTo(this, ToStringJavaWriteStrategy) }

        val expectedCode = """
            |package com.example;
            |
            |public record SimpleRecord(String value) {
            |    public String getValue() {
            |        return value;
            |    }
            |}
        """.trimMargin()

        assertEquals(expectedCode, generatedCode)
    }
}
