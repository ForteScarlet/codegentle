package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.spec.internal.KotlinAnnotationTypeSpecBuilderImpl

/**
 * A generated Kotlin annotation class.
 *
 * ```kotlin
 * annotation class AnnotationType {
 * }
 * ```
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinAnnotationTypeSpec : KotlinTypeSpec {
    /**
     * The name of this annotation class
     */
    override val name: String

    /**
     * The kind of this annotation type, always returns [KotlinTypeSpec.Kind.CLASS]
     */
    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.CLASS

    /**
     * Always returns null since the annotation class cannot have a superclass
     */
    override val superclass: TypeName?
        get() = null

    /**
     * Always returns an empty list since annotation class cannot have superinterfaces
     */
    override val superinterfaces: List<TypeName>
        get() = emptyList()

    /**
     * The properties of this annotation type. Must be immutable `val`.
     */
    override val properties: List<KotlinPropertySpec>

    public companion object {
        /**
         * Create a builder for an annotation class.
         *
         * @param name the annotation class name
         * @return a new builder
         */
        public fun builder(name: String): Builder {
            return KotlinAnnotationTypeSpecBuilderImpl(name)
        }
    }

    /**
     * Builder for [KotlinAnnotationTypeSpec].
     */
    public interface Builder {
        /**
         * The annotation class name.
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
        public fun addModifiers(vararg modifiers: KotlinModifier): Builder

        /**
         * Add modifiers.
         */
        public fun addModifiers(modifiers: Iterable<KotlinModifier>): Builder

        /**
         * Add modifier.
         */
        public fun addModifier(modifier: KotlinModifier): Builder

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
         * Add properties.
         */
        public fun addProperties(vararg properties: KotlinPropertySpec): Builder

        /**
         * Add properties.
         */
        public fun addProperties(properties: Iterable<KotlinPropertySpec>): Builder

        /**
         * Add property.
         */
        public fun addProperty(property: KotlinPropertySpec): Builder

        /**
         * Build [KotlinAnnotationTypeSpec] instance.
         */
        public fun build(): KotlinAnnotationTypeSpec
    }
}

/**
 * Create a [KotlinAnnotationTypeSpec] with the given name.
 *
 * @param name the annotation class name
 * @param block the configuration block
 * @return a new [KotlinAnnotationTypeSpec] instance
 */
public inline fun KotlinAnnotationTypeSpec(
    name: String,
    block: KotlinAnnotationTypeSpec.Builder.() -> Unit = {}
): KotlinAnnotationTypeSpec {
    return KotlinAnnotationTypeSpec.builder(name).apply(block).build()
}

/**
 * Add KDoc to this builder with the given format and configuration block.
 *
 * @param format the KDoc format string
 * @param block the configuration block for the KDoc
 * @return the builder instance
 */
public inline fun KotlinAnnotationTypeSpec.Builder.addKDoc(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinAnnotationTypeSpec.Builder = addKDoc(CodeValue(format, block))

/**
 * Add a property to this builder with the given name and type.
 *
 * @param name the property name
 * @param type the property type
 * @param block the configuration block for the property
 * @return the builder instance
 */
public inline fun KotlinAnnotationTypeSpec.Builder.addProperty(
    name: String,
    type: TypeRef<*>,
    block: KotlinPropertySpec.Builder.() -> Unit = {}
): KotlinAnnotationTypeSpec.Builder = addProperty(KotlinPropertySpec(name, type, block))
