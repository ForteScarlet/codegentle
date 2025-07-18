package love.forte.codegentle.kotlin.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.kotlin.spec.internal.ConstructorDelegationBuilderImpl

public interface ConstructorDelegation {
    public enum class Kind { THIS, SUPER }

    public val kind: Kind

    public val arguments: List<CodeValue>

    public companion object {
        public fun builder(kind: Kind): Builder {
            return ConstructorDelegationBuilderImpl(kind)
        }
    }

    public interface Builder : BuilderDsl {

        public fun addArgument(argument: CodeValue): Builder

        public fun addArgument(format: String, vararg arguments: CodeArgumentPart): Builder

        public fun addArguments(vararg arguments: CodeValue): Builder

        public fun addArguments(arguments: Iterable<CodeValue>): Builder

        public fun build(): ConstructorDelegation
    }
}

public inline fun ConstructorDelegation(
    kind: ConstructorDelegation.Kind,
    block: ConstructorDelegation.Builder.() -> Unit = {}
): ConstructorDelegation = ConstructorDelegation.builder(kind).apply(block).build()

public inline fun ConstructorDelegation.Builder.addArgument(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
): ConstructorDelegation.Builder =
    addArgument(CodeValue(format, block))
