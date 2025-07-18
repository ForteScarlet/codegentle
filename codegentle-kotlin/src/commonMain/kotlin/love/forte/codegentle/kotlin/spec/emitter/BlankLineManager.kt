package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

internal data class BlankLineManager(
    val codeWriter: KotlinCodeWriter
) {
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
