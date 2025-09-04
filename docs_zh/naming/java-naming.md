# Java 命名实用工具

`codegentle-java` 模块提供了 Java 特定的命名实用工具和预定义常量，使 Java 代码生成更加便捷和一致。

## JavaClassNames

`JavaClassNames` 是一个包含常见 Java 类型预定义 `ClassName` 常量的实用工具对象。

### 基本类型

```kotlin
object JavaClassNames {
    // 基础类型
    val OBJECT: ClassName        // java.lang.Object
    val STRING: ClassName        // java.lang.String
    
    // 装箱基本类型
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

### 使用示例

```kotlin
// 使用预定义类名
val objectType = JavaClassNames.OBJECT
val stringType = JavaClassNames.STRING
val integerType = JavaClassNames.BOXED_INT

// 使用预定义类创建泛型类型
val listOfStrings = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())
val mapOfStringToInt = JavaClassNames.MAP.parameterized(
    JavaClassNames.STRING.ref(),
    JavaClassNames.BOXED_INT.ref()
)

// 在方法签名中使用
val valueOf = MemberName(JavaClassNames.STRING, "valueOf")
val parseInt = MemberName(JavaClassNames.BOXED_INT, "parseInt")

// 检查规范名称
println(JavaClassNames.BOXED_INT.canonicalName)  // "java.lang.Integer"
println(JavaClassNames.BOXED_CHAR.canonicalName) // "java.lang.Character"
```

## JavaPrimitiveTypeName

`JavaPrimitiveTypeName` 表示具有关键字和装箱功能的 Java 基本类型。

### 接口

```kotlin
interface JavaPrimitiveTypeName : TypeName {
    val keyword: String        // 基本类型关键字（例如 "int", "boolean"）
    fun box(): TypeName       // 返回对应的装箱类型
}
```

### 基本类型关键字

该接口包含所有 Java 基本类型关键字的内部常量：

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

`JavaPrimitiveTypeNames` 提供基本类型的预定义实例。

### 可用的基本类型

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

### 使用示例

```kotlin
// 在方法签名中使用基本类型
val primitiveInt = JavaPrimitiveTypeNames.INT
val primitiveBool = JavaPrimitiveTypeNames.BOOLEAN

// 创建基本类型数组
val intArray = ArrayTypeName(JavaPrimitiveTypeNames.INT.ref())
val booleanArray = ArrayTypeName(JavaPrimitiveTypeNames.BOOLEAN.ref())

// 装箱操作（如果实现支持）
if (primitiveInt is JavaPrimitiveTypeName) {
    val boxedInt = primitiveInt.box()  // java.lang.Integer
    println("装箱: ${boxedInt}")
}

// 在参数化类型中使用（使用装箱）
val listOfInts = JavaClassNames.LIST.parameterized(JavaClassNames.BOXED_INT.ref())

// 检查基本类型属性
if (primitiveBool is JavaPrimitiveTypeName) {
    println(primitiveBool.keyword)  // "boolean"
}
```

### 基本类型 vs 装箱类型

```kotlin
// 基本类型用于方法参数、局部变量
val method = JavaMethodSpec.builder("calculate") {
    addParameter("value", JavaPrimitiveTypeNames.INT)
    addParameter("flag", JavaPrimitiveTypeNames.BOOLEAN)
    returns(JavaPrimitiveTypeNames.DOUBLE)
}

// 装箱类型用于泛型和可空上下文
val optionalInt = ClassName("java.util", "Optional")
    .parameterized(JavaClassNames.BOXED_INT.ref())

val nullableInteger = JavaClassNames.BOXED_INT  // 可以为 null
```

## JavaAnnotationNames

`JavaAnnotationNames` 为常见 Java 注解提供预定义的 `ClassName` 常量。

### 标准注解

```kotlin
object JavaAnnotationNames {
    // 核心注解
    val Override: ClassName          // java.lang.Override
    val Deprecated: ClassName        // java.lang.Deprecated
    val SuppressWarnings: ClassName  // java.lang.SuppressWarnings
    val SafeVarargs: ClassName      // java.lang.SafeVarargs
    
    // 元注解  
    val Documented: ClassName       // java.lang.annotation.Documented
    val Retention: ClassName        // java.lang.annotation.Retention
    val Target: ClassName           // java.lang.annotation.Target
    val Inherited: ClassName        // java.lang.annotation.Inherited
    val Repeatable: ClassName       // java.lang.annotation.Repeatable
    val Native: ClassName           // java.lang.annotation.Native (since Java 8)
    
    // 函数式编程
    val FunctionalInterface: ClassName  // java.lang.FunctionalInterface (since Java 8)
    
    // 代码生成
    val Generated: ClassName        // javax.annotation.processing.Generated (since Java 9)
}
```

### 使用示例

```kotlin
// 为方法添加注解
val method = JavaMethodSpec.builder("toString") {
    addAnnotation(JavaAnnotationNames.Override)
    returns(JavaClassNames.STRING)
}

// 带原因的弃用
val deprecatedMethod = JavaMethodSpec.builder("oldMethod") {
    addAnnotation(JavaAnnotationNames.Deprecated) {
        // 如需要可添加注解参数
    }
    addAnnotation(JavaAnnotationNames.SuppressWarnings) {
        addMember("value", "\"deprecation\"")
    }
}

// 函数式接口注解
val functionalInterface = JavaTypeSpec.interfaceBuilder("Processor") {
    addAnnotation(JavaAnnotationNames.FunctionalInterface)
    addMethod("process") {
        addParameter("input", JavaClassNames.STRING)
        returns(JavaClassNames.STRING)
    }
}

// 生成代码注解
val generatedClass = JavaTypeSpec.classBuilder("GeneratedClass") {
    addAnnotation(JavaAnnotationNames.Generated) {
        addMember("value", "\"CodeGentle\"")
        addMember("date", "\"2025-01-01\"")
    }
}

// 元注解用法
val customAnnotation = JavaTypeSpec.annotationBuilder("MyAnnotation") {
    addAnnotation(JavaAnnotationNames.Target) {
        // 指定目标元素
    }
    addAnnotation(JavaAnnotationNames.Retention) {
        // 指定保留策略  
    }
    addAnnotation(JavaAnnotationNames.Documented)
}
```

### 版本兼容性

一些注解是版本特定的：

```kotlin
// Java 8+ 注解
val functionalInterface = JavaAnnotationNames.FunctionalInterface
val nativeAnnotation = JavaAnnotationNames.Native

// Java 9+ 注解  
val generated = JavaAnnotationNames.Generated  // javax.annotation.processing.Generated
```

## JavaArrayTypeName

`JavaArrayTypeName` 为数组类型发射提供 Java 特定的扩展。

### 可变参数支持

Java 模块使用可变参数支持扩展了 `ArrayTypeName`：

```kotlin
// Java 特定数组发射的扩展函数
fun ArrayTypeName.emitTo(codeWriter: JavaCodeWriter, varargs: Boolean)
```

### 使用示例

```kotlin
// 标准数组语法
val stringArray = ArrayTypeName(JavaClassNames.STRING.ref())
// 发射为：String[]

// 可变参数语法  
val varargsMethod = JavaMethodSpec.builder("process") {
    addParameter("items", stringArray, varargs = true)
    // 参数发射为：String... items
}

// 多维数组
val matrix = ArrayTypeName(ArrayTypeName(JavaPrimitiveTypeNames.INT.ref()).ref())
// 发射为：int[][]

// 复杂数组类型
val genericArray = ArrayTypeName(
    JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref()).ref()
)
// 发射为：List<String>[]

// 泛型可变参数
val genericVarargs = JavaMethodSpec.builder("combine") {
    addParameter("lists", genericArray, varargs = true)
    // 发射为：List<String>... lists
}
```

### 数组创建模式

```kotlin
// 基本类型数组
val intArray = ArrayTypeName(JavaPrimitiveTypeNames.INT.ref())
val byteArray = ArrayTypeName(JavaPrimitiveTypeNames.BYTE.ref())
val charArray = ArrayTypeName(JavaPrimitiveTypeNames.CHAR.ref())

// 对象数组
val stringArray = ArrayTypeName(JavaClassNames.STRING.ref())
val objectArray = ArrayTypeName(JavaClassNames.OBJECT.ref())

// 泛型数组（由于类型擦除应谨慎使用）
val listArray = ArrayTypeName(
    JavaClassNames.LIST.parameterized(EmptyWildcardTypeName.ref()).ref()
)

// 多维数组
fun createMultiDimArray(elementType: TypeRef<*>, dimensions: Int): ArrayTypeName {
    var arrayType = ArrayTypeName(elementType)
    repeat(dimensions - 1) {
        arrayType = ArrayTypeName(arrayType.ref())
    }
    return arrayType
}

val threeDArray = createMultiDimArray(JavaPrimitiveTypeNames.INT.ref(), 3)
// 结果为：int[][][]
```

## 与代码生成的集成

### 与 JavaFile 一起使用

```kotlin
val javaFile = JavaFile("com.example") {
    addClass("MyClass") {
        // 使用预定义类型
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

### 类型兼容性

```kotlin
// Java 类型与通用类型无缝配合
val commonString: ClassName = JavaClassNames.STRING
val javaString: ClassName = JavaClassNames.STRING

// 内容相等性在模块间有效
val isEqual = commonString contentEquals javaString  // true

// 在泛型上下文中使用
val listOfJavaStrings = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())
val setOfJavaInts = JavaClassNames.SET.parameterized(JavaClassNames.BOXED_INT.ref())
```

## 最佳实践

### 1. 使用预定义常量

始终优先使用预定义常量而不是手动构造：

```kotlin
// 推荐
val stringType = JavaClassNames.STRING
val intType = JavaPrimitiveTypeNames.INT

// 避免
val stringType = ClassName("java.lang", "String")
val intType = TypeVariableName("int")  // 错误！这创建了一个类型变量
```

### 2. 基本类型 vs 装箱类型

根据上下文选择适当的类型：

```kotlin
// 对局部变量、参数、返回类型使用基本类型
val method = JavaMethodSpec.builder("calculate") {
    addParameter("value", JavaPrimitiveTypeNames.INT)        // 基本类型参数
    returns(JavaPrimitiveTypeNames.DOUBLE)                   // 基本类型返回值
}

// 对泛型和可空上下文使用装箱类型
val optionalValue = ClassName("java.util", "Optional")
    .parameterized(JavaClassNames.BOXED_INT.ref())           // 必须是装箱类型

val nullableInteger: ClassName = JavaClassNames.BOXED_INT   // 可以为 null
```

### 3. 数组 vs 集合类型

选择数组和集合时考虑使用场景：

```kotlin
// 对固定大小、性能关键场景使用数组
val buffer = ArrayTypeName(JavaPrimitiveTypeNames.BYTE.ref())

// 对动态、功能丰富场景使用集合  
val dynamicList = JavaClassNames.LIST.parameterized(JavaClassNames.STRING.ref())

// 对灵活方法参数使用可变参数
val logMethod = JavaMethodSpec.builder("log") {
    addParameter("messages", ArrayTypeName(JavaClassNames.STRING.ref()), varargs = true)
}
```

### 4. 注解用法

适当使用注解以提高代码清晰度和工具支持：

```kotlin
// 始终覆盖 toString、equals、hashCode
val toString = JavaMethodSpec.builder("toString") {
    addAnnotation(JavaAnnotationNames.Override)
    addModifier(JavaModifier.PUBLIC)
    returns(JavaClassNames.STRING)
}

// 标记弃用的方法  
val oldMethod = JavaMethodSpec.builder("oldMethod") {
    addAnnotation(JavaAnnotationNames.Deprecated)
    addAnnotation(JavaAnnotationNames.SuppressWarnings) {
        addMember("value", "\"deprecation\"")
    }
}

// 记录生成的代码
val generatedClass = JavaTypeSpec.classBuilder("Generated") {
    addAnnotation(JavaAnnotationNames.Generated) {
        addMember("value", "\"CodeGentle\"")
    }
}
```

## 常见模式

### 使用 Java 类型的构建器模式

```kotlin
val builderClass = JavaTypeSpec.classBuilder("PersonBuilder") {
    // 使用 Java 类型的字段
    addField(JavaClassNames.STRING, "name") { addModifier(JavaModifier.PRIVATE) }
    addField(JavaPrimitiveTypeNames.INT, "age") { addModifier(JavaModifier.PRIVATE) }
    
    // 流畅方法
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

### 泛型实用工具类

```kotlin
val utilityClass = JavaTypeSpec.classBuilder("Collections") {
    addModifier(JavaModifier.PUBLIC, JavaModifier.FINAL)
    
    // 带类型变量的泛型方法
    addMethod("emptyList") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC)
        addTypeVariable(TypeVariableName("T"))
        returns(JavaClassNames.LIST.parameterized(TypeVariableName("T").ref()))
        addCode("return new ArrayList<>();")
    }
}
```
