# Generic Type Names

The CodeGentle naming system provides comprehensive support for representing generic and complex type constructs. This document covers the specialized `TypeName` implementations for arrays, parameterized types, type variables, and wildcards.

## ArrayTypeName

`ArrayTypeName` represents array types with a component type.

### Structure

```kotlin
interface ArrayTypeName : TypeName {
    val componentType: TypeRef<*>
}
```

### Construction

```kotlin
// Create array type from component type
val stringArrayType = ArrayTypeName(String::class.ref())
val intArrayType = ArrayTypeName(JavaPrimitiveTypeNames.INT.ref())

// Multi-dimensional arrays
val stringArrayArray = ArrayTypeName(stringArrayType.ref())
```

### Utility Functions

```kotlin
// Content comparison
fun contentHashCode(): Int
infix fun contentEquals(other: ArrayTypeName): Boolean
```

### Examples

```kotlin
// Basic array types
val byteArray = ArrayTypeName(JavaPrimitiveTypeNames.BYTE.ref())
val stringArray = ArrayTypeName(JavaClassNames.STRING.ref())

// Multi-dimensional arrays
val intMatrix = ArrayTypeName(ArrayTypeName(JavaPrimitiveTypeNames.INT.ref()).ref())

// Generic component arrays
val listArray = ArrayTypeName(
    JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref()).ref()
)

// Check array properties
println(stringArray.componentType.typeName)  // java.lang.String
```

## ParameterizedTypeName

`ParameterizedTypeName` represents generic types with type arguments (e.g., `List<String>`, `Map<K, V>`).

### Structure

```kotlin
interface ParameterizedTypeName : TypeName, Named {
    val enclosingType: ParameterizedTypeName?  // For nested generic types
    val rawType: ClassName                     // Base class without generics
    val typeArguments: List<TypeRef<*>>        // Generic type arguments
    val name: String                          // Same as rawType.name
}
```

### Core Methods

```kotlin
// Create nested generic classes
fun nestedClass(name: String): ParameterizedTypeName
fun nestedClass(name: String, typeArguments: List<TypeRef<*>>): ParameterizedTypeName
fun nestedClass(name: String, vararg typeArguments: TypeRef<*>): ParameterizedTypeName
```

### Construction

#### Basic Construction

```kotlin
// Create parameterized type from raw type and arguments
val listOfString = ParameterizedTypeName(
    JavaClassNames.LIST,
    JavaClassNames.STRING.ref()
)

val mapOfStringToInt = ParameterizedTypeName(
    JavaClassNames.MAP,
    JavaClassNames.STRING.ref(),
    JavaPrimitiveTypeNames.INT.ref()
)

// From iterable type arguments
val typeArgs = listOf(JavaClassNames.STRING.ref(), JavaClassNames.INTEGER.ref())
val mapType = ParameterizedTypeName(JavaClassNames.MAP, typeArgs)
```

#### DSL Extension

```kotlin
// Using ClassName extension method
val listOfString = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())
val setOfInt = JavaClassNames.SET.parameterized(JavaPrimitiveTypeNames.INT.ref())

// Multiple type arguments
val mapOfStringToList = JavaClassNames.MAP.parameterized(
    JavaClassNames.STRING.ref(),
    JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref()).ref()
)
```

### Nested Generic Classes

```kotlin
// Create nested generic types
val outerGeneric = JavaClassNames.OUTER.parameterized(JavaClassNames.STRING.ref())
val innerGeneric = outerGeneric.nestedClass("Inner", JavaPrimitiveTypeNames.INT.ref())

// Complex nesting
val complexType = outerGeneric
    .nestedClass("Middle", JavaClassNames.STRING.ref())
    .nestedClass("Deep", JavaPrimitiveTypeNames.LONG.ref())
```

### Examples

```kotlin
// Common generic types
val arrayList = ClassName("java.util", "ArrayList")
    .parameterized(JavaClassNames.STRING.ref())

val hashMap = ClassName("java.util", "HashMap").parameterized(
    JavaClassNames.STRING.ref(),
    JavaPrimitiveTypeNames.INT.ref()
)

// Nested generics
val listOfLists = JavaClassNames.LIST.parameterized(
    JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref()).ref()
)

// Complex generic types
val functionType = ClassName("java.util.function", "Function").parameterized(
    JavaClassNames.STRING.ref(),
    JavaPrimitiveTypeNames.INT.ref()
)

// Check properties
println(listOfString.rawType)                    // java.util.List
println(listOfString.typeArguments.first())      // java.lang.String
println(listOfString.name)                       // List
```

## TypeVariableName

`TypeVariableName` represents type variables used in generic declarations (e.g., `T`, `E`, `K`, `V`).

### Structure

```kotlin
interface TypeVariableName : TypeName, Named {
    val name: String                    // Variable name (e.g., "T", "E")
    val bounds: List<TypeRef<*>>       // Upper bounds constraints
}
```

### Construction

```kotlin
// Simple type variable without bounds
val t = TypeVariableName("T")
val e = TypeVariableName("E")

// Type variable with single bound
val bounded = TypeVariableName("T", JavaClassNames.STRING.ref())

// Type variable with multiple bounds
val multiBounded = TypeVariableName("T", 
    JavaClassNames.STRING.ref(),
    ClassName("java.io", "Serializable").ref()
)

// From iterable bounds
val bounds = listOf(JavaClassNames.STRING.ref(), JavaClassNames.COMPARABLE.ref())
val complexBounded = TypeVariableName("T", bounds)
```

### Examples

```kotlin
// Common type variables
val genericT = TypeVariableName("T")
val elementE = TypeVariableName("E")
val keyK = TypeVariableName("K")
val valueV = TypeVariableName("V")

// Bounded type variables
val numberBound = TypeVariableName("N", JavaClassNames.NUMBER.ref())
val comparableBound = TypeVariableName("T", JavaClassNames.COMPARABLE.parameterized(
    TypeVariableName("T").ref()
).ref())

// Multiple constraints
val serializable = ClassName("java.io", "Serializable")
val cloneable = ClassName("java.lang", "Cloneable")
val multiConstrained = TypeVariableName("T", serializable.ref(), cloneable.ref())

// Check properties
println(numberBound.name)                        // N
println(numberBound.bounds.first().typeName)     // java.lang.Number
println(multiConstrained.bounds.size)            // 2
```

## WildcardTypeName

`WildcardTypeName` represents wildcard types used in Java generics (e.g., `?`, `? extends String`, `? super Integer`).

### Hierarchy

```kotlin
sealed interface WildcardTypeName : TypeName {
    val bounds: List<TypeRef<*>>
    val isEmpty: Boolean  // True if no bounds
}

// No bounds: `?` in Java, `*` in Kotlin
data object EmptyWildcardTypeName : WildcardTypeName

// Upper bounds: `? extends T` in Java, `out T` in Kotlin  
interface UpperWildcardTypeName : WildcardTypeName

// Lower bounds: `? super T` in Java, `in T` in Kotlin
interface LowerWildcardTypeName : WildcardTypeName
```

### Construction

#### Empty Wildcard

```kotlin
// Unbounded wildcard
val unbounded = WildcardTypeName()  // or EmptyWildcardTypeName
```

#### Upper Bounded Wildcards

```kotlin
// Single upper bound: ? extends String
val extendsString = LowerWildcardTypeName(JavaClassNames.STRING.ref())

// Multiple upper bounds: ? extends T1 & T2
val multipleUpper = LowerWildcardTypeName(listOf(
    JavaClassNames.STRING.ref(),
    ClassName("java.io", "Serializable").ref()
))
```

#### Lower Bounded Wildcards

```kotlin
// Single lower bound: ? super Integer
val superInteger = UpperWildcardTypeName(JavaClassNames.INTEGER.ref())

// Multiple lower bounds: ? super T1 & T2
val multipleLower = UpperWildcardTypeName(listOf(
    JavaClassNames.NUMBER.ref(),
    ClassName("java.io", "Serializable").ref()
))
```

### Conversion

```kotlin
val wildcard = WildcardTypeName()

// Convert to specific bound types
val asUpper = wildcard.toUpper(listOf(JavaClassNames.STRING.ref()))
val asLower = wildcard.toLower(listOf(JavaClassNames.INTEGER.ref()))

// Convert existing bounds
val existing = LowerWildcardTypeName(JavaClassNames.STRING.ref())
val converted = existing.toUpper()  // Uses existing bounds
```

### Examples

```kotlin
// Unbounded wildcard for List<?>
val listOfAnything = JavaClassNames.LIST.parameterized(EmptyWildcardTypeName.ref())

// Upper bounded for List<? extends Number>
val extendsNumber = LowerWildcardTypeName(JavaClassNames.NUMBER.ref())
val listOfNumbers = JavaClassNames.LIST.parameterized(extendsNumber.ref())

// Lower bounded for List<? super Integer>
val superInteger = UpperWildcardTypeName(JavaClassNames.INTEGER.ref())
val listSuperInteger = JavaClassNames.LIST.parameterized(superInteger.ref())

// Complex wildcard usage
val comparator = ClassName("java.util", "Comparator")
val wildComparator = comparator.parameterized(
    LowerWildcardTypeName(JavaClassNames.STRING.ref()).ref()
)

// Check wildcard properties
println(extendsNumber.bounds.first())           // java.lang.Number
println(EmptyWildcardTypeName.isEmpty)          // true
println(EmptyWildcardTypeName.toString())       // "*"
```

## Type Reference Integration

All generic type names work seamlessly with the `TypeRef` system:

```kotlin
// Create type references
val stringRef = JavaClassNames.STRING.ref()
val listRef = JavaClassNames.LIST.parameterized(stringRef).ref()
val arrayRef = ArrayTypeName(stringRef).ref()

// Use in parameterized types
val mapOfListToArray = JavaClassNames.MAP.parameterized(listRef, arrayRef)

// Complex type hierarchies
val nestedGeneric = JavaClassNames.LIST.parameterized(
    JavaClassNames.MAP.parameterized(
        JavaClassNames.STRING.ref(),
        ArrayTypeName(JavaPrimitiveTypeNames.INT.ref()).ref()
    ).ref()
).ref()
```

## Best Practices

### 1. Use DSL Extensions

Prefer DSL methods over direct constructor calls:

```kotlin
// Preferred
val listOfString = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())

// Avoid
val listOfString = ParameterizedTypeName(JavaClassNames.LIST, JavaClassNames.STRING.ref())
```

### 2. Leverage Type References

Always use `.ref()` when passing types as arguments:

```kotlin
// Correct
val parameterized = rawType.parameterized(argumentType.ref())

// Incorrect
val parameterized = rawType.parameterized(argumentType)  // Compilation error
```

### 3. Wildcard Naming Convention

Follow Java naming conventions for wildcards:

```kotlin
// Upper bounds (covariant): ? extends T
val producer = LowerWildcardTypeName(elementType.ref())

// Lower bounds (contravariant): ? super T  
val consumer = UpperWildcardTypeName(elementType.ref())
```

### 4. Multi-dimensional Arrays

Build multi-dimensional arrays incrementally:

```kotlin
// 2D array: String[][]
val stringArray = ArrayTypeName(JavaClassNames.STRING.ref())
val string2DArray = ArrayTypeName(stringArray.ref())

// 3D array: String[][][]
val string3DArray = ArrayTypeName(string2DArray.ref())
```

## Common Patterns

### Generic Collections

```kotlin
// List<String>
val listOfString = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())

// Map<String, Integer>
val stringToInt = JavaClassNames.MAP.parameterized(
    JavaClassNames.STRING.ref(),
    JavaClassNames.INTEGER.ref()
)

// Set<? extends Number>
val numberSet = JavaClassNames.SET.parameterized(
    LowerWildcardTypeName(JavaClassNames.NUMBER.ref()).ref()
)
```

### Function Types

```kotlin
// Function<String, Integer>
val function = ClassName("java.util.function", "Function").parameterized(
    JavaClassNames.STRING.ref(),
    JavaClassNames.INTEGER.ref()
)

// Predicate<T>
val predicate = ClassName("java.util.function", "Predicate").parameterized(
    TypeVariableName("T").ref()
)
```

### Bounded Generics

```kotlin
// Class<? extends Enum<?>>
val enumClass = JavaClassNames.CLASS.parameterized(
    LowerWildcardTypeName(
        ClassName("java.lang", "Enum").parameterized(EmptyWildcardTypeName.ref()).ref()
    ).ref()
)
```
