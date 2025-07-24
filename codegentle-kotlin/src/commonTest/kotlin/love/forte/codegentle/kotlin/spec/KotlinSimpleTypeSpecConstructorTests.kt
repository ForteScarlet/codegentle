package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for constructor-related functionality in [KotlinSimpleTypeSpec].
 */
class KotlinSimpleTypeSpecConstructorTests {
    @Test
    fun testClassWithPrimaryConstructor() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val intType = ClassName("kotlin", "Int").kotlinRef()

        val param1 = KotlinValueParameterSpec.builder("name", stringType).build()
        val param2 = KotlinValueParameterSpec.builder("age", intType).defaultValue("0").build()

        val constructor = KotlinConstructorSpec.builder()
            .addParameter(param1)
            .addParameter(param2)
            .build()

        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Person")
            .primaryConstructor(constructor)
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals("class Person(name: String, age: Int = 0) {\n}", code)
    }

    @Test
    fun testClassWithPrimaryConstructorAndModifiers() {
        val stringType = ClassName("kotlin", "String").kotlinRef()

        val constructor = KotlinConstructorSpec.builder()
            .addParameter(KotlinValueParameterSpec.builder("name", stringType).build())
            .addModifier(KotlinModifier.PRIVATE)
            .build()

        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Person")
            .primaryConstructor(constructor)
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals("class Person private constructor(name: String) {\n}", code)
    }

    @Test
    fun testClassWithSecondaryConstructor() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val intType = ClassName("kotlin", "Int").kotlinRef()

        val primaryConstructor = KotlinConstructorSpec.builder()
            .addParameter(KotlinValueParameterSpec.builder("name", stringType).build())
            .build()

        val secondaryConstructor = KotlinConstructorSpec.builder()
            .addParameter(KotlinValueParameterSpec.builder("name", stringType).build())
            .addParameter(KotlinValueParameterSpec.builder("age", intType).build())
            .thisConstructorDelegation {
                addArgument("name")
            }
            .addCode("println(\"Secondary constructor called with age: \$age\")")
            .build()

        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Person")
            .primaryConstructor(primaryConstructor)
            .addSecondaryConstructor(secondaryConstructor)
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals(
            """
            class Person(name: String) {
                constructor(name: String, age: Int) : this(name) {
                    println("Secondary constructor called with age: ${'$'}age")
                }
            }
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testClassWithMultipleSecondaryConstructors() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val intType = ClassName("kotlin", "Int").kotlinRef()
        val booleanType = ClassName("kotlin", "Boolean").kotlinRef()

        val primaryConstructor = KotlinConstructorSpec.builder()
            .addParameter(KotlinValueParameterSpec.builder("name", stringType).build())
            .build()

        val secondaryConstructor1 = KotlinConstructorSpec.builder()
            .addParameter(KotlinValueParameterSpec.builder("name", stringType).build())
            .addParameter(KotlinValueParameterSpec.builder("age", intType).build())
            .thisConstructorDelegation {
                addArgument("name")
            }
            .build()

        val secondaryConstructor2 = KotlinConstructorSpec.builder()
            .addParameter(KotlinValueParameterSpec.builder("name", stringType).build())
            .addParameter(KotlinValueParameterSpec.builder("isActive", booleanType).build())
            .thisConstructorDelegation {
                addArgument("name")
            }
            .build()

        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Person")
            .primaryConstructor(primaryConstructor)
            .addSecondaryConstructors(secondaryConstructor1, secondaryConstructor2)
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals(
            """
            class Person(name: String) {
                constructor(name: String, age: Int) : this(name)
                constructor(name: String, isActive: Boolean) : this(name)
            }
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testClassWithSuperConstructorDelegation() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parentClass = ClassName("test", "Parent")

        val constructor = KotlinConstructorSpec.builder()
            .addParameter(KotlinValueParameterSpec.builder("name", stringType).build())
            .superConstructorDelegation {
                addArgument("name")
            }
            .build()

        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Child")
            .superclass(parentClass)
            .primaryConstructor(constructor)
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals("class Child(name: String) : test.Parent(name) {\n}", code)
    }

    @Test
    fun testClassWithConstructorAndProperties() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val intType = ClassName("kotlin", "Int").kotlinRef()

        val constructor = KotlinConstructorSpec.builder()
            .addParameter(KotlinValueParameterSpec.builder("name", stringType).build())
            .addParameter(KotlinValueParameterSpec.builder("age", intType).build())
            .build()

        val nameProperty = KotlinPropertySpec.builder("name", stringType)
            .initializer("name")
            .build()

        val ageProperty = KotlinPropertySpec.builder("age", intType)
            .initializer("age")
            .build()

        val typeSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Person")
            .primaryConstructor(constructor)
            .addProperty(nameProperty)
            .addProperty(ageProperty)
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals(
            """
            class Person(name: String, age: Int) {
                val name: String = name
                val age: Int = age
            }
            """.trimIndent(),
            code
        )
    }
}
