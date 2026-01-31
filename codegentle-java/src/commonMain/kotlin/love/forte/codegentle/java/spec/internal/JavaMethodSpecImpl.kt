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
package love.forte.codegentle.java.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueBuilder
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.MutableJavaModifierSet
import love.forte.codegentle.java.spec.JavaMethodSpec
import love.forte.codegentle.java.spec.JavaParameterSpec
import love.forte.codegentle.java.spec.emitter.emitTo
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emitToString


internal class JavaMethodSpecImpl(
    override val name: String,
    override val javadoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<JavaModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val returnType: TypeRef<*>?,
    override val parameters: List<JavaParameterSpec>,
    override val isVarargs: Boolean,
    override val exceptions: List<TypeRef<*>>,
    override val code: CodeValue,
    override val defaultValue: CodeValue,
) : JavaMethodSpec {
    override fun emit(codeWriter: JavaCodeWriter) {
        emit(codeWriter, null, emptySet())
    }

    override fun emit(codeWriter: JavaCodeWriter, name: String?, implicitModifiers: Set<JavaModifier>) {
        emitTo(codeWriter, name, implicitModifiers)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JavaMethodSpecImpl) return false

        if (isVarargs != other.isVarargs) return false
        if (name != other.name) return false
        if (javadoc != other.javadoc) return false
        if (annotations != other.annotations) return false
        if (modifiers != other.modifiers) return false
        if (typeVariables != other.typeVariables) return false
        if (returnType != other.returnType) return false
        if (parameters != other.parameters) return false
        if (exceptions != other.exceptions) return false
        if (code != other.code) return false
        if (defaultValue != other.defaultValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isVarargs.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + javadoc.hashCode()
        result = 31 * result + annotations.hashCode()
        result = 31 * result + modifiers.hashCode()
        result = 31 * result + typeVariables.hashCode()
        result = 31 * result + (returnType?.hashCode() ?: 0)
        result = 31 * result + parameters.hashCode()
        result = 31 * result + exceptions.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + defaultValue.hashCode()
        return result
    }

    override fun toString(): String {
        return emitToString()
    }

}

/**
 * Internal builder implementation for [JavaMethodSpec].
 */
internal class JavaMethodSpecBuilderImpl(
    override var name: String,
) : JavaMethodSpec.Builder {

    init {
        require(name.isNotBlank()) { "Method name cannot be blank" }
    }

    private val javadoc = CodeValue.builder()
    private var returnType: TypeRef<*>? = null
    private val code: CodeValueBuilder = CodeValue.builder()
    private var defaultValue: CodeValue? = null
    private val exceptions = linkedSetOf<TypeRef<*>>()

    override var isVarargs: Boolean = false

    private val typeVariables: MutableList<TypeRef<TypeVariableName>> = mutableListOf()
    private val annotations: MutableList<AnnotationRef> = mutableListOf()
    private val modifiers = MutableJavaModifierSet.empty()
    private val parameters: MutableList<JavaParameterSpec> = mutableListOf()

    override fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): JavaMethodSpec.Builder = apply {
        addDoc(CodeValue(format, *argumentParts))
    }

    override fun addDoc(codeValue: CodeValue): JavaMethodSpec.Builder = apply {
        javadoc.addCode(codeValue)
    }

    override fun addAnnotation(ref: AnnotationRef): JavaMethodSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): JavaMethodSpec.Builder = apply {
        this.annotations.addAll(refs)
    }

    override fun addModifier(modifier: JavaModifier): JavaMethodSpec.Builder = apply {
        modifiers.add(modifier)
    }

    override fun addModifiers(modifiers: Iterable<JavaModifier>): JavaMethodSpec.Builder = apply {
        this.modifiers.addAll(modifiers)
    }

    override fun addModifiers(vararg modifiers: JavaModifier): JavaMethodSpec.Builder = apply {
        this.modifiers.addAll(modifiers)
    }

    override fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): JavaMethodSpec.Builder = apply {
        typeVariables.add(typeVariable)
    }

    override fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): JavaMethodSpec.Builder =
        apply {
            this.typeVariables.addAll(typeVariables)
        }

    override fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): JavaMethodSpec.Builder =
        apply {
            this.typeVariables.addAll(typeVariables)
        }

    override fun addParameter(parameter: JavaParameterSpec): JavaMethodSpec.Builder = apply {
        parameters.add(parameter)
    }

    override fun addParameters(parameters: Iterable<JavaParameterSpec>): JavaMethodSpec.Builder = apply {
        this.parameters.addAll(parameters)
    }

    override fun addParameters(vararg parameters: JavaParameterSpec): JavaMethodSpec.Builder = apply {
        this.parameters.addAll(parameters)
    }

    override fun returns(typeRef: TypeRef<*>): JavaMethodSpec.Builder = apply {
        check(name != JavaMethodSpec.CONSTRUCTOR) { "Constructor cannot have return type." }
        returnType = typeRef
    }

    override fun varargs(): JavaMethodSpec.Builder = varargs(true)

    override fun varargs(varargs: Boolean): JavaMethodSpec.Builder = apply {
        this.isVarargs = varargs
    }

    override fun addException(exception: TypeRef<*>): JavaMethodSpec.Builder = apply {
        exceptions.add(exception)
    }

    override fun addExceptions(vararg exceptions: TypeRef<*>): JavaMethodSpec.Builder = apply {
        this.exceptions.addAll(exceptions)
    }

    override fun addExceptions(exceptions: Iterable<TypeRef<*>>): JavaMethodSpec.Builder = apply {
        this.exceptions.addAll(exceptions)
    }

    override fun addCode(codeValue: CodeValue): JavaMethodSpec.Builder = apply {
        this.code.addCode(codeValue)
    }

    override fun addCode(format: String, vararg argumentParts: CodeArgumentPart): JavaMethodSpec.Builder = apply {
        addCode(CodeValue(format, *argumentParts))
    }

    override fun addComment(format: String, vararg argumentParts: CodeArgumentPart): JavaMethodSpec.Builder = apply {
        addCode(CodeValue("// $format\n", *argumentParts))
    }

    override fun defaultValue(format: String, vararg argumentParts: CodeArgumentPart): JavaMethodSpec.Builder =
        defaultValue(CodeValue(format, *argumentParts))

    override fun defaultValue(codeBlock: CodeValue): JavaMethodSpec.Builder = apply {
        check(defaultValue == null) { "`defaultValue` was already set" }
        this.defaultValue = codeBlock
    }

    override fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): JavaMethodSpec.Builder = apply {
        addStatement(CodeValue(format, *argumentParts))
    }

    override fun addStatement(codeValue: CodeValue): JavaMethodSpec.Builder = apply {
        code.addStatement(codeValue)
    }

    override fun build(): JavaMethodSpec {
        return JavaMethodSpecImpl(
            name = name,
            javadoc = javadoc.build(),
            annotations = annotations.toList(),
            modifiers = modifiers.immutable(),
            typeVariables = typeVariables.toList(),
            returnType = returnType,
            parameters = parameters.toList(),
            isVarargs = isVarargs,
            exceptions = exceptions.toList(),
            code = code.build(),
            defaultValue = defaultValue ?: CodeValue()
        )
    }
}
