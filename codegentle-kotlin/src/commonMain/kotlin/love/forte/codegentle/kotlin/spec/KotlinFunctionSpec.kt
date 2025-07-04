package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollectable
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierBuilderContainer
import love.forte.codegentle.kotlin.naming.KotlinNames
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec.Companion.DEFAULT_REF
import love.forte.codegentle.kotlin.spec.internal.KotlinFunctionSpecBuilderImpl

/**
 * A Kotlin function。
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinFunctionSpec : KotlinCallableSpec {
    /**
     * Function name.
     */
    public val name: String
    public val returnType: TypeRef<*>

    override val modifiers: Set<KotlinModifier>
    override val annotations: List<AnnotationRef>
    public val typeVariables: List<TypeRef<TypeVariableName>>
    override val parameters: List<KotlinValueParameterSpec>
    public val receiver: TypeRef<*>?
    public val contextParameters: List<KotlinContextParameterSpec>
    override val kDoc: CodeValue
    override val code: CodeValue

    /**
     * Builder for [KotlinFunctionSpec].
     */
    public interface Builder :
        BuilderDsl,
        KotlinCallableSpec.Builder<KotlinFunctionSpec>,
        KotlinModifierBuilderContainer,
        AnnotationRefCollectable<Builder> {

        /**
         * Function name.
         */
        public val name: String

        /**
         * Function return type.
         */
        public val returnType: TypeRef<*>

        /**
         * Add KDoc to the function.
         */
        public fun addKDoc(codeValue: CodeValue): Builder

        /**
         * Add KDoc to the function.
         */
        public fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Add a type variable to the function.
         */
        public fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): Builder

        /**
         * Add type variables to the function.
         */
        public fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): Builder

        /**
         * Add type variables to the function.
         */
        public fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): Builder

        /**
         * Add a parameter to the function.
         */
        public fun addParameter(parameter: KotlinValueParameterSpec): Builder

        /**
         * Add parameters to the function.
         */
        public fun addParameters(parameters: Iterable<KotlinValueParameterSpec>): Builder

        /**
         * Add parameters to the function.
         */
        public fun addParameters(vararg parameters: KotlinValueParameterSpec): Builder

        /**
         * Set the receiver type for this function.
         *
         * @param receiverType the receiver type
         * @return this builder
         */
        public fun receiver(receiverType: TypeRef<*>): Builder

        /**
         * Add a context parameter to this function.
         *
         * @param contextParameter the context parameter
         * @return this builder
         */
        public fun addContextParameter(contextParameter: KotlinContextParameterSpec): Builder

        /**
         * Add multiple context parameters to this function.
         *
         * @param contextParameters the context parameters
         * @return this builder
         */
        public fun addContextParameters(contextParameters: Iterable<KotlinContextParameterSpec>): Builder

        /**
         * Add multiple context parameters to this function.
         *
         * @param contextParameters the context parameters
         * @return this builder
         */
        public fun addContextParameters(vararg contextParameters: KotlinContextParameterSpec): Builder

        public fun returns(type: TypeRef<*>): Builder

        /**
         * Add code to the function.
         */
        public fun addCode(codeValue: CodeValue): Builder

        /**
         * Add code to the function.
         */
        public fun addCode(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Add a statement to the function.
         */
        public fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Add a statement to the function.
         */
        public fun addStatement(codeValue: CodeValue): Builder

        override fun addModifier(modifier: KotlinModifier): Builder

        override fun addModifiers(modifiers: Iterable<KotlinModifier>): Builder

        override fun addModifiers(vararg modifiers: KotlinModifier): Builder

        /**
         * Build a [KotlinFunctionSpec] instance.
         */
        override fun build(): KotlinFunctionSpec
    }

    public companion object {
        public val DEFAULT_REF: TypeRef<*> = KotlinNames.Classes.UNIT.kotlinRef()

        /**
         * Create a function builder.
         *
         * @param name the function name
         * @param type the return type
         * @return new [Builder] instance.
         */
        public fun builder(name: String, type: TypeRef<*> = DEFAULT_REF): Builder {
            return KotlinFunctionSpecBuilderImpl(name, type)
        }
    }
}

/**
 * Create a [KotlinFunctionSpec] with the given name and return type.
 *
 * @param name the function name
 * @param type the return type
 * @param block the configuration block
 * @return a new [KotlinFunctionSpec] instance
 */
public inline fun KotlinFunctionSpec(
    name: String,
    type: TypeRef<*> = DEFAULT_REF,
    block: KotlinFunctionSpec.Builder.() -> Unit = {}
): KotlinFunctionSpec =
    KotlinFunctionSpec.builder(name, type).apply(block).build()

/**
 * Add KDoc with a format string and a configuration block.
 *
 * @param format the format string
 * @param block the configuration block
 * @return this builder
 */
public inline fun KotlinFunctionSpec.Builder.addKDoc(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinFunctionSpec.Builder = apply {
    addKDoc(CodeValue(format, block))
}

/**
 * Add code with a format string and a configuration block.
 *
 * @param format the format string
 * @param block the configuration block
 * @return this builder
 */
public inline fun KotlinFunctionSpec.Builder.addCode(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinFunctionSpec.Builder = apply {
    addCode(CodeValue(format, block))
}

/**
 * Add a statement with a format string and a configuration block.
 *
 * @param format the format string
 * @param block the configuration block
 * @return this builder
 */
public inline fun KotlinFunctionSpec.Builder.addStatement(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinFunctionSpec.Builder = apply {
    addStatement(CodeValue(format, block))
}

public inline fun KotlinFunctionSpec.Builder.addParameter(
    name: String,
    type: TypeRef<*>,
    block: KotlinValueParameterSpec.Builder.() -> Unit = {}
): KotlinFunctionSpec.Builder = addParameter(KotlinValueParameterSpec.builder(name, type).apply(block).build())

public inline fun KotlinFunctionSpec.Builder.addContextParameter(
    name: String?,
    type: TypeRef<*>,
    block: KotlinContextParameterSpec.Builder.() -> Unit = {}
): KotlinFunctionSpec.Builder = addContextParameter(KotlinContextParameterSpec.builder(name, type).apply(block).build())
