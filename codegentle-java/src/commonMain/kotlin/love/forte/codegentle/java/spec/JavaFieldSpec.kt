/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2015-2025 Forte Scarlet
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
package love.forte.codegentle.java.spec

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.code.DocCollector
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.*
import love.forte.codegentle.common.spec.NamedSpec
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.JavaModifierCollector
import love.forte.codegentle.java.spec.internal.JavaFieldSpecBuilderImpl
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

    public fun emit(codeWriter: JavaCodeWriter, implicitModifiers: Set<JavaModifier>)

    public companion object {
        /**
         * Create a builder for a Java field spec.
         *
         * @param type the field type
         * @param name the field name
         * @return a new builder
         */
        public fun builder(type: TypeRef<*>, name: String): Builder {
            return JavaFieldSpecBuilderImpl(type, name)
        }
    }

    /**
     * Builder for Java field specification.
     */
    public interface Builder :
        DocCollector<Builder>,
        JavaModifierCollector<Builder>,
        AnnotationRefCollector<Builder> {

        /**
         * The type of the field.
         */
        public val type: TypeRef<*>

        /**
         * The name of the field.
         */
        public val name: String

        /**
         * Set the initializer for this field.
         *
         * @param format the format string
         * @param argumentParts the format arguments
         * @return this builder
         */
        public fun initializer(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Set the initializer for this field.
         *
         * @param codeBlock the initializer code block
         * @return this builder
         */
        public fun initializer(codeBlock: CodeValue): Builder

        /**
         * Build a [JavaFieldSpec] instance.
         *
         * @return A new [JavaFieldSpec] instance
         */
        public fun build(): JavaFieldSpec
    }
}

/**
 * Create a [JavaFieldSpec] with the given type and name.
 *
 * @param type the field type
 * @param name the field name
 * @param block the configuration block
 * @return a new [JavaFieldSpec] instance
 */
public inline fun JavaFieldSpec(
    type: TypeRef<*>,
    name: String,
    block: JavaFieldSpec.Builder.() -> Unit = {}
): JavaFieldSpec =
    JavaFieldSpec.builder(type, name).apply(block).build()


/**
 * Create a [JavaFieldSpec] with the given type and name.
 *
 * @param type the field type
 * @param name the field name
 * @param ref the type reference configuration
 * @param block the configuration block
 * @return a new [JavaFieldSpec] instance
 */
public inline fun JavaFieldSpec(
    type: TypeName,
    name: String,
    ref: TypeRefBuilderDsl<TypeName> = {},
    block: JavaFieldSpec.Builder.() -> Unit = {}
): JavaFieldSpec =
    JavaFieldSpec.builder(type.ref(ref), name).apply(block).build()

/**
 * Set the initializer for this field builder.
 *
 * @param format the format string
 * @param block the format configuration block
 * @return this builder
 */
public inline fun JavaFieldSpec.Builder.initializer(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): JavaFieldSpec.Builder = apply {
    initializer(CodeValue(format, block))
}
