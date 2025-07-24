package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.TypeVariableCollector
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierCollector
import love.forte.codegentle.kotlin.naming.KotlinClassNames
import love.forte.codegentle.kotlin.ref.KotlinTypeRefBuilderDsl
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
        KotlinCallableSpec.Builder<KotlinFunctionSpec, Builder>,
        KotlinModifierCollector<Builder>,
        KotlinValueParameterCollector<Builder>,
        TypeVariableCollector<Builder> {

        /**
         * Function name.
         */
        public val name: String

        /**
         * Function return type.
         */
        public val returnType: TypeRef<*>

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
         * Build a [KotlinFunctionSpec] instance.
         */
        override fun build(): KotlinFunctionSpec
    }

    public companion object {
        public val DEFAULT_REF: TypeRef<*> = KotlinClassNames.UNIT.kotlinRef()

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

public inline fun KotlinFunctionSpec.Builder.addContextParameter(
    name: String?,
    type: TypeRef<*>,
    block: KotlinContextParameterSpec.Builder.() -> Unit = {}
): KotlinFunctionSpec.Builder =
    addContextParameter(KotlinContextParameterSpec.builder(name, type).apply(block).build())


public inline fun <T : TypeName> KotlinFunctionSpec.Builder.returns(
    type: T,
    block: KotlinTypeRefBuilderDsl<T> = {}
): KotlinFunctionSpec.Builder = returns(type.kotlinRef(block))

