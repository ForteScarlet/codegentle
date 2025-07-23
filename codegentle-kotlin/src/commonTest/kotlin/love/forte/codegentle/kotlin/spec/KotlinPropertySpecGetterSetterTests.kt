package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for [KotlinPropertySpec] with custom getters and setters.
 */
class KotlinPropertySpecGetterSetterTests {

    private val stringType = ClassName("kotlin", "String").kotlinRef()
    private val intType = ClassName("kotlin", "Int").kotlinRef()

    @Test
    fun testPropertyWithCustomGetter() {
        val propertySpec = KotlinPropertySpec.builder("name", stringType).getter {
            addCode("return field.uppercase()")
        }.build()

        assertNotNull(propertySpec.getter)
        assertNull(propertySpec.setter)

        val code = propertySpec.writeToKotlinString()
        assertEquals(
            """
                val name: String
                    get() = field.uppercase()
                """.trimIndent(), code
        )
    }

    @Test
    fun testPropertyWithCustomSetter() {
        val propertySpec = KotlinPropertySpec.builder("age", intType).setter("value") {
            addModifier(KotlinModifier.PRIVATE)
            addCode("field = if (value > 0) value else 0")
        }.build()

        assertNull(propertySpec.getter)
        assertNotNull(propertySpec.setter)

        val code = propertySpec.writeToKotlinString()
        assertEquals(
            """
                val age: Int
                    private set(value) {
                        field = if (value > 0) value else 0
                    }
                """.trimIndent(), code
        )
    }

    @Test
    fun testPropertyWithBothGetterAndSetter() {
        val propertySpec = KotlinPropertySpec.builder("counter", intType)
            .initializer("0")
            .mutable()
            .getter {
                addCode("return field")
            }
            .setter("value") {
                addCode("field = value")
            }.build()

        assertNotNull(propertySpec.getter)
        assertNotNull(propertySpec.setter)

        val code = propertySpec.writeToKotlinString()
        assertEquals(
            """
                var counter: Int = 0
                    get() = field
                    set(value) {
                        field = value
                    }
                """.trimIndent(),
            code
        )
    }

    @Test
    fun testPropertyWithGetterWithModifiers() {
        val propertySpec = KotlinPropertySpec.builder("name", stringType).getter {
            addModifier(KotlinModifier.INLINE)
            addCode("return field.uppercase()")
        }.build()

        assertNotNull(propertySpec.getter)
        assertEquals(setOf(KotlinModifier.INLINE), propertySpec.getter?.modifiers)

        val code = propertySpec.writeToKotlinString()
        assertEquals(
            """
                val name: String
                    inline get() = field.uppercase()
                """.trimIndent(), code
        )
    }

    @Test
    fun testPropertyWithSetterWithModifiers() {
        val propertySpec = KotlinPropertySpec.builder("age", intType).setter("value") {
            addModifier(KotlinModifier.PRIVATE)
            addModifier(KotlinModifier.INLINE)
            addCode("field = if (value > 0) value else 0")
        }.build()

        assertNotNull(propertySpec.setter)
        assertEquals(setOf(KotlinModifier.PRIVATE, KotlinModifier.INLINE), propertySpec.setter?.modifiers)

        val code = propertySpec.writeToKotlinString()
        assertEquals(
            """
                val age: Int
                    private inline set(value) {
                        field = if (value > 0) value else 0
                    }
                """.trimIndent(),
            code
        )
    }

    @Test
    fun testPropertyWithEmptyGetterAndSetter() {
        val propertySpec = KotlinPropertySpec.builder("name", stringType).getter { }.setter("value") { }.build()

        assertNotNull(propertySpec.getter)
        assertNotNull(propertySpec.setter)

        val code = propertySpec.writeToKotlinString()
        assertEquals(
            """
                val name: String
                    get
                    set
                """.trimIndent(), code
        )
    }

    @Test
    fun testPropertyWithGetterAndSetterInClass() {
        val classSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Person")
            .addProperty(KotlinPropertySpec.builder("name", stringType).getter {
                addCode("return field.uppercase()")
            }.setter("value") {
                addModifier(KotlinModifier.PRIVATE)
                addCode("field = value.trim()")
            }.build()).build()

        val code = classSpec.writeToKotlinString()
        assertEquals(
            """
                class Person {
                    val name: String
                        get() = field.uppercase()
                        private set(value) {
                            field = value.trim()
                        }
                }""".trimIndent(),
            code
        )
    }
}
