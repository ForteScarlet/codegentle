package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollectable
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierBuilderContainer
import love.forte.codegentle.kotlin.spec.internal.KotlinSimpleTypeSpecBuilderImpl

/**
 * Represents a simple Kotlin type specification, such as class, interface, etc.
 *
 * @property kind The kind of the type
 * @property name The name of the type
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinSimpleTypeSpec : KotlinTypeSpec {
    // simple, data class, sealed class, interface, sealed interface

    override val kind: KotlinTypeSpec.Kind

    override val modifiers: Set<KotlinModifier>

    public val primaryConstructor: KotlinConstructorSpec?

    public val secondaryConstructors: List<KotlinConstructorSpec>

    public companion object {
        /**
         * Create a builder for a simple Kotlin type spec.
         *
         * @param kind the type kind
         * @param name the type name
         * @return a new builder
         */
        public fun builder(
            kind: KotlinTypeSpec.Kind,
            name: String
        ): Builder {
            return KotlinSimpleTypeSpecBuilderImpl(kind, name)
        }
    }

    /**
     * Builder for simple Kotlin type specification.
     */
    public interface Builder :
        BuilderDsl,
        KotlinModifierBuilderContainer,
        AnnotationRefCollectable<Builder> {
        /**
         * The kind of the type.
         */
        public val kind: KotlinTypeSpec.Kind

        /**
         * The name of the type.
         */
        public val name: String

        /**
         * Add KDoc.
         */
        public fun addKDoc(codeValue: CodeValue): Builder

        /**
         * Add KDoc.
         */
        public fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Set superclass.
         */
        public fun superclass(superclass: TypeName): Builder

        /**
         * Add initializer block.
         */
        public fun addInitializerBlock(codeValue: CodeValue): Builder

        /**
         * Add initializer block.
         */
        public fun addInitializerBlock(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Add annotation reference.
         */
        override fun addAnnotationRef(ref: AnnotationRef): Builder

        /**
         * Add multiple annotation references.
         */
        override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): Builder

        /**
         * Add multiple modifiers.
         */
        override fun addModifiers(vararg modifiers: KotlinModifier): Builder

        /**
         * Add multiple modifiers.
         */
        override fun addModifiers(modifiers: Iterable<KotlinModifier>): Builder

        /**
         * Add modifier.
         */
        override fun addModifier(modifier: KotlinModifier): Builder

        /**
         * Add multiple type variable references.
         */
        public fun addTypeVariableRefs(vararg typeVariables: TypeRef<TypeVariableName>): Builder

        /**
         * Add multiple type variable references.
         */
        public fun addTypeVariableRefs(typeVariables: Iterable<TypeRef<TypeVariableName>>): Builder

        /**
         * Add type variable reference.
         */
        public fun addTypeVariableRef(typeVariable: TypeRef<TypeVariableName>): Builder

        /**
         * Add multiple superinterfaces.
         */
        public fun addSuperinterfaces(vararg superinterfaces: TypeName): Builder

        /**
         * Add multiple superinterfaces.
         */
        public fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): Builder

        /**
         * Add superinterface.
         */
        public fun addSuperinterface(superinterface: TypeName): Builder

        /**
         * Add multiple properties.
         */
        public fun addProperties(vararg properties: KotlinPropertySpec): Builder

        /**
         * Add multiple properties.
         */
        public fun addProperties(properties: Iterable<KotlinPropertySpec>): Builder

        /**
         * Add property.
         */
        public fun addProperty(property: KotlinPropertySpec): Builder

        /**
         * Add multiple functions.
         */
        public fun addFunctions(functions: Iterable<KotlinFunctionSpec>): Builder

        /**
         * Add multiple functions.
         */
        public fun addFunctions(vararg functions: KotlinFunctionSpec): Builder

        /**
         * Add function.
         */
        public fun addFunction(function: KotlinFunctionSpec): Builder

        /**
         * Add multiple subtypes.
         */
        public fun addSubtypes(types: Iterable<KotlinTypeSpec>): Builder

        /**
         * Add multiple subtypes.
         */
        public fun addSubtypes(vararg types: KotlinTypeSpec): Builder

        /**
         * Add subtype.
         */
        public fun addSubtype(type: KotlinTypeSpec): Builder

        /**
         * Set the primary constructor for this type.
         */
        public fun primaryConstructor(constructor: KotlinConstructorSpec?): Builder

        /**
         * Add a secondary constructor to this type.
         */
        public fun addSecondaryConstructor(constructor: KotlinConstructorSpec): Builder

        /**
         * Add secondary constructors to this type.
         */
        public fun addSecondaryConstructors(constructors: Iterable<KotlinConstructorSpec>): Builder

        /**
         * Add secondary constructors to this type.
         */
        public fun addSecondaryConstructors(vararg constructors: KotlinConstructorSpec): Builder

        /**
         * Build a [KotlinSimpleTypeSpec] instance.
         *
         * @return A new [KotlinSimpleTypeSpec] instance
         */
        public fun build(): KotlinSimpleTypeSpec
    }
}

public inline fun KotlinSimpleTypeSpec.Builder.addKDoc(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinSimpleTypeSpec.Builder = addKDoc(CodeValue(format, block))

public inline fun KotlinSimpleTypeSpec.Builder.addInitializerBlock(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinSimpleTypeSpec.Builder = addInitializerBlock(CodeValue(format, block))

public inline fun KotlinSimpleTypeSpec.Builder.addProperty(
    name: String,
    type: TypeRef<*>,
    block: KotlinPropertySpec.Builder.() -> Unit = {}
): KotlinSimpleTypeSpec.Builder = addProperty(KotlinPropertySpec(name, type, block))

public inline fun KotlinSimpleTypeSpec.Builder.addFunction(
    name: String,
    type: TypeRef<*>,
    block: KotlinFunctionSpec.Builder.() -> Unit = {}
): KotlinSimpleTypeSpec.Builder = addFunction(KotlinFunctionSpec(name, type, block))

/**
 * Set the primary constructor for this type.
 */
public inline fun KotlinSimpleTypeSpec.Builder.primaryConstructor(
    block: KotlinConstructorSpec.Builder.() -> Unit = {}
): KotlinSimpleTypeSpec.Builder = primaryConstructor(KotlinConstructorSpec.builder().apply(block).build())

/**
 * Add a secondary constructor to this type.
 */
public inline fun KotlinSimpleTypeSpec.Builder.addSecondaryConstructor(
    block: KotlinConstructorSpec.Builder.() -> Unit = {}
): KotlinSimpleTypeSpec.Builder = addSecondaryConstructor(KotlinConstructorSpec.builder().apply(block).build())
