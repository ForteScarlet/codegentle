package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.spec.addParameter
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class KotlinLambdaTypeNameTests {

    @Test
    fun testSimpleLambdaType() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val booleanType = ClassName("kotlin", "Boolean").kotlinRef()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter("", stringType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("(String) -> Boolean", result.trim())
    }

    @Test
    fun testSuspendLambdaType() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val unitType = ClassName("kotlin", "Unit").kotlinRef()

        val lambdaType = buildKotlinSuspendLambdaTypeName(unitType) {
            addParameter("", stringType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("suspend (String) -> Unit", result.trim())
    }

    @Test
    fun testBuilderPattern() {
        val stringType = ClassName("kotlin", "String").kotlinRef()
        val booleanType = ClassName("kotlin", "Boolean").kotlinRef()

        val lambdaType = KotlinLambdaTypeName {
            returns(booleanType)
            addParameter("input", stringType)
            addModifier(KotlinModifier.SUSPEND)
        }

        assertTrue(lambdaType.isSuspend)
        assertEquals(1, lambdaType.parameters.size)
        assertEquals("input", lambdaType.parameters[0].name)
        assertEquals(booleanType, lambdaType.returnType)
    }

    @Test
    fun testNoParameterLambdaType() {
        val unitType = ClassName("kotlin", "Unit").kotlinRef()

        val lambdaType = KotlinLambdaTypeName(unitType)

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("() -> Unit", result.trim())
    }

    @Test
    fun testExtensionLambdaType() {
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val booleanType = KotlinNames.Classes.BOOLEAN.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            receiver(stringType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("String.() -> Boolean", result.trim())
    }

    @Test
    fun testExtensionLambdaWithParameters() {
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val intType = KotlinNames.Classes.INT.kotlinRef()
        val unitType = KotlinNames.Classes.UNIT.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(unitType) {
            receiver(stringType)
            addParameter("index", intType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("String.(index: Int) -> Unit", result.trim())
    }

    @Test
    fun testSuspendExtensionLambdaType() {
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val unitType = KotlinNames.Classes.UNIT.kotlinRef()

        val lambdaType = buildKotlinSuspendLambdaTypeName(unitType) {
            receiver(stringType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("suspend String.() -> Unit", result.trim())
    }

    @Test
    fun testContextReceiverLambdaType() {
        val loggerType = ClassName("kotlin.io", "Logger").kotlinRef()
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val unitType = KotlinNames.Classes.UNIT.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(unitType) {
            addContextReceiver(loggerType)
            addParameter("message", stringType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("context(Logger) (message: String) -> Unit", result.trim())
    }

    @Test
    fun testMultipleContextReceiversLambdaType() {
        val loggerType = ClassName("kotlin.io", "Logger").kotlinRef()
        val contextType = ClassName("kotlin.coroutines", "CoroutineContext").kotlinRef()
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val unitType = KotlinNames.Classes.UNIT.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(unitType) {
            addContextReceivers(loggerType, contextType)
            addParameter("message", stringType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("context(Logger, kotlin.coroutines.CoroutineContext) (message: String) -> Unit", result.trim())
    }

    @Test
    fun testMultipleParametersLambdaType() {
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val intType = KotlinNames.Classes.INT.kotlinRef()
        val booleanType = KotlinNames.Classes.BOOLEAN.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter("name", stringType)
            addParameter("age", intType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("(name: String, age: Int) -> Boolean", result.trim())
    }

    @Test
    fun testUnnamedParametersLambdaType() {
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val intType = KotlinNames.Classes.INT.kotlinRef()
        val booleanType = KotlinNames.Classes.BOOLEAN.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter("", stringType)
            addParameter("", intType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("(String, Int) -> Boolean", result.trim())
    }

    @Test
    fun testMixedNamedAndUnnamedParametersLambdaType() {
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val intType = KotlinNames.Classes.INT.kotlinRef()
        val booleanType = KotlinNames.Classes.BOOLEAN.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter("name", stringType)
            addParameter("", intType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("(name: String, Int) -> Boolean", result.trim())
    }

    @Test
    fun testComplexCombinationLambdaType() {
        val loggerType = ClassName("kotlin.io", "Logger").kotlinRef()
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val intType = KotlinNames.Classes.INT.kotlinRef()
        val unitType = KotlinNames.Classes.UNIT.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(unitType) {
            addContextReceiver(loggerType)
            receiver(stringType)
            addParameter("count", intType)
            addModifier(KotlinModifier.SUSPEND)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("suspend context(Logger) String.(count: Int) -> Unit", result.trim())
    }

    @Test
    fun testLambdaWithGenericReturnType() {
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val listType = KotlinNames.Classes.LIST.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(listType) {
            addParameter("input", stringType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("(input: String) -> List", result.trim())
    }

    @Test
    fun testLambdaWithNothingReturnType() {
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val nothingType = KotlinNames.Classes.NOTHING.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(nothingType) {
            addParameter("message", stringType)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("(message: String) -> Nothing", result.trim())
    }

    @Test
    fun testBuilderWithoutReturnTypeThrowsException() {
        assertFailsWith<IllegalStateException> {
            KotlinLambdaTypeName.builder()
                .addParameter("input", KotlinNames.Classes.STRING.kotlinRef())
                .build()
        }
    }

    @Test
    fun testBuilderPropertiesAccess() {
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val intType = KotlinNames.Classes.INT.kotlinRef()
        val booleanType = KotlinNames.Classes.BOOLEAN.kotlinRef()
        val loggerType = ClassName("kotlin.io", "Logger").kotlinRef()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            receiver(stringType)
            addContextReceiver(loggerType)
            addParameter("count", intType)
            addModifier(KotlinModifier.SUSPEND)
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
        val unitType = KotlinNames.Classes.UNIT.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(unitType)

        assertTrue(lambdaType.contextReceivers.isEmpty())
    }

    @Test
    fun testParameterSpecDirectCreation() {
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val booleanType = KotlinNames.Classes.BOOLEAN.kotlinRef()

        val param = KotlinValueParameterSpec.builder("input", stringType).build()
        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter(param)
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(lambdaType)
        }

        assertEquals("(input: String) -> Boolean", result.trim())
    }

    @Test
    fun testToStringMethod() {
        val stringType = KotlinNames.Classes.STRING.kotlinRef()
        val booleanType = KotlinNames.Classes.BOOLEAN.kotlinRef()

        val lambdaType = KotlinLambdaTypeName(booleanType) {
            addParameter("input", stringType)
        }

        val toStringResult = lambdaType.toString()
        assertEquals("(input: String) -> Boolean", toStringResult)
    }
}
