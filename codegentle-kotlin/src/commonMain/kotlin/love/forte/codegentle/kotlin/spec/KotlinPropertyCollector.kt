package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.ref.kotlinRef

/**
 * Collector for [KotlinPropertySpec].
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface KotlinPropertyCollector<B : KotlinPropertyCollector<B>> {
    /**
     * Add properties.
     */
    public fun addProperties(vararg properties: KotlinPropertySpec): B =
        addProperties(properties.asList())

    /**
     * Add properties.
     */
    public fun addProperties(properties: Iterable<KotlinPropertySpec>): B

    /**
     * Add property.
     */
    public fun addProperty(property: KotlinPropertySpec): B
}

/**
 * Add a property to this builder with the given name and type.
 *
 * @param name the property name
 * @param type the property type
 * @param block the configuration block for the property
 * @return the builder instance
 */
public inline fun <B : KotlinPropertyCollector<B>> B.addProperty(
    name: String,
    type: TypeRef<*>,
    block: KotlinPropertySpec.Builder.() -> Unit = {}
): B = addProperty(KotlinPropertySpec(name, type, block))

/**
 * Add a property to this builder with the given name and type.
 *
 * @param name the property name
 * @param type the property type
 * @param block the configuration block for the property
 * @return the builder instance
 */
public inline fun <B : KotlinPropertyCollector<B>> B.addProperty(
    name: String,
    type: TypeName,
    block: KotlinPropertySpec.Builder.() -> Unit = {}
): B = addProperty(name, type.kotlinRef(), block)
