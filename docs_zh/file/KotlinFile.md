# KotlinFile API 文档

`KotlinFile` 是 CodeGentle 库中用于表示和生成 Kotlin 源文件的核心接口。它支持包含顶层类、接口、对象、函数、属性以及脚本代码的完整 Kotlin 文件生成。

## 目录

- [KotlinFile 接口](#kotlinfile-接口)
- [KotlinFileBuilder 层次结构](#kotlinfilebuilder-层次结构)
- [DSL 扩展函数](#dsl-扩展函数)
- [工具函数](#工具函数)
- [使用示例](#使用示例)

---

## KotlinFile 接口

`KotlinFile` 接口继承自 `KotlinCodeEmitter` 和 `Named`，提供了 Kotlin 文件的完整抽象。

### 属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `fileComment` | `CodeValue` | 文件顶部注释 |
| `packageName` | `PackageName` | 包名 |
| `name` | `String` | 文件名（不含扩展名） |
| `types` | `List<KotlinTypeSpec>` | 顶层类型列表（类、接口、对象等） |
| `functions` | `List<KotlinFunctionSpec>` | 顶层函数列表 |
| `properties` | `List<KotlinPropertySpec>` | 顶层属性列表 |
| `type` | `KotlinTypeSpec` | 第一个类型（兼容性属性，等同于 `types.first()`） |
| `staticImports` | `Set<String>` | 静态导入集合 |
| `alwaysQualify` | `Set<String>` | 始终使用全限定名的类型集合 |
| `indent` | `String` | 缩进字符串（默认为四个空格） |
| `annotations` | `List<AnnotationRef>` | 文件级注解列表（使用 `@file:` 语法） |

### 方法

#### writeTo

```kotlin
fun writeTo(out: Appendable, strategy: KotlinWriteStrategy)
```

将 Kotlin 文件写入到指定的 `Appendable` 输出目标。

**参数：**
- `out`: 输出目标（如 `StringBuilder`、`Writer` 等）
- `strategy`: 写入策略，控制代码生成的行为

---

## KotlinFileBuilder 层次结构

`KotlinFileBuilder` 是构建 `KotlinFile` 实例的抽象基类，提供了两种具体实现：

### 1. KotlinSimpleFileBuilder

用于构建普通 Kotlin 文件（`.kt`），包含类、接口、函数、属性等顶层元素。

### 2. KotlinScriptFileBuilder

用于构建 Kotlin 脚本文件（`.kts`），除了支持顶层元素外，还支持直接编写脚本代码。

---

## 创建 KotlinFile

### 使用 Builder

#### builder(packageName, type)

创建包含单个类型的文件构建器：

```kotlin
val file = KotlinFile.builder(
    packageName = "com.example".parseToPackageName(),
    type = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass")
).build()
```

#### builder(packageName)

创建空文件构建器，稍后添加元素：

```kotlin
val file = KotlinFile.builder("com.example".parseToPackageName())
    .addType(typeSpec)
    .addFunction(functionSpec)
    .addProperty(propertySpec)
    .build()
```

#### scriptBuilder()

创建脚本文件构建器：

```kotlin
val scriptFile = KotlinFile.scriptBuilder()
    .addCode("println(\"Hello, World!\")")
    .build()
```

### 使用 DSL

#### 普通文件 DSL

```kotlin
// 单个类型
val file = KotlinFile("com.example", typeSpec) {
    addFileComment("This is a generated file")
    addStaticImport("kotlin.collections.Collections", "emptyList")
}

// 多个类型（vararg）
val file = KotlinFile("com.example", typeSpec1, typeSpec2) {
    indent("\t")
}

// 多个类型（Iterable）
val types = listOf(typeSpec1, typeSpec2)
val file = KotlinFile("com.example", types) {
    name("CustomFileName")
}

// 仅函数和属性
val file = KotlinFile("com.example".parseToPackageName()) {
    addFunction(functionSpec)
    addProperty(propertySpec)
}
```

#### 脚本文件 DSL

```kotlin
val scriptFile = KotlinFile {
    addStatement("val x = 10")
    addStatement("println(x)")
    addFunction(helperFunction)
}
```

---

## Builder 方法详解

### 类型管理

```kotlin
// 添加单个类型
fun addType(type: KotlinTypeSpec): B

// 添加多个类型（Iterable）
fun addTypes(types: Iterable<KotlinTypeSpec>): B

// 添加多个类型（vararg）
fun addTypes(vararg types: KotlinTypeSpec): B
```

### 函数管理

```kotlin
// 添加单个函数
fun addFunction(function: KotlinFunctionSpec): B

// 添加多个函数
fun addFunctions(functions: Iterable<KotlinFunctionSpec>): B
```

### 属性管理

```kotlin
// 添加单个属性
fun addProperty(property: KotlinPropertySpec): B

// 添加多个属性
fun addProperties(properties: Iterable<KotlinPropertySpec>): B
```

### 文件注释

```kotlin
// 简单格式
fun addFileComment(format: String): B

// 带代码块
fun addFileComment(format: String, block: CodeValueSingleFormatBuilderDsl): B

// 使用 CodeValue
fun addFileComment(codeValue: CodeValue): B
```

**示例：**

```kotlin
KotlinFile("com.example", typeSpec) {
    addFileComment("Copyright (C) 2025 Example Corp")
    addFileComment("Generated at: %L", block = {
        add(System.currentTimeMillis())
    })
}
```

### 静态导入

```kotlin
// 导入字符串
fun addStaticImport(import: String): B

// 导入类成员（vararg）
fun addStaticImport(className: ClassName, vararg names: String): B

// 导入类成员（Iterable）
fun addStaticImport(className: ClassName, names: Iterable<String>): B
```

**示例：**

```kotlin
KotlinFile("com.example", typeSpec) {
    addStaticImport("kotlin.io.println")
    addStaticImport(
        ClassName("kotlin.collections", "Collections"),
        "emptyList", "emptyMap"
    )
}
```

### 文件级注解

文件级注解使用 `@file:` 语法，必须设置正确的 `useSite`：

```kotlin
// 添加单个注解
fun addAnnotation(ref: AnnotationRef): B

// 添加多个注解
fun addAnnotations(refs: Iterable<AnnotationRef>): B
```

**示例：**

```kotlin
val suppressAnnotation = ClassName("kotlin", "Suppress").kotlinAnnotationRef {
    addMember("\"UNUSED\"")
    status {
        useSite = KotlinAnnotationUseSite.FILE
    }
}

KotlinFile("com.example", typeSpec) {
    addAnnotation(suppressAnnotation)
}
```

**生成代码：**

```kotlin
@file:Suppress("UNUSED")
package com.example

class MyClass
```

**注意事项：**
- 只能添加 `useSite = FILE` 或未指定 `useSite` 的注解
- 其他 `useSite`（如 `FIELD`、`GET` 等）会抛出 `IllegalArgumentException`
- 注解会在 `package` 声明之前生成

### 其他配置

```kotlin
// 设置缩进（默认四个空格）
fun indent(indent: String): B

// 设置文件名
fun name(name: String): B
```

### 构建文件

```kotlin
fun build(): KotlinFile
```

---

## DSL 扩展函数

这些扩展函数简化了特定类型的创建：

### 简单类型

```kotlin
// 添加类
fun <B : KotlinFileBuilder<B>> B.addSimpleClassType(
    name: String,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): B

// 添加接口
fun <B : KotlinFileBuilder<B>> B.addSimpleInterfaceType(
    name: String,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): B
```

**示例：**

```kotlin
KotlinFile("com.example") {
    addSimpleClassType("Person") {
        addModifier(KotlinModifier.DATA)
        addProperty(nameProperty)
    }

    addSimpleInterfaceType("Drawable") {
        addFunction(drawFunction)
    }
}
```

### 对象

```kotlin
fun <B : KotlinFileBuilder<B>> B.addObjectType(
    name: String,
    isCompanion: Boolean = false,
    block: KotlinObjectTypeSpec.Builder.() -> Unit = {}
): B
```

**示例：**

```kotlin
KotlinFile("com.example") {
    addObjectType("Singleton") {
        addProperty(instanceProperty)
    }
}
```

### 枚举

```kotlin
fun <B : KotlinFileBuilder<B>> B.addEnumType(
    name: String,
    block: KotlinEnumTypeSpec.Builder.() -> Unit = {}
): B
```

**示例：**

```kotlin
KotlinFile("com.example") {
    addEnumType("Color") {
        addEnumConstant("RED")
        addEnumConstant("GREEN")
        addEnumConstant("BLUE")
    }
}
```

### 注解类

```kotlin
fun <B : KotlinFileBuilder<B>> B.addAnnotationType(
    name: String,
    block: KotlinAnnotationTypeSpec.Builder.() -> Unit = {}
): B
```

**示例：**

```kotlin
KotlinFile("com.example") {
    addAnnotationType("MyAnnotation") {
        addProperty(valueProperty)
    }
}
```

### 值类

```kotlin
// 使用 KotlinConstructorSpec
fun <B : KotlinFileBuilder<B>> B.addValueClassType(
    name: String,
    primaryConstructor: KotlinConstructorSpec,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): B

// 使用构造器 DSL
fun <B : KotlinFileBuilder<B>> B.addValueClassType(
    name: String,
    primaryConstructor: KotlinConstructorSpec.Builder.() -> Unit,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): B

// 使用单个参数
fun <B : KotlinFileBuilder<B>> B.addValueClassType(
    name: String,
    primaryParameter: KotlinValueParameterSpec,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): B
```

**示例：**

```kotlin
KotlinFile("com.example") {
    addValueClassType("UserId", primaryParameter = KotlinValueParameterSpec(
        name = "value",
        type = ClassName("kotlin", "String").ref()
    ) {
        immutableProperty()
    })
}
```

### 类型别名

```kotlin
fun <B : KotlinFileBuilder<B>> B.addTypealiasType(
    name: String,
    type: TypeRef<*>,
    block: KotlinTypealiasSpec.Builder.() -> Unit = {}
): B
```

**示例：**

```kotlin
KotlinFile("com.example") {
    addTypealiasType(
        name = "StringMap",
        type = ClassName("kotlin.collections", "Map")
            .parameterizedBy(stringRef, stringRef)
            .ref()
    )
}
```

---

## 工具函数

### toRelativePath

将 `KotlinFile` 转换为相对路径字符串，用于文件系统操作。

```kotlin
fun KotlinFile.toRelativePath(
    filename: String = this.name,
    isScript: Boolean = false,
    separator: String = "/"
): String
```

**参数：**
- `filename`: 文件名（默认使用 `this.name`）
- `isScript`: 是否为脚本文件（`.kts` vs `.kt`）
- `separator`: 路径分隔符（默认为 `/`）

**返回值：** 相对路径字符串，如 `com/example/MyClass.kt`

**示例：**

```kotlin
val file = KotlinFile("com.example", typeSpec)

// 使用默认文件名
val path1 = file.toRelativePath()
// "com/example/MyClass.kt"

// 自定义文件名
val path2 = file.toRelativePath(filename = "CustomName")
// "com/example/CustomName.kt"

// 脚本文件
val path3 = file.toRelativePath(isScript = true)
// "com/example/MyClass.kts"

// Windows 路径分隔符
val path4 = file.toRelativePath(separator = "\\")
// "com\example\MyClass.kt"
```

### writeToKotlinString

将 `KotlinFile` 转换为 Kotlin 代码字符串。

```kotlin
fun KotlinFile.writeToKotlinString(): String
```

**示例：**

```kotlin
val file = KotlinFile("com.example", typeSpec)
val kotlinCode = file.writeToKotlinString()
println(kotlinCode)
```

---

## 使用示例

### 示例 1：基本类文件

```kotlin
val personClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Person") {
    addModifier(KotlinModifier.DATA)
    primaryConstructor(KotlinConstructorSpec {
        addParameter("name", ClassName("kotlin", "String").ref()) {
            immutableProperty()
        }
        addParameter("age", ClassName("kotlin", "Int").ref()) {
            immutableProperty()
        }
    })
}

val file = KotlinFile("com.example.model", personClass) {
    addFileComment("Generated model class")
    indent("    ")
}

println(file.writeToKotlinString())
```

**输出：**

```kotlin
// Generated model class
package com.example.model

data class Person(val name: String, val age: Int)
```

### 示例 2：包含多个顶层元素

```kotlin
val file = KotlinFile("com.example.utils".parseToPackageName()) {
    // 顶层属性
    addProperty(KotlinPropertySpec("APP_NAME", ClassName("kotlin", "String").ref()) {
        addModifier(KotlinModifier.CONST)
        initializer("\"MyApp\"")
    })

    // 顶层函数
    addFunction(KotlinFunctionSpec("log", ClassName("kotlin", "Unit").ref()) {
        addParameter("message", ClassName("kotlin", "String").ref())
        addCode("println(\"[\$APP_NAME] \$message\")")
    })

    // 辅助类
    addSimpleClassType("Logger") {
        addModifier(KotlinModifier.INTERNAL)
    }
}
```

**输出：**

```kotlin
package com.example.utils

const val APP_NAME: String = "MyApp"

internal class Logger

fun log(message: String) {
    println("[$APP_NAME] $message")
}
```

### 示例 3：文件级注解

```kotlin
val suppressAnnotation = ClassName("kotlin", "Suppress").kotlinAnnotationRef {
    addMember("\"UNUSED\", \"DEPRECATION\"")
    status {
        useSite = KotlinAnnotationUseSite.FILE
    }
}

val jvmNameAnnotation = ClassName("kotlin.jvm", "JvmName").kotlinAnnotationRef {
    addMember("\"MyClassUtils\"")
    status {
        useSite = KotlinAnnotationUseSite.FILE
    }
}

val file = KotlinFile("com.example", typeSpec) {
    addAnnotation(suppressAnnotation)
    addAnnotation(jvmNameAnnotation)
}
```

**输出：**

```kotlin
@file:Suppress("UNUSED", "DEPRECATION")
@file:JvmName("MyClassUtils")
package com.example

class MyClass
```

### 示例 4：静态导入

```kotlin
val file = KotlinFile("com.example", typeSpec) {
    addStaticImport("kotlin.io.println")
    addStaticImport(
        ClassName("kotlin.collections", "Collections"),
        "emptyList",
        "emptyMap",
        "emptySet"
    )
}
```

**输出：**

```kotlin
package com.example

import kotlin.io.println
import kotlin.collections.Collections.emptyList
import kotlin.collections.Collections.emptyMap
import kotlin.collections.Collections.emptySet

class MyClass
```

### 示例 5：脚本文件

```kotlin
val scriptFile = KotlinFile {
    addStatement("val numbers = listOf(1, 2, 3, 4, 5)")
    addStatement("val doubled = numbers.map { it * 2 }")
    addStatement("println(doubled)")

    // 脚本中也可以定义函数
    addFunction(KotlinFunctionSpec("helper", ClassName("kotlin", "Unit").ref()) {
        addCode("println(\"Helper function\")")
    })

    addStatement("helper()")
}

// 保存为 .kts 文件
val scriptPath = scriptFile.toRelativePath(isScript = true)
```

**输出：**

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val doubled = numbers.map { it * 2 }
println(doubled)

fun helper() {
    println("Helper function")
}

helper()
```

### 示例 6：复杂文件结构

```kotlin
val file = KotlinFile("com.example.api") {
    // 文件注释
    addFileComment("API definitions\nGenerated on: 2025-12-23")

    // 文件级注解
    addAnnotation(ClassName("kotlin", "Suppress").kotlinAnnotationRef {
        addMember("\"unused\"")
        status { useSite = KotlinAnnotationUseSite.FILE }
    })

    // 顶层常量
    addProperty(KotlinPropertySpec("API_VERSION", ClassName("kotlin", "Int").ref()) {
        addModifier(KotlinModifier.CONST)
        initializer("1")
    })

    // 接口定义
    addSimpleInterfaceType("Api") {
        addFunction(KotlinFunctionSpec("call", ClassName("kotlin", "String").ref()))
    }

    // 实现类
    addSimpleClassType("ApiImpl") {
        addModifier(KotlinModifier.INTERNAL)
        addSuperinterface(ClassName("com.example.api", "Api").ref())

        addFunction(KotlinFunctionSpec("call", ClassName("kotlin", "String").ref()) {
            addModifier(KotlinModifier.OVERRIDE)
            addCode("return \"API v\$API_VERSION\"")
        })
    }

    // 工厂函数
    addFunction(KotlinFunctionSpec("createApi", ClassName("com.example.api", "Api").ref()) {
        addCode("return ApiImpl()")
    })

    name("Api")
    indent("  ")
}
```

**输出：**

```kotlin
// API definitions
// Generated on: 2025-12-23
@file:Suppress("unused")
package com.example.api

const val API_VERSION: Int = 1

interface Api {
  fun call(): String
}

internal class ApiImpl : Api {
  override fun call(): String = "API v$API_VERSION"
}

fun createApi(): Api = ApiImpl()
```

### 示例 7：多文件生成

```kotlin
fun generateModels(packageName: PackageName): List<KotlinFile> {
    val models = listOf("User", "Product", "Order")

    return models.map { modelName ->
        KotlinFile(packageName) {
            addSimpleClassType(modelName) {
                addModifier(KotlinModifier.DATA)
                primaryConstructor(KotlinConstructorSpec {
                    addParameter("id", ClassName("kotlin", "Long").ref()) {
                        immutableProperty()
                    }
                })
            }

            name(modelName)
        }
    }
}

// 使用
val files = generateModels("com.example.model".parseToPackageName())
files.forEach { file ->
    val path = file.toRelativePath()
    val code = file.writeToKotlinString()

    // 写入文件系统
    File(path).apply {
        parentFile.mkdirs()
        writeText(code)
    }
}
```

---

## 文件写入策略

`KotlinFile.writeTo()` 方法接受 `KotlinWriteStrategy` 参数，用于控制代码生成行为：

```kotlin
// 使用默认策略（toString）
val code = file.writeToKotlinString()

// 自定义策略
val customStrategy = object : KotlinWriteStrategy {
    override fun omitPackage(packageName: PackageName): Boolean {
        // 自定义包省略逻辑
        return packageName.toString() == "kotlin"
    }
}

val code = buildString {
    file.writeTo(this, customStrategy)
}
```

---

## 最佳实践

### 1. 文件命名

- 使用 `name()` 方法显式设置文件名
- 如果未设置，默认使用第一个类型的名称
- 对于纯函数/属性文件，建议手动指定有意义的名称

```kotlin
KotlinFile("com.example") {
    addFunction(helperFunction)
    name("Helpers")  // 明确指定文件名
}
```

### 2. 包名管理

- 使用 `PackageName` 类型而非字符串
- 使用 `parseToPackageName()` 扩展函数转换字符串

```kotlin
val packageName = "com.example.app".parseToPackageName()
KotlinFile(packageName, typeSpec)
```

### 3. 导入优化

- 使用 `staticImports` 简化常用成员调用
- 使用 `alwaysQualify` 避免名称冲突
- 避免重复导入同一类型

### 4. 文件级注解

- 必须使用 `KotlinAnnotationUseSite.FILE`
- 多个注解会按添加顺序生成
- 注解在 `package` 声明之前

### 5. 代码组织

- 属性 → 类型 → 函数（按此顺序生成）
- 相关元素分组添加
- 使用空行分隔不同逻辑块

### 6. 脚本文件

- 使用 `KotlinFile { }` DSL
- 可包含顶层语句、函数、属性
- 使用 `toRelativePath(isScript = true)` 生成 `.kts` 路径

---

## 常见问题

### Q: 如何生成空文件？

A: `KotlinFile` 至少需要一个元素（类型、函数或属性）。如果需要空脚本，使用：

```kotlin
KotlinFile {
    addStatement("// Empty script")
}
```

### Q: 如何控制生成的代码格式？

A: 使用 `indent()` 方法设置缩进，使用自定义 `KotlinWriteStrategy` 控制更多细节。

### Q: 文件级注解和类注解有什么区别？

A: 文件级注解使用 `@file:` 语法，在 `package` 声明之前。类注解直接在类声明之前。

### Q: 如何生成多个文件？

A: 创建 `List<KotlinFile>`，遍历并使用 `toRelativePath()` 和 `writeToKotlinString()` 输出。

---

## 相关 API

- [KotlinTypeSpec](../spec/KotlinTypeSpec.md) - 类型规范
- [KotlinFunctionSpec](../spec/KotlinFunctionSpec.md) - 函数规范
- [KotlinPropertySpec](../spec/KotlinPropertySpec.md) - 属性规范
- [PackageName](../naming/PackageName.md) - 包名
- [CodeValue](../naming/CodeValue.md) - 代码值
