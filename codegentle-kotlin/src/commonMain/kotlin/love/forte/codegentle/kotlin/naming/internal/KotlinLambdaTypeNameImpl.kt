package love.forte.codegentle.kotlin.naming.internal

import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.naming.KotlinLambdaTypeName
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.writer.writeToKotlinString

/**
 * Internal implementation of [KotlinLambdaTypeName].
 *
 * @author ForteScarlet
 */
internal data class KotlinLambdaTypeNameImpl(
    override val receiver: TypeRef<*>?,
    override val contextReceivers: List<TypeRef<*>>,
    override val parameters: List<KotlinValueParameterSpec>,
    override val returnType: TypeRef<*>,
    override val modifiers: Set<KotlinModifier>
) : KotlinLambdaTypeName {

    override fun toString(): String = writeToKotlinString()
}

/**
 * Internal implementation of [KotlinLambdaTypeName.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinLambdaTypeNameBuilderImpl : KotlinLambdaTypeName.Builder {
    private var receiver: TypeRef<*>? = null
    private val contextReceivers: MutableList<TypeRef<*>> = mutableListOf()
    private val parameters: MutableList<KotlinValueParameterSpec> = mutableListOf()
    private var returnType: TypeRef<*>? = null
    private val modifiers: MutableSet<KotlinModifier> = mutableSetOf()

    override fun receiver(receiver: TypeRef<*>): KotlinLambdaTypeName.Builder = apply {
        this.receiver = receiver
    }

    override fun addContextReceivers(vararg contextReceivers: TypeRef<*>): KotlinLambdaTypeName.Builder = apply {
        this.contextReceivers.addAll(contextReceivers)
    }

    override fun addContextReceivers(contextReceivers: Iterable<TypeRef<*>>): KotlinLambdaTypeName.Builder = apply {
        this.contextReceivers.addAll(contextReceivers)
    }

    override fun addContextReceiver(contextReceiver: TypeRef<*>): KotlinLambdaTypeName.Builder = apply {
        this.contextReceivers.add(contextReceiver)
    }

    override fun addParameters(vararg parameters: KotlinValueParameterSpec): KotlinLambdaTypeName.Builder = apply {
        this.parameters.addAll(parameters)
    }

    override fun addParameters(parameters: Iterable<KotlinValueParameterSpec>): KotlinLambdaTypeName.Builder = apply {
        this.parameters.addAll(parameters)
    }

    override fun addParameter(parameter: KotlinValueParameterSpec): KotlinLambdaTypeName.Builder = apply {
        this.parameters.add(parameter)
    }

    override fun returns(type: TypeRef<*>): KotlinLambdaTypeName.Builder = apply {
        this.returnType = type
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinLambdaTypeName.Builder = apply {
        this.modifiers.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinLambdaTypeName.Builder = apply {
        this.modifiers.addAll(modifiers)
    }

    override fun addModifier(modifier: KotlinModifier): KotlinLambdaTypeName.Builder = apply {
        this.modifiers.add(modifier)
    }

    override fun build(): KotlinLambdaTypeName {
        val finalReturnType = returnType ?: throw IllegalStateException("Return type must be specified")
        
        return KotlinLambdaTypeNameImpl(
            receiver = receiver,
            contextReceivers = contextReceivers.toList(),
            parameters = parameters.toList(),
            returnType = finalReturnType,
            modifiers = modifiers.toSet()
        )
    }
}
