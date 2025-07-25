package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeSimplePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.strategy.KotlinWriteStrategy
import love.forte.codegentle.kotlin.visibility

internal fun CodeValue.isStartWithReturn(): Boolean {
    if (isEmpty()) return false

    val part = parts.first()
    if (part is CodeSimplePart) {
        val value = part.value.trimStart()
        return value.startsWith("return ")
    } else {
        return false
    }
}

internal fun CodeValue.removeFirstReturn(): CodeValue {
    val first = parts.first()
    require(first is CodeSimplePart) { "First part must be CodeSimplePart." }

    val firstValue = first.value
    val replacedFirstValue = firstValue.replaceFirst("return ", "")

    return if (replacedFirstValue == firstValue) {
        this
    } else {
        CodeValue(buildList {
            add(CodePart.simple(replacedFirstValue))
            addAll(parts.subList(1, parts.size))
        })
    }
}

internal fun KotlinWriteStrategy.resolveModifiers(
    modifiers: Set<KotlinModifier>
): Set<KotlinModifier> {
    val currentVisibility = modifiers.visibility
    if (currentVisibility != null) {
        return modifiers
    }

    val defaultVisibility = defaultVisibility()
    if (defaultVisibility == null) {
        return modifiers
    }

    return MutableKotlinModifierSet.of(modifiers).also { set ->
        set.add(defaultVisibility)
    }
}
