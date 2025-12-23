# Type Conversion Functions

This document describes all type conversion functions provided by the KSP integration modules.

## Overview

CodeGentle provides extension functions to convert KSP type representations to CodeGentle's `TypeName` hierarchy. These functions handle all type categories including primitives, classes, generics, arrays, function types, and wildcards.

## Core Type Conversion

### `KSType.toTypeName(): TypeName`

**Location**: `codegentle-kotlin-ksp/KSTypeNameCross.kt`

Converts any KSP type to the corresponding CodeGentle `TypeName`.

**Supported Type Categories:**

1. **Primitive Types**
   ```kotlin
   val intType: KSType = ... // kotlin.Int
   val typeName = intType.toTypeName() // ClassName("kotlin", "Int")
   ```
   Primitives: Boolean, Byte, Short, Int, Long, Float, Double, Char, String, Unit, Nothing

2. **Array Types**
   ```kotlin
   val arrayType: KSType = ... // Array<String>
   val typeName = arrayType.toTypeName() // ArrayTypeName(String)
   ```

3. **Class Types**
   ```kotlin
   val classType: KSType = ... // com.example.MyClass
   val typeName = classType.toTypeName() // ClassName("com.example", "MyClass")
   ```

4. **Parameterized Types**
   ```kotlin
   val listType: KSType = ... // List<String>
   val typeName = listType.toTypeName() // ParameterizedTypeName(List, [String])
   ```

5. **Type Variables**
   ```kotlin
   val typeVar: KSType = ... // T
   val typeName = typeVar.toTypeName() // TypeVariableName("T")
   ```

6. **Function Types**
   ```kotlin
   val funcType: KSType = ... // (String, Int) -> Boolean
   val typeName = funcType.toTypeName() // KotlinLambdaTypeName(...)
   ```

### `KSTypeReference.toTypeName(): TypeName`

**Location**: `codegentle-kotlin-ksp/KSTypeNameCross.kt`

Resolves a type reference and converts it to `TypeName`.

```kotlin
val typeRef: KSTypeReference = parameterDecl.type
val typeName = typeRef.toTypeName()
```

### `KSDeclaration.toTypeName(): TypeName`

**Location**: `codegentle-kotlin-ksp/KSTypeNameCross.kt`

Polymorphic conversion supporting multiple declaration types:

```kotlin
when (val decl: KSDeclaration = ...) {
    is KSClassDeclaration -> decl.toTypeName() // -> ClassName
    is KSTypeAlias -> decl.toTypeName()        // -> resolved underlying type
    is KSTypeParameter -> decl.toTypeName()    // -> TypeVariableName
    else -> throw IllegalArgumentException()
}
```

## Type Argument Conversion

### `KSTypeArgument.toTypeName(): TypeName`

**Location**: `codegentle-kotlin-ksp/KSTypeNameCross.kt`

Converts type arguments including variance:

```kotlin
val typeArg: KSTypeArgument = ...
val typeName = when (typeArg.variance) {
    Variance.STAR -> WildcardTypeName()
    Variance.COVARIANT -> UpperWildcardTypeName(type) // out T
    Variance.CONTRAVARIANT -> LowerWildcardTypeName(type) // in T
    Variance.INVARIANT -> type.toTypeName()
}
```

**Variance Mapping:**
- `STAR` → `WildcardTypeName()` (unbounded wildcard)
- `COVARIANT` → `UpperWildcardTypeName` (`out T` in Kotlin, `? extends T` in Java)
- `CONTRAVARIANT` → `LowerWildcardTypeName` (`in T` in Kotlin, `? super T` in Java)
- `INVARIANT` → Direct type conversion

## Type Parameter Conversion

### `KSTypeParameter.toTypeVariableName(): TypeVariableName`

**Location**: `codegentle-kotlin-ksp/KSTypeNameCross.kt`

Converts type parameters including bounds:

```kotlin
// For: <T : Comparable<T>>
val typeParam: KSTypeParameter = ...
val typeVarName = typeParam.toTypeVariableName()
// TypeVariableName("T", bounds=[Comparable<T>])
```

**Features:**
- Extracts type parameter name
- Converts all bounds to `TypeRef`
- Preserves bound relationships

### `KSTypeParameter.toTypeName(): TypeName`

Convenience method delegating to `toTypeVariableName()`.

## Function Type Conversion

### Lambda Type Conversion

Function types are converted to `KotlinLambdaTypeName` with full support for:

```kotlin
// Simple function: (String, Int) -> Boolean
val funcType: KSType = ...
val lambda = funcType.toTypeName() as KotlinLambdaTypeName
// lambda.parameters = [String, Int]
// lambda.returnType = Boolean

// Extension function: String.(Int) -> Boolean
// lambda.receiver = String
// lambda.parameters = [Int]
// lambda.returnType = Boolean

// Suspend function: suspend (String) -> Unit
// lambda.modifiers = [SUSPEND]

// Context receivers: context(Logger) String.(Int) -> Unit
// lambda.contextReceivers = [Logger]
// lambda.receiver = String
// lambda.parameters = [Int]
```

**Implementation Details:**

Context receivers are detected via the `@ContextFunctionTypeParams` annotation:

```kotlin
val contextReceiverCount = annotations.firstNotNullOfOrNull { annotation ->
    if (annotation.shortName.asString() == "ContextFunctionTypeParams") {
        annotation.arguments.firstOrNull()?.value as? Int
    } else null
} ?: 0
```

Extension functions are detected via `@ExtensionFunctionType` annotation.

**Type Argument Layout:**
```
For: context(C1, C2) T.(P1, P2) -> R
Arguments: [C1, C2, T, P1, P2, R]
           ↑─────↑  ↑  ↑─────↑  ↑
           context  │  params  return
                  receiver
```

## Class Name Conversion

### `KSClassDeclaration.toClassName(): ClassName`

**Location**: `codegentle-common-ksp/KSTypeCross.kt`

Converts class declarations to fully-qualified class names:

```kotlin
val classDecl: KSClassDeclaration = ...
val className = classDecl.toClassName()
// ClassName("com.example", "OuterClass", "InnerClass")
```

**Handles:**
- Top-level classes
- Nested classes (preserves hierarchy)
- Inner classes
- Package names

### `KSType.toClassNameOrNull(): ClassName?`

**Location**: `codegentle-common-ksp/KSTypeCross.kt`

Safe conversion returning null for non-class types:

```kotlin
val type: KSType = ...
val className = type.toClassNameOrNull() ?: error("Not a class type")
```

### `KSTypeReference.toClassNameOrNull(): ClassName?`

Resolves and safely converts type reference to class name.

## Member Name Conversion

### `KSDeclaration.toMemberName(): MemberName`

**Location**: `codegentle-common-ksp/KSTypeCross.kt`

Converts declarations to member names for static imports:

```kotlin
val funcDecl: KSFunctionDeclaration = ...
val memberName = funcDecl.toMemberName()
// MemberName("com.example", "OuterClass", "staticMethod")
```

### `KSFunctionDeclaration.toMemberName(): MemberName`

Specialized for function declarations.

### `KSPropertyDeclaration.toMemberName(): MemberName`

Specialized for property declarations (static fields, constants).

## Package Name Conversion

### `KSFile.toPackageName(): PackageName`

**Location**: `codegentle-common-ksp/KSTypeCross.kt`

Converts file package to CodeGentle package name:

```kotlin
val file: KSFile = ...
val packageName = file.toPackageName()
```

## Utility Functions

### `KSType.isPrimitive(): Boolean`

**Location**: `codegentle-common-ksp/KSTypeCross.kt`

Checks if type is a Kotlin primitive:

```kotlin
val type: KSType = ...
if (type.isPrimitive()) {
    // Handle primitive
}
```

**Primitives**: Boolean, Byte, Short, Int, Long, Float, Double, Char, String, Unit, Nothing

### `KSType.isArrayType(): Boolean`

**Location**: `codegentle-common-ksp/KSTypeCross.kt`

Checks if type is `kotlin.Array`:

```kotlin
val type: KSType = ...
if (type.isArrayType()) {
    val componentType = type.getArrayComponentType()
}
```

### `KSType.getArrayComponentType(): KSType?`

Extracts component type from array types.

### `KSTypeParameter.boundClassNames: List<ClassName>`

**Location**: `codegentle-common-ksp/KSTypeCross.kt`

Property accessor extracting bound class names from type parameters:

```kotlin
val typeParam: KSTypeParameter = ...
val bounds = typeParam.boundClassNames
// For <T : Comparable<T>>, returns [ClassName("kotlin", "Comparable")]
```

## Error Handling

### ERROR TYPE Support

The conversion functions handle Kotlin's ERROR TYPE when annotations can't be validated:

```kotlin
val annotationType = annotation.annotationType
val annotationTypeValidated = annotationType.validate()

if (annotationTypeValidated) {
    // Normal path: annotation.annotationType.resolve()
} else {
    // ERROR TYPE path: check annotation.toString()
    annotationType.toString() == "<ERROR TYPE: kotlin.ExtensionFunctionType>"
}
```

This allows processing to continue even when annotation types are unresolved during KSP rounds.

## Best Practices

1. **Check validation first**: Use `KSType.validate()` before conversion
2. **Handle nullability**: Functions like `toClassNameOrNull()` return nullable types
3. **Use specific functions**: Prefer `toClassName()` over `toTypeName()` when you know it's a class
4. **Preserve metadata**: Consider using `TypeRef` wrappers to attach additional metadata

## Complete Example

```kotlin
class TypeConverter(val resolver: Resolver) {
    fun convertFunction(funcDecl: KSFunctionDeclaration): KotlinFunctionSpec {
        // Convert function name
        val name = funcDecl.simpleName.asString()

        // Convert return type
        val returnType = funcDecl.returnType?.toTypeName()
            ?: KotlinClassNames.UNIT

        // Convert parameters
        val parameters = funcDecl.parameters.map { param ->
            KotlinValueParameterSpec(
                name = param.name?.asString() ?: "_",
                typeRef = param.type.toTypeName().ref()
            )
        }

        // Check for receiver (extension function)
        val receiver = funcDecl.extensionReceiver?.toTypeName()

        // Build function spec
        return KotlinFunctionSpec(name) {
            returns(returnType.ref())
            parameters.forEach { addParameter(it) }
            receiver?.let { receiver(it.ref()) }
        }
    }
}
```

## Related Documentation

- [TypeName System](../naming/README.md) - Understanding CodeGentle's type hierarchy
- [Context Receivers](./context-receivers.md) - Detailed context receiver handling
- [Spec Conversion](./spec-conversion.md) - Converting complete declarations
