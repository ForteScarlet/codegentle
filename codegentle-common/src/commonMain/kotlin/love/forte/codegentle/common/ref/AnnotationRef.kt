package love.forte.codegentle.common.ref

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.computeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.AnnotationRef.MemberValue
import love.forte.codegentle.common.ref.internal.AnnotationRefImpl
import love.forte.codegentle.common.ref.internal.MultipleMemberValueImpl
import love.forte.codegentle.common.ref.internal.SingleMemberValueImpl

/**
 * A reference to an annotation.
 *
 * Could be emitted as [love.forte.codegentle.common.code.CodeArgumentPart.Literal]
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface AnnotationRef {
    public val typeName: ClassName
    public val members: Map<String, MemberValue>

    // TODO status? support Kotlin's `@get:, @file:, etc`

    /**
     * The [CodeValue] of annotation ref's member.
     */
    public sealed interface MemberValue {
        public val codeValues: List<CodeValue>

        /**
         * A single value member.
         */

        @SubclassOptInRequired(CodeGentleRefImplementation::class)
        public interface Single : MemberValue {
            public val codeValue: CodeValue
        }

        /**
         * An array member or a `vararg` member.
         */
        @SubclassOptInRequired(CodeGentleRefImplementation::class)
        public interface Multiple : MemberValue
    }
}

/**
 * Constructs an [AnnotationRef] instance based on the current [ClassName].
 *
 * @param block An optional lambda receiver of type [AnnotationRefBuilder]
 *              that can be used to configure the [AnnotationRef].
 *              If no block is provided, a default empty block is used.
 * @return An instance of [AnnotationRef] constructed using the [AnnotationRefBuilder].
 */
public inline fun ClassName.annotationRef(block: AnnotationRefBuilder.() -> Unit = {}): AnnotationRef {
    return AnnotationRefBuilder(this@annotationRef).apply {
        block()
    }.build()
}

/**
 * Builder for [AnnotationRef].
 */
public class AnnotationRefBuilder(public val className: ClassName) :
    BuilderDsl,
    AnnotationRefBuildable<AnnotationRefBuilder> {
    private val members = linkedMapOf<String, MemberValue>()

    override fun addMultipleMembers(
        name: String,
        codeValues: Iterable<CodeValue>
    ): AnnotationRefBuilder = apply {
        members.computeValue(name) { _, value ->
            if (value == null) {
                MultipleMemberValueImpl(codeValues.toList())
            } else {
                MultipleMemberValueImpl(value.codeValues + codeValues)
            }
        }
    }

    override fun addMember(name: String, codeValue: CodeValue): AnnotationRefBuilder = apply {
        members.computeValue(name) { _, value ->
            if (value == null) {
                SingleMemberValueImpl(codeValue)
            } else {
                MultipleMemberValueImpl(value.codeValues + codeValue)
            }
        }
    }

    public fun build(): AnnotationRef {
        return AnnotationRefImpl(
            typeName = className,
            members = members.toMap(linkedMapOf())
        )
    }
}
