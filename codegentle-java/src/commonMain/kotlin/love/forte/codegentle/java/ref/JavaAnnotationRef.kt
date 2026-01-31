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
package love.forte.codegentle.java.ref

import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.java.emitTo
import love.forte.codegentle.java.writer.JavaCodeWriter

internal fun AnnotationRef.emitTo(codeWriter: JavaCodeWriter) {
    codeWriter.emitJavaAnnotation(typeName, members)
}


private fun JavaCodeWriter.emitJavaAnnotation(
    type: TypeName,
    members: Map<String, AnnotationRef.MemberValue>
) {
    // val whitespace = if (inline) "" else "\n"
    val memberSeparator = ", " // member: inline only //if (inline) ", " else ",\n"

    if (members.isEmpty()) {
        // @Singleton
        emit("@")
        emit(type)
    } else if (members.size == 1 && members.containsKey("value")) {
        // @Named("foo")
        emit("@")
        emit(type)
        emit("(")
        // Always inline between values.
        emitAnnotationValues(memberSeparator, members["value"]!!)
        emit(")")
    } else {
        // @Column(name = "updated_at", nullable = false)
        emit("@")
        emit(type)
        emit("(")
        withIndent(2) {
            val i = members.entries.iterator()
            while (i.hasNext()) {
                val entry = i.next()
                emit(entry.key)
                emit(" = ")
                emitAnnotationValues(memberSeparator, entry.value)
                if (i.hasNext()) {
                    emit(memberSeparator)
                }
            }
        }
        emit(")")
    }
}

private fun JavaCodeWriter.emitAnnotationValues(
    memberSeparator: String,
    value: AnnotationRef.MemberValue
) {
    when (value) {
        is AnnotationRef.MemberValue.Single -> {
            withIndent(2) {
                value.codeValue.emitTo(this)
            }
            return
        }
        is AnnotationRef.MemberValue.Multiple -> {
            emit("{")
            var first = true
            for (codeValue in value.codeValues) {
                if (!first) emit(memberSeparator)
                codeValue.emitTo(this)
                first = false
            }
            emit("}")
        }
    }
}
