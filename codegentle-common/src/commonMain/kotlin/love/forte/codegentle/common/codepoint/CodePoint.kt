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
package love.forte.codegentle.common.codepoint

import kotlin.jvm.JvmInline

/**
 * Represents a Unicode code point.
 *
 * This class is an inline value class, providing a lightweight representation
 * for Unicode code points, stored as an unsigned integer.
 *
 * The `CodePoint` class is for internal use and marked with the `@InternalCodePointApi` annotation.
 * APIs annotated with `@InternalCodePointApi` require opt-in and are not guaranteed to maintain backward compatibility
 * or stability.
 *
 * @property code The Unicode code point value represented as an unsigned integer.
 */
@JvmInline
@InternalCodePointApi
public value class CodePoint internal constructor(public val code: Int) {
    override fun toString(): String = stringValue()
}

@InternalCodePointApi
public fun Char.codePoint(): CodePoint = CodePoint(this.code)

internal expect fun CodePoint.stringValue(): String

@InternalCodePointApi
public expect fun String.codePointAt(index: Int): CodePoint

@InternalCodePointApi
public expect fun CodePoint.isLowerCase(): Boolean

@InternalCodePointApi
public expect fun CodePoint.isUpperCase(): Boolean

@InternalCodePointApi
public expect fun CodePoint.charCount(): Int

@InternalCodePointApi
public expect fun StringBuilder.appendCodePoint(codePoint: CodePoint): StringBuilder

/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
internal fun StringBuilder.appendCodePointCommon(codePoint: CodePoint): StringBuilder {
    // Copied from StringBuilder.kt,
    val code = codePoint.code
    if (code <= Char.MAX_VALUE.code) {
        append(code.toChar())
    } else {
        append(Char.MIN_HIGH_SURROGATE + ((code - 0x10000) shr 10))
        append(Char.MIN_LOW_SURROGATE + (code and 0x3ff))
    }
    return this
}

internal fun CodePoint.charCountCommon(): Int {
    return if (code >= 0x10000) 2 else 1
}

/**
 * Determines the Unicode character category of this code point.
 *
 * This method checks whether the code point falls within the BMP (Basic Multilingual Plane)
 * or is a supplementary code point, and retrieves the appropriate `CharCategory`.
 *
 * @return The Unicode `CharCategory` of this code point, or null if the code point does not have a defined category.
 */
@InternalCodePointApi
public fun CodePoint.category(): CharCategory? {
    return if (code <= Char.MAX_VALUE.code) {
        Char(code).category
    } else {
        // 对于超出 BMP 范围的码点，我们需要特殊处理
        // 这里使用范围检查确定字符类别
        getCategoryForSupplementaryCodePoint(code)
    }
}

/**
 * Determines the Unicode `CharCategory` for a supplementary code point
 * (code points beyond the Basic Multilingual Plane (BMP)).
 *
 * The method evaluates the provided code point against defined ranges
 * based on the Unicode standard and returns the corresponding character category.
 *
 * @param codePoint The supplementary Unicode code point to determine the category for.
 *                  This should be a code point greater than `Char.MAX_VALUE.code`.
 * @return The `CharCategory` corresponding to the given code point, or `UNASSIGNED` if the category is not defined.
 */
private fun getCategoryForSupplementaryCodePoint(codePoint: Int): CharCategory {
    // 确定补充字符（超出 BMP 范围的码点）的 Unicode 类别

    // 这里基于 Unicode 标准的范围检查确定字符类别
    return when {
        // 各种文字系统的字母 (Lo - OTHER_LETTER)
        (codePoint in 0x10000..0x1000B) -> CharCategory.OTHER_LETTER // 线性文字 B 音节
        (codePoint in 0x10080..0x100FA) -> CharCategory.OTHER_LETTER // 线性文字 B 表意文字
        (codePoint in 0x10300..0x1031F) -> CharCategory.OTHER_LETTER // 古意大利文字
        (codePoint in 0x10330..0x1034A) -> CharCategory.OTHER_LETTER // 哥特文字

        // 数学字母数字符号 (Lu - UPPERCASE_LETTER, Ll - LOWERCASE_LETTER, Sm - MATH_SYMBOL)
        (codePoint in 0x1D400..0x1D434) -> CharCategory.UPPERCASE_LETTER // 数学粗体大写字母
        (codePoint in 0x1D435..0x1D467) -> CharCategory.LOWERCASE_LETTER // 数学粗体小写字母

        // 表情符号和图像符号 (So - OTHER_SYMBOL)
        (codePoint in 0x1F000..0x1FFFD) -> CharCategory.OTHER_SYMBOL

        // 如果找不到匹配的范围，默认为 UNASSIGNED
        else -> CharCategory.UNASSIGNED
    }
}
