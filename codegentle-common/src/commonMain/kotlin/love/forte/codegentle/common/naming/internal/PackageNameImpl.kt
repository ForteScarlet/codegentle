package love.forte.codegentle.common.naming.internal

import love.forte.codegentle.common.naming.PackageName
import love.forte.codegentle.common.naming.appendTo
import love.forte.codegentle.common.naming.isEmpty

/**
 *
 * @author ForteScarlet
 */
internal data class PackageNameImpl(
    override val previous: PackageName?,
    override val name: String
) : PackageName {
    override fun toString(): String {
        if (previous?.isEmpty != false) {
            return name
        }

        return buildString(::appendTo)
    }
}
