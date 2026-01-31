/*
 * Copyright (C) 2015-2026 Forte Scarlet
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
package love.forte.codegentle.java

import love.forte.codegentle.java.naming.JavaPrimitiveTypeNames
import love.forte.codegentle.java.spec.JavaFieldSpec
import love.forte.codegentle.java.writer.writeToJavaString
import kotlin.test.Test
import kotlin.test.assertEquals


class FieldSpecTest {
    @Test
    fun equalsAndHashCode() {
        var a = JavaFieldSpec(JavaPrimitiveTypeNames.INT, "foo")
        var b = JavaFieldSpec(JavaPrimitiveTypeNames.INT, "foo")
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(a.writeToJavaString(), b.writeToJavaString())
        a = JavaFieldSpec(
            JavaPrimitiveTypeNames.INT,
            "FOO",
        ) {
            addModifiers(JavaModifier.PUBLIC, JavaModifier.STATIC)
        }

        b = JavaFieldSpec(
            JavaPrimitiveTypeNames.INT,
            "FOO",
        ) {
            addModifiers(JavaModifier.PUBLIC, JavaModifier.STATIC)
        }

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertEquals(a.writeToJavaString(), b.writeToJavaString())
    }
}
