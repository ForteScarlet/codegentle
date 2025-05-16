package love.forte.codegentle.common.naming

import love.forte.codegentle.common.naming.internal.ParameterizedTypeNameImpl
import love.forte.codegentle.common.ref.TypeRef

/**
 *
 * @author ForteScarlet
 */
public interface ParameterizedTypeName : TypeName, Named {
    public val enclosingType: ParameterizedTypeName?
    public val rawType: ClassName
    public val typeArguments: List<TypeRef<*>>

    /**
     * Same as [rawType.name][ClassName.name]
     */
    override val name: String
        get() = rawType.name

    /**
     * Returns a new [ParameterizedTypeName] instance for the specified [name] as nested inside this class.
     */
    public fun nestedClass(name: String): ParameterizedTypeName

    /**
     * Returns a new [ParameterizedTypeName] instance for the specified [name] as nested
     * inside this class, with the specified [typeArguments].
     */
    public fun nestedClass(name: String, typeArguments: List<TypeRef<*>>): ParameterizedTypeName

    /**
     * Returns a new [ParameterizedTypeName] instance for the specified [name] as nested
     * inside this class, with the specified [typeArguments].
     */
    public fun nestedClass(name: String, vararg typeArguments: TypeRef<*>): ParameterizedTypeName =
        nestedClass(name, typeArguments.asList())

}

/** Returns a parameterized type, applying [typeArguments] to [rawType]. */
public fun ParameterizedTypeName(rawType: ClassName, vararg typeArguments: TypeRef<*>): ParameterizedTypeName {
    return ParameterizedTypeNameImpl(null, rawType, typeArguments.asList())
}

/** Returns a parameterized type, applying [typeArguments] to [rawType]. */
public fun ParameterizedTypeName(rawType: ClassName, typeArguments: Iterable<TypeRef<*>>): ParameterizedTypeName {
    return ParameterizedTypeNameImpl(null, rawType, typeArguments.toList())
}
