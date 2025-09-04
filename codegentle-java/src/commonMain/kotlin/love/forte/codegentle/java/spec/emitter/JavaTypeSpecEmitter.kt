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

import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.internal.doEmit
import love.forte.codegentle.java.internal.emitMembers
import love.forte.codegentle.java.internal.emitSupers
import love.forte.codegentle.java.internal.toVirtualTypeSpec
import love.forte.codegentle.java.spec.JavaTypeSpec
import love.forte.codegentle.java.writer.JavaCodeWriter

/**
 * Extension function to emit a [JavaTypeSpec] to a [JavaCodeWriter].
 */
internal fun JavaTypeSpec.emitTo(
    codeWriter: JavaCodeWriter,
    implicitModifiers: Set<JavaModifier> = emptySet()
) {
    doEmit(codeWriter) {
        val typeName = name ?: ""
        
        // Push an empty type (specifically without nested types) for type-resolution.
        codeWriter.pushType(this.toVirtualTypeSpec(typeName))
        
        // Emit javadoc
        codeWriter.emitDoc(javadoc)
        
        // Emit annotations
        codeWriter.emitAnnotationRefs(annotations, false)
        
        // Emit modifiers
        codeWriter.emitModifiers(modifiers, implicitModifiers + kind.asMemberModifiers)
        
        // Emit type declaration
        codeWriter.emit("${kind.name.lowercase()} $typeName")
        
        // Emit type variables
        codeWriter.emitTypeVariableRefs(typeVariables)

        // Emit superclass and interfaces
        emitSupers(codeWriter)

        codeWriter.popType()
        codeWriter.emitNewLine(" {")

        // Emit all members
        val blankLineManager = BlankLineManager(codeWriter)
        emitMembers(codeWriter, blankLineManager)

        codeWriter.popTypeVariableRefs(typeVariables)

        codeWriter.emit("}")
    }
}

/**
 * Extension function to emit a [JavaTypeSpec] to a [JavaCodeWriter].
 */
internal fun JavaTypeSpec.emitTo(codeWriter: JavaCodeWriter) {
    emitTo(codeWriter, emptySet())
}
