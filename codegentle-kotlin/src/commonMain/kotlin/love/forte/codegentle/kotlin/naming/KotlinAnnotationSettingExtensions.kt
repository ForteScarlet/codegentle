/*
 * Copyright (C) 2025-2026 Forte Scarlet
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
package love.forte.codegentle.kotlin.naming

import love.forte.codegentle.common.code.CodePart
import love.forte.codegentle.common.code.CodeValue
import love.forte.codegentle.common.code.emitName
import love.forte.codegentle.common.code.emitType
import love.forte.codegentle.common.naming.ClassName
import love.forte.codegentle.kotlin.ref.KotlinAnnotationRefBuilder


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
public fun KotlinAnnotationRefBuilder.setAllowedTargets(vararg setAllowedTargets: AnnotationTarget) {
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
public fun KotlinAnnotationRefBuilder.setValue(retention: AnnotationRetention): KotlinAnnotationRefBuilder {
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
public fun KotlinAnnotationRefBuilder.setName(name: String): KotlinAnnotationRefBuilder {
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
public fun KotlinAnnotationRefBuilder.setMessage(message: String): KotlinAnnotationRefBuilder {
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
public fun KotlinAnnotationRefBuilder.setReplaceWith(replaceWith: CodeValue): KotlinAnnotationRefBuilder {
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
public fun KotlinAnnotationRefBuilder.setLevel(level: DeprecationLevel): KotlinAnnotationRefBuilder {
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
public fun KotlinAnnotationRefBuilder.setExpression(expression: String): KotlinAnnotationRefBuilder {
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
public fun KotlinAnnotationRefBuilder.setImports(vararg imports: String) {
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
public fun KotlinAnnotationRefBuilder.setNames(vararg names: String): KotlinAnnotationRefBuilder {
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
public fun KotlinAnnotationRefBuilder.setMarkerClass(vararg markerClass: ClassName): KotlinAnnotationRefBuilder {
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
public fun KotlinAnnotationRefBuilder.setMessage(message: String): KotlinAnnotationRefBuilder {
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
public fun KotlinAnnotationRefBuilder.setLevel(level: RequiresOptIn.Level): KotlinAnnotationRefBuilder {
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
public fun KotlinAnnotationRefBuilder.setExceptionClasses(vararg exceptionClasses: ClassName): KotlinAnnotationRefBuilder {
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
public fun KotlinAnnotationRefBuilder.setSuppress(suppress: Boolean): KotlinAnnotationRefBuilder {
    return addMember("suppress", CodeValue(CodePart.literal(suppress)))
}
