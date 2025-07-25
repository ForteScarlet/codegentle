package love.forte.codegentle.java

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.addAnnotation
import love.forte.codegentle.common.ref.status
import love.forte.codegentle.java.ref.javaRef
import love.forte.codegentle.java.writer.writeToJavaString
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class JavaTypeRefTests {
    @Test
    fun testJavaClassNameRefString() {
        val className = ClassName("com.example", "Example")
        val ref = className.javaRef {
            status {
                addAnnotation(ClassName("com.example.anno", "Anno"))
            }
        }

        assertEquals(
            "@com.example.anno.Anno com.example.Example",
            ref.writeToJavaString()
        )
    }
}
