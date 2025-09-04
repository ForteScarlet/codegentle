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
package love.forte.codegentle.kotlin

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.spec.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for KotlinFile functionality.
 */
class KotlinFileTests {

    @Test
    fun testTopLevelFunction() {
        val packageName = "com.example".parseToPackageName()
        val function = KotlinFunctionSpec("myFunction", ClassName("kotlin", "Unit").ref()) {
            addCode("println(\"Hello, World!\")")
        }

        val kotlinFile = KotlinFile(packageName) {
            addFunction(function)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                fun myFunction() {
                    println("Hello, World!")
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testTopLevelProperty() {
        val packageName = "com.example".parseToPackageName()
        val property = KotlinPropertySpec("greeting", ClassName("kotlin", "String").ref()) {
            initializer("\"Hello, World!\"")
        }

        val kotlinFile = KotlinFile(packageName) {
            addProperty(property)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                val greeting: String = "Hello, World!"
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testMixedTopLevelElements() {
        val packageName = "com.example".parseToPackageName()
        val property = KotlinPropertySpec("greeting", ClassName("kotlin", "String").ref()) {
            initializer("\"Hello, World!\"")
        }
        val function = KotlinFunctionSpec("sayHello", ClassName("kotlin", "Unit").ref()) {
            addCode("println(greeting)")
        }
        val typeSpec = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Greeter")

        val kotlinFile = KotlinFile(packageName) {
            addProperty(property)
            addFunction(function)
            addType(typeSpec)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                val greeting: String = "Hello, World!"

                class Greeter
                
                fun sayHello() {
                    println(greeting)
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testMultipleFunctions() {
        val packageName = "com.example".parseToPackageName()
        val function1 = KotlinFunctionSpec("function1", ClassName("kotlin", "Unit").ref()) {
            addCode("println(\"Function 1\")")
        }
        val function2 = KotlinFunctionSpec("function2", ClassName("kotlin", "Unit").ref()) {
            addCode("println(\"Function 2\")")
        }
        val function3 = KotlinFunctionSpec("function3", ClassName("kotlin", "Unit").ref()) {
            addCode("println(\"Function 3\")")
        }

        val kotlinFile = KotlinFile(packageName) {
            addFunctions(function1, function2, function3)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                fun function1() {
                    println("Function 1")
                }

                fun function2() {
                    println("Function 2")
                }

                fun function3() {
                    println("Function 3")
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testMultipleProperties() {
        val packageName = "com.example".parseToPackageName()
        val property1 = KotlinPropertySpec("property1", ClassName("kotlin", "String").ref()) {
            initializer("\"Property 1\"")
        }
        val property2 = KotlinPropertySpec("property2", ClassName("kotlin", "Int").ref()) {
            initializer("42")
        }
        val property3 = KotlinPropertySpec("property3", ClassName("kotlin", "Boolean").ref()) {
            initializer("true")
        }

        val kotlinFile = KotlinFile(packageName) {
            addProperties(listOf(property1, property2, property3))
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                val property1: String = "Property 1"

                val property2: Int = 42

                val property3: Boolean = true
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testFileWithOnlyFunctionsAndProperties() {
        val packageName = "com.example".parseToPackageName()
        val property = KotlinPropertySpec("config", ClassName("kotlin", "String").ref()) {
            initializer("\"Configuration\"")
        }
        val function = KotlinFunctionSpec("configure", ClassName("kotlin", "Unit").ref()) {
            addCode("println(config)")
        }

        val kotlinFile = KotlinFile(packageName) {
            addProperty(property)
            addFunction(function)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                val config: String = "Configuration"

                fun configure() {
                    println(config)
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testMultipleTypesInKotlinFile() {
        val class1 = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Class1")
        val class2 = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Class2")
        val interface1 = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.INTERFACE, "Interface1")

        val packageName = "com.example".parseToPackageName()

        val kotlinFile = KotlinFile(packageName) {
            addType(class1)
            addType(class2)
            addType(interface1)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                class Class1

                class Class2

                interface Interface1
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testMultipleTypesWithVarargs() {
        val class1 = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Class1")
        val class2 = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Class2")

        val packageName = "com.example".parseToPackageName()

        val kotlinFile = KotlinFile(packageName, class1, class2)

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                class Class1

                class Class2
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testMultipleTypesWithIterable() {
        val class1 = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Class1")
        val class2 = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Class2")

        val packageName = "com.example".parseToPackageName()
        val types = listOf(class1, class2)

        val kotlinFile = KotlinFile(packageName, types)

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                class Class1

                class Class2
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testBasicKotlinFile() {
        val typeSpec = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass")
        val packageName = "com.example".parseToPackageName()

        val kotlinFile = KotlinFile(packageName, typeSpec)

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                class MyClass
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testKotlinFileWithComment() {
        val typeSpec = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass")
        val packageName = "com.example".parseToPackageName()

        val kotlinFile = KotlinFile(packageName, typeSpec) {
            addFileComment("This is a test file.")
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                // This is a test file.
                package com.example

                class MyClass
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testKotlinFileWithStaticImports() {
        val typeSpec = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass")
        val packageName = "com.example".parseToPackageName()

        val kotlinFile = KotlinFile(packageName, typeSpec) {
            addStaticImport(ClassName("kotlin.collections", "Collections"), "emptyList")
            addStaticImport(ClassName("kotlin.collections", "Collections"), "emptyMap")
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                import kotlin.collections.Collections.emptyList
                import kotlin.collections.Collections.emptyMap

                class MyClass
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testKotlinFileWithSkipKotlinImports() {
        val property = KotlinPropertySpec("name", ClassName("kotlin", "String").ref())
        val typeSpec = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass") {
            addProperty(property)
        }
        val packageName = "com.example".parseToPackageName()

        val kotlinFile = KotlinFile(packageName, typeSpec)

        val output = kotlinFile.writeToKotlinString()

        // Should not contain import for kotlin.String (verified by the complete output check below)
        assertEquals(
            """
                package com.example

                class MyClass {
                    val name: String
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testKotlinFileWithCustomIndent() {
        val typeSpec = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass")
        val packageName = "com.example".parseToPackageName()

        val kotlinFile = KotlinFile(packageName, typeSpec) {
            indent("\t") // Use tab for indentation
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                class MyClass
            """.trimIndent(),
            output
        )
    }

    // ========== COMPREHENSIVE TYPE TESTS ==========

    @Test
    fun testSealedClass() {
        val packageName = "com.example".parseToPackageName()
        val sealedClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Result") {
            addModifier(KotlinModifier.SEALED)
        }

        val kotlinFile = KotlinFile(packageName) {
            addType(sealedClass)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                sealed class Result
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testSealedInterface() {
        val packageName = "com.example".parseToPackageName()
        val sealedInterface = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.INTERFACE, "State") {
            addModifier(KotlinModifier.SEALED)
        }

        val kotlinFile = KotlinFile(packageName) {
            addType(sealedInterface)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                sealed interface State
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testDataClass() {
        val packageName = "com.example".parseToPackageName()
        val property = KotlinPropertySpec("name", ClassName("kotlin", "String").ref())
        val dataClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Person") {
            addModifier(KotlinModifier.DATA)
            addProperty(property)
        }

        val kotlinFile = KotlinFile(packageName) {
            addType(dataClass)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                data class Person {
                    val name: String
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testValueClass() {
        val packageName = "com.example".parseToPackageName()
        val parameter = KotlinValueParameterSpec("value", ClassName("kotlin", "String").ref()) {
            immutableProperty()
        }
        val constructor = KotlinConstructorSpec {
            addParameter(parameter)
        }
        val valueClass = KotlinValueClassTypeSpec("UserId", constructor)

        val kotlinFile = KotlinFile(packageName) {
            addType(valueClass)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                value class UserId(val value: String)
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testEnumClass() {
        val packageName = "com.example".parseToPackageName()
        val enumClass = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED")
            addEnumConstant("GREEN")
            addEnumConstant("BLUE")
        }

        val kotlinFile = KotlinFile(packageName) {
            addType(enumClass)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testAnnotationClass() {
        val packageName = "com.example".parseToPackageName()
        val annotationClass = KotlinAnnotationTypeSpec("MyAnnotation")

        val kotlinFile = KotlinFile(packageName) {
            addType(annotationClass)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                annotation class MyAnnotation
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testObject() {
        val packageName = "com.example".parseToPackageName()
        val objectType = KotlinObjectTypeSpec("Singleton")

        val kotlinFile = KotlinFile(packageName, objectType)

        val output = kotlinFile.writeToKotlinString()

        // Temporarily use assertEquals to see actual output
        assertEquals(
            """
            package com.example
            
            object Singleton
        """.trimIndent(), output
        )
    }

    @Test
    fun testCompanionObject() {
        val packageName = "com.example".parseToPackageName()
        val companionObject = KotlinObjectTypeSpec { }
        val classWithCompanion = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass") {
            addSubtype(companionObject)
        }

        val kotlinFile = KotlinFile(packageName) {
            addType(classWithCompanion)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example
                
                class MyClass {
                    companion object
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testFunInterface() {
        val packageName = "com.example".parseToPackageName()
        val funInterface = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.INTERFACE, "Processor") {
            addModifier(KotlinModifier.FUN)
            addFunction(
                KotlinFunctionSpec("process", ClassName("kotlin", "Unit").ref()) {
                    addParameter("input", ClassName("kotlin", "String").ref())
                }
            )
        }

        val kotlinFile = KotlinFile(packageName) {
            addType(funInterface)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example
                
                fun interface Processor {
                    fun process(input: String)
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testComplexClassWithAllElements() {
        val packageName = "com.example".parseToPackageName()

        // Create a function with parameters
        val function = KotlinFunctionSpec("greet", ClassName("kotlin", "String").ref()) {
            addModifier(KotlinModifier.PUBLIC)
            addParameter("greeting", ClassName("kotlin", "String").ref())
            addCode("return \"\$greeting, \$name!\"")
        }

        // Create a constructor
        val constructor = KotlinConstructorSpec {
            addParameter(
                name = "initialName",
                type = ClassName("kotlin", "String").ref()
            )
        }

        // Create a simple property
        val property = KotlinPropertySpec("name", ClassName("kotlin", "String").ref()) {
            mutable(true)
            addModifier(KotlinModifier.PRIVATE)
            initializer("initialName")
        }

        // Create a nested class
        val nestedClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "NestedClass") {
            addModifier(KotlinModifier.INNER)
        }

        val complexClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "ComplexClass") {
            addModifier(KotlinModifier.OPEN)
            primaryConstructor(constructor)
            addProperty(property)
            addFunction(function)
            addSubtype(nestedClass)
        }

        val kotlinFile = KotlinFile(packageName) {
            addType(complexClass)
        }

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            $$"""
                |package com.example
                |
                |open class ComplexClass(initialName: String) {
                |    private var name: String = initialName
                |
                |    public fun greet(greeting: String): String = "$greeting, $name!"
                |
                |    inner class NestedClass
                |}
            """.trimMargin(),
            output
        )
    }
}
