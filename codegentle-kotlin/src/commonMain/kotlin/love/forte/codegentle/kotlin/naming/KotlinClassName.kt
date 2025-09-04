/*
 * Copyright (C) 2014-2024 Square, Inc.
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
package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.PackageName
import love.forte.codegentle.common.naming.canonicalName
import love.forte.codegentle.common.naming.isNotEmpty
import love.forte.codegentle.common.utils.InternalMultisetApi
import love.forte.codegentle.common.writer.InternalWriterApi
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
import love.forte.codegentle.kotlin.strategy.omitPackageNullable
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [ClassName] to a [KotlinCodeWriter].
 */
@OptIn(InternalMultisetApi::class)
internal fun ClassName.emitTo(codeWriter: KotlinCodeWriter) {
    // Check if the class is already imported, in the same package, or in kotlin.* packages
    var omitPackage = codeWriter.isInSamePackage(this)

    if (codeWriter.strategy.omitPackageNullable(packageName)) {
        omitPackage = true
    }

    val importSimpleNames = mutableListOf<String>()
    val visitedClassNames = mutableSetOf<ClassName>()
    var className = this

    while (className !in visitedClassNames) {
        // For check for cycles to prevent infinite loop
        visitedClassNames.add(className)
        
        importSimpleNames.add(className.simpleName)
        if (codeWriter.isImported(className)) {
            // Check for conflicts with type variables
            if (codeWriter.currentTypeVariables.contains(className.simpleName)) {
                // If there's a name conflict, still use the fully qualified name
                continue
            }

            // Check for conflicts with nested types in the current type stack
            for (typeSpec in codeWriter.typeSpecStack) {
                if (typeSpec.nestedTypesSimpleNames.contains(simpleName)) {
                    continue
                }
            }

            omitPackage = true
            break
        } else {
            // Check enclosing class
            val enclosingClassName = className.enclosingClassName
                ?: break // Stop loop

            className = enclosingClassName
        }
    }

    if (!omitPackage && packageName.isNotEmpty()) {
        packageName.emitTo(codeWriter)
        codeWriter.emit(".")
    }

    importSimpleNames.reverse()
    importSimpleNames.forEachIndexed { index, name ->
        codeWriter.emit(name)
        if (index != importSimpleNames.lastIndex) {
            // Not last
            codeWriter.emit(".")
        }
    }
}

// Helper methods
@OptIn(InternalWriterApi::class)
private fun KotlinCodeWriter.isImported(className: ClassName): Boolean {
    // Check if the class is imported, either as a regular import or a static import
    return importedTypeName(className.simpleName) == className ||
        className.canonicalName in staticImports
}

private fun KotlinCodeWriter.isInSamePackage(className: ClassName): Boolean {
    return packageName == className.packageName
}

// Extension to get nested type simple names for KotlinTypeSpec
internal val KotlinTypeSpec.nestedTypesSimpleNames: Set<String>
    get() = subtypes.map { it.name }.toSet()

// Extension to emit a PackageName to a KotlinCodeWriter
private fun PackageName.emitTo(codeWriter: KotlinCodeWriter) {
    codeWriter.emit(toString())
}
