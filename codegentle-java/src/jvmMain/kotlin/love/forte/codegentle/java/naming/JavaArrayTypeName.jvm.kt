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

import love.forte.codegentle.common.naming.ArrayTypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.TypeRefBuilderDsl
import love.forte.codegentle.common.ref.ref
import java.lang.reflect.GenericArrayType
import java.lang.reflect.Type
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.type.ArrayType

public inline fun Type.toArrayTypeNameComponent(
    block: TypeRefBuilderDsl<*> = {}
): ArrayTypeName {
    return ArrayTypeName(toTypeName().ref(block))
}

/**
 * Create an [ArrayTypeName] from [GenericArrayType].
 */
public inline fun GenericArrayType.toArrayTypeName(
    block: TypeRefBuilderDsl<*> = {}
): ArrayTypeName {
    return toArrayTypeName(linkedMapOf(), block)
}

@PublishedApi
internal inline fun GenericArrayType.toArrayTypeName(
    map: MutableMap<Type, TypeVariableName>,
    block: TypeRefBuilderDsl<*> = {}
): ArrayTypeName {
    return ArrayTypeName(genericComponentType.toTypeName(map).ref(block))
}

// javax.lang.model

public fun ArrayType.toArrayTypeName(
    block: TypeRefBuilderDsl<*> = {}
): ArrayTypeName {
    return toArrayTypeName(mutableMapOf(), block)
}

internal fun ArrayType.toArrayTypeName(
    map: MutableMap<TypeParameterElement, TypeVariableName>,
    block: TypeRefBuilderDsl<*> = {}
): ArrayTypeName {
    return ArrayTypeName(componentType.toTypeName(map).ref(block))
}
