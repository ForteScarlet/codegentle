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

/**
 * Begins a control flow block with the specified code value.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow(CodeValue("if (x > 0)"))
 *     addCode("println(\"positive\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0) {
 *     println("positive")
 * }
 * ```
 *
 * @param code the control flow code value
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.beginControlFlow(code: CodeValue): CodeValueBuilder =
    addCode(CodeValue(CodePart.beginControlFlow(code)))

/**
 * Begins a control flow block with the specified format string and arguments.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow("for (int i = 0; i < %V; i++)", CodePart.literal(10))
 *     addCode("processItem(i)")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```java
 * for (int i = 0; i < 10; i++) {
 *     processItem(i);
 * }
 * ```
 *
 * @param controlFlow the control flow format string
 * @param argumentParts the arguments for the format string
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.beginControlFlow(
    controlFlow: String,
    vararg argumentParts: CodeArgumentPart
): CodeValueBuilder = beginControlFlow(CodeValue(controlFlow, *argumentParts))

/**
 * Begins a control flow block with the specified format string and a builder block for arguments.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow("while (%V)") {
 *         addValue(CodePart.literal("hasNext()"))
 *     }
 *     addCode("processNext()")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * while (hasNext()) {
 *     processNext()
 * }
 * ```
 *
 * @param controlFlow the control flow format string
 * @param block the builder block for adding format arguments
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.beginControlFlow(
    controlFlow: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder = beginControlFlow(CodeValue(controlFlow, block))

/**
 * Adds a next control flow clause with the specified code value.
 * Typically used for `else`, `else if`, `catch`, `finally` clauses.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow("if (x > 0)")
 *     addCode("println(\"positive\")")
 *     nextControlFlow(CodeValue("else"))
 *     addCode("println(\"not positive\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0) {
 *     println("positive")
 * } else {
 *     println("not positive")
 * }
 * ```
 *
 * @param code the next control flow code value
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.nextControlFlow(code: CodeValue): CodeValueBuilder =
    addCode(CodeValue(CodePart.nextControlFlow(code)))

/**
 * Adds a next control flow clause with the specified format string and arguments.
 * Typically used for `else if`, `catch` with exception types, etc.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow("try")
 *     addCode("riskyOperation()")
 *     nextControlFlow("catch (%V e)", CodePart.literal("IOException"))
 *     addCode("handleError()")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```java
 * try {
 *     riskyOperation();
 * } catch (IOException e) {
 *     handleError();
 * }
 * ```
 *
 * @param controlFlow the control flow format string
 * @param argumentParts the arguments for the format string
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.nextControlFlow(
    controlFlow: String,
    vararg argumentParts: CodeArgumentPart
): CodeValueBuilder = nextControlFlow(CodeValue(controlFlow, *argumentParts))

/**
 * Adds a next control flow clause with the specified format string and a builder block for arguments.
 * Typically used for `else if`, `catch` with complex formatting, etc.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow("try")
 *     addCode("riskyOperation()")
 *     nextControlFlow("catch (%V e)") {
 *         addValue(CodePart.literal("IOException"))
 *     }
 *     addCode("handleError()")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```java
 * try {
 *     riskyOperation();
 * } catch (IOException e) {
 *     handleError();
 * }
 * ```
 *
 * @param controlFlow the control flow format string
 * @param block the builder block for adding format arguments
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.nextControlFlow(
    controlFlow: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder = nextControlFlow(CodeValue(controlFlow, block))

/**
 * Adds a next control flow clause using a builder block to construct the clause.
 * Typically used for complex `else if`, `catch`, or `finally` clauses.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow("if (x > 0)")
 *     addCode("println(\"positive\")")
 *     nextControlFlow {
 *         addCode("else if (x < 0)")
 *     }
 *     addCode("println(\"negative\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0) {
 *     println("positive")
 * } else if (x < 0) {
 *     println("negative")
 * }
 * ```
 *
 * @param block the builder block for constructing the control flow clause
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.nextControlFlow(
    block: CodeValueBuilderDsl = {}
): CodeValueBuilder = nextControlFlow(CodeValue(block))

/**
 * Ends the current control flow block.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow("if (condition)")
 *     addCode("doSomething()")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (condition) {
 *     doSomething()
 * }
 * ```
 *
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.endControlFlow(): CodeValueBuilder =
    addCode(CodeValue(CodePart.endControlFlow()))

/**
 * Ends the current control flow block with the specified code value.
 * Typically used for `do-while` loops where the `while` condition needs to be specified.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow("do")
 *     addCode("processItem()")
 *     addCode("count++")
 *     endControlFlow(CodeValue("while (count < 10)"))
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * do {
 *     processItem();
 *     count++;
 * } while (count < 10);
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * do {
 *     processItem()
 *     count++
 * } while (count < 10)
 * ```
 *
 * @param code the ending control flow code value
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.endControlFlow(code: CodeValue): CodeValueBuilder =
    addCode(CodeValue(CodePart.endControlFlow(code)))

/**
 * Ends the current control flow block with the specified format string and arguments.
 * Typically used for `do-while` loops where the `while` condition needs to be specified with parameters.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow("do")
 *     addCode("processItem()")
 *     addCode("count++")
 *     endControlFlow("while (count < %V)", CodePart.literal(10))
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * do {
 *     processItem();
 *     count++;
 * } while (count < 10);
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * do {
 *     processItem()
 *     count++
 * } while (count < 10)
 * ```
 *
 * @param controlFlow the control flow format string (e.g., "while(foo == %V)")
 * @param argumentParts the arguments for the format string
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.endControlFlow(
    controlFlow: String,
    vararg argumentParts: CodeArgumentPart
): CodeValueBuilder = endControlFlow(CodeValue(controlFlow, *argumentParts))

/**
 * Ends the current control flow block with the specified format string and a builder block for arguments.
 * Typically used for `do-while` loops where the `while` condition needs to be specified with complex formatting.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow("do")
 *     addCode("processItem()")
 *     addCode("count++")
 *     endControlFlow("while (count < %V)") {
 *         addValue(CodePart.literal("MAX_COUNT"))
 *     }
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * do {
 *     processItem();
 *     count++;
 * } while (count < MAX_COUNT);
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * do {
 *     processItem()
 *     count++
 * } while (count < MAX_COUNT)
 * ```
 *
 * @param controlFlow the control flow format string (e.g., "while(foo == %V)")
 * @param block the builder block for adding format arguments
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.endControlFlow(
    controlFlow: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder = endControlFlow(CodeValue(controlFlow, block))

/**
 * Ends the current control flow block using a builder block to construct the ending clause.
 * Typically used for `do-while` loops where complex conditions are needed.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     beginControlFlow("do")
 *     addCode("processItem()")
 *     addCode("count++")
 *     endControlFlow {
 *         addCode("while (count < limit && hasNext())")
 *     }
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * do {
 *     processItem();
 *     count++;
 * } while (count < limit && hasNext());
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * do {
 *     processItem()
 *     count++
 * } while (count < limit && hasNext())
 * ```
 *
 * @param block the builder block for constructing the ending control flow clause
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.endControlFlow(
    block: CodeValueBuilderDsl = {}
): CodeValueBuilder = endControlFlow(CodeValue(block))


/**
 * Creates a complete control flow block with the specified format string and arguments.
 * This is a convenience method that combines `beginControlFlow`, content, and `endControlFlow`.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     inControlflow("if (x > %V)", CodePart.literal(0)) {
 *         addCode("println(\"positive\")")
 *     }
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0) {
 *     println("positive")
 * }
 * ```
 *
 * @param format the control flow format string
 * @param argumentParts the arguments for the format string
 * @param block the content block to execute within the control flow
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.inControlFlow(
    format: String,
    vararg argumentParts: CodeArgumentPart,
    block: CodeValueBuilderDsl = {}
): CodeValueBuilder = apply {
    beginControlFlow(format, *argumentParts)
    block()
    endControlFlow()
}

/**
 * Creates a complete control flow block with the specified begin and optional end code values.
 * This is a convenience method that combines `beginControlFlow`, content, and `endControlFlow`.
 * Particularly useful for `do-while` loops.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     inControlflow(
 *         beginCode = CodeValue("do"),
 *         endCode = CodeValue("while (count < 10)")
 *     ) {
 *         addCode("processItem()")
 *         addCode("count++")
 *     }
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * do {
 *     processItem();
 *     count++;
 * } while (count < 10);
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * do {
 *     processItem()
 *     count++
 * } while (count < 10)
 * ```
 *
 * @param beginCode the beginning control flow code value
 * @param endCode the optional ending control flow code value (null for simple blocks)
 * @param block the content block to execute within the control flow
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.inControlFlow(
    beginCode: CodeValue,
    endCode: CodeValue? = null,
    block: CodeValueBuilderDsl = {}
): CodeValueBuilder = apply {
    beginControlFlow(beginCode)
    block()
    if (endCode != null) {
        endControlFlow(endCode)
    } else {
        endControlFlow()
    }
}

/**
 * Creates a complete control flow block with specified begin and optional end format strings and builder blocks.
 * This is the most flexible version that allows complex formatting for both begin and end clauses.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     inControlflow(
 *         beginControlFlow = "if (x == %V)",
 *         beginBlock = { addValue(CodePart.literal("SPECIAL")) },
 *         endControlFlow = null,
 *         endBlock = {}
 *     ) {
 *         addCode("handleSpecialCase()")
 *     }
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x == SPECIAL) {
 *     handleSpecialCase()
 * }
 * ```
 *
 * @param beginControlFlow the beginning control flow format string
 * @param beginBlock the builder block for beginning format arguments
 * @param endControlFlow the optional ending control flow format string
 * @param endBlock the builder block for ending format arguments
 * @param block the content block to execute within the control flow
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.inControlFlow(
    beginControlFlow: String,
    beginBlock: CodeValueSingleFormatBuilderDsl = {},
    endControlFlow: String? = null,
    endBlock: CodeValueSingleFormatBuilderDsl = {},
    block: CodeValueBuilderDsl = {}
): CodeValueBuilder = apply {
    beginControlFlow(beginControlFlow, beginBlock)
    block()
    if (endControlFlow != null) {
        endControlFlow(endControlFlow, endBlock)
    } else {
        endControlFlow()
    }
}

//// if else-if else

/**
 * Begins an `if` control flow with the specified condition code.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     ifControlFlow(CodeValue("x > 0"))
 *     addCode("println(\"positive\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0) {
 *     println("positive")
 * }
 * ```
 *
 * @param conditionCode the condition code for the if statement
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.ifControlFlow(
    conditionCode: CodeValue
): CodeValueBuilder =
    beginControlFlow("if (%V)", CodePart.otherCodeValue(conditionCode))

/**
 * Begins an `if` control flow with the specified condition format and arguments.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     ifControlFlow("x > %V", CodePart.literal(0))
 *     addCode("println(\"positive\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0) {
 *     println("positive")
 * }
 * ```
 *
 * @param conditionFormat the condition format string
 * @param conditionArguments the arguments for the condition format
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.ifControlFlow(
    conditionFormat: String,
    vararg conditionArguments: CodeArgumentPart
): CodeValueBuilder =
    ifControlFlow(CodeValue(conditionFormat, *conditionArguments))

/**
 * Begins an `if` control flow using a builder block for the condition.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     ifControlFlow {
 *         addCode("x > 0 && y < 10")
 *     }
 *     addCode("println(\"condition met\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0 && y < 10) {
 *     println("condition met")
 * }
 * ```
 *
 * @param block the builder block for constructing the condition
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.ifControlFlow(
    block: CodeValueBuilderDsl = {}
): CodeValueBuilder =
    ifControlFlow(CodeValue(block))

/**
 * Begins an `if` control flow with the specified condition format and a builder block.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     ifControlFlow("x == %V") {
 *         addValue(CodePart.literal("SPECIAL_VALUE"))
 *     }
 *     addCode("println(\"special case\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x == SPECIAL_VALUE) {
 *     println("special case")
 * }
 * ```
 *
 * @param conditionFormat the condition format string
 * @param block the builder block for adding format arguments
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.ifControlFlow(
    conditionFormat: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder =
    ifControlFlow(CodeValue(conditionFormat, block))

/**
 * Adds an `else if` control flow with the specified condition code.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     ifControlFlow("x > 0")
 *     addCode("println(\"positive\")")
 *     elseIfControlFlow(CodeValue("x < 0"))
 *     addCode("println(\"negative\")")
 *     elseControlFlow()
 *     addCode("println(\"zero\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0) {
 *     println("positive")
 * } else if (x < 0) {
 *     println("negative")
 * } else {
 *     println("zero")
 * }
 * ```
 *
 * @param conditionCode the condition code for the else-if clause
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.elseIfControlFlow(
    conditionCode: CodeValue
): CodeValueBuilder =
    nextControlFlow("else if (%V)", CodePart.otherCodeValue(conditionCode))

/**
 * Adds an `else if` control flow with the specified condition format and arguments.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     ifControlFlow("x > 0")
 *     addCode("println(\"positive\")")
 *     elseIfControlFlow("x == %V", CodePart.literal(-1))
 *     addCode("println(\"negative one\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0) {
 *     println("positive")
 * } else if (x == -1) {
 *     println("negative one")
 * }
 * ```
 *
 * @param conditionFormat the condition format string
 * @param conditionArguments the arguments for the condition format
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.elseIfControlFlow(
    conditionFormat: String,
    vararg conditionArguments: CodeArgumentPart
): CodeValueBuilder =
    elseIfControlFlow(CodeValue(conditionFormat, *conditionArguments))

/**
 * Adds an `else if` control flow using a builder block for the condition.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     ifControlFlow("x > 0")
 *     addCode("println(\"positive\")")
 *     elseIfControlFlow {
 *         addCode("x < 0")
 *     }
 *     addCode("println(\"negative\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0) {
 *     println("positive")
 * } else if (x < 0) {
 *     println("negative")
 * }
 * ```
 *
 * @param block the builder block for constructing the condition
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.elseIfControlFlow(
    block: CodeValueBuilderDsl = {}
): CodeValueBuilder =
    elseIfControlFlow(CodeValue(block))

/**
 * Adds an `else if` control flow with the specified condition format and a builder block.
 *
 * Example:
 * ```kotlin
 * CodeValue {
 *     ifControlFlow("x > 0")
 *     addCode("println(\"positive\")")
 *     elseIfControlFlow("x == %V") {
 *         addValue(CodePart.literal("SPECIAL_VALUE"))
 *     }
 *     addCode("println(\"special\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0) {
 *     println("positive")
 * } else if (x == SPECIAL_VALUE) {
 *     println("special")
 * }
 * ```
 *
 * @param conditionFormat the condition format string
 * @param block the builder block for adding format arguments
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.elseIfControlFlow(
    conditionFormat: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder =
    elseIfControlFlow(CodeValue(conditionFormat, block))

/**
 * Adds an `else` control flow clause.
 *
 * Example:
 * ```kotlin
 * CodeValue {
 *     ifControlFlow("x > 0")
 *     addCode("println(\"positive\")")
 *     elseIfControlFlow("x < 0")
 *     addCode("println(\"negative\")")
 *     elseControlFlow()
 *     addCode("println(\"zero\")")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```kotlin
 * if (x > 0) {
 *     println("positive")
 * } else if (x < 0) {
 *     println("negative")
 * } else {
 *     println("zero")
 * }
 * ```
 *
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.elseControlFlow(): CodeValueBuilder =
    nextControlFlow("else")

//// try-catch-finally

/**
 * Begins a `try` control flow block.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     tryControlFlow()
 *     addCode("riskyOperation()")
 *     catchControlFlow(CodeValue("Exception e"))
 *     addCode("handleError()")
 *     finallyControlFlow()
 *     addCode("cleanup()")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * try {
 *     riskyOperation();
 * } catch (Exception e) {
 *     handleError();
 * } finally {
 *     cleanup();
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * try {
 *     riskyOperation()
 * } catch (e: Exception) {
 *     handleError()
 * } finally {
 *     cleanup()
 * }
 * ```
 *
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.tryControlFlow(): CodeValueBuilder =
    beginControlFlow("try")

/**
 * Adds a `catch` control flow clause with the specified exception parameter.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     tryControlFlow()
 *     addCode("riskyOperation()")
 *     catchControlFlow(CodeValue("IOException e"))
 *     addCode("handleIOError()")
 *     catchControlFlow(CodeValue("Exception e"))
 *     addCode("handleGeneralError()")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * try {
 *     riskyOperation();
 * } catch (IOException e) {
 *     handleIOError();
 * } catch (Exception e) {
 *     handleGeneralError();
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * try {
 *     riskyOperation()
 * } catch (e: IOException) {
 *     handleIOError()
 * } catch (e: Exception) {
 *     handleGeneralError()
 * }
 * ```
 *
 * @param code the exception parameter code value (e.g., CodeValue("IOException e"))
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.catchControlFlow(
    code: CodeValue
): CodeValueBuilder =
    nextControlFlow("catch (%V)", CodePart.otherCodeValue(code))

/**
 * Adds a `catch` control flow clause with the specified exception parameter format and arguments.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     tryControlFlow()
 *     addCode("riskyOperation()")
 *     catchControlFlow("%V e", CodePart.literal("IOException"))
 *     addCode("handleIOError()")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code:
 * ```java
 * try {
 *     riskyOperation();
 * } catch (IOException e) {
 *     handleIOError();
 * }
 * ```
 *
 * @param exceptionParameterFormat the exception parameter format string
 * @param exceptionArguments the arguments for the exception parameter format
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.catchControlFlow(
    exceptionParameterFormat: String,
    vararg exceptionArguments: CodeArgumentPart
): CodeValueBuilder =
    catchControlFlow(CodeValue(exceptionParameterFormat, *exceptionArguments))

/**
 * Adds a `catch` control flow clause using a builder block for the exception parameter.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     tryControlFlow()
 *     addCode("riskyOperation()")
 *     catchControlFlow {
 *         addCode("java.io.IOException e")
 *     }
 *     addCode("handleIOError()")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * try {
 *     riskyOperation();
 * } catch (java.io.IOException e) {
 *     handleIOError();
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * try {
 *     riskyOperation()
 * } catch (e: java.io.IOException) {
 *     handleIOError()
 * }
 * ```
 *
 * @param block the builder block for constructing the exception parameter
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.catchControlFlow(
    block: CodeValueBuilderDsl = {}
): CodeValueBuilder =
    catchControlFlow(CodeValue(block))

/**
 * Adds a `catch` control flow clause with the specified exception parameter format and a builder block.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     tryControlFlow()
 *     addCode("riskyOperation()")
 *     catchControlFlow("%V e") {
 *         addValue(CodePart.literal("IOException"))
 *     }
 *     addCode("handleIOError()")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * try {
 *     riskyOperation();
 * } catch (IOException e) {
 *     handleIOError();
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * try {
 *     riskyOperation()
 * } catch (e: IOException) {
 *     handleIOError()
 * }
 * ```
 *
 * @param exceptionParameterFormat the exception parameter format string
 * @param block the builder block for adding format arguments
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.catchControlFlow(
    exceptionParameterFormat: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder =
    catchControlFlow(CodeValue(exceptionParameterFormat, block))

/**
 * Adds a `finally` control flow clause.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     tryControlFlow()
 *     addCode("riskyOperation()")
 *     catchControlFlow(CodeValue("Exception e"))
 *     addCode("handleError()")
 *     finallyControlFlow()
 *     addCode("cleanup()")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * try {
 *     riskyOperation();
 * } catch (Exception e) {
 *     handleError();
 * } finally {
 *     cleanup();
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * try {
 *     riskyOperation()
 * } catch (e: Exception) {
 *     handleError()
 * } finally {
 *     cleanup()
 * }
 * ```
 *
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.finallyControlFlow(): CodeValueBuilder =
    nextControlFlow("finally")


//// while do-while

/**
 * Begins a `while` control flow with the specified condition code.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     whileControlFlow(CodeValue("i < 10"))
 *     addCode("processItem(i)")
 *     addCode("i++")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * while (i < 10) {
 *     processItem(i);
 *     i++;
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * while (i < 10) {
 *     processItem(i)
 *     i++
 * }
 * ```
 *
 * @param conditionCode the condition code for the while loop
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.whileControlFlow(
    conditionCode: CodeValue
): CodeValueBuilder =
    beginControlFlow("while (%V)", CodePart.otherCodeValue(conditionCode))

/**
 * Begins a `while(true)` control flow - an infinite loop.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     whileTrueControlFlow()
 *     addCode("processItem(i)")
 *     addCode("i++")
 *     addCode("if (shouldBreak) break")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * while (true) {
 *     processItem(i);
 *     i++;
 *     if (shouldBreak) break;
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * while (true) {
 *     processItem(i)
 *     i++
 *     if (shouldBreak) break
 * }
 * ```
 *
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.whileTrueControlFlow(): CodeValueBuilder =
    whileControlFlow(CodeValue(CodePart.simple("true")))

/**
 * Begins a `while` control flow with the specified condition format and arguments.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     whileControlFlow("index < %V", CodePart.literal(10))
 *     addCode("processItem(index)")
 *     addCode("index++")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * while (index < 10) {
 *     processItem(index);
 *     index++;
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * while (index < 10) {
 *     processItem(index)
 *     index++
 * }
 * ```
 *
 * @param conditionFormat the condition format string
 * @param conditionArguments the arguments for the condition format
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.whileControlFlow(
    conditionFormat: String,
    vararg conditionArguments: CodeArgumentPart
): CodeValueBuilder =
    whileControlFlow(CodeValue(conditionFormat, *conditionArguments))

/**
 * Begins a `while` control flow using a builder block for the condition.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     whileControlFlow {
 *         addCode("hasNext() && count < limit")
 *     }
 *     addCode("processNext()")
 *     addCode("count++")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * while (hasNext() && count < limit) {
 *     processNext();
 *     count++;
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * while (hasNext() && count < limit) {
 *     processNext()
 *     count++
 * }
 * ```
 *
 * @param block the builder block for constructing the condition
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.whileControlFlow(
    block: CodeValueBuilderDsl = {}
): CodeValueBuilder =
    whileControlFlow(CodeValue(block))

/**
 * Begins a `while` control flow with the specified condition format and a builder block.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     whileControlFlow("index < %V") {
 *         addValue(CodePart.literal("MAX_SIZE"))
 *     }
 *     addCode("processItem(index)")
 *     addCode("index++")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * while (index < MAX_SIZE) {
 *     processItem(index);
 *     index++;
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * while (index < MAX_SIZE) {
 *     processItem(index)
 *     index++
 * }
 * ```
 *
 * @param conditionFormat the condition format string
 * @param block the builder block for adding format arguments
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.whileControlFlow(
    conditionFormat: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder =
    whileControlFlow(CodeValue(conditionFormat, block))

/**
 * Begins a `do` control flow block for a do-while loop.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     doControlFlow()
 *     addCode("processItem()")
 *     addCode("count++")
 *     doWhileEndControlFlow(CodeValue("count < 10"))
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * do {
 *     processItem();
 *     count++;
 * } while (count < 10);
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * do {
 *     processItem()
 *     count++
 * } while (count < 10)
 * ```
 *
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.doControlFlow(): CodeValueBuilder =
    beginControlFlow("do")

/**
 * Ends a do-while control flow with the specified condition code.
 * This is the only control flow function that uses `endControlFlow` instead of `nextControlFlow`.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     doControlFlow()
 *     addCode("processItem()")
 *     addCode("count++")
 *     doWhileEndControlFlow(CodeValue("count < 10"))
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * do {
 *     processItem();
 *     count++;
 * } while (count < 10);
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * do {
 *     processItem()
 *     count++
 * } while (count < 10)
 * ```
 *
 * @param conditionCode the condition code for the while clause
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.doWhileEndControlFlow(
    conditionCode: CodeValue
): CodeValueBuilder =
    endControlFlow("while (%V)", CodePart.otherCodeValue(conditionCode))

/**
 * Ends a do-while control flow with the specified condition format and arguments.
 * This is the only control flow function that uses `endControlFlow` instead of `nextControlFlow`.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     doControlFlow()
 *     addCode("processItem()")
 *     addCode("count++")
 *     doWhileEndControlFlow("count < %V", CodePart.literal(10))
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * do {
 *     processItem();
 *     count++;
 * } while (count < 10);
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * do {
 *     processItem()
 *     count++
 * } while (count < 10)
 * ```
 *
 * @param conditionFormat the condition format string
 * @param conditionArguments the arguments for the condition format
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.doWhileEndControlFlow(
    conditionFormat: String,
    vararg conditionArguments: CodeArgumentPart
): CodeValueBuilder =
    doWhileEndControlFlow(CodeValue(conditionFormat, *conditionArguments))

/**
 * Ends a do-while control flow using a builder block for the condition.
 * This is the only control flow function that uses `endControlFlow` instead of `nextControlFlow`.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     doControlFlow()
 *     addCode("processItem()")
 *     addCode("count++")
 *     doWhileEndControlFlow {
 *         addCode("count < 10")
 *     }
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * do {
 *     processItem();
 *     count++;
 * } while (count < 10);
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * do {
 *     processItem()
 *     count++
 * } while (count < 10)
 * ```
 *
 * @param block the builder block for constructing the condition
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.doWhileEndControlFlow(
    block: CodeValueBuilderDsl = {}
): CodeValueBuilder =
    doWhileEndControlFlow(CodeValue(block))

/**
 * Ends a do-while control flow with the specified condition format and a builder block.
 * This is the only control flow function that uses `endControlFlow` instead of `nextControlFlow`.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     doControlFlow()
 *     addCode("processItem()")
 *     addCode("count++")
 *     doWhileEndControlFlow("count < %V") {
 *         addValue(CodePart.literal("MAX_COUNT"))
 *     }
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * do {
 *     processItem();
 *     count++;
 * } while (count < MAX_COUNT);
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * do {
 *     processItem()
 *     count++
 * } while (count < MAX_COUNT)
 * ```
 *
 * @param conditionFormat the condition format string
 * @param block the builder block for adding format arguments
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.doWhileEndControlFlow(
    conditionFormat: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder =
    doWhileEndControlFlow(CodeValue(conditionFormat, block))

//// for 

/**
 * Begins a `for` control flow with the specified loop statement code.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     forControlFlow(CodeValue("int i = 0; i < 10; i++"))
 *     addCode("processItem(i)")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * for (int i = 0; i < 10; i++) {
 *     processItem(i);
 * }
 * ```
 *
 * ```kotlin
 * val codeValue = CodeValue {
 *     forControlFlow(CodeValue("i in 0 until 10"))
 *     addCode("processItem(i)")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * for (i in 0 until 10) {
 *     processItem(i)
 * }
 * ```
 *
 * @param loopCode the for loop statement code
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.forControlFlow(
    loopCode: CodeValue
): CodeValueBuilder =
    beginControlFlow("for (%V)", CodePart.otherCodeValue(loopCode))

/**
 * Begins a `for` control flow with the specified loop statement format and arguments.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     forControlFlow("int i = 0; i < %V; i++", CodePart.literal(10))
 *     addCode("processItem(i)")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * for (int i = 0; i < 10; i++) {
 *     processItem(i);
 * }
 * ```
 *
 * ```kotlin
 * val codeValue = CodeValue {
 *     forControlFlow(CodeValue("i in 0 until 10"))
 *     addCode("processItem(i)")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * for (i in 0 until 10) {
 *     processItem(i)
 * }
 * ```
 *
 * @param loopFormat the for loop statement format string
 * @param loopArguments the arguments for the loop statement format
 * @return this builder instance for method chaining
 */
public fun CodeValueBuilder.forControlFlow(
    loopFormat: String,
    vararg loopArguments: CodeArgumentPart
): CodeValueBuilder =
    forControlFlow(CodeValue(loopFormat, *loopArguments))

/**
 * Begins a `for` control flow using a builder block for the loop statement.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     forControlFlow {
 *         addCode("int i = 0; i < size; i++")
 *     }
 *     addCode("processItem(i)")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * for (int i = 0; i < size; i++) {
 *     processItem(i);
 * }
 * ```
 *
 * ```kotlin
 * val codeValue = CodeValue {
 *     forControlFlow(CodeValue("i in 0 until 10"))
 *     addCode("processItem(i)")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * for (i in 0 until size) {
 *     processItem(i)
 * }
 * ```
 *
 * @param block the builder block for constructing the loop statement
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.forControlFlow(
    block: CodeValueBuilderDsl = {}
): CodeValueBuilder =
    forControlFlow(CodeValue(block))

/**
 * Begins a `for` control flow with the specified loop statement format and a builder block.
 *
 * Example:
 * ```kotlin
 * val codeValue = CodeValue {
 *     forControlFlow("int i = 0; i < %V; i++") {
 *         addValue(CodePart.literal("MAX_SIZE"))
 *     }
 *     addCode("processItem(i)")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Java):
 * ```java
 * for (int i = 0; i < MAX_SIZE; i++) {
 *     processItem(i);
 * }
 * ```
 *
 * ```kotlin
 * val codeValue = CodeValue {
 *     forControlFlow(CodeValue("i in 0 until 10"))
 *     addCode("processItem(i)")
 *     endControlFlow()
 * }
 * ```
 *
 * Generated code (Kotlin):
 * ```kotlin
 * for (i in 0 until MAX_SIZE) {
 *     processItem(i)
 * }
 * ```
 *
 * @param loopFormat the for loop statement format string
 * @param block the builder block for adding format arguments
 * @return this builder instance for method chaining
 */
public inline fun CodeValueBuilder.forControlFlow(
    loopFormat: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): CodeValueBuilder =
    forControlFlow(CodeValue(loopFormat, block))
