# CodeGentle 规范文档

CodeGentle 提供了一套全面的规范（Spec）类，用于生成 Java 和 Kotlin 源代码。本文档涵盖了三个主要模块中所有 XxxSpec 类的使用方法和构造模式：`codegentle-common`、`codegentle-java` 和 `codegentle-kotlin`。

## 快速导航

### 📚 文档部分
- **[通用规范](common-specs.md)** - 基础接口和通用功能
- **[Java 规范](java-specs.md)** - Java 特定的代码生成规范  
- **[Kotlin 规范](kotlin-specs.md)** - Kotlin 特定的代码生成规范

### 🏗️ 规范类别
- **类型规范** - 类、接口、枚举、对象、记录
- **成员规范** - 方法、函数、属性、字段、构造函数
- **参数规范** - 方法/函数参数和类型参数

## 概述

CodeGentle 规范类在所有模块中遵循一致的设计模式：

1. **基于接口的设计**：每个 Spec 都定义为具有特定属性的密封接口
2. **构建器模式**：伴生对象提供构造的构建器方法
3. **DSL 扩展**：内联函数启用流畅的、基于 lambda 的配置
4. **收集器模式**：构建器扩展多个收集器接口以实现流畅的 API

## 快速开始示例

### Java 类生成

```kotlin
import love.forte.codegentle.java.spec.*
import love.forte.codegentle.java.JavaFile
import love.forte.codegentle.common.naming.parseToPackageName

// 使用 DSL 创建简单的 Java 类
val javaClass = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "HelloWorld") {
    addModifier(JavaModifier.PUBLIC)
    addDoc("一个简单的问候类。")
    
    addMethod(JavaMethodSpec("greet") {
        addModifier(JavaModifier.PUBLIC)
        returns(JavaClassNames.STRING.ref())
        addParameter(JavaParameterSpec("name", JavaClassNames.STRING.ref()))
        addCode("return \"Hello, \" + name + \"!\";")
    })
}

// 创建包含该类的 Java 文件
val javaFile = JavaFile("com.example".parseToPackageName()) {
    addType(javaClass)
}
```

### Kotlin 类生成

```kotlin
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.KotlinFile
import love.forte.codegentle.common.naming.parseToPackageName

// 使用 DSL 创建简单的 Kotlin 类
val kotlinClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "HelloWorld") {
    addModifier(KotlinModifier.DATA)
    addKdoc("一个简单的问候类。")
    
    primaryConstructor {
        addParameter("name", KotlinClassNames.STRING)
    }
    
    addFunction(KotlinFunctionSpec("greet") {
        returns(KotlinClassNames.STRING)
        addCode("return \"Hello, \$name!\"")
    })
}

// 创建包含该类的 Kotlin 文件
val kotlinFile = KotlinFile("com.example".parseToPackageName()) {
    addType(kotlinClass)
}
```

## 构造模式

CodeGentle 提供两种主要的构造 Spec 实例的方法：

### 1. 构建器模式（原始 API）

```kotlin
// Java 示例
val methodSpec = JavaMethodSpec.methodBuilder("calculate")
    .addModifier(JavaModifier.PUBLIC)
    .returns(JavaClassNames.INT.ref())
    .addParameter(JavaParameterSpec("a", JavaClassNames.INT.ref()))
    .addParameter(JavaParameterSpec("b", JavaClassNames.INT.ref()))
    .addCode("return a + b;")
    .build()
```

### 2. DSL Lambda 扩展（推荐）

```kotlin
// Java 示例 - 与上面相同的结果但更简洁
val methodSpec = JavaMethodSpec("calculate") {
    addModifier(JavaModifier.PUBLIC)
    returns(JavaClassNames.INT.ref())
    addParameter(JavaParameterSpec("a", JavaClassNames.INT.ref()))
    addParameter(JavaParameterSpec("b", JavaClassNames.INT.ref()))
    addCode("return a + b;")
}
```

**DSL 优势：**
- 更简洁易读
- 更好的 IDE 支持与上下文补全
- 更容易嵌套和组合
- 遵循 Kotlin 习惯用法

## 模块特定功能

### 仅 Java 功能
- 静态块和静态成员
- 方法签名中的受检异常
- 记录类型和密封类（Java 14+）
- 带默认值的注解类型
- 包私有可见性

### 仅 Kotlin 功能
- 主构造函数和副构造函数
- 属性访问器（getter/setter）
- 扩展函数和属性
- 上下文接收器和挂起函数
- 值类和内联类
- 伴生对象和对象声明
