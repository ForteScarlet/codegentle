package love.forte.codegentle.java.spec.internal

import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.naming.TypeVariableName
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.internal.doEmit
import love.forte.codegentle.java.internal.emitMembers
import love.forte.codegentle.java.spec.JavaAnonymousClassTypeSpec
import love.forte.codegentle.java.spec.JavaFieldSpec
import love.forte.codegentle.java.spec.JavaMethodSpec
import love.forte.codegentle.java.spec.JavaTypeSpec
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emit
import love.forte.codegentle.java.writer.emitToString


internal class JavaAnonymousClassTypeSpecImpl(
    override val kind: JavaTypeSpec.Kind,
    override val anonymousTypeArguments: CodeValue,
    override val javadoc: CodeValue,
    override val annotations: List<AnnotationRef>,
    override val modifiers: Set<JavaModifier>,
    override val typeVariables: List<TypeRef<TypeVariableName>>,
    override val superclass: TypeName?,
    override val superinterfaces: List<TypeName>,
    override val fields: List<JavaFieldSpec>,
    override val staticBlock: CodeValue,
    override val initializerBlock: CodeValue,
    override val methods: List<JavaMethodSpec>,
    override val subtypes: List<JavaTypeSpec>
) : JavaAnonymousClassTypeSpec {

    override fun emit(codeWriter: JavaCodeWriter, enumName: String?, implicitModifiers: Set<JavaModifier>) {
        doEmit(codeWriter) {
            if (enumName != null) {
                // TODO emit javadoc?
                codeWriter.emit(javadoc)
                // javadoc.emit(codeWriter)
                codeWriter.emitAnnotationRefs(annotations, false)
                codeWriter.emit(enumName)
                if (!anonymousTypeArguments.isEmpty()) {
                    codeWriter.emit("(")
                    codeWriter.emit(anonymousTypeArguments)
                    // anonymousTypeArguments.emit(codeWriter)
                    codeWriter.emit(")")
                }
                if (fields.isEmpty() && methods.isEmpty() && subtypes.isEmpty()) {
                    return
                }
                codeWriter.emitNewLine(" {")
            } else {
                val supertype = superinterfaces.firstOrNull()
                    ?: superclass

                codeWriter.emit("new %V(") { emitType(supertype!!) }
                codeWriter.emit(anonymousTypeArguments)
                // anonymousTypeArguments.emit(codeWriter)
                codeWriter.emitNewLine(") {")
            }

            emitMembers(codeWriter)
            codeWriter.popTypeVariableRefs(typeVariables)
            codeWriter.emit("}")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JavaAnonymousClassTypeSpec) return false

        if (kind != other.kind) return false
        if (anonymousTypeArguments != other.anonymousTypeArguments) return false
        if (javadoc != other.javadoc) return false
        if (annotations != other.annotations) return false
        if (modifiers != other.modifiers) return false
        if (typeVariables != other.typeVariables) return false
        if (superclass != other.superclass) return false
        if (superinterfaces != other.superinterfaces) return false
        if (fields != other.fields) return false
        if (staticBlock != other.staticBlock) return false
        if (initializerBlock != other.initializerBlock) return false
        if (methods != other.methods) return false
        if (subtypes != other.subtypes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = kind.hashCode()
        result = 31 * result + anonymousTypeArguments.hashCode()
        result = 31 * result + javadoc.hashCode()
        result = 31 * result + annotations.hashCode()
        result = 31 * result + modifiers.hashCode()
        result = 31 * result + typeVariables.hashCode()
        result = 31 * result + (superclass?.hashCode() ?: 0)
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
