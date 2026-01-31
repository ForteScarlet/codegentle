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
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.spec.internal.KotlinValueClassSpecBuilderImpl

/**
 * A generated Kotlin value class.
 *
 * ```kotlin
 * value class ValueClass(val value: String)
 * ```
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinValueClassTypeSpec : KotlinTypeSpec {
    override val name: String

    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.CLASS

    /**
     * The primary constructor of the value class.
     * Cannot have constructorDelegation since value classes cannot inherit from other classes.
     */
    public val primaryConstructor: KotlinConstructorSpec

    /**
     * Secondary constructors of the value class.
     */
    public val secondaryConstructors: List<KotlinConstructorSpec>

    /**
     * Value class cannot have subtypes.
     * Always empty.
     */
    override val subtypes: List<KotlinTypeSpec>
        get() = emptyList()

    override fun isMemberEmpty(): Boolean {
        return secondaryConstructors.isEmpty() && super.isMemberEmpty()
    }

    public companion object {
        /**
         * Create a builder for a value class.
         *
         * @param name the value class name
         * @param primaryConstructor the primary constructor
         * @return a new builder
         */
        public fun builder(name: String, primaryConstructor: KotlinConstructorSpec): Builder {
            return KotlinValueClassSpecBuilderImpl(name, primaryConstructor)
        }
    }

    /**
     * Builder for [KotlinValueClassTypeSpec].
     */
    public interface Builder :
        KotlinTypeSpecBuilder<KotlinValueClassTypeSpec, Builder>,
        KotlinPropertyCollector<Builder>,
        KotlinFunctionCollector<Builder>,
        InitializerBlockCollector<Builder>,
        SuperinterfaceCollector<Builder>,
        KotlinSecondaryConstructorCollector<Builder> {
        /**
         * The value class name.
         */
        public val name: String

        /**
         * The primary constructor of the value class.
         */
        public val primaryConstructor: KotlinConstructorSpec

        /**
         * Build [KotlinValueClassTypeSpec] instance.
         */
        override fun build(): KotlinValueClassTypeSpec
    }
}

/**
 * Create a [KotlinValueClassTypeSpec] by providing the name and a pre-built primary constructor.
 *
 * @param name the value class name
 * @param primaryConstructor the pre-built primary constructor
 * @param block optional configuration block for additional class specifications
 * @return a new [KotlinValueClassTypeSpec] instance
 */
public inline fun KotlinValueClassTypeSpec(
    name: String,
    primaryConstructor: KotlinConstructorSpec,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): KotlinValueClassTypeSpec {
    return KotlinValueClassTypeSpec.builder(name, primaryConstructor).apply(block).build()
}

/**
 * Create a [KotlinValueClassTypeSpec] by providing the name and a builder for the primary constructor.
 *
 * @param name the value class name
 * @param primaryConstructor builder block for constructing the primary constructor
 * @param block optional configuration block for additional class specifications
 * @return a new [KotlinValueClassTypeSpec] instance
 */
public inline fun KotlinValueClassTypeSpec(
    name: String,
    primaryConstructor: KotlinConstructorSpec.Builder.() -> Unit,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): KotlinValueClassTypeSpec {
    return KotlinValueClassTypeSpec(
        name = name,
        primaryConstructor = KotlinConstructorSpec(primaryConstructor),
        block = block
    )
}

/**
 * Create a [KotlinValueClassTypeSpec] by providing the name and a single parameter specification.
 *
 * @param name the value class name
 * @param primaryParameter pre-built parameter specification for the primary constructor
 * @param block optional configuration block for additional class specifications
 * @return a new [KotlinValueClassTypeSpec] instance
 */
public inline fun KotlinValueClassTypeSpec(
    name: String,
    primaryParameter: KotlinValueParameterSpec,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): KotlinValueClassTypeSpec {
    return KotlinValueClassTypeSpec(
        name = name,
        primaryConstructor = KotlinConstructorSpec {
            addParameter(primaryParameter)
        },
        block = block
    )
}

/**
 * Create a [KotlinValueClassTypeSpec] by providing the name and parameter details using TypeRef.
 *
 * @param name the value class name
 * @param primaryParameterName name of the single parameter
 * @param primaryParameterType type of the parameter as TypeRef
 * @param primaryParameterBuilder optional configuration for the parameter
 * @param block optional configuration block for additional class specifications
 * @return a new [KotlinValueClassTypeSpec] instance
 */
public inline fun KotlinValueClassTypeSpec(
    name: String,
    primaryParameterName: String,
    primaryParameterType: TypeRef<*>,
    primaryParameterBuilder: KotlinValueParameterSpec.Builder.() -> Unit = {},
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): KotlinValueClassTypeSpec {
    return KotlinValueClassTypeSpec(
        name = name,
        primaryConstructor = KotlinConstructorSpec {
            addParameter(primaryParameterName, primaryParameterType, primaryParameterBuilder)
        },
        block = block
    )
}

/**
 * Create a [KotlinValueClassTypeSpec] by providing the name and parameter details using TypeName.
 *
 * @param name the value class name
 * @param primaryParameterName name of the single parameter
 * @param primaryParameterType type of the parameter as TypeName
 * @param primaryParameterBuilder optional configuration for the parameter
 * @param block optional configuration block for additional class specifications
 * @return a new [KotlinValueClassTypeSpec] instance
 */
public inline fun KotlinValueClassTypeSpec(
    name: String,
    primaryParameterName: String,
    primaryParameterType: TypeName,
    primaryParameterBuilder: KotlinValueParameterSpec.Builder.() -> Unit = {},
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): KotlinValueClassTypeSpec {
    return KotlinValueClassTypeSpec(
        name = name,
        primaryConstructor = KotlinConstructorSpec {
            addParameter(primaryParameterName, primaryParameterType.ref(), primaryParameterBuilder)
        },
        block = block
    )
}

