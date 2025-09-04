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
@file:JvmName("JavaMethodSpecs")
@file:JvmMultifileClass

package love.forte.codegentle.java.spec

import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.common.ref.ref
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.naming.JavaAnnotationNames
import love.forte.codegentle.java.naming.toTypeName
import love.forte.codegentle.java.naming.toTypeVariableName
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeVariable

public inline fun ExecutableElement.toJavaMethodSpecOverring(
    block: JavaMethodSpec.Builder.() -> Unit = {}
): JavaMethodSpec {
    val method = this

    val name = method.simpleName.toString()
    val enclosingElement = method.enclosingElement
    require(Modifier.FINAL !in enclosingElement.modifiers) {
        "Cannot override method $name on final class $enclosingElement"
    }
    val methodModifiers = method.modifiers.map { JavaModifier.valueOf(it.name) }
    require(JavaModifier.FINAL !in methodModifiers) {
        "Cannot override final method $name on class $enclosingElement"
    }
    require(JavaModifier.PRIVATE !in methodModifiers) {
        "Cannot override private method $name on class $enclosingElement"
    }
    require(JavaModifier.STATIC !in methodModifiers) {
        "Cannot override static method $name on class $enclosingElement"
    }

    return JavaMethodSpec(name) {
        addAnnotation(JavaAnnotationNames.Override.annotationRef())

        // copy modifiers
        val newModifierSet = mutableSetOf<JavaModifier>()
        newModifierSet.addAll(methodModifiers)
        newModifierSet.remove(JavaModifier.ABSTRACT)
        newModifierSet.remove(JavaModifier.DEFAULT)
        addModifiers(newModifierSet)

        for (typeParameterElement in method.typeParameters) {
            val typeParameterElementAsType = typeParameterElement.asType() as TypeVariable
            addTypeVariable(typeParameterElementAsType.toTypeVariableName().ref())
        }

        returns(method.returnType.toTypeName().ref())
        addParameters(method.javaParameterSpecs)
        varargs(method.isVarArgs)

        addExceptions(method.thrownTypes.map { it.toTypeName().ref() })

        block()
    }
}


