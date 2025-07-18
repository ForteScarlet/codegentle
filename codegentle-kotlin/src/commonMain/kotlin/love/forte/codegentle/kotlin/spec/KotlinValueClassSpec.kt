package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.InitializerBlockCollector
import love.forte.codegentle.common.code.KDocCollector
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.TypeVariableCollector
import love.forte.codegentle.kotlin.KotlinModifierCollector
import love.forte.codegentle.kotlin.spec.internal.KotlinValueClassSpecBuilderImpl

/**
 * A generated Kotlin value class.
 *
 * ```kotlin
 * value class ValueClass(val value: String)
 * ```
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinValueClassSpec : KotlinTypeSpec {
    override val name: String

    override val kind: KotlinTypeSpec.Kind
        get() = KotlinTypeSpec.Kind.CLASS

    /**
     * The primary constructor parameter of the value class.
     */
    public val primaryParameter: KotlinValueParameterSpec

    public companion object {
        /**
         * Create a builder for a value class.
         *
         * @param name the value class name
         * @param primaryParameter the primary constructor parameter
         * @return a new builder
         */
        public fun builder(name: String, primaryParameter: KotlinValueParameterSpec): Builder {
            return KotlinValueClassSpecBuilderImpl(name, primaryParameter)
        }
    }

    /**
     * Builder for [KotlinValueClassSpec].
     */
    public interface Builder :
        BuilderDsl,
        KotlinModifierCollector<Builder>,
        AnnotationRefCollector<Builder>,
        KDocCollector<Builder>,
        InitializerBlockCollector<Builder>,
        TypeVariableCollector<Builder> {
        /**
         * The value class name.
         */
        public val name: String

        /**
         * The primary constructor parameter of the value class.
         */
        public val primaryParameter: KotlinValueParameterSpec

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
         * Build [KotlinValueClassSpec] instance.
         */
        public fun build(): KotlinValueClassSpec
    }
}


/**
 * Create a [KotlinValueClassSpec] with the given name and primary parameter.
 *
 * @param name the value class name
 * @param primaryParameter the primary constructor parameter
 * @param block the configuration block
 * @return a new [KotlinValueClassSpec] instance
 */
public inline fun KotlinValueClassSpec(
    name: String,
    primaryParameter: KotlinValueParameterSpec,
    block: KotlinValueClassSpec.Builder.() -> Unit = {}
): KotlinValueClassSpec {
    return KotlinValueClassSpec.builder(name, primaryParameter).apply(block).build()
}

public inline fun KotlinValueClassSpec.Builder.addProperty(
    name: String,
    type: TypeRef<*>,
    block: KotlinPropertySpec.Builder.() -> Unit = {}
): KotlinValueClassSpec.Builder = addProperty(KotlinPropertySpec(name, type, block))

public inline fun KotlinValueClassSpec.Builder.addFunction(
    name: String,
    type: TypeRef<*>,
    block: KotlinFunctionSpec.Builder.() -> Unit = {}
): KotlinValueClassSpec.Builder = addFunction(KotlinFunctionSpec(name, type, block))
