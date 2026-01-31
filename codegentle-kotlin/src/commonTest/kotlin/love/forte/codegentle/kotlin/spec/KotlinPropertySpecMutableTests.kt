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
package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for [KotlinPropertySpec] mutable property functionality.
 */
class KotlinPropertySpecMutableTests {

    private val stringType = ClassName("kotlin", "String").ref()
    private val intType = ClassName("kotlin", "Int").ref()

    @Test
    fun testImmutablePropertyDefault() {
        val propertySpec = KotlinPropertySpec.builder("name", stringType)
            .build()

        assertFalse(propertySpec.mutable, "Property should be immutable by default")

        val code = propertySpec.writeToKotlinString()
        assertEquals("val name: String", code)
    }

    @Test
    fun testMutableProperty() {
        val propertySpec = KotlinPropertySpec.builder("name", stringType)
            .mutable(true)
            .build()

        assertTrue(propertySpec.mutable, "Property should be mutable")

        val code = propertySpec.writeToKotlinString()
        assertEquals("var name: String", code)
    }

    @Test
    fun testImmutablePropertyExplicit() {
        val propertySpec = KotlinPropertySpec.builder("name", stringType)
            .mutable(false)
            .build()

        assertFalse(propertySpec.mutable, "Property should be immutable")

        val code = propertySpec.writeToKotlinString()
        assertEquals("val name: String", code)
    }

    @Test
    fun testMutablePropertyWithInitializer() {
        val propertySpec = KotlinPropertySpec.builder("counter", intType)
            .mutable(true)
            .initializer("0")
            .build()

        assertTrue(propertySpec.mutable, "Property should be mutable")

        val code = propertySpec.writeToKotlinString()
        assertEquals("var counter: Int = 0", code)
    }

    @Test
    fun testMutablePropertyWithDelegate() {
        val propertySpec = KotlinPropertySpec.builder("name", stringType)
            .mutable(true)
            .delegate("lazy { \"default\" }")
            .build()

        assertTrue(propertySpec.mutable, "Property should be mutable")

        val code = propertySpec.writeToKotlinString()
        assertEquals("var name: String by lazy { \"default\" }", code)
    }

    @Test
    fun testConvenienceExtensionFunctions() {
        val mutableProperty = KotlinPropertySpec.builder("mutableProp", stringType)
            .mutable()
            .build()

        assertTrue(mutableProperty.mutable, "Property should be mutable using convenience function")

        val immutableProperty = KotlinPropertySpec.builder("immutableProp", stringType)
            .immutable()
            .build()

        assertFalse(immutableProperty.mutable, "Property should be immutable using convenience function")

        val mutableCode = mutableProperty.writeToKotlinString()
        val immutableCode = immutableProperty.writeToKotlinString()

        assertEquals("var mutableProp: String", mutableCode)
        assertEquals("val immutableProp: String", immutableCode)
    }

    @Test
    fun testMutablePropertyInClass() {
        val classSpec = KotlinTypeSpec.classBuilder("Person")
            .addProperty(
                KotlinPropertySpec.builder("name", stringType)
                    .mutable(true)
                    .initializer("\"Unknown\"")
                    .build()
            )
            .addProperty(
                KotlinPropertySpec.builder("age", intType)
                    .mutable(false)
                    .initializer("0")
                    .build()
            )
            .build()

        val code = classSpec.writeToKotlinString()
        assertEquals(
            """
                class Person {
                    var name: String = "Unknown"
                
                    val age: Int = 0
                }""".trimIndent(), code
        )
    }
}
