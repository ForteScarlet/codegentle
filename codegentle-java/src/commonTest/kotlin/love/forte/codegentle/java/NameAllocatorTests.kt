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
//
// import kotlin.test.Test
// import kotlin.test.assertEquals
// import kotlin.test.assertFailsWith
// import kotlin.test.assertTrue
//
// /**
//  * Tests for NameAllocator functionality.
//  */
// class NameAllocatorTests {
//
//     @Test
//     fun testNewNameWithSuggestion() {
//         val allocator = NameAllocator()
//         val name = allocator.newName("test")
//
//         println(name)
//
//         // The name should start with "test"
//         assertTrue(name.startsWith("test"))
//     }
//
//     @Test
//     fun testNewNameWithTag() {
//         val allocator = NameAllocator()
//         val name = allocator.newName("test", "tag1")
//
//         // The name should be retrievable by tag
//         assertEquals(name, allocator.get("tag1"))
//     }
//
//     @Test
//     fun testNewNameWithInvalidJavaIdentifier() {
//         val allocator = NameAllocator()
//         val name = allocator.newName("123test")
//
//         println(name)
//
//         // The name should be a valid Java identifier (shouldn't start with a number)
//         assertTrue(!name.startsWith("123"))
//         assertTrue(name.contains("test"))
//     }
//
//     @Test
//     fun testNewNameWithJavaKeyword() {
//         val allocator = NameAllocator()
//         val name = allocator.newName("class")
//
//         println(name)
//
//         // The name should not be a Java keyword
//         assertTrue(name != "class")
//         assertTrue(name.startsWith("class"))
//     }
//
//     @Test
//     fun testNewNameWithDuplicateSuggestion() {
//         val allocator = NameAllocator()
//         val name1 = allocator.newName("test")
//         val name2 = allocator.newName("test")
//
//         // The names should be different
//         assertTrue(name1 != name2)
//     }
//
//     @Test
//     fun testNewNameWithDuplicateTag() {
//         val allocator = NameAllocator()
//         allocator.newName("test1", "tag")
//
//         // Using the same tag for a different name should throw an exception
//         assertFailsWith<IllegalArgumentException> {
//             allocator.newName("test2", "tag")
//         }
//     }
//
//     @Test
//     fun testGetWithUnknownTag() {
//         val allocator = NameAllocator()
//
//         // Getting a name for an unknown tag should throw an exception
//         assertFailsWith<IllegalArgumentException> {
//             allocator.get("unknown")
//         }
//     }
//
//     @Test
//     fun testSpecialCharactersInName() {
//         val allocator = NameAllocator()
//         val name = allocator.newName("test@123")
//
//         // Special characters should be replaced with underscores
//         assertTrue(!name.contains("@"))
//         assertTrue(name.contains("_"))
//     }
// }
