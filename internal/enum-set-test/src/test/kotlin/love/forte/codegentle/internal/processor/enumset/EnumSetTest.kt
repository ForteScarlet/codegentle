package love.forte.codegentle.internal.processor.enumset

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EnumSetTest {

    @Test
    fun testEnumSet() {
        // Test empty set
        val emptySet = TestEnumSet.empty()
        assertTrue(emptySet.isEmpty())
        assertEquals(0, emptySet.size)

        // Test set with elements
        val set = TestEnumSet.of(TestEnum.A, TestEnum.C)
        assertFalse(set.isEmpty())
        assertEquals(2, set.size)
        assertTrue(set.contains(TestEnum.A))
        assertTrue(set.contains(TestEnum.C))
        assertFalse(set.contains(TestEnum.B))

        // Test mutable set
        val mutableSet = set.mutable()
        assertEquals(2, mutableSet.size)
        mutableSet.add(TestEnum.B)
        assertEquals(3, mutableSet.size)
        assertTrue(mutableSet.contains(TestEnum.B))

        // Test immutable conversion
        val immutableSet = mutableSet.immutable()
        assertEquals(3, immutableSet.size)
        assertTrue(immutableSet.contains(TestEnum.B))
    }

    @Test
    fun testInternalEnumSet() {
        // Test empty set
        val emptySet = InternalTestEnumSet.empty()
        assertTrue(emptySet.isEmpty())
        assertEquals(0, emptySet.size)

        // Test set with elements
        val set = InternalTestEnumSet.of(InternalTestEnum.A, InternalTestEnum.C)
        assertFalse(set.isEmpty())
        assertEquals(2, set.size)
        assertTrue(set.contains(InternalTestEnum.A))
        assertTrue(set.contains(InternalTestEnum.C))
        assertFalse(set.contains(InternalTestEnum.B))
    }

    @Test
    fun testBigEnumSet() {
        // Test empty set
        val emptySet = BigTestEnumSet.empty()
        assertTrue(emptySet.isEmpty())
        assertEquals(0, emptySet.size)

        // Test set with elements
        val set = BigTestEnumSet.of(BigTestEnum.A1, BigTestEnum.C5, BigTestEnum.G10)
        assertFalse(set.isEmpty())
        assertEquals(3, set.size)
        assertTrue(set.contains(BigTestEnum.A1))
        assertTrue(set.contains(BigTestEnum.C5))
        assertTrue(set.contains(BigTestEnum.G10))
        assertFalse(set.contains(BigTestEnum.B2))
    }

    @Test
    fun testI32EnumSetOperations() {
        // Test containsAny
        val set1 = TestEnumSet.of(TestEnum.A, TestEnum.B)
        val set2 = TestEnumSet.of(TestEnum.B, TestEnum.C)
        val set3 = TestEnumSet.of(TestEnum.D, TestEnum.E)
        
        assertTrue(set1.containsAny(listOf(TestEnum.A, TestEnum.D)))
        assertTrue(set1.containsAny(listOf(TestEnum.B)))
        assertFalse(set1.containsAny(listOf(TestEnum.D, TestEnum.E)))
        assertFalse(set1.containsAny(emptyList()))
        
        // Test intersect
        val intersection = set1.intersect(set2)
        assertEquals(1, intersection.size)
        assertTrue(intersection.contains(TestEnum.B))
        
        val emptyIntersection = set1.intersect(set3)
        assertTrue(emptyIntersection.isEmpty())
        
        // Test union
        val union = set1.union(set2)
        assertEquals(3, union.size)
        assertTrue(union.contains(TestEnum.A))
        assertTrue(union.contains(TestEnum.B))
        assertTrue(union.contains(TestEnum.C))
        
        // Test difference
        val difference = set1.difference(set2)
        assertEquals(1, difference.size)
        assertTrue(difference.contains(TestEnum.A))
        assertFalse(difference.contains(TestEnum.B))
        
        val noDifference = set1.difference(set1)
        assertTrue(noDifference.isEmpty())
    }

    @Test
    fun testInternalEnumSetOperations() {
        // Test containsAny
        val set1 = InternalTestEnumSet.of(InternalTestEnum.A, InternalTestEnum.B)
        val set2 = InternalTestEnumSet.of(InternalTestEnum.B, InternalTestEnum.C)
        val set3 = InternalTestEnumSet.of(InternalTestEnum.C)
        
        assertTrue(set1.containsAny(listOf(InternalTestEnum.A)))
        assertTrue(set1.containsAny(listOf(InternalTestEnum.B, InternalTestEnum.C)))
        assertFalse(set1.containsAny(listOf(InternalTestEnum.C)))
        
        // Test intersect
        val intersection = set1.intersect(set2)
        assertEquals(1, intersection.size)
        assertTrue(intersection.contains(InternalTestEnum.B))
        
        // Test union
        val union = set1.union(set3)
        assertEquals(3, union.size)
        assertTrue(union.contains(InternalTestEnum.A))
        assertTrue(union.contains(InternalTestEnum.B))
        assertTrue(union.contains(InternalTestEnum.C))
        
        // Test difference
        val difference = set2.difference(set1)
        assertEquals(1, difference.size)
        assertTrue(difference.contains(InternalTestEnum.C))
    }

    @Test
    fun testI64EnumSetOperations() {
        // Test containsAny
        val set1 = MediumTestEnumSet.of(MediumTestEnum.A1, MediumTestEnum.B5, MediumTestEnum.E5)
        val set2 = MediumTestEnumSet.of(MediumTestEnum.B5, MediumTestEnum.C3, MediumTestEnum.D8)
        val set3 = MediumTestEnumSet.of(MediumTestEnum.C1, MediumTestEnum.D1)
        
        assertTrue(set1.containsAny(listOf(MediumTestEnum.A1, MediumTestEnum.C1)))
        assertTrue(set1.containsAny(listOf(MediumTestEnum.E5)))
        assertFalse(set1.containsAny(listOf(MediumTestEnum.C3, MediumTestEnum.D8)))
        assertFalse(set1.containsAny(emptyList()))
        
        // Test intersect
        val intersection = set1.intersect(set2)
        assertEquals(1, intersection.size)
        assertTrue(intersection.contains(MediumTestEnum.B5))
        
        val emptyIntersection = set1.intersect(set3)
        assertTrue(emptyIntersection.isEmpty())
        
        // Test union
        val union = set1.union(set2)
        assertEquals(5, union.size)
        assertTrue(union.contains(MediumTestEnum.A1))
        assertTrue(union.contains(MediumTestEnum.B5))
        assertTrue(union.contains(MediumTestEnum.E5))
        assertTrue(union.contains(MediumTestEnum.C3))
        assertTrue(union.contains(MediumTestEnum.D8))
        
        // Test difference
        val difference = set1.difference(set2)
        assertEquals(2, difference.size)
        assertTrue(difference.contains(MediumTestEnum.A1))
        assertTrue(difference.contains(MediumTestEnum.E5))
        assertFalse(difference.contains(MediumTestEnum.B5))
        
        val noDifference = set1.difference(set1)
        assertTrue(noDifference.isEmpty())
    }

    @Test
    fun testBigEnumSetOperations() {
        // Test containsAny
        val set1 = BigTestEnumSet.of(BigTestEnum.A1, BigTestEnum.B5, BigTestEnum.G10)
        val set2 = BigTestEnumSet.of(BigTestEnum.B5, BigTestEnum.C3, BigTestEnum.F8)
        val set3 = BigTestEnumSet.of(BigTestEnum.D1, BigTestEnum.E7)
        
        assertTrue(set1.containsAny(listOf(BigTestEnum.A1, BigTestEnum.D1)))
        assertTrue(set1.containsAny(listOf(BigTestEnum.G10)))
        assertFalse(set1.containsAny(listOf(BigTestEnum.C3, BigTestEnum.F8)))
        
        // Test intersect
        val intersection = set1.intersect(set2)
        assertEquals(1, intersection.size)
        assertTrue(intersection.contains(BigTestEnum.B5))
        
        val emptyIntersection = set1.intersect(set3)
        assertTrue(emptyIntersection.isEmpty())
        
        // Test union
        val union = set1.union(set2)
        assertEquals(5, union.size)
        assertTrue(union.contains(BigTestEnum.A1))
        assertTrue(union.contains(BigTestEnum.B5))
        assertTrue(union.contains(BigTestEnum.G10))
        assertTrue(union.contains(BigTestEnum.C3))
        assertTrue(union.contains(BigTestEnum.F8))
        
        // Test difference
        val difference = set1.difference(set2)
        assertEquals(2, difference.size)
        assertTrue(difference.contains(BigTestEnum.A1))
        assertTrue(difference.contains(BigTestEnum.G10))
        assertFalse(difference.contains(BigTestEnum.B5))
    }

    @Test
    fun testContainerEnumSet() {
        // Test that the container interface is generated with generic type
        val container = TestEnumBuilderContainerImpl()

        // Test adding modifiers
        container.addModifier(ContainerTestEnum.ONE)
        container.addModifiers(ContainerTestEnum.TWO, ContainerTestEnum.THREE)
    }

    // Helper implementation for the test
    private class TestEnumBuilderContainerImpl : TestEnumBuilderContainer<TestEnumBuilderContainerImpl> {
        private val modifiers = mutableSetOf<ContainerTestEnum>()

        override fun addModifier(modifier: ContainerTestEnum): TestEnumBuilderContainerImpl {
            modifiers.add(modifier)
            return this
        }

        override fun addModifiers(vararg modifiers: ContainerTestEnum): TestEnumBuilderContainerImpl {
            this.modifiers.addAll(modifiers)
            return this
        }

        override fun addModifiers(modifiers: Iterable<ContainerTestEnum>): TestEnumBuilderContainerImpl {
            this.modifiers.addAll(modifiers)
            return this
        }
    }

    @Test
    fun testOperatorsEnumSet() {
        // Test that the value class is generated
        val container = OperatorsTestEnumBuilderContainerImpl()

        // Test using the value class directly
        val operators = OperatorsTestEnumModifiers(container)
        operators.alpha()
        operators.beta()
        operators.gamma()
    }

    // Helper implementation for the test
    private class OperatorsTestEnumBuilderContainerImpl : OperatorsTestEnumBuilderContainer<OperatorsTestEnumBuilderContainerImpl> {
        private val modifiers = mutableSetOf<OperatorsTestEnum>()

        override fun addModifier(modifier: OperatorsTestEnum): OperatorsTestEnumBuilderContainerImpl {
            modifiers.add(modifier)
            return this
        }

        override fun addModifiers(vararg modifiers: OperatorsTestEnum): OperatorsTestEnumBuilderContainerImpl {
            this.modifiers.addAll(modifiers)
            return this
        }

        override fun addModifiers(modifiers: Iterable<OperatorsTestEnum>): OperatorsTestEnumBuilderContainerImpl {
            this.modifiers.addAll(modifiers)
            return this
        }
    }

    @Test
    fun testCustomAdderEnumSet() {
        // Test that the container interface is generated with custom adder function names
        val container = CustomAdderTestEnumBuilderContainerImpl()

        // Test adding elements using the custom adder functions
        container.addElement(CustomAdderTestEnum.ONE)
        container.addElements(CustomAdderTestEnum.TWO, CustomAdderTestEnum.THREE)

        // Test using the value class directly
        val operators = CustomAdderTestEnumModifiers(container)
        operators.one()
        operators.two()
        operators.three()
    }

    // Helper implementation for the test
    private class CustomAdderTestEnumBuilderContainerImpl : CustomAdderTestEnumBuilderContainer<CustomAdderTestEnumBuilderContainerImpl> {
        private val elements = mutableSetOf<CustomAdderTestEnum>()

        override fun addElement(modifier: CustomAdderTestEnum): CustomAdderTestEnumBuilderContainerImpl {
            elements.add(modifier)
            return this
        }

        override fun addElements(vararg modifiers: CustomAdderTestEnum): CustomAdderTestEnumBuilderContainerImpl {
            this.elements.addAll(modifiers)
            return this
        }

        override fun addElements(modifiers: Iterable<CustomAdderTestEnum>): CustomAdderTestEnumBuilderContainerImpl {
            this.elements.addAll(modifiers)
            return this
        }
    }

    @Test
    fun testKeywordEnumSet() {
        // Test that the value class is generated with backticks for Kotlin keywords
        val container = KeywordTestEnumBuilderContainerImpl()

        // Test using the value class directly
        // These function calls would cause compilation errors if the keywords weren't properly escaped with backticks
        val operators = KeywordTestEnumModifiers(container)

        // Call methods that should be wrapped in backticks because they are Kotlin keywords
        operators.`fun`()
        operators.`in`()
        operators.`is`()
        operators.`as`()
        operators.`object`()
        operators.`class`()
        operators.`interface`()
        operators.`val`()
        operators.`var`()
        operators.`when`()
        operators.`if`()
        operators.`else`()
        operators.`return`()
    }

    // Helper implementation for the test
    private class KeywordTestEnumBuilderContainerImpl : KeywordTestEnumBuilderContainer<KeywordTestEnumBuilderContainerImpl> {
        private val modifiers = mutableSetOf<KeywordTestEnum>()

        override fun addModifier(modifier: KeywordTestEnum): KeywordTestEnumBuilderContainerImpl {
            modifiers.add(modifier)
            return this
        }

        override fun addModifiers(vararg modifiers: KeywordTestEnum): KeywordTestEnumBuilderContainerImpl {
            this.modifiers.addAll(modifiers)
            return this
        }

        override fun addModifiers(modifiers: Iterable<KeywordTestEnum>): KeywordTestEnumBuilderContainerImpl {
            this.modifiers.addAll(modifiers)
            return this
        }
    }
}
