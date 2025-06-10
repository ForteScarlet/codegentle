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
            mutableName = mutableName
        )
    }

    private fun generateEnumSetCode(
        enumClass: KSClassDeclaration,
        enumName: String,
        packageName: String,
        baseClass: String,
        visibilityModifier: String,
        immutableName: String,
        mutableName: String
    ) {
        // Determine the actual interface names to use (custom or default)
        val actualImmutableName = if (immutableName.isNotEmpty()) immutableName else "${enumName}Set"
        val actualMutableName = if (mutableName.isNotEmpty()) mutableName else "Mutable${enumName}Set"

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
            writer.write("// Generated by EnumSetProcessor at ${nowTime()}\n")
            writer.write("@file:Suppress(\"ALL\", \"RedundantVisibilityModifier\", \"unused\")\n\n")
            writer.write("package $packageName\n\n")

            // Add imports
            writer.write("import ${enumClass.qualifiedName?.asString()}\n")
            writer.write("import love.forte.codegentle.common.utils.$baseClass\n")
            writer.write("import love.forte.codegentle.common.utils.InternalEnumSetApi\n\n")

            // Generate the immutable EnumSet interface
            writer.write("/**\n")
            writer.write(" * A set implementation for the ${enumName} enum.\n")
            writer.write(" * @see $enumName\n")
            writer.write(" */\n")
            writer.write("@OptIn(InternalEnumSetApi::class)\n")
            writer.write("$visibilityModifier interface $actualImmutableName : Set<$enumName> {\n")
            writer.write("    fun mutable(): $actualMutableName\n\n")

            writer.write("    companion object {\n")
            writer.write("        fun empty(): $actualImmutableName = $immutableImplName()\n")

            // Generate the of() factory methods
            writer.write("        fun of(vararg entries: $enumName): $actualImmutableName {\n")

            // Generate different implementation based on the base class
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

            writer.write("        }\n\n")

            // Generate the of(Collection) factory method
            writer.write("        fun of(entries: Collection<$enumName>): $actualImmutableName {\n")

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

            writer.write("        }\n")
            writer.write("    }\n")
            writer.write("}\n\n")

            // Generate the mutable EnumSet interface
            writer.write("/**\n")
            writer.write(" * A mutable set implementation for the ${enumName} enum.\n")
            writer.write(" * @see $enumName\n")
            writer.write(" */\n")
            writer.write("@OptIn(InternalEnumSetApi::class)\n")
            writer.write("$visibilityModifier interface $actualMutableName : MutableSet<$enumName> {\n")
            writer.write("    fun immutable(): $actualImmutableName\n\n")

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

            writer.write("    override fun mutable(): $mutableImplName =\n")

            when (baseClass) {
                "I32EnumSet", "I64EnumSet" -> {
                    writer.write("        $mutableImplName(bitset)\n")
                }
                "BigEnumSet" -> {
                    writer.write("        $mutableImplName(bitset.copyOf())\n")
                }
            }

            writer.write("}\n\n")

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

            writer.write("    override fun immutable(): $immutableImplName =\n")

            when (baseClass) {
                "I32EnumSet", "I64EnumSet" -> {
                    writer.write("        $immutableImplName(bitset)\n")
                }
                "BigEnumSet" -> {
                    writer.write("        $immutableImplName(bitset.copyOf())\n")
                }
            }

            writer.write("}\n")
        }

        logger.info("Generated EnumSet implementation for $enumName")
    }

    private fun nowTime(): String {
        return DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.FULL)
            .withLocale(Locale.ROOT)
            .format(ZonedDateTime.now(ZoneId.of("UTC")))
    }
}
