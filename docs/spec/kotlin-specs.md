# Kotlin Spec Documentation

The `codegentle-kotlin` module provides comprehensive Kotlin code generation capabilities through various specification classes. All Kotlin specs extend the base `KotlinSpec` interface and support DSL-based construction patterns.

## Table of Contents

- [Base Interfaces](#base-interfaces)
- [Type Specifications](#type-specifications)
- [Callable Specifications](#callable-specifications)
- [Parameter Specifications](#parameter-specifications)
- [Other Specifications](#other-specifications)
- [DSL Patterns and Examples](#dsl-patterns-and-examples)

## Base Interfaces

### KotlinSpec

The base interface for all Kotlin code generation specifications.

```kotlin
public interface KotlinSpec : Spec, KotlinCodeEmitter
```

**Key Features**:
- Inherits from `Spec` (common base)
- Extends `KotlinCodeEmitter` for Kotlin code generation
- Provides `writeToKotlinString()` extension function

**Usage**:
```kotlin
val kotlinClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass") {
    // DSL configuration
}

// Generate Kotlin code as string
val generatedCode = kotlinClass.writeToKotlinString()
```

### KotlinCallableSpec

Base interface for callable specifications (functions and constructors).

```kotlin
public sealed interface KotlinCallableSpec : KotlinSpec, KotlinModifierContainer
```

**Implemented by**:
- `KotlinFunctionSpec`
- `KotlinConstructorSpec`
- `KotlinPropertyAccessorSpec`

## Type Specifications

### KotlinTypeSpec

Base interface for all Kotlin type declarations.

```kotlin
public sealed interface KotlinTypeSpec : KotlinSpec, NamedSpec, KotlinModifierContainer {
    val kind: Kind
    val name: String
    val kDoc: CodeValue
    val annotations: List<AnnotationRef>
    val modifiers: Set<KotlinModifier>
    val typeVariables: List<TypeRef<TypeVariableName>>
    val superclass: TypeName?
    val superinterfaces: List<TypeName>
    val properties: List<KotlinPropertySpec>
    val initializerBlock: CodeValue
    val functions: List<KotlinFunctionSpec>
    val subtypes: List<KotlinTypeSpec>
}
```

**Kind enum values**:
- `CLASS` - Regular classes
- `INTERFACE` - Interfaces
- `OBJECT` - Object declarations
- `TYPE_ALIAS` - Type aliases

**Factory Methods**:
```kotlin
// Companion object methods
KotlinTypeSpec.classBuilder("MyClass")
KotlinTypeSpec.interfaceBuilder("MyInterface") 
KotlinTypeSpec.objectBuilder("MyObject")
KotlinTypeSpec.companionObjectBuilder()
KotlinTypeSpec.enumBuilder("MyEnum")
KotlinTypeSpec.annotationBuilder("MyAnnotation")
KotlinTypeSpec.valueClassBuilder("MyValue", constructor)
KotlinTypeSpec.typeAliasBuilder("MyAlias", type)
```

### KotlinSimpleTypeSpec

For regular classes and interfaces.

```kotlin
public interface KotlinSimpleTypeSpec : KotlinTypeSpec {
    override val name: String
}
```

**DSL Construction**:
```kotlin
val kotlinClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Person") {
    addModifier(KotlinModifier.DATA)
    addKdoc("Represents a person with name and age.")
    
    // Primary constructor
    primaryConstructor {
        addParameter("name", KotlinClassNames.STRING) {
            addModifier(KotlinModifier.VAL)
        }
        addParameter("age", KotlinClassNames.INT) {
            addModifier(KotlinModifier.VAL)
        }
    }
    
    // Add function
    addFunction("greet") {
        returns(KotlinClassNames.STRING)
        addCode("return \"Hello, I'm \$name and I'm \$age years old\"")
    }
}
```

**Generated Output**:
```kotlin
/**
 * Represents a person with name and age.
 */
data class Person(
    val name: String,
    val age: Int
) {
    fun greet(): String = "Hello, I'm $name and I'm $age years old"
}
```

### KotlinEnumTypeSpec

For enum classes.

```kotlin
public interface KotlinEnumTypeSpec : KotlinTypeSpec {
    val enumConstants: List<KotlinEnumConstant>
}
```

**DSL Construction**:
```kotlin
val enumClass = KotlinEnumTypeSpec("Color") {
    addKdoc("Represents RGB colors.")
    
    // Add enum constants
    addEnumConstant("RED") {
        addConstructorArgument("0xFF0000")
    }
    addEnumConstant("GREEN") {
        addConstructorArgument("0x00FF00") 
    }
    addEnumConstant("BLUE") {
        addConstructorArgument("0x0000FF")
    }
    
    // Primary constructor for enum
    primaryConstructor {
        addParameter("rgb", KotlinClassNames.INT) {
            addModifier(KotlinModifier.VAL)
        }
    }
    
    // Add member function
    addFunction("toHex") {
        returns(KotlinClassNames.STRING)
        addCode("return \"#\" + rgb.toString(16).padStart(6, '0')")
    }
}
```

### KotlinObjectTypeSpec

For object declarations and companion objects.

```kotlin
public interface KotlinObjectTypeSpec : KotlinTypeSpec {
    val isCompanion: Boolean
}
```

**DSL Construction**:
```kotlin
// Regular object
val singletonObject = KotlinObjectTypeSpec("DatabaseManager") {
    addKdoc("Manages database connections.")
    
    addProperty("connection", DatabaseConnection::class.asClassName()) {
        addModifier(KotlinModifier.PRIVATE)
        initializer("createConnection()")
    }
    
    addFunction("query") {
        addParameter("sql", KotlinClassNames.STRING)
        returns(ResultSet::class.asClassName())
        addCode("return connection.executeQuery(sql)")
    }
}

// Companion object
val companionObject = KotlinObjectTypeSpec.companionBuilder() {
    addFunction("create") {
        addParameter("name", KotlinClassNames.STRING)
        returns(MyClass::class.asClassName())
        addCode("return MyClass(name)")
    }
}
```

### KotlinAnnotationTypeSpec

For annotation classes.

```kotlin
public interface KotlinAnnotationTypeSpec : KotlinTypeSpec
```

**DSL Construction**:
```kotlin
val annotationClass = KotlinAnnotationTypeSpec("Deprecated") {
    addKdoc("Marks declarations as deprecated.")
    
    // Annotation constructor parameter
    primaryConstructor {
        addParameter("message", KotlinClassNames.STRING) {
            defaultValue("\"\"")
        }
        addParameter("level", DeprecationLevel::class.asClassName()) {
            defaultValue("DeprecationLevel.WARNING")
        }
    }
}
```

### KotlinValueClassTypeSpec

For value classes (inline classes).

```kotlin
public interface KotlinValueClassTypeSpec : KotlinTypeSpec {
    val primaryConstructor: KotlinConstructorSpec
}
```

**DSL Construction**:
```kotlin
val valueClass = KotlinValueClassTypeSpec("UserId", 
    KotlinConstructorSpec {
        addParameter("value", KotlinClassNames.STRING) {
            addModifier(KotlinModifier.VAL)
        }
    }
) {
    addModifier(KotlinModifier.VALUE)
    addAnnotation(JvmInline::class)
    
    addFunction("isValid") {
        returns(KotlinClassNames.BOOLEAN)
        addCode("return value.isNotBlank() && value.length >= 3")
    }
}
```

## Callable Specifications

### KotlinFunctionSpec

For function declarations.

```kotlin
public interface KotlinFunctionSpec : KotlinCallableSpec {
    override val name: String
    val kDoc: CodeValue
    val annotations: List<AnnotationRef>
    override val modifiers: Set<KotlinModifier>
    val typeVariables: List<TypeRef<TypeVariableName>>
    val receiverType: TypeName?
    val contextReceivers: List<KotlinContextParameterSpec>
    val parameters: List<KotlinValueParameterSpec>
    val returnType: TypeRef<*>?
    val body: CodeValue
}
```

**DSL Construction**:
```kotlin
// Regular function
val function = KotlinFunctionSpec("calculateTotal") {
    addKdoc("Calculates the total price including tax.")
    
    addParameter("price", KotlinClassNames.DOUBLE)
    addParameter("taxRate", KotlinClassNames.DOUBLE) {
        defaultValue("0.08")
    }
    
    returns(KotlinClassNames.DOUBLE)
    addCode("return price * (1 + taxRate)")
}

// Extension function
val extensionFunction = KotlinFunctionSpec("isEven") {
    receiver(KotlinClassNames.INT)
    returns(KotlinClassNames.BOOLEAN)
    addCode("return this % 2 == 0")
}

// Suspend function
val suspendFunction = KotlinFunctionSpec("fetchData") {
    addModifier(KotlinModifier.SUSPEND)
    returns(String::class.asClassName())
    addCode("return withContext(Dispatchers.IO) { /* fetch data */ }")
}
```

### KotlinConstructorSpec

For constructor declarations.

```kotlin
public interface KotlinConstructorSpec : KotlinCallableSpec {
    val parameters: List<KotlinValueParameterSpec>
    val delegatedConstructorCall: CodeValue
    val body: CodeValue
    val isPrimary: Boolean
}
```

**DSL Construction**:
```kotlin
// Primary constructor (used in type specs)
val primaryConstructor = KotlinConstructorSpec {
    addParameter("name", KotlinClassNames.STRING) {
        addModifier(KotlinModifier.VAL)
    }
    addParameter("age", KotlinClassNames.INT) {
        addModifier(KotlinModifier.VAL)
        defaultValue("0")
    }
}

// Secondary constructor
val secondaryConstructor = KotlinConstructorSpec {
    addParameter("name", KotlinClassNames.STRING)
    delegateToThis("name", "0") // Delegate to primary constructor
    addCode("println(\"Created person: \$name\")")
}
```

### KotlinPropertySpec

For property declarations.

```kotlin
public interface KotlinPropertySpec : KotlinSpec, KotlinModifierContainer {
    val name: String
    val kDoc: CodeValue
    val annotations: List<AnnotationRef>
    override val modifiers: Set<KotlinModifier>
    val typeVariables: List<TypeRef<TypeVariableName>>
    val receiverType: TypeName?
    val type: TypeRef<*>
    val initializer: CodeValue
    val delegated: CodeValue
    val getter: KotlinGetterSpec?
    val setter: KotlinSetterSpec?
    val isMutable: Boolean
}
```

**DSL Construction**:
```kotlin
// Simple property
val property = KotlinPropertySpec("name", KotlinClassNames.STRING) {
    isMutable(false) // val
    initializer("\"Unknown\"")
}

// Property with custom getter/setter
val computedProperty = KotlinPropertySpec("fullName", KotlinClassNames.STRING) {
    getter {
        addCode("return \"\$firstName \$lastName\"")
    }
}

// Delegated property  
val delegatedProperty = KotlinPropertySpec("lazy", KotlinClassNames.STRING) {
    delegate("lazy { \"Computed value\" }")
}

// Extension property
val extensionProperty = KotlinPropertySpec("isBlank", KotlinClassNames.BOOLEAN) {
    receiver(KotlinClassNames.STRING)
    getter {
        addCode("return this.trim().isEmpty()")
    }
}
```

## Parameter Specifications

### KotlinValueParameterSpec

For function/constructor value parameters.

```kotlin
public interface KotlinValueParameterSpec : KotlinParameterSpec, KotlinModifierContainer {
    override val name: String
    val kDoc: CodeValue
    val annotations: List<AnnotationRef>
    override val modifiers: Set<KotlinModifier>
    val type: TypeRef<*>
    val defaultValue: CodeValue
}
```

**DSL Construction**:
```kotlin
val parameter = KotlinValueParameterSpec("data", List::class.asClassName().parameterizedBy(KotlinClassNames.STRING)) {
    addModifier(KotlinModifier.VARARG)
    addKdoc("The data items to process.")
}
```

### KotlinContextParameterSpec

For context receivers (Kotlin-specific feature).

```kotlin
public interface KotlinContextParameterSpec : KotlinParameterSpec {
    override val name: String
    val type: TypeRef<*>
}
```

**DSL Construction**:
```kotlin
val contextParameter = KotlinContextParameterSpec("logger", Logger::class.asClassName())

val functionWithContext = KotlinFunctionSpec("log") {
    addContextParameter(contextParameter)
    addParameter("message", KotlinClassNames.STRING)
    addCode("logger.info(message)")
}
```

## Other Specifications

### KotlinPropertyAccessorSpec

Base for property getters and setters.

#### KotlinGetterSpec

```kotlin
val customGetter = KotlinGetterSpec {
    addCode("return field.uppercase()")
}
```

#### KotlinSetterSpec

```kotlin
val customSetter = KotlinSetterSpec {
    addParameter("value", KotlinClassNames.STRING)
    addCode("field = value.trim()")
}
```

### KotlinTypealiasSpec

For type aliases.

```kotlin
public interface KotlinTypealiasSpec : KotlinTypeSpec {
    val aliasedType: TypeRef<*>
}
```

**DSL Construction**:
```kotlin
val typeAlias = KotlinTypealiasSpec("StringList", List::class.asClassName().parameterizedBy(KotlinClassNames.STRING)) {
    addKdoc("A list of strings.")
    addModifier(KotlinModifier.TYPEALIAS)
}
```

## DSL Patterns and Examples

### Complete Class Example

```kotlin
val kotlinClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Repository") {
    addKdoc("Data repository for managing entities.")
    addModifier(KotlinModifier.OPEN)
    
    // Type parameter
    addTypeVariable(TypeVariableName.create("T"))
    
    // Primary constructor
    primaryConstructor {
        addParameter("database", Database::class.asClassName()) {
            addModifier(KotlinModifier.PRIVATE)
            addModifier(KotlinModifier.VAL)
        }
    }
    
    // Properties
    addProperty("cache", Map::class.asClassName().parameterizedBy(
        KotlinClassNames.STRING, 
        TypeVariableName.create("T").asTypeName()
    )) {
        addModifier(KotlinModifier.PRIVATE)
        addModifier(KotlinModifier.VAL)
        delegate("mutableMapOf()")
    }
    
    // Functions
    addFunction("findById") {
        addModifier(KotlinModifier.SUSPEND)
        addParameter("id", KotlinClassNames.STRING)
        returns(TypeVariableName.create("T").asTypeName().copy(nullable = true))
        
        beginControlFlow("return cache[id] ?: run")
        addCode("val entity = database.query<T>(id)")
        addCode("entity?.let { cache[id] = it }")
        addCode("entity")
        endControlFlow()
    }
    
    addFunction("save") {
        addModifier(KotlinModifier.SUSPEND)
        addParameter("entity", TypeVariableName.create("T").asTypeName())
        returns(KotlinClassNames.UNIT)
        
        addCode("database.save(entity)")
        addCode("cache[entity.id] = entity")
    }
    
    // Companion object
    addType(KotlinObjectTypeSpec.companionBuilder() {
        addFunction("create") {
            addTypeVariable(TypeVariableName.create("T"))
            addParameter("database", Database::class.asClassName())
            returns(Repository::class.asClassName().parameterizedBy(TypeVariableName.create("T").asTypeName()))
            addCode("return Repository<T>(database)")
        }
    })
}
```

### Interface with Extension Functions

```kotlin
val serviceInterface = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.INTERFACE, "UserService") {
    addKdoc("Service interface for user operations.")
    
    addFunction("findUser") {
        addModifier(KotlinModifier.ABSTRACT)
        addModifier(KotlinModifier.SUSPEND)
        addParameter("id", KotlinClassNames.STRING)
        returns(User::class.asClassName().copy(nullable = true))
    }
    
    addFunction("createUser") {
        addModifier(KotlinModifier.ABSTRACT)
        addParameter("userData", UserData::class.asClassName())
        returns(User::class.asClassName())
    }
}

// Extension function for the interface
val extensionFunction = KotlinFunctionSpec("findUserOrThrow") {
    addModifier(KotlinModifier.SUSPEND)
    receiver(UserService::class.asClassName())
    addParameter("id", KotlinClassNames.STRING)
    returns(User::class.asClassName())
    
    addCode("return findUser(id) ?: throw UserNotFoundException(id)")
}
```

## Best Practices

1. **Use DSL Extensions**: Prefer lambda-based construction over builder chains
2. **Leverage Type Safety**: Use `asClassName()` and type-safe references
3. **Documentation**: Always add KDoc for public APIs
4. **Modifiers**: Use appropriate Kotlin modifiers (`data`, `sealed`, `inline`, etc.)
5. **Null Safety**: Consider nullable types where appropriate
6. **Context Receivers**: Use context parameters for dependency injection patterns

## Platform-Specific Features

### Kotlin-Only Capabilities

- **Primary/Secondary Constructors**: Distinctive constructor syntax
- **Properties**: First-class property support with getters/setters  
- **Extension Functions**: Extend existing types
- **Object Declarations**: Singleton objects and companion objects
- **Value Classes**: Zero-cost wrappers
- **Context Receivers**: Dependency injection without parameters
- **Suspend Functions**: Coroutine support
- **Type Aliases**: Create alternative names for types

