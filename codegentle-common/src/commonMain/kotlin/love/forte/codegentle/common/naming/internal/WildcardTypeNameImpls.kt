package love.forte.codegentle.common.naming.internal

import love.forte.codegentle.common.naming.LowerWildcardTypeName
import love.forte.codegentle.common.naming.UpperWildcardTypeName
import love.forte.codegentle.common.ref.TypeRef

internal class UpperWildcardTypeNameImpl(
    override val bounds: List<TypeRef<*>>
) : UpperWildcardTypeName {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UpperWildcardTypeNameImpl) return false

        if (bounds != other.bounds) return false

        return true
    }

    override fun hashCode(): Int {
        return bounds.hashCode()
    }

    override fun toString(): String {
        return buildString {
            append("Upper in ")
            bounds.joinTo(this) { it.typeName.toString() }
        }
    }
}

internal class LowerWildcardTypeNameImpl(
    override val bounds: List<TypeRef<*>>
) : LowerWildcardTypeName {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LowerWildcardTypeNameImpl) return false

        if (bounds != other.bounds) return false

        return true
    }

    override fun hashCode(): Int {
        return bounds.hashCode()
    }

    override fun toString(): String {
        return buildString {
            append("Lower out ")
            bounds.joinTo(this) { it.typeName.toString() }
        }
    }
}
