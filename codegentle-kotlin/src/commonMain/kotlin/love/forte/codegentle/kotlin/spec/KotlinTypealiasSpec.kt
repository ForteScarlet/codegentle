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

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.TypeRefBuilderDsl
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.spec.internal.KotlinTypealiasSpecBuilderImpl

/**
 * A Kotlin typealias.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinTypealiasSpec : KotlinTypeSpec {
    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.TYPE_ALIAS

    /**
     * The original type.
     */
    public val type: TypeRef<*>

    // Typealias with no supers, properties, init block, functions, subtypes, etc.
    override val superclass: TypeName?
        get() = null

    override val superinterfaces: List<TypeName>
        get() = emptyList()

    override val properties: List<KotlinPropertySpec>
        get() = emptyList()

    override val initializerBlock: CodeValue
        get() = CodeValue()

    override val functions: List<KotlinFunctionSpec>
        get() = emptyList()

    override val subtypes: List<KotlinTypeSpec>
        get() = emptyList()

    override fun isMemberEmpty(): Boolean = true

    public companion object {
        /**
         * Create a builder for a Kotlin typealias spec.
         *
         * @param name the typealias name
         * @return a new builder
         */
        public fun builder(name: String, type: TypeRef<*>): Builder {
            return KotlinTypealiasSpecBuilderImpl(name, type)
        }
    }

    /**
     * Builder for Kotlin typealias specification.
     */
    public interface Builder : KotlinTypeSpecBuilder<KotlinTypealiasSpec, Builder> {

        /**
         * The name of the typealias.
         */
        public val name: String

        /**
         * The target type for this typealias.
         */
        public val type: TypeRef<*>

        /**
         * Build a [KotlinTypealiasSpec] instance.
         *
         * @return A new [KotlinTypealiasSpec] instance
         */
        override fun build(): KotlinTypealiasSpec
    }
}

/**
 * Create a [KotlinTypealiasSpec] with the given name.
 *
 * @param name the typealias name
 * @param block the configuration block
 * @return a new [KotlinTypealiasSpec] instance
 */
public inline fun KotlinTypealiasSpec(
    name: String,
    type: TypeRef<*>,
    block: KotlinTypealiasSpec.Builder.() -> Unit = {}
): KotlinTypealiasSpec {
    return KotlinTypealiasSpec.builder(name, type).apply(block).build()
}

/**
 * Create a [KotlinTypealiasSpec] with the given name.
 *
 * @param name the typealias name
 * @param block the configuration block
 * @return a new [KotlinTypealiasSpec] instance
 */
public inline fun <T : TypeName> KotlinTypealiasSpec(
    name: String,
    typeName: T,
    ref: TypeRefBuilderDsl<T> = {},
    block: KotlinTypealiasSpec.Builder.() -> Unit = {}
): KotlinTypealiasSpec {
    return KotlinTypealiasSpec.builder(name, typeName.ref(ref)).apply(block).build()
}
