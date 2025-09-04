/*
 * Copyright (C) 2014-2024 Square, Inc.
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
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.code.DocCollector
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierCollector
import love.forte.codegentle.kotlin.KotlinModifierContainer
import love.forte.codegentle.kotlin.spec.internal.KotlinPropertySpecBuilderImpl

/**
 * A Kotlin property.
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinPropertySpec : KotlinSpec, KotlinModifierContainer {
    /**
     * Property's name.
     */
    public val name: String

    /**
     * Property's type ref.
     */
    public val typeRef: TypeRef<*>

    public val annotations: List<AnnotationRef>
    override val modifiers: Set<KotlinModifier>
    public val kDoc: CodeValue
    public val initializer: CodeValue?
    public val delegate: CodeValue?

    public val getter: KotlinGetterSpec?
    public val setter: KotlinSetterSpec?

    /**
     * Whether this property is mutable (var) or immutable (val).
     *
     * @return true if the property is mutable (var), false if immutable (val)
     */
    public val mutable: Boolean

    /**
     * Builder for [KotlinPropertySpec].
     */
    public interface Builder : BuilderDsl,
        KotlinModifierCollector<Builder>,
        AnnotationRefCollector<Builder>,
        DocCollector<Builder> {

        /**
         * Property's name.
         */
        public val name: String

        /**
         * Property's type.
         */
        public val type: TypeRef<*>

        /**
         * Set the initializer for the property.
         *
         * @param codeValue the initializer code
         * @return this builder
         * @throws IllegalArgumentException if the property already has a delegate
         */
        public fun initializer(codeValue: CodeValue): Builder

        /**
         * Set the initializer for the property.
         *
         * @param format the initializer format string
         * @param arguments the initializer arguments
         * @return this builder
         * @throws IllegalArgumentException if the property already has a delegate
         */
        public fun initializer(format: String, vararg arguments: CodeArgumentPart): Builder

        /**
         * Set the delegate for the property.
         *
         * @param codeValue the delegate code
         * @return this builder
         * @throws IllegalArgumentException if the property already has an initializer
         */
        public fun delegate(codeValue: CodeValue): Builder

        /**
         * Set the delegate for the property.
         *
         * @param format the delegate format string
         * @param arguments the delegate arguments
         * @return this builder
         * @throws IllegalArgumentException if the property already has an initializer
         */
        public fun delegate(format: String, vararg arguments: CodeArgumentPart): Builder

        /**
         * Set a custom getter for the property.
         *
         * @param getter the getter spec
         * @return this builder
         */
        public fun getter(getter: KotlinGetterSpec): Builder

        /**
         * Set a custom setter for the property.
         *
         * @param setter the setter spec
         * @return this builder
         */
        public fun setter(setter: KotlinSetterSpec): Builder

        /**
         * Set whether this property is mutable (var) or immutable (val).
         *
         * @param mutable true for mutable property (var), false for immutable property (val)
         * @return this builder
         */
        public fun mutable(mutable: Boolean): Builder

        /**
         * Build a [KotlinPropertySpec] instance.
         *
         * @return a new [KotlinPropertySpec] instance
         */
        public fun build(): KotlinPropertySpec
    }

    public companion object {
        /**
         * Create a builder for a property.
         *
         * @param name the property name
         * @param type the property type
         * @return a new builder
         */
        public fun builder(name: String, type: TypeRef<*>): Builder {
            return KotlinPropertySpecBuilderImpl(name, type)
        }

        public operator fun invoke(
            name: String,
            type: TypeRef<*>,
            block: Builder.() -> Unit = {}
        ): KotlinPropertySpec {
            return builder(name, type).apply(block).build()
        }
    }
}

/**
 * Whether this property is immutable (val) or mutable (var).
 *
 * @return ![KotlinPropertySpec.mutable],
 * true if the property is immutable (val).
 */
public val KotlinPropertySpec.immutable: Boolean
    get() = !mutable

/**
 * Create a [KotlinPropertySpec] with the given name and type.
 *
 * @param name the property name
 * @param type the property type
 * @param block the configuration block
 * @return a new [KotlinPropertySpec] instance
 */
public inline fun KotlinPropertySpec(
    name: String,
    type: TypeRef<*>,
    block: KotlinPropertySpec.Builder.() -> Unit = {}
): KotlinPropertySpec =
    KotlinPropertySpec.builder(name, type).also(block).build()

public inline fun KotlinPropertySpec.Builder.initializer(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinPropertySpec.Builder = initializer(CodeValue(format, block))

public inline fun KotlinPropertySpec.Builder.delegate(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinPropertySpec.Builder = delegate(CodeValue(format, block))

/**
 * Set a custom getter for the property using a configuration block.
 *
 * @param block the configuration block for the getter
 * @return this builder
 */
public inline fun KotlinPropertySpec.Builder.getter(
    block: KotlinGetterSpec.Builder.() -> Unit
): KotlinPropertySpec.Builder = getter(KotlinPropertyAccessorSpec.getterBuilder().apply(block).build())

/**
 * Set a custom setter for the property using a configuration block.
 *
 * @param parameterName the name of the setter parameter
 * @param block the configuration block for the setter
 * @return this builder
 */
public inline fun KotlinPropertySpec.Builder.setter(
    parameterName: String = "value",
    block: KotlinSetterSpec.Builder.() -> Unit
): KotlinPropertySpec.Builder = setter(KotlinPropertyAccessorSpec.setterBuilder(parameterName).apply(block).build())

/**
 * Set this property as mutable (var).
 *
 * @return this builder
 */
public fun KotlinPropertySpec.Builder.mutable(): KotlinPropertySpec.Builder = mutable(true)

/**
 * Set this property as immutable (val).
 *
 * @return this builder
 */
public fun KotlinPropertySpec.Builder.immutable(): KotlinPropertySpec.Builder = mutable(false)
