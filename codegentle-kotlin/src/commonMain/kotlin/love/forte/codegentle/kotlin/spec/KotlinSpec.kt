package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.spec.Spec
import love.forte.codegentle.kotlin.strategy.KotlinWriteStrategy
import love.forte.codegentle.kotlin.strategy.ToStringKotlinWriteStrategy
import love.forte.codegentle.kotlin.writer.KotlinCodeEmitter
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinSpec : Spec, KotlinCodeEmitter

public fun KotlinSpec.writeToKotlinString(strategy: KotlinWriteStrategy = ToStringKotlinWriteStrategy): String {
    return buildString {
        val writer = KotlinCodeWriter.create(this, strategy)
        emit(writer)
    }
}
