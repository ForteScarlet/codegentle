package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollectable
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierBuilderContainer
import love.forte.codegentle.kotlin.spec.internal.ConstructorDelegationBuilderImpl
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
    // but if used as a primary constructor, it must be empty: this is validated in TypeSpec.
    override val code: CodeValue

    public companion object {
        public fun builder(): Builder {
            return KotlinConstructorSpecBuilderImpl()
        }
    }

    public interface Builder :
        BuilderDsl,
        KotlinCallableSpec.Builder<KotlinConstructorSpec>,
        KotlinModifierBuilderContainer,
        AnnotationRefCollectable<Builder> {

        /**
         * Add a parameter to the constructor.
         */
        public fun addParameter(parameter: KotlinValueParameterSpec): Builder

        /**
         * Add parameters to the constructor.
         */
        public fun addParameters(parameters: Iterable<KotlinValueParameterSpec>): Builder

        /**
         * Add parameters to the constructor.
         */
        public fun addParameters(vararg parameters: KotlinValueParameterSpec): Builder

        /**
         * Add KDoc to the constructor.
         */
        public fun addKDoc(codeValue: CodeValue): Builder

        /**
         * Add KDoc to the constructor.
         */
        public fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Add code to the constructor.
         */
        public fun addCode(codeValue: CodeValue): Builder

        /**
         * Add code to the constructor.
         */
        public fun addCode(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Add a statement to the constructor.
         */
        public fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Add a statement to the constructor.
         */
        public fun addStatement(codeValue: CodeValue): Builder

        /**
         * Set the constructor delegation.
         */
        public fun constructorDelegation(delegation: ConstructorDelegation?): Builder

        /**
         * Add a modifier to the constructor.
         */
        override fun addModifier(modifier: KotlinModifier): Builder

        /**
         * Add modifiers to the constructor.
         */
        override fun addModifiers(modifiers: Iterable<KotlinModifier>): Builder

        /**
         * Add modifiers to the constructor.
         */
        override fun addModifiers(vararg modifiers: KotlinModifier): Builder

        /**
         * Add an annotation reference to the constructor.
         */
        override fun addAnnotationRef(ref: AnnotationRef): Builder

        /**
         * Add annotation references to the constructor.
         */
        override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): Builder

        override fun build(): KotlinConstructorSpec
    }
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

public interface ConstructorDelegation {
    public enum class Kind { THIS, SUPER }

    // TODO primary 如果有，必须是 SUPER
    public val kind: Kind

    public val arguments: List<CodeValue>

    public companion object {
        public fun builder(kind: Kind): Builder {
            return ConstructorDelegationBuilderImpl(kind)
        }
    }

    public interface Builder {

        public fun addArgument(argument: CodeValue): Builder

        public fun addArgument(format: String, vararg arguments: CodeArgumentPart): Builder

        public fun addArguments(vararg arguments: CodeValue): Builder

        public fun addArguments(arguments: Iterable<CodeValue>): Builder


        public fun build(): ConstructorDelegation
    }

}

public inline fun ConstructorDelegation(
    kind: ConstructorDelegation.Kind,
    block: ConstructorDelegation.Builder.() -> Unit = {}
): ConstructorDelegation = ConstructorDelegation.builder(kind).apply(block).build()

public inline fun ConstructorDelegation.Builder.addArgument(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): ConstructorDelegation.Builder =
    addArgument(CodeValue(format, block))
