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
package love.forte.codegentle.java.spec.emitter

import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.spec.JavaFieldSpec
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emit

/**
 * Extension function to emit a [JavaFieldSpec] to a [JavaCodeWriter].
 */
internal fun JavaFieldSpec.emitTo(
    codeWriter: JavaCodeWriter,
    implicitModifiers: Set<JavaModifier> = emptySet()
) {
    // Emit javadoc
    codeWriter.emitDoc(javadoc)
    
    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)
    
    // Emit modifiers
    codeWriter.emitModifiers(modifiers, implicitModifiers)
    
    // Emit type and name
    codeWriter.emit("%V $name") {
        emitType(type)
    }
    
    // Emit initializer if present
    if (!initializer.isEmpty()) {
        codeWriter.emit(" = ")
        codeWriter.emit(initializer)
    }
    
    // End with semicolon
    codeWriter.emitNewLine(";")
}

/**
 * Extension function to emit a [JavaFieldSpec] to a [JavaCodeWriter].
 */
internal fun JavaFieldSpec.emitTo(codeWriter: JavaCodeWriter) {
    emitTo(codeWriter, emptySet())
}
