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
package love.forte.codegentle.kotlin.writer

import love.forte.codegentle.common.code.addCode
import love.forte.codegentle.common.code.emitName
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [KotlinCodeWriter].
 */
class KotlinCodeWriterTests {

    @Test
    fun testSimpleClass() {
        val className = ClassName("com.example", "MyClass")
        val typeRef = className.ref()

        val propertySpec = KotlinPropertySpec.builder("myProperty", typeRef)
            .addDoc("This is a property.")
            .build()

        val functionSpec = KotlinFunctionSpec.builder("myFunction", typeRef)
            .addDoc("This is a function.")
            .addCode("return %V") {
                emitName("myProperty")
            }
            .build()

        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "MyClass")
            .addDoc("This is a class.")
            .addProperty(propertySpec)
            .addFunction(functionSpec)
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            typeSpec.emitTo(writer)
        }

        val expected = """
            /**
             * This is a class.
             */
            class MyClass {
                /**
                 * This is a property.
                 */
                val myProperty: com.example.MyClass

                /**
                 * This is a function.
                 */
                fun myFunction(): com.example.MyClass = myProperty
            }
        """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun testInterface() {
        val className = ClassName("com.example", "MyInterface")
        val typeRef = className.ref()

        val propertySpec = KotlinPropertySpec.builder("myProperty", typeRef)
            .addDoc("This is an interface property.")
            .build()

        val functionSpec = KotlinFunctionSpec.builder("myFunction", typeRef)
            .addDoc("This is an interface function.")
            .build()

        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.INTERFACE, "MyInterface")
            .addDoc("This is an interface.")
            .addProperty(propertySpec)
            .addFunction(functionSpec)
            .build()

        println("[DEBUG_LOG] Interface test - typeSpec kind: ${typeSpec.kind}")

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            println("[DEBUG_LOG] Interface test - writer typeSpecStack before: ${writer.typeSpecStack}")
            typeSpec.emitTo(writer)
            println("[DEBUG_LOG] Interface test - writer typeSpecStack after: ${writer.typeSpecStack}")
        }

        val expected = """
            /**
             * This is an interface.
             */
            interface MyInterface {
                /**
                 * This is an interface property.
                 */
                val myProperty: com.example.MyInterface

                /**
                 * This is an interface function.
                 */
                fun myFunction(): com.example.MyInterface
            }
        """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun testClassWithModifiers() {
        val className = ClassName("com.example", "MyClass")
        val typeRef = className.ref()

        val propertySpec = KotlinPropertySpec.builder("myProperty", typeRef)
            .addModifier(KotlinModifier.PRIVATE)
            .build()

        val functionSpec = KotlinFunctionSpec.builder("myFunction", typeRef)
            .addModifier(KotlinModifier.PUBLIC)
            .addModifier(KotlinModifier.OVERRIDE)
            .addCode("return %V") {
                emitName("myProperty")
            }
            .build()

        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "MyClass")
            .addModifier(KotlinModifier.DATA)
            .addProperty(propertySpec)
            .addFunction(functionSpec)
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            typeSpec.emitTo(writer)
        }

        val expected = """
            data class MyClass {
                private val myProperty: com.example.MyClass

                public override fun myFunction(): com.example.MyClass = myProperty
            }
        """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun testNestedTypes() {
        val innerClass = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "InnerClass")
            .addDoc("This is an inner class.")
            .addModifier(KotlinModifier.INNER)
            .build()

        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "OuterClass")
            .addDoc("This is an outer class.")
            .addSubtype(innerClass)
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            typeSpec.emitTo(writer)
        }

        val expected = """
            /**
             * This is an outer class.
             */
            class OuterClass {
                /**
                 * This is an inner class.
                 */
                inner class InnerClass
            }
        """.trimIndent()

        assertEquals(expected, result)
    }

    @Test
    fun testFunctionWithParameters() {
        val className = ClassName("com.example", "MyClass")
        val typeRef = className.ref()

        val param1 = KotlinValueParameterSpec.builder("param1", typeRef)
            .addDoc("This is parameter 1.")
            .build()

        val param2 = KotlinValueParameterSpec.builder("param2", typeRef)
            .addDoc("This is parameter 2.")
            .defaultValue("null")
            .build()

        val functionSpec = KotlinFunctionSpec.builder("myFunction", typeRef)
            .addDoc("This is a function with parameters.")
            .addParameter(param1)
            .addParameter(param2)
            .addCode("return param1")
            .build()

        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "MyClass")
            .addFunction(functionSpec)
            .build()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            typeSpec.emitTo(writer)
        }

        val expected = """
            class MyClass {
                /**
                 * This is a function with parameters.
                 */
                fun myFunction(
                    /**
                     * This is parameter 1.
                     */
                    param1: com.example.MyClass,
                    /**
                     * This is parameter 2.
                     */
                    param2: com.example.MyClass = null
                ): com.example.MyClass = param1
            }
        """.trimIndent()

        assertEquals(expected, result)
    }
}
