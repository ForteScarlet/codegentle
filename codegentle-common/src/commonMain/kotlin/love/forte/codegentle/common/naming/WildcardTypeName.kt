/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2014-2026 Forte Scarlet
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
package love.forte.codegentle.common.naming

import love.forte.codegentle.common.naming.internal.LowerWildcardTypeNameImpl
import love.forte.codegentle.common.naming.internal.UpperWildcardTypeNameImpl
import love.forte.codegentle.common.ref.TypeRef
import kotlin.js.JsName

/**
 * A Wildcard type name.
 *
 * @author ForteScarlet
 */
public sealed interface WildcardTypeName : TypeName {
    public val bounds: List<TypeRef<*>>

    // Java:
    //  uppers: `? extends T1 & T2`
    //  lowers: `? super T1 & T2`
    // Kotlin:
    //  `out A`, `out B`
    //  `in A`, `in B`
}


/**
 * No bounds [WildcardTypeName].
 * - Java `?` or `? extends Object`
 * - Kotlin `*` or `out Any?`
 */
public data object EmptyWildcardTypeName : WildcardTypeName {
    override val bounds: List<TypeRef<*>> = emptyList()
    override fun toString(): String = "*"
}

public val WildcardTypeName.isEmpty: Boolean
    get() = bounds.isEmpty()

/**
 * Upper wildcard type name, subtype wildcard type name.
 * - Java: `? super T`.
 * - Kotlin `in T`.
 *
 * The bounds are *lower bounds*.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleNamingImplementation::class)
public interface UpperWildcardTypeName : WildcardTypeName

/**
 * Lower wildcard type name, supertype wildcard type name.
 * - Java: `? extends T`.
 * - Kotlin: `T : ?`, `out T`.
 *
 * The bounds are *upper bounds*.
 */
@SubclassOptInRequired(CodeGentleNamingImplementation::class)
public interface LowerWildcardTypeName : WildcardTypeName

@JsName("emptyWildcardTypeName")
public fun WildcardTypeName(): WildcardTypeName = EmptyWildcardTypeName

public fun WildcardTypeName.toLower(bounds: List<TypeRef<*>> = this.bounds): LowerWildcardTypeName =
    this as? LowerWildcardTypeName ?: LowerWildcardTypeNameImpl(bounds)

public fun WildcardTypeName.toUpper(bounds: List<TypeRef<*>> = this.bounds): UpperWildcardTypeName =
    this as? UpperWildcardTypeName ?: UpperWildcardTypeNameImpl(bounds)

public fun LowerWildcardTypeName(upperBound: TypeRef<*>): LowerWildcardTypeName =
    LowerWildcardTypeNameImpl(listOf(upperBound))

public fun UpperWildcardTypeName(lowerBound: TypeRef<*>): UpperWildcardTypeName =
    UpperWildcardTypeNameImpl(listOf(lowerBound))

public fun LowerWildcardTypeName(upperBounds: List<TypeRef<*>>): LowerWildcardTypeName {
    require(upperBounds.isNotEmpty()) { "Upper bounds must not be empty." }
    return LowerWildcardTypeNameImpl(upperBounds)
}

public fun UpperWildcardTypeName(lowerBounds: List<TypeRef<*>>): UpperWildcardTypeName {
    require(lowerBounds.isNotEmpty()) { "Lower bounds must not be empty." }
    return UpperWildcardTypeNameImpl(lowerBounds)
}
