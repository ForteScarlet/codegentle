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
public interface AnnotationRefCollectable<B : AnnotationRefCollectable<B>> : BuilderDsl {
    // private val annotations: MutableList<AnnotationRef> = mutableListOf()

    public fun addAnnotationRef(ref: AnnotationRef): B

    public fun addAnnotationRefs(refs: Iterable<AnnotationRef>): B

    public fun addAnnotationRefs(vararg refs: AnnotationRef): B =
        addAnnotationRefs(refs.asList())
}

public inline fun <B : AnnotationRefCollectable<B>> B.addAnnotationRef(
    className: ClassName,
    block: AnnotationRefBuilder.() -> Unit = {}
): B = addAnnotationRef(AnnotationRefBuilder(className).apply(block).build())

/**
 * Some operations for [AnnotationRefCollectable].
 */
@JvmInline
public value class AnnotationRefCollectableOps<B : AnnotationRefCollectable<B>>
@PublishedApi
internal constructor(public val collectable: B) {
    public fun add(ref: AnnotationRef): B = collectable.addAnnotationRef(ref)
    public fun addAll(refs: Iterable<AnnotationRef>): B = collectable.addAnnotationRefs(refs)
    public fun addAll(vararg refs: AnnotationRef): B = collectable.addAnnotationRefs(refs.asList())

}

public inline val <B : AnnotationRefCollectable<B>> B.annotationRefs: AnnotationRefCollectableOps<B>
    get() = AnnotationRefCollectableOps(this)

/**
 * DSL block with [AnnotationRefCollectableOps].
 */
public inline fun <B : AnnotationRefCollectable<B>> B.annotationRefs(
    block: AnnotationRefCollectableOps<B>.() -> Unit
) {
    annotationRefs.block()
}
