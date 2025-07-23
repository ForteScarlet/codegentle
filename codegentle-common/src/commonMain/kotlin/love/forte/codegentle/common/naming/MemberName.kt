package love.forte.codegentle.common.naming

import love.forte.codegentle.common.naming.internal.MemberNameImpl

/**
 * A Member name.
 *
 * - Java: static methods, static fields, Enum's elements, enclosed classes, etc.
 * - Kotlin: top-level functions, Object's properties, Enum's elements, enclosed classes, etc.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleNamingImplementation::class)
public interface MemberName : Named {
    /**
     * Member's name.
     */
    override val name: String

    /**
     * Member's package name.
     */
    public val packageName: PackageName

    /**
     * Enclosing [ClassName] if it exists.
     *
     */
    public val enclosingClassName: ClassName?
}

public fun MemberName.contentHashCode(): Int {
    var result = packageName.hashCode()
    result = 31 * result + (enclosingClassName?.hashCode() ?: 0)
    result = 31 * result + name.hashCode()
    return result
}

public infix fun MemberName.contentEquals(other: MemberName): Boolean {
    if (packageName != other.packageName) return false
    if (name != other.name) return false

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
 * Returns the full member name. Like `"java.util.Map.Entry.KEY"` for a member KEY in Map.Entry.
 */
public val MemberName.canonicalName: String
    get() = buildString { appendCanonicalNameTo(this) }

public fun <A : Appendable> MemberName.appendCanonicalNameTo(appendable: A): A {
    val enclosingClassName = enclosingClassName
    if (enclosingClassName != null) {
        enclosingClassName.appendCanonicalNameTo(appendable).append('.').append(name)
    } else {
        packageName.appendTo(appendable)
        appendable.append('.').append(name)
    }

    return appendable
}

/**
 * Returns a member name created from the given parts.
 */
public fun MemberName(packageName: PackageName, name: String): MemberName =
    MemberNameImpl(packageName, null, name)

/**
 * Returns a member name created from the given parts.
 */
public fun MemberName(packageName: String, name: String): MemberName =
    MemberName(packageName.parseToPackageName(), name)

/**
 * Returns a member name created from the given parts with an enclosing class.
 */
public fun MemberName(enclosingClassName: ClassName, name: String): MemberName =
    MemberNameImpl(enclosingClassName.packageName ?: PackageName(), enclosingClassName, name)

/**
 * Returns a member name created from the given parts.
 */
public fun MemberName(packageName: PackageName, enclosingClassName: ClassName?, name: String): MemberName =
    MemberNameImpl(packageName, enclosingClassName, name)
