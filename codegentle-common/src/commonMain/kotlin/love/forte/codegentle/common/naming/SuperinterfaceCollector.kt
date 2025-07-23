package love.forte.codegentle.common.naming

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation


/**
 * Collector for [super interface][TypeName]
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface SuperinterfaceCollector<B : SuperinterfaceCollector<B>> {
    /**
     * Add superinterfaces.
     */
    public fun addSuperinterfaces(vararg superinterfaces: TypeName): B =
        addSuperinterfaces(superinterfaces.asList())

    /**
     * Add superinterfaces.
     */
    public fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): B

    /**
     * Add superinterface.
     */
    public fun addSuperinterface(superinterface: TypeName): B
}
