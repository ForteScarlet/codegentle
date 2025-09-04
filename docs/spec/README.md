# CodeGentle Spec Documentation

CodeGentle provides a comprehensive set of specification (Spec) classes for generating Java and Kotlin source code. This documentation covers the usage methods and construction patterns for all XxxSpec classes across the three main modules: `codegentle-common`, `codegentle-java`, and `codegentle-kotlin`.

## Quick Navigation

### 📚 Documentation Sections
- **[Common Specs](common-specs.md)** - Base interfaces and common functionality
- **[Java Specs](java-specs.md)** - Java-specific code generation specifications  
- **[Kotlin Specs](kotlin-specs.md)** - Kotlin-specific code generation specifications

### 🏗️ Spec Categories
- **Type Specs** - Classes, interfaces, enums, objects, records
- **Member Specs** - Methods, functions, properties, fields, constructors
- **Parameter Specs** - Method/function parameters and type parameters

## Overview

CodeGentle Spec classes follow a consistent design pattern across all modules:

1. **Interface-based Design**: Each Spec is defined as a sealed interface with specific properties
2. **Builder Pattern**: Companion objects provide builder methods for construction
3. **DSL Extensions**: Inline functions enable fluent, lambda-based configuration
4. **Collector Pattern**: Builders extend multiple collector interfaces for fluent API

## Quick Start Examples

### Java Class Generation

```kotlin
import love.forte.codegentle.java.spec.*
import love.forte.codegentle.java.JavaFile
import love.forte.codegentle.common.naming.parseToPackageName

// Create a simple Java class with DSL
val javaClass = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "HelloWorld") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("A simple greeting class.")
    
    addMethod(JavaMethodSpec("greet") {
        addModifier(JavaModifier.PUBLIC)
        returns(JavaClassNames.STRING.ref())
        addParameter(JavaParameterSpec("name", JavaClassNames.STRING.ref()))
        addCode("return \"Hello, \" + name + \"!\";")
    })
}

// Create a Java file containing the class
val javaFile = JavaFile("com.example".parseToPackageName()) {
    addType(javaClass)
}
```

### Kotlin Class Generation

```kotlin
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.KotlinFile
import love.forte.codegentle.common.naming.parseToPackageName

// Create a simple Kotlin class with DSL
val kotlinClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "HelloWorld") {
    addModifier(KotlinModifier.DATA)
    addKdoc("A simple greeting class.")
    
    primaryConstructor {
        addParameter("name", KotlinClassNames.STRING)
    }
    
    addFunction(KotlinFunctionSpec("greet") {
        returns(KotlinClassNames.STRING)
        addCode("return \"Hello, \$name!\"")
    })
}

// Create a Kotlin file containing the class
val kotlinFile = KotlinFile("com.example".parseToPackageName()) {
    addType(kotlinClass)
}
```

## Construction Patterns

CodeGentle provides two main approaches for constructing Spec instances:

### 1. Builder Pattern (Raw API)

```kotlin
// Java example
val methodSpec = JavaMethodSpec.methodBuilder("calculate")
    .addModifier(JavaModifier.PUBLIC)
    .returns(JavaClassNames.INT.ref())
    .addParameter(JavaParameterSpec("a", JavaClassNames.INT.ref()))
    .addParameter(JavaParameterSpec("b", JavaClassNames.INT.ref()))
    .addCode("return a + b;")
    .build()
```

### 2. DSL Lambda Extensions (Recommended)

```kotlin
// Java example - same result as above but more concise
val methodSpec = JavaMethodSpec("calculate") {
    addModifier(JavaModifier.PUBLIC)
    returns(JavaClassNames.INT.ref())
    addParameter(JavaParameterSpec("a", JavaClassNames.INT.ref()))
    addParameter(JavaParameterSpec("b", JavaClassNames.INT.ref()))
    addCode("return a + b;")
}
```

**DSL benefits:**
- More concise and readable
- Better IDE support with context completion
- Easier to nest and compose
- Follows Kotlin idioms

## Module-Specific Features

### Java-Only Features
- Static blocks and static members
- Checked exceptions in method signatures
- Record types and sealed classes (Java 14+)
- Annotation types with default values
- Package-private visibility

### Kotlin-Only Features
- Primary and secondary constructors
- Property accessors (getter/setter)
- Extension functions and properties
- Context receivers and suspend functions
- Value classes and inline classes
- Companion objects and object declarations
