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
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 *
 * @author ForteScarlet
 */
internal data class KotlinValueParameterSpecImpl(
    override val name: String,
    override val typeRef: TypeRef<*>,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val kDoc: CodeValue,
    override val defaultValue: CodeValue?,
    override val propertyfication: KotlinValueParameterSpec.Propertyfication?
) : KotlinValueParameterSpec {
    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinValueParameterSpec(name='$name', type=${typeRef.typeName})"
    }
}

/**
 * Internal implementation of [KotlinValueParameterSpec.Propertyfication].
 */
internal data class PropertyficationImpl(
    override val mutable: Boolean
) : KotlinValueParameterSpec.Propertyfication

/**
 * Internal implementation of [KotlinValueParameterSpec.PropertyficationBuilder].
 */
internal class PropertyficationBuilderImpl : KotlinValueParameterSpec.PropertyficationBuilder {
    override var mutable: Boolean = false

    override fun mutable(mutable: Boolean): KotlinValueParameterSpec.PropertyficationBuilder = apply {
        this.mutable = mutable
    }

    override fun build(): KotlinValueParameterSpec.Propertyfication = PropertyficationImpl(mutable)
}

/**
 * Implementation of [KotlinValueParameterSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinValueParameterSpecBuilderImpl(
    override val name: String,
    override val type: TypeRef<*>
) : KotlinValueParameterSpec.Builder {
    private val modifierSet = MutableKotlinModifierSet.empty()
    private var defaultValue: CodeValue? = null
    private val kDoc: CodeValueBuilder = CodeValue.builder()
    private val annotations = mutableListOf<AnnotationRef>()
    private var propertyfication: KotlinValueParameterSpec.Propertyfication? = null

    override fun addModifier(modifier: KotlinModifier): KotlinValueParameterSpec.Builder = apply {
        modifierSet.add(modifier)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinValueParameterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinValueParameterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun defaultValue(codeValue: CodeValue): KotlinValueParameterSpec.Builder = apply {
        defaultValue = codeValue
    }

    override fun defaultValue(format: String, vararg argumentParts: CodeArgumentPart): KotlinValueParameterSpec.Builder =
        apply {
            defaultValue = CodeValue(format, *argumentParts)
        }

    override fun addDoc(codeValue: CodeValue): KotlinValueParameterSpec.Builder = apply {
        kDoc.addCode(codeValue)
    }

    override fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinValueParameterSpec.Builder = apply {
        kDoc.addCode(format, *argumentParts)
    }

    override fun addAnnotation(ref: AnnotationRef): KotlinValueParameterSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): KotlinValueParameterSpec.Builder = apply {
        annotations.addAll(refs)
    }

    override fun propertyfy(propertyfication: KotlinValueParameterSpec.Propertyfication): KotlinValueParameterSpec.Builder = apply {
        this.propertyfication = propertyfication
    }

    /**
     * Build [KotlinValueParameterSpec].
     */
    override fun build(): KotlinValueParameterSpec =
        KotlinValueParameterSpecImpl(
            name = name,
            typeRef = type,
            annotations = annotations.toList(),
            modifiers = modifierSet.immutable(),
            kDoc = kDoc.build(),
            defaultValue = defaultValue,
            propertyfication = propertyfication
        )
}
