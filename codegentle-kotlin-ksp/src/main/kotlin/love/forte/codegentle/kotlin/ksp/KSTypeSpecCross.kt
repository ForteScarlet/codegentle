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
package love.forte.codegentle.kotlin.ksp

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.*
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.ref.kotlinStatus
import love.forte.codegentle.kotlin.ref.kotlinVariableStatus
import love.forte.codegentle.kotlin.spec.*


/**
 * Converts a KSP [KSDeclaration] to a CodeGentle [KotlinTypeSpec].
 *
 * This function creates the appropriate type of [KotlinTypeSpec] based on the declaration type.
 * Supports both [KSClassDeclaration] and [KSTypeAlias].
 *
 * @return The corresponding [KotlinTypeSpec] for this KSP declaration
 */
public fun KSDeclaration.toKotlinTypeSpec(): KotlinTypeSpec {
    return when (this) {
        is KSClassDeclaration -> toKotlinTypeSpec()
        is KSTypeAlias -> toKotlinTypealiasSpec()
        else -> throw IllegalArgumentException("Unsupported declaration type: ${this::class.simpleName}")
    }
}

/**
 * Converts a KSP [KSClassDeclaration] to a CodeGentle [KotlinTypeSpec].
 *
 * This function creates the appropriate type of [KotlinTypeSpec] based on the class kind.
 *
 * @return The corresponding [KotlinTypeSpec] for this KSP class declaration
 */
public fun KSClassDeclaration.toKotlinTypeSpec(): KotlinTypeSpec {
    return when (classKind) {
        ClassKind.ENUM_CLASS -> toKotlinEnumTypeSpec()
        ClassKind.ANNOTATION_CLASS -> toKotlinAnnotationTypeSpec()
        ClassKind.OBJECT -> toKotlinObjectTypeSpec()
        else -> {
            if (modifiers.contains(Modifier.VALUE)) {
                toKotlinValueClassSpec()
            } else {
                toKotlinSimpleTypeSpec()
            }
        }
    }
}

/**
 * Converts a KSP [KSClassDeclaration] to a CodeGentle [KotlinSimpleTypeSpec].
 *
 * This function creates a [KotlinSimpleTypeSpec] based on the KSP class declaration.
 * It handles regular classes, interfaces, objects, and companion objects.
 *
 * @return The corresponding [KotlinSimpleTypeSpec] for this KSP class declaration
 */
public fun KSClassDeclaration.toKotlinSimpleTypeSpec(): KotlinSimpleTypeSpec {
    val kind = when (classKind) {
        ClassKind.CLASS -> KotlinTypeSpec.Kind.CLASS
        ClassKind.INTERFACE -> KotlinTypeSpec.Kind.INTERFACE
        ClassKind.OBJECT -> KotlinTypeSpec.Kind.OBJECT // Not valid
        else -> KotlinTypeSpec.Kind.CLASS // Default to CLASS for other kinds
    }

    require(KotlinSimpleTypeSpec.isValidKind(kind)) {
        "Class kind $classKind is not a valid KotlinSimpleTypeSpec kind"
    }

    val builder = KotlinSimpleTypeSpec.builder(kind, simpleName.asString())

    // Add modifiers
    modifiers.forEach { modifier ->
        builder.addModifier(modifier.toKotlinModifier())
    }

    // Add annotations
    annotations.forEach { annotation ->
        builder.addAnnotation(annotation.toAnnotationRef())
    }

    // Add type parameters
    typeParameters.forEach { typeParam ->
        builder.addTypeVariable(typeParam.toTypeVariableRef())
    }

    // Add supertype references
    superTypes.forEach { superType ->
        // Convert the supertype to a TypeRef
        val supertypeRef = superType.toTypeRef()

        // Add the supertype to the builder
        builder.addSuperinterface(supertypeRef.typeName)
    }

    // Add the primary constructor if available
    primaryConstructor?.let { constructor ->
        builder.primaryConstructor(constructor.toKotlinConstructorSpec())
    }

    // Add properties
    transformPropertiesTo(builder)

    // Add functions and secondary constructors
    transformFunctionsAndSecondaryConstructorsTo(builder)

    // Add nested classes
    declarations
        .filterIsInstance<KSClassDeclaration>()
        .forEach { nestedClass ->
            builder.addSubtype(nestedClass.toKotlinTypeSpec())
        }

    return builder.build()
}

public fun KSClassDeclaration.toKotlinObjectTypeSpec(): KotlinObjectTypeSpec {
    require(classKind == ClassKind.OBJECT) {
        "ClassKind of this KSClassDeclaration must be `OBJECT`, but was $classKind"
    }

    val name = simpleName.asString()
    val builder = KotlinObjectTypeSpec.builder(name)

    // Add modifiers
    modifiers.forEach { modifier ->
        builder.addModifier(modifier.toKotlinModifier())
    }

    // Add annotations
    annotations.forEach { annotation ->
        builder.addAnnotation(annotation.toAnnotationRef())
    }

    // Add type parameters
    typeParameters.forEach { typeParam ->
        builder.addTypeVariable(typeParam.toTypeVariableRef())
    }

    // Add superinterfaces
    superTypes.forEach { superType ->
        val resolvedType = superType.resolve()
        if (resolvedType.declaration is KSClassDeclaration) {
            val classDecl = resolvedType.declaration as KSClassDeclaration
            if (classDecl.classKind == ClassKind.INTERFACE) {
                builder.addSuperinterface(superType.toTypeRef().typeName)
            }
        }
    }

    // Add properties
    getAllProperties().forEach { property ->
        builder.addProperty(property.toKotlinPropertySpec())
    }

    // Add functions
    // Skip constructors as objects don't have constructors
    transformFunctionsTo(functionCollector = builder)

    // Add nested classes
    declarations
        .filterIsInstance<KSClassDeclaration>()
        .forEach { nestedClass ->
            builder.addSubtype(nestedClass.toKotlinTypeSpec())
        }

    return builder.build()
}

public fun KSClassDeclaration.toKotlinValueClassSpec(): KotlinValueClassTypeSpec {
    require(classKind == ClassKind.CLASS && modifiers.contains(Modifier.VALUE)) {
        "ClassKind of this KSClassDeclaration must be `CLASS` and modifiers must contain `VALUE`, " +
            "but was $classKind and $modifiers"
    }

    val name = simpleName.asString()

    // Find the primary constructor
    val primaryConstructor = primaryConstructor
        ?: throw IllegalStateException("Value class $name must have a primary constructor")

    // Value classes must have exactly one parameter in their primary constructor
    val constructorParams = primaryConstructor.parameters
    require(constructorParams.size == 1) {
        "Value class $name must have exactly one parameter in its primary constructor, but has ${constructorParams.size}"
    }

    // Convert the primary constructor to KotlinConstructorSpec
    val constructorSpec = primaryConstructor.toKotlinConstructorSpec()

    val builder = KotlinValueClassTypeSpec.builder(name, constructorSpec)

    // Add modifiers (excluding VALUE as it's implicit for value classes)
    modifiers.filter { it != Modifier.VALUE }.forEach { modifier ->
        builder.addModifier(modifier.toKotlinModifier())
    }

    // Add annotations
    annotations.forEach { annotation ->
        builder.addAnnotation(annotation.toAnnotationRef())
    }

    // Add type parameters
    typeParameters.forEach { typeParam ->
        builder.addTypeVariable(typeParam.toTypeVariableRef())
    }

    // Add superinterfaces
    superTypes.forEach { superType ->
        val resolvedType = superType.resolve()
        if (resolvedType.declaration is KSClassDeclaration) {
            val classDecl = resolvedType.declaration as KSClassDeclaration
            if (classDecl.classKind == ClassKind.INTERFACE) {
                builder.addSuperinterface(superType.toTypeRef().typeName)
            }
        }
    }

    // Add properties (excluding the primary constructor property)
    val primaryParamName = constructorParams.first().name?.asString()
    getAllProperties().forEach { property ->
        // Skip the property that corresponds to the primary constructor parameter
        if (property.simpleName.asString() != primaryParamName) {
            builder.addProperty(property.toKotlinPropertySpec())
        }
    }

    // Add functions and secondary constructors
    transformFunctionsAndSecondaryConstructorsTo(builder)

    return builder.build()
}

/**
 * Converts a KSP ENUM_ENTRY to a CodeGentle [KotlinAnonymousClassTypeSpec].
 */
public fun KSClassDeclaration.toKSClassAnonymousClassTypeSpec(): KotlinAnonymousClassTypeSpec? {
    require(classKind == ClassKind.ENUM_ENTRY) {
        "KSClassDeclaration must be an enum entry, " +
            "but was $classKind"
    }

    // Check if the enum entry has any custom implementation
    val functions = getAllFunctions().filter { !it.isConstructor() }.toList()
    val properties = getAllProperties().toList()

    // If functions and properties are empty, return null directly
    // Note: Anonymous classes don't support nested types
    if (functions.isEmpty() && properties.isEmpty()) {
        return null
    }

    val builder = KotlinAnonymousClassTypeSpec.builder()

    // Add annotations
    annotations.forEach { annotation ->
        builder.addAnnotation(annotation.toAnnotationRef())
    }

    // Add properties
    transformPropertiesTo(builder, { properties.iterator() })

    // Add functions
    transformFunctionsTo(builder, { functions.iterator() })

    return builder.build()
}

/**
 * Converts a KSP [KSClassDeclaration] to a CodeGentle [KotlinEnumTypeSpec].
 *
 * This function creates a [KotlinEnumTypeSpec] for enum classes.
 *
 * @return The corresponding [KotlinEnumTypeSpec] for this KSP enum class declaration
 */
public fun KSClassDeclaration.toKotlinEnumTypeSpec(): KotlinEnumTypeSpec {
    require(classKind == ClassKind.ENUM_CLASS) { "Class must be an enum class" }

    val builder = KotlinEnumTypeSpec.builder(simpleName.asString())

    // Add modifiers
    modifiers.forEach { modifier ->
        builder.addModifier(modifier.toKotlinModifier())
    }

    // Add annotations
    annotations.forEach { annotation ->
        builder.addAnnotation(annotation.toAnnotationRef())
    }

    // Add enum entries
    declarations
        .filterIsInstance<KSClassDeclaration>()
        .filter { it.classKind == ClassKind.ENUM_ENTRY }
        .forEach { entry ->
            val entryName = entry.simpleName.asString()
            val anonymousClass = entry.toKSClassAnonymousClassTypeSpec()
            if (anonymousClass != null) {
                builder.addEnumConstant(entryName, anonymousClass)
            } else {
                builder.addEnumConstant(entryName)
            }
        }

    // Add properties
    transformPropertiesTo(builder)

    // Add functions and secondary constructors
    transformFunctionsAndSecondaryConstructorsTo(builder)

    // Primary constructor
    primaryConstructor
        ?.toKotlinConstructorSpec()
        ?.also(builder::primaryConstructor)

    transformFunctionsAndSecondaryConstructorsTo(builder)

    return builder.build()
}

/**
 * Converts a KSP [KSClassDeclaration] to a CodeGentle [KotlinAnnotationTypeSpec].
 *
 * This function creates a [KotlinAnnotationTypeSpec] for annotation classes.
 *
 * @return The corresponding [KotlinAnnotationTypeSpec] for this KSP annotation class declaration
 */
public fun KSClassDeclaration.toKotlinAnnotationTypeSpec(): KotlinAnnotationTypeSpec {
    require(classKind == ClassKind.ANNOTATION_CLASS) { "Class must be an annotation class" }

    val builder = KotlinAnnotationTypeSpec.builder(simpleName.asString())

    // Add modifiers
    modifiers.forEach { modifier ->
        builder.addModifier(modifier.toKotlinModifier())
    }

    // Add annotations
    annotations.forEach { annotation ->
        builder.addAnnotation(annotation.toAnnotationRef())
    }

    // Add properties (annotation members)
    transformPropertiesTo(builder)

    return builder.build()
}

/**
 * Converts a KSP [KSTypeAlias] to a CodeGentle [KotlinTypealiasSpec].
 *
 * This function creates a [KotlinTypealiasSpec] based on the KSP typealias declaration.
 *
 * @return The corresponding [KotlinTypealiasSpec] for this KSP typealias declaration
 */
public fun KSTypeAlias.toKotlinTypealiasSpec(): KotlinTypealiasSpec {
    val name = simpleName.asString()
    val targetType = type.toTypeRef()
    
    val builder = KotlinTypealiasSpec.builder(name, targetType)

    // Add modifiers
    modifiers.forEach { modifier ->
        builder.addModifier(modifier.toKotlinModifier())
    }

    // Add annotations
    annotations.forEach { annotation ->
        builder.addAnnotation(annotation.toAnnotationRef())
    }

    // Add type parameters
    typeParameters.forEach { typeParam ->
        builder.addTypeVariable(typeParam.toTypeVariableRef())
    }

    return builder.build()
}

/**
 * Converts a KSP [KSFunctionDeclaration] to a CodeGentle [KotlinFunctionSpec].
 *
 * This function creates a [KotlinFunctionSpec] based on the KSP function declaration.
 *
 * @return The corresponding [KotlinFunctionSpec] for this KSP function declaration
 */
public fun KSFunctionDeclaration.toKotlinFunctionSpec(): KotlinFunctionSpec {
    val builder = KotlinFunctionSpec.builder(simpleName.asString())

    // Add modifiers
    modifiers.forEach { modifier ->
        builder.addModifier(modifier.toKotlinModifier())
    }

    // Add annotations
    annotations.forEach { annotation ->
        builder.addAnnotation(annotation.toAnnotationRef())
    }

    // Add type parameters
    typeParameters.forEach { typeParam ->
        builder.addTypeVariable(typeParam.toTypeVariableRef())
    }

    // Add parameters
    parameters.forEach { param ->
        builder.addParameter(param.toKotlinValueParameterSpec())
    }

    // Set return type
    returnType?.let { returnTypeRef ->
        builder.returns(returnTypeRef.toTypeRef())
    }

    // Set receiver type if available
    extensionReceiver?.let { receiverTypeRef ->
        builder.receiver(receiverTypeRef.toTypeRef())
    }

    return builder.build()
}

public fun KSTypeParameter.toTypeVariableRef(): TypeRef<TypeVariableName> {
    // Get the core TypeVariableName using the primary conversion logic
    val typeVariableName = toTypeVariableName()

    return typeVariableName.ref {
        kotlinStatus {
            // Add annotations
            annotations.forEach { annotation ->
                addAnnotation(annotation.toAnnotationRef())
            }
            // Type parameters are not nullable by default
            nullable = false
        }
        kotlinVariableStatus.reified = isReified
    }
}

/**
 * Converts a KSP [KSFunctionDeclaration] to a CodeGentle [KotlinConstructorSpec].
 *
 * This function creates a [KotlinConstructorSpec] based on the KSP constructor declaration.
 *
 * @return The corresponding [KotlinConstructorSpec] for this KSP constructor declaration
 */
public fun KSFunctionDeclaration.toKotlinConstructorSpec(): KotlinConstructorSpec {
    require(isConstructor()) { "Function must be a constructor" }

    val builder = KotlinConstructorSpec.builder()

    // Add modifiers
    modifiers.forEach { modifier ->
        builder.addModifier(modifier.toKotlinModifier())
    }

    // Add annotations
    annotations.forEach { annotation ->
        builder.addAnnotation(annotation.toAnnotationRef())
    }

    // Add parameters
    parameters.forEach { param ->
        builder.addParameter(param.toKotlinValueParameterSpec())
    }

    return builder.build()
}

/**
 * Converts a KSP [KSPropertyDeclaration] to a CodeGentle [KotlinPropertySpec].
 *
 * This function creates a [KotlinPropertySpec] based on the KSP property declaration.
 *
 * @return The corresponding [KotlinPropertySpec] for this KSP property declaration
 */
public fun KSPropertyDeclaration.toKotlinPropertySpec(): KotlinPropertySpec {
    val builder = KotlinPropertySpec.builder(
        simpleName.asString(),
        type.toTypeRef()
    )

    // Add modifiers
    modifiers.forEach { modifier ->
        builder.addModifier(modifier.toKotlinModifier())
    }

    // Add annotations
    annotations.forEach { annotation ->
        builder.addAnnotation(annotation.toAnnotationRef())
    }

    // Set mutability
    builder.mutable(isMutable)

    // Add getter if available
    getter?.let { getterFunc ->
        // Note: KSP provides limited information about property accessors
        // We can detect custom getters exist but cannot access their implementation
        // For code generation, custom accessors would need to be handled separately
        // Most properties use default accessors which don't need explicit specification
    }

    // Add setter if available
    setter?.let { setterFunc ->
        // Note: KSP provides limited information about property accessors
        // We can detect custom setters exist but cannot access their implementation
        // For code generation, custom accessors would need to be handled separately
        // Most properties use default accessors which don't need explicit specification
    }

    return builder.build()
}

/**
 * Converts a KSP [KSValueParameter] to a CodeGentle [KotlinValueParameterSpec].
 *
 * This function creates a [KotlinValueParameterSpec] based on the KSP value parameter.
 *
 * @return The corresponding [KotlinValueParameterSpec] for this KSP value parameter
 */
public fun KSValueParameter.toKotlinValueParameterSpec(): KotlinValueParameterSpec {
    val builder = KotlinValueParameterSpec.builder(
        name = name?.asString() ?: "",
        type = type.toTypeRef()
    )

    // Add annotations
    annotations.forEach { annotation ->
        builder.addAnnotation(annotation.toAnnotationRef())
    }

    // Set default value if available
    // if (hasDefault) {
    // Note: KSP doesn't provide access to the actual default value expression
    // We can only detect that a default value exists, but not its content
    // This is a limitation of the KSP API for code generation scenarios
    // The default value would need to be handled at the call site
    // }

    // Handle vararg
    if (isVararg) {
        // Note: KotlinValueParameterSpec doesn't have a direct vararg method
        // We need to add the VARARG modifier instead
        builder.addModifier(KotlinModifier.VARARG)
    }

    return builder.build()
}

/**
 * Converts a KSP [KSTypeReference] to a CodeGentle [TypeRef].
 *
 * This function creates a [TypeRef] based on the KSP type reference.
 * It delegates to [KSTypeReference.toTypeName] for the core conversion logic
 * and wraps the result in a TypeRef.
 *
 * @return The corresponding [TypeRef] for this KSP type reference
 */
public fun KSTypeReference.toTypeRef(): TypeRef<*> {
    // Get the core TypeName using the primary conversion logic
    val typeName = toTypeName()

    // Wrap in TypeRef (nullable status is handled by the resolved KSType)
    return typeName.ref {
        kotlinStatus.nullable = resolve().isMarkedNullable
    }
}

/**
 * Converts a KSP [KSTypeArgument] to a CodeGentle [TypeRef].
 *
 * This function creates a [TypeRef] based on the KSP type argument.
 * It delegates to [KSTypeArgument.toTypeName] for the core conversion logic
 * and wraps the result in a TypeRef.
 *
 * @return The corresponding [TypeRef] for this KSP type argument
 */
public fun KSTypeArgument.toTypeRef(): TypeRef<*> {
    // Get the core TypeName using the primary conversion logic
    val typeName = toTypeName()

    val nullable = this.type?.resolve()?.isMarkedNullable

    // Wrap in TypeRef
    return typeName.ref {
        if (nullable == true) {
            kotlinStatus {
                this.nullable = true
            }
        }
    }
}

/**
 * Converts a KSP [KSType] to a CodeGentle [TypeRef].
 *
 * This function creates a [TypeRef] based on the KSP type.
 * It delegates to [KSType.toTypeName] for the core conversion logic
 * and wraps the result in a TypeRef with appropriate metadata.
 *
 * @return The corresponding [TypeRef] for this KSP type
 */
public fun KSType.toTypeRef(): TypeRef<*> {
    val typeName = toTypeName()

    // Wrap in TypeRef with nullable metadata
    return typeName.ref {
        kotlinStatus.nullable = isMarkedNullable
    }
}
