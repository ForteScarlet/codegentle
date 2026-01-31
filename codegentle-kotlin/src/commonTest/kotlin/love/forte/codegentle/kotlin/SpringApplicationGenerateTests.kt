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
package love.forte.codegentle.kotlin

import love.forte.codegentle.common.code.addCode
import love.forte.codegentle.common.code.addDoc
import love.forte.codegentle.common.code.emitLiteral
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.naming.ArrayTypeName
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.MemberName
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.common.ref.addAnnotation
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.kotlin.naming.KotlinClassNames
import love.forte.codegentle.kotlin.spec.addMainFunction
import love.forte.codegentle.kotlin.spec.addParameter
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class SpringApplicationGenerateTests {
    private val springBootPkg = "org.springframework.boot".parseToPackageName()
    private val springBootApplicationAno = ClassName(springBootPkg, "SpringBootApplication")


    @Test
    fun testGenerateSpringApplicationMain() {
        val projectPackage = "com.example.spring.application".parseToPackageName()
        val name = "ExampleApplication"

        val mainFile = KotlinFile(projectPackage) {
            addSimpleClassType(name) {
                addAnnotation(springBootApplicationAno)
                addDoc("Spring程序的入口注解类。添加 [%V] 注解来标记启动类。") {
                    emitType(springBootApplicationAno)
                }
            }

            addMainFunction {
                addParameter("args", ArrayTypeName(KotlinClassNames.STRING.ref()))
                addCode("%V<%V>(*args)") {
                    emitType(MemberName("org.springframework.boot", "runApplication"))
                    emitLiteral(name)
                }
            }
        }

        assertEquals(
            """
                |package com.example.spring.application
                |
                |import org.springframework.boot.SpringBootApplication
                |import org.springframework.boot.runApplication
                |
                |/**
                | * Spring程序的入口注解类。添加 [SpringBootApplication] 注解来标记启动类。
                | */
                |@SpringBootApplication
                |class ExampleApplication
                |
                |fun main(args: Array<String>) {
                |    runApplication<ExampleApplication>(*args)
                |}
            """.trimMargin(),
            mainFile.writeToKotlinString()
        )
    }

}
