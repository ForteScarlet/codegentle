/*
 * Copyright (C) 2025-2026 Forte Scarlet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueBuilder
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.CodeGentleKotlinSpecImplementation
import love.forte.codegentle.kotlin.spec.KotlinConstructorDelegation
import love.forte.codegentle.kotlin.spec.KotlinConstructorSpec
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinConstructorDelegation].
 *
 * @author ForteScarlet
 */
internal data class KotlinConstructorDelegationImpl(
    override val kind: KotlinConstructorDelegation.Kind,
    override val arguments: List<CodeValue>
) : KotlinConstructorDelegation {

    override fun toString(): String {
        return "ConstructorDelegation(kind=$kind, arguments=$arguments)"
    }
}

/**
 * Implementation of [KotlinConstructorDelegation.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinConstructorDelegationBuilderImpl(
    private val kind: KotlinConstructorDelegation.Kind
) : KotlinConstructorDelegation.Builder {
    private val arguments = mutableListOf<CodeValue>()

    override fun addArgument(argument: CodeValue): KotlinConstructorDelegation.Builder = apply {
        arguments.add(argument)
    }

    override fun addArgument(format: String, vararg arguments: CodeArgumentPart): KotlinConstructorDelegation.Builder =
        apply {
            this.arguments.add(CodeValue(format, *arguments))
        }

    override fun addArguments(vararg arguments: CodeValue): KotlinConstructorDelegation.Builder = apply {
        this.arguments.addAll(arguments)
    }

    override fun addArguments(arguments: Iterable<CodeValue>): KotlinConstructorDelegation.Builder = apply {
        this.arguments.addAll(arguments)
    }

    override fun build(): KotlinConstructorDelegation =
        KotlinConstructorDelegationImpl(
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
    override val constructorDelegation: KotlinConstructorDelegation?
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
    private var constructorDelegation: KotlinConstructorDelegation? = null

    override fun addModifier(modifier: KotlinModifier): KotlinConstructorSpec.Builder = apply {
        modifierSet.add(modifier)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinConstructorSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinConstructorSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addAnnotation(ref: AnnotationRef): KotlinConstructorSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): KotlinConstructorSpec.Builder = apply {
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

    override fun addDoc(codeValue: CodeValue): KotlinConstructorSpec.Builder = apply {
        kDoc.addCode(codeValue)
    }

    override fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinConstructorSpec.Builder =
        apply {
            kDoc.addCode(format, *argumentParts)
        }

    override fun addCode(codeValue: CodeValue): KotlinConstructorSpec.Builder = apply {
        code.addCode(codeValue)
    }

    override fun addCode(format: String, vararg argumentParts: CodeArgumentPart): KotlinConstructorSpec.Builder =
        apply {
            code.addCode(format, *argumentParts)
        }

    override fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): KotlinConstructorSpec.Builder =
        apply {
            code.addStatement(format, *argumentParts)
        }

    override fun addStatement(codeValue: CodeValue): KotlinConstructorSpec.Builder = apply {
        code.addStatement(codeValue)
    }

    override fun constructorDelegation(delegation: KotlinConstructorDelegation?): KotlinConstructorSpec.Builder = apply {
        this.constructorDelegation = delegation
    }

    override fun build(): KotlinConstructorSpec {
        return KotlinConstructorSpecImpl(
            annotations = annotations.toList(),
            modifiers = modifierSet.immutable(),
            parameters = parameters.toList(),
            kDoc = kDoc.build(),
            code = code.build(),
            constructorDelegation = constructorDelegation
        )
    }
}
