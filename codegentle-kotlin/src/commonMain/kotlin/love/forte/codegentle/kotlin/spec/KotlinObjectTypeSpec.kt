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
package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.code.InitializerBlockCollector
import love.forte.codegentle.common.naming.SuperinterfaceCollector
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.internal.KotlinObjectTypeSpecBuilderImpl

/**
 * A generated Kotlin object.
 *
 * ```kotlin
 * object MyObject {
 * }
 * ```
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinObjectTypeSpec : KotlinTypeSpec {
    override val name: String

    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.OBJECT

    /**
     * Whether this is a companion object.
     */
    public val isCompanion: Boolean
        get() = KotlinModifier.COMPANION in modifiers

    public companion object {
        public const val DEFAULT_COMPANION_NAME: String = "Companion"

        /**
         * Create a builder for an object.
         *
         * @param name the object name
         * @return a new builder
         */
        public fun builder(name: String): Builder {
            return KotlinObjectTypeSpecBuilderImpl(name, false)
        }

        /**
         * Create a builder for a companion object.
         *
         * @return a new builder
         */
        public fun companionBuilder(name: String = DEFAULT_COMPANION_NAME): Builder {
            return KotlinObjectTypeSpecBuilderImpl(name, true)
        }
    }

    /**
     * Builder for [KotlinObjectTypeSpec].
     */
    public interface Builder :
        KotlinTypeSpecBuilder<KotlinObjectTypeSpec, Builder>,
        KotlinPropertyCollector<Builder>,
        KotlinFunctionCollector<Builder>,
        InitializerBlockCollector<Builder>,
        SuperinterfaceCollector<Builder> {
        /**
         * The object name.
         */
        public val name: String

        /**
         * Whether this is a companion object.
         */
        public val isCompanion: Boolean

        /**
         * Add subtype.
         */
        public fun addSubtype(subtype: KotlinTypeSpec): Builder

        /**
         * Add subtypes.
         */
        public fun addSubtypes(subtypes: Iterable<KotlinTypeSpec>): Builder

        /**
         * Add subtypes.
         */
        public fun addSubtypes(vararg subtypes: KotlinTypeSpec): Builder = apply {
            addSubtypes(subtypes.asList())
        }

        /**
         * Build [KotlinObjectTypeSpec] instance.
         */
        override fun build(): KotlinObjectTypeSpec
    }
}


/**
 * Create a [KotlinObjectTypeSpec] with the given name.
 *
 * @param name the object name
 * @param block the configuration block
 * @return a new [KotlinObjectTypeSpec] instance
 */
public inline fun KotlinObjectTypeSpec(
    name: String,
    isCompanion: Boolean = false,
    block: KotlinObjectTypeSpec.Builder.() -> Unit = {}
): KotlinObjectTypeSpec {
    return if (isCompanion) {
        KotlinObjectTypeSpec.companionBuilder(name).apply(block).build()
    } else {
        KotlinObjectTypeSpec.builder(name).apply(block).build()
    }
}

/**
 * Create a companion [KotlinObjectTypeSpec].
 *
 * @param block the configuration block
 * @return a new companion [KotlinObjectTypeSpec] instance
 */
public inline fun KotlinObjectTypeSpec(
    block: KotlinObjectTypeSpec.Builder.() -> Unit = {}
): KotlinObjectTypeSpec {
    return KotlinObjectTypeSpec.companionBuilder().apply(block).build()
}

public inline fun KotlinObjectTypeSpec.Builder.addSubtype(
    kind: KotlinTypeSpec.Kind,
    name: String,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): KotlinObjectTypeSpec.Builder = addSubtype(KotlinSimpleTypeSpec.builder(kind, name).apply(block).build())
