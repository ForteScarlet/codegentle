# CodeValue

`CodeValue` is the core interface for representing code fragments in CodeGentle. It encapsulates a list of `CodePart` instances that together form a complete piece of code.

## Interface Definition

```kotlin
interface CodeValue {
    val parts: List<CodePart>
}
```

A `CodeValue` is essentially a container for a sequence of code parts that will be emitted as source code.

## The Placeholder System

CodeValue uses `%V` as a universal placeholder for dynamic content insertion. When you write a format string, each `%V` is replaced by the corresponding `CodeArgumentPart` you provide.

```kotlin
// Single placeholder
CodeValue("val name = %V", CodePart.string("Alice"))
// Results in: val name = "Alice"

// Multiple placeholders
CodeValue("val %V: %V = %V", 
    CodePart.name("count"),
    CodePart.type(IntTypeName),
    CodePart.literal(42)
)
// Results in: val count: Int = 42
```

### Skipping Placeholders

If you need to output the literal string `%V`, use `CodePart.skip()`:

```kotlin
CodeValue("The placeholder is %V", CodePart.skip())
// Results in: The placeholder is %V
```

## Creating CodeValue Instances

### Empty Instance

```kotlin
val empty = CodeValue()
```

Creates an empty `CodeValue` with no parts.

### From Parts List

```kotlin
val code = CodeValue(listOf(
    CodePart.simple("val x = "),
    CodePart.literal(10)
))
```

Creates a `CodeValue` from an explicit list of parts.

### Simple Format String

```kotlin
// Without placeholders
val simple = CodeValue("println(\"Hello World\")")

// With single placeholder
val withArg = CodeValue("val x = %V", CodePart.literal(42))

// With multiple placeholders
val multi = CodeValue(
    "fun %V(%V: %V): %V",
    CodePart.name("greet"),
    CodePart.name("name"),
    CodePart.type(StringTypeName),
    CodePart.type(UnitTypeName)
)
```

### Format String with Builder Block

```kotlin
val code = CodeValue("val %V = %V") {
    addValue(CodePart.name("result"))
    addValue(CodePart.literal(100))
}
// Results in: val result = 100
```

### Using CodeValueBuilder

For complex code construction:

```kotlin
val code = CodeValue {
    addCode("class Person {")
    indent()
    addStatement("val name: String")
    addStatement("val age: Int")
    unindent()
    addCode("}")
}
```

## CodeValueSingleFormatBuilder

Optimized builder for working with a single format string containing placeholders.

### Creating a Builder

```kotlin
val builder = CodeValue.builder("%V %V = %V;")
```

### Adding Values

```kotlin
val code = CodeValue.builder("%V %V = %V;")
    .addValue(CodePart.type(IntTypeName))
    .addValue(CodePart.name("count"))
    .addValue(CodePart.literal(0))
    .build()
// Results in: int count = 0;
```

### Adding Multiple Values

```kotlin
// Using vararg
builder.addValues(
    CodePart.type(StringTypeName),
    CodePart.name("message"),
    CodePart.string("Hello")
)

// Using Iterable
val parts = listOf(
    CodePart.name("x"),
    CodePart.literal(10)
)
builder.addValues(parts)
```

### Builder with DSL

```kotlin
val code = CodeValue("%V %V = %V") {
    addValue(CodePart.type(StringTypeName))
    addValue(CodePart.name("greeting"))
    addValue(CodePart.string("Hello"))
}
```

## CodeValueBuilder

General-purpose builder for constructing complex code structures.

### Creating a Builder

```kotlin
val builder = CodeValue.builder()
```

### Adding Code

#### addCode(codeValue)

Adds another `CodeValue` to the builder:

```kotlin
val builder = CodeValue.builder()
builder.addCode(CodeValue("val x = 10"))
builder.addCode(CodeValue("val y = 20"))
```

#### addCode(format)

Adds a simple format string without placeholders:

```kotlin
builder.addCode("println(\"Hello\")")
```

#### addCode(format, vararg argumentParts)

Adds code with placeholders and arguments:

```kotlin
builder.addCode("val %V = %V", 
    CodePart.name("count"),
    CodePart.literal(0)
)
```

#### addCode(format, block)

Adds code with a builder block for arguments:

```kotlin
builder.addCode("fun %V(): %V") {
    addValue(CodePart.name("getData"))
    addValue(CodePart.type(StringTypeName))
}
```

### Adding Statements

#### addStatement(format, vararg argumentParts)

Adds a statement with automatic `StatementBegin` and `StatementEnd` markers. These markers help with proper formatting and semicolon placement:

```kotlin
builder.addStatement("val x = %V", CodePart.literal(10))
// Wrapped with statement markers for proper formatting
```

#### addStatement(codeValue)

Adds a `CodeValue` as a statement:

```kotlin
val assignment = CodeValue("x = y + z")
builder.addStatement(assignment)
```

### Indentation Control

#### indent()

Increases the indentation level by one:

```kotlin
builder.addCode("class Person {")
builder.indent()
builder.addCode("val name: String")
builder.unindent()
builder.addCode("}")
```

#### unindent()

Decreases the indentation level by one:

```kotlin
builder.unindent()
```

### Clearing

#### clear()

Removes all parts from the builder:

```kotlin
builder.addCode("val x = 10")
builder.clear() // Builder is now empty
```

### Building

#### build()

Constructs the final `CodeValue`:

```kotlin
val code = builder.build()
```

## Control Flow

CodeValue provides extensive support for control flow structures. See [Control Flow examples](#control-flow-examples) below.

### Basic Control Flow

```kotlin
CodeValue {
    beginControlFlow("if (x > 0)")
    addCode("println(\"positive\")")
    endControlFlow()
}
```

### Control Flow with Arguments

```kotlin
CodeValue {
    beginControlFlow("if (%V > %V)", 
        CodePart.name("x"),
        CodePart.literal(0)
    )
    addCode("println(\"positive\")")
    endControlFlow()
}
```

### if-else-if-else

```kotlin
CodeValue {
    ifControlFlow("x > 0")
    addCode("println(\"positive\")")
    elseIfControlFlow("x < 0")
    addCode("println(\"negative\")")
    elseControlFlow()
    addCode("println(\"zero\")")
    endControlFlow()
}
```

### try-catch-finally

```kotlin
CodeValue {
    tryControlFlow()
    addCode("riskyOperation()")
    catchControlFlow("IOException e")
    addCode("handleError()")
    finallyControlFlow()
    addCode("cleanup()")
    endControlFlow()
}
```

### Loops

#### while Loop

```kotlin
CodeValue {
    whileControlFlow("i < 10")
    addCode("process(i)")
    addCode("i++")
    endControlFlow()
}
```

#### do-while Loop

```kotlin
CodeValue {
    doControlFlow()
    addCode("process(item)")
    addCode("count++")
    doWhileEndControlFlow("count < 10")
}
```

#### for Loop

```kotlin
// Java-style
CodeValue {
    forControlFlow("int i = 0; i < 10; i++")
    addCode("process(i)")
    endControlFlow()
}

// Kotlin-style
CodeValue {
    forControlFlow("i in 0 until 10")
    addCode("process(i)")
    endControlFlow()
}
```

### Convenience: inControlFlow

Creates a complete control flow block in one call:

```kotlin
CodeValue {
    inControlFlow("if (x > %V)", CodePart.literal(0)) {
        addCode("println(\"positive\")")
    }
}
// Automatically begins and ends the control flow
```

## Operators

### Plus Operator

Combines two `CodeValue` instances:

```kotlin
val code1 = CodeValue("val x = 10")
val code2 = CodeValue("val y = 20")
val combined = code1 + code2
// Combined contains both parts
```

## Utility Functions

### isEmpty()

Checks if a `CodeValue` has no parts:

```kotlin
val empty = CodeValue()
if (empty.isEmpty()) {
    println("No code")
}
```

### isNotEmpty()

Checks if a `CodeValue` has parts:

```kotlin
val code = CodeValue("val x = 10")
if (code.isNotEmpty()) {
    println("Has code")
}
```

## Complete Examples

### Building a Class

```kotlin
val classCode = CodeValue {
    addCode("class User(")
    indent()
    addStatement("val name: String")
    addStatement("val email: String")
    unindent()
    addCode(") {")
    indent()
    
    // Add a method
    addCode("fun greet(): String {")
    indent()
    addStatement("return %V", CodePart.string("Hello, \$name"))
    unindent()
    addCode("}")
    
    unindent()
    addCode("}")
}
```

### Building a Function with Error Handling

```kotlin
val functionCode = CodeValue {
    addCode("fun loadData(id: %V): %V {", 
        CodePart.type(IntTypeName),
        CodePart.type(DataTypeName)
    )
    indent()
    
    tryControlFlow()
    addStatement("val result = database.query(id)")
    addStatement("return result")
    
    catchControlFlow("SQLException e")
    addStatement("logger.error(%V, e)", 
        CodePart.string("Failed to load data")
    )
    addStatement("throw RuntimeException(e)")
    
    finallyControlFlow()
    addStatement("database.close()")
    
    endControlFlow()
    
    unindent()
    addCode("}")
}
```

### Building Conditional Logic

```kotlin
val conditionalCode = CodeValue {
    ifControlFlow("status == %V", CodePart.literal("ACTIVE")) {
        addValue(CodePart.name("Status.ACTIVE"))
    }
    addStatement("processActive()")
    
    elseIfControlFlow("status == %V", CodePart.literal("PENDING")) {
        addValue(CodePart.name("Status.PENDING"))
    }
    addStatement("processPending()")
    
    elseControlFlow()
    addStatement("processInactive()")
    
    endControlFlow()
}
```

### Building a Loop

```kotlin
val loopCode = CodeValue {
    addStatement("val items = getItems()")
    
    forControlFlow("item in items")
    ifControlFlow("item.isValid()")
    addStatement("process(item)")
    elseControlFlow()
    addStatement("skip(item)")
    endControlFlow()
    endControlFlow()
}
```

## Best Practices

1. **Use Appropriate Builders**: Use `CodeValueSingleFormatBuilder` for simple format strings, `CodeValueBuilder` for complex structures.

2. **Leverage Control Flow Helpers**: Use `ifControlFlow`, `tryControlFlow`, etc., instead of manually building control structures.

3. **Manage Indentation**: Always pair `indent()` with `unindent()` to maintain proper code structure.

4. **Use Statement Markers**: Use `addStatement()` instead of `addCode()` for statements that need proper formatting.

5. **Choose Correct Part Types**: Use appropriate `CodePart` types (`literal`, `string`, `name`, `type`) for different code elements.

6. **Combine Values**: Use the `+` operator to combine simple `CodeValue` instances.

7. **Builder Pattern**: Use builder DSL for readable, maintainable code construction.

## See Also

- [CodePart](./CodePart.md) - Detailed documentation for all CodePart types
- [README](./README.md) - Overview of the code generation system
