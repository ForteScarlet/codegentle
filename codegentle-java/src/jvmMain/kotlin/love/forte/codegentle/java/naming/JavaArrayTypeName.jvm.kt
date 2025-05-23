/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import love.forte.codegentle.java.ref.JavaTypeRefBuilderDsl
import love.forte.codegentle.java.ref.javaRef
import java.lang.reflect.GenericArrayType
import java.lang.reflect.Type
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.type.ArrayType

public inline fun Type.toArrayTypeNameComponent(
    block: JavaTypeRefBuilderDsl<*> = {}
): ArrayTypeName {
    return ArrayTypeName(toTypeName().javaRef(block))
}

/**
 * Create an [ArrayTypeName] from [GenericArrayType].
 */
public inline fun GenericArrayType.toArrayTypeName(
    block: JavaTypeRefBuilderDsl<*> = {}
): ArrayTypeName {
    return toArrayTypeName(linkedMapOf(), block)
}

@PublishedApi
internal inline fun GenericArrayType.toArrayTypeName(
    map: MutableMap<Type, TypeVariableName>,
    block: JavaTypeRefBuilderDsl<*> = {}
): ArrayTypeName {
    return ArrayTypeName(genericComponentType.toTypeName(map).javaRef(block))
}

// javax.lang.model

public fun ArrayType.toArrayTypeName(
    block: JavaTypeRefBuilderDsl<*> = {}
): ArrayTypeName {
    return toArrayTypeName(mutableMapOf(), block)
}

internal fun ArrayType.toArrayTypeName(
    map: MutableMap<TypeParameterElement, TypeVariableName>,
    block: JavaTypeRefBuilderDsl<*> = {}
): ArrayTypeName {
    return ArrayTypeName(componentType.toTypeName(map).javaRef(block))
}
