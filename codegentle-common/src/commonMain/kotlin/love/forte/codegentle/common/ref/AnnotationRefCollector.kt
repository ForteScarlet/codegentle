package love.forte.codegentle.common.ref

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.naming.ClassName
import kotlin.jvm.JvmInline

/**
 *
 *
 * ```Kotlin
 * builder {
 *    addAnnotation(...)
 * }
 * ```
 *
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface AnnotationRefCollector<B : AnnotationRefCollector<B>> {
    public fun addAnnotation(ref: AnnotationRef): B

    public fun addAnnotations(refs: Iterable<AnnotationRef>): B

    public fun addAnnotations(vararg refs: AnnotationRef): B =
        addAnnotations(refs.asList())
}

public inline fun <S : AnnotationRefStatus,
    SB : AnnotationRefStatusBuilder<S>,
    B : AnnotationRefCollector<B>> B.addAnnotation(
    className: ClassName,
    factory: AnnotationRefStatusBuilderFactory<S, SB>,
    block: AnnotationRefBuilderDsl<S, SB> = {}
): B = addAnnotation(className.annotationRef(factory, block))

public inline fun <B : AnnotationRefCollector<B>> B.addAnnotation(
    className: ClassName,
    block: BasicAnnotationRefBuilderDsl = {}
): B = addAnnotation(className.annotationRef(block))

/**
 * Some operations for [AnnotationRefCollector].
 */
@JvmInline
public value class AnnotationRefCollectorOps<B : AnnotationRefCollector<B>>
@PublishedApi
internal constructor(public val collector: B) {
    public fun add(ref: AnnotationRef): B = collector.addAnnotation(ref)
    public fun addAll(refs: Iterable<AnnotationRef>): B = collector.addAnnotations(refs)
    public fun addAll(vararg refs: AnnotationRef): B = collector.addAnnotations(refs.asList())
}

public inline val <B : AnnotationRefCollector<B>> B.annotations: AnnotationRefCollectorOps<B>
    get() = AnnotationRefCollectorOps(this)

/**
 * DSL block with [AnnotationRefCollectorOps].
 */
public inline fun <B : AnnotationRefCollector<B>> B.annotations(
    block: AnnotationRefCollectorOps<B>.() -> Unit
) {
    annotations.block()
}
