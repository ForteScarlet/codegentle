package love.forte.codegentle.kotlin

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.kotlin.ref.kotlinRef
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
        val function = KotlinFunctionSpec.builder("myFunction", ClassName("kotlin", "Unit").kotlinRef())
            .addCode("println(\"Hello, World!\")")
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addFunction(function)
            .build()

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                fun myFunction(): Unit {
                    println("Hello, World!")
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testTopLevelProperty() {
        val packageName = "com.example".parseToPackageName()
        val property = KotlinPropertySpec.builder("greeting", ClassName("kotlin", "String").kotlinRef())
            .initializer("\"Hello, World!\"")
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addProperty(property)
            .build()

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
        val property = KotlinPropertySpec.builder("greeting", ClassName("kotlin", "String").kotlinRef())
            .initializer("\"Hello, World!\"")
            .build()
        val function = KotlinFunctionSpec.builder("sayHello", ClassName("kotlin", "Unit").kotlinRef())
            .addCode("println(greeting)")
            .build()
        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Greeter").build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addProperty(property)
            .addFunction(function)
            .addType(typeSpec)
            .build()

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                val greeting: String = "Hello, World!"

                fun sayHello(): Unit {
                    println(greeting)
                }

                class Greeter
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testMultipleFunctions() {
        val packageName = "com.example".parseToPackageName()
        val function1 = KotlinFunctionSpec.builder("function1", ClassName("kotlin", "Unit").kotlinRef())
            .addCode("println(\"Function 1\")")
            .build()
        val function2 = KotlinFunctionSpec.builder("function2", ClassName("kotlin", "Unit").kotlinRef())
            .addCode("println(\"Function 2\")")
            .build()
        val function3 = KotlinFunctionSpec.builder("function3", ClassName("kotlin", "Unit").kotlinRef())
            .addCode("println(\"Function 3\")")
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addFunctions(function1, function2, function3)
            .build()

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                fun function1(): Unit {
                    println("Function 1")
                }

                fun function2(): Unit {
                    println("Function 2")
                }

                fun function3(): Unit {
                    println("Function 3")
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testMultipleProperties() {
        val packageName = "com.example".parseToPackageName()
        val property1 = KotlinPropertySpec.builder("property1", ClassName("kotlin", "String").kotlinRef())
            .initializer("\"Property 1\"")
            .build()
        val property2 = KotlinPropertySpec.builder("property2", ClassName("kotlin", "Int").kotlinRef())
            .initializer("42")
            .build()
        val property3 = KotlinPropertySpec.builder("property3", ClassName("kotlin", "Boolean").kotlinRef())
            .initializer("true")
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addProperties(listOf(property1, property2, property3))
            .build()

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
        val property = KotlinPropertySpec.builder("config", ClassName("kotlin", "String").kotlinRef())
            .initializer("\"Configuration\"")
            .build()
        val function = KotlinFunctionSpec.builder("configure", ClassName("kotlin", "Unit").kotlinRef())
            .addCode("println(config)")
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addProperty(property)
            .addFunction(function)
            .build()

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example

                val config: String = "Configuration"

                fun configure(): Unit {
                    println(config)
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testMultipleTypesInKotlinFile() {
        val class1 = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Class1").build()
        val class2 = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Class2").build()
        val interface1 = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.INTERFACE, "Interface1").build()

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
        val class1 = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Class1").build()
        val class2 = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Class2").build()

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
        val class1 = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Class1").build()
        val class2 = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Class2").build()

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
        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "MyClass").build()
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
        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "MyClass").build()
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
        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "MyClass").build()
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
        val property = KotlinPropertySpec.builder("name", ClassName("kotlin", "String").kotlinRef()).build()
        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "MyClass")
            .addProperty(property)
            .build()
        val packageName = "com.example".parseToPackageName()

        val kotlinFile = KotlinFile(packageName, typeSpec) {
            skipKotlinImports(true)
        }

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
        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "MyClass").build()
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
        val sealedClass = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Result")
            .addModifier(KotlinModifier.SEALED)
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addType(sealedClass)
            .build()

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
        val sealedInterface = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.INTERFACE, "State")
            .addModifier(KotlinModifier.SEALED)
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addType(sealedInterface)
            .build()

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
        val property = KotlinPropertySpec.builder("name", ClassName("kotlin", "String").kotlinRef())
            .build()
        val dataClass = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Person")
            .addModifier(KotlinModifier.DATA)
            .addProperty(property)
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addType(dataClass)
            .build()

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
        val parameter = KotlinValueParameterSpec.builder("value", ClassName("kotlin", "String").kotlinRef())
            .immutableProperty()
            .build()
        val constructor = KotlinConstructorSpec.builder()
            .addParameter(parameter)
            .build()
        val valueClass = KotlinValueClassSpec.builder("UserId", constructor)
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addType(valueClass)
            .build()

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
        val enumClass = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED")
            .addEnumConstant("GREEN")
            .addEnumConstant("BLUE")
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addType(enumClass)
            .build()

        val output = kotlinFile.writeToKotlinString()

        // TODO 元素紧凑？
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
        val annotationClass = KotlinAnnotationTypeSpec.builder("MyAnnotation")
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addType(annotationClass)
            .build()

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
        val objectType = KotlinObjectTypeSpec.builder("Singleton")
            .build()

        val kotlinFile = KotlinFile.builder(packageName, objectType).build()

        val output = kotlinFile.writeToKotlinString()

        // Temporarily use assertEquals to see actual output
        assertEquals("""
            package com.example
            
            object Singleton
        """.trimIndent(), output)
    }

    @Test
    fun testCompanionObject() {
        val packageName = "com.example".parseToPackageName()
        val companionObject = KotlinObjectTypeSpec.companionBuilder()
            .build()
        val classWithCompanion = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "MyClass")
            .addSubtype(companionObject)
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addType(classWithCompanion)
            .build()

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
        val funInterface = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.INTERFACE, "Processor")
            .addModifier(KotlinModifier.FUN)
            .addFunction(
                KotlinFunctionSpec.builder("process", ClassName("kotlin", "Unit").kotlinRef())
                    .addParameter("input", ClassName("kotlin", "String").kotlinRef())
                    .build()
            )
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addType(funInterface)
            .build()

        val output = kotlinFile.writeToKotlinString()

        assertEquals(
            """
                package com.example
                
                fun interface Processor {
                    fun process(input: String): Unit
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testComplexClassWithAllElements() {
        val packageName = "com.example".parseToPackageName()

        // Create a function with parameters
        val function = KotlinFunctionSpec.builder("greet", ClassName("kotlin", "String").kotlinRef())
            .addModifier(KotlinModifier.PUBLIC)
            .addParameter("greeting", ClassName("kotlin", "String").kotlinRef())
            .addCode("return \"\$greeting, \$name!\"")
            .build()

        // Create a constructor
        val constructor = KotlinConstructorSpec.builder()
            .addParameter(
                name = "initialName",
                type = ClassName("kotlin", "String").kotlinRef()
            )
            .build()

        // Create a simple property
        val property = KotlinPropertySpec.builder("name", ClassName("kotlin", "String").kotlinRef())
            .mutable(true)
            .addModifier(KotlinModifier.PRIVATE)
            .initializer("initialName")
            .build()

        // Create a nested class
        val nestedClass = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "NestedClass")
            .addModifier(KotlinModifier.INNER)
            .build()

        val complexClass = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "ComplexClass")
            .addModifier(KotlinModifier.OPEN)
            .primaryConstructor(constructor)
            .addProperty(property)
            .addFunction(function)
            .addSubtype(nestedClass)
            .build()

        val kotlinFile = KotlinFile.builder(packageName)
            .addType(complexClass)
            .build()

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
