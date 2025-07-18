package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.kotlin.spec.internal.KotlinGetterSpecBuilderImpl
import love.forte.codegentle.kotlin.spec.internal.KotlinSetterSpecBuilderImpl

/**
 * The Kotlin getter/setter spec.
 *
 * @author ForteScarlet
 */
public sealed interface KotlinPropertyAccessorSpec : KotlinCallableSpec {
    public enum class Kind { GETTER, SETTER }

    public val kind: Kind

    /**
     * Accessor's parameters are always empty.
     */
    override val parameters: List<KotlinValueParameterSpec>
        get() = emptyList()

    public interface Builder<S : KotlinPropertyAccessorSpec, B : Builder<S, B>> : 
        KotlinCallableSpec.Builder<S, B> {

        override fun build(): S
    }

    public companion object {
        public fun getterBuilder(): KotlinGetterSpec.Builder {
            return KotlinGetterSpecBuilderImpl()
        }

        public fun setterBuilder(parameterName: String): KotlinSetterSpec.Builder {
            return KotlinSetterSpecBuilderImpl(parameterName)
        }
    }
}

/**
 * The Kotlin property's getter.
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinGetterSpec : KotlinPropertyAccessorSpec {
    override val kind: KotlinPropertyAccessorSpec.Kind
        get() = KotlinPropertyAccessorSpec.Kind.GETTER

    public interface Builder : KotlinPropertyAccessorSpec.Builder<KotlinGetterSpec, Builder> {
        override fun build(): KotlinGetterSpec
    }
}

/**
 * The Kotlin property's setter.
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinSetterSpec : KotlinPropertyAccessorSpec {
    override val kind: KotlinPropertyAccessorSpec.Kind
        get() = KotlinPropertyAccessorSpec.Kind.SETTER

    public val parameterName: String

    public interface Builder : KotlinPropertyAccessorSpec.Builder<KotlinSetterSpec, Builder> {
        public val parameterName: String

        override fun build(): KotlinSetterSpec
    }
}

/**
 * Create a [KotlinGetterSpec] with the given configuration.
 *
 * @param block the configuration block
 * @return a new [KotlinGetterSpec] instance
 */
public inline fun KotlinGetterSpec(
    block: KotlinGetterSpec.Builder.() -> Unit = {}
): KotlinGetterSpec {
    return KotlinPropertyAccessorSpec.getterBuilder().apply(block).build()
}

/**
 * Create a [KotlinSetterSpec] with the given parameter name and configuration.
 *
 * @param parameterName the setter parameter name
 * @param block the configuration block
 * @return a new [KotlinSetterSpec] instance
 */
public inline fun KotlinSetterSpec(
    parameterName: String,
    block: KotlinSetterSpec.Builder.() -> Unit = {}
): KotlinSetterSpec {
    return KotlinPropertyAccessorSpec.setterBuilder(parameterName).apply(block).build()
}
