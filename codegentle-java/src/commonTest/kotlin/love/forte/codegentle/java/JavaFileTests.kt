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

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.naming.parseToPackageName
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.java.spec.JavaFieldSpec
import love.forte.codegentle.java.spec.JavaSimpleTypeSpec
import love.forte.codegentle.java.spec.JavaTypeSpec
import love.forte.codegentle.java.writer.writeToJavaString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for JavaFile functionality.
 */
class JavaFileTests {

    @Test
    fun testBasicJavaFile() {
        val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MyClass")
        val packageName = "com.example".parseToPackageName()

        val javaFile = JavaFile(packageName, typeSpec)

        val output = javaFile.writeToJavaString()

        assertEquals(
            """
            package com.example;
            
            class MyClass {
            }
        """.trimIndent(), output)
    }

    @Test
    fun testJavaFileWithComment() {
        val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MyClass")
        val packageName = "com.example".parseToPackageName()

        val javaFile = JavaFile(packageName, typeSpec) {
            addFileComment("This is a test file.")
        }

        val output = javaFile.writeToJavaString()

        assertEquals(
            """
            // This is a test file.
            package com.example;
            
            class MyClass {
            }
        """.trimIndent(), output)
    }

    @Test
    fun testJavaFileWithStaticImports() {
        val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MyClass")
        val packageName = "com.example".parseToPackageName()

        val javaFile = JavaFile(packageName, typeSpec) {
            addStaticImport(ClassName("java.util", "Collections"), "emptyList")
            addStaticImport(ClassName("java.util", "Collections"), "emptyMap")
        }

        val output = javaFile.writeToJavaString()

        assertEquals(
            """
            package com.example;

            import static java.util.Collections.emptyList;
            import static java.util.Collections.emptyMap;
            
            class MyClass {
            }
        """.trimIndent(), output
        )
    }

    @Test
    fun testJavaFileWithSkipJavaLangImports() {
        val field = JavaFieldSpec(ClassName("java.lang", "String").ref(), "name")
        val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MyClass") {
            addField(field)
        }
        val packageName = "com.example".parseToPackageName()

        val javaFile = JavaFile(packageName, typeSpec) {
            skipJavaLangImports(true)
        }

        val output = javaFile.writeToJavaString()

        // Should not contain import for java.lang.String
        assertTrue(!output.contains("import java.lang.String;"))
    }

    @Test
    fun testJavaFileWithCustomIndent() {
        val typeSpec = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MyClass")
        val packageName = "com.example".parseToPackageName()

        val javaFile = JavaFile(packageName, typeSpec) {
            indent("\t") // Use tab for indentation
        }

        val output = javaFile.writeToJavaString()

        assertEquals(
            """
            package com.example;
            
            class MyClass {
            }
        """.trimIndent(), output)
    }

    @Test
    fun testJavaFileWithSecondaryTypes() {
        val mainType = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MainClass") {
            addModifier(JavaModifier.PUBLIC)
        }
        val secondaryType1 = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "HelperClass")
        val secondaryType2 = JavaSimpleTypeSpec(JavaTypeSpec.Kind.INTERFACE, "UtilInterface")
        val packageName = "com.example".parseToPackageName()

        val javaFile = JavaFile(packageName, mainType) {
            addSecondaryType(secondaryType1)
            addSecondaryType(secondaryType2)
        }

        val output = javaFile.writeToJavaString()

        assertEquals(
            """
            package com.example;
            
            public class MainClass {
            }
            
            class HelperClass {
            }
            
            interface UtilInterface {
            }
        """.trimIndent(), output)
    }

    @Test
    fun testJavaFileWithSecondaryTypesVarargs() {
        val mainType = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MainClass") {
            addModifier(JavaModifier.PUBLIC)
        }
        val secondaryType1 = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "HelperClass")
        val secondaryType2 = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "Status")
        val packageName = "com.example".parseToPackageName()

        val javaFile = JavaFile(packageName, mainType) {
            addSecondaryTypes(secondaryType1, secondaryType2)
        }

        val output = javaFile.writeToJavaString()

        assertEquals(
            """
            package com.example;
            
            public class MainClass {
            }
            
            class HelperClass {
            }
            
            class Status {
            }
        """.trimIndent(), output)
    }

    @Test
    fun testJavaFileWithSecondaryTypesIterable() {
        val mainType = JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "MainClass") {
            addModifier(JavaModifier.PUBLIC)
        }
        val secondaryTypes = listOf(
            JavaSimpleTypeSpec(JavaTypeSpec.Kind.CLASS, "HelperClass"),
            JavaSimpleTypeSpec(JavaTypeSpec.Kind.INTERFACE, "UtilInterface")
        )
        val packageName = "com.example".parseToPackageName()

        val javaFile = JavaFile(packageName, mainType) {
            addSecondaryTypes(secondaryTypes)
        }

        val output = javaFile.writeToJavaString()

        assertEquals(
            """
            package com.example;
            
            public class MainClass {
            }
            
            class HelperClass {
            }
            
            interface UtilInterface {
            }
        """.trimIndent(), output)
    }
}
