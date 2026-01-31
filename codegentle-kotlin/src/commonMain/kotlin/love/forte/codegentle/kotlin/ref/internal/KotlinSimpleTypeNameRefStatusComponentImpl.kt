/*
 * Copyright (C) 2025-2026 Forte Scarlet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package love.forte.codegentle.kotlin.ref.internal

import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.kotlin.ref.KotlinSimpleTypeNameRefStatusComponent
import love.forte.codegentle.kotlin.ref.KotlinVariableNameRefStatusComponent

/**
 * [love.forte.codegentle.kotlin.ref.KotlinSimpleTypeNameRefStatusComponent] 的实现类。
 *
 * @property annotations 类型上的注解列表
 * @property nullable 类型是否可为空
 */
internal data class KotlinSimpleTypeNameRefStatusComponentImpl(
    override val annotations: List<AnnotationRef>,
    override val nullable: Boolean
) : KotlinSimpleTypeNameRefStatusComponent {

    override fun toString(): String {
        return "KotlinSimpleTypeNameRefStatusComponent(annotations=$annotations, nullable=$nullable)"
    }
}

internal data class KotlinVariableNameRefStatusComponentImpl(
    override val reified: Boolean
) : KotlinVariableNameRefStatusComponent {

    override fun toString(): String {
        return "KotlinVariableNameRefStatusComponent(reified=$reified)"
    }
}
