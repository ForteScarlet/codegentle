# KotlinFile API Documentation

## Overview

`KotlinFile` represents a Kotlin source file and provides a fluent API for generating Kotlin code programmatically. A Kotlin source file can contain one or more top-level classes, interfaces, objects, functions, properties, and script code.

## KotlinFile Interface

The `KotlinFile` interface represents a complete Kotlin source file with the following structure:

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `fileComment` | `CodeValue` | Comment at the top of the file (before package declaration) |
| `packageName` | `PackageName` | The package name for the file |
| `name` | `String` | The filename (without extension) |
| `types` | `List<KotlinTypeSpec>` | All top-level type declarations (classes, interfaces, objects, etc.) |
| `functions` | `List<KotlinFunctionSpec>` | All top-level function declarations |
| `properties` | `List<KotlinPropertySpec>` | All top-level property declarations |
| `type` | `KotlinTypeSpec` | **Deprecated**: The first type in the file. Throws if file has no types (only functions/properties). Use `types.firstOrNull()` for safety. |
| `staticImports` | `Set<String>` | Statically imported types and members |
| `alwaysQualify` | `Set<String>` | Types that should always use fully qualified names |
| `indent` | `String` | Indentation string (default: 4 spaces) |
| `annotations` | `List<AnnotationRef>` | File-level annotations (using `@file:` syntax) |

### Methods

#### writeTo

```kotlin
fun writeTo(out: Appendable, strategy: KotlinWriteStrategy)
```

Writes the Kotlin file to the specified `Appendable` using the provided writing strategy.

**Parameters:**
- `out`: The output target (e.g., `StringBuilder`, `Writer`)
- `strategy`: The writing strategy controlling output format

## Builder Hierarchy

### KotlinFileBuilder<B>

Abstract base class for all file builders, providing common functionality.

#### Methods

**Type Management:**
```kotlin
fun addType(type: KotlinTypeSpec): B
fun addTypes(types: Iterable<KotlinTypeSpec>): B
fun addTypes(vararg types: KotlinTypeSpec): B
```

**Function Management:**
```kotlin
fun addFunction(function: KotlinFunctionSpec): B
fun addFunctions(functions: Iterable<KotlinFunctionSpec>): B
```

**Property Management:**
```kotlin
fun addProperty(property: KotlinPropertySpec): B
fun addProperties(properties: Iterable<KotlinPropertySpec>): B
```

**File Configuration:**
```kotlin
fun addFileComment(format: String, block: CodeValueSingleFormatBuilderDsl = {}): B
fun addFileComment(codeValue: CodeValue): B
fun addStaticImport(import: String): B
fun addStaticImport(className: ClassName, vararg names: String): B
fun addStaticImport(className: ClassName, names: Iterable<String>): B
fun indent(indent: String): B
fun name(name: String): B
```

**Annotation Management:**
```kotlin
fun addAnnotation(ref: AnnotationRef): B
fun addAnnotations(refs: Iterable<AnnotationRef>): B
```

**Note:** File-level annotations must use `KotlinAnnotationUseSite.FILE` or no useSite (defaults to FILE). Other useSites will throw `IllegalArgumentException`.

**Build:**
```kotlin
fun build(): KotlinFile
```

### KotlinSimpleFileBuilder

Builder for regular Kotlin files (`.kt` extension).

**Construction:**
```kotlin
// With initial type
KotlinFile.builder(packageName: PackageName, type: KotlinTypeSpec): KotlinSimpleFileBuilder

// Without initial type
KotlinFile.builder(packageName: PackageName): KotlinSimpleFileBuilder
```

**Requirements:**
- Must contain at least one type, function, or property
- Filename defaults to first type's name or "File" if not explicitly set

### KotlinScriptFileBuilder

Builder for Kotlin script files (`.kts` extension).

**Construction:**
```kotlin
KotlinFile.scriptBuilder(): KotlinScriptFileBuilder
```

**Additional Capabilities:**
- Implements `CodeValueCollector<KotlinScriptFileBuilder>`
- Can contain arbitrary code in addition to types, functions, and properties

**Additional Methods:**
```kotlin
fun addCode(codeValue: CodeValue): KotlinScriptFileBuilder
fun addCode(format: String, vararg argumentParts: CodeArgumentPart): KotlinScriptFileBuilder
fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): KotlinScriptFileBuilder
fun addStatement(codeValue: CodeValue): KotlinScriptFileBuilder
```

**Requirements:**
- Must contain at least some code, types, functions, or properties
- Filename defaults to first type's name or "script" if not explicitly set

## DSL Factory Functions

### Regular Files

**With Single Type:**
```kotlin
inline fun KotlinFile(
    packageName: PackageName,
    type: KotlinTypeSpec,
    block: KotlinSimpleFileBuilder.() -> Unit = {}
): KotlinFile

inline fun KotlinFile(
    packageNamePaths: String,
    type: KotlinTypeSpec,
    block: KotlinSimpleFileBuilder.() -> Unit = {}
): KotlinFile
```

**With Multiple Types:**
```kotlin
inline fun KotlinFile(
    packageName: PackageName,
    types: Iterable<KotlinTypeSpec>,
    block: KotlinSimpleFileBuilder.() -> Unit = {}
): KotlinFile

inline fun KotlinFile(
    packageName: PackageName,
    vararg types: KotlinTypeSpec,
    block: KotlinSimpleFileBuilder.() -> Unit = {}
): KotlinFile

inline fun KotlinFile(
    packageNamePaths: String,
    types: Iterable<KotlinTypeSpec>,
    block: KotlinSimpleFileBuilder.() -> Unit = {}
): KotlinFile

inline fun KotlinFile(
    packageNamePaths: String,
    vararg types: KotlinTypeSpec,
    block: KotlinSimpleFileBuilder.() -> Unit = {}
): KotlinFile
```

### Script Files

```kotlin
inline fun KotlinFile(
    block: KotlinScriptFileBuilder.() -> Unit = {}
): KotlinFile
```

## Type Creation Extensions

Convenience extensions for creating and adding types directly in the file builder:

```kotlin
// Simple class or interface
fun <B : KotlinFileBuilder<B>> B.addSimpleClassType(
    name: String,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): B

fun <B : KotlinFileBuilder<B>> B.addSimpleInterfaceType(
    name: String,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): B

// Object
fun <B : KotlinFileBuilder<B>> B.addObjectType(
    name: String,
    isCompanion: Boolean,
    block: KotlinObjectTypeSpec.Builder.() -> Unit = {}
): B

// Enum
fun <B : KotlinFileBuilder<B>> B.addEnumType(
    name: String,
    block: KotlinEnumTypeSpec.Builder.() -> Unit = {}
): B

// Annotation
fun <B : KotlinFileBuilder<B>> B.addAnnotationType(
    name: String,
    block: KotlinAnnotationTypeSpec.Builder.() -> Unit = {}
): B

// Value class
fun <B : KotlinFileBuilder<B>> B.addValueClassType(
    name: String,
    primaryConstructor: KotlinConstructorSpec,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): B

fun <B : KotlinFileBuilder<B>> B.addValueClassType(
    name: String,
    primaryConstructor: KotlinConstructorSpec.Builder.() -> Unit,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): B

fun <B : KotlinFileBuilder<B>> B.addValueClassType(
    name: String,
    primaryParameter: KotlinValueParameterSpec,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): B

// Typealias
fun <B : KotlinFileBuilder<B>> B.addTypealiasType(
    name: String,
    type: TypeRef<*>,
    block: KotlinTypealiasSpec.Builder.() -> Unit = {}
): B
```

## Utility Functions

### toRelativePath

Converts a `KotlinFile` to a relative file path based on its package structure.

```kotlin
fun KotlinFile.toRelativePath(
    filename: String = this.name,
    isScript: Boolean = false,
    separator: String = "/"
): String
```

**Parameters:**
- `filename`: The filename to use (defaults to `this.name`). If no extension is provided, `.kt` or `.kts` will be appended
- `isScript`: Whether this is a script file (determines `.kt` vs `.kts` extension)
- `separator`: Path separator (defaults to `/`)

**Returns:** Relative path string combining package path and filename

**Example:**
```kotlin
val file = KotlinFile("com.example", myClass)
file.toRelativePath() // "com/example/MyClass.kt"
file.toRelativePath(isScript = true) // "com/example/MyClass.kts"
file.toRelativePath(separator = "\\") // "com\example\MyClass.kt"
```

### writeToKotlinString

Converts a `KotlinFile` to its Kotlin source code string representation.

```kotlin
fun KotlinFile.writeToKotlinString(): String
```

**Returns:** Complete Kotlin source code as a string

## Usage Examples

### Basic File with Single Class

```kotlin
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.kotlin.KotlinFile
import love.forte.codegentle.kotlin.spec.KotlinSimpleTypeSpec
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec

val myClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass")
val file = KotlinFile("com.example".parseToPackageName(), myClass)

println(file.writeToKotlinString())
```

**Output:**
```kotlin
package com.example

class MyClass
```

### File with Multiple Types

```kotlin
val class1 = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "FirstClass")
val class2 = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "SecondClass")
val interface1 = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.INTERFACE, "MyInterface")

val file = KotlinFile("com.example") {
    addType(class1)
    addType(class2)
    addType(interface1)
}

println(file.writeToKotlinString())
```

**Output:**
```kotlin
package com.example

class FirstClass

class SecondClass

interface MyInterface
```

### File with Top-Level Functions

```kotlin
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec

val function1 = KotlinFunctionSpec("greet", ClassName("kotlin", "Unit").ref()) {
    addParameter("name", ClassName("kotlin", "String").ref())
    addCode("println(\"Hello, \$name!\")")
}

val function2 = KotlinFunctionSpec("calculate", ClassName("kotlin", "Int").ref()) {
    addParameter("a", ClassName("kotlin", "Int").ref())
    addParameter("b", ClassName("kotlin", "Int").ref())
    addCode("return a + b")
}

val file = KotlinFile("com.example") {
    addFunction(function1)
    addFunction(function2)
}

println(file.writeToKotlinString())
```

**Output:**
```kotlin
package com.example

fun greet(name: String) {
    println("Hello, $name!")
}

fun calculate(a: Int, b: Int): Int = return a + b
```

### File with Top-Level Properties

```kotlin
import love.forte.codegentle.kotlin.spec.KotlinPropertySpec

val property1 = KotlinPropertySpec("appName", ClassName("kotlin", "String").ref()) {
    initializer("\"MyApplication\"")
}

val property2 = KotlinPropertySpec("version", ClassName("kotlin", "Int").ref()) {
    initializer("1")
}

val file = KotlinFile("com.example") {
    addProperty(property1)
    addProperty(property2)
}

println(file.writeToKotlinString())
```

**Output:**
```kotlin
package com.example

val appName: String = "MyApplication"

val version: Int = 1
```

### File with Mixed Top-Level Elements

```kotlin
val config = KotlinPropertySpec("config", ClassName("kotlin", "String").ref()) {
    initializer("\"Configuration\"")
}

val utility = KotlinFunctionSpec("initialize", ClassName("kotlin", "Unit").ref()) {
    addCode("println(config)")
}

val service = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Service")

val file = KotlinFile("com.example") {
    addProperty(config)
    addType(service)
    addFunction(utility)
}

println(file.writeToKotlinString())
```

**Output:**
```kotlin
package com.example

val config: String = "Configuration"

class Service

fun initialize() {
    println(config)
}
```

### File with Comments and Static Imports

```kotlin
val myClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass")

val file = KotlinFile("com.example", myClass) {
    addFileComment("This file contains generated code.")
    addFileComment("Do not modify manually.")
    addStaticImport(ClassName("kotlin.collections", "Collections"), "emptyList", "emptyMap")
}

println(file.writeToKotlinString())
```

**Output:**
```kotlin
// This file contains generated code.
// Do not modify manually.
package com.example

import kotlin.collections.Collections.emptyList
import kotlin.collections.Collections.emptyMap

class MyClass
```

### File with File-Level Annotations

```kotlin
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.kotlin.ref.KotlinAnnotationUseSite
import love.forte.codegentle.kotlin.ref.kotlinAnnotationRef
import love.forte.codegentle.common.ref.status

val suppressAnnotation = ClassName("kotlin", "Suppress").kotlinAnnotationRef {
    addMember("\"UNUSED\"")
    status {
        useSite = KotlinAnnotationUseSite.FILE
    }
}

val file = KotlinFile("com.example") {
    addAnnotation(suppressAnnotation)
    addSimpleClassType("MyClass")
}

println(file.writeToKotlinString())
```

**Output:**
```kotlin
@file:Suppress("UNUSED")
package com.example

class MyClass
```

### File with Custom Indentation

```kotlin
val myClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "MyClass") {
    addFunction(
        KotlinFunctionSpec("doSomething", ClassName("kotlin", "Unit").ref()) {
            addCode("println(\"Hello\")")
        }
    )
}

val file = KotlinFile("com.example", myClass) {
    indent("\t") // Use tabs instead of spaces
}

println(file.writeToKotlinString())
```

**Output:**
```kotlin
package com.example

class MyClass {
	fun doSomething() {
		println("Hello")
	}
}
```

### Script File

```kotlin
val scriptFile = KotlinFile {
    addFileComment("Kotlin script file")
    addCode("println(\"Hello from script!\")")
    addStatement("val x = 42")
    addStatement("println(\"x = \$x\")")
}

println(scriptFile.writeToKotlinString())
```

**Output:**
```kotlin
// Kotlin script file

println("Hello from script!")
val x = 42
println("x = $x")
```

### Using Type Creation Extensions

```kotlin
val file = KotlinFile("com.example") {
    // Add a simple class
    addSimpleClassType("Person") {
        addProperty(
            KotlinPropertySpec("name", ClassName("kotlin", "String").ref())
        )
    }

    // Add an enum
    addEnumType("Status") {
        addEnumConstant("ACTIVE")
        addEnumConstant("INACTIVE")
    }

    // Add a value class
    addValueClassType(
        name = "UserId",
        primaryParameter = KotlinValueParameterSpec("value", ClassName("kotlin", "String").ref()) {
            immutableProperty()
        }
    )

    // Add a typealias
    addTypealiasType(
        name = "StringMap",
        type = ClassName("kotlin.collections", "Map")
            .parameterizedBy(
                ClassName("kotlin", "String").ref(),
                ClassName("kotlin", "String").ref()
            )
    )
}

println(file.writeToKotlinString())
```

**Output:**
```kotlin
package com.example

class Person {
    val name: String
}

enum class Status {
    ACTIVE,
    INACTIVE
}

value class UserId(val value: String)

typealias StringMap = Map<String, String>
```

### Complete Example with All Features

```kotlin
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinFile
import love.forte.codegentle.kotlin.ref.KotlinAnnotationUseSite
import love.forte.codegentle.kotlin.ref.kotlinAnnotationRef
import love.forte.codegentle.kotlin.spec.*

// Create file-level annotation
val jvmNameAnnotation = ClassName("kotlin.jvm", "JvmName").kotlinAnnotationRef {
    addMember("\"MyClassUtils\"")
    status {
        useSite = KotlinAnnotationUseSite.FILE
    }
}

// Create top-level property
val version = KotlinPropertySpec("VERSION", ClassName("kotlin", "String").ref()) {
    addModifier(KotlinModifier.CONST)
    initializer("\"1.0.0\"")
}

// Create top-level function
val helper = KotlinFunctionSpec("logVersion", ClassName("kotlin", "Unit").ref()) {
    addCode("println(\"Version: \$VERSION\")")
}

// Create main class
val mainClass = KotlinSimpleTypeSpec(KotlinTypeSpec.Kind.CLASS, "Application") {
    addModifier(KotlinModifier.PUBLIC)

    addFunction(
        KotlinFunctionSpec("start", ClassName("kotlin", "Unit").ref()) {
            addCode("logVersion()")
        }
    )
}

// Build the complete file
val file = KotlinFile("com.example.app".parseToPackageName()) {
    addFileComment("Generated code for Application")
    addFileComment("Version: 1.0.0")

    addAnnotation(jvmNameAnnotation)

    addStaticImport(
        ClassName("kotlin.io", "ConsoleKt"),
        "println"
    )

    addProperty(version)
    addFunction(helper)
    addType(mainClass)

    name("Application")
    indent("    ")
}

println(file.writeToKotlinString())

// Get relative path
println("Relative path: ${file.toRelativePath()}")
```

**Output:**
```kotlin
// Generated code for Application
// Version: 1.0.0
@file:JvmName("MyClassUtils")
package com.example.app

import kotlin.io.ConsoleKt.println

const val VERSION: String = "1.0.0"

public class Application {
    fun start() {
        logVersion()
    }
}

fun logVersion() {
    println("Version: $VERSION")
}

Relative path: com/example/app/Application.kt
```

## Notes

### File Structure
The generated Kotlin file follows this structure:
1. File comments (if any)
2. File-level annotations (if any)
3. Package declaration
4. Static imports (if any)
5. Regular imports (automatically collected)
6. Top-level properties
7. Top-level types
8. Top-level functions
9. Script code (for script files only)

### Import Management
- Imports are automatically collected from all types, functions, properties, and annotations
- Types from the `kotlin` package are omitted unless specified in `alwaysQualify`
- Types in the same package are not imported
- Duplicate imports are automatically handled

### File Validation
- Regular files must contain at least one type, function, or property
- Script files must contain at least some code, types, functions, or properties
- File-level annotations must use `FILE` useSite or no useSite (defaults to FILE)
- Filename defaults to the first type's name if not explicitly set

### Best Practices
1. Use DSL factory functions for concise file creation
2. Use extension functions for adding types directly
3. Set explicit filenames when creating files with only functions/properties
4. Use `addFileComment()` for file-level documentation
5. Use `toRelativePath()` when writing files to disk to maintain package structure
