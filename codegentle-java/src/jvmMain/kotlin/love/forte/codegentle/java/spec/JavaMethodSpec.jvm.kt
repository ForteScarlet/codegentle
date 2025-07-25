@file:JvmName("JavaMethodSpecs")
@file:JvmMultifileClass

package love.forte.codegentle.java.spec

import love.forte.codegentle.common.ref.annotationRef
import love.forte.codegentle.java.JavaModifier
import love.forte.codegentle.java.naming.JavaAnnotationNames
import love.forte.codegentle.java.naming.toTypeName
import love.forte.codegentle.java.naming.toTypeVariableName
import love.forte.codegentle.java.ref.javaRef
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.type.TypeVariable

public inline fun ExecutableElement.toJavaMethodSpecOverring(
    block: JavaMethodSpecBuilder.() -> Unit = {}
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
            addTypeVariable(typeParameterElementAsType.toTypeVariableName())
        }

        returns(method.returnType.toTypeName())
        addParameters(method.javaParameterSpecs)
        varargs(method.isVarArgs)

        addExceptions(method.thrownTypes.map { it.toTypeName().javaRef() })

        block()
    }
}


