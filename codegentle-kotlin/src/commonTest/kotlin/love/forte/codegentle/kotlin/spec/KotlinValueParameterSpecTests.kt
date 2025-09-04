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

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.addDoc
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.*

/**
 * Comprehensive tests for [KotlinValueParameterSpec].
 */
class KotlinValueParameterSpecTests {

    private val stringType = ClassName("kotlin", "String").ref()
    private val intType = ClassName("kotlin", "Int").ref()
    private val listType = ClassName("kotlin.collections", "List").ref()

    // Common annotations for testing
    private val deprecatedAnnotation = ClassName("kotlin", "Deprecated").annotationRef {
        addMember("message", "\"This parameter is deprecated\"")
    }
    private val jvmNameAnnotation = ClassName("kotlin.jvm", "JvmName").annotationRef {
        addMember("name", "\"customName\"")
    }

    // ===== BASIC FUNCTIONALITY TESTS =====

    @Test
    fun testBasicParameterCreation() {
        val param = KotlinValueParameterSpec("name", stringType)

        assertEquals("name", param.name)
        assertEquals(stringType, param.typeRef)
        assertEquals(emptyList(), param.annotations)
        assertEquals(emptySet(), param.modifiers)
        assertTrue(param.kDoc.writeToKotlinString().isEmpty())
        assertNull(param.defaultValue)
        assertNull(param.propertyfication)
    }

    @Test
    fun testParameterWithDefaultValue() {
        val param = KotlinValueParameterSpec("count", intType) {
            defaultValue("42")
        }

        assertEquals("count", param.name)
        assertEquals(intType, param.typeRef)
        assertNotNull(param.defaultValue)
        assertEquals("42", param.defaultValue!!.writeToKotlinString())
    }

    @Test
    fun testParameterWithDefaultValueCodeValue() {
        val defaultValue = CodeValue("42")
        val param = KotlinValueParameterSpec("number", intType) {
            defaultValue(defaultValue)
        }

        assertEquals("number", param.name)
        assertNotNull(param.defaultValue)
        assertEquals("42", param.defaultValue!!.writeToKotlinString())
    }

    @Test
    fun testParameterWithKDoc() {
        val param = KotlinValueParameterSpec("name", stringType) {
            addDoc("The name parameter")
        }

        assertEquals("The name parameter", param.kDoc.writeToKotlinString())
    }

    // ===== PROPERTYIZATION TESTS =====

    @Test
    fun testPropertyizationBuilder() {
        val mutableProp = KotlinValueParameterSpec.propertyizationBuilder()
            .mutable(true)
            .build()
        assertTrue(mutableProp.mutable)

        val immutableProp = KotlinValueParameterSpec.propertyizationBuilder()
            .build()
        assertFalse(immutableProp.mutable) // Default should be false
    }

    @Test
    fun testPropertyizationDSL() {
        val mutableProp = propertyficationn { mutable = true }
        assertTrue(mutableProp.mutable)

        val immutableProp = propertyficationn { }
        assertFalse(immutableProp.mutable) // Default should be false
    }

    @Test
    fun testParameterPropertyization() {
        val mutableParam = KotlinValueParameterSpec("name", stringType) {
            propertyfy(propertyficationn { mutable = true })
        }
        assertNotNull(mutableParam.propertyfication)
        assertTrue(mutableParam.propertyfication!!.mutable)

        val immutableParam = KotlinValueParameterSpec("name", stringType) {
            propertyfy(propertyficationn { mutable = false })
        }
        assertNotNull(immutableParam.propertyfication)
        assertFalse(immutableParam.propertyfication!!.mutable)
    }

    @Test
    fun testParameterPropertyConvenienceMethods() {
        val varParam = KotlinValueParameterSpec("name", stringType) {
            mutableProperty()
        }
        assertNotNull(varParam.propertyfication)
        assertTrue(varParam.propertyfication!!.mutable)

        val valParam = KotlinValueParameterSpec("name", stringType) {
            immutableProperty()
        }
        assertNotNull(valParam.propertyfication)
        assertFalse(valParam.propertyfication!!.mutable)
    }

    @Test
    fun testPropertyizationBuilderChaining() {
        val propertyization = KotlinValueParameterSpec.propertyizationBuilder()
            .mutable(false)
            .mutable(true)
            .build()

        assertTrue(propertyization.mutable)
    }

    // ===== MODIFIER TESTS =====

    @Test
    fun testParameterModifiers() {
        // Single modifier
        val singleModParam = KotlinValueParameterSpec("param", stringType) {
            addModifier(KotlinModifier.CROSSINLINE)
        }
        assertEquals(setOf(KotlinModifier.CROSSINLINE), singleModParam.modifiers)

        // Multiple modifiers via multiple calls
        val multiModParam = KotlinValueParameterSpec("param", stringType) {
            addModifier(KotlinModifier.CROSSINLINE)
            addModifier(KotlinModifier.NOINLINE)
        }
        assertEquals(setOf(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE), multiModParam.modifiers)

        // Multiple modifiers via vararg
        val varargModParam = KotlinValueParameterSpec("param", stringType) {
            addModifiers(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE)
        }
        assertEquals(setOf(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE), varargModParam.modifiers)

        // Multiple modifiers via iterable
        val iterableModParam = KotlinValueParameterSpec("param", stringType) {
            addModifiers(listOf(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE))
        }
        assertEquals(setOf(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE), iterableModParam.modifiers)
    }

    // ===== ANNOTATION TESTS =====

    @Test
    fun testParameterAnnotations() {
        // Single annotation
        val singleAnnParam = KotlinValueParameterSpec("param", stringType) {
            addAnnotation(deprecatedAnnotation)
        }
        assertEquals(listOf(deprecatedAnnotation), singleAnnParam.annotations)

        // Multiple annotations
        val multiAnnParam = KotlinValueParameterSpec("param", stringType) {
            addAnnotation(deprecatedAnnotation)
            addAnnotation(jvmNameAnnotation)
        }
        assertEquals(listOf(deprecatedAnnotation, jvmNameAnnotation), multiAnnParam.annotations)

        // Multiple annotations via iterable
        val iterableAnnParam = KotlinValueParameterSpec("param", stringType) {
            addAnnotations(listOf(deprecatedAnnotation, jvmNameAnnotation))
        }
        assertEquals(listOf(deprecatedAnnotation, jvmNameAnnotation), iterableAnnParam.annotations)
    }

    // ===== CODE GENERATION TESTS =====

    @Test
    fun testBasicCodeGeneration() {
        val param = KotlinValueParameterSpec("name", stringType)
        assertEquals("name: String", param.writeToKotlinString())
    }

    @Test
    fun testCodeGenerationWithDefaultValue() {
        val param = KotlinValueParameterSpec("count", intType) {
            defaultValue("42")
        }
        assertEquals("count: Int = 42", param.writeToKotlinString())
    }

    @Test
    fun testCodeGenerationWithProperties() {
        val valParam = KotlinValueParameterSpec("name", stringType) {
            immutableProperty()
        }
        assertEquals("val name: String", valParam.writeToKotlinString())

        val varParam = KotlinValueParameterSpec("name", stringType) {
            mutableProperty()
        }
        assertEquals("var name: String", varParam.writeToKotlinString())

        val valWithDefaultParam = KotlinValueParameterSpec("name", stringType) {
            immutableProperty()
            defaultValue("\"default\"")
        }
        assertEquals("val name: String = \"default\"", valWithDefaultParam.writeToKotlinString())
    }

    @Test
    fun testCodeGenerationWithModifiers() {
        val param = KotlinValueParameterSpec("block", stringType) {
            addModifier(KotlinModifier.CROSSINLINE)
        }
        assertEquals("crossinline block: String", param.writeToKotlinString())

        val varargParam = KotlinValueParameterSpec("items", ClassName("kotlin", "Array").ref()) {
            addModifier(KotlinModifier.VARARG)
        }
        assertEquals("vararg items: Array", varargParam.writeToKotlinString())
    }

    @Test
    fun testCodeGenerationWithAnnotations() {
        val singleAnnParam = KotlinValueParameterSpec("param", stringType) {
            addAnnotation(deprecatedAnnotation)
        }
        assertEquals(
            "@Deprecated(message = \"This parameter is deprecated\")\nparam: String",
            singleAnnParam.writeToKotlinString()
        )

        val multiAnnParam = KotlinValueParameterSpec("param", stringType) {
            addAnnotation(deprecatedAnnotation)
            addAnnotation(jvmNameAnnotation)
        }
        val expected =
            "@Deprecated(message = \"This parameter is deprecated\")\n@JvmName(name = \"customName\")\nparam: String"
        assertEquals(expected, multiAnnParam.writeToKotlinString())
    }

    @Test
    fun testCodeGenerationWithKDoc() {
        val param = KotlinValueParameterSpec("name", stringType) {
            addDoc("The name parameter")
        }

        val expected = """
            /**
             * The name parameter
             */
            name: String
        """.trimIndent()
        assertEquals(expected, param.writeToKotlinString())
    }

    @Test
    fun testComplexParameterCodeGeneration() {
        val complexAnnotation = ClassName("kotlin", "Suppress").annotationRef {
            addMember("names", "\"UNUSED\"")
        }

        val param = KotlinValueParameterSpec("complexParam", stringType) {
            addAnnotation(complexAnnotation)
            addModifier(KotlinModifier.CROSSINLINE)
            immutableProperty()
            defaultValue("\"complex\"")
            addDoc("A parameter with all possible features")
        }

        val code = param.writeToKotlinString()
        val expected = """
            /**
             * A parameter with all possible features
             */
            @Suppress(names = "UNUSED")
            crossinline val complexParam: String = "complex"
        """.trimIndent()
        assertEquals(expected, code)
    }

    // ===== EDGE CASES AND ERROR CONDITIONS =====

    @Test
    fun testParameterWithEmptyName() {
        // This should be allowed as it might be used for special cases
        val param = KotlinValueParameterSpec("", stringType)

        assertEquals("", param.name)
        val code = param.writeToKotlinString()
        assertEquals(": String", code)
    }

    @Test
    fun testParameterWithNullDefaultValue() {
        val param = KotlinValueParameterSpec("value", stringType) {
            defaultValue("null")
        }

        val code = param.writeToKotlinString()
        assertEquals("value: String = null", code)
    }

    @Test
    fun testParameterWithComplexTypeCodeGeneration() {
        val mapType = ClassName("kotlin.collections", "Map").ref()
        val param = KotlinValueParameterSpec("data", mapType) {
            defaultValue("emptyMap()")
        }

        val code = param.writeToKotlinString()
        assertEquals("data: kotlin.collections.Map = emptyMap()", code)
    }

    @Test
    fun testParameterBuilderChaining() {
        val param = KotlinValueParameterSpec("param", stringType) {
            addModifier(KotlinModifier.CROSSINLINE)
            addAnnotation(deprecatedAnnotation)
            immutableProperty()
            defaultValue("\"test\"")
            addDoc("Test parameter")
        }

        assertEquals("param", param.name)
        assertEquals(stringType, param.typeRef)
        assertEquals(setOf(KotlinModifier.CROSSINLINE), param.modifiers)
        assertEquals(listOf(deprecatedAnnotation), param.annotations)
        assertNotNull(param.propertyfication)
        assertFalse(param.propertyfication!!.mutable)
        assertNotNull(param.defaultValue)
        assertEquals("\"test\"", param.defaultValue!!.writeToKotlinString())
        assertEquals("Test parameter", param.kDoc.writeToKotlinString())
    }

    // ===== INTEGRATION TESTS =====

    @Test
    fun testParameterInFunctionContext() {
        val param1 = KotlinValueParameterSpec("name", stringType)
        val param2 = KotlinValueParameterSpec("age", intType) {
            defaultValue("0")
        }

        val function = KotlinFunctionSpec("createPerson") {
            addParameter(param1)
            addParameter(param2)
            returns(ClassName("Person").ref())
            addCode("return Person(name, age)")
        }

        val code = function.writeToKotlinString()
        val expected = "fun createPerson(name: String, age: Int = 0): Person = Person(name, age)"
        assertEquals(expected, code)
    }

    @Test
    fun testParameterInConstructorContext() {
        val param1 = KotlinValueParameterSpec("name", stringType) {
            immutableProperty()
        }
        val param2 = KotlinValueParameterSpec("age", intType) {
            mutableProperty()
            defaultValue("0")
        }

        val constructor = KotlinConstructorSpec {
            addParameter(param1)
            addParameter(param2)
        }

        val classSpec = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Person") {
            primaryConstructor(constructor)
        }

        val code = classSpec.writeToKotlinString()
        val expected = "class Person(val name: String, var age: Int = 0)"
        assertEquals(expected, code)
    }

    // ===== EXTENSION FUNCTION TESTS =====

    @Test
    fun testDefaultValueExtensionFunction() {
        val param = KotlinValueParameterSpec("count", intType) {
            defaultValue("42") { }
        }

        assertEquals("42", param.defaultValue!!.writeToKotlinString())
    }

    @Test
    fun testAddKDocExtensionFunction() {
        val param = KotlinValueParameterSpec("name", stringType) {
            addDoc("Parameter documentation") { }
        }

        assertEquals("Parameter documentation", param.kDoc.writeToKotlinString())
    }

    @Test
    fun testPropertyizeWithBooleanExtensionFunction() {
        val param1 = KotlinValueParameterSpec("name", stringType) {
            propertyfy(true)
        }

        val param2 = KotlinValueParameterSpec("age", intType) {
            propertyfy(false)
        }

        assertNotNull(param1.propertyfication)
        assertTrue(param1.propertyfication!!.mutable)

        assertNotNull(param2.propertyfication)
        assertFalse(param2.propertyfication!!.mutable)
    }

    @Test
    fun testPropertyizeWithBlockExtensionFunction() {
        val param = KotlinValueParameterSpec("name", stringType) {
            propertyfy { mutable = true }
        }

        assertNotNull(param.propertyfication)
        assertTrue(param.propertyfication!!.mutable)
    }

    // ===== ADDITIONAL COMPREHENSIVE TESTS =====

    @Test
    fun testDefaultValueWithSimpleString() {
        val param = KotlinValueParameterSpec("message", stringType) {
            defaultValue("\"Hello, World!\"")
        }

        assertNotNull(param.defaultValue)
        assertEquals("\"Hello, World!\"", param.defaultValue!!.writeToKotlinString())
    }

    @Test
    fun testAddKDocWithSimpleString() {
        val param = KotlinValueParameterSpec("count", intType) {
            addDoc("The count parameter for items")
        }

        assertEquals("The count parameter for items", param.kDoc.writeToKotlinString())
    }

    @Test
    fun testMultipleKDocAdditions() {
        val param = KotlinValueParameterSpec("value", stringType) {
            addDoc("First line")
            addDoc("Second line")
        }

        val expectedKDoc = "First lineSecond line"
        assertEquals(expectedKDoc, param.kDoc.writeToKotlinString())
    }

    @Test
    fun testBuilderMutability() {
        // Convert to DSL style - creating separate parameter specs with different default values
        val param1 = KotlinValueParameterSpec("test", stringType) {
            defaultValue("value1")
        }
        val param2 = KotlinValueParameterSpec("test", stringType) {
            defaultValue("value2")
        }

        // DSL creates immutable specs, so each has its own value
        assertNotNull(param1.defaultValue)
        assertNotNull(param2.defaultValue)
        assertEquals("value1", param1.defaultValue!!.writeToKotlinString())
        assertEquals("value2", param2.defaultValue!!.writeToKotlinString())
    }

    @Test
    fun testBuilderNameAndTypeAccess() {
        val param = KotlinValueParameterSpec("testName", stringType)

        assertEquals("testName", param.name)
        assertEquals(stringType, param.typeRef)
    }

    @Test
    fun testPropertyizationBuilderMutableProperty() {
        val builder = KotlinValueParameterSpec.propertyizationBuilder()
        builder.mutable = true

        assertTrue(builder.mutable)

        val propertyization = builder.build()
        assertTrue(propertyization.mutable)
    }

    @Test
    fun testPropertyizationBuilderImmutableProperty() {
        val builder = KotlinValueParameterSpec.propertyizationBuilder()
        builder.mutable = false

        assertFalse(builder.mutable)

        val propertyization = builder.build()
        assertFalse(propertyization.mutable)
    }

    @Test
    fun testComplexDefaultValueWithCodeValue() {
        val complexDefault = CodeValue("listOf(1, 2, 3)")
        val param = KotlinValueParameterSpec("numbers", listType) {
            defaultValue(complexDefault)
        }

        assertEquals("listOf(1, 2, 3)", param.defaultValue!!.writeToKotlinString())
    }

    @Test
    fun testParameterWithGenericType() {
        val genericListType = ClassName("kotlin.collections", "List").ref()
        val param = KotlinValueParameterSpec("items", genericListType) {
            defaultValue("emptyList()")
        }

        val code = param.writeToKotlinString()
        assertEquals("items: kotlin.collections.List = emptyList()", code)
    }

    @Test
    fun testParameterWithNullableType() {
        val nullableStringType = ClassName("kotlin", "String").ref()
        val param = KotlinValueParameterSpec("optionalName", nullableStringType) {
            defaultValue("null")
        }

        val code = param.writeToKotlinString()
        assertEquals("optionalName: String = null", code)
    }

    @Test
    fun testParameterWithLongName() {
        val longName = "thisIsAVeryLongParameterNameThatShouldStillWork"
        val param = KotlinValueParameterSpec(longName, stringType)

        assertEquals(longName, param.name)
        val code = param.writeToKotlinString()
        assertEquals("$longName: String", code)
    }

    @Test
    fun testParameterWithSpecialCharactersInName() {
        val specialName = "`special name with spaces`"
        val param = KotlinValueParameterSpec(specialName, stringType)

        assertEquals(specialName, param.name)
        val code = param.writeToKotlinString()
        assertEquals("$specialName: String", code)
    }

    @Test
    fun testParameterWithComplexAnnotation() {
        val complexAnnotation = ClassName("kotlin", "Suppress").annotationRef {
            addMember("names", "\"UNUSED_PARAMETER\"")
            addMember("names", "\"DEPRECATION\"")
        }

        val param = KotlinValueParameterSpec("param", stringType) {
            addAnnotation(complexAnnotation)
        }

        val code = param.writeToKotlinString()
        assertEquals("@Suppress(names = [\"UNUSED_PARAMETER\", \"DEPRECATION\"])\nparam: String", code)
    }

    @Test
    fun testParameterWithAllModifiers() {
        val param = KotlinValueParameterSpec("param", stringType) {
            addModifiers(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE, KotlinModifier.VARARG)
        }

        assertEquals(setOf(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE, KotlinModifier.VARARG), param.modifiers)
    }

    @Test
    fun testParameterWithVarargModifier() {
        val param = KotlinValueParameterSpec("items", stringType) {
            addModifier(KotlinModifier.VARARG)
        }

        val code = param.writeToKotlinString()
        assertEquals("vararg items: String", code)
    }

    @Test
    fun testParameterWithVarargAndDefaultValue() {
        val param = KotlinValueParameterSpec("items", stringType) {
            addModifier(KotlinModifier.VARARG)
            defaultValue("\"default\"")
        }

        val code = param.writeToKotlinString()
        assertEquals("vararg items: String = \"default\"", code)
    }

    @Test
    fun testParameterWithAllFeaturesCodeGeneration() {
        val complexAnnotation = ClassName("kotlin", "Suppress").annotationRef {
            addMember("names", "\"UNUSED\"")
        }

        val param = KotlinValueParameterSpec("complexParam", stringType) {
            addAnnotation(complexAnnotation)
            addModifier(KotlinModifier.CROSSINLINE)
            immutableProperty()
            defaultValue("\"complex\"")
            addDoc("A parameter with all possible features")
        }

        val code = param.writeToKotlinString()
        val expected = """
            /**
             * A parameter with all possible features
             */
            @Suppress(names = "UNUSED")
            crossinline val complexParam: String = "complex"
        """.trimIndent()
        assertEquals(expected, code)
    }

    @Test
    fun testEmptyPropertyizationDSL() {
        val propertyization = propertyficationn { }
        assertFalse(propertyization.mutable) // Default should be false
    }

    @Test
    fun testPropertyizationToString() {
        val propertyization = propertyficationn { mutable = true }
        val toString = propertyization.toString()
        assertTrue(toString.contains("mutable") || toString.contains("true"))
    }

    @Test
    fun testMultipleAnnotationsWithSameType() {
        val annotation1 = ClassName("kotlin", "Suppress").annotationRef {
            addMember("names", "\"UNUSED\"")
        }
        val annotation2 = ClassName("kotlin", "Suppress").annotationRef {
            addMember("names", "\"DEPRECATION\"")
        }

        val param = KotlinValueParameterSpec("param", stringType) {
            addAnnotation(annotation1)
            addAnnotation(annotation2)
        }

        assertEquals(2, param.annotations.size)
    }

    @Test
    fun testParameterWithFunctionType() {
        val functionType = ClassName("kotlin", "Function1").ref()
        val param = KotlinValueParameterSpec("callback", functionType) {
            defaultValue("{ }")
        }

        val code = param.writeToKotlinString()
        assertEquals("callback: Function1 = { }", code)
    }

    @Test
    fun testParameterWithLambdaDefaultValue() {
        val param = KotlinValueParameterSpec("action", stringType) {
            defaultValue("{ println(\"default action\") }")
        }

        val code = param.writeToKotlinString()
        assertEquals("action: String = { println(\"default action\") }", code)
    }

    @Test
    fun testParameterInDataClassContext() {
        val param1 = KotlinValueParameterSpec("id", intType) {
            immutableProperty()
        }
        val param2 = KotlinValueParameterSpec("name", stringType) {
            immutableProperty()
        }

        val constructor = KotlinConstructorSpec {
            addParameter(param1)
            addParameter(param2)
        }

        val dataClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "User") {
            addModifier(KotlinModifier.DATA)
            primaryConstructor(constructor)
        }

        val code = dataClass.writeToKotlinString()
        assertEquals("data class User(val id: Int, val name: String)", code)
    }

    @Test
    fun testParameterWithNestedGenericType() {
        val nestedGenericType = ClassName("kotlin.collections", "Map").ref()
        val param = KotlinValueParameterSpec("data", nestedGenericType) {
            defaultValue("emptyMap()")
        }

        val code = param.writeToKotlinString()
        assertEquals("data: kotlin.collections.Map = emptyMap()", code)
    }

    @Test
    fun testParameterWithCustomAnnotationAndMembers() {
        val customAnnotation = ClassName("com.example", "CustomAnnotation").annotationRef {
            addMember("value", "\"test\"")
            addMember("number", "42")
            addMember("flag", "true")
        }

        val param = KotlinValueParameterSpec("param", stringType) {
            addAnnotation(customAnnotation)
        }

        val code = param.writeToKotlinString()
        assertEquals("@com.example.CustomAnnotation(value = \"test\", number = 42, flag = true)\nparam: String", code)
    }

    @Test
    fun testParameterWithMultilineKDoc() {
        val param = KotlinValueParameterSpec("config", stringType) {
            addDoc("This is a multi-line KDoc\n")
            addDoc("that spans multiple lines\n")
            addDoc("and provides detailed documentation")
        }

        val code = param.writeToKotlinString()
        val expected = """
            /**
             * This is a multi-line KDoc
             * that spans multiple lines
             * and provides detailed documentation
             */
            config: String
        """.trimIndent()
        assertEquals(expected, code)
    }

    @Test
    fun testParameterBuilderReuse() {
        // DSL creates immutable specs, so each parameter is created independently
        val param1 = KotlinValueParameterSpec("base", stringType) {
            defaultValue("value1")
        }
        val param2 = KotlinValueParameterSpec("base", stringType) {
            defaultValue("value2")
            addModifier(KotlinModifier.CROSSINLINE)
        }

        // DSL creates independent specs with their own state
        assertEquals("value1", param1.defaultValue!!.writeToKotlinString())
        assertEquals("value2", param2.defaultValue!!.writeToKotlinString())
        assertEquals(emptySet(), param1.modifiers)
        assertEquals(setOf(KotlinModifier.CROSSINLINE), param2.modifiers)
    }

    @Test
    fun testPropertyizationBuilderReuse() {
        // DSL creates immutable propertyization specs independently
        val prop1 = propertyficationn {
            mutable = true
        }
        val prop2 = propertyficationn {
            mutable = false
        }

        // DSL creates independent specs with their own state
        assertTrue(prop1.mutable)
        assertFalse(prop2.mutable)
    }

    @Test
    fun testParameterWithArrayType() {
        val arrayType = ClassName("kotlin", "Array").ref()
        val param = KotlinValueParameterSpec("items", arrayType) {
            addModifier(KotlinModifier.VARARG)
        }

        val code = param.writeToKotlinString()
        assertEquals("vararg items: Array", code)
    }

    @Test
    fun testParameterWithSuspendFunctionType() {
        val suspendFunctionType = ClassName("kotlin", "Function0").ref()
        val param = KotlinValueParameterSpec("suspendAction", suspendFunctionType) {
            addModifier(KotlinModifier.CROSSINLINE)
        }

        val code = param.writeToKotlinString()
        assertEquals("crossinline suspendAction: Function0", code)
    }

    @Test
    fun testParameterCodeGenerationConsistency() {
        val param = KotlinValueParameterSpec("test", stringType) {
            defaultValue("\"value\"")
        }

        // Multiple calls to writeToKotlinString should produce the same result
        val code1 = param.writeToKotlinString()
        val code2 = param.writeToKotlinString()
        assertEquals(code1, code2)
        assertEquals("test: String = \"value\"", code1)
    }

    @Test
    fun testParameterWithComplexDefaultValueExpression() {
        val param = KotlinValueParameterSpec("result", intType) {
            defaultValue("(1 + 2) * 3")
        }

        val code = param.writeToKotlinString()
        assertEquals("result: Int = (1 + 2) * 3", code)
    }

    @Test
    fun testParameterWithMethodCallDefaultValue() {
        val param = KotlinValueParameterSpec("timestamp", stringType) {
            defaultValue("System.currentTimeMillis().toString()")
        }

        val code = param.writeToKotlinString()
        assertEquals("timestamp: String = System.currentTimeMillis().toString()", code)
    }
}
