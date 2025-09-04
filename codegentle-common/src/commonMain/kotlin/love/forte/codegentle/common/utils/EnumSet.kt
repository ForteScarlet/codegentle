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
package love.forte.codegentle.common.utils

import kotlin.jvm.JvmInline

@RequiresOptIn("This api is internal. It may be changed in the future.")
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
public annotation class InternalEnumSetApi

/**
 * A bitset-based enum set.
 *
 * @author ForteScarlet
 */
@InternalEnumSetApi
public interface EnumSet<E : Enum<E>> : Set<E> {
    override fun contains(element: E): Boolean

    override fun containsAll(elements: Collection<E>): Boolean

    public fun containsAny(elements: Collection<E>): Boolean

    /**
     * Returns a new set containing elements present in both this set and [other].
     */
    public fun intersect(other: Set<E>): Set<E>

    /**
     * Returns a new set containing elements present in either this set or [other].
     */
    public fun union(other: Set<E>): Set<E>

    /**
     * Returns a new set containing elements present in this set but not in [other].
     */
    public fun difference(other: Set<E>): Set<E>

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

    protected abstract fun newBy(bitset: UInt): I32EnumSet<E>

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

    override fun containsAny(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is I32EnumSet<*>) {
            return (bitset and elements.bitset) != 0u
        }
        return elements.any { contains(it) }
    }

    override fun intersect(other: Set<E>): Set<E> {
        if (other is I32EnumSet<*>) {
            return newBy(bitset and other.bitset)
        }
        // Fallback for other EnumSet implementations
        val result = newBy(0u)
        forEach { e ->
            if (other.contains(e)) {
                result.add(e)
            }
        }
        return result
    }

    override fun union(other: Set<E>): Set<E> {
        if (other is I32EnumSet<*>) {
            return newBy(bitset or other.bitset)
        }
        // Fallback for other EnumSet implementations
        val result = newBy(bitset)
        other.forEach { result.add(it) }
        return result
    }

    override fun difference(other: Set<E>): Set<E> {
        if (other is I32EnumSet<*>) {
            return newBy(bitset and other.bitset.inv())
        }
        // Fallback for other EnumSet implementations
        val result = newBy(bitset)
        other.forEach { result.remove(it) }
        return result
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

    override fun toString(): String {
        return joinToString(", ", "[", "]")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Set<*>) return false
        if (size != other.size) return false

        if (other is I32EnumSet<*>) {
            return bitset == other.bitset
        }

        return other.all { it in this }
    }

    override fun hashCode(): Int {
        // 使用标准的 Set hashCode 计算方式
        return sumOf { it.hashCode() }
    }
}

/**
 * Base class for enum sets using 64-bit storage.
 * Uses a ULong bitset to efficiently store and manage enum values.
 */
@InternalEnumSetApi
public abstract class I64EnumSet<E : Enum<E>>(protected open var bitset: ULong = 0u) : MutableEnumSet<E> {
    protected abstract val entries: List<E>

    protected abstract fun newBy(bitset: ULong): I64EnumSet<E>

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

    override fun containsAny(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is I64EnumSet<*>) {
            return (bitset and elements.bitset) != 0uL
        }
        return elements.any { contains(it) }
    }

    override fun intersect(other: Set<E>): Set<E> {
        if (other is I64EnumSet<*>) {
            return newBy(bitset and other.bitset)
        }
        // Fallback for other EnumSet implementations
        val result = newBy(0uL)
        forEach { e ->
            if (other.contains(e)) {
                result.add(e)
            }
        }
        return result
    }

    override fun union(other: Set<E>): Set<E> {
        if (other is I64EnumSet<*>) {
            return newBy(bitset or other.bitset)
        }
        // Fallback for other EnumSet implementations
        val result = newBy(bitset)
        other.forEach { result.add(it) }
        return result
    }

    override fun difference(other: Set<E>): Set<E> {
        if (other is I64EnumSet<*>) {
            return newBy(bitset and other.bitset.inv())
        }
        // Fallback for other EnumSet implementations
        val result = newBy(bitset)
        other.forEach { result.remove(it) }
        return result
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

    override fun toString(): String {
        return joinToString(", ", "[", "]")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Set<*>) return false
        if (size != other.size) return false

        if (other is I64EnumSet<*>) {
            return bitset == other.bitset
        }

        return other.all { it in this }
    }

    override fun hashCode(): Int {
        return sumOf { it.hashCode() }
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

    protected abstract fun newBy(bitset: LongArray): BigEnumSet<E>

    @JvmInline
    private value class IndexAndBit(val value: Long) {
        operator fun component1(): Int = (value ushr 32).toInt()
        operator fun component2(): Int = value.toInt()
    }

    /**
     * Gets array index and bit position for an enum ordinal
     */
    private fun getIndexAndBit(ordinal: Int): IndexAndBit {
        // return ordinal / 64 to ordinal % 64
        val c1 = ordinal / 64
        val c2 = ordinal % 64
        val value = (c1.toLong() shl 32) or c2.toLong()
        return IndexAndBit(value)
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

    override fun containsAny(elements: Collection<E>): Boolean {
        // Optimize when source is also a bitset-based enum set
        if (elements is BigEnumSet<*>) {
            val eb = elements.bitset
            val size = minOf(bitset.size, eb.size)
            for (i in 0 until size) {
                if ((bitset[i] and eb[i]) != 0L) return true
            }
            return false
        }
        return elements.any { contains(it) }
    }

    override fun intersect(other: Set<E>): Set<E> {
        if (other is BigEnumSet<*>) {
            val eb = other.bitset
            val size = minOf(bitset.size, eb.size)
            val result = LongArray(size)
            for (i in 0 until size) {
                result[i] = bitset[i] and eb[i]
            }
            return newBy(result)
        }
        // Fallback for other EnumSet implementations
        val result = newBy(LongArray(bitset.size))
        forEach { e ->
            if (other.contains(e)) {
                val (index, bit) = getIndexAndBit(e.ordinal)
                if (index < result.size) {
                    result.bitset[index] = result.bitset[index] or (1L shl bit)
                }
            }
        }
        return result
    }

    override fun union(other: Set<E>): Set<E> {
        if (other is BigEnumSet<*>) {
            val eb = other.bitset
            val size = maxOf(bitset.size, eb.size)
            val result = LongArray(size)

            // Copy this bitset
            for (i in bitset.indices) {
                result[i] = bitset[i]
            }

            // OR with other bitset
            for (i in eb.indices) {
                result[i] = result[i] or eb[i]
            }

            return newBy(result)
        }
        // Fallback for other EnumSet implementations
        val result = newBy(bitset.copyOf())
        other.forEach { result.add(it) }
        return result
    }

    override fun difference(other: Set<E>): Set<E> {
        if (other is BigEnumSet<*>) {
            val eb = other.bitset
            val size = bitset.size
            val result = LongArray(size)

            // Copy this bitset
            for (i in bitset.indices) {
                result[i] = bitset[i]
            }

            // Remove bits from other bitset
            val minSize = minOf(size, eb.size)
            for (i in 0 until minSize) {
                result[i] = result[i] and eb[i].inv()
            }

            return newBy(result)
        }
        // Fallback for other EnumSet implementations
        val result = newBy(bitset.copyOf())
        other.forEach { result.remove(it) }
        return result
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

    override fun toString(): String {
        return joinToString(", ", "[", "]")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Set<*>) return false
        if (size != other.size) return false

        // 如果是同类型的 BigEnumSet，使用位运算优化
        if (other is BigEnumSet<*>) {
            val otherBitset = other.bitset
            val maxSize = maxOf(bitset.size, otherBitset.size)

            for (i in 0 until maxSize) {
                val thisBit = if (i < bitset.size) bitset[i] else 0L
                val otherBit = if (i < otherBitset.size) otherBitset[i] else 0L
                if (thisBit != otherBit) return false
            }
            return true
        }

        // 使用标准的 Set equals 语义
        return other.all { it in this }
    }

    override fun hashCode(): Int {
        // 使用标准的 Set hashCode 计算方式
        return sumOf { it.hashCode() }
    }
}
