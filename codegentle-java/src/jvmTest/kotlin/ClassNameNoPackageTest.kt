/*
 * Copyright (C) 2019-2025 Forte Scarlet
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
import love.forte.codegentle.common.naming.isEmpty
import love.forte.codegentle.java.naming.toJavaClassName
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals


class ClassNameNoPackageTest {

    @Test
    fun shouldSupportClassInDefaultPackage() {
        val className = ClassNameNoPackageTest::class.toJavaClassName()
        assertTrue(className.packageName.isEmpty())
        assertEquals("ClassNameNoPackageTest", className.simpleName)
        assertEquals("ClassNameNoPackageTest", className.toString())
    }

}
