package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.*

/**
 * Comprehensive tests for [KotlinValueParameterSpec].
 */
class KotlinValueParameterSpecTests {

    private val stringType = ClassName("kotlin", "String").kotlinRef()
    private val intType = ClassName("kotlin", "Int").kotlinRef()

    @Test
    fun testBasicParameterCreation() {
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .build()

        assertEquals("name", param.name)
        assertEquals(stringType, param.typeRef)
        assertEquals(emptyList(), param.annotations)
        assertEquals(emptySet<KotlinModifier>(), param.modifiers)
        assertTrue(param.kDoc.writeToKotlinString().isEmpty())
        assertNull(param.defaultValue)
        assertNull(param.propertyization)
    }

    @Test
    fun testParameterWithDefaultValue() {
        val param = KotlinValueParameterSpec.builder("count", intType)
            .defaultValue("0")
            .build()

        assertEquals("count", param.name)
        assertEquals(intType, param.typeRef)
        assertNotNull(param.defaultValue)
        assertEquals("0", param.defaultValue!!.writeToKotlinString())
    }

    @Test
    fun testParameterWithDefaultValueCodeValue() {
        val defaultValue = CodeValue("42")
        val param = KotlinValueParameterSpec.builder("number", intType)
            .defaultValue(defaultValue)
            .build()

        assertEquals("number", param.name)
        assertNotNull(param.defaultValue)
        assertEquals("42", param.defaultValue!!.writeToKotlinString())
    }

    @Test
    fun testParameterWithKDoc() {
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .addKDoc("The name parameter")
            .build()

        assertEquals("The name parameter", param.kDoc.writeToKotlinString())
    }

    // Propertyization Tests

    @Test
    fun testPropertyizationBuilder() {
        val propertyization = KotlinValueParameterSpec.propertyizationBuilder()
            .mutable(true)
            .build()

        assertTrue(propertyization.mutable)
    }

    @Test
    fun testPropertyizationBuilderDefault() {
        val propertyization = KotlinValueParameterSpec.propertyizationBuilder()
            .build()

        assertFalse(propertyization.mutable)
    }

    @Test
    fun testPropertyizationDSL() {
        val propertyization = propertyization {
            mutable = true
        }

        assertTrue(propertyization.mutable)
    }

    @Test
    fun testParameterWithMutablePropertyization() {
        val propertyization = propertyization { mutable = true }
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .propertyize(propertyization)
            .build()

        assertNotNull(param.propertyization)
        assertTrue(param.propertyization!!.mutable)
    }

    @Test
    fun testParameterWithImmutablePropertyization() {
        val propertyization = propertyization { mutable = false }
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .propertyize(propertyization)
            .build()

        assertNotNull(param.propertyization)
        assertFalse(param.propertyization!!.mutable)
    }

    @Test
    fun testParameterPropertyizeDSL() {
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .propertyize { mutable = true }
            .build()

        assertNotNull(param.propertyization)
        assertTrue(param.propertyization!!.mutable)
    }

    @Test
    fun testParameterMutableProperty() {
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .mutableProperty()
            .build()

        assertNotNull(param.propertyization)
        assertTrue(param.propertyization!!.mutable)
    }

    @Test
    fun testParameterImmutableProperty() {
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .immutableProperty()
            .build()

        assertNotNull(param.propertyization)
        assertFalse(param.propertyization!!.mutable)
    }

    @Test
    fun testParameterVarProperty() {
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .varProperty()
            .build()

        assertNotNull(param.propertyization)
        assertTrue(param.propertyization!!.mutable)
    }

    @Test
    fun testParameterValProperty() {
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .valProperty()
            .build()

        assertNotNull(param.propertyization)
        assertFalse(param.propertyization!!.mutable)
    }

    @Test
    fun testParameterToString() {
        val param = KotlinValueParameterSpec.builder("testParam", stringType)
            .build()

        val toString = param.toString()
        assertTrue(toString.contains("testParam"))
        assertTrue(toString.contains("String"))
    }

    @Test
    fun testPropertyizationBuilderChaining() {
        val propertyization = KotlinValueParameterSpec.propertyizationBuilder()
            .mutable(false)
            .mutable(true)
            .build()

        assertTrue(propertyization.mutable)
    }
}
