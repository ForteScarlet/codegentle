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
package love.forte.codegentle.kotlin

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.status
import love.forte.codegentle.kotlin.ref.KotlinAnnotationUseSite
import love.forte.codegentle.kotlin.ref.kotlinAnnotationRef
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for file-level annotation functionality in KotlinFile.
 */
class KotlinFileAnnotationTests {

    @Test
    fun testBasicFileAnnotation() {
        // Test with simple annotation without useSite (should default to FILE)
        val suppressAnnotation = ClassName("kotlin", "Suppress").kotlinAnnotationRef {
            addMember(format = "\"UNUSED\"")
            status {
                useSite = KotlinAnnotationUseSite.FILE
            }
        }

        val file = KotlinFile("com.example") {
            addSimpleClassType("TestClass")
            addAnnotation(suppressAnnotation)
        }

        assertEquals(1, file.annotations.size)

        val output = file.writeToKotlinString()
        assertEquals(
            """
            @file:Suppress("UNUSED")
            package com.example

            class TestClass
        """.trimIndent(), output
        )
    }

    @Test
    fun testAnnotationWithoutUseSiteAllowed() {
        // Test that annotations without useSite are allowed (should default to FILE)
        val deprecatedAnnotation = ClassName("kotlin", "Deprecated").kotlinAnnotationRef {
            addMember("message", "\"This is deprecated\"")
            // No useSite specified - should be allowed
        }

        val file = KotlinFile("com.example") {
            addSimpleClassType("TestClass")
            addAnnotation(deprecatedAnnotation)
        }

        assertEquals(1, file.annotations.size)

        val output = file.writeToKotlinString()
        assertEquals(
            """
            @file:Deprecated(message = "This is deprecated")
            package com.example

            class TestClass
        """.trimIndent(), output
        )
    }

    @Test
    fun testInvalidUseSiteRejected() {
        // Test that non-FILE useSite annotations are rejected
        val fieldAnnotation = ClassName("kotlin", "Volatile").kotlinAnnotationRef {
            status {
                useSite = KotlinAnnotationUseSite.FIELD
            }
        }

        val exception = kotlin.test.assertFailsWith<IllegalArgumentException> {
            KotlinFile("com.example") {
                addSimpleClassType("TestClass")
                addAnnotation(fieldAnnotation)
            }
        }

        assertEquals(
            "File-level annotations must use @file: syntax. Found useSite: FIELD",
            exception.message
        )
    }

    @Test
    fun testMultipleFileAnnotations() {
        // Test multiple file-level annotations
        val suppressAnnotation = ClassName("kotlin", "Suppress").kotlinAnnotationRef {
            addMember("value", "\"UNUSED\"")
            status {
                useSite = KotlinAnnotationUseSite.FILE
            }
        }

        val deprecatedAnnotation = ClassName("kotlin", "Deprecated").kotlinAnnotationRef {
            addMember("message", "\"Old API\"")
            status {
                useSite = KotlinAnnotationUseSite.FILE
            }
        }

        val file = KotlinFile("com.example") {
            addSimpleClassType("TestClass")
            addAnnotation(suppressAnnotation)
            addAnnotation(deprecatedAnnotation)
        }

        assertEquals(2, file.annotations.size)

        val output = file.writeToKotlinString()
        assertEquals(
            """
            @file:Suppress(value = "UNUSED")
            @file:Deprecated(message = "Old API")
            package com.example

            class TestClass
        """.trimIndent(), output
        )
    }

}
