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
package love.forte.codegentle.kotlin.ksp

import com.google.devtools.ksp.symbol.Modifier
import love.forte.codegentle.kotlin.KotlinModifier

/**
 * Converts a KSP [Modifier] to a CodeGentle [KotlinModifier].
 *
 * This function maps KSP modifiers to their corresponding CodeGentle modifiers.
 *
 * @return The corresponding [KotlinModifier] for this KSP modifier
 * @throws IllegalArgumentException if the modifier cannot be mapped
 */
public fun Modifier.toKotlinModifier(): KotlinModifier {
    return when (this) {
        Modifier.PUBLIC -> KotlinModifier.PUBLIC
        Modifier.PRIVATE -> KotlinModifier.PRIVATE
        Modifier.PROTECTED -> KotlinModifier.PROTECTED
        Modifier.INTERNAL -> KotlinModifier.INTERNAL
        Modifier.EXPECT -> KotlinModifier.EXPECT
        Modifier.ACTUAL -> KotlinModifier.ACTUAL
        Modifier.FINAL -> KotlinModifier.FINAL
        Modifier.OPEN -> KotlinModifier.OPEN
        Modifier.ABSTRACT -> KotlinModifier.ABSTRACT
        Modifier.SEALED -> KotlinModifier.SEALED
        Modifier.CONST -> KotlinModifier.CONST
        Modifier.EXTERNAL -> KotlinModifier.EXTERNAL
        Modifier.OVERRIDE -> KotlinModifier.OVERRIDE
        Modifier.LATEINIT -> KotlinModifier.LATEINIT
        Modifier.TAILREC -> KotlinModifier.TAILREC
        Modifier.VARARG -> KotlinModifier.VARARG
        Modifier.SUSPEND -> KotlinModifier.SUSPEND
        Modifier.INNER -> KotlinModifier.INNER
        Modifier.ENUM -> KotlinModifier.ENUM
        Modifier.ANNOTATION -> KotlinModifier.ANNOTATION
        Modifier.FUN -> KotlinModifier.FUN
        Modifier.VALUE -> KotlinModifier.VALUE
        Modifier.INLINE -> KotlinModifier.INLINE
        Modifier.NOINLINE -> KotlinModifier.NOINLINE
        Modifier.CROSSINLINE -> KotlinModifier.CROSSINLINE
        Modifier.REIFIED -> KotlinModifier.REIFIED
        Modifier.INFIX -> KotlinModifier.INFIX
        Modifier.OPERATOR -> KotlinModifier.OPERATOR
        Modifier.DATA -> KotlinModifier.DATA
        Modifier.IN -> KotlinModifier.IN
        Modifier.OUT -> KotlinModifier.OUT
        // Handle COMPANION modifier if it exists in the KSP API
        // If not, it will be caught by the else branch
        else -> {
            // Try to map by name as a fallback
            val name = this.name
            KotlinModifier.entries.find { it.name == name }
                ?: throw IllegalArgumentException("Unsupported modifier: $this")
        }
    }
}
