package love.forte.codegentle.common

@InternalCommonCodeGentleApi
public actual inline fun <K, V> MutableMap<K, V>.computeValueIfAbsent(key: K, crossinline f: (K) -> V): V {
    return computeIfAbsent(key) { f(it) }
}

@InternalCommonCodeGentleApi
public actual inline fun <K, V> MutableMap<K, V>.computeValue(key: K, crossinline f: (K, V?) -> V?): V? {
    return compute(key) { k, v -> f(k, v) }
}
