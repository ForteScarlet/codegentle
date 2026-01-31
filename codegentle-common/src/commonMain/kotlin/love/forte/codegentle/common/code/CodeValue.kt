/*
 * Copyright (C) 2025-2026 Forte Scarlet
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

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeValue.Companion.builder
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
    return builder(format).also(block).build()
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
    return builder().also(block).build()
}

// Builders


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
    // TODO impl CodeCollector?
    private val parts = mutableListOf<CodePart>()

    internal fun addParts(parts: List<CodePart>): CodeValueBuilder = apply {
        this.parts.addAll(parts)
    }

    public fun addCode(codeValue: CodeValue): CodeValueBuilder = apply {
        parts.addAll(codeValue.parts)
    }

    public fun addCode(format: String): CodeValueBuilder = apply {
        parts.add(CodePart.simple(format))
    }

    public fun addCode(format: String, vararg argumentParts: CodeArgumentPart): CodeValueBuilder = apply {
        addParts(builder(format).addValues(*argumentParts).parts())
    }

    public fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): CodeValueBuilder = apply {
        parts.add(CodePart.statementBegin())
        addCode(CodeValue(format, *argumentParts))
        parts.add(CodePart.statementEnd())
    }

    public fun addStatement(codeValue: CodeValue): CodeValueBuilder = apply {
        parts.add(CodePart.statementBegin())
        addCode(codeValue)
        parts.add(CodePart.statementEnd())
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

public inline fun CodeValueBuilder.addCode(
    format: String,
    block: CodeValueSingleFormatBuilder.() -> Unit
): CodeValueBuilder =
    addCode(builder(format).also(block).build())

public inline fun CodeValueBuilder.addStatement(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder = apply {
    addStatement(CodeValue(format, block))
}

context(builder: CodeValueBuilder)
public inline operator fun String.invoke(block: CodeValueSingleFormatBuilder.() -> Unit): CodeValueBuilder =
    builder.addCode(this, block)
