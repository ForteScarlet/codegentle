# Kotlin Naming Utilities

The `codegentle-kotlin` module provides Kotlin-specific naming utilities and predefined constants that make Kotlin code generation more convenient and consistent.

## KotlinClassNames

`KotlinClassNames` is a utility object containing predefined `ClassName` constants for common Kotlin types.

### Basic Types

```kotlin
object KotlinClassNames {
    // Special Kotlin types
    val UNIT: ClassName           // kotlin.Unit
    val NOTHING: ClassName        // kotlin.Nothing  
    val ANY: ClassName            // kotlin.Any
    
    // Primitive types
    val BOOLEAN: ClassName        // kotlin.Boolean
    val BYTE: ClassName           // kotlin.Byte
    val SHORT: ClassName          // kotlin.Short
    val INT: ClassName            // kotlin.Int
    val LONG: ClassName           // kotlin.Long
    val FLOAT: ClassName          // kotlin.Float
    val DOUBLE: ClassName         // kotlin.Double
    val CHAR: ClassName           // kotlin.Char
    val STRING: ClassName         // kotlin.String
    
    // Collection types (immutable)
    val LIST: ClassName           // kotlin.collections.List
    val SET: ClassName            // kotlin.collections.Set
    val MAP: ClassName            // kotlin.collections.Map
    val COLLECTION: ClassName     // kotlin.collections.Collection
    val ITERABLE: ClassName       // kotlin.collections.Iterable
    
    // Mutable collection types
    val MUTABLE_LIST: ClassName       // kotlin.collections.MutableList
    val MUTABLE_SET: ClassName        // kotlin.collections.MutableSet
    val MUTABLE_MAP: ClassName        // kotlin.collections.MutableMap
    val MUTABLE_COLLECTION: ClassName // kotlin.collections.MutableCollection
    
    // Array type
    val ARRAY: ClassName          // kotlin.Array
}
```

### Usage Examples

```kotlin
// Use predefined Kotlin types
val unitType = KotlinClassNames.UNIT
val stringType = KotlinClassNames.STRING
val intType = KotlinClassNames.INT

// Create generic types
val listOfStrings = KotlinClassNames.LIST.parameterized(KotlinClassNames.STRING.ref())
val mapOfIntToString = KotlinClassNames.MAP.parameterized(
    KotlinClassNames.INT.ref(),
    KotlinClassNames.STRING.ref()
)

// Mutable collections
val mutableList = KotlinClassNames.MUTABLE_LIST.parameterized(KotlinClassNames.STRING.ref())
val mutableSet = KotlinClassNames.MUTABLE_SET.parameterized(KotlinClassNames.INT.ref())

// Use in function signatures
val toStringMethod = KotlinFunctionSpec.builder("toString") {
    returns(KotlinClassNames.STRING)
    addCode("return \"Example\"")
}

// Check canonical names
println(KotlinClassNames.UNIT.canonicalName)     // "kotlin.Unit"
println(KotlinClassNames.LIST.canonicalName)     // "kotlin.collections.List"
```

### Kotlin vs Java Types

```kotlin
// Kotlin types map to platform types
val kotlinString = KotlinClassNames.STRING      // kotlin.String
val kotlinInt = KotlinClassNames.INT           // kotlin.Int
val kotlinBoolean = KotlinClassNames.BOOLEAN   // kotlin.Boolean

// Collections are different from Java
val kotlinList = KotlinClassNames.LIST         // kotlin.collections.List (immutable)
val javaList = JavaClassNames.LIST             // java.util.List (mutable)

// Special Kotlin types
val unit = KotlinClassNames.UNIT               // No Java equivalent
val nothing = KotlinClassNames.NOTHING         // No Java equivalent  
val any = KotlinClassNames.ANY                 // Similar to java.lang.Object
```

## KotlinLambdaTypeName

`KotlinLambdaTypeName` represents Kotlin function/lambda types with support for receivers, context receivers, and suspend functions.

### Structure

```kotlin
interface KotlinLambdaTypeName : TypeName, KotlinModifierContainer {
    val receiver: TypeRef<*>?                    // Extension receiver type
    val contextReceivers: List<TypeRef<*>>       // Context receivers  
    val parameters: List<KotlinValueParameterSpec> // Function parameters
    val returnType: TypeRef<*>                   // Return type
    val isSuspend: Boolean                       // Suspend function flag
}
```

### Builder Interface

```kotlin
interface Builder : KotlinValueParameterCollector<Builder> {
    fun receiver(receiver: TypeRef<*>): Builder
    fun addContextReceivers(vararg contextReceivers: TypeRef<*>): Builder
    fun addContextReceivers(contextReceivers: Iterable<TypeRef<*>>): Builder
    fun addContextReceiver(contextReceiver: TypeRef<*>): Builder
    fun returns(type: TypeRef<*>): Builder
    fun suspend(isSuspend: Boolean = true): Builder
    fun build(): KotlinLambdaTypeName
}
```

### Construction

#### Basic Lambda Types

```kotlin
// Simple lambda: () -> Unit
val simpleFunction = KotlinLambdaTypeName()

// Lambda with parameters: (String, Int) -> String
val withParams = KotlinLambdaTypeName(KotlinClassNames.STRING.ref()) {
    addParameter(KotlinClassNames.STRING.ref())
    addParameter(KotlinClassNames.INT.ref())
}

// Lambda with custom return type: (Int) -> Boolean
val predicate = KotlinLambdaTypeName(KotlinClassNames.BOOLEAN.ref()) {
    addParameter(KotlinClassNames.INT.ref())
}
```

#### Extension Functions

```kotlin
// Extension function: String.() -> Unit
val stringExtension = KotlinLambdaTypeName {
    receiver(KotlinClassNames.STRING.ref())
}

// Extension with parameters: String.(Int) -> Boolean
val stringPredicate = KotlinLambdaTypeName(KotlinClassNames.BOOLEAN.ref()) {
    receiver(KotlinClassNames.STRING.ref())
    addParameter(KotlinClassNames.INT.ref())
}
```

#### Suspend Functions

```kotlin
// Suspend function: suspend () -> String
val suspendFunction = KotlinLambdaTypeName(KotlinClassNames.STRING.ref()) {
    suspend()
}

// Suspend extension: suspend String.() -> Unit  
val suspendExtension = KotlinLambdaTypeName {
    receiver(KotlinClassNames.STRING.ref())
    suspend()
}
```

#### Context Receivers

```kotlin
// Context receiver function: context(Logger) () -> Unit
val contextLogger = ClassName("com.example", "Logger")
val withContext = KotlinLambdaTypeName {
    addContextReceiver(contextLogger.ref())
}

// Multiple context receivers: context(Logger, Database) (String) -> User
val multiContext = KotlinLambdaTypeName(userType.ref()) {
    addContextReceiver(contextLogger.ref())
    addContextReceiver(databaseType.ref())
    addParameter(KotlinClassNames.STRING.ref())
}
```

### DSL Extensions

```kotlin
// Extension functions for cleaner syntax
fun receiver(receiver: TypeName, block: TypeRefBuilderDsl<TypeName> = {}): KotlinLambdaTypeName.Builder

fun addContextReceiver(contextReceiver: TypeName, block: TypeRefBuilderDsl<TypeName> = {}): KotlinLambdaTypeName.Builder

fun returns(type: TypeName, block: TypeRefBuilderDsl<TypeName> = {}): KotlinLambdaTypeName.Builder
```

### Usage Examples

```kotlin
// DSL-style construction
val dslLambda = KotlinLambdaTypeName(KotlinClassNames.STRING) {
    receiver(KotlinClassNames.STRING)
    addParameter(KotlinClassNames.INT) {
        name = "index" 
    }
    suspend(true)
}

// Complex function type: context(CoroutineScope) suspend String.(Int) -> List<String>
val complexLambda = KotlinLambdaTypeName {
    addContextReceiver(ClassName("kotlinx.coroutines", "CoroutineScope"))
    receiver(KotlinClassNames.STRING)
    addParameter(KotlinClassNames.INT)
    returns(KotlinClassNames.LIST.parameterized(KotlinClassNames.STRING.ref()))
    suspend()
}

// Use in property declarations
val lambdaProperty = KotlinPropertySpec.builder("callback", complexLambda.ref()) {
    addModifier(KotlinModifier.PRIVATE)
}

// Use in function parameters
val higherOrderFunction = KotlinFunctionSpec.builder("processStrings") {
    addParameter("processor", dslLambda.ref())
    returns(KotlinClassNames.UNIT)
}
```

## KotlinAnnotationNames

`KotlinAnnotationNames` provides predefined `ClassName` constants for common Kotlin annotations.

**Note**: This section requires examination of the actual KotlinAnnotationNames implementation to provide accurate documentation.

### Common Kotlin Annotations

Based on typical Kotlin annotation usage, this would likely include:

```kotlin
object KotlinAnnotationNames {
    // Nullability annotations
    val Nullable: ClassName       // kotlin.annotations.Nullable (if exists)
    val NonNull: ClassName        // kotlin.annotations.NonNull (if exists)
    
    // JVM interop annotations  
    val JvmStatic: ClassName      // kotlin.jvm.JvmStatic
    val JvmField: ClassName       // kotlin.jvm.JvmField
    val JvmName: ClassName        // kotlin.jvm.JvmName
    val JvmOverloads: ClassName   // kotlin.jvm.JvmOverloads
    val Throws: ClassName         // kotlin.jvm.Throws
    
    // Serialization annotations (if kotlinx.serialization support exists)
    val Serializable: ClassName   // kotlinx.serialization.Serializable
    val SerialName: ClassName     // kotlinx.serialization.SerialName
    
    // Experimental annotations
    val OptIn: ClassName          // kotlin.OptIn
    val RequiresOptIn: ClassName  // kotlin.RequiresOptIn
}
```

## KotlinMemberName

`KotlinMemberName` extends the common `MemberName` functionality with Kotlin-specific features.

**Note**: This section requires examination of the actual KotlinMemberName implementation.

## KotlinDelegatedClassName

`KotlinDelegatedClassName` represents delegated class names in Kotlin.

**Note**: This section requires examination of the actual KotlinDelegatedClassName implementation.

### Potential Structure

```kotlin
interface KotlinDelegatedClassName : TypeName {
    val delegateType: TypeRef<*>      // The type being delegated to
    val interfaceType: TypeRef<*>     // The interface being implemented
}
```

## Integration with Kotlin Code Generation

### Using with KotlinFile

```kotlin
val kotlinFile = KotlinFile("com.example") {
    addClass("MyClass") {
        // Use Kotlin predefined types
        addProperty(KotlinClassNames.STRING, "name") {
            addModifier(KotlinModifier.PRIVATE)
            mutable(true)
        }
        
        addFunction("processStrings") {
            addParameter("processor", KotlinLambdaTypeName(KotlinClassNames.STRING.ref()) {
                receiver(KotlinClassNames.STRING.ref())
            })
            returns(KotlinClassNames.LIST.parameterized(KotlinClassNames.STRING.ref()))
        }
        
        // Suspend function
        addFunction("fetchData") {
            addModifier(KotlinModifier.SUSPEND)
            returns(KotlinClassNames.STRING)
        }
    }
}
```

### Type Compatibility

```kotlin
// Kotlin types work with common naming system
val kotlinString: ClassName = KotlinClassNames.STRING
val commonString: ClassName = ClassName("kotlin", "String")

// Content equality across modules
val isEqual = kotlinString contentEquals commonString  // true

// Generic type compatibility
val kotlinList = KotlinClassNames.LIST.parameterized(KotlinClassNames.STRING.ref())
val javaList = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())
// These are different types with different semantics
```

## Best Practices

### 1. Use Kotlin-Specific Types

Prefer Kotlin types over Java equivalents for Kotlin code generation:

```kotlin
// Preferred for Kotlin
val kotlinString = KotlinClassNames.STRING
val kotlinList = KotlinClassNames.LIST
val kotlinInt = KotlinClassNames.INT

// Avoid for pure Kotlin code (unless Java interop required)
val javaString = JavaClassNames.STRING
val javaList = JavaClassNames.LIST
val javaInt = JavaPrimitiveTypeNames.INT
```

### 2. Leverage Function Types

Use `KotlinLambdaTypeName` for proper Kotlin function type representation:

```kotlin
// Correct Kotlin function type
val kotlinFunction = KotlinLambdaTypeName(KotlinClassNames.STRING.ref()) {
    addParameter(KotlinClassNames.INT.ref())
}

// Less idiomatic (Java-style)  
val javaFunction = ClassName("kotlin.jvm.functions", "Function1")
    .parameterized(KotlinClassNames.INT.ref(), KotlinClassNames.STRING.ref())
```

### 3. Handle Nullability

Consider nullability in type usage:

```kotlin
// Nullable types in Kotlin
val nullableString = KotlinClassNames.STRING.nullable()  // String?

// Non-null types (default)
val nonNullString = KotlinClassNames.STRING             // String
```

### 4. Use Appropriate Collection Types

Choose between mutable and immutable collections:

```kotlin
// Immutable collections (preferred for data)
val readOnlyList = KotlinClassNames.LIST.parameterized(KotlinClassNames.STRING.ref())

// Mutable collections (when mutation needed)
val mutableList = KotlinClassNames.MUTABLE_LIST.parameterized(KotlinClassNames.STRING.ref())
```

## Common Patterns

### Data Classes with Kotlin Types

```kotlin
val dataClass = KotlinTypeSpec.classBuilder("Person") {
    addModifier(KotlinModifier.DATA)
    
    primaryConstructor {
        addParameter("name", KotlinClassNames.STRING)
        addParameter("age", KotlinClassNames.INT)
        addParameter("emails", KotlinClassNames.LIST.parameterized(KotlinClassNames.STRING.ref()))
    }
    
    addProperty("name", KotlinClassNames.STRING) {
        initializer("name")
    }
    
    addProperty("age", KotlinClassNames.INT) {
        initializer("age")
    }
    
    addProperty("emails", KotlinClassNames.LIST.parameterized(KotlinClassNames.STRING.ref())) {
        initializer("emails")
    }
}
```

### Extension Functions

```kotlin
val extensionFunction = KotlinFunctionSpec.builder("isNotEmpty") {
    receiver(KotlinClassNames.STRING)
    returns(KotlinClassNames.BOOLEAN)
    addCode("return this.length > 0")
}
```

### Higher-Order Functions

```kotlin
val higherOrderFunction = KotlinFunctionSpec.builder("processItems") {
    addTypeVariable(TypeVariableName("T"))
    addParameter("items", KotlinClassNames.LIST.parameterized(TypeVariableName("T").ref()))
    addParameter("processor", KotlinLambdaTypeName(KotlinClassNames.BOOLEAN.ref()) {
        addParameter(TypeVariableName("T").ref())
    })
    returns(KotlinClassNames.LIST.parameterized(TypeVariableName("T").ref()))
}
```

### Suspend Functions

```kotlin
val suspendFunction = KotlinFunctionSpec.builder("fetchUserData") {
    addModifier(KotlinModifier.SUSPEND)
    addParameter("userId", KotlinClassNames.STRING)
    returns(ClassName("com.example", "User"))
    
    addCode("""
        // Simulate async operation
        delay(100)
        return User(userId)
    """.trimIndent())
}
```

## Platform Interoperability

### JVM Target Considerations

```kotlin
// When targeting JVM, some interop might be needed
val jvmInteropFunction = KotlinFunctionSpec.builder("javaCompatible") {
    addAnnotation(KotlinAnnotationNames.JvmName) {
        addMember("name", "\"javaFriendlyName\"")
    }
    addAnnotation(KotlinAnnotationNames.JvmStatic)
}
```

### Multiplatform Considerations

```kotlin
// Types that work across all Kotlin platforms
val multiplatformTypes = listOf(
    KotlinClassNames.STRING,
    KotlinClassNames.INT,
    KotlinClassNames.BOOLEAN,
    KotlinClassNames.LIST,
    KotlinClassNames.MAP
)

// Platform-specific types should be avoided in common code
```
