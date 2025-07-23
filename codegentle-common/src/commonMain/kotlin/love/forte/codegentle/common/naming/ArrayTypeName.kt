package love.forte.codegentle.common.naming

import love.forte.codegentle.common.naming.internal.ArrayTypeNameImpl
import love.forte.codegentle.common.ref.TypeRef

/**
 *
 * @author ForteScarlet
 */
public interface ArrayTypeName : TypeName {
    public val componentType: TypeRef<*>
}

public fun ArrayTypeName.contentHashCode(): Int {
    return componentType.hashCode()
}

public infix fun ArrayTypeName.contentEquals(other: ArrayTypeName): Boolean {
    if (this === other) return true

    return componentType == other.componentType
}

public fun ArrayTypeName(componentType: TypeRef<*>): ArrayTypeName =
    ArrayTypeNameImpl(componentType)
