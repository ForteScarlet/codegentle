package love.forte.codegentle.common.ksp

import com.google.devtools.ksp.symbol.*
import love.forte.codegentle.common.naming.*

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
 * Checks if a KSP [KSFunctionDeclaration] is a constructor.
 *
 * @return True if the function is a constructor, false otherwise
 */
public fun KSFunctionDeclaration.isConstructor(): Boolean {
    return simpleName.asString() == "<init>"
}

/**
 * Gets the class names of the bounds of a KSP [KSTypeParameter].
 *
 * @return The list of class names of the bounds
 */
public fun KSTypeParameter.getBoundClassNames(): List<ClassName> {
    return bounds.map { it.resolve() }
        .mapNotNull { it.declaration as? KSClassDeclaration }
        .map { it.toClassName() }
        .toList()
}

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

/**
 * Checks if a KSP [KSType] is a function type.
 *
 * @return True if the type is a function type, false otherwise
 */
public fun KSType.isFunctionType(): Boolean {
    val simpleName = declaration.simpleName.asString()
    val packageName = declaration.packageName.asString()
    
    return packageName == "kotlin" && 
        (simpleName.startsWith("Function") && simpleName.drop(8).all { it.isDigit() })
}

/**
 * Gets the function arity (number of parameters) of a function type.
 *
 * @return The function arity, or -1 if this is not a function type
 */
public fun KSType.getFunctionArity(): Int {
    if (!isFunctionType()) {
        return -1
    }
    
    val simpleName = declaration.simpleName.asString()
    return simpleName.drop(8).toIntOrNull() ?: -1
}

/**
 * Gets the return type of a function type.
 *
 * @return The return type, or null if this is not a function type or has no return type
 */
public fun KSType.getFunctionReturnType(): KSType? {
    if (!isFunctionType()) {
        return null
    }
    
    val arity = getFunctionArity()
    if (arity < 0) {
        return null
    }
    
    // The return type is the last type argument
    return arguments.lastOrNull()?.type?.resolve()
}

/**
 * Gets the parameter types of a function type.
 *
 * @return The list of parameter types, or an empty list if this is not a function type
 */
public fun KSType.getFunctionParameterTypes(): List<KSType> {
    if (!isFunctionType()) {
        return emptyList()
    }
    
    val arity = getFunctionArity()
    if (arity <= 0) {
        return emptyList()
    }
    
    // The parameter types are all type arguments except the last one (which is the return type)
    return arguments.dropLast(1)
        .mapNotNull { it.type?.resolve() }
}
