@file:JvmName("JavaParameterSpecs")
@file:JvmMultifileClass

package love.forte.codegentle.java.spec

import love.forte.codegentle.java.naming.toTypeName
import love.forte.codegentle.java.ref.javaRef
import love.forte.codegentle.java.toJavaModifier
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

/**
 * Converts a `VariableElement` instance to a `JavaParameterSpec`.
 *
 * This method validates that the element is of kind `ElementKind.PARAMETER`,
 * transforms its type and name into their respective representations,
 * and constructs a new `JavaParameterSpec` with those values.
 *
 * @return A `JavaParameterSpec` representing the parameter element.
 * @throws IllegalArgumentException If `kind` is not `ElementKind.PARAMETER`.
 */
public fun VariableElement.toJavaParameterSpec(): JavaParameterSpec {
    require(kind == ElementKind.PARAMETER) {
        "Element.kind must be ElementKind.PARAMETER, but is $kind"
    }

    val type = asType().toTypeName()
    val name = simpleName.toString()

    // TODO annotationMirrors?

    return JavaParameterSpec(type.javaRef(), name) {
        addModifiers(this@toJavaParameterSpec.modifiers.map { it.toJavaModifier() })
    }
}

/**
 * Returns a list of `JavaParameterSpec` objects representing the parameters
 * of the `ExecutableElement`.
 *
 * Each parameter is converted to a `JavaParameterSpec` using the
 * `toJavaParameterSpec` extension function, which maps the parameter's type,
 * name, and other properties to a corresponding `JavaParameterSpec` instance.
 *
 * This property simplifies the process of extracting parameter metadata from
 * an `ExecutableElement`, offering a collection of type-safe, high-level
 * representations of its parameters.
 *
 * @see VariableElement.toJavaParameterSpec
 */
public val ExecutableElement.javaParameterSpecs: List<JavaParameterSpec>
    get() = parameters.map { it.toJavaParameterSpec() }
