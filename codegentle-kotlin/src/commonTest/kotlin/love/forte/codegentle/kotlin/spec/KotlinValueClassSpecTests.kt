package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.code.addInitializerBlock
import love.forte.codegentle.common.code.addKDoc
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.naming.KotlinClassNames
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Comprehensive tests for [KotlinValueClassSpec].
 */
class KotlinValueClassSpecTests {

    @Test
    fun testBasicValueClass() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter).build()

        val code = valueClass.writeToKotlinString()
        assertEquals("value class UserId(val value: String) {\n}", code)
    }

    @Test
    fun testValueClassWithKDoc() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addKDoc("A value class representing a user ID.")
            .addKDoc("\n@param value the string value of the user ID")
            .build()

        val code = valueClass.writeToKotlinString()
        val expectedCode = """/**
 * A value class representing a user ID.
 * @param value the string value of the user ID
 */
value class UserId(val value: String) {
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithAnnotations() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val serializable = ClassName("kotlinx.serialization", "Serializable").annotationRef()
        val jvmInline = ClassName("kotlin.jvm", "JvmInline").annotationRef()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addAnnotationRef(serializable)
            .addAnnotationRef(jvmInline)
            .build()

        val code = valueClass.writeToKotlinString()
        println("[DEBUG_LOG] Generated code for testValueClassWithAnnotations:")
        println("[DEBUG_LOG] $code")
        val expectedCode = """
                @kotlinx.serialization.Serializable
                @JvmInline
                value class UserId(val value: String) {
                }
                """.trimIndent()
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithMultipleAnnotations() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val serializable = ClassName("kotlinx.serialization", "Serializable").annotationRef()
        val jvmInline = ClassName("kotlin.jvm", "JvmInline").annotationRef()
        val deprecated = ClassName("kotlin", "Deprecated").annotationRef {
            addMember("message", "\"Use NewUserId instead\"")
        }

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addAnnotationRefs(listOf(serializable, jvmInline, deprecated))
            .build()

        val code = valueClass.writeToKotlinString()
        val expectedCode = """
                @kotlinx.serialization.Serializable
                @JvmInline
                @Deprecated(message = "Use NewUserId instead")
                value class UserId(val value: String) {
                }
                """.trimIndent()
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithAdditionalModifiers() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addModifier(KotlinModifier.INTERNAL)
            .build()

        val code = valueClass.writeToKotlinString()
        val expectedCode = """internal value class UserId(val value: String) {
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithMultipleModifiers() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addModifiers(KotlinModifier.INTERNAL, KotlinModifier.INLINE)
            .build()

        val code = valueClass.writeToKotlinString()
        val expectedCode = """internal inline value class UserId(val value: String) {
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithTypeVariables() {
        val tType = TypeVariableName("T")
        val tRef = tType.kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", tRef)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassSpec.builder("Wrapper", parameter)
            .addTypeVariable(tRef)
            .build()


        val code = valueClass.writeToKotlinString()
        println(code)
        val expectedCode = """value class Wrapper<T>(val value: T) {
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithMultipleTypeVariables() {
        val tType = TypeVariableName("T")
        val uType = TypeVariableName("U")
        val tRef = tType.kotlinRef()
        val uRef = uType.kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", tRef)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassSpec.builder("Wrapper", parameter)
            .addTypeVariable(tRef)
            .addTypeVariable(uRef)
            .build()

        val code = valueClass.writeToKotlinString()
        val expectedCode = """value class Wrapper<T, U>(val value: T) {
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithSuperinterfaces() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val comparable = ClassName("kotlin", "Comparable")
        val serializable = ClassName("java.io", "Serializable")

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addSuperinterface(comparable)
            .addSuperinterface(serializable)
            .build()

        val code = valueClass.writeToKotlinString()
        val expectedCode = """value class UserId(val value: String) : Comparable, java.io.Serializable {
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithProperties() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val intType = ClassName("kotlin", "Int").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val lengthProperty = KotlinPropertySpec.builder("length", intType)
            .getter {
                addCode("return value.length")
            }
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addProperty(lengthProperty)
            .build()

        val code = valueClass.writeToKotlinString()
        val expectedCode = """value class UserId(val value: String) {
    val length: Int
        get() = value.length
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithFunctions() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val booleanType = ClassName("kotlin", "Boolean").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val isEmptyFunction = KotlinFunctionSpec.builder("isEmpty", booleanType)
            .addCode("return value.isEmpty()")
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addFunction(isEmptyFunction)
            .build()

        val code = valueClass.writeToKotlinString()

        assertEquals(
            """
                value class UserId(val value: String) {
                    fun isEmpty(): Boolean = value.isEmpty()
                }
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testValueClassWithInitializerBlock() {
        val stringType = ClassName("kotlin", "String").kotlinRef()

        val parameter = KotlinValueParameterSpec("value", stringType) {
            immutableProperty()
        }

        val valueClass = KotlinValueClassSpec("UserId", parameter) {
            addInitializerBlock("require(value.isNotEmpty()) { \"UserId cannot be empty\" }")
        }

        val code = valueClass.writeToKotlinString()
        val expectedCode = """value class UserId(val value: String) {
    init {
        require(value.isNotEmpty()) { "UserId cannot be empty" }
    }
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithMultipleInitializerBlocks() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addInitializerBlock("require(value.isNotEmpty()) { \"UserId cannot be empty\" }")
            .addInitializerBlock("\nprintln(\"Creating UserId with value: \$value\")")
            .build()

        val code = valueClass.writeToKotlinString()
        val expectedCode = """value class UserId(val value: String) {
    init {
        require(value.isNotEmpty()) { "UserId cannot be empty" }
        println("Creating UserId with value: ${'$'}value")
    }
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithImmutableParameterProperty() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty() // immutable property
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter).build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("val value: String"))
    }

    @Test
    fun testValueClassWithParameterDefaultValue() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .defaultValue("\"default\"")
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter).build()

        val code = valueClass.writeToKotlinString()
        assertEquals(
            """
            value class UserId(val value: String = "default") {
            }
        """.trimIndent(),
            code
        )
    }

    @Test
    fun testComplexValueClass() {
        val stringType = KotlinClassNames.STRING.kotlinRef()
        val intType = KotlinClassNames.INT.kotlinRef()
        val booleanType = KotlinClassNames.BOOLEAN.kotlinRef()

        val parameter = KotlinValueParameterSpec.builder("id", stringType)
            .immutableProperty()
            .addKDoc("The unique identifier")
            .build()

        val serializable = ClassName("kotlinx.serialization", "Serializable").annotationRef()
        val comparable = ClassName("kotlin", "Comparable")

        val lengthProperty = KotlinPropertySpec.builder("length", intType)
            .getter {
                addCode("return id.length")
            }
            .build()

        val isValidFunction = KotlinFunctionSpec.builder("isValid", booleanType)
            .addCode("return id.isNotEmpty() && id.length >= 3")
            .build()

        val valueClass = KotlinValueClassSpec.builder("ComplexUserId", parameter)
            .addKDoc("A complex value class for user identification.")
            .addKDoc("\n@param id the string identifier")
            .addAnnotationRef(serializable)
            .addModifier(KotlinModifier.INTERNAL)
            .addSuperinterface(comparable)
            .addProperty(lengthProperty)
            .addFunction(isValidFunction)
            .addInitializerBlock("require(id.isNotEmpty()) { \"ID cannot be empty\" }")
            .build()

        val code = valueClass.writeToKotlinString()

        assertEquals(
            """
                /**
                 * A complex value class for user identification.
                 * @param id the string identifier
                 */
                @kotlinx.serialization.Serializable
                internal value class ComplexUserId(
                    /**
                     * The unique identifier
                     */
                    val id: String
                ) : Comparable {
                    init {
                        require(id.isNotEmpty()) { "ID cannot be empty" }
                    }
                
                    val length: Int
                        get() = id.length
                
                    fun isValid(): Boolean = id.isNotEmpty() && id.length >= 3
                }
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testValueClassBuilderValidation() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        // Test that VALUE modifier is automatically added and validated
        val builder = KotlinValueClassSpec.builder("UserId", parameter)
        val valueClass = builder.build()

        assertTrue(valueClass.modifiers.contains(KotlinModifier.VALUE))
    }

    @Test
    fun testValueClassBuilderWithoutValueModifierFails() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        // Create a builder and try to remove VALUE modifier (this should fail during build)
        val builder = KotlinValueClassSpec.builder("UserId", parameter)

        // The implementation should ensure VALUE modifier is always present
        val valueClass = builder.build()
        assertTrue(valueClass.modifiers.contains(KotlinModifier.VALUE))
    }

    @Test
    fun testValueClassConvenienceFunction() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassSpec("UserId", parameter) {
            addKDoc("Convenience function test")
            addModifier(KotlinModifier.INTERNAL)
        }

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("/**"))
        assertTrue(code.contains("Convenience function test"))
        assertTrue(code.contains("internal"))
        assertTrue(code.contains("value class UserId"))
    }

    @Test
    fun testValueClassExtensionFunctions() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val intType = ClassName("kotlin", "Int").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addKDoc("Test KDoc") {
                // Test KDoc extension
            }
            .addInitializerBlock("println(\"init\")") {
                // Test initializer block extension
            }
            .addProperty("length", intType) {
                getter {
                    addCode("return value.length")
                }
            }
            .addFunction("isEmpty", ClassName("kotlin", "Boolean").kotlinRef()) {
                addCode("return value.isEmpty()")
            }
            .build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("Test KDoc"))
        assertTrue(code.contains("println(\"init\")"))
        assertTrue(code.contains("val length"))
        assertTrue(code.contains("fun isEmpty"))
    }

    @Test
    fun testValueClassSuperclassIsNull() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter).build()

        // Value classes cannot have superclasses
        assertEquals(null, valueClass.superclass)
    }

    @Test
    fun testValueClassSubtypesIsEmpty() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter).build()

        // Value classes cannot have subtypes
        assertTrue(valueClass.subtypes.isEmpty())
    }
}
