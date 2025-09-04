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
package love.forte.codegentle.java.naming

import love.forte.codegentle.common.naming.ClassName

public object JavaClassNames {

    internal const val JAVA_LANG_PACKAGE = "java.lang"

    public val OBJECT: ClassName = ClassName(JAVA_LANG_PACKAGE, "Object")

    public val STRING: ClassName = ClassName(JAVA_LANG_PACKAGE, "String")

    // primitives

    internal const val BOXED_VOID_SIMPLE_NAME = "Void"

    public val BOXED_VOID: ClassName = ClassName(JAVA_LANG_PACKAGE, BOXED_VOID_SIMPLE_NAME)

    internal const val BOXED_BOOLEAN_SIMPLE_NAME = "Boolean"

    public val BOXED_BOOLEAN: ClassName = ClassName(JAVA_LANG_PACKAGE, BOXED_BOOLEAN_SIMPLE_NAME)

    internal const val BOXED_BYTE_SIMPLE_NAME = "Byte"

    public val BOXED_BYTE: ClassName = ClassName(JAVA_LANG_PACKAGE, BOXED_BYTE_SIMPLE_NAME)

    internal const val BOXED_SHORT_SIMPLE_NAME = "Short"

    public val BOXED_SHORT: ClassName = ClassName(JAVA_LANG_PACKAGE, BOXED_SHORT_SIMPLE_NAME)

    internal const val BOXED_INT_SIMPLE_NAME = "Integer"

    public val BOXED_INT: ClassName = ClassName(JAVA_LANG_PACKAGE, BOXED_INT_SIMPLE_NAME)

    internal const val BOXED_LONG_SIMPLE_NAME = "Long"

    public val BOXED_LONG: ClassName = ClassName(JAVA_LANG_PACKAGE, BOXED_LONG_SIMPLE_NAME)

    internal const val BOXED_CHAR_SIMPLE_NAME = "Character"

    public val BOXED_CHAR: ClassName = ClassName(JAVA_LANG_PACKAGE, BOXED_CHAR_SIMPLE_NAME)

    internal const val BOXED_FLOAT_SIMPLE_NAME = "Float"

    public val BOXED_FLOAT: ClassName = ClassName(JAVA_LANG_PACKAGE, BOXED_FLOAT_SIMPLE_NAME)

    internal const val BOXED_DOUBLE_SIMPLE_NAME = "Double"

    public val BOXED_DOUBLE: ClassName = ClassName(JAVA_LANG_PACKAGE, BOXED_DOUBLE_SIMPLE_NAME)


}
