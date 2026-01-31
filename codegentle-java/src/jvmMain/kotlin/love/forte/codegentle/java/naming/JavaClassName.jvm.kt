/*
 * Copyright (C) 2014-2026 Forte Scarlet
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
import love.forte.codegentle.common.naming.toClassName
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

/**
 * Create a [ClassName] from [KClass].
 *
 * @see Class.toJavaClassName
 */
public fun KClass<*>.toJavaClassName(): ClassName = java.toJavaClassName()

/**
 * Create a [ClassName] from [Class].
 */
public fun Class<*>.toJavaClassName(): ClassName {
    when (this) {
        VOID_BOXED_CLASS -> return JavaClassNames.BOXED_VOID
        BOOLEAN_BOXED_CLASS -> return JavaClassNames.BOXED_BOOLEAN
        BYTE_BOXED_CLASS -> return JavaClassNames.BOXED_BYTE
        SHORT_BOXED_CLASS -> return JavaClassNames.BOXED_SHORT
        INT_BOXED_CLASS -> return JavaClassNames.BOXED_INT
        LONG_BOXED_CLASS -> return JavaClassNames.BOXED_LONG
        CHAR_BOXED_CLASS -> return JavaClassNames.BOXED_CHAR
        FLOAT_BOXED_CLASS -> return JavaClassNames.BOXED_FLOAT
        OBJECT_CLASS -> return JavaClassNames.OBJECT
        STRING_CLASS -> return JavaClassNames.STRING
    }

    return toClassName()
}

// javax.lang.model

@Deprecated("Use toClassName()", ReplaceWith("toClassName()"))
public fun TypeElement.toJavaClassName(): ClassName = toClassName()
