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
package love.forte.codegentle.java.ref

import love.forte.codegentle.common.code.CodePart.Companion.literal
import love.forte.codegentle.common.code.CodePart.Companion.string
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.java.writer.writeToJavaString
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for JavaAnnotationRef functionality.
 */
class JavaAnnotationRefTests {

    @Test
    fun testEmptyAnnotation() {
        val anno = ClassName("com.example.anno", "Anno").annotationRef()

        assertEquals(
            "@com.example.anno.Anno",
            anno.writeToJavaString()
        )
    }

    @Test
    fun testAnnotationWithSingleValueMember() {
        val anno = ClassName("com.example.anno", "Anno").annotationRef {
            addMember("value", "%V", string("test"))
        }

        assertEquals(
            "@com.example.anno.Anno(\"test\")",
            anno.writeToJavaString()
        )
    }

    @Test
    fun testAnnotationWithMultipleMembers() {
        val anno = ClassName("com.example.anno", "Anno").annotationRef {
            addMember("name", "%V", string("test"))
            addMember("value", "%V", literal(42))
        }

        assertEquals(
            "@com.example.anno.Anno(name = \"test\", value = 42)",
            anno.writeToJavaString()
        )
    }

    @Test
    fun testAnnotationWithArrayValues() {
        val anno = ClassName("com.example.anno", "Anno").annotationRef {
            addMember("value", "%V", string("test1"))
            addMember("value", "%V", string("test2"))
        }

        assertEquals(
            "@com.example.anno.Anno({\"test1\", \"test2\"})",
            anno.writeToJavaString()
        )
    }

    @Test
    fun testAnnotationWithMixedArrayValues() {
        val anno = ClassName("com.example.anno", "Anno").annotationRef {
            addMember("values", "%V", literal(1))
            addMember("values", "%V", literal(2))
            addMember("values", "%V", literal(3))
            addMember("name", "%V", string("test"))
        }

        assertEquals(
            "@com.example.anno.Anno(values = {1, 2, 3}, name = \"test\")",
            anno.writeToJavaString()
        )
    }
}
