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
package love.forte.codegentle.java.spec

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef

@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface JavaFieldCollector<B : JavaMethodCollector<B>> {
    /**
     * Add fields to this sealed type.
     */
    public fun addFields(vararg fields: JavaFieldSpec): B =
        addFields(fields.asList())

    /**
     * Add fields to this sealed type.
     */
    public fun addFields(fields: Iterable<JavaFieldSpec>): B

    /**
     * Add a field to this sealed type.
     */
    public fun addField(field: JavaFieldSpec): B
}

/**
 * Add a field with the given type and name.
 *
 * @param type the field type
 * @param name the field name
 * @param block the configuration block for the field
 * @return this collector instance
 */
public inline fun <B : JavaFieldCollector<B>> B.addField(
    type: TypeRef<*>,
    name: String,
    block: JavaFieldSpec.Builder.() -> Unit = {}
): B = addField(JavaFieldSpec(type, name, block))

/**
 * Add a field with the given type and name.
 *
 * @param type the field type
 * @param name the field name
 * @param block the configuration block for the field
 * @return this collector instance
 */
public inline fun <B : JavaFieldCollector<B>> B.addField(
    type: TypeName,
    name: String,
    block: JavaFieldSpec.Builder.() -> Unit = {}
): B = addField(JavaFieldSpec(type, name, block = block))
