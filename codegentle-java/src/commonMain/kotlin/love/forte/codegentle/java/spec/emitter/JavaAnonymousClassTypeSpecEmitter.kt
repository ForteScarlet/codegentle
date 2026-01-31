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
package love.forte.codegentle.java.spec.emitter

import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.java.internal.doEmit
import love.forte.codegentle.java.internal.emitMembers
import love.forte.codegentle.java.spec.JavaAnonymousClassTypeSpec
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emit

/**
 * Extension function to emit a [JavaAnonymousClassTypeSpec] to a [JavaCodeWriter].
 */
internal fun JavaAnonymousClassTypeSpec.emitTo(codeWriter: JavaCodeWriter, enumName: String? = null) {
    doEmit(codeWriter) {
        if (enumName != null) {
            // Emit enum constant with anonymous class body
            codeWriter.emit(javadoc)
            codeWriter.emitAnnotationRefs(annotations, false)
            codeWriter.emit(enumName)
            
            if (!anonymousTypeArguments.isEmpty()) {
                codeWriter.emit("(")
                codeWriter.emit(anonymousTypeArguments)
                codeWriter.emit(")")
            }
            
            // If no members, don't emit the body
            if (fields.isEmpty() && methods.isEmpty() && subtypes.isEmpty()) {
                return
            }
            
            codeWriter.emitNewLine(" {")
        } else {
            // Emit anonymous class instantiation
            val supertype = superinterfaces.firstOrNull() ?: superclass
            
            codeWriter.emit("new %V(") { emitType(supertype!!) }
            codeWriter.emit(anonymousTypeArguments)
            codeWriter.emitNewLine(") {")
        }

        // Emit all members
        val blankLineManager = BlankLineManager(codeWriter)
        emitMembers(codeWriter, blankLineManager)
        codeWriter.popTypeVariableRefs(typeVariables)
        codeWriter.emit("}")
    }
}
