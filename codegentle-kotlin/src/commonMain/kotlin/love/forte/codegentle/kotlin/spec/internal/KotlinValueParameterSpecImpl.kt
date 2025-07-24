package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueBuilder
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 *
 * @author ForteScarlet
 */
internal data class KotlinValueParameterSpecImpl(
    override val name: String,
    override val typeRef: TypeRef<*>,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val kDoc: CodeValue,
    override val defaultValue: CodeValue?,
    override val propertyfication: KotlinValueParameterSpec.Propertyfication?
) : KotlinValueParameterSpec {
    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinValueParameterSpec(name='$name', type=${typeRef.typeName})"
    }
}

/**
 * Internal implementation of [KotlinValueParameterSpec.Propertyfication].
 */
internal data class PropertyficationImpl(
    override val mutable: Boolean
) : KotlinValueParameterSpec.Propertyfication

/**
 * Internal implementation of [KotlinValueParameterSpec.PropertyficationBuilder].
 */
internal class PropertyficationBuilderImpl : KotlinValueParameterSpec.PropertyficationBuilder {
    override var mutable: Boolean = false

    override fun mutable(mutable: Boolean): KotlinValueParameterSpec.PropertyficationBuilder = apply {
        this.mutable = mutable
    }

    override fun build(): KotlinValueParameterSpec.Propertyfication = PropertyficationImpl(mutable)
}

/**
 * Implementation of [KotlinValueParameterSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinValueParameterSpecBuilderImpl(
    override val name: String,
    override val type: TypeRef<*>
) : KotlinValueParameterSpec.Builder {
    private val modifierSet = MutableKotlinModifierSet.empty()
    private var defaultValue: CodeValue? = null
    private val kDoc: CodeValueBuilder = CodeValue.builder()
    private val annotations = mutableListOf<AnnotationRef>()
    private var propertyfication: KotlinValueParameterSpec.Propertyfication? = null

    override fun addModifier(modifier: KotlinModifier): KotlinValueParameterSpec.Builder = apply {
        modifierSet.add(modifier)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinValueParameterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinValueParameterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun defaultValue(codeValue: CodeValue): KotlinValueParameterSpec.Builder = apply {
        defaultValue = codeValue
    }

    override fun defaultValue(format: String, vararg argumentParts: CodeArgumentPart): KotlinValueParameterSpec.Builder =
        apply {
            defaultValue = CodeValue(format, *argumentParts)
        }

    override fun addKDoc(codeValue: CodeValue): KotlinValueParameterSpec.Builder = apply {
        kDoc.add(codeValue)
    }

    override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinValueParameterSpec.Builder = apply {
        kDoc.add(format, *argumentParts)
    }

    override fun addAnnotationRef(ref: AnnotationRef): KotlinValueParameterSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): KotlinValueParameterSpec.Builder = apply {
        annotations.addAll(refs)
    }

    override fun propertyfy(propertyfication: KotlinValueParameterSpec.Propertyfication): KotlinValueParameterSpec.Builder = apply {
        this.propertyfication = propertyfication
    }

    /**
     * Build [KotlinValueParameterSpec].
     */
    override fun build(): KotlinValueParameterSpec =
        KotlinValueParameterSpecImpl(
            name = name,
            typeRef = type,
            annotations = annotations.toList(),
            modifiers = modifierSet.immutable(),
            kDoc = kDoc.build(),
            defaultValue = defaultValue,
            propertyfication = propertyfication
        )
}
