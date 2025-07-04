package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.PackageName

public object KotlinNames {
    public object Packages {
        /**
         * The standard library package name for Kotlin
         */
        public val KOTLIN: PackageName = PackageName("kotlin")

        /**
         * The collections package name for Kotlin
         */
        public val KOTLIN_COLLECTIONS: PackageName = PackageName("kotlin", "collections")

        /**
         * The IO package name for Kotlin
         */
        public val KOTLIN_IO: PackageName = PackageName("kotlin", "io")

        /**
         * The text processing package name for Kotlin
         */
        public val KOTLIN_TEXT: PackageName = PackageName("kotlin", "text")

        /**
         * The ranges package name for Kotlin
         */
        public val KOTLIN_RANGES: PackageName = PackageName("kotlin", "ranges")

        /**
         * The coroutines package name for Kotlin
         */
        public val KOTLIN_COROUTINES: PackageName = PackageName("kotlin", "coroutines")

        /**
         * The reflection package name for Kotlin
         */
        public val KOTLIN_REFLECT: PackageName = PackageName("kotlin", "reflect")
    }

    public object Classes {
        /**
         * The Unit type in Kotlin
         */
        public val UNIT: ClassName = ClassName(Packages.KOTLIN, "Unit")

        /**
         * The Nothing type in Kotlin
         */
        public val NOTHING: ClassName = ClassName(Packages.KOTLIN, "Nothing")

        // Basic types
        /**
         * The Boolean primitive type in Kotlin
         */
        public val BOOLEAN: ClassName = ClassName(Packages.KOTLIN, "Boolean")

        /**
         * The Byte primitive type in Kotlin
         */
        public val BYTE: ClassName = ClassName(Packages.KOTLIN, "Byte")

        /**
         * The Short primitive type in Kotlin
         */
        public val SHORT: ClassName = ClassName(Packages.KOTLIN, "Short")

        /**
         * The Int primitive type in Kotlin
         */
        public val INT: ClassName = ClassName(Packages.KOTLIN, "Int")

        /**
         * The Long primitive type in Kotlin
         */
        public val LONG: ClassName = ClassName(Packages.KOTLIN, "Long")

        /**
         * The Float primitive type in Kotlin
         */
        public val FLOAT: ClassName = ClassName(Packages.KOTLIN, "Float")

        /**
         * The Double primitive type in Kotlin
         */
        public val DOUBLE: ClassName = ClassName(Packages.KOTLIN, "Double")

        /**
         * The String type in Kotlin
         */
        public val STRING: ClassName = ClassName(Packages.KOTLIN, "String")

        /**
         * The Char primitive type in Kotlin
         */
        public val CHAR: ClassName = ClassName(Packages.KOTLIN, "Char")

        // Collection types
        /**
         * The immutable List interface in Kotlin
         */
        public val LIST: ClassName = ClassName(Packages.KOTLIN_COLLECTIONS, "List")

        /**
         * The immutable Set interface in Kotlin
         */
        public val SET: ClassName = ClassName(Packages.KOTLIN_COLLECTIONS, "Set")

        /**
         * The immutable Map interface in Kotlin
         */
        public val MAP: ClassName = ClassName(Packages.KOTLIN_COLLECTIONS, "Map")

        /**
         * The immutable Collection interface in Kotlin
         */
        public val COLLECTION: ClassName = ClassName(Packages.KOTLIN_COLLECTIONS, "Collection")

        /**
         * The Iterable interface in Kotlin
         */
        public val ITERABLE: ClassName = ClassName(Packages.KOTLIN_COLLECTIONS, "Iterable")

        // Mutable collection types
        /**
         * The mutable List interface in Kotlin
         */
        public val MUTABLE_LIST: ClassName = ClassName(Packages.KOTLIN_COLLECTIONS, "MutableList")

        /**
         * The mutable Set interface in Kotlin
         */
        public val MUTABLE_SET: ClassName = ClassName(Packages.KOTLIN_COLLECTIONS, "MutableSet")

        /**
         * The mutable Map interface in Kotlin
         */
        public val MUTABLE_MAP: ClassName = ClassName(Packages.KOTLIN_COLLECTIONS, "MutableMap")

        /**
         * The mutable Collection interface in Kotlin
         */
        public val MUTABLE_COLLECTION: ClassName = ClassName(Packages.KOTLIN_COLLECTIONS, "MutableCollection")

        /**
         * The Array class in Kotlin
         */
        public val ARRAY: ClassName = ClassName(Packages.KOTLIN, "Array")
    }
}
