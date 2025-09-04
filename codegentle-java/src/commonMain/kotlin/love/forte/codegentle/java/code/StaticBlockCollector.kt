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
package love.forte.codegentle.java.code

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueBuilderDsl
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl

@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface StaticBlockCollector<B : StaticBlockCollector<B>> {
    /**
     * Add a static block.
     */
    public fun addStaticBlock(codeValue: CodeValue): B

    /**
     * Add a static block.
     */
    public fun addStaticBlock(format: String, vararg argumentParts: CodeArgumentPart): B
}

/**
 * Add a static block with the given format string and builder configuration.
 *
 * @param format the format string for the static block code
 * @param block the configuration block for the code value
 * @return this collector instance
 */
public inline fun <B : StaticBlockCollector<B>> B.addStaticBlock(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): B = addStaticBlock(CodeValue(format, block))

/**
 * Add a static block with the given builder configuration.
 *
 * @param block the configuration block for the code value
 * @return this collector instance
 */
public inline fun <B : StaticBlockCollector<B>> B.addStaticBlock(
    block: CodeValueBuilderDsl = {}
): B = addStaticBlock(CodeValue(block))
