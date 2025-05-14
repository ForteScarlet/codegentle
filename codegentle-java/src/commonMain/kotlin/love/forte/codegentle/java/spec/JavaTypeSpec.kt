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

@file:JvmName("JavaTypeSpecs")
@file:JvmMultifileClass

package love.forte.codegentle.java.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.java.*
import love.forte.codegentle.java.internal.isSourceName
import love.forte.codegentle.java.naming.JavaTypeName
import love.forte.codegentle.java.naming.JavaTypeVariableName
import love.forte.codegentle.java.naming.isPrimitive
import love.forte.codegentle.java.ref.JavaAnnotationRef
import love.forte.codegentle.java.ref.JavaAnnotationRefCollectable
import love.forte.codegentle.java.ref.JavaTypeRef
import love.forte.codegentle.java.writer.JavaCodeWriter
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


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
    public val javadoc: JavaCodeValue
    public val annotations: List<JavaAnnotationRef>
    public val modifiers: Set<JavaModifier>

    public fun hasModifier(modifier: JavaModifier): Boolean = modifier in modifiers

    public val typeVariables: List<JavaTypeRef<JavaTypeVariableName>>

    // TODO super class:
    //  `extends` One if is class,
    //  Nothing if is record, enum, annotation, interface.

    public val superclass: JavaTypeName?


    // TODO super interfaces:
    //  `extends` if is interface,
    //  `implements` others.

    public val superinterfaces: List<JavaTypeName>

    public val fields: List<JavaFieldSpec>

    public val staticBlock: JavaCodeValue

    public val initializerBlock: JavaCodeValue

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
) : JavaModifierBuilderContainer,
    JavaAnnotationRefCollectable<B>,
    BuilderDsl {
    init {
        check(name == null || name.isSourceName()) { "Invalid `name`: $name" }
    }

    internal val javadoc = JavaCodeValue.builder()
    internal var superclass: JavaTypeName? = null
    internal val staticBlock = JavaCodeValue.builder()
    internal val initializerBlock = JavaCodeValue.builder()

    internal val annotationRefs: MutableList<JavaAnnotationRef> = mutableListOf()
    internal val modifiers = JavaModifierSet()
    internal val typeVariableRefs: MutableList<JavaTypeRef<JavaTypeVariableName>> = mutableListOf()
    internal val superinterfaces: MutableList<JavaTypeName> = mutableListOf()
    internal val fields: MutableList<JavaFieldSpec> = mutableListOf()
    internal val methods: MutableList<JavaMethodSpec> = mutableListOf()
    internal val subtypes: MutableList<JavaTypeSpec> = mutableListOf()

    protected abstract val self: B

    public fun addJavadoc(codeValue: JavaCodeValue): B = self.apply {
        javadoc.add(codeValue)
    }

    public fun superclass(superclass: JavaTypeName): B = self.apply {
        check(kind.states.superclassSupport) { "`superclass` is not supported for kind $kind" }
        require(!superclass.isPrimitive) { "`superclass` can't be primitive." }
        this.superclass = superclass
    }

    public fun addStaticBlock(codeValue: JavaCodeValue): B = self.apply {
        this.staticBlock.add(codeValue)
    }

    public fun addInitializerBlock(codeValue: JavaCodeValue): B = self.apply {
        this.initializerBlock.add(codeValue)
    }

    override fun addAnnotationRef(ref: JavaAnnotationRef): B = self.apply {
        annotationRefs.add(ref)
    }

    override fun addAnnotationRefs(refs: Iterable<JavaAnnotationRef>): B = self.apply {
        annotationRefs.addAll(refs)
    }

    override fun addModifiers(vararg modifiers: JavaModifier): B = self.apply {
        this.modifiers.addAll(*modifiers)
    }

    override fun addModifiers(modifiers: Iterable<JavaModifier>): B = self.apply {
        this.modifiers.addAll(modifiers)
    }

    override fun addModifier(modifier: JavaModifier): B = self.apply {
        this.modifiers.add(modifier)
    }

    public fun addTypeVariableRefs(vararg typeVariables: JavaTypeRef<JavaTypeVariableName>): B = self.apply {
        this.typeVariableRefs.addAll(typeVariables)
    }

    public fun addTypeVariableRefs(typeVariables: Iterable<JavaTypeRef<JavaTypeVariableName>>): B = self.apply {
        this.typeVariableRefs.addAll(typeVariables)
    }

    public fun addTypeVariableRef(typeVariable: JavaTypeRef<JavaTypeVariableName>): B = self.apply {
        this.typeVariableRefs.add(typeVariable)
    }

    public fun addSuperinterfaces(vararg superinterfaces: JavaTypeName): B = self.apply {
        checkSuperinterfaceSupport()
        this.superinterfaces.addAll(superinterfaces)
    }

    public fun addSuperinterfaces(superinterfaces: Iterable<JavaTypeName>): B = self.apply {
        checkSuperinterfaceSupport()
        this.superinterfaces.addAll(superinterfaces)
    }

    public fun addSuperinterface(superinterface: JavaTypeName): B = self.apply {
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
    addJavadoc(JavaCodeValue(format, block))
}


public inline fun JavaTypeSpecBuilder<*, *>.addStaticBlock(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
) {
    addStaticBlock(JavaCodeValue(format, block))
}

public inline fun JavaTypeSpecBuilder<*, *>.addInitializerBlock(
    format: String,
    block: CodeValueSingleFormatBuilderDsl = {}
) {
    addInitializerBlock(JavaCodeValue(format, block))
}

public inline fun JavaTypeSpecBuilder<*, *>.addField(
    type: JavaTypeName,
    name: String,
    block: JavaFieldSpecBuilder.() -> Unit = {}
) {
    addField(JavaFieldSpec(type, name, block))
}

public inline fun JavaTypeSpecBuilder<*, *>.addField(
    type: JavaTypeRef<*>,
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
