package love.forte.codegentle.common.naming.internal

import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.TypeRef

/**
 *
 * @author ForteScarlet
 */
internal class TypeVariableNameImpl(
    override val name: String,
    override val bounds: List<TypeRef<*>>,
) : TypeVariableName {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypeVariableNameImpl) return false

        if (name != other.name) return false
        if (bounds != other.bounds) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + bounds.hashCode()
        return result
    }

    override fun toString(): String {
        if (bounds.isEmpty()) {
            return name
        }

        return buildString {
            append(name)
            append(" : ")
            bounds.joinTo(this) { it.typeName.toString() }
        }
    }


}
