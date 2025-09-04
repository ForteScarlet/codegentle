/*
 * Copyright (C) 2025 Forte Scarlet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package love.forte.codegentle.kotlin

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.*
import love.forte.codegentle.common.naming.*
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.writer.InternalWriterApi
import love.forte.codegentle.kotlin.internal.KotlinFileImpl
import love.forte.codegentle.kotlin.ref.KotlinAnnotationUseSite
import love.forte.codegentle.kotlin.ref.kotlinStatusOrNull
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.strategy.KotlinWriteStrategy
import love.forte.codegentle.kotlin.strategy.ToStringKotlinWriteStrategy
import love.forte.codegentle.kotlin.writer.KotlinCodeEmitter

/**
 * Represents a Kotlin source file.
 *
 * A Kotlin source file can contain one or more top-level classes, interfaces, objects, functions, properties, etc.
 */
public interface KotlinFile : KotlinCodeEmitter, Named {

    public val fileComment: CodeValue
    public val packageName: PackageName

    /**
     * The filename of the Kotlin file
     */
    override val name: String

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
     * File-level annotations (using @file: syntax)
     */
    public val annotations: List<AnnotationRef>

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
        public fun builder(packageName: PackageName, type: KotlinTypeSpec): KotlinSimpleFileBuilder =
            KotlinSimpleFileBuilder(packageName).addType(type)

        /**
         * Creates a [KotlinFileBuilder] instance without an initial type.
         *
         * @param packageName The package name
         * @return A new [KotlinFileBuilder] instance
         */
        public fun builder(packageName: PackageName): KotlinSimpleFileBuilder =
            KotlinSimpleFileBuilder(packageName)

        /**
         * Creates a [KotlinScriptFileBuilder] instance.
         */
        @OptIn(InternalWriterApi::class)
        public fun scriptBuilder(): KotlinScriptFileBuilder = KotlinScriptFileBuilder()
    }
}

/**
 * Converts the current Kotlin file to a relative path string based on the provided parameters.
 *
 * The relative path is constructed using the package name of the file, the provided filename,
 * and an optional file extension (`.kt` or `.kts` for scripts), joined with the specified separator.
 *
 * @param filename the name of the file to include in the path. If no extension is provided,
 *                 `.kt` or `.kts` will be appended based on the `isScript` parameter. Defaults to the file's name.
 * @param isScript indicates whether the file is a Kotlin script. If true and `filename` has no extension,
 *                 a `.kts` extension is added. Otherwise, `.kt` is added. Defaults to false.
 * @param separator the string used to separate parts of the path. Defaults to "/".
 * @return the constructed relative path string.
 */
public fun KotlinFile.toRelativePath(
    filename: String = this.name,
    isScript: Boolean = false,
    separator: String = "/"
): String {
    val filenameWithExtension = if (filename.contains('.')) {
        filename
    } else {
        filename + if (isScript) ".kts" else ".kt"
    }

    val packageName = this.packageName
    return if (packageName.isEmpty()) {
        filenameWithExtension
    } else {
        packageName.toRelativePath(separator) + separator + filenameWithExtension
    }
}


public sealed class KotlinFileBuilder<B : KotlinFileBuilder<B>>
protected constructor(public val packageName: PackageName) :
    BuilderDsl,
    KotlinFunctionCollector<B>,
    KotlinPropertyCollector<B>,
    AnnotationRefCollector<B> {
    protected val fileComment: CodeValueBuilder = CodeValue.builder()
    protected var name: String? = null
    protected var indent: String = "    "
    protected val staticImports: LinkedHashSet<String> = linkedSetOf<String>()
    protected val types: MutableList<KotlinTypeSpec> = mutableListOf()
    protected val functions: MutableList<KotlinFunctionSpec> = mutableListOf()
    protected val properties: MutableList<KotlinPropertySpec> = mutableListOf()
    protected val annotations: MutableList<AnnotationRef> = mutableListOf()

    protected abstract val self: B

    /**
     * Validates that the annotation ref is suitable for file-level usage.
     * Only annotations with FILE useSite or no useSite (defaults to file) are allowed.
     */
    private fun validateFileAnnotation(ref: AnnotationRef) {
        val kotlinStatus = ref.kotlinStatusOrNull
        val useSite = kotlinStatus?.useSite
        require(useSite == null || useSite == KotlinAnnotationUseSite.FILE) {
            "File-level annotations must use @file: syntax. Found useSite: $useSite"
        }
    }

    /**
     * Adds a type to the file
     *
     * @param type The type to add
     * @return The current builder instance
     */
    public open fun addType(type: KotlinTypeSpec): B = self.apply {
        types.add(type)
    }

    /**
     * Adds multiple types to the file
     *
     * @param types The collection of types to add
     * @return The current builder instance
     */
    public open fun addTypes(types: Iterable<KotlinTypeSpec>): B = self.apply {
        this.types.addAll(types)
    }

    /**
     * Adds multiple types to the file
     *
     * @param types The array of types to add
     * @return The current builder instance
     */
    public open fun addTypes(vararg types: KotlinTypeSpec): B = self.apply {
        this.types.addAll(types)
    }

    /**
     * Adds a function to the file
     *
     * @param function The function to add
     * @return The current builder instance
     */
    override fun addFunction(function: KotlinFunctionSpec): B = self.apply {
        functions.add(function)
    }

    /**
     * Adds multiple functions to the file
     *
     * @param functions The collection of functions to add
     * @return The current builder instance
     */
    override fun addFunctions(functions: Iterable<KotlinFunctionSpec>): B = self.apply {
        this.functions.addAll(functions)
    }

    /**
     * Adds a property to the file
     *
     * @param property The property to add
     * @return The current builder instance
     */
    override fun addProperty(property: KotlinPropertySpec): B = self.apply {
        properties.add(property)
    }

    /**
     * Adds multiple properties to the file
     *
     * @param properties The collection of properties to add
     * @return The current builder instance
     */
    override fun addProperties(properties: Iterable<KotlinPropertySpec>): B = self.apply {
        this.properties.addAll(properties)
    }

    /**
     * Adds a file comment
     *
     * @param format The format string
     * @param block The code value builder block
     * @return The current builder instance
     */
    public open fun addFileComment(format: String, block: CodeValueSingleFormatBuilderDsl = {}): B = self.apply {
        addFileComment(CodeValue(format, block))
    }

    /**
     * Adds a file comment
     *
     * @param codeValue The code value
     * @return The current builder instance
     */
    public open fun addFileComment(codeValue: CodeValue): B = self.apply {
        fileComment.addCode(codeValue)
    }

    /**
     * Adds a static import
     *
     * @param import The import statement
     * @return The current builder instance
     */
    public open fun addStaticImport(import: String): B = self.apply {
        staticImports.add(import)
    }

    /**
     * Adds a static import
     *
     * @param className The class name
     * @param names The member names
     * @return The current builder instance
     */
    public open fun addStaticImport(className: ClassName, vararg names: String): B = self.apply {
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
    public open fun addStaticImport(className: ClassName, names: Iterable<String>): B = self.apply {
        val iter = names.iterator()
        require(iter.hasNext()) { "`names` is empty" }

        for (name in iter) {
            staticImports.add(className.canonicalName + "." + name)
        }
    }

    /**
     * Sets the indentation string
     *
     * @param indent The indentation string
     * @return The current builder instance
     */
    public open fun indent(indent: String): B = self.apply {
        this.indent = indent
    }

    /**
     * Sets the filename
     *
     * @param name The filename
     * @return The current builder instance
     */
    public open fun name(name: String): B = self.apply {
        this.name = name
    }

    /**
     * Adds a file-level annotation
     *
     * @param ref The annotation reference
     * @return The current builder instance
     */
    override fun addAnnotation(ref: AnnotationRef): B = self.apply {
        validateFileAnnotation(ref)
        annotations.add(ref)
    }

    /**
     * Adds multiple file-level annotations
     *
     * @param refs The collection of annotation references
     * @return The current builder instance
     */
    override fun addAnnotations(refs: Iterable<AnnotationRef>): B = self.apply {
        for (ref in refs) {
            validateFileAnnotation(ref)
            annotations.add(ref)
        }
    }

    /**
     * Builds a [KotlinFile] instance
     *
     * @return A new [KotlinFile] instance
     */
    public abstract fun build(): KotlinFile
}

/**
 * Kotlin file builder
 */
@SubclassOptInRequired(InternalWriterApi::class)
public open class KotlinSimpleFileBuilder internal constructor(packageName: PackageName) :
    KotlinFileBuilder<KotlinSimpleFileBuilder>(packageName) {

    override val self: KotlinSimpleFileBuilder
        get() = this

    /**
     * Builds a [KotlinFile] instance
     *
     * @return A new [KotlinFile] instance
     */
    override fun build(): KotlinFile {
        val alwaysQualify = linkedSetOf<String>()

        // Ensure there is at least one element (type, function, or property)
        if (types.isEmpty() && functions.isEmpty() && properties.isEmpty()) {
            throw IllegalStateException("At least one type, function, or property must be added to the file")
        }

        // Determine the filename: use explicit name if set, otherwise default to first type's name or "File"
        val fileName = name ?: if (types.isNotEmpty()) {
            types.first().name
        } else {
            "File"
        }

        return KotlinFileImpl(
            name = fileName,
            fileComment = fileComment.build(),
            packageName = packageName,
            types = types.toList(),
            functions = functions.toList(),
            properties = properties.toList(),
            code = null,
            staticImports = LinkedHashSet(staticImports),
            alwaysQualify = alwaysQualify,
            indent = indent,
            annotations = annotations.toList()
        )
    }
}

@OptIn(InternalWriterApi::class)
public class KotlinScriptFileBuilder internal constructor() :
    KotlinFileBuilder<KotlinScriptFileBuilder>(PackageName()),
    CodeValueCollector<KotlinScriptFileBuilder> {
    private val code = CodeValue.builder()

    override val self: KotlinScriptFileBuilder
        get() = this

    override fun addCode(codeValue: CodeValue): KotlinScriptFileBuilder = apply {
        code.addCode(codeValue)
    }

    override fun addCode(
        format: String,
        vararg argumentParts: CodeArgumentPart
    ): KotlinScriptFileBuilder = apply {
        code.addCode(format, *argumentParts)
    }

    override fun addStatement(
        format: String,
        vararg argumentParts: CodeArgumentPart
    ): KotlinScriptFileBuilder = apply {
        code.addStatement(format, *argumentParts)
    }

    override fun addStatement(codeValue: CodeValue): KotlinScriptFileBuilder = apply {
        code.addStatement(codeValue)
    }

    override fun build(): KotlinFile {
        val alwaysQualify = linkedSetOf<String>()

        // For script files, allow only code without requiring types, functions, or properties
        val builtCode = code.build()
        if (types.isEmpty() && functions.isEmpty() && properties.isEmpty() && builtCode.isEmpty()) {
            throw IllegalStateException("Script file must contain at least some code, types, functions, or properties")
        }

        // Determine the filename: use explicit name if set, otherwise default to first type's name or "script"
        val fileName = name ?: if (types.isNotEmpty()) {
            types.first().name
        } else {
            "script"
        }

        return KotlinFileImpl(
            name = fileName,
            fileComment = fileComment.build(),
            packageName = packageName,
            types = types.toList(),
            functions = functions.toList(),
            properties = properties.toList(),
            code = builtCode,
            staticImports = LinkedHashSet(staticImports),
            alwaysQualify = alwaysQualify,
            indent = indent,
            annotations = annotations.toList()
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
    block: KotlinSimpleFileBuilder.() -> Unit = {}
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
    block: KotlinSimpleFileBuilder.() -> Unit = {}
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
    block: KotlinSimpleFileBuilder.() -> Unit = {}
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
    block: KotlinSimpleFileBuilder.() -> Unit = {}
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
    block: KotlinSimpleFileBuilder.() -> Unit = {}
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
    block: KotlinSimpleFileBuilder.() -> Unit = {}
): KotlinFile =
    KotlinFile.builder(packageNamePaths.parseToPackageName()).apply { addTypes(*types) }.also(block).build()

/**
 * Creates a [KotlinFile] instance
 */
@OptIn(InternalWriterApi::class)
public inline fun KotlinFile(block: KotlinScriptFileBuilder.() -> Unit = {}): KotlinFile =
    KotlinFile.scriptBuilder().also(block).build()


/**
 * Writes the Kotlin file to a string
 *
 * @return A string containing the Kotlin code
 */
public fun KotlinFile.writeToKotlinString(): String = buildString {
    writeTo(this, ToStringKotlinWriteStrategy)
}

// extensions

public inline fun <B : KotlinFileBuilder<B>> B.addSimpleType(
    kind: KotlinTypeSpec.Kind,
    name: String,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): B = addType(KotlinSimpleTypeSpec(kind, name, block))

public inline fun <B : KotlinFileBuilder<B>> B.addSimpleClassType(
    name: String,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): B = addSimpleType(KotlinTypeSpec.Kind.CLASS, name, block)

public inline fun <B : KotlinFileBuilder<B>> B.addSimpleInterfaceType(
    name: String,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): B = addSimpleType(KotlinTypeSpec.Kind.INTERFACE, name, block)

public inline fun <B : KotlinFileBuilder<B>> B.addObjectType(
    name: String,
    isCompanion: Boolean,
    block: KotlinObjectTypeSpec.Builder.() -> Unit = {}
): B = addType(KotlinObjectTypeSpec(name, isCompanion, block))

public inline fun <B : KotlinFileBuilder<B>> B.addEnumType(
    name: String,
    block: KotlinEnumTypeSpec.Builder.() -> Unit = {}
): B = addType(KotlinEnumTypeSpec(name, block))

public inline fun <B : KotlinFileBuilder<B>> B.addAnnotationType(
    name: String,
    block: KotlinAnnotationTypeSpec.Builder.() -> Unit = {}
): B = addType(KotlinAnnotationTypeSpec(name, block))

public inline fun <B : KotlinFileBuilder<B>> B.addValueClassType(
    name: String,
    primaryConstructor: KotlinConstructorSpec,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): B = addType(KotlinValueClassTypeSpec(name, primaryConstructor, block))

public inline fun <B : KotlinFileBuilder<B>> B.addValueClassType(
    name: String,
    primaryConstructor: KotlinConstructorSpec.Builder.() -> Unit,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): B = addType(KotlinValueClassTypeSpec(name, primaryConstructor, block))

public inline fun <B : KotlinFileBuilder<B>> B.addValueClassType(
    name: String,
    primaryParameter: KotlinValueParameterSpec,
    block: KotlinValueClassTypeSpec.Builder.() -> Unit = {}
): B = addType(
    KotlinValueClassTypeSpec(
        name = name,
        primaryConstructor = KotlinConstructorSpec { addParameter(primaryParameter) },
        block = block
    )
)

public inline fun <B : KotlinFileBuilder<B>> B.addTypealiasType(
    name: String,
    type: TypeRef<*>,
    block: KotlinTypealiasSpec.Builder.() -> Unit = {}
): B = addType(KotlinTypealiasSpec(name, type, block))

