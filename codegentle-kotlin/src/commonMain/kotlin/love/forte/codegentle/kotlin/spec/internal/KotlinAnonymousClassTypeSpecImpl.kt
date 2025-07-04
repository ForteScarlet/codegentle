package love.forte.codegentle.kotlin.spec.internal

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.KotlinModifier
import love.forte.codegentle.kotlin.MutableKotlinModifierSet
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

/**
 * Implementation of [KotlinAnonymousClassTypeSpec].
 *
 * @author ForteScarlet
 */
@OptIn(CodeGentleKotlinSpecImplementation::class)
internal data class KotlinAnonymousClassTypeSpecImpl(
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
    override val superConstructorArguments: List<CodeValue>
) : KotlinAnonymousClassTypeSpec {
    override fun emit(codeWriter: KotlinCodeWriter) {
        emitTo(codeWriter)
    }

    override fun toString(): String {
        return "KotlinAnonymousClassTypeSpec(superclass=$superclass, superinterfaces=$superinterfaces)"
    }
}

/**
 * Implementation of [KotlinAnonymousClassTypeSpec.Builder].
 *
 * @author ForteScarlet
 */
internal class KotlinAnonymousClassTypeSpecBuilderImpl : KotlinAnonymousClassTypeSpec.Builder {
    private val kDoc = CodeValue.builder()
    private var superclass: TypeName? = null
    private val initializerBlock = CodeValue.builder()

    private val annotationRefs: MutableList<AnnotationRef> = mutableListOf()
    private val modifierSet = MutableKotlinModifierSet.empty()
    private val typeVariableRefs: MutableList<TypeRef<TypeVariableName>> = mutableListOf()
    private val superinterfaces: MutableList<TypeName> = mutableListOf()
    private val properties: MutableList<KotlinPropertySpec> = mutableListOf()
    private val functions: MutableList<KotlinFunctionSpec> = mutableListOf()
    private val superConstructorArguments: MutableList<CodeValue> = mutableListOf()

    override fun addKDoc(codeValue: CodeValue): KotlinAnonymousClassTypeSpec.Builder = apply {
        kDoc.add(codeValue)
    }

    override fun addKDoc(format: String, vararg argumentParts: CodeArgumentPart): KotlinAnonymousClassTypeSpec.Builder = apply {
        kDoc.add(format, *argumentParts)
    }

    override fun superclass(superclass: TypeName): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superclass = superclass
    }

    override fun addInitializerBlock(codeValue: CodeValue): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.initializerBlock.add(codeValue)
    }

    override fun addInitializerBlock(format: String, vararg argumentParts: CodeArgumentPart): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.initializerBlock.add(format, *argumentParts)
    }

    override fun addAnnotationRef(ref: AnnotationRef): KotlinAnonymousClassTypeSpec.Builder = apply {
        annotationRefs.add(ref)
    }

    override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): KotlinAnonymousClassTypeSpec.Builder = apply {
        annotationRefs.addAll(refs)
    }

    override fun addModifiers(vararg modifiers: KotlinModifier): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<KotlinModifier>): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifier(modifier: KotlinModifier): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.modifierSet.add(modifier)
    }

    override fun addTypeVariableRefs(vararg typeVariables: TypeRef<TypeVariableName>): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.typeVariableRefs.addAll(typeVariables)
    }

    override fun addTypeVariableRefs(typeVariables: Iterable<TypeRef<TypeVariableName>>): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.typeVariableRefs.addAll(typeVariables)
    }

    override fun addTypeVariableRef(typeVariable: TypeRef<TypeVariableName>): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.typeVariableRefs.add(typeVariable)
    }

    override fun addSuperinterfaces(vararg superinterfaces: TypeName): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterface(superinterface: TypeName): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superinterfaces.add(superinterface)
    }

    override fun addProperties(vararg properties: KotlinPropertySpec): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.properties.addAll(properties)
    }

    override fun addProperties(properties: Iterable<KotlinPropertySpec>): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.properties.addAll(properties)
    }

    override fun addProperty(property: KotlinPropertySpec): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.properties.add(property)
    }

    override fun addFunctions(functions: Iterable<KotlinFunctionSpec>): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.functions.addAll(functions)
    }

    override fun addFunctions(vararg functions: KotlinFunctionSpec): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.functions.addAll(functions)
    }

    override fun addFunction(function: KotlinFunctionSpec): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.functions.add(function)
    }

    override fun addSuperConstructorArguments(vararg arguments: CodeValue): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superConstructorArguments.addAll(arguments)
    }

    override fun addSuperConstructorArguments(arguments: Iterable<CodeValue>): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superConstructorArguments.addAll(arguments)
    }

    override fun addSuperConstructorArgument(argument: CodeValue): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superConstructorArguments.add(argument)
    }

    override fun addSuperConstructorArgument(format: String, vararg argumentParts: CodeArgumentPart): KotlinAnonymousClassTypeSpec.Builder = apply {
        this.superConstructorArguments.add(CodeValue(format, *argumentParts))
    }

    override fun build(): KotlinAnonymousClassTypeSpec {
        return KotlinAnonymousClassTypeSpecImpl(
            kDoc = kDoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = modifierSet.immutable(),
            typeVariables = typeVariableRefs.toList(),
            superclass = superclass,
            superinterfaces = superinterfaces.toList(),
            properties = properties.toList(),
            initializerBlock = initializerBlock.build(),
            functions = functions.toList(),
            subtypes = emptyList(),
            superConstructorArguments = superConstructorArguments.toList()
        )
    }
}

/**
 * Extension function to emit a [KotlinAnonymousClassTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinAnonymousClassTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    // Push this type spec onto the stack
    codeWriter.pushType(this)
    var blockLineRequired = false

    // Emit KDoc
    if (!kDoc.isEmpty) {
        codeWriter.emitDoc(kDoc)
    }

    // Emit annotations
    codeWriter.emitAnnotationRefs(annotations, false)

    // Emit "object : " for anonymous class
    codeWriter.emit("object")

    // Emit superclass and superinterfaces
    val hasExtends = superclass != null
    val hasImplements = superinterfaces.isNotEmpty()

    if (hasExtends || hasImplements) {
        codeWriter.emit(" : ")

        if (hasExtends) {
            codeWriter.emit(superclass!!)

            // Emit super constructor arguments if any
            // This allows anonymous classes to call superclass constructors with arguments
            if (superConstructorArguments.isNotEmpty()) {
                codeWriter.emit("(")
                superConstructorArguments.forEachIndexed { index, argument ->
                    if (index > 0) codeWriter.emit(", ")
                    codeWriter.emit(argument)
                }
                codeWriter.emit(")")
            }

            if (hasImplements) {
                codeWriter.emit(", ")
            }
        }

        if (hasImplements) {
            superinterfaces.forEachIndexed { index, typeName ->
                if (index > 0) codeWriter.emit(", ")
                codeWriter.emit(typeName)
            }
        }
    }

    // Emit the body
    codeWriter.emitNewLine(" {")
    codeWriter.indent()

    // Anonymous classes cannot have constructors, so we skip constructor emission

    // Emit initializer block
    if (!initializerBlock.isEmpty) {
        if (blockLineRequired) {
            codeWriter.emitNewLine()
        }
        codeWriter.emitNewLine("init {")
        codeWriter.withIndent {
            emit(initializerBlock)
        }
        codeWriter.emitNewLine("}")
        blockLineRequired = true
    }

    // Emit properties
    if (properties.isNotEmpty()) {
        if (blockLineRequired) {
            codeWriter.emitNewLine()
        }

        for (property in properties) {
            property.emitTo(codeWriter)
            codeWriter.emitNewLine()
        }
        blockLineRequired = true
    }

    // Emit functions
    if (functions.isNotEmpty()) {
        if (blockLineRequired) {
            codeWriter.emitNewLine()
        }
        for (function in functions) {
            function.emitTo(codeWriter)
            codeWriter.emitNewLine()
        }
    }

    codeWriter.unindent()
    codeWriter.emit("}")

    // Pop this type spec from the stack
    codeWriter.popType()
}
