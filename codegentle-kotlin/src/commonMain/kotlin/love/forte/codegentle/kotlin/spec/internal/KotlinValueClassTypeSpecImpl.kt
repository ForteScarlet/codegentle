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
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.KotlinConstructorSpec
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec
import love.forte.codegentle.kotlin.spec.KotlinPropertySpec
import love.forte.codegentle.kotlin.spec.KotlinValueClassTypeSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinValueClassTypeSpec].
 *
 * @author ForteScarlet
 */
internal data class KotlinValueClassTypeSpecImpl(
    override val name: String,
    override val primaryConstructor: KotlinConstructorSpec,
    override val secondaryConstructors: List<KotlinConstructorSpec>,
    override val kDoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val superinterfaces: List<TypeName>,
    override val properties: List<KotlinPropertySpec>,
    override val initializerBlock: CodeValue,
    override val functions: List<KotlinFunctionSpec>,
) : KotlinValueClassTypeSpec {
    override val superclass: TypeName? = null

    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinValueClassSpec(name='$name', kind=$kind)"
    }
}

/**
 * Builder implementation for [KotlinValueClassTypeSpec].
 */
internal class KotlinValueClassSpecBuilderImpl(
    override val name: String,
    override val primaryConstructor: KotlinConstructorSpec
) : KotlinValueClassTypeSpec.Builder {
    init {
        // Validate that the constructor cannot have constructorDelegation
        require(primaryConstructor.constructorDelegation == null) {
            "Value class primary constructor cannot have constructorDelegation " +
                "since value classes cannot inherit from other classes, " +
                "but found constructorDelegation: ${primaryConstructor.constructorDelegation}"
        }
        
        // Validate that the constructor has exactly one parameter and it's immutable
        require(primaryConstructor.parameters.size == 1) {
            "Value class primary constructor must have exactly one parameter, " +
                "but found ${primaryConstructor.parameters.size} parameters"
        }
        
        val parameter = primaryConstructor.parameters.first()
        val propertyization = parameter.propertyfication
        require(propertyization?.mutable == false) {
            "The primary constructor parameter property of value class must be immutable " +
                "(`propertyization.mutable` must be `false`), " +
                "but current parameter's `propertyization.mutable` is ${propertyization?.mutable}"
        }
    }

    private val kDoc = CodeValue.builder()
    private val initializerBlock = CodeValue.builder()

    private val annotationRefs: MutableList<AnnotationRef> = mutableListOf()
    private val modifierSet = MutableKotlinModifierSet.of(KotlinModifier.VALUE)
    private val typeVariableRefs: MutableList<TypeRef<TypeVariableName>> = mutableListOf()
    private val superinterfaces: MutableList<TypeName> = mutableListOf()
    private val properties: MutableList<KotlinPropertySpec> = mutableListOf()
    private val functions: MutableList<KotlinFunctionSpec> = mutableListOf()
    private val secondaryConstructors: MutableList<KotlinConstructorSpec> = mutableListOf()

    override fun addDoc(codeValue: CodeValue): KotlinValueClassTypeSpec.Builder = apply {
        kDoc.addCode(codeValue)
    }

    override fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinValueClassTypeSpec.Builder = apply {
        kDoc.addCode(format, *argumentParts)
    }

    override fun addInitializerBlock(codeValue: CodeValue): KotlinValueClassTypeSpec.Builder = apply {
        this.initializerBlock.addCode(codeValue)
    }

    override fun addInitializerBlock(
        format: String,
        vararg argumentParts: CodeArgumentPart
    ): KotlinValueClassTypeSpec.Builder = apply {
        this.initializerBlock.addCode(format, *argumentParts)
    }

    override fun addAnnotation(ref: AnnotationRef): KotlinValueClassTypeSpec.Builder = apply {
        annotationRefs.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): KotlinValueClassTypeSpec.Builder = apply {
        annotationRefs.addAll(refs)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinValueClassTypeSpec.Builder = apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinValueClassTypeSpec.Builder = apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifier(modifier: KotlinModifier): KotlinValueClassTypeSpec.Builder = apply {
        this.modifierSet.add(modifier)
    }

    override fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): KotlinValueClassTypeSpec.Builder =
        apply {
            this.typeVariableRefs.addAll(typeVariables)
        }

    override fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): KotlinValueClassTypeSpec.Builder =
        apply {
            this.typeVariableRefs.addAll(typeVariables)
        }

    override fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): KotlinValueClassTypeSpec.Builder = apply {
        this.typeVariableRefs.add(typeVariable)
    }

    override fun addSuperinterfaces(vararg superinterfaces: TypeName): KotlinValueClassTypeSpec.Builder = apply {
        this.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): KotlinValueClassTypeSpec.Builder = apply {
        this.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterface(superinterface: TypeName): KotlinValueClassTypeSpec.Builder = apply {
        this.superinterfaces.add(superinterface)
    }

    override fun addProperties(vararg properties: KotlinPropertySpec): KotlinValueClassTypeSpec.Builder = apply {
        this.properties.addAll(properties)
    }

    override fun addProperties(properties: Iterable<KotlinPropertySpec>): KotlinValueClassTypeSpec.Builder = apply {
        this.properties.addAll(properties)
    }

    override fun addProperty(property: KotlinPropertySpec): KotlinValueClassTypeSpec.Builder = apply {
        this.properties.add(property)
    }

    override fun addFunctions(functions: Iterable<KotlinFunctionSpec>): KotlinValueClassTypeSpec.Builder = apply {
        this.functions.addAll(functions)
    }

    override fun addFunctions(vararg functions: KotlinFunctionSpec): KotlinValueClassTypeSpec.Builder = apply {
        this.functions.addAll(functions)
    }

    override fun addFunction(function: KotlinFunctionSpec): KotlinValueClassTypeSpec.Builder = apply {
        this.functions.add(function)
    }

    override fun addSecondaryConstructor(constructor: KotlinConstructorSpec): KotlinValueClassTypeSpec.Builder = apply {
        this.secondaryConstructors.add(constructor)
    }

    override fun addSecondaryConstructors(constructors: Iterable<KotlinConstructorSpec>): KotlinValueClassTypeSpec.Builder = apply {
        this.secondaryConstructors.addAll(constructors)
    }

    override fun addSecondaryConstructors(vararg constructors: KotlinConstructorSpec): KotlinValueClassTypeSpec.Builder = apply {
        this.secondaryConstructors.addAll(constructors)
    }

    override fun build(): KotlinValueClassTypeSpec {
        val immutableModifiers = MutableKotlinModifierSet.of(modifierSet).apply {
            add(KotlinModifier.VALUE)
        }.immutable()

        return KotlinValueClassTypeSpecImpl(
            name = name,
            primaryConstructor = primaryConstructor,
            secondaryConstructors = secondaryConstructors.toList(),
            kDoc = kDoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = immutableModifiers,
            typeVariables = typeVariableRefs.toList(),
            superinterfaces = superinterfaces.toList(),
            properties = properties.toList(),
            initializerBlock = initializerBlock.build(),
            functions = functions.toList()
        )
    }
}
