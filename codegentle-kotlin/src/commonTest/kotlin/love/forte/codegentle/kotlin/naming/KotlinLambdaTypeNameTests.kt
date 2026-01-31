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
package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.spec.addParameter
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class KotlinLambdaTypeNameTests {

    @Test
    fun testEmptyLambdaType() {
        val lambdaType = KotlinLambdaTypeName()
        val result = lambdaType.writeToKotlinString()
        assertEquals("() -> Unit", result)
    }

    @Test
    fun testSimpleLambdaType() {
        val stringType = ClassName("kotlin", "String").ref()
        val booleanType = ClassName("kotlin", "Boolean").ref()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter(stringType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("(String) -> Boolean", result.trim())
    }

    @Test
    fun testSimpleLambdaWithNameType() {
        val stringType = ClassName("kotlin", "String").ref()
        val booleanType = ClassName("kotlin", "Boolean").ref()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter("name", stringType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("(name: String) -> Boolean", result.trim())
    }

    @Test
    fun testSuspendLambdaType() {
        val stringType = ClassName("kotlin", "String").ref()
        val unitType = ClassName("kotlin", "Unit").ref()

        val lambdaType = KotlinLambdaTypeName(unitType) {
            suspend()
            addParameter("", stringType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("suspend (String) -> Unit", result.trim())
    }

    @Test
    fun testBuilderPattern() {
        val stringType = ClassName("kotlin", "String").ref()
        val booleanType = ClassName("kotlin", "Boolean").ref()

        val lambdaType = KotlinLambdaTypeName {
            suspend()
            returns(booleanType)
            addParameter("input", stringType)
        }

        assertTrue(lambdaType.isSuspend)
        assertEquals(1, lambdaType.parameters.size)
        assertEquals("input", lambdaType.parameters[0].name)
        assertEquals(booleanType, lambdaType.returnType)
    }

    @Test
    fun testNoParameterLambdaType() {
        val unitType = ClassName("kotlin", "Unit").ref()

        val lambdaType = KotlinLambdaTypeName(unitType)

        val result = lambdaType.writeToKotlinString()

        assertEquals("() -> Unit", result.trim())
    }

    @Test
    fun testExtensionLambdaType() {
        val stringType = KotlinClassNames.STRING.ref()
        val booleanType = KotlinClassNames.BOOLEAN.ref()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            receiver(stringType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("String.() -> Boolean", result.trim())
    }

    @Test
    fun testExtensionLambdaWithParameters() {
        val stringType = KotlinClassNames.STRING.ref()
        val intType = KotlinClassNames.INT.ref()
        val unitType = KotlinClassNames.UNIT.ref()

        val lambdaType = KotlinLambdaTypeName(unitType) {
            receiver(stringType)
            addParameter("index", intType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("String.(index: Int) -> Unit", result.trim())
    }

    @Test
    fun testSuspendExtensionLambdaType() {
        val stringType = KotlinClassNames.STRING.ref()
        val unitType = KotlinClassNames.UNIT.ref()

        val lambdaType = KotlinLambdaTypeName(unitType) {
            suspend()
            receiver(stringType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("suspend String.() -> Unit", result.trim())
    }

    @Test
    fun testContextReceiverLambdaType() {
        val loggerType = ClassName("kotlin.io", "Logger").ref()
        val stringType = KotlinClassNames.STRING.ref()
        val unitType = KotlinClassNames.UNIT.ref()

        val lambdaType = KotlinLambdaTypeName(unitType) {
            addContextReceiver(loggerType)
            addParameter("message", stringType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("context(kotlin.io.Logger) (message: String) -> Unit", result.trim())
    }

    @Test
    fun testMultipleContextReceiversLambdaType() {
        val loggerType = ClassName("kotlin.io", "Logger").ref()
        val contextType = ClassName("kotlin.coroutines", "CoroutineContext").ref()
        val stringType = KotlinClassNames.STRING.ref()
        val unitType = KotlinClassNames.UNIT.ref()

        val lambdaType = KotlinLambdaTypeName(unitType) {
            addContextReceivers(loggerType, contextType)
            addParameter("message", stringType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals(
            "context(kotlin.io.Logger, kotlin.coroutines.CoroutineContext) (message: String) -> Unit",
            result.trim()
        )
    }

    @Test
    fun testMultipleParametersLambdaType() {
        val stringType = KotlinClassNames.STRING.ref()
        val intType = KotlinClassNames.INT.ref()
        val booleanType = KotlinClassNames.BOOLEAN.ref()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter("name", stringType)
            addParameter("age", intType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("(name: String, age: Int) -> Boolean", result.trim())
    }

    @Test
    fun testUnnamedParametersLambdaType() {
        val stringType = KotlinClassNames.STRING.ref()
        val intType = KotlinClassNames.INT.ref()
        val booleanType = KotlinClassNames.BOOLEAN.ref()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter("", stringType)
            addParameter("", intType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("(String, Int) -> Boolean", result.trim())
    }

    @Test
    fun testMixedNamedAndUnnamedParametersLambdaType() {
        val stringType = KotlinClassNames.STRING.ref()
        val intType = KotlinClassNames.INT.ref()
        val booleanType = KotlinClassNames.BOOLEAN.ref()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter("name", stringType)
            addParameter("", intType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("(name: String, Int) -> Boolean", result.trim())
    }

    @Test
    fun testComplexCombinationLambdaType() {
        val loggerType = ClassName("kotlin.io", "Logger").ref()
        val stringType = KotlinClassNames.STRING.ref()
        val intType = KotlinClassNames.INT.ref()
        val unitType = KotlinClassNames.UNIT.ref()

        val lambdaType = KotlinLambdaTypeName(unitType) {
            addContextReceiver(loggerType)
            receiver(stringType)
            addParameter("count", intType)
            suspend()
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("suspend context(kotlin.io.Logger) String.(count: Int) -> Unit", result.trim())
    }

    @Test
    fun testLambdaWithGenericReturnType() {
        val stringType = KotlinClassNames.STRING.ref()
        val listType = KotlinClassNames.LIST.ref()

        val lambdaType = KotlinLambdaTypeName(listType) {
            addParameter("input", stringType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("(input: String) -> kotlin.collections.List", result.trim())
    }

    @Test
    fun testLambdaWithNothingReturnType() {
        val stringType = KotlinClassNames.STRING.ref()
        val nothingType = KotlinClassNames.NOTHING.ref()

        val lambdaType = KotlinLambdaTypeName(nothingType) {
            addParameter("message", stringType)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("(message: String) -> Nothing", result.trim())
    }

    @Test
    fun testBuilderWithoutReturnTypeThrowsException() {
        assertFailsWith<IllegalStateException> {
            KotlinLambdaTypeName.builder()
                .addParameter("input", KotlinClassNames.STRING.ref())
                .build()
        }
    }

    @Test
    fun testBuilderPropertiesAccess() {
        val stringType = KotlinClassNames.STRING.ref()
        val intType = KotlinClassNames.INT.ref()
        val booleanType = KotlinClassNames.BOOLEAN.ref()
        val loggerType = ClassName("kotlin.io", "Logger").ref()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            receiver(stringType)
            addContextReceiver(loggerType)
            addParameter("count", intType)
            suspend()
        }

        assertEquals(stringType, lambdaType.receiver)
        assertEquals(listOf(loggerType), lambdaType.contextReceivers)
        assertEquals(1, lambdaType.parameters.size)
        assertEquals("count", lambdaType.parameters[0].name)
        assertEquals(intType, lambdaType.parameters[0].typeRef)
        assertEquals(booleanType, lambdaType.returnType)
        assertTrue(lambdaType.isSuspend)
        assertTrue(KotlinModifier.SUSPEND in lambdaType.modifiers)
    }

    @Test
    fun testEmptyContextReceiversList() {
        val unitType = KotlinClassNames.UNIT.ref()

        val lambdaType = KotlinLambdaTypeName(unitType)

        assertTrue(lambdaType.contextReceivers.isEmpty())
    }

    @Test
    fun testParameterSpecDirectCreation() {
        val stringType = KotlinClassNames.STRING.ref()
        val booleanType = KotlinClassNames.BOOLEAN.ref()

        val param = KotlinValueParameterSpec.builder("input", stringType).build()
        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter(param)
        }

        val result = lambdaType.writeToKotlinString()

        assertEquals("(input: String) -> Boolean", result.trim())
    }

    @Test
    fun testToStringMethod() {
        val stringType = KotlinClassNames.STRING.ref()
        val booleanType = KotlinClassNames.BOOLEAN.ref()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter("input", stringType)
        }

        val toStringResult = lambdaType.toString()
        assertEquals("(input: String) -> Boolean", toStringResult)
    }
}
