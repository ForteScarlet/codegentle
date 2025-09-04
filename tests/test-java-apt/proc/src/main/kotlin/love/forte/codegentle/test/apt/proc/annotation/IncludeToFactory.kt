package love.forte.codegentle.test.apt.proc.annotation

/**
 * 标记在一个 static fun 上，会被拷贝整合到一个 `Factories` 中。
 *
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class IncludeToFactory
