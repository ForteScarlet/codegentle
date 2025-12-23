# File Generation APIs

The `codegentle` file generation modules provide APIs for creating complete Java and Kotlin source files.

## Modules

### codegentle-java
Generate Java source files with the `JavaFile` API.
- [JavaFile Documentation](./JavaFile.md)

### codegentle-kotlin
Generate Kotlin source files with the `KotlinFile` API.
- [KotlinFile Documentation](./KotlinFile.md)

## Platform Support

### Common APIs (All Platforms)
Both `JavaFile` and `KotlinFile` provide these APIs on all platforms (JVM, JS, Native, Wasm):
- `writeTo(Appendable, Strategy)` - Write to any `Appendable`
- `write*String()` extensions - Generate string output
- `toRelativePath()` - Get file path relative to source root

### JVM-Only APIs
The following methods are only available on JVM targets:

**JavaFile (JVM)**:
- `writeTo(path: Path)` - Write to `java.nio.file.Path`
- `writeTo(directory: File)` - Write to `java.io.File`
- `writeTo(filer: Filer, vararg originatingElements: Element)` - APT integration

**KotlinFile (JVM)**:
- `writeTo(path: Path)` - Write to `java.nio.file.Path`
- `writeTo(directory: File)` - Write to `java.io.File`
- `writeTo(filer: Filer, vararg originatingElements: Element)` - APT integration

> **Important**: When targeting non-JVM platforms, use `writeTo(Appendable, Strategy)` with appropriate file I/O for your target platform.

## Quick Examples

### Java File
```kotlin
val javaFile = JavaFile("com.example".parseToPackageName(), myClassSpec)
javaFile.writeToJavaString() // Platform-independent string generation
```

### Kotlin File
```kotlin
val kotlinFile = KotlinFile("com.example".parseToPackageName(), myClassSpec)
kotlinFile.writeToKotlinString() // Platform-independent string generation
```

## Related Documentation
- [JavaFile API Reference](./JavaFile.md)
- [KotlinFile API Reference](./KotlinFile.md)
