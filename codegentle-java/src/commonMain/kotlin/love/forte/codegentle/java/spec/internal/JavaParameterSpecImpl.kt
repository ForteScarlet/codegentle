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
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.MutableJavaModifierSet
import love.forte.codegentle.java.spec.JavaParameterSpec
import love.forte.codegentle.java.spec.emitter.emitTo
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emitToString

/**
 * Internal builder implementation for [JavaParameterSpec].
 */
internal class JavaParameterSpecBuilderImpl(
    override val type: TypeRef<*>,
    override val name: String
) : JavaParameterSpec.Builder {
    private val javadoc = CodeValue.builder()
    private val annotations = mutableListOf<AnnotationRef>()
    private val modifiers = MutableJavaModifierSet.empty()

    override fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): JavaParameterSpec.Builder = apply {
        addDoc(CodeValue(format, *argumentParts))
    }

    override fun addDoc(codeValue: CodeValue): JavaParameterSpec.Builder = apply {
        javadoc.addCode(codeValue)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): JavaParameterSpec.Builder = apply {
        this.annotations.addAll(refs)
    }

    override fun addAnnotation(ref: AnnotationRef): JavaParameterSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addModifiers(vararg modifiers: JavaModifier): JavaParameterSpec.Builder = apply {
        modifiers.forEach { checkModifier(it) }
        this.modifiers.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<JavaModifier>): JavaParameterSpec.Builder = apply {
        modifiers.forEach { checkModifier(it) }
        this.modifiers.addAll(modifiers)
    }

    override fun addModifier(modifier: JavaModifier): JavaParameterSpec.Builder = apply {
        checkModifier(modifier)
        modifiers.add(modifier)
    }

    /**
     * Only support [final][JavaModifier.FINAL]
     */
    private fun checkModifier(modifier: JavaModifier) {
        check(modifier == JavaModifier.FINAL) {
            "Unexpected parameter modifier: $modifier"
        }
    }

    override fun build(): JavaParameterSpec {
        return JavaParameterSpecImpl(
            type = type,
            name = name,
            annotations = annotations.toList(),
            modifiers = modifiers.toSet(),
            javadoc = javadoc.build()
        )
    }
}

internal class JavaParameterSpecImpl internal constructor(
    override val type: TypeRef<*>,
    override val name: String,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<JavaModifier>,
    override val javadoc: CodeValue,
) : JavaParameterSpec {
    override fun emit(codeWriter: JavaCodeWriter) {
        emit(codeWriter, false)
    }

    override fun emit(codeWriter: JavaCodeWriter, vararg: Boolean) {
        emitTo(codeWriter, vararg)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JavaParameterSpec) return false

        if (type != other.type) return false
        if (name != other.name) return false
        if (annotations != other.annotations) return false
        if (modifiers != other.modifiers) return false
        if (javadoc != other.javadoc) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + annotations.hashCode()
        result = 31 * result + modifiers.hashCode()
        result = 31 * result + javadoc.hashCode()
        return result
    }

    override fun toString(): String {
        return emitToString()
    }
}
