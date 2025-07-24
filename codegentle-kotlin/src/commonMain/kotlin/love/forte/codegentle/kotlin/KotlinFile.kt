package love.forte.codegentle.kotlin

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.PackageName
import love.forte.codegentle.common.naming.canonicalName
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.kotlin.internal.KotlinFileImpl
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec
import love.forte.codegentle.kotlin.spec.KotlinPropertySpec
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
import love.forte.codegentle.kotlin.strategy.KotlinWriteStrategy
import love.forte.codegentle.kotlin.strategy.ToStringKotlinWriteStrategy
import love.forte.codegentle.kotlin.writer.KotlinCodeEmitter

/**
 * Represents a Kotlin source file.
 *
 * A Kotlin source file can contain one or more top-level classes, interfaces, objects, functions, properties, etc.
 */
public interface KotlinFile : KotlinCodeEmitter {

    public val fileComment: CodeValue
    public val packageName: PackageName

    /**
     * All top-level types in the file
     */
    public val types: List<KotlinTypeSpec>

    /**
     * All top-level functions in the file
     */
    public val functions: List<KotlinFunctionSpec>

    /**
     * All top-level properties in the file
     */
    public val properties: List<KotlinPropertySpec>

    /**
     * Gets the first type in the file (if any)
     *
     * This property is provided for backward compatibility
     */
    public val type: KotlinTypeSpec
        get() = types.first()

    /**
     * Whether to skip importing types from the `kotlin.*` package
     */
    public val skipKotlinImports: Boolean

    /**
     * Statically imported types and members
     */
    public val staticImports: Set<String>

    /**
     * Types that should always use fully qualified names
     */
    public val alwaysQualify: Set<String>

    /**
     * Indentation string
     */
    public val indent: String

    /**
     * Writes the Kotlin file to the specified Appendable
     *
     * @param out The output target
     * @param strategy The writing strategy
     */
    public fun writeTo(out: Appendable, strategy: KotlinWriteStrategy)

    public companion object {
        /**
         * Creates a [KotlinFileBuilder] instance.
         *
         * @param packageName The package name
         * @param type The type specification
         * @return A new [KotlinFileBuilder] instance
         */
        public fun builder(packageName: PackageName, type: KotlinTypeSpec): KotlinFileBuilder =
            KotlinFileBuilder(packageName).addType(type)

        /**
         * Creates a [KotlinFileBuilder] instance without an initial type.
         *
         * @param packageName The package name
         * @return A new [KotlinFileBuilder] instance
         */
        public fun builder(packageName: PackageName): KotlinFileBuilder =
            KotlinFileBuilder(packageName)
    }
}

/**
 * Kotlin file builder
 */
public class KotlinFileBuilder internal constructor(public val packageName: PackageName) : BuilderDsl {
    private val fileComment = CodeValue.builder()
    private var skipKotlinImports: Boolean = true
    private var indent: String = "    "
    private val staticImports = linkedSetOf<String>()
    private val types = mutableListOf<KotlinTypeSpec>()
    private val functions = mutableListOf<KotlinFunctionSpec>()
    private val properties = mutableListOf<KotlinPropertySpec>()

    /**
     * Adds a type to the file
     *
     * @param type The type to add
     * @return The current builder instance
     */
    public fun addType(type: KotlinTypeSpec): KotlinFileBuilder = apply {
        types.add(type)
    }

    /**
     * Adds multiple types to the file
     *
     * @param types The collection of types to add
     * @return The current builder instance
     */
    public fun addTypes(types: Iterable<KotlinTypeSpec>): KotlinFileBuilder = apply {
        this.types.addAll(types)
    }

    /**
     * Adds multiple types to the file
     *
     * @param types The array of types to add
     * @return The current builder instance
     */
    public fun addTypes(vararg types: KotlinTypeSpec): KotlinFileBuilder = apply {
        this.types.addAll(types)
    }

    /**
     * Adds a function to the file
     *
     * @param function The function to add
     * @return The current builder instance
     */
    public fun addFunction(function: KotlinFunctionSpec): KotlinFileBuilder = apply {
        functions.add(function)
    }

    /**
     * Adds multiple functions to the file
     *
     * @param functions The collection of functions to add
     * @return The current builder instance
     */
    public fun addFunctions(functions: Iterable<KotlinFunctionSpec>): KotlinFileBuilder = apply {
        this.functions.addAll(functions)
    }

    /**
     * Adds multiple functions to the file
     *
     * @param functions The array of functions to add
     * @return The current builder instance
     */
    public fun addFunctions(vararg functions: KotlinFunctionSpec): KotlinFileBuilder = apply {
        this.functions.addAll(functions)
    }

    /**
     * Adds a property to the file
     *
     * @param property The property to add
     * @return The current builder instance
     */
    public fun addProperty(property: KotlinPropertySpec): KotlinFileBuilder = apply {
        properties.add(property)
    }

    /**
     * Adds multiple properties to the file
     *
     * @param properties The collection of properties to add
     * @return The current builder instance
     */
    public fun addProperties(properties: Iterable<KotlinPropertySpec>): KotlinFileBuilder = apply {
        this.properties.addAll(properties)
    }

    /**
     * Adds multiple properties to the file
     *
     * @param properties The array of properties to add
     * @return The current builder instance
     */
    public fun addProperties(vararg properties: KotlinPropertySpec): KotlinFileBuilder = apply {
        this.properties.addAll(properties)
    }

    /**
     * Adds a file comment
     *
     * @param format The format string
     * @param block The code value builder block
     * @return The current builder instance
     */
    public fun addFileComment(format: String, block: CodeValueSingleFormatBuilderDsl = {}): KotlinFileBuilder = apply {
        addFileComment(CodeValue(format, block))
    }

    /**
     * Adds a file comment
     *
     * @param codeValue The code value
     * @return The current builder instance
     */
    public fun addFileComment(codeValue: CodeValue): KotlinFileBuilder = apply {
        fileComment.add(codeValue)
    }

    /**
     * Adds a static import
     *
     * @param import The import statement
     * @return The current builder instance
     */
    public fun addStaticImport(import: String): KotlinFileBuilder = apply {
        staticImports.add(import)
    }

    /**
     * Adds a static import
     *
     * @param className The class name
     * @param names The member names
     * @return The current builder instance
     */
    public fun addStaticImport(className: ClassName, vararg names: String): KotlinFileBuilder = apply {
        require(names.isNotEmpty()) { "`names` is empty" }
        for (name in names) {
            staticImports.add(className.canonicalName + "." + name)
        }
    }

    /**
     * Adds a static import
     *
     * @param className The class name
     * @param names The collection of member names
     * @return The current builder instance
     */
    public fun addStaticImport(className: ClassName, names: Iterable<String>): KotlinFileBuilder = apply {
        val iter = names.iterator()
        require(iter.hasNext()) { "`names` is empty" }

        for (name in iter) {
            staticImports.add(className.canonicalName + "." + name)
        }
    }

    /**
     * Sets whether to skip importing types from the kotlin.* package
     *
     * @param skipKotlinImports Whether to skip
     * @return The current builder instance
     */
    public fun skipKotlinImports(skipKotlinImports: Boolean): KotlinFileBuilder = apply {
        this.skipKotlinImports = skipKotlinImports
    }

    /**
     * Sets the indentation string
     *
     * @param indent The indentation string
     * @return The current builder instance
     */
    public fun indent(indent: String): KotlinFileBuilder = apply {
        this.indent = indent
    }

    /**
     * Builds a [KotlinFile] instance
     *
     * @return A new [KotlinFile] instance
     */
    public fun build(): KotlinFile {
        val alwaysQualify = linkedSetOf<String>()

        // Ensure there is at least one element (type, function, or property)
        if (types.isEmpty() && functions.isEmpty() && properties.isEmpty()) {
            throw IllegalStateException("At least one type, function, or property must be added to the file")
        }

        return KotlinFileImpl(
            fileComment = fileComment.build(),
            packageName = packageName,
            types = types.toList(),
            functions = functions.toList(),
            properties = properties.toList(),
            skipKotlinImports = skipKotlinImports,
            staticImports = LinkedHashSet(staticImports),
            alwaysQualify = alwaysQualify,
            indent = indent
        )
    }
}

/**
 * Creates a [KotlinFile] instance
 *
 * @param packageName The package name
 * @param type The type specification
 * @param block The code block to configure the [KotlinFileBuilder]
 * @return A new [KotlinFile] instance
 */
public inline fun KotlinFile(
    packageName: PackageName,
    type: KotlinTypeSpec,
    block: KotlinFileBuilder.() -> Unit = {}
): KotlinFile =
    KotlinFile.builder(packageName, type).also(block).build()

/**
 * Creates a [KotlinFile] instance
 *
 * @param packageName The package name
 * @param types The list of type specifications
 * @param block The code block to configure the [KotlinFileBuilder]
 * @return A new [KotlinFile] instance
 */
public inline fun KotlinFile(
    packageName: PackageName,
    types: Iterable<KotlinTypeSpec>,
    block: KotlinFileBuilder.() -> Unit = {}
): KotlinFile =
    KotlinFile.builder(packageName).apply { addTypes(types) }.also(block).build()

/**
 * Creates a [KotlinFile] instance
 *
 * @param packageName The package name
 * @param types The array of type specifications
 * @param block The code block to configure the [KotlinFileBuilder]
 * @return A new [KotlinFile] instance
 */
public inline fun KotlinFile(
    packageName: PackageName,
    vararg types: KotlinTypeSpec,
    block: KotlinFileBuilder.() -> Unit = {}
): KotlinFile =
    KotlinFile.builder(packageName).apply { addTypes(*types) }.also(block).build()

/**
 * Creates a [KotlinFile] instance
 *
 * @param packageNamePaths The package name path
 * @param type The type specification
 * @param block The code block to configure the [KotlinFileBuilder]
 * @return A new [KotlinFile] instance
 */
public inline fun KotlinFile(
    packageNamePaths: String,
    type: KotlinTypeSpec,
    block: KotlinFileBuilder.() -> Unit = {}
): KotlinFile =
    KotlinFile.builder(packageNamePaths.parseToPackageName(), type).also(block).build()

/**
 * Creates a [KotlinFile] instance
 *
 * @param packageNamePaths The package name path
 * @param types The list of type specifications
 * @param block The code block to configure the [KotlinFileBuilder]
 * @return A new [KotlinFile] instance
 */
public inline fun KotlinFile(
    packageNamePaths: String,
    types: Iterable<KotlinTypeSpec>,
    block: KotlinFileBuilder.() -> Unit = {}
): KotlinFile =
    KotlinFile.builder(packageNamePaths.parseToPackageName()).apply { addTypes(types) }.also(block).build()

/**
 * Creates a [KotlinFile] instance
 *
 * @param packageNamePaths The package name path
 * @param types The array of type specifications
 * @param block The code block to configure the [KotlinFileBuilder]
 * @return A new [KotlinFile] instance
 */
public inline fun KotlinFile(
    packageNamePaths: String,
    vararg types: KotlinTypeSpec,
    block: KotlinFileBuilder.() -> Unit = {}
): KotlinFile =
    KotlinFile.builder(packageNamePaths.parseToPackageName()).apply { addTypes(*types) }.also(block).build()

/**
 * Writes the Kotlin file to a string
 *
 * @return A string containing the Kotlin code
 */
public fun KotlinFile.writeToKotlinString(): String = buildString {
    writeTo(this, ToStringKotlinWriteStrategy)
}
