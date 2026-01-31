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
package love.forte.codegentle.common.ref

/**
 * A reference status for annotations.
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface AnnotationRefStatus {
    // TODO 也使用 TypeRefStatus 的 Component 的方案
}

// Builders

@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface AnnotationRefStatusBuilder<out T : AnnotationRefStatus> {
    public fun build(): T
}

@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface AnnotationRefStatusBuilderFactory<out T : AnnotationRefStatus, out B : AnnotationRefStatusBuilder<T>> {
    public fun createBuilder(): B
}
