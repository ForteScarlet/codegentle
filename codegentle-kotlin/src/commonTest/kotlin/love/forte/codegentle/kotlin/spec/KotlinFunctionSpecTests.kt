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
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.*

/**
 * Comprehensive tests for [KotlinFunctionSpec].
 */
class KotlinFunctionSpecTests {

    private val unitType = ClassName("kotlin", "Unit").ref()
    private val stringType = ClassName("kotlin", "String").ref()
    private val intType = ClassName("kotlin", "Int").ref()
    private val booleanType = ClassName("kotlin", "Boolean").ref()

    @Test
    fun testBasicFunctionCreation() {
        val functionSpec = KotlinFunctionSpec("testFunction", unitType)

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
        val functionSpec = KotlinFunctionSpec("testFunction")

        assertEquals(unitType, functionSpec.returnType)
    }

    @Test
    fun testFunctionWithCustomReturnType() {
        val functionSpec = KotlinFunctionSpec("getString", stringType)

        assertEquals(stringType, functionSpec.returnType)
    }

    @Test
    fun testFunctionWithModifiers() {
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addModifier(KotlinModifier.PUBLIC)
            addModifier(KotlinModifier.SUSPEND)
        }

        assertEquals(setOf(KotlinModifier.PUBLIC, KotlinModifier.SUSPEND), functionSpec.modifiers)
    }

    @Test
    fun testFunctionWithMultipleModifiers() {
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addModifiers(KotlinModifier.PRIVATE, KotlinModifier.INLINE)
        }

        assertEquals(setOf(KotlinModifier.PRIVATE, KotlinModifier.INLINE), functionSpec.modifiers)
    }

    @Test
    fun testFunctionWithModifiersIterable() {
        val modifiers = listOf(KotlinModifier.INTERNAL, KotlinModifier.SUSPEND)
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addModifiers(modifiers)
        }

        assertEquals(setOf(KotlinModifier.INTERNAL, KotlinModifier.SUSPEND), functionSpec.modifiers)
    }

    @Test
    fun testFunctionWithParameters() {
        val param1 = KotlinValueParameterSpec("param1", stringType)
        val param2 = KotlinValueParameterSpec("param2", intType)

        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addParameter(param1)
            addParameter(param2)
        }

        assertEquals(2, functionSpec.parameters.size)
        assertEquals("param1", functionSpec.parameters[0].name)
        assertEquals("param2", functionSpec.parameters[1].name)
    }

    @Test
    fun testFunctionWithParametersVararg() {
        val param1 = KotlinValueParameterSpec("param1", stringType)
        val param2 = KotlinValueParameterSpec("param2", intType)

        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addParameters(param1, param2)
        }

        assertEquals(2, functionSpec.parameters.size)
    }

    @Test
    fun testFunctionWithParametersIterable() {
        val params = listOf(
            KotlinValueParameterSpec("param1", stringType),
            KotlinValueParameterSpec("param2", intType)
        )

        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addParameters(params)
        }

        assertEquals(2, functionSpec.parameters.size)
    }

    @Test
    fun testFunctionWithParameterDSL() {
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addParameter("name", stringType) {
                defaultValue("\"default\"")
            }
        }

        assertEquals(1, functionSpec.parameters.size)
        assertEquals("name", functionSpec.parameters[0].name)
        assertEquals(stringType, functionSpec.parameters[0].typeRef)
        assertNotNull(functionSpec.parameters[0].defaultValue)
    }

    @Test
    fun testFunctionWithTypeVariables() {
        val typeVar1Name = TypeVariableName("T")
        val typeVar2Name = TypeVariableName("R")
        val typeVar1 = typeVar1Name.ref()
        val typeVar2 = typeVar2Name.ref()

        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addTypeVariable(typeVar1)
            addTypeVariable(typeVar2)
        }

        assertEquals(2, functionSpec.typeVariables.size)
        assertEquals("T", typeVar1Name.name)
        assertEquals("R", typeVar2Name.name)
    }

    @Test
    fun testFunctionWithTypeVariablesVararg() {
        val typeVar1 = TypeVariableName("T").ref()
        val typeVar2 = TypeVariableName("R").ref()

        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addTypeVariables(typeVar1, typeVar2)
        }

        assertEquals(2, functionSpec.typeVariables.size)
    }

    @Test
    fun testFunctionWithTypeVariablesIterable() {
        val typeVars = listOf(
            TypeVariableName("T").ref(),
            TypeVariableName("R").ref()
        )

        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addTypeVariables(typeVars)
        }

        assertEquals(2, functionSpec.typeVariables.size)
    }

    @Test
    fun testFunctionWithReceiverType() {
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            receiver(stringType)
        }

        assertEquals(stringType, functionSpec.receiver)
    }

    @Test
    fun testFunctionWithContextParameters() {
        val contextParam1 = KotlinContextParameterSpec("context1", stringType)
        val contextParam2 = KotlinContextParameterSpec("context2", intType)

        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addContextParameter(contextParam1)
            addContextParameter(contextParam2)
        }

        assertEquals(2, functionSpec.contextParameters.size)
        assertEquals("context1", functionSpec.contextParameters[0].name)
        assertEquals("context2", functionSpec.contextParameters[1].name)
    }

    @Test
    fun testFunctionWithContextParametersVararg() {
        val contextParam1 = KotlinContextParameterSpec("context1", stringType)
        val contextParam2 = KotlinContextParameterSpec("context2", intType)

        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addContextParameters(contextParam1, contextParam2)
        }

        assertEquals(2, functionSpec.contextParameters.size)
    }

    @Test
    fun testFunctionWithContextParametersIterable() {
        val contextParams = listOf(
            KotlinContextParameterSpec("context1", stringType),
            KotlinContextParameterSpec("context2", intType)
        )

        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addContextParameters(contextParams)
        }

        assertEquals(2, functionSpec.contextParameters.size)
    }

    @Test
    fun testFunctionWithContextParameterDSL() {
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addContextParameter("logger", stringType) {
                // Context parameter configuration
            }
        }

        assertEquals(1, functionSpec.contextParameters.size)
        assertEquals("logger", functionSpec.contextParameters[0].name)
    }

    @Test
    fun testFunctionWithKDoc() {
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addDoc("This is a test function.")
        }

        assertNotNull(functionSpec.kDoc)
        assertEquals("This is a test function.", functionSpec.kDoc.writeToKotlinString())
    }

    @Test
    fun testFunctionWithKDocCodeValue() {
        val kDocValue = CodeValue("This is a test function with CodeValue.")
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addDoc(kDocValue)
        }

        assertEquals("This is a test function with CodeValue.", functionSpec.kDoc.writeToKotlinString())
    }

    @Test
    fun testFunctionWithCode() {
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addCode("println(\"Hello World\")")
        }

        assertNotNull(functionSpec.code)
        assertEquals("println(\"Hello World\")", functionSpec.code.writeToKotlinString())
    }

    @Test
    fun testFunctionWithCodeValue() {
        val codeValue = CodeValue("println(\"Hello from CodeValue\")")
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addCode(codeValue)
        }

        assertEquals("println(\"Hello from CodeValue\")", functionSpec.code.writeToKotlinString())
    }

    @Test
    fun testFunctionWithStatement() {
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addStatement("val x = 42")
        }

        assertEquals("val x = 42\n", functionSpec.code.writeToKotlinString())
    }

    @Test
    fun testFunctionWithStatementCodeValue() {
        val statementValue = CodeValue("val y = 24")
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addStatement(statementValue)
        }

        assertEquals("val y = 24\n", functionSpec.code.writeToKotlinString())
    }

    @Test
    fun testFunctionWithMultipleStatements() {
        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addStatement("val x = 1")
            addStatement("val y = 2")
            addStatement("println(x + y)")
        }

        val expected = "val x = 1\nval y = 2\nprintln(x + y)\n"
        assertEquals(expected, functionSpec.code.writeToKotlinString())
    }

    @Test
    fun testFunctionWithAnnotations() {
        val deprecatedAnnotation = ClassName("kotlin", "Deprecated").annotationRef {
            addMember("message", "\"Use newFunction instead\"")
        }

        val functionSpec = KotlinFunctionSpec("testFunction", unitType) {
            addAnnotation(deprecatedAnnotation)
        }

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
        val typeVar = TypeVariableName("T").ref()
        val contextParam = KotlinContextParameterSpec("logger", stringType)
        val param = KotlinValueParameterSpec("input", typeVar) {
            defaultValue("null")
        }

        val functionSpec = KotlinFunctionSpec("complexFunction", booleanType) {
            addModifier(KotlinModifier.SUSPEND)
            addModifier(KotlinModifier.INLINE)
            addTypeVariable(typeVar)
            receiver(stringType)
            addContextParameter(contextParam)
            addParameter(param)
            addDoc("A complex function with all features.")
            addStatement("logger.info(\"Processing input: \$input\")")
            addStatement("return input != null")
        }

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
        val functionSpec = KotlinFunctionSpec("chainedFunction", unitType) {
            addModifier(KotlinModifier.PUBLIC)
            addParameter("param1", stringType)
            addParameter("param2", intType)
            addStatement("println(\"\$param1: \$param2\")")
        }

        assertEquals("chainedFunction", functionSpec.name)
        assertEquals(setOf(KotlinModifier.PUBLIC), functionSpec.modifiers)
        assertEquals(2, functionSpec.parameters.size)
    }

    @Test
    fun testFunctionBuilderReuse() {
        val function1 = KotlinFunctionSpec("reusableFunction", unitType) {
            addModifier(KotlinModifier.PRIVATE)
        }
        val function2 = KotlinFunctionSpec("reusableFunction", unitType) {
            addModifier(KotlinModifier.PRIVATE)
            addParameter("newParam", stringType)
        }

        assertEquals("reusableFunction", function1.name)
        assertEquals("reusableFunction", function2.name)
        assertEquals(0, function1.parameters.size)
        assertEquals(1, function2.parameters.size)
    }

    @Test
    fun testFunctionWithGenericReturnType() {
        val listType = ClassName("kotlin.collections", "List").ref()
        val functionSpec = KotlinFunctionSpec("getList", listType) {
            addStatement("return emptyList()")
        }

        assertEquals(listType, functionSpec.returnType)
    }

    @Test
    fun testFunctionWithLambdaParameter() {
        val lambdaType = ClassName("kotlin", "Function1").ref()
        val functionSpec = KotlinFunctionSpec("withLambda", unitType) {
            addParameter("callback", lambdaType)
            addStatement("callback.invoke()")
        }

        assertEquals(1, functionSpec.parameters.size)
        assertEquals("callback", functionSpec.parameters[0].name)
    }

    @Test
    fun testFunctionWithSuspendModifier() {
        val functionSpec = KotlinFunctionSpec("suspendFunction", unitType) {
            addModifier(KotlinModifier.SUSPEND)
            addStatement("delay(1000)")
        }

        assertTrue(functionSpec.modifiers.contains(KotlinModifier.SUSPEND))
    }

    @Test
    fun testFunctionWithInlineModifier() {
        val functionSpec = KotlinFunctionSpec("inlineFunction", unitType) {
            addModifier(KotlinModifier.INLINE)
            addStatement("// inline implementation")
        }

        assertTrue(functionSpec.modifiers.contains(KotlinModifier.INLINE))
    }

    @Test
    fun testFunctionCodeGenerationConsistency() {
        val functionSpec = KotlinFunctionSpec("testFunction", stringType) {
            addParameter("input", intType)
            addStatement("return input.toString()")
        }

        val code1 = functionSpec.writeToKotlinString()
        val code2 = functionSpec.writeToKotlinString()

        assertEquals(code1, code2, "Code generation should be consistent")
    }

    @Test
    fun testFunctionWithComplexTypeVariables() {
        val typeVar1 = TypeVariableName("T").ref()
        val typeVar2 = TypeVariableName("R").ref()

        val functionSpec = KotlinFunctionSpec("transform", typeVar2) {
            addTypeVariable(typeVar1)
            addTypeVariable(typeVar2)
            addParameter("input", typeVar1)
            addParameter("transformer", ClassName("kotlin", "Function1").ref())
            addStatement("return transformer(input)")
        }

        assertEquals(2, functionSpec.typeVariables.size)
        assertEquals(typeVar2, functionSpec.returnType)
    }

    @Test
    fun testFunctionWithVarargParameter() {
        val functionSpec = KotlinFunctionSpec("processItems", unitType) {
            addParameter("items", stringType) {
                addModifier(KotlinModifier.VARARG)
            }
            addStatement("items.forEach { println(it) }")
        }

        assertEquals(1, functionSpec.parameters.size)
        assertTrue(functionSpec.parameters[0].modifiers.contains(KotlinModifier.VARARG))
    }

    @Test
    fun testFunctionWithDefaultParameterValues() {
        val functionSpec = KotlinFunctionSpec("greet", unitType) {
            addParameter("name", stringType) {
                defaultValue("\"World\"")
            }
            addParameter("times", intType) {
                defaultValue("1")
            }
            addStatement("repeat(times) { println(\"Hello, \$name!\") }")
        }

        assertEquals(2, functionSpec.parameters.size)
        assertNotNull(functionSpec.parameters[0].defaultValue)
        assertNotNull(functionSpec.parameters[1].defaultValue)
    }

    @Test
    fun testFunctionCodeGeneration() {
        val functionSpec = KotlinFunctionSpec("simpleFunction", stringType) {
            addParameter("input", intType)
            addStatement("return \"Value: \$input\"")
        }

        val generatedCode = functionSpec.writeToKotlinString()
        val expectedCode = """fun simpleFunction(input: Int): String {
    return "Value: ${'$'}input"
}"""

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testFunctionWithReceiverCodeGeneration() {
        val functionSpec = KotlinFunctionSpec("extension", unitType) {
            receiver(stringType)
            addStatement("println(this)")
        }

        val generatedCode = functionSpec.writeToKotlinString()
        val expectedCode = """fun String.extension() {
    println(this)
}"""

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testFunctionWithModifiersCodeGeneration() {
        val functionSpec = KotlinFunctionSpec("suspendInlineFunction", unitType) {
            addModifier(KotlinModifier.SUSPEND)
            addModifier(KotlinModifier.INLINE)
            addStatement("// implementation")
        }

        val generatedCode = functionSpec.writeToKotlinString()
        val expectedCode = """
            |suspend inline fun suspendInlineFunction() {
            |    // implementation
            |}""".trimMargin()

        assertEquals(expectedCode, generatedCode.trim())
    }
}
