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
package love.forte.codegentle.kotlin.internal

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
import love.forte.codegentle.kotlin.KotlinFile
import love.forte.codegentle.kotlin.ref.KotlinSimpleTypeNameRefStatusComponent
import love.forte.codegentle.kotlin.spec.*
import love.forte.codegentle.kotlin.spec.emitter.emitTo
import love.forte.codegentle.kotlin.strategy.KotlinWriteStrategy
import love.forte.codegentle.kotlin.strategy.ToStringKotlinWriteStrategy
import love.forte.codegentle.kotlin.writer.KotlinCodeWriter
import love.forte.codegentle.kotlin.writer.inPackage

/**
 * Concrete implementation of the [KotlinFile] interface
 */
@OptIn(InternalWriterApi::class)
internal class KotlinFileImpl(
    override val name: String,
    override val fileComment: CodeValue,
    override val packageName: PackageName,
    override val types: List<KotlinTypeSpec>,
    override val functions: List<KotlinFunctionSpec>,
    override val properties: List<KotlinPropertySpec>,
    private val code: CodeValue? = null,
    override val staticImports: Set<String>,
    override val alwaysQualify: Set<String>,
    override val indent: String,
    override val annotations: List<AnnotationRef>
) : KotlinFile {
    override fun writeTo(out: Appendable, strategy: KotlinWriteStrategy) {
        // Step 1: Collect types that need to be imported
        val suggestedImports = linkedMapOf<String, ImportName>()
        val classImportVisitor = ClassImportVisitor(alwaysQualify, suggestedImports, packageName)

        // visit all top-level types
        for (typeSpec in types) {
            classImportVisitor.visitTypeSpec(typeSpec)
        }

        // visit all top-level functions
        for (function in functions) {
            classImportVisitor.visitFunctionSpec(function)
        }

        // visit all top-level properties
        for (property in properties) {
            classImportVisitor.visitPropertySpec(property)
        }

        if (code != null) {
            classImportVisitor.visitCodeValue(code)
        }

        // visit all annotations
        for (annotation in annotations) {
            classImportVisitor.visitAnnotationRef(annotation)
        }

        // Create code writer
        val codeWriter = KotlinCodeWriter.create(
            strategy = strategy,
            out = out,
            indent = indent,
            importedTypes = suggestedImports,
            staticImports = staticImports,
            alwaysQualify = alwaysQualify
        )

        // Emit code
        emit(codeWriter)
    }

    override fun emit(codeWriter: KotlinCodeWriter) {
        val blankLineRequired = BlankLineManager(codeWriter)

        codeWriter.inPackage(packageName) {
            if (!fileComment.isEmpty()) {
                codeWriter.emitComment(fileComment)
            }

            // emit file-level annotations before package
            if (annotations.isNotEmpty()) {
                for (annotation in annotations) {
                    emitFileAnnotation(annotation, codeWriter)
                    codeWriter.emitNewLine()
                }
            }

            if (packageName.isNotEmpty()) {
                codeWriter.emitNewLine("package $packageName")
                blankLineRequired.required()
            }

            var importedTypesCount = 0

            if (staticImports.isNotEmpty()) {
                blankLineRequired.withRequirement {
                    for (signature in staticImports) {
                        if (importedTypesCount > 0) {
                            codeWriter.emitNewLine()
                        }
                        codeWriter.emit("import $signature")
                        importedTypesCount++
                    }
                }
            }

            for (importName in codeWriter.importedTypes.values) {
                // use codeWriter.strategy
                // if (skipKotlinImports
                //     && importName.packageName == PackageNames.KOTLIN
                if (codeWriter.strategy.omitPackage(importName.packageName)
                    && !alwaysQualify.contains(importName.name)
                ) {
                    continue
                }

                if (importedTypesCount == 0) {
                    if (blankLineRequired.blankLineRequired) {
                        codeWriter.emitNewLine()
                    }
                } else {
                    codeWriter.emitNewLine()
                }

                codeWriter.emit("import ${importName.canonicalName}")
                importedTypesCount++
            }

            if (importedTypesCount > 0) {
                codeWriter.emitNewLine()
                blankLineRequired.required()
            }

            // emit all top-level elements (types, functions, properties)
            var first = true

            // emit all properties
            for (property in properties) {
                blankLineRequired.withRequirement {
                    if (!first) {
                        codeWriter.emitNewLine()
                    }
                    property.emitTo(codeWriter)
                    // codeWriter.emitNewLine()
                    first = false
                }
            }

            // emit all types
            for (typeSpec in types) {
                blankLineRequired.withRequirement {
                    if (!first) {
                        codeWriter.emitNewLine()
                    }
                    typeSpec.emitTo(codeWriter)
                    // codeWriter.emitNewLine()
                    first = false
                }
            }

            // emit all functions
            for (function in functions) {
                blankLineRequired.withRequirement {
                    if (!first) {
                        codeWriter.emitNewLine()
                    }
                    function.emitTo(codeWriter)
                    // codeWriter.emitNewLine()
                    first = false
                }
            }

            if (code != null) {
                if (!first) {
                    codeWriter.emitNewLine()
                }
                blankLineRequired.withRequirement {
                    codeWriter.emit(code)
                }
            }
        }
    }

    /**
     * Emits a file-level annotation, ensuring it has @file: prefix.
     */
    private fun emitFileAnnotation(annotation: AnnotationRef, codeWriter: KotlinCodeWriter) {
        codeWriter.emit("@file:")
        codeWriter.emit(annotation.typeName)

        // Handle annotation parameters
        if (annotation.members.isNotEmpty()) {
            codeWriter.emit("(")
            var first = true
            for ((name, values) in annotation.members) {
                if (!first) {
                    codeWriter.emit(", ")
                }
                first = false

                // Emit parameter name
                if (name.isNotEmpty()) {
                    codeWriter.emit(name)
                    codeWriter.emit(" = ")
                }

                when (values) {
                    is AnnotationRef.MemberValue.Single -> {
                        codeWriter.emit(values.codeValue)
                    }

                    is AnnotationRef.MemberValue.Multiple -> {
                        // Multiple values - emit as array
                        codeWriter.emit("[")
                        var firstValue = true
                        for (value in values.codeValues) {
                            if (!firstValue) {
                                codeWriter.emit(", ")
                            }
                            firstValue = false
                            codeWriter.emit(value)
                        }
                        codeWriter.emit("]")
                    }
                }
            }
            codeWriter.emit(")")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KotlinFileImpl) return false

        if (fileComment != other.fileComment) return false
        if (packageName != other.packageName) return false
        if (types != other.types) return false
        if (functions != other.functions) return false
        if (properties != other.properties) return false
        if (staticImports != other.staticImports) return false
        if (alwaysQualify != other.alwaysQualify) return false
        if (indent != other.indent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileComment.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + types.hashCode()
        result = 31 * result + functions.hashCode()
        result = 31 * result + properties.hashCode()
        result = 31 * result + staticImports.hashCode()
        result = 31 * result + alwaysQualify.hashCode()
        result = 31 * result + indent.hashCode()
        return result
    }

    override fun toString(): String {
        return buildString {
            writeTo(this, ToStringKotlinWriteStrategy)
        }
    }
}

/**
 * 用于收集需要导入的类型的访问器
 */
@OptIn(InternalWriterApi::class)
private class ClassImportVisitor(
    val alwaysQualify: Set<String>,
    val importableTypes: LinkedHashMap<String, ImportName>,
    val currentPackageName: PackageName
) {
    fun visitTypeSpec(type: KotlinTypeSpec) {
        // 父类和接口
        type.superclass?.also { visitTypeName(it) }
        type.superinterfaces.forEach { visitTypeName(it) }

        // 注解
        for (ref in type.annotations) {
            visitAnnotationRef(ref)
        }

        // 类型变量
        for (ref in type.typeVariables) {
            visitTypeRef(ref)
        }

        // 代码值
        visitCodeValue(type.initializerBlock)
        visitCodeValue(type.kDoc)

        // 属性
        for (property in type.properties) {
            visitPropertySpec(property)
        }

        // 函数
        for (function in type.functions) {
            visitFunctionSpec(function)
        }

        // 子类型
        for (subtype in type.subtypes) {
            visitTypeSpec(subtype)
        }
    }

    fun visitFunctionSpec(function: KotlinFunctionSpec) {
        // 注解
        for (ref in function.annotations) {
            visitAnnotationRef(ref)
        }

        // 代码值
        visitCodeValue(function.code)
        visitCodeValue(function.kDoc)

        // 类型变量
        for (ref in function.typeVariables) {
            visitTypeRef(ref)
        }

        // 返回类型
        function.returnType.also { visitTypeRef(it) }

        // 参数
        for (parameter in function.parameters) {
            visitValueParameterSpec(parameter)
        }

        // 上下文参数
        for (contextParam in function.contextParameters) {
            visitContextParameterSpec(contextParam)
        }

        // 扩展接收者
        function.receiver?.also { visitTypeRef(it) }
    }

    fun visitPropertySpec(property: KotlinPropertySpec) {
        // 类型
        property.typeRef.also { visitTypeRef(it) }

        // 注解
        for (annotationRef in property.annotations) {
            visitAnnotationRef(annotationRef)
        }

        // 代码
        property.initializer?.also { visitCodeValue(it) }
        visitCodeValue(property.kDoc)
    }

    fun visitValueParameterSpec(parameter: KotlinValueParameterSpec) {
        // 类型
        visitTypeRef(parameter.typeRef)

        // 注解
        for (annotationRef in parameter.annotations) {
            visitAnnotationRef(annotationRef)
        }

        // 代码
        parameter.defaultValue?.also { visitCodeValue(it) }
    }

    fun visitContextParameterSpec(parameter: KotlinContextParameterSpec) {
        // 类型
        visitTypeRef(parameter.typeRef)
    }

    fun visitCodeValue(codeValue: CodeValue) {
        for (part in codeValue.parts) {
            when (part) {
                is CodeArgumentPart.Type -> visitTypeName(part.type)
                is CodeArgumentPart.TypeRef -> visitTypeRef(part.type)
                is CodeArgumentPart.OtherCodeValue -> visitCodeValue(part.value)
                is CodeArgumentPart.Literal -> {
                    val value = part.value
                    when (value) {
                        is KotlinTypeSpec -> visitTypeSpec(value)
                        is KotlinFunctionSpec -> visitFunctionSpec(value)
                        is KotlinPropertySpec -> visitPropertySpec(value)
                        is KotlinValueParameterSpec -> visitValueParameterSpec(value)
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
        (typeRef.status as? KotlinSimpleTypeNameRefStatusComponent)?.also { status ->
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

            is MemberName -> {
                importable(typeName)
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
            return
        }

        importableTypes.computeValueIfAbsent(className.simpleName) { ImportName.Class(className) }
    }

    private fun importable(memberName: MemberName) {
        val packageName = memberName.packageName
        if (packageName.isEmpty()) {
            return
        } else if (packageName == currentPackageName) {
            // Skip import if the member is in the same package as the current file
            return
        } else if (alwaysQualify.contains(memberName.name)) {
            return
        }

        importableTypes.computeValueIfAbsent(memberName.name) { ImportName.Member(memberName) }
    }
}
