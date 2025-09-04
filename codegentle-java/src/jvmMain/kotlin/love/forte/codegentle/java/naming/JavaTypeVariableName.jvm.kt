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
package love.forte.codegentle.java.naming

import love.forte.codegentle.common.computeValue
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.ref
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import javax.lang.model.element.TypeParameterElement

public fun TypeVariable<*>.toTypeVariableName(): TypeVariableName =
    toTypeVariableName(linkedMapOf())

internal fun TypeVariable<*>.toTypeVariableName(map: MutableMap<Type, TypeVariableName>): TypeVariableName {
    val type = this
    return map.computeValue(type) { _, old ->
        old ?: TypeVariableName(
            name = type.name,
            bounds = bounds.mapNotNull { bound ->
                bound.takeIf { it != Object::class.java }?.toTypeName(map)?.ref()
            }
        )
    }!!
}

public fun javax.lang.model.type.TypeVariable.toTypeVariableName(): TypeVariableName =
    (asElement() as TypeParameterElement).toTypeVariableName()

public fun TypeParameterElement.toTypeVariableName(): TypeVariableName {
    return TypeVariableName(
        name = simpleName.toString(),
        bounds = bounds.map { it.toTypeName().ref() }
    )
}


internal fun javax.lang.model.type.TypeVariable.toTypeVariableName(
    typeVariables: MutableMap<TypeParameterElement, TypeVariableName>
): TypeVariableName {
    val element = asElement() as TypeParameterElement
    return typeVariables.computeValue(element) { _, old ->
        old ?: TypeVariableName(
            name = element.simpleName.toString(),
            bounds = element.bounds.mapNotNull { typeMirror ->
                typeMirror.toTypeName(typeVariables)
                    .takeIf { it != JavaClassNames.OBJECT }
                    ?.ref()
            }
        )
    }!!
}
