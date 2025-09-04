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
package love.forte.codegentle.kotlin.ref

import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

internal fun TypeRef<*>.emitKotlinTo(codeWriter: KotlinCodeWriter) {
    val status = this.status.kotlinOrNull
    
    // Emit annotations if any
    status?.annotations?.forEach { annotation ->
        codeWriter.emit(annotation)
        codeWriter.emit(" ")
    }
    
    // Emit the type name
    codeWriter.emit(this.typeName)
    
    // Emit nullable marker if needed
    if (status?.nullable == true) {
        codeWriter.emit("?")
    }
}
