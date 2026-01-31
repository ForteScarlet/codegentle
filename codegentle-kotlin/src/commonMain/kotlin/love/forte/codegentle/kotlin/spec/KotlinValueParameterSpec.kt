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
import love.forte.codegentle.common.code.DocCollector
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierCollector
import love.forte.codegentle.kotlin.KotlinModifierContainer
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec.Companion.propertyizationBuilder
import love.forte.codegentle.kotlin.spec.internal.KotlinValueParameterSpecBuilderImpl
import love.forte.codegentle.kotlin.spec.internal.PropertyficationBuilderImpl

/**
 * A Kotlin value parameter.
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinValueParameterSpec : KotlinParameterSpec, KotlinModifierContainer {
    /**
     * Parameter name.
     */
    override val name: String
    override val typeRef: TypeRef<*>

    public val annotations: List<AnnotationRef>
    override val modifiers: Set<KotlinModifier>
    public val kDoc: CodeValue
    public val defaultValue: CodeValue?

    public val propertyfication: Propertyfication?

    /**
     * Represents the propertyfication configuration for a value parameter.
     * When a parameter is propertyfied, it becomes a property in the constructor.
     *
     * @see PropertyficationBuilder
     */
    public interface Propertyfication {
        /**
         * Whether the property should be mutable (var) or immutable (val).
         */
        public val mutable: Boolean
    }

    /**
     * Builder for [Propertyfication].
     *
     * @see propertyizationBuilder
     */
    public interface PropertyficationBuilder {
        /**
         * Whether the property should be mutable (var) or immutable (val).
         */
        public var mutable: Boolean

        /**
         * Set whether the property should be mutable (var) or immutable (val).
         *
         * @param mutable true for mutable property (var), false for immutable property (val)
         * @return this builder
         */
        public fun mutable(mutable: Boolean = true): PropertyficationBuilder

        /**
         * Build a [Propertyfication] instance.
         *
         * @return a new [Propertyfication] instance
         */
        public fun build(): Propertyfication
    }

    /**
     * Builder for [KotlinValueParameterSpec].
     */
    public interface Builder :
        KotlinParameterSpec.Builder<KotlinValueParameterSpec>,
        KotlinModifierCollector<Builder>,
        AnnotationRefCollector<Builder>,
        DocCollector<Builder> {
        /**
         * Parameter name.
         */
        override val name: String

        /**
         * Parameter type.
         */
        override val type: TypeRef<*>

        /**
         * Set the default value for the parameter.
         *
         * @param codeValue the default value code
         * @return this builder
         */
        public fun defaultValue(codeValue: CodeValue): Builder

        /**
         * Set the default value for the parameter.
         *
         * @param format the default value format string
         * @param argumentParts the default value arguments
         * @return this builder
         */
        public fun defaultValue(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Set the propertyization for this parameter.
         * When propertyized, the parameter becomes a property in the constructor.
         *
         * @param propertyfication the propertyization configuration
         * @return this builder
         */
        public fun propertyfy(propertyfication: Propertyfication): Builder

        /**
         * Build a [KotlinValueParameterSpec] instance.
         *
         * @return a new [KotlinValueParameterSpec] instance
         */
        override fun build(): KotlinValueParameterSpec
    }

    public companion object {
        /**
         * Create a builder for a value parameter.
         *
         * @param name the parameter name
         * @param type the parameter type
         * @return a new builder
         */
        public fun builder(name: String, type: TypeRef<*>): Builder =
            KotlinValueParameterSpecBuilderImpl(name, type)

        /**
         * Create a [PropertyficationBuilder] instance.
         *
         * @return a new [PropertyficationBuilder] instance
         */
        public fun propertyizationBuilder(): PropertyficationBuilder {
            return PropertyficationBuilderImpl()
        }
    }
}

/**
 * Create a [KotlinValueParameterSpec] with the given name and type.
 *
 * @param name the parameter name
 * @param type the parameter type
 * @param block the configuration block
 * @return a new [KotlinValueParameterSpec] instance
 */
public inline fun KotlinValueParameterSpec(
    name: String,
    type: TypeRef<*>,
    block: KotlinValueParameterSpec.Builder.() -> Unit = {}
): KotlinValueParameterSpec =
    KotlinValueParameterSpec.builder(name, type).apply(block).build()

/**
 * Create a [KotlinValueParameterSpec.Propertyfication] instance using a configuration block.
 *
 * @param block the configuration block for the propertyization
 * @return a new [KotlinValueParameterSpec.Propertyfication] instance
 */
public inline fun propertyficationn(
    block: KotlinValueParameterSpec.PropertyficationBuilder.() -> Unit = {}
): KotlinValueParameterSpec.Propertyfication =
    propertyizationBuilder().apply(block).build()

public inline fun KotlinValueParameterSpec.Builder.defaultValue(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinValueParameterSpec.Builder = apply {
    defaultValue(CodeValue(format, block))
}

public fun KotlinValueParameterSpec.Builder.propertyfy(mutable: Boolean): KotlinValueParameterSpec.Builder =
    propertyfy { this.mutable = mutable }

public inline fun KotlinValueParameterSpec.Builder.propertyfy(
    block: KotlinValueParameterSpec.PropertyficationBuilder.() -> Unit = {}
): KotlinValueParameterSpec.Builder = propertyfy(propertyficationn(block))

/**
 * Set this parameter as a mutable property (var) in the constructor.
 *
 * @return this builder
 */
public fun KotlinValueParameterSpec.Builder.mutableProperty(): KotlinValueParameterSpec.Builder =
    propertyfy { mutable = true }

/**
 * Set this parameter as an immutable property (val) in the constructor.
 *
 * @return this builder
 */
public fun KotlinValueParameterSpec.Builder.immutableProperty(): KotlinValueParameterSpec.Builder =
    propertyfy { mutable = false }

