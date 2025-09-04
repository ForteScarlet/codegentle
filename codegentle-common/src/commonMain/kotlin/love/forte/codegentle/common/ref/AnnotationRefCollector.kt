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
package love.forte.codegentle.common.ref

import love.forte.codegentle.common.CodeGentleBuilderExtensionImplementation
import love.forte.codegentle.common.naming.ClassName
import kotlin.jvm.JvmInline

/**
 *
 *
 * ```Kotlin
 * builder {
 *    addAnnotation(...)
 * }
 * ```
 *
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleBuilderExtensionImplementation::class)
public interface AnnotationRefCollector<B : AnnotationRefCollector<B>> {
    public fun addAnnotation(ref: AnnotationRef): B

    public fun addAnnotations(refs: Iterable<AnnotationRef>): B

    public fun addAnnotations(vararg refs: AnnotationRef): B =
        addAnnotations(refs.asList())
}

public inline fun <S : AnnotationRefStatus,
    SB : AnnotationRefStatusBuilder<S>,
    B : AnnotationRefCollector<B>> B.addAnnotation(
    className: ClassName,
    factory: AnnotationRefStatusBuilderFactory<S, SB>,
    block: AnnotationRefBuilderDsl<S, SB> = {}
): B = addAnnotation(className.annotationRef(factory, block))

public inline fun <B : AnnotationRefCollector<B>> B.addAnnotation(
    className: ClassName,
    block: BasicAnnotationRefBuilderDsl = {}
): B = addAnnotation(className.annotationRef(block))

/**
 * Some operations for [AnnotationRefCollector].
 */
@JvmInline
public value class AnnotationRefCollectorOps<B : AnnotationRefCollector<B>>
@PublishedApi
internal constructor(public val collector: B) {
    public fun add(ref: AnnotationRef): B = collector.addAnnotation(ref)
    public fun addAll(refs: Iterable<AnnotationRef>): B = collector.addAnnotations(refs)
    public fun addAll(vararg refs: AnnotationRef): B = collector.addAnnotations(refs.asList())
}

public inline val <B : AnnotationRefCollector<B>> B.annotations: AnnotationRefCollectorOps<B>
    get() = AnnotationRefCollectorOps(this)

/**
 * DSL block with [AnnotationRefCollectorOps].
 */
public inline fun <B : AnnotationRefCollector<B>> B.annotations(
    block: AnnotationRefCollectorOps<B>.() -> Unit
) {
    annotations.block()
}
