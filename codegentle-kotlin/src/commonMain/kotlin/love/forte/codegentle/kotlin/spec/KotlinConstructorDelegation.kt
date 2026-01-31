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
package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.kotlin.spec.internal.KotlinConstructorDelegationBuilderImpl

public interface KotlinConstructorDelegation {
    public enum class Kind { THIS, SUPER }

    public val kind: Kind

    public val arguments: List<CodeValue>

    public companion object {
        public fun builder(kind: Kind): Builder {
            return KotlinConstructorDelegationBuilderImpl(kind)
        }
    }

    public interface Builder : BuilderDsl {

        public fun addArgument(argument: CodeValue): Builder

        public fun addArgument(format: String, vararg arguments: CodeArgumentPart): Builder

        public fun addArguments(vararg arguments: CodeValue): Builder

        public fun addArguments(arguments: Iterable<CodeValue>): Builder

        public fun build(): KotlinConstructorDelegation
    }
}

public inline fun KotlinConstructorDelegation(
    kind: KotlinConstructorDelegation.Kind,
    block: KotlinConstructorDelegation.Builder.() -> Unit = {}
): KotlinConstructorDelegation = KotlinConstructorDelegation.builder(kind).apply(block).build()

public inline fun KotlinConstructorDelegation.Builder.addArgument(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): KotlinConstructorDelegation.Builder =
    addArgument(CodeValue(format, block))
