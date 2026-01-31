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

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.addDoc
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.addAnnotation
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.writer.writeToKotlinString
import kotlin.test.*

/**
 * Tests for [KotlinAnnotationTypeSpec].
 */
class KotlinAnnotationTypeSpecTests {

    private val stringType = ClassName("kotlin", "String").ref()
    private val intType = ClassName("kotlin", "Int").ref()

    @Test
    fun testBasicAnnotationCreation() {
        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .build()

        assertEquals("TestAnnotation", annotationSpec.name)
        assertEquals(KotlinTypeSpec.Kind.CLASS, annotationSpec.kind)
        assertNull(annotationSpec.superclass)
        assertEquals(emptyList(), annotationSpec.superinterfaces)
        assertEquals(emptyList(), annotationSpec.properties)
        assertEquals(setOf(KotlinModifier.ANNOTATION), annotationSpec.modifiers)
    }

    @Test
    fun testAnnotationWithModifiers() {
        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addModifier(KotlinModifier.PUBLIC)
            .build()

        assertEquals(setOf(KotlinModifier.PUBLIC, KotlinModifier.ANNOTATION), annotationSpec.modifiers)
    }

    @Test
    fun testAnnotationWithMultipleModifiers() {
        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addModifiers(KotlinModifier.PUBLIC, KotlinModifier.INTERNAL)
            .build()

        assertEquals(
            setOf(KotlinModifier.PUBLIC, KotlinModifier.INTERNAL, KotlinModifier.ANNOTATION),
            annotationSpec.modifiers
        )
    }

    @Test
    fun testAnnotationWithModifiersIterable() {
        val modifiers = listOf(KotlinModifier.PUBLIC, KotlinModifier.INTERNAL)
        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addModifiers(modifiers)
            .build()

        assertEquals(
            setOf(KotlinModifier.PUBLIC, KotlinModifier.INTERNAL, KotlinModifier.ANNOTATION),
            annotationSpec.modifiers
        )
    }

    @Test
    fun testAnnotationWithKDoc() {
        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addDoc("This is a test annotation.")
            .build()

        assertNotNull(annotationSpec.kDoc)
        assertEquals("This is a test annotation.", annotationSpec.kDoc.writeToKotlinString())
    }

    @Test
    fun testAnnotationWithKDocCodeValue() {
        val kDoc = CodeValue("This is a test annotation with parameter.")
        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addDoc(kDoc)
            .build()

        assertNotNull(annotationSpec.kDoc)
        assertEquals("This is a test annotation with parameter.", annotationSpec.kDoc.writeToKotlinString())
    }

    @Test
    fun testAnnotationWithKDocExtension() {
        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addDoc("This is a test annotation with extension.") {
                // Extension function usage
            }
            .build()

        assertNotNull(annotationSpec.kDoc)
        assertEquals("This is a test annotation with extension.", annotationSpec.kDoc.writeToKotlinString())
    }

    @Test
    fun testAnnotationWithAnnotations() {
        val targetAnnotation = ClassName("kotlin.annotation", "Target").annotationRef {
            addMember("value", "AnnotationTarget.CLASS")
        }
        val retentionAnnotation = ClassName("kotlin.annotation", "Retention").annotationRef {
            addMember("value", "AnnotationRetention.RUNTIME")
        }

        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addAnnotation(targetAnnotation)
            .addAnnotation(retentionAnnotation)
            .build()

        assertEquals(2, annotationSpec.annotations.size)
    }

    @Test
    fun testAnnotationWithAnnotationsIterable() {
        val annotations = listOf(
            ClassName("kotlin.annotation", "Target").annotationRef(),
            ClassName("kotlin.annotation", "Retention").annotationRef()
        )

        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addAnnotations(annotations)
            .build()

        assertEquals(2, annotationSpec.annotations.size)
    }

    @Test
    fun testAnnotationWithTypeVariables() {
        val typeVariableName = TypeVariableName("T")
        val typeVariable = typeVariableName.ref()
        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addTypeVariable(typeVariable)
            .build()

        assertEquals(1, annotationSpec.typeVariables.size)
        assertEquals("T", typeVariableName.name)
    }

    @Test
    fun testAnnotationWithTypeVariablesVararg() {
        val typeVar1 = TypeVariableName("T").ref()
        val typeVar2 = TypeVariableName("R").ref()
        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addTypeVariables(typeVar1, typeVar2)
            .build()

        assertEquals(2, annotationSpec.typeVariables.size)
    }

    @Test
    fun testAnnotationWithTypeVariablesIterable() {
        val typeVariables = listOf(
            TypeVariableName("T").ref(),
            TypeVariableName("R").ref()
        )
        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addTypeVariables(typeVariables)
            .build()

        assertEquals(2, annotationSpec.typeVariables.size)
    }

    @Test
    fun testAnnotationWithProperties() {
        val property1 = KotlinPropertySpec.builder("value", stringType)
            .build()
        val property2 = KotlinPropertySpec.builder("count", intType)
            .initializer("1")
            .build()

        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addProperty(property1)
            .addProperty(property2)
            .build()

        assertEquals(2, annotationSpec.properties.size)
        assertEquals("value", annotationSpec.properties[0].name)
        assertEquals("count", annotationSpec.properties[1].name)
    }

    @Test
    fun testAnnotationWithPropertiesVararg() {
        val property1 = KotlinPropertySpec.builder("value", stringType).build()
        val property2 = KotlinPropertySpec.builder("count", intType).build()

        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addProperties(property1, property2)
            .build()

        assertEquals(2, annotationSpec.properties.size)
    }

    @Test
    fun testAnnotationWithPropertiesIterable() {
        val properties = listOf(
            KotlinPropertySpec.builder("value", stringType).build(),
            KotlinPropertySpec.builder("count", intType).build()
        )

        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addProperties(properties)
            .build()

        assertEquals(2, annotationSpec.properties.size)
    }

    @Test
    fun testAnnotationWithPropertyExtension() {
        val annotationSpec = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addProperty("value", stringType) {
                initializer("\"default\"")
            }
            .build()

        assertEquals(1, annotationSpec.properties.size)
        assertEquals("value", annotationSpec.properties[0].name)
        assertNotNull(annotationSpec.properties[0].initializer)
    }

    @Test
    fun testAnnotationDSLFactory() {
        val annotationSpec = KotlinAnnotationTypeSpec("TestAnnotation") {
            addModifier(KotlinModifier.PUBLIC)
            addDoc("Test annotation")
            addProperty("value", stringType)
        }

        assertEquals("TestAnnotation", annotationSpec.name)
        assertEquals(setOf(KotlinModifier.PUBLIC, KotlinModifier.ANNOTATION), annotationSpec.modifiers)
        assertNotNull(annotationSpec.kDoc)
        assertEquals(1, annotationSpec.properties.size)
    }

    @Test
    fun testComplexAnnotationWithAllFeatures() {
        val annotationSpec = KotlinAnnotationTypeSpec("ComplexAnnotation") {
            addModifier(KotlinModifier.PUBLIC)
            addDoc("A complex annotation with all features.")
            addAnnotation(ClassName("kotlin.annotation", "Target").annotationRef())
            addTypeVariable(TypeVariableName("T").ref())
            addProperty("value", stringType) {
                initializer("\"default\"")
            }
            addProperty("count", intType) {
                initializer("1")
            }
        }

        assertEquals("ComplexAnnotation", annotationSpec.name)
        assertEquals(setOf(KotlinModifier.PUBLIC, KotlinModifier.ANNOTATION), annotationSpec.modifiers)
        assertNotNull(annotationSpec.kDoc)
        assertEquals(1, annotationSpec.annotations.size)
        assertEquals(1, annotationSpec.typeVariables.size)
        assertEquals(2, annotationSpec.properties.size)
    }

    @Test
    fun testAnnotationBuilderChaining() {
        val builder = KotlinAnnotationTypeSpec.builder("TestAnnotation")
        val result = builder
            .addModifier(KotlinModifier.PUBLIC)
            .addDoc("Test")
            .addProperty("value", stringType)

        assertSame(builder, result)
    }

    @Test
    fun testAnnotationBuilderReuse() {
        val builder = KotlinAnnotationTypeSpec.builder("TestAnnotation")
            .addModifier(KotlinModifier.PUBLIC)

        val annotation1 = builder.build()
        val annotation2 = builder.addProperty("value", stringType).build()

        assertEquals("TestAnnotation", annotation1.name)
        assertEquals("TestAnnotation", annotation2.name)
        assertEquals(0, annotation1.properties.size)
        assertEquals(1, annotation2.properties.size)
    }

    @Test
    fun testBasicAnnotationCodeGeneration() {
        val annotationSpec = KotlinAnnotationTypeSpec("TestAnnotation")

        val generatedCode = annotationSpec.writeToKotlinString()
        val expectedCode = """annotation class TestAnnotation"""

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testAnnotationWithModifiersCodeGeneration() {
        val annotationSpec = KotlinAnnotationTypeSpec("TestAnnotation") {
            addModifier(KotlinModifier.PUBLIC)
        }

        val generatedCode = annotationSpec.writeToKotlinString()
        val expectedCode = """public annotation class TestAnnotation"""

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testAnnotationWithKDocCodeGeneration() {
        val annotationSpec = KotlinAnnotationTypeSpec("TestAnnotation") {
            addDoc("This is a test annotation.")
        }

        val generatedCode = annotationSpec.writeToKotlinString()
        val expectedCode = """/**
 * This is a test annotation.
 */
annotation class TestAnnotation"""

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testAnnotationWithAnnotationsCodeGeneration() {
        val annotationSpec = KotlinAnnotationTypeSpec("TestAnnotation") {
            addAnnotation(ClassName("kotlin.annotation", "Target").annotationRef {
                addMember("value", "AnnotationTarget.CLASS")
            })
        }

        val generatedCode = annotationSpec.writeToKotlinString()
        val expectedCode = """@Target(value = AnnotationTarget.CLASS)
annotation class TestAnnotation"""

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testAnnotationWithPropertiesCodeGeneration() {
        val annotationSpec = KotlinAnnotationTypeSpec("TestAnnotation") {
            addProperty("value", stringType) {
                initializer("\"default\"")
            }
            addProperty("count", intType) {
                initializer("1")
            }
        }

        val generatedCode = annotationSpec.writeToKotlinString()
        val expectedCode = """annotation class TestAnnotation(
    val value: String = "default",
    val count: Int = 1
)"""

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testComplexAnnotationCodeGeneration() {
        val annotationSpec = KotlinAnnotationTypeSpec("ComplexAnnotation") {
            addModifier(KotlinModifier.PUBLIC)
            addDoc("A complex test annotation.")
            addAnnotation(ClassName("kotlin.annotation", "Target")) {
                addMember("value", "AnnotationTarget.CLASS")
            }
            addProperty("value", stringType) {
                initializer("\"default\"")
            }
        }

        val generatedCode = annotationSpec.writeToKotlinString()
        val expectedCode = """
            /**
             * A complex test annotation.
             */
            @Target(value = AnnotationTarget.CLASS)
            public annotation class ComplexAnnotation(
                val value: String = "default"
            )""".trimIndent()

        assertEquals(expectedCode, generatedCode.trim())
    }

    @Test
    fun testAnnotationCodeGenerationConsistency() {
        val annotationSpec = KotlinAnnotationTypeSpec("TestAnnotation") {
            addProperty("value", stringType)
        }

        val generatedCode1 = annotationSpec.writeToKotlinString()
        val generatedCode2 = annotationSpec.writeToKotlinString()

        assertEquals(generatedCode1, generatedCode2)
    }

    @Test
    fun testAnnotationSuperclassAlwaysNull() {
        val annotationSpec = KotlinAnnotationTypeSpec("TestAnnotation")
        assertNull(annotationSpec.superclass)
    }

    @Test
    fun testAnnotationSuperinterfacesAlwaysEmpty() {
        val annotationSpec = KotlinAnnotationTypeSpec("TestAnnotation")
        assertEquals(emptyList(), annotationSpec.superinterfaces)
    }

    @Test
    fun testAnnotationKindAlwaysClass() {
        val annotationSpec = KotlinAnnotationTypeSpec("TestAnnotation")
        assertEquals(KotlinTypeSpec.Kind.CLASS, annotationSpec.kind)
    }
}
