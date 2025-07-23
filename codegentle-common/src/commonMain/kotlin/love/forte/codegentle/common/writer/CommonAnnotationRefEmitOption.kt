package love.forte.codegentle.common.writer

public sealed class CommonAnnotationRefEmitOption : AnnotationRefEmitOption {
    public data object Inline : CommonAnnotationRefEmitOption()
}
