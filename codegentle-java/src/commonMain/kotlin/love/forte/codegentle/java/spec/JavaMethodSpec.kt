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
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueCollector
import love.forte.codegentle.common.code.DocCollector
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.TypeVariableCollector
import love.forte.codegentle.common.spec.NamedSpec
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.JavaModifierCollector
import love.forte.codegentle.java.JavaModifierContainer
import love.forte.codegentle.java.spec.internal.JavaMethodSpecBuilderImpl
import love.forte.codegentle.java.writer.JavaCodeWriter
import kotlin.jvm.JvmStatic


/**
 * A generated constructor or method declaration.
 */
@SubclassOptInRequired(CodeGentleJavaSpecImplementation::class)
public interface JavaMethodSpec : JavaSpec, NamedSpec, JavaModifierContainer {
    override val name: String
    public val javadoc: CodeValue
    public val annotations: List<AnnotationRef>
    override val modifiers: Set<JavaModifier>
    public val typeVariables: List<TypeRef<TypeVariableName>>
    public val returnType: TypeRef<*>?
    public val parameters: List<JavaParameterSpec>
    public val isVarargs: Boolean
    public val exceptions: List<TypeRef<*>>
    public val code: CodeValue
    public val defaultValue: CodeValue

    public val isConstructor: Boolean
        get() = name == CONSTRUCTOR

    override fun emit(codeWriter: JavaCodeWriter) {
        emit(codeWriter, null, emptySet())
    }

    public fun emit(codeWriter: JavaCodeWriter, name: String? = null, implicitModifiers: Set<JavaModifier> = emptySet())

    public companion object {
        internal const val CONSTRUCTOR: String = "<init>"
        public const val MAIN_NAME: String = "main"

        /**
         * Create a builder for a Java method spec.
         *
         * @param name the method name
         * @return a new builder
         */
        @JvmStatic
        public fun methodBuilder(name: String): Builder = JavaMethodSpecBuilderImpl(name)

        /**
         * Create a builder for a Java constructor spec.
         *
         * @return a new builder
         */
        @JvmStatic
        public fun constructorBuilder(): Builder = JavaMethodSpecBuilderImpl(CONSTRUCTOR)

        /**
         * Create a builder for a Java method spec named `'main'`.
         *
         * @return a new builder
         */
        @JvmStatic
        public fun mainBuilder(): Builder = JavaMethodSpecBuilderImpl(MAIN_NAME)
    }

    /**
     * Builder for Java method specification.
     */
    public interface Builder :
        BuilderDsl,
        DocCollector<Builder>,
        CodeValueCollector<Builder>,
        JavaModifierCollector<Builder>,
        JavaParameterCollector<Builder>,
        TypeVariableCollector<Builder>,
        AnnotationRefCollector<Builder> {

        /**
         * The name of the method.
         */
        public var name: String

        /**
         * Whether this method has varargs.
         */
        public var isVarargs: Boolean

        /**
         * Set the return type of this method.
         */
        public fun returns(typeRef: TypeRef<*>): Builder

        /**
         * Set this method as varargs.
         */
        public fun varargs(): Builder

        /**
         * Set whether this method has varargs.
         */
        public fun varargs(varargs: Boolean): Builder

        /**
         * Add an exception to this method.
         */
        public fun addException(exception: TypeRef<*>): Builder

        /**
         * Add exceptions to this method.
         */
        public fun addExceptions(vararg exceptions: TypeRef<*>): Builder

        /**
         * Add exceptions to this method.
         */
        public fun addExceptions(exceptions: Iterable<TypeRef<*>>): Builder

        /**
         * Add a comment to this method.
         */
        public fun addComment(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Set the default value for this method (for annotation methods).
         */
        public fun defaultValue(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Set the default value for this method (for annotation methods).
         */
        public fun defaultValue(codeBlock: CodeValue): Builder

        /**
         * Build a [JavaMethodSpec] instance.
         */
        public fun build(): JavaMethodSpec
    }

}

/**
 * @see JavaMethodSpec.methodBuilder
 */
public inline fun JavaMethodSpec(name: String, block: JavaMethodSpec.Builder.() -> Unit = {}): JavaMethodSpec =
    JavaMethodSpec.methodBuilder(name).apply(block).build()

/**
 * @see JavaMethodSpec.constructorBuilder
 */
public inline fun JavaMethodSpec(block: JavaMethodSpec.Builder.() -> Unit = {}): JavaMethodSpec =
    JavaMethodSpec.constructorBuilder().apply(block).build()

