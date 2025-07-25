package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.ref.kotlinRef

/**
 * A Kotlin parameter.
 *
 * @see KotlinContextParameterSpec
 * @see KotlinValueParameterSpec
 *
 * @author ForteScarlet
 */
public sealed interface KotlinParameterSpec : KotlinSpec {
    /**
     * Parameter name.
     */
    public val name: String?
    public val typeRef: TypeRef<*>

    public sealed interface Builder<S : KotlinParameterSpec> : BuilderDsl {
        /**
         * Parameter name.
         * `null` if it's `_`, e.g., `context(_: ParameterType)`.
         */
        public val name: String?

        /**
         * Parameter type.
         */
        public val type: TypeRef<*>

        public fun build(): S
    }
}

public interface KotlinValueParameterCollector<B : KotlinValueParameterCollector<B>> {
    /**
     * Add a value parameter to the function.
     */
    public fun addParameter(parameter: KotlinValueParameterSpec): B

    /**
     * Add value parameters to the function.
     */
    public fun addParameters(parameters: Iterable<KotlinValueParameterSpec>): B

    /**
     * Add value parameters to the function.
     */
    public fun addParameters(vararg parameters: KotlinValueParameterSpec): B

}

public inline fun <C : KotlinValueParameterCollector<C>> C.addParameter(
    name: String,
    type: TypeRef<*>,
    block: KotlinValueParameterSpec.Builder.() -> Unit = {}
): C = addParameter(KotlinValueParameterSpec.builder(name, type).apply(block).build())

public inline fun <C : KotlinValueParameterCollector<C>> C.addParameter(
    name: String,
    type: TypeName,
    block: KotlinValueParameterSpec.Builder.() -> Unit = {}
): C = addParameter(name, type.kotlinRef(), block)
