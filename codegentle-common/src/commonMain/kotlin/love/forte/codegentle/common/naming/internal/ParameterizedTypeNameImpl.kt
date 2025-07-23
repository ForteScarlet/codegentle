package love.forte.codegentle.common.naming.internal

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.ParameterizedTypeName
import love.forte.codegentle.common.ref.TypeRef

/**
 *
 * @author ForteScarlet
 */
internal class ParameterizedTypeNameImpl(
    override val enclosingType: ParameterizedTypeName?,
    override val rawType: ClassName,
    override val typeArguments: List<TypeRef<*>>
) : ParameterizedTypeName {
    override fun nestedClass(name: String): ParameterizedTypeName {
        return ParameterizedTypeNameImpl(
            enclosingType = this,
            rawType = rawType.nestedClass(name),
            typeArguments = emptyList()
        )
    }

    override fun nestedClass(
        name: String,
        typeArguments: List<TypeRef<*>>
    ): ParameterizedTypeName {
        return ParameterizedTypeNameImpl(
            enclosingType = this,
            rawType = rawType.nestedClass(name),
            typeArguments = typeArguments.toList()
        )
    }

    override fun nestedClass(name: String, vararg typeArguments: TypeRef<*>): ParameterizedTypeName {
        return ParameterizedTypeNameImpl(
            enclosingType = this,
            rawType = rawType.nestedClass(name),
            typeArguments = typeArguments.asList()
        )
    }

    override fun toString(): String {
        return buildString {
            val enclosingType = this@ParameterizedTypeNameImpl.enclosingType
            if (enclosingType != null) {
                append(enclosingType)
                append(".")
                append(rawType.simpleName)
            } else {
                append(rawType)
            }
            
            // Emit type arguments if any
            if (typeArguments.isNotEmpty()) {
                append("<")
                typeArguments.forEachIndexed { index, typeArg ->
                    if (index > 0) append(", ")
                    append(typeArg.typeName)
                }
                append(">")
            }
        }
    }
}
