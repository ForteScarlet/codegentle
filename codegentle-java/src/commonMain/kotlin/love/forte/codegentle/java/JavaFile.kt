/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2015-2025 Forte Scarlet
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
@file:JvmName("JavaFiles")
@file:JvmMultifileClass


package love.forte.codegentle.java

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.naming.*
import love.forte.codegentle.java.internal.JavaFileImpl
import love.forte.codegentle.java.spec.*
import love.forte.codegentle.java.strategy.JavaWriteStrategy
import love.forte.codegentle.java.writer.JavaCodeEmitter
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic


/**
 *
 * @author ForteScarlet
 */
public interface JavaFile : JavaCodeEmitter {

    public val fileComment: CodeValue
    public val packageName: PackageName

    public val type: JavaTypeSpec

    /**
     * Secondary types that can be included in the same Java file.
     * These types cannot be public (only the main type can be public).
     */
    public val secondaryTypes: List<JavaTypeSpec>

    /**
     * Call this to omit imports for classes in `java.lang`, such as `java.lang.String`.
     *
     * By default, JavaPoet explicitly imports types in `java.lang` to defend against
     * naming conflicts. Suppose an (ill-advised) class is named `com.example.String`. When
     * `java.lang` imports are skipped, generated code in `com.example` that references
     * `java.lang.String` will get `com.example.String` instead.
     */
    public val skipJavaLangImports: Boolean

    public val staticImports: Set<String>
    public val alwaysQualify: Set<String>

    public val indent: String

    public fun writeTo(out: Appendable, strategy: JavaWriteStrategy)

    public companion object {

        @JvmStatic
        public fun builder(packageName: PackageName, type: JavaTypeSpec): JavaFileBuilder =
            JavaFileBuilder(packageName, type)

    }
}

/**
 * Converts this [JavaFile] to a relative path string.
 * The relative path string is constructed by combining the package name path and the file name,
 * separated by the specified separator. The file name will include a `.java` extension if not provided.
 *
 * @param filename the name of the file. If not explicitly provided, defaults to the name of
 *                 the main type in the `JavaFile`. If no explicit name or main type name is available,
 *                 defaults to an empty string. If the provided file name does not include an extension,
 *                 `.java` is appended automatically.
 * @param separator the string to use as a separator for constructing the relative path. Defaults to `"/"`.
 * @return a string representing the relative path of this `JavaFile`, including the package path and the file name.
 */
public fun JavaFile.toRelativePath(filename: String = type.name ?: "", separator: String = "/"): String {
    val filenameWithExtension = if (filename.contains('.')) {
        filename
    } else {
        "$filename.java"
    }

    val packageName = this.packageName
    return if (packageName.isEmpty()) {
        filenameWithExtension
    } else {
        packageName.toRelativePath(separator) + separator + filenameWithExtension
    }
}


public class JavaFileBuilder internal constructor(
    public val packageName: PackageName,
    public val type: JavaTypeSpec,
) : BuilderDsl {
    private val fileComment = CodeValue.builder()
    private var skipJavaLangImports: Boolean = true
    private var indent: String = "    "
    private val staticImports = linkedSetOf<String>()
    private val secondaryTypes = mutableListOf<JavaTypeSpec>()

    public fun addFileComment(format: String, vararg argumentParts: CodeArgumentPart): JavaFileBuilder = apply {
        addFileComment(CodeValue(format, *argumentParts))
    }

    public fun addFileComment(codeValue: CodeValue): JavaFileBuilder = apply {
        fileComment.addCode(codeValue)
    }

    public fun addStaticImport(import: String): JavaFileBuilder = apply {
        staticImports.add(import)
    }

    public fun addStaticImport(className: ClassName, vararg names: String): JavaFileBuilder = apply {
        require(names.isNotEmpty()) { "`names` is empty" }
        for (name in names) {
            staticImports.add(className.canonicalName + "." + name)
        }
    }

    public fun addStaticImport(className: ClassName, names: Iterable<String>): JavaFileBuilder = apply {
        val iter = names.iterator()
        require(!iter.hasNext()) { "`names` is empty" }

        for (name in iter) {
            staticImports.add(className.canonicalName + "." + name)
        }
    }

    public fun skipJavaLangImports(skipJavaLangImports: Boolean): JavaFileBuilder = apply {
        this.skipJavaLangImports = skipJavaLangImports
    }

    public fun indent(indent: String): JavaFileBuilder = apply {
        this.indent = indent
    }

    public fun addSecondaryType(type: JavaTypeSpec): JavaFileBuilder = apply {
        secondaryTypes.add(type)
    }

    public fun addSecondaryTypes(types: Iterable<JavaTypeSpec>): JavaFileBuilder = apply {
        secondaryTypes.addAll(types)
    }

    public fun addSecondaryTypes(vararg types: JavaTypeSpec): JavaFileBuilder = apply {
        secondaryTypes.addAll(types)
    }

    public fun build(): JavaFile {
        val alwaysQualify = linkedSetOf<String>()

        return JavaFileImpl(
            fileComment = fileComment.build(),
            packageName = packageName,
            type = type,
            secondaryTypes = secondaryTypes.toList(),
            skipJavaLangImports = skipJavaLangImports,
            staticImports = LinkedHashSet(staticImports),
            alwaysQualify = alwaysQualify,
            indent = indent
        )
    }
}

public inline fun JavaFileBuilder.addFileComment(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): JavaFileBuilder = apply {
    addFileComment(CodeValue(format, block))
}

public inline fun JavaFile(
    packageName: PackageName,
    type: JavaTypeSpec,
    block: JavaFileBuilder.() -> Unit = {}
): JavaFile =
    JavaFile.builder(packageName, type).also(block).build()

public inline fun JavaFile(
    packageNamePaths: String,
    type: JavaTypeSpec,
    block: JavaFileBuilder.() -> Unit = {}
): JavaFile =
    JavaFile.builder(packageNamePaths.parseToPackageName(), type).also(block).build()

// Extensions for secondary types DSL

public inline fun JavaFileBuilder.addSecondarySimpleType(
    kind: JavaTypeSpec.Kind,
    name: String,
    block: JavaSimpleTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder = addSecondaryType(JavaSimpleTypeSpec(kind, name, block))

public inline fun JavaFileBuilder.addSecondaryClass(
    name: String,
    block: JavaSimpleTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder = addSecondarySimpleType(JavaTypeSpec.Kind.CLASS, name, block)

public inline fun JavaFileBuilder.addSecondaryInterface(
    name: String,
    block: JavaSimpleTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder = addSecondarySimpleType(JavaTypeSpec.Kind.INTERFACE, name, block)

public inline fun JavaFileBuilder.addSecondaryEnum(
    name: String,
    block: JavaEnumTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder = addSecondaryType(JavaEnumTypeSpec(name, block))

public inline fun JavaFileBuilder.addSecondaryAnnotationType(
    name: String,
    block: JavaAnnotationTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder = addSecondaryType(JavaAnnotationTypeSpec(name, block))

public inline fun JavaFileBuilder.addSecondaryRecord(
    name: String,
    block: JavaRecordTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder = addSecondaryType(JavaRecordTypeSpec(name, block))

public inline fun JavaFileBuilder.addSecondarySealedType(
    kind: JavaTypeSpec.Kind,
    name: String,
    block: JavaSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder = addSecondaryType(JavaSealedTypeSpec(kind, name, block))

public inline fun JavaFileBuilder.addSecondarySealedClass(
    name: String,
    block: JavaSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder = addSecondarySealedType(JavaTypeSpec.Kind.SEALED_CLASS, name, block)

public inline fun JavaFileBuilder.addSecondarySealedInterface(
    name: String,
    block: JavaSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder =
    addSecondarySealedType(JavaTypeSpec.Kind.SEALED_INTERFACE, name, block)

public inline fun JavaFileBuilder.addSecondaryNonSealedType(
    kind: JavaTypeSpec.Kind,
    name: String,
    block: JavaNonSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder = addSecondaryType(JavaNonSealedTypeSpec(kind, name, block))

public inline fun JavaFileBuilder.addSecondaryNonSealedClass(
    name: String,
    block: JavaNonSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder =
    addSecondaryNonSealedType(JavaTypeSpec.Kind.NON_SEALED_CLASS, name, block)

public inline fun JavaFileBuilder.addSecondaryNonSealedInterface(
    name: String,
    block: JavaNonSealedTypeSpec.Builder.() -> Unit = {}
): JavaFileBuilder =
    addSecondaryNonSealedType(JavaTypeSpec.Kind.NON_SEALED_INTERFACE, name, block)
