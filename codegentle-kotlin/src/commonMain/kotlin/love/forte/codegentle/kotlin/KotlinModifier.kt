/*
 * Copyright (C) 2017 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
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
    containerName = "KotlinModifierBuilderContainer",
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

    // @GenEnumSet(
    //     internal = true,
    //     immutableName = "KotlinModifierTargetSet",
    //     mutableName = "MutableKotlinModifierTargetSet"
    // )
    // internal enum class Target {
    //     CLASS,
    //     VARIANCE,
    //     PARAMETER,
    //     TYPE_PARAMETER,
    //     FUNCTION,
    //     PROPERTY,
    //     INTERFACE,
    // }

}

internal val VISIBILITY_MODIFIERS: Set<KotlinModifier> = KotlinModifierSet.of(
    KotlinModifier.PUBLIC,
    KotlinModifier.INTERNAL,
    KotlinModifier.PROTECTED,
    KotlinModifier.PRIVATE
)

public interface KotlinModifierContainer {
    public val modifiers: Set<KotlinModifier>

    public fun hasModifier(modifier: KotlinModifier): Boolean = modifier in modifiers
}


public inline val KotlinModifierBuilderContainer<*>.modifiers: KotlinModifiers
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
public inline fun KotlinModifierBuilderContainer<*>.modifiers(block: KotlinModifiers.() -> Unit) {
    modifiers.block()
}
