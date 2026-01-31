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
package love.forte.codegentle.common.naming

import love.forte.codegentle.common.ref.ref
import kotlin.test.Test
import kotlin.test.assertEquals

class ParameterizedTypeNameToStringTest {
    @Test
    fun testSimpleParameterizedType() {
        val stringClassName = ClassName("kotlin", "String")
        val listClassName = ClassName("kotlin.collections", "List")
        val stringTypeRef = stringClassName.ref()

        val listOfString = ParameterizedTypeName(listClassName, listOf(stringTypeRef))

        assertEquals("kotlin.collections.List<kotlin.String>", listOfString.toString())
    }

    @Test
    fun testParameterizedTypeWithMultipleArguments() {
        val stringClassName = ClassName("kotlin", "String")
        val intClassName = ClassName("kotlin", "Int")
        val mapClassName = ClassName("kotlin.collections", "Map")

        val stringTypeRef = stringClassName.ref()
        val intTypeRef = intClassName.ref()

        val mapOfStringInt = ParameterizedTypeName(mapClassName, listOf(stringTypeRef, intTypeRef))

        assertEquals("kotlin.collections.Map<kotlin.String, kotlin.Int>", mapOfStringInt.toString())
    }

    @Test
    fun testParameterizedTypeWithNoArguments() {
        val listClassName = ClassName("kotlin.collections", "List")
        val rawList = ParameterizedTypeName(listClassName, emptyList())

        assertEquals("kotlin.collections.List", rawList.toString())
    }

    @Test
    fun testNestedParameterizedType() {
        val stringClassName = ClassName("kotlin", "String")
        val intClassName = ClassName("kotlin", "Int")
        val listClassName = ClassName("kotlin.collections", "List")
        val mapClassName = ClassName("kotlin.collections", "Map")

        val stringTypeRef = stringClassName.ref()
        val intTypeRef = intClassName.ref()

        val mapOfStringInt = ParameterizedTypeName(mapClassName, listOf(stringTypeRef, intTypeRef))
        val mapTypeRef = mapOfStringInt.ref()
        val listOfMap = ParameterizedTypeName(listClassName, listOf(mapTypeRef))

        assertEquals("kotlin.collections.List<kotlin.collections.Map<kotlin.String, kotlin.Int>>", listOfMap.toString())
    }

    @Test
    fun testNestedClassParameterizedType() {
        val outerClassName = ClassName("com.example", "Outer")
        val stringClassName = ClassName("kotlin", "String")
        val stringTypeRef = stringClassName.ref()

        val outerParameterized = ParameterizedTypeName(outerClassName, listOf(stringTypeRef))
        val innerParameterized = outerParameterized.nestedClass("Inner", listOf(stringTypeRef))

        assertEquals("com.example.Outer<kotlin.String>.Inner<kotlin.String>", innerParameterized.toString())
    }
}
