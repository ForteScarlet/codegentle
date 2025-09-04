# Kotlin 规范文档

`codegentle-kotlin` 模块通过各种规范类提供全面的 Kotlin 代码生成功能。所有 Kotlin 规范都扩展基础的 `KotlinSpec` 接口，并支持基于 DSL 的构造模式。

## 目录

- [基础接口](#基础接口)
- [类型规范](#类型规范)
- [可调用规范](#可调用规范)
- [参数规范](#参数规范)
- [其他规范](#其他规范)
- [DSL 模式和示例](#dsl-模式和示例)

## 基础接口

### KotlinSpec

所有 Kotlin 代码生成规范的基础接口。

```kotlin
public interface KotlinSpec : Spec, KotlinCodeEmitter
```

**主要功能**：
- 继承自 `Spec`（通用基础）
- 扩展 `KotlinCodeEmitter` 用于 Kotlin 代码生成
- 提供 `writeToKotlinString()` 扩展函数

**用法**：
```kotlin
val kotlinClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass") {
    // DSL 配置
}

// 生成 Kotlin 代码字符串
val generatedCode = kotlinClass.writeToKotlinString()
```

### KotlinCallableSpec

可调用规范（函数和构造函数）的基础接口。

```kotlin
public sealed interface KotlinCallableSpec : KotlinSpec, KotlinModifierContainer
```

**实现者**：
- `KotlinFunctionSpec`
- `KotlinConstructorSpec`
- `KotlinPropertyAccessorSpec`

## 类型规范

### KotlinTypeSpec

所有 Kotlin 类型声明的基础接口。

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

**Kind 枚举值**：
- `CLASS` - 常规类
- `INTERFACE` - 接口
- `OBJECT` - 对象声明
- `TYPE_ALIAS` - 类型别名

**工厂方法**：
```kotlin
// 伴生对象方法
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

用于常规类和接口。

```kotlin
public interface KotlinSimpleTypeSpec : KotlinTypeSpec {
    override val name: String
}
```

**DSL 构造**：
```kotlin
val kotlinClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Person") {
    addModifier(KotlinModifier.DATA)
    addKdoc("表示具有姓名和年龄的人。")
    
    // 主构造函数
    primaryConstructor {
        addParameter("name", KotlinClassNames.STRING) {
            addModifier(KotlinModifier.VAL)
        }
        addParameter("age", KotlinClassNames.INT) {
            addModifier(KotlinModifier.VAL)
        }
    }
    
    // 添加函数
    addFunction("greet") {
        returns(KotlinClassNames.STRING)
        addCode("return \"Hello, I'm \$name and I'm \$age years old\"")
    }
}
```

**生成的输出**：
```kotlin
/**
 * 表示具有姓名和年龄的人。
 */
data class Person(
    val name: String,
    val age: Int
) {
    fun greet(): String = "Hello, I'm $name and I'm $age years old"
}
```

### KotlinEnumTypeSpec

用于枚举类。

```kotlin
public interface KotlinEnumTypeSpec : KotlinTypeSpec {
    val enumConstants: List<KotlinEnumConstant>
}
```

**DSL 构造**：
```kotlin
val enumClass = KotlinEnumTypeSpec("Color") {
    addKdoc("表示 RGB 颜色。")
    
    // 添加枚举常量
    addEnumConstant("RED") {
        addConstructorArgument("0xFF0000")
    }
    addEnumConstant("GREEN") {
        addConstructorArgument("0x00FF00") 
    }
    addEnumConstant("BLUE") {
        addConstructorArgument("0x0000FF")
    }
    
    // 枚举的主构造函数
    primaryConstructor {
        addParameter("rgb", KotlinClassNames.INT) {
            addModifier(KotlinModifier.VAL)
        }
    }
    
    // 添加成员函数
    addFunction("toHex") {
        returns(KotlinClassNames.STRING)
        addCode("return \"#\" + rgb.toString(16).padStart(6, '0')")
    }
}
```

### KotlinObjectTypeSpec

用于对象声明和伴生对象。

```kotlin
public interface KotlinObjectTypeSpec : KotlinTypeSpec {
    val isCompanion: Boolean
}
```

**DSL 构造**：
```kotlin
// 常规对象
val singletonObject = KotlinObjectTypeSpec("DatabaseManager") {
    addKdoc("管理数据库连接。")
    
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

// 伴生对象
val companionObject = KotlinObjectTypeSpec.companionBuilder() {
    addFunction("create") {
        addParameter("name", KotlinClassNames.STRING)
        returns(MyClass::class.asClassName())
        addCode("return MyClass(name)")
    }
}
```

### KotlinAnnotationTypeSpec

用于注解类。

```kotlin
public interface KotlinAnnotationTypeSpec : KotlinTypeSpec
```

**DSL 构造**：
```kotlin
val annotationClass = KotlinAnnotationTypeSpec("Deprecated") {
    addKdoc("将声明标记为已弃用。")
    
    // 注解构造函数参数
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

用于值类（内联类）。

```kotlin
public interface KotlinValueClassTypeSpec : KotlinTypeSpec {
    val primaryConstructor: KotlinConstructorSpec
}
```

**DSL 构造**：
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

## 可调用规范

### KotlinFunctionSpec

用于函数声明。

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

**DSL 构造**：
```kotlin
// 常规函数
val function = KotlinFunctionSpec("calculateTotal") {
    addKdoc("计算包含税款的总价。")
    
    addParameter("price", KotlinClassNames.DOUBLE)
    addParameter("taxRate", KotlinClassNames.DOUBLE) {
        defaultValue("0.08")
    }
    
    returns(KotlinClassNames.DOUBLE)
    addCode("return price * (1 + taxRate)")
}

// 扩展函数
val extensionFunction = KotlinFunctionSpec("isEven") {
    receiver(KotlinClassNames.INT)
    returns(KotlinClassNames.BOOLEAN)
    addCode("return this % 2 == 0")
}

// 挂起函数
val suspendFunction = KotlinFunctionSpec("fetchData") {
    addModifier(KotlinModifier.SUSPEND)
    returns(String::class.asClassName())
    addCode("return withContext(Dispatchers.IO) { /* fetch data */ }")
}
```

### KotlinConstructorSpec

用于构造函数声明。

```kotlin
public interface KotlinConstructorSpec : KotlinCallableSpec {
    val parameters: List<KotlinValueParameterSpec>
    val delegatedConstructorCall: CodeValue
    val body: CodeValue
    val isPrimary: Boolean
}
```

**DSL 构造**：
```kotlin
// 主构造函数（在类型规范中使用）
val primaryConstructor = KotlinConstructorSpec {
    addParameter("name", KotlinClassNames.STRING) {
        addModifier(KotlinModifier.VAL)
    }
    addParameter("age", KotlinClassNames.INT) {
        addModifier(KotlinModifier.VAL)
        defaultValue("0")
    }
}

// 副构造函数
val secondaryConstructor = KotlinConstructorSpec {
    addParameter("name", KotlinClassNames.STRING)
    delegateToThis("name", "0") // 委托给主构造函数
    addCode("println(\"Created person: \$name\")")
}
```

### KotlinPropertySpec

用于属性声明。

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

**DSL 构造**：
```kotlin
// 简单属性
val property = KotlinPropertySpec("name", KotlinClassNames.STRING) {
    isMutable(false) // val
    initializer("\"Unknown\"")
}

// 带自定义 getter/setter 的属性
val computedProperty = KotlinPropertySpec("fullName", KotlinClassNames.STRING) {
    getter {
        addCode("return \"\$firstName \$lastName\"")
    }
}

// 委托属性  
val delegatedProperty = KotlinPropertySpec("lazy", KotlinClassNames.STRING) {
    delegate("lazy { \"Computed value\" }")
}

// 扩展属性
val extensionProperty = KotlinPropertySpec("isBlank", KotlinClassNames.BOOLEAN) {
    receiver(KotlinClassNames.STRING)
    getter {
        addCode("return this.trim().isEmpty()")
    }
}
```

## 参数规范

### KotlinValueParameterSpec

用于函数/构造函数值参数。

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

**DSL 构造**：
```kotlin
val parameter = KotlinValueParameterSpec("data", List::class.asClassName().parameterizedBy(KotlinClassNames.STRING)) {
    addModifier(KotlinModifier.VARARG)
    addKdoc("要处理的数据项。")
}
```

### KotlinContextParameterSpec

用于上下文接收器（Kotlin 特定功能）。

```kotlin
public interface KotlinContextParameterSpec : KotlinParameterSpec {
    override val name: String
    val type: TypeRef<*>
}
```

**DSL 构造**：
```kotlin
val contextParameter = KotlinContextParameterSpec("logger", Logger::class.asClassName())

val functionWithContext = KotlinFunctionSpec("log") {
    addContextParameter(contextParameter)
    addParameter("message", KotlinClassNames.STRING)
    addCode("logger.info(message)")
}
```

## 其他规范

### KotlinPropertyAccessorSpec

属性 getter 和 setter 的基础。

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

用于类型别名。

```kotlin
public interface KotlinTypealiasSpec : KotlinTypeSpec {
    val aliasedType: TypeRef<*>
}
```

**DSL 构造**：
```kotlin
val typeAlias = KotlinTypealiasSpec("StringList", List::class.asClassName().parameterizedBy(KotlinClassNames.STRING)) {
    addKdoc("字符串列表。")
    addModifier(KotlinModifier.TYPEALIAS)
}
```

## DSL 模式和示例

### 完整类示例

```kotlin
val kotlinClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Repository") {
    addKdoc("用于管理实体的数据存储库。")
    addModifier(KotlinModifier.OPEN)
    
    // 类型参数
    addTypeVariable(TypeVariableName.create("T"))
    
    // 主构造函数
    primaryConstructor {
        addParameter("database", Database::class.asClassName()) {
            addModifier(KotlinModifier.PRIVATE)
            addModifier(KotlinModifier.VAL)
        }
    }
    
    // 属性
    addProperty("cache", Map::class.asClassName().parameterizedBy(
        KotlinClassNames.STRING, 
        TypeVariableName.create("T").asTypeName()
    )) {
        addModifier(KotlinModifier.PRIVATE)
        addModifier(KotlinModifier.VAL)
        delegate("mutableMapOf()")
    }
    
    // 函数
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
    
    // 伴生对象
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

### 带扩展函数的接口

```kotlin
val serviceInterface = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.INTERFACE, "UserService") {
    addKdoc("用户操作的服务接口。")
    
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

// 接口的扩展函数
val extensionFunction = KotlinFunctionSpec("findUserOrThrow") {
    addModifier(KotlinModifier.SUSPEND)
    receiver(UserService::class.asClassName())
    addParameter("id", KotlinClassNames.STRING)
    returns(User::class.asClassName())
    
    addCode("return findUser(id) ?: throw UserNotFoundException(id)")
}
```

## 最佳实践

1. **使用 DSL 扩展**：优先使用基于 lambda 的构造而不是构建器链
2. **利用类型安全**：使用 `asClassName()` 和类型安全引用
3. **文档**：始终为公共 API 添加 KDoc
4. **修饰符**：使用适当的 Kotlin 修饰符（`data`、`sealed`、`inline` 等）
5. **空安全**：在适当的地方考虑可空类型
6. **上下文接收器**：使用上下文参数进行依赖注入模式

## 平台特定功能

### 仅 Kotlin 功能

- **主/副构造函数**：独特的构造函数语法
- **属性**：一级属性支持，带 getter/setter  
- **扩展函数**：扩展现有类型
- **对象声明**：单例对象和伴生对象
- **值类**：零成本包装器
- **上下文接收器**：无参数的依赖注入
- **挂起函数**：协程支持
- **类型别名**：为类型创建替代名称

