package love.forte.codegentle.java.spec

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef

/**
 * Java parameters collector.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface JavaParameterCollector<B : JavaParameterCollector<B>> {
    /**
     * Add a parameter to this collector.
     */
    public fun addParameter(parameter: JavaParameterSpec): B

    /**
     * Add parameters to this collector.
     */
    public fun addParameters(parameters: Iterable<JavaParameterSpec>): B

    /**
     * Add parameters to this collector.
     */
    public fun addParameters(vararg parameters: JavaParameterSpec): B =
        addParameters(parameters.asList())
}

public inline fun <C : JavaParameterCollector<B>, B> C.addParameter(
    name: String,
    type: TypeRef<*>,
    block: JavaParameterSpec.Builder.() -> Unit = {}
): B {
    return addParameter(JavaParameterSpec(name, type, block))
}

public inline fun <C : JavaParameterCollector<B>, B> C.addParameter(
    name: String,
    type: TypeName,
    block: JavaParameterSpec.Builder.() -> Unit = {}
): B {
    return addParameter(JavaParameterSpec(name, type, block))
}
