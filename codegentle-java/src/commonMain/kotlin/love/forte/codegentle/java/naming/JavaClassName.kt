package love.forte.codegentle.java.naming

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.canonicalName
import love.forte.codegentle.common.naming.isEmpty
import love.forte.codegentle.common.naming.simpleNames
import love.forte.codegentle.common.utils.InternalMultisetApi
import love.forte.codegentle.java.spec.JavaTypeSpec
import love.forte.codegentle.java.spec.nestedTypesSimpleNames
import love.forte.codegentle.java.writer.JavaCodeWriter


@OptIn(InternalMultisetApi::class)
internal fun ClassName.emitTo(codeWriter: JavaCodeWriter) {
    // 优先查看当前类是否已经被导入,
    // 如果当前类没有被导入，向上查找，并记录names到缓存列表 [currentName, enclosing1Name, ...]
    // 列表最后一个元素是最后一个能找到、被导入的类型，或没有任何类型被导入。
    // 如果没有被导入类型，先emit package，再依次倒序emit names。
    // 如果有被导入的类型，从它开始 emit names，不需要 emit package。

    var omitPackage = codeWriter.isInSamePackage(this)

    // 如果java.lang需要处理或处于同一个包内
    if ((codeWriter.strategy.omitJavaLangPackage()
            && packageName?.isJavaLang == true)
    ) {
        omitPackage = true
    }

    val importSimpleNames = mutableListOf<String>()
    var className = this

    while (true) {
        importSimpleNames.add(className.simpleName)
        val imported = codeWriter.isImported(className)
        if (imported) {
            // 检查 typeVariables 名称冲突
            if (codeWriter.currentTypeVariables.contains(className.simpleName)) {
                // 如果名称冲突了，仍然使用全限定名（不跳过包的输出）
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
            // check `java.lang`
            val enclosingClassName = className.enclosingClassName
            if (enclosingClassName == null) {
                // stop loop
                break
            }

            className = enclosingClassName
        }
    }

    if (!omitPackage) {
        packageName?.also { p ->
            p.emitTo(codeWriter)
            codeWriter.emit(".")
        }
    }

    importSimpleNames.reverse()
    importSimpleNames.forEachIndexed { index, name ->
        codeWriter.emit(name)
        if (index != importSimpleNames.lastIndex) {
            // not last
            codeWriter.emit(".")
        }
    }
}

// 辅助方法
private fun JavaCodeWriter.isImported(className: ClassName): Boolean {
    // 看看当前类有没有被导入。可能是普通导入或 static 导入
    return importedTypes[className.simpleName] == className ||
        className.canonicalName in staticImports
}

private fun JavaCodeWriter.isInSamePackage(className: ClassName): Boolean {
    return packageName == className.packageName
}

//////////

internal fun ClassName.emitTo1(codeWriter: JavaCodeWriter) {
    fun enclosingClasses(): List<ClassName> {
        val result = mutableListOf<ClassName>()
        var c: ClassName? = this
        while (c != null) {
            result.add(c)
            c = c.enclosingClassName
        }
        result.reverse()
        return result
    }

    var charsEmitted = false

    for (className in enclosingClasses()) {
        val simpleName: String
        if (charsEmitted) {
            // We've already emitted an enclosing class. Emit as we go.
            codeWriter.emit(".")
            simpleName = className.simpleName
        } else if (className === this) {
            // We encountered the first enclosing class that must be emitted.
            val qualifiedName: String = codeWriter.lookupName(className)
            val dot = qualifiedName.lastIndexOf('.')
            if (dot != -1) {
                codeWriter.emit(qualifiedName.substring(0, dot + 1))
                simpleName = qualifiedName.substring(dot + 1)
            } else {
                simpleName = qualifiedName
            }
        } else {
            // Don't emit this enclosing type. Keep going so we can be more precise.
            continue
        }

        codeWriter.emit(simpleName)
        charsEmitted = true
    }
}


/**
 * Returns the best name to identify `className` with in the current context. This uses the
 * available imports and the current scope to find the shortest name available. It does not honor
 * names visible due to inheritance.
 */
@OptIn(InternalMultisetApi::class)
private fun JavaCodeWriter.lookupName(className: ClassName): String {
    /*
     * Copyright (C) 2015 Square, Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

    // If the top level simple name is masked by a current type variable, use the canonical name.
    val topLevelSimpleName: String = className.topLevelClassName.simpleName
    if (currentTypeVariables.contains(topLevelSimpleName)) {
        return className.canonicalName
    }

    // Find the shortest suffix of className that resolves to className. This uses both local type
    // names (so `Entry` in `Map` refers to `Map.Entry`). Also uses imports.
    var nameResolved = false
    var c: ClassName? = className
    while (c != null) {
        val resolved = resolve(c.simpleName)
        nameResolved = resolved != null

        if (resolved != null && resolved.canonicalName == c.canonicalName) {
            val suffixOffset: Int = c.simpleNames.size - 1
            return className.simpleNames.subList(
                suffixOffset, className.simpleNames.size
            ).joinToString(".")
        }

        c = c.enclosingClassName
    }

    // If the name resolved but wasn't a match, we're stuck with the fully qualified name.
    if (nameResolved) {
        return className.canonicalName
    }

    // If the class is in the same package, we're done.
    if (packageName == className.packageName) {
        referencedNames.add(topLevelSimpleName)
        return className.simpleNames.joinToString(".")
    }

    // Handle java.lang package based on strategy
    if (strategy.omitJavaLangPackage()
        && className.packageName?.isJavaLang == true
        && !alwaysQualify.contains(className.simpleName)
    ) {
        referencedNames.add(topLevelSimpleName)
        return className.simpleNames.joinToString(".")
    }

    // We'll have to use the fully-qualified name. Mark the type as importable for a future pass.
    if (commentType?.isJavadoc != true) {
        importableType(className)
    }

    return className.canonicalName

}

@OptIn(InternalMultisetApi::class)
private fun JavaCodeWriter.importableType(className: ClassName) {
    /*
     * Copyright (C) 2015 Square, Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

    val packageName = className.packageName
    if (packageName == null || packageName.isEmpty) {
        // null, or is empty.
        return
    }

    // Skip java.lang imports based on strategy, unless always qualified
    if (packageName.isJavaLang
        && strategy.omitJavaLangPackage()
        && !alwaysQualify.contains(className.simpleName)
    ) {
        return
    }

    val topLevelClassName: ClassName = className.topLevelClassName
    val simpleName: String = topLevelClassName.simpleName

    // Avoid conflicts with types already in scope
    if (currentTypeVariables.contains(simpleName)) {
        return
    }

    // Check for conflicts with nested types in current type stack
    for (typeSpec in typeSpecStack) {
        if (typeSpec.nestedTypesSimpleNames.contains(simpleName)) {
            return
        }
    }

    val existing: ClassName? = importableTypes.put(simpleName, topLevelClassName)
    if (existing != null) {
        // On collision, prefer the first inserted to maintain consistency
        importableTypes[simpleName] = existing
    }
}

/**
 * Returns the class referenced by `simpleName`, using the current nesting context and
 * imports.
 */
private fun JavaCodeWriter.resolve(simpleName: String): ClassName? {
    /*
     * Copyright (C) 2015 Square, Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

    // Match a child of the current (potentially nested) class.
    for (i in typeSpecStack.indices.reversed()) {
        val typeSpec: JavaTypeSpec = typeSpecStack[i]
        if (typeSpec.nestedTypesSimpleNames.contains(simpleName)) {
            return stackClassName(i, simpleName)
        }
    }

    // Match the top-level class.
    if (typeSpecStack.isNotEmpty() && typeSpecStack[0].name == simpleName) {
        return ClassName(packageName, simpleName)
    }

    // Match an imported type.
    val importedType: ClassName? = importedTypes[simpleName]
    if (importedType != null) return importedType

    // Match java.lang types implicitly if strategy allows omitting java.lang
    if (strategy.omitJavaLangPackage() && isCommonJavaLangType(simpleName)) {
        return ClassName(JavaLangPackageName, simpleName)
    }

    // No match.
    return null
}

private val commonJavaLangTypeSimpleNames = setOf(
    "Object", "String", "Class", "Integer", "Long", "Double", "Float",
    "Boolean", "Character", "Byte", "Short", "Number", "Throwable",
    "Exception", "RuntimeException", "Error", "Thread", "System",
    "Math", "StringBuilder", "StringBuffer", "Void"
)

/**
 * Check if a simple name refers to a common java.lang type
 * This is a conservative list of commonly used java.lang classes
 */
private fun isCommonJavaLangType(simpleName: String): Boolean {
    return simpleName in commonJavaLangTypeSimpleNames
}

/** Returns the class named `simpleName` when nested in the class at `stackDepth`.  */
private fun JavaCodeWriter.stackClassName(
    stackDepth: Int,
    simpleName: String
): ClassName {
    /*
     * Copyright (C) 2015 Square, Inc.
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

    var className = ClassName(packageName!!, typeSpecStack[0].name!!)
    for (i in 1..stackDepth) {
        className = className.nestedClass(typeSpecStack[i].name!!)
    }
    return className.nestedClass(simpleName)
}
