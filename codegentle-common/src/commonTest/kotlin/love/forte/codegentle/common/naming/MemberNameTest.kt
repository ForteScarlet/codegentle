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
package love.forte.codegentle.common.naming

import kotlin.test.*

/**
 * Tests for [MemberName].
 *
 * @author ForteScarlet
 */
class MemberNameTest {

    @Test
    fun memberNameToString() {
        // Test a simple member name with package
        val memberName1 = MemberName(packageName = "java.util", name = "sort")
        assertEquals("java.util.sort", memberName1.canonicalName)
        assertEquals(PackageName(listOf("java", "util")), memberName1.packageName)
        assertEquals("sort", memberName1.name)
        assertNull(memberName1.enclosingClassName)

        // Test a member name with enclosing class
        val className = ClassName(packageName = "java.util", simpleName = "Collections")
        val memberName2 = MemberName(enclosingClassName = className, name = "sort")
        assertEquals("java.util.Collections.sort", memberName2.canonicalName)
        assertEquals(PackageName(listOf("java", "util")), memberName2.packageName)
        assertEquals("sort", memberName2.name)
        assertEquals(className, memberName2.enclosingClassName)
    }

    @Test
    fun memberNameEquality() {
        val memberName1 = MemberName(packageName = "java.util", name = "sort")
        val memberName2 = MemberName(packageName = "java.util", name = "sort")
        val memberName3 = MemberName(packageName = "java.lang", name = "sort")

        // Test equality
        assertEquals(memberName1, memberName2)
        assertNotEquals(memberName1, memberName3)

        // Test content equality
        assertTrue(memberName1 contentEquals memberName2)
        assertFalse(memberName1 contentEquals memberName3)
    }
    
    @Test
    fun memberNameComparable() {
        val memberName1 = MemberName(packageName = "java.util", name = "sort")
        val memberName2 = MemberName(packageName = "java.util", name = "sort")
        val memberName3 = MemberName(packageName = "java.lang", name = "sort")
        val memberName4 = MemberName(packageName = "java.util", name = "reverse")
        
        // Test compareTo
        assertEquals(0, memberName1.compareTo(memberName2))
        assertTrue(memberName1.compareTo(memberName3) > 0) // "java.util" > "java.lang"
        assertTrue(memberName3.compareTo(memberName1) < 0) // "java.lang" < "java.util"
        assertTrue(memberName1.compareTo(memberName4) > 0) // "sort" > "reverse"
        assertTrue(memberName4.compareTo(memberName1) < 0) // "reverse" < "sort"
        
        // Test with enclosing class
        val className1 = ClassName(packageName = "java.util", simpleName = "Collections")
        val className2 = ClassName(packageName = "java.util", simpleName = "Arrays")
        val memberName5 = MemberName(enclosingClassName = className1, name = "sort")
        val memberName6 = MemberName(enclosingClassName = className2, name = "sort")
        
        assertTrue(memberName5.compareTo(memberName6) > 0) // "Collections" > "Arrays"
        assertTrue(memberName6.compareTo(memberName5) < 0) // "Arrays" < "Collections"
    }
    
    @Test
    fun memberNameAsTypeName() {
        // Verify MemberName can be used where TypeName is expected
        val memberName = MemberName(packageName = "java.util", name = "sort")
        val typeName: TypeName = memberName
        
        // Just verifying it compiles is enough for this test
        assertTrue(typeName is MemberName)
        assertEquals("java.util.sort", typeName.canonicalName)
    }
}
