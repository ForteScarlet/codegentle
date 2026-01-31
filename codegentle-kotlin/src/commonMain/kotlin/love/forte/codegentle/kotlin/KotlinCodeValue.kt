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
package love.forte.codegentle.kotlin

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeSimplePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.MemberName
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.canonicalName
import love.forte.codegentle.common.writer.InternalWriterApi
import love.forte.codegentle.kotlin.naming.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

private sealed class ClassNameOrMemberName {
    abstract val type: TypeName
    abstract val canonicalName: String

    data class Class(override val type: ClassName) : ClassNameOrMemberName() {
        override val canonicalName: String get() = type.canonicalName
    }

    data class Member(override val type: MemberName) : ClassNameOrMemberName() {
        override val canonicalName: String get() = type.canonicalName
    }
}

private fun TypeName.toClassNameOrMemberName(): ClassNameOrMemberName =
    when (this) {
        is ClassName -> ClassNameOrMemberName.Class(this)
        is MemberName -> ClassNameOrMemberName.Member(this)
        else -> error("Unknown type name: $this")
    }

@OptIn(InternalWriterApi::class)
internal fun CodeValue.emitTo(codeWriter: KotlinCodeWriter, ensureTrailingNewline: Boolean = false) {
    // ClassName | MemberName
    var deferredTypeName: ClassNameOrMemberName? = null
    val iterator = parts.listIterator()

    // Do continue if true returned
    fun resolveDeferredTypeName(typeName: TypeName): Boolean {
        if ((typeName is ClassName || typeName is MemberName) && iterator.hasNext()) {
            fun typeNameEnclosingClassName(): ClassName? {
                return when (typeName) {
                    is ClassName -> typeName.enclosingClassName
                    is MemberName -> typeName.enclosingClassName
                    else -> error("Unknown type name: $typeName")
                }
            }

            val next = parts[iterator.nextIndex()]
            if (next !is CodeArgumentPart) {
                val enclosingClassName = typeNameEnclosingClassName()
                if (enclosingClassName != null
                    && codeWriter.importedTypeName(typeName.name) == typeName
                ) {
                    check(deferredTypeName == null) { "pending type for static import?!" }
                    deferredTypeName = typeName.toClassNameOrMemberName()
                    return true // continue
                }
            }
        }

        return false
    }



    while (iterator.hasNext()) {
        when (val part = iterator.next()) {
            is CodeSimplePart -> {
                val value = part.value
                // handle deferred type
                if (deferredTypeName != null) {
                    if (value.startsWith(".")) {
                        if (codeWriter.emitStaticImportMember(deferredTypeName.canonicalName, value)) {
                            // okay, static import hit and all was emitted, so clean-up and jump to next part
                            deferredTypeName = null
                            continue
                        }
                    }
                    codeWriter.emit(deferredTypeName.type)
                    deferredTypeName = null
                }
                codeWriter.emit(value)
            }

            is CodeArgumentPart.Skip -> {
                codeWriter.emit(CodePart.PLACEHOLDER)
            }

            is CodeArgumentPart.Literal -> {
                codeWriter.emitLiteral(part.value)
            }

            is CodeArgumentPart.Name -> {
                val originalValue = part.originalValue
                if (originalValue is MemberName) {
                    originalValue.emitTo(codeWriter)
                } else {
                    codeWriter.emitLiteral(part.name)
                }
            }

            is CodeArgumentPart.Str -> {
                codeWriter.emit(
                    part.value.stringLiteralWithQuotes(
                        indent = codeWriter.indentValue,
                        ignoreStringInterpolation = part.handleSpecialCharacter
                    )
                )
            }

            is CodeArgumentPart.Type -> {
                val typeName = part.type

                if (resolveDeferredTypeName(typeName)) {
                    continue
                }

                codeWriter.emit(typeName)
            }

            is CodeArgumentPart.TypeRef -> {
                val typeRef = part.type
                val typeName = typeRef.typeName
                if (resolveDeferredTypeName(typeName)) {
                    continue
                }

                codeWriter.emit(typeRef)
            }

            is CodeArgumentPart.Indent -> {
                codeWriter.indent(part.levels)
            }

            is CodeArgumentPart.Unindent -> {
                codeWriter.unindent(part.levels)
            }

            is CodeArgumentPart.StatementBegin -> {
                check(codeWriter.statementLine == -1) {
                    "statement begin followed by statement begin"
                }
                codeWriter.statementLine = 0
            }

            is CodeArgumentPart.StatementEnd -> {
                check(codeWriter.statementLine != -1) {
                    "statement end has no matching statement begin"
                }
                if (codeWriter.statementLine > 0) {
                    codeWriter.unindent(2) // End a multi-line statement. Decrease the indentation level.
                }
                codeWriter.statementLine = -1
                codeWriter.emitNewLine()
            }

            is CodeArgumentPart.WrappingSpace -> {
                // Use a fixed indentation level for wrapping spaces
                codeWriter.out.wrappingSpace(2)
            }

            is CodeArgumentPart.ZeroWidthSpace -> {
                // Use a fixed indentation level for zero-width spaces
                codeWriter.out.zeroWidthSpace(2)
            }

            is CodeArgumentPart.Newline -> {
                codeWriter.emitNewLine()
            }

            is CodeArgumentPart.OtherCodeValue -> {
                codeWriter.emit(part.value)
            }

            is CodeArgumentPart.ControlFlow -> {
                when (part.position) {
                    CodeArgumentPart.ControlFlow.Position.BEGIN -> {
                        part.codeValue?.let { codeWriter.emit(it) }
                        codeWriter.emit(" {")
                        codeWriter.emitNewLine()
                        codeWriter.indent()
                    }
                    CodeArgumentPart.ControlFlow.Position.NEXT -> {
                        codeWriter.unindent()
                        codeWriter.emit("}")
                        part.codeValue?.let {
                            codeWriter.emit(" ")
                            codeWriter.emit(it)
                        }
                        codeWriter.emit(" {")
                        codeWriter.emitNewLine()
                        codeWriter.indent()
                    }
                    CodeArgumentPart.ControlFlow.Position.END -> {
                        codeWriter.unindent()
                        codeWriter.emit("}")
                        part.codeValue?.let {
                            codeWriter.emit(" ")
                            codeWriter.emit(it)
                        }
                        codeWriter.emitNewLine()
                    }
                }
            }
        }
    }

    if (ensureTrailingNewline && codeWriter.out.lastChar != '\n') {
        codeWriter.emitNewLine()
    }
}
