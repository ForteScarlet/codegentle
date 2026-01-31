/*
 * Copyright (C) 2024-2026 Forte Scarlet
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
package love.forte.codegentle.common.writer

@InternalWriterApi
public class LineWrapper private constructor(
    private val out: RecordingAppendable,
    private val indent: String,
    private val columnLimit: Int,
) : AutoCloseable, Appendable {
    private var closed: Boolean = false

    /**
     * Characters written since the last wrapping space that haven't yet been flushed.
     */
    private val buffer = StringBuilder()

    /**
     * The number of characters since the most recent newline. Includes both out and the buffer.
     */
    private var column: Int = 0

    /**
     * `-1` if we have no buffering; otherwise the number of `indent`s to write after wrapping.
     */
    private var indentLevel = -1

    /**
     * Null if we have no buffering; otherwise the type to pass to the next call to [.flush].
     */
    private var nextFlush: FlushType? = null

    public val lastChar: Char?
        get() = out.lastChar

    public val lastNonBlankChar: Char?
        get() = out.lastNonBlankChar

    public fun startRecordLastNonBlankChar() {
        out.recordingLastNonBlankChar = true
    }

    public fun stopRecordLastNonBlankChar() {
        out.recordingLastNonBlankChar = false
        out.lastNonBlankChar = null
    }

    private fun checkClose() {
        check(!closed) { "closed" }
    }

    override fun append(value: Char): Appendable {
        append0(value.toString())
        return this
    }

    override fun append(value: CharSequence?): Appendable {
        append0(value ?: "null")
        return this
    }

    override fun append(
        value: CharSequence?,
        startIndex: Int,
        endIndex: Int
    ): Appendable {
        append0((value ?: "").substring(startIndex, endIndex))
        return this
    }

    internal fun append0(value: CharSequence) {
        checkClose()

        nextFlush?.also { nextFlush ->
            val nextNewline: Int = value.indexOf('\n')

            // If s doesn't cause the current line to cross the limit, buffer it and return. We'll decide
            // whether or not we have to wrap it later.
            if (nextNewline == -1 && column + value.length <= columnLimit) {
                buffer.append(value)
                column += value.length
                return
            }

            // Wrap if appending s would overflow the current line.
            val wrap = nextNewline == -1 || column + nextNewline > columnLimit
            flush(if (wrap) FlushType.WRAP else nextFlush)
        }

        out.append(value)
        val lastNewline: Int = value.lastIndexOf('\n')
        column = if (lastNewline != -1) {
            value.length - lastNewline - 1
        } else {
            column + value.length
        }
    }


    /**
     * Emit either a space or a newline character.
     */
    public fun wrappingSpace(indentLevel: Int) {
        checkClose()

        nextFlush?.also { flush(it) }
        column++ // Increment the column even though the space is deferred to next call to flush().
        this.nextFlush = FlushType.SPACE
        this.indentLevel = indentLevel
    }

    /**
     * Emit a newline character if the line will exceed it's limit, otherwise do nothing.
     */
    public fun zeroWidthSpace(indentLevel: Int) {
        checkClose()

        if (column == 0) return

        nextFlush?.also { flush(it) }
        this.nextFlush = FlushType.EMPTY
        this.indentLevel = indentLevel
    }

    private fun flush(flushType: FlushType) {
        when (flushType) {
            FlushType.WRAP -> {
                out.append('\n')
                var i = 0
                while (i < indentLevel) {
                    out.append(indent)
                    i++
                }
                column = indentLevel * indent.length
                column += buffer.length
            }

            FlushType.SPACE -> out.append(' ')
            FlushType.EMPTY -> {}
        }

        out.append(buffer)
        buffer.deleteRange(0, buffer.length)
        indentLevel = -1
        nextFlush = null
    }


    override fun close() {
        nextFlush?.also { flush(it) }
        closed = true
    }

    public companion object {
        @InternalWriterApi
        public fun create(out: Appendable, indent: String, columnLimit: Int): LineWrapper {
            return LineWrapper(RecordingAppendable(out), indent, columnLimit)
        }
    }
}

private enum class FlushType {
    WRAP, SPACE, EMPTY
}

private class RecordingAppendable(
    private val delegate: Appendable,
) : Appendable {
    var lastChar: Char? = null
    var lastNonBlankChar: Char? = null
    var recordingLastNonBlankChar: Boolean = false

    override fun append(value: Char): Appendable {
        lastChar = value
        if (recordingLastNonBlankChar && !value.isWhitespace()) {
            lastNonBlankChar = value
        }
        delegate.append(value)
        return this
    }

    private fun appendNull(): Appendable {
        // "null"
        lastChar = 'l'
        if (recordingLastNonBlankChar) {
            lastNonBlankChar = lastChar
        }
        delegate.append("null")
        return this
    }

    override fun append(value: CharSequence?): Appendable {
        if (value == null) return appendNull()

        if (value.isEmpty()) return this

        lastChar = value[value.lastIndex]
        if (recordingLastNonBlankChar) {
            value.lastOrNull { !it.isWhitespace() }?.also { lastNonBlankChar = it }
        }

        return delegate.append(value)
    }

    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): Appendable {
        if (value == null) {
            return append("null", startIndex, endIndex)
        }

        if (startIndex == endIndex) {
            // empty, return
            return this
        }

        require(startIndex >= 0) {
            "Start index cannot be negative: $startIndex"
        }

        require(startIndex <= endIndex) {
            "End index out of range, start index must <= end index, " +
                "but: $startIndex..$endIndex"
        }

        require(value.length >= endIndex) {
            "End index out of range: $startIndex..$endIndex, but value.length is ${value.length}"
        }

        lastChar = value[endIndex - 1]
        if (recordingLastNonBlankChar) {
            for (index in endIndex - 1 downTo startIndex) {
                val char = value[index]
                if (!char.isWhitespace()) {
                    lastNonBlankChar = char
                    break
                }
            }
        }

        delegate.append(value, startIndex, endIndex)

        return this
    }
}
