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
package love.forte.codegentle.common.utils

import love.forte.codegentle.common.computeValue

@RequiresOptIn("This api is internal. It may be changed in the future.")
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
public annotation class InternalMultisetApi

@InternalMultisetApi
public class Multiset<T> {
    private val map = linkedMapOf<T, Int>()

    public fun add(t: T) {
        map.computeValue(t) { _, old ->
            old?.plus(1) ?: 1
        }
    }

    public fun remove(t: T) {
        map.computeValue(t) { _, old ->
            // 如果-1后小于等于0，移除，否则保存计算结果
            old?.minus(1)?.takeIf { value -> value > 0 }
        }
    }

    public operator fun contains(t: T): Boolean {
        return (map[t] ?: 0) > 0
    }
}
