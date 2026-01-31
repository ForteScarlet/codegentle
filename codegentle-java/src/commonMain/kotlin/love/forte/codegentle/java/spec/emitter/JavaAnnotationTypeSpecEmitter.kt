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

import love.forte.codegentle.common.code.emitLiteral
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.internal.doEmit
import love.forte.codegentle.java.internal.emitMembers
import love.forte.codegentle.java.internal.toVirtualTypeSpec
import love.forte.codegentle.java.spec.JavaAnnotationTypeSpec
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emit

/**
 * Extension function to emit a [JavaAnnotationTypeSpec] to a [JavaCodeWriter].
 */
internal fun JavaAnnotationTypeSpec.emitTo(codeWriter: JavaCodeWriter, implicitModifiers: Set<JavaModifier>) {
    doEmit(codeWriter) {
        // Push an empty type (specifically without nested types) for type-resolution.
        codeWriter.pushType(this.toVirtualTypeSpec(name))

        // Emit javadoc
        codeWriter.emitDoc(javadoc)
        
        // Emit annotations
        codeWriter.emitAnnotationRefs(annotations, false)
        
        // Emit modifiers
        codeWriter.emitModifiers(modifiers, implicitModifiers + kind.asMemberModifiers)
        
        // Emit @interface keyword and name
        codeWriter.emit("@interface %V") {
            emitLiteral(name)
        }

        // Emit type variables
        codeWriter.emitTypeVariableRefs(typeVariables)

        codeWriter.popType()
        codeWriter.emitNewLine(" {")

        // Emit members
        val blankLineManager = BlankLineManager(codeWriter)
        emitMembers(codeWriter, blankLineManager)

        codeWriter.popTypeVariableRefs(typeVariables)
        codeWriter.emit("}")
    }
}
