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
