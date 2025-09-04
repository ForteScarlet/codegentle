/*
 * Copyright (C) 2014-2024 Square, Inc.
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

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.TypeName

/**
 * Indicates whether this `TypeName` represents a primitive type in Java,
 * excluding the `void` type.
 *
 * A `TypeName` is considered a primitive type if it is an instance of
 * `JavaPrimitiveTypeName` and does not represent the `void` type.
 */
public val TypeName.isPrimitive: Boolean
    get() = this is JavaPrimitiveTypeName && this != JavaPrimitiveTypeNames.VOID

/**
 * If this type is one of the primitive types ([JavaPrimitiveTypeNames.VOID],
 * [JavaPrimitiveTypeNames.INT], etc.),
 * return the boxed type ([JavaClassNames.BOXED_VOID], [JavaClassNames.BOXED_INT], etc.).
 * Otherwise, return itself.
 */
public fun TypeName.box(): TypeName {
    return (this as? JavaPrimitiveTypeName)?.box() ?: this
}

/**
 * Converts the current `TypeName` into its unboxed representation if it corresponds
 * to a boxed primitive type. If the `TypeName` is not a boxed primitive or cannot
 * be unboxed, an exception is thrown.
 *
 *
 * @return The unboxed `TypeName` corresponding to the given `TypeName`.
 * @throws UnsupportedOperationException if the current `TypeName` cannot be unboxed.
 * @see JavaPrimitiveTypeNames
 */
public fun TypeName.unbox(): TypeName =
    when (this) {
        is JavaPrimitiveTypeName -> this
        is ClassName -> when (this) {
            JavaClassNames.BOXED_INT -> JavaPrimitiveTypeNames.INT
            JavaClassNames.BOXED_LONG -> JavaPrimitiveTypeNames.LONG
            JavaClassNames.BOXED_FLOAT -> JavaPrimitiveTypeNames.FLOAT
            JavaClassNames.BOXED_DOUBLE -> JavaPrimitiveTypeNames.DOUBLE
            JavaClassNames.BOXED_BYTE -> JavaPrimitiveTypeNames.BYTE
            JavaClassNames.BOXED_SHORT -> JavaPrimitiveTypeNames.SHORT
            JavaClassNames.BOXED_CHAR -> JavaPrimitiveTypeNames.CHAR
            JavaClassNames.BOXED_BOOLEAN -> JavaPrimitiveTypeNames.BOOLEAN
            JavaClassNames.BOXED_VOID -> JavaPrimitiveTypeNames.VOID
            else -> throw UnsupportedOperationException("Can't unbox ClassName $this")
        }

        else -> throw UnsupportedOperationException("Can't unbox $this")
    }

/**
 * Attempts to unbox a boxed Java type to its primitive equivalent or returns null if
 * the type cannot be unboxed.
 *
 * @return the corresponding unboxed primitive type if the type is a known boxed Java type,
 * or null if unboxing is not applicable.
 */
public fun TypeName.unboxOrNull(): TypeName? =
    when (this) {
        is JavaPrimitiveTypeName -> this
        is ClassName -> when (this) {
            JavaClassNames.BOXED_INT -> JavaPrimitiveTypeNames.INT
            JavaClassNames.BOXED_LONG -> JavaPrimitiveTypeNames.LONG
            JavaClassNames.BOXED_FLOAT -> JavaPrimitiveTypeNames.FLOAT
            JavaClassNames.BOXED_DOUBLE -> JavaPrimitiveTypeNames.DOUBLE
            JavaClassNames.BOXED_BYTE -> JavaPrimitiveTypeNames.BYTE
            JavaClassNames.BOXED_SHORT -> JavaPrimitiveTypeNames.SHORT
            JavaClassNames.BOXED_CHAR -> JavaPrimitiveTypeNames.CHAR
            JavaClassNames.BOXED_BOOLEAN -> JavaPrimitiveTypeNames.BOOLEAN
            JavaClassNames.BOXED_VOID -> JavaPrimitiveTypeNames.VOID
            else -> null
        }

        else -> null
    }
