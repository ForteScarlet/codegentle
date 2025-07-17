package love.forte.codegentle.kotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KotlinModifiersTests {

    @Test
    fun testKotlinModalityValues() {
        val modalities = KotlinModality.entries

        assertEquals(4, modalities.size)
        assertTrue(modalities.contains(KotlinModality.FINAL))
        assertTrue(modalities.contains(KotlinModality.OPEN))
        assertTrue(modalities.contains(KotlinModality.ABSTRACT))
        assertTrue(modalities.contains(KotlinModality.SEALED))
    }

    @Test
    fun testKotlinModalityOrdering() {
        val modalities = KotlinModality.entries

        // Test that modalities are in expected order
        assertEquals(KotlinModality.FINAL, modalities[0])
        assertEquals(KotlinModality.OPEN, modalities[1])
        assertEquals(KotlinModality.ABSTRACT, modalities[2])
        assertEquals(KotlinModality.SEALED, modalities[3])
    }

    @Test
    fun testKotlinModalityEnumProperties() {
        // Test that each modality has correct enum properties
        assertEquals("FINAL", KotlinModality.FINAL.name)
        assertEquals("OPEN", KotlinModality.OPEN.name)
        assertEquals("ABSTRACT", KotlinModality.ABSTRACT.name)
        assertEquals("SEALED", KotlinModality.SEALED.name)

        assertEquals(0, KotlinModality.FINAL.ordinal)
        assertEquals(1, KotlinModality.OPEN.ordinal)
        assertEquals(2, KotlinModality.ABSTRACT.ordinal)
        assertEquals(3, KotlinModality.SEALED.ordinal)
    }

    @Test
    fun testKotlinVisibilityValues() {
        val visibilities = KotlinVisibility.entries

        assertEquals(4, visibilities.size)
        assertTrue(visibilities.contains(KotlinVisibility.PUBLIC))
        assertTrue(visibilities.contains(KotlinVisibility.PROTECTED))
        assertTrue(visibilities.contains(KotlinVisibility.INTERNAL))
        assertTrue(visibilities.contains(KotlinVisibility.PRIVATE))
    }

    @Test
    fun testKotlinVisibilityOrdering() {
        val visibilities = KotlinVisibility.entries

        // Test that visibilities are in expected order
        assertEquals(KotlinVisibility.PUBLIC, visibilities[0])
        assertEquals(KotlinVisibility.PROTECTED, visibilities[1])
        assertEquals(KotlinVisibility.INTERNAL, visibilities[2])
        assertEquals(KotlinVisibility.PRIVATE, visibilities[3])
    }

    @Test
    fun testKotlinVisibilityEnumProperties() {
        // Test that each visibility has correct enum properties
        assertEquals("PUBLIC", KotlinVisibility.PUBLIC.name)
        assertEquals("PROTECTED", KotlinVisibility.PROTECTED.name)
        assertEquals("INTERNAL", KotlinVisibility.INTERNAL.name)
        assertEquals("PRIVATE", KotlinVisibility.PRIVATE.name)

        assertEquals(0, KotlinVisibility.PUBLIC.ordinal)
        assertEquals(1, KotlinVisibility.PROTECTED.ordinal)
        assertEquals(2, KotlinVisibility.INTERNAL.ordinal)
        assertEquals(3, KotlinVisibility.PRIVATE.ordinal)
    }

    @Test
    fun testKotlinModalityValueOf() {
        assertEquals(KotlinModality.FINAL, KotlinModality.valueOf("FINAL"))
        assertEquals(KotlinModality.OPEN, KotlinModality.valueOf("OPEN"))
        assertEquals(KotlinModality.ABSTRACT, KotlinModality.valueOf("ABSTRACT"))
        assertEquals(KotlinModality.SEALED, KotlinModality.valueOf("SEALED"))
    }

    @Test
    fun testKotlinVisibilityValueOf() {
        assertEquals(KotlinVisibility.PUBLIC, KotlinVisibility.valueOf("PUBLIC"))
        assertEquals(KotlinVisibility.PROTECTED, KotlinVisibility.valueOf("PROTECTED"))
        assertEquals(KotlinVisibility.INTERNAL, KotlinVisibility.valueOf("INTERNAL"))
        assertEquals(KotlinVisibility.PRIVATE, KotlinVisibility.valueOf("PRIVATE"))
    }

    @Test
    fun testModalityAndVisibilityAreDistinct() {
        // Test that modality and visibility enums are independent
        val modalityNames = KotlinModality.entries.map { it.name }.toSet()
        val visibilityNames = KotlinVisibility.entries.map { it.name }.toSet()

        // Only PUBLIC and PRIVATE might overlap conceptually, but they're in different enums
        assertTrue(modalityNames.isNotEmpty())
        assertTrue(visibilityNames.isNotEmpty())

        // Verify specific values exist in correct enums
        assertTrue(modalityNames.contains("FINAL"))
        assertTrue(modalityNames.contains("OPEN"))
        assertTrue(modalityNames.contains("ABSTRACT"))
        assertTrue(modalityNames.contains("SEALED"))

        assertTrue(visibilityNames.contains("PUBLIC"))
        assertTrue(visibilityNames.contains("PROTECTED"))
        assertTrue(visibilityNames.contains("INTERNAL"))
        assertTrue(visibilityNames.contains("PRIVATE"))
    }
}
