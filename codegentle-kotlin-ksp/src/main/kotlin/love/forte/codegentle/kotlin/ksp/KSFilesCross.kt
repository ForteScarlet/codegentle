package love.forte.codegentle.kotlin.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSFile
import love.forte.codegentle.kotlin.KotlinFile
import love.forte.codegentle.kotlin.strategy.DefaultKotlinWriteStrategy
import love.forte.codegentle.kotlin.strategy.KotlinWriteStrategy
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

public fun KotlinFile.writeTo(
    codeGenerator: CodeGenerator,
    aggregating: Boolean,
    originatingKSFiles: Iterable<KSFile>,
    strategy: KotlinWriteStrategy = DefaultKotlinWriteStrategy()
) {
    val dependencies = Dependencies(aggregating = aggregating, sources = originatingKSFiles.toList().toTypedArray())
    val file = codeGenerator.createNewFile(dependencies, packageName.toString(), type.name)
    // Don't use writeTo(file) because that tries to handle directories under the hood
    OutputStreamWriter(file, StandardCharsets.UTF_8)
        .use { this.writeTo(it, strategy) }
}

