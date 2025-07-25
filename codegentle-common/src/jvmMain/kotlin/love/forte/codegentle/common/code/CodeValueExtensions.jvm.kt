package love.forte.codegentle.common.code

import love.forte.codegentle.common.naming.toClassName
import kotlin.reflect.KClass

public fun CodeValueSingleFormatBuilder.emitType(type: KClass<*>): CodeValueSingleFormatBuilder {
    return emitType(type.toClassName())
}

public fun CodeValueSingleFormatBuilder.emitType(type: Class<*>): CodeValueSingleFormatBuilder {
    return emitType(type.toClassName())
}
