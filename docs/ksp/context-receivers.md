# Context Receivers in KSP

This document explains how CodeGentle's KSP integration handles Kotlin 2.0+ context receivers.

## Overview

Context receivers allow functions and lambdas to implicitly receive context parameters. CodeGentle KSP integration fully supports detecting and converting context receivers from KSP annotations.

## Detection Mechanism

Context receivers are detected through the `@ContextFunctionTypeParams` annotation:

```kotlin
// For a function like:
context(Logger, Database)
suspend fun query(sql: String): Result

// KSP represents this with annotation:
@ContextFunctionTypeParams(2) // 2 context receivers
```

### Implementation

```kotlin
val contextReceiverCount = annotations.firstNotNullOfOrNull { annotation ->
    val annotationType = annotation.annotationType
    val annotationTypeValidated = annotationType.validate()

    val isContextAnnotation = annotation.shortName.asString() == "ContextFunctionTypeParams" &&
        if (annotationTypeValidated) {
            annotationType.resolve().declaration.qualifiedName?.asString() ==
                "kotlin.ContextFunctionTypeParams"
        } else {
            // Handle ERROR TYPE situation
            annotationType.toString() == "<ERROR TYPE: kotlin.ContextFunctionTypeParams>"
        }

    if (isContextAnnotation) {
        annotation.arguments.firstOrNull()?.value as? Int
    } else null
} ?: 0
```

## Type Argument Layout

For function types with context receivers, KSP arranges type arguments as:

```
context(C1, C2) T.(P1, P2) -> R
Arguments: [C1, C2, T, P1, P2, R]
           ↑─────↑  ↑  ↑─────↑  ↑
           context  │  params  return
                  receiver
```

## Conversion Example

```kotlin
// Source function
context(Logger, Database)
suspend String.query(sql: String): Result { ... }

// KSP processing
val funcDecl: KSFunctionDeclaration = ...
val spec = funcDecl.toKotlinFunctionSpec()

// Result
assert(spec.contextParameters.size == 2)
assert(spec.contextParameters[0].typeRef.typeName == ClassName("", "Logger"))
assert(spec.contextParameters[1].typeRef.typeName == ClassName("", "Database"))
assert(spec.receiver == ClassName("kotlin", "String"))
assert(spec.modifiers.contains(KotlinModifier.SUSPEND))
```

## KotlinContextParameterSpec

Context parameters are represented as:

```kotlin
interface KotlinContextParameterSpec : KotlinParameterSpec {
    val name: String?  // Can be null for `_` unnamed contexts
    val typeRef: TypeRef<*>
}
```

### Unnamed Context Receivers

```kotlin
context(_) // Unnamed context
fun foo() { ... }

// Converts to KotlinContextParameterSpec with name = null
```

## ERROR TYPE Handling

During KSP processing, annotation types may not validate and show as ERROR TYPE:

```
<ERROR TYPE: kotlin.ContextFunctionTypeParams>
```

CodeGentle handles this by checking annotation short names and toString() representations, allowing processing to continue.

## Complete Example

```kotlin
@OptIn(ExperimentalContextReceivers::class)
context(Logger)
suspend fun query(sql: String): Result = TODO()

// In KSP processor:
val funcDecl = resolver.getFunctionDeclarationsByName("query").first()
val spec = funcDecl.toKotlinFunctionSpec()

// Generate implementation with context:
val generated = KotlinFile("com.example".parseToPackageName()) {
    addFunction(spec)
}
```

## Related Documentation

- [Type Conversion](./type-conversion.md) - How context receivers are extracted from function types
- [Spec Conversion](./spec-conversion.md) - Converting complete functions with contexts
- [Kotlin Specs](../spec/kotlin-specs.md) - KotlinContextParameterSpec documentation
