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

import love.forte.codegentle.common.code.CodeArgumentPart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.isEmpty
import love.forte.codegentle.common.computeValueIfAbsent
import love.forte.codegentle.common.naming.*
import love.forte.codegentle.common.ref.AnnotationRef
import love.forte.codegentle.common.ref.TypeRef
import love.forte.codegentle.common.utils.BlankLineManager
import love.forte.codegentle.common.writer.ImportName
import love.forte.codegentle.common.writer.InternalWriterApi
import love.forte.codegentle.java.JavaFile
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.naming.JavaLangPackageName
import love.forte.codegentle.java.ref.JavaTypeNameRefStatusComponent
import love.forte.codegentle.java.spec.JavaFieldSpec
import love.forte.codegentle.java.spec.JavaMethodSpec
import love.forte.codegentle.java.spec.JavaParameterSpec
import love.forte.codegentle.java.spec.JavaTypeSpec
import love.forte.codegentle.java.strategy.JavaWriteStrategy
import love.forte.codegentle.java.strategy.ToStringJavaWriteStrategy
import love.forte.codegentle.java.writer.JavaCodeWriter
import love.forte.codegentle.java.writer.inPackage

@OptIn(InternalWriterApi::class)
internal class JavaFileImpl(
    override val fileComment: CodeValue,
    override val packageName: PackageName,
    override val type: JavaTypeSpec,
    override val secondaryTypes: List<JavaTypeSpec>,
    override val skipJavaLangImports: Boolean,
    override val staticImports: Set<String>,
    override val alwaysQualify: Set<String>,
    override val indent: String
) : JavaFile {
    override fun writeTo(out: Appendable, strategy: JavaWriteStrategy) {
        // First pass: emit the entire class, just to collect the types we'll need to import.
        val suggestedImports = linkedMapOf<String, ImportName>()
        val classImportVisitor = ClassImportVisitor(alwaysQualify, suggestedImports, packageName)
        classImportVisitor.visitTypeSpec(type)

        // Also collect imports from secondary types
        for (secondaryType in secondaryTypes) {
            classImportVisitor.visitTypeSpec(secondaryType)
        }

        val codeWriter = JavaCodeWriter.create(
            strategy = strategy,
            out = out,
            indent = indent,
            importedTypes = suggestedImports,
            staticImports = staticImports,
            alwaysQualify = alwaysQualify
        )

        emit(codeWriter, type.kind.implicitFieldModifiers)
    }

    override fun emit(codeWriter: JavaCodeWriter) {
        emit(codeWriter, type.kind.implicitFieldModifiers)
    }

    fun emit(codeWriter: JavaCodeWriter, implicitFieldModifiers: Set<JavaModifier>) {
        codeWriter.inPackage(packageName) {
            val blankLineManager = BlankLineManager(codeWriter)

            if (!fileComment.isEmpty()) {
                codeWriter.emitComment(fileComment)
            }

            if (packageName.isNotEmpty()) {
                blankLineManager.withRequirement {
                    codeWriter.emitNewLine("package $packageName;")
                }
            }

            if (staticImports.isNotEmpty()) {
                blankLineManager.withRequirement {
                    for (signature in staticImports) {
                        codeWriter.emitNewLine("import static $signature;")
                    }
                }
            }

            var importedTypesCount = 0
            val importedTypeSet = LinkedHashSet(codeWriter.importedTypes.values)
            val importLines = mutableListOf<String>()

            for (importName in importedTypeSet) {
                // Use strategy-based package omission like kotlin
                if (codeWriter.strategy.omitJavaLangPackage()
                    && importName.packageName == JavaLangPackageName
                    && !alwaysQualify.contains(importName.name)
                ) {
                    continue
                }
                // check if it's a static import (member import)
                when (importName) {
                    is ImportName.Member -> {
                        importLines.add("import static ${importName.canonicalName};")
                    }
                    is ImportName.Class -> {
                        importLines.add("import ${importName.canonicalName};")
                    }
                }
                importedTypesCount++
            }

            if (importedTypesCount > 0) {
                blankLineManager.withRequirement {
                    for (importLine in importLines) {
                        codeWriter.emitNewLine(importLine)
                    }
                }
            }

            blankLineManager.withRequirement {
                type.emit(codeWriter)
            }

            // Emit secondary types
            for (secondaryType in secondaryTypes) {
                codeWriter.emitNewLine()
                // Ensure blank line before each secondary type
                blankLineManager.withRequirement {
                    secondaryType.emit(codeWriter)

                }
                // codeWriter.emitNewLine()
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JavaFileImpl) return false

        if (skipJavaLangImports != other.skipJavaLangImports) return false
        if (fileComment != other.fileComment) return false
        if (packageName != other.packageName) return false
        if (type != other.type) return false
        if (secondaryTypes != other.secondaryTypes) return false
        if (staticImports != other.staticImports) return false
        if (alwaysQualify != other.alwaysQualify) return false
        if (indent != other.indent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = skipJavaLangImports.hashCode()
        result = 31 * result + fileComment.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + secondaryTypes.hashCode()
        result = 31 * result + staticImports.hashCode()
        result = 31 * result + alwaysQualify.hashCode()
        result = 31 * result + indent.hashCode()
        return result
    }

    override fun toString(): String {
        return buildString {
            writeTo(this, ToStringJavaWriteStrategy)
        }
    }
}


@OptIn(InternalWriterApi::class)
private class ClassImportVisitor(
    val alwaysQualify: Set<String>,
    val importableTypes: LinkedHashMap<String, ImportName>,
    val currentPackageName: PackageName
) {
    fun visitTypeSpec(type: JavaTypeSpec) {
        // Supers
        type.superclass?.also { visitTypeName(it) }
        type.superinterfaces.forEach { visitTypeName(it) }
        // Annotations
        for (ref in type.annotations) {
            visitAnnotationRef(ref)
        }
        // TypeVariables
        for (ref in type.typeVariables) {
            visitTypeRef(ref)
        }
        // CodeValues
        visitCodeValue(type.staticBlock)
        visitCodeValue(type.initializerBlock)
        visitCodeValue(type.javadoc)
        // Fields
        for (field in type.fields) {
            visitFieldSpec(field)
        }
        // Methods
        for (method in type.methods) {
            visitMethodSpec(method)
        }
        // Subtypes
        for (subtype in type.subtypes) {
            visitTypeSpec(subtype)
        }
    }

    fun visitMethodSpec(method: JavaMethodSpec) {
        // Annotations
        for (ref in method.annotations) {
            visitAnnotationRef(ref)
        }
        // CodeValues
        visitCodeValue(method.code)
        visitCodeValue(method.defaultValue)
        visitCodeValue(method.javadoc)
        // TypeVariables
        for (ref in method.typeVariables) {
            visitTypeRef(ref)
        }
        // Return
        method.returnType?.also { visitTypeRef(it) }
        // Parameters
        for (parameter in method.parameters) {
            visitParameterSpec(parameter)
        }
        // Exceptions
        for (exception in method.exceptions) {
            visitTypeRef(exception)
        }
    }

    fun visitFieldSpec(field: JavaFieldSpec) {
        // Type
        visitTypeRef(field.type)
        // Annotations
        for (annotationRef in field.annotations) {
            visitAnnotationRef(annotationRef)
        }
        // Codes
        visitCodeValue(field.initializer)
        visitCodeValue(field.javadoc)
    }

    fun visitParameterSpec(parameter: JavaParameterSpec) {
        // Type
        visitTypeRef(parameter.type)
        // Annotations
        for (annotationRef in parameter.annotations) {
            visitAnnotationRef(annotationRef)
        }
        // Codes
        visitCodeValue(parameter.javadoc)
    }

    fun visitCodeValue(codeValue: CodeValue) {
        for (part in codeValue.parts) {
            when (part) {
                is CodeArgumentPart.Type -> visitTypeName(part.type)
                is CodeArgumentPart.TypeRef -> visitTypeRef(part.type)
                is CodeArgumentPart.OtherCodeValue -> visitCodeValue(part.value)
                is CodeArgumentPart.Literal -> {
                    when (val value = part.value) {
                        is JavaTypeSpec -> visitTypeSpec(value)
                        is JavaMethodSpec -> visitMethodSpec(value)
                        is JavaFieldSpec -> visitFieldSpec(value)
                        is JavaParameterSpec -> visitParameterSpec(value)
                        is AnnotationRef -> visitAnnotationRef(value)
                    }
                }

                else -> {
                    // Do nothing.
                }
            }
        }
    }

    fun visitAnnotationRef(annotationRef: AnnotationRef) {
        visitTypeName(annotationRef.typeName)
        for (memberValue in annotationRef.members.values) {
            for (value in memberValue.codeValues) {
                visitCodeValue(value)
            }
        }
    }

    fun visitTypeRef(typeRef: TypeRef<*>) {
        (typeRef.status as? JavaTypeNameRefStatusComponent)?.also { status ->
            for (annotationRef in status.annotations) {
                visitAnnotationRef(annotationRef)
            }
        }
        visitTypeName(typeRef.typeName)
    }

    fun visitTypeName(typeName: TypeName) {
        when (typeName) {
            is ClassName -> importable(typeName)
            is ParameterizedTypeName -> importable(typeName.rawType)
            is TypeVariableName -> {
                for (ref in typeName.bounds) {
                    visitTypeRef(ref)
                }
            }

            is ArrayTypeName -> {
                visitTypeRef(typeName.componentType)
            }

            is WildcardTypeName -> {
                for (ref in typeName.bounds) {
                    visitTypeRef(ref)
                }
            }
        }
    }

    private fun importable(className: ClassName) {
        val packageName = className.packageName
        if (packageName.isEmpty()) {
            return
        } else if (packageName == currentPackageName) {
            // Skip import if the class is in the same package as the current file
            return
        } else if (alwaysQualify.contains(className.simpleName)) {
            // TODO what about nested types like java.util.Map.Entry?
            return
        }
        // val topLevelClassName: ClassName = className.topLevelClassName
        // val simpleName: String = topLevelClassName.simpleName
        // importableTypes.computeValueIfAbsent(simpleName) { topLevelClassName }
        importableTypes.computeValueIfAbsent(className.simpleName) { ImportName.Class(className) }
        // val replaced: ClassName? = importableTypes.put(simpleName, topLevelClassName)
        // if (replaced != null) {
        //     importableTypes[simpleName] = replaced // On collision, prefer the first inserted.
        // }
    }
}

