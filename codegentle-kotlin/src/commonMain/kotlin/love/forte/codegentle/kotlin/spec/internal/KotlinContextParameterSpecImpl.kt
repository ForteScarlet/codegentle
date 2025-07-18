package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.spec.KotlinContextParameterSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 *
 * @author ForteScarlet
 */
internal data class KotlinContextParameterSpecImpl(
    override val name: String?,
    override val typeRef: TypeRef<*>,
) : KotlinContextParameterSpec {
    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinContextParameterSpec(name='$name', type=${typeRef.typeName})"
    }
}



/**
 * Implementation of [KotlinContextParameterSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinContextParameterSpecBuilderImpl(
    override val name: String?,
    override val type: TypeRef<*>
) : KotlinContextParameterSpec.Builder {

    /**
     * Build [KotlinContextParameterSpec].
     */
    override fun build(): KotlinContextParameterSpec =
        KotlinContextParameterSpecImpl(name, type)
}
