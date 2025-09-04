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
package love.forte.codegentle.java.naming

import love.forte.codegentle.common.naming.CodeGentleNamingImplementation
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.java.writer.JavaCodeWriter

@SubclassOptInRequired(CodeGentleNamingImplementation::class)
public interface JavaPrimitiveTypeName : TypeName {
    public val keyword: String

    public fun box(): TypeName

    public companion object {
        internal const val VOID = "void"
        internal const val BOOLEAN = "boolean"
        internal const val BYTE = "byte"
        internal const val SHORT = "short"
        internal const val INT = "int"
        internal const val LONG = "long"
        internal const val CHAR = "char"
        internal const val FLOAT = "float"
        internal const val DOUBLE = "double"
    }
}

internal fun JavaPrimitiveTypeName.emitTo(codeWriter: JavaCodeWriter) {
    codeWriter.emit(keyword)
}
