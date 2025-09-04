# Common Specs

The `codegentle-common` module provides the foundational interfaces and common functionality that all other Spec classes extend. These base interfaces define the core contracts and shared behavior across both Java and Kotlin code generation.

## Base Interfaces

### Spec

The root interface for all code specifications.

```kotlin
public interface Spec
```

This is the fundamental interface that all specification classes implement. It serves as a marker interface to identify code generation specifications and provides a common base for the type hierarchy.

**Usage:**
- All XxxSpec interfaces extend from this base
- Used for generic constraints and type safety
- No direct construction methods (abstract interface)

### NamedSpec

Extends both `Spec` and `Named` for specifications that have names.

```kotlin
public interface NamedSpec : Spec, Named
```

This interface combines the specification marker with naming capability, making it suitable for any code element that has an identifiable name.

**Properties:**
- Inherits `name: String` from `Named` interface
- Provides identity and lookup capabilities

**Used by:**
- All type specifications (classes, interfaces, enums)
- Method and function specifications  
- Field and property specifications
- Parameter specifications

**Example Usage:**
```kotlin
// NamedSpec is typically extended by concrete spec interfaces
interface MySpec : NamedSpec {
    // Additional properties and methods
}
```

## Common Collector Interfaces

The common module provides several collector interfaces that enable fluent builder APIs. These are mixed into spec builders to provide domain-specific functionality.

### DocCollector<T>

Provides documentation/javadoc/kdoc collection capabilities.

```kotlin
public interface DocCollector<T> {
    fun addDoc(format: String, vararg args: CodeArgumentPart): T
    fun addDoc(codeBlock: CodeValue): T
    // Additional documentation methods
}
```

**Methods:**
- `addDoc(format, ...args)` - Add formatted documentation
- `addDoc(codeBlock)` - Add pre-built code block documentation
- Format string supports placeholders for arguments

**Usage Example:**
```kotlin
// In a spec builder that extends DocCollector
builder.addDoc("This is a documentation comment.")
builder.addDoc("Method with parameter %T", someType.ref())
```

### InitializerBlockCollector<T>

Collects initializer blocks for types that support them.

```kotlin
public interface InitializerBlockCollector<T> {
    fun addInitializerBlock(format: String, vararg args: CodeArgumentPart): T
    fun addInitializerBlock(codeBlock: CodeValue): T
}
```

**Methods:**
- `addInitializerBlock(format, ...args)` - Add formatted initializer code
- `addInitializerBlock(codeBlock)` - Add pre-built initializer block

**Usage Example:**
```kotlin
// Add initialization code that runs when instance is created
builder.addInitializerBlock("println(\"Object initialized\")")
builder.addInitializerBlock("this.field = defaultValue")
```

### TypeVariableCollector<T>

Manages generic type parameters for types and methods.

```kotlin
public interface TypeVariableCollector<T> {
    fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): T
    fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): T
    fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): T
}
```

**Methods:**
- `addTypeVariable(typeVariable)` - Add single type parameter
- `addTypeVariables(iterable)` - Add multiple type parameters from collection
- `addTypeVariables(vararg)` - Add multiple type parameters as varargs

**Usage Example:**
```kotlin
// Add generic type parameters like <T>, <T extends Something>
val tParam = TypeVariableName.get("T").ref()
val boundedParam = TypeVariableName.get("E", someInterface).ref()

builder.addTypeVariable(tParam)
builder.addTypeVariable(boundedParam)
```

### AnnotationRefCollector<T>

Collects annotation references for annotated elements.

```kotlin
public interface AnnotationRefCollector<T> {
    fun addAnnotation(annotation: AnnotationRef): T
    fun addAnnotations(annotations: Iterable<AnnotationRef>): T
    fun addAnnotations(vararg annotations: AnnotationRef): T
}
```

**Methods:**
- `addAnnotation(annotation)` - Add single annotation
- `addAnnotations(iterable)` - Add multiple annotations from collection  
- `addAnnotations(vararg)` - Add multiple annotations as varargs

**Usage Example:**
```kotlin
// Add annotations like @Override, @Deprecated, etc.
val overrideAnnotation = JavaAnnotationNames.OVERRIDE.ref()
val deprecatedAnnotation = JavaAnnotationNames.DEPRECATED.ref()

builder.addAnnotation(overrideAnnotation)
builder.addAnnotations(overrideAnnotation, deprecatedAnnotation)
```

## Configuration Interfaces

### SuperclassConfigurer<T>

Configures superclass relationships for types that support inheritance.

```kotlin
public interface SuperclassConfigurer<T> {
    fun superclass(superclass: TypeName): T
    fun superclass(superclass: TypeRef<*>): T
}
```

**Methods:**
- `superclass(TypeName)` - Set superclass by type name
- `superclass(TypeRef)` - Set superclass by type reference

**Usage Example:**
```kotlin
// Set superclass for a class that extends another
builder.superclass(ClassName.get("com.example", "BaseClass"))
builder.superclass(someClassRef)
```

### SuperinterfaceCollector<T>

Collects interface implementations for types that can implement interfaces.

```kotlin
public interface SuperinterfaceCollector<T> {
    fun addSuperinterface(superinterface: TypeName): T
    fun addSuperinterface(superinterface: TypeRef<*>): T
    fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): T
    // Additional overloads
}
```

**Methods:**
- `addSuperinterface(TypeName/TypeRef)` - Add single interface implementation
- `addSuperinterfaces(Iterable)` - Add multiple interface implementations
- Support for both `TypeName` and `TypeRef<*>` parameters

**Usage Example:**
```kotlin
// Add interface implementations
builder.addSuperinterface(ClassName.get("java.io", "Serializable"))
builder.addSuperinterface(someInterfaceRef)

val interfaces = listOf(
    ClassName.get("java.lang", "Comparable"),
    ClassName.get("java.lang", "Cloneable")
)
builder.addSuperinterfaces(interfaces)
```

## Design Patterns

### Collector Pattern

The collector interfaces follow a consistent pattern:

1. **Single Item Methods**: `add[Item](item)` for adding individual elements
2. **Multiple Item Methods**: `add[Items](iterable)` and `add[Items](vararg)` for bulk operations
3. **Fluent Interface**: All methods return the builder type `T` for method chaining
4. **Type Safety**: Use strongly-typed parameters and generic constraints

### Builder Integration

These collectors are mixed into spec builders through interface inheritance:

```kotlin
// Example spec builder extending multiple collectors
public interface SomeSpecBuilder<T, B> : 
    DocCollector<B>,
    TypeVariableCollector<B>,
    AnnotationRefCollector<B> {
    
    fun build(): T
}
```

This approach provides:
- **Composition over Inheritance**: Mix only needed functionality
- **Consistent API**: Same method patterns across all collectors
- **Type Safety**: Strongly-typed builder chains
- **Extensibility**: Easy to add new collector types

## Cross-Module Usage

These common interfaces are used consistently across Java and Kotlin modules:

### In Java Specs
```kotlin
// Java builders extend common collectors
public interface JavaMethodSpecBuilder : 
    DocCollector<JavaMethodSpecBuilder>,
    TypeVariableCollector<JavaMethodSpecBuilder>,
    JavaModifierCollector<JavaMethodSpecBuilder> // Java-specific
```

### In Kotlin Specs  
```kotlin
// Kotlin builders extend common collectors
public interface KotlinFunctionSpecBuilder :
    DocCollector<KotlinFunctionSpecBuilder>, 
    TypeVariableCollector<KotlinFunctionSpecBuilder>,
    KotlinModifierCollector<KotlinFunctionSpecBuilder> // Kotlin-specific
```
