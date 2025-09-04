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
package love.forte.codegentle.java.writer

import love.forte.codegentle.common.code.CodePart.Companion.type
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.naming.*
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.utils.InternalMultisetApi
import love.forte.codegentle.common.utils.Multiset
import love.forte.codegentle.common.writer.*
import love.forte.codegentle.common.writer.CodeWriter.Companion.DEFAULT_COLUMN_LIMIT
import love.forte.codegentle.common.writer.CodeWriter.Companion.DEFAULT_INDENT
import love.forte.codegentle.java.InternalJavaCodeGentleApi
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.emitTo
import love.forte.codegentle.java.internal.isSourceIdentifier
import love.forte.codegentle.java.isJavaIdentifierStart
import love.forte.codegentle.java.naming.JavaPrimitiveTypeName
import love.forte.codegentle.java.naming.emitTo
import love.forte.codegentle.java.naming.isJavaLang
import love.forte.codegentle.java.ref.emitTo
import love.forte.codegentle.java.ref.javaOrNull
import love.forte.codegentle.java.spec.JavaTypeSpec
import love.forte.codegentle.java.strategy.DefaultJavaWriteStrategy
import love.forte.codegentle.java.strategy.JavaWriteStrategy
import love.forte.codegentle.java.strategy.ToStringJavaWriteStrategy
import love.forte.codegentle.java.writer.JavaTypeRefEmitOption.AnnotationOptions
import love.forte.codegentle.java.writer.JavaTypeRefEmitOption.TypeNameOptions


@OptIn(InternalWriterApi::class, InternalMultisetApi::class)
@InternalJavaCodeGentleApi
public class JavaCodeWriter private constructor(
    override val strategy: JavaWriteStrategy,
    override val indentValue: String,
    internal val out: LineWrapper,

    override val staticImports: Set<String> = emptySet(),
    override val alwaysQualify: Set<String> = emptySet(),
    internal val importedTypes: Map<String, ImportName> = emptyMap()
) : AbstractCodeWriter() {
    internal fun importedTypeName(name: String): TypeName? = importedTypes[name]?.type

    internal enum class CommentType(
        val isJavadoc: Boolean = false,
    ) {
        JAVADOC(isJavadoc = true),
        COMMENT;
    }


    internal var commentType: CommentType? = null

    internal var packageName: PackageName? = null

    // private val typeSpecStack: MutableList<TypeSpec> = mutableListOf()
    internal val typeSpecStack = ArrayDeque<JavaTypeSpec>()
    internal val currentTypeVariables: Multiset<String> = Multiset()

    /**
     * When emitting a statement, this is the line of the statement currently being written. The first
     * line of a statement is indented normally and subsequent wrapped lines are double-indented. This
     * is -1 when the currently-written line isn't part of a statement.
     */
    internal var statementLine: Int = -1

    internal fun pushPackage(packageName: PackageName) {
        check(this.packageName == null) { "package already set: ${this.packageName}" }
        this.packageName = packageName
    }

    internal fun popPackage() {
        check(this.packageName != null) { "package not set" }
        this.packageName = null
    }

    internal fun pushType(type: JavaTypeSpec) {
        this.typeSpecStack.addLast(type)
    }

    internal fun popType() {
        this.typeSpecStack.removeLast()
    }

    override fun emitComment(comment: CodeValue, vararg options: CodeValueEmitOption) {
        trailingNewline = true
        commentType = CommentType.COMMENT
        // comment = true
        try {
            comment.emitTo(this)
            // codeBlock.emit(this)
            emitNewLine()
        } finally {
            commentType = null
            // comment = false
        }
    }


    override fun emitDoc(doc: CodeValue, vararg options: CodeValueEmitOption) {
        if (doc.isEmpty()) return

        emit("/**${strategy.newline()}")
        this.commentType = CommentType.JAVADOC
        // this.javadoc = true
        try {
            doc.emitTo(this, true)
        } finally {
            this.commentType = null
            // this.javadoc = false
        }

        emit(" */${strategy.newline()}")
    }

    internal fun emitAnnotationRefs(annotations: Iterable<AnnotationRef>, inline: Boolean) {
        for (annotation in annotations) {
            annotation.emitTo(this)
            emit(if (inline) " " else strategy.newline())
        }
    }

    internal fun emitModifiers(modifiers: Set<JavaModifier>, implicitModifiers: Set<JavaModifier> = emptySet()) {
        if (modifiers.isEmpty()) return

        for (modifier in modifiers) {
            if (modifier in implicitModifiers) continue
            emitAndIndent(modifier.name.lowercase())
            emitAndIndent(" ")
        }
    }

    internal fun emitTypeVariableRefs(typeVariables: List<TypeRef<TypeVariableName>>) {
        if (typeVariables.isEmpty()) return

        typeVariables.forEach { typeVariable -> currentTypeVariables.add(typeVariable.typeName.name) }

        emit("<")
        var firstTypeVariable = true
        for (typeVariable in typeVariables) {
            if (!firstTypeVariable) emit(", ")
            emit(typeVariable)
            var firstBound = true
            for (bound in typeVariable.typeName.bounds) {
                emit(if (firstBound) " extends %V" else " & %V") {
                    type(bound)
                }
                firstBound = false
            }
            firstTypeVariable = false
        }
        emit(">")
    }

    internal fun popTypeVariableRefs(typeVariables: List<TypeRef<TypeVariableName>>) {
        typeVariables.forEach { typeVariableRef -> currentTypeVariables.remove(typeVariableRef.typeName.name) }
    }

    internal fun emitWrappingSpace() {
        out.wrappingSpace(indentLevel + 2)
    }

    internal fun extractMemberName(part: String): String {
        require(part[0].isJavaIdentifierStart()) { "not an identifier: $part" }
        for (i in 1..part.length) {
            if (!part.substring(0, i).isSourceIdentifier()) {
                return part.substring(0, i - 1)
            }
        }
        return part
    }

    internal fun emitStaticImportMember(canonical: String, part: String): Boolean {
        val partWithoutLeadingDot = part.substring(1)
        if (partWithoutLeadingDot.isEmpty()) return false
        val first = partWithoutLeadingDot[0]
        if (!first.isJavaIdentifierStart()) return false
        val explicit = canonical + "." + extractMemberName(partWithoutLeadingDot)
        val wildcard = "$canonical.*"
        if (staticImports.contains(explicit) || staticImports.contains(wildcard)) {
            emitAndIndent(partWithoutLeadingDot)
            return true
        }
        return false
    }

    internal fun emitLiteral(value: Any?) {
        when (value) {
            is AnnotationRef -> {
                emit(value)
            }

            is JavaCodeEmitter -> {
                value.emit(this)
            }

            is CodeValue -> {
                value.emitTo(this)
            }

            else -> emitAndIndent(value.toString())
        }
    }

    /**
     * Emits a ParameterizedTypeName in Java syntax.
     * Parameterized types in Java follow the pattern: RawType<TypeArg1, TypeArg2, ...>
     * For nested types: Outer<String>.Inner<Integer>
     */
    private fun emitParameterizedTypeName(parameterizedTypeName: ParameterizedTypeName) {
        val enclosingType = parameterizedTypeName.enclosingType
        if (enclosingType != null) {
            emit(enclosingType)
            emit(".")
            emit(parameterizedTypeName.rawType.simpleName)
        } else {
            emit(parameterizedTypeName.rawType)
        }

        // Emit type arguments if any
        if (parameterizedTypeName.typeArguments.isNotEmpty()) {
            emit("<")
            parameterizedTypeName.typeArguments.forEachIndexed { index, typeArg ->
                if (index > 0) emit(", ")
                emit(typeArg)
            }
            emit(">")
        }
    }

    /**
     * Emits a WildcardTypeName in Java syntax.
     * - EmptyWildcardTypeName: ? 
     * - LowerWildcardTypeName: ? extends T
     * - UpperWildcardTypeName: ? super T
     */
    private fun emitWildcardTypeName(wildcardTypeName: WildcardTypeName) {
        when (wildcardTypeName) {
            is EmptyWildcardTypeName -> {
                // Unbounded wildcard in Java
                emit("?")
            }

            is LowerWildcardTypeName -> {
                // Upper bounded wildcard: ? extends T
                emit("?")
                if (wildcardTypeName.bounds.isNotEmpty()) {
                    emit(" extends ")
                    wildcardTypeName.bounds.forEachIndexed { index, bound ->
                        if (index > 0) emit(" & ")
                        emit(bound)
                    }
                }
            }

            is UpperWildcardTypeName -> {
                // Lower bounded wildcard: ? super T
                emit("?")
                if (wildcardTypeName.bounds.isNotEmpty()) {
                    emit(" super ")
                    wildcardTypeName.bounds.forEachIndexed { index, bound ->
                        if (index > 0) emit(" & ")
                        emit(bound)
                    }
                }
            }
        }
    }

    override fun emit(s: String) {
        emitAndIndent(s)
    }

    override fun emit(code: CodeValue, vararg options: CodeValueEmitOption) {
        val ensureTrailingNewline = options.contains(JavaCodeValueEmitOption.EnsureTrailingNewline)
        code.emitTo(this, ensureTrailingNewline)
    }

    override fun emit(typeName: TypeName, vararg options: TypeNameEmitOption) {
        val isVararg = options.contains(JavaTypeNameEmitOption.Vararg)
        
        when (typeName) {
            is JavaPrimitiveTypeName -> {
                typeName.emitTo(this)
                if (isVararg) emit("...")
            }

            is ClassName -> {
                typeName.emitTo(this)
                if (isVararg) emit("...")
            }

            is ArrayTypeName -> {
                typeName.emitTo(this, isVararg)
            }

            is TypeVariableName -> {
                typeName.emitTo(this)
                if (isVararg) emit("...")
            }

            is ParameterizedTypeName -> {
                emitParameterizedTypeName(typeName)
                if (isVararg) emit("...")
            }

            is WildcardTypeName -> {
                emitWildcardTypeName(typeName)
                if (isVararg) emit("...")
            }

            is MemberName -> {
                typeName.emitTo(this)
                if (isVararg) emit("...")
            }

            else -> throw IllegalArgumentException("Unsupported TypeName for Java code writer $typeName (${typeName::class})")
        }
    }

    override fun emit(annotationRef: AnnotationRef, vararg options: AnnotationRefEmitOption) {
        annotationRef.emitTo(this)
    }

    override fun emit(typeRef: TypeRef<*>, vararg options: TypeRefEmitOption) {
        val typeNameOptions = mutableListOf<TypeNameEmitOption>()
        val annotationOptions = mutableListOf<AnnotationRefEmitOption>()

        for (option in options) {
            when (option) {
                is TypeNameOptions -> typeNameOptions.addAll(option.options)
                is AnnotationOptions -> annotationOptions.addAll(option.options)
            }
        }

        // typeRef.emitAnnotations(this)
        typeRef.status.javaOrNull?.annotations?.forEach { annotation ->
            // emit(annotation, CommonAnnotationRefEmitOption.Inline)
            emit(annotation, *annotationOptions.toTypedArray())
            emit(" ")
        }
        emit(typeRef.typeName, *typeNameOptions.toTypedArray())
    }

    internal fun emitAndIndent(s: String) {
        var first = true
        for (line in s.lineSequence()) {
            // Emit a newline character. Make sure blank lines in Javadoc & comments look good.
            if (!first) {
                // if ((javadoc || comment) && trailingNewline) {
                if (commentType != null && trailingNewline) {
                    emitIndentation()
                    out.append(if (commentType?.isJavadoc == true) " *" else "//")
                    // out.append(if (javadoc) " *" else "//")
                }
                out.append(strategy.newline())
                trailingNewline = true
                if (statementLine != -1) {
                    if (statementLine == 0) {
                        indent(2) // Begin multiple-line statement. Increase the indentation level.
                    }
                    statementLine++
                }
            }

            first = false
            if (line.isEmpty()) continue  // Don't indent empty lines.

            // Emit indentation and comment prefix if necessary.
            if (trailingNewline) {
                emitIndentation()
                when (commentType) {
                    CommentType.JAVADOC -> {
                        out.append(" * ")
                    }

                    CommentType.COMMENT -> {
                        out.append("// ")
                    }

                    null -> {}
                }
            }

            out.append(line)
            trailingNewline = false
        }
    }

    internal fun emitIndentation() {
        repeat(indentLevel) {
            out.append(indentValue)
        }
    }

    // internal fun suggestedImports(): Map<String, ClassName> {
    //     val result = LinkedHashMap(importableTypes)
    //     result.keys.removeAll(referencedNames)
    //     return result
    // }

    public companion object {
        internal fun create(
            out: Appendable,
            strategy: JavaWriteStrategy = DefaultJavaWriteStrategy()
        ): JavaCodeWriter {
            return create(
                strategy = strategy,
                out = out,
                indent = DEFAULT_INDENT,
                staticImports = emptySet(),
                alwaysQualify = emptySet()
            )
        }

        internal fun create(
            strategy: JavaWriteStrategy,
            out: Appendable,
            indent: String,
            staticImports: Set<String>,
            alwaysQualify: Set<String>,
        ): JavaCodeWriter {
            return create(
                strategy = strategy,
                out = out,
                indent = indent,
                importedTypes = emptyMap(),
                staticImports = staticImports,
                alwaysQualify = alwaysQualify
            )
        }

        internal fun create(
            strategy: JavaWriteStrategy,
            out: Appendable,
            indent: String,
            importedTypes: Map<String, ImportName>,
            staticImports: Set<String>,
            alwaysQualify: Set<String>
        ): JavaCodeWriter {
            return JavaCodeWriter(
                strategy = strategy,
                indentValue = indent,
                out = LineWrapper.create(out, indent, DEFAULT_COLUMN_LIMIT),
                importedTypes = importedTypes,
                staticImports = staticImports,
                alwaysQualify = alwaysQualify,
            )
        }
    }
}

internal inline fun JavaCodeWriter.inPackage(packageName: PackageName, block: () -> Unit) {
    pushPackage(packageName)
    try {
        block()
    } finally {
        popPackage()
    }
}

internal inline fun JavaCodeWriter.emit(
    format: String,
    vararg options: CodeValueEmitOption,
    block: CodeValueSingleFormatBuilderDsl = {}
) {
    emit(CodeValue(format, block), *options)
}

internal inline fun JavaCodeWriter.emit(format: String, block: CodeValueSingleFormatBuilderDsl = {}) {
    emit(CodeValue(format, block))
}

internal fun JavaCodeEmitter.emitToString(): String =
    buildString {
        this@emitToString.emit(
            JavaCodeWriter.create(
                out = this,
                strategy = ToStringJavaWriteStrategy
            )
        )
    }


public fun TypeRef<*>.writeToJavaString(strategy: JavaWriteStrategy = ToStringJavaWriteStrategy): String =
    buildString {
        JavaCodeWriter.create(out = this, strategy = strategy)
            .emit(this@writeToJavaString)
    }

public fun TypeName.writeToJavaString(strategy: JavaWriteStrategy = ToStringJavaWriteStrategy): String =
    when (this) {
        is MemberName -> writeToJavaString(strategy)
        else -> buildString {
            JavaCodeWriter.create(out = this, strategy = strategy)
                .emit(this@writeToJavaString)
        }
    }

/**
 * Extension function to write a [MemberName] to a Java string.
 * 
 * The behavior is different from [MemberName.emitTo]:
 * - For a member name with no enclosing class, it emits just the name
 * - For a member name with an enclosing class, it emits the class name and the member name
 * - For a member name with an enclosing class in the java.lang package, it emits the full canonical name if the strategy doesn't omit java.lang
 */
public fun MemberName.writeToJavaString(strategy: JavaWriteStrategy = ToStringJavaWriteStrategy): String =
    buildString {
        val writer = JavaCodeWriter.create(out = this, strategy = strategy)
        
        // If there's an enclosing class
        val enclosingClassName = enclosingClassName
        if (enclosingClassName != null) {
            // If it's in java.lang and the strategy is to omit it
            if (enclosingClassName.packageName.isJavaLang && strategy.omitJavaLangPackage()) {
                // Just emit the class name and member name
                writer.emit(enclosingClassName.simpleName)
                writer.emit(".")
                writer.emit(name)
            } else {
                // Emit the full canonical name
                enclosingClassName.emitTo(writer)
                writer.emit(".")
                writer.emit(name)
            }
        } else {
            // Just emit the name
            writer.emit(name)
        }
    }

public fun AnnotationRef.writeToJavaString(strategy: JavaWriteStrategy = ToStringJavaWriteStrategy): String =
    buildString {
        JavaCodeWriter.create(out = this, strategy = strategy)
            .emit(this@writeToJavaString)
    }

public fun CodeValue.writeToJavaString(strategy: JavaWriteStrategy = ToStringJavaWriteStrategy): String =
    buildString {
        JavaCodeWriter.create(out = this, strategy = strategy)
            .emit(this@writeToJavaString)
    }

public fun JavaCodeEmitter.writeToJavaString(strategy: JavaWriteStrategy = ToStringJavaWriteStrategy): String =
    buildString {
        val writer = JavaCodeWriter.create(out = this, strategy = strategy)
        this@writeToJavaString.emit(writer)
    }
