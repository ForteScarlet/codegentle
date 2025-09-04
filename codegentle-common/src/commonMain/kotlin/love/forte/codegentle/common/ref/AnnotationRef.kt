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
package love.forte.codegentle.common.ref

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.computeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.AnnotationRef.MemberValue
import love.forte.codegentle.common.ref.internal.AnnotationRefImpl
import love.forte.codegentle.common.ref.internal.MultipleMemberValueImpl
import love.forte.codegentle.common.ref.internal.SingleMemberValueImpl


/**
 * Basic implementation of [AnnotationRefStatus] with no special content.
 */
public object BasicAnnotationRefStatus : AnnotationRefStatus {
    public object Builder : AnnotationRefStatusBuilder<BasicAnnotationRefStatus> {
        override fun build(): BasicAnnotationRefStatus = BasicAnnotationRefStatus
    }

    public object Factory : AnnotationRefStatusBuilderFactory<BasicAnnotationRefStatus, Builder> {
        override fun createBuilder(): Builder = Builder
    }
}

/**
 * A reference to an annotation.
 *
 * Could be emitted as [love.forte.codegentle.common.code.CodeArgumentPart.Literal]
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface AnnotationRef {
    public val typeName: ClassName
    public val members: Map<String, MemberValue>
    public val status: AnnotationRefStatus

    /**
     * The [CodeValue] of annotation ref's member.
     */
    public sealed interface MemberValue {
        public val codeValues: List<CodeValue>

        /**
         * A single value member.
         */

        @SubclassOptInRequired(CodeGentleRefImplementation::class)
        public interface Single : MemberValue {
            public val codeValue: CodeValue
        }

        /**
         * An array member or a `vararg` member.
         */
        @SubclassOptInRequired(CodeGentleRefImplementation::class)
        public interface Multiple : MemberValue
    }
}

public typealias AnnotationRefBuilderDsl<S, B> = AnnotationRefBuilder<S, B>.() -> Unit
public typealias BasicAnnotationRefBuilderDsl =
    AnnotationRefBuilder<BasicAnnotationRefStatus, BasicAnnotationRefStatus.Builder>.() -> Unit

/**
 * Create an [AnnotationRef] with status [S].
 *
 * @see AnnotationRef
 */
public inline fun <S : AnnotationRefStatus, B : AnnotationRefStatusBuilder<S>> ClassName.annotationRef(
    statusBuilderFactory: AnnotationRefStatusBuilderFactory<S, B>,
    block: AnnotationRefBuilderDsl<S, B> = {}
): AnnotationRef = AnnotationRefBuilder(this, statusBuilderFactory).also(block).build()

/**
 * Constructs an [AnnotationRef] instance based on the current [ClassName] with basic status.
 *
 * @param block An optional lambda receiver of type [AnnotationRefBuilder]
 *              that can be used to configure the [AnnotationRef].
 *              If no block is provided, a default empty block is used.
 * @return An instance of [AnnotationRef] constructed using the [AnnotationRefBuilder].
 */
public inline fun ClassName.annotationRef(
    block: BasicAnnotationRefBuilderDsl = {}
): AnnotationRef {
    return AnnotationRefBuilder(this, BasicAnnotationRefStatus.Factory).apply(block).build()
}

/**
 * Builder for [AnnotationRef].
 */
public class AnnotationRefBuilder<S : AnnotationRefStatus, B : AnnotationRefStatusBuilder<S>>(
    public val className: ClassName,
    builderFactory: AnnotationRefStatusBuilderFactory<S, B>
) : BuilderDsl,
    AnnotationRefBuildable<AnnotationRefBuilder<S, B>> {
    private val members = linkedMapOf<String, MemberValue>()
    public val status: B = builderFactory.createBuilder()

    override fun addMultipleMembers(
        name: String,
        codeValues: Iterable<CodeValue>
    ): AnnotationRefBuilder<S, B> = apply {
        members.computeValue(name) { _, value ->
            if (value == null) {
                MultipleMemberValueImpl(codeValues.toList())
            } else {
                MultipleMemberValueImpl(value.codeValues + codeValues)
            }
        }
    }

    override fun addMember(name: String, codeValue: CodeValue): AnnotationRefBuilder<S, B> = apply {
        members.computeValue(name) { _, value ->
            if (value == null) {
                SingleMemberValueImpl(codeValue)
            } else {
                MultipleMemberValueImpl(value.codeValues + codeValue)
            }
        }
    }

    public fun build(): AnnotationRef {
        return AnnotationRefImpl(
            typeName = className,
            members = members.toMap(linkedMapOf()),
            status = status.build()
        )
    }
}

/**
 * ```Kotlin
 * val ref = className.annotationRef(statusFactory) {
 *     status {
 *         // ...
 *     }
 * }
 */
public inline fun <
    S : AnnotationRefStatus,
    RB : AnnotationRefStatusBuilder<S>,
    B : AnnotationRefBuilder<S, RB>
    > B.status(
    block: RB.() -> Unit = {}
): B = apply { status.block() }
