package test.ksp

/**
 * 标记一个函数，让 KSP 处理器生成它的备份版本。
 * 备份函数的名字会加上一个随机后缀。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class GenerateBackup
