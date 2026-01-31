/*
 * Copyright (C) 2014-2024 Square, Inc.
 * Copyright (C) 2015-2026 Forte Scarlet
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
package love.forte.codegentle.java.spec

import love.forte.codegentle.common.BuilderDsl
import love.forte.codegentle.common.GenEnumSet
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.DocCollector
import love.forte.codegentle.common.code.InitializerBlockCollector
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.AnnotationRefCollector
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.ref.TypeVariableCollector
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.JavaModifierCollector
import love.forte.codegentle.java.MutableJavaModifierSet
import love.forte.codegentle.java.code.StaticBlockCollector
import love.forte.codegentle.java.spec.JavaTypeSpec.Kind.State
import love.forte.codegentle.java.writer.JavaCodeWriter


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

    // super class:
    //  `extends` One if is class,
    //  Nothing if is record, enum, annotation, interface.

    public val superclass: TypeName?


    // super interfaces:
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
        internal val states: Set<State> = emptySet(),
        internal val implicitFieldModifiers: Set<JavaModifier> = emptySet(),
        internal val implicitMethodModifiers: Set<JavaModifier> = emptySet(),
        internal val implicitTypeModifiers: Set<JavaModifier> = emptySet(),
        internal val asMemberModifiers: Set<JavaModifier> = emptySet(),
    ) {
        CLASS(
            states = MutableJavaTypeKindSpecStateSet.of(
                State.SUPERCLASS_SUPPORT,
                State.SUPERINTERFACES_SUPPORT,
            )
        ),
        INTERFACE(
            states = MutableJavaTypeKindSpecStateSet.of(
                State.SUPERINTERFACES_SUPPORT,
            ),
            implicitFieldModifiers = MutableJavaModifierSet.of(
                JavaModifier.PUBLIC, JavaModifier.STATIC, JavaModifier.FINAL
            ),
            implicitMethodModifiers = MutableJavaModifierSet.of(JavaModifier.PUBLIC, JavaModifier.ABSTRACT),
            implicitTypeModifiers = MutableJavaModifierSet.of(JavaModifier.PUBLIC, JavaModifier.STATIC),
            asMemberModifiers = MutableJavaModifierSet.of(JavaModifier.STATIC),
        ),
        ENUM(
            states = MutableJavaTypeKindSpecStateSet.of(
                State.SUPERINTERFACES_SUPPORT,
            ),
            asMemberModifiers = MutableJavaModifierSet.of(JavaModifier.STATIC),
        ),
        ANNOTATION(
            implicitFieldModifiers = MutableJavaModifierSet.of(
                JavaModifier.PUBLIC, JavaModifier.STATIC, JavaModifier.FINAL
            ),
            implicitMethodModifiers = MutableJavaModifierSet.of(JavaModifier.PUBLIC, JavaModifier.ABSTRACT),
            implicitTypeModifiers = MutableJavaModifierSet.of(JavaModifier.PUBLIC, JavaModifier.STATIC),
            asMemberModifiers = MutableJavaModifierSet.of(JavaModifier.STATIC),
        ),
        RECORD(
            states = MutableJavaTypeKindSpecStateSet.of(
                State.SUPERINTERFACES_SUPPORT,
            )
        ),

        // abstract sealed class Vehicle permits Car, Truck
        SEALED_CLASS(
            states = MutableJavaTypeKindSpecStateSet.of(
                State.SUPERCLASS_SUPPORT,
                State.SUPERINTERFACES_SUPPORT
            )
        ),

        // non-sealed class Car extends Vehicle implements Service
        NON_SEALED_CLASS(
            states = MutableJavaTypeKindSpecStateSet.of(
                State.SUPERCLASS_SUPPORT,
                State.SUPERINTERFACES_SUPPORT,
            )
        ),

        // sealed interface Service permits Car, Truck
        SEALED_INTERFACE(
            states = MutableJavaTypeKindSpecStateSet.of(
                State.SUPERINTERFACES_SUPPORT,
            ),
            implicitFieldModifiers = MutableJavaModifierSet.of(
                JavaModifier.PUBLIC, JavaModifier.STATIC, JavaModifier.FINAL
            ),
            implicitMethodModifiers = MutableJavaModifierSet.of(JavaModifier.PUBLIC, JavaModifier.ABSTRACT),
            implicitTypeModifiers = MutableJavaModifierSet.of(JavaModifier.PUBLIC, JavaModifier.STATIC),
            asMemberModifiers = MutableJavaModifierSet.of(JavaModifier.STATIC),
        ),

        // non-sealed interface Service permits Car, Truck
        NON_SEALED_INTERFACE(
            states = MutableJavaTypeKindSpecStateSet.of(
                State.SUPERINTERFACES_SUPPORT,
            ),
            implicitFieldModifiers = MutableJavaModifierSet.of(
                JavaModifier.PUBLIC, JavaModifier.STATIC, JavaModifier.FINAL
            ),
            implicitMethodModifiers = MutableJavaModifierSet.of(JavaModifier.PUBLIC, JavaModifier.ABSTRACT),
            implicitTypeModifiers = MutableJavaModifierSet.of(JavaModifier.PUBLIC, JavaModifier.STATIC),
            asMemberModifiers = MutableJavaModifierSet.of(JavaModifier.STATIC),
        );

        @GenEnumSet(
            internal = true,
            mutableName = "MutableJavaTypeKindSpecStateSet",
            immutableName = "JavaTypeSpecKindStateSet",
        )
        internal enum class State {
            SUPERCLASS_SUPPORT,
            SUPERINTERFACES_SUPPORT
        }
    }
}

public val JavaTypeSpec.nestedTypesSimpleNames: Set<String>
    get() = subtypes.mapTo(linkedSetOf()) { it.name!! }


public interface JavaTypeSpecBuilder<T : JavaTypeSpec, B : JavaTypeSpecBuilder<T, B>> :
    BuilderDsl,
    DocCollector<B>,
    InitializerBlockCollector<B>,
    StaticBlockCollector<B>,
    TypeVariableCollector<B>,
    JavaModifierCollector<B>,
    JavaMethodCollector<B>,
    JavaFieldCollector<B>,
    JavaSubtypeCollector<B>,
    AnnotationRefCollector<B> {

        // TODO kind 和 name 需要都有吗？

    /**
     * The type kind.
     */
    public val kind: JavaTypeSpec.Kind
    
    /**
     * The type name.
     */
    public val name: String
    
    /**
     * Build [JavaTypeSpec] instance.
     */
    public fun build(): T
}


internal val Set<State>.superclassSupport: Boolean
    get() = State.SUPERCLASS_SUPPORT in this

internal val Set<State>.superinterfacesSupport: Boolean
    get() = State.SUPERINTERFACES_SUPPORT in this
