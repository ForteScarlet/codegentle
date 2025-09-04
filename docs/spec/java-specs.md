# Java Specs

The `codegentle-java` module provides comprehensive specifications for generating Java source code. This document covers all Java-specific Spec classes, their construction methods, properties, and unique Java language features.

## Table of Contents

- [Base Java Interfaces](#base-java-interfaces)
- [Type Specifications](#type-specifications)
- [Member Specifications](#member-specifications)
- [Parameter Specifications](#parameter-specifications)
- [Java-Specific Collectors](#java-specific-collectors)
- [Construction Patterns](#construction-patterns)
- [Examples](#examples)

## Base Java Interfaces

### JavaSpec

The base interface for all Java specifications.

```kotlin
public interface JavaSpec : Spec {
    fun emit(codeWriter: JavaCodeWriter)
}
```

**Features:**
- Extends common `Spec` interface
- Provides emission capability to `JavaCodeWriter`
- Base for all Java-specific specification types

## Type Specifications

### JavaTypeSpec

The sealed interface for all Java type declarations (classes, interfaces, enums, etc.).

```kotlin
public sealed interface JavaTypeSpec : JavaSpec {
    val name: String?
    val kind: Kind
    val javadoc: CodeValue
    val annotations: List<AnnotationRef>
    val modifiers: Set<JavaModifier>
    val typeVariables: List<TypeRef<TypeVariableName>>
    val superclass: TypeName?
    val superinterfaces: List<TypeName>
    val fields: List<JavaFieldSpec>
    val staticBlock: CodeValue
    val initializerBlock: CodeValue
    val methods: List<JavaMethodSpec>
    val subtypes: List<JavaTypeSpec>
}
```

**Properties:**
- `name` - Type name (nullable for anonymous types)
- `kind` - Type kind (CLASS, INTERFACE, ENUM, etc.)
- `javadoc` - Documentation comments
- `annotations` - Type annotations
- `modifiers` - Access modifiers and keywords
- `typeVariables` - Generic type parameters
- `superclass` - Extended class (if applicable)
- `superinterfaces` - Implemented interfaces
- `fields` - Field declarations
- `staticBlock` - Static initialization block
- `initializerBlock` - Instance initialization block
- `methods` - Method declarations
- `subtypes` - Nested types

#### JavaTypeSpec.Kind

Enumeration of Java type kinds with specific behaviors:

```kotlin
enum class Kind {
    CLASS,                    // Standard classes
    INTERFACE,               // Interfaces
    ENUM,                    // Enum types
    ANNOTATION,              // Annotation types
    RECORD,                  // Record types (Java 14+)
    SEALED_CLASS,            // Sealed classes (Java 17+)
    NON_SEALED_CLASS,        // Non-sealed classes
    SEALED_INTERFACE,        // Sealed interfaces
    NON_SEALED_INTERFACE     // Non-sealed interfaces
}
```

**Kind-Specific Features:**
- **INTERFACE**: Implicit `public static final` for fields, `public abstract` for methods
- **ENUM**: Supports enum constants, implements interfaces only
- **ANNOTATION**: Supports default values for methods
- **RECORD**: Immutable data carriers with automatic accessors
- **SEALED_***: Restricted inheritance hierarchies

### JavaSimpleTypeSpec

Specification for simple types (classes and interfaces).

```kotlin
public interface JavaSimpleTypeSpec : NamedSpec, JavaTypeSpec {
    override val name: String
}
```

**Construction:**
```kotlin
// DSL construction (recommended)
val classSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MyClass") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("A simple example class.")
    
    addField(JavaFieldSpec("field", JavaClassNames.STRING.ref()) {
        addModifier(JavaModifier.PRIVATE)
        initializer("\"default\"")
    })
    
    addMethod(JavaMethodSpec("getField") {
        addModifier(JavaModifier.PUBLIC)
        returns(JavaClassNames.STRING.ref())
        addCode("return this.field;")
    })
}

// Builder construction
val classSpec = JavaSimpleTypeSpec.builder(JavaTypeSpec.Kind.CLASS, "MyClass")
    .addModifier(JavaModifier.PUBLIC)
    .addDoc("A simple example class.")
    .build()
```

**Generated Output:**
```java
/**
 * A simple example class.
 */
public class MyClass {
    private String field = "default";
    
    public String getField() {
        return this.field;
    }
}
```

### JavaEnumTypeSpec

Specification for Java enum types.

```kotlin
public interface JavaEnumTypeSpec : NamedSpec, JavaTypeSpec {
    val enumConstants: List<JavaFieldSpec>
}
```

**Construction:**
```kotlin
val enumSpec = JavaEnumTypeSpec("Color") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("RGB color enumeration.")
    
    addEnumConstant("RED")
    addEnumConstant("GREEN") 
    addEnumConstant("BLUE")
    
    addMethod(JavaMethodSpec("isWarm") {
        addModifier(JavaModifier.PUBLIC)
        returns(JavaPrimitiveTypeNames.BOOLEAN.ref())
        addCode("return this == RED;")
    })
}
```

**Generated Output:**
```java
/**
 * RGB color enumeration.
 */
public enum Color {
    RED,
    GREEN,
    BLUE;
    
    public boolean isWarm() {
        return this == RED;
    }
}
```

### JavaRecordTypeSpec

Specification for Java record types (Java 14+).

```kotlin
public interface JavaRecordTypeSpec : NamedSpec, JavaTypeSpec {
    val recordComponents: List<JavaParameterSpec>
}
```

**Construction:**
```kotlin
val recordSpec = JavaRecordTypeSpec("Person") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("A person record.")
    
    addRecordComponent(JavaParameterSpec("name", JavaClassNames.STRING.ref()))
    addRecordComponent(JavaParameterSpec("age", JavaPrimitiveTypeNames.INT.ref()))
    
    addMethod(JavaMethodSpec("isAdult") {
        addModifier(JavaModifier.PUBLIC)
        returns(JavaPrimitiveTypeNames.BOOLEAN.ref())
        addCode("return age >= 18;")
    })
}
```

**Generated Output:**
```java
/**
 * A person record.
 */
public record Person(String name, int age) {
    public boolean isAdult() {
        return age >= 18;
    }
}
```

### JavaAnnotationTypeSpec

Specification for Java annotation types.

```kotlin
public interface JavaAnnotationTypeSpec : NamedSpec, JavaTypeSpec {
    // Annotation-specific properties
}
```

**Construction:**
```kotlin
val annotationSpec = JavaAnnotationTypeSpec("MyAnnotation") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("Custom annotation.")
    
    addMethod(JavaMethodSpec("value") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.ABSTRACT)
        returns(JavaClassNames.STRING.ref())
        defaultValue("\"default\"")
    })
    
    addMethod(JavaMethodSpec("required") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.ABSTRACT)
        returns(JavaPrimitiveTypeNames.BOOLEAN.ref())
        defaultValue("false")
    })
}
```

**Generated Output:**
```java
/**
 * Custom annotation.
 */
public @interface MyAnnotation {
    String value() default "default";
    boolean required() default false;
}
```

## Member Specifications

### JavaMethodSpec

Specification for Java methods and constructors.

```kotlin
public interface JavaMethodSpec : JavaSpec, NamedSpec, JavaModifierContainer {
    val name: String
    val javadoc: CodeValue
    val annotations: List<AnnotationRef>
    val modifiers: Set<JavaModifier>
    val typeVariables: List<TypeRef<TypeVariableName>>
    val returnType: TypeRef<*>?
    val parameters: List<JavaParameterSpec>
    val isVarargs: Boolean
    val exceptions: List<TypeRef<*>>
    val code: CodeValue
    val defaultValue: CodeValue
    val isConstructor: Boolean
}
```

**Construction:**
```kotlin
// Regular method
val methodSpec = JavaMethodSpec("calculateSum") {
    addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC)
    addDoc("Calculates the sum of two integers.")
    returns(JavaPrimitiveTypeNames.INT.ref())
    addParameter(JavaParameterSpec("a", JavaPrimitiveTypeNames.INT.ref()))
    addParameter(JavaParameterSpec("b", JavaPrimitiveTypeNames.INT.ref()))
    addCode("return a + b;")
}

// Constructor
val constructorSpec = JavaMethodSpec {
    addModifier(JavaModifier.PUBLIC)
    addParameter(JavaParameterSpec("name", JavaClassNames.STRING.ref()))
    addCode("this.name = name;")
}

// Varargs method
val varargsMethodSpec = JavaMethodSpec("sum") {
    addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC)
    returns(JavaPrimitiveTypeNames.INT.ref())
    addParameter(JavaParameterSpec("numbers", JavaPrimitiveTypeNames.INT.arrayType().ref()))
    varargs()
    addCode("""
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    """.trimIndent())
}

// Method with exceptions
val methodWithExceptions = JavaMethodSpec("readFile") {
    addModifier(JavaModifier.PUBLIC)
    returns(JavaClassNames.STRING.ref())
    addParameter(JavaParameterSpec("path", JavaClassNames.STRING.ref()))
    addException(ClassName.get("java.io", "IOException").ref())
    addCode("// File reading code")
}
```

**Generated Output:**
```java
/**
 * Calculates the sum of two integers.
 */
public static int calculateSum(int a, int b) {
    return a + b;
}

public MyClass(String name) {
    this.name = name;
}

public static int sum(int... numbers) {
    int sum = 0;
    for (int number : numbers) {
        sum += number;
    }
    return sum;
}

public String readFile(String path) throws IOException {
    // File reading code
}
```

### JavaFieldSpec

Specification for Java fields.

```kotlin
public interface JavaFieldSpec : JavaSpec, NamedSpec, JavaModifierContainer {
    val name: String
    val type: TypeRef<*>
    val javadoc: CodeValue
    val annotations: List<AnnotationRef>
    val modifiers: Set<JavaModifier>
    val initializer: CodeValue
}
```

**Construction:**
```kotlin
// Simple field
val fieldSpec = JavaFieldSpec("name", JavaClassNames.STRING.ref()) {
    addModifier(JavaModifier.PRIVATE)
    addDoc("The name field.")
}

// Field with initializer
val initializedFieldSpec = JavaFieldSpec("count", JavaPrimitiveTypeNames.INT.ref()) {
    addModifier(JavaModifier.PRIVATE, JavaModifier.STATIC)
    initializer("0")
}

// Constant field
val constantSpec = JavaFieldSpec("MAX_SIZE", JavaPrimitiveTypeNames.INT.ref()) {
    addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC, JavaModifier.FINAL)
    initializer("1000")
}

// Generic field
val listFieldSpec = JavaFieldSpec("items", 
    ParameterizedTypeName.get(
        ClassName.get("java.util", "List"),
        JavaClassNames.STRING
    ).ref()
) {
    addModifier(JavaModifier.PRIVATE)
    initializer("new %T()", ClassName.get("java.util", "ArrayList"))
}
```

**Generated Output:**
```java
/**
 * The name field.
 */
private String name;

private static int count = 0;

public static final int MAX_SIZE = 1000;

private List<String> items = new ArrayList<>();
```

## Parameter Specifications

### JavaParameterSpec

Specification for method/constructor parameters.

```kotlin
public interface JavaParameterSpec : JavaSpec, NamedSpec, JavaModifierContainer {
    val name: String
    val type: TypeRef<*>
    val annotations: List<AnnotationRef>
    val modifiers: Set<JavaModifier>
}
```

**Construction:**
```kotlin
// Simple parameter
val paramSpec = JavaParameterSpec("value", JavaClassNames.STRING.ref())

// Parameter with annotations
val annotatedParamSpec = JavaParameterSpec("id", JavaPrimitiveTypeNames.LONG.ref()) {
    addAnnotation(ClassName.get("javax.annotation", "Nonnull").ref())
}

// Final parameter
val finalParamSpec = JavaParameterSpec("config", someConfigType.ref()) {
    addModifier(JavaModifier.FINAL)
}
```

**Generated Output:**
```java
String value
@Nonnull long id
final Config config
```

## Java-Specific Collectors

### JavaModifierCollector<T>

Collects Java access modifiers and keywords.

```kotlin
public interface JavaModifierCollector<T> {
    fun addModifier(modifier: JavaModifier): T
    fun addModifiers(modifiers: Iterable<JavaModifier>): T
    fun addModifiers(vararg modifiers: JavaModifier): T
}
```

**Java Modifiers:**
- **Access**: `PUBLIC`, `PROTECTED`, `PRIVATE`
- **Inheritance**: `ABSTRACT`, `FINAL`, `STATIC`
- **Synchronization**: `SYNCHRONIZED`, `VOLATILE`
- **Native**: `NATIVE`, `STRICTFP`, `TRANSIENT`
- **Sealed**: `SEALED`, `NON_SEALED` (Java 17+)

### StaticBlockCollector<T>

Collects static initialization blocks (Java-specific).

```kotlin
public interface StaticBlockCollector<T> {
    fun addStaticBlock(format: String, vararg args: CodeArgumentPart): T
    fun addStaticBlock(codeBlock: CodeValue): T
}
```

**Usage:**
```kotlin
classBuilder.addStaticBlock("""
    System.out.println("Class loaded");
    initialize();
""".trimIndent())
```

### JavaMethodCollector<T>

Collects Java methods.

```kotlin
public interface JavaMethodCollector<T> {
    fun addMethod(method: JavaMethodSpec): T
    fun addMethods(methods: Iterable<JavaMethodSpec>): T
    fun addMethods(vararg methods: JavaMethodSpec): T
}
```

### JavaFieldCollector<T>

Collects Java fields.

```kotlin
public interface JavaFieldCollector<T> {
    fun addField(field: JavaFieldSpec): T
    fun addFields(fields: Iterable<JavaFieldSpec>): T
    fun addFields(vararg fields: JavaFieldSpec): T
}
```

### JavaSubtypeCollector<T>

Collects nested Java types.

```kotlin
public interface JavaSubtypeCollector<T> {
    fun addSubtype(subtype: JavaTypeSpec): T
    fun addSubtypes(subtypes: Iterable<JavaTypeSpec>): T
    fun addSubtypes(vararg subtypes: JavaTypeSpec): T
}
```

## Construction Patterns

### DSL vs Builder

CodeGentle provides both DSL and traditional builder patterns:

```kotlin
// DSL Pattern (Recommended)
val spec = JavaSimpleTypeSpec(Kind.CLASS, "MyClass") {
    addModifier(JavaModifier.PUBLIC)
    addField(JavaFieldSpec("field", stringType) {
        addModifier(JavaModifier.PRIVATE)
    })
}

// Builder Pattern
val spec = JavaSimpleTypeSpec.builder(Kind.CLASS, "MyClass")
    .addModifier(JavaModifier.PUBLIC)
    .addField(
        JavaFieldSpec.builder("field", stringType)
            .addModifier(JavaModifier.PRIVATE)
            .build()
    )
    .build()
```

### Companion Object Methods

Each spec type provides companion object methods for construction:

```kotlin
// JavaMethodSpec companions
JavaMethodSpec.methodBuilder("methodName")     // Regular method
JavaMethodSpec.constructorBuilder()            // Constructor
JavaMethodSpec.mainBuilder()                   // Main method

// JavaSimpleTypeSpec companion
JavaSimpleTypeSpec.builder(kind, name)
```

## Examples

### Complete Java Class

```kotlin
val serviceClass = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "UserService") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("Service for managing users.")
    
    // Add interface
    addSuperinterface(ClassName.get("com.example", "Service"))
    
    // Private field
    addField(JavaFieldSpec("repository", repositoryType) {
        addModifier(JavaModifier.PRIVATE, JavaModifier.FINAL)
    })
    
    // Constructor
    addMethod(JavaMethodSpec {
        addModifier(JavaModifier.PUBLIC)
        addParameter(JavaParameterSpec("repository", repositoryType))
        addCode("this.repository = repository;")
    })
    
    // Business method
    addMethod(JavaMethodSpec("findUser") {
        addModifier(JavaModifier.PUBLIC)
        returns(userType.ref())
        addParameter(JavaParameterSpec("id", JavaPrimitiveTypeNames.LONG.ref()))
        addException(ClassName.get("java.sql", "SQLException").ref())
        addCode("""
            return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        """.trimIndent())
    })
    
    // Static factory method
    addMethod(JavaMethodSpec("create") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC)
        returns(ClassName.get("com.example", "UserService").ref())
        addParameter(JavaParameterSpec("repository", repositoryType))
        addCode("return new UserService(repository);")
    })
}
```

### Java Interface with Default Methods

```kotlin
val serviceInterface = JavaSimpleTypeSpec(JavaTypeSpec.Kind.INTERFACE, "Cacheable") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("Interface for cacheable objects.")
    
    // Abstract method
    addMethod(JavaMethodSpec("getCacheKey") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.ABSTRACT)
        returns(JavaClassNames.STRING.ref())
    })
    
    // Default method  
    addMethod(JavaMethodSpec("evictCache") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.DEFAULT)
        addCode("""
            String key = getCacheKey();
            CacheManager.evict(key);
        """.trimIndent())
    })
    
    // Static method
    addMethod(JavaMethodSpec("clearAll") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC)
        addCode("CacheManager.clear();")
    })
}
```
