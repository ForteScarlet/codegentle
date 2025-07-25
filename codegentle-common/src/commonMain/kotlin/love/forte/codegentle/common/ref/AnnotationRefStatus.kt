package love.forte.codegentle.common.ref

/**
 * A reference status for annotations.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface AnnotationRefStatus

// Builders

@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface AnnotationRefStatusBuilder<out T : AnnotationRefStatus> {
    public fun build(): T
}

@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface AnnotationRefStatusBuilderFactory<out T : AnnotationRefStatus, out B : AnnotationRefStatusBuilder<T>> {
    public fun createBuilder(): B
}
