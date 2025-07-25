package love.forte.codegentle.kotlin.ref.internal

import love.forte.codegentle.kotlin.ref.KotlinAnnotationRefStatus
import love.forte.codegentle.kotlin.ref.KotlinAnnotationUseSite

/**
 *
 * @author ForteScarlet
 */
internal data class KotlinAnnotationRefStatusImpl(
    override val useSite: KotlinAnnotationUseSite?
) : KotlinAnnotationRefStatus
