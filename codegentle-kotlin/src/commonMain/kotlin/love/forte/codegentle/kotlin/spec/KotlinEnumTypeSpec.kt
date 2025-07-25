package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.InitializerBlockCollector
import love.forte.codegentle.common.code.KDocCollector
import love.forte.codegentle.common.naming.SuperinterfaceCollector
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeVariableCollector
import love.forte.codegentle.kotlin.KotlinModifierCollector
import love.forte.codegentle.kotlin.spec.internal.KotlinEnumTypeSpecBuilderImpl

/**
 * A generated Kotlin enum class.
 *
 * ```kotlin
 * enum class EnumType {
 * }
 * ```
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinEnumTypeSpec : KotlinTypeSpec {
    override val name: String

    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.CLASS

    override val superclass: TypeName?
        get() = null

    /**
     * Enum constants.
     */
    public val enumConstants: Map<String, KotlinAnonymousClassTypeSpec?>

    override fun isMemberEmpty(): Boolean {
        return enumConstants.isEmpty() && super.isMemberEmpty()
    }

    public companion object {
        /**
         * Create a builder for an enum class.
         *
         * @param name the enum class name
         * @return a new builder
         */
        public fun builder(name: String): Builder {
            return KotlinEnumTypeSpecBuilderImpl(name)
        }
    }

    /**
     * Builder for [KotlinEnumTypeSpec].
     */
    public interface Builder :
        BuilderDsl,
        KotlinModifierCollector<Builder>,
        KotlinPropertyCollector<Builder>,
        KotlinFunctionCollector<Builder>,
        AnnotationRefCollector<Builder>,
        KDocCollector<Builder>,
        InitializerBlockCollector<Builder>,
        TypeVariableCollector<Builder>,
        SuperinterfaceCollector<Builder> {
        /**
         * The enum class name.
         */
        public val name: String

        /**
         * Add enum constant.
         */
        public fun addEnumConstant(name: String): Builder

        /**
         * Add enum constant with anonymous type.
         */
        public fun addEnumConstant(name: String, typeSpec: KotlinAnonymousClassTypeSpec): Builder

        /**
         * Build [KotlinEnumTypeSpec] instance.
         */
        public fun build(): KotlinEnumTypeSpec
    }
}

/**
 * Create a [KotlinEnumTypeSpec] with the given name.
 *
 * @param name the enum class name
 * @param block the configuration block
 * @return a new [KotlinEnumTypeSpec] instance
 */
public inline fun KotlinEnumTypeSpec(
    name: String,
    block: KotlinEnumTypeSpec.Builder.() -> Unit = {}
): KotlinEnumTypeSpec {
    return KotlinEnumTypeSpec.builder(name).apply(block).build()
}

public inline fun KotlinEnumTypeSpec.Builder.addEnumConstant(
    name: String,
    block: KotlinAnonymousClassTypeSpec.Builder.() -> Unit = {}
): KotlinEnumTypeSpec.Builder = addEnumConstant(name, KotlinAnonymousClassTypeSpec(block))
