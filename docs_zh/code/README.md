# CodeValue 和 CodePart

CodeValue 和 CodePart 是 codegentle 的核心代码表示 API，提供了灵活且类型安全的方式来构建代码片段。

## 概述

### CodeValue

`CodeValue` 是代码值的抽象表示，由多个 `CodePart` 组成。它使用**占位符模式**来安全地插入代码元素，避免了简单字符串拼接可能产生的问题。

**核心特性：**
- 使用 `%V` 作为占位符
- 支持多种构建方式（简单创建、格式化字符串、Builder DSL）
- 类型安全的代码生成
- 支持缩进、语句、控制流等结构

### CodePart

`CodePart` 是 CodeValue 的组成单元，分为两大类：
- `CodeSimplePart` - 简单文本部分，直接输出
- `CodeArgumentPart` - 参数部分，对应占位符 `%V`，提供特殊处理（类型引用、字符串转义、缩进等）

## 快速开始

### 基础示例

```kotlin
// 1. 空 CodeValue
val empty = CodeValue()

// 2. 简单字符串
val simple = CodeValue("val x = 10")

// 3. 使用占位符
val withPlaceholder = CodeValue("val name = %V") {
    addValue(CodePart.string("codegentle"))
}
// 输出: val name = "codegentle"

// 4. 多个占位符
val multiple = CodeValue("fun %V(): %V = %V") {
    addValue(CodePart.name("hello"))
    addValue(CodePart.type(ClassName("kotlin", "String")))
    addValue(CodePart.string("world"))
}
// 输出: fun hello(): kotlin.String = "world"
```

### Builder DSL

```kotlin
val code = CodeValue {
    addStatement("val x = %V", CodePart.literal(42))
    addStatement("val y = %V", CodePart.string("test"))

    inControlFlow("if (x > 0)") {
        addStatement("println(%V)", CodePart.string("positive"))
    }
}
```

### 控制流

```kotlin
val ifElse = CodeValue {
    beginControlFlow("if (condition)")
    addCode("statement1\n")
    nextControlFlow("else")
    addCode("statement2\n")
    endControlFlow()
}
```

## 主要用途

### 1. 类型安全的代码生成

使用 `CodePart.type()` 确保类型引用正确：

```kotlin
val className = ClassName("com.example", "User")
val code = CodeValue("val user: %V = null") {
    addValue(CodePart.type(className))
}
```

### 2. 自动字符串转义

使用 `CodePart.string()` 自动处理引号和特殊字符：

```kotlin
val str = CodeValue("val message = %V") {
    addValue(CodePart.string("Hello \"World\""))
}
// 输出: val message = "Hello \"World\""
```

### 3. 结构化代码构建

使用缩进和控制流构建复杂结构：

```kotlin
val functionCode = CodeValue {
    addCode("fun example() {\n")
    indent()
    beginControlFlow("while (running)")
    addStatement("process()")
    endControlFlow()
    unindent()
    addCode("}\n")
}
```

## CodePart 类型概览

### 文本与值
- `CodeSimplePart` - 简单文本
- `Literal` - 字面量值（无转义）
- `Name` - 标识符名称
- `Str` - 字符串（带引号和转义）

### 类型引用
- `Type` - 类型名称引用
- `TypeRef` - 类型引用对象

### 格式控制
- `Indent` / `Unindent` - 缩进控制
- `StatementBegin` / `StatementEnd` - 语句边界
- `WrappingSpace` - 智能换行空格
- `ZeroWidthSpace` - 零宽空格
- `Newline` - 换行符

### 结构控制
- `ControlFlow` - 控制流（BEGIN/NEXT/END）
- `OtherCodeValue` - 嵌套 CodeValue

### 特殊
- `Skip` - 跳过占位符，输出 `%V` 本身

## 进阶使用

### 组合 CodeValue

```kotlin
val part1 = CodeValue("val x = 1")
val part2 = CodeValue("val y = 2")
val combined = part1 + part2
```

### 嵌套 CodeValue

```kotlin
val inner = CodeValue("inner content")
val outer = CodeValue("outer %V end") {
    addValue(CodePart.otherCodeValue(inner))
}
```

### 自定义控制流

```kotlin
val tryBlock = CodeValue {
    tryControlFlow()
    addStatement("riskyOperation()")
    catchControlFlow("Exception e")
    addStatement("handleError(e)")
    finallyControlFlow()
    addStatement("cleanup()")
    endControlFlow()
}
```

## 相关文档

- [CodeValue 详细文档](CodeValue.md)
- [CodePart 详细文档](CodePart.md)

## 注意事项

1. **占位符匹配**：占位符 `%V` 的数量必须与提供的参数数量一致
2. **Builder 复用**：Builder 对象可以多次 `build()`，但建议创建新实例
3. **缩进管理**：确保 `indent()` 和 `unindent()` 成对使用
4. **控制流匹配**：控制流的 `begin`、`next`、`end` 必须正确配对
5. **线程安全**：CodeValue 和 CodePart 实例不保证线程安全

## 性能建议

- 优先使用格式化字符串方式创建简单 CodeValue
- 对于复杂逻辑，使用 Builder DSL
- 避免过度嵌套 CodeValue
- 大量代码生成时考虑使用 SpecBuilder API
