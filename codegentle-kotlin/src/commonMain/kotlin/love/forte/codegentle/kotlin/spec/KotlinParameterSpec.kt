package love.forte.codegentle.kotlin.spec

/**
 *
 * @author ForteScarlet
 */
public sealed interface KotlinParameterSpec : KotlinSpec {
    // TODO context parameter 和 value parameter 的基类

}


// TODO 考虑提供二者互相转化的扩展函数
// public fun KotlinParameterSpec.toContextParameter(): KotlinContextParameterSpec =
//     this as? KotlinContextParameterSpec ?: TODO()
//
//
// public fun KotlinParameterSpec.toValueParameter(): KotlinValueParameterSpec =
//     this as? KotlinValueParameterSpec ?: TODO()
