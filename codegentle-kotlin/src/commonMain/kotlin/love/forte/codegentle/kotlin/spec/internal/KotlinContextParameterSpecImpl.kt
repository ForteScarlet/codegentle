/*
 * Copyright (C) 2025 Forte Scarlet
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
