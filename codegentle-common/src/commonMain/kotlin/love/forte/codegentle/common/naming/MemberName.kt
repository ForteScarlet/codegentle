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
package love.forte.codegentle.common.naming

import love.forte.codegentle.common.naming.internal.MemberNameImpl

/**
 * A Member name.
 *
 * - Java: static methods, static fields, Enum's elements, enclosed classes, etc.
 * - Kotlin: top-level functions, Object's properties, Enum's elements, enclosed classes, etc.
 *
 * Similar to [ClassName] but represents a member rather than a type.
 * Can be emitted and auto-imported in both Java and Kotlin code.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleNamingImplementation::class)
public interface MemberName : TypeName, Named, Comparable<MemberName> {
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
     */
    public val enclosingClassName: ClassName?
    
    /**
     * Compares this member name with another member name.
     * The comparison is based on the canonical name.
     */
    override fun compareTo(other: MemberName): Int = canonicalName.compareTo(other.canonicalName)
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
    MemberNameImpl(enclosingClassName.packageName, enclosingClassName, name)

/**
 * Returns a member name created from the given parts.
 */
public fun MemberName(packageName: PackageName, enclosingClassName: ClassName?, name: String): MemberName =
    MemberNameImpl(packageName, enclosingClassName, name)
