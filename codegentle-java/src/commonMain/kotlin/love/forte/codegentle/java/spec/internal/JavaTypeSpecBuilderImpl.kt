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
package love.forte.codegentle.java.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.MutableJavaModifierSet
import love.forte.codegentle.java.spec.JavaFieldSpec
import love.forte.codegentle.java.spec.JavaMethodSpec
import love.forte.codegentle.java.spec.JavaTypeSpec
import love.forte.codegentle.java.spec.JavaTypeSpecBuilder

/**
 * Abstract implementation of [JavaTypeSpecBuilder] that provides common functionality
 * for all concrete builder implementations.
 *
 * @param B The concrete builder type
 */
internal abstract class JavaTypeSpecBuilderImpl<
    T : JavaTypeSpec,
    B : JavaTypeSpecBuilder<T, B>
    > : JavaTypeSpecBuilder<T, B> {

    protected val javadoc = CodeValue.builder()
    protected val staticBlock = CodeValue.builder()
    protected val initializerBlock = CodeValue.builder()

    protected val annotationRefs: MutableList<AnnotationRef> = mutableListOf()
    protected val modifierSet = MutableJavaModifierSet.empty()
    protected val typeVariableRefs: MutableList<TypeRef<TypeVariableName>> = mutableListOf()
    protected val fields: MutableList<JavaFieldSpec> = mutableListOf()
    protected val methods: MutableList<JavaMethodSpec> = mutableListOf()
    protected val subtypes: MutableList<JavaTypeSpec> = mutableListOf()

    @Suppress("UNCHECKED_CAST")
    protected open val self: B
        get() = this as B

    override fun addDoc(codeValue: CodeValue): B = self.apply {
        javadoc.addCode(codeValue)
    }

    override fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): B = self.apply {
        javadoc.addCode(format, *argumentParts)
    }

    override fun addStaticBlock(codeValue: CodeValue): B = self.apply {
        staticBlock.addCode(codeValue)
    }

    override fun addStaticBlock(format: String, vararg argumentParts: CodeArgumentPart): B = self.apply {
        staticBlock.addCode(format, *argumentParts)
    }

    override fun addInitializerBlock(codeValue: CodeValue): B = self.apply {
        initializerBlock.addCode(codeValue)
    }

    override fun addInitializerBlock(format: String, vararg argumentParts: CodeArgumentPart): B = self.apply {
        initializerBlock.addCode(format, *argumentParts)
    }

    override fun addAnnotation(ref: AnnotationRef): B = self.apply {
        annotationRefs.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): B = self.apply {
        annotationRefs.addAll(refs)
    }

    override fun addModifiers(vararg modifiers: JavaModifier): B = self.apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<JavaModifier>): B = self.apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifier(modifier: JavaModifier): B = self.apply {
        modifierSet.add(modifier)
    }

    override fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): B = self.apply {
        typeVariableRefs.addAll(typeVariables)
    }

    override fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): B = self.apply {
        typeVariableRefs.addAll(typeVariables)
    }

    override fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): B = self.apply {
        typeVariableRefs.add(typeVariable)
    }

    override fun addFields(vararg fields: JavaFieldSpec): B = self.apply {
        this@JavaTypeSpecBuilderImpl.fields.addAll(fields)
    }

    override fun addFields(fields: Iterable<JavaFieldSpec>): B = self.apply {
        this@JavaTypeSpecBuilderImpl.fields.addAll(fields)
    }

    override fun addField(field: JavaFieldSpec): B = self.apply {
        fields.add(field)
    }

    override fun addMethods(methods: Iterable<JavaMethodSpec>): B = self.apply {
        this@JavaTypeSpecBuilderImpl.methods.addAll(methods)
    }

    override fun addMethods(vararg methods: JavaMethodSpec): B = self.apply {
        this@JavaTypeSpecBuilderImpl.methods.addAll(methods)
    }

    override fun addMethod(method: JavaMethodSpec): B = self.apply {
        methods.add(method)
    }

    override fun addSubtypes(vararg types: JavaTypeSpec): B = self.apply {
        subtypes.addAll(types)
    }

    override fun addSubtypes(types: Iterable<JavaTypeSpec>): B = self.apply {
        subtypes.addAll(types)
    }

    override fun addSubtype(type: JavaTypeSpec): B = self.apply {
        subtypes.add(type)
    }
}
