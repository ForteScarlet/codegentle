package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollectable
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierBuilderContainer
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

    public interface Builder<S : KotlinPropertyAccessorSpec> : 
        KotlinCallableSpec.Builder<S>,
        KotlinModifierBuilderContainer,
        AnnotationRefCollectable<Builder<S>> {

        /**
         * Add KDoc to the accessor.
         */
        public fun addKDoc(codeValue: CodeValue): Builder<S>

        /**
         * Add KDoc to the accessor.
         */
        public fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): Builder<S>

        /**
         * Add code to the accessor.
         */
        public fun addCode(codeValue: CodeValue): Builder<S>

        /**
         * Add code to the accessor.
         */
        public fun addCode(format: String, vararg argumentParts: CodeArgumentPart): Builder<S>

        /**
         * Add a statement to the accessor.
         */
        public fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): Builder<S>

        /**
         * Add a statement to the accessor.
         */
        public fun addStatement(codeValue: CodeValue): Builder<S>

        override fun addModifier(modifier: KotlinModifier): Builder<S>

        override fun addModifiers(modifiers: Iterable<KotlinModifier>): Builder<S>

        override fun addModifiers(vararg modifiers: KotlinModifier): Builder<S>

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

    public interface Builder : KotlinPropertyAccessorSpec.Builder<KotlinGetterSpec> {
        override fun addModifier(modifier: KotlinModifier): Builder
        override fun addModifiers(modifiers: Iterable<KotlinModifier>): Builder
        override fun addModifiers(vararg modifiers: KotlinModifier): Builder
        override fun addAnnotationRef(ref: AnnotationRef): Builder
        override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): Builder
        override fun addKDoc(codeValue: CodeValue): Builder
        override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): Builder
        override fun addCode(codeValue: CodeValue): Builder
        override fun addCode(format: String, vararg argumentParts: CodeArgumentPart): Builder
        override fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): Builder
        override fun addStatement(codeValue: CodeValue): Builder
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

    public interface Builder : KotlinPropertyAccessorSpec.Builder<KotlinSetterSpec> {
        public val parameterName: String

        override fun addModifier(modifier: KotlinModifier): Builder
        override fun addModifiers(modifiers: Iterable<KotlinModifier>): Builder
        override fun addModifiers(vararg modifiers: KotlinModifier): Builder
        override fun addAnnotationRef(ref: AnnotationRef): Builder
        override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): Builder
        override fun addKDoc(codeValue: CodeValue): Builder
        override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): Builder
        override fun addCode(codeValue: CodeValue): Builder
        override fun addCode(format: String, vararg argumentParts: CodeArgumentPart): Builder
        override fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): Builder
        override fun addStatement(codeValue: CodeValue): Builder
        override fun build(): KotlinSetterSpec
    }
}
