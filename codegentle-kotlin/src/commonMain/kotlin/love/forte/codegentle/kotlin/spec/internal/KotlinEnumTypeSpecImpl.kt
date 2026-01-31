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
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinEnumTypeSpec].
 *
 * @author ForteScarlet
 */
internal data class KotlinEnumTypeSpecImpl(
    override val name: String,
    override val enumConstants: Map<String, KotlinAnonymousClassTypeSpec?>,
    override val primaryConstructor: KotlinConstructorSpec?,
    override val secondaryConstructors: List<KotlinConstructorSpec>,
    override val kDoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val superinterfaces: List<TypeName>,
    override val properties: List<KotlinPropertySpec>,
    override val initializerBlock: CodeValue,
    override val functions: List<KotlinFunctionSpec>,
    override val subtypes: List<KotlinTypeSpec>
) : KotlinEnumTypeSpec {
    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinEnumTypeSpec(name='$name', kind=$kind)"
    }
}

/**
 * Implementation of [KotlinEnumTypeSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinEnumTypeSpecBuilderImpl(
    override val name: String
) : KotlinTypeSpecBuilderImpl<KotlinEnumTypeSpec, KotlinEnumTypeSpec.Builder>(), KotlinEnumTypeSpec.Builder {
    
    init {
        modifierSet.add(KotlinModifier.ENUM)
    }
    
    private val enumConstants = linkedMapOf<String, KotlinAnonymousClassTypeSpec?>()
    private var primaryConstructor: KotlinConstructorSpec? = null

    override val self: KotlinEnumTypeSpec.Builder
        get() = this

    override fun addInitializerBlock(codeValue: CodeValue): KotlinEnumTypeSpec.Builder = self.apply {
        this@KotlinEnumTypeSpecBuilderImpl.initializerBlock.addCode(codeValue)
    }

    override fun addInitializerBlock(format: String, vararg argumentParts: CodeArgumentPart): KotlinEnumTypeSpec.Builder = self.apply {
        this@KotlinEnumTypeSpecBuilderImpl.initializerBlock.addCode(format, *argumentParts)
    }


    override fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): KotlinEnumTypeSpec.Builder = self.apply {
        this@KotlinEnumTypeSpecBuilderImpl.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterface(superinterface: TypeName): KotlinEnumTypeSpec.Builder = self.apply {
        this@KotlinEnumTypeSpecBuilderImpl.superinterfaces.add(superinterface)
    }

    override fun addProperties(properties: Iterable<KotlinPropertySpec>): KotlinEnumTypeSpec.Builder = self.apply {
        this@KotlinEnumTypeSpecBuilderImpl.properties.addAll(properties)
    }

    override fun addProperty(property: KotlinPropertySpec): KotlinEnumTypeSpec.Builder = self.apply {
        this@KotlinEnumTypeSpecBuilderImpl.properties.add(property)
    }

    override fun addFunctions(functions: Iterable<KotlinFunctionSpec>): KotlinEnumTypeSpec.Builder = self.apply {
        this@KotlinEnumTypeSpecBuilderImpl.functions.addAll(functions)
    }

    override fun addFunction(function: KotlinFunctionSpec): KotlinEnumTypeSpec.Builder = self.apply {
        this@KotlinEnumTypeSpecBuilderImpl.functions.add(function)
    }

    override fun addEnumConstant(name: String): KotlinEnumTypeSpec.Builder = apply {
        this.enumConstants[name] = null
    }

    override fun addEnumConstant(name: String, typeSpec: KotlinAnonymousClassTypeSpec): KotlinEnumTypeSpec.Builder = apply {
        this.enumConstants[name] = typeSpec
    }

    override fun primaryConstructor(constructor: KotlinConstructorSpec?): KotlinEnumTypeSpec.Builder = apply {
        this.primaryConstructor = constructor
    }

    override fun addSecondaryConstructor(constructor: KotlinConstructorSpec): KotlinEnumTypeSpec.Builder = self.apply {
        this@KotlinEnumTypeSpecBuilderImpl.secondaryConstructors.add(constructor)
    }

    override fun addSecondaryConstructors(constructors: Iterable<KotlinConstructorSpec>): KotlinEnumTypeSpec.Builder = self.apply {
        this@KotlinEnumTypeSpecBuilderImpl.secondaryConstructors.addAll(constructors)
    }

    override fun addSecondaryConstructors(vararg constructors: KotlinConstructorSpec): KotlinEnumTypeSpec.Builder = self.apply {
        this@KotlinEnumTypeSpecBuilderImpl.secondaryConstructors.addAll(constructors)
    }

    override fun build(): KotlinEnumTypeSpec {
        val immutableModifiers = modifierSet.immutable()

        check(KotlinModifier.ENUM in immutableModifiers) {
            "Enum class $name must have `${KotlinModifier.ENUM}` modifier, but $immutableModifiers"
        }

        return KotlinEnumTypeSpecImpl(
            name = name,
            enumConstants = enumConstants.toMap(linkedMapOf()),
            primaryConstructor = primaryConstructor,
            secondaryConstructors = secondaryConstructors.toList(),
            kDoc = kDoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = immutableModifiers,
            typeVariables = typeVariableRefs.toList(),
            superinterfaces = superinterfaces.toList(),
            properties = properties.toList(),
            initializerBlock = initializerBlock.build(),
            functions = functions.toList(),
            subtypes = emptyList()
        )
    }
}
