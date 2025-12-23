# CodeGentle Documentation

CodeGentle is a Kotlin multiplatform library for generating Java and Kotlin source code programmatically.

## 📚 Documentation Structure

### Core Concepts

#### [Code Generation](./code/README.md)
Learn about CodeGentle's code generation system built on `CodeValue` and `CodePart`:
- **[CodeValue](./code/CodeValue.md)** - Container for code fragments with placeholder support
- **[CodePart](./code/CodePart.md)** - Building blocks for code construction
- Control flow, indentation, and formatting

#### [Naming System](./naming/README.md)
Understand how to represent types, classes, and members:
- **[Common Naming](./naming/common-naming.md)** - TypeName, ClassName, PackageName, MemberName
- **[Generic Types](./naming/generic-types.md)** - ParameterizedTypeName, TypeVariableName, WildcardTypeName, ArrayTypeName
- **[Java Naming](./naming/java-naming.md)** - Java-specific utilities and constants
- **[Kotlin Naming](./naming/kotlin-naming.md)** - KotlinLambdaTypeName, context receivers, value classes

#### [File Generation](./file/README.md)
Generate complete source files with imports and package structure:
- **[JavaFile](./file/JavaFile.md)** - Java source file generation with secondary types
- **[KotlinFile](./file/KotlinFile.md)** - Kotlin source files with top-level functions/properties

#### [Spec System](./spec/README.md)
Build type, method, and property specifications:
- **[Common Specs](./spec/common-specs.md)** - Base interfaces and patterns
- **[Java Specs](./spec/java-specs.md)** - Classes, interfaces, enums, records, sealed types
- **[Kotlin Specs](./spec/kotlin-specs.md)** - Classes, functions, properties, value classes

### Advanced Features

#### [KSP Integration](./ksp/README.md)
Seamlessly integrate with Kotlin Symbol Processing:
- **[Type Conversion](./ksp/type-conversion.md)** - Convert KSP types to TypeName (15+ functions)
- **[Context Receivers](./ksp/context-receivers.md)** - Handle Kotlin 2.0+ context receivers
- Class and member name conversion
- Spec conversion from KSP symbols

## 🚀 Quick Start

### Java Code Generation

```kotlin
import love.forte.codegentle.java.*

val classSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "HelloWorld") {
    addModifier(JavaModifier.PUBLIC)
    addMethod(JavaMethodSpec("main") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC)
        returns(JavaClassNames.VOID.ref())
        addParameter(JavaParameterSpec("args", JavaClassNames.STRING.ref().array()))
        addCode("System.out.println(\"Hello, World!\");")
    })
}

val javaFile = JavaFile("com.example".parseToPackageName(), classSpec)
println(javaFile.writeToJavaString())
```

### Kotlin Code Generation

```kotlin
import love.forte.codegentle.kotlin.*

val classSpec = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "HelloWorld") {
    addFunction(KotlinFunctionSpec("main") {
        returns(KotlinClassNames.UNIT.ref())
        addCode("println(\"Hello, World!\")")
    })
}

val kotlinFile = KotlinFile("com.example".parseToPackageName(), classSpec)
println(kotlinFile.writeToKotlinString())
```

### KSP Integration

```kotlin
import love.forte.codegentle.kotlin.ksp.*

class MyProcessor : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation("MyAnnotation")
            .filterIsInstance<KSClassDeclaration>()
            .forEach { classDecl ->
                // Convert KSP types directly
                val className = classDecl.toClassName()
                val functionSpecs = classDecl.getAllFunctions()
                    .map { it.toKotlinFunctionSpec() }
                // Generate code...
            }
        return emptyList()
    }
}
```

## 📦 Modules

| Module                  | Description                                      |
|-------------------------|--------------------------------------------------|
| `codegentle-common`     | Core APIs: CodeValue, TypeName, common specs     |
| `codegentle-java`       | Java code generation: JavaFile, Java specs       |
| `codegentle-kotlin`     | Kotlin code generation: KotlinFile, Kotlin specs |
| `codegentle-common-ksp` | KSP common utilities for type conversion         |
| `codegentle-kotlin-ksp` | KSP Kotlin integration for spec conversion       |

## 🎯 Key Features

### Multiplatform Support
- JVM, JavaScript, Native, Wasm targets
- Platform-independent APIs with JVM-specific extensions

### Modern Language Features
**Java**:
- Records (Java 16+)
- Sealed classes/interfaces (Java 17+)
- Non-sealed types

**Kotlin**:
- Value classes (inline classes)
- Context receivers (Kotlin 2.0+)
- Suspend functions
- Extension functions and properties
- Top-level declarations

### KSP Integration
- Direct conversion from KSP symbols
- Context receiver detection
- Full type system support
- ERROR TYPE handling

### Flexible Code Construction
- Placeholder system (`%V`) for dynamic content
- Control flow extensions (if/else, try/catch, loops)
- Smart line wrapping (100 column limit)
- Builder DSL patterns

## 🔗 External Resources

- **GitHub**: [ForteScarlet/codegentle](https://github.com/ForteScarlet/codegentle)
- **API Reference**: See module-specific documentation
- **Examples**: Check `/tests/` directory in the repository

## 📖 Language Versions

- **English Documentation**: You are here (`docs/`)
- **中文文档**: See [`docs_zh/`](../docs_zh/README.md) for Chinese documentation

## 💡 Getting Help

If you have questions or need help:
1. Check the relevant documentation section
2. Review the examples in this guide
3. Explore the test files in the repository
4. Open an issue on GitHub

---

**License**: Apache License 2.0

**Copyright**: (C) 2024-2025 Forte Scarlet
