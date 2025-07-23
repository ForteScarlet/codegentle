package love.forte.codegentle.kotlin.strategy

import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.writer.Strategy

/**
 * The strategies for writing Kotlin code.
 *
 * @author ForteScarlet
 */
public interface KotlinWriteStrategy : Strategy {
    /**
     * Whether to omit reference to `kotlin` packages.
     */
    public fun omitKotlinPackage(): Boolean

    // No need to declare methods that are already in Strategy
}

/**
 * Default implementation of [KotlinWriteStrategy].
 */
public object DefaultKotlinWriteStrategy : KotlinWriteStrategy {
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

    override fun omitKotlinPackage(): Boolean = true
}

/**
 * A [KotlinWriteStrategy] for generating code as a string.
 */
public object ToStringKotlinWriteStrategy : KotlinWriteStrategy {
    override fun isIdentifier(value: String): Boolean = true

    override fun isValidSourceName(name: TypeName): Boolean = true

    override fun isValidSourceName(name: String): Boolean = true

    override fun omitKotlinPackage(): Boolean = true
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
