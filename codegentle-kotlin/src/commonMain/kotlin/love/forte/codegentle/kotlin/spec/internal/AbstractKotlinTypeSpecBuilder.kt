package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueBuilder
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.CodeGentleKotlinSpecImplementation
import love.forte.codegentle.kotlin.spec.KotlinFunctionSpec
import love.forte.codegentle.kotlin.spec.KotlinPropertySpec
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec

/**
 * Abstract implementation of [KotlinTypeSpec.Builder] that provides common functionality.
 */
@OptIn(CodeGentleKotlinSpecImplementation::class)
internal abstract class AbstractKotlinTypeSpecBuilder<B : AbstractKotlinTypeSpecBuilder<B, T>, T : KotlinTypeSpec>(
    override val kind: KotlinTypeSpec.Kind,
    override val name: String?,
) : KotlinTypeSpec.Builder<B, T> {

    protected val kDoc: CodeValueBuilder = CodeValue.Companion.builder()
    protected var superclass: TypeName? = null
    protected val initializerBlock: CodeValueBuilder = CodeValue.Companion.builder()

    protected val annotationRefs: MutableList<AnnotationRef> = mutableListOf()
    private val modifierSet: MutableKotlinModifierSet = MutableKotlinModifierSet.Companion.empty()
    protected val typeVariableRefs: MutableList<TypeRef<TypeVariableName>> = mutableListOf()
    protected val superinterfaces: MutableList<TypeName> = mutableListOf()
    protected val properties: MutableList<KotlinPropertySpec> = mutableListOf()
    protected val functions: MutableList<KotlinFunctionSpec> = mutableListOf()
    protected val subtypes: MutableList<KotlinTypeSpec> = mutableListOf()

    /**
     * Get the self reference for method chaining.
     */
    @Suppress("UNCHECKED_CAST")
    protected abstract val self: B

    override fun addKDoc(codeValue: CodeValue): B = self.apply {
        kDoc.add(codeValue)
    }

    override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): B = self.apply {
        kDoc.add(format, *argumentParts)
    }

    override fun superclass(superclass: TypeName): B = self.apply {
        this.superclass = superclass
    }

    override fun addInitializerBlock(codeValue: CodeValue): B = self.apply {
        initializerBlock.add(codeValue)
    }

    override fun addInitializerBlock(format: String, vararg argumentParts: CodeArgumentPart): B = self.apply {
        initializerBlock.add(format, *argumentParts)
    }

    override fun addAnnotationRef(ref: AnnotationRef): B = self.apply {
        annotationRefs.add(ref)
    }

    override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): B = self.apply {
        annotationRefs.addAll(refs)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): B = self.apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): B = self.apply {
        modifierSet.addAll(modifiers)
    }

    override fun addModifier(modifier: KotlinModifier): B = self.apply {
        modifierSet.add(modifier)
    }

    override fun addTypeVariables(vararg typeVariables: TypeRef<TypeVariableName>): B = self.apply {
        typeVariableRefs.addAll(typeVariables)
    }

    override fun addTypeVariables(typeVariables: Iterable<TypeRef<TypeVariableName>>): B = self.apply {
        typeVariableRefs.addAll(typeVariables)
    }

    override fun addTypeVariable(typeVariable: TypeRef<TypeVariableName>): B = self.apply {
        typeVariableRefs.add(typeVariable)
    }

    override fun addSuperinterfaces(vararg superinterfaces: TypeName): B = self.apply {
        this.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): B = self.apply {
        this.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterface(superinterface: TypeName): B = self.apply {
        this.superinterfaces.add(superinterface)
    }

    override fun addProperties(vararg properties: KotlinPropertySpec): B = self.apply {
        this.properties.addAll(properties)
    }

    override fun addProperties(properties: Iterable<KotlinPropertySpec>): B = self.apply {
        this.properties.addAll(properties)
    }

    override fun addProperty(property: KotlinPropertySpec): B = self.apply {
        this.properties.add(property)
    }

    override fun addFunctions(functions: Iterable<KotlinFunctionSpec>): B = self.apply {
        this.functions.addAll(functions)
    }

    override fun addFunctions(vararg functions: KotlinFunctionSpec): B = self.apply {
        this.functions.addAll(functions)
    }

    override fun addFunction(function: KotlinFunctionSpec): B = self.apply {
        this.functions.add(function)
    }

    override fun addSubtypes(types: Iterable<KotlinTypeSpec>): B = self.apply {
        this.subtypes.addAll(types)
    }

    override fun addSubtypes(vararg types: KotlinTypeSpec): B = self.apply {
        this.subtypes.addAll(types)
    }

    override fun addSubtype(type: KotlinTypeSpec): B = self.apply {
        this.subtypes.add(type)
    }
}
