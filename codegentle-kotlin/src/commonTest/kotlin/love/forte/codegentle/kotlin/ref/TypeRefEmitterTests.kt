package love.forte.codegentle.kotlin.ref

import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.status
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
        val typeRef = className.kotlinRef {
            status {
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
        
        val typeRef = className.kotlinRef {
            status {
                addAnnotationRef(annotation)
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
        
        val typeRef = className.kotlinRef {
            status {
                addAnnotationRef(annotation)
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
        
        val typeRef = className.kotlinRef {
            status {
                addAnnotationRef(annotation1)
                addAnnotationRef(annotation2)
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
        
        val typeRef = className.kotlinRef {
            status {
                addAnnotationRef(annotation1)
                addAnnotationRef(annotation2)
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
        
        val typeRef = className.kotlinRef {
            status {
                addAnnotationRef(annotation)
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
        val typeRef = className.kotlinRef()
        
        val result = buildString {
            val writer = KotlinCodeWriter.create(this)
            writer.emit(typeRef)
        }
        
        assertEquals("com.example.TestClass", result)
    }
}
