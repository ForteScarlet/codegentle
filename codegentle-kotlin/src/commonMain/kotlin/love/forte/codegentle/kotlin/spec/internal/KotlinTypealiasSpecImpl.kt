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

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinTypealiasSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Internal implementation of [KotlinTypealiasSpec].
 */
internal data class KotlinTypealiasSpecImpl(
    override val name: String,
    override val type: TypeRef<*>,
    override val kDoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
) : KotlinTypealiasSpec {

    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinTypealiasSpec(name=$name, type=$type)"
    }
}

/**
 * Internal builder implementation for [KotlinTypealiasSpec].
 */
internal class KotlinTypealiasSpecBuilderImpl(
    override val name: String,
    override val type: TypeRef<*>
) : KotlinTypeSpecBuilderImpl<KotlinTypealiasSpec, KotlinTypealiasSpec.Builder>(), KotlinTypealiasSpec.Builder {

    init {
        require(name.isNotBlank()) { "Typealias name cannot be blank" }
    }

    override val self: KotlinTypealiasSpec.Builder
        get() = this


    override fun build(): KotlinTypealiasSpec {
        return KotlinTypealiasSpecImpl(
            name = name,
            type = type,
            kDoc = kDoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = modifierSet.immutable(),
            typeVariables = typeVariableRefs.toList(),
        )
    }
}
