package love.forte.codegentle.common.naming

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation


/**
 * Configurer with [SuperclassConfigurer] and [SuperinterfaceCollector].
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface SuperConfigurer<B : SuperConfigurer<B>> : SuperclassConfigurer<B>, SuperinterfaceCollector<B>
