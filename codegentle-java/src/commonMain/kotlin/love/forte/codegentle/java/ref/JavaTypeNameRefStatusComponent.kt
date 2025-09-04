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
package love.forte.codegentle.java.ref

import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.*
import love.forte.codegentle.java.ref.internal.JavaTypeNameRefStatusComponentImpl

@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface JavaTypeNameRefStatusComponent {
    public val annotations: List<AnnotationRef>

    public interface Builder :
        TypeNameRefStatus.Builder<JavaTypeNameRefStatusComponent>,
        AnnotationRefCollector<Builder>

    public companion object Key :
        TypeNameRefStatus.Key<JavaTypeNameRefStatusComponent>,
        TypeNameRefStatus.BuilderKey<JavaTypeNameRefStatusComponent, Builder> {
        override val key: TypeNameRefStatus.Key<JavaTypeNameRefStatusComponent>
            get() = this

        override fun cast(value: Any): JavaTypeNameRefStatusComponent =
            value as JavaTypeNameRefStatusComponent

        override fun castBuilder(value: Any): Builder =
            value as Builder

        override fun builder(): Builder =
            JavaTypeNameRefStatusBuilder()

        override fun toString(): String = "JavaTypeNameRefStatusComponent.Key"
    }
}

/**
 * A builder for [JavaTypeNameRefStatusComponent].
 */
private class JavaTypeNameRefStatusBuilder : JavaTypeNameRefStatusComponent.Builder {
    private val annotations: MutableList<AnnotationRef> = mutableListOf()

    override fun addAnnotation(ref: AnnotationRef): JavaTypeNameRefStatusBuilder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): JavaTypeNameRefStatusBuilder = apply {
        annotations.addAll(refs)
    }

    override fun build(): JavaTypeNameRefStatusComponent {
        return JavaTypeNameRefStatusComponentImpl(
            annotations = annotations.toList()
        )
    }
}

public val
    <T : TypeName, B : TypeRefBuilder<T>>
    B.javaStatus: JavaTypeNameRefStatusComponent.Builder
    get() = status.configure(JavaTypeNameRefStatusComponent)

public inline fun
    <T : TypeName, B : TypeRefBuilder<T>>
    B.javaStatus(
    block: JavaTypeNameRefStatusComponent.Builder.() -> Unit
): B = apply {
    status.configure(JavaTypeNameRefStatusComponent, block)
}

public val TypeNameRefStatus.javaOrNull: JavaTypeNameRefStatusComponent?
    get() = this[JavaTypeNameRefStatusComponent]

public val TypeRef<*>.javaStatusOrNull: JavaTypeNameRefStatusComponent?
    get() = status.javaOrNull
