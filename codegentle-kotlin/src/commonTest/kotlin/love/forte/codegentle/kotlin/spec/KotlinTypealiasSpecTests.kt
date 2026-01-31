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
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [KotlinTypealiasSpec].
 */
class KotlinTypealiasSpecTests {

    @Test
    fun testSimpleTypealias() {
        val stringType = ClassName("kotlin", "String").ref()
        val typealiasSpec = KotlinTypealiasSpec("MyString", stringType)

        val code = typealiasSpec.writeToKotlinString()
        assertEquals("typealias MyString = String", code)
    }

    @Test
    fun testTypealiasWithModifiers() {
        val stringType = ClassName("kotlin", "String").ref()

        val typealiasSpec = KotlinTypealiasSpec("MyString", stringType) {
            addModifier(KotlinModifier.INTERNAL)
        }

        val code = typealiasSpec.writeToKotlinString()
        assertEquals("internal typealias MyString = String", code)
    }

    @Test
    fun testTypealiasWithMultipleModifiers() {
        val stringType = ClassName("kotlin", "String").ref()

        val typealiasSpec = KotlinTypealiasSpec("MyString", stringType) {
            addModifiers(KotlinModifier.PUBLIC, KotlinModifier.ACTUAL)
        }

        val code = typealiasSpec.writeToKotlinString()
        assertEquals("public actual typealias MyString = String", code)
    }

    @Test
    fun testTypealiasWithKDoc() {
        val stringType = ClassName("kotlin", "String").ref()

        val typealiasSpec = KotlinTypealiasSpec("MyString", stringType) {
            addDoc("This is a custom string type alias.")
        }

        val code = typealiasSpec.writeToKotlinString()
        assertEquals(
            """
            /**
             * This is a custom string type alias.
             */
            typealias MyString = String
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testTypealiasWithSimpleAnnotation() {
        val stringType = ClassName("kotlin", "String").ref()
        val deprecatedAnnotation = ClassName("kotlin", "Deprecated").annotationRef()

        val typealiasSpec = KotlinTypealiasSpec("MyString", stringType) {
            addAnnotation(deprecatedAnnotation)
        }

        val code = typealiasSpec.writeToKotlinString()
        assertEquals(
            """
            @Deprecated
            typealias MyString = String
            """.trimIndent(),
            code
        )
    }

    @Test
    fun testTypealiasWithSingleTypeParameter() {
        val listType = ClassName("kotlin.collections", "List").ref()
        val tTypeVariable = TypeVariableName("T").ref()

        val typealiasSpec = KotlinTypealiasSpec("MyList", listType) {
            addTypeVariable(tTypeVariable)
        }

        val code = typealiasSpec.writeToKotlinString()
        assertEquals("typealias MyList<T> = kotlin.collections.List", code)
    }

    @Test
    fun testTypealiasWithMultipleTypeParameters() {
        val mapType = ClassName("kotlin.collections", "Map").ref()
        val kTypeVariable = TypeVariableName("K").ref()
        val vTypeVariable = TypeVariableName("V").ref()

        val typealiasSpec = KotlinTypealiasSpec("MyMap", mapType) {
            addTypeVariables(kTypeVariable, vTypeVariable)
        }

        val code = typealiasSpec.writeToKotlinString()
        assertEquals("typealias MyMap<K, V> = kotlin.collections.Map", code)
    }

    @Test
    fun testTypealiasBuilder() {
        val stringType = ClassName("kotlin", "String").ref()

        val typealiasSpec = KotlinTypealiasSpec("MyString", stringType) {
            addModifier(KotlinModifier.INTERNAL)
        }

        val code = typealiasSpec.writeToKotlinString()

        assertEquals("internal typealias MyString = String", code)
    }

    @Test
    fun testTypealiasProperties() {
        val stringType = ClassName("kotlin", "String").ref()

        val typealiasSpec = KotlinTypealiasSpec("MyString", stringType)

        assertEquals("MyString", typealiasSpec.name)
        assertEquals(KotlinTypeSpec.Kind.TYPE_ALIAS, typealiasSpec.kind)
        assertEquals(stringType, typealiasSpec.type)
        assertEquals(null, typealiasSpec.superclass)
        assertEquals(emptyList(), typealiasSpec.superinterfaces)
        assertEquals(emptyList(), typealiasSpec.properties)
        assertEquals(emptyList(), typealiasSpec.functions)
        assertEquals(emptyList(), typealiasSpec.subtypes)
        assertEquals(true, typealiasSpec.isMemberEmpty())
    }
}
