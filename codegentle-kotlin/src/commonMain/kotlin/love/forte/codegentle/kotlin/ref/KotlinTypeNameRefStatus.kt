package love.forte.codegentle.kotlin.ref

import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.*
import love.forte.codegentle.kotlin.InternalKotlinCodeGentleApi
import love.forte.codegentle.kotlin.ref.internal.KotlinTypeNameRefStatusImpl

@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface KotlinTypeNameRefStatus : TypeNameRefStatus {
    public val annotations: List<AnnotationRef>
    public val nullable: Boolean

    public companion object : TypeNameRefStatusBuilderFactory<KotlinTypeNameRefStatus, KotlinTypeNameRefStatusBuilder> {
        override fun createBuilder(): KotlinTypeNameRefStatusBuilder = KotlinTypeNameRefStatusBuilder()
    }
}

/**
 * A builder for [KotlinTypeNameRefStatus].
 */
public class KotlinTypeNameRefStatusBuilder @PublishedApi internal constructor() :
    AnnotationRefCollector<KotlinTypeNameRefStatusBuilder>,
    TypeNameRefStatusBuilder<KotlinTypeNameRefStatus> {
    public var nullable: Boolean = false
    private val annotations: MutableList<AnnotationRef> = mutableListOf()

    override fun addAnnotation(ref: AnnotationRef): KotlinTypeNameRefStatusBuilder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): KotlinTypeNameRefStatusBuilder = apply {
        annotations.addAll(refs)
    }

    @OptIn(InternalKotlinCodeGentleApi::class)
    override fun build(): KotlinTypeNameRefStatus {
        return KotlinTypeNameRefStatusImpl(
            annotations = annotations.toList(),
            nullable = nullable
        )
    }
}

public typealias KotlinTypeRefBuilderDsl<T> =
    TypeRefBuilder<T, KotlinTypeNameRefStatus, KotlinTypeNameRefStatusBuilder>.() -> Unit

/**
 * Create a [TypeRef] with [T] and [KotlinTypeNameRefStatus].
 *
 * @see TypeRef
 */
public inline fun <T : TypeName> T.kotlinRef(
    block: KotlinTypeRefBuilderDsl<T> = {}
): TypeRef<T> = ref(KotlinTypeNameRefStatus, block)

/**
 * ```Kotlin
 * this as? JavaTypeNameRefStatus
 * ```
 */
public val TypeNameRefStatus.kotlinOrNull: KotlinTypeNameRefStatus?
    get() = this as? KotlinTypeNameRefStatus?

/**
 * ```Kotlin
 * status as? JavaTypeNameRefStatus
 * ```
 */
public val TypeRef<*>.kotlinStatusOrNull: KotlinTypeNameRefStatus?
    get() = status as? KotlinTypeNameRefStatus?
