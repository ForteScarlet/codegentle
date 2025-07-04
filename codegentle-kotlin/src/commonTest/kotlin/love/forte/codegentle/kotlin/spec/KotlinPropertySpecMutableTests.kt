package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for [KotlinPropertySpec] mutable property functionality.
 */
class KotlinPropertySpecMutableTests {

    private val stringType = ClassName("kotlin", "String").kotlinRef()
    private val intType = ClassName("kotlin", "Int").kotlinRef()

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
        assertEquals("class Person {\n    var name: String = \"Unknown\"\n    val age: Int = 0\n}", code)
    }
}
