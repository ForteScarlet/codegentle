package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollectable
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierBuilderContainer
import love.forte.codegentle.kotlin.KotlinModifierContainer
import love.forte.codegentle.kotlin.spec.internal.KotlinValueParameterSpecBuilderImpl
import love.forte.codegentle.kotlin.spec.internal.PropertyizationBuilderImpl

/**
 * A Kotlin value parameter.
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinValueParameterSpec : KotlinSpec, KotlinModifierContainer {
    /**
     * Parameter name.
     */
    public val name: String
    public val typeRef: TypeRef<*>

    public val annotations: List<AnnotationRef>
    override val modifiers: Set<KotlinModifier>
    public val kDoc: CodeValue
    public val defaultValue: CodeValue?

    public val propertyization: Propertyization?

    /**
     * Represents the propertyization configuration for a value parameter.
     * When a parameter is propertyized, it becomes a property in the constructor.
     */
    public interface Propertyization {
        /**
         * Whether the property should be mutable (var) or immutable (val).
         */
        public val mutable: Boolean
    }

    /**
     * Builder for [Propertyization].
     */
    public interface PropertyizationBuilder {
        /**
         * Whether the property should be mutable (var) or immutable (val).
         */
        public var mutable: Boolean

        /**
         * Set whether the property should be mutable (var) or immutable (val).
         *
         * @param mutable true for mutable property (var), false for immutable property (val)
         * @return this builder
         */
        public fun mutable(mutable: Boolean): PropertyizationBuilder

        /**
         * Build a [Propertyization] instance.
         *
         * @return a new [Propertyization] instance
         */
        public fun build(): Propertyization
    }

    /**
     * Builder for [KotlinValueParameterSpec].
     */
    public interface Builder : BuilderDsl,
        KotlinModifierBuilderContainer,
        AnnotationRefCollectable<Builder> {
        /**
         * Parameter name.
         */
        public val name: String

        /**
         * Parameter type.
         */
        public val type: TypeRef<*>

        /**
         * Set the default value for the parameter.
         *
         * @param codeValue the default value code
         * @return this builder
         */
        public fun defaultValue(codeValue: CodeValue): Builder

        /**
         * Set the default value for the parameter.
         *
         * @param format the default value format string
         * @param argumentParts the default value arguments
         * @return this builder
         */
        public fun defaultValue(format: String, vararg argumentParts: CodeArgumentPart): Builder

        /**
         * Add KDoc to the parameter.
         *
         * @param codeValue the KDoc code
         * @return this builder
         */
        public fun addKDoc(codeValue: CodeValue): Builder

        /**
         * Add KDoc to the parameter.
         *
         * @param format the KDoc format string
         * @param argumentParts the KDoc arguments
         * @return this builder
         */
        public fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): Builder

        override fun addModifier(modifier: KotlinModifier): Builder

        override fun addModifiers(modifiers: Iterable<KotlinModifier>): Builder

        override fun addModifiers(vararg modifiers: KotlinModifier): Builder

        /**
         * Set the propertyization for this parameter.
         * When propertyized, the parameter becomes a property in the constructor.
         *
         * @param propertyization the propertyization configuration
         * @return this builder
         */
        public fun propertyize(propertyization: Propertyization): Builder

        /**
         * Build a [KotlinValueParameterSpec] instance.
         *
         * @return a new [KotlinValueParameterSpec] instance
         */
        public fun build(): KotlinValueParameterSpec
    }

    public companion object {
        /**
         * Create a builder for a value parameter.
         *
         * @param name the parameter name
         * @param type the parameter type
         * @return a new builder
         */
        public fun builder(name: String, type: TypeRef<*>): Builder =
            KotlinValueParameterSpecBuilderImpl(name, type)

        /**
         * Create a [PropertyizationBuilder] instance.
         *
         * @return a new [PropertyizationBuilder] instance
         */
        public fun propertyizationBuilder(): PropertyizationBuilder {
            return PropertyizationBuilderImpl()
        }
    }
}

/**
 * Create a [KotlinValueParameterSpec.Propertyization] instance using a configuration block.
 *
 * @param block the configuration block for the propertyization
 * @return a new [KotlinValueParameterSpec.Propertyization] instance
 */
public inline fun propertyization(
    block: KotlinValueParameterSpec.PropertyizationBuilder.() -> Unit = {}
): KotlinValueParameterSpec.Propertyization = 
    KotlinValueParameterSpec.propertyizationBuilder().apply(block).build()

public inline fun KotlinValueParameterSpec.Builder.defaultValue(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinValueParameterSpec.Builder = apply {
    defaultValue(CodeValue(format, block))
}

public inline fun KotlinValueParameterSpec.Builder.addKDoc(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinValueParameterSpec.Builder = apply {
    addKDoc(CodeValue(format, block))
}

public fun KotlinValueParameterSpec.Builder.propertyize(mutable: Boolean): KotlinValueParameterSpec.Builder =
    propertyize { this.mutable = mutable }

public inline fun KotlinValueParameterSpec.Builder.propertyize(
    block: KotlinValueParameterSpec.PropertyizationBuilder.() -> Unit = {}
): KotlinValueParameterSpec.Builder = propertyize(propertyization(block))

/**
 * Set this parameter as a mutable property (var) in the constructor.
 *
 * @return this builder
 */
public fun KotlinValueParameterSpec.Builder.mutableProperty(): KotlinValueParameterSpec.Builder = 
    propertyize { mutable = true }

/**
 * Set this parameter as an immutable property (val) in the constructor.
 *
 * @return this builder
 */
public fun KotlinValueParameterSpec.Builder.immutableProperty(): KotlinValueParameterSpec.Builder = 
    propertyize { mutable = false }

/**
 * Set this parameter as a mutable property (var) in the constructor.
 * Alias for [mutableProperty].
 *
 * @return this builder
 */
public fun KotlinValueParameterSpec.Builder.varProperty(): KotlinValueParameterSpec.Builder = 
    mutableProperty()

/**
 * Set this parameter as an immutable property (val) in the constructor.
 * Alias for [immutableProperty].
 *
 * @return this builder
 */
public fun KotlinValueParameterSpec.Builder.valProperty(): KotlinValueParameterSpec.Builder = 
    immutableProperty()
