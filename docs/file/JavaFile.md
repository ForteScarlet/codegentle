# JavaFile API Reference

The `JavaFile` interface and its associated builder provide a complete API for generating Java source files with full control over package declarations, imports, comments, and type definitions.

## Table of Contents

- [JavaFile Interface](#javafile-interface)
  - [Properties](#properties)
  - [Methods](#methods)
- [JavaFileBuilder](#javafilebuilder)
  - [Construction](#construction)
  - [Configuration Methods](#configuration-methods)
- [DSL Extensions](#dsl-extensions)
  - [File Creation Functions](#file-creation-functions)
  - [Secondary Type Extensions](#secondary-type-extensions)
- [Utility Functions](#utility-functions)
  - [toRelativePath](#torelativepath)
  - [Platform-Specific Write Methods](#platform-specific-write-methods)
- [Usage Examples](#usage-examples)

---

## JavaFile Interface

The `JavaFile` interface represents a complete Java source file.

### Properties

#### fileComment
```kotlin
val fileComment: CodeValue
```
File-level comments that appear at the top of the generated file, before the package declaration.

**Example:**
```kotlin
JavaFile(packageName, typeSpec) {
    addFileComment("This file is auto-generated. Do not modify.")
}
```

#### packageName
```kotlin
val packageName: PackageName
```
The package name for the Java file. Use `PackageName.EMPTY` or an empty string for the default package.

**Example:**
```kotlin
val packageName = "com.example.model".parseToPackageName()
```

#### type
```kotlin
val type: JavaTypeSpec
```
The primary type definition for this file. This is the main class, interface, enum, or other type that defines the file's name.

**Requirements:**
- The primary type should be `public` if intended for use outside the package
- The file name will be derived from this type's name

#### secondaryTypes
```kotlin
val secondaryTypes: List<JavaTypeSpec>
```
Additional type definitions included in the same file. These types are package-private (not public) and provide supporting types.

**Use Cases:**
- Helper classes
- Internal interfaces
- Private enums or records
- Utility types

**Constraints:**
- Secondary types cannot be `public` (only the primary type can be public)
- All types must be in the same package

#### skipJavaLangImports
```kotlin
val skipJavaLangImports: Boolean
```
**Note**: This property is currently stored but **not consulted** by the default implementation. Import behavior is controlled by `JavaWriteStrategy.omitJavaLangPackage()` instead.

**Default Strategy Behavior:**
- The default `JavaWriteStrategy` always omits `java.lang` imports
- To customize import behavior, provide a custom `JavaWriteStrategy` when calling `writeTo()`

**Rationale:**
By default, imports from `java.lang` are skipped to defend against naming conflicts. For example, if a class named `com.example.String` exists, skipping `java.lang` imports ensures references to `String` in `com.example` won't conflict with `java.lang.String`.

**Custom Strategy Example:**
```kotlin
val customStrategy = object : JavaWriteStrategy by DefaultJavaWriteStrategy() {
    override fun omitJavaLangPackage(): Boolean = false
}

javaFile.writeTo(output, customStrategy)
```

#### staticImports
```kotlin
val staticImports: Set<String>
```
Set of static import statements for static members (fields or methods).

**Format:** Fully qualified member names (e.g., `"java.util.Collections.emptyList"`)

#### alwaysQualify
```kotlin
val alwaysQualify: Set<String>
```
Set of class simple names that should always be fully qualified, never imported.

**Use Case:** Prevent ambiguity when multiple classes have the same simple name.

#### indent
```kotlin
val indent: String
```
The indentation string used for code formatting.

**Default:** `"    "` (4 spaces)

**Common Values:**
- `"    "` - 4 spaces
- `"  "` - 2 spaces
- `"\t"` - Tab character

### Methods

#### writeTo
```kotlin
fun writeTo(out: Appendable, strategy: JavaWriteStrategy)
```
Writes the Java file content to an `Appendable` output using the specified write strategy.

**Parameters:**
- `out` - The output destination (e.g., `StringBuilder`, `Writer`)
- `strategy` - Controls formatting and output behavior

**Example:**
```kotlin
val output = StringBuilder()
javaFile.writeTo(output, JavaWriteStrategy)
println(output.toString())
```

---

## JavaFileBuilder

The builder class for constructing `JavaFile` instances with a fluent API.

### Construction

#### builder
```kotlin
companion object {
    @JvmStatic
    fun builder(packageName: PackageName, type: JavaTypeSpec): JavaFileBuilder
}
```
Creates a new `JavaFileBuilder` instance.

**Parameters:**
- `packageName` - The package name for the file
- `type` - The primary type specification

**Example:**
```kotlin
val builder = JavaFile.builder("com.example".parseToPackageName(), typeSpec)
```

### Configuration Methods

#### addFileComment
```kotlin
fun addFileComment(format: String, vararg argumentParts: CodeArgumentPart): JavaFileBuilder
fun addFileComment(codeValue: CodeValue): JavaFileBuilder
```
Adds file-level comments at the top of the file.

**Parameters:**
- `format` - Comment text or format string
- `argumentParts` - Optional format arguments
- `codeValue` - Pre-constructed CodeValue for complex comments

**Example:**
```kotlin
JavaFile.builder(packageName, typeSpec)
    .addFileComment("Generated on: \$L", java.time.LocalDate.now())
    .build()
```

#### addStaticImport
```kotlin
fun addStaticImport(import: String): JavaFileBuilder
fun addStaticImport(className: ClassName, vararg names: String): JavaFileBuilder
fun addStaticImport(className: ClassName, names: Iterable<String>): JavaFileBuilder
```
Adds static imports for static members.

**Parameters:**
- `import` - Fully qualified member name (e.g., `"java.util.Collections.emptyList"`)
- `className` - The class containing the static members
- `names` - Names of static members to import

**Examples:**
```kotlin
// Single static import
builder.addStaticImport("java.lang.Math.PI")

// Multiple static imports from same class
builder.addStaticImport(
    ClassName("java.util", "Collections"),
    "emptyList",
    "emptyMap",
    "emptySet"
)

// From iterable
val members = listOf("min", "max", "abs")
builder.addStaticImport(ClassName("java.lang", "Math"), members)
```

#### skipJavaLangImports
```kotlin
fun skipJavaLangImports(skipJavaLangImports: Boolean): JavaFileBuilder
```
Configures whether to skip imports from the `java.lang` package.

**Default:** `true`

**Example:**
```kotlin
builder.skipJavaLangImports(false)
```

#### indent
```kotlin
fun indent(indent: String): JavaFileBuilder
```
Sets the indentation string for generated code.

**Default:** `"    "` (4 spaces)

**Example:**
```kotlin
// Use tabs
builder.indent("\t")

// Use 2 spaces
builder.indent("  ")
```

#### addSecondaryType
```kotlin
fun addSecondaryType(type: JavaTypeSpec): JavaFileBuilder
fun addSecondaryTypes(types: Iterable<JavaTypeSpec>): JavaFileBuilder
fun addSecondaryTypes(vararg types: JavaTypeSpec): JavaFileBuilder
```
Adds secondary (package-private) types to the file.

**Parameters:**
- `type` - A single type specification
- `types` - Multiple type specifications (iterable or varargs)

**Example:**
```kotlin
val helper = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "Helper")
val callback = JavaSimpleTypeSpec(JavaTypeSpec.Kind.INTERFACE, "Callback")

builder
    .addSecondaryType(helper)
    .addSecondaryTypes(callback)
```

#### build
```kotlin
fun build(): JavaFile
```
Constructs the final `JavaFile` instance.

**Returns:** An immutable `JavaFile` object

**Example:**
```kotlin
val javaFile = builder.build()
```

---

## DSL Extensions

Kotlin DSL functions for concise file creation.

### File Creation Functions

#### JavaFile
```kotlin
inline fun JavaFile(
    packageName: PackageName,
    type: JavaTypeSpec,
    block: JavaFileBuilder.() -> Unit = {}
): JavaFile

inline fun JavaFile(
    packageNamePaths: String,
    type: JavaTypeSpec,
    block: JavaFileBuilder.() -> Unit = {}
): JavaFile
```
Creates a `JavaFile` using DSL syntax.

**Parameters:**
- `packageName` - Package name as `PackageName` or `String`
- `type` - Primary type specification
- `block` - Builder configuration lambda

**Example:**
```kotlin
val javaFile = JavaFile("com.example", typeSpec) {
    addFileComment("Auto-generated")
    addStaticImport(ClassName("java.util", "Collections"), "emptyList")
    indent("\t")
}
```

#### addFileComment (DSL)
```kotlin
inline fun JavaFileBuilder.addFileComment(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): JavaFileBuilder
```
DSL extension for adding formatted file comments.

**Example:**
```kotlin
JavaFile(packageName, typeSpec) {
    addFileComment("Generated by: \$L") {
        argument("CodeGentle")
    }
}
```

### Secondary Type Extensions

#### addSecondaryClass
```kotlin
inline fun JavaFileBuilder.addSecondaryClass(
    name: String,
    block: JavaSimpleTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```
Adds a package-private class as a secondary type.

**Example:**
```kotlin
JavaFile(packageName, mainType) {
    addSecondaryClass("Helper") {
        addModifier(JavaModifier.FINAL)
        addMethod(JavaMethodSpec(name = "help") {
            addModifier(JavaModifier.STATIC)
            // method configuration
        })
    }
}
```

#### addSecondaryInterface
```kotlin
inline fun JavaFileBuilder.addSecondaryInterface(
    name: String,
    block: JavaSimpleTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```
Adds a package-private interface as a secondary type.

**Example:**
```kotlin
JavaFile(packageName, mainType) {
    addSecondaryInterface("Callback") {
        addMethod(JavaMethodSpec(name = "onComplete") {
            addModifier(JavaModifier.ABSTRACT)
        })
    }
}
```

#### addSecondaryEnum
```kotlin
inline fun JavaFileBuilder.addSecondaryEnum(
    name: String,
    block: JavaEnumTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```
Adds a package-private enum as a secondary type.

**Example:**
```kotlin
JavaFile(packageName, mainType) {
    addSecondaryEnum("Status") {
        addEnumConstant("SUCCESS")
        addEnumConstant("FAILURE")
    }
}
```

#### addSecondaryAnnotationType
```kotlin
inline fun JavaFileBuilder.addSecondaryAnnotationType(
    name: String,
    block: JavaAnnotationTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```
Adds a package-private annotation type as a secondary type.

**Example:**
```kotlin
JavaFile(packageName, mainType) {
    addSecondaryAnnotationType("Internal") {
        // annotation configuration
    }
}
```

#### addSecondaryRecord
```kotlin
inline fun JavaFileBuilder.addSecondaryRecord(
    name: String,
    block: JavaRecordTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```
Adds a package-private record as a secondary type (Java 14+).

**Example:**
```kotlin
JavaFile(packageName, mainType) {
    addSecondaryRecord("Point") {
        addComponent(JavaRecordComponentSpec(IntType.ref(), "x"))
        addComponent(JavaRecordComponentSpec(IntType.ref(), "y"))
    }
}
```

#### addSecondarySealedClass
```kotlin
inline fun JavaFileBuilder.addSecondarySealedClass(
    name: String,
    block: JavaSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```
Adds a package-private sealed class as a secondary type (Java 17+).

**Example:**
```kotlin
JavaFile(packageName, mainType) {
    addSecondarySealedClass("Shape") {
        addPermittedSubclass(ClassName("com.example", "Circle"))
        addPermittedSubclass(ClassName("com.example", "Square"))
    }
}
```

#### addSecondarySealedInterface
```kotlin
inline fun JavaFileBuilder.addSecondarySealedInterface(
    name: String,
    block: JavaSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```
Adds a package-private sealed interface as a secondary type (Java 17+).

#### addSecondaryNonSealedClass
```kotlin
inline fun JavaFileBuilder.addSecondaryNonSealedClass(
    name: String,
    block: JavaNonSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```
Adds a package-private non-sealed class as a secondary type (Java 17+).

#### addSecondaryNonSealedInterface
```kotlin
inline fun JavaFileBuilder.addSecondaryNonSealedInterface(
    name: String,
    block: JavaNonSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder
```
Adds a package-private non-sealed interface as a secondary type (Java 17+).

---

## Utility Functions

### toRelativePath

```kotlin
fun JavaFile.toRelativePath(filename: String = type.name ?: "", separator: String = "/"): String
```
Converts a `JavaFile` to a relative path string suitable for file system operations.

**Parameters:**
- `filename` - The file name (defaults to the primary type's name). Automatically appends `.java` if no extension is present
- `separator` - Path separator (defaults to `"/"`)

**Returns:** A relative path string combining package path and filename

**Examples:**
```kotlin
val javaFile = JavaFile("com.example".parseToPackageName(), typeSpec)

// Default separator
javaFile.toRelativePath()
// Output: "com/example/MyClass.java"

// Custom separator for Windows
javaFile.toRelativePath(separator = "\\")
// Output: "com\\example\\MyClass.java"

// Custom filename
javaFile.toRelativePath(filename = "CustomName")
// Output: "com/example/CustomName.java"

// Empty package
val defaultPackageFile = JavaFile(PackageName.EMPTY, typeSpec)
defaultPackageFile.toRelativePath()
// Output: "MyClass.java"
```

### Platform-Specific Write Methods

#### writeTo (Path)
```kotlin
@JvmOverloads
fun JavaFile.writeTo(
    directory: Path,
    charset: Charset = Charsets.UTF_8,
    strategy: JavaWriteStrategy = JavaWriteStrategy
)
```
Writes the Java file to a directory using Java NIO Path API.

**Parameters:**
- `directory` - Target directory path (must exist or be creatable)
- `charset` - Character encoding (default: UTF-8)
- `strategy` - Write strategy for formatting

**Behavior:**
- Creates package subdirectories automatically
- File name is derived from the primary type's name
- Throws `IOException` if directory is not valid

**Example:**
```kotlin
import java.nio.file.Paths

val outputDir = Paths.get("src/main/java")
javaFile.writeTo(outputDir)
```

#### writeTo (File)
```kotlin
fun JavaFile.writeTo(
    directory: File,
    charset: Charset = Charsets.UTF_8,
    strategy: JavaWriteStrategy = JavaWriteStrategy
)
```
Writes the Java file to a directory using `java.io.File` API.

**Example:**
```kotlin
import java.io.File

val outputDir = File("src/main/java")
javaFile.writeTo(outputDir)
```

#### writeTo (Filer) - APT Support
```kotlin
fun JavaFile.writeTo(
    filer: Filer,
    strategy: JavaWriteStrategy = JavaWriteStrategy,
    vararg originatingElements: Element
)

fun JavaFile.writeTo(
    filer: Filer,
    strategy: JavaWriteStrategy = JavaWriteStrategy,
    originatingElements: Iterable<Element>
)
```
Writes the Java file using the Annotation Processing Tool (APT) `Filer` API.

**Parameters:**
- `filer` - The APT Filer instance
- `strategy` - Write strategy for formatting
- `originatingElements` - Source elements that caused this file to be generated

**Use Case:** Generating source files during annotation processing

**Example:**
```kotlin
// In an annotation processor
override fun process(
    annotations: Set<TypeElement>,
    roundEnv: RoundEnvironment
): Boolean {
    for (element in roundEnv.getElementsAnnotatedWith(MyAnnotation::class.java)) {
        val javaFile = generateJavaFile(element)
        javaFile.writeTo(processingEnv.filer, originatingElements = listOf(element))
    }
    return true
}
```

#### toJavaFileObject
```kotlin
fun JavaFile.toJavaFileObject(): JavaFileObject
```
Converts the `JavaFile` to a `JavaFileObject` for use with the Java Compiler API.

**Returns:** A `SimpleJavaFileObject` implementation

**Example:**
```kotlin
val fileObject = javaFile.toJavaFileObject()
// Use with JavaCompiler
```

---

## Usage Examples

### Example 1: Simple Java Class File

```kotlin
import love.forte.codegentle.java.*
import love.forte.codegentle.common.naming.*

val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "Person") {
    addModifier(JavaModifier.PUBLIC)

    addField(JavaFieldSpec(StringType.ref(), "name") {
        addModifier(JavaModifier.PRIVATE)
    })

    addMethod(JavaMethodSpec(name = "getName") {
        addModifier(JavaModifier.PUBLIC)
        returns(StringType.ref())
        addCode("return this.name;")
    })
}

val javaFile = JavaFile("com.example.model", typeSpec)

// Output:
// package com.example.model;
//
// public class Person {
//     private String name;
//
//     public String getName() {
//         return this.name;
//     }
// }
```

### Example 2: File with Comments and Static Imports

```kotlin
val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "CollectionUtils") {
    addModifier(JavaModifier.PUBLIC)
    addModifier(JavaModifier.FINAL)
}

val javaFile = JavaFile("com.example.util", typeSpec) {
    addFileComment("""
        Utility class for collection operations.

        @author CodeGentle
        @version 1.0
    """.trimIndent())

    addStaticImport(
        ClassName("java.util", "Collections"),
        "emptyList",
        "emptyMap",
        "emptySet"
    )

    addStaticImport("java.util.Objects.requireNonNull")
}

// Output:
// // Utility class for collection operations.
// //
// // @author CodeGentle
// // @version 1.0
// package com.example.util;
//
// import static java.util.Collections.emptyList;
// import static java.util.Collections.emptyMap;
// import static java.util.Collections.emptySet;
// import static java.util.Objects.requireNonNull;
//
// public final class CollectionUtils {
// }
```

### Example 3: File with Secondary Types

```kotlin
val mainType = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "UserService") {
    addModifier(JavaModifier.PUBLIC)

    addField(JavaFieldSpec(
        ClassName("com.example", "UserRepository").ref(),
        "repository"
    ) {
        addModifier(JavaModifier.PRIVATE)
        addModifier(JavaModifier.FINAL)
    })
}

val javaFile = JavaFile("com.example.service", mainType) {
    // Add helper class
    addSecondaryClass("UserValidator") {
        addMethod(JavaMethodSpec(name = "validate") {
            addModifier(JavaModifier.STATIC)
            addParameter(JavaParameterSpec(StringType.ref(), "username"))
            returns(BooleanType.ref())
            addCode("return username != null && !username.isEmpty();")
        })
    }

    // Add callback interface
    addSecondaryInterface("UserCallback") {
        addMethod(JavaMethodSpec(name = "onUserCreated") {
            addParameter(JavaParameterSpec(
                ClassName("com.example.model", "User").ref(),
                "user"
            ))
        })
    }

    // Add status enum
    addSecondaryEnum("UserStatus") {
        addEnumConstant("ACTIVE")
        addEnumConstant("INACTIVE")
        addEnumConstant("PENDING")
    }
}

// Output:
// package com.example.service;
//
// import com.example.UserRepository;
// import com.example.model.User;
//
// public class UserService {
//     private final UserRepository repository;
// }
//
// class UserValidator {
//     static boolean validate(String username) {
//         return username != null && !username.isEmpty();
//     }
// }
//
// interface UserCallback {
//     void onUserCreated(User user);
// }
//
// enum UserStatus {
//     ACTIVE,
//     INACTIVE,
//     PENDING
// }
```

### Example 4: Custom Indentation

```kotlin
val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "Example") {
    addModifier(JavaModifier.PUBLIC)
}

val javaFile = JavaFile("com.example", typeSpec) {
    indent("\t") // Use tabs instead of spaces
}
```

### Example 5: Controlling java.lang Imports

```kotlin
val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "StringWrapper") {
    addModifier(JavaModifier.PUBLIC)

    addField(JavaFieldSpec(
        ClassName("java.lang", "String").ref(),
        "value"
    ) {
        addModifier(JavaModifier.PRIVATE)
    })
}

// Skip java.lang imports (default)
val file1 = JavaFile("com.example", typeSpec) {
    skipJavaLangImports(true)
}
// Output: No import for java.lang.String

// Include java.lang imports
val file2 = JavaFile("com.example", typeSpec) {
    skipJavaLangImports(false)
}
// Output: import java.lang.String;
```

### Example 6: Writing to File System

```kotlin
import java.nio.file.Paths

val javaFile = JavaFile("com.example", typeSpec)

// Write to directory
val srcDir = Paths.get("src/main/java")
javaFile.writeTo(srcDir)
// Creates: src/main/java/com/example/MyClass.java

// Get relative path
val relativePath = javaFile.toRelativePath()
println(relativePath) // "com/example/MyClass.java"
```

### Example 7: Advanced - Record with Secondary Types (Java 14+)

```kotlin
val recordSpec = JavaRecordTypeSpec("Point") {
    addModifier(JavaModifier.PUBLIC)

    addComponent(JavaRecordComponentSpec(IntType.ref(), "x"))
    addComponent(JavaRecordComponentSpec(IntType.ref(), "y"))

    addMethod(JavaMethodSpec(name = "distance") {
        addModifier(JavaModifier.PUBLIC)
        returns(DoubleType.ref())
        addCode("return Math.sqrt(x * x + y * y);")
    })
}

val javaFile = JavaFile("com.example.geometry", recordSpec) {
    addFileComment("Geometric point representation")

    addStaticImport("java.lang.Math.sqrt")

    addSecondaryRecord("Point3D") {
        addComponent(JavaRecordComponentSpec(IntType.ref(), "x"))
        addComponent(JavaRecordComponentSpec(IntType.ref(), "y"))
        addComponent(JavaRecordComponentSpec(IntType.ref(), "z"))
    }
}

// Output:
// // Geometric point representation
// package com.example.geometry;
//
// import static java.lang.Math.sqrt;
//
// public record Point(int x, int y) {
//     public double distance() {
//         return Math.sqrt(x * x + y * y);
//     }
// }
//
// record Point3D(int x, int y, int z) {
// }
```

---

## Best Practices

### 1. Always Use Public for Primary Type
The primary type should be `public` if it's intended to be accessible outside the package:
```kotlin
val mainType = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MyClass") {
    addModifier(JavaModifier.PUBLIC) // Good
}
```

### 2. Secondary Types are Package-Private
Do not make secondary types public:
```kotlin
// Good
addSecondaryClass("Helper") {
    // No public modifier - automatically package-private
}

// Bad - will cause compilation issues
addSecondaryClass("Helper") {
    addModifier(JavaModifier.PUBLIC) // Don't do this!
}
```

### 3. Use Static Imports Judiciously
Static imports improve readability for commonly used constants and utility methods:
```kotlin
JavaFile(packageName, typeSpec) {
    // Good for commonly used utilities
    addStaticImport(ClassName("java.util", "Collections"), "emptyList")
    addStaticImport("java.lang.Math.PI")
}
```

### 4. Add File Comments for Generated Code
Help users understand the origin of generated files:
```kotlin
JavaFile(packageName, typeSpec) {
    addFileComment("""
        This file is auto-generated by CodeGentle.
        DO NOT MODIFY - Changes will be overwritten.

        Generated on: ${java.time.LocalDateTime.now()}
    """.trimIndent())
}
```

### 5. Use Consistent Indentation
Choose one indentation style and use it consistently:
```kotlin
// Prefer spaces for better cross-platform compatibility
JavaFile(packageName, typeSpec) {
    indent("    ") // 4 spaces (default)
}
```

### 6. Organize Secondary Types Logically
Group related secondary types together:
```kotlin
JavaFile(packageName, mainType) {
    // Interfaces first
    addSecondaryInterface("Callback")
    addSecondaryInterface("Validator")

    // Then classes
    addSecondaryClass("Helper")
    addSecondaryClass("Builder")

    // Finally enums
    addSecondaryEnum("Status")
}
```

---

## Common Pitfalls

### 1. Forgetting Package Name Parsing
```kotlin
// Wrong - String is not PackageName
// val javaFile = JavaFile("com.example", typeSpec) // Type mismatch

// Correct
val javaFile = JavaFile("com.example".parseToPackageName(), typeSpec)

// Or use string overload
val javaFile = JavaFile("com.example", typeSpec)
```

### 2. Multiple Public Types
```kotlin
// Wrong - causes compilation error
val mainType = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "Main") {
    addModifier(JavaModifier.PUBLIC)
}

val javaFile = JavaFile(packageName, mainType) {
    addSecondaryClass("Other") {
        addModifier(JavaModifier.PUBLIC) // Error: only one public type per file
    }
}
```

### 3. Incorrect Static Import Format
```kotlin
// Wrong - missing class name
addStaticImport("emptyList")

// Correct - fully qualified
addStaticImport("java.util.Collections.emptyList")

// Or use ClassName
addStaticImport(ClassName("java.util", "Collections"), "emptyList")
```

---

## Related APIs

- **[JavaTypeSpec](../spec/JavaTypeSpec.md)** - Type definitions
- **[PackageName](../naming/PackageName.md)** - Package naming
- **[CodeValue](../code/CodeValue.md)** - Code generation
- **[JavaWriteStrategy](../strategy/JavaWriteStrategy.md)** - Output formatting

---

## See Also

- [Type Specifications Overview](../spec/README.md)
- [Naming Conventions](../naming/README.md)
- [Code Generation Basics](../code/README.md)
