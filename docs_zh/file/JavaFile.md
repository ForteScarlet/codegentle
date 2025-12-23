# JavaFile API 详细文档

`JavaFile` 是用于构建完整 Java 源文件的接口，提供了文件级别的元素管理，包括包声明、导入语句、文件注释和类型定义。

## 目录
- [JavaFile 接口](#javafile-接口)
- [JavaFileBuilder 构建器](#javafilebuilder-构建器)
- [DSL 扩展函数](#dsl-扩展函数)
- [工具函数](#工具函数)
- [JVM 平台扩展](#jvm-平台扩展)
- [完整示例](#完整示例)

---

## JavaFile 接口

### 属性

#### fileComment
```kotlin
val fileComment: CodeValue
```
文件注释，通常用于版权声明、自动生成警告等。

**示例：**
```kotlin
JavaFile("com.example", typeSpec) {
    addFileComment("Copyright (C) 2025 Example Corp.")
    addFileComment("Generated code - DO NOT EDIT")
}
```

生成：
```java
// Copyright (C) 2025 Example Corp.
// Generated code - DO NOT EDIT
package com.example;
```

---

#### packageName
```kotlin
val packageName: PackageName
```
包名，表示此文件所属的 Java 包。

**示例：**
```kotlin
val packageName = "com.example.demo".parseToPackageName()
val javaFile = JavaFile(packageName, typeSpec)
```

---

#### type
```kotlin
val type: JavaTypeSpec
```
主类型定义，通常是文件中唯一的 `public` 类型。

**示例：**
```kotlin
val mainType = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "Application") {
    addModifier(JavaModifier.PUBLIC)
}
val javaFile = JavaFile("com.example", mainType)
```

---

#### secondaryTypes
```kotlin
val secondaryTypes: List<JavaTypeSpec>
```
次级类型列表，这些类型不能是 `public` 的，只有主类型可以是 `public`。

在 Java 中，一个源文件可以包含多个类定义，但只有一个可以是 public 的，且文件名必须与 public 类名一致。

**示例：**
```kotlin
val javaFile = JavaFile("com.example", mainType) {
    addSecondaryClass("Helper") {
        addModifier(JavaModifier.FINAL)
    }
    addSecondaryInterface("Callback")
}
```

生成：
```java
package com.example;

public class Application {
}

final class Helper {
}

interface Callback {
}
```

---

#### skipJavaLangImports
```kotlin
val skipJavaLangImports: Boolean
```
是否跳过 `java.lang` 包的导入。默认为 `true`。

设置为 `true` 时，`java.lang.String`、`java.lang.Integer` 等类型不会生成导入语句。这是因为 Java 编译器会自动导入 `java.lang` 包。

但在某些情况下（例如存在命名冲突），可能需要显式导入 `java.lang` 的类型。

**注意：** 虽然默认跳过导入，但在存在同名类的情况下，可能导致意外的类型引用。例如，如果存在 `com.example.String` 类，跳过 `java.lang.String` 的导入会导致引用到错误的类。

---

#### staticImports
```kotlin
val staticImports: Set<String>
```
静态导入集合，用于导入静态方法或常量。

**示例：**
```kotlin
JavaFile("com.example", typeSpec) {
    addStaticImport(ClassName("java.util", "Collections"), "emptyList", "emptyMap")
    addStaticImport("org.junit.Assert.assertEquals")
}
```

生成：
```java
package com.example;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
```

---

#### alwaysQualify
```kotlin
val alwaysQualify: Set<String>
```
总是使用全限定名的类型集合。对于这些类型，不会生成导入语句，而是在使用时直接使用完整的包名。

这在处理命名冲突或需要明确类型来源时很有用。

---

#### indent
```kotlin
val indent: String
```
缩进字符串，默认为 4 个空格 `"    "`。

**示例：**
```kotlin
JavaFile("com.example", typeSpec) {
    indent("\t") // 使用 Tab 缩进
}

JavaFile("com.example", typeSpec) {
    indent("  ") // 使用 2 个空格缩进
}
```

---

### 方法

#### writeTo
```kotlin
fun writeTo(out: Appendable, strategy: JavaWriteStrategy)
```
将文件内容写入到指定的 `Appendable` 对象中。

**参数：**
- `out`: 输出目标，如 `StringBuilder`、`Writer` 等
- `strategy`: 写入策略，用于控制代码格式和风格

**示例：**
```kotlin
val output = StringBuilder()
javaFile.writeTo(output, JavaWriteStrategy)
println(output.toString())
```

---

## JavaFileBuilder 构建器

`JavaFileBuilder` 是用于构建 `JavaFile` 的构建器类。

### 创建构建器

```kotlin
companion object {
    fun builder(packageName: PackageName, type: JavaTypeSpec): JavaFileBuilder
}
```

**示例：**
```kotlin
val builder = JavaFile.builder(
    packageName = "com.example".parseToPackageName(),
    type = typeSpec
)
```

---

### 构建器方法

#### addFileComment
```kotlin
// 方式 1：格式化字符串
fun addFileComment(format: String, vararg argumentParts: CodeArgumentPart): JavaFileBuilder

// 方式 2：CodeValue
fun addFileComment(codeValue: CodeValue): JavaFileBuilder

// 方式 3：DSL（扩展函数）
inline fun addFileComment(format: String, block: CodeValueSingleFormatBuilderDsl): JavaFileBuilder
```

添加文件注释。可以多次调用，每次添加的内容会追加到现有注释后。

**示例：**
```kotlin
JavaFile.builder(packageName, typeSpec)
    .addFileComment("Copyright (C) 2025 Example Corp.")
    .addFileComment("Author: %L", "John Doe")
    .build()
```

生成：
```java
// Copyright (C) 2025 Example Corp.
// Author: John Doe
```

---

#### addStaticImport
```kotlin
// 方式 1：直接指定完整路径
fun addStaticImport(import: String): JavaFileBuilder

// 方式 2：通过类名和成员名
fun addStaticImport(className: ClassName, vararg names: String): JavaFileBuilder
fun addStaticImport(className: ClassName, names: Iterable<String>): JavaFileBuilder
```

添加静态导入。

**示例：**
```kotlin
// 方式 1
builder.addStaticImport("java.util.Collections.emptyList")

// 方式 2
builder.addStaticImport(
    ClassName("java.util", "Collections"),
    "emptyList", "emptyMap", "singleton"
)
```

**注意：** `names` 参数不能为空，否则会抛出 `IllegalArgumentException`。

---

#### skipJavaLangImports
```kotlin
fun skipJavaLangImports(skipJavaLangImports: Boolean): JavaFileBuilder
```

设置是否跳过 `java.lang` 包的导入。

**示例：**
```kotlin
builder.skipJavaLangImports(false) // 不跳过，显式导入 java.lang 类型
```

---

#### indent
```kotlin
fun indent(indent: String): JavaFileBuilder
```

设置缩进字符串。

**示例：**
```kotlin
builder.indent("\t")    // Tab 缩进
builder.indent("  ")    // 2 空格缩进
builder.indent("    ")  // 4 空格缩进（默认）
```

---

#### addSecondaryType
```kotlin
fun addSecondaryType(type: JavaTypeSpec): JavaFileBuilder
fun addSecondaryTypes(types: Iterable<JavaTypeSpec>): JavaFileBuilder
fun addSecondaryTypes(vararg types: JavaTypeSpec): JavaFileBuilder
```

添加次级类型。

**示例：**
```kotlin
val helper = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "Helper")
val util = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "Util")

// 单个添加
builder.addSecondaryType(helper)

// 批量添加（varargs）
builder.addSecondaryTypes(helper, util)

// 批量添加（集合）
builder.addSecondaryTypes(listOf(helper, util))
```

---

#### build
```kotlin
fun build(): JavaFile
```

构建 `JavaFile` 实例。

**示例：**
```kotlin
val javaFile = JavaFile.builder(packageName, typeSpec)
    .addFileComment("Generated code")
    .addStaticImport(ClassName("java.util", "Collections"), "emptyList")
    .indent("\t")
    .build()
```

---

## DSL 扩展函数

### JavaFile 工厂函数

```kotlin
// 使用 PackageName
inline fun JavaFile(
    packageName: PackageName,
    type: JavaTypeSpec,
    block: JavaFileBuilder.() -> Unit = {}
): JavaFile

// 使用字符串（自动解析为 PackageName）
inline fun JavaFile(
    packageNamePaths: String,
    type: JavaTypeSpec,
    block: JavaFileBuilder.() -> Unit = {}
): JavaFile
```

**示例：**
```kotlin
// 方式 1
val javaFile = JavaFile(
    packageName = "com.example".parseToPackageName(),
    type = typeSpec
) {
    addFileComment("Generated code")
    skipJavaLangImports(false)
}

// 方式 2（自动解析）
val javaFile = JavaFile("com.example", typeSpec) {
    addFileComment("Generated code")
}
```

---

### 次级类型 DSL

为了更方便地添加次级类型，提供了一系列扩展函数：

#### addSecondaryClass
```kotlin
inline fun JavaFileBuilder.addSecondaryClass(
    name: String,
    block: JavaSimpleTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```

添加次级类。

**示例：**
```kotlin
JavaFile("com.example", mainType) {
    addSecondaryClass("Helper") {
        addModifier(JavaModifier.FINAL)
        addField(JavaFieldSpec(ClassName.String.ref(), "name"))
    }
}
```

---

#### addSecondaryInterface
```kotlin
inline fun JavaFileBuilder.addSecondaryInterface(
    name: String,
    block: JavaSimpleTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```

添加次级接口。

**示例：**
```kotlin
JavaFile("com.example", mainType) {
    addSecondaryInterface("Callback") {
        addMethod(JavaMethodSpec("onComplete"))
    }
}
```

---

#### addSecondaryEnum
```kotlin
inline fun JavaFileBuilder.addSecondaryEnum(
    name: String,
    block: JavaEnumTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```

添加次级枚举类。

**示例：**
```kotlin
JavaFile("com.example", mainType) {
    addSecondaryEnum("Status") {
        addEnumConstant("SUCCESS")
        addEnumConstant("FAILURE")
    }
}
```

---

#### addSecondaryAnnotationType
```kotlin
inline fun JavaFileBuilder.addSecondaryAnnotationType(
    name: String,
    block: JavaAnnotationTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```

添加次级注解类型。

**示例：**
```kotlin
JavaFile("com.example", mainType) {
    addSecondaryAnnotationType("Internal") {
        addMethod(JavaMethodSpec("value") {
            returnType = ClassName.String.ref()
        })
    }
}
```

---

#### addSecondaryRecord
```kotlin
inline fun JavaFileBuilder.addSecondaryRecord(
    name: String,
    block: JavaRecordTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```

添加次级 Record 类型（Java 14+）。

**示例：**
```kotlin
JavaFile("com.example", mainType) {
    addSecondaryRecord("Point") {
        addComponent(JavaParameterSpec(ClassName.Int.ref(), "x"))
        addComponent(JavaParameterSpec(ClassName.Int.ref(), "y"))
    }
}
```

---

#### addSecondarySealedClass
```kotlin
inline fun JavaFileBuilder.addSecondarySealedClass(
    name: String,
    block: JavaSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```

添加次级密封类（Java 15+）。

**示例：**
```kotlin
JavaFile("com.example", mainType) {
    addSecondarySealedClass("Shape") {
        addPermittedSubclass(ClassName("com.example", "Circle"))
        addPermittedSubclass(ClassName("com.example", "Rectangle"))
    }
}
```

---

#### addSecondarySealedInterface
```kotlin
inline fun JavaFileBuilder.addSecondarySealedInterface(
    name: String,
    block: JavaSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```

添加次级密封接口（Java 15+）。

---

#### addSecondaryNonSealedClass
```kotlin
inline fun JavaFileBuilder.addSecondaryNonSealedClass(
    name: String,
    block: JavaNonSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```

添加次级非密封类（Java 15+）。

---

#### addSecondaryNonSealedInterface
```kotlin
inline fun JavaFileBuilder.addSecondaryNonSealedInterface(
    name: String,
    block: JavaNonSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```

添加次级非密封接口（Java 15+）。

---

## 工具函数

### toRelativePath

```kotlin
fun JavaFile.toRelativePath(
    filename: String = type.name ?: "",
    separator: String = "/"
): String
```

将 `JavaFile` 转换为相对路径字符串。路径由包名路径和文件名组成，用指定的分隔符连接。

**参数：**
- `filename`: 文件名。如果未提供，默认使用主类型的名称。如果文件名不包含扩展名，会自动添加 `.java`。
- `separator`: 路径分隔符，默认为 `"/"`。

**返回值：** 表示此 `JavaFile` 相对路径的字符串。

**示例：**
```kotlin
val javaFile = JavaFile("com.example.demo", typeSpec)

// 使用默认文件名（类型名称）
val path1 = javaFile.toRelativePath()
// 结果: "com/example/demo/TypeName.java"

// 指定文件名
val path2 = javaFile.toRelativePath("CustomName")
// 结果: "com/example/demo/CustomName.java"

// 自定义分隔符
val path3 = javaFile.toRelativePath(separator = "\\")
// 结果: "com\example\demo\TypeName.java"

// 包含扩展名的文件名（不会重复添加 .java）
val path4 = javaFile.toRelativePath("MyClass.java")
// 结果: "com/example/demo/MyClass.java"
```

---

## JVM 平台扩展

在 JVM 平台上，`JavaFile` 提供了额外的扩展函数用于文件输出和 APT 集成。

### writeTo (Path)

```kotlin
@JvmOverloads
fun JavaFile.writeTo(
    directory: Path,
    charset: Charset = Charsets.UTF_8,
    strategy: JavaWriteStrategy = JavaWriteStrategy
)
```

将 `JavaFile` 写入到指定目录的文件中。

**参数：**
- `directory`: 目标目录路径（必须是目录或不存在）
- `charset`: 字符编码，默认 UTF-8
- `strategy`: 写入策略

**行为：**
1. 根据包名自动创建子目录结构
2. 文件名为 `{类型名}.java`
3. 如果目录不存在，自动创建

**示例：**
```kotlin
val outputDir = Paths.get("src/main/java")
javaFile.writeTo(outputDir)

// 使用 GBK 编码
javaFile.writeTo(outputDir, charset = Charset.forName("GBK"))
```

假设包名为 `com.example`，类型名为 `HelloWorld`，则会创建：
```
src/main/java/com/example/HelloWorld.java
```

---

### writeTo (File)

```kotlin
fun JavaFile.writeTo(
    directory: File,
    charset: Charset = Charsets.UTF_8,
    strategy: JavaWriteStrategy = JavaWriteStrategy
)
```

与 `writeTo(Path)` 功能相同，只是参数类型为 `File`。

**示例：**
```kotlin
val outputDir = File("src/main/java")
javaFile.writeTo(outputDir)
```

---

### toJavaFileObject

```kotlin
fun JavaFile.toJavaFileObject(): JavaFileObject
```

将 `JavaFile` 转换为 `JavaFileObject`，用于编译器 API 或注解处理器。

**返回值：** 一个 `SimpleJavaFileObject` 实例。

**示例：**
```kotlin
val fileObject = javaFile.toJavaFileObject()

// 获取源代码内容
val content = fileObject.getCharContent(false)

// 获取 URI
val uri = fileObject.toUri()
// 结果: "com/example/HelloWorld.java"
```

---

### writeTo (Filer)

```kotlin
// 方式 1：varargs
fun JavaFile.writeTo(
    filer: Filer,
    strategy: JavaWriteStrategy = JavaWriteStrategy,
    vararg originatingElements: Element
)

// 方式 2：Iterable
fun JavaFile.writeTo(
    filer: Filer,
    strategy: JavaWriteStrategy = JavaWriteStrategy,
    originatingElements: Iterable<Element>
)
```

将 `JavaFile` 写入到 APT (Annotation Processing Tool) 的 `Filer` 中。这是在注解处理器中生成代码的标准方式。

**参数：**
- `filer`: APT 的 `Filer` 实例
- `strategy`: 写入策略
- `originatingElements`: 触发生成的源元素（用于增量编译）

**行为：**
- 如果写入失败，会尝试删除已创建的文件
- 文件名格式：`{包名}.{类型名}`

**示例（在注解处理器中）：**
```kotlin
@SupportedAnnotationTypes("com.example.MyAnnotation")
class MyProcessor : AbstractProcessor() {
    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(MyAnnotation::class.java)

        for (element in elements) {
            val javaFile = generateJavaFile(element)

            // 写入到 Filer
            javaFile.writeTo(
                filer = processingEnv.filer,
                originatingElements = element
            )
        }

        return true
    }
}
```

---

## 完整示例

### 示例 1：基础用法

```kotlin
import love.forte.codegentle.java.*
import love.forte.codegentle.common.naming.*

fun main() {
    val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "HelloWorld") {
        addModifier(JavaModifier.PUBLIC)
        addModifier(JavaModifier.FINAL)

        addMethod(JavaMethodSpec("main") {
            addModifier(JavaModifier.PUBLIC)
            addModifier(JavaModifier.STATIC)
            addParameter(JavaParameterSpec(
                ClassName.String.ref().asArray(),
                "args"
            ))
            addCode("System.out.println(%S);", "Hello, World!")
        })
    }

    val javaFile = JavaFile("com.example", typeSpec) {
        addFileComment("Generated code - DO NOT EDIT")
    }

    println(javaFile)
}
```

**输出：**
```java
// Generated code - DO NOT EDIT
package com.example;

public final class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

---

### 示例 2：带导入和次级类型

```kotlin
import love.forte.codegentle.java.*
import love.forte.codegentle.common.naming.*

fun main() {
    val mainClass = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "DataProcessor") {
        addModifier(JavaModifier.PUBLIC)

        addField(JavaFieldSpec(
            ClassName("java.util", "List").parameterized(ClassName.String),
            "data"
        ) {
            addModifier(JavaModifier.PRIVATE)
            addModifier(JavaModifier.FINAL)
            initializer = CodeValue("new %T()", ClassName("java.util", "ArrayList"))
        })

        addMethod(JavaMethodSpec("process") {
            addModifier(JavaModifier.PUBLIC)
            addCode("return Result.success(data);")
            returnType = ClassName.ref("Result")
        })
    }

    val javaFile = JavaFile("com.example", mainClass) {
        addFileComment("Copyright (C) 2025 Example Corp.")
        addFileComment("All rights reserved.")

        addStaticImport(ClassName("java.util", "Collections"), "emptyList")

        // 添加次级类
        addSecondaryClass("Result") {
            addModifier(JavaModifier.FINAL)

            addField(JavaFieldSpec(ClassName.Boolean.ref(), "success") {
                addModifier(JavaModifier.PRIVATE)
                addModifier(JavaModifier.FINAL)
            })

            addField(JavaFieldSpec(ClassName.Object.ref(), "data") {
                addModifier(JavaModifier.PRIVATE)
                addModifier(JavaModifier.FINAL)
            })

            addMethod(JavaMethodSpec("success") {
                addModifier(JavaModifier.STATIC)
                addParameter(JavaParameterSpec(ClassName.Object.ref(), "data"))
                addCode("return new Result(true, data);")
                returnType = ClassName.ref("Result")
            })
        }
    }

    println(javaFile)
}
```

**输出：**
```java
// Copyright (C) 2025 Example Corp.
// All rights reserved.
package com.example;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;

public class DataProcessor {
    private final List<String> data = new ArrayList();

    public Result process() {
        return Result.success(data);
    }
}

final class Result {
    private final Boolean success;
    private final Object data;

    static Result success(Object data) {
        return new Result(true, data);
    }
}
```

---

### 示例 3：注解处理器中使用

```kotlin
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.example.GenerateBuilder")
class BuilderProcessor : AbstractProcessor() {

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val elements = roundEnv.getElementsAnnotatedWith(
            GenerateBuilder::class.java
        )

        for (element in elements) {
            val className = element.simpleName.toString()
            val packageName = processingEnv.elementUtils
                .getPackageOf(element)
                .qualifiedName
                .toString()

            // 生成 Builder 类
            val builderClass = JavaSimpleTypeSpec(
                JavaTypeSpec.Kind.CLASS,
                "${className}Builder"
            ) {
                addModifier(JavaModifier.PUBLIC)
                addModifier(JavaModifier.FINAL)

                // 添加 build 方法
                addMethod(JavaMethodSpec("build") {
                    addModifier(JavaModifier.PUBLIC)
                    addCode("return new %T();", ClassName(packageName, className))
                    returnType = ClassName(packageName, className).ref()
                })
            }

            val javaFile = JavaFile(packageName, builderClass) {
                addFileComment("Generated by BuilderProcessor")
                addFileComment("DO NOT EDIT THIS FILE MANUALLY")
            }

            // 写入到 Filer
            javaFile.writeTo(
                filer = processingEnv.filer,
                originatingElements = element
            )
        }

        return true
    }
}
```

---

### 示例 4：生成枚举和接口

```kotlin
fun generateApiDefinition() {
    val statusEnum = JavaEnumTypeSpec("ApiStatus") {
        addEnumConstant("SUCCESS")
        addEnumConstant("ERROR")
        addEnumConstant("PENDING")
    }

    val responseInterface = JavaSimpleTypeSpec(
        JavaTypeSpec.Kind.INTERFACE,
        "ApiResponse"
    ) {
        addModifier(JavaModifier.PUBLIC)

        addMethod(JavaMethodSpec("getStatus") {
            returnType = ClassName.ref("ApiStatus")
        })

        addMethod(JavaMethodSpec("getData") {
            returnType = ClassName.Object.ref()
        })
    }

    val javaFile = JavaFile("com.example.api", responseInterface) {
        addFileComment("API Response Definition")
        addSecondaryEnum("ApiStatus") {
            addEnumConstant("SUCCESS")
            addEnumConstant("ERROR")
            addEnumConstant("PENDING")
        }
    }

    println(javaFile)
}
```

**输出：**
```java
// API Response Definition
package com.example.api;

public interface ApiResponse {
    ApiStatus getStatus();

    Object getData();
}

enum ApiStatus {
    SUCCESS,
    ERROR,
    PENDING
}
```

---

### 示例 5：Record 类型（Java 14+）

```kotlin
fun generateRecordFile() {
    val personRecord = JavaRecordTypeSpec("Person") {
        addModifier(JavaModifier.PUBLIC)

        // 添加组件
        addComponent(JavaParameterSpec(ClassName.String.ref(), "name"))
        addComponent(JavaParameterSpec(ClassName.Int.ref(), "age"))
        addComponent(JavaParameterSpec(ClassName.String.ref(), "email"))

        // 添加额外方法
        addMethod(JavaMethodSpec("isAdult") {
            addModifier(JavaModifier.PUBLIC)
            addCode("return age >= 18;")
            returnType = ClassName.Boolean.ref()
        })
    }

    val javaFile = JavaFile("com.example.model", personRecord) {
        addFileComment("Person data model")
    }

    println(javaFile)
}
```

**输出：**
```java
// Person data model
package com.example.model;

public record Person(String name, int age, String email) {
    public Boolean isAdult() {
        return age >= 18;
    }
}
```

---

### 示例 6：密封类（Java 15+）

```kotlin
fun generateSealedClassFile() {
    val shapeClass = JavaSealedTypeSpec(
        JavaTypeSpec.Kind.SEALED_CLASS,
        "Shape"
    ) {
        addModifier(JavaModifier.PUBLIC)
        addModifier(JavaModifier.ABSTRACT)

        addPermittedSubclass(ClassName("com.example", "Circle"))
        addPermittedSubclass(ClassName("com.example", "Rectangle"))

        addMethod(JavaMethodSpec("area") {
            addModifier(JavaModifier.PUBLIC)
            addModifier(JavaModifier.ABSTRACT)
            returnType = ClassName.Double.ref()
        })
    }

    val javaFile = JavaFile("com.example", shapeClass) {
        addFileComment("Shape hierarchy")

        // 添加实现类
        addSecondaryNonSealedClass("Circle") {
            addModifier(JavaModifier.FINAL)
            superclass = ClassName.ref("Shape")

            addField(JavaFieldSpec(ClassName.Double.ref(), "radius") {
                addModifier(JavaModifier.PRIVATE)
                addModifier(JavaModifier.FINAL)
            })

            addMethod(JavaMethodSpec("area") {
                addModifier(JavaModifier.PUBLIC)
                addAnnotation(ClassName("java.lang", "Override").ref())
                addCode("return Math.PI * radius * radius;")
                returnType = ClassName.Double.ref()
            })
        }

        addSecondaryNonSealedClass("Rectangle") {
            addModifier(JavaModifier.FINAL)
            superclass = ClassName.ref("Shape")

            addField(JavaFieldSpec(ClassName.Double.ref(), "width") {
                addModifier(JavaModifier.PRIVATE)
                addModifier(JavaModifier.FINAL)
            })

            addField(JavaFieldSpec(ClassName.Double.ref(), "height") {
                addModifier(JavaModifier.PRIVATE)
                addModifier(JavaModifier.FINAL)
            })

            addMethod(JavaMethodSpec("area") {
                addModifier(JavaModifier.PUBLIC)
                addAnnotation(ClassName("java.lang", "Override").ref())
                addCode("return width * height;")
                returnType = ClassName.Double.ref()
            })
        }
    }

    println(javaFile)
}
```

**输出：**
```java
// Shape hierarchy
package com.example;

public abstract sealed class Shape permits Circle, Rectangle {
    public abstract Double area();
}

final non-sealed class Circle extends Shape {
    private final Double radius;

    @Override
    public Double area() {
        return Math.PI * radius * radius;
    }
}

final non-sealed class Rectangle extends Shape {
    private final Double width;
    private final Double height;

    @Override
    public Double area() {
        return width * height;
    }
}
```

---

## 最佳实践

### 1. 文件注释

始终添加文件注释，说明代码的生成方式和注意事项：

```kotlin
JavaFile(packageName, typeSpec) {
    addFileComment("This file is auto-generated by %L", generatorName)
    addFileComment("DO NOT EDIT THIS FILE MANUALLY")
    addFileComment("Generated at: %L", timestamp)
}
```

---

### 2. 静态导入

谨慎使用静态导入，只导入常用的工具方法：

```kotlin
JavaFile(packageName, typeSpec) {
    // 好的实践：导入常用工具方法
    addStaticImport(ClassName("java.util", "Collections"), "emptyList", "emptyMap")
    addStaticImport(ClassName("java.lang", "Math"), "PI", "E")

    // 避免：导入过多方法，降低代码可读性
    // addStaticImport(ClassName("java.util", "Collections"), "*")
}
```

---

### 3. 次级类型

使用次级类型来组织相关的辅助类，但避免过度使用：

```kotlin
JavaFile(packageName, mainClass) {
    // 好的实践：添加紧密相关的辅助类
    addSecondaryClass("Builder") {
        // Builder 模式实现
    }

    addSecondaryEnum("Status") {
        // 状态枚举
    }

    // 避免：添加不相关的类，应该分离到独立文件
}
```

---

### 4. 命名冲突处理

当存在命名冲突时，使用 `alwaysQualify` 或显式导入：

```kotlin
// 场景：同时使用 java.util.Date 和 java.sql.Date
val javaFile = JavaFile.builder(packageName, typeSpec)
    .apply {
        // 方式 1：总是使用全限定名
        // alwaysQualify.add("Date")

        // 方式 2：只导入一个，另一个使用全限定名
        // 在代码中使用 java.sql.Date，而 java.util.Date 会被导入
    }
    .build()
```

---

### 5. 包名管理

使用 `PackageName` API 来处理包名，而不是字符串拼接：

```kotlin
// 好的实践
val basePackage = "com.example".parseToPackageName()
val subPackage = basePackage.append("api", "v1")

val javaFile = JavaFile(subPackage, typeSpec)

// 避免
val packageName = "com.example" + ".api.v1"  // 容易出错
```

---

### 6. 输出文件

在不同场景下选择合适的输出方式：

```kotlin
// APT 注解处理器 → 使用 Filer
javaFile.writeTo(processingEnv.filer, originatingElements = element)

// 构建工具插件 → 使用 Path/File
javaFile.writeTo(Paths.get("src/main/java"))

// 测试/调试 → 使用 toString
println(javaFile.toString())

// 自定义处理 → 使用 Appendable
val output = StringBuilder()
javaFile.writeTo(output, customStrategy)
```

---

### 7. 代码格式化

根据项目要求设置缩进：

```kotlin
// Google Style（2 空格）
JavaFile(packageName, typeSpec) {
    indent("  ")
}

// Oracle/Sun Style（4 空格，默认）
JavaFile(packageName, typeSpec) {
    indent("    ")
}

// Tab 缩进
JavaFile(packageName, typeSpec) {
    indent("\t")
}
```

---

## 注意事项

1. **主类型修饰符**：主类型通常应该是 `public` 的，文件名也应该与主类型名一致。
2. **次级类型限制**：次级类型不能是 `public` 的，否则会导致 Java 编译错误。
3. **导入顺序**：生成的导入语句顺序是固定的，先静态导入，后普通导入。
4. **包名为空**：如果包名为空（默认包），不会生成 `package` 声明。
5. **文件编码**：在 JVM 平台上使用 `writeTo(Path/File)` 时，默认使用 UTF-8 编码。
6. **线程安全**：`JavaFile` 实例是不可变的，可以安全地在多线程环境中使用。
7. **equals 和 hashCode**：`JavaFile` 正确实现了 `equals` 和 `hashCode`，可以用于集合操作。

---

## 相关文档

- [JavaTypeSpec](../spec/JavaTypeSpec.md) - 类型定义
- [PackageName](../naming/PackageName.md) - 包名处理
- [ClassName](../naming/ClassName.md) - 类名处理
- [JavaWriteStrategy](../strategy/JavaWriteStrategy.md) - 写入策略
- [CodeValue](../code/CodeValue.md) - 代码片段
