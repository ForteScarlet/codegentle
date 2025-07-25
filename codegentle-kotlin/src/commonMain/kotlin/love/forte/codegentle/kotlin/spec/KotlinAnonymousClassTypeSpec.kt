package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.*
import love.forte.codegentle.common.naming.SuperConfigurer
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeVariableCollector
import love.forte.codegentle.kotlin.KotlinModifierCollector
import love.forte.codegentle.kotlin.spec.internal.KotlinAnonymousClassTypeSpecBuilderImpl

/**
 * A Kotlin anonymous class type specification.
 * Can be used as an implementation body for enum constants.
 *
 * Anonymous classes in Kotlin cannot define their own constructors, but they can
 * call the superclass constructor with arguments when extending a class.
 *
 * ```kotlin
 * object : SuperType {
 *     // implementations
 * }
 * ```
 *
 * Or with superclass constructor arguments:
 * ```kotlin
 * object : SuperType(arg1, arg2) {
 *     // implementations
 * }
 * ```
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinAnonymousClassTypeSpec : KotlinTypeSpec {
    /**
     * Anonymous class has no name.
     */
    override val name: String
        get() = ""

    /**
     * Anonymous class has no specific kind.
     */
    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.CLASS

    /**
     * Defines the delegation information for the constructor within an anonymous class type specification.
     *
     * [ConstructorDelegation.kind] is always [SUPER][ConstructorDelegation.Kind.SUPER].
     */
    public val constructorDelegation: ConstructorDelegation

    public companion object {
        /**
         * Create a builder for an anonymous class.
         *
         * @return a new builder
         */
        public fun builder(): Builder {
            return KotlinAnonymousClassTypeSpecBuilderImpl()
        }
    }

    /**
     * Builder for [KotlinAnonymousClassTypeSpec].
     */
    public interface Builder :
        BuilderDsl,
        AnnotationRefCollector<Builder>,
        KDocCollector<Builder>,
        InitializerBlockCollector<Builder>,
        TypeVariableCollector<Builder>,
        SuperConfigurer<Builder>,
        KotlinModifierCollector<Builder>,
        KotlinPropertyCollector<Builder>,
        KotlinFunctionCollector<Builder> {
        /**
         * Add arguments to pass to the superclass constructor.
         */
        public fun addSuperConstructorArguments(vararg arguments: CodeValue): Builder

        /**
         * Add arguments to pass to the superclass constructor.
         */
        public fun addSuperConstructorArguments(arguments: Iterable<CodeValue>): Builder

        /**
         * Add a single argument to pass to the superclass constructor.
         */
        public fun addSuperConstructorArgument(argument: CodeValue): Builder

        /**
         * Add a single argument to pass to the superclass constructor.
         */
        public fun addSuperConstructorArgument(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Build [KotlinAnonymousClassTypeSpec] instance.
         */
        public fun build(): KotlinAnonymousClassTypeSpec
    }
}

/**
 * Create a [KotlinAnonymousClassTypeSpec] with the given configuration.
 *
 * @param block the configuration block
 * @return a new [KotlinAnonymousClassTypeSpec] instance
 */
public inline fun KotlinAnonymousClassTypeSpec(
    block: KotlinAnonymousClassTypeSpec.Builder.() -> Unit = {}
): KotlinAnonymousClassTypeSpec {
    return KotlinAnonymousClassTypeSpec.builder().apply(block).build()
}

public inline fun KotlinAnonymousClassTypeSpec.Builder.addSuperConstructorArgument(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinAnonymousClassTypeSpec.Builder = addSuperConstructorArgument(CodeValue(format, block))
