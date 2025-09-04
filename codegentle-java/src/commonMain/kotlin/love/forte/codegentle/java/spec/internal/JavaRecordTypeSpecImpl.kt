/*
 * Copyright (C) 2025 Forte Scarlet
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
package love.forte.codegentle.java.spec.internal

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.java.InternalJavaCodeGentleApi
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.spec.*
import love.forte.codegentle.java.spec.emitter.emitTo
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emitToString

/**
 * Internal builder implementation for [JavaRecordTypeSpec].
 */
internal class JavaRecordTypeSpecBuilderImpl(
    override val name: String
) : JavaTypeSpecBuilderImpl<JavaRecordTypeSpec, JavaRecordTypeSpec.Builder>(),
    JavaRecordTypeSpec.Builder {
    private val superinterfaces: MutableList<TypeName> = mutableListOf()
    private val mainConstructorParameters = mutableListOf<JavaParameterSpec>()

    override fun addSuperinterfaces(superinterfaces: Iterable<TypeName>): JavaRecordTypeSpec.Builder = apply {
        checkSuperinterfaceSupport()
        this.superinterfaces.addAll(superinterfaces)
    }

    override fun addSuperinterface(superinterface: TypeName): JavaRecordTypeSpec.Builder = apply {
        checkSuperinterfaceSupport()
        this.superinterfaces.add(superinterface)
    }

    private fun checkSuperinterfaceSupport() {
        check(JavaTypeSpec.Kind.RECORD.states.superinterfacesSupport) { "`superinterface` is not supported for kind RECORD" }
    }

    override fun addMainConstructorParameter(mainConstructorParameter: JavaParameterSpec): JavaRecordTypeSpec.Builder = apply {
        mainConstructorParameters.add(mainConstructorParameter)
    }

    override fun addMainConstructorParameters(vararg mainConstructorParams: JavaParameterSpec): JavaRecordTypeSpec.Builder = apply {
        mainConstructorParameters.addAll(mainConstructorParams)
    }

    override fun addMainConstructorParameters(mainConstructorParams: Iterable<JavaParameterSpec>): JavaRecordTypeSpec.Builder = apply {
        mainConstructorParameters.addAll(mainConstructorParams)
    }

    override fun build(): JavaRecordTypeSpec {
        return JavaRecordTypeSpecImpl(
            name = name,
            kind = JavaTypeSpec.Kind.RECORD,
            mainConstructorParameters = mainConstructorParameters.toList(),
            javadoc = javadoc.build(),
            annotations = annotationRefs.toList(),
            modifiers = modifierSet.toSet(),
            typeVariables = typeVariableRefs.toList(),
            superinterfaces = superinterfaces.toList(),
            fields = fields.toList(),
            staticBlock = staticBlock.build(),
            initializerBlock = initializerBlock.build(),
            methods = methods.toList(),
            subtypes = subtypes.toList(),
        )
    }
}

/**
 *
 * @author ForteScarlet
 */
internal class JavaRecordTypeSpecImpl(
    override val name: String,
    override val kind: JavaTypeSpec.Kind,
    override val mainConstructorParameters: List<JavaParameterSpec>,
    override val javadoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<JavaModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val superinterfaces: List<TypeName>,
    override val fields: List<JavaFieldSpec>,
    override val staticBlock: CodeValue,
    override val initializerBlock: CodeValue,
    override val methods: List<JavaMethodSpec>,
    override val subtypes: List<JavaTypeSpec>
) : JavaRecordTypeSpec {

    override fun emit(codeWriter: JavaCodeWriter, implicitModifiers: Set<JavaModifier>) {
        emitTo(codeWriter, implicitModifiers)
    }

    @InternalJavaCodeGentleApi
    override fun emit(codeWriter: JavaCodeWriter) {
        emit(codeWriter, emptySet())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JavaRecordTypeSpec) return false

        if (name != other.name) return false
        if (kind != other.kind) return false
        if (mainConstructorParameters != other.mainConstructorParameters) return false
        if (javadoc != other.javadoc) return false
        if (annotations != other.annotations) return false
        if (modifiers != other.modifiers) return false
        if (typeVariables != other.typeVariables) return false
        if (superinterfaces != other.superinterfaces) return false
        if (fields != other.fields) return false
        if (staticBlock != other.staticBlock) return false
        if (initializerBlock != other.initializerBlock) return false
        if (methods != other.methods) return false
        if (subtypes != other.subtypes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + kind.hashCode()
        result = 31 * result + mainConstructorParameters.hashCode()
        result = 31 * result + javadoc.hashCode()
        result = 31 * result + annotations.hashCode()
        result = 31 * result + modifiers.hashCode()
        result = 31 * result + typeVariables.hashCode()
        result = 31 * result + superinterfaces.hashCode()
        result = 31 * result + fields.hashCode()
        result = 31 * result + staticBlock.hashCode()
        result = 31 * result + initializerBlock.hashCode()
        result = 31 * result + methods.hashCode()
        result = 31 * result + subtypes.hashCode()
        return result
    }

    override fun toString(): String {
        return emitToString()
    }
}
