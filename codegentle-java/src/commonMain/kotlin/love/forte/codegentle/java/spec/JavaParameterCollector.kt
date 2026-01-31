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
package love.forte.codegentle.java.spec

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef

/**
 * Java parameters collector.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface JavaParameterCollector<B : JavaParameterCollector<B>> {
    /**
     * Add a parameter to this collector.
     */
    public fun addParameter(parameter: JavaParameterSpec): B

    /**
     * Add parameters to this collector.
     */
    public fun addParameters(parameters: Iterable<JavaParameterSpec>): B

    /**
     * Add parameters to this collector.
     */
    public fun addParameters(vararg parameters: JavaParameterSpec): B =
        addParameters(parameters.asList())
}

public inline fun <C : JavaParameterCollector<B>, B> C.addParameter(
    name: String,
    type: TypeRef<*>,
    block: JavaParameterSpec.Builder.() -> Unit = {}
): B {
    return addParameter(JavaParameterSpec(name, type, block))
}

public inline fun <C : JavaParameterCollector<B>, B> C.addParameter(
    name: String,
    type: TypeName,
    block: JavaParameterSpec.Builder.() -> Unit = {}
): B {
    return addParameter(JavaParameterSpec(name, type, block))
}
