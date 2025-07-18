package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollectable
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierBuilderContainer
import love.forte.codegentle.kotlin.KotlinModifierContainer

/**
 * A Kotlin callable:
 * - simple function
 * - constructor
 * - property accessor
 *     - getter
 *     - setter
 *
 * @author ForteScarlet
 */
public sealed interface KotlinCallableSpec : KotlinSpec, KotlinModifierContainer {
    override val modifiers: Set<KotlinModifier>
    public val annotations: List<AnnotationRef>
    public val parameters: List<KotlinValueParameterSpec>
    public val kDoc: CodeValue
    public val code: CodeValue

    // TODO ?

    public interface Builder<S : KotlinCallableSpec, B : Builder<S, B>> :
        BuilderDsl,
        KotlinModifierBuilderContainer,
        AnnotationRefCollectable<B> {

        /**
         * Add KDoc to the constructor.
         */
        public fun addKDoc(codeValue: CodeValue): KotlinConstructorSpec.Builder

        /**
         * Add KDoc to the constructor.
         */
        public fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinConstructorSpec.Builder

        /**
         * Add a parameter to the constructor.
         */
        public fun addParameter(parameter: KotlinValueParameterSpec): KotlinConstructorSpec.Builder

        /**
         * Add parameters to the constructor.
         */
        public fun addParameters(parameters: Iterable<KotlinValueParameterSpec>): KotlinConstructorSpec.Builder

        /**
         * Add parameters to the constructor.
         */
        public fun addParameters(vararg parameters: KotlinValueParameterSpec): KotlinConstructorSpec.Builder

        /**
         * Add code to the constructor.
         */
        public fun addCode(codeValue: CodeValue): KotlinConstructorSpec.Builder

        /**
         * Add code to the constructor.
         */
        public fun addCode(format: String, vararg argumentParts: CodeArgumentPart): KotlinConstructorSpec.Builder

        /**
         * Add a statement to the constructor.
         */
        public fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): KotlinConstructorSpec.Builder

        /**
         * Add a statement to the constructor.
         */
        public fun addStatement(codeValue: CodeValue): B

        /**
         * Add a modifier to the constructor.
         */
        override fun addModifier(modifier: KotlinModifier): B

        /**
         * Add modifiers to the constructor.
         */
        override fun addModifiers(modifiers: Iterable<KotlinModifier>): B

        /**
         * Add modifiers to the constructor.
         */
        override fun addModifiers(vararg modifiers: KotlinModifier): B

        /**
         * Add an annotation reference to the constructor.
         */
        override fun addAnnotationRef(ref: AnnotationRef): B

        /**
         * Add annotation references to the constructor.
         */
        override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): B

        public fun build(): S
    }

}
