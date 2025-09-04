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
package love.forte.codegentle.common.code

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation

@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface CodeValueCollector<B : CodeValueCollector<B>> {
    /**
     * Add code to this collector.
     */
    public fun addCode(codeValue: CodeValue): B

    /**
     * Add code to the collector.
     */
    public fun addCode(format: String, vararg argumentParts: CodeArgumentPart): B

    /**
     * Add a statement to the collector.
     */
    public fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): B

    /**
     * Add a statement to the collector.
     */
    public fun addStatement(codeValue: CodeValue): B
}

@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface DocCollector<B : DocCollector<B>> {
    /**
     * Add the document to the collector.
     */
    public fun addDoc(codeValue: CodeValue): B

    /**
     * Add the document to the collector.
     */
    public fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): B
}

@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface InitializerBlockCollector<B : InitializerBlockCollector<B>> {
    /**
     * Add an initializer block.
     */
    public fun addInitializerBlock(codeValue: CodeValue): B

    /**
     * Add an initializer block.
     */
    public fun addInitializerBlock(format: String, vararg argumentParts: CodeArgumentPart): B

}

/**
 * Add code with a format string and a configuration block.
 *
 * @param format the format string
 * @param block the configuration block
 * @return this builder
 */
public inline fun <C : CodeValueCollector<C>> C.addCode(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): C = addCode(CodeValue(format, block))

/**
 * Add code with [CodeValue] builder block.
 *
 * @param block the [CodeValue] builder block
 * @return this builder
 */
public inline fun <C : CodeValueCollector<C>> C.addCode(
    block: CodeValueBuilderDsl = {}
): C = addCode(CodeValue(block))

/**
 * Add a statement with a format string and a configuration block.
 *
 * @param format the format string
 * @param block the configuration block
 * @return this builder
 */
public inline fun <C : CodeValueCollector<C>> C.addStatement(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): C = addStatement(CodeValue(format, block))

/**
 * Add a statement with the [CodeValue] builder block.
 *
 * @param block the [CodeValue] builder block
 * @return this builder
 */
public inline fun <C : CodeValueCollector<C>> C.addStatement(
    block: CodeValueBuilderDsl = {}
): C = addStatement(CodeValue(block))


public inline fun <C : InitializerBlockCollector<C>> C.addInitializerBlock(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): C = addInitializerBlock(CodeValue(format, block))


/**
 * Add KDoc with a format string and a configuration block.
 *
 * @param format the format string
 * @param block the configuration block
 * @return this builder
 */
public inline fun <C : DocCollector<C>> C.addDoc(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): C = addDoc(CodeValue(format, block))


/**
 * Add a comment with a format string and a configuration block.
 *
 * @param format the format string
 * @param block the configuration block
 * @return this builder
 */
public inline fun <C : CodeValueCollector<C>> C.addComment(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): C = addCode(CodeValue("// $format", block))

/**
 * Add a comment with the [CodeValue] builder block.
 *
 * @return this builder
 */
public fun <C : CodeValueCollector<C>> C.addComment(
    format: String,
    vararg argumentParts: CodeArgumentPart
): C = addCode(format, *argumentParts)
