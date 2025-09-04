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

@InternalCommonCodeGentleApi
public actual inline fun <K, V> MutableMap<K, V>.computeValueIfAbsent(key: K, crossinline f: (K) -> V): V {
    return computeIfAbsent(key) { f(it) }
}

@InternalCommonCodeGentleApi
public actual inline fun <K, V> MutableMap<K, V>.computeValue(key: K, crossinline f: (K, V?) -> V?): V? {
    return compute(key) { k, v -> f(k, v) }
}
