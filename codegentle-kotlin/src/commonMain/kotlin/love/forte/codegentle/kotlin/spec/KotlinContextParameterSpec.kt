/*
 * Copyright (C) 2025 Forte Scarlet
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
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.spec.internal.KotlinContextParameterSpecBuilderImpl
import love.forte.codegentle.kotlin.spec.internal.KotlinContextParameterSpecImpl

/**
 * A Kotlin context parameter.
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinContextParameterSpec : KotlinParameterSpec {
    /**
     * Parameter name.
     * `null` if it's `_`, e.g., `context(_: ParameterType)`.
     */
    override val name: String?
    override val typeRef: TypeRef<*>

    /**
     * Builder for [KotlinContextParameterSpec].
     */
    public interface Builder :
        BuilderDsl,
        KotlinParameterSpec.Builder<KotlinContextParameterSpec> {
        /**
         * Parameter name.
         * `null` if it's `_`, e.g., `context(_: ParameterType)`.
         */
        override val name: String?

        /**
         * Parameter type.
         */
        override val type: TypeRef<*>

        /**
         * Build [KotlinContextParameterSpec].
         */
        override fun build(): KotlinContextParameterSpec
    }

    public companion object {
        /**
         * Create a [Builder].
         *
         * @param name the parameter name, or null for `_`
         * @param type the parameter type
         * @return new [Builder] instance
         */
        public fun builder(name: String?, type: TypeRef<*>): Builder =
            KotlinContextParameterSpecBuilderImpl(name, type)

        /**
         * Create a [KotlinContextParameterSpec].
         *
         * @param name the parameter name, or null for `_`
         * @param type the parameter type
         */
        public fun of(name: String?, type: TypeRef<*>): KotlinContextParameterSpec {
            return KotlinContextParameterSpecImpl(name, type)
        }
    }
}

/**
 * Create a [KotlinContextParameterSpec] with the given name and type.
 *
 * @param name the parameter name, or null for `_`
 * @param type the parameter type
 * @param block the configuration block
 * @return a new [KotlinContextParameterSpec] instance
 */
public inline fun KotlinContextParameterSpec(
    name: String?,
    type: TypeRef<*>,
    block: KotlinContextParameterSpec.Builder.() -> Unit = {}
): KotlinContextParameterSpec =
    KotlinContextParameterSpec.builder(name, type).also(block).build()
