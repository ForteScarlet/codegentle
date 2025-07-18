package love.forte.codegentle.common.code

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation

@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface CodeValueCollector<B : CodeValueCollector<B>> {
    /**
     * Add code to this collector.
     */
    public fun addCode(codeValue: CodeValue): B

    /**
     * Add code to the collector.
     */
    public fun addCode(format: String, vararg argumentParts: CodeArgumentPart): B

    /**
     * Add a statement to the collector.
     */
    public fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): B

    /**
     * Add a statement to the collector.
     */
    public fun addStatement(codeValue: CodeValue): B
}

@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface KDocCollector<B : KDocCollector<B>> {
    /**
     * Add KDoc to the collector.
     */
    public fun addKDoc(codeValue: CodeValue): B

    /**
     * Add KDoc to the collector.
     */
    public fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): B
}

@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface InitializerBlockCollector<B : InitializerBlockCollector<B>> {
    /**
     * Add an initializer block.
     */
    public fun addInitializerBlock(codeValue: CodeValue): B

    /**
     * Add an initializer block.
     */
    public fun addInitializerBlock(format: String, vararg argumentParts: CodeArgumentPart): B

}

/**
 * Add code with a format string and a configuration block.
 *
 * @param format the format string
 * @param block the configuration block
 * @return this builder
 */
public inline fun <C : CodeValueCollector<C>> C.addCode(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): C = addCode(CodeValue(format, block))

/**
 * Add a statement with a format string and a configuration block.
 *
 * @param format the format string
 * @param block the configuration block
 * @return this builder
 */
public inline fun <C : CodeValueCollector<C>> C.addStatement(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): C = addStatement(CodeValue(format, block))


public inline fun <C : InitializerBlockCollector<C>> C.addInitializerBlock(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): C = addInitializerBlock(CodeValue(format, block))


/**
 * Add KDoc with a format string and a configuration block.
 *
 * @param format the format string
 * @param block the configuration block
 * @return this builder
 */
public inline fun <C : KDocCollector<C>> C.addKDoc(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): C = addKDoc(CodeValue(format, block))
