package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.ref.TypeRef

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
