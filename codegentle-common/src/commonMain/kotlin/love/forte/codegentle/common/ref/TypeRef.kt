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
package love.forte.codegentle.common.ref

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.internal.TypeNameRefStatusBuilderImpl
import love.forte.codegentle.common.ref.internal.TypeRefImpl

/**
 * A reference to a [love.forte.codegentle.common.naming.TypeName].
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface TypeRef<out T : TypeName> {
    public val typeName: T
    public val status: TypeNameRefStatus
}

public typealias TypeRefBuilderDsl<T> = TypeRefBuilder<T>.() -> Unit

/**
 * Create a [TypeRef] with [T].
 *
 *  @see TypeRef
 */
public inline fun <T : TypeName> T.ref(block: TypeRefBuilderDsl<T> = {}): TypeRef<T> =
    TypeRefBuilder(this).also(block).build()

/**
 * Builder for [TypeRef].
 */
public class TypeRefBuilder<T : TypeName>
@PublishedApi internal constructor(public val typeName: T) : BuilderDsl {
    public val status: TypeNameRefStatusBuilder = TypeNameRefStatusBuilderImpl()

    public fun build(): TypeRef<T> {
        return TypeRefImpl(
            typeName = typeName,
            status = status.build()
        )
    }
}
