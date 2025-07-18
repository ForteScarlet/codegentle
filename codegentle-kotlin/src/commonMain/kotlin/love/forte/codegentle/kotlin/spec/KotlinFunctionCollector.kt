package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.ref.kotlinRef


/**
 * Collector for [KotlinFunctionSpec]
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface KotlinFunctionCollector<B : KotlinFunctionCollector<B>> {
    /**
     * Add functions.
     */
    public fun addFunctions(functions: Iterable<KotlinFunctionSpec>): B

    /**
     * Add functions.
     */
    public fun addFunctions(vararg functions: KotlinFunctionSpec): B

    /**
     * Add function.
     */
    public fun addFunction(function: KotlinFunctionSpec): B
}

public inline fun <B : KotlinFunctionCollector<B>> B.addFunction(
    name: String,
    type: TypeRef<*>,
    block: KotlinFunctionSpec.Builder.() -> Unit = {}
): B = addFunction(KotlinFunctionSpec(name, type, block))

public inline fun <B : KotlinFunctionCollector<B>> B.addFunction(
    name: String,
    type: TypeName,
    block: KotlinFunctionSpec.Builder.() -> Unit = {}
): B = addFunction(name, type.kotlinRef(), block)
