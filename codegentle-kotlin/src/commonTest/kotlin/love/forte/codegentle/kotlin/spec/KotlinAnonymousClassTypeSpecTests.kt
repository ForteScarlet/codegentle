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
package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [KotlinAnonymousClassTypeSpec].
 */
class KotlinAnonymousClassTypeSpecTests {
    @Test
    fun testBasicAnonymousClass() {
        val interfaceType = ClassName("test", "MyInterface")

        val typeSpec = KotlinAnonymousClassTypeSpec {
            addSuperinterface(interfaceType)
        }

        val code = typeSpec.writeToKotlinString()
        assertEquals("object : test.MyInterface {\n}", code)
    }

    @Test
    fun testAnonymousClassWithSuperclass() {
        val superclassType = ClassName("test", "MySuperClass")

        val typeSpec = KotlinAnonymousClassTypeSpec {
            superclass(superclassType)
        }

        val code = typeSpec.writeToKotlinString()
        assertEquals("object : test.MySuperClass() {\n}", code)
    }

    @Test
    fun testAnonymousClassWithSuperclassAndInterfaces() {
        val superclassType = ClassName("test", "MySuperClass")
        val interface1Type = ClassName("test", "Interface1")
        val interface2Type = ClassName("test", "Interface2")

        val typeSpec = KotlinAnonymousClassTypeSpec {
            superclass(superclassType)
            addSuperinterface(interface1Type)
            addSuperinterface(interface2Type)
        }

        val code = typeSpec.writeToKotlinString()
        assertEquals("object : test.MySuperClass(), test.Interface1, test.Interface2 {\n}", code)
    }

    @Test
    fun testAnonymousClassWithProperties() {
        val interfaceType = ClassName("test", "MyInterface")
        val stringType = ClassName("kotlin", "String").ref()
        val intType = ClassName("kotlin", "Int").ref()

        val typeSpec = KotlinAnonymousClassTypeSpec {
            addSuperinterface(interfaceType)
            addProperty(
                KotlinPropertySpec("name", stringType) {
                    initializer("\"John\"")
                }
            )
            addProperty(
                KotlinPropertySpec("age", intType) {
                    initializer("30")
                }
            )
        }

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
        val stringType = ClassName("kotlin", "String").ref()
        val unitType = ClassName("kotlin", "Unit").ref()

        val typeSpec = KotlinAnonymousClassTypeSpec {
            addSuperinterface(interfaceType)
            addFunction(
                KotlinFunctionSpec("sayHello", unitType) {
                    addModifier(KotlinModifier.OVERRIDE)
                    addCode("println(\"Hello, World!\")")
                }
            )
            addFunction(
                KotlinFunctionSpec("getName", stringType) {
                    addModifier(KotlinModifier.OVERRIDE)
                    addCode("return \"John\"")
                }
            )
        }

        val code = typeSpec.writeToKotlinString()
        assertEquals(
            """
            object : test.MyInterface {
                override fun sayHello() {
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

        val typeSpec = KotlinAnonymousClassTypeSpec {
            addSuperinterface(interfaceType)
            addInitializerBlock("println(\"Initializing anonymous class\")")
        }

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

        val typeSpec = KotlinAnonymousClassTypeSpec {
            superclass(superclassType)
            addSuperConstructorArgument("\"John\"")
            addSuperConstructorArgument("30")
        }

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
        val stringType = ClassName("kotlin", "String").ref()
        val intType = ClassName("kotlin", "Int").ref()
        val unitType = ClassName("kotlin", "Unit").ref()

        val typeSpec = KotlinAnonymousClassTypeSpec {
            superclass(superclassType)
            addSuperinterface(interface1Type)
            addProperty(
                KotlinPropertySpec("name", stringType) {
                    initializer("\"John\"")
                }
            )
            addProperty(
                KotlinPropertySpec("age", intType) {
                    initializer("30")
                }
            )
            addInitializerBlock("println(\"Initializing anonymous class\")")
            addFunction(
                KotlinFunctionSpec("sayHello", unitType) {
                    addModifier(KotlinModifier.OVERRIDE)
                    addCode("println(\"Hello, \$name!\")")
                }
            )
        }

        val code = typeSpec.writeToKotlinString()

        assertEquals(
            $$"""
            object : test.MySuperClass(), test.Interface1 {
                init {
                    println("Initializing anonymous class")
                }

                val name: String = "John"

                val age: Int = 30

                override fun sayHello() {
                    println("Hello, $name!")
                }
            }
        """.trimIndent(), code
        )
    }
}
