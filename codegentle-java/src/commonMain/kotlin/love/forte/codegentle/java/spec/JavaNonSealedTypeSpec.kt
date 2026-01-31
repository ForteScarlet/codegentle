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

import love.forte.codegentle.common.naming.SuperclassConfigurer
import love.forte.codegentle.common.naming.SuperinterfaceCollector
import love.forte.codegentle.common.spec.NamedSpec
import love.forte.codegentle.java.spec.internal.JavaNonSealedTypeSpecBuilderImpl

/**
 * A generated `non-sealed class/interface`.
 *
 * ```java
 * public non-sealed class NonSealedClass extends SealedType {
 * }
 * ```
 *
 * ```java
 * public non-sealed interface NonSealedInterface extends SealedType {
 * }
 * ```
 */
@SubclassOptInRequired(CodeGentleJavaSpecImplementation::class)
public interface JavaNonSealedTypeSpec : NamedSpec, JavaTypeSpec {
    override val name: String

    public companion object {
        /**
         * Create a builder for a non-sealed type (class or interface).
         *
         * @param kind the type kind (NON_SEALED_CLASS or NON_SEALED_INTERFACE)
         * @param name the type name
         * @return a new builder
         */
        public fun builder(kind: JavaTypeSpec.Kind, name: String): Builder {
            return JavaNonSealedTypeSpecBuilderImpl(kind, name)
        }

        /**
         * Creates a builder for a non-sealed class.
         *
         * @param name the name of the non-sealed class
         * @return a builder to further configure and generate the non-sealed class
         */
        public fun nonSealedClass(name: String): Builder = builder(JavaTypeSpec.Kind.NON_SEALED_CLASS, name)

        /**
         * Creates a builder for a non-sealed interface type definition.
         *
         * @param name the name of the non-sealed interface
         * @return a builder for constructing the non-sealed interface
         */
        public fun nonSealedInterface(name: String): Builder = builder(JavaTypeSpec.Kind.NON_SEALED_INTERFACE, name)
    }

    /**
     * Builder for [JavaNonSealedTypeSpec].
     */
    public interface Builder :
        JavaTypeSpecBuilder<JavaNonSealedTypeSpec, Builder>,
        SuperclassConfigurer<Builder>,
        SuperinterfaceCollector<Builder> {

        /**
         * The type kind.
         */
        override val kind: JavaTypeSpec.Kind

        /**
         * The type name.
         */
        override val name: String
    }
}

/**
 * Create a [JavaNonSealedTypeSpec] with the given kind and name.
 *
 * @param kind the type kind (NON_SEALED_CLASS or NON_SEALED_INTERFACE)
 * @param name the type name
 * @param block the configuration block
 * @return a new [JavaNonSealedTypeSpec] instance
 */
public inline fun JavaNonSealedTypeSpec(
    kind: JavaTypeSpec.Kind,
    name: String,
    block: JavaNonSealedTypeSpec.Builder.() -> Unit = {},
): JavaNonSealedTypeSpec {
    return JavaNonSealedTypeSpec.builder(kind, name).apply(block).build()
}
