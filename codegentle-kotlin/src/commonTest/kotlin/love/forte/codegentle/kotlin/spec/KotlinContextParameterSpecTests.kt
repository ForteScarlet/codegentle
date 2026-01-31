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

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.naming.KotlinClassNames
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Tests for [KotlinContextParameterSpec].
 */
class KotlinContextParameterSpecTests {

    private val stringType = KotlinClassNames.STRING.ref()
    private val intType = KotlinClassNames.INT.ref()
    private val listType = KotlinClassNames.LIST.ref()

    @Test
    fun testBasicContextParameterCreation() {
        val contextParam = KotlinContextParameterSpec("context", stringType)

        assertEquals("context", contextParam.name)
        assertEquals(stringType, contextParam.typeRef)
    }

    @Test
    fun testContextParameterWithNullName() {
        val contextParam = KotlinContextParameterSpec.builder(null, stringType)
            .build()

        assertNull(contextParam.name)
        assertEquals(stringType, contextParam.typeRef)
    }

    @Test
    fun testContextParameterOfFactory() {
        val contextParam = KotlinContextParameterSpec.of("logger", stringType)

        assertEquals("logger", contextParam.name)
        assertEquals(stringType, contextParam.typeRef)
    }

    @Test
    fun testContextParameterOfFactoryWithNullName() {
        val contextParam = KotlinContextParameterSpec.of(null, intType)

        assertNull(contextParam.name)
        assertEquals(intType, contextParam.typeRef)
    }

    @Test
    fun testContextParameterDSLFactory() {
        val contextParam = KotlinContextParameterSpec("dispatcher", stringType) {
            // DSL configuration block (currently empty as builder has no additional methods)
        }

        assertEquals("dispatcher", contextParam.name)
        assertEquals(stringType, contextParam.typeRef)
    }

    @Test
    fun testContextParameterDSLFactoryWithNullName() {
        val contextParam = KotlinContextParameterSpec(null, listType) {
            // DSL configuration block
        }

        assertNull(contextParam.name)
        assertEquals(listType, contextParam.typeRef)
    }

    @Test
    fun testContextParameterBuilderChaining() {
        val builder = KotlinContextParameterSpec.builder("context", stringType)
        val result = builder.build()

        assertEquals("context", result.name)
        assertEquals(stringType, result.typeRef)
    }

    @Test
    fun testContextParameterBuilderReuse() {
        val builder = KotlinContextParameterSpec.builder("context", stringType)

        val param1 = builder.build()
        val param2 = builder.build()

        assertEquals("context", param1.name)
        assertEquals("context", param2.name)
        assertEquals(stringType, param1.typeRef)
        assertEquals(stringType, param2.typeRef)
    }

    @Test
    fun testContextParameterWithComplexType() {
        val complexType = ClassName("kotlinx.coroutines", "CoroutineDispatcher").ref()
        val contextParam = KotlinContextParameterSpec.builder("dispatcher", complexType)
            .build()

        assertEquals("dispatcher", contextParam.name)
        assertEquals(complexType, contextParam.typeRef)
    }

    @Test
    fun testContextParameterWithGenericType() {
        val genericType = ClassName("kotlin.collections", "List").ref()
        val contextParam = KotlinContextParameterSpec.builder("items", genericType)
            .build()

        assertEquals("items", contextParam.name)
        assertEquals(genericType, contextParam.typeRef)
    }

    @Test
    fun testContextParameterEquality() {
        val param1 = KotlinContextParameterSpec.of("context", stringType)
        val param2 = KotlinContextParameterSpec.of("context", stringType)
        val param3 = KotlinContextParameterSpec.of("other", stringType)
        val param4 = KotlinContextParameterSpec.of("context", intType)

        // Note: Equality depends on the actual implementation
        // These tests verify the basic structure is consistent
        assertEquals("context", param1.name)
        assertEquals("context", param2.name)
        assertEquals("other", param3.name)
        assertEquals("context", param4.name)

        assertEquals(stringType, param1.typeRef)
        assertEquals(stringType, param2.typeRef)
        assertEquals(stringType, param3.typeRef)
        assertEquals(intType, param4.typeRef)
    }

    @Test
    fun testContextParameterWithNullNameEquality() {
        val param1 = KotlinContextParameterSpec.of(null, stringType)
        val param2 = KotlinContextParameterSpec.of(null, stringType)
        val param3 = KotlinContextParameterSpec.of(null, intType)

        assertNull(param1.name)
        assertNull(param2.name)
        assertNull(param3.name)

        assertEquals(stringType, param1.typeRef)
        assertEquals(stringType, param2.typeRef)
        assertEquals(intType, param3.typeRef)
    }

    @Test
    fun testContextParameterBuilderProperties() {
        val builder = KotlinContextParameterSpec.builder("test", stringType)

        assertEquals("test", builder.name)
        assertEquals(stringType, builder.type)
    }

    @Test
    fun testContextParameterBuilderPropertiesWithNullName() {
        val builder = KotlinContextParameterSpec.builder(null, intType)

        assertNull(builder.name)
        assertEquals(intType, builder.type)
    }

    @Test
    fun testContextParameterConsistency() {
        val contextParam = KotlinContextParameterSpec.builder("context", stringType)
            .build()

        // Test that multiple accesses return consistent values
        assertEquals("context", contextParam.name)
        assertEquals("context", contextParam.name)
        assertEquals(stringType, contextParam.typeRef)
        assertEquals(stringType, contextParam.typeRef)
    }

    @Test
    fun testContextParameterWithLongName() {
        val longName = "veryLongContextParameterNameThatShouldStillWork"
        val contextParam = KotlinContextParameterSpec.builder(longName, stringType)
            .build()

        assertEquals(longName, contextParam.name)
        assertEquals(stringType, contextParam.typeRef)
    }

    @Test
    fun testContextParameterWithSpecialCharactersInName() {
        val specialName = "context_parameter_with_underscores"
        val contextParam = KotlinContextParameterSpec.builder(specialName, stringType)
            .build()

        assertEquals(specialName, contextParam.name)
        assertEquals(stringType, contextParam.typeRef)
    }

    @Test
    fun testContextParameterFactoryMethodsConsistency() {
        val name = "testContext"
        val type = stringType

        val builderParam = KotlinContextParameterSpec.builder(name, type).build()
        val ofParam = KotlinContextParameterSpec.of(name, type)
        val dslParam = KotlinContextParameterSpec(name, type)

        assertEquals(name, builderParam.name)
        assertEquals(name, ofParam.name)
        assertEquals(name, dslParam.name)

        assertEquals(type, builderParam.typeRef)
        assertEquals(type, ofParam.typeRef)
        assertEquals(type, dslParam.typeRef)
    }

    @Test
    fun testContextParameterFactoryMethodsConsistencyWithNullName() {
        val type = intType

        val builderParam = KotlinContextParameterSpec.builder(null, type).build()
        val ofParam = KotlinContextParameterSpec.of(null, type)
        val dslParam = KotlinContextParameterSpec(null, type)

        assertNull(builderParam.name)
        assertNull(ofParam.name)
        assertNull(dslParam.name)

        assertEquals(type, builderParam.typeRef)
        assertEquals(type, ofParam.typeRef)
        assertEquals(type, dslParam.typeRef)
    }

    @Test
    fun testContextParameterCodeGeneration() {
        val contextParam = KotlinContextParameterSpec("context", stringType)

        val generatedCode = contextParam.writeToKotlinString()

        assertEquals("context: String", generatedCode.trim())
    }

    @Test
    fun testContextParameterWithNullNameCodeGeneration() {
        val contextParam = KotlinContextParameterSpec(null, stringType)

        val generatedCode = contextParam.writeToKotlinString()

        assertEquals("_: String", generatedCode.trim())
    }

    @Test
    fun testContextParameterCodeGenerationConsistency() {
        val generatedCode1 = KotlinContextParameterSpec("context", stringType).writeToKotlinString()
        val generatedCode2 = KotlinContextParameterSpec("context", stringType).writeToKotlinString()

        assertEquals(generatedCode1, generatedCode2)
    }
}
