package love.forte.codegentle.java

import love.forte.codegentle.java.spec.JavaSimpleTypeSpec
import love.forte.codegentle.java.spec.JavaTypeSpec
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class JavaFileRelationPathTests {

    @Test
    fun testRelationPath() {
        val file = JavaFile(
            "com.example.test",
            JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "Test")
        )

        assertEquals("com/example/test/Test.java", file.toRelativePath())
    }

}
