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
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec
import love.forte.codegentle.kotlin.spec.KotlinObjectTypeSpec
import love.forte.codegentle.kotlin.spec.KotlinPropertySpec
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinObjectTypeSpec].
 *
 * @author ForteScarlet
 */
internal data class KotlinObjectTypeSpecImpl(
    override val name: String,
    override val kDoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val superinterfaces: List<TypeName>,
    override val properties: List<KotlinPropertySpec>,
    override val initializerBlock: CodeValue,
    override val functions: List<KotlinFunctionSpec>,
    override val subtypes: List<KotlinTypeSpec>
) : KotlinObjectTypeSpec {
    override val superclass: TypeName? = null

    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinObjectTypeSpec(name='$name', kind=$kind)"
    }
}

/**
 * Builder implementation for [KotlinObjectTypeSpec].
 *
 * @author ForteScarlet
 */
internal class KotlinObjectTypeSpecBuilderImpl(
    override val name: String,
    override val isCompanion: Boolean
) : KotlinTypeSpecBuilderImpl<KotlinObjectTypeSpec, KotlinObjectTypeSpec.Builder>(), KotlinObjectTypeSpec.Builder {
    
    init {
        if (isCompanion) {
            modifierSet.add(KotlinModifier.COMPANION)
        }
    }

    override val self: KotlinObjectTypeSpec.Builder
        get() = this

    override fun addInitializerBlock(codeValue: CodeValue): KotlinObjectTypeSpec.Builder = self.apply {
        this@KotlinObjectTypeSpecBuilderImpl.initializerBlock.addCode(codeValue)
    }

    override fun addInitializerBlock(
        format: String,
        vararg argumentParts: CodeArgumentPart
    ): KotlinObjectTypeSpec.Builder = self.apply {
        this@KotlinObjectTypeSpecBuilderImpl.initializerBlock.addCode(format, *argumentParts)
    }


    override fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): KotlinObjectTypeSpec.Builder = self.apply {
        this@KotlinObjectTypeSpecBuilderImpl.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterface(superinterface: TypeName): KotlinObjectTypeSpec.Builder = self.apply {
        this@KotlinObjectTypeSpecBuilderImpl.superinterfaces.add(superinterface)
    }

    override fun addProperties(properties: Iterable<KotlinPropertySpec>): KotlinObjectTypeSpec.Builder = self.apply {
        this@KotlinObjectTypeSpecBuilderImpl.properties.addAll(properties)
    }

    override fun addProperty(property: KotlinPropertySpec): KotlinObjectTypeSpec.Builder = self.apply {
        this@KotlinObjectTypeSpecBuilderImpl.properties.add(property)
    }

    override fun addFunctions(functions: Iterable<KotlinFunctionSpec>): KotlinObjectTypeSpec.Builder = self.apply {
        this@KotlinObjectTypeSpecBuilderImpl.functions.addAll(functions)
    }

    override fun addFunction(function: KotlinFunctionSpec): KotlinObjectTypeSpec.Builder = self.apply {
        this@KotlinObjectTypeSpecBuilderImpl.functions.add(function)
    }

    override fun addSubtype(subtype: KotlinTypeSpec): KotlinObjectTypeSpec.Builder = self.apply {
        this@KotlinObjectTypeSpecBuilderImpl.subtypes.add(subtype)
    }

    override fun addSubtypes(subtypes: Iterable<KotlinTypeSpec>): KotlinObjectTypeSpec.Builder = self.apply {
        this@KotlinObjectTypeSpecBuilderImpl.subtypes.addAll(subtypes)
    }

    override fun build(): KotlinObjectTypeSpec {
        val immutableModifiers = modifierSet.immutable()
        if (isCompanion) {
            check(KotlinModifier.COMPANION in immutableModifiers) {
                "Companion object `$name` must have `${KotlinModifier.COMPANION}` modifier, but $immutableModifiers"
            }
        }

        return KotlinObjectTypeSpecImpl(
            name = name,
            kDoc = kDoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = immutableModifiers,
            typeVariables = typeVariableRefs.toList(),
            superinterfaces = superinterfaces.toList(),
            properties = properties.toList(),
            initializerBlock = initializerBlock.build(),
            functions = functions.toList(),
            subtypes = subtypes.toList()
        )
    }
}
