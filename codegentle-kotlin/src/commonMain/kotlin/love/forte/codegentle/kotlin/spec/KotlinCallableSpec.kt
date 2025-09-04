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
package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueCollector
import love.forte.codegentle.common.code.DocCollector
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierCollector
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
        KotlinModifierCollector<B>,
        AnnotationRefCollector<B>,
        CodeValueCollector<B>,
        DocCollector<B> {
        public fun build(): S
    }
}
