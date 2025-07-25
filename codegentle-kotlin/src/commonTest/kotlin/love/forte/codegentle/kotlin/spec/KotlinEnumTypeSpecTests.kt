package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.emitString
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.PackageNames
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.naming.className
import love.forte.codegentle.common.ref.addAnnotationRef
import love.forte.codegentle.common.ref.addMember
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.*

/**
 * Comprehensive tests for [KotlinEnumTypeSpec].
 */
class KotlinEnumTypeSpecTests {

    private val stringType = ClassName("kotlin", "String")
    private val intType = ClassName("kotlin", "Int")
    private val stringTypeRef = stringType.kotlinRef()
    private val intTypeRef = intType.kotlinRef()

    // ========== Builder Functionality Tests ==========

    @Test
    fun testBasicEnumCreation() {
        val enumSpec = KotlinEnumTypeSpec.builder("Color").build()

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
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED")
            .addEnumConstant("GREEN")
            .addEnumConstant("BLUE")
            .build()

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
        val anonymousClass = KotlinAnonymousClassTypeSpec.builder()
            .addFunction(
                KotlinFunctionSpec.builder("getValue", stringTypeRef)
                    .addModifier(KotlinModifier.OVERRIDE)
                    .addCode("return \"255\"")
                    .build()
            )
            .build()

        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED", anonymousClass)
            .addEnumConstant("GREEN")
            .build()

        assertEquals(2, enumSpec.enumConstants.size)
        assertNotNull(enumSpec.enumConstants["RED"])
        assertNull(enumSpec.enumConstants["GREEN"])
    }

    @Test
    fun testEnumWithModifiers() {
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addModifier(KotlinModifier.PUBLIC)
            .addModifier(KotlinModifier.SEALED)
            .build()

        assertTrue(KotlinModifier.PUBLIC in enumSpec.modifiers)
        assertTrue(KotlinModifier.SEALED in enumSpec.modifiers)
        assertTrue(KotlinModifier.ENUM in enumSpec.modifiers)
    }

    @Test
    fun testEnumWithKDoc() {
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addKDoc("Represents different colors.")
            .build()

        assertFalse(enumSpec.kDoc.isEmpty())
    }

    @Test
    fun testEnumWithAnnotations() {
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addAnnotationRef(ClassName("kotlin", "Deprecated").annotationRef {
                addMember("message", "\"Use NewColor instead\"")
            })
            .build()

        assertEquals(1, enumSpec.annotations.size)
    }

    @Test
    fun testEnumWithTypeVariables() {
        val typeVar = TypeVariableName("T").kotlinRef()
        val enumSpec = KotlinEnumTypeSpec.builder("Container")
            .addTypeVariable(typeVar)
            .build()

        assertEquals(1, enumSpec.typeVariables.size)
        assertEquals("T", enumSpec.typeVariables[0].typeName.name)
    }

    @Test
    fun testEnumWithSuperinterfaces() {
        val interfaceType = ClassName("test", "Comparable")
        val enumSpec = KotlinEnumTypeSpec.builder("Priority")
            .addSuperinterface(interfaceType)
            .build()

        assertEquals(1, enumSpec.superinterfaces.size)
    }

    @Test
    fun testEnumWithProperties() {
        val property = KotlinPropertySpec.builder("value", intTypeRef)
            .initializer("0")
            .build()

        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addProperty(property)
            .build()

        assertEquals(1, enumSpec.properties.size)
        assertEquals("value", enumSpec.properties[0].name)
    }

    @Test
    fun testEnumWithFunctions() {
        val function = KotlinFunctionSpec.builder("toHex", stringTypeRef)
            .addCode("return \"#000000\"")
            .build()

        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addFunction(function)
            .build()

        assertEquals(1, enumSpec.functions.size)
        assertEquals("toHex", enumSpec.functions[0].name)
    }

    @Test
    fun testEnumWithInitializerBlock() {
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addInitializerBlock("println(\"Color enum initialized\")")
            .build()

        assertFalse(enumSpec.initializerBlock.isEmpty())
    }

    @Test
    fun testEnumBuilderChaining() {
        val result = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED")
            .addModifier(KotlinModifier.PUBLIC)
            .addKDoc("Color enumeration")

        // Test that chaining returns the same builder type
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
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addModifier(KotlinModifier.PUBLIC)
            .addEnumConstant("RED")
            .build()

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
                    public enum class Color {
                        RED
                    }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithKDocCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addKDoc("Represents different colors.")
            .addEnumConstant("RED")
            .build()

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
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addAnnotationRef(ClassName("kotlin", "Deprecated").annotationRef {
                addMember("message", "\"Use NewColor instead\"")
            })
            .addEnumConstant("RED")
            .build()

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
        val typeVar = TypeVariableName("T").kotlinRef()
        val enumSpec = KotlinEnumTypeSpec.builder("Container")
            .addTypeVariable(typeVar)
            .addEnumConstant("EMPTY")
            .build()

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
        val enumSpec = KotlinEnumTypeSpec.builder("Priority")
            .addSuperinterface(comparableType)
            .addEnumConstant("LOW")
            .addEnumConstant("HIGH")
            .build()

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
        val enumSpec = KotlinEnumTypeSpec.builder("Priority")
            .addSuperinterface(comparableType)
            .addSuperinterface(serializableType)
            .addEnumConstant("LOW")
            .build()

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Priority : Comparable, java.io.Serializable {
                LOW
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumWithPropertiesCodeGeneration() {
        val property = KotlinPropertySpec.builder("value", intTypeRef)
            .initializer("0")
            .build()

        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED")
            .addProperty(property)
            .build()

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
        val function = KotlinFunctionSpec.builder("toHex", stringTypeRef)
            .addCode("return \"#FF0000\"")
            .build()

        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED")
            .addFunction(function)
            .build()

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
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED")
            .addInitializerBlock("println(\"Color enum initialized\")")
            .build()

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
        val anonymousClass = KotlinAnonymousClassTypeSpec.builder()
            .addFunction(
                KotlinFunctionSpec.builder("getValue", intTypeRef)
                    .addModifier(KotlinModifier.OVERRIDE)
                    .addCode("return 255")
                    .build()
            )
            .build()

        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED", anonymousClass)
            .addEnumConstant("GREEN")
            .build()

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
        val typeVar = TypeVariableName("T").kotlinRef()

        val redAnonymous = KotlinAnonymousClassTypeSpec.builder()
            .addSuperConstructorArgument("255")
            .addSuperConstructorArgument("0")
            .addSuperConstructorArgument("0")
            .addFunction(
                KotlinFunctionSpec.builder("getName", stringTypeRef)
                    .addModifier(KotlinModifier.OVERRIDE)
                    .addCode("return \"Red\"")
                    .build()
            )
            .build()

        val property = KotlinPropertySpec.builder("defaultValue", intTypeRef)
            .initializer("0")
            .build()

        val function = KotlinFunctionSpec.builder("toHex", stringTypeRef)
            .addCode("return \"#000000\"")
            .build()

        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addModifier(KotlinModifier.PUBLIC)
            .addKDoc("Represents RGB colors with comparison capability.")
            .addAnnotationRef(ClassName("kotlin", "Suppress").annotationRef {
                addMember("names", "\"UNUSED\"")
            })
            .addTypeVariable(typeVar)
            .addSuperinterface(comparableType)
            .addEnumConstant("RED", redAnonymous)
            .addEnumConstant("GREEN")
            .addEnumConstant("BLUE")
            .addInitializerBlock("println(\"Color enum initialized\")")
            .addProperty(property)
            .addFunction(function)
            .build()

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
        val enumSpec = KotlinEnumTypeSpec.builder("Color").build()
        assertTrue(KotlinModifier.ENUM in enumSpec.modifiers)
    }

    @Test
    fun testEnumSuperclassAlwaysNull() {
        val enumSpec = KotlinEnumTypeSpec.builder("Color").build()
        assertNull(enumSpec.superclass)
    }

    @Test
    fun testEnumKindAlwaysClass() {
        val enumSpec = KotlinEnumTypeSpec.builder("Color").build()
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
        val builder = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED")

        val enum1 = builder.build()
        val enum2 = builder.addEnumConstant("GREEN").build()

        assertEquals("Color", enum1.name)
        assertEquals("Color", enum2.name)
        assertEquals(1, enum1.enumConstants.size)
        assertEquals(2, enum2.enumConstants.size)
    }

    @Test
    fun testEnumWithOnlyPropertiesNoConstantsCodeGeneration() {
        val property = KotlinPropertySpec.builder("defaultValue", intTypeRef)
            .initializer("0")
            .build()

        val enumSpec = KotlinEnumTypeSpec.builder("EmptyColor")
            .addProperty(property)
            .build()

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
        val function = KotlinFunctionSpec.builder("getDefault", stringTypeRef)
            .addCode("return \"none\"")
            .build()

        val enumSpec = KotlinEnumTypeSpec.builder("EmptyColor")
            .addFunction(function)
            .build()

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
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED") {
                addKDoc("Represents the red color with RGB value (255, 0, 0).")
            }
            .addEnumConstant("GREEN")
            .build()

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

    @Test
    fun testEnumConstantWithAnnotationCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED") {
                addAnnotationRef(PackageNames.KOTLIN.className("Deprecated")) {
                    addMember("message", "%V") {
                        emitString("Use CRIMSON instead")
                    }
                }
            }
            .addEnumConstant("GREEN")
            .build()

        val generatedCode = enumSpec.writeToKotlinString()
        val expectedCode = """
            enum class Color {
                @Deprecated(message = "Use CRIMSON instead")
                RED,
                GREEN
            }""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testEnumConstantWithKDocAndAnnotationCodeGeneration() {
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED") {
                addKDoc("Represents the red color.")
                addAnnotationRef(ClassName("kotlin", "Deprecated")) {
                    addMember("message", "%V") {
                        emitString("Use CRIMSON instead")
                    }
                }
            }
            .addEnumConstant("GREEN")
            .build()

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
        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED") {
                addAnnotationRef(ClassName("kotlin", "Deprecated")) {
                    addMember("message", "%V") {
                        emitString("Use CRIMSON instead")
                    }
                }
                addAnnotationRef(ClassName("kotlin", "Suppress")) {
                    addMember("names", "%V", CodePart.string("DEPRECATION"))
                }
            }
            .addEnumConstant("GREEN")
            .build()

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
        val redAnonymous = KotlinAnonymousClassTypeSpec.builder()
            .addKDoc("Red color with custom implementation.")
            .addAnnotationRef(ClassName("kotlin", "Suppress").annotationRef {
                addMember("names", "\"UNUSED\"")
            })
            .addFunction(
                KotlinFunctionSpec.builder("getValue", intTypeRef)
                    .addModifier(KotlinModifier.OVERRIDE)
                    .addCode("return 255")
                    .build()
            )
            .build()

        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED", redAnonymous)
            .addEnumConstant("GREEN")
            .build()

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
        val redAnonymous = KotlinAnonymousClassTypeSpec.builder()
            .addKDoc("Primary red color.")
            .addAnnotationRef(ClassName("kotlin", "Deprecated").annotationRef {
                addMember("message", "\"Use CRIMSON instead\"")
            })
            .build()

        val blueAnonymous = KotlinAnonymousClassTypeSpec.builder()
            .addKDoc("Primary blue color.")
            .addAnnotationRef(ClassName("kotlin", "Suppress").annotationRef {
                addMember("names", "\"UNUSED\"")
            })
            .build()

        val enumSpec = KotlinEnumTypeSpec.builder("Color")
            .addEnumConstant("RED", redAnonymous)
            .addEnumConstant("GREEN")
            .addEnumConstant("BLUE", blueAnonymous)
            .build()

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
}
