/*
 * Copyright (C) 2014 Google, Inc.
 * Copyright (C) 2014-2026 Forte Scarlet
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
package love.forte.codegentle.common.naming

import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.SimpleElementVisitor8
import kotlin.reflect.KClass


/**
 * Create a [love.forte.codegentle.common.naming.ClassName] from [KClass].
 *
 * @see Class.toClassName
 */
public fun KClass<*>.toClassName(): ClassName {
    return java.toClassName()
}

/**
 * Create a [ClassName] from [Class].
 */
public fun Class<*>.toClassName(): ClassName {
    var java = this
    require(!java.isPrimitive) { "Primitive types cannot be represented as a ClassName" }
    require(Void.TYPE != java) { "'void' type cannot be represented as a ClassName" }
    require(!java.isArray) { "Array types cannot be represented as a ClassName" }

    var anonymousSuffix = ""
    while (java.isAnonymousClass) {
        val lastDollar: Int = java.getName().lastIndexOf('$')
        anonymousSuffix = java.getName().substring(lastDollar) + anonymousSuffix
        java = java.getEnclosingClass()
    }
    val name: String = java.getSimpleName() + anonymousSuffix

    if (java.getEnclosingClass() == null) {
        // Avoid unreliable Class.getPackage(). https://github.com/square/javapoet/issues/295
        val lastDot: Int = java.getName().lastIndexOf('.')
        val packageName: PackageName = if (lastDot != -1) {
            java.getName().substring(0, lastDot).parseToPackageName()
        } else {
            PackageName()
        }

        return ClassName(packageName, name)
    }

    return java.enclosingClass.toClassName().nestedClass(name)
}

public fun TypeElement.toClassName(): ClassName {
    val simpleName = simpleName.toString()
    val visitor = object : SimpleElementVisitor8<ClassName, Void?>() {
        override fun visitPackage(packageElement: PackageElement, p: Void?): ClassName {
            return ClassName(packageElement.qualifiedName.parseToPackageName(), simpleName)
        }

        override fun visitType(typeElement: TypeElement, p: Void?): ClassName {
            return typeElement.toClassName().nestedClass(simpleName)
        }

        override fun visitUnknown(unknown: Element?, p: Void?): ClassName {
            return ClassName(PackageName(), simpleName)
        }

        override fun defaultAction(element: Element?, p: Void?): ClassName {
            throw IllegalArgumentException("Unexpected type nesting: $element")
        }
    }

    return enclosingElement.accept(visitor, null)
}
