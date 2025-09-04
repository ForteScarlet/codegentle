/*
 * Copyright (C) 2025 Forte Scarlet
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

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinAnnotationTypeSpec].
 *
 * @author ForteScarlet
 */
internal data class KotlinAnnotationTypeSpecImpl(
    override val name: String,
    override val kDoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val properties: List<KotlinPropertySpec>,
    override val functions: List<KotlinFunctionSpec>,
    override val subtypes: List<KotlinTypeSpec>
) : KotlinAnnotationTypeSpec {
    override val initializerBlock: CodeValue = CodeValue()

    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinAnnotationTypeSpec(name='$name', kind=$kind)"
    }
}

/**
 * Implementation of [KotlinAnnotationTypeSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinAnnotationTypeSpecBuilderImpl(
    override val name: String
) : KotlinTypeSpecBuilderImpl<KotlinAnnotationTypeSpec, KotlinAnnotationTypeSpec.Builder>(),
    KotlinAnnotationTypeSpec.Builder {
    
    init {
        modifierSet.add(KotlinModifier.ANNOTATION)
    }

    override val self: KotlinAnnotationTypeSpec.Builder
        get() = this


    override fun addProperties(vararg properties: KotlinPropertySpec): KotlinAnnotationTypeSpec.Builder = apply {
        properties.forEach {
            addProperty(it)
        }
    }

    override fun addProperties(properties: Iterable<KotlinPropertySpec>): KotlinAnnotationTypeSpec.Builder = apply {
        properties.forEach {
            addProperty(it)
        }
    }

    override fun addProperty(property: KotlinPropertySpec): KotlinAnnotationTypeSpec.Builder = apply {
        checkProperty(property)
        properties.add(property)
    }

    private fun checkProperty(property: KotlinPropertySpec) {
        require(property.immutable) {
            "Annotation's property must be immutable, but $property"
        }
    }

    override fun build(): KotlinAnnotationTypeSpec {
        return KotlinAnnotationTypeSpecImpl(
            name = name,
            kDoc = kDoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = modifierSet.immutable(),
            typeVariables = typeVariableRefs.toList(),
            properties = properties.toList(),
            functions = emptyList(),
            subtypes = emptyList()
        )
    }
}
