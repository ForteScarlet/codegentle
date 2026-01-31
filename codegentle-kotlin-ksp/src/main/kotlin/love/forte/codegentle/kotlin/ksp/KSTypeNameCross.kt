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

import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import love.forte.codegentle.common.ksp.boundClassNames
import love.forte.codegentle.common.ksp.getArrayComponentType
import love.forte.codegentle.common.ksp.isArrayType
import love.forte.codegentle.common.ksp.toClassName
import love.forte.codegentle.common.naming.*
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.naming.KotlinLambdaTypeName
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec

private val PRIMITIVE_NAMES = setOf(
    "Boolean", "Byte", "Short", "Int", "Long", "Float", "Double", "Char", "String",
    "Unit", "Nothing"
)

/**
 * Checks if a KSP [KSType] is a primitive type.
 *
 * @return True if the type is a primitive type, false otherwise
 */
private fun KSType.isPrimitive(): Boolean {
    val packageName = declaration.packageName.asString()
    val name = declaration.simpleName.asString()
    return packageName == "kotlin" && name in PRIMITIVE_NAMES
}

/**
 * Converts a KSP [KSType] to a CodeGentle [TypeName].
 *
 * This function creates a [TypeName] based on the KSP type.
 * It handles various types including:
 * - Primitive types (Boolean, Int, String, etc.)
 * - Class types (converted to ClassName)
 * - Array types (converted to ArrayTypeName)
 * - Function types (converted to KotlinLambdaTypeName)
 * - Parameterized types (converted to ParameterizedTypeName)
 * - Type variables (converted to TypeVariableName)
 * - Wildcard types (converted to WildcardTypeName)
 *
 * @return The corresponding [TypeName] for this KSP type
 * @throws IllegalStateException if the type cannot be converted
 */
public fun KSType.toTypeName(): TypeName {
    // Check if it's an array type
    if (isArrayType()) {
        // Get the component type
        val componentType = getArrayComponentType()
        if (componentType != null) {
            return ArrayTypeName(componentType.toTypeRef())
        }
    }

    val isFunctionType = isFunctionType
    val isSuspendFunctionType = isSuspendFunctionType

    // Check if it's a function type
    if (isFunctionType || isSuspendFunctionType) {
        return toFunctionTypeName(isSuspendFunctionType)
    }

    // Handle primitive types and other class types
    val declaration = declaration
    val typeName = when {
        isPrimitive() -> {
            // Handle primitive types by creating a ClassName for the Kotlin type
            val name = declaration.simpleName.asString()
            ClassName(PackageNames.KOTLIN, name)
        }

        declaration is KSClassDeclaration -> {
            declaration.toClassName()
        }

        declaration is KSTypeParameter -> {
            // Handle type parameters correctly by converting to TypeVariableName
            declaration.toTypeVariableName()
        }

        else -> {
            // Default to using the declaration name as a ClassName
            // This is a best-effort approach and may not be accurate for all types
            val name = declaration.simpleName.asString()
            val packageName = declaration.packageName.asString()
            ClassName(packageName, name)
        }
    }

    // If there are no type arguments, return a simple type name
    if (arguments.isEmpty()) {
        return typeName
    }

    // Convert all type arguments to TypeName
    val typeArgs = arguments.map { arg ->
        arg.toTypeRef()
    }

    // Create a parameterized type with the type and its type arguments
    // Only ClassName can be parameterized, TypeVariableName cannot
    return when (typeName) {
        is ClassName -> typeName.parameterized(typeArgs)
        else -> {
            // For type variables and other non-parameterizable types, 
            // we should not try to parameterize them
            // This case should be rare, but we return the base type as-is
            typeName
        }
    }
}

private fun KSType.toFunctionTypeName(isSuspend: Boolean): KotlinLambdaTypeName {


    // Use KotlinLambdaTypeName for proper function type representation
    return KotlinLambdaTypeName {
        suspend(isSuspend)

        // Extract parameter types and return type from function type arguments
        // For function types like (A, B) -> C, the arguments are [A, B, C] where C is the return type
        // For extension function types like T.() -> R, the arguments are [T, R] where T is the receiver
        // and R is the return type. Extension function types are marked with @kotlin.ExtensionFunctionType
        // For context function types like context(C1, C2) T.(P1) -> R, the arguments are [C1, C2, T, P1, R]
        // where C1, C2 are context receivers, T is the receiver, P1 is the parameter, and R is the return type
        val typeArgs = arguments.map { arg ->
            arg.toTypeRef()
        }

        // check context receivers（via @ContextFunctionTypeParams）
        // TODO see https://github.com/google/ksp/issues/2702
        val contextReceiverCount = annotations.firstNotNullOfOrNull { annotation ->
            val annotationType = annotation.annotationType
            val annotationTypeValidated = annotation.annotationType.validate()

            val isContextAnnotation = annotation.shortName.asString() == "ContextFunctionTypeParams" &&
                if (annotationTypeValidated) {
                    annotationType.resolve().declaration.qualifiedName?.asString() == "kotlin.ContextFunctionTypeParams"
                } else {
                    // handle ERROR TYPE situation
                    annotationType.toString() == "<ERROR TYPE: kotlin.ContextFunctionTypeParams>"
                }

            if (isContextAnnotation) {
                // annotation's first argument is the number of context receivers
                annotation.arguments.firstOrNull()?.value as? Int
            } else {
                null
            }
        } ?: 0

        // check if this is an extension function type (has a receiver)
        val isExtensionFunctionType = annotations.any { annotation ->
            // annotation: @ExtensionFunctionType
            // annotation.annotationType: <ERROR TYPE: kotlin.ExtensionFunctionType>
            // annotation.annotationType.validate(): false
            // annotation.annotationType.resolve(): <ERROR TYPE: kotlin.ExtensionFunctionType>
            // annotation.annotationType.resolve().declaration: <ERROR TYPE: kotlin.ExtensionFunctionType>
            // annotation.annotationType.resolve().declaration.name: null
            val annotationType = annotation.annotationType
            val annotationTypeValidated = annotation.annotationType.validate()

            annotation.shortName.asString() == "ExtensionFunctionType" &&
                if (annotationTypeValidated) {
                    annotationType.resolve().declaration.qualifiedName?.asString() == "kotlin.ExtensionFunctionType"
                } else {
                    // ERROR TYPE
                    annotationType.toString() == "<ERROR TYPE: kotlin.ExtensionFunctionType>"
                }
        }

        if (typeArgs.isNotEmpty()) {
            var currentIndex = 0

            // extract context receivers (if any)
            if (contextReceiverCount > 0) {
                repeat(contextReceiverCount) {
                    addContextReceiver(typeArgs[currentIndex])
                    currentIndex++
                }
            }

            // extract receiver (if any)
            if (isExtensionFunctionType) {
                receiver(typeArgs[currentIndex])
                currentIndex++
            }

            // last is return type
            val returnType = typeArgs.last()
            returns(returnType)

            // middle is parameter type
            for (index in currentIndex until typeArgs.lastIndex) {
                val paramType = typeArgs[index]
                addParameter(KotlinValueParameterSpec.builder("", paramType).build())
            }
        }
    }
}

/**
 * Converts a KSP [KSTypeReference] to a CodeGentle [TypeName].
 *
 * This function resolves the type reference and delegates to [KSType.toTypeName].
 * It handles all the same type categories as [KSType.toTypeName].
 *
 * @return The corresponding [TypeName] for this KSP type reference
 * @throws IllegalStateException if the type reference cannot be resolved or converted
 */
public fun KSTypeReference.toTypeName(): TypeName {
    // Resolve the type reference and delegate to the KSType.toTypeName() function
    return resolve().toTypeName()
}

/**
 * Converts a KSP [KSDeclaration] to a CodeGentle [TypeName] if possible.
 *
 * This function handles different types of declarations:
 * - [KSClassDeclaration]: converted to ClassName
 * - [KSTypeAlias]: resolved to the underlying type and converted
 * - [KSTypeParameter]: converted to TypeVariableName
 *
 * @return The corresponding [TypeName] for this KSP declaration
 * @throws IllegalArgumentException if the declaration type is not supported
 */
public fun KSDeclaration.toTypeName(): TypeName {
    return when (this) {
        is KSClassDeclaration -> toClassName()
        is KSTypeAlias -> type.resolve().toTypeName()
        is KSTypeParameter -> toTypeVariableName()
        else -> throw IllegalArgumentException(
            "Unsupported declaration type: ${this::class.simpleName}. " +
                "Only KSClassDeclaration, KSTypeAlias, and KSTypeParameter are supported."
        )
    }
}

/**
 * Converts a KSP [KSTypeArgument] to a CodeGentle [TypeName].
 *
 * This function handles type arguments including:
 * - Regular types (converted to their corresponding TypeName)
 * - Star projections (converted to WildcardTypeName)
 * - Variance projections (converted to appropriate WildcardTypeName)
 *
 * @return The corresponding [TypeName] for this KSP type argument
 */
public fun KSTypeArgument.toTypeName(): TypeName {
    // Handle star projection
    if (variance == Variance.STAR) {
        return WildcardTypeName()
    }

    // Get the type
    val type = this.type ?: return WildcardTypeName() // Default to star projection if type is null

    // Handle variance
    return when (variance) {
        Variance.COVARIANT -> UpperWildcardTypeName(type.resolve().toTypeRef()) // out T
        Variance.CONTRAVARIANT -> LowerWildcardTypeName(type.resolve().toTypeRef()) // in T
        Variance.INVARIANT -> type.resolve().toTypeName() // T
        else -> type.resolve().toTypeName() // Default to invariant
    }
}

/**
 * Converts a KSP [KSTypeParameter] to a CodeGentle [TypeName].
 *
 * This function creates a TypeVariableName based on the type parameter
 * name and bounds.
 *
 * @return The corresponding [TypeName] ([TypeVariableName]) for this KSP type parameter
 */
public fun KSTypeParameter.toTypeName(): TypeName {
    return toTypeVariableName()
}

/**
 * Converts a KSP [KSTypeParameter] to a CodeGentle [TypeVariableName].
 *
 * This function creates a [TypeVariableName] based on the KSP type parameter.
 * It handles the type parameter name and bounds.
 *
 * @return The corresponding [TypeVariableName] for this KSP type parameter
 */
public fun KSTypeParameter.toTypeVariableName(): TypeVariableName {
    val name = name.asString()

    // Get bounds using the helper function from common-ksp
    val boundClassNames = this.boundClassNames

    // Convert bounds to TypeRef
    val boundRefs = boundClassNames.map { className -> className.ref() }

    // Create a TypeVariableName with the name and bounds
    return if (boundRefs.isEmpty()) {
        TypeVariableName(name)
    } else {
        TypeVariableName(name, boundRefs)
    }
}
