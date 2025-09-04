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
package love.forte.codegentle.java

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeSimplePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.MemberName
import love.forte.codegentle.common.naming.canonicalName
import love.forte.codegentle.common.writer.InternalWriterApi
import love.forte.codegentle.java.naming.emitTo
import love.forte.codegentle.java.writer.JavaCodeWriter

@OptIn(InternalWriterApi::class)
internal fun CodeValue.emitTo(codeWriter: JavaCodeWriter, ensureTrailingNewline: Boolean = false) {
    var deferredTypeName: ClassName? = null
    val iterator = parts.listIterator()

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
                    deferredTypeName.emitTo(codeWriter)
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
                codeWriter.emit(part.value.stringLiteralWithQuotes(codeWriter.indentValue))
            }

            is CodeArgumentPart.Type -> {
                val typeName = part.type
                // TODO 下面这逻辑干啥的？
                if (typeName is ClassName && iterator.hasNext()) {
                    val next = parts[iterator.nextIndex()]
                    // !next.start('$')
                    if (next !is CodeArgumentPart) {
                        val candidate: ClassName = typeName
                        if (candidate.enclosingClassName != null
                            && codeWriter.importedTypes[candidate.simpleName] == candidate
                        ) {
                            check(deferredTypeName == null) { "pending type for static import?!" }
                            deferredTypeName = candidate
                            continue
                        }
                    }
                }

                codeWriter.emit(typeName)
            }

            is CodeArgumentPart.TypeRef -> {
                val typeRef = part.type
                val typeName = typeRef.typeName
                // TODO 下面这逻辑干啥的？
                if (typeName is ClassName && iterator.hasNext()) {
                    val next = parts[iterator.nextIndex()]
                    // !next.start('$')
                    if (next !is CodeArgumentPart) {
                        val candidate: ClassName = typeName
                        if (candidate.enclosingClassName != null
                            && codeWriter.importedTypes[candidate.simpleName] == candidate
                        ) {
                            check(deferredTypeName == null) { "pending type for static import?!" }
                            deferredTypeName = candidate
                            continue
                        }
                    }
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
                codeWriter.out.startRecordLastNonBlankChar()
            }

            is CodeArgumentPart.StatementEnd -> {
                check(codeWriter.statementLine != -1) {
                    "statement end has no matching statement begin"
                }
                if (codeWriter.statementLine > 0) {
                    codeWriter.unindent(2) // End a multi-line statement. Decrease the indentation level.
                }
                codeWriter.statementLine = -1
                if (codeWriter.out.lastNonBlankChar != ';') {
                    codeWriter.out.append(";")
                }
                codeWriter.emitNewLine()
                codeWriter.out.stopRecordLastNonBlankChar()
            }

            is CodeArgumentPart.WrappingSpace -> {
                codeWriter.out.wrappingSpace(codeWriter.indentLevel + 2)
            }

            is CodeArgumentPart.ZeroWidthSpace -> {
                codeWriter.out.zeroWidthSpace(codeWriter.indentLevel + 2)
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
                        part.codeValue?.let { codeValue ->
                            codeWriter.emit(" ")
                            codeWriter.emit(codeValue)
                            // For Java, check if this is a statement that needs a semicolon
                            // Only add semicolon for statements like "while (condition)" in do-while
                            val codeText = codeValue.parts.joinToString("") { codePart ->
                                when (codePart) {
                                    is love.forte.codegentle.common.code.CodeSimplePart -> codePart.value
                                    else -> ""
                                }
                            }
                            codeWriter.emit(";")
                            codeWriter.emitNewLine()
                            // if (codeText.trim().startsWith("while")) {
                            //     codeWriter.emit(";")
                            //     codeWriter.emitNewLine()
                            // } else {
                            //     // If endControlFlow has content that's not "while", it opens a new block
                            //     codeWriter.emit(" {")
                            //     codeWriter.emitNewLine()
                            //     codeWriter.indent()
                            // }
                        } ?: run {
                            codeWriter.emitNewLine()
                        }
                    }
                }
            }

        }
    }

    if (ensureTrailingNewline && codeWriter.out.lastChar != '\n') {
        codeWriter.emitNewLine()
    }

    codeWriter.out.stopRecordLastNonBlankChar()
}
