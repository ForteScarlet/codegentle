package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierBuilderContainer
import love.forte.codegentle.kotlin.spec.internal.KotlinObjectTypeSpecBuilderImpl

/**
 * A generated Kotlin object.
 *
 * ```kotlin
 * object MyObject {
 * }
 * ```
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinObjectTypeSpec : KotlinTypeSpec {
    override val name: String

    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.OBJECT

    /**
     * Whether this is a companion object.
     */
    public val isCompanion: Boolean
        get() = KotlinModifier.COMPANION in modifiers

    public companion object {
        public const val DEFAULT_COMPANION_NAME: String = "Companion"

        /**
         * Create a builder for an object.
         *
         * @param name the object name
         * @return a new builder
         */
        public fun builder(name: String): Builder {
            return KotlinObjectTypeSpecBuilderImpl(name, false)
        }

        /**
         * Create a builder for a companion object.
         *
         * @return a new builder
         */
        public fun companionBuilder(name: String = DEFAULT_COMPANION_NAME): Builder {
            return KotlinObjectTypeSpecBuilderImpl(name, true)
        }
    }

    /**
     * Builder for [KotlinObjectTypeSpec].
     */
    public interface Builder : BuilderDsl, KotlinModifierBuilderContainer<Builder> {
        /**
         * The object name.
         */
        public val name: String

        /**
         * Whether this is a companion object.
         */
        public val isCompanion: Boolean

        /**
         * Add KDoc.
         */
        public fun addKDoc(codeValue: CodeValue): Builder

        /**
         * Add KDoc.
         */
        public fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): Builder

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
        public fun addAnnotationRef(ref: AnnotationRef): Builder

        /**
         * Add annotation references.
         */
        public fun addAnnotationRefs(refs: Iterable<AnnotationRef>): Builder

        /**
         * Add modifiers.
         */
        override fun addModifiers(vararg modifiers: KotlinModifier): Builder

        /**
         * Add modifiers.
         */
        override fun addModifiers(modifiers: Iterable<KotlinModifier>): Builder

        /**
         * Add modifier.
         */
        override fun addModifier(modifier: KotlinModifier): Builder

        /**
         * Add type variable references.
         */
        public fun addTypeVariableRefs(vararg typeVariables: TypeRef<TypeVariableName>): Builder

        /**
         * Add type variable references.
         */
        public fun addTypeVariableRefs(typeVariables: Iterable<TypeRef<TypeVariableName>>): Builder

        /**
         * Add type variable reference.
         */
        public fun addTypeVariableRef(typeVariable: TypeRef<TypeVariableName>): Builder

        /**
         * Add superinterfaces.
         */
        public fun addSuperinterfaces(vararg superinterfaces: TypeName): Builder = apply {
            addSuperinterfaces(superinterfaces.asList())
        }

        /**
         * Add superinterfaces.
         */
        public fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): Builder

        /**
         * Add superinterface.
         */
        public fun addSuperinterface(superinterface: TypeName): Builder

        /**
         * Add properties.
         */
        public fun addProperties(vararg properties: KotlinPropertySpec): Builder = apply {
            addProperties(properties.asList())
        }

        /**
         * Add properties.
         */
        public fun addProperties(properties: Iterable<KotlinPropertySpec>): Builder

        /**
         * Add property.
         */
        public fun addProperty(property: KotlinPropertySpec): Builder

        /**
         * Add functions.
         */
        public fun addFunctions(functions: Iterable<KotlinFunctionSpec>): Builder

        /**
         * Add functions.
         */
        public fun addFunctions(vararg functions: KotlinFunctionSpec): Builder = apply {
            addFunctions(functions.asList())
        }

        /**
         * Add function.
         */
        public fun addFunction(function: KotlinFunctionSpec): Builder

        /**
         * Add subtype.
         */
        public fun addSubtype(subtype: KotlinTypeSpec): Builder

        /**
         * Add subtypes.
         */
        public fun addSubtypes(subtypes: Iterable<KotlinTypeSpec>): Builder

        /**
         * Add subtypes.
         */
        public fun addSubtypes(vararg subtypes: KotlinTypeSpec): Builder = apply {
            addSubtypes(subtypes.asList())
        }

        /**
         * Build [KotlinObjectTypeSpec] instance.
         */
        public fun build(): KotlinObjectTypeSpec
    }
}


/**
 * Create a [KotlinObjectTypeSpec] with the given name.
 *
 * @param name the object name
 * @param block the configuration block
 * @return a new [KotlinObjectTypeSpec] instance
 */
public inline fun KotlinObjectTypeSpec(
    name: String,
    isCompanion: Boolean = false,
    block: KotlinObjectTypeSpec.Builder.() -> Unit = {}
): KotlinObjectTypeSpec {
    return if (isCompanion) {
        KotlinObjectTypeSpec.companionBuilder(name).apply(block).build()
    } else {
        KotlinObjectTypeSpec.builder(name).apply(block).build()
    }
}

/**
 * Create a companion [KotlinObjectTypeSpec].
 *
 * @param block the configuration block
 * @return a new companion [KotlinObjectTypeSpec] instance
 */
public inline fun KotlinObjectTypeSpec(
    block: KotlinObjectTypeSpec.Builder.() -> Unit = {}
): KotlinObjectTypeSpec {
    return KotlinObjectTypeSpec.companionBuilder().apply(block).build()
}

public inline fun KotlinObjectTypeSpec.Builder.addKDoc(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinObjectTypeSpec.Builder = addKDoc(CodeValue(format, block))

public inline fun KotlinObjectTypeSpec.Builder.addInitializerBlock(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinObjectTypeSpec.Builder = addInitializerBlock(CodeValue(format, block))

public inline fun KotlinObjectTypeSpec.Builder.addProperty(
    name: String,
    type: TypeRef<*>,
    block: KotlinPropertySpec.Builder.() -> Unit = {}
): KotlinObjectTypeSpec.Builder = addProperty(KotlinPropertySpec(name, type, block))

public inline fun KotlinObjectTypeSpec.Builder.addFunction(
    name: String,
    type: TypeRef<*>,
    block: KotlinFunctionSpec.Builder.() -> Unit = {}
): KotlinObjectTypeSpec.Builder = addFunction(KotlinFunctionSpec(name, type, block))

public inline fun KotlinObjectTypeSpec.Builder.addSubtype(
    kind: KotlinTypeSpec.Kind,
    name: String,
    block: KotlinSimpleTypeSpec.Builder.() -> Unit = {}
): KotlinObjectTypeSpec.Builder = addSubtype(KotlinSimpleTypeSpec.builder(kind, name).apply(block).build())
