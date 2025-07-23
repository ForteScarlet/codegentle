package love.forte.codegentle.java.ref

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.emitLiteral
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.java.emitTo
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emit

internal fun AnnotationRef.emitTo(codeWriter: JavaCodeWriter) {
    codeWriter.emitJavaAnnotation(typeName, members)
}


private fun JavaCodeWriter.emitJavaAnnotation(
    type: TypeName,
    members: Map<String, List<CodeValue>>
) {
    // val whitespace = if (inline) "" else "\n"
    val memberSeparator = ", " // member: inline only //if (inline) ", " else ",\n"

    if (members.isEmpty()) {
        // @Singleton
        emit("@%V") { emitType(type) }
    } else if (members.size == 1 && members.containsKey("value")) {
        // @Named("foo")
        emit("@%V(") { emitType(type) }
        // Always inline between values.
        emitAnnotationValues(memberSeparator, members["value"]!!)
        emit(")")
    } else {
        // @Column(name = "updated_at", nullable = false)
        emit("@%V(") { emitType(type) }
        withIndent(2) {
            val i = members.entries.iterator()
            while (i.hasNext()) {
                val entry = i.next()
                emit("%V = ") { this.emitLiteral(entry.key) }
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
    values: List<CodeValue>
) {
    if (values.size == 1) {
        indent(2)
        values[0].emitTo(this)
        unindent(2)
        return
    }

    emit("{")
    var first = true
    for (codeBlock in values) {
        if (!first) emit(memberSeparator)
        codeBlock.emitTo(this)
        first = false
    }
    emit("}")
}
