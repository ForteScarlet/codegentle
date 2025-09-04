# Naming System Documentation

The CodeGentle naming system provides a comprehensive set of classes and utilities for representing and manipulating various types of names used in code generation. This documentation covers the naming classes from all three main modules: `codegentle-common`, `codegentle-java`, and `codegentle-kotlin`.

## Overview

The naming system is built around a hierarchy of interfaces and classes that represent different aspects of programming language constructs:

- **Type Names**: Represent class names, primitive types, generic types, arrays, etc.
- **Package Names**: Represent package/namespace structures
- **Member Names**: Represent class members like methods, fields, and properties
- **Utility Classes**: Provide predefined constants and helper functions

## Module Structure

### Common Module (`codegentle-common`)

The common module provides the foundation for all naming functionality, with platform-agnostic interfaces and implementations:

- [**Core Naming Classes**](common-naming.md) - TypeName, ClassName, PackageName, MemberName
- [**Generic Type Names**](generic-types.md) - ParameterizedTypeName, TypeVariableName, WildcardTypeName, ArrayTypeName

### Java Module (`codegentle-java`)

The Java module extends the common functionality with Java-specific utilities and constants:

- [**Java Naming Utilities**](java-naming.md) - JavaClassNames, JavaPrimitiveTypeNames, JavaAnnotationNames

### Kotlin Module (`codegentle-kotlin`)

The Kotlin module provides Kotlin-specific naming classes and utilities:

- [**Kotlin Naming Utilities**](kotlin-naming.md) - KotlinClassNames, KotlinLambdaTypeName, KotlinAnnotationNames

## Quick Start Examples

### Basic Usage

```kotlin
// Create a simple class name
val stringClass = ClassName("java.lang", "String")

// Create a package name
val javaLang = "java.lang".parseToPackageName()

// Create a member name
val valueOf = MemberName("java.lang.String", "valueOf")
```

### Using Predefined Constants

```kotlin
// Java predefined types
val javaString = JavaClassNames.STRING
val javaInt = JavaPrimitiveTypeNames.INT

// Kotlin predefined types  
val kotlinString = KotlinClassNames.STRING
val kotlinUnit = KotlinClassNames.UNIT
```

### DSL and Builder Patterns

```kotlin
// Create a parameterized type using DSL
val listOfString = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())

// Create a Kotlin lambda type
val lambdaType = KotlinLambdaTypeName(KotlinClassNames.STRING.ref()) {
    addParameter(KotlinClassNames.INT.ref())
    suspend(true)
}
```

## Documentation Structure

Each documentation file focuses on specific aspects of the naming system:

1. **[Common Naming Classes](common-naming.md)** - Foundation interfaces and classes
2. **[Generic Type Names](generic-types.md)** - Handling generics, type variables, and wildcards
3. **[Java Naming Utilities](java-naming.md)** - Java-specific constants and utilities
4. **[Kotlin Naming Utilities](kotlin-naming.md)** - Kotlin-specific types and features

## Key Features

### DSL Support
Most naming classes provide DSL (Domain-Specific Language) extensions that make code generation more readable and maintainable.

### Type Safety
All naming classes are designed with type safety in mind, using generic types and sealed interfaces where appropriate.

### Platform Compatibility
The naming system works across all supported CodeGentle platforms (JVM, JavaScript, Native, etc.).

### Import Management
The naming system integrates with code writers to automatically handle imports and qualified names.
