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

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.spec.KotlinSimpleTypeSpec.Companion.validKinds
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinSimpleTypeSpec].
 *
 * @author ForteScarlet
 */
internal data class KotlinSimpleTypeSpecImpl(
    override val kind: KotlinTypeSpec.Kind,
    override val name: String,
    override val kDoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val superclass: TypeName?,
    override val superinterfaces: List<TypeName>,
    override val properties: List<KotlinPropertySpec>,
    override val initializerBlock: CodeValue,
    override val functions: List<KotlinFunctionSpec>,
    override val subtypes: List<KotlinTypeSpec>,
    override val primaryConstructor: KotlinConstructorSpec?,
    override val secondaryConstructors: List<KotlinConstructorSpec>
) : KotlinSimpleTypeSpec {
    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinSimpleTypeSpec(name='$name', kind=$kind)"
    }
}

/**
 * Implementation of [KotlinSimpleTypeSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinSimpleTypeSpecBuilderImpl(
    override val kind: KotlinTypeSpec.Kind,
    override val name: String
) : KotlinTypeSpecBuilderImpl<KotlinSimpleTypeSpec, KotlinSimpleTypeSpec.Builder>(), KotlinSimpleTypeSpec.Builder {
    init {
        require(kind in validKinds) {
            "Invalid kind $kind, must be one of $validKinds"
        }
    }

    private var superclass: TypeName? = null
    private var primaryConstructor: KotlinConstructorSpec? = null

    override val self: KotlinSimpleTypeSpec.Builder
        get() = this

    override fun superclass(superclass: TypeName): KotlinSimpleTypeSpec.Builder = apply {
        this.superclass = superclass
    }

    override fun addInitializerBlock(codeValue: CodeValue): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.initializerBlock.addCode(codeValue)
    }

    override fun addInitializerBlock(
        format: String,
        vararg argumentParts: CodeArgumentPart
    ): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.initializerBlock.addCode(format, *argumentParts)
    }


    override fun addSuperinterfaces(vararg superinterfaces: TypeName): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterface(superinterface: TypeName): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.superinterfaces.add(superinterface)
    }

    override fun addProperties(vararg properties: KotlinPropertySpec): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.properties.addAll(properties)
    }

    override fun addProperties(properties: Iterable<KotlinPropertySpec>): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.properties.addAll(properties)
    }

    override fun addProperty(property: KotlinPropertySpec): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.properties.add(property)
    }

    override fun addFunctions(functions: Iterable<KotlinFunctionSpec>): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.functions.addAll(functions)
    }

    override fun addFunctions(vararg functions: KotlinFunctionSpec): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.functions.addAll(functions)
    }

    override fun addFunction(function: KotlinFunctionSpec): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.functions.add(function)
    }

    override fun addSubtypes(types: Iterable<KotlinTypeSpec>): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.subtypes.addAll(types)
    }

    override fun addSubtypes(vararg types: KotlinTypeSpec): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.subtypes.addAll(types)
    }

    override fun addSubtype(type: KotlinTypeSpec): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.subtypes.add(type)
    }

    override fun primaryConstructor(constructor: KotlinConstructorSpec?): KotlinSimpleTypeSpec.Builder = apply {
        require(constructor == null || constructor.code.isEmpty()) {
            "Primary constructor cannot have code."
        }
        this.primaryConstructor = constructor
    }

    override fun addSecondaryConstructor(constructor: KotlinConstructorSpec): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.secondaryConstructors.add(constructor)
    }

    override fun addSecondaryConstructors(constructors: Iterable<KotlinConstructorSpec>): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.secondaryConstructors.addAll(constructors)
    }

    override fun addSecondaryConstructors(vararg constructors: KotlinConstructorSpec): KotlinSimpleTypeSpec.Builder = self.apply {
        this@KotlinSimpleTypeSpecBuilderImpl.secondaryConstructors.addAll(constructors)
    }

    override fun build(): KotlinSimpleTypeSpec {
        val primaryConstructor = primaryConstructor
        require(primaryConstructor == null || primaryConstructor.code.isEmpty()) {
            "Primary constructor cannot have code."
        }

        return KotlinSimpleTypeSpecImpl(
            kind = kind,
            name = name,
            kDoc = kDoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = modifierSet.immutable(),
            typeVariables = typeVariableRefs.toList(),
            superclass = superclass,
            superinterfaces = superinterfaces.toList(),
            properties = properties.toList(),
            initializerBlock = initializerBlock.build(),
            functions = functions.toList(),
            subtypes = subtypes.toList(),
            primaryConstructor = primaryConstructor,
            secondaryConstructors = secondaryConstructors.toList()
        )
    }
}
