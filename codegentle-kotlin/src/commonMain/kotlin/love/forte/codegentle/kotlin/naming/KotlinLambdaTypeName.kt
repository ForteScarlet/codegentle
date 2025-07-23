package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.naming.TypeName

/**
 *
 * @author ForteScarlet
 */
public interface KotlinLambdaTypeName : TypeName {
    // receiver?
    // value parameters
    // context parameter type refs
    // return type
    // suspend (via KotlinModifier, so it has modifiers)
}

