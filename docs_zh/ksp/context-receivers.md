# KSP 中的上下文接收器

本文档解释 CodeGentle KSP 集成如何处理 Kotlin 2.0+ 上下文接收器。

## 概述

上下文接收器允许函数和 lambda 隐式接收上下文参数。CodeGentle KSP 集成完全支持从 KSP 注解中检测和转换上下文接收器。

## 检测机制

通过 `@ContextFunctionTypeParams` 注解检测上下文接收器：

```kotlin
// 对于如下函数：
context(Logger, Database)
suspend fun query(sql: String): Result

// KSP 用注解表示：
@ContextFunctionTypeParams(2) // 2 个上下文接收器
```

## 类型参数布局

对于带上下文接收器的函数类型，KSP 按以下方式排列类型参数：

```
context(C1, C2) T.(P1, P2) -> R
参数：[C1, C2, T, P1, P2, R]
      ↑─────↑  ↑  ↑─────↑  ↑
      上下文   │  参数   返回
              接收器
```

## 转换示例

```kotlin
// 源函数
context(Logger, Database)
suspend String.query(sql: String): Result { ... }

// KSP 处理
val funcDecl: KSFunctionDeclaration = ...
val spec = funcDecl.toKotlinFunctionSpec()

// 结果
assert(spec.contextParameters.size == 2)
assert(spec.contextParameters[0].typeRef.typeName == ClassName("", "Logger"))
assert(spec.contextParameters[1].typeRef.typeName == ClassName("", "Database"))
assert(spec.receiver == ClassName("kotlin", "String"))
assert(spec.modifiers.contains(KotlinModifier.SUSPEND))
```

## ERROR TYPE 处理

在 KSP 处理期间，注解类型可能无法验证并显示为 ERROR TYPE。CodeGentle 通过检查注解短名称和 toString() 表示来处理此问题，允许处理继续。

## 相关文档

- [类型转换](./type-conversion.md) - 如何从函数类型中提取上下文接收器
- [Spec 转换](./spec-conversion.md) - 转换带上下文的完整函数
- [Kotlin Specs](../spec/kotlin-specs.md) - KotlinContextParameterSpec 文档
