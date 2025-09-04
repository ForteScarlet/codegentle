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
package love.forte.codegentle.java.internal

import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.naming.TypeName
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.common.writer.withIndentBlock
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.naming.JavaClassNames
import love.forte.codegentle.java.spec.JavaTypeSpec
import love.forte.codegentle.java.spec.internal.JavaSimpleTypeSpecImpl
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.emit

internal inline fun doEmit(
    codeWriter: JavaCodeWriter,
    block: () -> Unit
) {
    // Nested classes interrupt wrapped line indentation. Stash the current wrapping state and put
    // it back afterwards when this type is complete.
    val previousStatementLine = codeWriter.statementLine
    codeWriter.statementLine = -1

    try {
        block()
    } finally {
        codeWriter.statementLine = previousStatementLine
    }
}


internal fun JavaTypeSpec.emitSupers(codeWriter: JavaCodeWriter) {
    val extendsTypes: List<TypeName>
    val implementsTypes: List<TypeName>

    if (kind == JavaTypeSpec.Kind.INTERFACE) {
        extendsTypes = superinterfaces
        implementsTypes = emptyList()
    } else {
        extendsTypes = superclass?.let {
            if (it != JavaClassNames.OBJECT) {
                listOf(it)
            } else null
        } ?: emptyList()

        implementsTypes = superinterfaces
    }

    codeWriter.emitExtends(extendsTypes)
    codeWriter.emitImplements(implementsTypes)
}

internal fun JavaCodeWriter.emitExtends(extendsTypes: List<TypeName>) {
    if (extendsTypes.isNotEmpty()) {
        emit(" extends")
        var firstType = true
        for (extendsType in extendsTypes) {
            if (!firstType) {
                emit(",")
            }
            emit(" %V") { emitType(extendsType) }
            firstType = false
        }
    }
}

internal fun JavaCodeWriter.emitImplements(implementsTypes: List<TypeName>) {
    if (implementsTypes.isNotEmpty()) {
        emit(" implements")
        var firstType = true
        for (implementsType in implementsTypes) {
            if (!firstType) {
                emit(",")
            }
            emit(" %V") { emitType(implementsType) }
            firstType = false
        }
    }
}

internal inline fun JavaTypeSpec.emitMembers(
    codeWriter: JavaCodeWriter,
    blankLineManager: BlankLineManager,
    isRecord: Boolean = false,
    block: (blankLineManager: BlankLineManager) -> Unit = { }
) {
    codeWriter.pushType(this)
    codeWriter.indent()

    block(blankLineManager)

    // Static fields
    for (field in fields) {
        if (!field.hasModifier(JavaModifier.STATIC)) continue
        blankLineManager.withRequirement {
            field.emit(codeWriter, kind.implicitFieldModifiers)
        }
    }

    // Static block
    if (!staticBlock.isEmpty()) {
        blankLineManager.withRequirement {
            codeWriter.emit(staticBlock)
        }
    }

    // Non-static fields
    for (field in fields) {
        if (field.hasModifier(JavaModifier.STATIC)) continue
        blankLineManager.withRequirement {
            field.emit(codeWriter, kind.implicitFieldModifiers)
        }
    }

    // Initializer block
    if (!initializerBlock.isEmpty()) {
        // Both regular classes and records support instance initializer blocks
        // In records, initializer blocks are executed after the implicit constructor
        // assigns the record components

        // Each part in the initializerBlock should be emitted as a separate {} block
        blankLineManager.withRequirement {
            codeWriter.withIndentBlock {
                codeWriter.emit(initializerBlock)
                codeWriter.emitNewLine()
            }
            codeWriter.emitNewLine()
        }
    }

    // Constructors
    for (method in methods) {
        if (!method.isConstructor) continue
        blankLineManager.withRequirement {
            method.emit(codeWriter, name, kind.implicitMethodModifiers)
            codeWriter.emitNewLine()
        }
    }

    // Methods
    for (method in methods) {
        if (method.isConstructor) continue
        blankLineManager.withRequirement {
            method.emit(codeWriter, null, kind.implicitMethodModifiers)
            codeWriter.emitNewLine()
        }
    }

    // Types
    for (type in subtypes) {
        blankLineManager.withRequirement {
            type.emit(codeWriter, kind.implicitFieldModifiers)
        }
    }

    codeWriter.unindent()
    codeWriter.popType()
}


internal fun JavaTypeSpec.toVirtualTypeSpec(name: String) =
    JavaSimpleTypeSpecImpl(
        name = name,
        kind = kind,
        javadoc = javadoc,
        annotations = emptyList(),
        modifiers = emptySet(),
        typeVariables = emptyList(),
        superclass = null,
        superinterfaces = emptyList(),
        fields = emptyList(),
        staticBlock = staticBlock,
        initializerBlock = initializerBlock,
        methods = emptyList(),
        subtypes = emptyList(),
    )
