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

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.code.InitializerBlockCollector
import love.forte.codegentle.common.naming.SuperConfigurer
import love.forte.codegentle.kotlin.spec.internal.KotlinAnonymousClassTypeSpecBuilderImpl

/**
 * A Kotlin anonymous class type specification.
 * Can be used as an implementation body for enum constants.
 *
 * Anonymous classes in Kotlin cannot define their own constructors, but they can
 * call the superclass constructor with arguments when extending a class.
 *
 * ```kotlin
 * object : SuperType {
 *     // implementations
 * }
 * ```
 *
 * Or with superclass constructor arguments:
 * ```kotlin
 * object : SuperType(arg1, arg2) {
 *     // implementations
 * }
 * ```
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinAnonymousClassTypeSpec : KotlinTypeSpec {
    /**
     * Anonymous class has no name.
     */
    override val name: String
        get() = ""

    /**
     * Anonymous class has no specific kind.
     */
    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.CLASS

    /**
     * Defines the delegation information for the constructor within an anonymous class type specification.
     *
     * [KotlinConstructorDelegation.kind] is always [SUPER][KotlinConstructorDelegation.Kind.SUPER].
     */
    public val constructorDelegation: KotlinConstructorDelegation

    public companion object {
        /**
         * Create a builder for an anonymous class.
         *
         * @return a new builder
         */
        public fun builder(): Builder {
            return KotlinAnonymousClassTypeSpecBuilderImpl()
        }
    }

    /**
     * Builder for [KotlinAnonymousClassTypeSpec].
     */
    public interface Builder :
        KotlinTypeSpecBuilder<KotlinAnonymousClassTypeSpec, Builder>,
        InitializerBlockCollector<Builder>,
        SuperConfigurer<Builder>,
        KotlinPropertyCollector<Builder>,
        KotlinFunctionCollector<Builder> {
        /**
         * Add arguments to pass to the superclass constructor.
         */
        public fun addSuperConstructorArguments(vararg arguments: CodeValue): Builder

        /**
         * Add arguments to pass to the superclass constructor.
         */
        public fun addSuperConstructorArguments(arguments: Iterable<CodeValue>): Builder

        /**
         * Add a single argument to pass to the superclass constructor.
         */
        public fun addSuperConstructorArgument(argument: CodeValue): Builder

        /**
         * Add a single argument to pass to the superclass constructor.
         */
        public fun addSuperConstructorArgument(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Build [KotlinAnonymousClassTypeSpec] instance.
         */
        override fun build(): KotlinAnonymousClassTypeSpec
    }
}

/**
 * Create a [KotlinAnonymousClassTypeSpec] with the given configuration.
 *
 * @param block the configuration block
 * @return a new [KotlinAnonymousClassTypeSpec] instance
 */
public inline fun KotlinAnonymousClassTypeSpec(
    block: KotlinAnonymousClassTypeSpec.Builder.() -> Unit = {}
): KotlinAnonymousClassTypeSpec {
    return KotlinAnonymousClassTypeSpec.builder().apply(block).build()
}

public inline fun KotlinAnonymousClassTypeSpec.Builder.addSuperConstructorArgument(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinAnonymousClassTypeSpec.Builder = addSuperConstructorArgument(CodeValue(format, block))
