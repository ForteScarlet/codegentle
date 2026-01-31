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

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.common.writer.InternalWriterApi
import love.forte.codegentle.kotlin.spec.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for KotlinScriptFileBuilder functionality.
 */
@OptIn(InternalWriterApi::class)
class KotlinScriptFileTests {

    @Test
    fun testBasicScriptWithOnlyCode() {
        val scriptFile = KotlinFile {
            addStatement("println(\"Hello, Script World!\")")
            addStatement("val x = 42")
            addStatement("println(\"Value: \$x\")")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                println("Hello, Script World!")
                val x = 42
                println("Value: ${"$"}x")
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithStatements() {
        val scriptFile = KotlinFile {
            addStatement("println(\"First statement\")")
            addStatement("val greeting = \"Hello\"")
            addStatement("println(greeting)")
        }

        val output = scriptFile.writeToKotlinString().trimEnd().trimEnd()

        assertEquals(
            """
                println("First statement")
                val greeting = "Hello"
                println(greeting)
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithCodeAndStatements() {
        val scriptFile = KotlinFile {
            addStatement("// Inline code")
            addStatement("val name = \"Script\"")
            addStatement("println(\"Hello, \$name!\")")
        }

        val output = scriptFile.writeToKotlinString().trimEnd().trimEnd()

        assertEquals(
            """
                // Inline code
                val name = "Script"
                println("Hello, ${"$"}name!")
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithFunction() {
        val function = KotlinFunctionSpec.builder("greet", ClassName("kotlin", "Unit").ref())
            .addParameter("name", ClassName("kotlin", "String").ref())
            .addStatement("println(\"Hello, \$name!\")")
            .build()

        val scriptFile = KotlinFile {
            addFunction(function)
            addStatement("greet(\"World\")")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                fun greet(name: String) {
                    println("Hello, ${"$"}name!")
                }

                greet("World")
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithProperty() {
        val property = KotlinPropertySpec.builder("greeting", ClassName("kotlin", "String").ref())
            .initializer("\"Hello, Script!\"")
            .build()

        val scriptFile = KotlinFile {
            addProperty(property)
            addStatement("println(greeting)")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                val greeting: String = "Hello, Script!"
                
                println(greeting)
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithClass() {
        val classSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "ScriptClass")
            .addFunction("sayHello") {
                addStatement("println(\"Hello from ScriptClass\")")
            }
            .build()

        val scriptFile = KotlinFile {
            addType(classSpec)
            addStatement("val instance = ScriptClass()")
            addStatement("instance.sayHello()")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                class ScriptClass {
                    fun sayHello() {
                        println("Hello from ScriptClass")
                    }
                }

                val instance = ScriptClass()
                instance.sayHello()
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithObject() {
        val objectSpec = KotlinObjectTypeSpec.builder("ScriptObject")
            .addFunction("process", ClassName("kotlin", "Unit").ref()) {
                addStatement("println(\"Processing...\")")
            }
            .build()

        val scriptFile = KotlinFile {
            addType(objectSpec)
            addStatement("ScriptObject.process()")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                object ScriptObject {
                    fun process() {
                        println("Processing...")
                    }
                }

                ScriptObject.process()
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithMixedContent() {
        val property = KotlinPropertySpec.builder("counter", ClassName("kotlin", "Int").ref())
            .mutable()
            .initializer("0")
            .build()

        val function = KotlinFunctionSpec.builder("increment", ClassName("kotlin", "Unit").ref())
            .addStatement("counter++")
            .build()

        val classSpec = KotlinSimpleTypeSpec.builder(KotlinTypeSpec.Kind.CLASS, "Counter")
            .addProperty("value", ClassName("kotlin", "Int").ref()) {
                mutable()
                initializer("0")
            }
            .addFunction("increment", ClassName("kotlin", "Unit").ref()) {
                addStatement("value++")
            }
            .build()

        val scriptFile = KotlinFile {
            addProperty(property)
            addFunction(function)
            addType(classSpec)
            addStatement("println(\"Initial counter: \$counter\")")
            addStatement("increment()")
            addStatement("println(\"After increment: \$counter\")")
            addStatement("")
            addStatement("val counterObj = Counter()")
            addStatement("println(\"Object counter: \${counterObj.value}\")")
            addStatement("counterObj.increment()")
            addStatement("println(\"After object increment: \${counterObj.value}\")")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                var counter: Int = 0

                class Counter {
                    var value: Int = 0

                    fun increment() {
                        value++
                    }
                }
                
                fun increment() {
                    counter++
                }

                println("Initial counter: ${"$"}counter")
                increment()
                println("After increment: ${"$"}counter")

                val counterObj = Counter()
                println("Object counter: ${"$"}{counterObj.value}")
                counterObj.increment()
                println("After object increment: ${"$"}{counterObj.value}")
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithFileComment() {
        val scriptFile = KotlinFile {
            addFileComment("This is a build script\n")
            addFileComment("Generated automatically")
            addStatement("")
            addStatement("println(\"Build script executing...\")")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                // This is a build script
                // Generated automatically
                
                println("Build script executing...")
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithStaticImports() {
        val scriptFile = KotlinFile {
            addStaticImport("kotlin.math.*")
            addStatement("val result = sqrt(16.0)")
            addStatement("println(\"Square root: \$result\")")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                import kotlin.math.*
                
                val result = sqrt(16.0)
                println("Square root: ${"$"}result")
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithCustomIndent() {
        val scriptFile = KotlinFile {
            indent("  ") // 2 spaces instead of 4
            addStatement("if (true) {")
            addStatement("println(\"Indented with 2 spaces\")")
            addStatement("}")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                if (true) {
                println("Indented with 2 spaces")
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithControlFlow() {
        val scriptFile = KotlinFile {
            addStatement("val items = listOf(\"apple\", \"banana\", \"cherry\")")
            addStatement("")
            addStatement("for (item in items) {")
            addStatement("    println(\"Item: \$item\")")
            addStatement("}")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                val items = listOf("apple", "banana", "cherry")

                for (item in items) {
                    println("Item: ${"$"}item")
                }
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testScriptWithMultilineCode() {
        val scriptFile = KotlinFile {
            addCode(
                """
                fun multilineFunction() {
                    val x = 1
                    val y = 2
                    println("Sum: ${'$'}{x + y}")
                }
                
                """.trimIndent()
            )
            addStatement("multilineFunction()")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                fun multilineFunction() {
                    val x = 1
                    val y = 2
                    println("Sum: ${"$"}{x + y}")
                }
                multilineFunction()
            """.trimIndent(),
            output
        )
    }

    @Test
    fun testEmptyScriptFailure() {
        assertFailsWith<IllegalStateException> {
            KotlinFile {
                // No content added
            }
        }
    }

    @Test
    fun testScriptWithOnlyEmptyCodeDoesNotFail() {
        // This should not fail because we're adding some code, even if it's just empty
        val scriptFile = KotlinFile {
            addStatement("")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()
        assertEquals("", output)
    }

    @Test
    fun testScriptFileRelativePath() {
        val scriptFile = KotlinFile {
            addStatement("println(\"Hello\")")
        }

        val relativePath = scriptFile.toRelativePath(filename = "build", isScript = true)
        assertEquals("build.kts", relativePath)
    }

    @Test
    fun testScriptFileRelativePathWithExistingExtension() {
        val scriptFile = KotlinFile {
            addStatement("println(\"Hello\")")
        }

        val relativePath = scriptFile.toRelativePath(filename = "build.gradle.kts", isScript = true)
        assertEquals("build.gradle.kts", relativePath)
    }

    @Test
    fun testBuildGradleKtsExample() {
        val scriptFile = KotlinFile {
            addStatement("plugins {")
            addStatement("    kotlin(\"jvm\") version \"1.9.10\"")
            addStatement("}")
            addStatement("")
            addStatement("repositories {")
            addStatement("    mavenCentral()")
            addStatement("}")
            addStatement("")
            addStatement("dependencies {")
            addStatement("    implementation(\"org.jetbrains.kotlin:kotlin-stdlib\")")
            addStatement("    testImplementation(\"org.junit.jupiter:junit-jupiter:5.8.2\")")
            addStatement("}")
            addStatement("")
            addStatement("tasks.test {")
            addStatement("    useJUnitPlatform()")
            addStatement("}")
        }

        val output = scriptFile.writeToKotlinString().trimEnd()

        assertEquals(
            """
                plugins {
                    kotlin("jvm") version "1.9.10"
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-stdlib")
                    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
                }

                tasks.test {
                    useJUnitPlatform()
                }
            """.trimIndent(),
            output
        )
    }
}
