# CodeValue API 详解

## 概述

`CodeValue` 是代码片段的抽象表示，由一系列 `CodePart` 组成。它提供了类型安全的代码构建方式，使用 `%V` 作为占位符来插入各种代码元素。

## 接口定义

```kotlin
interface CodeValue {
    val parts: List<CodePart>
}
```

## 占位符

CodeValue 使用 `%V` 作为统一占位符标记，用于标识需要插入参数的位置。

```kotlin
CodeValue("val x = %V")  // %V 将被替换为提供的参数
```

## 创建方式

### 1. 空实例

```kotlin
fun CodeValue(): CodeValue
```

创建一个空的 CodeValue 实例：

```kotlin
val empty = CodeValue()
// empty.parts.isEmpty() == true
```

### 2. 从 CodePart 列表创建

```kotlin
fun CodeValue(parts: List<CodePart>): CodeValue
fun CodeValue(part: CodePart): CodeValue
```

示例：

```kotlin
val parts = listOf(
    CodePart.simple("val x = "),
    CodePart.literal(42)
)
val code = CodeValue(parts)
```

### 3. 格式化字符串方式（推荐）

#### 3.1 基础格式化

```kotlin
fun CodeValue(format: String, block: CodeValueSingleFormatBuilderDsl = {}): CodeValue
```

使用格式化字符串和 DSL 块：

```kotlin
// 无参数
val simple = CodeValue("val x = 10")

// 单个参数
val withArg = CodeValue("val name = %V") {
    addValue(CodePart.string("codegentle"))
}

// 多个参数
val multiple = CodeValue("fun %V(): %V = %V") {
    addValue(CodePart.name("getName"))
    addValue(CodePart.type(stringType))
    addValue(CodePart.string("default"))
}
```

#### 3.2 使用 vararg 参数

```kotlin
fun CodeValue(format: String, vararg argumentParts: CodeArgumentPart): CodeValue
fun CodeValue(format: String, argumentParts: Iterable<CodeArgumentPart>): CodeValue
```

直接传递参数：

```kotlin
val code = CodeValue(
    "fun %V(): %V",
    CodePart.name("test"),
    CodePart.type(intType)
)
```

### 4. Builder DSL 方式

```kotlin
fun CodeValue(block: CodeValueBuilderDsl): CodeValue
```

使用 Builder 构建复杂代码：

```kotlin
val code = CodeValue {
    addStatement("val x = %V", CodePart.literal(10))
    addStatement("val y = %V", CodePart.literal(20))

    inControlFlow("if (x > y)") {
        addStatement("println(%V)", CodePart.string("x is greater"))
    }
}
```

## CodeValueBuilder

`CodeValueBuilder` 是构建复杂 CodeValue 的主要工具。

### 主要方法

#### addCode

添加代码片段：

```kotlin
// 添加其他 CodeValue
fun addCode(codeValue: CodeValue): CodeValueBuilder

// 添加简单字符串
fun addCode(format: String): CodeValueBuilder

// 添加格式化代码
fun addCode(format: String, vararg argumentParts: CodeArgumentPart): CodeValueBuilder

// 使用 DSL 添加格式化代码
fun addCode(format: String, block: CodeValueSingleFormatBuilder.() -> Unit): CodeValueBuilder
```

示例：

```kotlin
CodeValue {
    addCode("val x = ")
    addCode("%V", CodePart.literal(42))
    addCode("\n")
}
```

#### addStatement

添加语句（自动处理语句边界）：

```kotlin
fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): CodeValueBuilder
fun addStatement(codeValue: CodeValue): CodeValueBuilder
fun addStatement(format: String, block: CodeValueSingleFormatBuilderDsl = {}): CodeValueBuilder
```

示例：

```kotlin
CodeValue {
    addStatement("val name = %V", CodePart.string("test"))
    addStatement("val count = %V", CodePart.literal(0))
}
// 输出（Java）:
// val name = "test";
// val count = 0;
```

#### 缩进控制

```kotlin
fun indent(): CodeValueBuilder
fun unindent(): CodeValueBuilder
```

示例：

```kotlin
CodeValue {
    addCode("class Example {\n")
    indent()
    addStatement("val x = 1")
    addStatement("val y = 2")
    unindent()
    addCode("}\n")
}
```

#### 清除内容

```kotlin
fun clear(): CodeValueBuilder
```

清空 Builder 中的所有内容，允许重用：

```kotlin
val builder = CodeValue.builder()
builder.addCode("first")
builder.clear()
builder.addCode("second")
val code = builder.build()  // 只包含 "second"
```

#### 构建

```kotlin
fun build(): CodeValue
```

从 Builder 创建最终的 CodeValue：

```kotlin
val code = CodeValue.builder()
    .addStatement("println(%V)", CodePart.string("Hello"))
    .build()
```

## CodeValueSingleFormatBuilder

专门用于单一格式字符串的 Builder。

### 主要方法

#### addValue

添加单个参数：

```kotlin
fun addValue(argument: CodeArgumentPart): CodeValueSingleFormatBuilder
```

#### addValues

批量添加参数：

```kotlin
fun addValues(vararg arguments: CodeArgumentPart): CodeValueSingleFormatBuilder
fun addValues(arguments: Iterable<CodeArgumentPart>): CodeValueSingleFormatBuilder
```

### 使用示例

```kotlin
// 方式 1：使用 DSL
val code1 = CodeValue("val %V: %V = %V") {
    addValue(CodePart.name("user"))
    addValue(CodePart.type(userType))
    addValue(CodePart.literal("null"))
}

// 方式 2：使用 builder
val code2 = CodeValue.builder("val %V: %V = %V")
    .addValue(CodePart.name("user"))
    .addValue(CodePart.type(userType))
    .addValue(CodePart.literal("null"))
    .build()
```

### 参数验证

Builder 会验证占位符和参数的匹配：

```kotlin
// 错误：缺少参数
CodeValue("val %V = %V") {
    addValue(CodePart.name("x"))
    // 抛出异常：Miss argument in index 2
}

// 错误：多余参数
CodeValue("val x = 1") {
    addValue(CodePart.literal(42))
    // 抛出异常：redundant argument
}
```

## 扩展功能

### isEmpty / isNotEmpty

```kotlin
fun CodeValue.isEmpty(): Boolean
fun CodeValue.isNotEmpty(): Boolean
```

检查 CodeValue 是否为空：

```kotlin
val empty = CodeValue()
empty.isEmpty()  // true

val notEmpty = CodeValue("code")
notEmpty.isNotEmpty()  // true
```

### 组合 CodeValue

```kotlin
operator fun CodeValue.plus(codeValue: CodeValue): CodeValue
```

合并两个 CodeValue：

```kotlin
val part1 = CodeValue("val x = 1\n")
val part2 = CodeValue("val y = 2\n")
val combined = part1 + part2
// 输出:
// val x = 1
// val y = 2
```

## 控制流 API

### 基础控制流

```kotlin
fun CodeValueBuilder.beginControlFlow(codeValue: CodeValue): CodeValueBuilder
fun CodeValueBuilder.beginControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl = {}): CodeValueBuilder

fun CodeValueBuilder.nextControlFlow(codeValue: CodeValue): CodeValueBuilder
fun CodeValueBuilder.nextControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl = {}): CodeValueBuilder

fun CodeValueBuilder.endControlFlow(): CodeValueBuilder
fun CodeValueBuilder.endControlFlow(codeValue: CodeValue): CodeValueBuilder
fun CodeValueBuilder.endControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl = {}): CodeValueBuilder
```

示例：

```kotlin
// if-else
CodeValue {
    beginControlFlow("if (condition)")
    addStatement("doSomething()")
    nextControlFlow("else")
    addStatement("doOther()")
    endControlFlow()
}

// do-while（Java 会自动添加分号）
CodeValue {
    beginControlFlow("do")
    addStatement("action()")
    endControlFlow("while (running)")
}
```

### 便捷控制流 API

#### inControlFlow

```kotlin
fun CodeValueBuilder.inControlFlow(
    codeValue: CodeValue,
    block: CodeValueBuilderDsl
): CodeValueBuilder

fun CodeValueBuilder.inControlFlow(
    beginCode: CodeValue,
    endCode: CodeValue,
    block: CodeValueBuilderDsl
): CodeValueBuilder
```

示例：

```kotlin
CodeValue {
    inControlFlow("if (x > 0)") {
        addStatement("println(%V)", CodePart.string("positive"))
    }
}

// do-while 简化版
CodeValue {
    inControlFlow(CodeValue("do"), CodeValue("while (running)")) {
        addStatement("process()")
    }
}
```

#### 条件控制流

```kotlin
fun CodeValueBuilder.ifControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl = {}): CodeValueBuilder
fun CodeValueBuilder.elseIfControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl = {}): CodeValueBuilder
fun CodeValueBuilder.elseControlFlow(): CodeValueBuilder
```

示例：

```kotlin
CodeValue {
    ifControlFlow("x > 0")
    addStatement("println(%V)", CodePart.string("positive"))
    elseIfControlFlow("x < 0")
    addStatement("println(%V)", CodePart.string("negative"))
    elseControlFlow()
    addStatement("println(%V)", CodePart.string("zero"))
    endControlFlow()
}
```

#### 循环控制流

```kotlin
fun CodeValueBuilder.whileControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl = {}): CodeValueBuilder
fun CodeValueBuilder.doControlFlow(): CodeValueBuilder
fun CodeValueBuilder.doWhileEndControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl = {}): CodeValueBuilder
```

示例：

```kotlin
// while 循环
CodeValue {
    whileControlFlow("i < 10")
    addStatement("process(i)")
    addStatement("i++")
    endControlFlow()
}

// do-while 循环
CodeValue {
    doControlFlow()
    addStatement("action()")
    doWhileEndControlFlow("running")
}
```

#### 异常处理

```kotlin
fun CodeValueBuilder.tryControlFlow(): CodeValueBuilder
fun CodeValueBuilder.catchControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl = {}): CodeValueBuilder
fun CodeValueBuilder.finallyControlFlow(): CodeValueBuilder
```

示例：

```kotlin
CodeValue {
    tryControlFlow()
    addStatement("riskyOperation()")
    catchControlFlow("IOException e")
    addStatement("handleIOError()")
    catchControlFlow("Exception e")
    addStatement("handleError()")
    finallyControlFlow()
    addStatement("cleanup()")
    endControlFlow()
}
```

## 高级用法

### Context Receiver 语法糖

在 `CodeValueBuilder` 上下文中，可以使用字符串调用语法：

```kotlin
context(builder: CodeValueBuilder)
operator fun String.invoke(block: CodeValueSingleFormatBuilder.() -> Unit): CodeValueBuilder
```

示例：

```kotlin
val builder = CodeValue.builder()
with(builder) {
    "val %V = %V" {
        addValue(CodePart.name("x"))
        addValue(CodePart.literal(10))
    }
}
```

### 嵌套结构

```kotlin
CodeValue {
    beginControlFlow("try")

    whileControlFlow("hasNext()")

    ifControlFlow("item.isValid()")
    addStatement("process(item)")
    elseControlFlow()
    addStatement("skip(item)")
    endControlFlow()

    endControlFlow()  // while

    catchControlFlow("Exception e")
    addStatement("handleError(e)")
    endControlFlow()  // try
}
```

## 最佳实践

### 1. 选择合适的创建方式

```kotlin
// 简单代码：使用格式化字符串
val simple = CodeValue("val x = %V", CodePart.literal(42))

// 复杂逻辑：使用 Builder DSL
val complex = CodeValue {
    addStatement("// Initialize")
    addStatement("val x = %V", CodePart.literal(0))

    inControlFlow("for (i in 1..10)") {
        addStatement("x += i")
    }

    addStatement("return x")
}
```

### 2. 避免过度嵌套

```kotlin
// 不推荐：过度嵌套
val nested = CodeValue {
    addCode("%V", CodePart.otherCodeValue(
        CodeValue("%V") {
            addValue(CodePart.otherCodeValue(
                CodeValue("value")
            ))
        }
    ))
}

// 推荐：扁平化
val inner = CodeValue("value")
val outer = CodeValue("%V") {
    addValue(CodePart.otherCodeValue(inner))
}
```

### 3. 使用 addStatement 处理语句

```kotlin
// 推荐：使用 addStatement
CodeValue {
    addStatement("val x = 1")
    addStatement("val y = 2")
}

// 不推荐：手动添加分号和换行
CodeValue {
    addCode("val x = 1;\n")
    addCode("val y = 2;\n")
}
```

### 4. 正确管理缩进

```kotlin
CodeValue {
    addCode("class Example {\n")
    indent()  // 增加缩进

    addStatement("val field = 1")

    addCode("fun method() {\n")
    indent()  // 再次增加缩进
    addStatement("println(field)")
    unindent()  // 减少缩进
    addCode("}\n")

    unindent()  // 减少缩进
    addCode("}\n")
}
```

### 5. 复用 CodeValue

```kotlin
// 定义可复用的代码片段
val logStatement = CodeValue("println(%V)") {
    addValue(CodePart.string("Debug"))
}

// 在多处使用
CodeValue {
    addCode(logStatement)
    addStatement("doWork()")
    addCode(logStatement)
}
```

## 注意事项

1. **占位符匹配**：占位符数量必须与参数数量严格匹配
2. **Builder 可变性**：Builder 是可变的，build() 后仍可继续修改
3. **线程安全**：CodeValue 和 Builder 不是线程安全的
4. **内存占用**：大量嵌套会增加内存占用，考虑直接使用 Spec API
5. **语言差异**：Java 和 Kotlin 的 Writer 对相同 CodeValue 可能产生不同输出（如分号）

## 相关 API

- [CodePart 详细文档](CodePart.md)
- [CodeValue 和 CodePart 概述](README.md)
