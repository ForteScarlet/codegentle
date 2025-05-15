package love.forte.codegentle.common.naming

import love.forte.codegentle.common.codepoint.codePointAt
import love.forte.codegentle.common.codepoint.isLowerCase
import love.forte.codegentle.common.codepoint.isUpperCase
import love.forte.codegentle.common.naming.internal.ClassNameImpl

/**
 * A JVM-based, fully qualified class name for top-level and member classes.
 *
 * @author ForteScarlet
 */
public interface ClassName : Named, TypeName, Comparable<ClassName> {
    /**
     * Returns the simple name of this class, like `"Entry"` for `Map.Entry`.
     */
    public val simpleName: String

    /**
     * Same as [simpleName].
     */
    override val name: String
        get() = simpleName

    /**
     * Returns the package name, like `"java.util"` for [Map.Entry].
     * Returns null for the default package.
     */
    public val packageName: String?

    /**
     * Returns the enclosing class, like `Map` for `Map.Entry`.
     * Returns null if this class is not nested in another class.
     */
    public val enclosingClassName: ClassName?

    /**
     * Returns the top class in this nesting group.
     * Equivalent to chained calls to [ClassName.enclosingClassName] until the result's enclosing class is null.
     */
    public val topLevelClassName: ClassName

    /**
     * Returns a class that shares the same enclosing package or class.
     * If this class is enclosed by another class, this is equivalent to `enclosingClassName().nestedClass(name)`.
     * Otherwise, it is equivalent to `get(packageName(), name)`.
     */
    public fun peerClass(name: String): ClassName

    /**
     * Returns a new `ClassName` instance for the specified `name` as nested inside this class.
     */
    public fun nestedClass(name: String): ClassName


    override fun compareTo(other: ClassName): Int = canonicalName.compareTo(other.canonicalName)
}

public fun ClassName.contentHashCode(): Int {
    var result = packageName?.hashCode() ?: 0
    result = 31 * result + (enclosingClassName?.hashCode() ?: 0)
    result = 31 * result + simpleName.hashCode()
    return result
}

public infix fun ClassName.contentEquals(other: ClassName): Boolean {
    if (packageName != other.packageName) return false
    if (simpleName != other.simpleName) return false

    val thisEnclosingClassName = enclosingClassName
    val otherEnclosingClassName = other.enclosingClassName

    if (thisEnclosingClassName === otherEnclosingClassName) return true
    return when {
        thisEnclosingClassName === otherEnclosingClassName -> true
        thisEnclosingClassName != null && otherEnclosingClassName != null ->
            thisEnclosingClassName.contentEquals(otherEnclosingClassName)

        else -> enclosingClassName == null && other.enclosingClassName == null
    }
}

/**
 * Returns the full class name of this class. Like `"java.util.Map.Entry"` for [Map.Entry].
 */
public val ClassName.canonicalName: String
    get() = buildString { appendCanonicalNameTo(this) }

public fun <A : Appendable> ClassName.appendCanonicalNameTo(appendable: A): A {
    val enclosingClassName = enclosingClassName
    if (enclosingClassName != null) {
        enclosingClassName.appendCanonicalNameTo(appendable).append('.').append(simpleName)
    } else {
        val packageName = packageName
        if (packageName != null) {
            appendable.append(packageName).append('.')
        }
        appendable.append(simpleName)
    }

    return appendable
}

/**
 * Return the binary name of a class.
 *
 * `java.util.Map$Entry`
 */
public val ClassName.reflectionName: String
    get() = buildString { appendReflectionNameTo(this) }

public fun <A : Appendable> ClassName.appendReflectionNameTo(appendable: A): A {
    val enclosingClassName = enclosingClassName
    if (enclosingClassName != null) {
        enclosingClassName.appendReflectionNameTo(appendable).append('$').append(simpleName)
    } else {
        val packageName = packageName
        if (packageName != null) {
            appendable.append(packageName).append('.')
        }
        appendable.append(simpleName)
    }

    return appendable
}

public val ClassName.simpleNames: List<String>
    get() = enclosingClassName?.simpleNames?.plus(simpleName)
        ?: listOf(simpleName)

/**
 * Returns a class name created from the given parts. For example, calling this with package name `"java.util"`
 * and simple names `"Map"`, `"Entry"` yields [Map.Entry].
 */
public fun ClassName(packageName: String?, simpleName: String, vararg simpleNames: String): ClassName {
    var className: ClassName = ClassNameImpl(packageName, null, simpleName)
    for (nested in simpleNames) {
        className = className.nestedClass(nested)
    }
    return className
}

/**
 * Returns a new [ClassName] instance for the given fully-qualified class name string. This
 * method assumes that the input is ASCII and follows typical Java style (lowercase package
 * names, UpperCamelCase class names) and may produce incorrect results or throw
 * [IllegalArgumentException] otherwise.
 * For that reason,
 * [ClassName] should be preferred as they can correctly create [ClassName]
 * instances without such restrictions.
 */
public fun ClassName(bestGuessClassNameString: String): ClassName {
    // Add the package name, like "java.util.concurrent", or "" for no package.
    var p = 0
    while (p < bestGuessClassNameString.length && bestGuessClassNameString.codePointAt(p).isLowerCase()) {
        p = bestGuessClassNameString.indexOf('.', p) + 1
        require(p != 0) {
            "couldn't make a guess for $bestGuessClassNameString"
        }
    }

    val packageName: String? =
        if (p == 0) null else bestGuessClassNameString.substring(0, p - 1)

    // Add class names like "Map" and "Entry".
    var className: ClassName? = null

    for (simpleName in bestGuessClassNameString.substring(p).split("\\.".toRegex()).toTypedArray()) {
        require(simpleName.isNotEmpty() && simpleName.codePointAt(0).isUpperCase()) {
            "couldn't make a guess for $bestGuessClassNameString"
        }
        className = ClassNameImpl(packageName, className, simpleName)
    }

    return className!!
}
