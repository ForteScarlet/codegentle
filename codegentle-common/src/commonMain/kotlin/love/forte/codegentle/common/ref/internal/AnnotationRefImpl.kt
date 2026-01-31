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
package love.forte.codegentle.common.ref.internal

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefStatus

/**
 *
 * @author ForteScarlet
 */
internal data class AnnotationRefImpl(
    override val typeName: ClassName,
    override val members: Map<String, AnnotationRef.MemberValue>,
    override val status: AnnotationRefStatus
) : AnnotationRef

internal class SingleMemberValueImpl(
    override val codeValue: CodeValue
) : AnnotationRef.MemberValue.Single {
    override val codeValues: List<CodeValue> = listOf(codeValue)

    override fun toString(): String {
        return "MemberValue.Single(codeValue=$codeValue)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnnotationRef.MemberValue.Single) return false

        if (codeValue != other.codeValue) return false
        if (codeValues != other.codeValues) return false

        return true
    }

    override fun hashCode(): Int {
        var result = codeValue.hashCode()
        result = 31 * result + codeValues.hashCode()
        return result
    }
}

internal class MultipleMemberValueImpl(
    override val codeValues: List<CodeValue>
) : AnnotationRef.MemberValue.Multiple {
    override fun toString(): String {
        return "MemberValue.Multiple(codeValues=$codeValues)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AnnotationRef.MemberValue.Multiple) return false

        if (codeValues != other.codeValues) return false

        return true
    }

    override fun hashCode(): Int {
        return codeValues.hashCode()
    }
}
