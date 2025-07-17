package love.forte.codegentle.common.code

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.internal.CodeValueImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 *
 * @author ForteScarlet
 */
public interface CodeValue {
    public val parts: List<CodePart>

    public companion object {
        internal val EMPTY = CodeValueImpl(emptyList())

        @JvmStatic
        public fun builder(format: String): CodeValueSingleFormatBuilder = CodeValueSingleFormatBuilder(format)

        @JvmStatic
        public fun builder(): CodeValueBuilder = CodeValueBuilder()
    }
}

public fun CodeValue.isEmpty(): Boolean = parts.isEmpty()

public fun CodeValue.isNotEmpty(): Boolean = !isEmpty()


public operator fun CodeValue.plus(codeValue: CodeValue): CodeValue {
    return CodeValue(parts + codeValue.parts)
}

public typealias CodeValueSingleFormatBuilderDsl = CodeValueSingleFormatBuilder.() -> Unit
public typealias CodeValueBuilderDsl = CodeValueBuilder.() -> Unit

/**
 * Creates and returns an empty instance of [CodeValue].
 *
 * @return An empty [CodeValue] instance.
 */
@JsName("emptyCodeValue")
public fun CodeValue(): CodeValue = CodeValue.EMPTY

public fun CodeValue(parts: List<CodePart>): CodeValue {
    return CodeValueImpl(parts.toList())
}

public fun CodeValue(part: CodePart): CodeValue {
    return CodeValueImpl(listOf(part))
}

public inline fun CodeValue(format: String, block: CodeValueSingleFormatBuilderDsl = {}): CodeValue {
    return CodeValue.builder(format).also(block).build()
}

public fun CodeValue(format: String, argumentPart: CodeArgumentPart): CodeValue {
    return CodeValue(format) {
        addValue(argumentPart)
    }
}

public fun CodeValue(format: String, vararg argumentParts: CodeArgumentPart): CodeValue {
    return CodeValue(format) {
        addValues(*argumentParts)
    }
}

public fun CodeValue(format: String, argumentParts: Iterable<CodeArgumentPart>): CodeValue {
    return CodeValue(format) {
        addValues(argumentParts)
    }
}

public inline fun CodeValue(block: CodeValueBuilderDsl): CodeValue {
    return CodeValue.builder().also(block).build()
}

// Builders

public inline fun CodeValueBuilder.addStatement(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder = apply {
    addStatement(CodeValue(format, block))
}


public inline fun CodeValueBuilder.beginControlFlow(
    controlFlow: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder =
    apply {
        add("$controlFlow {\n") { block() }
        indent()
    }

public inline fun CodeValueBuilder.nextControlFlow(
    controlFlow: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder =
    apply {
        unindent()
        add("} $controlFlow {\n") { block() }
        indent()
    }

/**
 * @param controlFlow the optional control flow construct and its code, such as
 * `"while(foo == 20)"`. Only used for `"do/while"` control flows.
 */
public inline fun CodeValueBuilder.endControlFlow(
    controlFlow: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder =
    apply {
        unindent()
        add("} $controlFlow;\n") { block() }
    }

/**
 * ```Kotlin
 * builder {
 *  "%V, %V" {
 *    value(...) // for 1st `%V`
 *    value(...) // for 2nd `%V`
 *  }
 *  // Same as
 *  add("%V, %V") {
 *    value(...) // for 1st `%V`
 *    value(...) // for 2nd `%V`
 *  }
 * }
 * ```
 */
public class CodeValueBuilder internal constructor() : BuilderDsl {
    private val parts = mutableListOf<CodePart>()

    @PublishedApi
    internal fun addParts(parts: List<CodePart>): CodeValueBuilder = apply {
        this.parts.addAll(parts)
    }

    public fun add(codeValue: CodeValue): CodeValueBuilder = apply {
        parts.addAll(codeValue.parts)
    }

    public fun add(format: String): CodeValueBuilder = apply {
        parts.add(CodePart.simple(format))
    }

    public fun add(format: String, vararg argumentParts: CodeArgumentPart): CodeValueBuilder = apply {
        addParts(CodeValue.Companion.builder(format).addValues(*argumentParts).parts())
    }

    public inline fun add(format: String, block: CodeValueSingleFormatBuilder.() -> Unit): CodeValueBuilder =
        apply {
            addParts(CodeValue.Companion.builder(format).also(block).parts())
        }

    public inline operator fun String.invoke(block: CodeValueSingleFormatBuilder.() -> Unit): CodeValueBuilder =
        add(this, block)

    public fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): CodeValueBuilder = apply {
        parts.add(CodePart.statementBegin())
        add(CodeValue(format, *argumentParts))
        add(";\n")
        parts.add(CodePart.statementEnd())
    }

    public fun addStatement(codeValue: CodeValue): CodeValueBuilder = apply {
        parts.add(CodePart.statementBegin())
        add(codeValue)
        add(";\n")
        parts.add(CodePart.statementEnd())
    }

    public fun beginControlFlow(controlFlow: String, vararg argumentParts: CodeArgumentPart): CodeValueBuilder =
        apply {
            add("$controlFlow {\n", *argumentParts)
            indent()
        }

    public fun nextControlFlow(controlFlow: String, vararg argumentParts: CodeArgumentPart): CodeValueBuilder =
        apply {
            unindent()
            add("} $controlFlow {\n", *argumentParts)
            indent()
        }

    public fun endControlFlow(): CodeValueBuilder = apply {
        unindent()
        add("}\n")
    }

    /**
     * @param controlFlow the optional control flow construct and its code, such as
     * `"while(foo == 20)"`. Only used for `"do/while"` control flows.
     */
    public fun endControlFlow(controlFlow: String, vararg argumentParts: CodeArgumentPart): CodeValueBuilder =
        apply {
            unindent()
            add("} $controlFlow;\n", *argumentParts)
        }

    public fun indent(): CodeValueBuilder = apply {
        parts.add(CodePart.indent())
    }

    public fun unindent(): CodeValueBuilder = apply {
        parts.add(CodePart.unindent())
    }

    public fun clear(): CodeValueBuilder = apply {
        parts.clear()
    }

    public fun build(): CodeValue = CodeValueImpl(parts)
}

/**
 * The Builder for [CodeValue] with single [format] target.
 *
 * ```Kotlin
 * builder("%V %V = %V;") {
 *   value(...) // For 1st `%V`
 *   value(...) // For 2nd `%V`
 *   value(...) // For 3rd `%V`
 * }.build()
 * ```
 *
 */
public class CodeValueSingleFormatBuilder internal constructor(public val format: String) : BuilderDsl {
    private val arguments = mutableListOf<CodeArgumentPart>()

    /**
     * Add a [CodeArgumentPart] for next argument placeholder.
     */
    public fun addValue(argument: CodeArgumentPart): CodeValueSingleFormatBuilder = apply {
        arguments.add(argument)
    }

    /**
     * Add some [CodeArgumentPart]s for next argument placeholder.
     */
    public fun addValues(vararg arguments: CodeArgumentPart): CodeValueSingleFormatBuilder = apply {
        this.arguments.addAll(arguments)
    }

    /**
     * Add some [CodeArgumentPart]s for next argument placeholder.
     */
    public fun addValues(arguments: Iterable<CodeArgumentPart>): CodeValueSingleFormatBuilder = apply {
        this.arguments.addAll(arguments)
    }

    @PublishedApi
    internal fun parts(): List<CodePart> {
        if (arguments.isEmpty()) {
            return listOf(CodePart.simple(format))
        }

        val argumentsStack = ArrayDeque(arguments)
        val parts = mutableListOf<CodePart>()

        var last = false

        var i = 0
        var argumentCount = 0
        for (simplePart in format.splitToSequence(CodePart.PLACEHOLDER)) {
            if (simplePart.isNotEmpty()) {
                parts.add(CodePart.simple(simplePart))
            }
            // remove stack first
            val nextArg = argumentsStack.removeFirstOrNull()

            if (nextArg == null) {
                check(!last) { "Miss argument in index $i" }
                last = true
            } else {
                parts.add(nextArg)
                argumentCount++
            }

            i++
        }

        check(argumentsStack.isEmpty()) { "${argumentsStack.size} redundant argument(s): $argumentsStack" }
        // 如果根据占位符切割，那么 argument 的数量应该和占位符的数量一致，而 i 的数量应当 = argument + 1
        check(i == argumentCount + 1) { "redundant argument: ${parts.last { it is CodeArgumentPart }}" }

        return parts
    }

    public fun build(): CodeValue = CodeValueImpl(parts())
}
