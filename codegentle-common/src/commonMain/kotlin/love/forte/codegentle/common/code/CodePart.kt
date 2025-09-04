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
package love.forte.codegentle.common.code

import love.forte.codegentle.common.code.CodeArgumentPart.ControlFlow.Position
import love.forte.codegentle.common.naming.Named
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef

/**
 * Mark a CodePart factory function in its companion object
 * or a top-level function with a companion receiver parameter.
 */
@Retention(AnnotationRetention.SOURCE)
public annotation class CodePartFactory

/**
 * A part of [CodeValue].
 *
 * @see CodeSimplePart
 * @see CodeArgumentPart
 */
public sealed class CodePart {
    public companion object {
        public const val PLACEHOLDER: String = "%V"

        /**
         * A simple part, without any process.
         */
        public fun simple(value: String): CodeSimplePart = CodeSimplePart(value)

        /**
         * Skip this `%V`, Just write `"%V"` itself.
         */
        @CodePartFactory
        public fun skip(): CodeArgumentPart = CodeArgumentPart.Skip

        /**
         * Emits a `literal`.
         */
        @CodePartFactory
        public fun literal(value: Any?): CodeArgumentPart = CodeArgumentPart.Literal(value)

        /**
         * Emits a `name`.
         */
        @CodePartFactory
        public fun name(nameValue: Any?): CodeArgumentPart = CodeArgumentPart.Name(nameValue)

        /**
         * Emit a `string`, wraps it with double quotes, and emits
         *  that. For example, `6" sandwich` is emitted `"6\" sandwich"`.
         *
         *  @param handleSpecialCharacter If true, will escape some characters.
         *  See [CodeArgumentPart.Str.handleSpecialCharacter], default is `true`.
         */
        @CodePartFactory
        public fun string(
            value: String?,
            handleSpecialCharacter: Boolean
        ): CodeArgumentPart = CodeArgumentPart.Str(value, handleSpecialCharacter)

        /**
         * Emit a `string`, wraps it with double quotes, and emits
         *  that. For example, `6" sandwich` is emitted `"6\" sandwich"`.
         */
        @CodePartFactory
        public fun string(value: String?): CodeArgumentPart = string(value, true)

        /**
         * Emits a `type` reference.
         */
        @CodePartFactory
        public fun type(type: TypeName): CodeArgumentPart = CodeArgumentPart.Type(type)

        /**
         * Emits a `type` reference.
         */
        @CodePartFactory
        public fun type(type: TypeRef<*>): CodeArgumentPart = CodeArgumentPart.TypeRef(type)


        /**
         * Increases the indentation level.
         */
        @CodePartFactory
        public fun indent(levels: Int = 1): CodeArgumentPart = CodeArgumentPart.Indent(levels)

        /**
         * Decreases the indentation level.
         */
        @CodePartFactory
        public fun unindent(levels: Int = 1): CodeArgumentPart = CodeArgumentPart.Unindent(levels)


        /**
         * Begins a statement.
         * For multiline statements, every line after the first line
         * is double-indented.
         */
        @CodePartFactory
        public fun statementBegin(): CodeArgumentPart = CodeArgumentPart.StatementBegin

        /**
         * Ends a statement.
         */
        @CodePartFactory
        public fun statementEnd(): CodeArgumentPart = CodeArgumentPart.StatementEnd

        // TODO wrappingSpace 和 zeroWidthSpace 的 limit 可配置
        /**
         * Emits a space or a newline, depending on its position on the line. This prefers
         * to wrap lines before 100 columns.
         */
        @CodePartFactory
        public fun wrappingSpace(): CodeArgumentPart = CodeArgumentPart.WrappingSpace

        /**
         * Acts as a zero-width space. This prefers to wrap lines before 100 columns.
         */
        @CodePartFactory
        public fun zeroWidthSpace(): CodeArgumentPart = CodeArgumentPart.ZeroWidthSpace

        /**
         * Emits a newline character using the strategy's newline value.
         */
        @CodePartFactory
        public fun newline(): CodeArgumentPart = CodeArgumentPart.Newline

        @CodePartFactory
        public fun beginControlFlow(): CodeArgumentPart =
            CodeArgumentPart.ControlFlow(Position.BEGIN, null)

        @CodePartFactory
        public fun beginControlFlow(codeValue: CodeValue): CodeArgumentPart =
            CodeArgumentPart.ControlFlow(Position.BEGIN, codeValue)

        @CodePartFactory
        public fun nextControlFlow(): CodeArgumentPart =
            CodeArgumentPart.ControlFlow(Position.NEXT, null)

        @CodePartFactory
        public fun nextControlFlow(codeValue: CodeValue): CodeArgumentPart =
            CodeArgumentPart.ControlFlow(Position.NEXT, codeValue)

        @CodePartFactory
        public fun endControlFlow(): CodeArgumentPart =
            CodeArgumentPart.ControlFlow(Position.END, null)

        @CodePartFactory
        public fun endControlFlow(codeValue: CodeValue): CodeArgumentPart =
            CodeArgumentPart.ControlFlow(Position.END, codeValue)

        @CodePartFactory
        public fun otherCodeValue(value: CodeValue): CodeArgumentPart =
            CodeArgumentPart.OtherCodeValue(value)
    }
}

/**
 * @see CodePart.Companion
 */
public sealed class CodeArgumentPart : CodePart() {
    /**
     * Skip this `%V`, Just write `"%V"` itself.
     */
    public data object Skip : CodeArgumentPart()

    /**
     * Emits a `literal` value with no escaping.
     */
    public class Literal internal constructor(public val value: Any?) : CodeArgumentPart() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Literal) return false

            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            return value?.hashCode() ?: 0
        }

        override fun toString(): String {
            return "Literal(value=$value)"
        }
    }

    /**
     * Emits a `name`.
     */
    public class Name internal constructor(
        public val name: String?,
        public val originalValue: Any? = null
    ) : CodeArgumentPart() {

        internal constructor(name: Any?) : this(argToName(name), name)

        public companion object {
            private fun argToName(o: Any?): String? {
                return when (o) {
                    is CharSequence -> o.toString()
                    is Named -> o.name
                    else -> throw IllegalArgumentException("expected name but was $o")
                }
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Name) return false

            if (name != other.name) return false
            if (originalValue != other.originalValue) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name?.hashCode() ?: 0
            result = 31 * result + (originalValue?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "Name(name=$name, originalValue=$originalValue)"
        }
    }

    /**
     * Emit a `string`, wraps it with double quotes, and emits
     *  that. For example, `6" sandwich` is emitted `"6\" sandwich"`.
     *
     *  @property handleSpecialCharacter If true, will escape some characters.
     *  - Kotlin: the String interpolation `$`.
     */
    public class Str(
        public val value: String?,
        public val handleSpecialCharacter: Boolean
    ) : CodeArgumentPart() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Str) return false

            if (handleSpecialCharacter != other.handleSpecialCharacter) return false
            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            var result = handleSpecialCharacter.hashCode()
            result = 31 * result + (value?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "Str(value=$value, handleSpecialCharacter=$handleSpecialCharacter)"
        }
    }

    /**
     * Emit a `type` reference.
     */
    public class Type internal constructor(public val type: TypeName) : CodeArgumentPart() {

        public companion object {
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Type) return false

            if (type != other.type) return false

            return true
        }

        override fun hashCode(): Int {
            return type.hashCode()
        }

        override fun toString(): String {
            return "Type(type=$type)"
        }
    }

    public class TypeRef internal constructor(public val type: love.forte.codegentle.common.ref.TypeRef<*>) :
        CodeArgumentPart() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Type) return false

            if (type != other.type) return false

            return true
        }

        override fun hashCode(): Int {
            return type.hashCode()
        }

        override fun toString(): String {
            return "Type(type=$type)"
        }
    }

    /**
     * Increases the indentation level.
     */
    public class Indent internal constructor(public val levels: Int = 1) : CodeArgumentPart() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Indent) return false

            if (levels != other.levels) return false

            return true
        }

        override fun hashCode(): Int {
            return levels
        }

        override fun toString(): String {
            return "Indent(levels=$levels)"
        }
    }


    /**
     * Decreases the indentation level.
     */
    public class Unindent internal constructor(public val levels: Int = 1) : CodeArgumentPart() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Unindent) return false

            if (levels != other.levels) return false

            return true
        }

        override fun hashCode(): Int {
            return levels
        }

        override fun toString(): String {
            return "Unindent(levels=$levels)"
        }
    }

    /**
     * Begins a statement.
     */
    public data object StatementBegin : CodeArgumentPart()

    /**
     * Ends a statement.
     */
    public data object StatementEnd : CodeArgumentPart()

    /**
     * Emits a space or a newline, depending on its position on the line.
     */
    public data object WrappingSpace : CodeArgumentPart()

    /**
     * `Acts as a zero-width space.
     */
    public data object ZeroWidthSpace : CodeArgumentPart()

    /**
     * Emits a newline character using the strategy's newline value.
     */
    public data object Newline : CodeArgumentPart()

    /**
     *
     * In Java:
     *
     * ```Java
     * $beginControlFlow("if (foo)") {
     * $codeValue
     * } $nextControlFlow("else") {
     * $codeValue
     * } $endControlFlow();
     * ```
     *
     * e.g.
     *
     * ```Java
     * if (foo) {
     *     bar();
     * } else {
     *     baz();
     * } // no codeValue, no ';', otherwise, ';' added.
     * ```
     *
     * In Kotlin:
     *
     * ```Kotlin
     * $beginControlFlow("if (foo)") {
     * $codeValue
     * } $nextControlFlow("else") {
     * $codeValue
     * } $endControlFlow()
     * ```
     * e.g.
     *
     * ```Kotlin
     * if (foo) {
     *     bar()
     * } else {
     *     baz()
     * }
     * ```
     */
    public class ControlFlow internal constructor(
        public val position: Position,
        public val codeValue: CodeValue?
    ) : CodeArgumentPart() {
        public enum class Position {
            BEGIN, NEXT, END
        }

        override fun toString(): String {
            return "ControlFlow(position=$position, codeValue=$codeValue)"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ControlFlow) return false

            if (position != other.position) return false
            if (codeValue != other.codeValue) return false

            return true
        }

        override fun hashCode(): Int {
            var result = position.hashCode()
            result = 31 * result + (codeValue?.hashCode() ?: 0)
            return result
        }
    }

    /**
     * Other Code Value.
     */
    public class OtherCodeValue internal constructor(public val value: CodeValue) : CodeArgumentPart() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is OtherCodeValue) return false

            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun toString(): String {
            return "OtherCodeValue(value=$value)"
        }
    }
}

public class CodeSimplePart internal constructor(public val value: String) : CodePart() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CodeSimplePart) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "CodeSimplePart(value='$value')"
    }
}

/*
        @CodePartFactory
        public fun nextControlFlow(format: String, vararg arguments: CodeArgumentPart): CodeArgumentPart =
            nextControlFlow(format, arguments.asList())
 */


@CodePartFactory
public fun CodePart.Companion.beginControlFlow(
    format: String,
    arguments: Iterable<CodeArgumentPart>
): CodeArgumentPart =
    beginControlFlow(CodeValue(format, arguments))

@CodePartFactory
public fun CodePart.Companion.beginControlFlow(format: String, vararg arguments: CodeArgumentPart): CodeArgumentPart =
    beginControlFlow(format, arguments.asList())

@CodePartFactory
public inline fun CodePart.Companion.beginControlFlow(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeArgumentPart =
    beginControlFlow(CodeValue(format, block))

@CodePartFactory
public fun CodePart.Companion.nextControlFlow(
    format: String,
    arguments: Iterable<CodeArgumentPart>
): CodeArgumentPart =
    nextControlFlow(CodeValue(format, arguments))

@CodePartFactory
public fun CodePart.Companion.nextControlFlow(format: String, vararg arguments: CodeArgumentPart): CodeArgumentPart =
    nextControlFlow(format, arguments.asList())

@CodePartFactory
public inline fun CodePart.Companion.nextControlFlow(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeArgumentPart =
    nextControlFlow(CodeValue(format, block))

@CodePartFactory
public fun CodePart.Companion.endControlFlow(
    format: String,
    arguments: Iterable<CodeArgumentPart>
): CodeArgumentPart =
    endControlFlow(CodeValue(format, arguments))

@CodePartFactory
public fun CodePart.Companion.endControlFlow(format: String, vararg arguments: CodeArgumentPart): CodeArgumentPart =
    endControlFlow(format, arguments.asList())

@CodePartFactory
public inline fun CodePart.Companion.endControlFlow(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeArgumentPart =
    endControlFlow(CodeValue(format, block))
