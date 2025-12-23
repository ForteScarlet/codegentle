# KSP Integration Documentation

CodeGentle provides seamless integration with Kotlin Symbol Processing (KSP) through extension functions that convert KSP types to CodeGentle's type system.

## Overview

The KSP integration modules provide extension functions on KSP types, allowing you to directly convert KSP symbols to CodeGentle specifications during annotation processing.

**Two modules:**
- `codegentle-common-ksp` - Common KSP utilities for type names and class names
- `codegentle-kotlin-ksp` - Kotlin-specific KSP extensions for TypeSpec, FunctionSpec, and PropertySpec

## Quick Start

```kotlin
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import love.forte.codegentle.kotlin.ksp.*
import love.forte.codegentle.kotlin.spec.*

class MyProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.example.MyAnnotation")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDecl ->
            // Convert KSP class to ClassName
            val className = classDecl.toClassName()

            // Convert KSP function to FunctionSpec
            classDecl.getAllFunctions().forEach { funcDecl ->
                val functionSpec = funcDecl.toKotlinFunctionSpec()
                // Use functionSpec to generate code...
            }
        }

        return emptyList()
    }
}
```

## Documentation Files

- **[Type Conversion](./type-conversion.md)** - Converting KSP types to TypeName
- **[Class and Member Names](./class-member-names.md)** - Converting declarations to names
- **[Spec Conversion](./spec-conversion.md)** - Converting KSP symbols to Spec objects
- **[Context Receivers](./context-receivers.md)** - Handling context receivers in KSP

## Key Features

### Type Name Conversion

Convert any KSP type to CodeGentle TypeName:

```kotlin
val ksType: KSType = ...
val typeName: TypeName = ksType.toTypeName()
```

Handles:
- Primitive types (Boolean, Int, String, etc.)
- Class types
- Array types
- Generic/parameterized types
- Function/lambda types (including suspend, receiver, and context receivers)
- Type variables with bounds
- Wildcard types

### Class Name Conversion

```kotlin
val classDecl: KSClassDeclaration = ...
val className: ClassName = classDecl.toClassName()
```

### Function Conversion

```kotlin
val functionDecl: KSFunctionDeclaration = ...
val functionSpec: KotlinFunctionSpec = functionDecl.toKotlinFunctionSpec()
// Automatically handles:
// - Receivers (extension functions)
// - Context receivers
// - Suspend modifier
// - Parameters with default values
```

### Property Conversion

```kotlin
val propertyDecl: KSPropertyDeclaration = ...
val propertySpec: KotlinPropertySpec = propertyDecl.toKotlinPropertySpec()
```

## Advanced Features

### Context Receivers Support

CodeGentle KSP integration fully supports Kotlin 2.0+ context receivers:

```kotlin
// For a function like:
// context(Logger, Database)
// suspend fun query(): Result

val functionDecl: KSFunctionDeclaration = ...
val spec = functionDecl.toKotlinFunctionSpec()
// spec.contextParameters will contain [Logger, Database]
// spec.modifiers will include SUSPEND
```

Context receivers are detected via the `@ContextFunctionTypeParams` annotation on function types.

### Type Parameter Bounds

```kotlin
// For a type parameter like: T : Comparable<T>
val typeParam: KSTypeParameter = ...
val typeVarName: TypeVariableName = typeParam.toTypeVariableName()
// typeVarName.bounds will contain [Comparable<T>]
```

### Error Type Handling

The KSP integration handles Kotlin's ERROR TYPE gracefully when annotations can't be validated during processing:

```kotlin
// Even if annotation types show as <ERROR TYPE: kotlin.ExtensionFunctionType>
// the conversion still works by checking annotation names
```

## Module Dependencies

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    // For type name conversion
    implementation("love.forte.codegentle:codegentle-common-ksp:$version")

    // For Kotlin spec conversion
    implementation("love.forte.codegentle:codegentle-kotlin-ksp:$version")
}
```

## Related Documentation

- [TypeName System](../naming/README.md) - Understanding CodeGentle's type system
- [Kotlin Specs](../spec/kotlin-specs.md) - Kotlin specification classes
- [Context Receivers](./context-receivers.md) - Detailed context receiver handling

## Best Practices

1. **Use extension functions directly** - No need to manually construct TypeName objects
2. **Handle nullable returns** - Some functions like `toClassNameOrNull()` return nullable types for safety
3. **Check validation** - Use `KSType.validate()` before conversion for better error handling
4. **Preserve metadata** - TypeRef system preserves additional metadata through conversion

## Example: Complete Annotation Processor

See `/tests/test-ksp-receiver-and-contexts/proc/` for a complete example of using KSP conversion to generate backup functions with receiver and context support.
