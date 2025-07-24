package love.forte.codegentle.kotlin.ref

import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter

internal fun TypeRef<*>.emitKotlinTo(codeWriter: KotlinCodeWriter) {
    val status = this.status.kotlinOrNull
    
    // Emit annotations if any
    status?.annotations?.forEach { annotation ->
        codeWriter.emit(annotation)
        codeWriter.emit(" ")
    }
    
    // Emit the type name
    codeWriter.emit(this.typeName)
    
    // Emit nullable marker if needed
    if (status?.nullable == true) {
        codeWriter.emit("?")
    }
}
