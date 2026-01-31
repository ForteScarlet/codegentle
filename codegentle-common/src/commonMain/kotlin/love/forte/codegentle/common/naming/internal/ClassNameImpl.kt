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

import love.forte.codegentle.common.naming.*

/**
 *
 * @author ForteScarlet
 */
internal data class ClassNameImpl(
    override val packageName: PackageName,
    override val enclosingClassName: ClassName?,
    override val simpleName: String,
) : ClassName {
    override val topLevelClassName: ClassName
        get() = enclosingClassName?.topLevelClassName ?: this


    override fun peerClass(name: String): ClassName {
        return ClassNameImpl(packageName, enclosingClassName, name)
    }

    override fun nestedClass(name: String): ClassName {
        return ClassNameImpl(packageName, this, name)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClassName) return false

        return this contentEquals other
    }

    override fun hashCode(): Int {
        return contentHashCode()
    }

    override fun toString(): String {
        return canonicalName
    }
}
