# Kotlin 命名实用工具

`codegentle-kotlin` 模块提供了 Kotlin 特定的命名实用工具和预定义常量，使 Kotlin 代码生成更加便捷和一致。

## KotlinClassNames

`KotlinClassNames` 是一个包含常见 Kotlin 类型预定义 `ClassName` 常量的实用工具对象。

### 基本类型

```kotlin
object KotlinClassNames {
    // 特殊的 Kotlin 类型
    val UNIT: ClassName           // kotlin.Unit
    val NOTHING: ClassName        // kotlin.Nothing  
    val ANY: ClassName            // kotlin.Any
    
    // 基本类型
    val BOOLEAN: ClassName        // kotlin.Boolean
    val BYTE: ClassName           // kotlin.Byte
    val SHORT: ClassName          // kotlin.Short
    val INT: ClassName            // kotlin.Int
    val LONG: ClassName           // kotlin.Long
    val FLOAT: ClassName          // kotlin.Float
    val DOUBLE: ClassName         // kotlin.Double
    val CHAR: ClassName           // kotlin.Char
    val STRING: ClassName         // kotlin.String
    
    // 集合类型（不可变）
    val LIST: ClassName           // kotlin.collections.List
    val SET: ClassName            // kotlin.collections.Set
    val MAP: ClassName            // kotlin.collections.Map
    val COLLECTION: ClassName     // kotlin.collections.Collection
    val ITERABLE: ClassName       // kotlin.collections.Iterable
    
    // 可变集合类型
    val MUTABLE_LIST: ClassName       // kotlin.collections.MutableList
    val MUTABLE_SET: ClassName        // kotlin.collections.MutableSet
    val MUTABLE_MAP: ClassName        // kotlin.collections.MutableMap
    val MUTABLE_COLLECTION: ClassName // kotlin.collections.MutableCollection
    
    // 数组类型
    val ARRAY: ClassName          // kotlin.Array
}
```

### 使用示例

```kotlin
// 使用预定义的 Kotlin 类型
val unitType = KotlinClassNames.UNIT
val stringType = KotlinClassNames.STRING
val intType = KotlinClassNames.INT

// 创建泛型类型
val listOfStrings = KotlinClassNames.LIST.parameterized(KotlinClassNames.STRING.ref())
val mapOfIntToString = KotlinClassNames.MAP.parameterized(
    KotlinClassNames.INT.ref(),
    KotlinClassNames.STRING.ref()
)

// 可变集合
val mutableList = KotlinClassNames.MUTABLE_LIST.parameterized(KotlinClassNames.STRING.ref())
val mutableSet = KotlinClassNames.MUTABLE_SET.parameterized(KotlinClassNames.INT.ref())

// 在函数签名中使用
val toStringMethod = KotlinFunctionSpec.builder("toString") {
    returns(KotlinClassNames.STRING)
    addCode("return \"Example\"")
}

// 检查规范名称
println(KotlinClassNames.UNIT.canonicalName)     // "kotlin.Unit"
println(KotlinClassNames.LIST.canonicalName)     // "kotlin.collections.List"
```

### Kotlin vs Java 类型

```kotlin
// Kotlin 类型映射到平台类型
val kotlinString = KotlinClassNames.STRING      // kotlin.String
val kotlinInt = KotlinClassNames.INT           // kotlin.Int
val kotlinBoolean = KotlinClassNames.BOOLEAN   // kotlin.Boolean

// 集合与 Java 不同
val kotlinList = KotlinClassNames.LIST         // kotlin.collections.List （不可变）
val javaList = JavaClassNames.LIST             // java.util.List （可变）

// 特殊的 Kotlin 类型
val unit = KotlinClassNames.UNIT               // 没有 Java 等价物
val nothing = KotlinClassNames.NOTHING         // 没有 Java 等价物  
val any = KotlinClassNames.ANY                 // 类似于 java.lang.Object
```

## KotlinLambdaTypeName

`KotlinLambdaTypeName` 表示 Kotlin 函数/lambda 类型，支持接收器、上下文接收器和挂起函数。

### 结构

```kotlin
interface KotlinLambdaTypeName : TypeName, KotlinModifierContainer {
    val receiver: TypeRef<*>?                    // 扩展接收器类型
    val contextReceivers: List<TypeRef<*>>       // 上下文接收器  
    val parameters: List<KotlinValueParameterSpec> // 函数参数
    val returnType: TypeRef<*>                   // 返回类型
    val isSuspend: Boolean                       // 挂起函数标志
}
```

### 构建器接口

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

### 构造

#### 基本 Lambda 类型

```kotlin
// 简单 lambda：() -> Unit
val simpleFunction = KotlinLambdaTypeName()

// 带参数的 lambda：(String, Int) -> String
val withParams = KotlinLambdaTypeName(KotlinClassNames.STRING.ref()) {
    addParameter(KotlinClassNames.STRING.ref())
    addParameter(KotlinClassNames.INT.ref())
}

// 带自定义返回类型的 lambda：(Int) -> Boolean
val predicate = KotlinLambdaTypeName(KotlinClassNames.BOOLEAN.ref()) {
    addParameter(KotlinClassNames.INT.ref())
}
```

#### 扩展函数

```kotlin
// 扩展函数：String.() -> Unit
val stringExtension = KotlinLambdaTypeName {
    receiver(KotlinClassNames.STRING.ref())
}

// 带参数的扩展：String.(Int) -> Boolean
val stringPredicate = KotlinLambdaTypeName(KotlinClassNames.BOOLEAN.ref()) {
    receiver(KotlinClassNames.STRING.ref())
    addParameter(KotlinClassNames.INT.ref())
}
```

#### 挂起函数

```kotlin
// 挂起函数：suspend () -> String
val suspendFunction = KotlinLambdaTypeName(KotlinClassNames.STRING.ref()) {
    suspend()
}

// 挂起扩展：suspend String.() -> Unit  
val suspendExtension = KotlinLambdaTypeName {
    receiver(KotlinClassNames.STRING.ref())
    suspend()
}
```

#### 上下文接收器

```kotlin
// 上下文接收器函数：context(Logger) () -> Unit
val contextLogger = ClassName("com.example", "Logger")
val withContext = KotlinLambdaTypeName {
    addContextReceiver(contextLogger.ref())
}

// 多个上下文接收器：context(Logger, Database) (String) -> User
val multiContext = KotlinLambdaTypeName(userType.ref()) {
    addContextReceiver(contextLogger.ref())
    addContextReceiver(databaseType.ref())
    addParameter(KotlinClassNames.STRING.ref())
}
```

### DSL 扩展

```kotlin
// 用于更清洁语法的扩展函数
fun receiver(receiver: TypeName, block: TypeRefBuilderDsl<TypeName> = {}): KotlinLambdaTypeName.Builder

fun addContextReceiver(contextReceiver: TypeName, block: TypeRefBuilderDsl<TypeName> = {}): KotlinLambdaTypeName.Builder

fun returns(type: TypeName, block: TypeRefBuilderDsl<TypeName> = {}): KotlinLambdaTypeName.Builder
```

### 使用示例

```kotlin
// DSL 风格构造
val dslLambda = KotlinLambdaTypeName(KotlinClassNames.STRING) {
    receiver(KotlinClassNames.STRING)
    addParameter(KotlinClassNames.INT) {
        name = "index" 
    }
    suspend(true)
}

// 复杂函数类型：context(CoroutineScope) suspend String.(Int) -> List<String>
val complexLambda = KotlinLambdaTypeName {
    addContextReceiver(ClassName("kotlinx.coroutines", "CoroutineScope"))
    receiver(KotlinClassNames.STRING)
    addParameter(KotlinClassNames.INT)
    returns(KotlinClassNames.LIST.parameterized(KotlinClassNames.STRING.ref()))
    suspend()
}

// 在属性声明中使用
val lambdaProperty = KotlinPropertySpec.builder("callback", complexLambda.ref()) {
    addModifier(KotlinModifier.PRIVATE)
}

// 在函数参数中使用
val higherOrderFunction = KotlinFunctionSpec.builder("processStrings") {
    addParameter("processor", dslLambda.ref())
    returns(KotlinClassNames.UNIT)
}
```

## KotlinAnnotationNames

`KotlinAnnotationNames` 为常见 Kotlin 注解提供预定义的 `ClassName` 常量。

**注意**：此部分需要检查实际的 KotlinAnnotationNames 实现以提供准确的文档。

### 常见的 Kotlin 注解

基于典型的 Kotlin 注解用法，这可能包括：

```kotlin
object KotlinAnnotationNames {
    // 可空性注解
    val Nullable: ClassName       // kotlin.annotations.Nullable （如果存在）
    val NonNull: ClassName        // kotlin.annotations.NonNull （如果存在）
    
    // JVM 互操作注解  
    val JvmStatic: ClassName      // kotlin.jvm.JvmStatic
    val JvmField: ClassName       // kotlin.jvm.JvmField
    val JvmName: ClassName        // kotlin.jvm.JvmName
    val JvmOverloads: ClassName   // kotlin.jvm.JvmOverloads
    val Throws: ClassName         // kotlin.jvm.Throws
    
    // 序列化注解（如果 kotlinx.serialization 支持存在）
    val Serializable: ClassName   // kotlinx.serialization.Serializable
    val SerialName: ClassName     // kotlinx.serialization.SerialName
    
    // 实验性注解
    val OptIn: ClassName          // kotlin.OptIn
    val RequiresOptIn: ClassName  // kotlin.RequiresOptIn
}
```

## KotlinMemberName

`KotlinMemberName` 使用 Kotlin 特定功能扩展通用的 `MemberName` 功能。

**注意**：此部分需要检查实际的 KotlinMemberName 实现。

## KotlinDelegatedClassName

`KotlinDelegatedClassName` 表示 Kotlin 中的委托类名。

**注意**：此部分需要检查实际的 KotlinDelegatedClassName 实现。

### 潜在结构

```kotlin
interface KotlinDelegatedClassName : TypeName {
    val delegateType: TypeRef<*>      // 被委托的类型
    val interfaceType: TypeRef<*>     // 被实现的接口
}
```

## 与 Kotlin 代码生成的集成

### 与 KotlinFile 一起使用

```kotlin
val kotlinFile = KotlinFile("com.example") {
    addClass("MyClass") {
        // 使用 Kotlin 预定义类型
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
        
        // 挂起函数
        addFunction("fetchData") {
            addModifier(KotlinModifier.SUSPEND)
            returns(KotlinClassNames.STRING)
        }
    }
}
```

### 类型兼容性

```kotlin
// Kotlin 类型与通用命名系统配合
val kotlinString: ClassName = KotlinClassNames.STRING
val commonString: ClassName = ClassName("kotlin", "String")

// 跨模块内容相等性
val isEqual = kotlinString contentEquals commonString  // true

// 泛型类型兼容性
val kotlinList = KotlinClassNames.LIST.parameterized(KotlinClassNames.STRING.ref())
val javaList = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())
// 这些是具有不同语义的不同类型
```

## 最佳实践

### 1. 使用 Kotlin 特定类型

对于 Kotlin 代码生成，优先使用 Kotlin 类型而不是 Java 等价物：

```kotlin
// Kotlin 推荐
val kotlinString = KotlinClassNames.STRING
val kotlinList = KotlinClassNames.LIST
val kotlinInt = KotlinClassNames.INT

// 避免用于纯 Kotlin 代码（除非需要 Java 互操作）
val javaString = JavaClassNames.STRING
val javaList = JavaClassNames.LIST
val javaInt = JavaPrimitiveTypeNames.INT
```

### 2. 利用函数类型

使用 `KotlinLambdaTypeName` 进行适当的 Kotlin 函数类型表示：

```kotlin
// 正确的 Kotlin 函数类型
val kotlinFunction = KotlinLambdaTypeName(KotlinClassNames.STRING.ref()) {
    addParameter(KotlinClassNames.INT.ref())
}

// 不太地道（Java 风格）  
val javaFunction = ClassName("kotlin.jvm.functions", "Function1")
    .parameterized(KotlinClassNames.INT.ref(), KotlinClassNames.STRING.ref())
```

### 3. 处理可空性

在类型使用中考虑可空性：

```kotlin
// Kotlin 中的可空类型
val nullableString = KotlinClassNames.STRING.nullable()  // String?

// 非空类型（默认）
val nonNullString = KotlinClassNames.STRING             // String
```

### 4. 使用适当的集合类型

在可变和不可变集合之间选择：

```kotlin
// 不可变集合（数据首选）
val readOnlyList = KotlinClassNames.LIST.parameterized(KotlinClassNames.STRING.ref())

// 可变集合（需要变化时）
val mutableList = KotlinClassNames.MUTABLE_LIST.parameterized(KotlinClassNames.STRING.ref())
```

## 常见模式

### 使用 Kotlin 类型的数据类

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

### 扩展函数

```kotlin
val extensionFunction = KotlinFunctionSpec.builder("isNotEmpty") {
    receiver(KotlinClassNames.STRING)
    returns(KotlinClassNames.BOOLEAN)
    addCode("return this.length > 0")
}
```

### 高阶函数

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

### 挂起函数

```kotlin
val suspendFunction = KotlinFunctionSpec.builder("fetchUserData") {
    addModifier(KotlinModifier.SUSPEND)
    addParameter("userId", KotlinClassNames.STRING)
    returns(ClassName("com.example", "User"))
    
    addCode("""
        // 模拟异步操作
        delay(100)
        return User(userId)
    """.trimIndent())
}
```

## 平台互操作性

### JVM 目标考虑

```kotlin
// 当目标为 JVM 时，可能需要一些互操作
val jvmInteropFunction = KotlinFunctionSpec.builder("javaCompatible") {
    addAnnotation(KotlinAnnotationNames.JvmName) {
        addMember("name", "\"javaFriendlyName\"")
    }
    addAnnotation(KotlinAnnotationNames.JvmStatic)
}
```

### 多平台考虑

```kotlin
// 在所有 Kotlin 平台上工作的类型
val multiplatformTypes = listOf(
    KotlinClassNames.STRING,
    KotlinClassNames.INT,
    KotlinClassNames.BOOLEAN,
    KotlinClassNames.LIST,
    KotlinClassNames.MAP
)

// 在通用代码中应避免平台特定类型
```
