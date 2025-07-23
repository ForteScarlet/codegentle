package love.forte.codegentle.common.naming

import love.forte.codegentle.common.naming.PackageName.Companion.EMPTY
import love.forte.codegentle.common.naming.internal.PackageNameImpl
import kotlin.js.JsName

/**
 * A package name.
 *
 * Chain-like:
 * `null` <- [EMPTY] <- Path1 <- Path2 - ...
 *
 * The [Empty PackageName][EMPTY]:
 * `null` <- ""
 *
 * The others:
 * [EMPTY] <- Path1 <= Path2 ...
 *
 * Note: [PackageName] is only the carrier of the name, and does not verify the validity of the name.
 * Therefore, the following code is valid without additional validation:
 * ```Kotlin
 * PackageName("love.forte")
 * ```
 * will be
 * ```
 * (EMPTY <- "love.forte")
 * ```
 * instead of
 * ```
 * (EMPTY <- "love" <- "forte")
 * ```
 *
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleNamingImplementation::class)
public interface PackageName : Named {
    /**
     * The previous package name.
     * For example, the `love.forte` is previous of `love.forte.codegentle`.
     *
     * If it's currently an [empty package][EMPTY], get null.
     * If it's top package (e.g., `love`), get [EMPTY].
     */
    public val previous: PackageName?

    /**
     * Current path's name.
     * For example, the `"codegentle"` of `love.forte.codegentle`.
     *
     */
    override val name: String

    public companion object {
        /**
         * An empty package.
         */
        public val EMPTY: PackageName = PackageNameImpl(null, "")

        public fun valueOf(packageNameValue: String): PackageName =
            packageNameValue.parseToPackageName()
    }
}


public val PackageName.isEmpty: Boolean
    get() = previous == null && name.isEmpty()

public val PackageName.isNotEmpty: Boolean
    get() = !isEmpty

public val PackageName.parts: List<String>
    get() = partSequence.toList()

public val PackageName.partSequence: Sequence<String>
    get() = sequence {
        emit(this@partSequence)
    }

private suspend fun SequenceScope<String>.emit(packageName: PackageName) {
    if (packageName.isNotEmpty) {
        packageName.previous?.also { emit(it) }
        yield(packageName.name)
    }
}

/**
 * An empty package.
 */
@JsName("emptyPackageName")
public fun PackageName(): PackageName = EMPTY

/**
 * A top package.
 */
public fun PackageName(root: String, strict: Boolean = true): PackageName {
    if (root.isEmpty()) {
        return EMPTY
    }

    if (strict) {
        require(root.none { it == '.' }) {
            "PackageName's path can not contain '.' but name = '$root'." +
                "Use `CharSequence.parseToPackageName` if you wish to parse the package path string."
        }
    }

    return PackageNameImpl(EMPTY, root)
}

/**
 * A package.
 */
public fun PackageName(previous: PackageName?, name: String, strict: Boolean = true): PackageName {
    if (previous == null) {
        return PackageName(name)
    }

    if (previous.isEmpty) {
        require(name.isNotEmpty()) { "PackageName's name can not be empty." }
    }

    if (strict) {
        // check previous?
        require(name.none { it == '.' }) {
            "PackageName's path can not contain '.', but name = '$name'." +
                "Use `CharSequence.parseToPackageName` if you wish to parse the package path string."
        }
    }

    return PackageNameImpl(previous, name)
}

public fun PackageName(vararg paths: String): PackageName {
    return PackageName(paths.asList())
}

public fun PackageName(paths: List<String>, strict: Boolean = true): PackageName {
    if (paths.isEmpty()) {
        return EMPTY
    }

    if (paths.size == 1) {
        return PackageName(paths[0], strict)
    }

    var current = EMPTY

    for (currentPath in paths) {
        current = PackageName(current, currentPath, strict)
    }

    return current
}

public fun CharSequence.parseToPackageName(): PackageName {
    if (isEmpty()) {
        return EMPTY
    }

    return PackageName(split('.'))
}

/**
 * `love.forte` + `"codegentle"` -> `love.forte.codegentle`.
 */
public operator fun PackageName.plus(subPath: String): PackageName =
    PackageName(this, subPath, strict = true)

/**
 * `love.forte` + `codegentle.naming` -> `love.forte.codegentle.naming`.
 */
public operator fun PackageName.plus(subPaths: PackageName): PackageName {
    return when {
        subPaths.isEmpty -> this
        this.isEmpty -> subPaths
        subPaths.previous?.isEmpty != false -> PackageName(this, subPaths.name)
        else -> PackageName((this.nameSequence() + subPaths.nameSequence()).map { it.name }.toList())
    }
}

public fun PackageName.top(): PackageName {
    return previous?.takeUnless { it.isEmpty }?.top() ?: this
}

public fun PackageName.nameSequence(): Sequence<PackageName> {
    if (isEmpty) {
        return emptySequence()
    }

    if (previous?.isEmpty != false) {
        return sequenceOf(this)
    }

    return sequence {
        suspend fun SequenceScope<PackageName>.yieldNames(name: PackageName) {
            name.previous?.takeUnless { it.isEmpty }?.also { yieldNames(it) }
            yield(name)
        }

        yieldNames(this@nameSequence)
    }
}

public fun PackageName.names(): List<PackageName> =
    nameSequence().toList()

public fun PackageName.appendTo(appendable: Appendable, separator: String = ".") {
    previous
        ?.takeUnless { it.isEmpty }
        ?.also {
            it.appendTo(appendable)
            appendable.append(separator)
        }

    appendable.append(name)
}
