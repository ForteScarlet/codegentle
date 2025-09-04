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
package love.forte.codegentle.java.naming

import love.forte.codegentle.common.naming.MemberName
import love.forte.codegentle.common.naming.canonicalName
import love.forte.codegentle.common.utils.InternalMultisetApi
import love.forte.codegentle.java.writer.JavaCodeWriter

/**
 * Extension function to emit a [MemberName] to a [JavaCodeWriter].
 */
@OptIn(InternalMultisetApi::class)
internal fun MemberName.emitTo(codeWriter: JavaCodeWriter) {
    // Check if the member is already imported, in the same package, or in java.lang package
    var omitPackage = codeWriter.isInSamePackage(this)

    // If java.lang needs to be handled or is in the same package
    if ((codeWriter.strategy.omitJavaLangPackage()
            && packageName.isJavaLang)
    ) {
        omitPackage = true
    }

    // Check if the member is imported as a static import
    val isStaticImported = codeWriter.isStaticImported(this)
    if (isStaticImported) {
        // If it's a static import, just emit the name
        codeWriter.emit(name)
        return
    }

    // If there's an enclosing class, check if it's imported
    val enclosingClassName = enclosingClassName
    if (enclosingClassName != null) {
        // Emit the enclosing class name
        enclosingClassName.emitTo(codeWriter)
        codeWriter.emit(".")
        codeWriter.emit(name)
        return
    }

    // Otherwise, emit the package name if needed, followed by the name
    if (!omitPackage) {
        packageName.emitTo(codeWriter)
        codeWriter.emit(".")
    }

    codeWriter.emit(name)
}


// Helper methods
private fun JavaCodeWriter.isInSamePackage(memberName: MemberName): Boolean {
    return packageName == memberName.packageName
}

private fun JavaCodeWriter.isStaticImported(memberName: MemberName): Boolean {
    return memberName.canonicalName in staticImports
}
