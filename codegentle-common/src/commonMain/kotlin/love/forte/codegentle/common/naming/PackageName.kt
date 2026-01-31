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

public fun PackageName.isEmpty(): Boolean =
    previous == null && name.isEmpty()

public fun PackageName.isNotEmpty(): Boolean =
    !isEmpty()

public inline fun PackageName.ifEmpty(block: (PackageName) -> Unit) {
    if (isEmpty()) {
        block(this)
    }
}

public inline fun PackageName.ifNotEmpty(block: (PackageName) -> Unit) {
    if (isNotEmpty()) {
        block(this)
    }
}

public val PackageName.parts: List<String>
    get() = partSequence.toList()

public val PackageName.partSequence: Sequence<String>
    get() = sequence {
        emit(this@partSequence)
    }

private suspend fun SequenceScope<String>.emit(packageName: PackageName) {
    if (packageName.isNotEmpty()) {
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

    if (previous.isEmpty()) {
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

/**
 * A package.
 */
public fun PackageName(previous: PackageName?, paths: List<String>, strict: Boolean = true): PackageName {
    if (previous == null) {
        return PackageName(paths, strict)
    }

    if (previous.isEmpty()) {
        return PackageName()
    }

    var current: PackageName = previous

    for (currentPath in paths) {
        current = PackageName(current, currentPath, strict)
    }

    return current
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
        subPaths.isEmpty() -> this
        isEmpty() -> subPaths
        subPaths.previous?.isEmpty() != false -> PackageName(this, subPaths.parts)
        else -> PackageName((this.nameSequence() + subPaths.nameSequence()).map { it.name }.toList())
    }
}

public fun PackageName.top(): PackageName {
    return previous?.takeUnless { it.isEmpty() }?.top() ?: this
}

public fun PackageName.nameSequence(): Sequence<PackageName> {
    if (isEmpty()) {
        return emptySequence()
    }

    if (previous?.isEmpty() != false) {
        return sequenceOf(this)
    }

    return sequence {
        suspend fun SequenceScope<PackageName>.yieldNames(name: PackageName) {
            name.previous?.takeUnless { it.isEmpty() }?.also { yieldNames(it) }
            yield(name)
        }

        yieldNames(this@nameSequence)
    }
}

public fun PackageName.names(): List<PackageName> =
    nameSequence().toList()

public fun PackageName.appendTo(appendable: Appendable, separator: String = ".") {
    previous
        ?.takeUnless { it.isEmpty() }
        ?.also {
            it.appendTo(appendable)
            appendable.append(separator)
        }

    appendable.append(name)
}

/**
 * Converts this [PackageName] into a relative path separated by the given separator.
 *
 * The relative path is constructed by joining all the names in the package name sequence
 * with the specified separator.
 *
 * @param separator the string to separate names in the relative path. Defaults to `"/"`.
 * @return a string representing the relative path of this [PackageName].
 */
public fun PackageName.toRelativePath(separator: String = "/"): String {
    return nameSequence().joinToString(separator = separator) { it.name }
}
