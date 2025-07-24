package love.forte.codegentle.common.codepoint


@Suppress("UNUSED_PARAMETER")
internal actual fun jsCodePointAt(str: String, index: Int): Int =
    str.asDynamic().codePointAt(index).unsafeCast<Int>()

@Suppress("UNUSED_PARAMETER")
internal actual fun jsFromCodePoint(code: Int): String =
    js("String").fromCodePoint(code).toString()
