package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.writer.CodeWriter

internal class BlankLineManager(val codeWriter: CodeWriter) {
    var blankLineRequired = false

    fun required() {
        blankLineRequired = true
    }

    inline fun withRequirement(block: () -> Unit) {
        if (blankLineRequired) {
            codeWriter.emitNewLine()
        }
        block()
        required()
    }
}
