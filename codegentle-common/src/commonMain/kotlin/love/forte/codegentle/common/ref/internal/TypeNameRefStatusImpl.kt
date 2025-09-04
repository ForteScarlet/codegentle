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
package love.forte.codegentle.common.ref.internal

import love.forte.codegentle.common.computeValueIfAbsent
import love.forte.codegentle.common.ref.TypeNameRefStatus
import love.forte.codegentle.common.ref.TypeNameRefStatusBuilder

internal class TypeNameRefStatusImpl(
    val components: Map<TypeNameRefStatus.Key<*>, Any>
) : TypeNameRefStatus {
    override fun <T : Any> get(key: TypeNameRefStatus.Key<T>): T? =
        components[key]?.let(key::cast)

    override fun contains(key: TypeNameRefStatus.Key<*>): Boolean =
        components.containsKey(key)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TypeNameRefStatusImpl) return false

        if (components != other.components) return false

        return true
    }

    override fun hashCode(): Int {
        return components.hashCode()
    }

    override fun toString(): String {
        return "TypeNameRefStatus(components=$components)"
    }
}

internal class TypeNameRefStatusBuilderImpl : TypeNameRefStatusBuilder {
    private val components = mutableMapOf<TypeNameRefStatus.BuilderKey<*, *>, TypeNameRefStatus.Builder<*>>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any, B : TypeNameRefStatus.Builder<T>> configure(key: TypeNameRefStatus.BuilderKey<T, B>): B {
        return components.computeValueIfAbsent(key) { key -> key.builder() }
            .let(key::castBuilder)
    }

    override fun build(): TypeNameRefStatus {
        val released = components
            .map { (builderKey, builder) ->
                builderKey.key to builder.build()
            }
            .toMap()

        return TypeNameRefStatusImpl(released)
    }
}
