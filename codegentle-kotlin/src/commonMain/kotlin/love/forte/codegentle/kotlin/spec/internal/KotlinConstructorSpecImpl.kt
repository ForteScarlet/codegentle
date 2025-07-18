package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueBuilder
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.CodeGentleKotlinSpecImplementation
import love.forte.codegentle.kotlin.spec.ConstructorDelegation
import love.forte.codegentle.kotlin.spec.KotlinConstructorSpec
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [ConstructorDelegation].
 *
 * @author ForteScarlet
 */
internal data class ConstructorDelegationImpl(
    override val kind: ConstructorDelegation.Kind,
    override val arguments: List<CodeValue>
) : ConstructorDelegation {

    override fun toString(): String {
        return "ConstructorDelegation(kind=$kind, arguments=$arguments)"
    }
}

/**
 * Implementation of [ConstructorDelegation.Builder].
 *
 * @author ForteScarlet
 */
internal class ConstructorDelegationBuilderImpl(
    private val kind: ConstructorDelegation.Kind
) : ConstructorDelegation.Builder {
    private val arguments = mutableListOf<CodeValue>()

    override fun addArgument(argument: CodeValue): ConstructorDelegation.Builder = apply {
        arguments.add(argument)
    }

    override fun addArgument(format: String, vararg arguments: CodeArgumentPart): ConstructorDelegation.Builder =
        apply {
            this.arguments.add(CodeValue(format, *arguments))
        }

    override fun addArguments(vararg arguments: CodeValue): ConstructorDelegation.Builder = apply {
        this.arguments.addAll(arguments)
    }

    override fun addArguments(arguments: Iterable<CodeValue>): ConstructorDelegation.Builder = apply {
        this.arguments.addAll(arguments)
    }

    override fun build(): ConstructorDelegation =
        ConstructorDelegationImpl(
            kind = kind,
            arguments = arguments.toList()
        )
}

/**
 * Implementation of [KotlinConstructorSpec].
 *
 * @author ForteScarlet
 */
@OptIn(CodeGentleKotlinSpecImplementation::class)
internal data class KotlinConstructorSpecImpl(
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val parameters: List<KotlinValueParameterSpec>,
    override val kDoc: CodeValue,
    override val code: CodeValue,
    override val constructorDelegation: ConstructorDelegation?
) : KotlinConstructorSpec {
    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }
    
    override fun toString(): String {
        return "KotlinConstructorSpec(modifiers=$modifiers, parameters=$parameters, constructorDelegation=$constructorDelegation)"
    }
}

/**
 * Implementation of [KotlinConstructorSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinConstructorSpecBuilderImpl : KotlinConstructorSpec.Builder {
    private val kDoc: CodeValueBuilder = CodeValue.builder()
    private val code: CodeValueBuilder = CodeValue.builder()
    private val modifierSet = MutableKotlinModifierSet.empty()
    private val annotations = mutableListOf<AnnotationRef>()
    private val parameters = mutableListOf<KotlinValueParameterSpec>()
    private var constructorDelegation: ConstructorDelegation? = null

    override fun addModifier(modifier: KotlinModifier): KotlinConstructorSpec.Builder = apply {
        modifierSet.add(modifier)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinConstructorSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinConstructorSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addAnnotationRef(ref: AnnotationRef): KotlinConstructorSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): KotlinConstructorSpec.Builder = apply {
        annotations.addAll(refs)
    }

    override fun addParameter(parameter: KotlinValueParameterSpec): KotlinConstructorSpec.Builder = apply {
        parameters.add(parameter)
    }

    override fun addParameters(parameters: Iterable<KotlinValueParameterSpec>): KotlinConstructorSpec.Builder = apply {
        this.parameters.addAll(parameters)
    }

    override fun addParameters(vararg parameters: KotlinValueParameterSpec): KotlinConstructorSpec.Builder = apply {
        this.parameters.addAll(parameters)
    }

    override fun addKDoc(codeValue: CodeValue): KotlinConstructorSpec.Builder = apply {
        kDoc.add(codeValue)
    }

    override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinConstructorSpec.Builder =
        apply {
            kDoc.add(format, *argumentParts)
        }

    override fun addCode(codeValue: CodeValue): KotlinConstructorSpec.Builder = apply {
        code.add(codeValue)
    }

    override fun addCode(format: String, vararg argumentParts: CodeArgumentPart): KotlinConstructorSpec.Builder =
        apply {
            code.add(format, *argumentParts)
        }

    override fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): KotlinConstructorSpec.Builder =
        apply {
            code.addStatement(format, *argumentParts)
        }

    override fun addStatement(codeValue: CodeValue): KotlinConstructorSpec.Builder = apply {
        code.addStatement(codeValue)
    }

    override fun constructorDelegation(delegation: ConstructorDelegation?): KotlinConstructorSpec.Builder = apply {
        this.constructorDelegation = delegation
    }

    override fun build(): KotlinConstructorSpec =
        KotlinConstructorSpecImpl(
            annotations = annotations.toList(),
            modifiers = modifierSet.immutable(),
            parameters = parameters.toList(),
            kDoc = kDoc.build(),
            code = code.build(),
            constructorDelegation = constructorDelegation
        )
}
