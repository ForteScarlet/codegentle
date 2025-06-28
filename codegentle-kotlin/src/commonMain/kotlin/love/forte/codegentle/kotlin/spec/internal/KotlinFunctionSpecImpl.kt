package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueBuilder
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.KotlinContextParameterSpec
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinFunctionSpec].
 *
 * @author ForteScarlet
 */
internal data class KotlinFunctionSpecImpl(
    override val name: String,
    override val returnType: TypeRef<*>,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val parameters: List<KotlinValueParameterSpec>,
    override val receiver: TypeRef<*>?,
    override val contextParameters: List<KotlinContextParameterSpec>,
    override val kDoc: CodeValue,
    override val code: CodeValue
) : KotlinFunctionSpec {
    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }
    override fun toString(): String {
        return "KotlinFunctionSpec(name='$name', returnType=${returnType.typeName})"
    }
}



/**
 * Implementation of [KotlinFunctionSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinFunctionSpecBuilderImpl(
    override val name: String,
    override val returnType: TypeRef<*>
) : KotlinFunctionSpec.Builder {

    private val kDoc: CodeValueBuilder = CodeValue.builder()
    private val code: CodeValueBuilder = CodeValue.builder()
    private val modifierSet = MutableKotlinModifierSet.empty()
    private val annotations = mutableListOf<AnnotationRef>()
    private val typeVariables = mutableListOf<TypeRef<TypeVariableName>>()
    private val parameters = mutableListOf<KotlinValueParameterSpec>()
    private var receiver: TypeRef<*>? = null
    private val contextParameters = mutableListOf<KotlinContextParameterSpec>()

    override fun addModifier(modifier: KotlinModifier): KotlinFunctionSpec.Builder = apply {
        modifierSet.add(modifier)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinFunctionSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinFunctionSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addAnnotationRef(ref: AnnotationRef): KotlinFunctionSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): KotlinFunctionSpec.Builder = apply {
        annotations.addAll(refs)
    }

    override fun addKDoc(codeValue: CodeValue): KotlinFunctionSpec.Builder = apply {
        kDoc.add(codeValue)
    }

    override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinFunctionSpec.Builder = apply {
        kDoc.add(format, *argumentParts)
    }

    override fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): KotlinFunctionSpec.Builder = apply {
        typeVariables.add(typeVariable)
    }

    override fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): KotlinFunctionSpec.Builder = apply {
        this.typeVariables.addAll(typeVariables)
    }

    override fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): KotlinFunctionSpec.Builder = apply {
        this.typeVariables.addAll(typeVariables)
    }

    override fun addParameter(parameter: KotlinValueParameterSpec): KotlinFunctionSpec.Builder = apply {
        parameters.add(parameter)
    }

    override fun addParameters(parameters: Iterable<KotlinValueParameterSpec>): KotlinFunctionSpec.Builder = apply {
        this.parameters.addAll(parameters)
    }

    override fun addParameters(vararg parameters: KotlinValueParameterSpec): KotlinFunctionSpec.Builder = apply {
        this.parameters.addAll(parameters)
    }

    override fun receiver(receiverType: TypeRef<*>): KotlinFunctionSpec.Builder = apply {
        this.receiver = receiverType
    }

    override fun addContextParameter(contextParameter: KotlinContextParameterSpec): KotlinFunctionSpec.Builder = apply {
        contextParameters.add(contextParameter)
    }

    override fun addContextParameters(contextParameters: Iterable<KotlinContextParameterSpec>): KotlinFunctionSpec.Builder = apply {
        this.contextParameters.addAll(contextParameters)
    }

    override fun addContextParameters(vararg contextParameters: KotlinContextParameterSpec): KotlinFunctionSpec.Builder = apply {
        this.contextParameters.addAll(contextParameters)
    }

    override fun addCode(codeValue: CodeValue): KotlinFunctionSpec.Builder = apply {
        code.add(codeValue)
    }

    override fun addCode(format: String, vararg argumentParts: CodeArgumentPart): KotlinFunctionSpec.Builder = apply {
        code.add(format, *argumentParts)
    }

    override fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): KotlinFunctionSpec.Builder = apply {
        code.addStatement(format, *argumentParts)
    }

    override fun addStatement(codeValue: CodeValue): KotlinFunctionSpec.Builder = apply {
        code.addStatement(codeValue)
    }

    override fun build(): KotlinFunctionSpec =
        KotlinFunctionSpecImpl(
            name = name,
            returnType = returnType,
            annotations = annotations.toList(),
            modifiers = modifierSet.immutable(),
            typeVariables = typeVariables.toList(),
            parameters = parameters.toList(),
            receiver = receiver,
            contextParameters = contextParameters.toList(),
            kDoc = kDoc.build(),
            code = code.build()
        )
}
