package love.forte.codegentle.kotlin.ref

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.*
import love.forte.codegentle.kotlin.ref.internal.KotlinAnnotationRefStatusImpl

/**
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface KotlinAnnotationRefStatus : AnnotationRefStatus {
    public val useSite: KotlinAnnotationUseSite?

    public companion object :
        AnnotationRefStatusBuilderFactory<KotlinAnnotationRefStatus, KotlinAnnotationRefStatusBuilder> {
        override fun createBuilder(): KotlinAnnotationRefStatusBuilder = KotlinAnnotationRefStatusBuilder()
    }
}

public class KotlinAnnotationRefStatusBuilder @PublishedApi internal constructor() :
    AnnotationRefStatusBuilder<KotlinAnnotationRefStatus> {
    public var useSite: KotlinAnnotationUseSite? = null

    override fun build(): KotlinAnnotationRefStatus {
        return KotlinAnnotationRefStatusImpl(useSite)
    }
}

public typealias KotlinAnnotationRefBuilder =
    AnnotationRefBuilder<KotlinAnnotationRefStatus, KotlinAnnotationRefStatusBuilder>

public typealias KotlinAnnotationRefBuilderDsl =
    AnnotationRefBuilder<KotlinAnnotationRefStatus, KotlinAnnotationRefStatusBuilder>.() -> Unit

public inline fun ClassName.kotlinAnnotationRef(
    block: KotlinAnnotationRefBuilderDsl = {}
): AnnotationRef = annotationRef(KotlinAnnotationRefStatus, block)

public val AnnotationRefStatus.kotlinOrNull: KotlinAnnotationRefStatus?
    get() = this as? KotlinAnnotationRefStatus

public val AnnotationRef.kotlinStatusOrNull: KotlinAnnotationRefStatus?
    get() = status as? KotlinAnnotationRefStatus

// AnnotationRefCollector

public inline fun <B : AnnotationRefCollector<B>> B.addKotlinAnnotationRef(
    className: ClassName,
    block: KotlinAnnotationRefBuilderDsl = {}
): B = addAnnotationRef(className.kotlinAnnotationRef(block))
