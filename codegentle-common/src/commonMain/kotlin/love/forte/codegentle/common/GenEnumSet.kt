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
package love.forte.codegentle.common

/**
 * Mark an `enum class` to generate a `Set<TheEnum>` implementation
 */
@Retention(AnnotationRetention.SOURCE)
public annotation class GenEnumSet(
    val internal: Boolean = false,
    val mutableName: String = "",
    val immutableName: String = "",
    val containerName: String = "",
    val containerSingleAdder: String = "",
    val containerMultiAdder: String = "",
    val operatorsName: String = "",
) {

    /**
     * A set of mutually exclusive enum elements.
     * Within this group, setting one element will cancel the settings of other elements in the group.
     */
    public annotation class Group(val name: String)
}
