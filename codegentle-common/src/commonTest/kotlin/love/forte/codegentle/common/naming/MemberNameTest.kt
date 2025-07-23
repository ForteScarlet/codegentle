package love.forte.codegentle.common.naming

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 *
 * @author ForteScarlet
 */
class MemberNameTest {

    @Test
    fun memberNameToString() {
        // Test with package and name only
        val member1 = MemberName("java.lang", "System")
        assertEquals("java.lang.System", member1.canonicalName)
        assertEquals(PackageName(listOf("java", "lang")), member1.packageName)
        assertEquals("System", member1.name)
        assertNull(member1.enclosingClassName)
    }

    @Test
    fun memberNameWithEnclosingClass() {
        // Test with enclosing class
        val className = ClassName("java.util", "Map")
        val member2 = MemberName(className, "Entry")
        assertEquals("java.util.Map.Entry", member2.canonicalName)
        assertEquals(PackageName(listOf("java", "util")), member2.packageName)
        assertEquals("Entry", member2.name)
        assertEquals(className, member2.enclosingClassName)
    }

    @Test
    fun memberNameEquality() {
        val member1 = MemberName("java.lang", "System")
        val member2 = MemberName("java.lang", "System")
        val member3 = MemberName("java.util", "List")

        assertEquals(member1, member2)
        assertEquals(member1.hashCode(), member2.hashCode())
        assertEquals(true, member1.contentEquals(member2))
        assertEquals(false, member1.contentEquals(member3))
    }

    @Test
    fun memberNameFactoryMethods() {
        // Test different factory methods
        val packageName = "java.lang".parseToPackageName()
        val member1 = MemberName(packageName, "String")
        val member2 = MemberName("java.lang", "String")
        
        assertEquals(member1, member2)
        assertEquals(member1.canonicalName, member2.canonicalName)
    }

    @Test
    fun memberNameWithComplexEnclosingClass() {
        // Test with nested class as enclosing
        val outerClass = ClassName("java.util", "Map")
        val nestedClass = outerClass.nestedClass("Entry")
        val member = MemberName(nestedClass, "KEY")
        
        assertEquals("java.util.Map.Entry.KEY", member.canonicalName)
        assertEquals("KEY", member.name)
        assertEquals(nestedClass, member.enclosingClassName)
    }
}
