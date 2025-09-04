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
package love.forte.codegentle.kotlin.ref

import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for TypeRef emission with annotations and nullable information.
 */
class TypeRefEmitterTests {

    @Test
    fun testTypeRefWithNullable() {
        val className = ClassName("com.example", "TestClass")
        val typeRef = className.ref {
            kotlinStatus {
                nullable = true
            }
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(typeRef)
        }

        assertEquals("com.example.TestClass?", result)
    }

    @Test
    fun testTypeRefWithAnnotation() {
        val className = ClassName("com.example", "TestClass")
        val annotationClass = ClassName("com.example", "MyAnnotation")
        val annotation = annotationClass.annotationRef()

        val typeRef = className.ref {
            kotlinStatus {
                addAnnotation(annotation)
            }
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(typeRef)
        }

        assertEquals("@com.example.MyAnnotation com.example.TestClass", result)
    }

    @Test
    fun testTypeRefWithAnnotationAndNullable() {
        val className = ClassName("com.example", "TestClass")
        val annotationClass = ClassName("com.example", "MyAnnotation")
        val annotation = annotationClass.annotationRef()

        val typeRef = className.ref {
            kotlinStatus {
                addAnnotation(annotation)
                nullable = true
            }
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(typeRef)
        }

        assertEquals("@com.example.MyAnnotation com.example.TestClass?", result)
    }

    @Test
    fun testTypeRefWithMultipleAnnotations() {
        val className = ClassName("com.example", "TestClass")
        val annotation1 = ClassName("com.example", "Annotation1").annotationRef()
        val annotation2 = ClassName("com.example", "Annotation2").annotationRef()

        val typeRef = className.ref {
            kotlinStatus {
                addAnnotation(annotation1)
                addAnnotation(annotation2)
            }
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(typeRef)
        }

        assertEquals("@com.example.Annotation1 @com.example.Annotation2 com.example.TestClass", result)
    }

    @Test
    fun testTypeRefWithMultipleAnnotationsAndNullable() {
        val className = ClassName("com.example", "TestClass")
        val annotation1 = ClassName("com.example", "Annotation1").annotationRef()
        val annotation2 = ClassName("com.example", "Annotation2").annotationRef()

        val typeRef = className.ref {
            kotlinStatus {
                addAnnotation(annotation1)
                addAnnotation(annotation2)
                nullable = true
            }
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(typeRef)
        }

        assertEquals("@com.example.Annotation1 @com.example.Annotation2 com.example.TestClass?", result)
    }

    @Test
    fun testTypeRefInCodeValue() {
        val className = ClassName("com.example", "TestClass")
        val annotationClass = ClassName("com.example", "MyAnnotation")
        val annotation = annotationClass.annotationRef()

        val typeRef = className.ref {
            kotlinStatus {
                addAnnotation(annotation)
                nullable = true
            }
        }

        val codeValue = CodeValue("val property: %V = null") {
            addValue(CodePart.type(typeRef))
        }

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(codeValue)
        }

        assertEquals("val property: @com.example.MyAnnotation com.example.TestClass? = null", result)
    }

    @Test
    fun testTypeRefWithoutStatus() {
        val className = ClassName("com.example", "TestClass")
        val typeRef = className.ref()

        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(typeRef)
        }

        assertEquals("com.example.TestClass", result)
    }
}
