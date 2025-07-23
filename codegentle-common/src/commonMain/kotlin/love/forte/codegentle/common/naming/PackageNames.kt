package love.forte.codegentle.common.naming

/**
 * Some common package names.
 */
public object PackageNames {
    /**
     * `kotlin.*`
     */
    public val kotlin: PackageName = "kotlin".parseToPackageName()

    /**
     * `kotlin.jvm.*`
     */
    public val kotlinJvm: PackageName = "kotlin.jvm".parseToPackageName()

    /**
     * `kotlin.js.*`
     */
    public val kotlinJs: PackageName = "kotlin.js".parseToPackageName()

    /**
     * `kotlin.annotation.*`
     */
    public val kotlinAnnotation: PackageName = "kotlin.annotation".parseToPackageName()

    /**
     * `kotlin.collections.*`
     */
    public val kotlinCollections: PackageName = "kotlin.collections".parseToPackageName()

    /**
     * `kotlin.sequences.*`
     */
    public val kotlinSequences: PackageName = "kotlin.sequences".parseToPackageName()

    /**
     * `kotlin.comparisons.*`
     */
    public val kotlinComparisons: PackageName = "kotlin.comparisons".parseToPackageName()

    /**
     * `kotlin.io.*`
     */
    public val kotlinIo: PackageName = "kotlin.io".parseToPackageName()

    /**
     * `kotlin.text.*`
     */
    public val kotlinText: PackageName = "kotlin.text".parseToPackageName()

    /**
     * `kotlin.ranges.*`
     */
    public val kotlinRanges: PackageName = "kotlin.ranges".parseToPackageName()

    /**
     * `kotlin.coroutines.*`
     */
    public val kotlinCoroutines: PackageName = "kotlin.coroutines".parseToPackageName()

    /**
     * `kotlin.reflect.*`
     */
    public val kotlinReflect: PackageName = "kotlin.reflect".parseToPackageName()

    /**
     * `java.lang.*`
     */
    public val javaLang: PackageName = "java.lang".parseToPackageName()

    /**
     * `java.util.*`
     */
    public val javaUtil: PackageName = "java.util".parseToPackageName()

    /**
     * `java.io.*`
     */
    public val javaIo: PackageName = "java.io".parseToPackageName()

    /**
     * `java.time.*`
     */
    public val javaTime: PackageName = "java.time".parseToPackageName()

    /**
     * `java.math.*`
     */
    public val javaMath: PackageName = "java.math".parseToPackageName()

    /**
     * `java.net.*`
     */
    public val javaNet: PackageName = "java.net".parseToPackageName()
}
