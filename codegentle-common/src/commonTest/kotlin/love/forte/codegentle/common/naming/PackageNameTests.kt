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
package love.forte.codegentle.common.naming

import kotlin.test.*

/**
 *
 * @author ForteScarlet
 */
class PackageNameTests {
    private fun PackageName.assertNameAndToString(name: String, path: String) {
        assertEquals(name, this.name)
        assertEquals(path, this.toString())
    }

    @Test
    fun testEmptyPackageName() {
        assertSame(PackageName.EMPTY, PackageName())
        assertSame(PackageName.EMPTY, PackageName(""))
        assertSame(PackageName.EMPTY, PackageName(null, ""))
        assertSame(PackageName.EMPTY, PackageName(emptyList()))
        assertSame(PackageName(), PackageName())
        assertSame(PackageName(), PackageName(""))
        assertSame(PackageName(), PackageName(null, ""))
        assertSame(PackageName(), PackageName(emptyList()))
    }

    @Test
    fun testRootPackageName() {
        PackageName("love").assertNameAndToString("love", "love")
        PackageName(null, "love").assertNameAndToString("love", "love")
    }

    @Test
    fun testMultiLevelPackageName() {
        val loveForteCodegentle = PackageName(listOf("love", "forte", "codegentle"))

        loveForteCodegentle.assertNameAndToString("codegentle", "love.forte.codegentle")

        val loveForte = PackageName(listOf("love", "forte"))

        loveForte.assertNameAndToString("forte", "love.forte")

        val loveForteCodegentle2 = PackageName(loveForte, "codegentle")

        loveForteCodegentle2.assertNameAndToString("codegentle", "love.forte.codegentle")

        assertEquals(loveForteCodegentle, loveForteCodegentle2)
        assertEquals(loveForte, loveForteCodegentle.previous)
        assertEquals(loveForte, loveForteCodegentle2.previous)
    }

    @Test
    fun testPackageNameTop() {
        PackageName.EMPTY.top().assertNameAndToString("", "")
        PackageName("love").top().assertNameAndToString("love", "love")
        PackageName(listOf("love", "forte")).top().assertNameAndToString("love", "love")
        PackageName(listOf("love", "forte", "codeGentle")).top().assertNameAndToString("love", "love")
    }

    @Test
    fun testPackageNameParse() {
        "love.forte.codegentle".parseToPackageName().assertNameAndToString("codegentle", "love.forte.codegentle")
        "love.forte".parseToPackageName().assertNameAndToString("forte", "love.forte")

        assertEquals(
            PackageName(listOf("love")),
            "love".parseToPackageName()
        )

        assertEquals(
            PackageName(listOf("love", "forte")),
            "love.forte".parseToPackageName()
        )

        assertEquals(
            PackageName(listOf("love", "forte", "codegentle")),
            "love.forte.codegentle".parseToPackageName()
        )

        assertEquals(
            PackageName("love"),
            "love".parseToPackageName()
        )

        assertEquals(
            PackageName(),
            "".parseToPackageName()
        )

        assertEquals(
            PackageName(""),
            "".parseToPackageName()
        )
    }

    @Test
    fun testPackageNamePlus() {
        (PackageName() + "love").assertNameAndToString("love", "love")
        ((PackageName() + "love") + "forte").assertNameAndToString("forte", "love.forte")
        (PackageName("love") + "forte").assertNameAndToString("forte", "love.forte")

        assertSame(PackageName.EMPTY, PackageName.EMPTY + PackageName.EMPTY)

        val p1 = PackageName(listOf("love", "forte"))
        assertSame(p1, PackageName.EMPTY + p1)
        assertSame(p1, p1 + PackageName.EMPTY)

        (PackageName(listOf("love", "forte")) + PackageName("codegentle"))
            .assertNameAndToString("codegentle", "love.forte.codegentle")

        (PackageName(listOf("love", "forte")) + "codegentle.naming".parseToPackageName())
            .assertNameAndToString("naming", "love.forte.codegentle.naming")
    }

    @Test
    fun testPackageNameNames() {
        "love.forte.codegentle".parseToPackageName()
            .apply {
                assertContentEquals(listOf("love", "forte", "codegentle"), nameSequence().map { it.name }.toList())
            }
        "love".parseToPackageName()
            .apply {
                assertContentEquals(listOf("love"), nameSequence().map { it.name }.toList())
            }
        "".parseToPackageName()
            .apply {
                assertContentEquals(emptyList(), nameSequence().map { it.name }.toList())
            }

        assertContentEquals(emptyList(), PackageName.EMPTY.nameSequence().map { it.name }.toList())
    }

    @Test
    fun testPackageParts() {
        with("love.forte.codegentle".parseToPackageName().parts) {
            assertEquals(3, size)
            assertContentEquals(listOf("love", "forte", "codegentle"), this)
        }

        with("love".parseToPackageName().parts) {
            assertEquals(1, size)
            assertContentEquals(listOf("love"), this)
        }

        with("".parseToPackageName().parts) {
            assertTrue(isEmpty())
        }

        with(PackageName().parts) {
            assertTrue(isEmpty())
        }
    }

}
