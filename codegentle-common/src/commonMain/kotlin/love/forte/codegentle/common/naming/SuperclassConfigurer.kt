package love.forte.codegentle.common.naming

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation


/**
 * Config a [super class][TypeName].
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface SuperclassConfigurer<B : SuperclassConfigurer<B>> {
    public fun superclass(superclass: TypeName): B
}
