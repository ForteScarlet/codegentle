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
package love.forte.codegentle.java

import love.forte.codegentle.common.code.*
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.java.strategy.ToStringJavaWriteStrategy
import love.forte.codegentle.java.strategy.WrapperJavaWriteStrategy
import love.forte.codegentle.java.writer.writeToJavaString
import kotlin.test.Test
import kotlin.test.assertEquals


class OtherCodeValueTest {

    private val javaLongToStringStrategy = object : WrapperJavaWriteStrategy(
        ToStringJavaWriteStrategy
    ) {
        override fun omitJavaLangPackage(): Boolean = false
    }

    @Test
    fun testCodeValueFormat() {
        with(
            CodeValue("%V%V = %V%V;") {
                emitType(ClassName("java.lang", "String"))
                emitLiteral(" name")
                emitLiteral("")
                emitString("Hello World")
            }
        ) {
            assertEquals(parts.size, 6)

            assertEquals(CodePart.type(ClassName("java.lang", "String")), parts[0])
            assertEquals(CodePart.literal(" name"), parts[1])
            assertEquals(CodePart.simple(" = "), parts[2])
            assertEquals(CodePart.literal(""), parts[3])
            assertEquals(CodePart.string("Hello World"), parts[4])
            assertEquals(CodePart.simple(";"), parts[5])
            assertEquals("String name = \"Hello World\";", writeToJavaString())
            assertEquals(
                "java.lang.String name = \"Hello World\";", writeToJavaString(
                    javaLongToStringStrategy
                )
            )
        }

        with(
            CodeValue("%V%V = %V%V") {
                emitType(ClassName("java.lang", "String"))
                emitLiteral(" name")
                emitLiteral("")
                emitString("Hello World")
            }
        ) {
            assertEquals(parts.size, 5)

            assertEquals(CodePart.type(ClassName("java.lang", "String")), parts[0])
            assertEquals(CodePart.literal(" name"), parts[1])
            assertEquals(CodePart.simple(" = "), parts[2])
            assertEquals(CodePart.literal(""), parts[3])
            assertEquals(CodePart.string("Hello World"), parts[4])
            assertEquals("String name = \"Hello World\"", writeToJavaString())
            assertEquals("java.lang.String name = \"Hello World\"", writeToJavaString(javaLongToStringStrategy))
        }

        with(
            CodeValue("%V %V = %V;") {
                emitType(ClassName("java.lang", "String"))
                emitLiteral("name")
                emitString("Hello World")
            }
        ) {
            assertEquals(parts.size, 6)

            assertEquals(CodePart.type(ClassName("java.lang", "String")), parts[0])
            assertEquals(CodePart.simple(" "), parts[1])
            assertEquals(CodePart.literal("name"), parts[2])
            assertEquals(CodePart.simple(" = "), parts[3])
            assertEquals(CodePart.string("Hello World"), parts[4])
            assertEquals(CodePart.simple(";"), parts[5])
            assertEquals("String name = \"Hello World\";", writeToJavaString())
            assertEquals(
                "java.lang.String name = \"Hello World\";",
                writeToJavaString(javaLongToStringStrategy)
            )
        }

        with(
            CodeValue("%V %V = %V") {
                emitType(ClassName("java.lang", "String"))
                emitLiteral("name")
                emitString("Hello World")
            }
        ) {
            assertEquals(parts.size, 5)

            assertEquals(CodePart.type(ClassName("java.lang", "String")), parts[0])
            assertEquals(CodePart.simple(" "), parts[1])
            assertEquals(CodePart.literal("name"), parts[2])
            assertEquals(CodePart.simple(" = "), parts[3])
            assertEquals(CodePart.string("Hello World"), parts[4])
            assertEquals("String name = \"Hello World\"", writeToJavaString())
            assertEquals(
                "java.lang.String name = \"Hello World\"",
                writeToJavaString(javaLongToStringStrategy)
            )
        }

        with(
            CodeValue("String %V = %V;") {
                emitLiteral("name")
                emitString("Hello World")
            }
        ) {
            assertEquals(parts.size, 5)

            assertEquals(CodePart.simple("String "), parts[0])
            assertEquals(CodePart.literal("name"), parts[1])
            assertEquals(CodePart.simple(" = "), parts[2])
            assertEquals(CodePart.string("Hello World"), parts[3])
            assertEquals(CodePart.simple(";"), parts[4])
            assertEquals("String name = \"Hello World\";", writeToJavaString())
        }

        with(
            CodeValue("String %V = %V") {
                emitLiteral("name")
                emitString("Hello World")
            }
        ) {
            assertEquals(parts.size, 4)

            assertEquals(CodePart.simple("String "), parts[0])
            assertEquals(CodePart.literal("name"), parts[1])
            assertEquals(CodePart.simple(" = "), parts[2])
            assertEquals(CodePart.string("Hello World"), parts[3])
            assertEquals("String name = \"Hello World\"", writeToJavaString())
        }


    }


}
