package love.forte.codegentle.kotlin.ksp

import com.google.devtools.ksp.symbol.*
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.ksp.toClassName
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.status
import love.forte.codegentle.kotlin.ref.KotlinAnnotationUseSite
import love.forte.codegentle.kotlin.ref.kotlinAnnotationRef
import love.forte.codegentle.kotlin.ref.kotlinRef
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
    val builder = KotlinAnnotationTypeSpec.builder(className.name)
    
    // Add properties based on annotation arguments
    arguments.forEach { argument ->
        val name = argument.name?.asString() ?: "value"
        val value = argument.value
        
        // Create a TypeRef based on the value type
        val typeRef = when (value) {
            null -> ClassName("kotlin", "Any").kotlinRef { status.nullable = true }
            is Boolean -> ClassName("kotlin", "Boolean").kotlinRef()
            is Byte -> ClassName("kotlin", "Byte").kotlinRef()
            is Short -> ClassName("kotlin", "Short").kotlinRef()
            is Int -> ClassName("kotlin", "Int").kotlinRef()
            is Long -> ClassName("kotlin", "Long").kotlinRef()
            is Float -> ClassName("kotlin", "Float").kotlinRef()
            is Double -> ClassName("kotlin", "Double").kotlinRef()
            is Char -> ClassName("kotlin", "Char").kotlinRef()
            is String -> ClassName("kotlin", "String").kotlinRef()
            is KSType -> ClassName("kotlin.reflect", "KClass").kotlinRef()
            is KSDeclaration -> {
                // Assume it's an enum value
                val parentDeclaration = value.parentDeclaration
                if (parentDeclaration is KSClassDeclaration) {
                    parentDeclaration.toClassName().kotlinRef()
                } else {
                    ClassName("kotlin", "Enum").kotlinRef()
                }
            }
            is List<*> -> ClassName("kotlin", "Array").kotlinRef()
            else -> ClassName("kotlin", "Any").kotlinRef()
        }
        
        // Create a property spec for the argument
        val propertySpec = KotlinPropertySpec.builder(name, typeRef).build()
        builder.addProperty(propertySpec)
    }
    
    return builder.build()
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
        is Boolean -> CodeValue(value.toString())
        is Byte -> CodeValue("${value}")
        is Short -> CodeValue("${value}")
        is Int -> CodeValue("${value}")
        is Long -> CodeValue("${value}L")
        is Float -> CodeValue("${value}f")
        is Double -> CodeValue("${value}")
        is Char -> CodeValue("'${value}'")
        is String -> CodeValue("\"${value}\"")
        
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
            CodeValue("${className.simpleName}::class")
        }
        
        // Handle enum value
        is KSDeclaration -> {
            val parentDeclaration = value.parentDeclaration
            val enumValue = value.simpleName.asString()
            
            if (parentDeclaration is KSClassDeclaration) {
                val enumClassName = parentDeclaration.toClassName()
                CodeValue("${enumClassName.simpleName}.${enumValue}")
            } else {
                // Fallback if we can't determine the enum class
                CodeValue(enumValue)
            }
        }
        
        // Handle array/vararg values
        is List<*> -> {
            val builder = CodeValue.builder()
            builder.add("[")
            
            var first = true
            for (item in value) {
                if (!first) {
                    builder.add(", ")
                }
                first = false
                
                builder.add(convertKSValueToCodeValue(item))
            }
            
            builder.add("]")
            builder.build()
        }
        
        // Handle other types as string representation
        else -> CodeValue(value.toString())
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

