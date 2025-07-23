package love.forte.codegentle.common.naming

import love.forte.codegentle.common.ref.TypeNameRefStatus
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.internal.TypeRefImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class ParameterizedTypeNameToStringTest {

    // Simple mock implementation for testing
    private object MockTypeNameRefStatus : TypeNameRefStatus

    private fun TypeName.mockRef(): TypeRef<TypeName> {
        return TypeRefImpl(this, MockTypeNameRefStatus)
    }

    @Test
    fun testSimpleParameterizedType() {
        val stringClassName = ClassName("kotlin", "String")
        val listClassName = ClassName("kotlin.collections", "List")
        val stringTypeRef = stringClassName.mockRef()

        val listOfString = ParameterizedTypeName(listClassName, listOf(stringTypeRef))

        assertEquals("kotlin.collections.List<kotlin.String>", listOfString.toString())
    }

    @Test
    fun testParameterizedTypeWithMultipleArguments() {
        val stringClassName = ClassName("kotlin", "String")
        val intClassName = ClassName("kotlin", "Int")
        val mapClassName = ClassName("kotlin.collections", "Map")

        val stringTypeRef = stringClassName.mockRef()
        val intTypeRef = intClassName.mockRef()

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

        val stringTypeRef = stringClassName.mockRef()
        val intTypeRef = intClassName.mockRef()

        val mapOfStringInt = ParameterizedTypeName(mapClassName, listOf(stringTypeRef, intTypeRef))
        val mapTypeRef = mapOfStringInt.mockRef()
        val listOfMap = ParameterizedTypeName(listClassName, listOf(mapTypeRef))

        assertEquals("kotlin.collections.List<kotlin.collections.Map<kotlin.String, kotlin.Int>>", listOfMap.toString())
    }

    @Test
    fun testNestedClassParameterizedType() {
        val outerClassName = ClassName("com.example", "Outer")
        val stringClassName = ClassName("kotlin", "String")
        val stringTypeRef = stringClassName.mockRef()

        val outerParameterized = ParameterizedTypeName(outerClassName, listOf(stringTypeRef))
        val innerParameterized = outerParameterized.nestedClass("Inner", listOf(stringTypeRef))

        assertEquals("com.example.Outer<kotlin.String>.Inner<kotlin.String>", innerParameterized.toString())
    }
}
