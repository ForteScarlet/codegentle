package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.spec.internal.KotlinContextParameterSpecBuilderImpl
import love.forte.codegentle.kotlin.spec.internal.KotlinContextParameterSpecImpl

/**
 * A Kotlin context parameter.
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinContextParameterSpec : KotlinParameterSpec {
    /**
     * Parameter name.
     * `null` if it's `_`, e.g., `context(_: ParameterType)`.
     */
    override val name: String?
    override val typeRef: TypeRef<*>

    /**
     * Builder for [KotlinContextParameterSpec].
     */
    public interface Builder : KotlinParameterSpec.Builder<KotlinContextParameterSpec> {
        /**
         * Parameter name.
         * `null` if it's `_`, e.g., `context(_: ParameterType)`.
         */
        override val name: String?

        /**
         * Parameter type.
         */
        override val type: TypeRef<*>

        /**
         * Build [KotlinContextParameterSpec].
         */
        override fun build(): KotlinContextParameterSpec
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

        /**
         * Create a [KotlinContextParameterSpec].
         *
         * @param name the parameter name, or null for `_`
         * @param type the parameter type
         */
        public fun of(name: String?, type: TypeRef<*>): KotlinContextParameterSpec {
            return KotlinContextParameterSpecImpl(name, type)
        }
    }
}

/**
 * Create a [KotlinContextParameterSpec] with the given name and type.
 *
 * @param name the parameter name, or null for `_`
 * @param type the parameter type
 * @param block the configuration block
 * @return a new [KotlinContextParameterSpec] instance
 */
public inline fun KotlinContextParameterSpec(
    name: String?,
    type: TypeRef<*>,
    block: KotlinContextParameterSpec.Builder.() -> Unit = {}
): KotlinContextParameterSpec =
    KotlinContextParameterSpec.builder(name, type).also(block).build()
