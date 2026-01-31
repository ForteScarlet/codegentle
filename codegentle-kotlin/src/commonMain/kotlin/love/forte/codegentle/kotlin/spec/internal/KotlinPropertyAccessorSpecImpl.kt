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
import love.forte.codegentle.kotlin.spec.KotlinGetterSpec
import love.forte.codegentle.kotlin.spec.KotlinSetterSpec
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinGetterSpec].
 *
 * @author ForteScarlet
 */
@OptIn(CodeGentleKotlinSpecImplementation::class)
internal data class KotlinGetterSpecImpl(
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val kDoc: CodeValue,
    override val code: CodeValue
) : KotlinGetterSpec {
    override val parameters: List<KotlinValueParameterSpec> = emptyList()

    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinGetterSpec(modifiers=$modifiers)"
    }
}

/**
 * Implementation of [KotlinGetterSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinGetterSpecBuilderImpl : KotlinGetterSpec.Builder {
    private val kDoc: CodeValueBuilder = CodeValue.builder()
    private val code: CodeValueBuilder = CodeValue.builder()
    private val modifierSet = MutableKotlinModifierSet.empty()
    private val annotations = mutableListOf<AnnotationRef>()

    override fun addModifier(modifier: KotlinModifier): KotlinGetterSpec.Builder = apply {
        modifierSet.add(modifier)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinGetterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinGetterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addAnnotation(ref: AnnotationRef): KotlinGetterSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): KotlinGetterSpec.Builder = apply {
        annotations.addAll(refs)
    }

    override fun addDoc(codeValue: CodeValue): KotlinGetterSpec.Builder = apply {
        kDoc.addCode(codeValue)
    }

    override fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinGetterSpec.Builder = apply {
        kDoc.addCode(format, *argumentParts)
    }

    override fun addCode(codeValue: CodeValue): KotlinGetterSpec.Builder = apply {
        code.addCode(codeValue)
    }

    override fun addCode(format: String, vararg argumentParts: CodeArgumentPart): KotlinGetterSpec.Builder = apply {
        code.addCode(format, *argumentParts)
    }

    override fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): KotlinGetterSpec.Builder =
        apply {
            code.addStatement(format, *argumentParts)
        }

    override fun addStatement(codeValue: CodeValue): KotlinGetterSpec.Builder = apply {
        code.addStatement(codeValue)
    }

    override fun build(): KotlinGetterSpec =
        KotlinGetterSpecImpl(
            annotations = annotations.toList(),
            modifiers = modifierSet.immutable(),
            kDoc = kDoc.build(),
            code = code.build()
        )
}

/**
 * Implementation of [KotlinSetterSpec].
 *
 * @author ForteScarlet
 */
@OptIn(CodeGentleKotlinSpecImplementation::class)
internal data class KotlinSetterSpecImpl(
    override val parameterName: String,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val kDoc: CodeValue,
    override val code: CodeValue
) : KotlinSetterSpec {
    override val parameters: List<KotlinValueParameterSpec> = emptyList()

    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinSetterSpec(parameterName='$parameterName', modifiers=$modifiers)"
    }
}

/**
 * Implementation of [KotlinSetterSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinSetterSpecBuilderImpl(
    override val parameterName: String
) : KotlinSetterSpec.Builder {
    private val kDoc: CodeValueBuilder = CodeValue.builder()
    private val code: CodeValueBuilder = CodeValue.builder()
    private val modifierSet = MutableKotlinModifierSet.empty()
    private val annotations = mutableListOf<AnnotationRef>()

    override fun addModifier(modifier: KotlinModifier): KotlinSetterSpec.Builder = apply {
        modifierSet.add(modifier)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinSetterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinSetterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addAnnotation(ref: AnnotationRef): KotlinSetterSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): KotlinSetterSpec.Builder = apply {
        annotations.addAll(refs)
    }

    override fun addDoc(codeValue: CodeValue): KotlinSetterSpec.Builder = apply {
        kDoc.addCode(codeValue)
    }

    override fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinSetterSpec.Builder = apply {
        kDoc.addCode(format, *argumentParts)
    }

    override fun addCode(codeValue: CodeValue): KotlinSetterSpec.Builder = apply {
        code.addCode(codeValue)
    }

    override fun addCode(format: String, vararg argumentParts: CodeArgumentPart): KotlinSetterSpec.Builder = apply {
        code.addCode(format, *argumentParts)
    }

    override fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): KotlinSetterSpec.Builder =
        apply {
            code.addStatement(format, *argumentParts)
        }

    override fun addStatement(codeValue: CodeValue): KotlinSetterSpec.Builder = apply {
        code.addStatement(codeValue)
    }

    override fun build(): KotlinSetterSpec =
        KotlinSetterSpecImpl(
            parameterName = parameterName,
            annotations = annotations.toList(),
            modifiers = modifierSet.immutable(),
            kDoc = kDoc.build(),
            code = code.build()
        )
}
