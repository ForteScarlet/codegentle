/*
 * Copyright (C) 2005-2026 Forte Scarlet
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
package love.forte.codegentle.java.internal

import love.forte.codegentle.common.codepoint.charCount
import love.forte.codegentle.common.codepoint.codePointAt
import love.forte.codegentle.java.codepoint.isJavaIdentifierPart
import love.forte.codegentle.java.codepoint.isJavaIdentifierStart

// See javax.lang.model.SourceVersion.java

private val keywords = setOf(
    "abstract", "continue", "for", "new", "switch",
    "assert", "default", "if", "package", "synchronized",
    "boolean", "do", "goto", "private", "this",
    "break", "double", "implements", "protected", "throw",
    "byte", "else", "import", "public", "throws",
    "case", "enum", "instanceof", "return", "transient",
    "catch", "extends", "int", "short", "try",
    "char", "final", "interface", "static", "void",
    "class", "finally", "long", "strictfp", "volatile",
    "const", "float", "native", "super", "while",
    // literals
    "null", "true", "false"
)

internal fun CharSequence.isSourceKeyword() = toString() in keywords

internal fun CharSequence.isSourceName(): Boolean {
    val id = toString()

    for (s in id.split('.')) {
        if (!s.isSourceIdentifier() || s.isSourceKeyword()) return false
    }
    return true
}

internal fun CharSequence.isSourceIdentifier(): Boolean {
    val id: String = toString()

    if (id.isEmpty()) {
        return false
    }
    var cp = id.codePointAt(0)
    if (!cp.isJavaIdentifierStart()) {
        return false
    }

    var i: Int = cp.charCount()
    while (i < id.length) {
        cp = id.codePointAt(i)

        if (!cp.isJavaIdentifierPart()) {
            return false
        }

        i += cp.charCount()
    }

    return true
}

