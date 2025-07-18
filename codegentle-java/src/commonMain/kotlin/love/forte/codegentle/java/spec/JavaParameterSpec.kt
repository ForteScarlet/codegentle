/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package love.forte.codegentle.java.spec

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.spec.NamedSpec
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.JavaModifierCollector
import love.forte.codegentle.java.MutableJavaModifierSet
import love.forte.codegentle.java.spec.internal.JavaParameterSpecImpl
import love.forte.codegentle.java.writer.JavaCodeWriter
import kotlin.jvm.JvmStatic

/**
 * A generated parameter declaration.
 */
@SubclassOptInRequired(CodeGentleJavaSpecImplementation::class)
public interface JavaParameterSpec : JavaSpec, NamedSpec {
    override val name: String
    public val type: TypeRef<*>

    public val annotations: List<AnnotationRef>

    public val modifiers: Set<JavaModifier>

    public fun hasModifier(modifier: JavaModifier): Boolean = modifier in modifiers

    public val javadoc: CodeValue

    override fun emit(codeWriter: JavaCodeWriter) {
        emit(codeWriter, false)
    }

    public fun emit(codeWriter: JavaCodeWriter, vararg: Boolean = false)


    public companion object {
        @JvmStatic
        public fun builder(type: TypeRef<*>, name: String, vararg modifiers: JavaModifier): JavaParameterSpecBuilder {
            return JavaParameterSpecBuilder(type, name).addModifiers(*modifiers)
        }
    }
}

public class JavaParameterSpecBuilder internal constructor(
    public val type: TypeRef<*>,
    public val name: String
) : JavaModifierCollector<JavaParameterSpecBuilder>,
    AnnotationRefCollector<JavaParameterSpecBuilder> {
    internal val javadoc = CodeValue.builder()
    internal val annotations = mutableListOf<AnnotationRef>()
    internal val modifiers = MutableJavaModifierSet.empty()

    public fun addJavadoc(format: String, vararg argumentParts: CodeArgumentPart): JavaParameterSpecBuilder = apply {
        addJavadoc(CodeValue(format, *argumentParts))
    }

    public fun addJavadoc(codeValue: CodeValue): JavaParameterSpecBuilder = apply {
        javadoc.add(codeValue)
    }

    override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): JavaParameterSpecBuilder = apply {
        this.annotations.addAll(refs)
    }

    override fun addAnnotationRef(ref: AnnotationRef): JavaParameterSpecBuilder = apply {
        annotations.add(ref)
    }

    override fun addModifiers(vararg modifiers: JavaModifier): JavaParameterSpecBuilder = apply {
        modifiers.forEach { checkModifier(it) }
        this.modifiers.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<JavaModifier>): JavaParameterSpecBuilder = apply {
        modifiers.forEach { checkModifier(it) }
        this.modifiers.addAll(modifiers)
    }

    override fun addModifier(modifier: JavaModifier): JavaParameterSpecBuilder = apply {
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

    public fun build(): JavaParameterSpec {
        return JavaParameterSpecImpl(
            type = type,
            name = name,
            annotations = annotations.toList(),
            modifiers = modifiers.toSet(),
            javadoc = javadoc.build()
        )
    }
}


public inline fun JavaParameterSpec(
    type: TypeRef<*>,
    name: String,
    block: JavaParameterSpecBuilder.() -> Unit = {}
): JavaParameterSpec = JavaParameterSpec.builder(type, name).apply(block).build()

public inline fun JavaParameterSpecBuilder.addJavadoc(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): JavaParameterSpecBuilder = apply {
    addJavadoc(CodeValue(format, block))
}
