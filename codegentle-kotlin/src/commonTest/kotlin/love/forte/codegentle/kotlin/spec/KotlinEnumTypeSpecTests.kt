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

import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.emitString
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.addAnnotation
import love.forte.codegentle.common.ref.addMember
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.naming.AnnotationTargetExtensionScope
import love.forte.codegentle.kotlin.naming.addDeprecated
import love.forte.codegentle.kotlin.naming.setMessage
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.*

/**
 * Comprehensive tests for [KotlinEnumTypeSpec].
 */
class KotlinEnumTypeSpecTests {

    private val stringType = ClassName("kotlin", "String")
    private val intType = ClassName("kotlin", "Int")
    private val stringTypeRef = stringType.ref()
    private val intTypeRef = intType.ref()

    // ========== Builder Functionality Tests ==========

    @Test
    fun testBasicEnumCreation() {
        val enumSpec = KotlinEnumTypeSpec("Color")

        assertEquals("Color", enumSpec.name)
        assertEquals(KotlinTypeSpec.Kind.CLASS, enumSpec.kind)
        assertNull(enumSpec.superclass)
        assertTrue(enumSpec.enumConstants.isEmpty())
        assertTrue(enumSpec.properties.isEmpty())
        assertTrue(enumSpec.functions.isEmpty())
        assertTrue(enumSpec.subtypes.isEmpty())
        assertTrue(KotlinModifier.ENUM in enumSpec.modifiers)
    }

    @Test
    fun testEnumWithConstants() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED")
            addEnumConstant("GREEN")
            addEnumConstant("BLUE")
        }

        assertEquals(3, enumSpec.enumConstants.size)
        assertTrue(enumSpec.enumConstants.containsKey("RED"))
        assertTrue(enumSpec.enumConstants.containsKey("GREEN"))
        assertTrue(enumSpec.enumConstants.containsKey("BLUE"))
        assertNull(enumSpec.enumConstants["RED"])
        assertNull(enumSpec.enumConstants["GREEN"])
        assertNull(enumSpec.enumConstants["BLUE"])
    }

    @Test
    fun testEnumWithAnonymousClassConstants() {
        val anonymousClass = KotlinAnonymousClassTypeSpec {
            addFunction("getValue", stringTypeRef) {
                addModifier(KotlinModifier.OVERRIDE)
                addCode("return \"255\"")
            }
        }

        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED", anonymousClass)
            addEnumConstant("GREEN")
        }

        assertEquals(2, enumSpec.enumConstants.size)
        assertNotNull(enumSpec.enumConstants["RED"])
        assertNull(enumSpec.enumConstants["GREEN"])
    }

    @Test
    fun testEnumWithModifiers() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addModifier(KotlinModifier.PUBLIC)
            addModifier(KotlinModifier.SEALED)
        }

        assertTrue(KotlinModifier.PUBLIC in enumSpec.modifiers)
        assertTrue(KotlinModifier.SEALED in enumSpec.modifiers)
        assertTrue(KotlinModifier.ENUM in enumSpec.modifiers)
    }

    @Test
    fun testEnumWithKDoc() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addDoc("Represents different colors.")
        }

        assertFalse(enumSpec.kDoc.isEmpty())
    }

    @Test
    fun testEnumWithAnnotations() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addAnnotation(ClassName("kotlin", "Deprecated").annotationRef {
                addMember("message", "\"Use NewColor instead\"")
            })
        }

        assertEquals(1, enumSpec.annotations.size)
    }

    @Test
    fun testEnumWithTypeVariables() {
        val typeVar = TypeVariableName("T").ref()
        val enumSpec = KotlinEnumTypeSpec("Container") {
            addTypeVariable(typeVar)
        }

        assertEquals(1, enumSpec.typeVariables.size)
        assertEquals("T", enumSpec.typeVariables[0].typeName.name)
    }

    @Test
    fun testEnumWithSuperinterfaces() {
        val interfaceType = ClassName("test", "Comparable")
        val enumSpec = KotlinEnumTypeSpec("Priority") {
            addSuperinterface(interfaceType)
        }

        assertEquals(1, enumSpec.superinterfaces.size)
    }

    @Test
    fun testEnumWithProperties() {
        val property = KotlinPropertySpec("value", intTypeRef) {
            initializer("0")
        }

        val enumSpec = KotlinEnumTypeSpec("Color") {
            addProperty(property)
        }

        assertEquals(1, enumSpec.properties.size)
        assertEquals("value", enumSpec.properties[0].name)
    }

    @Test
    fun testEnumWithFunctions() {
        val function = KotlinFunctionSpec("toHex", stringTypeRef) {
            addCode("return \"#000000\"")
        }

        val enumSpec = KotlinEnumTypeSpec("Color") {
            addFunction(function)
        }

        assertEquals(1, enumSpec.functions.size)
        assertEquals("toHex", enumSpec.functions[0].name)
    }

    @Test
    fun testEnumWithInitializerBlock() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addInitializerBlock("println(\"Color enum initialized\")")
        }

        assertFalse(enumSpec.initializerBlock.isEmpty())
    }

    @Test
    fun testEnumBuilderChaining() {
        val result = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED")
            addModifier(KotlinModifier.PUBLIC)
            addDoc("Color enumeration")
        }

        // Test that DSL returns the correct spec
        assertNotNull(result)
        assertEquals("Color", result.name)
    }

    @Test
    fun testEnumDSLFactory() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED")
            addEnumConstant("GREEN")
        }

        assertEquals("Color", enumSpec.name)
        assertEquals(2, enumSpec.enumConstants.size)
    }

    // ========== Code Generation Tests ==========

    @Test
    fun testBasicEnumCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color")

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """enum class Color"""

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithConstantsCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED")
            addEnumConstant("GREEN")
            addEnumConstant("BLUE")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithSingleConstantCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Status") {
            addEnumConstant("ACTIVE")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
                enum class Status {
                    ACTIVE
                }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithModifiersCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addModifier(KotlinModifier.PUBLIC)
            addEnumConstant("RED")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
                    public enum class Color {
                        RED
                    }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithKDocCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addDoc("Represents different colors.")
            addEnumConstant("RED")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            /**
             * Represents different colors.
             */
            enum class Color {
                RED
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithAnnotationsCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addAnnotation(ClassName("kotlin", "Deprecated").annotationRef {
                addMember("message", "\"Use NewColor instead\"")
            })
            addEnumConstant("RED")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            @Deprecated(message = "Use NewColor instead")
            enum class Color {
                RED
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithTypeVariablesCodeGeneration() {
        val typeVar = TypeVariableName("T").ref()
        val enumSpec = KotlinEnumTypeSpec("Container") {
            addTypeVariable(typeVar)
            addEnumConstant("EMPTY")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Container<T> {
                EMPTY
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithSuperinterfacesCodeGeneration() {
        val comparableType = ClassName("kotlin", "Comparable")
        val enumSpec = KotlinEnumTypeSpec("Priority") {
            addSuperinterface(comparableType)
            addEnumConstant("LOW")
            addEnumConstant("HIGH")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Priority : Comparable {
                LOW,
                HIGH
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithMultipleSuperinterfacesCodeGeneration() {
        val comparableType = ClassName("kotlin", "Comparable")
        val serializableType = ClassName("java.io", "Serializable")
        val enumSpec = KotlinEnumTypeSpec("Priority") {
            addSuperinterface(comparableType)
            addSuperinterface(serializableType)
            addEnumConstant("LOW")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Priority : Comparable, java.io.Serializable {
                LOW
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithPropertiesCodeGeneration() {
        val property = KotlinPropertySpec("value", intTypeRef) {
            initializer("0")
        }

        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED")
            addProperty(property)
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color {
                RED;
            
                val value: Int = 0
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithFunctionsCodeGeneration() {
        val function = KotlinFunctionSpec("toHex", stringTypeRef) {
            addCode("return \"#FF0000\"")
        }

        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED")
            addFunction(function)
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color {
                RED;
            
                fun toHex(): String = "#FF0000"
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithInitializerBlockCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED")
            addInitializerBlock("println(\"Color enum initialized\")")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color {
                RED;
            
                init {
                    println("Color enum initialized")
                }
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithAnonymousClassConstantCodeGeneration() {
        val anonymousClass = KotlinAnonymousClassTypeSpec {
            addFunction("getValue", intTypeRef) {
                addModifier(KotlinModifier.OVERRIDE)
                addCode("return 255")
            }
        }

        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED", anonymousClass)
            addEnumConstant("GREEN")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color {
                RED {
                    override fun getValue(): Int = 255
                },

                GREEN
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testComplexEnumCodeGeneration() {
        val comparableType = ClassName("kotlin", "Comparable")
        val typeVar = TypeVariableName("T").ref()

        val redAnonymous = KotlinAnonymousClassTypeSpec {
            addSuperConstructorArgument("255")
            addSuperConstructorArgument("0")
            addSuperConstructorArgument("0")
            addFunction("getName", stringTypeRef) {
                addModifier(KotlinModifier.OVERRIDE)
                addCode("return \"Red\"")
            }
        }

        val property = KotlinPropertySpec("defaultValue", intTypeRef) {
            initializer("0")
        }

        val function = KotlinFunctionSpec("toHex", stringTypeRef) {
            addCode("return \"#000000\"")
        }

        val enumSpec = KotlinEnumTypeSpec("Color") {
            addModifier(KotlinModifier.PUBLIC)
            addDoc("Represents RGB colors with comparison capability.")
            addAnnotation(ClassName("kotlin", "Suppress").annotationRef {
                addMember("names", "\"UNUSED\"")
            })
            addTypeVariable(typeVar)
            addSuperinterface(comparableType)
            addEnumConstant("RED", redAnonymous)
            addEnumConstant("GREEN")
            addEnumConstant("BLUE")
            addInitializerBlock("println(\"Color enum initialized\")")
            addProperty(property)
            addFunction(function)
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            /**
             * Represents RGB colors with comparison capability.
             */
            @Suppress(names = "UNUSED")
            public enum class Color<T> : Comparable {
                RED(255, 0, 0) {
                    override fun getName(): String = "Red"
                },
            
                GREEN,
                BLUE;
            
                init {
                    println("Color enum initialized")
                }
            
                val defaultValue: Int = 0
            
                fun toHex(): String = "#000000"
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    // ========== Edge Cases and Error Conditions ==========

    @Test
    fun testEnumAlwaysHasEnumModifier() {
        val enumSpec = KotlinEnumTypeSpec("Color")
        assertTrue(KotlinModifier.ENUM in enumSpec.modifiers)
    }

    @Test
    fun testEnumSuperclassAlwaysNull() {
        val enumSpec = KotlinEnumTypeSpec("Color")
        assertNull(enumSpec.superclass)
    }

    @Test
    fun testEnumKindAlwaysClass() {
        val enumSpec = KotlinEnumTypeSpec("Color")
        assertEquals(KotlinTypeSpec.Kind.CLASS, enumSpec.kind)
    }

    @Test
    fun testEnumCodeGenerationConsistency() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED")
        }

        val code1 = enumSpec.writeToKotlinString()
        val code2 = enumSpec.writeToKotlinString()

        assertEquals(code1, code2)
    }

    @Test
    fun testEnumBuilderReuse() {
        // Note: DSL style creates immutable specs, so this test demonstrates the difference
        val enum1 = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED")
        }
        val enum2 = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED")
            addEnumConstant("GREEN")
        }

        assertEquals("Color", enum1.name)
        assertEquals("Color", enum2.name)
        assertEquals(1, enum1.enumConstants.size)
        assertEquals(2, enum2.enumConstants.size)
    }

    @Test
    fun testEnumWithOnlyPropertiesNoConstantsCodeGeneration() {
        val property = KotlinPropertySpec("defaultValue", intTypeRef) {
            initializer("0")
        }

        val enumSpec = KotlinEnumTypeSpec("EmptyColor") {
            addProperty(property)
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class EmptyColor {
                ;
                val defaultValue: Int = 0
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithOnlyFunctionsNoConstantsCodeGeneration() {
        val function = KotlinFunctionSpec("getDefault", stringTypeRef) {
            addCode("return \"none\"")
        }

        val enumSpec = KotlinEnumTypeSpec("EmptyColor") {
            addFunction(function)
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class EmptyColor {
                ;
                fun getDefault(): String = "none"
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    // ========== Enum Constant KDoc and Annotation Tests ==========

    @Test
    fun testEnumConstantWithKDocCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED") {
                addDoc("Represents the red color with RGB value (255, 0, 0).")
            }
            addEnumConstant("GREEN")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color {
                /**
                 * Represents the red color with RGB value (255, 0, 0).
                 */
                RED,
                GREEN
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Suppress("ImplicitThis")
    @OptIn(AnnotationTargetExtensionScope::class)
    @Test
    fun testEnumConstantWithAnnotationCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED") {
                // addDeprecated {
                //     addMember("message", CodeValue(CodePart.string("Use CRIMSON instead")))
                // }
                addAnnotation(ClassName("kotlin", "Deprecated")) {
                    addMember("message", "%V") {
                        emitString("Use CRIMSON instead")
                    }
                }
            }
            addEnumConstant("GREEN")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color {
                @Deprecated(message = "Use CRIMSON instead")
                RED,
                GREEN
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @OptIn(AnnotationTargetExtensionScope::class)
    @Test
    fun testEnumConstantWithKDocAndAnnotationCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED") {
                addDoc("Represents the red color.")
                addDeprecated {
                    setMessage("Use CRIMSON instead")
                }
            }
            addEnumConstant("GREEN")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color {
                /**
                 * Represents the red color.
                 */
                @Deprecated(message = "Use CRIMSON instead")
                RED,
                GREEN
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumConstantWithMultipleAnnotationsCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED") {
                addAnnotation(ClassName("kotlin", "Deprecated")) {
                    addMember("message", "%V") {
                        emitString("Use CRIMSON instead")
                    }
                }
                addAnnotation(ClassName("kotlin", "Suppress")) {
                    addMember("names", "%V", CodePart.string("DEPRECATION"))
                }
            }
            addEnumConstant("GREEN")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color {
                @Deprecated(message = "Use CRIMSON instead")
                @Suppress(names = "DEPRECATION")
                RED,
                GREEN
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumConstantWithKDocAnnotationAndFunctionCodeGeneration() {
        val redAnonymous = KotlinAnonymousClassTypeSpec {
            addDoc("Red color with custom implementation.")
            addAnnotation(ClassName("kotlin", "Suppress").annotationRef {
                addMember("names", "\"UNUSED\"")
            })
            addFunction("getValue", intTypeRef) {
                addModifier(KotlinModifier.OVERRIDE)
                addCode("return 255")
            }
        }

        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED", redAnonymous)
            addEnumConstant("GREEN")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color {
                /**
                 * Red color with custom implementation.
                 */
                @Suppress(names = "UNUSED")
                RED {
                    override fun getValue(): Int = 255
                },

                GREEN
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testMultipleEnumConstantsWithKDocAndAnnotationsCodeGeneration() {
        val redAnonymous = KotlinAnonymousClassTypeSpec {
            addDoc("Primary red color.")
            addAnnotation(ClassName("kotlin", "Deprecated").annotationRef {
                addMember("message", "\"Use CRIMSON instead\"")
            })
        }

        val blueAnonymous = KotlinAnonymousClassTypeSpec {
            addDoc("Primary blue color.")
            addAnnotation(ClassName("kotlin", "Suppress").annotationRef {
                addMember("names", "\"UNUSED\"")
            })
        }

        val enumSpec = KotlinEnumTypeSpec("Color") {
            addEnumConstant("RED", redAnonymous)
            addEnumConstant("GREEN")
            addEnumConstant("BLUE", blueAnonymous)
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color {
                /**
                 * Primary red color.
                 */
                @Deprecated(message = "Use CRIMSON instead")
                RED,
                GREEN,

                /**
                 * Primary blue color.
                 */
                @Suppress(names = "UNUSED")
                BLUE
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithPrimaryConstructorCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            primaryConstructor {
                addParameter("rgb", intType)
            }
            addEnumConstant("RED")
            addEnumConstant("GREEN")
            addEnumConstant("BLUE")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color(rgb: Int) {
                RED,
                GREEN,
                BLUE
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithSecondaryConstructorCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Status") {
            primaryConstructor {
                addParameter("code", intType)
            }
            addSecondaryConstructor {
                addParameter("message", stringType)
                thisConstructorDelegation {
                    addArgument("0")
                }
            }
            addEnumConstant("SUCCESS")
            addEnumConstant("ERROR")
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Status(code: Int) {
                SUCCESS,
                ERROR;

                constructor(message: String) : this(0)
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithBothConstructorsAndMembersCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec("Color") {
            primaryConstructor {
                addParameter("rgb", intType)
            }
            addSecondaryConstructor {
                addParameter("name", stringType)
                thisConstructorDelegation {
                    addArgument("0")
                }
            }
            addEnumConstant("RED")
            addEnumConstant("GREEN")
            addProperty("displayName", stringTypeRef)
        }

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color(rgb: Int) {
                RED,
                GREEN;

                constructor(name: String) : this(0)

                val displayName: String
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }
}
