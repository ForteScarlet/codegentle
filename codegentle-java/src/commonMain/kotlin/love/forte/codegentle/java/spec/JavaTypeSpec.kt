/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package love.forte.codegentle.java.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.CodeValueSingleFormatBuilderDsl
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.JavaModifierCollector
import love.forte.codegentle.java.MutableJavaModifierSet
import love.forte.codegentle.java.internal.isSourceName
import love.forte.codegentle.java.naming.isPrimitive
import love.forte.codegentle.java.ref.JavaTypeRefBuilderDsl
import love.forte.codegentle.java.writer.JavaCodeWriter
import kotlin.jvm.JvmInline


/**
 * A generated class, interface, or enum declaration.
 *
 * @see JavaSimpleTypeSpec
 * @see JavaAnnotationTypeSpec
 * @see JavaEnumTypeSpec
 * @see JavaNonSealedTypeSpec
 * @see JavaSealedTypeSpec
 * @see JavaRecordTypeSpec
 *
 * @author ForteScarlet
 */
public sealed interface JavaTypeSpec : JavaSpec {
    public val name: String?
    public val kind: Kind
    public val javadoc: CodeValue
    public val annotations: List<AnnotationRef>
    public val modifiers: Set<JavaModifier>

    public fun hasModifier(modifier: JavaModifier): Boolean = modifier in modifiers

    public val typeVariables: List<TypeRef<TypeVariableName>>

    // TODO super class:
    //  `extends` One if is class,
    //  Nothing if is record, enum, annotation, interface.

    public val superclass: TypeName?


    // TODO super interfaces:
    //  `extends` if is interface,
    //  `implements` others.

    public val superinterfaces: List<TypeName>

    public val fields: List<JavaFieldSpec>

    public val staticBlock: CodeValue

    public val initializerBlock: CodeValue

    public val methods: List<JavaMethodSpec>

    // subtypes
    public val subtypes: List<JavaTypeSpec>

    // val nestedTypesSimpleNames: Set<String>? = null
    // val alwaysQualifiedNames: Set<String>? = null

    override fun emit(codeWriter: JavaCodeWriter) {
        emit(codeWriter, emptySet())
    }

    public fun emit(codeWriter: JavaCodeWriter, implicitModifiers: Set<JavaModifier> = emptySet())

    /**
     * Type kind
     */
    public enum class Kind(
        internal val states: States = States(0),
        internal val implicitFieldModifiers: Set<JavaModifier> = emptySet(),
        internal val implicitMethodModifiers: Set<JavaModifier> = emptySet(),
        internal val implicitTypeModifiers: Set<JavaModifier> = emptySet(),
        internal val asMemberModifiers: Set<JavaModifier> = emptySet(),
    ) {
        CLASS(
            states = States(
                State.SUPERCLASS_SUPPORT,
                State.SUPERINTERFACES_SUPPORT,
            )
        ),
        INTERFACE(
            states = States(
                State.SUPERINTERFACES_SUPPORT,
            ),
            implicitFieldModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.STATIC, JavaModifier.FINAL),
            implicitMethodModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.ABSTRACT),
            implicitTypeModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.STATIC),
            asMemberModifiers = setOf(JavaModifier.STATIC),
        ),
        ENUM(
            states = States(
                State.SUPERINTERFACES_SUPPORT,
            ),
            asMemberModifiers = setOf(JavaModifier.STATIC),
        ),
        ANNOTATION(
            states = States(),
            implicitFieldModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.STATIC, JavaModifier.FINAL),
            implicitMethodModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.ABSTRACT),
            implicitTypeModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.STATIC),
            asMemberModifiers = setOf(JavaModifier.STATIC),
        ),
        RECORD(
            states = States(
                State.SUPERINTERFACES_SUPPORT,
            )
        ),

        // abstract sealed class Vehicle permits Car, Truck
        SEALED_CLASS(
            states = States(
                State.SUPERCLASS_SUPPORT,
                State.SUPERINTERFACES_SUPPORT
            )
        ),

        // non-sealed class Car extends Vehicle implements Service
        NON_SEALED_CLASS(
            states = States(
                State.SUPERCLASS_SUPPORT
            )
        ),

        // sealed interface Service permits Car, Truck
        SEALED_INTERFACE(
            states = States(
                State.SUPERINTERFACES_SUPPORT,
            ),
            implicitFieldModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.STATIC, JavaModifier.FINAL),
            implicitMethodModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.ABSTRACT),
            implicitTypeModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.STATIC),
            asMemberModifiers = setOf(JavaModifier.STATIC),
        ),

        // non-sealed interface Service permits Car, Truck
        NON_SEALED_INTERFACE(
            states = States(
                State.SUPERINTERFACES_SUPPORT,
            ),
            implicitFieldModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.STATIC, JavaModifier.FINAL),
            implicitMethodModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.ABSTRACT),
            implicitTypeModifiers = setOf(JavaModifier.PUBLIC, JavaModifier.STATIC),
            asMemberModifiers = setOf(JavaModifier.STATIC),
        );

        @JvmInline
        internal value class States(private val bits: Int) {
            constructor(vararg states: State) : this(
                states.fold(0) { acc, state -> acc or state.bit }
            )

            operator fun contains(state: State): Boolean =
                bits and state.bit != 0

            inline val superclassSupport: Boolean
                get() = bits and State.SUPERCLASS_SUPPORT.bit != 0

            inline val superinterfacesSupport: Boolean
                get() = bits and State.SUPERINTERFACES_SUPPORT.bit != 0
        }

        internal enum class State {
            SUPERCLASS_SUPPORT,
            SUPERINTERFACES_SUPPORT,
            ;

            val bit: Int get() = 1 shl ordinal
        }
    }
}

public val JavaTypeSpec.nestedTypesSimpleNames: Set<String>
    get() = subtypes.mapTo(linkedSetOf()) { it.name!! }

/**
 * @see JavaSimpleTypeSpecBuilder
 * @see JavaAnnotationTypeSpecBuilder
 * @see JavaEnumTypeSpecBuilder
 * @see JavaNonSealedTypeSpecBuilder
 * @see JavaSealedTypeSpec.JavaSealedTypeSpecBuilder
 * @see JavaRecordTypeSpecBuilder
 */
public sealed class JavaTypeSpecBuilder<B : JavaTypeSpecBuilder<B, T>, T : JavaTypeSpec>(
    public val kind: JavaTypeSpec.Kind,
    public val name: String?,
) : JavaModifierCollector<B>,
    AnnotationRefCollector<B>,
    BuilderDsl {
    init {
        check(name == null || name.isSourceName()) { "Invalid `name`: $name" }
    }

    internal val javadoc = CodeValue.builder()
    internal var superclass: TypeName? = null
    internal val staticBlock = CodeValue.builder()
    internal val initializerBlock = CodeValue.builder()

    internal val annotationRefs: MutableList<AnnotationRef> = mutableListOf()
    internal val modifierSet = MutableJavaModifierSet.empty()
    internal val typeVariableRefs: MutableList<TypeRef<TypeVariableName>> = mutableListOf()
    internal val superinterfaces: MutableList<TypeName> = mutableListOf()
    internal val fields: MutableList<JavaFieldSpec> = mutableListOf()
    internal val methods: MutableList<JavaMethodSpec> = mutableListOf()
    internal val subtypes: MutableList<JavaTypeSpec> = mutableListOf()

    protected abstract val self: B

    public fun addJavadoc(codeValue: CodeValue): B = self.apply {
        javadoc.add(codeValue)
    }

    public fun superclass(superclass: TypeName): B = self.apply {
        check(kind.states.superclassSupport) { "`superclass` is not supported for kind $kind" }
        require(!superclass.isPrimitive) { "`superclass` can't be primitive." }
        this.superclass = superclass
    }

    public fun addStaticBlock(codeValue: CodeValue): B = self.apply {
        this.staticBlock.add(codeValue)
    }

    public fun addInitializerBlock(codeValue: CodeValue): B = self.apply {
        this.initializerBlock.add(codeValue)
    }

    override fun addAnnotationRef(ref: AnnotationRef): B = self.apply {
        annotationRefs.add(ref)
    }

    override fun addAnnotationRefs(refs: Iterable<AnnotationRef>): B = self.apply {
        annotationRefs.addAll(refs)
    }

    override fun addModifiers(vararg modifiers: JavaModifier): B = self.apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifiers(modifiers: Iterable<JavaModifier>): B = self.apply {
        this.modifierSet.addAll(modifiers)
    }

    override fun addModifier(modifier: JavaModifier): B = self.apply {
        this.modifierSet.add(modifier)
    }

    public fun addTypeVariableRefs(vararg typeVariables: TypeRef<TypeVariableName>): B = self.apply {
        this.typeVariableRefs.addAll(typeVariables)
    }

    public fun addTypeVariableRefs(typeVariables: Iterable<TypeRef<TypeVariableName>>): B = self.apply {
        this.typeVariableRefs.addAll(typeVariables)
    }

    public fun addTypeVariableRef(typeVariable: TypeRef<TypeVariableName>): B = self.apply {
        this.typeVariableRefs.add(typeVariable)
    }

    public fun addSuperinterfaces(vararg superinterfaces: TypeName): B = self.apply {
        checkSuperinterfaceSupport()
        this.superinterfaces.addAll(superinterfaces)
    }

    public fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): B = self.apply {
        checkSuperinterfaceSupport()
        this.superinterfaces.addAll(superinterfaces)
    }

    public fun addSuperinterface(superinterface: TypeName): B = self.apply {
        checkSuperinterfaceSupport()
        this.superinterfaces.add(superinterface)
    }

    private fun checkSuperinterfaceSupport() {
        check(kind.states.superinterfacesSupport) { "`superinterface` is not supported for kind $kind" }
    }

    public fun addFields(vararg fields: JavaFieldSpec): B = self.apply {
        this.fields.addAll(fields)
    }

    public fun addFields(fields: Iterable<JavaFieldSpec>): B = self.apply {
        this.fields.addAll(fields)
    }

    public fun addField(field: JavaFieldSpec): B = self.apply {
        this.fields.add(field)
    }

    // TODO check field?

    public fun addMethods(methods: Iterable<JavaMethodSpec>): B = self.apply {
        this.methods.addAll(methods)
    }

    public fun addMethods(vararg methods: JavaMethodSpec): B = self.apply {
        this.methods.addAll(methods)
    }

    public fun addMethod(method: JavaMethodSpec): B = self.apply {
        this.methods.add(method)
    }

    public fun addSubtypes(types: Iterable<JavaTypeSpec>): B = self.apply {
        this.subtypes.addAll(types)
    }

    public fun addSubtypes(vararg types: JavaTypeSpec): B = self.apply {
        this.subtypes.addAll(types)
    }

    public fun addSubtype(type: JavaTypeSpec): B = self.apply {
        this.subtypes.add(type)
    }


    public abstract fun build(): T
}


// extensions

public inline fun JavaTypeSpecBuilder<*, *>.addJavadoc(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
) {
    addJavadoc(CodeValue(format, block))
}


public inline fun JavaTypeSpecBuilder<*, *>.addStaticBlock(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
) {
    addStaticBlock(CodeValue(format, block))
}

public inline fun JavaTypeSpecBuilder<*, *>.addInitializerBlock(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
) {
    addInitializerBlock(CodeValue(format, block))
}

public inline fun JavaTypeSpecBuilder<*, *>.addField(
    type: TypeName,
    name: String,
    refBlock: JavaTypeRefBuilderDsl<TypeName> = {},
    block: JavaFieldSpecBuilder.() -> Unit = {}
) {
    addField(JavaFieldSpec(type, name, refBlock, block))
}

public inline fun JavaTypeSpecBuilder<*, *>.addField(
    type: TypeRef<*>,
    name: String,
    block: JavaFieldSpecBuilder.() -> Unit = {}
) {
    addField(JavaFieldSpec(type, name, block))
}

/**
 * Add a method
 */
public inline fun JavaTypeSpecBuilder<*, *>.addMethod(
    name: String,
    block: JavaMethodSpecBuilder.() -> Unit = {}
) {
    addMethod(JavaMethodSpec(name, block))
}

/**
 * Add a constructor
 */
public inline fun JavaTypeSpecBuilder<*, *>.addConstructor(
    block: JavaMethodSpecBuilder.() -> Unit = {}
) {
    addMethod(JavaMethodSpec(block))
}

// TODO addSubEnumType, addSubSimpleType ..., etc.
