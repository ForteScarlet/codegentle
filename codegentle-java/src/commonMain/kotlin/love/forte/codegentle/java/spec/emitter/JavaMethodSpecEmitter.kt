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

import love.forte.codegentle.common.code.*
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.naming.JavaPrimitiveTypeNames
import love.forte.codegentle.java.spec.JavaMethodSpec
import love.forte.codegentle.java.writer.JavaCodeValueEmitOption
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emit

/**
 * Extension function to emit a [JavaMethodSpec] to a [JavaCodeWriter].
 */
internal fun JavaMethodSpec.emitTo(
    codeWriter: JavaCodeWriter,
    name: String? = null,
    implicitModifiers: Set<JavaModifier> = emptySet()
) {
    // Emit javadoc with parameters
    codeWriter.emitDoc(javadocWithParameters())
    
    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)
    
    // Emit modifiers
    codeWriter.emitModifiers(modifiers, implicitModifiers)

    // Emit type variables if any
    if (typeVariables.isNotEmpty()) {
        codeWriter.emitTypeVariableRefs(typeVariables)
        codeWriter.emit(" ")
    }

    // Emit method signature
    if (isConstructor) {
        codeWriter.emit("$name(") {
            // emitZeroWidthSpace()
        }
    } else {
        codeWriter.emit("%V ${this.name}(") {
            emitType(returnType ?: JavaPrimitiveTypeNames.VOID.ref())
            // emitZeroWidthSpace()
        }
    }

    // Emit parameters
    var firstParameter = true
    val i = parameters.iterator()
    while (i.hasNext()) {
        val parameter = i.next()
        if (!firstParameter) {
            codeWriter.emit(",")
            codeWriter.emitWrappingSpace()
        }

        parameter.emit(codeWriter, !i.hasNext() && isVarargs)
        firstParameter = false
    }

    codeWriter.emit(")")

    // Emit default value if present
    if (!defaultValue.isEmpty()) {
        codeWriter.emit(" default ")
        codeWriter.emit(defaultValue)
    }

    // Emit exceptions if any
    if (exceptions.isNotEmpty()) {
        codeWriter.emitWrappingSpace()
        codeWriter.emit("throws")
        var firstException = true
        for (exception in exceptions) {
            if (!firstException) codeWriter.emit(",")
            codeWriter.emitWrappingSpace()
            codeWriter.emit("%V") {
                emitType(exception)
            }

            firstException = false
        }
    }

    // Emit method body based on modifiers
    when {
        hasModifier(JavaModifier.ABSTRACT) -> {
            codeWriter.emit(";")
        }

        hasModifier(JavaModifier.NATIVE) -> {
            // Code is allowed to support stuff like GWT JSNI.
            codeWriter.emit(code)
            codeWriter.emit(";")
        }

        else -> {
            codeWriter.emitNewLine(" {")

            codeWriter.indent()
            codeWriter.emit(code, JavaCodeValueEmitOption.EnsureTrailingNewline)
            codeWriter.unindent()

            codeWriter.emit("}")
        }
    }
    
    // Clean up type variables
    codeWriter.popTypeVariableRefs(typeVariables)
}

/**
 * Extension function to emit a [JavaMethodSpec] to a [JavaCodeWriter].
 */
internal fun JavaMethodSpec.emitTo(codeWriter: JavaCodeWriter) {
    emitTo(codeWriter, null, emptySet())
}

/**
 * Helper function to build javadoc with parameter documentation.
 */
private fun JavaMethodSpec.javadocWithParameters(): CodeValue {
    if (parameters.isEmpty()) return javadoc

    val builder = CodeValue.builder().addCode(javadoc)
    var emitTagNewline = true
    for (parameter in parameters) {
        val parameterDoc = parameter.javadoc
        if (parameterDoc.isEmpty()) {
            continue
        }

        // Emit a new line before @param section only if the method javadoc is present.
        if (emitTagNewline && !javadoc.isEmpty()) builder.addCode("\n")
        emitTagNewline = false
        builder.addCode("@param %V %V%V") {
            emitLiteral(parameter.name)
            emitLiteral(parameterDoc)
            emitNewline()
        }
    }

    return builder.build()
}
