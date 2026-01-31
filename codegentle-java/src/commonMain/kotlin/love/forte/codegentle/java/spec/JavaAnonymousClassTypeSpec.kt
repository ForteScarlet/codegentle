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
package love.forte.codegentle.java.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.SuperclassConfigurer
import love.forte.codegentle.common.naming.SuperinterfaceCollector
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.spec.emitter.emitTo
import love.forte.codegentle.java.spec.internal.JavaAnonymousClassTypeSpecBuilderImpl
import love.forte.codegentle.java.writer.JavaCodeWriter

/**
 * A generated anonymous class.
 * ```java
 * new java.lang.Object() {
 * }
 * ////
 * new HashMap<String, String>(1) {
 * // `anonymousTypeArguments` 👆
 * }
 * ```
 *
 * Also used in enum constants, see [JavaEnumTypeSpec.enumConstants].
 *
 */
@SubclassOptInRequired(CodeGentleJavaSpecImplementation::class)
public interface JavaAnonymousClassTypeSpec : JavaTypeSpec {
    override val kind: JavaTypeSpec.Kind
        get() = JavaTypeSpec.Kind.CLASS

    override val name: String?
        get() = null

    public val anonymousTypeArguments: CodeValue

    public fun emit(codeWriter: JavaCodeWriter, enumName: String?, implicitModifiers: Set<JavaModifier>) {
        emitTo(codeWriter, enumName)
    }

    public companion object {
        /**
         * Create a builder for an anonymous class type.
         *
         * @param anonymousTypeArguments the anonymous type arguments
         * @return a new builder
         */
        public fun builder(anonymousTypeArguments: CodeValue): Builder {
            return JavaAnonymousClassTypeSpecBuilderImpl(anonymousTypeArguments)
        }
    }

    /**
     * Builder for [JavaAnonymousClassTypeSpec].
     */
    public interface Builder :
        BuilderDsl,
        JavaTypeSpecBuilder<JavaAnonymousClassTypeSpec, Builder>,
        SuperclassConfigurer<Builder>,
        SuperinterfaceCollector<Builder> {

        override val kind: JavaTypeSpec.Kind
            get() = JavaTypeSpec.Kind.CLASS

        override val name: String
            get() = ""

        // TODO constructor?
        /**
         * The anonymous type arguments.
         */
        public val anonymousTypeArguments: CodeValue
    }
}

/**
 * Create a [JavaAnonymousClassTypeSpec] with the given anonymous type arguments.
 *
 * @param anonymousTypeArguments the anonymous type arguments
 * @param block the configuration block
 * @return a new [JavaAnonymousClassTypeSpec] instance
 */
public inline fun JavaAnonymousClassTypeSpec(
    anonymousTypeArguments: CodeValue,
    block: JavaAnonymousClassTypeSpec.Builder.() -> Unit = {},
): JavaAnonymousClassTypeSpec {
    return JavaAnonymousClassTypeSpec.builder(anonymousTypeArguments).apply(block).build()
}
