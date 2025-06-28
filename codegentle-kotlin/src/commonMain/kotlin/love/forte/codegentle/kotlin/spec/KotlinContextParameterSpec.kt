package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.spec.internal.KotlinContextParameterSpecBuilderImpl

/**
 * A Kotlin context parameter.
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinContextParameterSpec : KotlinSpec {
    /**
     * Parameter name.
     * `null` if it's `_`, e.g., `context(_: ParameterType)`.
     */
    public val name: String?
    public val typeRef: TypeRef<*>

    /**
     * Builder for [KotlinContextParameterSpec].
     */
    public interface Builder : BuilderDsl {
        /**
         * Parameter name.
         * `null` if it's `_`, e.g., `context(_: ParameterType)`.
         */
        public val name: String?

        /**
         * Parameter type.
         */
        public val type: TypeRef<*>

        /**
         * Build [KotlinContextParameterSpec].
         */
        public fun build(): KotlinContextParameterSpec
    }

    public companion object {
        /**
         * Create a [Builder].
         *
         * @param name the parameter name, or null for `_`
         * @param type the parameter type
         * @return new [Builder] instance
         */
        public fun builder(name: String?, type: TypeRef<*>): Builder =
            KotlinContextParameterSpecBuilderImpl(name, type)
    }
}
