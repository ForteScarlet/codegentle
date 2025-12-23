# 类型转换函数

本文档描述 KSP 集成模块提供的所有类型转换函数。

## 概述

CodeGentle 提供扩展函数将 KSP 类型表示转换为 CodeGentle 的 `TypeName` 层次结构。这些函数处理所有类型类别，包括基本类型、类、泛型、数组、函数类型和通配符。

## 核心类型转换

### `KSType.toTypeName(): TypeName`

**位置**: `codegentle-kotlin-ksp/KSTypeNameCross.kt`

将任何 KSP 类型转换为相应的 CodeGentle `TypeName`。

**支持的类型类别：**

1. **基本类型**
   ```kotlin
   val intType: KSType = ... // kotlin.Int
   val typeName = intType.toTypeName() // ClassName("kotlin", "Int")
   ```
   基本类型：Boolean、Byte、Short、Int、Long、Float、Double、Char、String、Unit、Nothing

2. **数组类型**
   ```kotlin
   val arrayType: KSType = ... // Array<String>
   val typeName = arrayType.toTypeName() // ArrayTypeName(String)
   ```

3. **类类型**
   ```kotlin
   val classType: KSType = ... // com.example.MyClass
   val typeName = classType.toTypeName() // ClassName("com.example", "MyClass")
   ```

4. **参数化类型**
   ```kotlin
   val listType: KSType = ... // List<String>
   val typeName = listType.toTypeName() // ParameterizedTypeName(List, [String])
   ```

5. **类型变量**
   ```kotlin
   val typeVar: KSType = ... // T
   val typeName = typeVar.toTypeName() // TypeVariableName("T")
   ```

6. **函数类型**
   ```kotlin
   val funcType: KSType = ... // (String, Int) -> Boolean
   val typeName = funcType.toTypeName() // KotlinLambdaTypeName(...)
   ```

### `KSTypeReference.toTypeName(): TypeName`

**位置**: `codegentle-kotlin-ksp/KSTypeNameCross.kt`

解析类型引用并将其转换为 `TypeName`。

```kotlin
val typeRef: KSTypeReference = parameterDecl.type
val typeName = typeRef.toTypeName()
```

### `KSDeclaration.toTypeName(): TypeName`

**位置**: `codegentle-kotlin-ksp/KSTypeNameCross.kt`

支持多种声明类型的多态转换：

```kotlin
when (val decl: KSDeclaration = ...) {
    is KSClassDeclaration -> decl.toTypeName() // -> ClassName
    is KSTypeAlias -> decl.toTypeName()        // -> 解析的底层类型
    is KSTypeParameter -> decl.toTypeName()    // -> TypeVariableName
    else -> throw IllegalArgumentException()
}
```

## 类型参数转换

### `KSTypeParameter.toTypeVariableName(): TypeVariableName`

**位置**: `codegentle-kotlin-ksp/KSTypeNameCross.kt`

转换包括边界的类型参数：

```kotlin
// 对于：<T : Comparable<T>>
val typeParam: KSTypeParameter = ...
val typeVarName = typeParam.toTypeVariableName()
// TypeVariableName("T", bounds=[Comparable<T>])
```

**功能：**
- 提取类型参数名称
- 将所有边界转换为 `TypeRef`
- 保留边界关系

## 函数类型转换

Lambda 类型转换为 `KotlinLambdaTypeName`，完全支持：
- 简单函数：`(String, Int) -> Boolean`
- 扩展函数：`String.(Int) -> Boolean` （带接收器）
- Suspend 函数：`suspend (String) -> Unit`
- 上下文接收器：`context(Logger) String.(Int) -> Unit`

详细信息请参阅完整的英文版文档。

## 最佳实践

1. **先检查验证**：转换前使用 `KSType.validate()`
2. **处理可空性**：`toClassNameOrNull()` 等函数返回可空类型
3. **使用特定函数**：知道是类时优先使用 `toClassName()` 而非 `toTypeName()`
4. **保留元数据**：考虑使用 `TypeRef` 包装器附加额外元数据

## 相关文档

- [TypeName 系统](../naming/README.md) - 理解 CodeGentle 的类型层次结构
- [上下文接收器](./context-receivers.md) - 详细的上下文接收器处理
- [Spec 转换](./spec-conversion.md) - 转换完整声明
