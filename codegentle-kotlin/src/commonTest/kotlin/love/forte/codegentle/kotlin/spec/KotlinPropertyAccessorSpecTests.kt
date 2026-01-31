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

import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for [KotlinPropertyAccessorSpec] and its subclasses.
 */
class KotlinPropertyAccessorSpecTests {

    @Test
    fun testBasicGetter() {
        val getterSpec = KotlinPropertyAccessorSpec.getterBuilder()
            .build()

        assertNotNull(getterSpec)
        assertEquals(KotlinPropertyAccessorSpec.Kind.GETTER, getterSpec.kind)
        assertEquals(emptySet<KotlinModifier>(), getterSpec.modifiers)
        assertEquals(emptyList<KotlinValueParameterSpec>(), getterSpec.parameters)
    }

    @Test
    fun testGetterWithModifiers() {
        val getterSpec = KotlinPropertyAccessorSpec.getterBuilder()
            .addModifier(KotlinModifier.PRIVATE)
            .build()

        assertEquals(setOf(KotlinModifier.PRIVATE), getterSpec.modifiers)
    }

    @Test
    fun testGetterWithCode() {
        val getterSpec = KotlinPropertyAccessorSpec.getterBuilder()
            .addCode("return field")
            .build()

        assertNotNull(getterSpec.code)
        assertEquals("return field", getterSpec.code.writeToKotlinString())
    }

    @Test
    fun testGetterWithKDoc() {
        val getterSpec = KotlinPropertyAccessorSpec.getterBuilder()
            .addDoc("This is a getter.")
            .build()

        assertNotNull(getterSpec.kDoc)
        assertEquals("This is a getter.", getterSpec.kDoc.writeToKotlinString())
    }

    @Test
    fun testBasicSetter() {
        val setterSpec = KotlinPropertyAccessorSpec.setterBuilder("value")
            .build()

        assertNotNull(setterSpec)
        assertEquals(KotlinPropertyAccessorSpec.Kind.SETTER, setterSpec.kind)
        assertEquals(emptySet<KotlinModifier>(), setterSpec.modifiers)
        assertEquals(emptyList<KotlinValueParameterSpec>(), setterSpec.parameters)
        assertEquals("value", setterSpec.parameterName)
    }

    @Test
    fun testSetterWithModifiers() {
        val setterSpec = KotlinPropertyAccessorSpec.setterBuilder("value")
            .addModifier(KotlinModifier.PRIVATE)
            .build()

        assertEquals(setOf(KotlinModifier.PRIVATE), setterSpec.modifiers)
    }

    @Test
    fun testSetterWithCode() {
        val setterSpec = KotlinPropertyAccessorSpec.setterBuilder("value")
            .addCode("field = value")
            .build()

        assertNotNull(setterSpec.code)
        assertEquals("field = value", setterSpec.code.writeToKotlinString())
    }

    @Test
    fun testSetterWithKDoc() {
        val setterSpec = KotlinPropertyAccessorSpec.setterBuilder("value")
            .addDoc("This is a setter.")
            .build()

        assertNotNull(setterSpec.kDoc)
        assertEquals("This is a setter.", setterSpec.kDoc.writeToKotlinString())
    }
}
