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
package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.PackageNames

/**
 * @see PackageNames
 * @see KotlinAnnotationNames
 */
public object KotlinClassNames {

    /**
     * The [Unit] type in Kotlin
     */
    public val UNIT: ClassName = ClassName(PackageNames.KOTLIN, "Unit")

    /**
     * The [Nothing] type in Kotlin
     */
    public val NOTHING: ClassName = ClassName(PackageNames.KOTLIN, "Nothing")

    /**
     * The [Any] type in Kotlin
     */
    public val ANY: ClassName = ClassName(PackageNames.KOTLIN, "Any")

    // Basic types
    /**
     * The [Boolean] primitive type in Kotlin
     */
    public val BOOLEAN: ClassName = ClassName(PackageNames.KOTLIN, "Boolean")

    /**
     * The [Byte] primitive type in Kotlin
     */
    public val BYTE: ClassName = ClassName(PackageNames.KOTLIN, "Byte")

    /**
     * The [Short] primitive type in Kotlin
     */
    public val SHORT: ClassName = ClassName(PackageNames.KOTLIN, "Short")

    /**
     * The [Int] primitive type in Kotlin
     */
    public val INT: ClassName = ClassName(PackageNames.KOTLIN, "Int")

    /**
     * The [Long] primitive type in Kotlin
     */
    public val LONG: ClassName = ClassName(PackageNames.KOTLIN, "Long")

    /**
     * The [Float] primitive type in Kotlin
     */
    public val FLOAT: ClassName = ClassName(PackageNames.KOTLIN, "Float")

    /**
     * The [Double] primitive type in Kotlin
     */
    public val DOUBLE: ClassName = ClassName(PackageNames.KOTLIN, "Double")

    /**
     * The [String] type in Kotlin
     */
    public val STRING: ClassName = ClassName(PackageNames.KOTLIN, "String")

    /**
     * The [Char] primitive type in Kotlin
     */
    public val CHAR: ClassName = ClassName(PackageNames.KOTLIN, "Char")

    // Collection types
    /**
     * The immutable [List] interface in Kotlin
     */
    public val LIST: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "List")

    /**
     * The immutable [Set] interface in Kotlin
     */
    public val SET: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "Set")

    /**
     * The immutable [Map] interface in Kotlin
     */
    public val MAP: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "Map")

    /**
     * The immutable [Collection] interface in Kotlin
     */
    public val COLLECTION: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "Collection")

    /**
     * The [Iterable] interface in Kotlin
     */
    public val ITERABLE: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "Iterable")

    // Mutable collection types
    /**
     * The [MutableList] interface in Kotlin
     */
    public val MUTABLE_LIST: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "MutableList")

    /**
     * The [MutableSet] interface in Kotlin
     */
    public val MUTABLE_SET: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "MutableSet")

    /**
     * The [MutableMap] interface in Kotlin
     */
    public val MUTABLE_MAP: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "MutableMap")

    /**
     * The [MutableCollection] interface in Kotlin
     */
    public val MUTABLE_COLLECTION: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "MutableCollection")

    /**
     * The [Array] class in Kotlin
     */
    public val ARRAY: ClassName = ClassName(PackageNames.KOTLIN, "Array")
}

