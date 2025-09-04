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


@InternalCodePointApi
public actual fun CodePoint.isLowerCase(): Boolean {
    // 直接使用Character.isLowerCase的行为逻辑
    // 这确保与Java标准库行为一致
    return if (code <= Char.MAX_VALUE.code) {
        // 对于BMP范围内的码点，使用Kotlin的Char.isLowerCase()
        Char(code).isLowerCase()
    } else {
        // 对于补充码点，遵循Java的Character.isLowerCase(int)行为
        // Java对补充码点的处理主要基于Unicode的一般类别属性
        when (category()) {
            CharCategory.LOWERCASE_LETTER -> true
            else -> false
        }
    }
}

@InternalCodePointApi
public actual fun CodePoint.isUpperCase(): Boolean {
    // 直接使用Character.isUpperCase的行为逻辑
    return if (code <= Char.MAX_VALUE.code) {
        // 对于BMP范围内的码点，使用Kotlin的Char.isUpperCase()
        Char(code).isUpperCase()
    } else {
        // 对于补充码点，遵循Java的Character.isUpperCase(int)行为
        when (category()) {
            CharCategory.UPPERCASE_LETTER -> true
            else -> false
        }
    }
}

@InternalCodePointApi
public actual fun CodePoint.charCount(): Int = charCountCommon()

@InternalCodePointApi
public actual fun StringBuilder.appendCodePoint(codePoint: CodePoint): StringBuilder =
    appendCodePointCommon(codePoint)
