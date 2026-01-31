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
import love.forte.codegentle.common.naming.SuperConfigurer
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.internal.KotlinSimpleTypeSpecBuilderImpl

/**
 * Represents a simple Kotlin type specification, such as class, interface, etc.
 *
 * @property kind The kind of the type
 * @property name The name of the type
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinSimpleTypeSpec : KotlinTypeSpec {
    // simple, data class, sealed class, interface, sealed interface, fun interface

    override val kind: KotlinTypeSpec.Kind

    override val modifiers: Set<KotlinModifier>

    public val primaryConstructor: KotlinConstructorSpec?

    public val secondaryConstructors: List<KotlinConstructorSpec>

    override fun isMemberEmpty(): Boolean {
        return secondaryConstructors.isEmpty() && super.isMemberEmpty()
    }

    public companion object {
        public val validKinds: Set<KotlinTypeSpec.Kind> = MutableKotlinTypeSpecKindSet.of(
            KotlinTypeSpec.Kind.INTERFACE,
            KotlinTypeSpec.Kind.CLASS,
        )

        public fun isValidKind(kind: KotlinTypeSpec.Kind): Boolean =
            validKinds.contains(kind)

        /**
         * Create a builder for a simple Kotlin type spec.
         *
         * @param kind the type kind
         * @param name the type name
         * @return a new builder
         */
        public fun builder(
            kind: KotlinTypeSpec.Kind,
            name: String
        ): Builder {
            return KotlinSimpleTypeSpecBuilderImpl(kind, name)
        }
    }

    /**
     * Builder for simple Kotlin type specification.
     */
    public interface Builder :
        KotlinTypeSpecBuilder<KotlinSimpleTypeSpec, Builder>,
        KotlinPropertyCollector<Builder>,
        KotlinFunctionCollector<Builder>,
        InitializerBlockCollector<Builder>,
        SuperConfigurer<Builder>,
        KotlinPrimaryConstructorConfigurer<Builder>,
        KotlinSecondaryConstructorCollector<Builder> {
        /**
         * The kind of the type.
         */
        public val kind: KotlinTypeSpec.Kind

        /**
         * The name of the type.
         */
        public val name: String

        /**
         * Add multiple subtypes.
         */
        public fun addSubtypes(types: Iterable<KotlinTypeSpec>): Builder

        /**
         * Add multiple subtypes.
         */
        public fun addSubtypes(vararg types: KotlinTypeSpec): Builder

        /**
         * Add subtype.
         */
        public fun addSubtype(type: KotlinTypeSpec): Builder

        /**
         * Build a [KotlinSimpleTypeSpec] instance.
         *
         * @return A new [KotlinSimpleTypeSpec] instance
         */
        override fun build(): KotlinSimpleTypeSpec
    }
}

/**
 * Create a [KotlinSimpleTypeSpec] with the given kind and name.
 *
 * @param kind the type kind (CLASS, INTERFACE, etc.)
 * @param name the type name
 * @param block the configuration block
 * @return a new [KotlinSimpleTypeSpec] instance
 */
public inline fun KotlinSimpleTypeSpec(
    kind: KotlinTypeSpec.Kind,
    name: String,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): KotlinSimpleTypeSpec {
    return KotlinSimpleTypeSpec.builder(kind, name).apply(block).build()
}

/**
 * Add a companion object to this type with configuration.
 */
public inline fun KotlinSimpleTypeSpec.Builder.addCompanionObject(
    name: String = KotlinObjectTypeSpec.DEFAULT_COMPANION_NAME,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): KotlinSimpleTypeSpec.Builder =
    addSubtype(KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.OBJECT, name, block))
