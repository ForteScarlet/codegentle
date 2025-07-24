package love.forte.codegentle.kotlin.strategy

import love.forte.codegentle.common.naming.PackageName
import love.forte.codegentle.common.naming.PackageNames
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.writer.Strategy

/**
 * The strategies for writing Kotlin code.
 *
 * @author ForteScarlet
 */
public interface KotlinWriteStrategy : Strategy {
    /**
     * A number of packages are imported into every Kotlin file by default:
     *
     * - `kotlin.*`
     * - `kotlin.annotation.*`
     * - `kotlin.collections.*`
     * - `kotlin.comparisons.*`
     * - `kotlin.io.*`
     * - `kotlin.ranges.*`
     * - `kotlin.sequences.*`
     * - `kotlin.text.*`
     *
     * Additional packages are imported depending on the target platform:
     *
     * JVM:
     *
     * - `java.lang.*`
     * - `kotlin.jvm.*`
     *
     * JS:
     *
     * - `kotlin.js.*`
     *
     * see [Kotlin Documentation](https://kotlinlang.org/docs/packages.html#default-imports)
     */
    public fun omitPackage(packageName: PackageName): Boolean
    // No need to declare methods that are already in Strategy
}

/**
 * Default implementation of [KotlinWriteStrategy].
 */
public open class DefaultKotlinWriteStrategy : KotlinWriteStrategy {
    override fun isIdentifier(value: String): Boolean {
        if (value.isEmpty()) return false

        // Check if the first character is a valid Kotlin identifier start
        if (!value[0].isKotlinIdentifierStart()) return false

        // Check if the rest of the characters are valid Kotlin identifier parts
        for (i in 1 until value.length) {
            if (!value[i].isKotlinIdentifierPart()) return false
        }

        return true
    }

    override fun isValidSourceName(name: TypeName): Boolean {
        // For TypeName, we'll consider it valid if it's a valid Kotlin identifier
        return isValidSourceName(name.toString())
    }

    override fun isValidSourceName(name: String): Boolean {
        // For Kotlin, we'll use the same rules as for identifiers
        return isIdentifier(name)
    }

    override fun omitPackage(packageName: PackageName): Boolean =
        packageName in defaultImports

    public companion object {
        public val defaultImports: Set<PackageName> = setOf(
            PackageNames.KOTLIN,
            PackageNames.KOTLIN_JVM,
            PackageNames.KOTLIN_JS,
            PackageNames.JAVA_LANG,
            PackageNames.KOTLIN_ANNOTATION,
            PackageNames.KOTLIN_COLLECTIONS,
            PackageNames.KOTLIN_COMPARISONS,
            PackageNames.KOTLIN_IO,
            PackageNames.KOTLIN_RANGES,
            PackageNames.KOTLIN_SEQUENCES,
            PackageNames.KOTLIN_TEXT,
        )
    }
}

/**
 * A [KotlinWriteStrategy] for generating code as a string.
 */
public object ToStringKotlinWriteStrategy : KotlinWriteStrategy {
    private val defaultImportPackages = setOf(
        PackageNames.KOTLIN,
        PackageNames.KOTLIN_JVM,
        PackageNames.KOTLIN_JS,
        PackageNames.KOTLIN_ANNOTATION,
    )

    override fun isIdentifier(value: String): Boolean = true

    override fun isValidSourceName(name: TypeName): Boolean = true

    override fun isValidSourceName(name: String): Boolean = true

    /**
     * ToString only omit `kotlin` package.
     */
    override fun omitPackage(packageName: PackageName): Boolean =
        packageName in defaultImportPackages
}

/**
 * Returns true if this character is a valid start for a Kotlin identifier.
 */
private fun Char.isKotlinIdentifierStart(): Boolean {
    return this == '_' || this.isLetter()
}

/**
 * Returns true if this character is a valid part of a Kotlin identifier.
 */
private fun Char.isKotlinIdentifierPart(): Boolean {
    return this == '_' || this.isLetterOrDigit()
}

public fun KotlinWriteStrategy.omitPackageNullable(packageName: PackageName?): Boolean =
    packageName != null && omitPackage(packageName)
