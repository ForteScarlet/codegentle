package love.forte.codegentle.common.naming

/**
 * Some common package names.
 */
public object PackageNames {
    /**
     * `kotlin.*`
     */
    public val KOTLIN: PackageName = "kotlin".parseToPackageName()

    /**
     * `kotlin.jvm.*`
     */
    public val KOTLIN_JVM: PackageName = "kotlin.jvm".parseToPackageName()

    /**
     * `kotlin.js.*`
     */
    public val KOTLIN_JS: PackageName = "kotlin.js".parseToPackageName()

    /**
     * `kotlin.annotation.*`
     */
    public val KOTLIN_ANNOTATION: PackageName = "kotlin.annotation".parseToPackageName()

    /**
     * `kotlin.collections.*`
     */
    public val KOTLIN_COLLECTIONS: PackageName = "kotlin.collections".parseToPackageName()

    /**
     * `kotlin.sequences.*`
     */
    public val KOTLIN_SEQUENCES: PackageName = "kotlin.sequences".parseToPackageName()

    /**
     * `kotlin.comparisons.*`
     */
    public val KOTLIN_COMPARISONS: PackageName = "kotlin.comparisons".parseToPackageName()

    /**
     * `kotlin.io.*`
     */
    public val KOTLIN_IO: PackageName = "kotlin.io".parseToPackageName()

    /**
     * `kotlin.text.*`
     */
    public val KOTLIN_TEXT: PackageName = "kotlin.text".parseToPackageName()

    /**
     * `kotlin.ranges.*`
     */
    public val KOTLIN_RANGES: PackageName = "kotlin.ranges".parseToPackageName()

    /**
     * `kotlin.coroutines.*`
     */
    public val KOTLIN_COROUTINES: PackageName = "kotlin.coroutines".parseToPackageName()

    /**
     * `kotlin.reflect.*`
     */
    public val KOTLIN_REFLECT: PackageName = "kotlin.reflect".parseToPackageName()

    /**
     * `java.lang.*`
     */
    public val JAVA_LANG: PackageName = "java.lang".parseToPackageName()

    /**
     * `java.util.*`
     */
    public val JAVA_UTIL: PackageName = "java.util".parseToPackageName()

    /**
     * `java.io.*`
     */
    public val JAVA_IO: PackageName = "java.io".parseToPackageName()

    /**
     * `java.time.*`
     */
    public val JAVA_TIME: PackageName = "java.time".parseToPackageName()

    /**
     * `java.math.*`
     */
    public val JAVA_MATH: PackageName = "java.math".parseToPackageName()

    /**
     * `java.net.*`
     */
    public val JAVA_NET: PackageName = "java.net".parseToPackageName()
}
