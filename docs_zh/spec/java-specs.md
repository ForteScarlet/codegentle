# Java 规范

`codegentle-java` 模块为生成 Java 源代码提供全面的规范。本文档涵盖了所有 Java 特定的 Spec 类、它们的构造方法、属性和独特的 Java 语言功能。

## 目录

- [基础 Java 接口](#基础-java-接口)
- [类型规范](#类型规范)
- [成员规范](#成员规范)
- [参数规范](#参数规范)
- [Java 特定收集器](#java-特定收集器)
- [构造模式](#构造模式)
- [示例](#示例)

## 基础 Java 接口

### JavaSpec

所有 Java 规范的基础接口。

```kotlin
public interface JavaSpec : Spec {
    fun emit(codeWriter: JavaCodeWriter)
}
```

**功能：**
- 扩展通用 `Spec` 接口
- 提供向 `JavaCodeWriter` 发射的功能
- 所有 Java 特定规范类型的基础

## 类型规范

### JavaTypeSpec

所有 Java 类型声明（类、接口、枚举等）的密封接口。

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

**属性：**
- `name` - 类型名称（匿名类型可为 null）
- `kind` - 类型种类（CLASS、INTERFACE、ENUM 等）
- `javadoc` - 文档注释
- `annotations` - 类型注解
- `modifiers` - 访问修饰符和关键字
- `typeVariables` - 泛型类型参数
- `superclass` - 扩展的类（如果适用）
- `superinterfaces` - 实现的接口
- `fields` - 字段声明
- `staticBlock` - 静态初始化块
- `initializerBlock` - 实例初始化块
- `methods` - 方法声明
- `subtypes` - 嵌套类型

#### JavaTypeSpec.Kind

具有特定行为的 Java 类型种类枚举：

```kotlin
enum class Kind {
    CLASS,                    // 标准类
    INTERFACE,               // 接口
    ENUM,                    // 枚举类型
    ANNOTATION,              // 注解类型
    RECORD,                  // 记录类型（Java 14+）
    SEALED_CLASS,            // 密封类（Java 17+）
    NON_SEALED_CLASS,        // 非密封类
    SEALED_INTERFACE,        // 密封接口
    NON_SEALED_INTERFACE     // 非密封接口
}
```

**种类特定功能：**
- **INTERFACE**：字段隐式 `public static final`，方法隐式 `public abstract`
- **ENUM**：支持枚举常量，仅实现接口
- **ANNOTATION**：支持方法默认值
- **RECORD**：带自动访问器的不可变数据载体
- **SEALED_***：受限制的继承层次结构

### JavaSimpleTypeSpec

简单类型（类和接口）的规范。

```kotlin
public interface JavaSimpleTypeSpec : NamedSpec, JavaTypeSpec {
    override val name: String
}
```

**构造：**
```kotlin
// DSL 构造（推荐）
val classSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MyClass") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("一个简单的示例类。")
    
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

// 构建器构造
val classSpec = JavaSimpleTypeSpec.builder(JavaTypeSpec.Kind.CLASS, "MyClass")
    .addModifier(JavaModifier.PUBLIC)
    .addDoc("一个简单的示例类。")
    .build()
```

**生成的输出：**
```java
/**
 * 一个简单的示例类。
 */
public class MyClass {
    private String field = "default";
    
    public String getField() {
        return this.field;
    }
}
```

### JavaEnumTypeSpec

Java 枚举类型的规范。

```kotlin
public interface JavaEnumTypeSpec : NamedSpec, JavaTypeSpec {
    val enumConstants: List<JavaFieldSpec>
}
```

**构造：**
```kotlin
val enumSpec = JavaEnumTypeSpec("Color") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("RGB 颜色枚举。")
    
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

**生成的输出：**
```java
/**
 * RGB 颜色枚举。
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

Java 记录类型的规范（Java 14+）。

```kotlin
public interface JavaRecordTypeSpec : NamedSpec, JavaTypeSpec {
    val recordComponents: List<JavaParameterSpec>
}
```

**构造：**
```kotlin
val recordSpec = JavaRecordTypeSpec("Person") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("人员记录。")
    
    addRecordComponent(JavaParameterSpec("name", JavaClassNames.STRING.ref()))
    addRecordComponent(JavaParameterSpec("age", JavaPrimitiveTypeNames.INT.ref()))
    
    addMethod(JavaMethodSpec("isAdult") {
        addModifier(JavaModifier.PUBLIC)
        returns(JavaPrimitiveTypeNames.BOOLEAN.ref())
        addCode("return age >= 18;")
    })
}
```

**生成的输出：**
```java
/**
 * 人员记录。
 */
public record Person(String name, int age) {
    public boolean isAdult() {
        return age >= 18;
    }
}
```

### JavaAnnotationTypeSpec

Java 注解类型的规范。

```kotlin
public interface JavaAnnotationTypeSpec : NamedSpec, JavaTypeSpec {
    // 注解特定属性
}
```

**构造：**
```kotlin
val annotationSpec = JavaAnnotationTypeSpec("MyAnnotation") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("自定义注解。")
    
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

**生成的输出：**
```java
/**
 * 自定义注解。
 */
public @interface MyAnnotation {
    String value() default "default";
    boolean required() default false;
}
```

## 成员规范

### JavaMethodSpec

Java 方法和构造函数的规范。

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

**构造：**
```kotlin
// 常规方法
val methodSpec = JavaMethodSpec("calculateSum") {
    addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC)
    addDoc("计算两个整数的和。")
    returns(JavaPrimitiveTypeNames.INT.ref())
    addParameter(JavaParameterSpec("a", JavaPrimitiveTypeNames.INT.ref()))
    addParameter(JavaParameterSpec("b", JavaPrimitiveTypeNames.INT.ref()))
    addCode("return a + b;")
}

// 构造函数
val constructorSpec = JavaMethodSpec {
    addModifier(JavaModifier.PUBLIC)
    addParameter(JavaParameterSpec("name", JavaClassNames.STRING.ref()))
    addCode("this.name = name;")
}

// 可变参数方法
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

// 带异常的方法
val methodWithExceptions = JavaMethodSpec("readFile") {
    addModifier(JavaModifier.PUBLIC)
    returns(JavaClassNames.STRING.ref())
    addParameter(JavaParameterSpec("path", JavaClassNames.STRING.ref()))
    addException(ClassName.get("java.io", "IOException").ref())
    addCode("// 文件读取代码")
}
```

**生成的输出：**
```java
/**
 * 计算两个整数的和。
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
    // 文件读取代码
}
```

### JavaFieldSpec

Java 字段的规范。

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

**构造：**
```kotlin
// 简单字段
val fieldSpec = JavaFieldSpec("name", JavaClassNames.STRING.ref()) {
    addModifier(JavaModifier.PRIVATE)
    addDoc("名称字段。")
}

// 带初始化器的字段
val initializedFieldSpec = JavaFieldSpec("count", JavaPrimitiveTypeNames.INT.ref()) {
    addModifier(JavaModifier.PRIVATE, JavaModifier.STATIC)
    initializer("0")
}

// 常量字段
val constantSpec = JavaFieldSpec("MAX_SIZE", JavaPrimitiveTypeNames.INT.ref()) {
    addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC, JavaModifier.FINAL)
    initializer("1000")
}

// 泛型字段
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

**生成的输出：**
```java
/**
 * 名称字段。
 */
private String name;

private static int count = 0;

public static final int MAX_SIZE = 1000;

private List<String> items = new ArrayList<>();
```

## 参数规范

### JavaParameterSpec

方法/构造函数参数的规范。

```kotlin
public interface JavaParameterSpec : JavaSpec, NamedSpec, JavaModifierContainer {
    val name: String
    val type: TypeRef<*>
    val annotations: List<AnnotationRef>
    val modifiers: Set<JavaModifier>
}
```

**构造：**
```kotlin
// 简单参数
val paramSpec = JavaParameterSpec("value", JavaClassNames.STRING.ref())

// 带注解的参数
val annotatedParamSpec = JavaParameterSpec("id", JavaPrimitiveTypeNames.LONG.ref()) {
    addAnnotation(ClassName.get("javax.annotation", "Nonnull").ref())
}

// final 参数
val finalParamSpec = JavaParameterSpec("config", someConfigType.ref()) {
    addModifier(JavaModifier.FINAL)
}
```

**生成的输出：**
```java
String value
@Nonnull long id
final Config config
```

## Java 特定收集器

### JavaModifierCollector<T>

收集 Java 访问修饰符和关键字。

```kotlin
public interface JavaModifierCollector<T> {
    fun addModifier(modifier: JavaModifier): T
    fun addModifiers(modifiers: Iterable<JavaModifier>): T
    fun addModifiers(vararg modifiers: JavaModifier): T
}
```

**Java 修饰符：**
- **访问**：`PUBLIC`、`PROTECTED`、`PRIVATE`
- **继承**：`ABSTRACT`、`FINAL`、`STATIC`
- **同步**：`SYNCHRONIZED`、`VOLATILE`
- **本机**：`NATIVE`、`STRICTFP`、`TRANSIENT`
- **密封**：`SEALED`、`NON_SEALED`（Java 17+）

### StaticBlockCollector<T>

收集静态初始化块（Java 特定）。

```kotlin
public interface StaticBlockCollector<T> {
    fun addStaticBlock(format: String, vararg args: CodeArgumentPart): T
    fun addStaticBlock(codeBlock: CodeValue): T
}
```

**用法：**
```kotlin
classBuilder.addStaticBlock("""
    System.out.println("类已加载");
    initialize();
""".trimIndent())
```

### JavaMethodCollector<T>

收集 Java 方法。

```kotlin
public interface JavaMethodCollector<T> {
    fun addMethod(method: JavaMethodSpec): T
    fun addMethods(methods: Iterable<JavaMethodSpec>): T
    fun addMethods(vararg methods: JavaMethodSpec): T
}
```

### JavaFieldCollector<T>

收集 Java 字段。

```kotlin
public interface JavaFieldCollector<T> {
    fun addField(field: JavaFieldSpec): T
    fun addFields(fields: Iterable<JavaFieldSpec>): T
    fun addFields(vararg fields: JavaFieldSpec): T
}
```

### JavaSubtypeCollector<T>

收集嵌套 Java 类型。

```kotlin
public interface JavaSubtypeCollector<T> {
    fun addSubtype(subtype: JavaTypeSpec): T
    fun addSubtypes(subtypes: Iterable<JavaTypeSpec>): T
    fun addSubtypes(vararg subtypes: JavaTypeSpec): T
}
```

## 构造模式

### DSL vs 构建器

CodeGentle 提供 DSL 和传统构建器模式：

```kotlin
// DSL 模式（推荐）
val spec = JavaSimpleTypeSpec(Kind.CLASS, "MyClass") {
    addModifier(JavaModifier.PUBLIC)
    addField(JavaFieldSpec("field", stringType) {
        addModifier(JavaModifier.PRIVATE)
    })
}

// 构建器模式
val spec = JavaSimpleTypeSpec.builder(Kind.CLASS, "MyClass")
    .addModifier(JavaModifier.PUBLIC)
    .addField(
        JavaFieldSpec.builder("field", stringType)
            .addModifier(JavaModifier.PRIVATE)
            .build()
    )
    .build()
```

### 伴生对象方法

每种规范类型都提供伴生对象方法用于构造：

```kotlin
// JavaMethodSpec 伴生
JavaMethodSpec.methodBuilder("methodName")     // 常规方法
JavaMethodSpec.constructorBuilder()            // 构造函数
JavaMethodSpec.mainBuilder()                   // main 方法

// JavaSimpleTypeSpec 伴生
JavaSimpleTypeSpec.builder(kind, name)
```

## 示例

### 完整的 Java 类

```kotlin
val serviceClass = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "UserService") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("管理用户的服务。")
    
    // 添加接口
    addSuperinterface(ClassName.get("com.example", "Service"))
    
    // 私有字段
    addField(JavaFieldSpec("repository", repositoryType) {
        addModifier(JavaModifier.PRIVATE, JavaModifier.FINAL)
    })
    
    // 构造函数
    addMethod(JavaMethodSpec {
        addModifier(JavaModifier.PUBLIC)
        addParameter(JavaParameterSpec("repository", repositoryType))
        addCode("this.repository = repository;")
    })
    
    // 业务方法
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
    
    // 静态工厂方法
    addMethod(JavaMethodSpec("create") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC)
        returns(ClassName.get("com.example", "UserService").ref())
        addParameter(JavaParameterSpec("repository", repositoryType))
        addCode("return new UserService(repository);")
    })
}
```

### 带默认方法的 Java 接口

```kotlin
val serviceInterface = JavaSimpleTypeSpec(JavaTypeSpec.Kind.INTERFACE, "Cacheable") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("可缓存对象的接口。")
    
    // 抽象方法
    addMethod(JavaMethodSpec("getCacheKey") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.ABSTRACT)
        returns(JavaClassNames.STRING.ref())
    })
    
    // 默认方法  
    addMethod(JavaMethodSpec("evictCache") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.DEFAULT)
        addCode("""
            String key = getCacheKey();
            CacheManager.evict(key);
        """.trimIndent())
    })
    
    // 静态方法
    addMethod(JavaMethodSpec("clearAll") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC)
        addCode("CacheManager.clear();")
    })
}
```
