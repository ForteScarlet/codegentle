# CodePart API 详解

## 概述

`CodePart` 是 `CodeValue` 的组成单元。每个 CodeValue 由多个 CodePart 组成，这些 CodePart 协同工作以生成最终的代码输出。

## 类型层次

```
CodePart (sealed class)
├── CodeSimplePart - 简单文本，直接输出
└── CodeArgumentPart (sealed class) - 参数部分，对应占位符 %V
    ├── Skip - 跳过占位符
    ├── Literal - 字面量值
    ├── Name - 标识符名称
    ├── Str - 字符串（带引号和转义）
    ├── Type - 类型名称引用
    ├── TypeRef - 类型引用对象
    ├── Indent - 增加缩进
    ├── Unindent - 减少缩进
    ├── StatementBegin - 语句开始标记
    ├── StatementEnd - 语句结束标记
    ├── WrappingSpace - 智能换行空格
    ├── ZeroWidthSpace - 零宽空格
    ├── Newline - 换行符
    ├── ControlFlow - 控制流标记（BEGIN/NEXT/END）
    └── OtherCodeValue - 嵌套的 CodeValue
```

## 占位符

```kotlin
const val PLACEHOLDER: String = "%V"
```

CodePart 使用 `%V` 作为统一占位符，标识需要插入 `CodeArgumentPart` 的位置。

## 工厂方法

所有 CodePart 通过 `CodePart.Companion` 的工厂方法创建，标注了 `@CodePartFactory` 注解。

### CodeSimplePart

#### simple

```kotlin
fun simple(value: String): CodeSimplePart
```

创建简单文本部分，内容直接输出，不做任何处理：

```kotlin
val part = CodePart.simple("val x = 10")
```

## CodeArgumentPart 子类

### 特殊标记

#### skip

```kotlin
fun skip(): CodeArgumentPart
```

跳过此占位符，直接输出 `%V` 字符本身：

```kotlin
val code = CodeValue("before %V after") {
    addValue(CodePart.skip())
}
// 输出: before %V after
```

**使用场景**：
- 在文档或注释中显示占位符语法
- 作为转义机制

### 值类型

#### literal

```kotlin
fun literal(value: Any?): CodeArgumentPart
```

输出字面量值，不做任何转义或引号处理：

```kotlin
// 数字
CodePart.literal(42)           // 输出: 42
CodePart.literal(3.14)         // 输出: 3.14

// 布尔值
CodePart.literal(true)         // 输出: true

// null
CodePart.literal(null)         // 输出: null

// 对象（使用 toString）
CodePart.literal(customObj)    // 输出: customObj.toString()
```

**使用场景**：
- 数字字面量
- 布尔值
- null 值
- 已经格式化的字符串

#### name

```kotlin
fun name(nameValue: Any?): CodeArgumentPart
```

输出标识符名称，支持 `CharSequence` 和 `Named` 接口：

```kotlin
// 字符串
CodePart.name("variableName")  // 输出: variableName

// Named 对象
val memberName = MemberName("com.example", "function")
CodePart.name(memberName)      // 输出: function

// 其他类型会抛出 IllegalArgumentException
```

**使用场景**：
- 变量名
- 函数名
- 参数名
- 成员引用

#### string

```kotlin
fun string(value: String?): CodeArgumentPart
fun string(value: String?, handleSpecialCharacter: Boolean): CodeArgumentPart
```

输出字符串字面量，自动添加双引号并转义特殊字符：

```kotlin
// 基础用法
CodePart.string("hello")              // 输出: "hello"

// null 值
CodePart.string(null)                 // 输出: null

// 转义引号
CodePart.string("Say \"Hi\"")         // 输出: "Say \"Hi\""

// 转义特殊字符
CodePart.string("line1\nline2")       // 输出: "line1\nline2"

// 禁用特殊字符处理（handleSpecialCharacter = false）
CodePart.string("$variable", false)   // 输出: "$variable"
CodePart.string("$variable", true)    // 输出: "\$variable" (Kotlin 中转义 $)
```

**转义规则**：
- `\"` - 双引号
- `\n` - 换行符
- `\r` - 回车符
- `\t` - 制表符
- `\\` - 反斜杠
- `\$` - 美元符号（Kotlin 字符串插值）

**使用场景**：
- 字符串常量
- 日志消息
- 异常消息

### 类型引用

#### type (TypeName)

```kotlin
fun type(type: TypeName): CodeArgumentPart
```

输出类型名称引用：

```kotlin
val className = ClassName("com.example", "User")
CodePart.type(className)
// 输出: com.example.User（完全限定名，Writer 会处理 import）
```

**使用场景**：
- 变量类型声明
- 函数返回类型
- 泛型参数
- 类型转换

#### type (TypeRef)

```kotlin
fun type(type: TypeRef<*>): CodeArgumentPart
```

输出类型引用对象：

```kotlin
val typeRef = ClassName("kotlin", "String").ref()
CodePart.type(typeRef)
// 输出: kotlin.String
```

**TypeRef vs TypeName**：
- `TypeRef` 包含更多元数据（注解、可空性等）
- `TypeName` 仅包含类型名称信息
- 两者在输出时行为类似，但 TypeRef 提供更丰富的上下文

### 格式控制

#### indent

```kotlin
fun indent(levels: Int = 1): CodeArgumentPart
```

增加缩进级别：

```kotlin
CodeValue {
    addCode("class Example {\n")
    addCode("%V", CodePart.indent())    // 增加 1 级缩进
    addCode("val x = 1\n")
    addCode("%V", CodePart.indent(2))   // 增加 2 级缩进
    addCode("val y = 2\n")
}
```

**注意事项**：
- 缩进级别累积
- 默认增加 1 级
- 必须与 `unindent` 配对使用

#### unindent

```kotlin
fun unindent(levels: Int = 1): CodeArgumentPart
```

减少缩进级别：

```kotlin
CodeValue {
    addCode("%V", CodePart.indent())
    addCode("indented code\n")
    addCode("%V", CodePart.unindent())
    addCode("back to normal\n")
}
```

**注意事项**：
- 不能减少到负数级别
- 默认减少 1 级
- 必须与 `indent` 配对

#### statementBegin / statementEnd

```kotlin
fun statementBegin(): CodeArgumentPart
fun statementEnd(): CodeArgumentPart
```

标记语句的开始和结束，用于处理多行语句的缩进：

```kotlin
CodeValue {
    addCode("%V", CodePart.statementBegin())
    addCode("val longVariableName = someFunction()\n")
    addCode("    .chainedCall()\n")        // 多行语句的后续行会双倍缩进
    addCode("    .anotherCall()\n")
    addCode("%V", CodePart.statementEnd())
}
```

**作用**：
- 自动处理多行语句的对齐
- Java 的 `statementEnd` 会添加分号
- Kotlin 的 `statementEnd` 不添加分号

**使用场景**：
- 通过 `addStatement()` 自动添加
- 手动构建复杂语句时使用

#### wrappingSpace

```kotlin
fun wrappingSpace(): CodeArgumentPart
```

智能换行空格，根据行长度决定输出空格还是换行：

```kotlin
CodeValue {
    addCode("val result = %V", CodePart.wrappingSpace())
    addCode("longMethodName(param1, param2, param3)")
}
// 如果行未超过限制：val result = longMethodName(...)
// 如果行超过限制：
// val result =
//     longMethodName(...)
```

**特性**：
- 默认行宽限制 100 列
- 换行后自动添加缩进
- 适用于长表达式

#### zeroWidthSpace

```kotlin
fun zeroWidthSpace(): CodeArgumentPart
```

零宽空格，提供换行机会但不输出可见字符：

```kotlin
CodeValue {
    addCode("veryLongMethodName(")
    addCode("%V", CodePart.zeroWidthSpace())
    addCode("param1, param2, param3)")
}
// 如果行超过限制会在零宽空格处换行
```

**与 wrappingSpace 的区别**：
- `wrappingSpace`：不换行时输出空格
- `zeroWidthSpace`：不换行时不输出任何字符

#### newline

```kotlin
fun newline(): CodeArgumentPart
```

显式输出换行符：

```kotlin
CodePart.newline()  // 输出: \n（使用 Writer 的换行策略）
```

**使用场景**：
- 显式控制换行位置
- 在格式化字符串中使用 `\n` 通常更方便

### 控制流

#### ControlFlow

控制流标记，包含三种位置：BEGIN、NEXT、END。

```kotlin
fun beginControlFlow(): CodeArgumentPart
fun beginControlFlow(codeValue: CodeValue): CodeArgumentPart

fun nextControlFlow(): CodeArgumentPart
fun nextControlFlow(codeValue: CodeValue): CodeArgumentPart

fun endControlFlow(): CodeArgumentPart
fun endControlFlow(codeValue: CodeValue): CodeArgumentPart
```

**Position 枚举**：
- `BEGIN` - 开始控制流块
- `NEXT` - 继续控制流块（如 else、catch）
- `END` - 结束控制流块

**基础用法**：

```kotlin
// if 语句
CodeValue {
    addCode("%V", CodePart.beginControlFlow(CodeValue("if (condition)")))
    addCode("statement;\n")
    addCode("%V", CodePart.endControlFlow())
}
// Java 输出:
// if (condition) {
//     statement;
// }

// if-else 语句
CodeValue {
    addCode("%V", CodePart.beginControlFlow(CodeValue("if (x > 0)")))
    addCode("positive();\n")
    addCode("%V", CodePart.nextControlFlow(CodeValue("else")))
    addCode("nonPositive();\n")
    addCode("%V", CodePart.endControlFlow())
}
// Java 输出:
// if (x > 0) {
//     positive();
// } else {
//     nonPositive();
// }
```

**带参数的扩展方法**：

```kotlin
// 使用格式化字符串
fun beginControlFlow(format: String, vararg arguments: CodeArgumentPart): CodeArgumentPart
fun beginControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl): CodeArgumentPart

fun nextControlFlow(format: String, vararg arguments: CodeArgumentPart): CodeArgumentPart
fun nextControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl): CodeArgumentPart

fun endControlFlow(format: String, vararg arguments: CodeArgumentPart): CodeArgumentPart
fun endControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl): CodeArgumentPart
```

示例：

```kotlin
// 使用扩展方法
CodeValue {
    addCode("%V", CodePart.beginControlFlow("if (%V)") {
        addValue(CodePart.name("condition"))
    })
    addCode("action();\n")
    addCode("%V", CodePart.endControlFlow())
}
```

**语言差异**：

Java 输出：
```java
if (condition) {
    action();
}

do {
    action();
} while (running);  // 注意分号
```

Kotlin 输出：
```kotlin
if (condition) {
    action()
}

do {
    action()
} while (running)  // 无分号
```

**复杂控制流**：

```kotlin
// try-catch-finally
CodeValue {
    addCode("%V", CodePart.beginControlFlow(CodeValue("try")))
    addCode("riskyOperation();\n")

    addCode("%V", CodePart.nextControlFlow(CodeValue("catch (Exception e)")))
    addCode("handleError();\n")

    addCode("%V", CodePart.nextControlFlow(CodeValue("finally")))
    addCode("cleanup();\n")

    addCode("%V", CodePart.endControlFlow())
}
```

**使用场景**：
- if-else 条件分支
- try-catch-finally 异常处理
- do-while 循环
- when/switch 语句（通过 NEXT 连接多个 case）

### 嵌套 CodeValue

#### otherCodeValue

```kotlin
fun otherCodeValue(value: CodeValue): CodeArgumentPart
```

在占位符位置嵌入另一个 CodeValue：

```kotlin
val innerCode = CodeValue("inner content")

val outerCode = CodeValue("outer [%V] end") {
    addValue(CodePart.otherCodeValue(innerCode))
}
// 输出: outer [inner content] end
```

**与直接使用 addCode 的区别**：

```kotlin
// 方式 1：使用 otherCodeValue（占位符）
CodeValue("prefix %V suffix") {
    addValue(CodePart.otherCodeValue(innerCode))
}

// 方式 2：使用 addCode（直接添加）
CodeValue {
    addCode("prefix ")
    addCode(innerCode)
    addCode(" suffix")
}

// 两者输出相同，但语义不同：
// - otherCodeValue: 表示"这是一个参数"
// - addCode: 表示"顺序添加代码片段"
```

**使用场景**：
- 动态生成的代码片段
- 可复用的代码模板
- 条件性代码插入

## CodePart 类型详解

### Skip

```kotlin
data object Skip : CodeArgumentPart()
```

跳过占位符，输出 `%V` 本身。

**属性**：
- 单例对象（data object）
- 不包含任何数据

### Literal

```kotlin
class Literal(val value: Any?) : CodeArgumentPart()
```

**属性**：
- `value: Any?` - 字面量值

**输出规则**：
- 直接调用 `value.toString()`
- null 输出 "null"

### Name

```kotlin
class Name(val name: String?, val originalValue: Any?) : CodeArgumentPart()
```

**属性**：
- `name: String?` - 解析后的名称字符串
- `originalValue: Any?` - 原始输入值（用于调试）

**支持的输入类型**：
- `CharSequence` - 直接转换为字符串
- `Named` - 调用 `name` 属性
- 其他类型 - 抛出 `IllegalArgumentException`

### Str

```kotlin
class Str(val value: String?, val handleSpecialCharacter: Boolean) : CodeArgumentPart()
```

**属性**：
- `value: String?` - 字符串内容
- `handleSpecialCharacter: Boolean` - 是否处理特殊字符

**转义处理**：
- Kotlin：转义 `$`（字符串插值）
- Java：不需要额外转义
- 通用：转义 `"`、`\n`、`\r`、`\t`、`\\`

### Type

```kotlin
class Type(val type: TypeName) : CodeArgumentPart()
```

**属性**：
- `type: TypeName` - 类型名称

**输出**：
- 完全限定名（Writer 负责管理 import）

### TypeRef

```kotlin
class TypeRef(val type: love.forte.codegentle.common.ref.TypeRef<*>) : CodeArgumentPart()
```

**属性**：
- `type: TypeRef<*>` - 类型引用

**与 Type 的区别**：
- 包含注解信息
- 包含可空性信息
- 包含其他类型元数据

### Indent / Unindent

```kotlin
class Indent(val levels: Int = 1) : CodeArgumentPart()
class Unindent(val levels: Int = 1) : CodeArgumentPart()
```

**属性**：
- `levels: Int` - 缩进级别数（默认 1）

**累积规则**：
- 每次 `indent(n)` 增加 n 级
- 每次 `unindent(n)` 减少 n 级
- 缩进级别不能小于 0

### StatementBegin / StatementEnd

```kotlin
data object StatementBegin : CodeArgumentPart()
data object StatementEnd : CodeArgumentPart()
```

**语言行为差异**：

Java：
```java
statement;  // StatementEnd 添加分号
```

Kotlin：
```kotlin
statement  // StatementEnd 不添加分号
```

### WrappingSpace / ZeroWidthSpace

```kotlin
data object WrappingSpace : CodeArgumentPart()
data object ZeroWidthSpace : CodeArgumentPart()
```

**行为对比**：

| 场景 | WrappingSpace | ZeroWidthSpace |
|------|---------------|----------------|
| 行未超限 | 输出空格 | 不输出 |
| 行超限 | 换行 + 缩进 | 换行 + 缩进 |

### Newline

```kotlin
data object Newline : CodeArgumentPart()
```

**输出**：
- 使用 Writer 配置的换行符（`\n` 或 `\r\n`）

### ControlFlow

```kotlin
class ControlFlow(val position: Position, val codeValue: CodeValue?) : CodeArgumentPart()

enum class Position {
    BEGIN, NEXT, END
}
```

**属性**：
- `position: Position` - 控制流位置
- `codeValue: CodeValue?` - 可选的代码内容

**模式**：
1. `BEGIN` + content - 开始块（如 `if (condition)`）
2. `NEXT` + content - 继续块（如 `else if (condition)`）
3. `END` + content - 结束块（如 `while (condition)`）
4. `END` + null - 简单结束

### OtherCodeValue

```kotlin
class OtherCodeValue(val value: CodeValue) : CodeArgumentPart()
```

**属性**：
- `value: CodeValue` - 嵌套的 CodeValue

**递归处理**：
- Writer 会递归处理嵌套的 CodeValue
- 继承当前的缩进级别
- 保持上下文状态

## 使用示例

### 综合示例：生成 Java 方法

```kotlin
val methodCode = CodeValue {
    addCode("public %V %V(",
        CodePart.type(returnType),
        CodePart.name("calculateSum")
    )

    // 参数列表
    addCode("%V %V",
        CodePart.type(intType),
        CodePart.name("a")
    )
    addCode("%V", CodePart.wrappingSpace())
    addCode(", %V %V",
        CodePart.type(intType),
        CodePart.name("b")
    )

    addCode(") {\n")
    indent()

    // 日志语句
    addStatement(
        "logger.info(%V)",
        CodePart.string("Calculating sum")
    )

    // 条件判断
    beginControlFlow("if (a < 0 || b < 0)")
    addStatement(
        "throw new %V(%V)",
        CodePart.type(illegalArgumentExceptionType),
        CodePart.string("Negative numbers not allowed")
    )
    endControlFlow()

    // 返回语句
    addStatement("return a + b")

    unindent()
    addCode("}\n")
}
```

输出：
```java
public int calculateSum(int a, int b) {
    logger.info("Calculating sum");
    if (a < 0 || b < 0) {
        throw new IllegalArgumentException("Negative numbers not allowed");
    }
    return a + b;
}
```

### 综合示例：生成 Kotlin 属性

```kotlin
val propertyCode = CodeValue {
    addCode("var %V: %V",
        CodePart.name("username"),
        CodePart.type(stringType)
    )

    indent()

    // getter
    addCode("\n%V", CodePart.indent())
    addCode("get() {\n")
    indent()
    addStatement("return field.trim()")
    unindent()
    addCode("}\n")

    // setter
    addCode("set(value) {\n")
    indent()

    beginControlFlow("if (value.isBlank())")
    addStatement(
        "throw %V(%V)",
        CodePart.type(illegalArgumentExceptionType),
        CodePart.string("Username cannot be blank")
    )
    endControlFlow()

    addStatement("field = value")

    unindent()
    addCode("}\n")

    unindent()
}
```

输出：
```kotlin
var username: String
    get() {
        return field.trim()
    }
    set(value) {
        if (value.isBlank()) {
            throw IllegalArgumentException("Username cannot be blank")
        }
        field = value
    }
```

## 最佳实践

### 1. 选择合适的 CodePart 类型

```kotlin
// ✅ 正确：使用 string 处理用户输入
CodePart.string(userInput)

// ❌ 错误：使用 literal 可能导致注入
CodePart.literal(userInput)

// ✅ 正确：使用 name 处理标识符
CodePart.name(variableName)

// ❌ 错误：使用 string 会添加引号
CodePart.string(variableName)

// ✅ 正确：使用 type 处理类型引用
CodePart.type(className)

// ❌ 错误：使用 literal 可能导致错误的导入
CodePart.literal(className.toString())
```

### 2. 正确配对缩进

```kotlin
// ✅ 正确：成对使用
CodeValue {
    addCode("%V", CodePart.indent())
    addCode("content\n")
    addCode("%V", CodePart.unindent())
}

// ❌ 错误：未配对
CodeValue {
    addCode("%V", CodePart.indent())
    addCode("content\n")
    // 忘记 unindent
}
```

### 3. 使用高级 API 而非直接使用 CodePart

```kotlin
// ✅ 推荐：使用 CodeValueBuilder 的方法
CodeValue {
    ifControlFlow("x > 0")
    addStatement("process()")
    endControlFlow()
}

// ⚠️ 可行但繁琐：直接使用 CodePart
CodeValue {
    addCode("%V", CodePart.beginControlFlow(CodeValue("if (x > 0)")))
    addCode("%V", CodePart.statementBegin())
    addCode("process()")
    addCode("%V", CodePart.statementEnd())
    addCode("%V", CodePart.endControlFlow())
}
```

### 4. 复用常见模式

```kotlin
// 定义可复用的工厂函数
fun nullCheck(variable: String): CodeValue = CodeValue {
    beginControlFlow("if (%V == null)") {
        addValue(CodePart.name(variable))
    }
    addStatement(
        "throw new %V(%V)",
        CodePart.type(npeType),
        CodePart.string("$variable must not be null")
    )
    endControlFlow()
}

// 在多处使用
CodeValue {
    addCode(nullCheck("param1"))
    addCode(nullCheck("param2"))
}
```

### 5. 处理长行

```kotlin
// 使用 wrappingSpace 自动处理长行
CodeValue {
    addCode("val result = someObject.veryLongMethodName(")
    addCode("%V", CodePart.wrappingSpace())
    addCode("param1,%V", CodePart.wrappingSpace())
    addCode("param2,%V", CodePart.wrappingSpace())
    addCode("param3)")
}

// 根据行长自动决定：
// 短行: val result = someObject.veryLongMethodName( param1, param2, param3)
// 长行:
// val result = someObject.veryLongMethodName(
//     param1,
//     param2,
//     param3)
```

## 注意事项

1. **类型安全**：使用正确的 CodePart 类型避免生成错误的代码
2. **转义处理**：字符串内容使用 `string()`，不要用 `literal()`
3. **缩进平衡**：确保 indent/unindent 配对
4. **控制流配对**：begin/next/end 必须正确配对
5. **语言差异**：了解 Java 和 Kotlin Writer 的行为差异
6. **性能考虑**：避免过度创建临时 CodeValue
7. **可读性**：复杂逻辑考虑使用 Spec Builder API

## 相关 API

- [CodeValue 详细文档](CodeValue.md)
- [CodeValue 和 CodePart 概述](README.md)
