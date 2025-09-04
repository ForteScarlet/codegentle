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
import love.forte.codegentle.kotlin.spec.*

/**
 * Abstract base implementation of [KotlinTypeSpecBuilder] that provides common functionality
 * to reduce boilerplate code in concrete builder implementations.
 *
 * @author ForteScarlet
 */
internal abstract class KotlinTypeSpecBuilderImpl<T : KotlinTypeSpec, B : KotlinTypeSpecBuilder<T, B>> :
    KotlinTypeSpecBuilder<T, B> {

    // Common properties that most builders need
    protected val kDoc = CodeValue.builder()
    protected val initializerBlock = CodeValue.builder()
    protected val annotationRefs: MutableList<AnnotationRef> = mutableListOf()
    protected val modifierSet = MutableKotlinModifierSet.empty()
    protected val typeVariableRefs: MutableList<TypeRef<TypeVariableName>> = mutableListOf()
    protected val superinterfaces: MutableList<TypeName> = mutableListOf()
    protected val properties: MutableList<KotlinPropertySpec> = mutableListOf()
    protected val functions: MutableList<KotlinFunctionSpec> = mutableListOf()
    protected val subtypes: MutableList<KotlinTypeSpec> = mutableListOf()
    protected val secondaryConstructors: MutableList<KotlinConstructorSpec> = mutableListOf()

    protected abstract val self: B

    // Common documentation methods
    override fun addDoc(codeValue: CodeValue): B = self.apply {
        kDoc.addCode(codeValue)
    }

    override fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): B = self.apply {
            kDoc.addCode(format, *argumentParts)
        }


    // Common annotation methods
    override fun addAnnotation(ref: AnnotationRef): B = self.apply {
        annotationRefs.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): B = self.apply {
        annotationRefs.addAll(refs)
    }

    // Common modifier methods
    override fun addModifiers(vararg modifiers: KotlinModifier): B = self.apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): B = self.apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifier(modifier: KotlinModifier): B = self.apply {
        modifierSet.add(modifier)
    }

    // Common type variable methods
    override fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): B = self.apply {
        typeVariableRefs.addAll(typeVariables)
    }

    override fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): B = self.apply {
        typeVariableRefs.addAll(typeVariables)
    }

    override fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): B = self.apply {
        typeVariableRefs.add(typeVariable)
    }
}
