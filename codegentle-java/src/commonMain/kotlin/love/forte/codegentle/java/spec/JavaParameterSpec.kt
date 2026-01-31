/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2015-2026 Forte Scarlet
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

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.code.DocCollector
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.common.spec.NamedSpec
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.JavaModifierCollector
import love.forte.codegentle.java.spec.internal.JavaParameterSpecBuilderImpl
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
        /**
         * Create a builder for a Java parameter spec.
         *
         * @param type the parameter type
         * @param name the parameter name
         * @param modifiers the parameter modifiers
         * @return a new builder
         */
        @JvmStatic
        public fun builder(type: TypeRef<*>, name: String, vararg modifiers: JavaModifier): Builder {
            return JavaParameterSpecBuilderImpl(type, name).addModifiers(*modifiers)
        }
    }

    /**
     * Builder for Java parameter specification.
     */
    public interface Builder :
        BuilderDsl,
        DocCollector<Builder>,
        JavaModifierCollector<Builder>,
        AnnotationRefCollector<Builder> {

        /**
         * The name of the parameter.
         */
        public val name: String

        /**
         * The type of the parameter.
         */
        public val type: TypeRef<*>

        /**
         * Build a [JavaParameterSpec] instance.
         *
         * @return A new [JavaParameterSpec] instance
         */
        public fun build(): JavaParameterSpec
    }
}

/**
 * Create a [JavaParameterSpec] with the given type and name.
 *
 * @param name the parameter name
 * @param type the parameter type
 * @param block the configuration block
 * @return a new [JavaParameterSpec] instance
 */
public inline fun JavaParameterSpec(
    name: String,
    type: TypeRef<*>,
    block: JavaParameterSpec.Builder.() -> Unit = {}
): JavaParameterSpec = JavaParameterSpec.builder(type, name).apply(block).build()

/**
 * Create a [JavaParameterSpec] with the given type and name.
 *
 * @param name the parameter name
 * @param type the parameter type
 * @param block the configuration block
 * @return a new [JavaParameterSpec] instance
 */
public inline fun JavaParameterSpec(
    name: String,
    type: TypeName,
    block: JavaParameterSpec.Builder.() -> Unit = {}
): JavaParameterSpec = JavaParameterSpec(name, type.ref(), block)

/**
 * Add javadoc to this parameter builder.
 *
 * @param format the format string
 * @param block the format configuration block
 * @return this builder
 */
public inline fun JavaParameterSpec.Builder.addDoc(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): JavaParameterSpec.Builder = apply {
    addDoc(CodeValue(format, block))
}
