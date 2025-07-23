package love.forte.codegentle.common.naming.internal

import love.forte.codegentle.common.naming.ArrayTypeName
import love.forte.codegentle.common.naming.contentEquals
import love.forte.codegentle.common.naming.contentHashCode
import love.forte.codegentle.common.ref.TypeRef

/**
 *
 * @author ForteScarlet
 */
internal class ArrayTypeNameImpl(
    override val componentType: TypeRef<*>,
) : ArrayTypeName {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ArrayTypeName) return false

        return contentEquals(other)
    }

    override fun hashCode(): Int {
        return contentHashCode()
    }

    override fun toString(): String {
        return "$componentType[]"
    }
}
