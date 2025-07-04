package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.naming.*
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Extension function to emit a [ClassName] to a [KotlinCodeWriter].
 */
internal fun ClassName.emitTo(codeWriter: KotlinCodeWriter) {
    // Check if the class is already imported, in the same package, or in kotlin.* packages
    var omitPackage = codeWriter.isInSamePackage(this)

    // If kotlin.* packages need to be handled or in the same package
    if ((codeWriter.strategy.omitKotlinPackage()
            && packageName?.isKotlinPackage == true)
    ) {
        omitPackage = true
    }

    val importSimpleNames = mutableListOf<String>()
    var className = this

    while (true) {
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
            if (enclosingClassName == null) {
                // Stop loop
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
            // Not last
            codeWriter.emit(".")
        }
    }
}

// Helper methods
private fun KotlinCodeWriter.isImported(className: ClassName): Boolean {
    // Check if the class is imported, either as a regular import or a static import
    return importedTypes[className.simpleName] == className ||
        className.canonicalName in staticImports
}

private fun KotlinCodeWriter.isInSamePackage(className: ClassName): Boolean {
    return packageName == className.packageName
}

/**
 * Returns the best name to identify `className` with in the current context.
 */
private fun KotlinCodeWriter.lookupName(className: ClassName): String {
    // If the top level simple name is masked by a current type variable, use the canonical name
    val topLevelSimpleName: String = className.topLevelClassName.simpleName
    if (currentTypeVariables.contains(topLevelSimpleName)) {
        return className.canonicalName
    }

    // Find the shortest suffix of className that resolves to className
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

    // If the name resolved but wasn't a match, we're stuck with the fully qualified name
    if (nameResolved) {
        return className.canonicalName
    }

    // If the class is in the same package, we're done
    if (packageName == className.packageName) {
        referencedNames.add(topLevelSimpleName)
        return className.simpleNames.joinToString(".")
    }

    // Handle kotlin.* packages based on strategy
    if (strategy.omitKotlinPackage()
        && className.packageName?.isKotlinPackage == true
        && !alwaysQualify.contains(className.simpleName)
    ) {
        referencedNames.add(topLevelSimpleName)
        return className.simpleNames.joinToString(".")
    }

    // We'll have to use the fully-qualified name. Mark the type as importable for a future pass
    // Only mark as importable if we're not in a doc comment
    importableType(className)

    return className.canonicalName
}

private fun KotlinCodeWriter.importableType(className: ClassName) {
    val packageName = className.packageName
    if (packageName == null || packageName.isEmpty) {
        // null, or is empty
        return
    }

    // Skip kotlin.* imports based on strategy, unless always qualified
    if (packageName.isKotlinPackage
        && strategy.omitKotlinPackage()
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
 * Returns the class referenced by `simpleName`, using the current nesting context and imports.
 */
private fun KotlinCodeWriter.resolve(simpleName: String): ClassName? {
    // Match a child of the current (potentially nested) class
    for (i in typeSpecStack.indices.reversed()) {
        val typeSpec: KotlinTypeSpec = typeSpecStack[i]
        if (typeSpec.nestedTypesSimpleNames.contains(simpleName)) {
            return stackClassName(i, simpleName)
        }
    }

    // Match the top-level class
    if (typeSpecStack.isNotEmpty() && typeSpecStack[0].name == simpleName) {
        return ClassName(packageName, simpleName)
    }

    // Match an imported type
    val importedType: ClassName? = importedTypes[simpleName]
    if (importedType != null) return importedType

    // Match kotlin.* types implicitly if strategy allows omitting kotlin.*
    if (strategy.omitKotlinPackage() && isCommonKotlinType(simpleName)) {
        return ClassName(KotlinNames.Packages.KOTLIN, simpleName)
    }

    // No match
    return null
}

private val commonKotlinTypeSimpleNames = setOf(
    "Any",
    "Unit",
    "String",
    "Char",
    "Boolean",
    "Byte",
    "Short",
    "Int",
    "Long",
    "Float",
    "Double",
    "Array",
    "ByteArray",
    "ShortArray",
    "IntArray",
    "LongArray",
    "FloatArray",
    "DoubleArray",
    "BooleanArray",
    "CharArray",
    "List",
    "Set",
    "Map",
    "MutableList",
    "MutableSet",
    "MutableMap",
    "Collection",
    "MutableCollection",
    "Iterable",
    "MutableIterable",
    "Pair",
    "Triple",
    "Nothing",
    "Throwable",
    "Exception",
    "RuntimeException",
    "Error"
)

/**
 * Check if a simple name refers to a common kotlin.* type
 */
private fun isCommonKotlinType(simpleName: String): Boolean {
    return simpleName in commonKotlinTypeSimpleNames
}

/** Returns the class named `simpleName` when nested in the class at `stackDepth`.  */
private fun KotlinCodeWriter.stackClassName(
    stackDepth: Int,
    simpleName: String
): ClassName {
    val typeSpec = typeSpecStack[stackDepth]
    val enclosingClassName = if (stackDepth > 0) {
        stackClassName(0, typeSpecStack[0].name)
    } else {
        ClassName(packageName, typeSpecStack[0].name)
    }

    return enclosingClassName.nestedClass(simpleName)
}

// Extension property to check if a package is a kotlin.* package
private val PackageName?.isKotlinPackage: Boolean
    get() = this == KotlinNames.Packages.KOTLIN

// Extension to get nested type simple names for KotlinTypeSpec
internal val KotlinTypeSpec.nestedTypesSimpleNames: Set<String>
    get() = subtypes.map { it.name }.toSet()

// Extension to emit a PackageName to a KotlinCodeWriter
private fun PackageName.emitTo(codeWriter: KotlinCodeWriter) {
    codeWriter.emit(toString())
}
