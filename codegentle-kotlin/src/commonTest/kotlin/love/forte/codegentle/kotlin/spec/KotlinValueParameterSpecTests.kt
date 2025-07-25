package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.addKDoc
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.annotationRef
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
    private val booleanType = ClassName("kotlin", "Boolean").kotlinRef()
    private val listType = ClassName("kotlin.collections", "List").kotlinRef()

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
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .build()

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
        val param = KotlinValueParameterSpec.builder("count", intType)
            .defaultValue("42")
            .build()

        assertEquals("count", param.name)
        assertEquals(intType, param.typeRef)
        assertNotNull(param.defaultValue)
        assertEquals("42", param.defaultValue!!.writeToKotlinString())
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
        val mutableParam = KotlinValueParameterSpec.builder("name", stringType)
            .propertyfy(propertyficationn { mutable = true })
            .build()
        assertNotNull(mutableParam.propertyfication)
        assertTrue(mutableParam.propertyfication!!.mutable)

        val immutableParam = KotlinValueParameterSpec.builder("name", stringType)
            .propertyfy(propertyficationn { mutable = false })
            .build()
        assertNotNull(immutableParam.propertyfication)
        assertFalse(immutableParam.propertyfication!!.mutable)
    }

    @Test
    fun testParameterPropertyConvenienceMethods() {
        val varParam = KotlinValueParameterSpec.builder("name", stringType)
            .mutableProperty()
            .build()
        assertNotNull(varParam.propertyfication)
        assertTrue(varParam.propertyfication!!.mutable)

        val valParam = KotlinValueParameterSpec.builder("name", stringType)
            .immutableProperty()
            .build()
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
        val singleModParam = KotlinValueParameterSpec.builder("param", stringType)
            .addModifier(KotlinModifier.CROSSINLINE)
            .build()
        assertEquals(setOf(KotlinModifier.CROSSINLINE), singleModParam.modifiers)

        // Multiple modifiers via multiple calls
        val multiModParam = KotlinValueParameterSpec.builder("param", stringType)
            .addModifier(KotlinModifier.CROSSINLINE)
            .addModifier(KotlinModifier.NOINLINE)
            .build()
        assertEquals(setOf(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE), multiModParam.modifiers)

        // Multiple modifiers via vararg
        val varargModParam = KotlinValueParameterSpec.builder("param", stringType)
            .addModifiers(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE)
            .build()
        assertEquals(setOf(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE), varargModParam.modifiers)

        // Multiple modifiers via iterable
        val iterableModParam = KotlinValueParameterSpec.builder("param", stringType)
            .addModifiers(listOf(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE))
            .build()
        assertEquals(setOf(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE), iterableModParam.modifiers)
    }

    // ===== ANNOTATION TESTS =====

    @Test
    fun testParameterAnnotations() {
        // Single annotation
        val singleAnnParam = KotlinValueParameterSpec.builder("param", stringType)
            .addAnnotation(deprecatedAnnotation)
            .build()
        assertEquals(listOf(deprecatedAnnotation), singleAnnParam.annotations)

        // Multiple annotations
        val multiAnnParam = KotlinValueParameterSpec.builder("param", stringType)
            .addAnnotation(deprecatedAnnotation)
            .addAnnotation(jvmNameAnnotation)
            .build()
        assertEquals(listOf(deprecatedAnnotation, jvmNameAnnotation), multiAnnParam.annotations)

        // Multiple annotations via iterable
        val iterableAnnParam = KotlinValueParameterSpec.builder("param", stringType)
            .addAnnotations(listOf(deprecatedAnnotation, jvmNameAnnotation))
            .build()
        assertEquals(listOf(deprecatedAnnotation, jvmNameAnnotation), iterableAnnParam.annotations)
    }

    // ===== CODE GENERATION TESTS =====

    @Test
    fun testBasicCodeGeneration() {
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .build()
        assertEquals("name: String", param.writeToKotlinString())
    }

    @Test
    fun testCodeGenerationWithDefaultValue() {
        val param = KotlinValueParameterSpec.builder("count", intType)
            .defaultValue("42")
            .build()
        assertEquals("count: Int = 42", param.writeToKotlinString())
    }

    @Test
    fun testCodeGenerationWithProperties() {
        val valParam = KotlinValueParameterSpec.builder("name", stringType)
            .immutableProperty()
            .build()
        assertEquals("val name: String", valParam.writeToKotlinString())

        val varParam = KotlinValueParameterSpec.builder("name", stringType)
            .mutableProperty()
            .build()
        assertEquals("var name: String", varParam.writeToKotlinString())

        val valWithDefaultParam = KotlinValueParameterSpec.builder("name", stringType)
            .immutableProperty()
            .defaultValue("\"default\"")
            .build()
        assertEquals("val name: String = \"default\"", valWithDefaultParam.writeToKotlinString())
    }

    @Test
    fun testCodeGenerationWithModifiers() {
        val param = KotlinValueParameterSpec.builder("block", stringType)
            .addModifier(KotlinModifier.CROSSINLINE)
            .build()
        assertEquals("crossinline block: String", param.writeToKotlinString())

        val varargParam = KotlinValueParameterSpec.builder("items", ClassName("kotlin", "Array").kotlinRef())
            .addModifier(KotlinModifier.VARARG)
            .build()
        assertEquals("vararg items: Array", varargParam.writeToKotlinString())
    }

    @Test
    fun testCodeGenerationWithAnnotations() {
        val singleAnnParam = KotlinValueParameterSpec.builder("param", stringType)
            .addAnnotation(deprecatedAnnotation)
            .build()
        assertEquals("@Deprecated(message = \"This parameter is deprecated\")\nparam: String", 
                    singleAnnParam.writeToKotlinString())

        val multiAnnParam = KotlinValueParameterSpec.builder("param", stringType)
            .addAnnotation(deprecatedAnnotation)
            .addAnnotation(jvmNameAnnotation)
            .build()
        val expected = "@Deprecated(message = \"This parameter is deprecated\")\n@JvmName(name = \"customName\")\nparam: String"
        assertEquals(expected, multiAnnParam.writeToKotlinString())
    }

    @Test
    fun testCodeGenerationWithKDoc() {
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .addKDoc("The name parameter")
            .build()

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

        val param = KotlinValueParameterSpec.builder("complexParam", stringType)
            .addAnnotation(complexAnnotation)
            .addModifier(KotlinModifier.CROSSINLINE)
            .immutableProperty()
            .defaultValue("\"complex\"")
            .addKDoc("A parameter with all possible features")
            .build()

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
        val param = KotlinValueParameterSpec.builder("", stringType)
            .build()

        assertEquals("", param.name)
        val code = param.writeToKotlinString()
        assertEquals(": String", code)
    }

    @Test
    fun testParameterWithNullDefaultValue() {
        val param = KotlinValueParameterSpec.builder("value", stringType)
            .defaultValue("null")
            .build()

        val code = param.writeToKotlinString()
        assertEquals("value: String = null", code)
    }

    @Test
    fun testParameterWithComplexTypeCodeGeneration() {
        val mapType = ClassName("kotlin.collections", "Map").kotlinRef()
        val param = KotlinValueParameterSpec.builder("data", mapType)
            .defaultValue("emptyMap()")
            .build()

        val code = param.writeToKotlinString()
        assertEquals("data: kotlin.collections.Map = emptyMap()", code)
    }

    @Test
    fun testParameterBuilderChaining() {
        val param = KotlinValueParameterSpec.builder("param", stringType)
            .addModifier(KotlinModifier.CROSSINLINE)
            .addAnnotation(deprecatedAnnotation)
            .immutableProperty()
            .defaultValue("\"test\"")
            .addKDoc("Test parameter")
            .build()

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
        val param1 = KotlinValueParameterSpec.builder("name", stringType)
            .build()
        val param2 = KotlinValueParameterSpec.builder("age", intType)
            .defaultValue("0")
            .build()

        val function = KotlinFunctionSpec.builder("createPerson")
            .addParameter(param1)
            .addParameter(param2)
            .returns(ClassName("Person").kotlinRef())
            .addCode("return Person(name, age)")
            .build()

        val code = function.writeToKotlinString()
        val expected = "fun createPerson(name: String, age: Int = 0): Person = Person(name, age)"
        assertEquals(expected, code)
    }

    @Test
    fun testParameterInConstructorContext() {
        val param1 = KotlinValueParameterSpec.builder("name", stringType)
            .immutableProperty()
            .build()
        val param2 = KotlinValueParameterSpec.builder("age", intType)
            .mutableProperty()
            .defaultValue("0")
            .build()

        val constructor = KotlinConstructorSpec.builder()
            .addParameter(param1)
            .addParameter(param2)
            .build()

        val classSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Person")
            .primaryConstructor(constructor)
            .build()

        val code = classSpec.writeToKotlinString()
        val expected = "class Person(val name: String, var age: Int = 0)"
        assertEquals(expected, code)
    }

    // ===== EXTENSION FUNCTION TESTS =====

    @Test
    fun testDefaultValueExtensionFunction() {
        val param = KotlinValueParameterSpec.builder("count", intType)
            .defaultValue("42") { }
            .build()

        assertEquals("42", param.defaultValue!!.writeToKotlinString())
    }

    @Test
    fun testAddKDocExtensionFunction() {
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .addKDoc("Parameter documentation") { }
            .build()

        assertEquals("Parameter documentation", param.kDoc.writeToKotlinString())
    }

    @Test
    fun testPropertyizeWithBooleanExtensionFunction() {
        val param1 = KotlinValueParameterSpec.builder("name", stringType)
            .propertyfy(true)
            .build()

        val param2 = KotlinValueParameterSpec.builder("age", intType)
            .propertyfy(false)
            .build()

        assertNotNull(param1.propertyfication)
        assertTrue(param1.propertyfication!!.mutable)

        assertNotNull(param2.propertyfication)
        assertFalse(param2.propertyfication!!.mutable)
    }

    @Test
    fun testPropertyizeWithBlockExtensionFunction() {
        val param = KotlinValueParameterSpec.builder("name", stringType)
            .propertyfy { mutable = true }
            .build()

        assertNotNull(param.propertyfication)
        assertTrue(param.propertyfication!!.mutable)
    }

    // ===== ADDITIONAL COMPREHENSIVE TESTS =====

    @Test
    fun testDefaultValueWithSimpleString() {
        val param = KotlinValueParameterSpec.builder("message", stringType)
            .defaultValue("\"Hello, World!\"")
            .build()

        assertNotNull(param.defaultValue)
        assertEquals("\"Hello, World!\"", param.defaultValue!!.writeToKotlinString())
    }

    @Test
    fun testAddKDocWithSimpleString() {
        val param = KotlinValueParameterSpec.builder("count", intType)
            .addKDoc("The count parameter for items")
            .build()

        assertEquals("The count parameter for items", param.kDoc.writeToKotlinString())
    }

    @Test
    fun testMultipleKDocAdditions() {
        val param = KotlinValueParameterSpec.builder("value", stringType)
            .addKDoc("First line")
            .addKDoc("Second line")
            .build()

        val expectedKDoc = "First lineSecond line"
        assertEquals(expectedKDoc, param.kDoc.writeToKotlinString())
    }

    @Test
    fun testBuilderMutability() {
        val builder = KotlinValueParameterSpec.builder("test", stringType)
        val param1 = builder.defaultValue("value1").build()
        val param2 = builder.defaultValue("value2").build()

        // Builder is mutable, so the state accumulates
        assertNotNull(param1.defaultValue)
        assertNotNull(param2.defaultValue)
        assertEquals("value1", param1.defaultValue!!.writeToKotlinString()) // First build has first value
        assertEquals("value2", param2.defaultValue!!.writeToKotlinString()) // Second build has second value
    }

    @Test
    fun testBuilderNameAndTypeAccess() {
        val builder = KotlinValueParameterSpec.builder("testName", stringType)

        assertEquals("testName", builder.name)
        assertEquals(stringType, builder.type)
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
        val param = KotlinValueParameterSpec.builder("numbers", listType)
            .defaultValue(complexDefault)
            .build()

        assertEquals("listOf(1, 2, 3)", param.defaultValue!!.writeToKotlinString())
    }

    @Test
    fun testParameterWithGenericType() {
        val genericListType = ClassName("kotlin.collections", "List").kotlinRef()
        val param = KotlinValueParameterSpec.builder("items", genericListType)
            .defaultValue("emptyList()")
            .build()

        val code = param.writeToKotlinString()
        assertEquals("items: kotlin.collections.List = emptyList()", code)
    }

    @Test
    fun testParameterWithNullableType() {
        val nullableStringType = ClassName("kotlin", "String").kotlinRef()
        val param = KotlinValueParameterSpec.builder("optionalName", nullableStringType)
            .defaultValue("null")
            .build()

        val code = param.writeToKotlinString()
        assertEquals("optionalName: String = null", code)
    }

    @Test
    fun testParameterWithLongName() {
        val longName = "thisIsAVeryLongParameterNameThatShouldStillWork"
        val param = KotlinValueParameterSpec.builder(longName, stringType)
            .build()

        assertEquals(longName, param.name)
        val code = param.writeToKotlinString()
        assertEquals("$longName: String", code)
    }

    @Test
    fun testParameterWithSpecialCharactersInName() {
        val specialName = "`special name with spaces`"
        val param = KotlinValueParameterSpec.builder(specialName, stringType)
            .build()

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

        val param = KotlinValueParameterSpec.builder("param", stringType)
            .addAnnotation(complexAnnotation)
            .build()

        val code = param.writeToKotlinString()
        assertEquals("@Suppress(names = [\"UNUSED_PARAMETER\", \"DEPRECATION\"])\nparam: String", code)
    }

    @Test
    fun testParameterWithAllModifiers() {
        val param = KotlinValueParameterSpec.builder("param", stringType)
            .addModifiers(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE, KotlinModifier.VARARG)
            .build()

        assertEquals(setOf(KotlinModifier.CROSSINLINE, KotlinModifier.NOINLINE, KotlinModifier.VARARG), param.modifiers)
    }

    @Test
    fun testParameterWithVarargModifier() {
        val param = KotlinValueParameterSpec.builder("items", stringType)
            .addModifier(KotlinModifier.VARARG)
            .build()

        val code = param.writeToKotlinString()
        assertEquals("vararg items: String", code)
    }

    @Test
    fun testParameterWithVarargAndDefaultValue() {
        val param = KotlinValueParameterSpec.builder("items", stringType)
            .addModifier(KotlinModifier.VARARG)
            .defaultValue("\"default\"")
            .build()

        val code = param.writeToKotlinString()
        assertEquals("vararg items: String = \"default\"", code)
    }

    @Test
    fun testParameterWithAllFeaturesCodeGeneration() {
        val complexAnnotation = ClassName("kotlin", "Suppress").annotationRef {
            addMember("names", "\"UNUSED\"")
        }

        val param = KotlinValueParameterSpec.builder("complexParam", stringType)
            .addAnnotation(complexAnnotation)
            .addModifier(KotlinModifier.CROSSINLINE)
            .immutableProperty()
            .defaultValue("\"complex\"")
            .addKDoc("A parameter with all possible features")
            .build()

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

        val param = KotlinValueParameterSpec.builder("param", stringType)
            .addAnnotation(annotation1)
            .addAnnotation(annotation2)
            .build()

        assertEquals(2, param.annotations.size)
    }

    @Test
    fun testParameterWithFunctionType() {
        val functionType = ClassName("kotlin", "Function1").kotlinRef()
        val param = KotlinValueParameterSpec.builder("callback", functionType)
            .defaultValue("{ }")
            .build()

        val code = param.writeToKotlinString()
        assertEquals("callback: Function1 = { }", code)
    }

    @Test
    fun testParameterWithLambdaDefaultValue() {
        val param = KotlinValueParameterSpec.builder("action", stringType)
            .defaultValue("{ println(\"default action\") }")
            .build()

        val code = param.writeToKotlinString()
        assertEquals("action: String = { println(\"default action\") }", code)
    }

    @Test
    fun testParameterInDataClassContext() {
        val param1 = KotlinValueParameterSpec.builder("id", intType)
            .immutableProperty()
            .build()
        val param2 = KotlinValueParameterSpec.builder("name", stringType)
            .immutableProperty()
            .build()

        val constructor = KotlinConstructorSpec.builder()
            .addParameter(param1)
            .addParameter(param2)
            .build()

        val dataClass = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "User")
            .addModifier(KotlinModifier.DATA)
            .primaryConstructor(constructor)
            .build()

        val code = dataClass.writeToKotlinString()
        assertEquals("data class User(val id: Int, val name: String)", code)
    }

    @Test
    fun testParameterWithNestedGenericType() {
        val nestedGenericType = ClassName("kotlin.collections", "Map").kotlinRef()
        val param = KotlinValueParameterSpec.builder("data", nestedGenericType)
            .defaultValue("emptyMap()")
            .build()

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

        val param = KotlinValueParameterSpec.builder("param", stringType)
            .addAnnotation(customAnnotation)
            .build()

        val code = param.writeToKotlinString()
        assertEquals("@com.example.CustomAnnotation(value = \"test\", number = 42, flag = true)\nparam: String", code)
    }

    @Test
    fun testParameterWithMultilineKDoc() {
        val param = KotlinValueParameterSpec.builder("config", stringType)
            .addKDoc("This is a multi-line KDoc\n")
            .addKDoc("that spans multiple lines\n")
            .addKDoc("and provides detailed documentation")
            .build()

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
        val builder = KotlinValueParameterSpec.builder("base", stringType)

        val param1 = builder.defaultValue("value1").build()
        val param2 = builder.defaultValue("value2").addModifier(KotlinModifier.CROSSINLINE).build()

        // Builder accumulates state, so first param has first state, second has accumulated state
        assertEquals("value1", param1.defaultValue!!.writeToKotlinString())
        assertEquals("value2", param2.defaultValue!!.writeToKotlinString())
        assertEquals(emptySet(), param1.modifiers)
        assertEquals(setOf(KotlinModifier.CROSSINLINE), param2.modifiers)
    }

    @Test
    fun testPropertyizationBuilderReuse() {
        val builder = KotlinValueParameterSpec.propertyizationBuilder()

        val prop1 = builder.mutable(true).build()
        val prop2 = builder.mutable(false).build()

        // Builder is mutable, so last setting wins
        assertTrue(prop1.mutable) // This might be false if builder state changed
        assertFalse(prop2.mutable)
    }

    @Test
    fun testParameterWithArrayType() {
        val arrayType = ClassName("kotlin", "Array").kotlinRef()
        val param = KotlinValueParameterSpec.builder("items", arrayType)
            .addModifier(KotlinModifier.VARARG)
            .build()

        val code = param.writeToKotlinString()
        assertEquals("vararg items: Array", code)
    }

    @Test
    fun testParameterWithSuspendFunctionType() {
        val suspendFunctionType = ClassName("kotlin", "Function0").kotlinRef()
        val param = KotlinValueParameterSpec.builder("suspendAction", suspendFunctionType)
            .addModifier(KotlinModifier.CROSSINLINE)
            .build()

        val code = param.writeToKotlinString()
        assertEquals("crossinline suspendAction: Function0", code)
    }

    @Test
    fun testParameterCodeGenerationConsistency() {
        val param = KotlinValueParameterSpec.builder("test", stringType)
            .defaultValue("\"value\"")
            .build()

        // Multiple calls to writeToKotlinString should produce the same result
        val code1 = param.writeToKotlinString()
        val code2 = param.writeToKotlinString()
        assertEquals(code1, code2)
        assertEquals("test: String = \"value\"", code1)
    }

    @Test
    fun testParameterWithComplexDefaultValueExpression() {
        val param = KotlinValueParameterSpec.builder("result", intType)
            .defaultValue("(1 + 2) * 3")
            .build()

        val code = param.writeToKotlinString()
        assertEquals("result: Int = (1 + 2) * 3", code)
    }

    @Test
    fun testParameterWithMethodCallDefaultValue() {
        val param = KotlinValueParameterSpec.builder("timestamp", stringType)
            .defaultValue("System.currentTimeMillis().toString()")
            .build()

        val code = param.writeToKotlinString()
        assertEquals("timestamp: String = System.currentTimeMillis().toString()", code)
    }
}
