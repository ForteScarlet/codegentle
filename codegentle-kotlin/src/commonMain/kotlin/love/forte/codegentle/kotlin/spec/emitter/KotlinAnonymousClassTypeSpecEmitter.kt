package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isNotEmpty
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.common.writer.withIndentBlock
import love.forte.codegentle.kotlin.spec.KotlinAnonymousClassTypeSpec
import love.forte.codegentle.kotlin.spec.KotlinTypeSpec
import love.forte.codegentle.kotlin.spec.isMemberNotEmpty
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType

/**
 * Extension function to emit a [KotlinAnonymousClassTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinAnonymousClassTypeSpec.emitTo(codeWriter: KotlinCodeWriter, isEnum: Boolean = false) {
    codeWriter.inType(this) {
        emitTo0(codeWriter, isEnum)
    }
}

private fun KotlinAnonymousClassTypeSpec.emitTo0(codeWriter: KotlinCodeWriter, isEnum: Boolean) {
    // Emit superclass and superinterfaces
    val hasExtends = superclass != null
    val hasImplements = superinterfaces.isNotEmpty()

    if (isEnum) {
        require(!hasImplements && !hasExtends) {
            "Enum constant cannot implement superinterfaces or extend superclass"
        }
    }

    if (!isEnum) {
        // Emit KDoc
        if (kDoc.isNotEmpty()) {
            codeWriter.emitDoc(kDoc)
        }
        // Emit annotations
        codeWriter.emitAnnotationRefs(annotations, false)

        // Emit "object : " for anonymous class
        codeWriter.emit(KotlinTypeSpec.Kind.OBJECT)
    }

    fun emitConstructorDelegation() {
        if (!isEnum || constructorDelegation.arguments.isNotEmpty()) {
            codeWriter.emit("(")
        }

        constructorDelegation.arguments.forEachIndexed { index, argument ->
            if (index > 0) codeWriter.emit(", ")
            codeWriter.emit(argument)
        }

        if (!isEnum || constructorDelegation.arguments.isNotEmpty()) {
            codeWriter.emit(")")
        }
    }

    if (hasExtends || hasImplements) {
        codeWriter.emit(" : ")

        if (hasExtends) {
            codeWriter.emit(superclass!!)

            // Emit super constructor arguments if any
            // This allows anonymous classes to call superclass constructors with arguments
            emitConstructorDelegation()

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
    } else {
        // is an enum
        emitConstructorDelegation()
    }

    // Emit the body
    // Anonymous classes cannot have constructors, so we skip constructor emission

    if (!isEnum || isMemberNotEmpty()) {
        val blankLineManager = BlankLineManager(codeWriter)
        codeWriter.withIndentBlock(prefix = " ") {
            emitMembers(codeWriter, blankLineManager)
        }
    }


}
