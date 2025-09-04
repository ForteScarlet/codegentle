package love.forte.codegentle.kotlin

import love.forte.codegentle.kotlin.spec.KotlinSimpleTypeSpec
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class KotlinFileRelationPathTests {

    @Test
    fun testRelationPath() {
        val file = KotlinFile("com.example.test") {
            addType(KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Test"))
        }

        assertEquals("com/example/test/Test.kt", file.toRelativePath())
    }

}
