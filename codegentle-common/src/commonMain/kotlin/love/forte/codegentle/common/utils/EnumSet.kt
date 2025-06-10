package love.forte.codegentle.common.utils

/**
 * A bitset-based enum set.
 *
 * @author ForteScarlet
 */
@InternalEnumSetApi
public interface EnumSet<E : Enum<E>> : Set<E> {
    override fun contains(element: E): Boolean

    override fun containsAll(elements: Collection<E>): Boolean

    override fun isEmpty(): Boolean

    override fun iterator(): Iterator<E>

    override val size: Int
}

/**
 * A mutable [EnumSet].
 */
@InternalEnumSetApi
public interface MutableEnumSet<E : Enum<E>> : EnumSet<E>, MutableSet<E>

/**
 * Base class for enum sets using 32-bit storage.
 * Uses a UInt bitset to efficiently store and manage enum values.
 */
@InternalEnumSetApi
public abstract class I32EnumSet<E : Enum<E>>(protected open var bitset: UInt = 0u) : MutableEnumSet<E> {
    protected abstract val entries: List<E>

    /**
     * Checks if the given enum element is present in the set.
     */
    override fun contains(element: E): Boolean = bitset and (1u shl element.ordinal) != 0u

    override fun containsAll(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is I32EnumSet<*>) {
            return (bitset and elements.bitset) == elements.bitset
        }
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean = bitset == 0u

    override val size: Int get() = bitset.countOneBits()

    override fun iterator(): MutableIterator<E> = object : MutableIterator<E> {
        private var currentBit = 0
        private var lastReturnedBit = -1

        override fun hasNext(): Boolean {
            while (currentBit < entries.size) {
                if (bitset and (1u shl currentBit) != 0u) return true
                currentBit++
            }
            return false
        }

        override fun next(): E {
            if (!hasNext()) throw NoSuchElementException()
            lastReturnedBit = currentBit
            return entries[currentBit++]
        }

        override fun remove() {
            check(lastReturnedBit != -1) { "next() must be called before remove()" }
            bitset = bitset and (1u shl lastReturnedBit).inv()
            lastReturnedBit = -1
        }
    }

    /**
     * Adds the given enum element to the set.
     * @return true if the element was added, false if it was already present
     */
    override fun add(element: E): Boolean {
        val mask = 1u shl element.ordinal
        val old = bitset
        bitset = bitset or mask
        return bitset != old
    }

    override fun addAll(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is I32EnumSet<*>) {
            val old = bitset
            bitset = bitset or elements.bitset
            return bitset != old
        }
        val old = bitset
        elements.forEach { add(it) }
        return bitset != old
    }

    override fun clear() {
        bitset = 0u
    }

    /**
     * Removes the given enum element from the set.
     * @return true if the element was removed, false if it wasn't present
     */
    override fun remove(element: E): Boolean {
        val mask = 1u shl element.ordinal
        val old = bitset
        bitset = bitset and mask.inv()
        return bitset != old
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is I32EnumSet<*>) {
            val old = bitset
            bitset = bitset and elements.bitset.inv()
            return bitset != old
        }
        val old = bitset
        elements.forEach { remove(it) }
        return bitset != old
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is I32EnumSet<*>) {
            val old = bitset
            bitset = bitset and elements.bitset
            return bitset != old
        }
        val old = bitset
        bitset = elements.fold(0u) { acc, e -> acc or (1u shl e.ordinal) }
        return bitset != old
    }
}

/**
 * Base class for enum sets using 64-bit storage.
 * Uses a ULong bitset to efficiently store and manage enum values.
 */
@InternalEnumSetApi
public abstract class I64EnumSet<E : Enum<E>>(protected open var bitset: ULong = 0u) : MutableEnumSet<E> {
    protected abstract val entries: List<E>

    /**
     * Checks if the given enum element is present in the set.
     */
    override fun contains(element: E): Boolean = bitset and (1uL shl element.ordinal) != 0uL

    override fun containsAll(elements: Collection<E>): Boolean {
        if (elements is EnumSet<*>) {
            // Optimize when source is also a bitset-based enum set
            if (elements is I64EnumSet<*>) {
                return (bitset and elements.bitset) == elements.bitset
            }
        }
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean = bitset == 0uL

    override val size: Int get() = bitset.countOneBits()

    override fun iterator(): MutableIterator<E> = object : MutableIterator<E> {
        private var currentBit = 0
        private var lastReturnedBit = -1

        override fun hasNext(): Boolean {
            while (currentBit < entries.size) {
                if (bitset and (1uL shl currentBit) != 0uL) return true
                currentBit++
            }
            return false
        }

        override fun next(): E {
            if (!hasNext()) throw NoSuchElementException()
            lastReturnedBit = currentBit
            return entries[currentBit++]
        }

        override fun remove() {
            check(lastReturnedBit != -1) { "next() must be called before remove()" }
            bitset = bitset and (1uL shl lastReturnedBit).inv()
            lastReturnedBit = -1
        }
    }

    /**
     * Adds the given enum element to the set.
     * @return true if the element was added, false if it was already present
     */
    override fun add(element: E): Boolean {
        val mask = 1uL shl element.ordinal
        val old = bitset
        bitset = bitset or mask
        return bitset != old
    }

    override fun addAll(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is I64EnumSet<*>) {
            val old = bitset
            bitset = bitset or elements.bitset
            return bitset != old
        }
        val old = bitset
        elements.forEach { add(it) }
        return bitset != old
    }

    override fun clear() {
        bitset = 0uL
    }

    /**
     * Removes the given enum element from the set.
     * @return true if the element was removed, false if it wasn't present
     */
    override fun remove(element: E): Boolean {
        val mask = 1uL shl element.ordinal
        val old = bitset
        bitset = bitset and mask.inv()
        return bitset != old
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is I64EnumSet<*>) {
            val old = bitset
            bitset = bitset and elements.bitset.inv()
            return bitset != old
        }
        val old = bitset
        elements.forEach { remove(it) }
        return bitset != old
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is I64EnumSet<*>) {
            val old = bitset
            bitset = bitset and elements.bitset
            return bitset != old
        }
        val old = bitset
        bitset = elements.fold(0uL) { acc, e -> acc or (1uL shl e.ordinal) }
        return bitset != old
    }
}

/**
 * Base class for enum sets requiring more than 64 bits of storage.
 * Uses a LongArray to store bit flags for enum values.
 */
@InternalEnumSetApi
public abstract class BigEnumSet<E : Enum<E>>(
    protected open var bitset: LongArray = LongArray(0)
) : MutableEnumSet<E> {
    protected abstract val entries: List<E>

    /**
     * Gets array index and bit position for an enum ordinal
     */
    private fun getIndexAndBit(ordinal: Int): Pair<Int, Int> {
        return ordinal / 64 to ordinal % 64
    }

    /**
     * Checks if the given enum element is present in the set.
     */
    override fun contains(element: E): Boolean {
        val (index, bit) = getIndexAndBit(element.ordinal)
        return if (index < bitset.size) {
            (bitset[index] and (1L shl bit)) != 0L
        } else false
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is BigEnumSet<*>) {
            val eb = elements.bitset
            if (eb.size > bitset.size) return false
            for (i in eb.indices) {
                if ((bitset[i] and eb[i]) != eb[i]) return false
            }
            return true
        }
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean = bitset.all { it == 0L }

    override val size: Int get() = bitset.sumOf { it.countOneBits() }

    override fun iterator(): MutableIterator<E> = object : MutableIterator<E> {
        private var currentIndex = 0
        private var currentBit = 0
        private var lastReturnedIndex = -1
        private var lastReturnedBit = -1

        override fun hasNext(): Boolean {
            while (currentIndex < bitset.size) {
                while (currentBit < 64) {
                    if (bitset[currentIndex] and (1L shl currentBit) != 0L) {
                        return true
                    }
                    currentBit++
                }
                currentIndex++
                currentBit = 0
            }
            return false
        }

        override fun next(): E {
            if (!hasNext()) throw NoSuchElementException()
            lastReturnedIndex = currentIndex
            lastReturnedBit = currentBit
            val ordinal = currentIndex * 64 + currentBit
            currentBit++
            return entries[ordinal]
        }

        override fun remove() {
            check(lastReturnedIndex != -1) { "next() must be called before remove()" }
            bitset[lastReturnedIndex] = bitset[lastReturnedIndex] and (1L shl lastReturnedBit).inv()
            lastReturnedIndex = -1
            lastReturnedBit = -1
        }
    }

    /**
     * Adds the given enum element to the set.
     * Expands storage array if needed.
     * @return true if the element was added, false if it was already present
     */
    override fun add(element: E): Boolean {
        val (index, bit) = getIndexAndBit(element.ordinal)
        if (index >= bitset.size) {
            bitset = bitset.copyOf(index + 1)
        }
        val mask = 1L shl bit
        val old = bitset[index]
        bitset[index] = old or mask
        return bitset[index] != old
    }

    override fun addAll(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is BigEnumSet<*>) {
            val eb = elements.bitset
            if (eb.size > bitset.size) {
                bitset = bitset.copyOf(eb.size)
            }
            var modified = false
            for (i in eb.indices) {
                val old = bitset[i]
                bitset[i] = old or eb[i]
                if (bitset[i] != old) modified = true
            }
            return modified
        }
        var modified = false
        elements.forEach { if (add(it)) modified = true }
        return modified
    }

    override fun clear() {
        bitset = LongArray(0)
    }

    /**
     * Removes the given enum element from the set.
     * @return true if the element was removed, false if it wasn't present
     */
    override fun remove(element: E): Boolean {
        val (index, bit) = getIndexAndBit(element.ordinal)
        if (index >= bitset.size) return false
        val mask = 1L shl bit
        val old = bitset[index]
        bitset[index] = old and mask.inv()
        return bitset[index] != old
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is BigEnumSet<*>) {
            val eb = elements.bitset
            val size = minOf(bitset.size, eb.size)
            var modified = false
            for (i in 0 until size) {
                val old = bitset[i]
                bitset[i] = old and eb[i].inv()
                if (bitset[i] != old) modified = true
            }
            return modified
        }
        var modified = false
        elements.forEach { if (remove(it)) modified = true }
        return modified
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is BigEnumSet<*>) {
            val eb = elements.bitset
            val size = minOf(bitset.size, eb.size)
            var modified = false
            for (i in 0 until size) {
                val old = bitset[i]
                bitset[i] = old and eb[i]
                if (bitset[i] != old) modified = true
            }
            // Clear any remaining bits
            for (i in size until bitset.size) {
                if (bitset[i] != 0L) modified = true
                bitset[i] = 0L
            }
            return modified
        }
        var modified = false
        val newBitset = LongArray(bitset.size)
        elements.forEach { e ->
            val (index, bit) = getIndexAndBit(e.ordinal)
            if (index < bitset.size) {
                val mask = 1L shl bit
                if ((bitset[index] and mask) != 0L) {
                    newBitset[index] = newBitset[index] or mask
                    modified = true
                }
            }
        }
        if (modified) bitset = newBitset
        return modified
    }
}
