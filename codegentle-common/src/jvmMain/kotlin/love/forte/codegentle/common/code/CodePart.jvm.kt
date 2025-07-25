package love.forte.codegentle.common.code

import love.forte.codegentle.common.naming.toClassName
import kotlin.reflect.KClass

/**
 * @see KClass.toClassName
 */
public fun CodePart.Companion.type(type: KClass<*>): CodeArgumentPart =
    type(type.toClassName())

/**
 * @see Class.toClassName
 */
public fun CodePart.Companion.type(type: Class<*>): CodeArgumentPart =
    type(type.toClassName())
