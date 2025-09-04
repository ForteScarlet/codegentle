# 通用规范

`codegentle-common` 模块提供了所有其他 Spec 类扩展的基础接口和通用功能。这些基础接口定义了 Java 和 Kotlin 代码生成中的核心契约和共享行为。

## 基础接口

### Spec

所有代码规范的根接口。

```kotlin
public interface Spec
```

这是所有规范类实现的基础接口。它作为标记接口来识别代码生成规范，并为类型层次结构提供通用基础。

**用法：**
- 所有 XxxSpec 接口都从这个基础扩展
- 用于泛型约束和类型安全
- 没有直接构造方法（抽象接口）

### NamedSpec

为具有名称的规范扩展 `Spec` 和 `Named`。

```kotlin
public interface NamedSpec : Spec, Named
```

此接口将规范标记与命名功能结合，使其适用于任何具有可识别名称的代码元素。

**属性：**
- 从 `Named` 接口继承 `name: String`
- 提供标识和查找功能

**使用者：**
- 所有类型规范（类、接口、枚举）
- 方法和函数规范  
- 字段和属性规范
- 参数规范

**使用示例：**
```kotlin
// NamedSpec 通常由具体的规范接口扩展
interface MySpec : NamedSpec {
    // 附加属性和方法
}
```

## 通用收集器接口

通用模块提供了几个收集器接口，用于实现流畅的构建器 API。这些接口混入到规范构建器中以提供领域特定的功能。

### DocCollector<T>

提供文档/javadoc/kdoc 收集功能。

```kotlin
public interface DocCollector<T> {
    fun addDoc(format: String, vararg args: CodeArgumentPart): T
    fun addDoc(codeBlock: CodeValue): T
    // 附加文档方法
}
```

**方法：**
- `addDoc(format, ...args)` - 添加格式化文档
- `addDoc(codeBlock)` - 添加预构建的代码块文档
- 格式字符串支持参数占位符

**使用示例：**
```kotlin
// 在扩展 DocCollector 的规范构建器中
builder.addDoc("这是一个文档注释。")
builder.addDoc("带参数的方法 %T", someType.ref())
```

### InitializerBlockCollector<T>

为支持初始化块的类型收集初始化块。

```kotlin
public interface InitializerBlockCollector<T> {
    fun addInitializerBlock(format: String, vararg args: CodeArgumentPart): T
    fun addInitializerBlock(codeBlock: CodeValue): T
}
```

**方法：**
- `addInitializerBlock(format, ...args)` - 添加格式化初始化代码
- `addInitializerBlock(codeBlock)` - 添加预构建的初始化块

**使用示例：**
```kotlin
// 添加在实例创建时运行的初始化代码
builder.addInitializerBlock("println(\"对象已初始化\")")
builder.addInitializerBlock("this.field = defaultValue")
```

### TypeVariableCollector<T>

管理类型和方法的泛型类型参数。

```kotlin
public interface TypeVariableCollector<T> {
    fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): T
    fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): T
    fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): T
}
```

**方法：**
- `addTypeVariable(typeVariable)` - 添加单个类型参数
- `addTypeVariables(iterable)` - 从集合添加多个类型参数
- `addTypeVariables(vararg)` - 添加多个类型参数作为可变参数

**使用示例：**
```kotlin
// 添加泛型类型参数如 <T>、<T extends Something>
val tParam = TypeVariableName.get("T").ref()
val boundedParam = TypeVariableName.get("E", someInterface).ref()

builder.addTypeVariable(tParam)
builder.addTypeVariable(boundedParam)
```

### AnnotationRefCollector<T>

为带注解的元素收集注解引用。

```kotlin
public interface AnnotationRefCollector<T> {
    fun addAnnotation(annotation: AnnotationRef): T
    fun addAnnotations(annotations: Iterable<AnnotationRef>): T
    fun addAnnotations(vararg annotations: AnnotationRef): T
}
```

**方法：**
- `addAnnotation(annotation)` - 添加单个注解
- `addAnnotations(iterable)` - 从集合添加多个注解  
- `addAnnotations(vararg)` - 添加多个注解作为可变参数

**使用示例：**
```kotlin
// 添加注解如 @Override、@Deprecated 等
val overrideAnnotation = JavaAnnotationNames.OVERRIDE.ref()
val deprecatedAnnotation = JavaAnnotationNames.DEPRECATED.ref()

builder.addAnnotation(overrideAnnotation)
builder.addAnnotations(overrideAnnotation, deprecatedAnnotation)
```

## 配置接口

### SuperclassConfigurer<T>

为支持继承的类型配置超类关系。

```kotlin
public interface SuperclassConfigurer<T> {
    fun superclass(superclass: TypeName): T
    fun superclass(superclass: TypeRef<*>): T
}
```

**方法：**
- `superclass(TypeName)` - 通过类型名设置超类
- `superclass(TypeRef)` - 通过类型引用设置超类

**使用示例：**
```kotlin
// 为扩展另一个类的类设置超类
builder.superclass(ClassName.get("com.example", "BaseClass"))
builder.superclass(someClassRef)
```

### SuperinterfaceCollector<T>

为可以实现接口的类型收集接口实现。

```kotlin
public interface SuperinterfaceCollector<T> {
    fun addSuperinterface(superinterface: TypeName): T
    fun addSuperinterface(superinterface: TypeRef<*>): T
    fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): T
    // 附加重载
}
```

**方法：**
- `addSuperinterface(TypeName/TypeRef)` - 添加单个接口实现
- `addSuperinterfaces(Iterable)` - 添加多个接口实现
- 支持 `TypeName` 和 `TypeRef<*>` 参数

**使用示例：**
```kotlin
// 添加接口实现
builder.addSuperinterface(ClassName.get("java.io", "Serializable"))
builder.addSuperinterface(someInterfaceRef)

val interfaces = listOf(
    ClassName.get("java.lang", "Comparable"),
    ClassName.get("java.lang", "Cloneable")
)
builder.addSuperinterfaces(interfaces)
```

## 设计模式

### 收集器模式

收集器接口遵循一致的模式：

1. **单项方法**：`add[Item](item)` 用于添加单个元素
2. **多项方法**：`add[Items](iterable)` 和 `add[Items](vararg)` 用于批量操作
3. **流畅接口**：所有方法返回构建器类型 `T` 以支持方法链
4. **类型安全**：使用强类型参数和泛型约束

### 构建器集成

这些收集器通过接口继承混入到规范构建器中：

```kotlin
// 扩展多个收集器的示例规范构建器
public interface SomeSpecBuilder<T, B> : 
    DocCollector<B>,
    TypeVariableCollector<B>,
    AnnotationRefCollector<B> {
    
    fun build(): T
}
```

这种方法提供：
- **组合优于继承**：仅混合所需的功能
- **一致的 API**：所有收集器使用相同的方法模式
- **类型安全**：强类型构建器链
- **可扩展性**：易于添加新的收集器类型

## 跨模块使用

这些通用接口在 Java 和 Kotlin 模块中一致使用：

### 在 Java 规范中
```kotlin
// Java 构建器扩展通用收集器
public interface JavaMethodSpecBuilder : 
    DocCollector<JavaMethodSpecBuilder>,
    TypeVariableCollector<JavaMethodSpecBuilder>,
    JavaModifierCollector<JavaMethodSpecBuilder> // Java 特定
```

### 在 Kotlin 规范中  
```kotlin
// Kotlin 构建器扩展通用收集器
public interface KotlinFunctionSpecBuilder :
    DocCollector<KotlinFunctionSpecBuilder>, 
    TypeVariableCollector<KotlinFunctionSpecBuilder>,
    KotlinModifierCollector<KotlinFunctionSpecBuilder> // Kotlin 特定
```
