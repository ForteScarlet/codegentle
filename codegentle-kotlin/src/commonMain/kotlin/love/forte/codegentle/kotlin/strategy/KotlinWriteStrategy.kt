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
package love.forte.codegentle.kotlin.strategy

import love.forte.codegentle.common.naming.PackageName
import love.forte.codegentle.common.naming.PackageNames
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.writer.Strategy
import love.forte.codegentle.kotlin.KotlinModifier

@RequiresOptIn
public annotation class KotlinWriteStrategyInterfaceImplementation

/**
 * The strategies for writing Kotlin code.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(KotlinWriteStrategyInterfaceImplementation::class)
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

    public fun omitFunctionUnitReturnType(): Boolean

    public fun replaceReturnWithExpressionBody(): Boolean

    public fun defaultVisibility(): KotlinModifier?
}

/**
 * Default implementation of [KotlinWriteStrategy].
 */
@OptIn(KotlinWriteStrategyInterfaceImplementation::class)
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

    override fun omitFunctionUnitReturnType(): Boolean = true

    override fun replaceReturnWithExpressionBody(): Boolean = true

    override fun defaultVisibility(): KotlinModifier? = null

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
@OptIn(KotlinWriteStrategyInterfaceImplementation::class)
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

    override fun omitFunctionUnitReturnType(): Boolean = true

    override fun replaceReturnWithExpressionBody(): Boolean = true

    override fun defaultVisibility(): KotlinModifier? = null
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
