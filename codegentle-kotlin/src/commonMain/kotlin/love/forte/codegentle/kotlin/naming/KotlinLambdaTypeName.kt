/*
 * Copyright (C) 2014-2024 Square, Inc.
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
package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.naming.CodeGentleNamingImplementation
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.TypeRefBuilderDsl
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierContainer
import love.forte.codegentle.kotlin.naming.internal.KotlinLambdaTypeNameBuilderImpl
import love.forte.codegentle.kotlin.spec.KotlinValueParameterCollector
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Represents a Kotlin lambda type name, such as `(String, Int) -> Boolean` or `suspend String.() -> Unit`.
 *
 * Lambda types in Kotlin can have:
 * - An optional receiver type (for extension lambdas)
 * - Context receivers (for context-dependent lambdas)
 * - Value parameters
 * - A return type
 * - Suspend modifier
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleNamingImplementation::class)
public interface KotlinLambdaTypeName : TypeName, KotlinModifierContainer {
    /**
     * This lambda's modifiers. It may contain the element [KotlinModifier.SUSPEND].
     */
    override val modifiers: Set<KotlinModifier>

    /**
     * The receiver type for extension lambdas, or null if this is not an extension lambda.
     * For example, in `String.() -> Unit`, the receiver type is `String`.
     */
    public val receiver: TypeRef<*>?

    /**
     * The context receiver types for context-dependent lambdas.
     * For example, in `context(Logger) String.() -> Unit`, the context receivers would contain `Logger`.
     */
    public val contextReceivers: List<TypeRef<*>>

    /**
     * The value parameters of the lambda.
     * For example, in `(String, Int) -> Boolean`, there would be two parameters with types `String` and `Int`.
     */
    public val parameters: List<KotlinValueParameterSpec>

    /**
     * The return type of the lambda.
     * For example, in `(String, Int) -> Boolean`, the return type is `Boolean`.
     */
    public val returnType: TypeRef<*>

    public interface Builder :
        BuilderDsl,
        KotlinValueParameterCollector<Builder> {
        /**
         * Sets the receiver type for this lambda.
         */
        public fun receiver(receiver: TypeRef<*>): Builder

        /**
         * Adds context receivers to this lambda.
         */
        public fun addContextReceivers(vararg contextReceivers: TypeRef<*>): Builder

        /**
         * Adds context receivers to this lambda.
         */
        public fun addContextReceivers(contextReceivers: Iterable<TypeRef<*>>): Builder

        /**
         * Adds a context receiver to this lambda.
         */
        public fun addContextReceiver(contextReceiver: TypeRef<*>): Builder

        /**
         * Sets the return type of this lambda.
         */
        public fun returns(type: TypeRef<*>): Builder

        /**
         * Set `suspend` for this lambda.
         */
        public fun suspend(isSuspend: Boolean = true): Builder

        /**
         * Builds the [KotlinLambdaTypeName].
         */
        public fun build(): KotlinLambdaTypeName
    }

    public companion object {
        /**
         * Creates a new [Builder] for building a [KotlinLambdaTypeName].
         */
        public fun builder(): Builder = KotlinLambdaTypeNameBuilderImpl()
    }
}

/**
 * Whether this lambda is `suspend`.
 * This is determined by checking if [KotlinModifier.SUSPEND] is present in the modifiers.
 */
public val KotlinLambdaTypeName.isSuspend: Boolean
    get() = KotlinModifier.SUSPEND in modifiers

/**
 * Creates a [KotlinLambdaTypeName] with the given return type.
 *
 * @param returnType the return type of the lambda
 * @param block the configuration block
 * @return a new [KotlinLambdaTypeName] instance
 */
public inline fun KotlinLambdaTypeName(
    returnType: TypeRef<*> = KotlinClassNames.UNIT.ref(),
    block: KotlinLambdaTypeName.Builder.() -> Unit = {}
): KotlinLambdaTypeName =
    KotlinLambdaTypeName.builder().returns(returnType).apply(block).build()

/**
 * Add a parameter with a empty name `""`.
 * In lambda, the parameter's name will be ignored if it's empty.
 *
 * Normal name:
 * ```Kotlin
 * // Builder:
 * addParameter("name", stringType)
 *
 * // Generated:
 * (name: String) -> Unit
 * ```
 *
 * Empty name:
 *
 * ```Kotlin
 * // Builder:
 * addParameter(stringType)
 * // or: addParameter("", stringType)
 *
 * // Generated:
 * (String) -> Unit
 * ```
 *
 */
public inline fun <C : KotlinValueParameterCollector<C>> C.addParameter(
    type: TypeRef<*>,
    block: KotlinValueParameterSpec.Builder.() -> Unit = {}
): C = addParameter(KotlinValueParameterSpec.builder("", type).apply(block).build())

/**
 * Sets the receiver type for this lambda.
 */
public inline fun <T : TypeName> KotlinLambdaTypeName.Builder.receiver(
    receiver: T,
    block: TypeRefBuilderDsl<T> = {}
): KotlinLambdaTypeName.Builder =
    receiver(receiver.ref(block))

/**
 * Adds a context receiver to this lambda.
 */
public inline fun <T : TypeName> KotlinLambdaTypeName.Builder.addContextReceiver(
    contextReceiver: T,
    block: TypeRefBuilderDsl<T> = {}
): KotlinLambdaTypeName.Builder =
    addContextReceiver(contextReceiver.ref(block))

public inline fun <T : TypeName> KotlinLambdaTypeName.Builder.returns(
    type: T,
    block: TypeRefBuilderDsl<T> = {}
): KotlinLambdaTypeName.Builder =
    returns(type.ref(block))

/**
 * Extension function to emit a [KotlinLambdaTypeName] to a [KotlinCodeWriter].
 *
 * Emits lambda types in Kotlin syntax, such as:
 * - `(String, Int) -> Boolean`
 * - `suspend String.() -> Unit`
 * - `context(Logger) (String) -> Unit`
 */
internal fun KotlinLambdaTypeName.emitTo(codeWriter: KotlinCodeWriter) {
    // emit modifiers
    codeWriter.emitModifiers(modifiers)

    // Emit context receivers if present
    if (contextReceivers.isNotEmpty()) {
        codeWriter.emit("context(")
        contextReceivers.forEachIndexed { index, contextReceiver ->
            if (index > 0) codeWriter.emit(", ")
            codeWriter.emit(contextReceiver)
        }
        codeWriter.emit(") ")
    }

    // Emit receiver if present
    receiver?.let { receiverType ->
        codeWriter.emit(receiverType)
        codeWriter.emit(".")
    }

    // Emit parameters
    codeWriter.emit("(")
    parameters.forEachIndexed { index, parameter ->
        if (index > 0) codeWriter.emit(", ")
        // For lambda parameters, we typically only emit the type, not the name
        // unless it's needed for clarity
        if (parameter.name.isEmpty()) {
            codeWriter.emit(parameter.typeRef)
        } else {
            parameter.emitTo(codeWriter)
        }
    }
    codeWriter.emit(")")

    // Emit arrow and return type
    codeWriter.emit(" -> ")

    codeWriter.emit(returnType)
}

