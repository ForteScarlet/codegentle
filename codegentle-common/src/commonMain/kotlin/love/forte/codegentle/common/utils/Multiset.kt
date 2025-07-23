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
