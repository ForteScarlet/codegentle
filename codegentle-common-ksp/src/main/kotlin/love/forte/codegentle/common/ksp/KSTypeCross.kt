/*
 * Copyright (C) 2025-2026 Forte Scarlet
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
package love.forte.codegentle.common.ksp

import com.google.devtools.ksp.symbol.*
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.MemberName
import love.forte.codegentle.common.naming.PackageName
import love.forte.codegentle.common.naming.parseToPackageName

/**
 * Converts a KSP [KSClassDeclaration] to a CodeGentle [ClassName].
 *
 * @return The corresponding [ClassName] for this KSP class declaration
 */
public fun KSClassDeclaration.toClassName(): ClassName {
    val packageName = packageName.asString()
    val simpleNames = qualifiedName?.asString()
        ?.removePrefix("$packageName.")
        ?.split('.')
        ?: listOf(simpleName.asString())

    return ClassName(
        packageName = packageName.parseToPackageName(),
        simpleName = simpleNames.first(),
        simpleNames = simpleNames.drop(1).toTypedArray()
    )
}

/**
 * Converts a KSP [KSFile] to a CodeGentle [PackageName].
 *
 * @return The corresponding [PackageName] for this KSP file's package
 */
public fun KSFile.toPackageName(): PackageName {
    return packageName.asString().parseToPackageName()
}

/**
 * Converts a KSP [KSType] to a CodeGentle [ClassName] if possible.
 *
 * This function handles only class types. For other types, it returns null.
 *
 * @return The corresponding [ClassName] for this KSP type, or null if the type is not a class
 */
public fun KSType.toClassNameOrNull(): ClassName? {
    if (this.isError) {
        return null
    }

    val declaration = this.declaration
    if (declaration is KSClassDeclaration) {
        return declaration.toClassName()
    }

    return null
}

/**
 * Converts a KSP [KSTypeReference] to a CodeGentle [ClassName] if possible.
 *
 * This function handles only class types. For other types, it returns null.
 *
 * @return The corresponding [ClassName] for this KSP type reference, or null if the type is not a class
 */
public fun KSTypeReference.toClassNameOrNull(): ClassName? {
    return resolve().toClassNameOrNull()
}

/**
 * Converts a KSP [KSDeclaration] to a CodeGentle [MemberName] if possible.
 *
 * This function handles function, property, and other member declarations.
 *
 * @return The corresponding [MemberName] for this KSP declaration
 */
public fun KSDeclaration.toMemberName(): MemberName {
    val name = simpleName.asString()
    val containingClass = parentDeclaration as? KSClassDeclaration

    return if (containingClass != null) {
        MemberName(containingClass.toClassName(), name)
    } else {
        val packageName = packageName.asString()
        MemberName(packageName.parseToPackageName(), name)
    }
}

/**
 * Converts a KSP [KSFunctionDeclaration] to a CodeGentle [MemberName].
 *
 * @return The corresponding [MemberName] for this KSP function declaration
 */
public fun KSFunctionDeclaration.toMemberName(): MemberName {
    return (this as KSDeclaration).toMemberName()
}

/**
 * Converts a KSP [KSPropertyDeclaration] to a CodeGentle [MemberName].
 *
 * @return The corresponding [MemberName] for this KSP property declaration
 */
public fun KSPropertyDeclaration.toMemberName(): MemberName {
    return (this as KSDeclaration).toMemberName()
}

/**
 * Checks if a KSP [KSType] is a primitive type.
 *
 * @return True if the type is a primitive type, false otherwise
 */
public fun KSType.isPrimitive(): Boolean {
    val name = declaration.simpleName.asString()
    return name in setOf(
        "Boolean", "Byte", "Short", "Int", "Long", "Float", "Double", "Char", "String",
        "Unit", "Nothing"
    )
}

/**
 * Gets the class names of the bounds of a KSP [KSTypeParameter].
 *
 * @return The list of class names of the bounds
 */
public val KSTypeParameter.boundClassNames: List<ClassName>
    get() = bounds.map { it.resolve() }
        .mapNotNull { it.declaration as? KSClassDeclaration }
        .map { it.toClassName() }
        .toList()

/**
 * Checks if a KSP [KSType] is an array type.
 *
 * @return True if the type is an array type, false otherwise
 */
public fun KSType.isArrayType(): Boolean {
    return declaration.qualifiedName?.asString() == "kotlin.Array"
}

/**
 * Gets the component type of an array type.
 *
 * @return The component type, or null if this is not an array type or has no component type
 */
public fun KSType.getArrayComponentType(): KSType? {
    if (!isArrayType()) {
        return null
    }

    return arguments.firstOrNull()?.type?.resolve()
}
