package love.forte.codegentle.common.naming.internal

import love.forte.codegentle.common.naming.*

/**
 *
 * @author ForteScarlet
 */
internal data class MemberNameImpl(
    override val packageName: PackageName,
    override val enclosingClassName: ClassName?,
    override val name: String,
) : MemberName {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MemberName) return false

        return this contentEquals other
    }

    override fun hashCode(): Int {
        return contentHashCode()
    }

    override fun toString(): String {
        return canonicalName
    }
}
