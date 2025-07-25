package love.forte.codegentle.common.ref

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.computeValueIfAbsent
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.internal.AnnotationRefImpl

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
    public val members: Map<String, List<CodeValue>>
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
    private val members: MutableMap<String, MutableList<CodeValue>> = linkedMapOf()

    override fun addMember(name: String, codeValue: CodeValue): AnnotationRefBuilder = apply {
        val values = members.computeValueIfAbsent(name) { mutableListOf() }
        values.add(codeValue)
    }

    public fun build(): AnnotationRef {
        return AnnotationRefImpl(
            typeName = className,
            members = members.mapValues { it.value.toList() }
        )
    }
}
