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
package love.forte.codegentle.java

import love.forte.codegentle.common.code.addStatement
import love.forte.codegentle.common.code.emitString
import love.forte.codegentle.java.spec.JavaMethodSpec
import love.forte.codegentle.java.spec.JavaSimpleTypeSpec
import love.forte.codegentle.java.spec.JavaTypeSpec
import love.forte.codegentle.java.writer.writeToJavaString
import kotlin.test.Test
import kotlin.test.assertEquals

class HelloWorldClassTests {

    @Test
    fun testHelloWorldClass() {
        val spec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "HelloWorld") {
            modifiers.public()
            modifiers.final()

            addMethod(JavaMethodSpec("main") {
                modifiers {
                    public()
                    static()
                }

                addStatement("System.out.println(%V)") {
                    emitString("Hello, World!")
                }
            })
        }

        assertEquals(
            """
                public final class HelloWorld {
                    public static void main() {
                        System.out.println("Hello, World!");
                    }
                }
            """.trimIndent(),
            spec.writeToJavaString()
        )
    }

}
