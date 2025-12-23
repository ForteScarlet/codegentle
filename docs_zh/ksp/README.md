# KSP 集成文档

CodeGentle 通过扩展函数提供与 Kotlin Symbol Processing (KSP) 的无缝集成，可直接将 KSP 类型转换为 CodeGentle 的类型系统。

## 概述

KSP 集成模块提供了 KSP 类型的扩展函数，允许您在注解处理过程中直接将 KSP 符号转换为 CodeGentle 规范。

**两个模块：**
- `codegentle-common-ksp` - 类型名称和类名的通用 KSP 工具
- `codegentle-kotlin-ksp` - Kotlin 特定的 KSP 扩展，用于 TypeSpec、FunctionSpec 和 PropertySpec

## 快速开始

```kotlin
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import love.forte.codegentle.kotlin.ksp.*
import love.forte.codegentle.kotlin.spec.*

class MyProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.example.MyAnnotation")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDecl ->
            // 将 KSP 类转换为 ClassName
            val className = classDecl.toClassName()

            // 将 KSP 函数转换为 FunctionSpec
            classDecl.getAllFunctions().forEach { funcDecl ->
                val functionSpec = funcDecl.toKotlinFunctionSpec()
                // 使用 functionSpec 生成代码...
            }
        }

        return emptyList()
    }
}
```

## 文档文件

- **[类型转换](./type-conversion.md)** - 将 KSP 类型转换为 TypeName
- **[类和成员名称](./class-member-names.md)** - 将声明转换为名称
- **[Spec 转换](./spec-conversion.md)** - 将 KSP 符号转换为 Spec 对象
- **[上下文接收器](./context-receivers.md)** - 处理 KSP 中的上下文接收器

## 主要功能

### 类型名称转换

将任何 KSP 类型转换为 CodeGentle TypeName：

```kotlin
val ksType: KSType = ...
val typeName: TypeName = ksType.toTypeName()
```

支持：
- 基本类型（Boolean、Int、String 等）
- 类类型
- 数组类型
- 泛型/参数化类型
- 函数/lambda 类型（包括 suspend、接收器和上下文接收器）
- 带边界的类型变量
- 通配符类型

### 类名转换

```kotlin
val classDecl: KSClassDeclaration = ...
val className: ClassName = classDecl.toClassName()
```

### 函数转换

```kotlin
val functionDecl: KSFunctionDeclaration = ...
val functionSpec: KotlinFunctionSpec = functionDecl.toKotlinFunctionSpec()
// 自动处理：
// - 接收器（扩展函数）
// - 上下文接收器
// - Suspend 修饰符
// - 带默认值的参数
```

### 属性转换

```kotlin
val propertyDecl: KSPropertyDeclaration = ...
val propertySpec: KotlinPropertySpec = propertyDecl.toKotlinPropertySpec()
```

## 高级功能

### 上下文接收器支持

CodeGentle KSP 集成完全支持 Kotlin 2.0+ 上下文接收器：

```kotlin
// 对于如下函数：
// context(Logger, Database)
// suspend fun query(): Result

val functionDecl: KSFunctionDeclaration = ...
val spec = functionDecl.toKotlinFunctionSpec()
// spec.contextParameters 将包含 [Logger, Database]
// spec.modifiers 将包含 SUSPEND
```

通过函数类型上的 `@ContextFunctionTypeParams` 注解检测上下文接收器。

### 类型参数边界

```kotlin
// 对于类型参数如：T : Comparable<T>
val typeParam: KSTypeParameter = ...
val typeVarName: TypeVariableName = typeParam.toTypeVariableName()
// typeVarName.bounds 将包含 [Comparable<T>]
```

### 错误类型处理

KSP 集成优雅地处理 Kotlin 的 ERROR TYPE，即使在处理过程中无法验证注解：

```kotlin
// 即使注解类型显示为 <ERROR TYPE: kotlin.ExtensionFunctionType>
// 转换仍然通过检查注解名称正常工作
```

## 模块依赖

添加到 `build.gradle.kts`：

```kotlin
dependencies {
    // 用于类型名称转换
    implementation("love.forte.codegentle:codegentle-common-ksp:$version")

    // 用于 Kotlin spec 转换
    implementation("love.forte.codegentle:codegentle-kotlin-ksp:$version")
}
```

## 相关文档

- [TypeName 系统](../naming/README.md) - 理解 CodeGentle 的类型系统
- [Kotlin Specs](../spec/kotlin-specs.md) - Kotlin 规范类
- [上下文接收器](./context-receivers.md) - 详细的上下文接收器处理

## 最佳实践

1. **直接使用扩展函数** - 无需手动构造 TypeName 对象
2. **处理可空返回** - 某些函数如 `toClassNameOrNull()` 返回可空类型以确保安全
3. **检查验证** - 在转换前使用 `KSType.validate()` 以获得更好的错误处理
4. **保留元数据** - TypeRef 系统通过转换保留额外的元数据

## 示例：完整的注解处理器

参见 `/tests/test-ksp-receiver-and-contexts/proc/` 以获取使用 KSP 转换生成带接收器和上下文支持的备份函数的完整示例。
