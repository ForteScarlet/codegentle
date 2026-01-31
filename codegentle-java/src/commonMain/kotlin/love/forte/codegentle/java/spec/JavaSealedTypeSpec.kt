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
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.spec.NamedSpec
import love.forte.codegentle.java.spec.internal.JavaSealedTypeSpecBuilderImpl

/**
 * A generated `sealed class/interface`.
 */
@SubclassOptInRequired(CodeGentleJavaSpecImplementation::class)
public interface JavaSealedTypeSpec : NamedSpec, JavaTypeSpec {
    override val name: String

    /**
     * The list of permitted subtypes for this sealed type.
     */
    public val permits: List<TypeName>

    public companion object {
        /**
         * Create a builder for a sealed type (sealed class or sealed interface).
         *
         * @param kind the type kind (SEALED_CLASS or SEALED_INTERFACE)
         * @param name the type name
         * @return a new builder
         */
        public fun builder(kind: JavaTypeSpec.Kind, name: String): Builder {
            return JavaSealedTypeSpecBuilderImpl(kind, name)
        }
    }

    /**
     * Builder for [JavaSealedTypeSpec].
     */
    public interface Builder :
        JavaTypeSpecBuilder<JavaSealedTypeSpec, Builder>,
        SuperclassConfigurer<Builder>,
        SuperinterfaceCollector<Builder> {


        /**
         * Add permitted subtypes to this sealed type.
         */
        public fun addPermits(permits: Iterable<TypeName>): Builder

        /**
         * Add permitted subtypes to this sealed type.
         */
        public fun addPermits(vararg permits: TypeName): Builder

        /**
         * Add permitted subtype to this sealed type.
         */
        public fun addPermit(permit: TypeName): Builder

        /**
         * Build [JavaSealedTypeSpec] instance.
         */
        override fun build(): JavaSealedTypeSpec
    }
}


/**
 * Create a [JavaSealedTypeSpec] with the given kind and name.
 *
 * @param kind the type kind (SEALED_CLASS or SEALED_INTERFACE)
 * @param name the type name
 * @param block the configuration block
 * @return a new [JavaSealedTypeSpec] instance
 */
public inline fun JavaSealedTypeSpec(
    kind: JavaTypeSpec.Kind,
    name: String,
    block: JavaSealedTypeSpec.Builder.() -> Unit = {}
): JavaSealedTypeSpec {
    return JavaSealedTypeSpec.builder(kind, name).apply(block).build()
}
