package love.forte.codegentle.common.code

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
         */
        @CodePartFactory
        public fun string(value: String?): CodeArgumentPart = CodeArgumentPart.Str(value)

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
    public class Name internal constructor(public val name: String?) : CodeArgumentPart() {

        internal constructor(name: Any?) : this(argToName(name))

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

            return true
        }

        override fun hashCode(): Int {
            return name?.hashCode() ?: 0
        }

        override fun toString(): String {
            return "Name(name=$name)"
        }
    }

    /**
     * Emit a `string`, wraps it with double quotes, and emits
     *  that. For example, `6" sandwich` is emitted `"6\" sandwich"`.
     */
    public class Str(public val value: String?) : CodeArgumentPart() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Str) return false

            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            return value?.hashCode() ?: 0
        }

        override fun toString(): String {
            return "Str(value=$value)"
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
