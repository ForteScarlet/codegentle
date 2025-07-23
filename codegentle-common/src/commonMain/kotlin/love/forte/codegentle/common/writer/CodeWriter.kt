package love.forte.codegentle.common.writer

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.PackageName
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef

/**
 * A code writer.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleCodeWriterImplementation::class)
public interface CodeWriter {
    public val strategy: Strategy
    public val indentValue: String

    // out?
    // public val staticImportClassNames: Set<ClassName>
    public val staticImports: Set<String> // String? or a MemberName?
    public val alwaysQualify: Set<String>

    public fun indent(levels: Int = 1)

    public fun unindent(levels: Int = 1)

    // comments and javadocs

    public fun emitComment(comment: CodeValue, vararg options: CodeValueEmitOption)

    public fun emitDoc(doc: CodeValue, vararg options: CodeValueEmitOption)

    public fun emit(code: CodeValue, vararg options: CodeValueEmitOption)

    public fun emit(typeName: TypeName, vararg options: TypeNameEmitOption)

    public fun emit(typeRef: TypeRef<*>, vararg options: TypeRefEmitOption)

    public fun emit(annotationRef: AnnotationRef, vararg options: AnnotationRefEmitOption)

    public fun emit(s: String)

    public fun emitNewLine(s: String? = null) {
        if (s != null) {
            emit(s + strategy.newline())
        } else {
            emit(strategy.newline())
        }
    }

    // TODO imports
    /**
     * Key: the identifier
     * Value: the import.
     */
    // public val imports: Map<String, Import>

    public companion object {
        public const val DEFAULT_COLUMN_LIMIT: Int = Int.MAX_VALUE
        public const val DEFAULT_INDENT: String = "    "
    }
}

public inline fun <C : CodeWriter> C.withIndent(
    levels: Int = 1,
    block: C.() -> Unit
) {
    indent(levels)
    try {
        block()
    } finally {
        unindent(levels)
    }
}

public inline fun <C : CodeWriter> C.withIndentBlock(
    levels: Int = 1,
    prefix: String = "",
    newLine: Boolean = true,
    block: C.() -> Unit
) {
    if (prefix.isNotEmpty()) {
        emit(prefix)
        emit(" ")
    }
    emit("{")
    if (newLine) {
        emitNewLine()
    }
    indent(levels)
    try {
        block()
    } finally {
        unindent(levels)
        emitNewLine()
        emit("}")
    }
}

public interface Import {
    // TODO

    // import a.b.c.d.$name1.$name2;
    // import static a.b.c.d.$name1.$name2;
    public val packageName: PackageName?

    public val names: List<String>

    /**
     * The import's identifier.
     */
    public val identifier: String
}
