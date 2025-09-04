/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2017-2025 Forte Scarlet
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
package love.forte.codegentle.kotlin

import love.forte.codegentle.common.GenEnumSet

@GenEnumSet(
    internal = true,
    containerName = "KotlinModifierCollector",
    containerSingleAdder = "addModifier",
    containerMultiAdder = "addModifiers",
    operatorsName = "KotlinModifiers"
)
public enum class KotlinModifier(
    internal val keyword: String,
) {
    // Modifier order defined here:
    // https://kotlinlang.org/docs/reference/coding-conventions.html#modifiers

    // Access.
    PUBLIC("public"),
    PROTECTED("protected"),
    PRIVATE("private"),
    INTERNAL("internal"),

    // Multiplatform modules.
    EXPECT("expect"),
    ACTUAL("actual"),

    FINAL("final"),
    OPEN("open"),
    ABSTRACT("abstract"),
    SEALED("sealed"),
    CONST("const"),

    EXTERNAL("external"),
    OVERRIDE("override"),
    LATEINIT("lateinit"),
    TAILREC("tailrec"),
    VARARG("vararg"),
    SUSPEND("suspend"),
    INNER("inner"),

    ENUM("enum"),
    ANNOTATION("annotation"),
    VALUE("value"),
    FUN("fun"),

    COMPANION("companion"),

    // Call-site compiler tips.
    INLINE("inline"),
    NOINLINE("noinline"),
    CROSSINLINE("crossinline"),
    REIFIED("reified"),

    INFIX("infix"),
    OPERATOR("operator"),

    DATA("data"),

    IN("in"),
    OUT("out"),
    ;
}

internal val VISIBILITY_MODIFIERS = KotlinModifierSet.of(
    KotlinModifier.PUBLIC,
    KotlinModifier.INTERNAL,
    KotlinModifier.PROTECTED,
    KotlinModifier.PRIVATE
)

internal fun Set<KotlinModifier>.containsVisibility(): Boolean {
    return if (this is KotlinModifierSet) {
        containsAny(VISIBILITY_MODIFIERS)
    } else {
        any { it in VISIBILITY_MODIFIERS }
    }
}

internal val Set<KotlinModifier>.visibilities: Set<KotlinModifier>
    get() = if (this is KotlinModifierSet) {
        intersect(VISIBILITY_MODIFIERS)
    } else {
        MutableKotlinModifierSet.of(VISIBILITY_MODIFIERS).also {
            it.retainAll(this)
        }
    }

internal val Set<KotlinModifier>.visibility: KotlinModifier?
    get() {
        val visibilities = visibilities
        if (visibilities.size > 1) {
            throw IllegalStateException("Multiple visibility modifiers: $visibilities")
        }

        return visibilities.firstOrNull()
    }

public interface KotlinModifierContainer {
    public val modifiers: Set<KotlinModifier>

    public fun hasModifier(modifier: KotlinModifier): Boolean = modifier in modifiers
}


public inline val KotlinModifierCollector<*>.modifiers: KotlinModifiers
    get() = KotlinModifiers(this)

/**
 * ```kotlin
 * container.modifiers {
 *    // add Modifier.PUBLIC
 *    public()
 *
 *    // add Modifier.IN
 *    `in`()
 * }
 * ```
 */
public inline fun KotlinModifierCollector<*>.modifiers(block: KotlinModifiers.() -> Unit) {
    modifiers.block()
}
