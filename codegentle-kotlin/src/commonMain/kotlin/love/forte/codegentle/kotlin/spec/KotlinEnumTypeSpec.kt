package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.InitializerBlockCollector
import love.forte.codegentle.common.code.KDocCollector
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifierBuilderContainer
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
        AnnotationRefCollector<Builder>,
        KotlinModifierBuilderContainer<Builder>,
        KDocCollector<Builder>,
        InitializerBlockCollector<Builder> {
        /**
         * The enum class name.
         */
        public val name: String

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
        public fun addSuperinterfaces(vararg superinterfaces: TypeName): Builder

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
         * Add functions.
         */
        public fun addFunctions(functions: Iterable<KotlinFunctionSpec>): Builder

        /**
         * Add functions.
         */
        public fun addFunctions(vararg functions: KotlinFunctionSpec): Builder

        /**
         * Add function.
         */
        public fun addFunction(function: KotlinFunctionSpec): Builder

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

public inline fun KotlinEnumTypeSpec.Builder.addProperty(
    name: String,
    type: TypeRef<*>,
    block: KotlinPropertySpec.Builder.() -> Unit = {}
): KotlinEnumTypeSpec.Builder = addProperty(KotlinPropertySpec(name, type, block))

public inline fun KotlinEnumTypeSpec.Builder.addFunction(
    name: String,
    type: TypeRef<*>,
    block: KotlinFunctionSpec.Builder.() -> Unit = {}
): KotlinEnumTypeSpec.Builder = addFunction(KotlinFunctionSpec(name, type, block))
