package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeSimplePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.isEmpty

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



