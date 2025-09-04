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
import love.forte.codegentle.java.spec.JavaFieldSpec
import love.forte.codegentle.java.spec.emitter.emitTo
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emitToString


/**
 *
 * @author ForteScarlet
 */
internal class JavaFieldSpecImpl internal constructor(
    override val type: TypeRef<*>,
    override val name: String,
    override val javadoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<JavaModifier>,
    override val initializer: CodeValue
) : JavaFieldSpec {

    override fun emit(codeWriter: JavaCodeWriter) {
        emit(codeWriter, emptySet())
    }

    override fun emit(codeWriter: JavaCodeWriter, implicitModifiers: Set<JavaModifier>) {
        emitTo(codeWriter, implicitModifiers)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JavaFieldSpecImpl) return false

        if (type != other.type) return false
        if (name != other.name) return false
        if (javadoc != other.javadoc) return false
        if (annotations != other.annotations) return false
        if (modifiers != other.modifiers) return false
        if (initializer != other.initializer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + javadoc.hashCode()
        result = 31 * result + annotations.hashCode()
        result = 31 * result + modifiers.hashCode()
        result = 31 * result + initializer.hashCode()
        return result
    }

    override fun toString(): String {
        return emitToString()
    }
}

/**
 * Internal builder implementation for [JavaFieldSpec].
 */
internal class JavaFieldSpecBuilderImpl(
    override val type: TypeRef<*>,
    override val name: String,
) : JavaFieldSpec.Builder {

    init {
        require(name.isNotBlank()) { "Field name cannot be blank" }
    }

    private val javadoc = CodeValue.builder()
    private val annotations = mutableListOf<AnnotationRef>()
    private val modifiers = MutableJavaModifierSet.empty()
    private var initializer: CodeValue? = null

    override fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): JavaFieldSpec.Builder = apply {
        addDoc(CodeValue(format, *argumentParts))
    }

    override fun addDoc(codeValue: CodeValue): JavaFieldSpec.Builder = apply {
        javadoc.addCode(codeValue)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): JavaFieldSpec.Builder = apply {
        annotations.addAll(refs)
    }

    override fun addAnnotation(ref: AnnotationRef): JavaFieldSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addModifiers(vararg modifiers: JavaModifier): JavaFieldSpec.Builder = apply {
        this.modifiers.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<JavaModifier>): JavaFieldSpec.Builder = apply {
        this.modifiers.addAll(modifiers)
    }

    override fun addModifier(modifier: JavaModifier): JavaFieldSpec.Builder = apply {
        this.modifiers.add(modifier)
    }

    override fun initializer(format: String, vararg argumentParts: CodeArgumentPart): JavaFieldSpec.Builder = apply {
        initializer(CodeValue(format, *argumentParts))
    }

    override fun initializer(codeBlock: CodeValue): JavaFieldSpec.Builder = apply {
        check(initializer == null) { "initializer was already set" }
        initializer = codeBlock
    }

    override fun build(): JavaFieldSpec {
        return JavaFieldSpecImpl(
            type = type,
            name = name,
            javadoc = javadoc.build(),
            annotations = annotations.toList(),
            modifiers = modifiers.toSet(),
            initializer = initializer ?: CodeValue()
        )
    }
}
