/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2015-2025 Forte Scarlet
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
// import love.forte.codegentle.common.codepoint.charCount
// import love.forte.codegentle.common.codepoint.codePointAt
// import love.forte.codegentle.java.codepoint.isJavaIdentifierPart
// import love.forte.codegentle.java.codepoint.isJavaIdentifierStart
// import love.forte.codegentle.java.internal.isSourceKeyword
// import kotlin.random.Random
// import kotlin.random.nextULong
//
// // TODO ? Keep?
//
// public class NameAllocator {
//     private val allocatedNames = linkedSetOf<String>()
//     private val tagToName = linkedMapOf<Any, String>()
//
//     /**
//      * Return a new name using `suggestion`
//      * that will not be a Java identifier or clash with other names.
//      */
//     public fun newName(suggestion: String): String {
//         return newName(suggestion, Random.nextULong().toString(16).padStart(16, '0'))
//     }
//
//     public fun newName(suggestion: String, tag: Any): String {
//         var toIdentifier = suggestion.toJavaIdentifier()
//         while (toIdentifier.isSourceKeyword() || !allocatedNames.add(toIdentifier)) {
//             toIdentifier += "_"
//         }
//
//         val replaced = tagToName.put(tag, toIdentifier)
//         if (replaced != null) {
//             tagToName[tag] = replaced
//             throw IllegalArgumentException("tag $tag can't be used for both '$replaced' and '$suggestion'")
//         }
//
//         return toIdentifier
//     }
//
//     /**
//      * Retrieve a name created with [newName].
//      */
//     public fun get(tag: Any): String {
//         return tagToName[tag] ?: throw IllegalArgumentException("Unknown tag: $tag")
//     }
//
//     public companion object {
//         private fun String.toJavaIdentifier(): String {
//             val l = length
//             var i = 0
//
//             val sb = StringBuilder()
//
//             while (i < l) {
//                 val cp = codePointAt(i)
//                 if (i == 0 && !cp.isJavaIdentifierStart() && cp.isJavaIdentifierPart()) {
//                     sb.append('_')
//                 }
//
//                 if (cp.isJavaIdentifierPart()) {
//                     sb.append(cp)
//                 } else {
//                     sb.append('_')
//                 }
//
//                 i += cp.charCount()
//             }
//
//             return sb.toString()
//         }
//     }
// }
