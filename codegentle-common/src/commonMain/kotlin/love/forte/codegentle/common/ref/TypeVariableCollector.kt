package love.forte.codegentle.common.ref

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.naming.TypeVariableName


/**
 * Collector for [TypeRef]<[love.forte.codegentle.common.naming.TypeVariableName]>.
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface TypeVariableCollector<B : TypeVariableCollector<B>> {
    /**
     * Add type variable reference.
     */
    public fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): B

    /**
     * Add type variable references.
     */
    public fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): B

    /**
     * Add type variable references.
     */
    public fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): B
}
