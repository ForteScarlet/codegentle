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
 * Implementation of [KotlinAnonymousClassTypeSpec].
 *
 * @author ForteScarlet
 */
@OptIn(CodeGentleKotlinSpecImplementation::class)
internal data class KotlinAnonymousClassTypeSpecImpl(
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
    override val constructorDelegation: KotlinConstructorDelegation
) : KotlinAnonymousClassTypeSpec {

    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinAnonymousClassTypeSpec(superclass=$superclass, superinterfaces=$superinterfaces)"
    }
}

/**
 * Implementation of [KotlinAnonymousClassTypeSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinAnonymousClassTypeSpecBuilderImpl : 
    KotlinTypeSpecBuilderImpl<KotlinAnonymousClassTypeSpec, KotlinAnonymousClassTypeSpec.Builder>(), 
    KotlinAnonymousClassTypeSpec.Builder {
    
    private var superclass: TypeName? = null
    private val superConstructorArguments: MutableList<CodeValue> = mutableListOf()

    override val self: KotlinAnonymousClassTypeSpec.Builder
        get() = this

    override fun superclass(superclass: TypeName): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superclass = superclass
    }

    override fun addInitializerBlock(codeValue: CodeValue): KotlinAnonymousClassTypeSpec.Builder = self.apply {
        this@KotlinAnonymousClassTypeSpecBuilderImpl.initializerBlock.addCode(codeValue)
    }

    override fun addInitializerBlock(format: String, vararg argumentParts: CodeArgumentPart): KotlinAnonymousClassTypeSpec.Builder = self.apply {
        this@KotlinAnonymousClassTypeSpecBuilderImpl.initializerBlock.addCode(format, *argumentParts)
    }


    override fun addSuperinterfaces(vararg superinterfaces: TypeName): KotlinAnonymousClassTypeSpec.Builder = self.apply {
        this@KotlinAnonymousClassTypeSpecBuilderImpl.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): KotlinAnonymousClassTypeSpec.Builder = self.apply {
        this@KotlinAnonymousClassTypeSpecBuilderImpl.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterface(superinterface: TypeName): KotlinAnonymousClassTypeSpec.Builder = self.apply {
        this@KotlinAnonymousClassTypeSpecBuilderImpl.superinterfaces.add(superinterface)
    }

    override fun addProperties(vararg properties: KotlinPropertySpec): KotlinAnonymousClassTypeSpec.Builder = self.apply {
        this@KotlinAnonymousClassTypeSpecBuilderImpl.properties.addAll(properties)
    }

    override fun addProperties(properties: Iterable<KotlinPropertySpec>): KotlinAnonymousClassTypeSpec.Builder = self.apply {
        this@KotlinAnonymousClassTypeSpecBuilderImpl.properties.addAll(properties)
    }

    override fun addProperty(property: KotlinPropertySpec): KotlinAnonymousClassTypeSpec.Builder = self.apply {
        this@KotlinAnonymousClassTypeSpecBuilderImpl.properties.add(property)
    }

    override fun addFunctions(functions: Iterable<KotlinFunctionSpec>): KotlinAnonymousClassTypeSpec.Builder = self.apply {
        this@KotlinAnonymousClassTypeSpecBuilderImpl.functions.addAll(functions)
    }

    override fun addFunctions(vararg functions: KotlinFunctionSpec): KotlinAnonymousClassTypeSpec.Builder = self.apply {
        this@KotlinAnonymousClassTypeSpecBuilderImpl.functions.addAll(functions)
    }

    override fun addFunction(function: KotlinFunctionSpec): KotlinAnonymousClassTypeSpec.Builder = self.apply {
        this@KotlinAnonymousClassTypeSpecBuilderImpl.functions.add(function)
    }

    override fun addSuperConstructorArguments(vararg arguments: CodeValue): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superConstructorArguments.addAll(arguments)
    }

    override fun addSuperConstructorArguments(arguments: Iterable<CodeValue>): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superConstructorArguments.addAll(arguments)
    }

    override fun addSuperConstructorArgument(argument: CodeValue): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superConstructorArguments.add(argument)
    }

    override fun addSuperConstructorArgument(format: String, vararg argumentParts: CodeArgumentPart): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superConstructorArguments.add(CodeValue(format, *argumentParts))
    }

    override fun build(): KotlinAnonymousClassTypeSpec {
        val superConstructorArguments = this.superConstructorArguments

        return KotlinAnonymousClassTypeSpecImpl(
            kDoc = kDoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = modifierSet.immutable(),
            typeVariables = typeVariableRefs.toList(),
            superclass = superclass,
            superinterfaces = superinterfaces.toList(),
            properties = properties.toList(),
            initializerBlock = initializerBlock.build(),
            functions = functions.toList(),
            subtypes = emptyList(),
            constructorDelegation = KotlinConstructorDelegation(KotlinConstructorDelegation.Kind.SUPER) {
                addArguments(superConstructorArguments)
            }
        )
    }
}
