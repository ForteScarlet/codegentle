/*
 * Copyright (C) 2014-2024 Square, Inc.
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
package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.ref

/**
 * A Kotlin parameter.
 *
 * @see KotlinContextParameterSpec
 * @see KotlinValueParameterSpec
 *
 * @author ForteScarlet
 */
public sealed interface KotlinParameterSpec : KotlinSpec {
    /**
     * Parameter name.
     */
    public val name: String?
    public val typeRef: TypeRef<*>

    public sealed interface Builder<S : KotlinParameterSpec> : BuilderDsl {
        /**
         * Parameter name.
         * `null` if it's `_`, e.g., `context(_: ParameterType)`.
         */
        public val name: String?

        /**
         * Parameter type.
         */
        public val type: TypeRef<*>

        public fun build(): S
    }
}

public interface KotlinValueParameterCollector<B : KotlinValueParameterCollector<B>> {
    /**
     * Add a value parameter to the function.
     */
    public fun addParameter(parameter: KotlinValueParameterSpec): B

    /**
     * Add value parameters to the function.
     */
    public fun addParameters(parameters: Iterable<KotlinValueParameterSpec>): B

    /**
     * Add value parameters to the function.
     */
    public fun addParameters(vararg parameters: KotlinValueParameterSpec): B

}

public inline fun <C : KotlinValueParameterCollector<C>> C.addParameter(
    name: String,
    type: TypeRef<*>,
    block: KotlinValueParameterSpec.Builder.() -> Unit = {}
): C = addParameter(KotlinValueParameterSpec.builder(name, type).apply(block).build())

public inline fun <C : KotlinValueParameterCollector<C>> C.addParameter(
    name: String,
    type: TypeName,
    block: KotlinValueParameterSpec.Builder.() -> Unit = {}
): C = addParameter(name, type.ref(), block)
