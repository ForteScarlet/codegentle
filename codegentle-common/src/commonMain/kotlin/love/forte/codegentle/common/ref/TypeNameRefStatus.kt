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
package love.forte.codegentle.common.ref

/**
 * A reference status.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface TypeNameRefStatus {

    public operator fun contains(key: Key<*>): Boolean

    public operator fun <T : Any> get(key: Key<T>): T?

    @SubclassOptInRequired(CodeGentleRefImplementation::class)
    public interface Key<T : Any> {
        /**
         * @throws ClassCastException
         */
        public fun cast(value: Any): T
    }

    @SubclassOptInRequired(CodeGentleRefImplementation::class)
    public interface BuilderKey<T : Any, B : Builder<T>> {
        public val key: Key<T>
        public fun builder(): B

        /**
         * @throws ClassCastException
         */
        public fun castBuilder(value: Any): B
    }

    @SubclassOptInRequired(CodeGentleRefImplementation::class)
    public interface Builder<T : Any> {
        public fun build(): T
    }
}

// Builders

@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface TypeNameRefStatusBuilder {
    public fun
        <T : Any, B : TypeNameRefStatus.Builder<T>>
        configure(key: TypeNameRefStatus.BuilderKey<T, B>): B

    public fun build(): TypeNameRefStatus
}

public inline fun <T : Any, B : TypeNameRefStatus.Builder<T>> TypeNameRefStatusBuilder.configure(
    key: TypeNameRefStatus.BuilderKey<T, B>,
    block: B.() -> Unit
): B = configure(key).also(block)

// @SubclassOptInRequired(CodeGentleRefImplementation::class)
// public interface TypeNameRefStatusBuilderFactory<out T : TypeNameRefStatus, out B : TypeNameRefStatusBuilder> {
//     public fun createBuilder(): B
// }
