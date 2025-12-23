# CodeGentle 文档

CodeGentle 是一个 Kotlin 多平台库，用于以编程方式生成 Java 和 Kotlin 源代码。

## 📚 文档结构

### 核心概念

#### [代码生成](./code/README.md)
了解基于 `CodeValue` 和 `CodePart` 构建的 CodeGentle 代码生成系统：
- **[CodeValue](./code/CodeValue.md)** - 支持占位符的代码片段容器
- **[CodePart](./code/CodePart.md)** - 代码构造的基础构建块
- 控制流、缩进和格式化

#### [命名系统](./naming/README.md)
了解如何表示类型、类和成员：
- **[通用命名](./naming/common-naming.md)** - TypeName、ClassName、PackageName、MemberName
- **[泛型类型](./naming/generic-types.md)** - ParameterizedTypeName、TypeVariableName、WildcardTypeName、ArrayTypeName
- **[Java 命名](./naming/java-naming.md)** - Java 特定的工具和常量
- **[Kotlin 命名](./naming/kotlin-naming.md)** - KotlinLambdaTypeName、上下文接收器、值类

#### [文件生成](./file/README.md)
生成带导入和包结构的完整源文件：
- **[JavaFile](./file/JavaFile.md)** - Java 源文件生成，支持次级类型
- **[KotlinFile](./file/KotlinFile.md)** - Kotlin 源文件，支持顶层函数/属性

#### [Spec 系统](./spec/README.md)
构建类型、方法和属性规范：
- **[通用 Specs](./spec/common-specs.md)** - 基础接口和模式
- **[Java Specs](./spec/java-specs.md)** - 类、接口、枚举、记录、密封类型
- **[Kotlin Specs](./spec/kotlin-specs.md)** - 类、函数、属性、值类

### 高级功能

#### [KSP 集成](./ksp/README.md)
与 Kotlin Symbol Processing 无缝集成：
- **[类型转换](./ksp/type-conversion.md)** - 将 KSP 类型转换为 TypeName（15+ 函数）
- **[上下文接收器](./ksp/context-receivers.md)** - 处理 Kotlin 2.0+ 上下文接收器
- 类和成员名称转换
- 从 KSP 符号转换 Spec

## 🚀 快速开始

### Java 代码生成

```kotlin
import love.forte.codegentle.java.*

val classSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "HelloWorld") {
    addModifier(JavaModifier.PUBLIC)
    addMethod(JavaMethodSpec("main") {
        addModifier(JavaModifier.PUBLIC, JavaModifier.STATIC)
        returns(JavaClassNames.VOID.ref())
        addParameter(JavaParameterSpec("args", JavaClassNames.STRING.ref().array()))
        addCode("System.out.println(\"Hello, World!\");")
    })
}

val javaFile = JavaFile("com.example".parseToPackageName(), classSpec)
println(javaFile.writeToJavaString())
```

### Kotlin 代码生成

```kotlin
import love.forte.codegentle.kotlin.*

val classSpec = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "HelloWorld") {
    addFunction(KotlinFunctionSpec("main") {
        returns(KotlinClassNames.UNIT.ref())
        addCode("println(\"Hello, World!\")")
    })
}

val kotlinFile = KotlinFile("com.example".parseToPackageName(), classSpec)
println(kotlinFile.writeToKotlinString())
```

### KSP 集成

```kotlin
import love.forte.codegentle.kotlin.ksp.*

class MyProcessor : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation("MyAnnotation")
            .filterIsInstance<KSClassDeclaration>()
            .forEach { classDecl ->
                // 直接转换 KSP 类型
                val className = classDecl.toClassName()
                val functionSpecs = classDecl.getAllFunctions()
                    .map { it.toKotlinFunctionSpec() }
                // 生成代码...
            }
        return emptyList()
    }
}
```

## 📦 模块

| 模块                      | 描述                                  |
|-------------------------|-------------------------------------|
| `codegentle-common`     | 核心 API：CodeValue、TypeName、通用 specs  |
| `codegentle-java`       | Java 代码生成：JavaFile、Java specs       |
| `codegentle-kotlin`     | Kotlin 代码生成：KotlinFile、Kotlin specs |
| `codegentle-common-ksp` | KSP 通用工具，用于类型转换                     |
| `codegentle-kotlin-ksp` | KSP Kotlin 集成，用于 spec 转换            |

## 🎯 主要功能

### 多平台支持
- JVM、JavaScript、Native、Wasm 目标
- 平台无关的 API 以及 JVM 特定的扩展

### 现代语言特性
**Java**：
- Record 类型（Java 16+）
- 密封类/接口（Java 17+）
- 非密封类型

**Kotlin**：
- 值类（内联类）
- 上下文接收器（Kotlin 2.0+）
- 挂起函数
- 扩展函数和属性
- 顶层声明

### KSP 集成
- 从 KSP 符号直接转换
- 上下文接收器检测
- 完整的类型系统支持
- ERROR TYPE 处理

### 灵活的代码构造
- 占位符系统（`%V`）用于动态内容
- 控制流扩展（if/else、try/catch、循环）
- 智能换行（100 列限制）
- Builder DSL 模式

## 🔗 外部资源

- **GitHub**: [ForteScarlet/codegentle](https://github.com/ForteScarlet/codegentle)
- **API 参考**: 查看模块特定文档
- **示例**: 检查仓库中的 `/tests/` 目录

## 📖 语言版本

- **English Documentation**: See [`docs/`](../docs/README.md) for English documentation
- **中文文档**: 您正在这里（`docs_zh/`）

