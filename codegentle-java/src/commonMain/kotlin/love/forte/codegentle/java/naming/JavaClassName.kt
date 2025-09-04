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
package love.forte.codegentle.java.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.canonicalName
import love.forte.codegentle.common.naming.isNotEmpty
import love.forte.codegentle.common.utils.InternalMultisetApi
import love.forte.codegentle.common.writer.ImportName
import love.forte.codegentle.common.writer.InternalWriterApi
import love.forte.codegentle.java.spec.nestedTypesSimpleNames
import love.forte.codegentle.java.writer.JavaCodeWriter


@OptIn(InternalMultisetApi::class)
internal fun ClassName.emitTo(codeWriter: JavaCodeWriter) {
    // Check if the class is already imported, in the same package, or in java.lang packages
    var omitPackage = codeWriter.isInSamePackage(this)

    // Handle java.lang package omission strategy
    if (codeWriter.strategy.omitJavaLangPackage() && packageName.isJavaLang) {
        omitPackage = true
    }

    val importSimpleNames = mutableListOf<String>()
    val visitedClassNames = mutableSetOf<ClassName>()
    var className = this

    while (className !in visitedClassNames) {
        // For check for cycles to prevent infinite loop
        visitedClassNames.add(className)

        importSimpleNames.add(className.simpleName)
        val imported = codeWriter.isImported(className)
        if (imported) {
            // Check for conflicts with type variables
            if (codeWriter.currentTypeVariables.contains(className.simpleName)) {
                // If there's a name conflict, still use the fully qualified name
                continue
            }

            // Check for conflicts with nested types in current type stack
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
private fun JavaCodeWriter.isImported(className: ClassName): Boolean {
    // Check if the class is imported, either as a regular import or a static import
    val importName = importedTypes[className.simpleName]
    return (importName is ImportName.Class && importName.type == className) ||
        className.canonicalName in staticImports
}

private fun JavaCodeWriter.isInSamePackage(className: ClassName): Boolean {
    return packageName == className.packageName
}
