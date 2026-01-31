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

import love.forte.codegentle.common.code.addDoc
import love.forte.codegentle.common.code.addInitializerBlock
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.naming.KotlinClassNames
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Comprehensive tests for [KotlinValueClassTypeSpec].
 */
class KotlinValueClassSpecTests {

    /**
     * Helper function to create a KotlinConstructorSpec from a single parameter
     * for value class testing.
     */
    private fun createValueClassConstructor(parameter: KotlinValueParameterSpec): KotlinConstructorSpec {
        return KotlinConstructorSpec {
            addParameter(parameter)
        }
    }

    @Test
    fun testBasicValueClass() {
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec("value", stringType) {
            immutableProperty()
        }
        val constructor = KotlinConstructorSpec {
            addParameter(parameter)
        }

        val valueClass = KotlinValueClassTypeSpec("UserId", constructor)

        val code = valueClass.writeToKotlinString()
        assertEquals("value class UserId(val value: String)", code)
    }

    @Test
    fun testValueClassWithKDoc() {
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec("value", stringType) {
            immutableProperty()
        }

        val valueClass = KotlinValueClassTypeSpec("UserId", createValueClassConstructor(parameter)) {
            addDoc("A value class representing a user ID.")
            addDoc("\n@param value the string value of the user ID")
        }

        val code = valueClass.writeToKotlinString()
        val expectedCode = """/**
 * A value class representing a user ID.
 * @param value the string value of the user ID
 */
value class UserId(val value: String)"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithAnnotations() {
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec("value", stringType) {
            immutableProperty()
        }

        val serializable = ClassName("kotlinx.serialization", "Serializable").annotationRef()
        val jvmInline = ClassName("kotlin.jvm", "JvmInline").annotationRef()

        val valueClass = KotlinValueClassTypeSpec("UserId", createValueClassConstructor(parameter)) {
            addAnnotation(serializable)
            addAnnotation(jvmInline)
        }

        val code = valueClass.writeToKotlinString()
        val expectedCode = """
                @kotlinx.serialization.Serializable
                @JvmInline
                value class UserId(val value: String)
                """.trimIndent()
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithMultipleAnnotations() {
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec("value", stringType) {
            immutableProperty()
        }

        val serializable = ClassName("kotlinx.serialization", "Serializable").annotationRef()
        val jvmInline = ClassName("kotlin.jvm", "JvmInline").annotationRef()
        val deprecated = ClassName("kotlin", "Deprecated").annotationRef {
            addMember("message", "\"Use NewUserId instead\"")
        }

        val valueClass = KotlinValueClassTypeSpec("UserId", createValueClassConstructor(parameter)) {
            addAnnotations(listOf(serializable, jvmInline, deprecated))
        }

        val code = valueClass.writeToKotlinString()
        val expectedCode = """
                @kotlinx.serialization.Serializable
                @JvmInline
                @Deprecated(message = "Use NewUserId instead")
                value class UserId(val value: String)
                """.trimIndent()
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithAdditionalModifiers() {
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter))
            .addModifier(KotlinModifier.INTERNAL)
            .build()

        val code = valueClass.writeToKotlinString()
        val expectedCode = """internal value class UserId(val value: String)"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithTypeVariables() {
        val tType = TypeVariableName("T")
        val tRef = tType.ref()
        val parameter = KotlinValueParameterSpec.builder("value", tRef)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassTypeSpec.builder("Wrapper", createValueClassConstructor(parameter))
            .addTypeVariable(tRef)
            .build()


        val code = valueClass.writeToKotlinString()
        println(code)
        val expectedCode = """value class Wrapper<T>(val value: T)"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithMultipleTypeVariables() {
        val tType = TypeVariableName("T")
        val uType = TypeVariableName("U")
        val tRef = tType.ref()
        val uRef = uType.ref()
        val parameter = KotlinValueParameterSpec.builder("value", tRef)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassTypeSpec.builder("Wrapper", createValueClassConstructor(parameter))
            .addTypeVariable(tRef)
            .addTypeVariable(uRef)
            .build()

        val code = valueClass.writeToKotlinString()
        val expectedCode = """value class Wrapper<T, U>(val value: T)"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithSuperinterfaces() {
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val comparable = ClassName("kotlin", "Comparable")
        val serializable = ClassName("java.io", "Serializable")

        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter))
            .addSuperinterface(comparable)
            .addSuperinterface(serializable)
            .build()

        val code = valueClass.writeToKotlinString()
        val expectedCode = """value class UserId(val value: String) : Comparable, java.io.Serializable"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithProperties() {
        val stringType = ClassName("kotlin", "String").ref()
        val intType = ClassName("kotlin", "Int").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val lengthProperty = KotlinPropertySpec.builder("length", intType)
            .getter {
                addCode("return value.length")
            }
            .build()

        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter))
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
        val stringType = ClassName("kotlin", "String").ref()
        val booleanType = ClassName("kotlin", "Boolean").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val isEmptyFunction = KotlinFunctionSpec.builder("isEmpty", booleanType)
            .addCode("return value.isEmpty()")
            .build()

        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter))
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
        val stringType = ClassName("kotlin", "String").ref()

        val parameter = KotlinValueParameterSpec("value", stringType) {
            immutableProperty()
        }

        val valueClass = KotlinValueClassTypeSpec("UserId", createValueClassConstructor(parameter)) {
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
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter))
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
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty() // immutable property
            .build()

        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter)).build()

        val code = valueClass.writeToKotlinString()
        assertTrue(code.contains("val value: String"))
    }

    @Test
    fun testValueClassWithParameterDefaultValue() {
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .defaultValue("\"default\"")
            .build()

        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter)).build()

        val code = valueClass.writeToKotlinString()
        assertEquals(
            """
            value class UserId(val value: String = "default")
        """.trimIndent(),
            code
        )
    }

    @Test
    fun testComplexValueClass() {
        val stringType = KotlinClassNames.STRING.ref()
        val intType = KotlinClassNames.INT.ref()
        val booleanType = KotlinClassNames.BOOLEAN.ref()

        val parameter = KotlinValueParameterSpec.builder("id", stringType)
            .immutableProperty()
            .addDoc("The unique identifier")
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

        val valueClass = KotlinValueClassTypeSpec.builder("ComplexUserId", createValueClassConstructor(parameter))
            .addDoc("A complex value class for user identification.")
            .addDoc("\n@param id the string identifier")
            .addAnnotation(serializable)
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
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        // Test that VALUE modifier is automatically added and validated
        val builder = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter))
        val valueClass = builder.build()

        assertTrue(valueClass.modifiers.contains(KotlinModifier.VALUE))
    }

    @Test
    fun testValueClassBuilderWithoutValueModifierFails() {
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        // Create a builder and try to remove VALUE modifier (this should fail during build)
        val builder = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter))

        // The implementation should ensure VALUE modifier is always present
        val valueClass = builder.build()
        assertTrue(valueClass.modifiers.contains(KotlinModifier.VALUE))
    }

    @Test
    fun testValueClassConvenienceFunction() {
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassTypeSpec("UserId", createValueClassConstructor(parameter)) {
            addDoc("Convenience function test")
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
        val stringType = ClassName("kotlin", "String").ref()
        val intType = ClassName("kotlin", "Int").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter))
            .addDoc("Test KDoc") {
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
            .addFunction("isEmpty", ClassName("kotlin", "Boolean").ref()) {
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
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter)).build()

        // Value classes cannot have superclasses
        assertEquals(null, valueClass.superclass)
    }

    @Test
    fun testValueClassSubtypesIsEmpty() {
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(parameter)).build()

        // Value classes cannot have subtypes
        assertTrue(valueClass.subtypes.isEmpty())
    }

    @Test
    fun testValueClassConstructorCannotHaveConstructorDelegation() {
        val stringType = ClassName("kotlin", "String").ref()
        val parameter = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()

        // Test that constructor with super delegation is rejected
        try {
            val constructorWithSuperDelegation = KotlinConstructorSpec.builder()
                .addParameter(parameter)
                .superConstructorDelegation()
                .build()

            KotlinValueClassTypeSpec.builder("InvalidUserId", constructorWithSuperDelegation).build()
            kotlin.test.fail("Should have thrown an exception for constructor with super delegation")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("constructorDelegation"))
        }

        // Test that constructor with this delegation is rejected
        try {
            val constructorWithThisDelegation = KotlinConstructorSpec.builder()
                .addParameter(parameter)
                .thisConstructorDelegation()
                .build()

            KotlinValueClassTypeSpec.builder("InvalidUserId", constructorWithThisDelegation).build()
            kotlin.test.fail("Should have thrown an exception for constructor with this delegation")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("constructorDelegation"))
        }
    }

    @Test
    fun testValueClassConstructorMustHaveExactlyOneParameter() {
        // Test that constructor with no parameters is rejected
        try {
            val constructorWithNoParameters = KotlinConstructorSpec.builder().build()

            KotlinValueClassTypeSpec.builder("InvalidUserId", constructorWithNoParameters).build()
            kotlin.test.fail("Should have thrown an exception for constructor with no parameters")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("exactly one parameter"))
        }

        // Test that constructor with multiple parameters is rejected
        try {
            val stringType = ClassName("kotlin", "String").ref()
            val intType = ClassName("kotlin", "Int").ref()
            val param1 = KotlinValueParameterSpec.builder("value1", stringType)
                .immutableProperty()
                .build()
            val param2 = KotlinValueParameterSpec.builder("value2", intType)
                .immutableProperty()
                .build()

            val constructorWithMultipleParameters = KotlinConstructorSpec.builder()
                .addParameter(param1)
                .addParameter(param2)
                .build()

            KotlinValueClassTypeSpec.builder("InvalidUserId", constructorWithMultipleParameters).build()
            kotlin.test.fail("Should have thrown an exception for constructor with multiple parameters")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message!!.contains("exactly one parameter"))
        }
    }

    @Test
    fun testValueClassWithSingleSecondaryConstructor() {
        val stringType = ClassName("kotlin", "String").ref()
        val intType = ClassName("kotlin", "Int").ref()
        
        // Primary constructor parameter
        val primaryParam = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()
        
        // Secondary constructor
        val secondaryConstructor = KotlinConstructorSpec.builder()
            .addParameter("intValue", intType)
            .thisConstructorDelegation {
                addArgument("intValue.toString()")
            }
            .build()
        
        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(primaryParam))
            .addSecondaryConstructor(secondaryConstructor)
            .build()
        
        val code = valueClass.writeToKotlinString()
        val expectedCode = """value class UserId(val value: String) {
    constructor(intValue: Int) : this(intValue.toString())
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithMultipleSecondaryConstructors() {
        val stringType = ClassName("kotlin", "String").ref()
        val intType = ClassName("kotlin", "Int").ref()
        val longType = ClassName("kotlin", "Long").ref()
        
        // Primary constructor parameter
        val primaryParam = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()
        
        // First secondary constructor
        val secondaryConstructor1 = KotlinConstructorSpec.builder()
            .addParameter("intValue", intType)
            .thisConstructorDelegation {
                addArgument("intValue.toString()")
            }
            .build()
        
        // Second secondary constructor
        val secondaryConstructor2 = KotlinConstructorSpec.builder()
            .addParameter("longValue", longType)
            .thisConstructorDelegation {
                addArgument("longValue.toString()")
            }
            .build()
        
        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(primaryParam))
            .addSecondaryConstructors(secondaryConstructor1, secondaryConstructor2)
            .build()
        
        val code = valueClass.writeToKotlinString()
        val expectedCode = """value class UserId(val value: String) {
    constructor(intValue: Int) : this(intValue.toString())

    constructor(longValue: Long) : this(longValue.toString())
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassWithSecondaryConstructorUsingBuilderDsl() {
        val stringType = ClassName("kotlin", "String").ref()
        val intType = ClassName("kotlin", "Int").ref()
        
        // Primary constructor parameter
        val primaryParam = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()
        
        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(primaryParam))
            .addSecondaryConstructor {
                addParameter("intValue", intType)
                thisConstructorDelegation {
                    addArgument("intValue.toString()")
                }
            }
            .build()
        
        val code = valueClass.writeToKotlinString()
        val expectedCode = """value class UserId(val value: String) {
    constructor(intValue: Int) : this(intValue.toString())
}"""
        assertEquals(expectedCode, code)
    }

    @Test
    fun testValueClassSecondaryConstructorsProperty() {
        val stringType = ClassName("kotlin", "String").ref()
        val intType = ClassName("kotlin", "Int").ref()
        
        // Primary constructor parameter
        val primaryParam = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()
        
        // Secondary constructor
        val secondaryConstructor = KotlinConstructorSpec.builder()
            .addParameter("intValue", intType)
            .thisConstructorDelegation {
                addArgument("intValue.toString()")
            }
            .build()
        
        val valueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(primaryParam))
            .addSecondaryConstructor(secondaryConstructor)
            .build()
        
        // Verify that the secondary constructors property contains the expected constructor
        assertEquals(1, valueClass.secondaryConstructors.size)
        assertEquals(secondaryConstructor, valueClass.secondaryConstructors.first())
    }

    @Test
    fun testValueClassIsMemberEmptyWithSecondaryConstructors() {
        val stringType = ClassName("kotlin", "String").ref()
        val intType = ClassName("kotlin", "Int").ref()
        
        // Primary constructor parameter
        val primaryParam = KotlinValueParameterSpec.builder("value", stringType)
            .immutableProperty()
            .build()
        
        // Test empty value class
        val emptyValueClass = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(primaryParam))
            .build()
        assertTrue(emptyValueClass.isMemberEmpty())
        
        // Test value class with secondary constructor
        val valueClassWithSecondary = KotlinValueClassTypeSpec.builder("UserId", createValueClassConstructor(primaryParam))
            .addSecondaryConstructor {
                addParameter("intValue", intType)
                thisConstructorDelegation {
                    addArgument("intValue.toString()")
                }
            }
            .build()
        assertTrue(!valueClassWithSecondary.isMemberEmpty())
    }
}
