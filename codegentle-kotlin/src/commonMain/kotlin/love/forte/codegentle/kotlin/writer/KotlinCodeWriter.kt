package love.forte.codegentle.kotlin.writer

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.computeValue
import love.forte.codegentle.common.naming.*
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.writer.*
import love.forte.codegentle.common.writer.CodeWriter.Companion.DEFAULT_COLUMN_LIMIT
import love.forte.codegentle.common.writer.CodeWriter.Companion.DEFAULT_INDENT
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.emitTo
import love.forte.codegentle.kotlin.naming.emitTo
import love.forte.codegentle.kotlin.ref.emitTo
import love.forte.codegentle.kotlin.ref.kotlinOrNull
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
import love.forte.codegentle.kotlin.strategy.DefaultKotlinWriteStrategy
import love.forte.codegentle.kotlin.strategy.KotlinWriteStrategy
import love.forte.codegentle.kotlin.strategy.ToStringKotlinWriteStrategy

/**
 * A code writer for generating Kotlin code.
 *
 * @author ForteScarlet
 */
@OptIn(InternalWriterApi::class)
public class KotlinCodeWriter private constructor(
    override val strategy: KotlinWriteStrategy,
    override val indentValue: String,
    internal val out: LineWrapper,

    override val staticImports: Set<String> = emptySet(),
    override val alwaysQualify: Set<String> = emptySet(),
    internal val importedTypes: Map<String, ClassName> = emptyMap()
) : AbstractCodeWriter() {

    override fun emitComment(
        comment: CodeValue,
        vararg options: CodeValueEmitOption
    ) {
        trailingNewline = true
        commentType = CommentType.COMMENT
        try {
            comment.emitTo(this)
            emit(strategy.newline())
        } finally {
            commentType = null
        }
    }

    override fun emitDoc(
        doc: CodeValue,
        vararg options: CodeValueEmitOption
    ) {
        if (doc.isEmpty()) return

        emit("/**${strategy.newline()}")
        this.commentType = CommentType.DOC
        try {
            doc.emitTo(this, true)
        } finally {
            this.commentType = null
        }

        emit(" */${strategy.newline()}")
    }

    override fun emit(
        code: CodeValue,
        vararg options: CodeValueEmitOption
    ) {
        val ensureTrailingNewline = options.contains(KotlinCodeValueEmitOption.EnsureTrailingNewline)
        code.emitTo(this, ensureTrailingNewline)
    }

    internal fun emitStaticImportMember(canonical: String, part: String): Boolean {
        val partWithoutLeadingDot = part.substring(1)
        if (partWithoutLeadingDot.isEmpty()) return false
        val first = partWithoutLeadingDot[0]
        if (!strategy.isIdentifier(first.toString())) return false
        val explicit = canonical + "." + extractMemberName(partWithoutLeadingDot)
        val wildcard = "$canonical.*"
        if (staticImports.contains(explicit) || staticImports.contains(wildcard)) {
            emitAndIndent(partWithoutLeadingDot)
            return true
        }
        return false
    }

    internal fun extractMemberName(part: String): String {
        require(strategy.isIdentifier(part[0].toString())) { "not an identifier: $part" }
        for (i in 1..part.length) {
            if (!strategy.isIdentifier(part.substring(0, i))) {
                return part.substring(0, i - 1)
            }
        }
        return part
    }

    internal fun emitLiteral(value: Any?) {
        when (value) {
            is AnnotationRef -> {
                emit(value, CommonAnnotationRefEmitOption.Inline)
            }

            else -> emitAndIndent(value.toString())
        }
    }

    override fun emit(
        typeName: TypeName,
        vararg options: TypeNameEmitOption
    ) {
        when (typeName) {
            is ClassName -> {
                // Use the emitTo extension function to handle imports and qualified names
                typeName.emitTo(this)
            }

            is ArrayTypeName -> {
                emitArrayTypeName(typeName)
            }

            is TypeVariableName -> {
                emitTypeVariableName(typeName)
            }

            is ParameterizedTypeName -> {
                emitParameterizedTypeName(typeName)
            }

            is WildcardTypeName -> {
                emitWildcardTypeName(typeName)
            }

            else -> throw IllegalArgumentException("Unsupported TypeName for Kotlin code writer $typeName (${typeName::class})")

        }
    }

    /**
     * Emits an ArrayTypeName in Kotlin syntax.
     * In Kotlin, arrays are represented as Array<T> for object types or specialized arrays like IntArray.
     */
    private fun emitArrayTypeName(arrayTypeName: ArrayTypeName) {
        val componentType = arrayTypeName.componentType.typeName

        // Handle primitive array types with specialized Kotlin array classes
        when (componentType) {
            is ClassName -> {
                when (componentType.canonicalName) {
                    "kotlin.Byte" -> emit("ByteArray")
                    "kotlin.Short" -> emit("ShortArray")
                    "kotlin.Int" -> emit("IntArray")
                    "kotlin.Long" -> emit("LongArray")
                    "kotlin.Float" -> emit("FloatArray")
                    "kotlin.Double" -> emit("DoubleArray")
                    "kotlin.Boolean" -> emit("BooleanArray")
                    "kotlin.Char" -> emit("CharArray")
                    else -> {
                        // Generic array type
                        emit("Array<")
                        emit(arrayTypeName.componentType)
                        emit(">")
                    }
                }
            }

            else -> {
                // For non-primitive types, use Array<T>
                emit("Array<")
                emit(arrayTypeName.componentType)
                emit(">")
            }
        }
    }

    /**
     * Emits a TypeVariableName in Kotlin syntax.
     * Type variables in Kotlin are represented by their name, with bounds handled separately in declarations.
     */
    private fun emitTypeVariableName(typeVariableName: TypeVariableName) {
        emit(typeVariableName.name)
    }

    /**
     * Emits a ParameterizedTypeName in Kotlin syntax.
     * Parameterized types in Kotlin follow the pattern: RawType<TypeArg1, TypeArg2, ...>
     */
    private fun emitParameterizedTypeName(parameterizedTypeName: ParameterizedTypeName) {
        // Emit the raw type
        emit(parameterizedTypeName.rawType)

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
     * Emits a WildcardTypeName in Kotlin syntax.
     * Kotlin uses variance annotations: 'out T' for covariance, 'in T' for contravariance, and '*' for star projection.
     */
    private fun emitWildcardTypeName(wildcardTypeName: WildcardTypeName) {
        when (wildcardTypeName) {
            is EmptyWildcardTypeName -> {
                // Star projection in Kotlin
                emit("*")
            }

            is LowerWildcardTypeName -> {
                // Covariance: out T (equivalent to Java's ? extends T)
                emit("out ")
                if (wildcardTypeName.bounds.isNotEmpty()) {
                    emit(wildcardTypeName.bounds.first())
                }
            }

            is UpperWildcardTypeName -> {
                // Contravariance: in T (equivalent to Java's ? super T)
                emit("in ")
                if (wildcardTypeName.bounds.isNotEmpty()) {
                    emit(wildcardTypeName.bounds.first())
                }
            }

            else -> {
                // Fallback for unknown wildcard types
                emit("*")
            }
        }
    }

    override fun emit(
        typeRef: TypeRef<*>,
        vararg options: TypeRefEmitOption
    ) {
        val typeNameOptions = mutableListOf<TypeNameEmitOption>()
        val annotationOptions = mutableListOf<AnnotationRefEmitOption>()

        // TODO KotlinTypeRefEmitOption?
        // for (option in options) {
        //     when (option) {
        //         is KotlinTypeRefEmitOption.TypeNameOptions -> typeNameOptions.addAll(option.options)
        //         is KotlinTypeRefEmitOption.AnnotationOptions -> annotationOptions.addAll(option.options)
        //         else -> {} // Ignore unknown options
        //     }
        // }

        // Emit annotations if any
        typeRef.status.kotlinOrNull?.annotations?.forEach { annotation ->
            emit(annotation, *annotationOptions.toTypedArray())
            emit(" ")
        }

        // Emit the type name
        emit(typeRef.typeName, *typeNameOptions.toTypedArray())
    }

    override fun emit(
        annotationRef: AnnotationRef,
        vararg options: AnnotationRefEmitOption
    ) {
        val inline = options.contains(CommonAnnotationRefEmitOption.Inline)

        emit("@")
        emit(annotationRef.typeName)

        // In a more complete implementation, we would handle annotation parameters
    }

    override fun emit(s: String) {
        emitAndIndent(s)
    }

    internal fun emitAndIndent(s: String) {
        var first = true
        for (line in s.lineSequence()) {
            // Emit a newline character. Make sure blank lines in comments look good.
            if (!first) {
                if (commentType != null && trailingNewline) {
                    emitIndentation()
                    out.append(if (commentType == CommentType.DOC) " *" else "//")
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
                    CommentType.DOC -> {
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

    internal enum class CommentType {
        DOC,
        COMMENT;
    }

    internal var commentType: CommentType? = null
    internal var statementLine: Int = -1
    internal var packageName: PackageName? = null

    // simple name -> class name
    internal val importableTypes: MutableMap<String, ClassName> = linkedMapOf()
    internal val referencedNames: MutableSet<String> = linkedSetOf()

    // Stack of type specs being processed
    internal val typeSpecStack = ArrayDeque<KotlinTypeSpec>()
    internal val currentTypeVariables: Multiset<String> = Multiset()

    internal fun pushPackage(packageName: PackageName) {
        check(this.packageName == null) { "package already set: ${this.packageName}" }
        this.packageName = packageName
    }

    internal fun popPackage() {
        check(this.packageName != null) { "package not set" }
        this.packageName = null
    }

    internal fun pushType(type: KotlinTypeSpec) {
        this.typeSpecStack.addLast(type)
    }

    internal fun popType() {
        this.typeSpecStack.removeLast()
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
                if (firstBound) {
                    emit(" : ")
                } else {
                    emit(" & ")
                }
                emit(bound)
                firstBound = false
            }
            firstTypeVariable = false
        }
        emit(">")
    }

    internal fun popTypeVariableRefs(typeVariableRefs: List<TypeRef<TypeVariableName>>) {
        typeVariableRefs.forEach { typeVariableRef -> currentTypeVariables.remove(typeVariableRef.typeName.name) }
    }

    internal fun emitModifiers(modifiers: Set<KotlinModifier>, implicitModifiers: Set<KotlinModifier> = emptySet()) {
        if (modifiers.isEmpty()) return

        for (modifier in modifiers) {
            if (modifier in implicitModifiers) continue
            emitAndIndent(modifier.keyword)
            emitAndIndent(" ")
        }
    }

    internal fun emitAnnotationRefs(annotations: Iterable<AnnotationRef>, inline: Boolean) {
        for (annotation in annotations) {
            annotation.emitTo(this)
            emit(if (inline) " " else strategy.newline())
        }
    }

    internal fun emitWrappingSpace() {
        out.wrappingSpace(indentLevel + 2)
    }

    public companion object {
        internal fun create(
            out: Appendable,
            strategy: KotlinWriteStrategy = DefaultKotlinWriteStrategy
        ): KotlinCodeWriter {
            return create(
                strategy = strategy,
                out = out,
                indent = DEFAULT_INDENT,
                staticImports = emptySet(),
                alwaysQualify = emptySet()
            )
        }

        internal fun create(
            strategy: KotlinWriteStrategy,
            out: Appendable,
            indent: String,
            staticImports: Set<String>,
            alwaysQualify: Set<String>,
        ): KotlinCodeWriter {
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
            strategy: KotlinWriteStrategy,
            out: Appendable,
            indent: String,
            importedTypes: Map<String, ClassName>,
            staticImports: Set<String>,
            alwaysQualify: Set<String>
        ): KotlinCodeWriter {
            return KotlinCodeWriter(
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

internal class Multiset<T> {
    private val map = linkedMapOf<T, Int>()

    fun add(t: T) {
        map.computeValue(t) { _, old ->
            old?.plus(1) ?: 1
        }
    }

    fun remove(t: T) {
        map.computeValue(t) { _, old ->
            // 如果-1后小于等于0，移除，否则保存计算结果
            old?.minus(1)?.takeIf { value -> value > 0 }
        }
    }

    fun contains(t: T): Boolean {
        return (map[t] ?: 0) > 0
    }
}

internal inline fun KotlinCodeWriter.inType(type: KotlinTypeSpec, block: KotlinCodeWriter.() -> Unit) {
    pushType(type)
    try {
        block()
    } finally {
        popType()
    }
}

internal inline fun KotlinCodeWriter.inPackage(packageName: PackageName, block: () -> Unit) {
    pushPackage(packageName)
    try {
        block()
    } finally {
        popPackage()
    }
}

internal inline fun KotlinCodeWriter.emit(
    format: String,
    vararg options: CodeValueEmitOption,
    block: CodeValueSingleFormatBuilderDsl = {}
) {
    emit(CodeValue(format, block), *options)
}

internal inline fun KotlinCodeWriter.emit(format: String, block: CodeValueSingleFormatBuilderDsl = {}) {
    emit(CodeValue(format, block))
}

internal fun KotlinCodeEmitter.emitToString(): String =
    buildString {
        emit(
            KotlinCodeWriter.create(
                out = this,
                strategy = ToStringKotlinWriteStrategy
            )
        )
    }

public fun TypeRef<*>.writeToKotlinString(strategy: KotlinWriteStrategy = ToStringKotlinWriteStrategy): String =
    buildString {
        KotlinCodeWriter.create(out = this, strategy = strategy)
            .emit(this@writeToKotlinString)
    }

public fun TypeName.writeToKotlinString(strategy: KotlinWriteStrategy = ToStringKotlinWriteStrategy): String =
    buildString {
        KotlinCodeWriter.create(out = this, strategy = strategy)
            .emit(this@writeToKotlinString)
    }

public fun AnnotationRef.writeToKotlinString(strategy: KotlinWriteStrategy = ToStringKotlinWriteStrategy): String =
    buildString {
        KotlinCodeWriter.create(out = this, strategy = strategy)
            .emit(this@writeToKotlinString)
    }

public fun CodeValue.writeToKotlinString(strategy: KotlinWriteStrategy = ToStringKotlinWriteStrategy): String =
    buildString {
        KotlinCodeWriter.create(out = this, strategy = strategy)
            .emit(this@writeToKotlinString)
    }

public fun KotlinCodeEmitter.writeToKotlinString(strategy: KotlinWriteStrategy = ToStringKotlinWriteStrategy): String =
    buildString {
        val writer = KotlinCodeWriter.create(out = this, strategy = strategy)
        this@writeToKotlinString.emit(writer)
    }
