/*
 * Copyright (C) 2015-2025 Forte Scarlet
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
@file:JvmName("JavaWildcardTypeNames")
@file:JvmMultifileClass

package love.forte.codegentle.java.naming

import love.forte.codegentle.common.naming.*
import love.forte.codegentle.common.ref.TypeRefBuilderDsl
import love.forte.codegentle.common.ref.ref
import java.lang.reflect.Type
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.type.WildcardType

public fun Type.toUpperWildcardTypeName(): UpperWildcardTypeName =
    listOf(this).toUpperWildcardTypeName()

public fun Type.toLowerWildcardTypeName(): LowerWildcardTypeName =
    listOf(this).toLowerWildcardTypeName()

public fun Iterable<Type>.toUpperWildcardTypeName(): UpperWildcardTypeName =
    UpperWildcardTypeName(this.map { it.toTypeName().ref() })

public fun Iterable<Type>.toLowerWildcardTypeName(): LowerWildcardTypeName =
    LowerWildcardTypeName(this.map { it.toTypeName().ref() })

public fun WildcardType.toWildcardTypeName(): WildcardTypeName {
    return toWildcardTypeName(linkedMapOf())
}

@PublishedApi
internal inline fun WildcardType.toWildcardTypeName(
    typeVariables: MutableMap<TypeParameterElement, TypeVariableName>,
    refBlock: TypeRefBuilderDsl<TypeName> = {},
): WildcardTypeName {
    val extendsBound = this.extendsBound
    if (extendsBound != null) {
        // Upper, has lower bounds
        val lower = extendsBound.toTypeName(typeVariables)
        return UpperWildcardTypeName(lower.ref(refBlock))
    }

    val superBound = this.superBound
    if (superBound == null) {
        return WildcardTypeName()
        // UpperWildcardTypeName(JavaClassNames.OBJECT.ref(refBlock))
    }

    val upper = superBound.toTypeName(typeVariables)
    return LowerWildcardTypeName(upper.ref(refBlock))
}

public fun java.lang.reflect.WildcardType.toWildcardTypeName(): WildcardTypeName {
    return toWildcardTypeName(linkedMapOf())
}

internal fun java.lang.reflect.WildcardType.toWildcardTypeName(
    map: MutableMap<Type, TypeVariableName>
): WildcardTypeName {
    val upperBounds = upperBounds.map { it.toTypeName(map).ref() }
    if (upperBounds.isNotEmpty()) {
        return LowerWildcardTypeName(upperBounds)
    }

    val lowerBounds = lowerBounds.map { it.toTypeName(map).ref() }
    if (upperBounds.isNotEmpty()) {
        return UpperWildcardTypeName(lowerBounds)
    }

    return WildcardTypeName()
    // return UpperWildcardTypeName(JavaClassNames.OBJECT.ref())
}
