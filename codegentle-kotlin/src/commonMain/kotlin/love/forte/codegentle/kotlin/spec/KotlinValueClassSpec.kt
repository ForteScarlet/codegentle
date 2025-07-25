package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.InitializerBlockCollector
import love.forte.codegentle.common.code.KDocCollector
import love.forte.codegentle.common.naming.SuperinterfaceCollector
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.TypeVariableCollector
import love.forte.codegentle.kotlin.KotlinModifierCollector
import love.forte.codegentle.kotlin.ref.kotlinRef
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
     * The primary constructor of the value class.
     * Cannot have constructorDelegation since value classes cannot inherit from other classes.
     */
    public val primaryConstructor: KotlinConstructorSpec

    /**
     * Value class cannot have subtypes.
     * Always empty.
     */
    override val subtypes: List<KotlinTypeSpec>
        get() = emptyList()

    public companion object {
        /**
         * Create a builder for a value class.
         *
         * @param name the value class name
         * @param primaryConstructor the primary constructor
         * @return a new builder
         */
        public fun builder(name: String, primaryConstructor: KotlinConstructorSpec): Builder {
            return KotlinValueClassSpecBuilderImpl(name, primaryConstructor)
        }
    }

    /**
     * Builder for [KotlinValueClassSpec].
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
         * The value class name.
         */
        public val name: String

        /**
         * The primary constructor of the value class.
         */
        public val primaryConstructor: KotlinConstructorSpec

        /**
         * Build [KotlinValueClassSpec] instance.
         */
        public fun build(): KotlinValueClassSpec
    }
}

/**
 * Create a [KotlinValueClassSpec] by providing the name and a pre-built primary constructor.
 *
 * @param name the value class name
 * @param primaryConstructor the pre-built primary constructor
 * @param block optional configuration block for additional class specifications
 * @return a new [KotlinValueClassSpec] instance
 */
public inline fun KotlinValueClassSpec(
    name: String,
    primaryConstructor: KotlinConstructorSpec,
    block: KotlinValueClassSpec.Builder.() -> Unit = {}
): KotlinValueClassSpec {
    return KotlinValueClassSpec.builder(name, primaryConstructor).apply(block).build()
}

/**
 * Create a [KotlinValueClassSpec] by providing the name and a builder for the primary constructor.
 *
 * @param name the value class name
 * @param primaryConstructor builder block for constructing the primary constructor
 * @param block optional configuration block for additional class specifications
 * @return a new [KotlinValueClassSpec] instance
 */
public inline fun KotlinValueClassSpec(
    name: String,
    primaryConstructor: KotlinConstructorSpec.Builder.() -> Unit,
    block: KotlinValueClassSpec.Builder.() -> Unit = {}
): KotlinValueClassSpec {
    return KotlinValueClassSpec(
        name = name,
        primaryConstructor = KotlinConstructorSpec(primaryConstructor),
        block = block
    )
}

/**
 * Create a [KotlinValueClassSpec] by providing the name and a single parameter specification.
 *
 * @param name the value class name
 * @param primaryParameter pre-built parameter specification for the primary constructor
 * @param block optional configuration block for additional class specifications
 * @return a new [KotlinValueClassSpec] instance
 */
public inline fun KotlinValueClassSpec(
    name: String,
    primaryParameter: KotlinValueParameterSpec,
    block: KotlinValueClassSpec.Builder.() -> Unit = {}
): KotlinValueClassSpec {
    return KotlinValueClassSpec(
        name = name,
        primaryConstructor = KotlinConstructorSpec {
            addParameter(primaryParameter)
        },
        block = block
    )
}

/**
 * Create a [KotlinValueClassSpec] by providing the name and parameter details using TypeRef.
 *
 * @param name the value class name
 * @param primaryParameterName name of the single parameter
 * @param primaryParameterType type of the parameter as TypeRef
 * @param primaryParameterBuilder optional configuration for the parameter
 * @param block optional configuration block for additional class specifications
 * @return a new [KotlinValueClassSpec] instance
 */
public inline fun KotlinValueClassSpec(
    name: String,
    primaryParameterName: String,
    primaryParameterType: TypeRef<*>,
    primaryParameterBuilder: KotlinValueParameterSpec.Builder.() -> Unit = {},
    block: KotlinValueClassSpec.Builder.() -> Unit = {}
): KotlinValueClassSpec {
    return KotlinValueClassSpec(
        name = name,
        primaryConstructor = KotlinConstructorSpec {
            addParameter(primaryParameterName, primaryParameterType, primaryParameterBuilder)
        },
        block = block
    )
}

/**
 * Create a [KotlinValueClassSpec] by providing the name and parameter details using TypeName.
 *
 * @param name the value class name
 * @param primaryParameterName name of the single parameter
 * @param primaryParameterType type of the parameter as TypeName
 * @param primaryParameterBuilder optional configuration for the parameter
 * @param block optional configuration block for additional class specifications
 * @return a new [KotlinValueClassSpec] instance
 */
public inline fun KotlinValueClassSpec(
    name: String,
    primaryParameterName: String,
    primaryParameterType: TypeName,
    primaryParameterBuilder: KotlinValueParameterSpec.Builder.() -> Unit = {},
    block: KotlinValueClassSpec.Builder.() -> Unit = {}
): KotlinValueClassSpec {
    return KotlinValueClassSpec(
        name = name,
        primaryConstructor = KotlinConstructorSpec {
            addParameter(primaryParameterName, primaryParameterType.kotlinRef(), primaryParameterBuilder)
        },
        block = block
    )
}

