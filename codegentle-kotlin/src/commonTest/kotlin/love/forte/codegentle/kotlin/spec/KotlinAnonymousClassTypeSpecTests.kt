package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [KotlinAnonymousClassTypeSpec].
 */
class KotlinAnonymousClassTypeSpecTests {
    // TODO 修正不对的测试

    @Test
    fun testBasicAnonymousClass() {
        val interfaceType = ClassName("test", "MyInterface")

        val typeSpec = KotlinAnonymousClassTypeSpec.builder()
            .addSuperinterface(interfaceType)
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals("object : test.MyInterface {\n}", code)
    }

    @Test
    fun testAnonymousClassWithSuperclass() {
        val superclassType = ClassName("test", "MySuperClass")

        val typeSpec = KotlinAnonymousClassTypeSpec.builder()
            .superclass(superclassType)
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals("object : test.MySuperClass() {\n}", code)
    }

    @Test
    fun testAnonymousClassWithSuperclassAndInterfaces() {
        val superclassType = ClassName("test", "MySuperClass")
        val interface1Type = ClassName("test", "Interface1")
        val interface2Type = ClassName("test", "Interface2")

        val typeSpec = KotlinAnonymousClassTypeSpec.builder()
            .superclass(superclassType)
            .addSuperinterface(interface1Type)
            .addSuperinterface(interface2Type)
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals("object : test.MySuperClass(), test.Interface1, test.Interface2 {\n}", code)
    }

    @Test
    fun testAnonymousClassWithProperties() {
        val interfaceType = ClassName("test", "MyInterface")
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val intType = ClassName("kotlin", "Int").kotlinRef()

        val typeSpec = KotlinAnonymousClassTypeSpec.builder()
            .addSuperinterface(interfaceType)
            .addProperty(
                KotlinPropertySpec.builder("name", stringType)
                    .initializer("\"John\"")
                    .build()
            )
            .addProperty(
                KotlinPropertySpec.builder("age", intType)
                    .initializer("30")
                    .build()
            )
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals(
            """
            object : test.MyInterface {
                val name: String = "John"

                val age: Int = 30
            }
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testAnonymousClassWithFunctions() {
        val interfaceType = ClassName("test", "MyInterface")
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val unitType = ClassName("kotlin", "Unit").kotlinRef()

        val typeSpec = KotlinAnonymousClassTypeSpec.builder()
            .addSuperinterface(interfaceType)
            .addFunction(
                KotlinFunctionSpec.builder("sayHello", unitType)
                    .addModifier(KotlinModifier.OVERRIDE)
                    .addCode("println(\"Hello, World!\")")
                    .build()
            )
            .addFunction(
                KotlinFunctionSpec.builder("getName", stringType)
                    .addModifier(KotlinModifier.OVERRIDE)
                    .addCode("return \"John\"")
                    .build()
            )
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals(
            """
            object : test.MyInterface {
                override fun sayHello(): Unit {
                    println("Hello, World!")
                }

                override fun getName(): String = "John"
            }
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testAnonymousClassWithInitializerBlock() {
        val interfaceType = ClassName("test", "MyInterface")

        val typeSpec = KotlinAnonymousClassTypeSpec.builder()
            .addSuperinterface(interfaceType)
            .addInitializerBlock("println(\"Initializing anonymous class\")")
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals(
            """
            object : test.MyInterface {
                init {
                    println("Initializing anonymous class")
                }
            }
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testAnonymousClassWithSuperConstructorArguments() {
        val superclassType = ClassName("test", "MySuperClass")

        val typeSpec = KotlinAnonymousClassTypeSpec.builder()
            .superclass(superclassType)
            .addSuperConstructorArgument("\"John\"")
            .addSuperConstructorArgument("30")
            .build()

        val code = typeSpec.writeToKotlinString()
        assertEquals(
            """
            object : test.MySuperClass("John", 30) {
            }
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testComplexAnonymousClass() {
        val superclassType = ClassName("test", "MySuperClass")
        val interface1Type = ClassName("test", "Interface1")
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val intType = ClassName("kotlin", "Int").kotlinRef()
        val unitType = ClassName("kotlin", "Unit").kotlinRef()

        val typeSpec = KotlinAnonymousClassTypeSpec.builder()
            .superclass(superclassType)
            .addSuperinterface(interface1Type)
            .addProperty(
                KotlinPropertySpec.builder("name", stringType)
                    .initializer("\"John\"")
                    .build()
            )
            .addProperty(
                KotlinPropertySpec.builder("age", intType)
                    .initializer("30")
                    .build()
            )
            .addInitializerBlock("println(\"Initializing anonymous class\")")
            .addFunction(
                KotlinFunctionSpec.builder("sayHello", unitType)
                    .addModifier(KotlinModifier.OVERRIDE)
                    .addCode("println(\"Hello, \$name!\")")
                    .build()
            )
            .build()

        val code = typeSpec.writeToKotlinString()

        assertEquals(
            $$"""
            object : test.MySuperClass(), test.Interface1 {
                init {
                    println("Initializing anonymous class")
                }

                val name: String = "John"

                val age: Int = 30

                override fun sayHello(): Unit {
                    println("Hello, $name!")
                }
            }
        """.trimIndent(), code)
    }
}
