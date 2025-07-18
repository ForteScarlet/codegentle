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
 * Implementation of [KotlinSimpleTypeSpec].
 *
 * @author ForteScarlet
 */
internal data class KotlinSimpleTypeSpecImpl(
    override val kind: KotlinTypeSpec.Kind,
    override val name: String,
    override val kDoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<KotlinModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val superclass: TypeName?,
    override val superinterfaces: List<TypeName>,
    override val properties: List<KotlinPropertySpec>,
    override val initializerBlock: CodeValue,
    override val functions: List<KotlinFunctionSpec>,
    override val subtypes: List<KotlinTypeSpec>,
    override val primaryConstructor: KotlinConstructorSpec?,
    override val secondaryConstructors: List<KotlinConstructorSpec>
) : KotlinSimpleTypeSpec {
    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinSimpleTypeSpec(name='$name', kind=$kind)"
    }
}

/**
 * Implementation of [KotlinSimpleTypeSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinSimpleTypeSpecBuilderImpl(
    override val kind: KotlinTypeSpec.Kind,
    override val name: String
) : KotlinSimpleTypeSpec.Builder {
    companion object {
        val validKinds = MutableKotlinTypeSpecKindSet.of(
            KotlinTypeSpec.Kind.INTERFACE,
            KotlinTypeSpec.Kind.CLASS,
        )
    }

    init {
        require(kind in validKinds) {
            "Invalid kind $kind, must be one of $validKinds"
        }
    }

    private val kDoc = CodeValue.builder()
    private var superclass: TypeName? = null
    private val initializerBlock = CodeValue.builder()

    private val annotationRefs: MutableList<AnnotationRef> = mutableListOf()
    private val modifierSet = MutableKotlinModifierSet.empty()
    private val typeVariableRefs: MutableList<TypeRef<TypeVariableName>> = mutableListOf()
    private val superinterfaces: MutableList<TypeName> = mutableListOf()
    private val properties: MutableList<KotlinPropertySpec> = mutableListOf()
    private val functions: MutableList<KotlinFunctionSpec> = mutableListOf()
    private val subtypes: MutableList<KotlinTypeSpec> = mutableListOf()
    private var primaryConstructor: KotlinConstructorSpec? = null
    private val secondaryConstructors: MutableList<KotlinConstructorSpec> = mutableListOf()

    override fun addKDoc(codeValue: CodeValue): KotlinSimpleTypeSpec.Builder = apply {
        kDoc.add(codeValue)
    }

    override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinSimpleTypeSpec.Builder = apply {
        kDoc.add(format, *argumentParts)
    }

    override fun superclass(superclass: TypeName): KotlinSimpleTypeSpec.Builder = apply {
        this.superclass = superclass
    }

    override fun addInitializerBlock(codeValue: CodeValue): KotlinSimpleTypeSpec.Builder = apply {
        this.initializerBlock.add(codeValue)
    }

    override fun addInitializerBlock(
        format: String,
        vararg argumentParts: CodeArgumentPart
    ): KotlinSimpleTypeSpec.Builder = apply {
        this.initializerBlock.add(format, *argumentParts)
    }

    override fun addAnnotationRef(ref: AnnotationRef): KotlinSimpleTypeSpec.Builder = apply {
        annotationRefs.add(ref)
    }

    override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): KotlinSimpleTypeSpec.Builder = apply {
        annotationRefs.addAll(refs)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinSimpleTypeSpec.Builder = apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinSimpleTypeSpec.Builder = apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifier(modifier: KotlinModifier): KotlinSimpleTypeSpec.Builder = apply {
        this.modifierSet.add(modifier)
    }

    override fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): KotlinSimpleTypeSpec.Builder =
        apply {
            this.typeVariableRefs.addAll(typeVariables)
        }

    override fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): KotlinSimpleTypeSpec.Builder =
        apply {
            this.typeVariableRefs.addAll(typeVariables)
        }

    override fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): KotlinSimpleTypeSpec.Builder = apply {
        this.typeVariableRefs.add(typeVariable)
    }

    override fun addSuperinterfaces(vararg superinterfaces: TypeName): KotlinSimpleTypeSpec.Builder = apply {
        this.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): KotlinSimpleTypeSpec.Builder = apply {
        this.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterface(superinterface: TypeName): KotlinSimpleTypeSpec.Builder = apply {
        this.superinterfaces.add(superinterface)
    }

    override fun addProperties(vararg properties: KotlinPropertySpec): KotlinSimpleTypeSpec.Builder = apply {
        this.properties.addAll(properties)
    }

    override fun addProperties(properties: Iterable<KotlinPropertySpec>): KotlinSimpleTypeSpec.Builder = apply {
        this.properties.addAll(properties)
    }

    override fun addProperty(property: KotlinPropertySpec): KotlinSimpleTypeSpec.Builder = apply {
        this.properties.add(property)
    }

    override fun addFunctions(functions: Iterable<KotlinFunctionSpec>): KotlinSimpleTypeSpec.Builder = apply {
        this.functions.addAll(functions)
    }

    override fun addFunctions(vararg functions: KotlinFunctionSpec): KotlinSimpleTypeSpec.Builder = apply {
        this.functions.addAll(functions)
    }

    override fun addFunction(function: KotlinFunctionSpec): KotlinSimpleTypeSpec.Builder = apply {
        this.functions.add(function)
    }

    override fun addSubtypes(types: Iterable<KotlinTypeSpec>): KotlinSimpleTypeSpec.Builder = apply {
        this.subtypes.addAll(types)
    }

    override fun addSubtypes(vararg types: KotlinTypeSpec): KotlinSimpleTypeSpec.Builder = apply {
        this.subtypes.addAll(types)
    }

    override fun addSubtype(type: KotlinTypeSpec): KotlinSimpleTypeSpec.Builder = apply {
        this.subtypes.add(type)
    }

    override fun primaryConstructor(constructor: KotlinConstructorSpec?): KotlinSimpleTypeSpec.Builder = apply {
        this.primaryConstructor = constructor
    }

    override fun addSecondaryConstructor(constructor: KotlinConstructorSpec): KotlinSimpleTypeSpec.Builder = apply {
        this.secondaryConstructors.add(constructor)
    }

    override fun addSecondaryConstructors(constructors: Iterable<KotlinConstructorSpec>): KotlinSimpleTypeSpec.Builder = apply {
        this.secondaryConstructors.addAll(constructors)
    }

    override fun addSecondaryConstructors(vararg constructors: KotlinConstructorSpec): KotlinSimpleTypeSpec.Builder = apply {
        this.secondaryConstructors.addAll(constructors)
    }

    override fun build(): KotlinSimpleTypeSpec {
        return KotlinSimpleTypeSpecImpl(
            kind = kind,
            name = name,
            kDoc = kDoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = modifierSet.immutable(),
            typeVariables = typeVariableRefs.toList(),
            superclass = superclass,
            superinterfaces = superinterfaces.toList(),
            properties = properties.toList(),
            initializerBlock = initializerBlock.build(),
            functions = functions.toList(),
            subtypes = subtypes.toList(),
            primaryConstructor = primaryConstructor,
            secondaryConstructors = secondaryConstructors.toList()
        )
    }
}
