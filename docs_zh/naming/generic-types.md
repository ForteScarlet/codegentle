# 泛型类型名称

CodeGentle 命名系统为表示泛型和复杂类型构造提供全面支持。本文档涵盖用于数组、参数化类型、类型变量和通配符的专用 `TypeName` 实现。

## ArrayTypeName

`ArrayTypeName` 表示具有组件类型的数组类型。

### 结构

```kotlin
interface ArrayTypeName : TypeName {
    val componentType: TypeRef<*>
}
```

### 构造

```kotlin
// 从组件类型创建数组类型
val stringArrayType = ArrayTypeName(String::class.ref())
val intArrayType = ArrayTypeName(JavaPrimitiveTypeNames.INT.ref())

// 多维数组
val stringArrayArray = ArrayTypeName(stringArrayType.ref())
```

### 实用工具函数

```kotlin
// 内容比较
fun contentHashCode(): Int
infix fun contentEquals(other: ArrayTypeName): Boolean
```

### 示例

```kotlin
// 基本数组类型
val byteArray = ArrayTypeName(JavaPrimitiveTypeNames.BYTE.ref())
val stringArray = ArrayTypeName(JavaClassNames.STRING.ref())

// 多维数组
val intMatrix = ArrayTypeName(ArrayTypeName(JavaPrimitiveTypeNames.INT.ref()).ref())

// 泛型组件数组
val listArray = ArrayTypeName(
    JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref()).ref()
)

// 检查数组属性
println(stringArray.componentType.typeName)  // java.lang.String
```

## ParameterizedTypeName

`ParameterizedTypeName` 表示带有类型参数的泛型类型（例如 `List<String>`、`Map<K, V>`）。

### 结构

```kotlin
interface ParameterizedTypeName : TypeName, Named {
    val enclosingType: ParameterizedTypeName?  // 用于嵌套泛型类型
    val rawType: ClassName                     // 不带泛型的基类
    val typeArguments: List<TypeRef<*>>        // 泛型类型参数
    val name: String                          // 与 rawType.name 相同
}
```

### 核心方法

```kotlin
// 创建嵌套泛型类
fun nestedClass(name: String): ParameterizedTypeName
fun nestedClass(name: String, typeArguments: List<TypeRef<*>>): ParameterizedTypeName
fun nestedClass(name: String, vararg typeArguments: TypeRef<*>): ParameterizedTypeName
```

### 构造

#### 基本构造

```kotlin
// 从原始类型和参数创建参数化类型
val listOfString = ParameterizedTypeName(
    JavaClassNames.LIST,
    JavaClassNames.STRING.ref()
)

val mapOfStringToInt = ParameterizedTypeName(
    JavaClassNames.MAP,
    JavaClassNames.STRING.ref(),
    JavaPrimitiveTypeNames.INT.ref()
)

// 从可迭代类型参数
val typeArgs = listOf(JavaClassNames.STRING.ref(), JavaClassNames.INTEGER.ref())
val mapType = ParameterizedTypeName(JavaClassNames.MAP, typeArgs)
```

#### DSL 扩展

```kotlin
// 使用 ClassName 扩展方法
val listOfString = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())
val setOfInt = JavaClassNames.SET.parameterized(JavaPrimitiveTypeNames.INT.ref())

// 多个类型参数
val mapOfStringToList = JavaClassNames.MAP.parameterized(
    JavaClassNames.STRING.ref(),
    JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref()).ref()
)
```

### 嵌套泛型类

```kotlin
// 创建嵌套泛型类型
val outerGeneric = JavaClassNames.OUTER.parameterized(JavaClassNames.STRING.ref())
val innerGeneric = outerGeneric.nestedClass("Inner", JavaPrimitiveTypeNames.INT.ref())

// 复杂嵌套
val complexType = outerGeneric
    .nestedClass("Middle", JavaClassNames.STRING.ref())
    .nestedClass("Deep", JavaPrimitiveTypeNames.LONG.ref())
```

### 示例

```kotlin
// 常见泛型类型
val arrayList = ClassName("java.util", "ArrayList")
    .parameterized(JavaClassNames.STRING.ref())

val hashMap = ClassName("java.util", "HashMap").parameterized(
    JavaClassNames.STRING.ref(),
    JavaPrimitiveTypeNames.INT.ref()
)

// 嵌套泛型
val listOfLists = JavaClassNames.LIST.parameterized(
    JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref()).ref()
)

// 复杂泛型类型
val functionType = ClassName("java.util.function", "Function").parameterized(
    JavaClassNames.STRING.ref(),
    JavaPrimitiveTypeNames.INT.ref()
)

// 检查属性
println(listOfString.rawType)                    // java.util.List
println(listOfString.typeArguments.first())      // java.lang.String
println(listOfString.name)                       // List
```

## TypeVariableName

`TypeVariableName` 表示在泛型声明中使用的类型变量（例如 `T`、`E`、`K`、`V`）。

### 结构

```kotlin
interface TypeVariableName : TypeName, Named {
    val name: String                    // 变量名（例如 "T", "E"）
    val bounds: List<TypeRef<*>>       // 上界约束
}
```

### 构造

```kotlin
// 无界限的简单类型变量
val t = TypeVariableName("T")
val e = TypeVariableName("E")

// 具有单个界限的类型变量
val bounded = TypeVariableName("T", JavaClassNames.STRING.ref())

// 具有多个界限的类型变量
val multiBounded = TypeVariableName("T", 
    JavaClassNames.STRING.ref(),
    ClassName("java.io", "Serializable").ref()
)

// 从可迭代界限
val bounds = listOf(JavaClassNames.STRING.ref(), JavaClassNames.COMPARABLE.ref())
val complexBounded = TypeVariableName("T", bounds)
```

### 示例

```kotlin
// 常见类型变量
val genericT = TypeVariableName("T")
val elementE = TypeVariableName("E")
val keyK = TypeVariableName("K")
val valueV = TypeVariableName("V")

// 有界类型变量
val numberBound = TypeVariableName("N", JavaClassNames.NUMBER.ref())
val comparableBound = TypeVariableName("T", JavaClassNames.COMPARABLE.parameterized(
    TypeVariableName("T").ref()
).ref())

// 多个约束
val serializable = ClassName("java.io", "Serializable")
val cloneable = ClassName("java.lang", "Cloneable")
val multiConstrained = TypeVariableName("T", serializable.ref(), cloneable.ref())

// 检查属性
println(numberBound.name)                        // N
println(numberBound.bounds.first().typeName)     // java.lang.Number
println(multiConstrained.bounds.size)            // 2
```

## WildcardTypeName

`WildcardTypeName` 表示在 Java 泛型中使用的通配符类型（例如 `?`、`? extends String`、`? super Integer`）。

### 层次结构

```kotlin
sealed interface WildcardTypeName : TypeName {
    val bounds: List<TypeRef<*>>
    val isEmpty: Boolean  // 如果没有界限则为 true
}

// 无界限：Java 中的 `?`，Kotlin 中的 `*`
data object EmptyWildcardTypeName : WildcardTypeName

// 上界：Java 中的 `? extends T`，Kotlin 中的 `out T`  
interface UpperWildcardTypeName : WildcardTypeName

// 下界：Java 中的 `? super T`，Kotlin 中的 `in T`
interface LowerWildcardTypeName : WildcardTypeName
```

### 构造

#### 空通配符

```kotlin
// 无界通配符
val unbounded = WildcardTypeName()  // 或 EmptyWildcardTypeName
```

#### 上界通配符

```kotlin
// 单个上界：? extends String
val extendsString = LowerWildcardTypeName(JavaClassNames.STRING.ref())

// 多个上界：? extends T1 & T2
val multipleUpper = LowerWildcardTypeName(listOf(
    JavaClassNames.STRING.ref(),
    ClassName("java.io", "Serializable").ref()
))
```

#### 下界通配符

```kotlin
// 单个下界：? super Integer
val superInteger = UpperWildcardTypeName(JavaClassNames.INTEGER.ref())

// 多个下界：? super T1 & T2
val multipleLower = UpperWildcardTypeName(listOf(
    JavaClassNames.NUMBER.ref(),
    ClassName("java.io", "Serializable").ref()
))
```

### 转换

```kotlin
val wildcard = WildcardTypeName()

// 转换为特定界限类型
val asUpper = wildcard.toUpper(listOf(JavaClassNames.STRING.ref()))
val asLower = wildcard.toLower(listOf(JavaClassNames.INTEGER.ref()))

// 转换现有界限
val existing = LowerWildcardTypeName(JavaClassNames.STRING.ref())
val converted = existing.toUpper()  // 使用现有界限
```

### 示例

```kotlin
// List<?> 的无界通配符
val listOfAnything = JavaClassNames.LIST.parameterized(EmptyWildcardTypeName.ref())

// List<? extends Number> 的上界
val extendsNumber = LowerWildcardTypeName(JavaClassNames.NUMBER.ref())
val listOfNumbers = JavaClassNames.LIST.parameterized(extendsNumber.ref())

// List<? super Integer> 的下界
val superInteger = UpperWildcardTypeName(JavaClassNames.INTEGER.ref())
val listSuperInteger = JavaClassNames.LIST.parameterized(superInteger.ref())

// 复杂通配符用法
val comparator = ClassName("java.util", "Comparator")
val wildComparator = comparator.parameterized(
    LowerWildcardTypeName(JavaClassNames.STRING.ref()).ref()
)

// 检查通配符属性
println(extendsNumber.bounds.first())           // java.lang.Number
println(EmptyWildcardTypeName.isEmpty)          // true
println(EmptyWildcardTypeName.toString())       // "*"
```

## 类型引用集成

所有泛型类型名称都与 `TypeRef` 系统无缝配合：

```kotlin
// 创建类型引用
val stringRef = JavaClassNames.STRING.ref()
val listRef = JavaClassNames.LIST.parameterized(stringRef).ref()
val arrayRef = ArrayTypeName(stringRef).ref()

// 在参数化类型中使用
val mapOfListToArray = JavaClassNames.MAP.parameterized(listRef, arrayRef)

// 复杂类型层次结构
val nestedGeneric = JavaClassNames.LIST.parameterized(
    JavaClassNames.MAP.parameterized(
        JavaClassNames.STRING.ref(),
        ArrayTypeName(JavaPrimitiveTypeNames.INT.ref()).ref()
    ).ref()
).ref()
```

## 最佳实践

### 1. 使用 DSL 扩展

优先使用 DSL 方法而不是直接构造函数调用：

```kotlin
// 推荐
val listOfString = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())

// 避免
val listOfString = ParameterizedTypeName(JavaClassNames.LIST, JavaClassNames.STRING.ref())
```

### 2. 利用类型引用

在将类型作为参数传递时始终使用 `.ref()`：

```kotlin
// 正确
val parameterized = rawType.parameterized(argumentType.ref())

// 错误
val parameterized = rawType.parameterized(argumentType)  // 编译错误
```

### 3. 通配符命名约定

遵循 Java 通配符命名约定：

```kotlin
// 上界（协变）：? extends T
val producer = LowerWildcardTypeName(elementType.ref())

// 下界（逆变）：? super T  
val consumer = UpperWildcardTypeName(elementType.ref())
```

### 4. 多维数组

逐步构建多维数组：

```kotlin
// 2D 数组：String[][]
val stringArray = ArrayTypeName(JavaClassNames.STRING.ref())
val string2DArray = ArrayTypeName(stringArray.ref())

// 3D 数组：String[][][]
val string3DArray = ArrayTypeName(string2DArray.ref())
```

## 常见模式

### 泛型集合

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

### 函数类型

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

### 有界泛型

```kotlin
// Class<? extends Enum<?>>
val enumClass = JavaClassNames.CLASS.parameterized(
    LowerWildcardTypeName(
        ClassName("java.lang", "Enum").parameterized(EmptyWildcardTypeName.ref()).ref()
    ).ref()
)
```
