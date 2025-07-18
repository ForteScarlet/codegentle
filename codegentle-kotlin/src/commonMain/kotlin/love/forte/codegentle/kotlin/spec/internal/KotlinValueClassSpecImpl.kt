package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinValueClassSpec].
 *
 * @author ForteScarlet
 */
internal data class KotlinValueClassSpecImpl(
    override val name: String,
    override val primaryParameter: KotlinValueParameterSpec,
    override val kDoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val superinterfaces: List<TypeName>,
    override val properties: List<KotlinPropertySpec>,
    override val initializerBlock: CodeValue,
    override val functions: List<KotlinFunctionSpec>,
    override val subtypes: List<KotlinTypeSpec>
) : KotlinValueClassSpec {
    override val superclass: TypeName? = null

    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinValueClassSpec(name='$name', kind=$kind)"
    }
}

/**
 * Builder implementation for [KotlinValueClassSpec].
 */
internal class KotlinValueClassSpecBuilderImpl(
    override val name: String,
    override val primaryParameter: KotlinValueParameterSpec
) : KotlinValueClassSpec.Builder {
    init {
        val propertyization = primaryParameter.propertyization
        require(propertyization?.mutable == false) {
            "The primary parameter property of value class must be immutable " +
                "(`propertyization.mutable` must be `false`), " +
                "but current parameter's `propertyization.mutable` is ${propertyization?.mutable}"
        }
    }

    private val kDoc = CodeValue.builder()
    private val initializerBlock = CodeValue.builder()

    private val annotationRefs: MutableList<AnnotationRef> = mutableListOf()
    private val modifierSet = MutableKotlinModifierSet.of(KotlinModifier.VALUE)
    private val typeVariableRefs: MutableList<TypeRef<TypeVariableName>> = mutableListOf()
    private val superinterfaces: MutableList<TypeName> = mutableListOf()
    private val properties: MutableList<KotlinPropertySpec> = mutableListOf()
    private val functions: MutableList<KotlinFunctionSpec> = mutableListOf()

    override fun addKDoc(codeValue: CodeValue): KotlinValueClassSpec.Builder = apply {
        kDoc.add(codeValue)
    }

    override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinValueClassSpec.Builder = apply {
        kDoc.add(format, *argumentParts)
    }

    override fun addInitializerBlock(codeValue: CodeValue): KotlinValueClassSpec.Builder = apply {
        this.initializerBlock.add(codeValue)
    }

    override fun addInitializerBlock(
        format: String,
        vararg argumentParts: CodeArgumentPart
    ): KotlinValueClassSpec.Builder = apply {
        this.initializerBlock.add(format, *argumentParts)
    }

    override fun addAnnotationRef(ref: AnnotationRef): KotlinValueClassSpec.Builder = apply {
        annotationRefs.add(ref)
    }

    override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): KotlinValueClassSpec.Builder = apply {
        annotationRefs.addAll(refs)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinValueClassSpec.Builder = apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinValueClassSpec.Builder = apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifier(modifier: KotlinModifier): KotlinValueClassSpec.Builder = apply {
        this.modifierSet.add(modifier)
    }

    override fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): KotlinValueClassSpec.Builder =
        apply {
            this.typeVariableRefs.addAll(typeVariables)
        }

    override fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): KotlinValueClassSpec.Builder =
        apply {
            this.typeVariableRefs.addAll(typeVariables)
        }

    override fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): KotlinValueClassSpec.Builder = apply {
        this.typeVariableRefs.add(typeVariable)
    }

    override fun addSuperinterfaces(vararg superinterfaces: TypeName): KotlinValueClassSpec.Builder = apply {
        this.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): KotlinValueClassSpec.Builder = apply {
        this.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterface(superinterface: TypeName): KotlinValueClassSpec.Builder = apply {
        this.superinterfaces.add(superinterface)
    }

    override fun addProperties(vararg properties: KotlinPropertySpec): KotlinValueClassSpec.Builder = apply {
        this.properties.addAll(properties)
    }

    override fun addProperties(properties: Iterable<KotlinPropertySpec>): KotlinValueClassSpec.Builder = apply {
        this.properties.addAll(properties)
    }

    override fun addProperty(property: KotlinPropertySpec): KotlinValueClassSpec.Builder = apply {
        this.properties.add(property)
    }

    override fun addFunctions(functions: Iterable<KotlinFunctionSpec>): KotlinValueClassSpec.Builder = apply {
        this.functions.addAll(functions)
    }

    override fun addFunctions(vararg functions: KotlinFunctionSpec): KotlinValueClassSpec.Builder = apply {
        this.functions.addAll(functions)
    }

    override fun addFunction(function: KotlinFunctionSpec): KotlinValueClassSpec.Builder = apply {
        this.functions.add(function)
    }

    override fun build(): KotlinValueClassSpec {
        val immutableModifiers = MutableKotlinModifierSet.of(modifierSet).apply {
            add(KotlinModifier.VALUE)
        }.immutable()

        return KotlinValueClassSpecImpl(
            name = name,
            primaryParameter = primaryParameter,
            kDoc = kDoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = immutableModifiers,
            typeVariables = typeVariableRefs.toList(),
            superinterfaces = superinterfaces.toList(),
            properties = properties.toList(),
            initializerBlock = initializerBlock.build(),
            functions = functions.toList(),
            subtypes = emptyList<KotlinTypeSpec>()
        )
    }
}
