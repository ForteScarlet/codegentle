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
import love.forte.codegentle.java.spec.internal.JavaSimpleTypeSpecBuilderImpl

/**
 * A generated `class` or `interface`.
 */
@SubclassOptInRequired(CodeGentleJavaSpecImplementation::class)
public interface JavaSimpleTypeSpec : NamedSpec, JavaTypeSpec {
    override val name: String

    public companion object {
        /**
         * Create a builder for a simple type (class or interface).
         *
         * @param kind the type kind (CLASS or INTERFACE)
         * @param name the type name
         * @return a new builder
         */
        public fun builder(kind: JavaTypeSpec.Kind, name: String): Builder {
            return JavaSimpleTypeSpecBuilderImpl(kind, name)
        }
    }

    /**
     * Builder for [JavaSimpleTypeSpec].
     */
    public interface Builder :
        JavaTypeSpecBuilder<JavaSimpleTypeSpec, Builder>,
        SuperclassConfigurer<Builder>,
        SuperinterfaceCollector<Builder>
}

/**
 * Create a [JavaSimpleTypeSpec] with the given kind and name.
 *
 * @param kind the type kind (CLASS or INTERFACE)
 * @param name the type name
 * @param block the configuration block
 * @return a new [JavaSimpleTypeSpec] instance
 */
public inline fun JavaSimpleTypeSpec(
    kind: JavaTypeSpec.Kind,
    name: String,
    block: JavaSimpleTypeSpec.Builder.() -> Unit = {}
): JavaSimpleTypeSpec {
    return JavaSimpleTypeSpec.builder(kind, name).apply(block).build()
}
