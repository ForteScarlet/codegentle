# CodeValue and CodePart API

This directory contains comprehensive documentation for the CodeValue and CodePart APIs, which form the foundation of code generation in CodeGentle.

## Overview

CodeGentle's code generation system is built on two core concepts:

- **CodeValue**: A container representing a piece of code, composed of multiple parts
- **CodePart**: Individual building blocks that make up a CodeValue

These APIs provide a type-safe, flexible way to construct source code programmatically with proper formatting, indentation, and structure.

## Documentation Files

- [**CodeValue.md**](./CodeValue.md) - Detailed documentation for the CodeValue interface and builders
- [**CodePart.md**](./CodePart.md) - Comprehensive guide to CodePart types and factory methods

## Quick Start

### Creating Simple Code

```kotlin
// Create empty CodeValue
val empty = CodeValue()

// Create simple code
val simple = CodeValue("val x = 10")

// Create code with placeholders
val withPlaceholder = CodeValue("val name = %V", CodePart.string("John"))
// Results in: val name = "John"
```

### Using the Builder

```kotlin
val code = CodeValue {
    addCode("class User {")
    indent()
    addCode("val name: String")
    addCode("val age: Int")
    unindent()
    addCode("}")
}
```

### Control Flow

```kotlin
val code = CodeValue {
    ifControlFlow("x > 0") {
        addValue(CodePart.literal(0))
    }
    addCode("println(\"positive\")")
    elseControlFlow()
    addCode("println(\"not positive\")")
    endControlFlow()
}
```

## Key Concepts

### Placeholder System

CodeValue uses the `%V` placeholder for dynamic content insertion. Each `%V` in a format string is replaced by corresponding CodeArgumentPart values:

```kotlin
CodeValue("Hello, %V!", CodePart.string("World"))
// Results in: Hello, "World"!

CodeValue("int %V = %V;", CodePart.name("count"), CodePart.literal(42))
// Results in: int count = 42;
```

### Code Parts

Different CodePart types handle different kinds of code elements:

- **Literal**: Raw values without quotes
- **String**: String values with proper quoting and escaping
- **Name**: Variable/function names
- **Type**: Type references
- **Indent/Unindent**: Control indentation
- **ControlFlow**: If/else, try/catch, loops, etc.

### Builders

Two builder types provide different construction patterns:

1. **CodeValueBuilder**: General-purpose builder for complex code construction
2. **CodeValueSingleFormatBuilder**: Optimized for single format strings with placeholders

## Design Philosophy

The CodeValue and CodePart APIs follow these principles:

1. **Type Safety**: Different part types prevent mixing incompatible code elements
2. **Immutability**: CodeValue instances are immutable once created
3. **Composability**: CodeValues can be combined and nested
4. **Flexibility**: Multiple construction patterns for different use cases
5. **Language Agnostic**: Core APIs work for any target language (Java, Kotlin, etc.)

## See Also

- For language-specific implementations, see `JavaCodeValue` and `KotlinCodeValue`
- For code emission and formatting, see the writer documentation
