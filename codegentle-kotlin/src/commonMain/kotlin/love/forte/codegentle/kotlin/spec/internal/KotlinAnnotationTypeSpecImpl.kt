package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinAnnotationTypeSpec].
 *
 * @author ForteScarlet
 */
internal data class KotlinAnnotationTypeSpecImpl(
    override val name: String,
    override val kDoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val properties: List<KotlinPropertySpec>,
    override val functions: List<KotlinFunctionSpec>,
    override val subtypes: List<KotlinTypeSpec>
) : KotlinAnnotationTypeSpec {
    override val initializerBlock: CodeValue = CodeValue()

    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinAnnotationTypeSpec(name='$name', kind=$kind)"
    }
}

/**
 * Implementation of [KotlinAnnotationTypeSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinAnnotationTypeSpecBuilderImpl(
    override val name: String
) : KotlinAnnotationTypeSpec.Builder {
    private val kDoc = CodeValue.builder()

    private val annotationRefs: MutableList<AnnotationRef> = mutableListOf()
    private val modifierSet = MutableKotlinModifierSet.of(KotlinModifier.ANNOTATION)
    private val typeVariableRefs: MutableList<TypeRef<TypeVariableName>> = mutableListOf()
    private val properties: MutableList<KotlinPropertySpec> = mutableListOf()

    override fun addKDoc(codeValue: CodeValue): KotlinAnnotationTypeSpec.Builder = apply {
        kDoc.add(codeValue)
    }

    override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinAnnotationTypeSpec.Builder = apply {
        kDoc.add(format, *argumentParts)
    }

    override fun addAnnotationRef(ref: AnnotationRef): KotlinAnnotationTypeSpec.Builder = apply {
        annotationRefs.add(ref)
    }

    override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): KotlinAnnotationTypeSpec.Builder = apply {
        annotationRefs.addAll(refs)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinAnnotationTypeSpec.Builder = apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinAnnotationTypeSpec.Builder = apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifier(modifier: KotlinModifier): KotlinAnnotationTypeSpec.Builder = apply {
        this.modifierSet.add(modifier)
    }

    override fun addTypeVariableRefs(vararg typeVariables: TypeRef<TypeVariableName>): KotlinAnnotationTypeSpec.Builder = apply {
        this.typeVariableRefs.addAll(typeVariables)
    }

    override fun addTypeVariableRefs(typeVariables: Iterable<TypeRef<TypeVariableName>>): KotlinAnnotationTypeSpec.Builder = apply {
        this.typeVariableRefs.addAll(typeVariables)
    }

    override fun addTypeVariableRef(typeVariable: TypeRef<TypeVariableName>): KotlinAnnotationTypeSpec.Builder = apply {
        this.typeVariableRefs.add(typeVariable)
    }

    override fun addProperties(vararg properties: KotlinPropertySpec): KotlinAnnotationTypeSpec.Builder = apply {
        properties.forEach {
            addProperty(it)
        }
    }

    override fun addProperties(properties: Iterable<KotlinPropertySpec>): KotlinAnnotationTypeSpec.Builder = apply {
        properties.forEach {
            addProperty(it)
        }
    }

    override fun addProperty(property: KotlinPropertySpec): KotlinAnnotationTypeSpec.Builder = apply {
        checkProperty(property)
        this.properties.add(property)
    }

    private fun checkProperty(property: KotlinPropertySpec) {
        require(property.immutable) {
            "Annotation's property must be immutable, but $property"
        }
    }

    override fun build(): KotlinAnnotationTypeSpec {
        return KotlinAnnotationTypeSpecImpl(
            name = name,
            kDoc = kDoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = modifierSet.immutable(),
            typeVariables = typeVariableRefs.toList(),
            properties = properties.toList(),
            functions = emptyList(),
            subtypes = emptyList()
        )
    }
}
