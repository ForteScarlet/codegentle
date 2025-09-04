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

import love.forte.codegentle.common.code.emitLiteral
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.internal.doEmit
import love.forte.codegentle.java.internal.emitImplements
import love.forte.codegentle.java.internal.emitMembers
import love.forte.codegentle.java.internal.toVirtualTypeSpec
import love.forte.codegentle.java.spec.JavaEnumTypeSpec
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emit

/**
 * Extension function to emit a [JavaEnumTypeSpec] to a [JavaCodeWriter].
 */
internal fun JavaEnumTypeSpec.emitTo(codeWriter: JavaCodeWriter, implicitModifiers: Set<JavaModifier>) {
    doEmit(codeWriter) {
        codeWriter.pushType(this.toVirtualTypeSpec(name))

        // Emit javadoc
        codeWriter.emit(javadoc)

        // Emit annotations
        codeWriter.emitAnnotationRefs(annotations, false)
        
        // Emit modifiers
        codeWriter.emitModifiers(modifiers, implicitModifiers + kind.asMemberModifiers)
        
        // Emit enum keyword and name
        codeWriter.emit("enum %V") {
            emitLiteral(name)
        }
        
        // Emit type variables
        codeWriter.emitTypeVariableRefs(typeVariables)

        // Emit implements clause
        codeWriter.emitImplements(superinterfaces)

        codeWriter.popType()
        codeWriter.emitNewLine(" {")

        // Emit enum body
        emitEnumBody(codeWriter)

        codeWriter.popTypeVariableRefs(typeVariables)
        codeWriter.emit("}")
    }
}

/**
 * Emit the enum body including constants and other members.
 */
private fun JavaEnumTypeSpec.emitEnumBody(codeWriter: JavaCodeWriter) {
    val blankLineManager = BlankLineManager(codeWriter)
    emitMembers(codeWriter, blankLineManager) { blankLineManager ->
        val needsSeparator = fields.isNotEmpty() || methods.isNotEmpty() || subtypes.isNotEmpty()
        val enumConstantIterator = enumConstants.entries.iterator()
        
        while (enumConstantIterator.hasNext()) {
            val enumConstant = enumConstantIterator.next()
            blankLineManager.withRequirement {
                // Emit enum constant
                enumConstant.value.emit(codeWriter, enumConstant.key, emptySet())
                
                if (enumConstantIterator.hasNext()) {
                    codeWriter.emitNewLine(",")
                } else if (!needsSeparator) {
                    codeWriter.emitNewLine("")
                }
            }
        }

        // Add semicolon after enum constants if there are other members
        if (needsSeparator) {
            codeWriter.emitNewLine(";")
            blankLineManager.required()
        }
    }
}
