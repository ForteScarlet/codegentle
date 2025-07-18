package love.forte.codegentle.kotlin.spec.emitter

import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.writer.withIndent
import love.forte.codegentle.kotlin.spec.KotlinAnonymousClassTypeSpec
import love.forte.codegentle.kotlin.spec.internal.emitTo
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inType

/**
 * Extension function to emit a [KotlinAnonymousClassTypeSpec] to a [KotlinCodeWriter].
 */
internal fun KotlinAnonymousClassTypeSpec.emitTo(codeWriter: KotlinCodeWriter) {
    codeWriter.inType(this) {
        emitTo0(codeWriter)
    }
}

private fun KotlinAnonymousClassTypeSpec.emitTo0(codeWriter: KotlinCodeWriter) {
    var blockLineRequired = false

    // Emit KDoc
    if (!kDoc.isEmpty()) {
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
    if (!initializerBlock.isEmpty()) {
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
}
