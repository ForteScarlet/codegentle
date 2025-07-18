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
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollectable
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.spec.NamedSpec
import love.forte.codegentle.java.InternalJavaCodeGentleApi
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.JavaModifierBuilderContainer
import love.forte.codegentle.java.MutableJavaModifierSet
import love.forte.codegentle.java.ref.JavaTypeRefBuilderDsl
import love.forte.codegentle.java.ref.javaRef
import love.forte.codegentle.java.spec.internal.JavaFieldSpecImpl
import love.forte.codegentle.java.writer.JavaCodeWriter


/**
 * A generated field declaration.
 */
@SubclassOptInRequired(CodeGentleJavaSpecImplementation::class)
public interface JavaFieldSpec : JavaSpec, NamedSpec {
    override val name: String

    public val type: TypeRef<*>

    public val javadoc: CodeValue

    public val annotations: List<AnnotationRef>

    public val modifiers: Set<JavaModifier>

    public val initializer: CodeValue

    public fun hasModifier(modifier: JavaModifier): Boolean = modifier in modifiers

    @InternalJavaCodeGentleApi
    override fun emit(codeWriter: JavaCodeWriter) {
        emit(codeWriter, emptySet())
    }

    @InternalJavaCodeGentleApi
    public fun emit(codeWriter: JavaCodeWriter, implicitModifiers: Set<JavaModifier> = emptySet())
}

/**
 * @see JavaFieldSpecBuilder
 */
public inline fun JavaFieldSpec(
    type: TypeRef<*>,
    name: String,
    block: JavaFieldSpecBuilder.() -> Unit = {}
): JavaFieldSpec =
    JavaFieldSpecBuilder(type, name).also(block).build()


/**
 * @see JavaFieldSpecBuilder
 */
public inline fun JavaFieldSpec(
    type: TypeName,
    name: String,
    refBlock: JavaTypeRefBuilderDsl<TypeName> = {},
    block: JavaFieldSpecBuilder.() -> Unit = {}
): JavaFieldSpec =
    JavaFieldSpec(type.javaRef(refBlock), name, block)

public class JavaFieldSpecBuilder @PublishedApi internal constructor(
    public val type: TypeRef<*>,
    public val name: String,
) : JavaModifierBuilderContainer<JavaFieldSpecBuilder>,
    AnnotationRefCollectable<JavaFieldSpecBuilder> {
    internal val javadoc = CodeValue.builder()
    internal val annotations = mutableListOf<AnnotationRef>()
    internal val modifiers = MutableJavaModifierSet.empty()
    internal var initializer: CodeValue? = null

    public fun addJavadoc(format: String, vararg argumentParts: CodeArgumentPart): JavaFieldSpecBuilder = apply {
        addJavadoc(CodeValue(format, *argumentParts))
    }

    public fun addJavadoc(codeValue: CodeValue): JavaFieldSpecBuilder = apply {
        javadoc.add(codeValue)
    }

    override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): JavaFieldSpecBuilder = apply {
        annotations.addAll(refs)
    }

    override fun addAnnotationRef(ref: AnnotationRef): JavaFieldSpecBuilder = apply {
        annotations.add(ref)
    }

    override fun addModifiers(vararg modifiers: JavaModifier): JavaFieldSpecBuilder = apply {
        this.modifiers.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<JavaModifier>): JavaFieldSpecBuilder = apply {
        this.modifiers.addAll(modifiers)
    }

    override fun addModifier(modifier: JavaModifier): JavaFieldSpecBuilder = apply {
        modifiers.add(modifier)
    }

    public fun initializer(format: String, vararg argumentParts: CodeArgumentPart): JavaFieldSpecBuilder = apply {
        initializer(CodeValue(format, *argumentParts))
    }

    public fun initializer(codeBlock: CodeValue): JavaFieldSpecBuilder = apply {
        check(initializer == null) { "initializer was already set" }
        initializer = codeBlock
    }

    public fun build(): JavaFieldSpec {
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

public inline fun JavaFieldSpecBuilder.addJavadoc(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): JavaFieldSpecBuilder = apply {
    addJavadoc(CodeValue(format, block))
}

public inline fun JavaFieldSpecBuilder.initializer(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): JavaFieldSpecBuilder = apply {
    initializer(CodeValue(format, block))
}
