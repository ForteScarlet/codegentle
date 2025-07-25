package love.forte.codegentle.kotlin.ref.internal

import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.kotlin.ref.KotlinTypeNameRefStatus

/**
 * [love.forte.codegentle.kotlin.ref.KotlinTypeNameRefStatus] 的实现类。
 *
 * @property annotations 类型上的注解列表
 * @property nullable 类型是否可为空
 */
internal data class KotlinTypeNameRefStatusImpl(
    override val annotations: List<AnnotationRef>,
    override val nullable: Boolean
) : KotlinTypeNameRefStatus
