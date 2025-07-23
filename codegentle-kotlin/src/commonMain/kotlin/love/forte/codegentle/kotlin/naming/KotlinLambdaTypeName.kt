package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.KotlinModifierCollector
import love.forte.codegentle.kotlin.KotlinModifierContainer
import love.forte.codegentle.kotlin.naming.internal.KotlinLambdaTypeNameBuilderImpl
import love.forte.codegentle.kotlin.ref.kotlinRef
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
public interface KotlinLambdaTypeName : TypeName, KotlinModifierContainer {
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
        KotlinModifierCollector<Builder>,
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
    returnType: TypeRef<*> = KotlinNames.Classes.UNIT.kotlinRef(),
    block: KotlinLambdaTypeName.Builder.() -> Unit = {}
): KotlinLambdaTypeName =
    KotlinLambdaTypeName.builder().returns(returnType).apply(block).build()

/**
 * Creates a suspending [KotlinLambdaTypeName] with the given return type.
 *
 * @param returnType the return type of the lambda
 * @param block the configuration block
 * @return a new suspending [KotlinLambdaTypeName] instance
 */
public inline fun buildKotlinSuspendLambdaTypeName(
    returnType: TypeRef<*> = KotlinNames.Classes.UNIT.kotlinRef(),
    block: KotlinLambdaTypeName.Builder.() -> Unit = {}
): KotlinLambdaTypeName =
    KotlinLambdaTypeName.builder().returns(returnType).suspend().apply(block).build()


/**
 * Makes this lambda suspending by adding the [KotlinModifier.SUSPEND] modifier.
 */
public fun KotlinLambdaTypeName.Builder.suspend(): KotlinLambdaTypeName.Builder =
    addModifier(KotlinModifier.SUSPEND)


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

