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
package love.forte.codegentle.kotlin.ref

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.status
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for KotlinAnnotationRefStatus with usesite functionality.
 * Ensures that when usesite exists, the emit results are normal and meet expectations.
 */
class KotlinAnnotationRefStatusTests {

    private val testAnnotationClass = ClassName("com.example", "TestAnnotation")

    @Test
    fun testAnnotationRefWithoutUseSite() {
        val annotation = testAnnotationClass.kotlinAnnotationRef()
        
        val kotlinStatus = annotation.kotlinStatusOrNull
        assertNotNull(kotlinStatus)
        assertNull(kotlinStatus.useSite)
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@com.example.TestAnnotation", result)
    }

    @Test
    fun testAnnotationRefWithFieldUseSite() {
        val annotation = testAnnotationClass.kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.FIELD
        }
        
        val kotlinStatus = annotation.kotlinStatusOrNull
        assertNotNull(kotlinStatus)
        assertEquals(KotlinAnnotationUseSite.FIELD, kotlinStatus.useSite)
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@field:com.example.TestAnnotation", result)
    }

    @Test
    fun testAnnotationRefWithPropertyUseSite() {
        val annotation = testAnnotationClass.kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.PROPERTY
        }
        
        val kotlinStatus = annotation.kotlinStatusOrNull
        assertNotNull(kotlinStatus)
        assertEquals(KotlinAnnotationUseSite.PROPERTY, kotlinStatus.useSite)
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@property:com.example.TestAnnotation", result)
    }

    @Test
    fun testAnnotationRefWithGetUseSite() {
        val annotation = testAnnotationClass.kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.GET
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@get:com.example.TestAnnotation", result)
    }

    @Test
    fun testAnnotationRefWithSetUseSite() {
        val annotation = testAnnotationClass.kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.SET
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@set:com.example.TestAnnotation", result)
    }

    @Test
    fun testAnnotationRefWithFileUseSite() {
        val annotation = testAnnotationClass.kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.FILE
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@file:com.example.TestAnnotation", result)
    }

    @Test
    fun testAnnotationRefWithParamUseSite() {
        val annotation = testAnnotationClass.kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.PARAM
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@param:com.example.TestAnnotation", result)
    }

    @Test
    fun testAnnotationRefWithSetParamUseSite() {
        val annotation = testAnnotationClass.kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.SETPARAM
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@setparam:com.example.TestAnnotation", result)
    }

    @Test
    fun testAnnotationRefWithReceiverUseSite() {
        val annotation = testAnnotationClass.kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.RECEIVER
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@receiver:com.example.TestAnnotation", result)
    }

    @Test
    fun testAnnotationRefWithDelegateUseSite() {
        val annotation = testAnnotationClass.kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.DELEGATE
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@delegate:com.example.TestAnnotation", result)
    }

    @Test
    fun testAnnotationRefWithAllUseSite() {
        val annotation = testAnnotationClass.kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.ALL
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@all:com.example.TestAnnotation", result)
    }

    @Test
    fun testAnnotationRefWithUseSiteAndParameters() {
        val annotation = testAnnotationClass.kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.FIELD
            addMember("value", "\"test\"")
            addMember("count", "42")
        }
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation.emitTo(writer)
        }
        
        assertEquals("@field:com.example.TestAnnotation(value = \"test\", count = 42)", result)
    }

    @Test
    fun testMultipleAnnotationsWithDifferentUseSites() {
        val annotation1 = testAnnotationClass.kotlinAnnotationRef {
            status {
                useSite = KotlinAnnotationUseSite.FIELD
            }
        }
        
        val annotation2 = ClassName("com.example", "AnotherAnnotation").kotlinAnnotationRef {
            status.useSite = KotlinAnnotationUseSite.GET
        }
        
        val result1 = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation1.emitTo(writer)
        }
        
        val result2 = buildString {
            val writer = KotlinCodeWriter.create(this)
            annotation2.emitTo(writer)
        }
        
        assertEquals("@field:com.example.TestAnnotation", result1)
        assertEquals("@get:com.example.AnotherAnnotation", result2)
    }

    @Test
    fun testKotlinAnnotationRefStatusBuilder() {
        val builder = KotlinAnnotationRefStatus.createBuilder()
        builder.useSite = KotlinAnnotationUseSite.PROPERTY
        
        val status = builder.build()
        assertEquals(KotlinAnnotationUseSite.PROPERTY, status.useSite)
    }

    @Test
    fun testKotlinAnnotationRefStatusBuilderWithNullUseSite() {
        val builder = KotlinAnnotationRefStatus.createBuilder()
        // useSite is null by default
        
        val status = builder.build()
        assertNull(status.useSite)
    }
}
