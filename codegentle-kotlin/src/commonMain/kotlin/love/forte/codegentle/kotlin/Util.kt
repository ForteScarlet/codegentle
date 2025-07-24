package love.forte.codegentle.kotlin

import love.forte.codegentle.common.appendCharacterLiteral

internal fun String?.stringLiteralWithQuotes(indent: String): String {
    if (this == null) return "null"
    val result = StringBuilder(this.length + 32)
    result.append('"')
    for ((i, c) in this.withIndex()) {
        when (c) {
            '"' -> result.append("\\\"")
            '\\' -> result.append("\\\\")
            // '\n' -> result.append("\\n\"\n$indent$indent+ \"")
            '\n' -> {
                if (i != this.lastIndex) {
                    result.append("\\n\"\n").append(indent).append(indent).append("+ \"")
                } else {
                    result.append("\\n")
                }
            }

            '\r' -> result.append("\\r")
            '\t' -> result.append("\\t")
            else -> result.appendCharacterLiteralWithoutSingleQuotes(c)
        }
    }
    result.append('"')
    return result.toString()
}

internal fun Appendable.appendCharacterLiteralWithoutSingleQuotes(c: Char) {
    if (!appendCharacterLiteral(c)) {
        when (c) {
            '$' -> append("\\$")
            '\u000c' -> append("\\u000c") // \f \u000c: form feed (FF)
            else -> append(c)
        }
    }
}
