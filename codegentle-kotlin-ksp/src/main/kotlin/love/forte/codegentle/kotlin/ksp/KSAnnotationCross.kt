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
import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.emitName
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.ksp.toClassName
import love.forte.codegentle.common.naming.*
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.common.ref.status
import love.forte.codegentle.kotlin.naming.KotlinClassNames
import love.forte.codegentle.kotlin.ref.KotlinAnnotationUseSite
import love.forte.codegentle.kotlin.ref.kotlinAnnotationRef
import love.forte.codegentle.kotlin.ref.kotlinStatus
import love.forte.codegentle.kotlin.spec.KotlinAnnotationTypeSpec
import love.forte.codegentle.kotlin.spec.KotlinPropertySpec

/**
 * Converts a KSP [KSAnnotation] to a CodeGentle [KotlinAnnotationTypeSpec].
 *
 * This function creates a [KotlinAnnotationTypeSpec] based on the KSP annotation.
 *
 * @return The corresponding [KotlinAnnotationTypeSpec] for this KSP annotation
 */
public fun KSAnnotation.toKotlinAnnotationTypeSpec(): KotlinAnnotationTypeSpec {
    val annotationType = annotationType.resolve().declaration
    val className = getClassName(annotationType)

    // Use the name property of ClassName which is the same as simpleName
    return KotlinAnnotationTypeSpec(className.name) {
        // Add properties based on annotation arguments
        arguments.forEach { argument ->
            val name = argument.name?.asString() ?: "value"
            val value = argument.value

            // Create a TypeRef based on the value type
            val typeRef: TypeRef<*> = when (value) {
                null -> KotlinClassNames.ANY.ref { kotlinStatus.nullable = true }
                is Boolean -> KotlinClassNames.BOOLEAN.ref()
                is Byte -> KotlinClassNames.BYTE.ref()
                is Short -> KotlinClassNames.SHORT.ref()
                is Int -> KotlinClassNames.INT.ref()
                is Long -> KotlinClassNames.LONG.ref()
                is Float -> KotlinClassNames.FLOAT.ref()
                is Double -> KotlinClassNames.DOUBLE.ref()
                is Char -> KotlinClassNames.CHAR.ref()
                is String -> KotlinClassNames.STRING.ref()
                is KSType -> PackageNames.KOTLIN_REFLECT.className("KClass")
                    .parameterized(WildcardTypeName().ref())
                    .ref()

                is KSDeclaration -> {
                    // Assume it's an enum value
                    val parentDeclaration = value.parentDeclaration
                    if (parentDeclaration is KSClassDeclaration) {
                        parentDeclaration.toClassName().ref()
                    } else {
                        ClassName("kotlin", "Enum")
                            .parameterized(WildcardTypeName().ref())
                            .ref()
                    }
                }

                is List<*> -> KotlinClassNames.ARRAY.ref()
                else -> KotlinClassNames.ANY.ref()
            }

            // Create a property spec for the argument
            val propertySpec = KotlinPropertySpec(name, typeRef)
            addProperty(propertySpec)
        }
    }
}

/**
 * Converts a KSP [KSAnnotation] to a CodeGentle [AnnotationRef].
 *
 * This function creates an [AnnotationRef] based on the KSP annotation.
 *
 * @return The corresponding [AnnotationRef] for this KSP annotation
 */
public fun KSAnnotation.toAnnotationRef(): AnnotationRef {
    val annotationType = annotationType.resolve().declaration
    val className = getClassName(annotationType)

    return className.kotlinAnnotationRef {
        // Set use site if available
        useSiteTarget?.let { target ->
            // Use the status extension function to access the KotlinAnnotationRefStatusBuilder
            status {
                useSite = target.toKotlinAnnotationUseSite()
            }
        }

        // Add arguments
        arguments.forEach { argument ->
            val name = argument.name?.asString() ?: "value"
            val value = argument.value

            // Convert the argument value to a CodeValue based on its type
            val codeValue = convertKSValueToCodeValue(value)
            addMember(name, codeValue)
        }
    }
}

/**
 * Helper function to convert a KSP [KSDeclaration] to a CodeGentle [ClassName].
 *
 * This function handles different types of declarations and converts them to a [ClassName].
 *
 * @param declaration The KSP declaration to convert
 * @return The corresponding [ClassName] for the declaration
 */
private fun getClassName(declaration: KSDeclaration): ClassName {
    return when (declaration) {
        is KSClassDeclaration -> declaration.toClassName()
        else -> {
            // Create a ClassName manually
            val packageName = declaration.packageName.asString()
            val simpleName = declaration.simpleName.asString()
            ClassName(packageName, simpleName)
        }
    }
}

/**
 * Converts a KSP value to a CodeGentle [CodeValue].
 *
 * This function handles different types of KSP values and converts them to appropriate CodeValue representations.
 *
 * @param value The KSP value to convert
 * @return The corresponding [CodeValue] for the KSP value
 */
private fun convertKSValueToCodeValue(value: Any?): CodeValue {
    return when (value) {
        // Handle null value
        null -> CodeValue("null")

        // Handle primitive types
        is Boolean, is Byte, is Short, is Int, is Double, is Char -> CodeValue(CodePart.literal(value))
        is Long -> CodeValue("${value}L")
        is Float -> CodeValue("${value}f")
        is String -> CodeValue(CodePart.string(value))

        // Handle class reference
        is KSType -> {
            val declaration = value.declaration
            val className = when (declaration) {
                is KSClassDeclaration -> declaration.toClassName()
                else -> {
                    val packageName = declaration.packageName.asString()
                    val simpleName = declaration.simpleName.asString()
                    ClassName(packageName, simpleName)
                }
            }
            CodeValue("%V::class") {
                emitType(className)
            }
        }

        // Handle enum value
        is KSDeclaration -> {
            val parentDeclaration = value.parentDeclaration
            val enumValue = value.simpleName.asString()

            if (parentDeclaration is KSClassDeclaration) {
                val enumClassName = parentDeclaration.toClassName()
                CodeValue("%V.%V") {
                    emitType(enumClassName)
                    emitName(enumValue)
                }
            } else {
                // Fallback if we can't determine the enum class
                CodeValue(CodePart.name(enumValue))
            }
        }

        // Handle array/vararg values
        is List<*> -> {
            val builder = CodeValue.builder()
            builder.addCode("[")

            var first = true
            for (item in value) {
                if (!first) {
                    builder.addCode(", ")
                }
                first = false

                builder.addCode(convertKSValueToCodeValue(item))
            }

            builder.addCode("]")
            builder.build()
        }

        // Handle other types as string representation
        else -> CodeValue(CodePart.literal(value))
    }
}

/**
 * Converts a KSP [AnnotationUseSiteTarget] to a CodeGentle [KotlinAnnotationUseSite].
 *
 * This function maps KSP annotation use site targets to their corresponding CodeGentle use sites.
 *
 * @return The corresponding [KotlinAnnotationUseSite] for this KSP annotation use site target
 * @throws IllegalArgumentException if the use site target cannot be mapped
 */
@Suppress("REDUNDANT_ELSE_IN_WHEN")
private fun AnnotationUseSiteTarget.toKotlinAnnotationUseSite(): KotlinAnnotationUseSite {
    return when (this) {
        AnnotationUseSiteTarget.FILE -> KotlinAnnotationUseSite.FILE
        AnnotationUseSiteTarget.PROPERTY -> KotlinAnnotationUseSite.PROPERTY
        AnnotationUseSiteTarget.FIELD -> KotlinAnnotationUseSite.FIELD
        AnnotationUseSiteTarget.GET -> KotlinAnnotationUseSite.GET
        AnnotationUseSiteTarget.SET -> KotlinAnnotationUseSite.SET
        AnnotationUseSiteTarget.RECEIVER -> KotlinAnnotationUseSite.RECEIVER
        AnnotationUseSiteTarget.PARAM -> KotlinAnnotationUseSite.PARAM
        AnnotationUseSiteTarget.SETPARAM -> KotlinAnnotationUseSite.SETPARAM
        AnnotationUseSiteTarget.DELEGATE -> KotlinAnnotationUseSite.DELEGATE
        AnnotationUseSiteTarget.ALL -> KotlinAnnotationUseSite.ALL
        else -> KotlinAnnotationUseSite.entries.find { it.name == this.name }
            ?: throw IllegalArgumentException("Unsupported annotation use site target: $this")
    }
}

