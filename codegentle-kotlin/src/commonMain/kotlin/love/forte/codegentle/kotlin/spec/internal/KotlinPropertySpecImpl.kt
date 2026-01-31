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
package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueBuilder
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.KotlinGetterSpec
import love.forte.codegentle.kotlin.spec.KotlinPropertySpec
import love.forte.codegentle.kotlin.spec.KotlinSetterSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 *
 * @author ForteScarlet
 */
internal data class KotlinPropertySpecImpl(
    override val name: String,
    override val typeRef: TypeRef<*>,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val kDoc: CodeValue,
    override val initializer: CodeValue?,
    override val delegate: CodeValue?,
    override val getter: KotlinGetterSpec?,
    override val setter: KotlinSetterSpec?,
    override val mutable: Boolean
) : KotlinPropertySpec {
    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinPropertySpec(name='$name', type=${typeRef.typeName}, mutable=$mutable)"
    }
}

/**
 * Implementation of [KotlinPropertySpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinPropertySpecBuilderImpl(
    override val name: String,
    override val type: TypeRef<*>
) : KotlinPropertySpec.Builder {

    private val kDoc: CodeValueBuilder = CodeValue.builder()
    private val modifierSet = MutableKotlinModifierSet.empty()
    private val annotations = mutableListOf<AnnotationRef>()

    /**
     * Property's delegate.
     *
     * `val prop: $type by $codeValue`.
     *
     * Cannot exist at the same time as `initializer`.
     */
    private var delegate: CodeValue? = null

    /**
     * Initializer for property.
     * Cannot exist at the same time as `delegate`.
     */
    private var initializer: CodeValue? = null

    /**
     * Custom getter for the property.
     */
    private var getter: KotlinGetterSpec? = null

    /**
     * Custom setter for the property.
     */
    private var setter: KotlinSetterSpec? = null

    /**
     * Whether this property is mutable (var) or immutable (val).
     * Defaults to false (val/immutable).
     */
    private var mutable: Boolean = false

    /**
     * @throws IllegalArgumentException Cannot exist at the same time as `delegate`.
     */
    override fun initializer(codeValue: CodeValue): KotlinPropertySpec.Builder = apply {
        require(delegate == null) { "Cannot exist at the same time as `delegate`" }
        initializer = codeValue
    }

    /**
     * @throws IllegalArgumentException Cannot exist at the same time as `initializer`.
     */
    override fun initializer(format: String, vararg arguments: CodeArgumentPart): KotlinPropertySpec.Builder = apply {
        initializer(CodeValue(format, *arguments))
    }

    /**
     * @throws IllegalArgumentException Cannot exist at the same time as `initializer`.
     */
    override fun delegate(codeValue: CodeValue): KotlinPropertySpec.Builder = apply {
        require(initializer == null) { "Cannot exist at the same time as `initializer`" }
        delegate = codeValue
    }

    /**
     * @throws IllegalArgumentException Cannot exist at the same time as `initializer`.
     */
    override fun delegate(format: String, vararg arguments: CodeArgumentPart): KotlinPropertySpec.Builder = apply {
        require(initializer == null) { "Cannot exist at the same time as `initializer`" }
        delegate(CodeValue(format, *arguments))
    }

    override fun addModifier(modifier: KotlinModifier): KotlinPropertySpec.Builder = apply {
        modifierSet.add(modifier)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinPropertySpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinPropertySpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addAnnotation(ref: AnnotationRef): KotlinPropertySpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): KotlinPropertySpec.Builder = apply {
        annotations.addAll(refs)
    }

    override fun addDoc(codeValue: CodeValue): KotlinPropertySpec.Builder = apply {
        kDoc.addCode(codeValue)
    }

    override fun addDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinPropertySpec.Builder = apply {
        kDoc.addCode(format, *argumentParts)
    }

    /**
     * Set a custom getter for the property.
     *
     * @param getter the getter spec
     * @return this builder
     */
    override fun getter(getter: KotlinGetterSpec): KotlinPropertySpec.Builder = apply {
        this.getter = getter
    }

    /**
     * Set a custom setter for the property.
     *
     * @param setter the setter spec
     * @return this builder
     */
    override fun setter(setter: KotlinSetterSpec): KotlinPropertySpec.Builder = apply {
        this.setter = setter
    }

    /**
     * Set whether this property is mutable (var) or immutable (val).
     *
     * @param mutable true for mutable property (var), false for immutable property (val)
     * @return this builder
     */
    override fun mutable(mutable: Boolean): KotlinPropertySpec.Builder = apply {
        this.mutable = mutable
    }

    /**
     * Build [KotlinPropertySpec] instance.
     */
    override fun build(): KotlinPropertySpec =
        KotlinPropertySpecImpl(
            name = name,
            typeRef = type,
            annotations = annotations.toList(),
            modifiers = modifierSet.immutable(),
            kDoc = kDoc.build(),
            initializer = initializer,
            delegate = delegate,
            getter = getter,
            setter = setter,
            mutable = mutable
        )
}
