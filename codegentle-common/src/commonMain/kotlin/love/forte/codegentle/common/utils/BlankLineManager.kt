package love.forte.codegentle.common.utils

import love.forte.codegentle.common.InternalCommonCodeGentleApi
import love.forte.codegentle.common.writer.CodeWriter

@InternalCommonCodeGentleApi
public class BlankLineManager(public val codeWriter: CodeWriter, initial: Boolean = false) {
    public var blankLineRequired: Boolean = initial

    public fun clear() {
        blankLineRequired = false
    }

    public fun required() {
        blankLineRequired = true
    }

    public inline fun withRequirement(block: () -> Unit) {
        if (blankLineRequired) {
            codeWriter.emitNewLine()
        }
        block()
        required()
    }
}
