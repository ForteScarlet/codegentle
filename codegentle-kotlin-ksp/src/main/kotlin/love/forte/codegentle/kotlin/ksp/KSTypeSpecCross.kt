package love.forte.codegentle.kotlin.ksp

import com.google.devtools.ksp.symbol.*
import love.forte.codegentle.common.ksp.*
import love.forte.codegentle.common.naming.*
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.ref.kotlinRef
import love.forte.codegentle.kotlin.spec.*

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
        ClassKind.OBJECT -> KotlinTypeSpec.Kind.OBJECT
        else -> KotlinTypeSpec.Kind.CLASS // Default to CLASS for other kinds
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
    
    // Add primary constructor if available
    primaryConstructor?.let { constructor ->
        builder.primaryConstructor(constructor.toKotlinConstructorSpec())
    }
    
    // Add properties
    getAllProperties().forEach { property ->
        builder.addProperty(property.toKotlinPropertySpec())
    }
    
    // Add functions
    getAllFunctions().forEach { function ->
        // Skip constructors as they're handled separately
        if (!function.isConstructor()) {
            builder.addFunction(function.toKotlinFunctionSpec())
        }
    }
    
    // Add nested classes
    declarations
        .filterIsInstance<KSClassDeclaration>()
        .forEach { nestedClass ->
            builder.addSubtype(nestedClass.toKotlinTypeSpec())
        }
    
    return builder.build()
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
        // TODO Object should use KotlinObjectTypeSpec
        // TODO Check is value class -> use KotlinValueClassSpec
        else -> toKotlinSimpleTypeSpec()
    }
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
        .filter { it is KSClassDeclaration && it.classKind == ClassKind.ENUM_ENTRY }
        .forEach { entry ->
            // TODO: Implement enum entry conversion
            //  Enum entry is based on KotlinAnonymousClassSpec
        }
    
    // Add properties
    getAllProperties().forEach { property ->
        builder.addProperty(property.toKotlinPropertySpec())
    }
    
    // Add functions
    getAllFunctions().forEach { function ->
        // Skip constructors as they're handled separately
        if (!function.isConstructor()) {
            builder.addFunction(function.toKotlinFunctionSpec())
        }
    }
    
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
    getAllProperties().forEach { property ->
        builder.addProperty(property.toKotlinPropertySpec())
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
        // TODO: Implement type parameter conversion
        //  see typeVariables and TypeVariableName
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
        // TODO: Implement annotation conversion when KSAnnotationCross is implemented
    }
    
    // Set mutability
    builder.mutable(isMutable)
    
    // Add getter if available
    getter?.let { getterFunc ->
        // TODO: Implement getter conversion
    }
    
    // Add setter if available
    setter?.let { setterFunc ->
        // TODO: Implement setter conversion
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
        // TODO: Implement annotation conversion when KSAnnotationCross is implemented
    }
    
    // Set default value if available
    if (hasDefault) {
        // TODO: Handle default value
    }
    
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
 * It handles various types including:
 * - Primitive types
 * - Class types
 * - Array types
 * - Function types
 * - Parameterized types with type arguments
 * - Type projections (variance)
 * - Star projections
 *
 * @return The corresponding [TypeRef] for this KSP type reference
 */
public fun KSTypeReference.toTypeRef(): TypeRef<*> {
    // Resolve the type reference and delegate to the KSType.toTypeRef() function
    return resolve().toTypeRef()
}

/**
 * Converts a KSP [KSTypeArgument] to a CodeGentle [TypeRef].
 *
 * This function creates a [TypeRef] based on the KSP type argument.
 * It handles variance (type projections) and star projections.
 *
 * @return The corresponding [TypeRef] for this KSP type argument
 */
public fun KSTypeArgument.toTypeRef(): TypeRef<*> {
    // Handle star projection
    if (variance == Variance.STAR) {
        return WildcardTypeName().kotlinRef()
    }
    
    // Get the type
    val type = this.type ?: return WildcardTypeName().kotlinRef() // Default to star projection if type is null
    
    // Convert the type to a TypeRef
    val typeRef = type.resolve().toTypeRef()
    
    // Handle variance
    return when (variance) {
        Variance.COVARIANT -> UpperWildcardTypeName(typeRef).kotlinRef() // out T
        Variance.CONTRAVARIANT -> LowerWildcardTypeName(typeRef).kotlinRef() // in T
        Variance.INVARIANT -> typeRef // T
        else -> typeRef // Default to invariant
    }
}

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
    val name = declaration.simpleName.asString()
    return name in PRIMITIVE_NAMES
}

/**
 * Converts a KSP [KSTypeParameter] to a CodeGentle [TypeRef] of [TypeVariableName].
 *
 * This function creates a [TypeRef] based on the KSP type parameter.
 * It handles the type parameter name and bounds.
 *
 * @return The corresponding [TypeRef] for this KSP type parameter
 */
public fun KSTypeParameter.toTypeVariableRef(): TypeRef<TypeVariableName> {
    val name = name.asString()
    
    // Get bounds using the helper function from common-ksp
    val boundClassNames = getBoundClassNames()
    
    // Convert bounds to TypeRef
    val boundRefs = boundClassNames.map { className -> className.kotlinRef() }
    
    // Create a TypeVariableName with the name and bounds
    val typeVariableName = if (boundRefs.isEmpty()) {
        TypeVariableName(name)
    } else {
        TypeVariableName(name, boundRefs)
    }
    
    // Create a TypeRef from the TypeVariableName
    return typeVariableName.kotlinRef()
}

/**
 * Checks if a KSP [KSFunctionDeclaration] is a constructor.
 *
 * @return True if the function is a constructor, false otherwise
 */
private fun KSFunctionDeclaration.isConstructor(): Boolean {
    return simpleName.asString() == "<init>"
}

/**
 * Converts a KSP [KSType] to a CodeGentle [TypeRef].
 *
 * This function creates a [TypeRef] based on the KSP type.
 * It handles various types including:
 * - Primitive types
 * - Class types
 * - Array types
 * - Function types
 * - Parameterized types with type arguments
 *
 * @return The corresponding [TypeRef] for this KSP type
 */
public fun KSType.toTypeRef(): TypeRef<*> {
    // Handle nullable types
    val isNullable = isMarkedNullable
    
    // Check if it's an array type
    if (isArrayType()) {
        // Get the component type
        val componentType = getArrayComponentType()
        if (componentType != null) {
            val componentTypeRef = componentType.toTypeRef()
            return ArrayTypeName(componentTypeRef).kotlinRef {
                status.nullable = isNullable
            }
        }
    }
    
    // Check if it's a function type
    // TODO 转成 KotlinLambdaTypeName?
    if (isFunctionType()) {
        // It's a function type like Function0, Function1, etc.
        // Create a parameterized type with the appropriate function type as the raw type
        val simpleName = declaration.simpleName.asString()
        val className = ClassName("kotlin", simpleName)
        
        // If there are no type arguments, return a simple reference to the function type
        if (arguments.isEmpty()) {
            return className.kotlinRef {
                status.nullable = isNullable
            }
        }
        
        // Convert all type arguments to TypeRef
        val typeArgs = arguments.map { arg -> 
            arg.type?.resolve()?.toTypeRef() ?: WildcardTypeName().kotlinRef()
        }
        
        // Create a parameterized type with the function type and its type arguments
        return className.parameterized(typeArgs).kotlinRef {
            status.nullable = isNullable
        }
    }
    
    // Handle primitive types and other class types
    val typeName = when {
        isPrimitive() -> {
            // Handle primitive types by creating a ClassName for the Kotlin type
            val name = declaration.simpleName.asString()
            ClassName("kotlin", name)
        }
        declaration is KSClassDeclaration -> {
            (declaration as KSClassDeclaration).toClassName()
        }
        else -> {
            // Default to using the declaration name as a ClassName
            // This is a best-effort approach and may not be accurate for all types
            val name = declaration.simpleName.asString()
            val packageName = declaration.packageName.asString()
            ClassName(packageName, name)
        }
    }
    
    // If there are no type arguments, return a simple reference to the type
    if (arguments.isEmpty()) {
        return typeName.kotlinRef {
            status.nullable = isNullable
        }
    }
    
    // Convert all type arguments to TypeRef
    val typeArgs = arguments.map { arg -> 
        arg.type?.resolve()?.toTypeRef() ?: WildcardTypeName().kotlinRef()
    }
    
    // Create a parameterized type with the type and its type arguments
    return typeName.parameterized(typeArgs).kotlinRef {
        status.nullable = isNullable
    }
}
