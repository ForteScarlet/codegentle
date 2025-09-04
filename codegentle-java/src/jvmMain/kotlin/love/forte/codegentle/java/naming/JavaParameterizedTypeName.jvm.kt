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

import love.forte.codegentle.common.naming.ParameterizedTypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.ref
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

public fun Class<*>.toParameterizedTypeName(vararg typeArguments: Type): ParameterizedTypeName {
    return ParameterizedTypeName(this.toJavaClassName(), typeArguments.map { it.toTypeName().ref() })
}

public fun ParameterizedType.toParameterizedTypeName(): ParameterizedTypeName =
    toParameterizedTypeName(linkedMapOf())

internal fun ParameterizedType.toParameterizedTypeName(map: MutableMap<Type, TypeVariableName>): ParameterizedTypeName {
    val type = this

    val rawType = (type.rawType as Class<*>).toJavaClassName()
    val ownerType = if (
        type.ownerType is ParameterizedType
        && !Modifier.isStatic((type.rawType as Class<*>).modifiers)
    ) {
        type.ownerType as ParameterizedType
    } else {
        null
    }

    val typeArguments = type.actualTypeArguments.map { it.toTypeName(map).ref() }

    return ownerType?.toParameterizedTypeName(map)?.nestedClass(rawType.simpleName, typeArguments)
        ?: ParameterizedTypeName(rawType, typeArguments)
}
