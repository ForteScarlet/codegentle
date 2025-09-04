# Java Naming Utilities

The `codegentle-java` module provides Java-specific naming utilities and predefined constants that make Java code generation more convenient and consistent.

## JavaClassNames

`JavaClassNames` is a utility object containing predefined `ClassName` constants for common Java types.

### Basic Types

```kotlin
object JavaClassNames {
    // Fundamental types
    val OBJECT: ClassName        // java.lang.Object
    val STRING: ClassName        // java.lang.String
    
    // Boxed primitive types
    val BOXED_VOID: ClassName     // java.lang.Void
    val BOXED_BOOLEAN: ClassName  // java.lang.Boolean
    val BOXED_BYTE: ClassName     // java.lang.Byte
    val BOXED_SHORT: ClassName    // java.lang.Short
    val BOXED_INT: ClassName      // java.lang.Integer
    val BOXED_LONG: ClassName     // java.lang.Long
    val BOXED_CHAR: ClassName     // java.lang.Character
    val BOXED_FLOAT: ClassName    // java.lang.Float
    val BOXED_DOUBLE: ClassName   // java.lang.Double
}
```

### Usage Examples

```kotlin
// Use predefined class names
val objectType = JavaClassNames.OBJECT
val stringType = JavaClassNames.STRING
val integerType = JavaClassNames.BOXED_INT

// Create generic types with predefined classes
val listOfStrings = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())
val mapOfStringToInt = JavaClassNames.MAP.parameterized(
    JavaClassNames.STRING.ref(),
    JavaClassNames.BOXED_INT.ref()
)

// Use in method signatures
val valueOf = MemberName(JavaClassNames.STRING, "valueOf")
val parseInt = MemberName(JavaClassNames.BOXED_INT, "parseInt")

// Check canonical names
println(JavaClassNames.BOXED_INT.canonicalName)  // "java.lang.Integer"
println(JavaClassNames.BOXED_CHAR.canonicalName) // "java.lang.Character"
```

## JavaPrimitiveTypeName

`JavaPrimitiveTypeName` represents Java primitive types with their keywords and boxing capabilities.

### Interface

```kotlin
interface JavaPrimitiveTypeName : TypeName {
    val keyword: String        // Primitive keyword (e.g., "int", "boolean")
    fun box(): TypeName       // Returns corresponding boxed type
}
```

### Primitive Keywords

The interface contains internal constants for all Java primitive keywords:

```kotlin
interface JavaPrimitiveTypeName : TypeName {
    val keyword: String
    fun box(): TypeName
    
    companion object {
        internal const val VOID = "void"
        internal const val BOOLEAN = "boolean"  
        internal const val BYTE = "byte"
        internal const val SHORT = "short"
        internal const val INT = "int"
        internal const val LONG = "long"
        internal const val CHAR = "char"
        internal const val FLOAT = "float"
        internal const val DOUBLE = "double"
    }
}
```

## JavaPrimitiveTypeNames

`JavaPrimitiveTypeNames` provides predefined instances of primitive types.

### Available Primitives

```kotlin
object JavaPrimitiveTypeNames {
    val VOID: TypeName      // void
    val BOOLEAN: TypeName   // boolean
    val BYTE: TypeName      // byte
    val SHORT: TypeName     // short
    val INT: TypeName       // int
    val LONG: TypeName      // long
    val CHAR: TypeName      // char
    val FLOAT: TypeName     // float
    val DOUBLE: TypeName    // double
}
```

### Usage Examples

```kotlin
// Use primitive types in method signatures
val primitiveInt = JavaPrimitiveTypeNames.INT
val primitiveBool = JavaPrimitiveTypeNames.BOOLEAN

// Create arrays of primitives
val intArray = ArrayTypeName(JavaPrimitiveTypeNames.INT.ref())
val booleanArray = ArrayTypeName(JavaPrimitiveTypeNames.BOOLEAN.ref())

// Boxing operations (if supported by implementation)
if (primitiveInt is JavaPrimitiveTypeName) {
    val boxedInt = primitiveInt.box()  // java.lang.Integer
    println("Boxed: ${boxedInt}")
}

// Use in parameterized types (with boxing)
val listOfInts = JavaClassNames.LIST.parameterized(JavaClassNames.BOXED_INT.ref())

// Check primitive properties
if (primitiveBool is JavaPrimitiveTypeName) {
    println(primitiveBool.keyword)  // "boolean"
}
```

### Primitive vs Boxed Types

```kotlin
// Primitive types for method parameters, local variables
val method = JavaMethodSpec.builder("calculate") {
    addParameter("value", JavaPrimitiveTypeNames.INT)
    addParameter("flag", JavaPrimitiveTypeNames.BOOLEAN)
    returns(JavaPrimitiveTypeNames.DOUBLE)
}

// Boxed types for generics and nullable contexts
val optionalInt = ClassName("java.util", "Optional")
    .parameterized(JavaClassNames.BOXED_INT.ref())

val nullableInteger = JavaClassNames.BOXED_INT  // Can be null
```

## JavaAnnotationNames

`JavaAnnotationNames` provides predefined `ClassName` constants for common Java annotations.

### Standard Annotations

```kotlin
object JavaAnnotationNames {
    // Core annotations
    val Override: ClassName          // java.lang.Override
    val Deprecated: ClassName        // java.lang.Deprecated
    val SuppressWarnings: ClassName  // java.lang.SuppressWarnings
    val SafeVarargs: ClassName      // java.lang.SafeVarargs
    
    // Meta-annotations  
    val Documented: ClassName       // java.lang.annotation.Documented
    val Retention: ClassName        // java.lang.annotation.Retention
    val Target: ClassName           // java.lang.annotation.Target
    val Inherited: ClassName        // java.lang.annotation.Inherited
    val Repeatable: ClassName       // java.lang.annotation.Repeatable
    val Native: ClassName           // java.lang.annotation.Native (since Java 8)
    
    // Functional programming
    val FunctionalInterface: ClassName  // java.lang.FunctionalInterface (since Java 8)
    
    // Code generation
    val Generated: ClassName        // javax.annotation.processing.Generated (since Java 9)
}
```

### Usage Examples

```kotlin
// Add annotations to methods
val method = JavaMethodSpec.builder("toString") {
    addAnnotation(JavaAnnotationNames.Override)
    returns(JavaClassNames.STRING)
}

// Deprecation with reason
val deprecatedMethod = JavaMethodSpec.builder("oldMethod") {
    addAnnotation(JavaAnnotationNames.Deprecated) {
        // Add annotation parameters if needed
    }
    addAnnotation(JavaAnnotationNames.SuppressWarnings) {
        addMember("value", "\"deprecation\"")
    }
}

// Functional interface annotation
val functionalInterface = JavaTypeSpec.interfaceBuilder("Processor") {
    addAnnotation(JavaAnnotationNames.FunctionalInterface)
    addMethod("process") {
        addParameter("input", JavaClassNames.STRING)
        returns(JavaClassNames.STRING)
    }
}

// Generated code annotation
val generatedClass = JavaTypeSpec.classBuilder("GeneratedClass") {
    addAnnotation(JavaAnnotationNames.Generated) {
        addMember("value", "\"CodeGentle\"")
        addMember("date", "\"2025-01-01\"")
    }
}

// Meta-annotation usage
val customAnnotation = JavaTypeSpec.annotationBuilder("MyAnnotation") {
    addAnnotation(JavaAnnotationNames.Target) {
        // Specify target elements
    }
    addAnnotation(JavaAnnotationNames.Retention) {
        // Specify retention policy  
    }
    addAnnotation(JavaAnnotationNames.Documented)
}
```

### Version Compatibility

Some annotations are version-specific:

```kotlin
// Java 8+ annotations
val functionalInterface = JavaAnnotationNames.FunctionalInterface
val nativeAnnotation = JavaAnnotationNames.Native

// Java 9+ annotations  
val generated = JavaAnnotationNames.Generated  // javax.annotation.processing.Generated
```

## JavaArrayTypeName

`JavaArrayTypeName` provides Java-specific extensions for array type emission.

### Varargs Support

The Java module extends `ArrayTypeName` with varargs support:

```kotlin
// Extension function for Java-specific array emission
fun ArrayTypeName.emitTo(codeWriter: JavaCodeWriter, varargs: Boolean)
```

### Usage Examples

```kotlin
// Standard array syntax
val stringArray = ArrayTypeName(JavaClassNames.STRING.ref())
// Emits as: String[]

// Varargs syntax  
val varargsMethod = JavaMethodSpec.builder("process") {
    addParameter("items", stringArray, varargs = true)
    // Parameter emits as: String... items
}

// Multi-dimensional arrays
val matrix = ArrayTypeName(ArrayTypeName(JavaPrimitiveTypeNames.INT.ref()).ref())
// Emits as: int[][]

// Complex array types
val genericArray = ArrayTypeName(
    JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref()).ref()
)
// Emits as: List<String>[]

// Varargs with generic arrays
val genericVarargs = JavaMethodSpec.builder("combine") {
    addParameter("lists", genericArray, varargs = true)
    // Emits as: List<String>... lists
}
```

### Array Creation Patterns

```kotlin
// Primitive arrays
val intArray = ArrayTypeName(JavaPrimitiveTypeNames.INT.ref())
val byteArray = ArrayTypeName(JavaPrimitiveTypeNames.BYTE.ref())
val charArray = ArrayTypeName(JavaPrimitiveTypeNames.CHAR.ref())

// Object arrays
val stringArray = ArrayTypeName(JavaClassNames.STRING.ref())
val objectArray = ArrayTypeName(JavaClassNames.OBJECT.ref())

// Generic arrays (use with caution due to type erasure)
val listArray = ArrayTypeName(
    JavaClassNames.LIST.parameterized(EmptyWildcardTypeName.ref()).ref()
)

// Multi-dimensional arrays
fun createMultiDimArray(elementType: TypeRef<*>, dimensions: Int): ArrayTypeName {
    var arrayType = ArrayTypeName(elementType)
    repeat(dimensions - 1) {
        arrayType = ArrayTypeName(arrayType.ref())
    }
    return arrayType
}

val threeDArray = createMultiDimArray(JavaPrimitiveTypeNames.INT.ref(), 3)
// Results in: int[][][]
```

## Integration with Code Generation

### Using with JavaFile

```kotlin
val javaFile = JavaFile("com.example") {
    addClass("MyClass") {
        // Use predefined types
        addField(JavaClassNames.STRING, "name") {
            addModifier(JavaModifier.PRIVATE)
        }
        
        addMethod("getName") {
            addModifier(JavaModifier.PUBLIC)
            returns(JavaClassNames.STRING)
            addCode("return this.name;")
        }
        
        addMethod("setName") {
            addModifier(JavaModifier.PUBLIC)  
            addParameter("name", JavaClassNames.STRING)
            returns(JavaPrimitiveTypeNames.VOID)
            addCode("this.name = name;")
        }
    }
}
```

### Type Compatibility

```kotlin
// Java types work seamlessly with common types
val commonString: ClassName = JavaClassNames.STRING
val javaString: ClassName = JavaClassNames.STRING

// Content equality works across modules
val isEqual = commonString contentEquals javaString  // true

// Use in generic contexts
val listOfJavaStrings = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())
val setOfJavaInts = JavaClassNames.SET.parameterized(JavaClassNames.BOXED_INT.ref())
```

## Best Practices

### 1. Use Predefined Constants

Always prefer predefined constants over manual construction:

```kotlin
// Preferred
val stringType = JavaClassNames.STRING
val intType = JavaPrimitiveTypeNames.INT

// Avoid
val stringType = ClassName("java.lang", "String")
val intType = TypeVariableName("int")  // Wrong! This creates a type variable
```

### 2. Primitive vs Boxed Types

Choose the appropriate type based on context:

```kotlin
// Use primitives for local variables, parameters, return types
val method = JavaMethodSpec.builder("calculate") {
    addParameter("value", JavaPrimitiveTypeNames.INT)        // primitive parameter
    returns(JavaPrimitiveTypeNames.DOUBLE)                   // primitive return
}

// Use boxed types for generics and nullable contexts
val optionalValue = ClassName("java.util", "Optional")
    .parameterized(JavaClassNames.BOXED_INT.ref())           // must be boxed

val nullableInteger: ClassName = JavaClassNames.BOXED_INT   // can be null
```

### 3. Array vs Collection Types

Consider the use case when choosing between arrays and collections:

```kotlin
// Arrays for fixed-size, performance-critical scenarios
val buffer = ArrayTypeName(JavaPrimitiveTypeNames.BYTE.ref())

// Collections for dynamic, feature-rich scenarios  
val dynamicList = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())

// Varargs for flexible method parameters
val logMethod = JavaMethodSpec.builder("log") {
    addParameter("messages", ArrayTypeName(JavaClassNames.STRING.ref()), varargs = true)
}
```

### 4. Annotation Usage

Use annotations appropriately for code clarity and tooling support:

```kotlin
// Always override toString, equals, hashCode
val toString = JavaMethodSpec.builder("toString") {
    addAnnotation(JavaAnnotationNames.Override)
    addModifier(JavaModifier.PUBLIC)
    returns(JavaClassNames.STRING)
}

// Mark deprecated methods  
val oldMethod = JavaMethodSpec.builder("oldMethod") {
    addAnnotation(JavaAnnotationNames.Deprecated)
    addAnnotation(JavaAnnotationNames.SuppressWarnings) {
        addMember("value", "\"deprecation\"")
    }
}

// Document generated code
val generatedClass = JavaTypeSpec.classBuilder("Generated") {
    addAnnotation(JavaAnnotationNames.Generated) {
        addMember("value", "\"CodeGentle\"")
    }
}
```

## Common Patterns

### Builder Pattern with Java Types

```kotlin
val builderClass = JavaTypeSpec.classBuilder("PersonBuilder") {
    // Fields using Java types
    addField(JavaClassNames.STRING, "name") { addModifier(JavaModifier.PRIVATE) }
    addField(JavaPrimitiveTypeNames.INT, "age") { addModifier(JavaModifier.PRIVATE) }
    
    // Fluent methods
    addMethod("name") {
        addModifier(JavaModifier.PUBLIC)
        addParameter("name", JavaClassNames.STRING)
        returns(ClassName("PersonBuilder"))
        addCode("this.name = name; return this;")
    }
    
    addMethod("build") {
        addModifier(JavaModifier.PUBLIC)
        returns(ClassName("Person"))
        addCode("return new Person(name, age);")
    }
}
```

### Generic Utility Classes

```kotlin
val utilityClass = JavaTypeSpec.classBuilder("Collections") {
    addModifier(JavaModifier.PUBLIC, JavaModifier.FINAL)
    
    // Generic method with type variables
    addMethod("emptyList") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC)
        addTypeVariable(TypeVariableName("T"))
        returns(JavaClassNames.LIST.parameterized(TypeVariableName("T").ref()))
        addCode("return new ArrayList<>();")
    }
}
```
