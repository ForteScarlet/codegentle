package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.PackageNames

/**
 * @see PackageNames
 * @see KotlinAnnotationNames
 */
public object KotlinClassNames {

    /**
     * The Unit type in Kotlin
     */
    public val UNIT: ClassName = ClassName(PackageNames.KOTLIN, "Unit")

    /**
     * The Nothing type in Kotlin
     */
    public val NOTHING: ClassName = ClassName(PackageNames.KOTLIN, "Nothing")

    // Basic types
    /**
     * The Boolean primitive type in Kotlin
     */
    public val BOOLEAN: ClassName = ClassName(PackageNames.KOTLIN, "Boolean")

    /**
     * The Byte primitive type in Kotlin
     */
    public val BYTE: ClassName = ClassName(PackageNames.KOTLIN, "Byte")

    /**
     * The Short primitive type in Kotlin
     */
    public val SHORT: ClassName = ClassName(PackageNames.KOTLIN, "Short")

    /**
     * The Int primitive type in Kotlin
     */
    public val INT: ClassName = ClassName(PackageNames.KOTLIN, "Int")

    /**
     * The Long primitive type in Kotlin
     */
    public val LONG: ClassName = ClassName(PackageNames.KOTLIN, "Long")

    /**
     * The Float primitive type in Kotlin
     */
    public val FLOAT: ClassName = ClassName(PackageNames.KOTLIN, "Float")

    /**
     * The Double primitive type in Kotlin
     */
    public val DOUBLE: ClassName = ClassName(PackageNames.KOTLIN, "Double")

    /**
     * The String type in Kotlin
     */
    public val STRING: ClassName = ClassName(PackageNames.KOTLIN, "String")

    /**
     * The Char primitive type in Kotlin
     */
    public val CHAR: ClassName = ClassName(PackageNames.KOTLIN, "Char")

    // Collection types
    /**
     * The immutable List interface in Kotlin
     */
    public val LIST: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "List")

    /**
     * The immutable Set interface in Kotlin
     */
    public val SET: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "Set")

    /**
     * The immutable Map interface in Kotlin
     */
    public val MAP: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "Map")

    /**
     * The immutable Collection interface in Kotlin
     */
    public val COLLECTION: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "Collection")

    /**
     * The Iterable interface in Kotlin
     */
    public val ITERABLE: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "Iterable")

    // Mutable collection types
    /**
     * The mutable List interface in Kotlin
     */
    public val MUTABLE_LIST: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "MutableList")

    /**
     * The mutable Set interface in Kotlin
     */
    public val MUTABLE_SET: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "MutableSet")

    /**
     * The mutable Map interface in Kotlin
     */
    public val MUTABLE_MAP: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "MutableMap")

    /**
     * The mutable Collection interface in Kotlin
     */
    public val MUTABLE_COLLECTION: ClassName = ClassName(PackageNames.KOTLIN_COLLECTIONS, "MutableCollection")

    /**
     * The Array class in Kotlin
     */
    public val ARRAY: ClassName = ClassName(PackageNames.KOTLIN, "Array")
}

