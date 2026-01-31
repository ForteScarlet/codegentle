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
package love.forte.codegentle.common.ref

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.naming.TypeVariableName


/**
 * Collector for [TypeRef]<[love.forte.codegentle.common.naming.TypeVariableName]>.
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface TypeVariableCollector<B : TypeVariableCollector<B>> {
    /**
     * Add type variable reference.
     */
    public fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): B

    /**
     * Add type variable references.
     */
    public fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): B

    /**
     * Add type variable references.
     */
    public fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): B
}
