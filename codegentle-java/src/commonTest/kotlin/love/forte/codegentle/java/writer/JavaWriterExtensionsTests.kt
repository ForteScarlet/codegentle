package love.forte.codegentle.java.writer

import love.forte.codegentle.common.code.CodePart.Companion.string
import love.forte.codegentle.common.code.CodePart.Companion.type
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.emitLiteral
import love.forte.codegentle.common.code.emitString
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.status
import love.forte.codegentle.java.ref.javaRef
import love.forte.codegentle.java.strategy.ToStringJavaWriteStrategy
import love.forte.codegentle.java.strategy.WrapperJavaWriteStrategy
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for JavaWriter extension functions like writeToJavaString.
 */
class JavaWriterExtensionsTests {

    @Test
    fun testCodeValueWriteToJavaString() {
        val codeValue = CodeValue("%V.out.println(%V)") {
            emitType(ClassName("java.lang", "System"))
            emitString("Hello, World!")
        }

        assertEquals("System.out.println(\"Hello, World!\")", codeValue.writeToJavaString())
    }

    @Test
    fun testTypeNameWriteToJavaString() {
        val className = ClassName("java.util", "List")

        assertEquals("java.util.List", className.writeToJavaString())
    }

    private val javaLangToStringStrategy = object : WrapperJavaWriteStrategy(
        ToStringJavaWriteStrategy
    ) {
        override fun omitJavaLangPackage(): Boolean = false
    }

    @Test
    fun testAnnotationRefWriteToJavaString() {
        val annotationRef = ClassName("java.lang", "Override").annotationRef()

        assertEquals("@Override", annotationRef.writeToJavaString())
        assertEquals(
            "@java.lang.Override", annotationRef.writeToJavaString(javaLangToStringStrategy)
        )
    }

    @Test
    fun testTypeRefWriteToJavaString() {
        val typeRef = ClassName("java.lang", "String").javaRef()

        assertEquals("String", typeRef.writeToJavaString())
        assertEquals(
            "java.lang.String",
            typeRef.writeToJavaString(javaLangToStringStrategy)
        )
    }

    @Test
    fun testComplexCodeValueWriteToJavaString() {
        val codeValue = CodeValue("%V %V = %V;") {
            emitType(ClassName("java.lang", "String"))
            emitLiteral("name")
            emitString("Hello World")
        }

        assertEquals("String name = \"Hello World\";", codeValue.writeToJavaString())
        assertEquals(
            "java.lang.String name = \"Hello World\";",
            codeValue.writeToJavaString(javaLangToStringStrategy)
        )
    }

    @Test
    fun testCodeValueWithMultipleFormatsWriteToJavaString() {
        val codeValue = CodeValue("for (%V %V : %V) {\n  %V.%V(%V);\n}") {
            emitType(ClassName("java.lang", "String"))
            emitLiteral("item")
            emitLiteral("items")
            emitLiteral("System")
            emitLiteral("out.println")
            emitLiteral("item")
        }

        val expected = "for (java.lang.String item : items) {\n  System.out.println(item);\n}"
        assertEquals(expected, codeValue.writeToJavaString(javaLangToStringStrategy))
    }

    @Test
    fun testAnnotationRefWithMembersWriteToJavaString() {
        val annotationRef = ClassName("javax.annotation", "Resource").annotationRef {
            addMember("name", "%V", string("myResource"))
            addMember(
                "type", "%V.class",
                type(ClassName("java.lang", "String"))
            )
        }

        assertEquals(
            "@javax.annotation.Resource(name = \"myResource\", type = String.class)",
            annotationRef.writeToJavaString()
        )

        assertEquals(
            "@javax.annotation.Resource(name = \"myResource\", type = java.lang.String.class)",
            annotationRef.writeToJavaString(javaLangToStringStrategy)
        )
    }

    @Test
    fun testTypeRefWithAnnotationsWriteToJavaString() {
        val annotationRef = ClassName("javax.annotation", "Nonnull").annotationRef()
        val typeRef = ClassName("java.lang", "String").javaRef {
            status {
                addAnnotation(annotationRef)
            }
        }

        assertEquals("@javax.annotation.Nonnull String", typeRef.writeToJavaString())
        assertEquals(
            "@javax.annotation.Nonnull java.lang.String",
            typeRef.writeToJavaString(javaLangToStringStrategy)
        )
    }
}
