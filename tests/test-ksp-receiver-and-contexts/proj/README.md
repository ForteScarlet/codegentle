# Test KSP - Extension Function Type Preservation Test

这个测试项目用于验证 GitHub Issue #1 的修复是否生效。

## 问题描述

在修复前，KSP 将函数类型参数从 KSType 转换为 KotlinFunctionSpec 时，扩展函数类型 `T.() -> Unit` 会被错误地转换为普通函数类型 `(T) -> Unit`。

## 测试方法

### 1. 测试函数

在 `TestFunctions.kt` 中定义了多个测试函数：

- `runWithReceiver(block: String.() -> Unit)` - 测试扩展函数类型
- `runWithParameter(block: (String) -> Unit)` - 测试普通函数类型
- `complexFunction(...)` - 测试多参数情况
- `multiParamReceiver(block: String.(Int, Boolean) -> Unit)` - 测试带参数的扩展函数类型
- `suspendWithReceiver(block: suspend String.() -> Unit)` - 测试 suspend 扩展函数类型

### 2. 预期生成

KSP 处理器应该为每个被 `@GenerateBackup` 标记的函数生成一个备份版本，例如：

**原函数**:
```kotlin
@GenerateBackup
fun runWithReceiver(block: String.() -> Unit)
```

**生成的备份函数**:
```kotlin
fun runWithReceiver_1234(block: String.() -> Unit) {
    // This is a backup of runWithReceiver
    TODO("Implement backup function")
}
```

### 3. 验证修复

如果修复生效，生成的函数应该保持：
- ✅ `String.() -> Unit` → `String.() -> Unit` (扩展函数类型保留)
- ✅ `(String) -> Unit` → `(String) -> Unit` (普通函数类型保留)
- ❌ 修复前: `String.() -> Unit` → `(String) -> Unit` (错误转换)

### 4. 构建和验证

#### 方式一：使用 IntelliJ IDEA

1. 在 IntelliJ IDEA 中打开项目
2. 右键点击 `tests:test-ksp:proj` 模块
3. 选择 "Rebuild Module"
4. 查看 `build/generated/ksp/main/kotlin/test/app/` 目录下生成的文件
5. 验证生成的函数参数类型是否正确

#### 方式二：使用 Gradle（需要 Java 11-21）

```bash
./gradlew :tests:test-ksp:proj:kspKotlin
```

查看生成的文件：
```bash
cat tests/test-ksp/proj/build/generated/ksp/main/kotlin/test/app/*Generated.kt
```

#### 方式三：手动验证处理器逻辑

查看 `GenerateBackupProcessor.kt` 中的关键代码：

```kotlin
// 使用 codegentle-kotlin-ksp 将 KSP 函数转换为 KotlinFunctionSpec
val originalSpec = function.toKotlinFunctionSpec()

// 创建备份函数 - 复制所有参数
originalSpec.parameters.forEach { addParameter(it) }
```

这里调用的 `toKotlinFunctionSpec()` 函数会：
1. 遍历函数的所有参数
2. 对每个参数调用 `toKotlinValueParameterSpec()`
3. 参数类型通过 `type.toTypeRef()` 转换
4. 如果参数类型是函数类型，会调用 `toFunctionTypeName()`
5. **关键修复点**: `toFunctionTypeName()` 现在会检查 `@kotlin.ExtensionFunctionType` 注解来正确识别扩展函数类型

## 核心修复代码

在 `codegentle-kotlin-ksp/src/main/kotlin/love/forte/codegentle/kotlin/ksp/KSTypeNameCross.kt` 中：

```kotlin
private fun KSType.toFunctionTypeName(isSuspend: Boolean): KotlinLambdaTypeName {
    return KotlinLambdaTypeName {
        suspend(isSuspend)

        val typeArgs = arguments.map { arg -> arg.toTypeRef() }

        // 检测是否为扩展函数类型
        val isExtensionFunctionType = annotations.any { annotation ->
            annotation.shortName.asString() == "ExtensionFunctionType" &&
            annotation.annotationType.resolve().declaration.qualifiedName?.asString() ==
                "kotlin.ExtensionFunctionType"
        }

        if (isExtensionFunctionType) {
            // 第一个参数是接收者类型
            val receiverType = typeArgs.first()
            receiver(receiverType)

            // 中间参数是函数参数
            for (index in 1 until typeArgs.lastIndex) {
                addParameter(KotlinValueParameterSpec.builder("", typeArgs[index]).build())
            }

            // 最后一个是返回类型
            returns(typeArgs.last())
        } else {
            // 普通函数类型处理
            // ...
        }
    }
}
```

## 预期输出示例

生成的文件应该类似于：

```kotlin
package test.app

fun runWithReceiver_3742(block: String.() -> Unit) {
    // This is a backup of runWithReceiver
    TODO("Implement backup function")
}

fun runWithParameter_8291(block: (String) -> Unit) {
    // This is a backup of runWithParameter
    TODO("Implement backup function")
}

fun complexFunction_5639(
    name: String,
    builder: StringBuilder.() -> Unit,
    callback: (Int) -> String
): String {
    // This is a backup of complexFunction
    TODO("Implement backup function")
}

fun multiParamReceiver_1847(block: String.(Int, Boolean) -> Unit) {
    // This is a backup of multiParamReceiver
    TODO("Implement backup function")
}

suspend fun suspendWithReceiver_9024(block: suspend String.() -> Unit) {
    // This is a backup of suspendWithReceiver
    TODO("Implement backup function")
}
```

注意 `String.() -> Unit` 和 `StringBuilder.() -> Unit` 等扩展函数类型被正确保留！
