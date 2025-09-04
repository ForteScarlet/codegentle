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

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.ref


/**
 * Collector for [KotlinFunctionSpec]
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface KotlinFunctionCollector<B : KotlinFunctionCollector<B>> {
    /**
     * Add functions.
     */
    public fun addFunctions(functions: Iterable<KotlinFunctionSpec>): B

    /**
     * Add functions.
     */
    public fun addFunctions(vararg functions: KotlinFunctionSpec): B =
        addFunctions(functions.asList())

    /**
     * Add function.
     */
    public fun addFunction(function: KotlinFunctionSpec): B
}

public inline fun <B : KotlinFunctionCollector<B>> B.addFunction(
    name: String,
    type: TypeRef<*> = KotlinFunctionSpec.DEFAULT_REF,
    block: KotlinFunctionSpec.Builder.() -> Unit = {}
): B = addFunction(KotlinFunctionSpec(name, type, block))

public inline fun <B : KotlinFunctionCollector<B>> B.addFunction(
    name: String,
    type: TypeName,
    block: KotlinFunctionSpec.Builder.() -> Unit = {}
): B = addFunction(name, type.ref(), block)

public inline fun <B : KotlinFunctionCollector<B>> B.addMainFunction(
    block: KotlinFunctionSpec.Builder.() -> Unit = {}
): B = addFunction(KotlinFunctionSpec.mainBuilder().also(block).build())
