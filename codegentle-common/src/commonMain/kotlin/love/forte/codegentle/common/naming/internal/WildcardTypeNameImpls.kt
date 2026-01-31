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
package love.forte.codegentle.common.naming.internal

import love.forte.codegentle.common.naming.LowerWildcardTypeName
import love.forte.codegentle.common.naming.UpperWildcardTypeName
import love.forte.codegentle.common.ref.TypeRef

internal class UpperWildcardTypeNameImpl(
    override val bounds: List<TypeRef<*>>
) : UpperWildcardTypeName {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UpperWildcardTypeNameImpl) return false

        if (bounds != other.bounds) return false

        return true
    }

    override fun hashCode(): Int {
        return bounds.hashCode()
    }

    override fun toString(): String {
        return buildString {
            append("Upper in ")
            bounds.joinTo(this) { it.typeName.toString() }
        }
    }
}

internal class LowerWildcardTypeNameImpl(
    override val bounds: List<TypeRef<*>>
) : LowerWildcardTypeName {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LowerWildcardTypeNameImpl) return false

        if (bounds != other.bounds) return false

        return true
    }

    override fun hashCode(): Int {
        return bounds.hashCode()
    }

    override fun toString(): String {
        return buildString {
            append("Lower out ")
            bounds.joinTo(this) { it.typeName.toString() }
        }
    }
}
