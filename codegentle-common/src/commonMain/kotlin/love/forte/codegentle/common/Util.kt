package love.forte.codegentle.common

@InternalCommonCodeGentleApi
public expect fun <K, V> MutableMap<K, V>.computeValue(key: K, f: (K, V?) -> V?): V?

@InternalCommonCodeGentleApi
public expect fun <K, V> MutableMap<K, V>.computeValueIfAbsent(key: K, f: (K) -> V): V

@InternalCommonCodeGentleApi
public fun String?.literalWithDoubleQuotes(indent: String): String {
    if (this == null) return "null"
    val result = StringBuilder(this.length + 2)
    result.append('"')
    for (c in this) {
        when (c) {
            '"' -> result.append("\\\"")
            '\\' -> result.append("\\\\")
            '\n' -> result.append("\\n\"\n$indent$indent+ \"")
            '\r' -> result.append("\\r")
            '\t' -> result.append("\\t")
            else -> result.append(c)
        }
    }
    result.append('"')
    return result.toString()
}
