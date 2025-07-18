package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueCollector
import love.forte.codegentle.common.code.KDocCollector
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierBuilderContainer
import love.forte.codegentle.kotlin.KotlinModifierContainer

/**
 * A Kotlin callable:
 * - simple function
 * - constructor
 * - property accessor
 *     - getter
 *     - setter
 *
 * @author ForteScarlet
 */
public sealed interface KotlinCallableSpec : KotlinSpec, KotlinModifierContainer {
    override val modifiers: Set<KotlinModifier>
    public val annotations: List<AnnotationRef>
    public val parameters: List<KotlinValueParameterSpec>
    public val kDoc: CodeValue
    public val code: CodeValue

    public interface Builder<S : KotlinCallableSpec, B : Builder<S, B>> :
        BuilderDsl,
        KotlinModifierBuilderContainer<B>,
        AnnotationRefCollector<B>,
        CodeValueCollector<B>,
        KDocCollector<B> {
        public fun build(): S
    }
}
