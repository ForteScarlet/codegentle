package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.KDocCollector
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeVariableCollector
import love.forte.codegentle.kotlin.KotlinModifierCollector
import love.forte.codegentle.kotlin.spec.internal.KotlinAnnotationTypeSpecBuilderImpl

/**
 * A Kotlin annotation class.
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
    public interface Builder :
        BuilderDsl,
        KotlinModifierCollector<Builder>,
        AnnotationRefCollector<Builder>,
        KDocCollector<Builder>,
        TypeVariableCollector<Builder>,
        KotlinPropertyCollector<Builder> {
        /**
         * The annotation class name.
         */
        public val name: String

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
