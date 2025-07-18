package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.InitializerBlockCollector
import love.forte.codegentle.common.code.KDocCollector
import love.forte.codegentle.common.naming.SuperinterfaceCollector
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeVariableCollector
import love.forte.codegentle.kotlin.KotlinModifierCollector
import love.forte.codegentle.kotlin.spec.internal.KotlinValueClassSpecBuilderImpl

/**
 * A generated Kotlin value class.
 *
 * ```kotlin
 * value class ValueClass(val value: String)
 * ```
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinValueClassSpec : KotlinTypeSpec {
    override val name: String

    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.CLASS

    /**
     * The primary constructor parameter of the value class.
     */
    public val primaryParameter: KotlinValueParameterSpec

    public companion object {
        /**
         * Create a builder for a value class.
         *
         * @param name the value class name
         * @param primaryParameter the primary constructor parameter
         * @return a new builder
         */
        public fun builder(name: String, primaryParameter: KotlinValueParameterSpec): Builder {
            return KotlinValueClassSpecBuilderImpl(name, primaryParameter)
        }
    }

    /**
     * Builder for [KotlinValueClassSpec].
     */
    public interface Builder :
        BuilderDsl,
        KotlinModifierCollector<Builder>,
        KotlinPropertyCollector<Builder>,
        KotlinFunctionCollector<Builder>,
        AnnotationRefCollector<Builder>,
        KDocCollector<Builder>,
        InitializerBlockCollector<Builder>,
        TypeVariableCollector<Builder>,
        SuperinterfaceCollector<Builder> {
        /**
         * The value class name.
         */
        public val name: String

        /**
         * The primary constructor parameter of the value class.
         */
        public val primaryParameter: KotlinValueParameterSpec

        /**
         * Build [KotlinValueClassSpec] instance.
         */
        public fun build(): KotlinValueClassSpec
    }
}


/**
 * Create a [KotlinValueClassSpec] with the given name and primary parameter.
 *
 * @param name the value class name
 * @param primaryParameter the primary constructor parameter
 * @param block the configuration block
 * @return a new [KotlinValueClassSpec] instance
 */
public inline fun KotlinValueClassSpec(
    name: String,
    primaryParameter: KotlinValueParameterSpec,
    block: KotlinValueClassSpec.Builder.() -> Unit = {}
): KotlinValueClassSpec {
    return KotlinValueClassSpec.builder(name, primaryParameter).apply(block).build()
}
