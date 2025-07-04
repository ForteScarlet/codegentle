package love.forte.codegentle.common.naming.internal

import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.TypeRef

/**
 *
 * @author ForteScarlet
 */
internal class TypeVariableNameImpl(
    override val name: String,
    override val bounds: List<TypeRef<*>>,
) : TypeVariableName {
    // TODO emit/emitTo
}
