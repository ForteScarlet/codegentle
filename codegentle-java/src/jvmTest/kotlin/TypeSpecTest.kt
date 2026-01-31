/*
 * Copyright (C) 2025-2026 Forte Scarlet
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
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.modifiers
import love.forte.codegentle.java.naming.JavaPrimitiveTypeNames
import love.forte.codegentle.java.spec.JavaFieldSpec
import love.forte.codegentle.java.spec.JavaMethodSpec
import love.forte.codegentle.java.spec.JavaSimpleTypeSpec
import love.forte.codegentle.java.spec.JavaTypeSpec
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class TypeSpecTest {

    @Test
    fun testPrintTypeSpec() {
        val simpleType = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MyClass") {
            addModifier(JavaModifier.PUBLIC)

            addMethod(JavaMethodSpec("methodPub") {
                modifiers { public() }
            })

            addField(JavaFieldSpec(JavaPrimitiveTypeNames.INT.ref(), "valuePub") {
                modifiers {
                    public()
                    static()
                    final()
                }
                initializer("1")
            })

            addField(JavaFieldSpec(JavaPrimitiveTypeNames.INT.ref(), "valuePri") {
                modifiers {
                    private()
                    static()
                    final()
                }
                initializer("2")
            })
        }

        println(simpleType)

        assertEquals(
            """
                public class MyClass {
                    public static final int valuePub = 1;

                    private static final int valuePri = 2;

                    public void methodPub() {
                    }
                }
            """.trimIndent(),
            simpleType.toString()
        )

    }

}
