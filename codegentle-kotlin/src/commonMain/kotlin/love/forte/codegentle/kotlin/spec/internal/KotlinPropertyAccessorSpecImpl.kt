package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueBuilder
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.CodeGentleKotlinSpecImplementation
import love.forte.codegentle.kotlin.spec.KotlinGetterSpec
import love.forte.codegentle.kotlin.spec.KotlinSetterSpec
import love.forte.codegentle.kotlin.spec.KotlinValueParameterSpec
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinGetterSpec].
 *
 * @author ForteScarlet
 */
@OptIn(CodeGentleKotlinSpecImplementation::class)
internal data class KotlinGetterSpecImpl(
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val kDoc: CodeValue,
    override val code: CodeValue
) : KotlinGetterSpec {
    override val parameters: List<KotlinValueParameterSpec> = emptyList()

    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinGetterSpec(modifiers=$modifiers)"
    }
}

/**
 * Implementation of [KotlinGetterSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinGetterSpecBuilderImpl : KotlinGetterSpec.Builder {
    private val kDoc: CodeValueBuilder = CodeValue.builder()
    private val code: CodeValueBuilder = CodeValue.builder()
    private val modifierSet = MutableKotlinModifierSet.empty()
    private val annotations = mutableListOf<AnnotationRef>()

    override fun addModifier(modifier: KotlinModifier): KotlinGetterSpec.Builder = apply {
        modifierSet.add(modifier)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinGetterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinGetterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addAnnotation(ref: AnnotationRef): KotlinGetterSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): KotlinGetterSpec.Builder = apply {
        annotations.addAll(refs)
    }

    override fun addKDoc(codeValue: CodeValue): KotlinGetterSpec.Builder = apply {
        kDoc.add(codeValue)
    }

    override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinGetterSpec.Builder = apply {
        kDoc.add(format, *argumentParts)
    }

    override fun addCode(codeValue: CodeValue): KotlinGetterSpec.Builder = apply {
        code.add(codeValue)
    }

    override fun addCode(format: String, vararg argumentParts: CodeArgumentPart): KotlinGetterSpec.Builder = apply {
        code.add(format, *argumentParts)
    }

    override fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): KotlinGetterSpec.Builder =
        apply {
            code.addStatement(format, *argumentParts)
        }

    override fun addStatement(codeValue: CodeValue): KotlinGetterSpec.Builder = apply {
        code.addStatement(codeValue)
    }

    override fun build(): KotlinGetterSpec =
        KotlinGetterSpecImpl(
            annotations = annotations.toList(),
            modifiers = modifierSet.immutable(),
            kDoc = kDoc.build(),
            code = code.build()
        )
}

/**
 * Implementation of [KotlinSetterSpec].
 *
 * @author ForteScarlet
 */
@OptIn(CodeGentleKotlinSpecImplementation::class)
internal data class KotlinSetterSpecImpl(
    override val parameterName: String,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val kDoc: CodeValue,
    override val code: CodeValue
) : KotlinSetterSpec {
    override val parameters: List<KotlinValueParameterSpec> = emptyList()

    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinSetterSpec(parameterName='$parameterName', modifiers=$modifiers)"
    }
}

/**
 * Implementation of [KotlinSetterSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinSetterSpecBuilderImpl(
    override val parameterName: String
) : KotlinSetterSpec.Builder {
    private val kDoc: CodeValueBuilder = CodeValue.builder()
    private val code: CodeValueBuilder = CodeValue.builder()
    private val modifierSet = MutableKotlinModifierSet.empty()
    private val annotations = mutableListOf<AnnotationRef>()

    override fun addModifier(modifier: KotlinModifier): KotlinSetterSpec.Builder = apply {
        modifierSet.add(modifier)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinSetterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinSetterSpec.Builder = apply {
        modifierSet.addAll(modifiers)
    }

    override fun addAnnotation(ref: AnnotationRef): KotlinSetterSpec.Builder = apply {
        annotations.add(ref)
    }

    override fun addAnnotations(refs: Iterable<AnnotationRef>): KotlinSetterSpec.Builder = apply {
        annotations.addAll(refs)
    }

    override fun addKDoc(codeValue: CodeValue): KotlinSetterSpec.Builder = apply {
        kDoc.add(codeValue)
    }

    override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinSetterSpec.Builder = apply {
        kDoc.add(format, *argumentParts)
    }

    override fun addCode(codeValue: CodeValue): KotlinSetterSpec.Builder = apply {
        code.add(codeValue)
    }

    override fun addCode(format: String, vararg argumentParts: CodeArgumentPart): KotlinSetterSpec.Builder = apply {
        code.add(format, *argumentParts)
    }

    override fun addStatement(format: String, vararg argumentParts: CodeArgumentPart): KotlinSetterSpec.Builder =
        apply {
            code.addStatement(format, *argumentParts)
        }

    override fun addStatement(codeValue: CodeValue): KotlinSetterSpec.Builder = apply {
        code.addStatement(codeValue)
    }

    override fun build(): KotlinSetterSpec =
        KotlinSetterSpecImpl(
            parameterName = parameterName,
            annotations = annotations.toList(),
            modifiers = modifierSet.immutable(),
            kDoc = kDoc.build(),
            code = code.build()
        )
}
