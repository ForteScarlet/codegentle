package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.InitializerBlockCollector
import love.forte.codegentle.common.code.KDocCollector
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.TypeVariableCollector
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierCollector
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
        KotlinModifierCollector<Builder>,
        AnnotationRefCollector<Builder>,
        KDocCollector<Builder>,
        InitializerBlockCollector<Builder>,
        TypeVariableCollector<Builder> {
        /**
         * The kind of the type.
         */
        public val kind: KotlinTypeSpec.Kind

        /**
         * The name of the type.
         */
        public val name: String

        /**
         * Set superclass.
         */
        public fun superclass(superclass: TypeName): Builder

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

/**
 * Create a [KotlinSimpleTypeSpec] with the given kind and name.
 *
 * @param kind the type kind (CLASS, INTERFACE, etc.)
 * @param name the type name
 * @param block the configuration block
 * @return a new [KotlinSimpleTypeSpec] instance
 */
public inline fun KotlinSimpleTypeSpec(
    kind: KotlinTypeSpec.Kind,
    name: String,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): KotlinSimpleTypeSpec {
    return KotlinSimpleTypeSpec.builder(kind, name).apply(block).build()
}

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
