package love.forte.codegentle.common.ref

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.naming.ClassName
import kotlin.jvm.JvmInline

/**
 *
 *
 * ```Kotlin
 * builder {
 *    addAnnotationRef(...)
 * }
 * ```
 *
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface AnnotationRefCollector<B : AnnotationRefCollector<B>> : BuilderDsl {
    // private val annotations: MutableList<AnnotationRef> = mutableListOf()

    public fun addAnnotationRef(ref: AnnotationRef): B

    public fun addAnnotationRefs(refs: Iterable<AnnotationRef>): B

    public fun addAnnotationRefs(vararg refs: AnnotationRef): B =
        addAnnotationRefs(refs.asList())
}

public inline fun <B : AnnotationRefCollector<B>> B.addAnnotationRef(
    className: ClassName,
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(AnnotationRefBuilder(className).apply(block).build())

/**
 * Some operations for [AnnotationRefCollector].
 */
@JvmInline
public value class AnnotationRefCollectorOps<B : AnnotationRefCollector<B>>
@PublishedApi
internal constructor(public val collector: B) {
    public fun add(ref: AnnotationRef): B = collector.addAnnotationRef(ref)
    public fun addAll(refs: Iterable<AnnotationRef>): B = collector.addAnnotationRefs(refs)
    public fun addAll(vararg refs: AnnotationRef): B = collector.addAnnotationRefs(refs.asList())

}

public inline val <B : AnnotationRefCollector<B>> B.annotationRefs: AnnotationRefCollectorOps<B>
    get() = AnnotationRefCollectorOps(this)

/**
 * DSL block with [AnnotationRefCollectorOps].
 */
public inline fun <B : AnnotationRefCollector<B>> B.annotationRefs(
    block: AnnotationRefCollectorOps<B>.() -> Unit
) {
    annotationRefs.block()
}
