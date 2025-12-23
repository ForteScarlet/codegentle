# 文件生成 API

`codegentle` 文件生成模块提供用于创建完整 Java 和 Kotlin 源文件的 API。

## 模块

### codegentle-java
使用 `JavaFile` API 生成 Java 源文件。
- [JavaFile 文档](./JavaFile.md)

### codegentle-kotlin
使用 `KotlinFile` API 生成 Kotlin 源文件。
- [KotlinFile 文档](./KotlinFile.md)

## 平台支持

### 通用 API（所有平台）
`JavaFile` 和 `KotlinFile` 在所有平台（JVM、JS、Native、Wasm）上提供这些 API：
- `writeTo(Appendable, Strategy)` - 写入任何 `Appendable`
- `write*String()` 扩展 - 生成字符串输出
- `toRelativePath()` - 获取相对于源根的文件路径

### 仅 JVM API
以下方法仅在 JVM 目标上可用：

**JavaFile（JVM）**：
- `writeTo(path: Path)` - 写入 `java.nio.file.Path`
- `writeTo(directory: File)` - 写入 `java.io.File`
- `writeTo(filer: Filer, vararg originatingElements: Element)` - APT 集成

**KotlinFile（JVM）**：
- `writeTo(path: Path)` - 写入 `java.nio.file.Path`
- `writeTo(directory: File)` - 写入 `java.io.File`
- `writeTo(filer: Filer, vararg originatingElements: Element)` - APT 集成

> **重要**：针对非 JVM 平台时，使用 `writeTo(Appendable, Strategy)` 配合目标平台适当的文件 I/O。

---

## JavaFile API 概述

`JavaFile` 是 CodeGentle 库中用于生成完整 Java 源文件的核心 API。

## 核心概念

一个 Java 源文件通常包含以下结构：
- 文件注释（可选）
- 包声明
- 导入语句（普通导入和静态导入）
- 主类型定义（public 类）
- 次级类型定义（同文件中的其他非 public 类）

`JavaFile` 完整封装了这些结构，并提供了灵活的构建器 API 和 DSL 风格的扩展函数。

## 主要特性

### 1. 智能导入管理
- 自动收集所有需要的类型并生成导入语句
- 支持跳过 `java.lang` 包的导入
- 支持静态导入
- 避免同包类型的导入
- 支持强制使用全限定名的类型

### 2. 次级类型支持
一个 Java 文件中可以包含多个类型定义，但只有一个可以是 public 的（主类型）。`JavaFile` 支持添加任意数量的非 public 次级类型。

### 3. 灵活的输出方式
- 输出到 `Appendable`（如 `StringBuilder`）
- 输出到 `Path` 或 `File`（JVM 平台）
- 输出到 `Filer`（APT 注解处理器）
- 转换为 `JavaFileObject`

### 4. 可定制的代码格式
- 支持自定义缩进（默认 4 个空格）
- 支持不同的写入策略（`JavaWriteStrategy`）

## 快速开始

### 基础用法

```kotlin
import love.forte.codegentle.java.*
import love.forte.codegentle.common.naming.parseToPackageName

// 创建类型
val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "HelloWorld") {
    addModifier(JavaModifier.PUBLIC)
}

// 创建文件
val javaFile = JavaFile(
    packageName = "com.example".parseToPackageName(),
    type = typeSpec
)

// 输出
println(javaFile.toString())
```

输出：
```java
package com.example;

public class HelloWorld {
}
```

### DSL 风格

```kotlin
val javaFile = JavaFile("com.example", typeSpec) {
    addFileComment("自动生成的文件，请勿手动修改")
    addStaticImport(ClassName("java.util", "Collections"), "emptyList", "emptyMap")
    skipJavaLangImports(true)
    indent("  ") // 使用 2 个空格缩进
}
```

---

## KotlinFile API 概述

`KotlinFile` 是 CodeGentle 库中用于生成完整 Kotlin 源文件的核心 API。

### 核心概念

一个 Kotlin 源文件可以包含：
- 文件注释（可选）
- 文件级注解（使用 `@file:` 语法）
- 包声明
- 导入语句（普通导入和静态导入）
- 顶层类型定义（类、接口、对象、枚举等）
- 顶层函数
- 顶层属性
- 脚本代码（`.kts` 文件）

### 主要特性

#### 1. 智能导入管理
- 自动收集所有需要的类型并生成导入语句
- 支持跳过 `kotlin` 包的导入
- 支持静态导入
- 避免同包类型的导入
- 支持强制使用全限定名的类型

#### 2. 多元素文件支持
Kotlin 文件可以包含多个顶层类型、函数和属性，`KotlinFile` 完全支持这些元素的灵活组合。

#### 3. 脚本文件支持
支持生成 Kotlin 脚本文件（`.kts`），可以包含直接执行的代码语句。

#### 4. 文件级注解
支持 Kotlin 特有的文件级注解语法 `@file:Annotation`。

### 快速开始

#### 基础用法

```kotlin
import love.forte.codegentle.kotlin.*
import love.forte.codegentle.common.naming.parseToPackageName

// 创建类型
val typeSpec = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "HelloWorld") {
    addModifier(KotlinModifier.PUBLIC)
}

// 创建文件
val kotlinFile = KotlinFile(
    packageName = "com.example".parseToPackageName(),
    type = typeSpec
)

// 输出
println(kotlinFile.writeToKotlinString())
```

输出：
```kotlin
package com.example

class HelloWorld
```

#### DSL 风格

```kotlin
val kotlinFile = KotlinFile("com.example", typeSpec) {
    addFileComment("自动生成的文件，请勿手动修改")
    addStaticImport(ClassName("kotlin.collections", "Collections"), "emptyList")
    indent("    ") // 使用 4 个空格缩进
}
```

---

## API 文档

详细的 API 文档请参考：
- [JavaFile 详细文档](./JavaFile.md) - 完整的 Java File API 说明和使用示例
- [KotlinFile 详细文档](./KotlinFile.md) - 完整的 Kotlin File API 说明和使用示例

## 相关链接

- [JavaTypeSpec](../spec/JavaTypeSpec.md) - Java 类型定义 API
- [KotlinTypeSpec](../spec/KotlinTypeSpec.md) - Kotlin 类型定义 API
- [PackageName](../naming/PackageName.md) - 包名处理
- [JavaWriteStrategy](../strategy/JavaWriteStrategy.md) - Java 写入策略
- [KotlinWriteStrategy](../strategy/KotlinWriteStrategy.md) - Kotlin 写入策略
