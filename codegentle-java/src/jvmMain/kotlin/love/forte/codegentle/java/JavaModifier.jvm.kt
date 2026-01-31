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
package love.forte.codegentle.java

import javax.lang.model.element.Modifier

// javax.lang.model.element.Modifier

// /**
//  * @see javax.lang.model.element.Modifier
//  */
// @Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
// public actual typealias JavaModifier = javax.lang.model.element.Modifier

public fun Modifier.toJavaModifier(): JavaModifier = JavaModifier.valueOf(name)

public fun JavaModifier.toJavaxModifier(): Modifier = Modifier.valueOf(name)


public fun <B : JavaModifierCollector<B>> B.addModifier(modifier: Modifier): B =
    addModifier(modifier.toJavaModifier())

public fun <B : JavaModifierCollector<B>> B.addModifier(vararg modifiers: Modifier): B =
    addModifiers(modifiers.map { it.toJavaModifier() })

public fun <B : JavaModifierCollector<B>> B.addModifier(modifiers: Iterable<Modifier>): B =
    addModifiers(modifiers.map { it.toJavaModifier() })
