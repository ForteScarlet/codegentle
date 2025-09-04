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

public interface KotlinSecondaryConstructorCollector<B : KotlinSecondaryConstructorCollector<B>> {
    public fun addSecondaryConstructor(constructor: KotlinConstructorSpec): B

    /**
     * Add secondary constructors to this collector.
     */
    public fun addSecondaryConstructors(constructors: Iterable<KotlinConstructorSpec>): B

    /**
     * Add secondary constructors to this collector.
     */
    public fun addSecondaryConstructors(vararg constructors: KotlinConstructorSpec): B =
        addSecondaryConstructors(constructors.asList())
}

public inline fun <B : KotlinSecondaryConstructorCollector<B>> B.addSecondaryConstructor(
    block: KotlinConstructorSpec.Builder.() -> Unit = {}
): B = addSecondaryConstructor(KotlinConstructorSpec(block))
