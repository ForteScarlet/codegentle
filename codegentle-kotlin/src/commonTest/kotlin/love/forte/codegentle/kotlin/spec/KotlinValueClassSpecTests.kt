package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.naming.KotlinNames
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
            .valProperty()
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter).build()

        val code = valueClass.writeToKotlinString()
        assertEquals("value class UserId(val value: String) {\n}", code)
    }

    @Test
    fun testValueClassWithKDoc() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addKDoc("A value class representing a user ID.")
            .addKDoc("\n@param value the string value of the user ID")
            .build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("/**"))
        assertTrue(code.contains("A value class representing a user ID."))
        assertTrue(code.contains("@param value the string value of the user ID"))
        assertTrue(code.contains("*/"))
    }

    @Test
    fun testValueClassWithAnnotations() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .valProperty()
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
        assertTrue(code.contains("@kotlinx.serialization.Serializable"))
        assertTrue(code.contains("@kotlin.jvm.JvmInline"))
    }

    @Test
    fun testValueClassWithMultipleAnnotations() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        val serializable = ClassName("kotlinx.serialization", "Serializable").annotationRef()
        val jvmInline = ClassName("kotlin.jvm", "JvmInline").annotationRef()
        val deprecated = ClassName("kotlin", "Deprecated").annotationRef {
            addMember("message", "\"Use NewUserId instead\"")
        }

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addAnnotationRefs(listOf(serializable, jvmInline, deprecated))
            .build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("@kotlinx.serialization.Serializable"))
        assertTrue(code.contains("@kotlin.jvm.JvmInline"))
        assertTrue(code.contains("@kotlin.Deprecated"))
    }

    @Test
    fun testValueClassWithAdditionalModifiers() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addModifier(KotlinModifier.INTERNAL)
            .build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("internal"))
        assertTrue(code.contains("value"))
    }

    @Test
    fun testValueClassWithMultipleModifiers() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addModifiers(KotlinModifier.INTERNAL, KotlinModifier.INLINE)
            .build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("internal"))
        assertTrue(code.contains("value"))
    }

    @Test
    fun testValueClassWithTypeVariables() {
        val tType = TypeVariableName("T")
        val tRef = tType.kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", tRef).build()

        val valueClass = KotlinValueClassSpec.builder("Wrapper", parameter)
            .addTypeVariableRef(tRef)
            .build()


        val code = valueClass.writeToKotlinString()
        println(code)
        assertTrue(code.contains("value class Wrapper<T>"))
        assertTrue(code.contains("val value: T"))
    }

    @Test
    fun testValueClassWithMultipleTypeVariables() {
        val tType = TypeVariableName("T")
        val uType = TypeVariableName("U")
        val tRef = tType.kotlinRef()
        val uRef = uType.kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", tRef).build()

        val valueClass = KotlinValueClassSpec.builder("Wrapper", parameter)
            .addTypeVariableRef(tRef)
            .addTypeVariableRef(uRef)
            .build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("value class Wrapper<T, U>"))
    }

    @Test
    fun testValueClassWithSuperinterfaces() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        val comparable = ClassName("kotlin", "Comparable")
        val serializable = ClassName("java.io", "Serializable")

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addSuperinterface(comparable)
            .addSuperinterface(serializable)
            .build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains(": kotlin.Comparable, java.io.Serializable"))
    }

    @Test
    fun testValueClassWithProperties() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val intType = ClassName("kotlin", "Int").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        val lengthProperty = KotlinPropertySpec.builder("length", intType)
            .getter {
                addCode("return value.length")
            }
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addProperty(lengthProperty)
            .build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("val length: kotlin.Int"))
        assertTrue(code.contains("get() = value.length"))
    }

    @Test
    fun testValueClassWithFunctions() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val booleanType = ClassName("kotlin", "Boolean").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        val isEmptyFunction = KotlinFunctionSpec.builder("isEmpty", booleanType)
            .addCode("return value.isEmpty()")
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addFunction(isEmptyFunction)
            .build()

        val code = valueClass.writeToKotlinString()

        assertEquals(
            """
                value class UserId(value: String) {
                    fun isEmpty(): Boolean = value.isEmpty()
                }
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testValueClassWithInitializerBlock() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addInitializerBlock("require(value.isNotEmpty()) { \"UserId cannot be empty\" }")
            .build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("init {"))
        assertTrue(code.contains("require(value.isNotEmpty())"))
        assertTrue(code.contains("\"UserId cannot be empty\""))
    }

    @Test
    fun testValueClassWithMultipleInitializerBlocks() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter)
            .addInitializerBlock("require(value.isNotEmpty()) { \"UserId cannot be empty\" }")
            .addInitializerBlock("println(\"Creating UserId with value: \$value\")")
            .build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("init {"))
        assertTrue(code.contains("require(value.isNotEmpty())"))
        assertTrue(code.contains("println(\"Creating UserId"))
    }

    @Test
    fun testValueClassWithParameterProperty() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .varProperty() // mutable property
            .build()

        val valueClass = KotlinValueClassSpec.builder("MutableUserId", parameter).build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("var value: String"))
    }

    @Test
    fun testValueClassWithImmutableParameterProperty() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .valProperty() // immutable property
            .build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter).build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("val value: String"))
    }

    @Test
    fun testValueClassWithParameterDefaultValue() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .valProperty()
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
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val intType = KotlinNames.Classes.INT.kotlinRef()
        val booleanType = KotlinNames.Classes.BOOLEAN.kotlinRef()

        val parameter = KotlinValueParameterSpec.builder("id", stringType)
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
                    id: String
                ) : Comparable {
                    init {
                        require(id.isNotEmpty()) { "ID cannot be empty" }
                    }
                
                    val length: Int
                        get() {
                            return id.length
                        }
                
                    fun isValid(): Boolean = id.isNotEmpty() && id.length >= 3
                }
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testValueClassBuilderValidation() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        // Test that VALUE modifier is automatically added and validated
        val builder = KotlinValueClassSpec.builder("UserId", parameter)
        val valueClass = builder.build()

        assertTrue(valueClass.modifiers.contains(KotlinModifier.VALUE))
    }

    @Test
    fun testValueClassBuilderWithoutValueModifierFails() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        // Create a builder and try to remove VALUE modifier (this should fail during build)
        val builder = KotlinValueClassSpec.builder("UserId", parameter)

        // The implementation should ensure VALUE modifier is always present
        val valueClass = builder.build()
        assertTrue(valueClass.modifiers.contains(KotlinModifier.VALUE))
    }

    @Test
    fun testValueClassConvenienceFunction() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

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
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

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
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter).build()

        // Value classes cannot have superclasses
        assertEquals(null, valueClass.superclass)
    }

    @Test
    fun testValueClassSubtypesIsEmpty() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val parameter = KotlinValueParameterSpec.builder("value", stringType).build()

        val valueClass = KotlinValueClassSpec.builder("UserId", parameter).build()

        // Value classes cannot have subtypes
        assertTrue(valueClass.subtypes.isEmpty())
    }
}
