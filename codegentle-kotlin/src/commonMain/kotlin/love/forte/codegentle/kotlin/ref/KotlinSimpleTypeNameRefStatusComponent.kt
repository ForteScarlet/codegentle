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

import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.ref.*
import love.forte.codegentle.kotlin.InternalKotlinCodeGentleApi
import love.forte.codegentle.kotlin.ref.internal.KotlinSimpleTypeNameRefStatusComponentImpl
import love.forte.codegentle.kotlin.ref.internal.KotlinVariableNameRefStatusComponentImpl

@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface KotlinSimpleTypeNameRefStatusComponent {
    public val annotations: List<AnnotationRef>
    public val nullable: Boolean

    public interface Builder :
        AnnotationRefCollector<Builder>,
        TypeNameRefStatus.Builder<KotlinSimpleTypeNameRefStatusComponent> {
        public var nullable: Boolean
    }

    public companion object Key :
        TypeNameRefStatus.Key<KotlinSimpleTypeNameRefStatusComponent>,
        TypeNameRefStatus.BuilderKey<KotlinSimpleTypeNameRefStatusComponent, Builder> {

        override fun cast(value: Any): KotlinSimpleTypeNameRefStatusComponent =
            value as KotlinSimpleTypeNameRefStatusComponent

        override fun castBuilder(value: Any): Builder =
            value as Builder

        override val key: TypeNameRefStatus.Key<KotlinSimpleTypeNameRefStatusComponent>
            get() = this

        override fun builder(): Builder =
            KotlinTypeNameRefStatusComponentBuilder()

        override fun toString(): String = "KotlinSimpleTypeNameRefStatusComponent.Key"
    }
}

@SubclassOptInRequired(CodeGentleRefImplementation::class)
public interface KotlinVariableNameRefStatusComponent {
    public val reified: Boolean

    public interface Builder : TypeNameRefStatus.Builder<KotlinVariableNameRefStatusComponent> {
        public var reified: Boolean

    }

    public companion object Key :
        TypeNameRefStatus.Key<KotlinVariableNameRefStatusComponent>,
        TypeNameRefStatus.BuilderKey<KotlinVariableNameRefStatusComponent, Builder> {
        override val key: TypeNameRefStatus.Key<KotlinVariableNameRefStatusComponent>
            get() = this

        override fun cast(value: Any): KotlinVariableNameRefStatusComponent =
            value as KotlinVariableNameRefStatusComponent

        override fun castBuilder(value: Any): Builder =
            value as Builder

        override fun builder(): Builder =
            KotlinVariableNameRefStatusComponentBuilder()

        override fun toString(): String = "KotlinVariableNameRefStatusComponent.Key"
    }
}

/**
 * A builder for [KotlinSimpleTypeNameRefStatusComponent].
 */
private class KotlinTypeNameRefStatusComponentBuilder : KotlinSimpleTypeNameRefStatusComponent.Builder {
    override var nullable: Boolean = false
    private val annotations: MutableList<AnnotationRef> = mutableListOf()

    override fun addAnnotation(ref: AnnotationRef): KotlinTypeNameRefStatusComponentBuilder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): KotlinTypeNameRefStatusComponentBuilder = apply {
        annotations.addAll(refs)
    }

    @OptIn(InternalKotlinCodeGentleApi::class)
    override fun build(): KotlinSimpleTypeNameRefStatusComponent {
        return KotlinSimpleTypeNameRefStatusComponentImpl(
            annotations = annotations.toList(),
            nullable = nullable
        )
    }
}

private class KotlinVariableNameRefStatusComponentBuilder : KotlinVariableNameRefStatusComponent.Builder {
    override var reified: Boolean = false
    override fun build(): KotlinVariableNameRefStatusComponent {
        return KotlinVariableNameRefStatusComponentImpl(reified)
    }
}

public val
    <T : TypeName, B : TypeRefBuilder<T>>
    B.kotlinStatus: KotlinSimpleTypeNameRefStatusComponent.Builder
    get() = status.configure(KotlinSimpleTypeNameRefStatusComponent)

public inline fun
    <T : TypeName, B : TypeRefBuilder<T>>
    B.kotlinStatus(
    block: KotlinSimpleTypeNameRefStatusComponent.Builder.() -> Unit
): B = apply {
    status.configure(KotlinSimpleTypeNameRefStatusComponent, block)
}

context(_: KotlinSimpleTypeNameRefStatusComponent.Key)
public inline fun <T : TypeName> TypeRefBuilder<T>.simpleStatus(
    block: KotlinSimpleTypeNameRefStatusComponent.Builder.() -> Unit
) {
    kotlinStatus.block()
}

public val
    <T : TypeName, B : TypeRefBuilder<T>>
    B.kotlinVariableStatus: KotlinVariableNameRefStatusComponent.Builder
    get() = status.configure(KotlinVariableNameRefStatusComponent)

public inline fun
    <T : TypeName, B : TypeRefBuilder<T>>
    B.kotlinVariableStatus(
    block: KotlinVariableNameRefStatusComponent.Builder.() -> Unit
): B = apply {
    status.configure(KotlinVariableNameRefStatusComponent, block)
}

context(_: KotlinVariableNameRefStatusComponent.Key)
public inline fun <T : TypeName> TypeRefBuilder<T>.variableStatus(
    block: KotlinVariableNameRefStatusComponent.Builder.() -> Unit
) {
    kotlinVariableStatus.block()
}

public val TypeNameRefStatus.kotlinOrNull: KotlinSimpleTypeNameRefStatusComponent?
    get() = this[KotlinSimpleTypeNameRefStatusComponent]

public val TypeRef<*>.kotlinStatusOrNull: KotlinSimpleTypeNameRefStatusComponent?
    get() = status.kotlinOrNull

public val TypeNameRefStatus.kotlinVariableOrNull: KotlinVariableNameRefStatusComponent?
    get() = this[KotlinVariableNameRefStatusComponent]

public val TypeRef<*>.kotlinVariableStatusOrNull: KotlinVariableNameRefStatusComponent?
    get() = status.kotlinVariableOrNull
