package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.*

/**
 * Comprehensive tests for [KotlinFunctionSpec].
 */
class KotlinFunctionSpecTests {

    private val unitType = ClassName("kotlin", "Unit").kotlinRef()
    private val stringType = ClassName("kotlin", "String").kotlinRef()
    private val intType = ClassName("kotlin", "Int").kotlinRef()
    private val booleanType = ClassName("kotlin", "Boolean").kotlinRef()

    @Test
    fun testBasicFunctionCreation() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .build()

        assertNotNull(functionSpec)
        assertEquals("testFunction", functionSpec.name)
        assertEquals(unitType, functionSpec.returnType)
        assertEquals(emptySet<KotlinModifier>(), functionSpec.modifiers)
        assertEquals(emptyList<KotlinValueParameterSpec>(), functionSpec.parameters)
        assertEquals(emptyList(), functionSpec.typeVariables)
        assertNull(functionSpec.receiver)
        assertEquals(emptyList(), functionSpec.contextParameters)
    }

    @Test
    fun testFunctionWithDefaultReturnType() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction")
            .build()

        assertEquals(unitType, functionSpec.returnType)
    }

    @Test
    fun testFunctionWithCustomReturnType() {
        val functionSpec = KotlinFunctionSpec.builder("getString", stringType)
            .build()

        assertEquals(stringType, functionSpec.returnType)
    }

    @Test
    fun testFunctionWithModifiers() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addModifier(KotlinModifier.PUBLIC)
            .addModifier(KotlinModifier.SUSPEND)
            .build()

        assertEquals(setOf(KotlinModifier.PUBLIC, KotlinModifier.SUSPEND), functionSpec.modifiers)
    }

    @Test
    fun testFunctionWithMultipleModifiers() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addModifiers(KotlinModifier.PRIVATE, KotlinModifier.INLINE)
            .build()

        assertEquals(setOf(KotlinModifier.PRIVATE, KotlinModifier.INLINE), functionSpec.modifiers)
    }

    @Test
    fun testFunctionWithModifiersIterable() {
        val modifiers = listOf(KotlinModifier.INTERNAL, KotlinModifier.SUSPEND)
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addModifiers(modifiers)
            .build()

        assertEquals(setOf(KotlinModifier.INTERNAL, KotlinModifier.SUSPEND), functionSpec.modifiers)
    }

    @Test
    fun testFunctionWithParameters() {
        val param1 = KotlinValueParameterSpec.builder("param1", stringType).build()
        val param2 = KotlinValueParameterSpec.builder("param2", intType).build()

        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addParameter(param1)
            .addParameter(param2)
            .build()

        assertEquals(2, functionSpec.parameters.size)
        assertEquals("param1", functionSpec.parameters[0].name)
        assertEquals("param2", functionSpec.parameters[1].name)
    }

    @Test
    fun testFunctionWithParametersVararg() {
        val param1 = KotlinValueParameterSpec.builder("param1", stringType).build()
        val param2 = KotlinValueParameterSpec.builder("param2", intType).build()

        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addParameters(param1, param2)
            .build()

        assertEquals(2, functionSpec.parameters.size)
    }

    @Test
    fun testFunctionWithParametersIterable() {
        val params = listOf(
            KotlinValueParameterSpec.builder("param1", stringType).build(),
            KotlinValueParameterSpec.builder("param2", intType).build()
        )

        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addParameters(params)
            .build()

        assertEquals(2, functionSpec.parameters.size)
    }

    @Test
    fun testFunctionWithParameterDSL() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addParameter("name", stringType) {
                defaultValue("\"default\"")
            }
            .build()

        assertEquals(1, functionSpec.parameters.size)
        assertEquals("name", functionSpec.parameters[0].name)
        assertEquals(stringType, functionSpec.parameters[0].typeRef)
        assertNotNull(functionSpec.parameters[0].defaultValue)
    }

    @Test
    fun testFunctionWithTypeVariables() {
        val typeVar1Name = TypeVariableName("T")
        val typeVar2Name = TypeVariableName("R")
        val typeVar1 = typeVar1Name.kotlinRef()
        val typeVar2 = typeVar2Name.kotlinRef()

        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addTypeVariable(typeVar1)
            .addTypeVariable(typeVar2)
            .build()

        assertEquals(2, functionSpec.typeVariables.size)
        assertEquals("T", typeVar1Name.name)
        assertEquals("R", typeVar2Name.name)
    }

    @Test
    fun testFunctionWithTypeVariablesVararg() {
        val typeVar1 = TypeVariableName("T").kotlinRef()
        val typeVar2 = TypeVariableName("R").kotlinRef()

        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addTypeVariables(typeVar1, typeVar2)
            .build()

        assertEquals(2, functionSpec.typeVariables.size)
    }

    @Test
    fun testFunctionWithTypeVariablesIterable() {
        val typeVars = listOf(
            TypeVariableName("T").kotlinRef(),
            TypeVariableName("R").kotlinRef()
        )

        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addTypeVariables(typeVars)
            .build()

        assertEquals(2, functionSpec.typeVariables.size)
    }

    @Test
    fun testFunctionWithReceiverType() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .receiver(stringType)
            .build()

        assertEquals(stringType, functionSpec.receiver)
    }

    @Test
    fun testFunctionWithContextParameters() {
        val contextParam1 = KotlinContextParameterSpec.builder("context1", stringType).build()
        val contextParam2 = KotlinContextParameterSpec.builder("context2", intType).build()

        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addContextParameter(contextParam1)
            .addContextParameter(contextParam2)
            .build()

        assertEquals(2, functionSpec.contextParameters.size)
        assertEquals("context1", functionSpec.contextParameters[0].name)
        assertEquals("context2", functionSpec.contextParameters[1].name)
    }

    @Test
    fun testFunctionWithContextParametersVararg() {
        val contextParam1 = KotlinContextParameterSpec.builder("context1", stringType).build()
        val contextParam2 = KotlinContextParameterSpec.builder("context2", intType).build()

        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addContextParameters(contextParam1, contextParam2)
            .build()

        assertEquals(2, functionSpec.contextParameters.size)
    }

    @Test
    fun testFunctionWithContextParametersIterable() {
        val contextParams = listOf(
            KotlinContextParameterSpec.builder("context1", stringType).build(),
            KotlinContextParameterSpec.builder("context2", intType).build()
        )

        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addContextParameters(contextParams)
            .build()

        assertEquals(2, functionSpec.contextParameters.size)
    }

    @Test
    fun testFunctionWithContextParameterDSL() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addContextParameter("logger", stringType) {
                // Context parameter configuration
            }
            .build()

        assertEquals(1, functionSpec.contextParameters.size)
        assertEquals("logger", functionSpec.contextParameters[0].name)
    }

    @Test
    fun testFunctionWithKDoc() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addKDoc("This is a test function.")
            .build()

        assertNotNull(functionSpec.kDoc)
        assertEquals("This is a test function.", functionSpec.kDoc.writeToKotlinString())
    }

    @Test
    fun testFunctionWithKDocCodeValue() {
        val kDocValue = CodeValue("This is a test function with CodeValue.")
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addKDoc(kDocValue)
            .build()

        assertEquals("This is a test function with CodeValue.", functionSpec.kDoc.writeToKotlinString())
    }

    @Test
    fun testFunctionWithCode() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addCode("println(\"Hello World\")")
            .build()

        assertNotNull(functionSpec.code)
        assertEquals("println(\"Hello World\")", functionSpec.code.writeToKotlinString())
    }

    @Test
    fun testFunctionWithCodeValue() {
        val codeValue = CodeValue("println(\"Hello from CodeValue\")")
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addCode(codeValue)
            .build()

        assertEquals("println(\"Hello from CodeValue\")", functionSpec.code.writeToKotlinString())
    }

    @Test
    fun testFunctionWithStatement() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addStatement("val x = 42")
            .build()

        assertEquals("val x = 42\n", functionSpec.code.writeToKotlinString())
    }

    @Test
    fun testFunctionWithStatementCodeValue() {
        val statementValue = CodeValue("val y = 24")
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addStatement(statementValue)
            .build()

        assertEquals("val y = 24\n", functionSpec.code.writeToKotlinString())
    }

    @Test
    fun testFunctionWithMultipleStatements() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addStatement("val x = 1")
            .addStatement("val y = 2")
            .addStatement("println(x + y)")
            .build()

        val expected = "val x = 1\nval y = 2\nprintln(x + y)\n"
        assertEquals(expected, functionSpec.code.writeToKotlinString())
    }

    @Test
    fun testFunctionWithAnnotations() {
        val deprecatedAnnotation = ClassName("kotlin", "Deprecated").annotationRef {
            addMember("message", "\"Use newFunction instead\"")
        }

        val functionSpec = KotlinFunctionSpec.builder("testFunction", unitType)
            .addAnnotationRef(deprecatedAnnotation)
            .build()

        assertEquals(1, functionSpec.annotations.size)
        assertEquals(listOf(deprecatedAnnotation), functionSpec.annotations)
    }

    @Test
    fun testFunctionDSLFactory() {
        val functionSpec = KotlinFunctionSpec("testFunction", stringType) {
            addModifier(KotlinModifier.PUBLIC)
            addParameter("input", intType)
            addStatement("return input.toString()")
        }

        assertEquals("testFunction", functionSpec.name)
        assertEquals(stringType, functionSpec.returnType)
        assertEquals(setOf(KotlinModifier.PUBLIC), functionSpec.modifiers)
        assertEquals(1, functionSpec.parameters.size)
        assertEquals("return input.toString()\n", functionSpec.code.writeToKotlinString())
    }

    @Test
    fun testComplexFunctionWithAllFeatures() {
        val typeVar = TypeVariableName("T").kotlinRef()
        val contextParam = KotlinContextParameterSpec.builder("logger", stringType).build()
        val param = KotlinValueParameterSpec.builder("input", typeVar)
            .defaultValue("null")
            .build()

        val functionSpec = KotlinFunctionSpec.builder("complexFunction", booleanType)
            .addModifier(KotlinModifier.SUSPEND)
            .addModifier(KotlinModifier.INLINE)
            .addTypeVariable(typeVar)
            .receiver(stringType)
            .addContextParameter(contextParam)
            .addParameter(param)
            .addKDoc("A complex function with all features.")
            .addStatement("logger.info(\"Processing input: \$input\")")
            .addStatement("return input != null")
            .build()

        assertEquals("complexFunction", functionSpec.name)
        assertEquals(booleanType, functionSpec.returnType)
        assertEquals(setOf(KotlinModifier.SUSPEND, KotlinModifier.INLINE), functionSpec.modifiers)
        assertEquals(1, functionSpec.typeVariables.size)
        assertEquals(stringType, functionSpec.receiver)
        assertEquals(1, functionSpec.contextParameters.size)
        assertEquals(1, functionSpec.parameters.size)
        assertNotNull(functionSpec.kDoc)
        assertNotNull(functionSpec.code)
    }

    @Test
    fun testFunctionBuilderChaining() {
        val builder = KotlinFunctionSpec.builder("chainedFunction", unitType)
            .addModifier(KotlinModifier.PUBLIC)
            .addParameter("param1", stringType)
            .addParameter("param2", intType)
            .addStatement("println(\"\$param1: \$param2\")")

        val functionSpec = builder.build()

        assertEquals("chainedFunction", functionSpec.name)
        assertEquals(setOf(KotlinModifier.PUBLIC), functionSpec.modifiers)
        assertEquals(2, functionSpec.parameters.size)
    }

    @Test
    fun testFunctionBuilderReuse() {
        val builder = KotlinFunctionSpec.builder("reusableFunction", unitType)
            .addModifier(KotlinModifier.PRIVATE)

        val function1 = builder.build()
        val function2 = builder.addParameter("newParam", stringType).build()

        assertEquals("reusableFunction", function1.name)
        assertEquals("reusableFunction", function2.name)
        assertEquals(0, function1.parameters.size)
        assertEquals(1, function2.parameters.size)
    }

    @Test
    fun testFunctionWithGenericReturnType() {
        val listType = ClassName("kotlin.collections", "List").kotlinRef()
        val functionSpec = KotlinFunctionSpec.builder("getList", listType)
            .addStatement("return emptyList()")
            .build()

        assertEquals(listType, functionSpec.returnType)
    }

    @Test
    fun testFunctionWithLambdaParameter() {
        val lambdaType = ClassName("kotlin", "Function1").kotlinRef()
        val functionSpec = KotlinFunctionSpec.builder("withLambda", unitType)
            .addParameter("callback", lambdaType)
            .addStatement("callback.invoke()")
            .build()

        assertEquals(1, functionSpec.parameters.size)
        assertEquals("callback", functionSpec.parameters[0].name)
    }

    @Test
    fun testFunctionWithSuspendModifier() {
        val functionSpec = KotlinFunctionSpec.builder("suspendFunction", unitType)
            .addModifier(KotlinModifier.SUSPEND)
            .addStatement("delay(1000)")
            .build()

        assertTrue(functionSpec.modifiers.contains(KotlinModifier.SUSPEND))
    }

    @Test
    fun testFunctionWithInlineModifier() {
        val functionSpec = KotlinFunctionSpec.builder("inlineFunction", unitType)
            .addModifier(KotlinModifier.INLINE)
            .addStatement("// inline implementation")
            .build()

        assertTrue(functionSpec.modifiers.contains(KotlinModifier.INLINE))
    }

    @Test
    fun testFunctionCodeGenerationConsistency() {
        val functionSpec = KotlinFunctionSpec.builder("testFunction", stringType)
            .addParameter("input", intType)
            .addStatement("return input.toString()")
            .build()

        val code1 = functionSpec.writeToKotlinString()
        val code2 = functionSpec.writeToKotlinString()

        assertEquals(code1, code2, "Code generation should be consistent")
    }

    @Test
    fun testFunctionWithComplexTypeVariables() {
        val typeVar1 = TypeVariableName("T").kotlinRef()
        val typeVar2 = TypeVariableName("R").kotlinRef()

        val functionSpec = KotlinFunctionSpec.builder("transform", typeVar2)
            .addTypeVariable(typeVar1)
            .addTypeVariable(typeVar2)
            .addParameter("input", typeVar1)
            .addParameter("transformer", ClassName("kotlin", "Function1").kotlinRef())
            .addStatement("return transformer(input)")
            .build()

        assertEquals(2, functionSpec.typeVariables.size)
        assertEquals(typeVar2, functionSpec.returnType)
    }

    @Test
    fun testFunctionWithVarargParameter() {
        val functionSpec = KotlinFunctionSpec.builder("processItems", unitType)
            .addParameter("items", stringType) {
                addModifier(KotlinModifier.VARARG)
            }
            .addStatement("items.forEach { println(it) }")
            .build()

        assertEquals(1, functionSpec.parameters.size)
        assertTrue(functionSpec.parameters[0].modifiers.contains(KotlinModifier.VARARG))
    }

    @Test
    fun testFunctionWithDefaultParameterValues() {
        val functionSpec = KotlinFunctionSpec.builder("greet", unitType)
            .addParameter("name", stringType) {
                defaultValue("\"World\"")
            }
            .addParameter("times", intType) {
                defaultValue("1")
            }
            .addStatement("repeat(times) { println(\"Hello, \$name!\") }")
            .build()

        assertEquals(2, functionSpec.parameters.size)
        assertNotNull(functionSpec.parameters[0].defaultValue)
        assertNotNull(functionSpec.parameters[1].defaultValue)
    }

    @Test
    fun testFunctionCodeGeneration() {
        val functionSpec = KotlinFunctionSpec.builder("simpleFunction", stringType)
            .addParameter("input", intType)
            .addStatement("return \"Value: \$input\"")
            .build()

        val generatedCode = functionSpec.writeToKotlinString()
        val expectedCode = """fun simpleFunction(input: Int): String {
    return "Value: ${'$'}input"

}"""

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testFunctionWithReceiverCodeGeneration() {
        val functionSpec = KotlinFunctionSpec.builder("extension", unitType)
            .receiver(stringType)
            .addStatement("println(this)")
            .build()

        val generatedCode = functionSpec.writeToKotlinString()
        val expectedCode = """fun String.extension(): Unit {
    println(this)

}"""

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testFunctionWithModifiersCodeGeneration() {
        val functionSpec = KotlinFunctionSpec.builder("suspendInlineFunction", unitType)
            .addModifier(KotlinModifier.SUSPEND)
            .addModifier(KotlinModifier.INLINE)
            .addStatement("// implementation")
            .build()

        val generatedCode = functionSpec.writeToKotlinString()
        val expectedCode = """suspend inline fun suspendInlineFunction(): Unit {
    // implementation

}"""

        assertEquals(expectedCode, generatedCode.trim())
    }
}
