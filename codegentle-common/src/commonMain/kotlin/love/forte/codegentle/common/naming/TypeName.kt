@file:JvmName("TypeNames")
@file:JvmMultifileClass

package love.forte.codegentle.common.naming

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * A JVM-based type name.
 *
 * @see ClassName
 * @see ArrayTypeName
 * @see ParameterizedTypeName
 * @see TypeVariableName
 * @see WildcardTypeName
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(CodeGentleNamingImplementation::class)
public interface TypeName
