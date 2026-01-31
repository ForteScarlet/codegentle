/*
 * Copyright (C) 2005-2026 Forte Scarlet
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
package love.forte.codegentle.java

import love.forte.codegentle.common.GenEnumSet


/**
 *
 * see `javax.lang.model.element.Modifier`
 */
@GenEnumSet(
    internal = true,
    containerName = "JavaModifierCollector",
    containerSingleAdder = "addModifier",
    containerMultiAdder = "addModifiers",
    operatorsName = "JavaModifiers"
)
public enum class JavaModifier {

    // See JLS sections 8.1.1, 8.3.1, 8.4.3, 8.8.3, and 9.1.1.
    // java.lang.reflect.Modifier includes INTERFACE, but that's a VMism.

    /** The modifier `public` */
    PUBLIC,

    /** The modifier `protected` */
    PROTECTED,

    /** The modifier `private` */
    PRIVATE,

    /** The modifier `abstract` */
    ABSTRACT,

    /** The modifier `default` */
    DEFAULT,

    /** The modifier `static` */
    STATIC,

    /**
     * The modifier `sealed` (since Java 17)
     */
    SEALED,

    /**
     * The modifier `non-sealed` (since Java 17)
     */
    NON_SEALED {
        override fun toString(): String = "non-sealed"
    },

    /** The modifier `final` */
    FINAL,

    /** The modifier `transient` */
    TRANSIENT,

    /** The modifier `volatile` */
    VOLATILE,

    /** The modifier `synchronized` */
    SYNCHRONIZED,

    /** The modifier `native` */
    NATIVE,

    /** The modifier `strictfp` */
    STRICTFP;

    /**
     * Returns this modifier's name as defined in
     * *The Java Language Specification*.
     * The modifier name is the [name of the enum constant][name]
     * in lowercase and with any underscores ("`_`")
     * replaced with hyphens ("`-`").
     *
     * @return the modifier's name
     */
    override fun toString(): String = name.lowercase()
}

public interface JavaModifierContainer {
    public val modifiers: Set<JavaModifier>

    public fun hasModifier(modifier: JavaModifier): Boolean = modifier in modifiers
}

public inline val JavaModifierCollector<*>.modifiers: JavaModifiers
    get() = JavaModifiers(this)

/**
 * ```kotlin
 * container.modifiers {
 *    // add Modifier.PUBLIC
 *    public()
 *
 *    // add Modifier.static
 *    static()
 * }
 * ```
 */
public inline fun JavaModifierCollector<*>.modifiers(block: JavaModifiers.() -> Unit) {
    modifiers.block()
}
