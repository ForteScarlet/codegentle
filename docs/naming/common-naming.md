# Common Naming Classes

The `codegentle-common` module provides the foundation for all naming functionality in CodeGentle. These classes are platform-agnostic and work across all supported targets (JVM, JavaScript, Native, etc.).

## TypeName

`TypeName` is the base interface for all type representations in the naming system. It serves as the common ancestor for all type-related naming classes.

```kotlin
interface TypeName
```

### Subclasses

- `ClassName` - Fully qualified class names
- `ArrayTypeName` - Array types
- `ParameterizedTypeName` - Generic/parameterized types
- `TypeVariableName` - Type variables (e.g., `T`, `E`)
- `WildcardTypeName` - Wildcard types (e.g., `? extends String`)

## ClassName

`ClassName` represents fully qualified class names for top-level and nested classes.

### Properties

```kotlin
interface ClassName : Named, TypeName, Comparable<ClassName> {
    val simpleName: String              // Simple class name (e.g., "Entry" for Map.Entry)
    val name: String                   // Same as simpleName
    val packageName: PackageName       // Package containing this class
    val enclosingClassName: ClassName? // Enclosing class for nested classes
    val topLevelClassName: ClassName   // Top-most class in nesting hierarchy
}
```

### Core Methods

```kotlin
// Create peer classes (same package/enclosing class)
fun peerClass(name: String): ClassName

// Create nested classes
fun nestedClass(name: String): ClassName

// Comparison
fun compareTo(other: ClassName): Int
```

### Construction

#### Basic Construction

```kotlin
// From package and class name
val mapEntry = ClassName("java.util", "Map", "Entry")
val string = ClassName("java.lang", "String")

// Using PackageName
val javaLang = PackageName("java", "lang") 
val string = ClassName(javaLang, "String")

// From fully qualified string (best-guess parsing)
val arrayList = ClassName("java.util.ArrayList")
```

#### DSL Extension

```kotlin
// Using PackageName extension
val javaUtil = PackageName("java", "util")
val list = javaUtil.className("List")
val arrayList = javaUtil.className("ArrayList")
```

### Utility Properties and Functions

#### Canonical Name

```kotlin
val canonicalName: String  // "java.util.Map.Entry"
fun appendCanonicalNameTo(appendable: Appendable): Appendable
```

#### Reflection Name (Binary Name)

```kotlin
val reflectionName: String  // "java.util.Map$Entry"
fun appendReflectionNameTo(appendable: Appendable): Appendable
```

#### Simple Names

```kotlin
val simpleNames: List<String>  // ["Map", "Entry"] for Map.Entry
```

#### Content Comparison

```kotlin
fun contentHashCode(): Int
infix fun contentEquals(other: ClassName): Boolean
```

### Examples

```kotlin
// Create class names for common Java types
val string = ClassName("java.lang", "String")
val mapEntry = ClassName("java.util", "Map", "Entry")

// Navigate class hierarchy
val map = mapEntry.enclosingClassName     // java.util.Map
val topLevel = mapEntry.topLevelClassName // java.util.Map

// Create related classes
val hashMap = map.peerClass("HashMap")           // java.util.HashMap
val entrySet = map.nestedClass("EntrySet")       // java.util.Map.EntrySet

// Get different name representations
println(mapEntry.canonicalName)   // "java.util.Map.Entry"
println(mapEntry.reflectionName)  // "java.util.Map$Entry"
println(mapEntry.simpleNames)     // [Map, Entry]
```

## PackageName

`PackageName` represents package names using a chain-like structure where each package component references its parent.

### Structure

```kotlin
interface PackageName : Named {
    val previous: PackageName?  // Parent package (null for empty package)
    val name: String           // Current component name
    
    companion object {
        val EMPTY: PackageName     // Represents empty/default package
    }
}
```

### Construction

#### Basic Construction

```kotlin
// Empty package
val empty = PackageName()

// Single component
val java = PackageName("java")

// Multiple components
val javaUtil = PackageName("java", "util")
val javaUtilConcurrent = PackageName("java", "util", "concurrent")

// From existing package
val util = PackageName(java, "util")

// From list
val path = listOf("java", "util", "concurrent")
val pkg = PackageName(path)
```

#### Parsing from Strings

```kotlin
// Parse dot-separated string
val javaLang = "java.lang".parseToPackageName()
val comExample = "com.example.project".parseToPackageName()

// Strict vs non-strict parsing
val strict = PackageName("java", strict = true)    // Validates no dots
val lenient = PackageName("java.lang", strict = false) // Single component with dots
```

### Operations

#### Concatenation

```kotlin
val java = PackageName("java")
val util = PackageName("util")

// Add single component
val javaUtil = java + "util"           // java.util

// Add package
val javaUtilConcurrent = javaUtil + PackageName("concurrent") // java.util.concurrent
```

#### Hierarchy Navigation

```kotlin
val javaUtilConcurrent = "java.util.concurrent".parseToPackageName()

// Get root package
val root = javaUtilConcurrent.top()  // PackageName("java")

// Get all components as sequence
val sequence = javaUtilConcurrent.nameSequence()  // [java, java.util, java.util.concurrent]

// Get all components as list  
val names = javaUtilConcurrent.names()  // Same as above but as List
```

#### Properties and Utilities

```kotlin
val pkg = "com.example.project".parseToPackageName()

// Check if empty
pkg.isEmpty()     // false
pkg.isNotEmpty()  // true

// Get parts
pkg.parts         // ["com", "example", "project"]
pkg.partSequence  // Sequence<String>

// Conditional execution
pkg.ifNotEmpty { p -> println("Package: $p") }
pkg.ifEmpty { p -> println("Empty package") }
```

#### String Operations

```kotlin
val pkg = "java.util.concurrent".parseToPackageName()

// Default string representation (uses dots)
pkg.toString()  // "java.util.concurrent"

// Custom separator for appendTo
val sb = StringBuilder()
pkg.appendTo(sb, separator = "/")  // "java/util/concurrent"

// Convert to relative path
pkg.toRelativePath()        // "java/util/concurrent" 
pkg.toRelativePath("\\")    // "java\util\concurrent"
```

### Examples

```kotlin
// Create package hierarchy
val empty = PackageName()
val com = PackageName("com")
val comExample = com + "example"
val project = comExample + "project"

// Parse from string
val javaLang = "java.lang".parseToPackageName()

// Navigate hierarchy
val util = "java.util".parseToPackageName()
val root = util.top()  // PackageName("java")

// Iterate components
util.nameSequence().forEach { pkg ->
    println("Package component: ${pkg.name}")
}
// Output: 
// Package component: java
// Package component: util

// File system paths
val sourcePath = javaLang.toRelativePath("/")  // "java/lang"
```

## MemberName

`MemberName` represents class members such as static methods, static fields, enum elements, and nested classes that can be imported and referenced.

### Properties

```kotlin
interface MemberName : TypeName, Named, Comparable<MemberName> {
    val name: String                   // Member name
    val packageName: PackageName       // Package containing the member
    val enclosingClassName: ClassName? // Class containing the member (if any)
}
```

### Construction

```kotlin
// Package-level member (e.g., Kotlin top-level function)
val topLevelFunction = MemberName("com.example", "utility")

// Class member (e.g., static method/field)
val valueOf = MemberName("java.lang.String", "valueOf")

// Using ClassName
val stringClass = ClassName("java.lang", "String")
val valueOf = MemberName(stringClass, "valueOf")

// Using PackageName
val javaLang = "java.lang".parseToPackageName()
val member = MemberName(javaLang, "member")

// Full specification
val packageName = "com.example".parseToPackageName()
val className = ClassName("com.example", "Utils")
val helper = MemberName(packageName, className, "helper")
```

### Utility Functions

```kotlin
// Canonical name
val canonicalName: String  // "java.lang.String.valueOf"
fun appendCanonicalNameTo(appendable: Appendable): Appendable

// Content comparison
fun contentHashCode(): Int
infix fun contentEquals(other: MemberName): Boolean
```

### Examples

```kotlin
// Static method reference
val integerValueOf = MemberName("java.lang.Integer", "valueOf")
println(integerValueOf.canonicalName)  // "java.lang.Integer.valueOf"

// Enum constant
val timeUnit = ClassName("java.util.concurrent", "TimeUnit")
val seconds = MemberName(timeUnit, "SECONDS")

// Top-level Kotlin function
val topLevel = MemberName("com.example.utils", "calculateHash")

// Check member properties
println(seconds.packageName)        // java.util.concurrent
println(seconds.enclosingClassName) // java.util.concurrent.TimeUnit
println(seconds.name)               // SECONDS
```

## Best Practices

### 1. Use Factory Functions

Prefer the factory functions over direct constructor calls:

```kotlin
// Preferred
val className = ClassName("java.lang", "String")
val packageName = "java.lang".parseToPackageName()

// Avoid direct implementation constructors
```

### 2. Leverage Extension Functions

Use extension functions for common operations:

```kotlin
// Package extension
val myPackage = "com.example".parseToPackageName()
val myClass = myPackage.className("MyClass")

// String parsing
val parsed = "java.util.ArrayList".parseToPackageName()
```

### 3. Content Equality

Use `contentEquals` for semantic equality instead of reference equality:

```kotlin
val class1 = ClassName("java.lang", "String")
val class2 = ClassName("java.lang.String")  // Best-guess constructor

// Reference equality - false
class1 == class2

// Content equality - true  
class1 contentEquals class2
```

### 4. Hierarchy Navigation

Take advantage of hierarchy navigation methods:

```kotlin
val innerClass = ClassName("com.example", "Outer", "Inner", "DeepInner")

// Navigate up the hierarchy
val outer = innerClass.topLevelClassName
val direct = innerClass.enclosingClassName

// Create siblings
val sibling = innerClass.peerClass("Sibling")
val child = innerClass.nestedClass("Child")
```
