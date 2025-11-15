package test.app

import test.ksp.GenerateBackup

/**
 * 测试扩展函数类型参数。
 * 这个函数的参数是 String.() -> Unit，应该在生成的备份函数中保持相同的类型。
 */
@GenerateBackup
fun runWithReceiver(block: String.() -> Unit) {
    "Hello".block()
}

/**
 * 测试普通函数类型参数。
 * 这个函数的参数是 (String) -> Unit，应该在生成的备份函数中保持相同的类型。
 */
@GenerateBackup
fun runWithParameter(block: (String) -> Unit) {
    block("Hello")
}

/**
 * 测试多个参数，包括扩展函数类型和普通类型。
 */
@GenerateBackup
fun complexFunction(
    name: String,
    builder: StringBuilder.() -> Unit,
    callback: (Int) -> String
): String {
    val sb = StringBuilder()
    sb.builder()
    return callback(name.length) + sb.toString()
}

/**
 * 测试带有多个参数的扩展函数类型。
 */
@GenerateBackup
fun multiParamReceiver(block: String.(Int, Boolean) -> Unit) {
    "Test".block(42, true)
}

/**
 * 测试 suspend 扩展函数类型。
 */
@GenerateBackup
suspend fun suspendWithReceiver(block: suspend String.() -> Unit) {
    "Suspend".block()
}

/**
 * 测试带有单个 context receiver 的函数类型。
 * 参数类型：context(String) () -> Unit
 */
@GenerateBackup
fun runWithContext(block: context(String) () -> Unit) {
    context("Context") {
        block()
    }
}

/**
 * 测试带有 context receiver 和扩展函数类型的组合。
 * 参数类型：context(String) Int.() -> Unit
 */
@GenerateBackup
fun runWithContextAndReceiver(block: context(String) Int.() -> Unit) {
    with("Context") {
        42.block()
    }
}

/**
 * 测试带有多个 context receivers 的函数类型。
 * 参数类型：context(String, Int) () -> Unit
 */
@GenerateBackup
fun runWithMultipleContexts(block: context(String, Int) () -> Unit) {
    context("Context", 42) {
        block()
    }
}

/**
 * 测试带有 context receivers、扩展 receiver 和参数的复杂函数类型。
 * 参数类型：context(String, Int) StringBuilder.(Boolean) -> Unit
 */
@GenerateBackup
fun complexContextFunction(block: context(String, Int) StringBuilder.(Boolean) -> Unit) {
    context("Context", 42) {
        StringBuilder().block(true)
    }
}

/**
 * 测试带有 context receiver 的 suspend 函数类型。
 * 参数类型：context(String) suspend () -> Unit
 */
@GenerateBackup
suspend fun suspendWithContext(block: suspend context(String) () -> Unit) {
    context("Context") {
        block()
    }
}

/**
 * 测试带有 context receiver 的 suspend 函数类型。
 * 参数类型：context(String) suspend () -> Unit
 */
@GenerateBackup
suspend fun suspendWithContextAndReceiver(block: suspend context(String) StringBuilder.() -> Unit) {
    context("Context") {
        StringBuilder().block()
    }
}
