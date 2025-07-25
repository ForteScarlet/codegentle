package love.forte.codegentle.common

@DslMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
public annotation class BuilderMarker

@BuilderMarker
public interface BuilderDsl
