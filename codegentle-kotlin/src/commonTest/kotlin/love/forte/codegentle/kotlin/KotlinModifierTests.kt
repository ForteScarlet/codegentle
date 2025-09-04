/*
 * Copyright (C) 2025 Forte Scarlet
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
package love.forte.codegentle.kotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KotlinModifierTests {

    @Test
    fun testModifierKeywords() {
        // Test access modifiers
        assertEquals("public", KotlinModifier.PUBLIC.keyword)
        assertEquals("protected", KotlinModifier.PROTECTED.keyword)
        assertEquals("private", KotlinModifier.PRIVATE.keyword)
        assertEquals("internal", KotlinModifier.INTERNAL.keyword)
        
        // Test multiplatform modifiers
        assertEquals("expect", KotlinModifier.EXPECT.keyword)
        assertEquals("actual", KotlinModifier.ACTUAL.keyword)
        
        // Test inheritance modifiers
        assertEquals("final", KotlinModifier.FINAL.keyword)
        assertEquals("open", KotlinModifier.OPEN.keyword)
        assertEquals("abstract", KotlinModifier.ABSTRACT.keyword)
        assertEquals("sealed", KotlinModifier.SEALED.keyword)
        
        // Test other common modifiers
        assertEquals("const", KotlinModifier.CONST.keyword)
        assertEquals("external", KotlinModifier.EXTERNAL.keyword)
        assertEquals("override", KotlinModifier.OVERRIDE.keyword)
        assertEquals("lateinit", KotlinModifier.LATEINIT.keyword)
        assertEquals("tailrec", KotlinModifier.TAILREC.keyword)
        assertEquals("vararg", KotlinModifier.VARARG.keyword)
        assertEquals("suspend", KotlinModifier.SUSPEND.keyword)
        assertEquals("inner", KotlinModifier.INNER.keyword)
        
        // Test type modifiers
        assertEquals("enum", KotlinModifier.ENUM.keyword)
        assertEquals("annotation", KotlinModifier.ANNOTATION.keyword)
        assertEquals("value", KotlinModifier.VALUE.keyword)
        assertEquals("fun", KotlinModifier.FUN.keyword)
        assertEquals("companion", KotlinModifier.COMPANION.keyword)
        assertEquals("data", KotlinModifier.DATA.keyword)
        
        // Test function modifiers
        assertEquals("inline", KotlinModifier.INLINE.keyword)
        assertEquals("noinline", KotlinModifier.NOINLINE.keyword)
        assertEquals("crossinline", KotlinModifier.CROSSINLINE.keyword)
        assertEquals("reified", KotlinModifier.REIFIED.keyword)
        assertEquals("infix", KotlinModifier.INFIX.keyword)
        assertEquals("operator", KotlinModifier.OPERATOR.keyword)
        
        // Test variance modifiers
        assertEquals("in", KotlinModifier.IN.keyword)
        assertEquals("out", KotlinModifier.OUT.keyword)
    }

    @Test
    fun testVisibilityModifiers() {
        assertTrue(KotlinModifier.PUBLIC in VISIBILITY_MODIFIERS)
        assertTrue(KotlinModifier.INTERNAL in VISIBILITY_MODIFIERS)
        assertTrue(KotlinModifier.PROTECTED in VISIBILITY_MODIFIERS)
        assertTrue(KotlinModifier.PRIVATE in VISIBILITY_MODIFIERS)
        
        // Test non-visibility modifiers are not included
        assertFalse(KotlinModifier.ABSTRACT in VISIBILITY_MODIFIERS)
        assertFalse(KotlinModifier.FINAL in VISIBILITY_MODIFIERS)
        assertFalse(KotlinModifier.OPEN in VISIBILITY_MODIFIERS)
        assertFalse(KotlinModifier.SUSPEND in VISIBILITY_MODIFIERS)
    }

    @Test
    fun testKotlinModifierContainer() {
        val container = object : KotlinModifierContainer {
            override val modifiers = setOf(KotlinModifier.PUBLIC, KotlinModifier.ABSTRACT)
        }
        
        assertTrue(container.hasModifier(KotlinModifier.PUBLIC))
        assertTrue(container.hasModifier(KotlinModifier.ABSTRACT))
        assertFalse(container.hasModifier(KotlinModifier.PRIVATE))
        assertFalse(container.hasModifier(KotlinModifier.FINAL))
    }

    @Test
    fun testModifierEnumValues() {
        // Test that all expected modifiers exist
        val allModifiers = KotlinModifier.values()
        
        // Verify we have the expected number of modifiers (this may need updating if new modifiers are added)
        assertTrue(allModifiers.size >= 30) // At least 30 modifiers based on current implementation
        
        // Test some specific modifiers exist
        assertTrue(allModifiers.contains(KotlinModifier.PUBLIC))
        assertTrue(allModifiers.contains(KotlinModifier.SUSPEND))
        assertTrue(allModifiers.contains(KotlinModifier.DATA))
        assertTrue(allModifiers.contains(KotlinModifier.SEALED))
    }

    @Test
    fun testModifierOrdering() {
        // Test that modifiers are defined in the correct order as per Kotlin conventions
        val modifiers = KotlinModifier.values()
        val publicIndex = modifiers.indexOf(KotlinModifier.PUBLIC)
        val protectedIndex = modifiers.indexOf(KotlinModifier.PROTECTED)
        val privateIndex = modifiers.indexOf(KotlinModifier.PRIVATE)
        val internalIndex = modifiers.indexOf(KotlinModifier.INTERNAL)
        
        // Access modifiers should be grouped together
        assertTrue(publicIndex < protectedIndex)
        assertTrue(protectedIndex < privateIndex)
        assertTrue(privateIndex < internalIndex)
        
        // Multiplatform modifiers should come after access modifiers
        val expectIndex = modifiers.indexOf(KotlinModifier.EXPECT)
        val actualIndex = modifiers.indexOf(KotlinModifier.ACTUAL)
        assertTrue(internalIndex < expectIndex)
        assertTrue(expectIndex < actualIndex)
    }
}
