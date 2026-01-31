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

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.ref

/**
 * Collector for [KotlinPropertySpec].
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface KotlinPropertyCollector<B : KotlinPropertyCollector<B>> {
    /**
     * Add properties.
     */
    public fun addProperties(vararg properties: KotlinPropertySpec): B =
        addProperties(properties.asList())

    /**
     * Add properties.
     */
    public fun addProperties(properties: Iterable<KotlinPropertySpec>): B

    /**
     * Add property.
     */
    public fun addProperty(property: KotlinPropertySpec): B
}

/**
 * Add a property to this builder with the given name and type.
 *
 * @param name the property name
 * @param type the property type
 * @param block the configuration block for the property
 * @return the builder instance
 */
public inline fun <B : KotlinPropertyCollector<B>> B.addProperty(
    name: String,
    type: TypeRef<*>,
    block: KotlinPropertySpec.Builder.() -> Unit = {}
): B = addProperty(KotlinPropertySpec(name, type, block))

/**
 * Add a property to this builder with the given name and type.
 *
 * @param name the property name
 * @param type the property type
 * @param block the configuration block for the property
 * @return the builder instance
 */
public inline fun <B : KotlinPropertyCollector<B>> B.addProperty(
    name: String,
    type: TypeName,
    block: KotlinPropertySpec.Builder.() -> Unit = {}
): B = addProperty(name, type.ref(), block)
