# 通用命名类

`codegentle-common` 模块为 CodeGentle 中的所有命名功能提供基础。这些类是平台无关的，可在所有支持的目标平台（JVM、JavaScript、Native 等）上工作。

## TypeName

`TypeName` 是命名系统中所有类型表示的基础接口。它作为所有类型相关命名类的共同祖先。

```kotlin
interface TypeName
```

### 子类

- `ClassName` - 完全限定的类名
- `ArrayTypeName` - 数组类型
- `ParameterizedTypeName` - 泛型/参数化类型
- `TypeVariableName` - 类型变量（例如 `T`、`E`）
- `WildcardTypeName` - 通配符类型（例如 `? extends String`）

## ClassName

`ClassName` 表示顶级类和嵌套类的完全限定类名。

### 属性

```kotlin
interface ClassName : Named, TypeName, Comparable<ClassName> {
    val simpleName: String              // 简单类名（例如 Map.Entry 的 "Entry"）
    val name: String                   // 与 simpleName 相同
    val packageName: PackageName       // 包含此类的包
    val enclosingClassName: ClassName? // 嵌套类的封闭类
    val topLevelClassName: ClassName   // 嵌套层次结构中的最顶层类
}
```

### 核心方法

```kotlin
// 创建同级类（相同包/封闭类）
fun peerClass(name: String): ClassName

// 创建嵌套类
fun nestedClass(name: String): ClassName

// 比较
fun compareTo(other: ClassName): Int
```

### 构造

#### 基本构造

```kotlin
// 从包名和类名构造
val mapEntry = ClassName("java.util", "Map", "Entry")
val string = ClassName("java.lang", "String")

// 使用 PackageName
val javaLang = PackageName("java", "lang") 
val string = ClassName(javaLang, "String")

// 从完全限定字符串（最佳猜测解析）
val arrayList = ClassName("java.util.ArrayList")
```

#### DSL 扩展

```kotlin
// 使用 PackageName 扩展
val javaUtil = PackageName("java", "util")
val list = javaUtil.className("List")
val arrayList = javaUtil.className("ArrayList")
```

### 实用工具属性和函数

#### 规范名称

```kotlin
val canonicalName: String  // "java.util.Map.Entry"
fun appendCanonicalNameTo(appendable: Appendable): Appendable
```

#### 反射名称（二进制名称）

```kotlin
val reflectionName: String  // "java.util.Map$Entry"
fun appendReflectionNameTo(appendable: Appendable): Appendable
```

#### 简单名称

```kotlin
val simpleNames: List<String>  // Map.Entry 的 ["Map", "Entry"]
```

#### 内容比较

```kotlin
fun contentHashCode(): Int
infix fun contentEquals(other: ClassName): Boolean
```

### 示例

```kotlin
// 为常见的 Java 类型创建类名
val string = ClassName("java.lang", "String")
val mapEntry = ClassName("java.util", "Map", "Entry")

// 导航类层次结构
val map = mapEntry.enclosingClassName     // java.util.Map
val topLevel = mapEntry.topLevelClassName // java.util.Map

// 创建相关类
val hashMap = map.peerClass("HashMap")           // java.util.HashMap
val entrySet = map.nestedClass("EntrySet")       // java.util.Map.EntrySet

// 获取不同的名称表示
println(mapEntry.canonicalName)   // "java.util.Map.Entry"
println(mapEntry.reflectionName)  // "java.util.Map$Entry"
println(mapEntry.simpleNames)     // [Map, Entry]
```

## PackageName

`PackageName` 使用链式结构表示包名，其中每个包组件都引用其父包。

### 结构

```kotlin
interface PackageName : Named {
    val previous: PackageName?  // 父包（空包为 null）
    val name: String           // 当前组件名称
    
    companion object {
        val EMPTY: PackageName     // 表示空/默认包
    }
}
```

### 构造

#### 基本构造

```kotlin
// 空包
val empty = PackageName()

// 单个组件
val java = PackageName("java")

// 多个组件
val javaUtil = PackageName("java", "util")
val javaUtilConcurrent = PackageName("java", "util", "concurrent")

// 从现有包
val util = PackageName(java, "util")

// 从列表
val path = listOf("java", "util", "concurrent")
val pkg = PackageName(path)
```

#### 从字符串解析

```kotlin
// 解析点分隔字符串
val javaLang = "java.lang".parseToPackageName()
val comExample = "com.example.project".parseToPackageName()

// 严格 vs 非严格解析
val strict = PackageName("java", strict = true)    // 验证无点
val lenient = PackageName("java.lang", strict = false) // 单个组件包含点
```

### 操作

#### 连接

```kotlin
val java = PackageName("java")
val util = PackageName("util")

// 添加单个组件
val javaUtil = java + "util"           // java.util

// 添加包
val javaUtilConcurrent = javaUtil + PackageName("concurrent") // java.util.concurrent
```

#### 层次结构导航

```kotlin
val javaUtilConcurrent = "java.util.concurrent".parseToPackageName()

// 获取根包
val root = javaUtilConcurrent.top()  // PackageName("java")

// 获取所有组件的序列
val sequence = javaUtilConcurrent.nameSequence()  // [java, java.util, java.util.concurrent]

// 获取所有组件的列表  
val names = javaUtilConcurrent.names()  // 与上面相同但作为 List
```

#### 属性和实用工具

```kotlin
val pkg = "com.example.project".parseToPackageName()

// 检查是否为空
pkg.isEmpty()     // false
pkg.isNotEmpty()  // true

// 获取部分
pkg.parts         // ["com", "example", "project"]
pkg.partSequence  // Sequence<String>

// 条件执行
pkg.ifNotEmpty { p -> println("包: $p") }
pkg.ifEmpty { p -> println("空包") }
```

#### 字符串操作

```kotlin
val pkg = "java.util.concurrent".parseToPackageName()

// 默认字符串表示（使用点）
pkg.toString()  // "java.util.concurrent"

// appendTo 的自定义分隔符
val sb = StringBuilder()
pkg.appendTo(sb, separator = "/")  // "java/util/concurrent"

// 转换为相对路径
pkg.toRelativePath()        // "java/util/concurrent" 
pkg.toRelativePath("\\")    // "java\util\concurrent"
```

### 示例

```kotlin
// 创建包层次结构
val empty = PackageName()
val com = PackageName("com")
val comExample = com + "example"
val project = comExample + "project"

// 从字符串解析
val javaLang = "java.lang".parseToPackageName()

// 导航层次结构
val util = "java.util".parseToPackageName()
val root = util.top()  // PackageName("java")

// 迭代组件
util.nameSequence().forEach { pkg ->
    println("包组件: ${pkg.name}")
}
// 输出: 
// 包组件: java
// 包组件: util

// 文件系统路径
val sourcePath = javaLang.toRelativePath("/")  // "java/lang"
```

## MemberName

`MemberName` 表示类成员，如静态方法、静态字段、枚举元素和可以导入和引用的嵌套类。

### 属性

```kotlin
interface MemberName : TypeName, Named, Comparable<MemberName> {
    val name: String                   // 成员名称
    val packageName: PackageName       // 包含成员的包
    val enclosingClassName: ClassName? // 包含成员的类（如果有）
}
```

### 构造

```kotlin
// 包级成员（例如 Kotlin 顶级函数）
val topLevelFunction = MemberName("com.example", "utility")

// 类成员（例如静态方法/字段）
val valueOf = MemberName("java.lang.String", "valueOf")

// 使用 ClassName
val stringClass = ClassName("java.lang", "String")
val valueOf = MemberName(stringClass, "valueOf")

// 使用 PackageName
val javaLang = "java.lang".parseToPackageName()
val member = MemberName(javaLang, "member")

// 完整规范
val packageName = "com.example".parseToPackageName()
val className = ClassName("com.example", "Utils")
val helper = MemberName(packageName, className, "helper")
```

### 实用工具函数

```kotlin
// 规范名称
val canonicalName: String  // "java.lang.String.valueOf"
fun appendCanonicalNameTo(appendable: Appendable): Appendable

// 内容比较
fun contentHashCode(): Int
infix fun contentEquals(other: MemberName): Boolean
```

### 示例

```kotlin
// 静态方法引用
val integerValueOf = MemberName("java.lang.Integer", "valueOf")
println(integerValueOf.canonicalName)  // "java.lang.Integer.valueOf"

// 枚举常量
val timeUnit = ClassName("java.util.concurrent", "TimeUnit")
val seconds = MemberName(timeUnit, "SECONDS")

// 顶级 Kotlin 函数
val topLevel = MemberName("com.example.utils", "calculateHash")

// 检查成员属性
println(seconds.packageName)        // java.util.concurrent
println(seconds.enclosingClassName) // java.util.concurrent.TimeUnit
println(seconds.name)               // SECONDS
```

## 最佳实践

### 1. 使用工厂函数

优先使用工厂函数而不是直接构造函数调用：

```kotlin
// 推荐
val className = ClassName("java.lang", "String")
val packageName = "java.lang".parseToPackageName()

// 避免直接实现构造函数
```

### 2. 利用扩展函数

为常见操作使用扩展函数：

```kotlin
// 包扩展
val myPackage = "com.example".parseToPackageName()
val myClass = myPackage.className("MyClass")

// 字符串解析
val parsed = "java.util.ArrayList".parseToPackageName()
```

### 3. 内容相等性

使用 `contentEquals` 进行语义相等性比较而不是引用相等性：

```kotlin
val class1 = ClassName("java.lang", "String")
val class2 = ClassName("java.lang.String")  // 最佳猜测构造函数

// 引用相等性 - false
class1 == class2

// 内容相等性 - true  
class1 contentEquals class2
```

### 4. 层次结构导航

利用层次结构导航方法：

```kotlin
val innerClass = ClassName("com.example", "Outer", "Inner", "DeepInner")

// 向上导航层次结构
val outer = innerClass.topLevelClassName
val direct = innerClass.enclosingClassName

// 创建同级类
val sibling = innerClass.peerClass("Sibling")
val child = innerClass.nestedClass("Child")
```
