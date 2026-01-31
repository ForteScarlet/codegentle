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
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.kotlin.spec.internal.KotlinEnumTypeSpecBuilderImpl

/**
 * A generated Kotlin enum class.
 *
 * ```kotlin
 * enum class EnumType {
 * }
 * ```
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinEnumTypeSpec : KotlinTypeSpec {
    override val name: String

    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.CLASS

    override val superclass: TypeName?
        get() = null

    /**
     * The primary constructor for this enum class.
     */
    public val primaryConstructor: KotlinConstructorSpec?

    /**
     * Secondary constructors for this enum class.
     */
    public val secondaryConstructors: List<KotlinConstructorSpec>

    /**
     * Enum constants.
     */
    public val enumConstants: Map<String, KotlinAnonymousClassTypeSpec?>

    override fun isMemberEmpty(): Boolean {
        return enumConstants.isEmpty() && secondaryConstructors.isEmpty() && super.isMemberEmpty()
    }

    public companion object {
        /**
         * Create a builder for an enum class.
         *
         * @param name the enum class name
         * @return a new builder
         */
        public fun builder(name: String): Builder {
            return KotlinEnumTypeSpecBuilderImpl(name)
        }
    }

    /**
     * Builder for [KotlinEnumTypeSpec].
     */
    public interface Builder :
        KotlinTypeSpecBuilder<KotlinEnumTypeSpec, Builder>,
        KotlinPropertyCollector<Builder>,
        KotlinFunctionCollector<Builder>,
        InitializerBlockCollector<Builder>,
        SuperinterfaceCollector<Builder>,
        KotlinPrimaryConstructorConfigurer<Builder>,
        KotlinSecondaryConstructorCollector<Builder> {
        /**
         * The enum class name.
         */
        public val name: String

        /**
         * Add enum constant.
         */
        public fun addEnumConstant(name: String): Builder

        /**
         * Add enum constant with anonymous type.
         */
        public fun addEnumConstant(name: String, typeSpec: KotlinAnonymousClassTypeSpec): Builder

        /**
         * Build [KotlinEnumTypeSpec] instance.
         */
        override fun build(): KotlinEnumTypeSpec
    }
}

/**
 * Create a [KotlinEnumTypeSpec] with the given name.
 *
 * @param name the enum class name
 * @param block the configuration block
 * @return a new [KotlinEnumTypeSpec] instance
 */
public inline fun KotlinEnumTypeSpec(
    name: String,
    block: KotlinEnumTypeSpec.Builder.() -> Unit = {}
): KotlinEnumTypeSpec {
    return KotlinEnumTypeSpec.builder(name).apply(block).build()
}

public inline fun KotlinEnumTypeSpec.Builder.addEnumConstant(
    name: String,
    block: KotlinAnonymousClassTypeSpec.Builder.() -> Unit = {}
): KotlinEnumTypeSpec.Builder = addEnumConstant(name, KotlinAnonymousClassTypeSpec(block))
