/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2015-2025 Forte Scarlet
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
package love.forte.codegentle.java.naming

import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.naming.EmptyWildcardTypeName
import love.forte.codegentle.common.naming.LowerWildcardTypeName
import love.forte.codegentle.common.naming.UpperWildcardTypeName
import love.forte.codegentle.common.naming.WildcardTypeName
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emit


internal fun WildcardTypeName.emitTo(codeWriter: JavaCodeWriter) {
    when (this) {
        is LowerWildcardTypeName -> emitTo(codeWriter)
        is UpperWildcardTypeName -> emitTo(codeWriter)
        EmptyWildcardTypeName -> codeWriter.emit("?")
    }
}

internal fun LowerWildcardTypeName.emitTo(codeWriter: JavaCodeWriter) {
    if (bounds.isNotEmpty()) {
        var extends = false
        bounds.forEachIndexed { index, upperBound ->
            if (index == 0) {
                // first
                codeWriter.emit("?")
            }

            if (upperBound.typeName == JavaClassNames.OBJECT) {
                // continue
                return@forEachIndexed
            }

            if (!extends) {
                codeWriter.emit(" extends %V") { emitType(upperBound) }
                extends = true
            } else {
                codeWriter.emit(" & %V") { emitType(upperBound) }
            }
        }
    }
}

internal fun UpperWildcardTypeName.emitTo(codeWriter: JavaCodeWriter) {
    bounds.forEachIndexed { index, lowerBound ->
        if (index == 0) {
            // first
            codeWriter.emit("? super %V") {
                emitType(lowerBound)
            }
        } else {
            codeWriter.emit(" & %V") {
                emitType(lowerBound)
            }
        }
    }
}
