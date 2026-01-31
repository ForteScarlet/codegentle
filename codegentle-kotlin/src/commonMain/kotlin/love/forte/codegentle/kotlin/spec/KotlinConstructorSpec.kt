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
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.kotlin.spec.internal.KotlinConstructorSpecBuilderImpl

/**
 * A Kotlin constructor.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleKotlinSpecImplementation::class)
public interface KotlinConstructorSpec : KotlinCallableSpec {
    public val constructorDelegation: KotlinConstructorDelegation?

    // Not primary constructor may have non-empty code,
    // but if used as a primary constructor, it must be empty.
    override val code: CodeValue

    public companion object {
        public fun builder(): Builder {
            return KotlinConstructorSpecBuilderImpl()
        }
    }

    public interface Builder :
        BuilderDsl,
        KotlinCallableSpec.Builder<KotlinConstructorSpec, Builder>,
        KotlinValueParameterCollector<Builder> {
        /**
         * Set the constructor delegation.
         */
        public fun constructorDelegation(delegation: KotlinConstructorDelegation?): Builder

        override fun build(): KotlinConstructorSpec
    }
}

/**
 * Create a [KotlinConstructorSpec] with the given configuration.
 *
 * @param block the configuration block
 * @return a new [KotlinConstructorSpec] instance
 */
public inline fun KotlinConstructorSpec(
    block: KotlinConstructorSpec.Builder.() -> Unit = {}
): KotlinConstructorSpec {
    return KotlinConstructorSpec.builder().apply(block).build()
}

public inline fun KotlinConstructorSpec.Builder.constructorDelegation(
    kind: KotlinConstructorDelegation.Kind,
    block: KotlinConstructorDelegation.Builder.() -> Unit = {}
): KotlinConstructorSpec.Builder =
    constructorDelegation(KotlinConstructorDelegation(kind, block))

public inline fun KotlinConstructorSpec.Builder.thisConstructorDelegation(
    block: KotlinConstructorDelegation.Builder.() -> Unit = {}
): KotlinConstructorSpec.Builder =
    constructorDelegation(KotlinConstructorDelegation.Kind.THIS, block)

public inline fun KotlinConstructorSpec.Builder.superConstructorDelegation(
    block: KotlinConstructorDelegation.Builder.() -> Unit = {}
): KotlinConstructorSpec.Builder =
    constructorDelegation(KotlinConstructorDelegation.Kind.SUPER, block)
