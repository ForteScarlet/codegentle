# CodePart

`CodePart` is the fundamental building block of code generation in CodeGentle. Each `CodePart` represents a specific element of source code, from simple text to complex structural elements like indentation and control flow.

## Class Hierarchy

```kotlin
sealed class CodePart
    ├── CodeSimplePart
    └── CodeArgumentPart (sealed)
            ├── Skip
            ├── Literal
            ├── Name
            ├── Str
            ├── Type
            ├── TypeRef
            ├── Indent
            ├── Unindent
            ├── StatementBegin
            ├── StatementEnd
            ├── WrappingSpace
            ├── ZeroWidthSpace
            ├── Newline
            ├── ControlFlow
            └── OtherCodeValue
```

## CodeSimplePart

Represents plain text without any special processing.

### Factory Method

```kotlin
CodePart.simple(value: String): CodeSimplePart
```

### Usage

```kotlin
val part = CodePart.simple("println(\"Hello\")")
// Outputs exactly: println("Hello")
```

Used internally when creating `CodeValue` instances without placeholders:

```kotlin
CodeValue("val x = 10")
// Internally creates: CodeSimplePart("val x = 10")
```

## CodeArgumentPart

Abstract base for all code parts that can be used as placeholder arguments. These are the parts you pass to replace `%V` placeholders in format strings.

## CodeArgumentPart Types

### Skip

Outputs the literal placeholder string `%V` without replacement.

#### Factory Method

```kotlin
CodePart.skip(): CodeArgumentPart
```

#### Usage

```kotlin
CodeValue("The placeholder is %V", CodePart.skip())
// Results in: The placeholder is %V
```

### Literal

Emits a value as-is, without quotes or escaping. Use for numbers, boolean values, constants, and expressions.

#### Factory Method

```kotlin
CodePart.literal(value: Any?): CodeArgumentPart
```

#### Usage

```kotlin
// Numbers
CodeValue("val count = %V", CodePart.literal(42))
// Results in: val count = 42

// Booleans
CodeValue("val isActive = %V", CodePart.literal(true))
// Results in: val isActive = true

// Constants/expressions
CodeValue("val max = %V", CodePart.literal("Integer.MAX_VALUE"))
// Results in: val max = Integer.MAX_VALUE

// Null
CodeValue("val optional = %V", CodePart.literal(null))
// Results in: val optional = null
```

### Name

Emits a name (variable, function, class, etc.). Accepts `String`, `CharSequence`, or any object implementing the `Named` interface.

#### Factory Method

```kotlin
CodePart.name(nameValue: Any?): CodeArgumentPart
```

#### Usage

```kotlin
// Simple string name
CodeValue("val %V = 10", CodePart.name("counter"))
// Results in: val counter = 10

// Function call
CodeValue("%V()", CodePart.name("calculateTotal"))
// Results in: calculateTotal()

// Named object
val parameter = SomeNamedObject(name = "userId")
CodeValue("val %V: Int", CodePart.name(parameter))
// Results in: val userId: Int
```

### Str

Emits a string value wrapped in double quotes with proper escaping. Use for string literals.

#### Factory Methods

```kotlin
CodePart.string(value: String?): CodeArgumentPart
CodePart.string(value: String?, handleSpecialCharacter: Boolean): CodeArgumentPart
```

#### Parameters

- `value`: The string value to emit
- `handleSpecialCharacter`: If `true` (default), escapes special characters like `$` in Kotlin

#### Usage

```kotlin
// Basic string
CodeValue("val message = %V", CodePart.string("Hello, World"))
// Results in: val message = "Hello, World"

// String with quotes
CodeValue("val quote = %V", CodePart.string("He said \"Hi\""))
// Results in: val quote = "He said \"Hi\""

// String with special characters
CodeValue("val template = %V", CodePart.string("Price: $100"))
// Results in Kotlin: val template = "Price: \$100"

// Null string
CodeValue("val empty = %V", CodePart.string(null))
// Results in: val empty = null
```

#### Special Character Handling

When `handleSpecialCharacter` is `true`:
- **Kotlin**: Escapes `$` to `\$` to prevent string interpolation
- **Java**: No special handling (double quotes are always escaped)

```kotlin
// With special character handling (default)
CodePart.string("Total: $amount", handleSpecialCharacter = true)
// Kotlin: "Total: \$amount"

// Without special character handling
CodePart.string("Total: $amount", handleSpecialCharacter = false)
// Kotlin: "Total: $amount" (may cause issues if $amount is not a variable)
```

### Type

Emits a type reference using a `TypeName` object.

#### Factory Method

```kotlin
CodePart.type(type: TypeName): CodeArgumentPart
```

#### Usage

```kotlin
// Simple type
CodeValue("val count: %V", CodePart.type(IntTypeName))
// Results in: val count: Int

// Generic type
val listType = TypeName("List", listOf(StringTypeName))
CodeValue("val items: %V", CodePart.type(listType))
// Results in: val items: List<String>

// Function parameter
CodeValue("fun process(data: %V)", CodePart.type(DataTypeName))
// Results in: fun process(data: Data)
```

### TypeRef

Emits a type reference using a `TypeRef<*>` object. `TypeRef` is a runtime type reference that can capture generic information.

#### Factory Method

```kotlin
CodePart.type(type: TypeRef<*>): CodeArgumentPart
```

#### Usage

```kotlin
// Using TypeRef
val typeRef = typeRef<List<String>>()
CodeValue("val items: %V", CodePart.type(typeRef))
// Results in: val items: List<String>

// Complex generic type
val complexRef = typeRef<Map<String, List<Int>>>()
CodeValue("val data: %V", CodePart.type(complexRef))
// Results in: val data: Map<String, List<Int>>
```

### Indent

Increases the indentation level. Each indent adds one level of indentation (typically 4 spaces or 1 tab, depending on the writer configuration).

#### Factory Method

```kotlin
CodePart.indent(levels: Int = 1): CodeArgumentPart
```

#### Usage

```kotlin
CodeValue {
    addCode("class Person {")
    addCode("%V", CodePart.indent())
    addCode("val name: String")
    addCode("%V", CodePart.unindent())
    addCode("}")
}
// Results in:
// class Person {
//     val name: String
// }

// Multiple levels
CodeValue {
    addCode("class Outer {")
    addCode("%V", CodePart.indent(2))
    addCode("val nested: String")
    addCode("%V", CodePart.unindent(2))
    addCode("}")
}
```

Typically used through `CodeValueBuilder.indent()` for cleaner code:

```kotlin
CodeValue {
    addCode("class Person {")
    indent()
    addCode("val name: String")
    unindent()
    addCode("}")
}
```

### Unindent

Decreases the indentation level.

#### Factory Method

```kotlin
CodePart.unindent(levels: Int = 1): CodeArgumentPart
```

#### Usage

```kotlin
CodeValue {
    addCode("if (condition) {")
    addCode("%V", CodePart.indent())
    addCode("doSomething()")
    addCode("%V", CodePart.unindent())
    addCode("}")
}
```

Use `CodeValueBuilder.unindent()` for cleaner syntax.

### StatementBegin

Marks the beginning of a statement. Used for multi-line statement formatting where continuation lines need double indentation.

#### Factory Method

```kotlin
CodePart.statementBegin(): CodeArgumentPart
```

#### Usage

Typically used internally by `addStatement()`:

```kotlin
// Manually
CodeValue {
    addCode("%V", CodePart.statementBegin())
    addCode("val result = someLongFunction(")
    indent()
    addCode("parameter1,")
    addCode("parameter2")
    unindent()
    addCode(")")
    addCode("%V", CodePart.statementEnd())
}

// Better: use addStatement()
CodeValue {
    addStatement("val x = %V", CodePart.literal(10))
}
```

### StatementEnd

Marks the end of a statement.

#### Factory Method

```kotlin
CodePart.statementEnd(): CodeArgumentPart
```

#### Usage

See `StatementBegin` above. Generally used through `addStatement()` rather than manually.

### WrappingSpace

Emits a space or newline depending on the current line position. Prefers to wrap lines before reaching a certain column limit (typically 100 columns).

#### Factory Method

```kotlin
CodePart.wrappingSpace(): CodeArgumentPart
```

#### Usage

```kotlin
CodeValue {
    addCode("fun veryLongFunctionName(")
    addCode("parameter1: String,%V", CodePart.wrappingSpace())
    addCode("parameter2: Int,%V", CodePart.wrappingSpace())
    addCode("parameter3: Boolean")
    addCode(")")
}
// If the line is short: fun veryLongFunctionName(parameter1: String, parameter2: Int, parameter3: Boolean)
// If the line is too long:
// fun veryLongFunctionName(parameter1: String,
//     parameter2: Int,
//     parameter3: Boolean)
```

### ZeroWidthSpace

Acts as a zero-width space that allows line wrapping at this position when lines exceed the column limit, but doesn't add any space.

#### Factory Method

```kotlin
CodePart.zeroWidthSpace(): CodeArgumentPart
```

#### Usage

```kotlin
CodeValue {
    addCode("veryLongClassName%V.%VveryLongMethodName()", 
        CodePart.zeroWidthSpace(),
        CodePart.zeroWidthSpace()
    )
}
// If line is short: veryLongClassName.veryLongMethodName()
// If line is too long:
// veryLongClassName.
//     veryLongMethodName()
```

### WrappingSpace

**Wrapping Space** - Emits a space or newline depending on line position, preferring wrapping before 100 columns.

#### Factory Method

```kotlin
CodePart.wrappingSpace(): CodeArgumentPart
```

#### Usage

```kotlin
// In a long method call
CodeValue("someMethod(%V,%V%V,%V)",
    CodePart.literal(arg1),
    CodePart.wrappingSpace(),  // Will wrap if line > 100 cols
    CodePart.literal(arg2),
    CodePart.literal(arg3)
)
// Results in either:
// someMethod(arg1, arg2, arg3)
// or (if long):
// someMethod(arg1,
//     arg2, arg3)
```

**Behavior:**
- Defers decision until flush
- Uses current indentation + 2 levels when wrapping
- Column limit: 100 characters (default)

### ZeroWidthSpace

**Zero-Width Space** - Acts as an optional line wrap point without emitting any character if not wrapped.

#### Factory Method

```kotlin
CodePart.zeroWidthSpace(): CodeArgumentPart
```

#### Usage

```kotlin
// For parameter lists that should break at specific points
CodeValue("foo(%Zarg1,%Zarg2,%Zarg3)",
    CodePart.zeroWidthSpace(),
    CodePart.literal("value1"),
    CodePart.zeroWidthSpace(),
    CodePart.literal("value2"),
    CodePart.zeroWidthSpace(),
    CodePart.literal("value3")
)
// Either: foo(arg1, arg2, arg3)
// Or wrapped: foo(
//     arg1,
//     arg2,
//     arg3
// )
```

**Behavior:**
- Emits nothing if line stays within limit
- Emits newline + indentation if wrapping needed
- Useful for optional breaking points

### Newline

Emits an explicit newline character.

#### Factory Method

```kotlin
CodePart.newline(): CodeArgumentPart
```

#### Usage

```kotlin
CodeValue("First line%VSecond line", CodePart.newline())
// Results in:
// First line
// Second line

// Multiple newlines for blank lines
CodeValue("Section 1%V%VSection 2", 
    CodePart.newline(),
    CodePart.newline()
)
// Results in:
// Section 1
//
// Section 2
```

### ControlFlow

Represents control flow structures like if/else, try/catch, loops, etc. This is a complex part that manages control flow block boundaries.

#### Position Enum

```kotlin
enum class Position {
    BEGIN,  // Start of a control flow block
    NEXT,   // Continuation (else, catch, etc.)
    END     // End of a control flow block
}
```

#### Factory Methods

##### Begin Control Flow

```kotlin
CodePart.beginControlFlow(): CodeArgumentPart
CodePart.beginControlFlow(codeValue: CodeValue): CodeArgumentPart
CodePart.beginControlFlow(format: String, vararg arguments: CodeArgumentPart): CodeArgumentPart
CodePart.beginControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl): CodeArgumentPart
```

##### Next Control Flow

```kotlin
CodePart.nextControlFlow(): CodeArgumentPart
CodePart.nextControlFlow(codeValue: CodeValue): CodeArgumentPart
CodePart.nextControlFlow(format: String, vararg arguments: CodeArgumentPart): CodeArgumentPart
CodePart.nextControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl): CodeArgumentPart
```

##### End Control Flow

```kotlin
CodePart.endControlFlow(): CodeArgumentPart
CodePart.endControlFlow(codeValue: CodeValue): CodeArgumentPart
CodePart.endControlFlow(format: String, vararg arguments: CodeArgumentPart): CodeArgumentPart
CodePart.endControlFlow(format: String, block: CodeValueSingleFormatBuilderDsl): CodeArgumentPart
```

#### Usage

##### Basic If Statement

```kotlin
CodeValue {
    addCode("%V", CodePart.beginControlFlow("if (x > 0)"))
    addCode("println(\"positive\")")
    addCode("%V", CodePart.endControlFlow())
}
// Results in:
// if (x > 0) {
//     println("positive")
// }
```

##### If-Else Statement

```kotlin
CodeValue {
    addCode("%V", CodePart.beginControlFlow("if (x > 0)"))
    addCode("println(\"positive\")")
    addCode("%V", CodePart.nextControlFlow("else"))
    addCode("println(\"not positive\")")
    addCode("%V", CodePart.endControlFlow())
}
// Results in:
// if (x > 0) {
//     println("positive")
// } else {
//     println("not positive")
// }
```

##### Try-Catch-Finally

```kotlin
CodeValue {
    addCode("%V", CodePart.beginControlFlow("try"))
    addCode("riskyOperation()")
    addCode("%V", CodePart.nextControlFlow("catch (Exception e)"))
    addCode("handleError()")
    addCode("%V", CodePart.nextControlFlow("finally"))
    addCode("cleanup()")
    addCode("%V", CodePart.endControlFlow())
}
// Results in:
// try {
//     riskyOperation()
// } catch (Exception e) {
//     handleError()
// } finally {
//     cleanup()
// }
```

##### Do-While Loop

```kotlin
CodeValue {
    addCode("%V", CodePart.beginControlFlow("do"))
    addCode("processItem()")
    addCode("count++")
    addCode("%V", CodePart.endControlFlow("while (count < 10)"))
}
// Results in (Java):
// do {
//     processItem();
//     count++;
// } while (count < 10);
```

##### With Arguments

```kotlin
CodeValue {
    addCode("%V", CodePart.beginControlFlow("if (%V > %V)", 
        CodePart.name("count"),
        CodePart.literal(0)
    ))
    addCode("process()")
    addCode("%V", CodePart.endControlFlow())
}
// Results in:
// if (count > 0) {
//     process()
// }
```

#### CodeValueBuilder Helpers

Instead of using `CodePart.beginControlFlow()` directly, use the builder methods:

```kotlin
CodeValue {
    beginControlFlow("if (x > 0)")  // Instead of addCode with CodePart.beginControlFlow
    addCode("println(\"positive\")")
    endControlFlow()
}
```

### OtherCodeValue

Embeds another `CodeValue` as a part. This allows nesting and composition of code values.

#### Factory Method

```kotlin
CodePart.otherCodeValue(value: CodeValue): CodeArgumentPart
```

#### Usage

```kotlin
// Create a reusable code value
val condition = CodeValue("x > 0 && y < 10")

// Use it in another code value
CodeValue("if (%V)", CodePart.otherCodeValue(condition))
// Results in: if (x > 0 && y < 10)

// Complex example
val parameters = CodeValue {
    addCode("name: String,")
    addCode("age: Int,")
    addCode("email: String")
}

val functionDecl = CodeValue("fun createUser(%V)", 
    CodePart.otherCodeValue(parameters)
)
// Results in: fun createUser(name: String, age: Int, email: String)
```

## Practical Examples

### Building a Variable Declaration

```kotlin
// Simple declaration
CodeValue("val %V = %V",
    CodePart.name("count"),
    CodePart.literal(0)
)
// Results in: val count = 0

// With type
CodeValue("val %V: %V = %V",
    CodePart.name("message"),
    CodePart.type(StringTypeName),
    CodePart.string("Hello")
)
// Results in: val message: String = "Hello"
```

### Building a Function Signature

```kotlin
CodeValue("fun %V(%V: %V, %V: %V): %V",
    CodePart.name("calculate"),
    CodePart.name("x"),
    CodePart.type(IntTypeName),
    CodePart.name("y"),
    CodePart.type(IntTypeName),
    CodePart.type(IntTypeName)
)
// Results in: fun calculate(x: Int, y: Int): Int
```

### Building a Class with Fields

```kotlin
CodeValue {
    addCode("class %V {", CodePart.name("Person"))
    indent()
    addStatement("val %V: %V", 
        CodePart.name("name"),
        CodePart.type(StringTypeName)
    )
    addStatement("val %V: %V",
        CodePart.name("age"),
        CodePart.type(IntTypeName)
    )
    unindent()
    addCode("}")
}
// Results in:
// class Person {
//     val name: String
//     val age: Int
// }
```

### Building Conditional Logic

```kotlin
CodeValue {
    beginControlFlow("if (%V)", CodePart.name("isValid"))
    addStatement("process()")
    nextControlFlow("else")
    addStatement("reject()")
    endControlFlow()
}
// Results in:
// if (isValid) {
//     process()
// } else {
//     reject()
// }
```

### Building a Try-Catch Block

```kotlin
CodeValue {
    beginControlFlow("try")
    addStatement("val result = fetchData()")
    addStatement("return result")
    nextControlFlow("catch (%V e)", CodePart.type(IOExceptionTypeName))
    addStatement("logger.error(%V, e)", CodePart.string("Failed to fetch"))
    addStatement("throw RuntimeException(e)")
    endControlFlow()
}
// Results in:
// try {
//     val result = fetchData()
//     return result
// } catch (IOException e) {
//     logger.error("Failed to fetch", e)
//     throw RuntimeException(e)
// }
```

### Building a For Loop

```kotlin
CodeValue {
    beginControlFlow("for (%V in %V)",
        CodePart.name("item"),
        CodePart.name("items")
    )
    addStatement("process(%V)", CodePart.name("item"))
    endControlFlow()
}
// Results in:
// for (item in items) {
//     process(item)
// }
```

## Choosing the Right CodePart

| Use Case | CodePart Type | Example |
|----------|---------------|---------|
| Plain text | `simple()` | `CodePart.simple("public")` |
| Numbers, booleans | `literal()` | `CodePart.literal(42)` |
| Constants, expressions | `literal()` | `CodePart.literal("MAX_VALUE")` |
| Variable/function names | `name()` | `CodePart.name("userName")` |
| String literals | `string()` | `CodePart.string("Hello")` |
| Type references | `type()` | `CodePart.type(StringTypeName)` |
| Increase indent | `indent()` | `CodePart.indent()` |
| Decrease indent | `unindent()` | `CodePart.unindent()` |
| Control structures | `beginControlFlow()` etc. | `CodePart.beginControlFlow("if (x)")` |
| Nested code | `otherCodeValue()` | `CodePart.otherCodeValue(code)` |
| Line break | `newline()` | `CodePart.newline()` |
| Smart wrapping | `wrappingSpace()` | `CodePart.wrappingSpace()` |
| Skip placeholder | `skip()` | `CodePart.skip()` |

## Best Practices

1. **Use Appropriate Types**: Choose the correct `CodePart` type for each element. Don't use `literal()` for strings that need quotes.

2. **Leverage Builder Methods**: Use `CodeValueBuilder` methods like `indent()`, `beginControlFlow()` instead of manually adding `CodePart` instances.

3. **Manage Indentation**: Always pair `indent()` with `unindent()`.

4. **Use Statement Markers**: Use `addStatement()` for statements to get proper formatting.

5. **Compose with OtherCodeValue**: Break complex code into smaller `CodeValue` pieces and compose them with `otherCodeValue()`.

6. **Handle Special Characters**: Use `string()` with `handleSpecialCharacter = true` (default) for Kotlin string templates.

7. **Smart Line Breaks**: Use `wrappingSpace()` for flexible formatting that adapts to line length.

## See Also

- [CodeValue](./CodeValue.md) - Detailed documentation for CodeValue and builders
- [README](./README.md) - Overview of the code generation system
