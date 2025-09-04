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
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.internal.doEmit
import love.forte.codegentle.java.internal.emitMembers
import love.forte.codegentle.java.internal.emitSupers
import love.forte.codegentle.java.internal.toVirtualTypeSpec
import love.forte.codegentle.java.spec.JavaSealedTypeSpec
import love.forte.codegentle.java.spec.JavaTypeSpec
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emit

/**
 * Extension function to emit a [JavaSealedTypeSpec] to a [JavaCodeWriter].
 */
internal fun JavaSealedTypeSpec.emitTo(
    codeWriter: JavaCodeWriter,
    implicitModifiers: Set<JavaModifier> = emptySet()
) {
    doEmit(codeWriter) {
        // Push an empty type (specifically without nested types) for type-resolution.
        codeWriter.pushType(this.toVirtualTypeSpec(name))
        codeWriter.emitDoc(javadoc)
        codeWriter.emitAnnotationRefs(annotations, false)
        codeWriter.emitModifiers(modifiers, implicitModifiers + kind.asMemberModifiers)
        
        val kindName = when (kind) {
            JavaTypeSpec.Kind.SEALED_CLASS -> "sealed class"
            JavaTypeSpec.Kind.SEALED_INTERFACE -> "sealed interface"
            else -> error("unexpected kind for sealed type: $kind")
        }
        
        codeWriter.emit("$kindName $name")
        codeWriter.emitTypeVariableRefs(typeVariables)

        emitSupers(codeWriter)

        // Emit permits clause if present
        if (permits.isNotEmpty()) {
            codeWriter.emit(" permits")
            var firstType = true
            for (permit in permits) {
                if (!firstType) {
                    codeWriter.emit(",")
                }
                codeWriter.emit(" %V") { emitType(permit) }
                firstType = false
            }
        }

        codeWriter.popType()
        codeWriter.emitNewLine(" {")

        val blankLineManager = BlankLineManager(codeWriter)
        emitMembers(codeWriter, blankLineManager)

        codeWriter.popTypeVariableRefs(typeVariables)

        codeWriter.emit("}")
    }
}
