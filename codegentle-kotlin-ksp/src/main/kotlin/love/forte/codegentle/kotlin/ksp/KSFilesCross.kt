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

