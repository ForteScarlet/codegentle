# 命名系统文档

CodeGentle 命名系统提供了一套全面的类和实用工具，用于表示和操作代码生成中使用的各种类型的名称。本文档涵盖了三个主要模块的命名类：`codegentle-common`、`codegentle-java` 和 `codegentle-kotlin`。

## 概述

命名系统建立在一个接口和类的层次结构之上，这些接口和类表示编程语言构造的不同方面：

- **类型名称**：表示类名、基本类型、泛型类型、数组等
- **包名称**：表示包/命名空间结构
- **成员名称**：表示类成员，如方法、字段和属性
- **实用工具类**：提供预定义常量和辅助函数

## 模块结构

### 通用模块（`codegentle-common`）

通用模块为所有命名功能提供基础，具有平台无关的接口和实现：

- [**核心命名类**](common-naming.md) - TypeName、ClassName、PackageName、MemberName
- [**泛型类型名称**](generic-types.md) - ParameterizedTypeName、TypeVariableName、WildcardTypeName、ArrayTypeName

### Java 模块（`codegentle-java`）

Java 模块使用 Java 特定的实用工具和常量扩展了通用功能：

- [**Java 命名实用工具**](java-naming.md) - JavaClassNames、JavaPrimitiveTypeNames、JavaAnnotationNames

### Kotlin 模块（`codegentle-kotlin`）

Kotlin 模块提供 Kotlin 特定的命名类和实用工具：

- [**Kotlin 命名实用工具**](kotlin-naming.md) - KotlinClassNames、KotlinLambdaTypeName、KotlinAnnotationNames

## 快速开始示例

### 基本用法

```kotlin
// 创建简单的类名
val stringClass = ClassName("java.lang", "String")

// 创建包名
val javaLang = "java.lang".parseToPackageName()

// 创建成员名
val valueOf = MemberName("java.lang.String", "valueOf")
```

### 使用预定义常量

```kotlin
// Java 预定义类型
val javaString = JavaClassNames.STRING
val javaInt = JavaPrimitiveTypeNames.INT

// Kotlin 预定义类型  
val kotlinString = KotlinClassNames.STRING
val kotlinUnit = KotlinClassNames.UNIT
```

### DSL 和构建器模式

```kotlin
// 使用 DSL 创建参数化类型
val listOfString = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())

// 创建 Kotlin lambda 类型
val lambdaType = KotlinLambdaTypeName(KotlinClassNames.STRING.ref()) {
    addParameter(KotlinClassNames.INT.ref())
    suspend(true)
}
```

## 文档结构

每个文档文件专注于命名系统的特定方面：

1. **[通用命名类](common-naming.md)** - 基础接口和类
2. **[泛型类型名称](generic-types.md)** - 处理泛型、类型变量和通配符
3. **[Java 命名实用工具](java-naming.md)** - Java 特定的常量和实用工具
4. **[Kotlin 命名实用工具](kotlin-naming.md)** - Kotlin 特定的类型和功能

## 主要功能

### DSL 支持
大多数命名类提供 DSL（领域特定语言）扩展，使代码生成更具可读性和可维护性。

### 类型安全
所有命名类都在设计时考虑了类型安全，在适当的地方使用泛型类型和密封接口。

### 平台兼容性
命名系统在所有支持的 CodeGentle 平台（JVM、JavaScript、Native 等）上都能工作。

### 导入管理
命名系统与代码编写器集成，自动处理导入和限定名称。
