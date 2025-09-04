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
package love.forte.codegentle.java.strategy

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.Named
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.simpleNames
import love.forte.codegentle.java.internal.isSourceIdentifier
import love.forte.codegentle.java.internal.isSourceName

/**
 *
 * @author ForteScarlet
 */
public open class DefaultJavaWriteStrategy : JavaWriteStrategy {
    override fun isValidSourceName(name: TypeName): Boolean {
        return when (name) {
            is ClassName -> {
                // TODO packageName ?
                name.simpleNames.all { it.isSourceName() }
            }

            is Named -> {
                name.name.isSourceName()
            }

            else -> true
        }
    }

    override fun isValidSourceName(name: String): Boolean =
        name.isSourceName()

    override fun isIdentifier(value: String): Boolean {
        return value.isSourceIdentifier()
    }

    override fun omitJavaLangPackage(): Boolean = true
}
