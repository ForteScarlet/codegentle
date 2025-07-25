package love.forte.codegentle.java.ref

import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.*
import love.forte.codegentle.java.ref.internal.JavaTypeNameRefStatusImpl

@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface JavaTypeNameRefStatus : TypeNameRefStatus {
    public val annotations: List<AnnotationRef>

    public companion object : TypeNameRefStatusBuilderFactory<JavaTypeNameRefStatus, JavaTypeNameRefStatusBuilder> {
        override fun createBuilder(): JavaTypeNameRefStatusBuilder = JavaTypeNameRefStatusBuilder()
    }
}

/**
 * A builder for [JavaTypeNameRefStatus].
 */
public class JavaTypeNameRefStatusBuilder @PublishedApi internal constructor() :
    AnnotationRefCollector<JavaTypeNameRefStatusBuilder>,
    TypeNameRefStatusBuilder<JavaTypeNameRefStatus> {
    private val annotations: MutableList<AnnotationRef> = mutableListOf()

    override fun addAnnotation(ref: AnnotationRef): JavaTypeNameRefStatusBuilder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): JavaTypeNameRefStatusBuilder = apply {
        annotations.addAll(refs)
    }

    override fun build(): JavaTypeNameRefStatus {
        return JavaTypeNameRefStatusImpl(
            annotations = annotations.toList()
        )
    }
}

public typealias JavaTypeRefBuilderDsl<T> = TypeRefBuilder<T, JavaTypeNameRefStatus, JavaTypeNameRefStatusBuilder>.() -> Unit

/**
 * Create a [TypeRef] with [T] and [JavaTypeNameRefStatus].
 *
 * @see TypeRef
 */
public inline fun <T : TypeName> T.javaRef(
    block: JavaTypeRefBuilderDsl<T> = {}
): TypeRef<T> = ref(JavaTypeNameRefStatus, block)

/**
 * ```Kotlin
 * status as? JavaTypeNameRefStatus
 * ```
 */
public val TypeNameRefStatus.javaOrNull: JavaTypeNameRefStatus?
    get() = this as? JavaTypeNameRefStatus?
