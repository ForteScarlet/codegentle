package love.forte.codegentle.internal.processor.enumset

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 * KSP processor for generating EnumSet implementations for enum classes annotated with @GenEnumSet.
 *
 * @author ForteScarlet
 */
internal class EnumSetProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val logger = environment.logger
    private val genEnumSetAnnotationName = "love.forte.codegentle.common.GenEnumSet"

    // List of Kotlin keywords that need to be escaped with backticks when used as identifiers
    private val kotlinKeywords = setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in", "interface", 
        "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try", "typealias", 
        "typeof", "val", "var", "when", "while", "by", "catch", "constructor", "delegate", "dynamic", 
        "field", "file", "finally", "get", "import", "init", "param", "property", "receiver", "set", 
        "setparam", "value", "where", "abstract", "actual", "annotation", "companion", "const", "crossinline", 
        "data", "enum", "expect", "external", "final", "infix", "inline", "inner", "internal", "lateinit", 
        "noinline", "open", "operator", "out", "override", "private", "protected", "public", "reified", 
        "sealed", "suspend", "tailrec", "vararg"
    )

    // Collected enum classes to process
    private val collectedEnums = mutableListOf<KSClassDeclaration>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Find all enum classes annotated with @GenEnumSet
        val symbols = resolver.getSymbolsWithAnnotation(genEnumSetAnnotationName)

        val enumClasses = symbols
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.ENUM_CLASS }
            .toList()

        logger.info("Found ${enumClasses.size} enum classes with @GenEnumSet annotation")

        collectedEnums.addAll(enumClasses)

        return emptyList()
    }

    override fun finish() {
        if (collectedEnums.isEmpty()) {
            logger.warn("No enum classes found with @GenEnumSet annotation, skipping code generation")
            return
        }

        for (enumClass in collectedEnums) {
            generateEnumSetImplementation(enumClass)
        }
    }

    private fun generateEnumSetImplementation(enumClass: KSClassDeclaration) {
        val packageName = enumClass.packageName.asString()
        val enumName = enumClass.simpleName.asString()

        // Get the enum entries to determine the appropriate EnumSet implementation
        val enumEntries = enumClass.declarations
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.ENUM_ENTRY }
            .toList()

        val enumSize = enumEntries.size
        logger.info("Enum $enumName has $enumSize entries")

        // Determine which EnumSet implementation to use based on the number of enum entries
        val baseClass = when {
            enumSize <= 32 -> "I32EnumSet"
            enumSize <= 64 -> "I64EnumSet"
            else -> "BigEnumSet"
        }

        // Get annotation parameters
        val annotation = enumClass.annotations
            .filter { it.annotationType.resolve().declaration.qualifiedName?.asString() == genEnumSetAnnotationName }
            .firstOrNull()

        // Get the internal flag from the annotation
        val isInternal = annotation
            ?.arguments
            ?.find { it.name?.asString() == "internal" }
            ?.value as? Boolean ?: false

        // Get custom names for the interfaces if provided
        val immutableName = annotation
            ?.arguments
            ?.find { it.name?.asString() == "immutableName" }
            ?.value as? String ?: ""

        val mutableName = annotation
            ?.arguments
            ?.find { it.name?.asString() == "mutableName" }
            ?.value as? String ?: ""

        // Get containerName and operatorsName
        val containerName = annotation
            ?.arguments
            ?.find { it.name?.asString() == "containerName" }
            ?.value as? String ?: ""

        val operatorsName = annotation
            ?.arguments
            ?.find { it.name?.asString() == "operatorsName" }
            ?.value as? String ?: ""

        // Get container adder function names
        val containerSingleAdder = annotation
            ?.arguments
            ?.find { it.name?.asString() == "containerSingleAdder" }
            ?.value as? String ?: ""

        val containerMultiAdder = annotation
            ?.arguments
            ?.find { it.name?.asString() == "containerMultiAdder" }
            ?.value as? String ?: ""

        // Determine visibility modifier
        val visibilityModifier = if (isInternal) "internal" else "public"

        // Generate the EnumSet interfaces and implementations
        generateEnumSetCode(
            enumClass = enumClass, 
            enumName = enumName, 
            packageName = packageName, 
            baseClass = baseClass, 
            visibilityModifier = visibilityModifier,
            immutableName = immutableName,
            mutableName = mutableName,
            containerName = containerName,
            operatorsName = operatorsName,
            containerSingleAdder = containerSingleAdder,
            containerMultiAdder = containerMultiAdder
        )
    }

    private fun generateEnumSetCode(
        enumClass: KSClassDeclaration,
        enumName: String,
        packageName: String,
        baseClass: String,
        visibilityModifier: String,
        immutableName: String,
        mutableName: String,
        containerName: String,
        operatorsName: String,
        containerSingleAdder: String,
        containerMultiAdder: String
    ) {
        // Determine the actual interface names to use (custom or default)
        val actualImmutableName = immutableName.ifEmpty { "${enumName}Set" }
        val actualMutableName = mutableName.ifEmpty { "Mutable${enumName}Set" }

        // Define implementation class names
        val immutableImplName = "${enumName}SetImpl"
        val mutableImplName = "Mutable${enumName}SetImpl"

        val dependencies = Dependencies(true, enumClass.containingFile!!)

        val outputFile = environment.codeGenerator.createNewFile(
            dependencies,
            packageName,
            actualImmutableName
        )

        outputFile.bufferedWriter().use { writer ->
            writeFileHeader(writer, packageName, enumClass, baseClass)
            writeImmutableInterface(writer, enumName, actualImmutableName, actualMutableName, visibilityModifier, immutableImplName, mutableImplName, baseClass)
            writeMutableInterface(writer, enumName, actualImmutableName, actualMutableName, visibilityModifier, mutableImplName)
            writeImmutableImplementation(writer, enumName, actualImmutableName, immutableImplName, mutableImplName, baseClass)
            writeMutableImplementation(writer, enumName, actualMutableName, immutableImplName, mutableImplName, baseClass)

            // Generate container interface if containerName is not empty
            if (containerName.isNotEmpty()) {
                writeContainerInterface(writer, enumName, containerName, containerSingleAdder, containerMultiAdder)
                
                // Generate value class if operatorsName is not empty
                if (operatorsName.isNotEmpty()) {
                    writeOperatorsValueClass(writer, enumClass, enumName, containerName, operatorsName, containerSingleAdder)
                }
            }
        }

        logger.info("Generated EnumSet implementation for $enumName")
    }

    private fun writeImmutableInterface(
        writer: java.io.BufferedWriter,
        enumName: String,
        actualImmutableName: String,
        actualMutableName: String,
        visibilityModifier: String,
        immutableImplName: String,
        mutableImplName: String,
        baseClass: String
    ) {
        // Generate the immutable EnumSet interface
        writer.write("/**\n")
        writer.write(" * A set implementation for the $enumName enum.\n")
        writer.write(" * @see $enumName\n")
        writer.write(" */\n")
        writer.write("@OptIn(InternalEnumSetApi::class)\n")
        writer.write("$visibilityModifier interface $actualImmutableName : Set<$enumName> {\n")
        writer.write("    fun mutable(): $actualMutableName\n\n")
        
        // Add the new functions
        writer.write("    /**\n")
        writer.write("     * Checks if this set contains any of the elements in the specified collection.\n")
        writer.write("     */\n")
        writer.write("    fun containsAny(elements: Collection<$enumName>): Boolean\n\n")
        
        writer.write("    /**\n")
        writer.write("     * Returns a new set containing elements present in both this set and [other].\n")
        writer.write("     */\n")
        writer.write("    fun intersect(other: Set<$enumName>): Set<$enumName>\n\n")
        
        writer.write("    /**\n")
        writer.write("     * Returns a new set containing elements present in either this set or [other].\n")
        writer.write("     */\n")
        writer.write("    fun union(other: Set<$enumName>): Set<$enumName>\n\n")
        
        writer.write("    /**\n")
        writer.write("     * Returns a new set containing elements present in this set but not in [other].\n")
        writer.write("     */\n")
        writer.write("    fun difference(other: Set<$enumName>): Set<$enumName>\n\n")

        writer.write("    companion object {\n")
        writer.write("        fun empty(): $actualImmutableName = $immutableImplName()\n")

        // Generate the of() factory methods
        writer.write("        fun of(vararg entries: $enumName): $actualImmutableName {\n")
        writeFactoryMethodBody(writer, baseClass, immutableImplName, mutableImplName)
        writer.write("        }\n\n")

        // Generate the of(Collection) factory method
        writer.write("        fun of(entries: Collection<$enumName>): $actualImmutableName {\n")
        writeFactoryMethodBody(writer, baseClass, immutableImplName, mutableImplName)
        writer.write("        }\n")
        writer.write("    }\n")
        writer.write("}\n\n")
    }

    private fun writeMutableInterface(
        writer: java.io.BufferedWriter,
        enumName: String,
        actualImmutableName: String,
        actualMutableName: String,
        visibilityModifier: String,
        mutableImplName: String
    ) {
        // Generate the mutable EnumSet interface
        writer.write("/**\n")
        writer.write(" * A mutable set implementation for the ${enumName} enum.\n")
        writer.write(" * @see $enumName\n")
        writer.write(" */\n")
        writer.write("@OptIn(InternalEnumSetApi::class)\n")
        writer.write("$visibilityModifier interface $actualMutableName : MutableSet<$enumName> {\n")
        writer.write("    fun immutable(): $actualImmutableName\n\n")
        
        // Add the new functions
        writer.write("    /**\n")
        writer.write("     * Checks if this set contains any of the elements in the specified collection.\n")
        writer.write("     */\n")
        writer.write("    fun containsAny(elements: Collection<$enumName>): Boolean\n\n")
        
        writer.write("    /**\n")
        writer.write("     * Returns a new set containing elements present in both this set and [other].\n")
        writer.write("     */\n")
        writer.write("    fun intersect(other: Set<$enumName>): Set<$enumName>\n\n")
        
        writer.write("    /**\n")
        writer.write("     * Returns a new set containing elements present in either this set or [other].\n")
        writer.write("     */\n")
        writer.write("    fun union(other: Set<$enumName>): Set<$enumName>\n\n")
        
        writer.write("    /**\n")
        writer.write("     * Returns a new set containing elements present in this set but not in [other].\n")
        writer.write("     */\n")
        writer.write("    fun difference(other: Set<$enumName>): Set<$enumName>\n\n")

        writer.write("    companion object {\n")
        writer.write("        fun empty(): $actualMutableName = $mutableImplName()\n")

        // Generate the of() factory methods
        writer.write("        fun of(vararg entries: $enumName): $actualMutableName {\n")
        writer.write("            return $mutableImplName().also {\n")
        writer.write("                if (entries.isNotEmpty()) {\n")
        writer.write("                    it.addAll(entries)\n")
        writer.write("                }\n")
        writer.write("            }\n")
        writer.write("        }\n\n")

        // Generate the of(Collection) factory method
        writer.write("        fun of(entries: Collection<$enumName>): $actualMutableName {\n")
        writer.write("            return $mutableImplName().also {\n")
        writer.write("                it.addAll(entries)\n")
        writer.write("            }\n")
        writer.write("        }\n")
        writer.write("    }\n")
        writer.write("}\n\n")
    }

    private fun writeImmutableImplementation(
        writer: java.io.BufferedWriter,
        enumName: String,
        actualImmutableName: String,
        immutableImplName: String,
        mutableImplName: String,
        baseClass: String
    ) {
        // Generate the immutable EnumSet implementation
        writer.write("@OptIn(InternalEnumSetApi::class)\n")

        // Generate different implementation based on the base class
        when (baseClass) {
            "I32EnumSet" -> {
                writer.write("private class $immutableImplName(bitset: UInt = 0u) : $actualImmutableName, $baseClass<$enumName>() {\n")
                writer.write("    override var bitset: UInt = bitset\n")
            }
            "I64EnumSet" -> {
                writer.write("private class $immutableImplName(bitset: ULong = 0uL) : $actualImmutableName, $baseClass<$enumName>() {\n")
                writer.write("    override var bitset: ULong = bitset\n")
            }
            "BigEnumSet" -> {
                writer.write("private class $immutableImplName(bitset: LongArray = LongArray(0)) : $actualImmutableName, $baseClass<$enumName>() {\n")
                writer.write("    override var bitset: LongArray = bitset\n")
            }
        }

        writer.write("        set(_) {\n")
        writer.write("            throw UnsupportedOperationException(\"Cannot modify the immutable set.\")\n")
        writer.write("        }\n\n")

        writer.write("    override val entries: List<$enumName>\n")
        writer.write("        get() = $enumName.entries\n\n")

        writeNewByMethod(writer, baseClass, immutableImplName)
        
        // Add overrides for the four methods
        writer.write("    override fun mutable(): $mutableImplName =\n")
        writeMutableConversion(writer, baseClass, mutableImplName)

        writer.write("}\n\n")
    }

    private fun writeMutableImplementation(
        writer: java.io.BufferedWriter,
        enumName: String,
        actualMutableName: String,
        immutableImplName: String,
        mutableImplName: String,
        baseClass: String
    ) {
        // Generate the mutable EnumSet implementation
        writer.write("@OptIn(InternalEnumSetApi::class)\n")

        // Generate different implementation based on the base class
        when (baseClass) {
            "I32EnumSet" -> {
                writer.write("private class $mutableImplName(bitset: UInt = 0u) : $actualMutableName, $baseClass<$enumName>(bitset) {\n")
            }
            "I64EnumSet" -> {
                writer.write("private class $mutableImplName(bitset: ULong = 0uL) : $actualMutableName, $baseClass<$enumName>(bitset) {\n")
            }
            "BigEnumSet" -> {
                writer.write("private class $mutableImplName(bitset: LongArray = LongArray(0)) : $actualMutableName, $baseClass<$enumName>(bitset) {\n")
            }
        }

        writer.write("    override val entries: List<$enumName>\n")
        writer.write("        get() = $enumName.entries\n\n")

        writeNewByMethod(writer, baseClass, mutableImplName)

        // Add overrides for the four methods
        writer.write("    override fun immutable(): $immutableImplName =\n")
        writeImmutableConversion(writer, baseClass, immutableImplName)

        writer.write("}\n")
    }

    private fun writeImmutableConversion(
        writer: java.io.BufferedWriter,
        baseClass: String,
        immutableImplName: String
    ) {
        when (baseClass) {
            "I32EnumSet", "I64EnumSet" -> {
                writer.write("        $immutableImplName(bitset)\n")
            }
            "BigEnumSet" -> {
                writer.write("        $immutableImplName(bitset.copyOf())\n")
            }
        }
    }

    private fun writeNewByMethod(
        writer: java.io.BufferedWriter,
        baseClass: String,
        implName: String
    ) {
        when (baseClass) {
            "I32EnumSet" -> {
                writer.write("    override fun newBy(bitset: UInt): $implName = $implName(bitset)\n\n")
            }
            "I64EnumSet" -> {
                writer.write("    override fun newBy(bitset: ULong): $implName = $implName(bitset)\n\n")
            }
            "BigEnumSet" -> {
                writer.write("    override fun newBy(bitset: LongArray): $implName = $implName(bitset.copyOf())\n\n")
            }
        }
    }

    private fun writeMutableConversion(
        writer: java.io.BufferedWriter,
        baseClass: String,
        mutableImplName: String
    ) {
        when (baseClass) {
            "I32EnumSet", "I64EnumSet" -> {
                writer.write("        $mutableImplName(bitset)\n")
            }
            "BigEnumSet" -> {
                writer.write("        $mutableImplName(bitset.copyOf())\n")
            }
        }
    }

    private fun writeFactoryMethodBody(
        writer: java.io.BufferedWriter,
        baseClass: String,
        immutableImplName: String,
        mutableImplName: String
    ) {
        when (baseClass) {
            "I32EnumSet" -> {
                writer.write("            var bits = 0u\n")
                writer.write("            for (entry in entries) {\n")
                writer.write("                bits = bits or (1u shl entry.ordinal)\n")
                writer.write("            }\n")
                writer.write("            return $immutableImplName(bits)\n")
            }
            "I64EnumSet" -> {
                writer.write("            var bits = 0uL\n")
                writer.write("            for (entry in entries) {\n")
                writer.write("                bits = bits or (1uL shl entry.ordinal)\n")
                writer.write("            }\n")
                writer.write("            return $immutableImplName(bits)\n")
            }
            "BigEnumSet" -> {
                writer.write("            val result = $mutableImplName()\n")
                writer.write("            for (entry in entries) {\n")
                writer.write("                result.add(entry)\n")
                writer.write("            }\n")
                writer.write("            return result.immutable()\n")
            }
        }
    }

    private fun writeContainerInterface(
        writer: java.io.BufferedWriter,
        enumName: String,
        containerName: String,
        containerSingleAdder: String,
        containerMultiAdder: String
    ) {
        // Determine the actual adder function names to use (custom or default)
        val actualSingleAdder = containerSingleAdder.ifEmpty { "addModifier" }
        val actualMultiAdder = containerMultiAdder.ifEmpty { "addModifiers" }

        writer.write("\n/**\n")
        writer.write(" * A builder container for $enumName modifiers.\n")
        writer.write(" */\n")
        writer.write("public interface $containerName<B : $containerName<B>> {\n")
        writer.write("    public fun $actualSingleAdder(modifier: $enumName): B\n")
        writer.write("    public fun $actualMultiAdder(vararg modifiers: $enumName): B\n")
        writer.write("    public fun $actualMultiAdder(modifiers: Iterable<$enumName>): B\n")
        writer.write("}\n")
    }

    private fun writeOperatorsValueClass(
        writer: java.io.BufferedWriter,
        enumClass: KSClassDeclaration,
        enumName: String,
        containerName: String,
        operatorsName: String,
        containerSingleAdder: String
    ) {
        // Determine the actual adder function name to use (custom or default)
        val actualSingleAdder = containerSingleAdder.ifEmpty { "addModifier" }

        writer.write("\n@kotlin.jvm.JvmInline\n")
        writer.write("public value class $operatorsName\n")
        writer.write("@PublishedApi internal constructor(private val container: $containerName<*>) {\n")

        // Generate methods for each enum entry
        for (entry in enumClass.declarations
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.ENUM_ENTRY }) {
            val entryName = entry.simpleName.asString()
            val methodName = entryName.lowercase()

            // Check if the method name is a Kotlin keyword, if so wrap it in backticks
            val safeMethodName = if (methodName in kotlinKeywords) "`$methodName`" else methodName

            writer.write("    public fun $safeMethodName() {\n")
            writer.write("        container.$actualSingleAdder($enumName.$entryName)\n")
            writer.write("    }\n\n")
        }

        writer.write("}\n")
    }

    private fun writeFileHeader(
        writer: java.io.BufferedWriter,
        packageName: String,
        enumClass: KSClassDeclaration,
        baseClass: String
    ) {
        writer.write("// Generated by EnumSetProcessor at ${nowTime()}\n")
        writer.write("@file:Suppress(\"ALL\", \"RedundantVisibilityModifier\", \"unused\")\n\n")
        writer.write("package $packageName\n\n")

        // Add imports
        writer.write("import ${enumClass.qualifiedName?.asString()}\n")
        writer.write("import love.forte.codegentle.common.utils.$baseClass\n")
        writer.write("import love.forte.codegentle.common.utils.InternalEnumSetApi\n\n")
    }

    private fun nowTime(): String {
        val utc = ZoneId.of("UTC")
        return DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.FULL)
            .withZone(utc)
            .localizedBy(Locale.ROOT)
            .format(ZonedDateTime.now(utc))
    }
}
