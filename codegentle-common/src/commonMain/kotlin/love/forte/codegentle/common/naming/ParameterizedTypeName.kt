/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2014-2025 Forte Scarlet
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
package love.forte.codegentle.common.naming

import love.forte.codegentle.common.naming.internal.ParameterizedTypeNameImpl
import love.forte.codegentle.common.ref.TypeRef

/**
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleNamingImplementation::class)
public interface ParameterizedTypeName : TypeName, Named {
    // `enclosingType` e.g.,
    // ```Java
    // Outer<String> outer = new Outer<>();
    // Outer<String>.Inner<Integer> inner = outer.new Inner<>();
    // ```

    public val enclosingType: ParameterizedTypeName?
    public val rawType: ClassName
    public val typeArguments: List<TypeRef<*>>

    /**
     * Same as [rawType.name][ClassName.name]
     */
    override val name: String
        get() = rawType.name

    /**
     * Returns a new [ParameterizedTypeName] instance for the specified [name] as nested inside this class.
     */
    public fun nestedClass(name: String): ParameterizedTypeName

    /**
     * Returns a new [ParameterizedTypeName] instance for the specified [name] as nested
     * inside this class, with the specified [typeArguments].
     */
    public fun nestedClass(name: String, typeArguments: List<TypeRef<*>>): ParameterizedTypeName

    /**
     * Returns a new [ParameterizedTypeName] instance for the specified [name] as nested
     * inside this class, with the specified [typeArguments].
     */
    public fun nestedClass(name: String, vararg typeArguments: TypeRef<*>): ParameterizedTypeName =
        nestedClass(name, typeArguments.asList())

}

/** Returns a parameterized type, applying [typeArguments] to [rawType]. */
public fun ParameterizedTypeName(rawType: ClassName, vararg typeArguments: TypeRef<*>): ParameterizedTypeName {
    return ParameterizedTypeNameImpl(null, rawType, typeArguments.asList())
}

/** Returns a parameterized type, applying [typeArguments] to [rawType]. */
public fun ParameterizedTypeName(rawType: ClassName, typeArguments: Iterable<TypeRef<*>>): ParameterizedTypeName {
    return ParameterizedTypeNameImpl(null, rawType, typeArguments.toList())
}

public fun ClassName.parameterized(vararg typeArguments: TypeRef<*>): ParameterizedTypeName =
    ParameterizedTypeName(this, *typeArguments)

public fun ClassName.parameterized(typeArguments: Iterable<TypeRef<*>>): ParameterizedTypeName =
    ParameterizedTypeName(this, typeArguments)
