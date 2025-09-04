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
package love.forte.codegentle.kotlin.ref

import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.*
import love.forte.codegentle.kotlin.ref.internal.KotlinAnnotationRefStatusImpl

/**
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface KotlinAnnotationRefStatus : AnnotationRefStatus {
    public val useSite: KotlinAnnotationUseSite?

    public companion object :
        AnnotationRefStatusBuilderFactory<KotlinAnnotationRefStatus, KotlinAnnotationRefStatusBuilder> {
        override fun createBuilder(): KotlinAnnotationRefStatusBuilder = KotlinAnnotationRefStatusBuilder()
    }
}

public class KotlinAnnotationRefStatusBuilder @PublishedApi internal constructor() :
    AnnotationRefStatusBuilder<KotlinAnnotationRefStatus> {
    public var useSite: KotlinAnnotationUseSite? = null

    override fun build(): KotlinAnnotationRefStatus {
        return KotlinAnnotationRefStatusImpl(useSite)
    }
}

public typealias KotlinAnnotationRefBuilder =
    AnnotationRefBuilder<KotlinAnnotationRefStatus, KotlinAnnotationRefStatusBuilder>

public typealias KotlinAnnotationRefBuilderDsl =
    AnnotationRefBuilder<KotlinAnnotationRefStatus, KotlinAnnotationRefStatusBuilder>.() -> Unit

public inline fun ClassName.kotlinAnnotationRef(
    block: KotlinAnnotationRefBuilderDsl = {}
): AnnotationRef = annotationRef(KotlinAnnotationRefStatus, block)

public val AnnotationRefStatus.kotlinOrNull: KotlinAnnotationRefStatus?
    get() = this as? KotlinAnnotationRefStatus

public val AnnotationRef.kotlinStatusOrNull: KotlinAnnotationRefStatus?
    get() = status as? KotlinAnnotationRefStatus

// AnnotationRefCollector

public inline fun <B : AnnotationRefCollector<B>> B.addKotlinAnnotation(
    className: ClassName,
    block: KotlinAnnotationRefBuilderDsl = {}
): B = addAnnotation(className.kotlinAnnotationRef(block))
