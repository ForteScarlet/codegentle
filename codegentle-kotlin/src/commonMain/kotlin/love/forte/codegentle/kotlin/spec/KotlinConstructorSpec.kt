package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.kotlin.spec.internal.KotlinConstructorSpecBuilderImpl

/**
 * A Kotlin constructor.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinConstructorSpec : KotlinCallableSpec {
    public val constructorDelegation: ConstructorDelegation?

    // Not primary constructor may have non-empty code,
    // but if used as a primary constructor, it must be empty.
    override val code: CodeValue

    public companion object {
        public fun builder(): Builder {
            return KotlinConstructorSpecBuilderImpl()
        }
    }

    public interface Builder :
        BuilderDsl,
        KotlinCallableSpec.Builder<KotlinConstructorSpec, Builder>,
        KotlinValueParameterCollector<Builder> {
        /**
         * Set the constructor delegation.
         */
        public fun constructorDelegation(delegation: ConstructorDelegation?): Builder

        override fun build(): KotlinConstructorSpec
    }
}

/**
 * Create a [KotlinConstructorSpec] with the given configuration.
 *
 * @param block the configuration block
 * @return a new [KotlinConstructorSpec] instance
 */
public inline fun KotlinConstructorSpec(
    block: KotlinConstructorSpec.Builder.() -> Unit = {}
): KotlinConstructorSpec {
    return KotlinConstructorSpec.builder().apply(block).build()
}

public inline fun KotlinConstructorSpec.Builder.constructorDelegation(
    kind: ConstructorDelegation.Kind,
    block: ConstructorDelegation.Builder.() -> Unit = {}
): KotlinConstructorSpec.Builder =
    constructorDelegation(ConstructorDelegation(kind, block))

public inline fun KotlinConstructorSpec.Builder.thisConstructorDelegation(
    block: ConstructorDelegation.Builder.() -> Unit = {}
): KotlinConstructorSpec.Builder =
    constructorDelegation(ConstructorDelegation.Kind.THIS, block)

public inline fun KotlinConstructorSpec.Builder.superConstructorDelegation(
    block: ConstructorDelegation.Builder.() -> Unit = {}
): KotlinConstructorSpec.Builder =
    constructorDelegation(ConstructorDelegation.Kind.SUPER, block)
