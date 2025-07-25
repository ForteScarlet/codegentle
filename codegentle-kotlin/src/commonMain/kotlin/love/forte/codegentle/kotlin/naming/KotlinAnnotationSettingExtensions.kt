package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.emitName
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.common.ref.AnnotationRefBuilder


@RequiresOptIn
public annotation class AnnotationTargetExtensionScope

@AnnotationTargetExtensionScope
public object KotlinTargetExtensionScope

/**
 * Extension for setting
 * [allowedTargets][AnnotationTarget] in
 * [KotlinAnnotationNames.TARGET].
 *
 * @see Target.allowedTargets
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinTargetExtensionScope)
public fun AnnotationRefBuilder.setAllowedTargets(vararg setAllowedTargets: AnnotationTarget) {
    addMultipleMembers(
        "allowedTargets",
        setAllowedTargets.map {
            CodeValue("%V.%V") {
                emitType(ClassName("kotlin.annotation", "AnnotationTarget"))
                emitName(it.name)
            }
        })
}

@AnnotationTargetExtensionScope
public object KotlinRetentionExtensionScope

/**
 * Extension for setting
 * [value][AnnotationRetention]
 * in [KotlinAnnotationNames.RETENTION].
 *
 * @see Retention.value
 * @see KotlinAnnotationNames.RETENTION
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinRetentionExtensionScope)
public fun AnnotationRefBuilder.setValue(retention: AnnotationRetention): AnnotationRefBuilder {
    return addMember(
        "value",
        CodeValue("%V.%V") {
            emitType(ClassName("kotlin.annotation", "AnnotationRetention"))
            emitName(retention.name)
        }
    )
}

@AnnotationTargetExtensionScope
public object KotlinJvmNameExtensionScope

/**
 * Extension for setting
 * [name] in [KotlinAnnotationNames.JVM_NAME].
 *
 * @see kotlin.jvm.JvmName.name
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinJvmNameExtensionScope)
public fun AnnotationRefBuilder.setName(name: String): AnnotationRefBuilder {
    return addMember("name", CodeValue(CodePart.string(name)))
}

@AnnotationTargetExtensionScope
public object KotlinDeprecatedExtensionScope

/**
 * Extension for setting
 * [message] in [KotlinAnnotationNames.DEPRECATED].
 *
 * @see Deprecated.message
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinDeprecatedExtensionScope)
public fun AnnotationRefBuilder.setMessage(message: String): AnnotationRefBuilder {
    return addMember("message", CodeValue(CodePart.string(message)))
}

/**
 * Extension for setting
 * [replaceWith] in [KotlinAnnotationNames.DEPRECATED].
 *
 * @see Deprecated.replaceWith
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinDeprecatedExtensionScope)
public fun AnnotationRefBuilder.setReplaceWith(replaceWith: CodeValue): AnnotationRefBuilder {
    return addMember("replaceWith", replaceWith)
}

/**
 * Extension for setting
 * [level] in [KotlinAnnotationNames.DEPRECATED].
 *
 * @see Deprecated.level
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinDeprecatedExtensionScope)
public fun AnnotationRefBuilder.setLevel(level: DeprecationLevel): AnnotationRefBuilder {
    return addMember(
        "level",
        CodeValue("%V.%V") {
            emitType(ClassName("kotlin", "DeprecationLevel"))
            emitName(level.name)
        }
    )
}

@AnnotationTargetExtensionScope
public object KotlinReplaceWithExtensionScope

/**
 * Extension for setting
 * [expression] in [KotlinAnnotationNames.REPLACE_WITH].
 *
 * @see ReplaceWith.expression
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinReplaceWithExtensionScope)
public fun AnnotationRefBuilder.setExpression(expression: String): AnnotationRefBuilder {
    return addMember("expression", CodeValue(CodePart.string(expression)))
}

/**
 * Extension for setting
 * [imports] in [KotlinAnnotationNames.REPLACE_WITH].
 *
 * @see ReplaceWith.imports
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinReplaceWithExtensionScope)
public fun AnnotationRefBuilder.setImports(vararg imports: String): Unit {
    addMultipleMembers("imports", imports.map { CodeValue(CodePart.string(it)) })
}

@AnnotationTargetExtensionScope
public object KotlinSuppressExtensionScope

/**
 * Extension for setting
 * [names] in [KotlinAnnotationNames.SUPPRESS].
 *
 * @see Suppress.names
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinSuppressExtensionScope)
public fun AnnotationRefBuilder.setNames(vararg names: String): AnnotationRefBuilder {
    return addMultipleMembers("names", names.map { CodeValue(CodePart.string(it)) })
}

@AnnotationTargetExtensionScope
public object KotlinOptInExtensionScope

/**
 * Extension for setting
 * [markerClass] in [KotlinAnnotationNames.OPT_IN].
 *
 * @see OptIn.markerClass
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinOptInExtensionScope)
public fun AnnotationRefBuilder.setMarkerClass(vararg markerClass: ClassName): AnnotationRefBuilder {
    return addMultipleMembers(
        "markerClass",
        markerClass.map { CodeValue("%V::class", CodePart.type(it)) }
    )
}

@AnnotationTargetExtensionScope
public object KotlinRequiresOptInExtensionScope

/**
 * Extension for setting
 * [message] in [KotlinAnnotationNames.REQUIRES_OPT_IN].
 *
 * @see RequiresOptIn.message
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinRequiresOptInExtensionScope)
public fun AnnotationRefBuilder.setMessage(message: String): AnnotationRefBuilder {
    return addMember("message", CodeValue(CodePart.string(message)))
}

/**
 * Extension for setting
 * [level] in [KotlinAnnotationNames.REQUIRES_OPT_IN].
 *
 * @see RequiresOptIn.level
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinRequiresOptInExtensionScope)
public fun AnnotationRefBuilder.setLevel(level: RequiresOptIn.Level): AnnotationRefBuilder {
    return addMember(
        "level",
        CodeValue("%V.%V") {
            emitType(ClassName("kotlin", "RequiresOptIn", "Level"))
            emitName(level.name)
        }
    )
}

@AnnotationTargetExtensionScope
public object KotlinThrowsExtensionScope

/**
 * Extension for setting
 * [exceptionClasses] in [KotlinAnnotationNames.THROWS].
 *
 * @see Throws.exceptionClasses
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinThrowsExtensionScope)
public fun AnnotationRefBuilder.setExceptionClasses(vararg exceptionClasses: ClassName): AnnotationRefBuilder {
    return addMultipleMembers(
        "exceptionClasses",
        exceptionClasses.map { CodeValue("%V::class", CodePart.type(it)) }
    )
}

@AnnotationTargetExtensionScope
public object KotlinJvmSuppressWildcardsExtensionScope

/**
 * Extension for setting
 * [suppress] in [KotlinAnnotationNames.JVM_SUPPRESS_WILDCARDS].
 *
 * @see kotlin.jvm.JvmSuppressWildcards.suppress
 */
@OptIn(AnnotationTargetExtensionScope::class)
context(_: KotlinJvmSuppressWildcardsExtensionScope)
public fun AnnotationRefBuilder.setSuppress(suppress: Boolean): AnnotationRefBuilder {
    return addMember("suppress", CodeValue(CodePart.literal(suppress)))
}
