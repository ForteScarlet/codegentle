package love.forte.codegentle.common.ref.internal

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefStatus

/**
 *
 * @author ForteScarlet
 */
internal data class AnnotationRefImpl(
    override val typeName: ClassName,
    override val members: Map<String, AnnotationRef.MemberValue>,
    override val status: AnnotationRefStatus
) : AnnotationRef

internal class SingleMemberValueImpl(
    override val codeValue: CodeValue
) : AnnotationRef.MemberValue.Single {
    override val codeValues: List<CodeValue> = listOf(codeValue)

    override fun toString(): String {
        return "MemberValue.Single(codeValue=$codeValue)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnnotationRef.MemberValue.Single) return false

        if (codeValue != other.codeValue) return false
        if (codeValues != other.codeValues) return false

        return true
    }

    override fun hashCode(): Int {
        var result = codeValue.hashCode()
        result = 31 * result + codeValues.hashCode()
        return result
    }
}

internal class MultipleMemberValueImpl(
    override val codeValues: List<CodeValue>
) : AnnotationRef.MemberValue.Multiple {
    override fun toString(): String {
        return "MemberValue.Multiple(codeValues=$codeValues)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnnotationRef.MemberValue.Multiple) return false

        if (codeValues != other.codeValues) return false

        return true
    }

    override fun hashCode(): Int {
        return codeValues.hashCode()
    }
}
