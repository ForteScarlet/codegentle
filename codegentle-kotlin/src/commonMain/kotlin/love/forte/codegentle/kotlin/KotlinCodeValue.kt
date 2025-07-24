package love.forte.codegentle.kotlin

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeSimplePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.canonicalName
import love.forte.codegentle.common.writer.InternalWriterApi
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

@OptIn(InternalWriterApi::class)
internal fun CodeValue.emitTo(codeWriter: KotlinCodeWriter, ensureTrailingNewline: Boolean = false) {
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
                    codeWriter.emit(deferredTypeName)
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
                codeWriter.emitLiteral(part.name)
            }

            is CodeArgumentPart.Str -> {
                codeWriter.emit(part.value.stringLiteralWithQuotes(codeWriter.indentValue))
            }

            is CodeArgumentPart.Type -> {
                val typeName = part.type
                if (typeName is ClassName && iterator.hasNext()) {
                    val next = parts[iterator.nextIndex()]
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
                if (typeName is ClassName && iterator.hasNext()) {
                    val next = parts[iterator.nextIndex()]
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

            is CodeArgumentPart.OtherCodeValue -> {
                codeWriter.emit(part.value)
            }
        }
    }

    if (ensureTrailingNewline && codeWriter.out.lastChar != '\n') {
        codeWriter.emitNewLine()
    }
}
