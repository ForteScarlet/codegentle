/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2015-2026 Forte Scarlet
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

import love.forte.codegentle.common.naming.ParameterizedTypeName
import love.forte.codegentle.java.writer.JavaCodeWriter

internal fun ParameterizedTypeName.emitTo(codeWriter: JavaCodeWriter) {
    val enclosingType = enclosingType
    if (enclosingType != null) {
        enclosingType.emitTo(codeWriter)
        codeWriter.emit(".")
        codeWriter.emit(rawType.simpleName)
    } else {
        rawType.emitTo(codeWriter)
    }

    if (typeArguments.isNotEmpty()) {
        codeWriter.emit("<")
        var firstParameter = true
        for (parameter in typeArguments) {
            if (!firstParameter) {
                codeWriter.emit(", ")
            }
            codeWriter.emit(parameter)
            firstParameter = false
        }
        codeWriter.emit(">")
    }
}
